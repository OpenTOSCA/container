package org.opentosca.containerapi.resources.csar.boundarydefinitions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPlan.InputParameters;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), plan + "/PlanWithMinimalInput"), XLinkConstants.SIMPLE, "PlanWithMinimalInput"));
		
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
	public JAXBElement<?> getPlanXML(@PathParam("PlanName") String plan) {
		return getPlan(plan);
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
	@Produces(ResourceConstants.TOSCA_JSON)
	public JAXBElement<?> getPlanJSON(@PathParam("PlanName") String plan) {
		return getPlan(plan);
	}
	
	public JAXBElement<?> getPlan(String plan) {
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
	 * 
	 * TODO consider to deliver this output not under the path
	 * "{PlanName}/PlanWithMinimalInput" but with other MIME type under
	 * "{PlanName}"
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("{PlanName}/PlanWithMinimalInput")
	@Produces(ResourceConstants.TOSCA_XML)
	public JAXBElement<?> getMissingInputFields(@PathParam("PlanName") String plan) {
		LOG.trace("Return missing input fields of plan " + plan);
		
		List<Document> docs = new ArrayList<Document>();
		
		List<QName> serviceTemplates = ToscaServiceHandler.getToscaEngineService().getServiceTemplatesInCSAR(csarID);
		for (QName serviceTemplate : serviceTemplates) {
			List<String> nodeTemplates = ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(csarID, serviceTemplate);
			
			for (String nodeTemplate : nodeTemplates) {
				Document doc = ToscaServiceHandler.getToscaEngineService().getPropertiesOfNodeTemplate(csarID, serviceTemplate, nodeTemplate);
				if (null != doc) {
					docs.add(doc);
					LOG.trace("Found property document: {}", ToscaServiceHandler.getIXMLSerializer().docToString(doc, false));
				}
			}
		}
		
		Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapPlans = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
		
		IToscaReferenceMapper service = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
		String ns = service.getNamespaceOfPlan(csarID, plan);
		
		QName id = new QName(ns, plan);
		TPlan tPlan = null;
		for (PlanTypes type : PlanTypes.values()) {
			if (mapPlans.get(type).containsKey(id)) {
				tPlan = mapPlans.get(type).get(id);
			}
		}
		
		TPlan retPlan = new TPlan();
		retPlan.setId(tPlan.getId());
		retPlan.setName(tPlan.getName());
		retPlan.setPlanLanguage(tPlan.getPlanLanguage());
		retPlan.setPlanType(tPlan.getPlanType());
		retPlan.setInputParameters(new InputParameters());
		
		List<TParameter> list = new ArrayList<>();
		for (TParameter para : tPlan.getInputParameters().getInputParameter()) {
			if (para.getType().equalsIgnoreCase("correlation") || para.getName().equalsIgnoreCase("csarName") || para.getName().equalsIgnoreCase("containerApiAddress") || para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
				LOG.trace("Skipping parameter {}", para.getName());
				// list.add(para);
			} else {
				LOG.trace("The parameter \"" + para.getName() + "\" may have values in the properties.");
				String value = "";
				for (Document doc : docs) {
					NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
					LOG.trace("Found {} nodes.", nodes.getLength());
					if (nodes.getLength() > 0) {
						value = nodes.item(0).getTextContent();
						LOG.debug("Found value {}", value);
						break;
					}
				}
				if (value.equals("")) {
					LOG.debug("Found empty input paramater {}.", para.getName());
					list.add(para);
				} else {
				}
			}
		}
		retPlan.getInputParameters().getInputParameter().addAll(list);
		
		return ToscaServiceHandler.getIXMLSerializer().createJAXBElement(retPlan);
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