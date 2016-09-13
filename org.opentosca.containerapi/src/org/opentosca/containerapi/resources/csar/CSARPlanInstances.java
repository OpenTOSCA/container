package org.opentosca.containerapi.resources.csar;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporary class until instance data api is merged into the csars api
 * 
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author User
 *
 */
public class CSARPlanInstances {
	// http://localhost:1337/containerapi/CSARs/BPMNLAMPStack.csar/PlanInstances/1473411281089-0
	
	private final Logger LOG = LoggerFactory.getLogger(CSARPlanInstances.class);
	
	CSARContent csar;
	
	
	public CSARPlanInstances(CSARContent csar) {
		this.csar = csar;
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		if (csar == null) {
			return Response.status(404).build();
		}
		
		References refs = new References();
		
		for (String corr : CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csar.getCSARID())) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), corr), XLinkConstants.SIMPLE, corr));
		}
		// LOG.info("Number of References in Root: {}",
		// refs.getReference().size());
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	//	@GET
	//	@Path("{corr}")
	//	@Consumes(ResourceConstants.TOSCA_XML)
	//	@Produces(MediaType.APPLICATION_XML)
	//	public Response getStatusOfPlanXML(@PathParam("corr") String corr) throws URISyntaxException {
	//		
	//		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()) && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()).contains(corr)) {
	//			String url = Utilities.buildURI("http://localhost:1337/containerapi", "/CSARs/" + csar.getCSARID() + "/PlanResults/" + corr);
	//			URI uri = new URI(url);
	//			LOG.trace("Redirect for correlation {}:\n{}", corr, uri);
	//			return Response.seeOther(uri).build();// status(Response.Status.SEE_OTHER).header("",
	//		} else if (null != CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csar.getCSARID()) && CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csar.getCSARID()).contains(corr)) {
	//			LOG.trace("Pending for correlation {}", corr);
	//			return Response.ok("<response><status>PENDING</status></response>", MediaType.APPLICATION_XML).build();
	//		} else {
	//			LOG.warn("Correlation not known for corr ", corr);
	//			return Response.status(Response.Status.BAD_REQUEST).entity("<response>Given correlation is not known.</response>").build();
	//		}
	//	}
	
	@GET
	@Path("{corr}")
	@Consumes(ResourceConstants.TOSCA_XML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatusOfPlanJSON(@PathParam("corr") String corr) throws URISyntaxException {
		
		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()) && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()).contains(corr)) {
			String url = Utilities.buildURI("http://localhost:1337/containerapi", "/CSARs/" + csar.getCSARID() + "/PlanResults/" + corr);
			URI uri = new URI(url);
			LOG.trace("Redirect for correlation {}:\n{}", corr, uri);
			return Response.seeOther(uri).build();
		} else if (null != CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csar.getCSARID()) && CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csar.getCSARID()).contains(corr)) {
			LOG.trace("Pending for correlation {}", corr);
			return Response.ok("<response><status>PENDING</status></response>", MediaType.APPLICATION_XML).build();
		} else {
			LOG.warn("Correlation not known for corr ", corr);
			return Response.status(Response.Status.BAD_REQUEST).entity("<response>Given correlation is not known.</response>").build();
		}
	}
	
}
