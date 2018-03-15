package org.opentosca.container.api.legacy.portability;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;

/**
 * Root-Resource represents all the portabilityAPI and all its features by linking to additional
 * Resources<br />
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */

@Path("/portability")
public class PortabilityRoot {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response doGet() {

        final List<SimpleXLink> links = new LinkedList<>();
        links.add(LinkBuilder.selfLink(this.uriInfo));
        links.add(new SimpleXLink(LinkBuilder.linkToArtifactList(this.uriInfo), "Artifacts"));
        links.add(new SimpleXLink(LinkBuilder.linkToPoliciesList(this.uriInfo), "Policies"));

        final PortabilityEntry pe = new PortabilityEntry(links);

        return Response.ok(pe).build();
    }

    @Path("/artifacts")
    public Object getArtifacts() {
        return new ArtifactsListResource();
    }

    @Path("/policies")
    public Object getPolicies() {
        return new PoliciesListResource();
    }
}
