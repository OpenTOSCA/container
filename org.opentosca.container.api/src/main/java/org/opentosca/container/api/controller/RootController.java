/*******************************************************************************
 * Copyright 2017-2019 University of Stuttgart
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.opentosca.container.api.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ResourceSupport;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.web.bind.annotation.RestController;

@SwaggerDefinition(info = @Info(title = "Public API for OpenTOSCA Container",
                                description = "API access to query entities and manipulate them using plans",
                                version = "2.1.0", termsOfService = "",
                                contact = @Contact(name = "OpenTOSCA", url = "http://opentosca.org",
                                                   email = "opentosca@iaas.uni-stuttgart.de"),
                                license = @License(name = "Apache License, Version 2.0",
                                                   url = "https://www.apache.org/licenses/LICENSE-2.0")))
@Path("/")
@RestController
public class RootController {

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRoot() {
        final ResourceSupport links = new ResourceSupport();
        links.add(Link.fromResource(RootController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());
        links.add(Link.fromResource(CsarController.class).rel("csars").baseUri(this.uriInfo.getBaseUri()).build());
        links.add(
            Link.fromResource(SituationsController.class).rel("situations").baseUri(this.uriInfo.getBaseUri())
                      .build());

        // TODO: This does not work anymore because the legacy API has been removed
        // Link to plan builder resources
        // links.add(Link.fromUriBuilder(this.uriInfo.getBaseUriBuilder().path("containerapi").path("planbuilder"))
        // .rel("planbuilder").baseUri(this.uriInfo.getBaseUri()).build());

        return Response.ok(links).build();
    }
    
    @GET
    @Path("favicon.ico")
    // this just stubs out the favicon endpoint to shut error-logging for requests to it up
    public Response faviconStub() {
        return Response.noContent().build();
    }
}
