package org.opentosca.containerapi.resources.csar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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

import org.opentosca.containerapi.osgi.servicegetter.FileAccessServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.opentoscacontrol.service.IOpenToscaControlService;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

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
		fileHandler = FileRepositoryServiceHandler.getFileHandler();
		control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		CSARsResource.LOG.debug("{} created: {}", this.getClass(), this);
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML() {
		return Response.ok(getRefs().getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON() {
		return Response.ok(getRefs().getJSONString()).build();
	}
	
	public References getRefs() {
		References refs = new References();
		for (CSARID csarID : fileHandler.getCSARIDs()) {
			Reference ref = new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), csarID.toString()), XLinkConstants.SIMPLE, csarID.toString());
			refs.getReference().add(ref);
			
			CSARsResource.LOG.debug("CSAR \"{}\" added as Reference.", csarID);
			
		}
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
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
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response uploadCSAR(String fileLocation) throws UserException, SystemException {
		
		CSARsResource.LOG.info("Upload file from location: {}", fileLocation);
		
		try {
			
			java.nio.file.Path csarFile = Paths.get(fileLocation.trim());
			
			// try {
			CSARID csarID = fileHandler.storeCSAR(csarFile);
			csarID = startPlanBuilder(csarID);
			// } catch (UserException exc) {
			// CSARsResource.LOG.warn("An User Exception occured.", exc);
			// return Response.status(Status.BAD_REQUEST).build();
			//
			// } catch (SystemException exc) {
			// CSARsResource.LOG.warn("An System Exception occured.", exc);
			// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			// }
			
			control.setDeploymentProcessStateStored(csarID);
			return Response.created(URI.create(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), csarID.toString()))).build();
			
		} catch (java.nio.file.InvalidPathException exc) {
			throw new SystemException("Given file path \"" + fileLocation + "\" is syntactically invalid.", exc);
		}
		
	}
	
	@POST
	@Consumes(ResourceConstants.APPLICATION_JSON)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response uploadCSARAdminUI(String json) throws MalformedURLException {
		
		LOG.debug("Received payload for uploading CSAR:\\n   {}", json);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = (JsonObject) parser.parse(json);
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
			LOG.trace("\n{}\n{}", urlStr, url);
			
			fileName = CSARsResource.createXMLidAsString(url.toExternalForm().substring(url.toExternalForm().lastIndexOf("/") + 1));
			fileName = fileName.replace("?", ".");
			
			CSARsResource.LOG.debug("Recieved URL " + urlStr);
			// } catch (UnsupportedEncodingException e1) {
			// CSARsResource.LOG.error("Encoding of recieved URL failed.");
			// e1.printStackTrace();
		} catch (MalformedURLException e) {
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
			return storeCSAR(fileName, url.openStream());
		} catch (IOException e) {
			CSARsResource.LOG.error("There was an error while opening the input stream.");
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
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response uploadCSARAdminUI(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		CSARsResource.LOG.info("Try to upload a new CSAR.");
		
		if (null == uploadedInputStream) {
			CSARsResource.LOG.error("The stream is null.");
			return Response.serverError().build();
		}
		if (null == fileDetail) {
			CSARsResource.LOG.error("The file details are null.");
			return Response.serverError().build();
		}
		
		CSARsResource.LOG.debug("Post for uploading a new CSAR as file with name \"" + fileDetail.getFileName() + "\" with size " + fileDetail.getSize() + ".");
		
		String fileName = fileDetail.getFileName();
		
		return storeCSAR(fileName, uploadedInputStream);
		
	}
	
	private Response storeCSAR(String fileName, InputStream uploadedInputStream) {
		
		File tmpDir = FileAccessServiceHandler.getFileAccessService().getTemp();
		tmpDir.mkdir();
		
		File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + fileName);
		
		OutputStream out;
		
		try {
			out = new FileOutputStream(uploadFile);
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			
			uploadedInputStream.close();
			
			CSARsResource.LOG.debug("Temporary file: " + uploadFile.getAbsolutePath() + " with size " + uploadFile.getTotalSpace());
			
			out.flush();
			out.close();
			
			CSARID csarID = null;
			csarID = fileHandler.storeCSAR(uploadFile.toPath());
			
			csarID = startPlanBuilder(csarID);
			
			if (csarID != null) {
				CSARsResource.LOG.info("Storing CSAR file \"{}\" was successful.", csarID.toString());
				CSARsResource.LOG.trace("Control is bound: " + (null != control));
				control.setDeploymentProcessStateStored(csarID);
				
				if (control.invokeTOSCAProcessing(csarID)) {
					
					List<QName> serviceTemplates = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID);
					
					for (QName serviceTemplate : serviceTemplates) {
						
						CSARsResource.LOG.debug("Invoke IADeployment for ServiceTemplate \"" + serviceTemplate + "\" of CSAR \"" + csarID + "\".");
						if (!control.invokeIADeployment(csarID, serviceTemplate)) {
							
							break;
						}
						
						CSARsResource.LOG.debug("Invoke PlanDeployment for ServiceTemplate \"" + serviceTemplate + "\" of CSAR \"" + csarID + "\".");
						CSARsResource.LOG.debug("");
						if (!control.invokePlanDeployment(csarID, serviceTemplate)) {
							
							break;
						}
						
					}
					
					JsonObject retObj = new JsonObject();
					retObj.addProperty("csarPath", Utilities.buildURI(uriInfo.getAbsolutePath().toString(), csarID.toString()));
					return Response.created(URI.create(retObj.toString())).build();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		CSARsResource.LOG.error("Can't upload CSAR located at \"{}\". CSAR is invalid!", uploadFile.getAbsolutePath());
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	@Path("{csarID}")
	public CSARResource getCSAR(@PathParam("csarID") String csarIDAsString) throws UserException {
		
		CSARsResource.LOG.debug("Searching for CSAR \"{}\".", csarIDAsString);
		
		// try {
		CSARContent csar = fileHandler.getCSAR(new CSARID(csarIDAsString));
		// } catch (UserException exc) {
		// CSARsResource.LOG.warn("An User Exception occured.", exc);
		// }
		
		if (csar != null) {
			CSARsResource.LOG.debug("CSAR \"{}\" was found.", csarIDAsString);
		} else {
			CSARsResource.LOG.warn("CSAR \"{}\" was not found.", csarIDAsString);
		}
		
		return new CSARResource(csar);
		
	}
	
	@DELETE
	@Produces("text/plain")
	public Response deleteCSARs() throws SystemException, UserException {
		
		List<String> notDeleted = new ArrayList<String>();
		
		for (CSARID csarID : fileHandler.getCSARIDs()) {
			CSARsResource.LOG.info("Deleting CSAR \"{}\".", csarID);
			if (!IOpenToscaControlServiceHandler.getOpenToscaControlService().deleteCSAR(csarID).isEmpty()) {
				notDeleted.add(csarID.toString());
			}
			
		}
		
		if (notDeleted.isEmpty()) {
			return Response.ok("All CSARs were deleted.").build();
		} else {
			return Response.serverError().build();
		}
	}
	
	/**
	 * Checks whether the root tosca definitions file, contains servicetemplates
	 * where no build plan is available. If there is no such plan available the
	 * plan builder starts to generate a build plan.
	 * 
	 * @param csarId the CSARID of the definitions to generate build plans for
	 * @return a new CSARID for the repackaged csar if new build plans were
	 *         generated, the same csarid if no build plans were generated, else
	 *         null
	 */
	private CSARID startPlanBuilder(CSARID csarId) {
		Importer planBuilderImporter = new Importer();
		Exporter planBuilderExporter = new Exporter();
		
		List<BuildPlan> buildPlans = planBuilderImporter.importDefs(csarId);
		
		// no buildplan generated <=> nothing to do
		if (buildPlans.isEmpty()) {
			return csarId;
		}
		
		File repackagedCsar = planBuilderExporter.export(buildPlans, csarId);
		try {
			fileHandler.deleteCSAR(csarId);
			return fileHandler.storeCSAR(repackagedCsar.toPath());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserException e) {
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
	private static String createXMLidAsString(String name) {
		
		// RegExp inspired by http://stackoverflow.com/a/5396246/873282
		// NameStartChar without ":"
		// stackoverflow: -dfff, standard: d7fff
		String RANGE_NCNAMESTARTCHAR = "A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d" + "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff" + "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x10000-\\xEFFFF";
		String REGEX_NCNAMESTARTCHAR = "[" + RANGE_NCNAMESTARTCHAR + "]";
		String RANGE_NCNAMECHAR = RANGE_NCNAMESTARTCHAR + "\\-\\.0-9\\u00b7\\u0300-\\u036f\\u203f-\\u2040";
		String REGEX_INVALIDNCNAMESCHAR = "[^" + RANGE_NCNAMECHAR + "]";
		
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
