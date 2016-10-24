package org.opentosca.containerapi.resources.csar;

import java.util.Iterator;

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

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
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
 * The representation lists the IDs of the CSAR-Instances for a CSARID.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstancesResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARInstancesResource.class);
	
	private final CSARID csarID;
	
	UriInfo uriInfo;
	
	
	public CSARInstancesResource(CSARID csarID) {
		this.csarID = csarID;
		if (null == csarID) {
			CSARInstancesResource.LOG.debug("{} created: {}", this.getClass(), "but the CSAR does not exist");
		} else {
			CSARInstancesResource.LOG.debug("{} created: {}", this.getClass(), csarID);
			CSARInstancesResource.LOG.debug("CSAR Instance list for requested CSAR: {}", this.csarID.getFileName());
		}
	}
	
	/**
	 * Produces the xml which shows the CSAR instances.
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
	 * Produces the JSON which shows the CSAR instances.
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
		
		if (csarID == null) {
			CSARInstancesResource.LOG.debug("The CSAR does not exist.");
			return null;
		}
		
		CSARInstancesResource.LOG.debug("Return available instances for CSAR {}.", csarID);
		
		References refs = new References();
		
		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getInstancesOfCSAR(csarID)) {
			for (CSARInstanceID id : CSARInstanceManagementHandler.csarInstanceManagement.getInstancesOfCSAR(csarID)) {
				refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(id.getInstanceID())), XLinkConstants.SIMPLE, Integer.toString(id.getInstanceID())));
			}
		}
		
		CSARInstancesResource.LOG.debug("Number of References in Root: {}", refs.getReference().size());
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns the CSAR-Instance representation for the given ID.
	 * 
	 * @param instanceID
	 * @return the representation object
	 */
	@Path("{instanceID}")
	@Produces(ResourceConstants.LINKED_XML)
	public Object getInstance(@PathParam("instanceID") String instanceID) {
		return new CSARInstanceResource(csarID, instanceID);
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 */
	@POST
	@Consumes(ResourceConstants.TOSCA_XML)
	public Response postManagementPlan(JAXBElement<TPlanDTO> planElement) {
		
		CSARInstancesResource.LOG.debug("Received a build plan for CSAR " + csarID);
		
		TPlanDTO plan = planElement.getValue();
		
		if (null == plan) {
			LOG.error("The given PublicPlan is null!");
			return Response.status(Status.CONFLICT).build();
		}
		
		if (null == plan.getId()) {
			LOG.error("The given PublicPlan has no ID!");
			return Response.status(Status.CONFLICT).build();
		}
		
		// if (null == plan.getId() || plan.getId().isEmpty()) {
		//
		// String id = plan.getId();
		// QName qname = new QName(id.substring(1, id.indexOf("}")),
		// id.substring(id.indexOf("}") + 1, id.length()));
		// plan.setPlanID(qname);
		// }
		
		String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarID, plan.getId().getLocalPart());
		plan.setId(new QName(namespace, plan.getId().getLocalPart()));
		
		LOG.debug("PublicPlan to invoke: " + plan.getId());
		
		CSARInstancesResource.LOG.debug("Post of the PublicPlan " + plan.getId());
		
		// TODO return correlation ID
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, -1, plan);
		
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
	public Response postManagementPlanJSON(String json) {
		
		CSARInstancesResource.LOG.debug("Received a build plan for CSAR " + csarID + "\npassed entity:\n   " + json);
		
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
		
		LOG.debug("Plan to invoke: " + plan.getId());
		
		CSARInstancesResource.LOG.debug("Post of the PublicPlan " + plan.getId());
		
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, -1, plan);
		
		return Response.ok(correlationID).build();
		
	}
	
	
}
