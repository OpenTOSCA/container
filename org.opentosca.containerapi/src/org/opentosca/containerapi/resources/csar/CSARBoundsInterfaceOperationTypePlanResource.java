package org.opentosca.containerapi.resources.csar;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSARBoundsInterfaceOperationTypePlanResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARBoundsInterfaceOperationTypePlanResource.class);
	CSARID csarID;
	String intName;
	String opName;
	
	
	public CSARBoundsInterfaceOperationTypePlanResource(CSARID csarID, String intName, String opName) {
		
		this.csarID = csarID;
		this.intName = intName;
		this.opName = opName;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			LOG.error("The ToscaEngineService is not alive.");
		}
	}
	
	/**
	 * Builds the references of the Boundary Definitions of a CSAR.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		References refs = new References();
		
		String plan = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getBoundaryPlanOfCSARInterface(csarID, intName, opName).getLocalPart();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), plan), XLinkConstants.SIMPLE, plan));
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("{PlanName}")
	@Produces(ResourceConstants.TOSCA_XML)
	public JAXBElement<?> getPlan(@PathParam("PlanName") String plan) {
		LOG.trace("Return plan " + plan);
		Map<PlanTypes, LinkedHashMap<QName, TPlan>> map = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
		
		IToscaReferenceMapper service = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
		String ns = service.getNamespaceOfPlan(csarID, plan);
		
		QName id = new QName(ns, plan);
		
		for (PlanTypes type : PlanTypes.values()) {
			if (map.get(type).containsKey(id)) {
				TPlan tPlan = map.get(type).get(id);
				return ToscaServiceHandler.getIXMLSerializer().createJAXBElement(tPlan);
			}
		}
		
		return null;
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 */
	@POST
	@Path("{PlanName}")
	@Consumes(ResourceConstants.TOSCA_XML)
	public Response postManagementPlan(@PathParam("PlanName") String plan, JAXBElement<TPlanDTO> planElement) {
		
		CSARBoundsInterfaceOperationTypePlanResource.LOG.debug("Received a plan for CSAR " + csarID);
		
		TPlanDTO planDTO = planElement.getValue();
		
		if (null == planDTO) {
			LOG.error("The given PublicPlan is null!");
			return Response.status(Status.CONFLICT).build();
		}
		
		if (null == planDTO.getId()) {
			LOG.error("The given PublicPlan has no ID!");
			return Response.status(Status.CONFLICT).build();
		}
		
		String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarID, planDTO.getId().getLocalPart());
		planDTO.setId(new QName(namespace, planDTO.getId().getLocalPart()));
		
		LOG.debug("PublicPlan to invoke: " + planDTO.getId());
		
		CSARBoundsInterfaceOperationTypePlanResource.LOG.debug("Post of the PublicPlan " + planDTO.getId());
		
		// TODO return correlation ID
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, -1, planDTO);
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI("http://localhost:1337/containerapi", "/CSARs/" + csarID + "/PlanInstances/" + correlationID), XLinkConstants.SIMPLE, plan));
		
		return Response.status(Response.Status.ACCEPTED).entity(refs.getXMLString()).build();
		
	}
}