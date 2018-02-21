package org.opentosca.container.api.legacy.resources.marketplace;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.csar.CSARsResource;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.connector.winery.WineryConnector;
import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class MarketplaceServiceTemplateResource {

    UriInfo uriInfo;

    private final WineryConnector connector = new WineryConnector();
    private final QName serviceTemplate;


    public MarketplaceServiceTemplateResource(final QName qname) {
        this.serviceTemplate = qname;
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getJSONString()).build();
    }

    public References getRefs() {
        final References refs = new References();

        refs.getReference()
            .add(new Reference(this.getWineryUri(), XLinkConstants.REFERENCE, this.serviceTemplate.toString()));
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
    }

    private String getWineryUri() {
        final String encodedNamespace = Utilities.URLencode(
            Utilities.URLencode(this.serviceTemplate.getNamespaceURI()));
        return Utilities.buildURI(this.connector.getWineryPath() + "servicetemplates/" + encodedNamespace,
            this.serviceTemplate.getLocalPart());
    }

    @POST
    public Response deploy(@Context final UriInfo uriInfo)
        throws MalformedURLException, IOException, URISyntaxException {
        this.uriInfo = uriInfo;
        // example url:
        // http://localhost:8080/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fdeclarative%252Fbpel/BPELStack/?csar
        final String csarUrl = this.getWineryUri() + "/?csar";

        final InputStream inputStream = new URL(csarUrl).openConnection().getInputStream();

        final CSARsResource res = new CSARsResource();

        final CSARID csarId = res.storeCSAR(this.serviceTemplate.getLocalPart() + ".csar", inputStream);

        final String csarsResourcePath = this.uriInfo.getAbsolutePath().getScheme() + "://"
            + this.uriInfo.getAbsolutePath().getHost() + ":" + this.uriInfo.getAbsolutePath().getPort()
            + "/containerapi/CSARs/" + csarId.toString();

        return Response.created(URI.create(csarsResourcePath)).build();
    }

}
