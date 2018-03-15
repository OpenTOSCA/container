package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans.PlanInstances;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate.NodeTemplatesResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate.RelationshipTemplatesResource;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 *
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceTemplateInstanceResource {

    private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);

    private final CSARID csarId;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;


    public ServiceTemplateInstanceResource(final CSARID csarId, final QName serviceTemplateID,
                                           final int serviceTemplateInstanceId) {
        this.csarId = csarId;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response doGetXML(@Context final UriInfo uriInfo) {

        final References idr = this.getRefs(uriInfo);

        if (null == idr) {
            Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(idr.getXMLString()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetJSON(@Context final UriInfo uriInfo) {

        final References idr = this.getRefs(uriInfo);

        if (null == idr) {
            Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(idr.getJSONString()).build();
    }

    public References getRefs(final UriInfo uriInfo) {

        final References refs = new References();

        refs.getReference()
            .add(new Reference(Utilities.buildURI(uriInfo, "NodeTemplates"), XLinkConstants.SIMPLE, "NodeTemplates"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(uriInfo, "PlanInstances"), XLinkConstants.SIMPLE, "PlanInstances"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(uriInfo, "Properties"), XLinkConstants.SIMPLE, "Properties"));
        refs.getReference().add(new Reference(Utilities.buildURI(uriInfo, "State"), XLinkConstants.SIMPLE, "State"));
        refs.getReference().add(new Reference(Utilities.buildURI(uriInfo, "RelationshipTemplates"),
            XLinkConstants.SIMPLE, "RelationshipTemplates"));

        // selflink
        refs.getReference()
            .add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
        // } catch (Exception e) {
        // e.printStackTrace();
        // return null;
        // }
    }

    @DELETE
    public Response deleteServiceInstance() {
        final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
        service.deleteServiceInstance(IdConverter.serviceInstanceIDtoURI(this.serviceTemplateInstanceId));
        return Response.noContent().build();
    }

    @Path("NodeTemplates")
    public Object getNodeTemplates() {
        return new NodeTemplatesResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId);
    }

    @Path("RelationshipTemplates")
    public Object getRelationshipTemplates() {
        return new RelationshipTemplatesResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId);
    }

    @Path("/Properties")
    public Object getProperties() {
        return new ServiceTemplateInstancePropertiesResource(this.csarId, this.serviceTemplateID,
            this.serviceTemplateInstanceId);
    }

    @Path("/State")
    public Object getState() {
        return new ServiceTemplateInstanceStateResource(this.serviceTemplateInstanceId);
    }

    @Path("/PlanInstances")
    public Object getPlanInstances() {
        return new PlanInstances(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId);
    }

    // @Path("/NodeTemplates")
    // public Object getNodeInstances() {
    // return new NodeTemplateInstancesResource(csarId, serviceTemplateID,
    // serviceTemplateInstanceId);
    // }

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
    public Response postBUILDJSONReturnJSON(@Context final UriInfo uriInfo,
                                            final String json) throws URISyntaxException, UnsupportedEncodingException {
        final String url = this.postManagementPlanJSON(uriInfo, json);
        final JsonObject ret = new JsonObject();
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
    public Response postBUILDJSONReturnXML(@Context final UriInfo uriInfo,
                                           final String json) throws URISyntaxException, UnsupportedEncodingException {

        final String url = this.postManagementPlanJSON(uriInfo, json);
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
    @Deprecated
    private String postManagementPlanJSON(final UriInfo uriInfo,
                                          final String json) throws UnsupportedEncodingException {

        throw new UnsupportedOperationException();

        // this.log
        // .debug("Received a build plan for CSAR " + this.csarId + "\npassed entity:\n " + json);
        //
        // final JsonParser parser = new JsonParser();
        // final JsonObject object = parser.parse(json).getAsJsonObject();
        //
        // this.log.trace(JSONUtils.withoutQuotationMarks(object.get("ID").toString()));
        //
        // final TPlanDTO plan = new TPlanDTO();
        //
        // plan.setId(new QName(JSONUtils.withoutQuotationMarks(object.get("ID").toString())));
        // plan.setName(JSONUtils.withoutQuotationMarks(object.get("Name").toString()));
        // plan.setPlanType(JSONUtils.withoutQuotationMarks(object.get("PlanType").toString()));
        // plan.setPlanLanguage(JSONUtils.withoutQuotationMarks(object.get("PlanLanguage").toString()));
        //
        // JsonArray array = object.get("InputParameters").getAsJsonArray();
        // Iterator<JsonElement> iterator = array.iterator();
        // while (iterator.hasNext()) {
        // final TParameterDTO para = new TParameterDTO();
        // final JsonObject tmp = iterator.next().getAsJsonObject();
        // para.setName(JSONUtils.withoutQuotationMarks(
        // tmp.get("InputParameter").getAsJsonObject().get("Name").toString()));
        // para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(
        // tmp.get("InputParameter").getAsJsonObject().get("Required").toString())));
        // para.setType(JSONUtils.withoutQuotationMarks(
        // tmp.get("InputParameter").getAsJsonObject().get("Type").toString()));
        // // if a parameter value is not set, just add "" as value
        // if (null != tmp.get("InputParameter").getAsJsonObject().get("Value")) {
        // para.setValue(JSONUtils.withoutQuotationMarks(
        // tmp.get("InputParameter").getAsJsonObject().get("Value").toString()));
        // } else {
        // para.setValue("");
        // }
        // plan.getInputParameters().getInputParameter().add(para);
        // }
        // array = object.get("OutputParameters").getAsJsonArray();
        // iterator = array.iterator();
        // while (iterator.hasNext()) {
        // final TParameterDTO para = new TParameterDTO();
        // final JsonObject tmp = iterator.next().getAsJsonObject();
        // para.setName(JSONUtils.withoutQuotationMarks(
        // tmp.get("OutputParameter").getAsJsonObject().get("Name").toString()));
        // para.setRequired(TBoolean.fromValue(JSONUtils.withoutQuotationMarks(
        // tmp.get("OutputParameter").getAsJsonObject().get("Required").toString())));
        // para.setType(JSONUtils.withoutQuotationMarks(
        // tmp.get("OutputParameter").getAsJsonObject().get("Type").toString()));
        // plan.getOutputParameters().getOutputParameter().add(para);
        // }
        //
        // final String namespace =
        // ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
        // .getNamespaceOfPlan(this.csarId, plan.getId().getLocalPart());
        // plan.setId(new QName(namespace, plan.getId().getLocalPart()));
        //
        // this.log.debug("Post of the Plan " + plan.getId());
        //
        // final String correlationID =
        // IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(
        // this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId, plan);
        //
        // this.log.debug("Return correlation ID of running plan: " + correlationID);
        //
        // final String url = uriInfo.getBaseUri().toString() + "CSARs/" + this.csarId.getFileName()
        // + "/ServiceTemplates/" + URLEncoder.encode(this.serviceTemplateID.toString(), "UTF-8")
        // + "/ServiceTemplateInstances/" + this.serviceTemplateInstanceId + "/PlanInstances/"
        // + correlationID;
        //
        // return url;
    }
}
