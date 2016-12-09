package org.opentosca.containerapi.resources.csar.servicetemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.LinkBuilder;
import org.opentosca.containerapi.instancedata.model.NodeInstanceList;
import org.opentosca.containerapi.instancedata.model.ServiceInstanceEntry;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans.PlanInstances;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
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
 * 
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceTemplateInstanceResource {
	
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);
	
	private final CSARID csarId;
	private final QName serviceTemplateID;
	private int serviceTemplateInstanceId;
	
	
	public ServiceTemplateInstanceResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context UriInfo uriInfo) {
		
		ServiceInstanceEntry idr = getRefs(uriInfo);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo) {
		
		ServiceInstanceEntry idr = getRefs(uriInfo);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.toJSON()).build();
	}
	
	public ServiceInstanceEntry getRefs(UriInfo uriInfo) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		URI serviceInstanceIDtoURI = IdConverter.serviceInstanceIDtoURI(serviceTemplateInstanceId);
		
		try {
			// self link is only link at the moment in the main list
			List<SimpleXLink> serviceInstanceLinks = new LinkedList<SimpleXLink>();
			serviceInstanceLinks.add(LinkBuilder.selfLink(uriInfo));
			
			// its ensured that this serviceInstance exists
			List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIDtoURI, null, null);
			ServiceInstance serviceInstance = serviceInstances.get(0);
			
			// extract values
			
			// build nodeInstanceList
			List<NodeInstance> result = service.getNodeInstances(null, null, null, serviceInstanceIDtoURI);
			List<SimpleXLink> nodeInstanceLinks = new LinkedList<SimpleXLink>();
			
			for (NodeInstance nodeInstance : result) {
				URI uriToNodeInstance = LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId());
				// build simpleXLink with the nodeInstanceID as LinkText
				nodeInstanceLinks.add(new SimpleXLink(uriToNodeInstance, nodeInstance.getNodeInstanceID().toString()));
			}
			// we dont want a self link because the InstanceList is part of
			// another list already containing a self link
			NodeInstanceList nil = new NodeInstanceList(null, nodeInstanceLinks);
			
			ServiceInstanceEntry sie = new ServiceInstanceEntry(serviceInstance, serviceInstanceLinks, nil);
			
			return sie;
		} catch (Exception e) {
			return null;
		}
	}
	
	// @DELETE
	// public Response deleteServiceInstance() {
	// IInstanceDataService service =
	// InstanceDataServiceHandler.getInstanceDataService();
	// service.deleteServiceInstance(IdConverter.serviceInstanceIDtoURI(id));
	// return Response.noContent().build();
	// }
	
	@Path("/Properties")
	public Object getProperties() {
		return new ServiceTemplateInstancePropertiesResource(csarId, serviceTemplateID, serviceTemplateInstanceId);
	}
	
	@Path("/State")
	public Object getState() {
		return new ServiceTemplateInstancePropertiesResource(csarId, serviceTemplateID, serviceTemplateInstanceId);
	}
	
	@Path("/PlanInstances")
	public Object getPlanInstances() {
		return new PlanInstances(csarId, serviceTemplateID, serviceTemplateInstanceId);
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response postBUILDJSONReturnJSON(@Context UriInfo uriInfo, String json) throws URISyntaxException, UnsupportedEncodingException {
		String url = postManagementPlanJSON(uriInfo, json);
		JsonObject ret = new JsonObject();
		ret.addProperty("PlanURL", url);
		return Response.created(new URI(url)).entity(ret.toString()).build();
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.TOSCA_XML)
	public Response postBUILDJSONReturnXML(@Context UriInfo uriInfo, String json) throws URISyntaxException, UnsupportedEncodingException {
		
		String url = postManagementPlanJSON(uriInfo, json);
		// return Response.ok(postManagementPlanJSON(uriInfo, json)).build();
		return Response.created(new URI(url)).build();
	}
	
	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws UnsupportedEncodingException
	 */
	private String postManagementPlanJSON(UriInfo uriInfo, String json) throws UnsupportedEncodingException {
		
		log.debug("Received a build plan for CSAR " + csarId + "\npassed entity:\n   " + json);
		
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		
		log.trace(JSONUtils.withoutQuotationMarks(object.get("ID").toString()));
		
		TPlanDTO plan = new TPlanDTO();
		
		plan.setId(new QName(JSONUtils.withoutQuotationMarks(object.get("ID").toString())));
		plan.setName(JSONUtils.withoutQuotationMarks(object.get("Name").toString()));
		plan.setPlanType(JSONUtils.withoutQuotationMarks(object.get("PlanType").toString()));
		plan.setPlanLanguage(JSONUtils.withoutQuotationMarks(object.get("PlanLanguage").toString()));
		
		JsonArray array = object.get("InputParameters").getAsJsonArray();
		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			JsonObject tmp = iterator.next().getAsJsonObject();
			para.setName(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Type").toString()));
			// if a parameter value is not set, just add "" as value
			if (null != tmp.get("InputParameter").getAsJsonObject().get("Value")) {
				para.setValue(JSONUtils.withoutQuotationMarks(tmp.get("InputParameter").getAsJsonObject().get("Value").toString()));
			} else {
				para.setValue("");
			}
			plan.getInputParameters().getInputParameter().add(para);
		}
		array = object.get("OutputParameters").getAsJsonArray();
		iterator = array.iterator();
		while (iterator.hasNext()) {
			TParameterDTO para = new TParameterDTO();
			JsonObject tmp = iterator.next().getAsJsonObject();
			para.setName(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Type").toString()));
			plan.getOutputParameters().getOutputParameter().add(para);
		}
		
		String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarId, plan.getId().getLocalPart());
		plan.setId(new QName(namespace, plan.getId().getLocalPart()));
		
		log.debug("Post of the Plan " + plan.getId());
		
		String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarId, serviceTemplateID, serviceTemplateInstanceId, plan);
		
		log.debug("Return correlation ID of running plan: " + correlationID);
		
		String url = uriInfo.getBaseUri().toString() + "CSARs/" + csarId.getFileName() + "/ServiceTemplates/" + URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") + "/ServiceTemplateInstances/" + serviceTemplateInstanceId + "/PlanInstances/" + correlationID;
		
		return url;
		
	}
	
}