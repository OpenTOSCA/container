package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInstance {
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstance.class);
	
	private final CSARID csarID;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final String correlationID;
	private UriInfo uriInfo;
	
	public PlanInstance(CSARID csarID, QName serviceTemplateID, int serviceTemplateInstanceId, String correlationID) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.correlationID = correlationID;
	}
	
	
	/**
	 * Produces the xml which lists the CorrelationIDs of the active
	 * PublicPlans.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(getReferences().getXMLString()).build();
	}
	
	/**
	 * Produces the JSON which lists the links to the History and the active
	 * plans.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(getReferences().getJSONString()).build();
	}
	
	public References getReferences(){
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Logs"), XLinkConstants.SIMPLE, "Logs"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "MetaData"), XLinkConstants.SIMPLE, "MetaData"));
		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID) && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID).contains(correlationID)) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Output"), XLinkConstants.SIMPLE, "Output"));
		}
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "State"), XLinkConstants.SIMPLE, "State"));
		
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	@GET
	@Path("State")
	@Produces(ResourceConstants.TOSCA_JSON)
	public PlanInstanceState getPlanState(@Context UriInfo uriInfo) throws URISyntaxException {
		return new PlanInstanceState(csarID, serviceTemplateID, serviceTemplateInstanceId, correlationID);
	}
	
	@GET
	@Path("Output")
	@Produces(ResourceConstants.TOSCA_JSON)
	public PlanInstanceOutput getPlanOutput(@Context UriInfo uriInfo) throws URISyntaxException {
		return new PlanInstanceOutput(csarID, serviceTemplateID, serviceTemplateInstanceId, correlationID);
	}
	
	@GET
	@Path("Logs")
	@Produces(ResourceConstants.TOSCA_JSON)
	public PlanInstanceLogs getPlanLogs(@Context UriInfo uriInfo) throws URISyntaxException {
		return new PlanInstanceLogs(csarID, serviceTemplateID, serviceTemplateInstanceId, correlationID);
	}
	
	@GET
	@Path("MetaData")
	@Produces(ResourceConstants.TOSCA_JSON)
	public PlanInstanceMetaData getPlanMetaData(@Context UriInfo uriInfo) throws URISyntaxException {
		return new PlanInstanceMetaData(csarID, serviceTemplateID, serviceTemplateInstanceId, correlationID);
	}
	
}
