package org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BoundsInterfaceOperationPlanResource {

    private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceOperationPlanResource.class);
    CSARID csarID;
    QName serviceTemplateID;
    String intName;
    String opName;

    UriInfo uriInfo;


    public BoundsInterfaceOperationPlanResource(final CSARID csarID, final QName serviceTemplateID,
                                                final String intName, final String opName) {

        this.csarID = csarID;
        this.serviceTemplateID = serviceTemplateID;
        this.intName = intName;
        this.opName = opName;

        if (null == ToscaServiceHandler.getToscaEngineService()) {
            LOG.error("The ToscaEngineService is not alive.");
        }
    }

    /**
     * Returns the Boundary Definitions Node Operation. TODO not yet implemented yet, thus, just returns
     * itself.
     *
     * @param uriInfo
     * @return Response
     * @throws UnsupportedEncodingException
     */
    @GET
    // @Path("{PlanName}")
    @Produces(ResourceConstants.TOSCA_XML)
    public JAXBElement getPlanXML(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        final String planName =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                               .getBoundaryPlanOfCSARInterface(this.csarID, this.intName, this.opName).getLocalPart();

        final TPlan plan = getPlan(planName);

        return ToscaServiceHandler.getIXMLSerializer().createJAXBElement(plan);
    }

    private String getPostPath() throws UnsupportedEncodingException {

        String serviceTemplateID = Utilities.URLencode(this.serviceTemplateID.toString());
        serviceTemplateID = Utilities.URLencode(serviceTemplateID);

        final String path =
            "CSARs/" + this.csarID.getFileName() + "/ServiceTemplates/" + serviceTemplateID + "/Instances";

        LOG.debug("POST URL: {}", path);

        return path;
    }

    /**
     * Returns the Plan.
     *
     * @param uriInfo
     * @return Response
     * @throws UnsupportedEncodingException
     */
    @GET
    // @Path("{PlanName}")
    @Produces(ResourceConstants.TOSCA_JSON)
    public Response getPlanJSON(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {

        final String planName =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                               .getBoundaryPlanOfCSARInterface(this.csarID, this.intName, this.opName).getLocalPart();

        final TPlan plan = getPlan(planName);

        final JsonObject json = new JsonObject();

        if (plan.getPlanType().startsWith("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan")) {
            final URI url = uriInfo.getBaseUriBuilder().path(getPostPath()).build();
            json.add("Reference", new Reference(url.toString(), XLinkConstants.REFERENCE, "PlanPostURL").toJson());
        } else {
            final URI url = uriInfo.getBaseUriBuilder().path(getPostPath()).build();
            json.add("Reference",
                     new Reference(url.toString() + "/{instanceId}", XLinkConstants.REFERENCE, "PlanPostURL").toJson());
        }

        final JsonObject planJson = new JsonObject();
        json.add("Plan", planJson);
        planJson.addProperty("ID", plan.getId());
        planJson.addProperty("Name", plan.getName());
        planJson.addProperty("PlanType", plan.getPlanType());
        planJson.addProperty("PlanLanguage", plan.getPlanLanguage());

        final JsonArray input = new JsonArray();
        try {
            for (final TParameter param : plan.getInputParameters().getInputParameter()) {
                final JsonObject paramObj = new JsonObject();
                final JsonObject paramDetails = new JsonObject();
                paramDetails.addProperty("Name", param.getName());
                paramDetails.addProperty("Type", param.getType());
                paramDetails.addProperty("Required", param.getRequired().value());
                paramObj.add("InputParameter", paramDetails);
                input.add(paramObj);
            }
        }
        catch (final NullPointerException e) {
        }
        planJson.add("InputParameters", input);

        final JsonArray output = new JsonArray();
        try {
            for (final TParameter param : plan.getOutputParameters().getOutputParameter()) {
                final JsonObject paramObj = new JsonObject();
                final JsonObject paramDetails = new JsonObject();
                paramDetails.addProperty("Name", param.getName());
                paramDetails.addProperty("Type", param.getType());
                paramDetails.addProperty("Required", param.getRequired().value());
                paramObj.add("OutputParameter", paramDetails);
                output.add(paramObj);
            }
        }
        catch (final NullPointerException e) {
        }
        planJson.add("OutputParameters", output);

        final JsonObject planModelReference = new JsonObject();
        planModelReference.addProperty("Reference", plan.getPlanModelReference().getReference());
        planJson.add("PlanModelReference", planModelReference);

        return Response.ok(json.toString()).build();
    }

    public TPlan getPlan(final String plan) {
        LOG.trace("Return plan " + plan);
        final Map<PlanTypes, LinkedHashMap<QName, TPlan>> map =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(this.csarID);

        final IToscaReferenceMapper service = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
        final String ns = service.getNamespaceOfPlan(this.csarID, plan);

        final QName id = new QName(ns, plan);

        for (final PlanTypes type : PlanTypes.values()) {
            if (map.get(type).containsKey(id)) {
                final TPlan tPlan = map.get(type).get(id);
                return tPlan;
            }
        }

        return null;
    }

    // /**
    // * PUT for BUILD plans which have no CSAR-Instance-ID yet.
    // *
    // * @param planElement the BUILD PublicPlan
    // * @return Response
    // */
    // @POST
    // @Path("{PlanName}")
    // @Consumes(ResourceConstants.TOSCA_XML)
    // public Response postManagementPlan(@PathParam("PlanName") String plan,
    // JAXBElement<TPlanDTO> planElement) {
    //
    // CSARBoundsInterfaceOperationPlanResource.LOG.debug("Received a plan for
    // CSAR " + csarID);
    //
    // TPlanDTO planDTO = planElement.getValue();
    //
    // if (null == planDTO) {
    // LOG.error("The given PublicPlan is null!");
    // return Response.status(Status.CONFLICT).build();
    // }
    //
    // if (null == planDTO.getId()) {
    // LOG.error("The given PublicPlan has no ID!");
    // return Response.status(Status.CONFLICT).build();
    // }
    //
    // String namespace =
    // ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarID,
    // planDTO.getId().getLocalPart());
    // planDTO.setId(new QName(namespace, planDTO.getId().getLocalPart()));
    //
    // LOG.debug("PublicPlan to invoke: " + planDTO.getId());
    //
    // CSARBoundsInterfaceOperationPlanResource.LOG.debug("Post of the
    // PublicPlan " + planDTO.getId());
    //
    // // TODO return correlation ID
    // String correlationID =
    // IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID,
    // -1, planDTO);
    //
    // References refs = new References();
    //
    // refs.getReference().add(new
    // Reference(Utilities.buildURI("http://localhost:1337/containerapi",
    // "/CSARs/" + csarID + "/PlanInstances/" + correlationID),
    // XLinkConstants.SIMPLE, plan));
    //
    // return
    // Response.status(Response.Status.ACCEPTED).entity(refs.getXMLString()).build();
    //
    // }

    // /**
    // *
    // * TODO consider to deliver this output not under the path
    // * "{PlanName}/PlanWithMinimalInput" but with other MIME type under
    // * "{PlanName}"
    // *
    // * @param uriInfo
    // * @return Response
    // */
    // @GET
    // @Path("{PlanName}/PlanWithMinimalInput")
    // @Produces(ResourceConstants.TOSCA_XML)
    // public JAXBElement<?> getMissingInputFields(@PathParam("PlanName") String
    // plan) {
    // LOG.trace("Return missing input fields of plan " + plan);
    //
    // List<Document> docs = new ArrayList<Document>();
    //
    // List<QName> serviceTemplates =
    // ToscaServiceHandler.getToscaEngineService().getServiceTemplatesInCSAR(csarID);
    // for (QName serviceTemplate : serviceTemplates) {
    // List<String> nodeTemplates =
    // ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(csarID,
    // serviceTemplate);
    //
    // for (String nodeTemplate : nodeTemplates) {
    // Document doc =
    // ToscaServiceHandler.getToscaEngineService().getPropertiesOfNodeTemplate(csarID,
    // serviceTemplate, nodeTemplate);
    // if (null != doc) {
    // docs.add(doc);
    // LOG.trace("Found property document: {}",
    // ToscaServiceHandler.getIXMLSerializer().docToString(doc, false));
    // }
    // }
    // }
    //
    // Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapPlans =
    // ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
    //
    // IToscaReferenceMapper service =
    // ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
    // String ns = service.getNamespaceOfPlan(csarID, plan);
    //
    // QName id = new QName(ns, plan);
    // TPlan tPlan = null;
    // for (PlanTypes type : PlanTypes.values()) {
    // if (mapPlans.get(type).containsKey(id)) {
    // tPlan = mapPlans.get(type).get(id);
    // }
    // }
    //
    // TPlan retPlan = new TPlan();
    // retPlan.setId(tPlan.getId());
    // retPlan.setName(tPlan.getName());
    // retPlan.setPlanLanguage(tPlan.getPlanLanguage());
    // retPlan.setPlanType(tPlan.getPlanType());
    // retPlan.setInputParameters(new InputParameters());
    //
    // List<TParameter> list = new ArrayList<>();
    // for (TParameter para : tPlan.getInputParameters().getInputParameter()) {
    // if (para.getType().equalsIgnoreCase("correlation") ||
    // para.getName().equalsIgnoreCase("csarName") ||
    // para.getName().equalsIgnoreCase("containerApiAddress") ||
    // para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
    // LOG.trace("Skipping parameter {}", para.getName());
    // // list.add(para);
    // } else {
    // LOG.trace("The parameter \"" + para.getName() + "\" may have values in
    // the properties.");
    // String value = "";
    // for (Document doc : docs) {
    // NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
    // LOG.trace("Found {} nodes.", nodes.getLength());
    // if (nodes.getLength() > 0) {
    // value = nodes.item(0).getTextContent();
    // LOG.debug("Found value {}", value);
    // break;
    // }
    // }
    // if (value.equals("")) {
    // LOG.debug("Found empty input paramater {}.", para.getName());
    // list.add(para);
    // } else {
    // }
    // }
    // }
    // retPlan.getInputParameters().getInputParameter().addAll(list);
    //
    // return
    // ToscaServiceHandler.getIXMLSerializer().createJAXBElement(retPlan);
    // }

}
