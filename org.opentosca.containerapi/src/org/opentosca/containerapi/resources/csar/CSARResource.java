package org.opentosca.containerapi.resources.csar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.csar.content.ContentResource;
import org.opentosca.containerapi.resources.csar.content.DirectoryResource;
import org.opentosca.containerapi.resources.csar.content.FileResource;
import org.opentosca.containerapi.resources.csar.servicetemplate.ServiceTemplatesResource;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource represents a CSAR.<br />
 * <br />
 *
 *
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 *
 */
public class CSARResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContentResource.class);

	// If csar is null, CSAR is not stored
	private final CSARContent CSAR;
	UriInfo uriInfo;


	public CSARResource(CSARContent csar) {
		Objects.requireNonNull(csar);

		this.CSAR = csar;
		CSARResource.LOG.info("{} created: {}", this.getClass(), this);
	}

	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getXMLString()).build();
	}

	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getJSONString()).build();
	}

	public References getRefs() {

		if (this.CSAR == null) {
			return null;
		}

		References refs = new References();

		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "Content"), XLinkConstants.SIMPLE, "Content"));
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// "BoundaryDefinitions"), XLinkConstants.SIMPLE,
		// "BoundaryDefinitions"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "MetaData"), XLinkConstants.SIMPLE, "MetaData"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "ServiceTemplates"), XLinkConstants.SIMPLE, "ServiceTemplates"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "TopologyPicture"), XLinkConstants.SIMPLE, "TopologyPicture"));

		// TODO both following links (PlanInstances, PlanResults) have to be
		// replaced as soon as the instance data api is merged into here
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// "PlanInstances"), XLinkConstants.SIMPLE, "PlanInstances"));
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// "PlanResults"), XLinkConstants.SIMPLE, "PlanResults"));

		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// "Instances"), XLinkConstants.SIMPLE, "Instances"));
		CSARResource.LOG.info("Number of References in Root: {}", refs.getReference().size());

		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

		return refs;
	}

	@Path("Content")
	public ContentResource getContent() {
		return new ContentResource(this.CSAR);

	}

	// @Path("BoundaryDefinitions")
	// public BoundsResource getBoundaryDefs() {
	// return new BoundsResource(CSAR.getCSARID());
	// }

	// @Path("Plans")
	// public CSARPlansResource getPuplicPlans() {
	// return new CSARPlansResource(CSAR.getCSARID());
	// }

	// @Path("Instances")
	// public InstancesResource getInstances() {
	// return new InstancesResource(CSAR.getCSARID());
	// }

	// "image/*" will be preferred over "text/xml" when requesting an image.
	// This is a fix for Webkit Browsers who are too dumb for content
	// negotiation.

	@Produces("image/*; qs=2.0")
	@GET
	@Path("TopologyPicture")
	public Response getTopologyPicture() throws SystemException {

		AbstractFile topologyPicture = this.CSAR.getTopologyPicture();

		if (topologyPicture != null) {
			MediaType mt = new MediaType("image", "*");

			// try {
			InputStream is = topologyPicture.getFileAsInputStream();
			return Response.ok(is, mt).header("Content-Disposition", "attachment; filename=\"" + topologyPicture.getName() + "\"").build();
			// } catch (SystemException exc) {
			// CSARResource.LOG.error("An System Exception occured.", exc);
			// }

		}
		return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("No Topology Picture exists in CSAR \"" + this.CSAR.getCSARID() + "\".").build();
	}

	@GET
	@Path("MetaData")
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response getMetaDataJSON() throws SystemException {
		// /containerapi/CSARs/MongoDB_On_VSphere.csar/Content/SELFSERVICE-Metadata/data.json

		DirectoryResource dir = (DirectoryResource) new ContentResource(this.CSAR).getDirectoryOrFile("SELFSERVICE-Metadata");
		FileResource file = (FileResource) dir.getDirectoryOrFile("data.json");
		CSARResource.LOG.trace("Metadata file is of class: {}", file.getClass());

		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(file.getAsJSONString()).build();// .type(MediaType.TEXT_PLAIN).entity("No
		// Topology
		// Picture
		// exists
		// in
		// CSAR
		// \""
		// +
		// CSAR.getCSARID()
		// +
		// "\".").build();
	}

	@Path("ServiceTemplates")
	public ServiceTemplatesResource getServiceTemplates() {

		return new ServiceTemplatesResource(this.CSAR);
	}

	/**
	 * Exports this CSAR.
	 *
	 * @return CSAR as {@code application/octet-stream}. If an error occurred
	 *         during exporting (e.g. during retrieving files from storage
	 *         provider(s)) 500 (internal server error).
	 * @throws SystemException
	 * @throws UserException
	 *
	 * @see ICoreFileService#exportCSAR(CSARID)
	 *
	 */
	@GET
	@Produces(ResourceConstants.OCTET_STREAM)
	public Response downloadCSAR() throws SystemException, UserException {

		CSARID csarID = this.CSAR.getCSARID();

		// try {

		java.nio.file.Path csarFile = FileRepositoryServiceHandler.getFileHandler().exportCSAR(csarID);
		InputStream csarFileInputStream;

		try {
			csarFileInputStream = Files.newInputStream(csarFile);
		} catch (IOException e) {
			throw new SystemException("Retrieving input stream of file \"" + csarFile.toString() + "\" failed.", e);
		}

		// We add Content Disposition header, because exported CSAR file to
		// download should have the correct file name.
		return Response.ok("CSAR \"" + csarID + "\" was successfully exported to \"" + csarFile + "\".").entity(csarFileInputStream).header("Content-Disposition", "attachment; filename=\"" + csarID.getFileName() + "\"").build();

	}

	@DELETE
	@Produces("text/plain")
	public Response delete() {

		CSARID csarID = this.CSAR.getCSARID();

		CSARResource.LOG.info("Deleting CSAR \"{}\".", csarID);
		List<String> errors = IOpenToscaControlServiceHandler.getOpenToscaControlService().deleteCSAR(csarID);

		// if (errors.contains("CSAR has instances.")) {
		// return Response.notModified("CSAR has instances.").build();
		// }

		if (errors.isEmpty()) {
			return Response.ok("Deletion of CSAR " + "\"" + csarID + "\" was sucessful.").build();
		} else {
			String errorList = "";
			for (String err : errors) {
				errorList = errorList + err + "\\n";
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Deletion of CSAR \"" + csarID + "\" failed with errors: " + errorList).build();
		}

	}

}
