package org.opentosca.container.api.legacy.resources.csar.servicetemplate;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions.BoundsResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.ServiceTemplateInstancesResource;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class ServiceTemplateResource {

    private final Logger log = LoggerFactory.getLogger(ServiceTemplateResource.class);
    private final CSARContent csarContent;
    private final QName serviceTemplateID;
    private UriInfo uriInfo;


    public ServiceTemplateResource(final CSARContent csarContent, final String serviceTemplateID) {

        this.csarContent = csarContent;
        final String namespace = serviceTemplateID.substring(1, serviceTemplateID.indexOf("}"));
        final String localName = serviceTemplateID.substring(serviceTemplateID.indexOf("}") + 1);
        this.serviceTemplateID = new QName(namespace, localName);
        this.log.info("{} created: \"{}\":\"{}\"; out of \"{}\"", this.getClass(),
            this.serviceTemplateID.getNamespaceURI(), this.serviceTemplateID.getLocalPart(), serviceTemplateID);
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getJSONString()).build();
    }

    public References getRefs() throws UnsupportedEncodingException {

        if (this.csarContent == null) {
            return null;
        }

        final References refs = new References();

        refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "BoundaryDefinitions"),
            XLinkConstants.SIMPLE, "BoundaryDefinitions"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo, "Instances"), XLinkConstants.SIMPLE, "Instances"));

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
    }

    @Path("BoundaryDefinitions")
    public BoundsResource getBoundaryDefs() {
        return new BoundsResource(this.csarContent.getCSARID(), this.serviceTemplateID);
    }

    @Path("Instances")
    public ServiceTemplateInstancesResource getInstances() {
        this.log.debug("Create ST instances list resource for {}", this.serviceTemplateID);
        return new ServiceTemplateInstancesResource(this.csarContent.getCSARID(), this.serviceTemplateID);
    }
}
