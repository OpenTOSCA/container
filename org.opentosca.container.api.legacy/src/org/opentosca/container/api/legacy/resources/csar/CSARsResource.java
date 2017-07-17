package org.opentosca.container.api.legacy.resources.csar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opentosca.container.api.legacy.osgi.servicegetter.FileAccessServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ModelUtils;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Resource represents all CSARs.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
@Path("/CSARs")
public class CSARsResource {

	private static final Logger LOG = LoggerFactory.getLogger(CSARsResource.class);

	private final ICoreFileService fileHandler;
	private final IOpenToscaControlService control;

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	public CSARsResource() {
		this.fileHandler = FileRepositoryServiceHandler.getFileHandler();
		this.control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		CSARsResource.LOG.debug("{} created: {}", this.getClass(), this);
	}

	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML() {
		return Response.ok(this.getRefs().getXMLString()).build();
	}

	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON() {
		return Response.ok(this.getRefs().getJSONString()).build();
	}

	public References getRefs() {
		final References refs = new References();
		for (final CSARID csarID : this.fileHandler.getCSARIDs()) {
			final Reference ref = new Reference(
					Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), csarID.toString()),
					XLinkConstants.SIMPLE, csarID.toString());
			refs.getReference().add(ref);

			CSARsResource.LOG.debug("CSAR \"{}\" added as Reference.", csarID);

		}
		refs.getReference().add(
				new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}

	/**
	 * Stores the CSAR file at {@code fileLocation} (absolute path on the local
	 * file system) in the Container.
	 *
	 * @param fileLocation
	 * @return
	 * @throws SystemException
	 * @throws UserException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response uploadCSAR(final String fileLocation)
			throws UserException, SystemException, IOException, URISyntaxException {
		CSARsResource.LOG.info("Upload file from location: {}", fileLocation);

		try {

			final java.nio.file.Path csarFile = Paths.get(fileLocation.trim());

			// try {
			return this.handleCSAR(csarFile.getFileName().toString(), Files.newInputStream(csarFile));

		} catch (final java.nio.file.InvalidPathException exc) {

			java.nio.file.Path csarFile = Paths.get(fileLocation.trim());

			// try {
			return this.handleCSAR(csarFile.getFileName().toString(), Files.newInputStream(csarFile));

		}
	}

	@POST
	@Consumes(ResourceConstants.APPLICATION_JSON)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response uploadCSARAdminUI(final String json) throws MalformedURLException {

		CSARsResource.LOG.debug("Received payload for uploading CSAR:\\n   {}", json);

		final JsonParser parser = new JsonParser();
		final JsonObject jsonObj = (JsonObject) parser.parse(json);

		String urlStr = jsonObj.get("URL").toString();
		urlStr = urlStr.substring(1, urlStr.length() - 1);

		// if ((null == url) || url.equals("")) {
		// CSARsResource.LOG.error("The url is null or empty.");
		// return Response.serverError().build();
		// }

		String fileName = "";
		URL url = null;
		try {

			// http://ds/joomla/images/a%25.txt to
			// http://ds/joomla/images/a%2525.txt

			// String urlStr = url.toExternalForm();
			// urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) +
			// Utilities.encodeURIPath((urlStr.substring(urlStr.lastIndexOf("/")
			// + 1)));
			url = new URL(urlStr);
			CSARsResource.LOG.trace("\n{}\n{}", urlStr, url);

			fileName = CSARsResource
					.createXMLidAsString(url.toExternalForm().substring(url.toExternalForm().lastIndexOf("/") + 1));
			fileName = fileName.replace("?", ".");

			CSARsResource.LOG.debug("Recieved URL " + urlStr);
			// } catch (UnsupportedEncodingException e1) {
			// CSARsResource.LOG.error("Encoding of recieved URL failed.");
			// e1.printStackTrace();
		} catch (final MalformedURLException e) {
			CSARsResource.LOG.error("Generation of URL of encoded URL failed.");
			e.printStackTrace();
		}

		if ((null == fileName) || fileName.equals("")) {
			CSARsResource.LOG.error("The decoding of the file name has failed.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		CSARsResource.LOG.info("Recieved the URL \"" + url + "\" for uploading a CSAR.");
		CSARsResource.LOG.debug("File name is \"" + fileName + "\".");

		try {
			return this.handleCSAR(fileName, url.openStream());
		} catch (final IOException e) {
			CSARsResource.LOG.error("There was an error while opening the input stream.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 *
	 * Accepts the InputStream of a CSAR file. After storing and unzipping the
	 * CSAR the processing of TOSCA, IAs and BPEL-Plans is triggered.
	 *
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SystemException
	 * @throws UserException
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response uploadCSARAdminUI(@FormDataParam("file") final InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition fileDetail)
			throws IOException, URISyntaxException, UserException, SystemException {

		CSARsResource.LOG.info("Try to upload a new CSAR.");

		if (null == uploadedInputStream) {
			CSARsResource.LOG.error("The stream is null.");
			return Response.serverError().build();
		}
		if (null == fileDetail) {
			CSARsResource.LOG.error("The file details are null.");
			return Response.serverError().build();
		}

		CSARsResource.LOG.debug("Post for uploading a new CSAR as file with name \"" + fileDetail.getFileName()
				+ "\" with size " + fileDetail.getSize() + ".");

		final String fileName = fileDetail.getFileName();

		return this.handleCSAR(fileName, uploadedInputStream);

	}

	private File storeTemporaryFile(String fileName, InputStream uploadedInputStream) throws IOException {
		File tmpDir = FileAccessServiceHandler.getFileAccessService().getTemp();
		tmpDir.mkdir();

		File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + fileName);

		OutputStream out;

		out = new FileOutputStream(uploadFile);

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}

		uploadedInputStream.close();

		CSARsResource.LOG
				.debug("Temporary file: " + uploadFile.getAbsolutePath() + " with size " + uploadFile.getTotalSpace());

		out.flush();
		out.close();
		return uploadFile;
	}

	public CSARID storeCSAR(String fileName, InputStream uploadedInputStream) {
		try {
			File uploadFile = this.storeTemporaryFile(fileName, uploadedInputStream);
			CSARID csarID = null;
			csarID = this.fileHandler.storeCSAR(uploadFile.toPath());

			csarID = this.startPlanBuilder(csarID);

			if (csarID != null) {
				CSARsResource.LOG.info("Storing CSAR file \"{}\" was successful.", csarID.toString());
				CSARsResource.LOG.trace("Control is bound: " + (null != this.control));
				this.control.setDeploymentProcessStateStored(csarID);

				if (this.control.invokeTOSCAProcessing(csarID)) {

					final List<QName> serviceTemplates = ToscaServiceHandler.getToscaEngineService()
							.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID);

					for (final QName serviceTemplate : serviceTemplates) {

						CSARsResource.LOG.debug("Invoke IADeployment for ServiceTemplate \"" + serviceTemplate
								+ "\" of CSAR \"" + csarID + "\".");
						if (!this.control.invokeIADeployment(csarID, serviceTemplate)) {

							break;
						}

						if (!this.control.invokePlanDeployment(csarID, serviceTemplate)) {

							break;
						}

					}
					return csarID;
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final UserException e) {
			e.printStackTrace();
		} catch (final SystemException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Response handleCSAR(final String fileName, final InputStream uploadedInputStream)
			throws IOException, URISyntaxException, UserException, SystemException {

		File uploadFile = this.storeTemporaryFile(fileName, uploadedInputStream);

		CSARID csarID = this.fileHandler.storeCSAR(uploadFile.toPath());

		this.control.invokeTOSCAProcessing(csarID);

		if (ModelUtils.hasOpenRequirements(csarID)) {
			// return a 406 with location to ServiceTemplate in local Winery in
			// Body
			// winery instance
			WineryConnector winCon = new WineryConnector();

			if (winCon.isWineryRepositoryAvailable()) {

				QName serviceTemplate = winCon.uploadCSAR(uploadFile);

				this.control.deleteCSAR(csarID);
				// TODO
				return Response.status(Response.Status.NOT_ACCEPTABLE).entity(
						"{ \"Location\": \"" + winCon.getServiceTemplateURI(serviceTemplate).toString() + "\" }")
						.build();
				// return
				// Response.status(Response.Status.SEE_OTHER).location(new
				// URI(winCon.getServiceTemplateURI(serviceTemplate).toString()+"/injector/options")).build();
			} else {
				this.fileHandler.deleteCSAR(csarID);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}

		// looks dumb, and it is. But the "TOSCA Processing" must be done again
		// here
		this.control.deleteCSAR(csarID);
		csarID = this.fileHandler.storeCSAR(uploadFile.toPath());

		csarID = this.startPlanBuilder(csarID);

		this.processTOSCA(csarID);

		if (csarID != null) {
			CSARsResource.LOG.info("Storing CSAR file \"{}\" was successful.", csarID.toString());
			final String path = Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), csarID.toString());
			final JsonObject retObj = new JsonObject();
			retObj.addProperty("csarPath", path);
			return Response.created(URI.create(path)).entity(retObj.toString()).build();

		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@Path("{csarID}")
	public CSARResource getCSAR(@PathParam("csarID") final String csarIDAsString) throws UserException {

		CSARsResource.LOG.debug("Searching for CSAR \"{}\".", csarIDAsString);

		final CSARContent csar = this.fileHandler.getCSAR(new CSARID(csarIDAsString));

		return new CSARResource(csar);
	}

	@DELETE
	@Produces("text/plain")
	public Response deleteCSARs() throws SystemException, UserException {

		final List<String> notDeleted = new ArrayList<>();

		for (final CSARID csarID : this.fileHandler.getCSARIDs()) {

			CSARsResource.LOG.info("Deleting CSAR \"{}\".", csarID);
			if (!IOpenToscaControlServiceHandler.getOpenToscaControlService().deleteCSAR(csarID).isEmpty()) {
				notDeleted.add(csarID.toString());
			}

		}
		return Response.ok(notDeleted).build();
	}

	public CSARID processTOSCA(CSARID csarID) {
		if (csarID != null) {
			CSARsResource.LOG.info("Storing CSAR file \"{}\" was successful.", csarID.toString());
			CSARsResource.LOG.trace("Control is bound: " + (null != this.control));
			this.control.setDeploymentProcessStateStored(csarID);

			if (this.control.invokeTOSCAProcessing(csarID)) {

				List<QName> serviceTemplates = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
						.getServiceTemplateIDsContainedInCSAR(csarID);

				for (QName serviceTemplate : serviceTemplates) {

					CSARsResource.LOG.debug("Invoke IADeployment for ServiceTemplate \"" + serviceTemplate
							+ "\" of CSAR \"" + csarID + "\".");
					if (!this.control.invokeIADeployment(csarID, serviceTemplate)) {

						break;
					}

					if (!this.control.invokePlanDeployment(csarID, serviceTemplate)) {

						break;
					}

				}
				return csarID;
			}
		}
		return null;
	}

	/**
	 * Checks whether the root tosca definitions file, contains servicetemplates
	 * where no build plan is available. If there is no such plan available the
	 * plan builder starts to generate a build plan.
	 *
	 * @param csarId
	 *            the CSARID of the definitions to generate build plans for
	 * @return a new CSARID for the repackaged csar if new build plans were
	 *         generated, the same csarid if no build plans were generated, else
	 *         null
	 */
	private CSARID startPlanBuilder(final CSARID csarId) {
		final Importer planBuilderImporter = new Importer();
		final Exporter planBuilderExporter = new Exporter();

		List<TOSCAPlan> buildPlans = planBuilderImporter.importDefs(csarId);
		// no buildplan generated <=> nothing to do
		if (buildPlans.isEmpty()) {
			return csarId;
		}

		final File repackagedCsar = planBuilderExporter.export(buildPlans, csarId);
		try {
			this.fileHandler.deleteCSAR(csarId);
			return this.fileHandler.storeCSAR(repackagedCsar.toPath());
		} catch (final SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates a (valid) XML ID (NCName) based on the passed name
	 *
	 * Valid NCNames: http://www.w3.org/TR/REC-xml-names/#NT-NCName /
	 * http://www.w3.org/TR/xml/#NT-Name http://www.w3.org/TR/xml/#NT-Name
	 *
	 * @author oliver.kopp@iaas.uni-stuttgart.de license: Apache 2.0 without
	 *         NOTICE
	 *
	 */
	private static String createXMLidAsString(final String name) {
		// RegExp inspired by http://stackoverflow.com/a/5396246/873282
		// NameStartChar without ":"
		// stackoverflow: -dfff, standard: d7fff
		final String RANGE_NCNAMESTARTCHAR = "A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d"
				+ "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff"
				+ "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x10000-\\xEFFFF";
		final String REGEX_NCNAMESTARTCHAR = "[" + RANGE_NCNAMESTARTCHAR + "]";
		final String RANGE_NCNAMECHAR = RANGE_NCNAMESTARTCHAR + "\\-\\.0-9\\u00b7\\u0300-\\u036f\\u203f-\\u2040";
		final String REGEX_INVALIDNCNAMESCHAR = "[^" + RANGE_NCNAMECHAR + "]";
		String id = name;
		if (!id.substring(0, 1).matches(REGEX_NCNAMESTARTCHAR)) {
			id = "_".concat(id);
		}
		// id starts with a valid character

		// before we wipe out all invalid characters, we do a readable
		// replacement for appropriate characters
		id = id.replace(' ', '_');

		// keep length of ID, only wipe out invalid characters
		// alternative: replace invalid characters by URLencoded version. As the
		// ID is visible only in the URL, this quick hack should be OK
		// ID is visible only in the URL, this quick hack should be OK
		id = id.replaceAll(REGEX_INVALIDNCNAMESCHAR, "_");

		return id;
	}

}
