package org.opentosca.container.api.legacy.resources.csar.control;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Resource shows all ongoing DeploymentProcesses within the Container.
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 *
 */
@Path("/CSARControl")
public class CSARControl {

	private static Logger LOG = LoggerFactory.getLogger(CSARControl.class);
	private final ICoreFileService fileHandler;
	@Context
	UriInfo uriInfo;
	@Context
	Request request;


	public CSARControl() {
		this.fileHandler = FileRepositoryServiceHandler.getFileHandler();
		CSARControl.LOG.info("{} created: {}", this.getClass(), this);
	}

	@GET
	@Produces(ResourceConstants.TOSCA_XML)
	public Response getReferences() {
		final References refs = new References();
		// If a CSAR file is stored, it automatically is an ongoing
		// DeploymentProcess
		for (final CSARID csarID : this.fileHandler.getCSARIDs()) {
			final Reference ref = new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), csarID.toString()), XLinkConstants.SIMPLE, csarID.toString());
			refs.getReference().add(ref);
		}

		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}

	@Path("{id}")
	public DeploymentProcessResource getDeploymentProcessResource(@PathParam("id") final String id) {
		CSARID processID = null;
		CSARControl.LOG.info("Trying to find DeploymentProcess with id: {}", id);
		for (final CSARID csarID : this.fileHandler.getCSARIDs()) {
			if (csarID.toString().trim().equals(id.trim())) {
				CSARControl.LOG.info("Found DeploymentProcess with id: {}", id);
				processID = csarID;
			}
		}
		return new DeploymentProcessResource(processID);
	}
}
