package org.opentosca.container.api.legacy.resources.marketplace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
@Path("/marketplace")
public class MarketplaceRootResource {

    @Context
    UriInfo uriInfo;


    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML() {
        return Response.ok(this.getRefs().getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON() {
        return Response.ok(this.getRefs().getJSONString()).build();
    }

    public References getRefs() {
        final References refs = new References();

        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "servicetemplates"),
                XLinkConstants.SIMPLE, "servicetemplates"));
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        return refs;
    }

    @Path("/servicetemplates")
    public MarketplaceServiceTemplatesResource getServiceTemplates() {
        return new MarketplaceServiceTemplatesResource();
    }
}
