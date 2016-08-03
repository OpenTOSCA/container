package org.opentosca.containerapi.resources.csar;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
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

	String plan = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
	    .getBoundaryPlanOfCSARInterface(csarID, intName, opName).getLocalPart();

	refs.getReference().add(
	    new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), plan), XLinkConstants.SIMPLE, plan));

	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
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
	Map<PlanTypes, LinkedHashMap<QName, TPlan>> map = ToscaServiceHandler.getToscaEngineService()
	    .getToscaReferenceMapper().getCSARIDToPlans(csarID);

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
}