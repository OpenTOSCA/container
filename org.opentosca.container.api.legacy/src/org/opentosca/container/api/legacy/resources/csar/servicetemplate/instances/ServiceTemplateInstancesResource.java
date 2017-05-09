package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.instancedata.ExistenceChecker;
import org.opentosca.container.api.legacy.instancedata.exception.GenericRestException;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.instancedata.utilities.Constants;
import org.opentosca.container.api.legacy.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.PlanInvocationEngineHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.JSONUtils;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.service.IInstanceDataService;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author User christian.endres@iaas.uni-stuttgart.de
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceTemplateInstancesResource {
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstancesResource.class);
	private final CSARID csarId;
	private final QName serviceTemplateID;
	
	
	public ServiceTemplateInstancesResource(final CSARID csarid, final QName serviceTemplateID) {
		this.csarId = csarid;
		this.serviceTemplateID = serviceTemplateID;
		this.log.debug("Created \"{}\":\"{}\";", serviceTemplateID.getNamespaceURI(), serviceTemplateID.getLocalPart());
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response doGetXML(@Context final UriInfo uriInfo, @QueryParam("BuildPlanCorrelationId") final String buildPlanCorrId) {
		
		final References refs = this.getRefs(uriInfo, buildPlanCorrId);
		
		return Response.ok(refs.getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response doGetJSON(@Context final UriInfo uriInfo, @QueryParam("BuildPlanCorrelationId") final String buildPlanCorrId) {
		
		final References refs = this.getRefs(uriInfo, buildPlanCorrId);
		
		return Response.ok(refs.getJSONString()).build();
	}
	
	public References getRefs(final UriInfo uriInfo, final String buildPlanCorrId) {
		
		// URI serviceInstanceIdURI = null;
		// QName serviceTemplateIDQName = null;
		// try {
		// if (serviceInstanceID != null) {
		// serviceInstanceIdURI = new URI(serviceInstanceID);
		// if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
		// throw new Exception("Error converting serviceInstanceID: invalid
		// format!");
		// }
		// }
		// if (serviceTemplateID != null) {
		// serviceTemplateIDQName = QName.valueOf(serviceTemplateID);
		// }
		// } catch (Exception e1) {
		// throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due
		// to bad variable content: " + e1.getMessage());
		// }
		
		// try {
		
		final References refs = new References();
		
		// get all instance ids
		if ((null == buildPlanCorrId) || buildPlanCorrId.equals("") || !BuildCorrelationToInstanceMapping.instance.knowsCorrelationId(buildPlanCorrId)) {
			
			final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			
			final List<ServiceInstance> serviceInstances = service.getServiceInstancesWithDetails(this.csarId, this.serviceTemplateID, null);
			// List<ServiceInstance> serviceInstances =
			// service.getServiceInstances(serviceInstanceIdURI,
			// serviceTemplateName, serviceTemplateIDQName);
			this.log.debug("Returning all known Service Template instance IDs ({}).", serviceInstances.size());
			
			for (final ServiceInstance serviceInstance : serviceInstances) {
				
				this.log.debug("ST ID of service \"{}\":\"{}\" vs. path \"{}\":\"{}\"", serviceInstance.getServiceTemplateID().getNamespaceURI(), serviceInstance.getServiceTemplateID().getLocalPart(), this.serviceTemplateID.getNamespaceURI(), this.serviceTemplateID.getLocalPart());
				if (serviceInstance.getServiceTemplateID().equals(this.serviceTemplateID)) {
					
					final int instanceId = serviceInstance.getDBId();
					refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(instanceId)), XLinkConstants.SIMPLE, Integer.toString(instanceId)));
				}
				// URI urlToServiceInstance =
				// LinkBuilder.linkToServiceInstance(uriInfo,
				// serviceInstance.getDBId());
				//
				// // build simpleXLink with the internalID as LinkText
				// // TODO: is the id the correct linkText?
				// links.add(new SimpleXLink(urlToServiceInstance,
				// serviceInstance.getDBId() + ""));
			}
		}
		// get instance id of plan correlation only
		else {
			
			final int instanceId = BuildCorrelationToInstanceMapping.instance.getServiceTemplateInstanceIdForBuildPlanCorrelation(buildPlanCorrId);
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(instanceId)), XLinkConstants.SIMPLE, Integer.toString(instanceId)));
			
			this.log.debug("Returning only the Service Template instance ID for correlation {} ({}).", buildPlanCorrId, instanceId);
			
			// URI urlToServiceInstance =
			// LinkBuilder.linkToServiceInstance(uriInfo, instanceId);
			//
			// // build simpleXLink with the internalID as LinkText
			// // TODO: is the id the correct linkText?
			// links.add(new SimpleXLink(urlToServiceInstance, instanceId +
			// ""));
			
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		// ServiceInstanceList sil = new
		// ServiceInstanceList(LinkBuilder.selfLink(uriInfo), links);
		return refs;
		// } catch (Exception e) {
		// throw new GenericRestException(Status.INTERNAL_SERVER_ERROR,
		// e.getMessage());
		// }
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response createServiceInstance(@Context final UriInfo uriInfo, final String xml) {
		
		this.log.debug("Create a instance of CSAR = \"{}\" Service Template = \"{}\"", this.csarId, this.serviceTemplateID);
		
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		try {
			
			final ServiceInstance createdServiceInstance = service.createServiceInstance(this.csarId, this.serviceTemplateID);
			
			// create xlink with the link to the newly created serviceInstance,
			// the link text is the internal serviceInstanceID
			
			final String corr = xml.substring(xml.indexOf(">") + 1, xml.indexOf("</"));
			
			final int serviceTemplateInstanceId = createdServiceInstance.getDBId();
			final String instanceURL = createdServiceInstance.getServiceInstanceID().toString();
			this.log.debug(corr + " : " + corr + " - " + instanceURL);
			
			// correlate true Service Template instance id with temporary one
			
			BuildCorrelationToInstanceMapping.instance.correlateCorrelationIdToServiceTemplateInstanceId(corr, serviceTemplateInstanceId);
			PlanInvocationEngineHandler.planInvocationEngine.correctCorrelationToServiceTemplateInstanceIdMapping(this.csarId, this.serviceTemplateID, corr, serviceTemplateInstanceId);
			final String redirectUrl = uriInfo.getAbsolutePath().toString();
			SimpleXLink response = null;
			if (redirectUrl.endsWith("/")) {
				response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + serviceTemplateInstanceId, "simple");
			} else {
				response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" + serviceTemplateInstanceId, "simple");
			}
			
			this.log.debug("Returning following link: " + response.getHref());
			return Response.ok(response).build();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Path("{" + Constants.ServiceInstanceListResource_getServiceInstance_PARAM + "}")
	public Object getServiceInstance(@PathParam(Constants.ServiceInstanceListResource_getServiceInstance_PARAM) final int id) {
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkServiceInstanceWithException(id, service);
		return new ServiceTemplateInstanceResource(this.csarId, this.serviceTemplateID, id);
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.APPLICATION_JSON)
	public Response postBUILDJSONReturnJSON(@Context final UriInfo uriInfo, final String json) throws URISyntaxException, UnsupportedEncodingException {
		final String url = this.postManagementPlanJSON(uriInfo, json);
		final JsonObject ret = new JsonObject();
		ret.addProperty("PlanURL", url);
		return Response.created(new URI(url)).entity(ret.toString()).build();
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes(ResourceConstants.TEXT_PLAIN)
	@Produces(ResourceConstants.TOSCA_XML)
	public Response postBUILDJSONReturnXML(@Context final UriInfo uriInfo, final String json) throws URISyntaxException, UnsupportedEncodingException {
		
		final String url = this.postManagementPlanJSON(uriInfo, json);
		// return Response.ok(postManagementPlanJSON(uriInfo, json)).build();
		return Response.created(new URI(url)).build();
	}
	
	/**
	 * POST for BUILD plans which have no CSAR-Instance-ID yet.
	 *
	 * @param planElement the BUILD PublicPlan
	 * @return Response
	 * @throws UnsupportedEncodingException
	 */
	private String postManagementPlanJSON(final UriInfo uriInfo, final String json) throws UnsupportedEncodingException {
		
		this.log.debug("Received a build plan for CSAR " + this.csarId + "\npassed entity:\n   " + json);
		
		final JsonParser parser = new JsonParser();
		final JsonObject object = parser.parse(json).getAsJsonObject();
		
		this.log.debug(object.toString());
		
		this.log.trace(JSONUtils.withoutQuotationMarks(object.get("ID").toString()));
		
		final TPlanDTO plan = new TPlanDTO();
		
		plan.setId(new QName(JSONUtils.withoutQuotationMarks(object.get("ID").toString())));
		plan.setName(JSONUtils.withoutQuotationMarks(object.get("Name").toString()));
		plan.setPlanType(JSONUtils.withoutQuotationMarks(object.get("PlanType").toString()));
		plan.setPlanLanguage(JSONUtils.withoutQuotationMarks(object.get("PlanLanguage").toString()));
		
		JsonArray array = object.get("InputParameters").getAsJsonArray();
		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			final TParameterDTO para = new TParameterDTO();
			final JsonObject tmp = iterator.next().getAsJsonObject();
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
			final TParameterDTO para = new TParameterDTO();
			final JsonObject tmp = iterator.next().getAsJsonObject();
			para.setName(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Name").toString()));
			para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Required").toString())));
			para.setType(JSONUtils.withoutQuotationMarks(tmp.get("OutputParameter").getAsJsonObject().get("Type").toString()));
			plan.getOutputParameters().getOutputParameter().add(para);
		}
		
		final String namespace = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(this.csarId, plan.getId().getLocalPart());
		plan.setId(new QName(namespace, plan.getId().getLocalPart()));
		
		this.log.debug("Post of the Plan " + plan.getId());
		
		final String correlationID = IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(this.csarId, this.serviceTemplateID, -1, plan);
		
		this.log.debug("Return correlation ID of running plan: " + correlationID);
		
		final URI url = uriInfo.getBaseUriBuilder().path("/CSARs/").path(this.csarId.getFileName()).path("/ServiceTemplates/").path(Utilities.UrlDoubleEncode(this.serviceTemplateID.toString())).path("/Instances").queryParam("BuildPlanCorrelationId", correlationID).build();

		this.log.debug("Callback URL is {}", url);
		
		return url.toString();
		
	}
}