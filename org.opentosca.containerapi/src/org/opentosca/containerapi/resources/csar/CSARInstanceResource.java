package org.opentosca.containerapi.resources.csar;

import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TBoolean;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This resource represents a CSAR-Instance.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARInstancesResource.class);
	
	// If the csarID is null, there is no CSAR file stored in the Container
	private final CSARID csarID;
	private final int instanceID;
	
	UriInfo uriInfo;
	
	
	public CSARInstanceResource(CSARID csarID, String instanceID) {
		if (null == csarID) {
			this.csarID = null;
			this.instanceID = -1;
			CSARInstanceResource.LOG.error("{} created: {}", this.getClass(), "but the CSAR does not exist");
		} else {
			if ((null == instanceID) || instanceID.equals("")) {
				CSARInstanceResource.LOG.error("CSAR Instance " + instanceID + " does not exit for requested CSAR: {}", csarID.getFileName());
				this.csarID = null;
				this.instanceID = -1;
			} else {
				this.csarID = csarID;
				this.instanceID = Integer.parseInt(instanceID);
				CSARInstanceResource.LOG.debug("{} created: {}", this.getClass(), csarID);
				CSARInstanceResource.LOG.debug("CSAR Instance " + instanceID + " for requested CSAR: {}", this.csarID.getFileName());
			}
		}
	}
	
	/**
	 * Produces the xml which lists the links to the History and the active
	 * plans.
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
	
	public References getReferences() {
		
		CSARInstanceResource.LOG.debug("Access the CSAR instance at " + uriInfo.getAbsolutePath().toString());
		
		if (csarID == null) {
			CSARInstanceResource.LOG.debug("The CSAR does not exist.");
			return null;
		}
		
		// selflink
		References refs = new References();
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "ActivePlans"), XLinkConstants.SIMPLE, "history"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "History"), XLinkConstants.SIMPLE, "history"));
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Post of a PublicPlan. Dedicated to OTHERMANAGEMENT and TERMINATION.
	 * 
	 * @param transferElement
	 * @return Response
	 */
	@POST
	@Consumes(ResourceConstants.TOSCA_XML)
	public Response postManagementPlan(JAXBElement<TPlanDTO> transferElement) {
		
		CSARInstanceResource.LOG.debug("Received a management request to invoke the plan for Instance " + instanceID + " of CSAR " + csarID);
		
		TPlanDTO plan = transferElement.getValue();
		// QName id = plan.getId();
		// QName qname = new QName(id.substring(1, id.indexOf("}")),
		// id.substring(id.indexOf("}") + 1, id.length()));
		// plan.setPlanID(qname);
		// plan.setInternalInstanceInternalID(instanceID);
		
		CSARInstanceResource.LOG.debug("Post of the PublicPlan " + plan.getId());
		
		// TODO return correlation ID
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, instanceID, plan);
		
		return Response.ok(correlationID).build();
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response postBUILDJSONReturnJSON(String json){
		String corr = postManagementPlanJSON(json);
		JsonObject ret = new JsonObject();
		ret.addProperty("CorrelationID", corr);
		return Response.ok(ret.toString()).build();
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.TOSCA_XML)
	public Response postBUILDJSONReturnXML(String json){
		
		return Response.ok(postManagementPlanJSON(json)).build();
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 */
	public String postManagementPlanJSON(String json) {
		
		LOG.debug("Received a build plan for CSAR " + csarID + "\npassed entity:\n   " + json);
		
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		
		LOG.trace(JSONUtils.withoutQuotationMarks(object.get("ID").toString()));
		
		// Example of JSON:
		// {
		// "ID":"BuildPlanNoImpl.war",
		// "Name":"BuildPlanNoImpl.war",
		// "PlanType":"http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan",
		// "PlanLanguage":"http://www.omg.org/spec/BPMN/2.0/",
		// "InputParameters":[
		// {"InputParameter":{"Name":"HypervisorEndpoint","Type":"String","Value":"HypervisorEndpoint","Required":"yes"}},
		// {"InputParameter":{"Name":"HypervisorTenantID","Type":"String","Value":"HypervisorTenantID","Required":"yes"}}
		// ],
		// "OutputParameters":[
		// {"OutputParameter":{"Name":"CorrelationID","Type":"correlation","Required":"yes"}}
		// ],
		// "PlanModelReference":{"Reference":"../servicetemplates/http%253A%252F%252Fopentosca.org%252FBPMN/BPMNLAMPStack/plans/BPMNLAMPStack_buildPlan/BuildPlanNoImpl.war"}
		// }
		
		TPlanDTO plan = new TPlanDTO();
		
		plan.setId(new QName(JSONUtils.withoutQuotationMarks(object.get("ID").toString())));
		plan.setName(JSONUtils.withoutQuotationMarks(object.get("Name").toString()));
		plan.setPlanType(JSONUtils.withoutQuotationMarks(object.get("PlanType").toString()));
		plan.setPlanLanguage(JSONUtils.withoutQuotationMarks(object.get("PlanLanguage").toString()));
		
		JsonArray array = object.get("InputParameters").getAsJsonArray();
		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			para.setName(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("InputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("InputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("InputParameter").getAsJsonObject().get("Type").toString()));
			para.setValue(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("InputParameter").getAsJsonObject().get("Value").toString()));
			plan.getInputParameters().getInputParameter().add(para);
		}
		array = object.get("OutputParameters").getAsJsonArray();
		iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			para.setName(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("OutputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("OutputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(iterator.next().getAsJsonObject().get("OutputParameter").getAsJsonObject().get("Type").toString()));
			plan.getOutputParameters().getOutputParameter().add(para);
		}
		
		String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarID, plan.getId().getLocalPart());
		plan.setId(new QName(namespace, plan.getId().getLocalPart()));
		
		LOG.debug("Post of the Plan " + plan.getId());
		
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, -1, plan);
		
		LOG.debug("Return correlation ID of running plan: " + correlationID);
		
		return correlationID;
		
	}
	
	/**
	 * This returns the History object.
	 * 
	 * @return the History representation
	 */
	@Path("History")
	@Produces(ResourceConstants.LINKED_XML)
	public Object getInstanceHistory() {
		CSARInstanceResource.LOG.debug("Access history");
		return new CSARInstancePlanHistoryResource(csarID, instanceID);
	}
	
	/**
	 * This returns the active plans.
	 * 
	 * @return active plans representation
	 */
	@Path("ActivePlans")
	@Produces(ResourceConstants.LINKED_XML)
	public Object getInstanceActivePublicPlans() {
		CSARInstanceResource.LOG.debug("Access active PublicPlans");
		return new CSARInstanceActivePlansResource(csarID, instanceID);
	}
}
