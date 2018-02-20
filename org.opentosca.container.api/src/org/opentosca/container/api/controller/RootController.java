/*******************************************************************************
 * Copyright 2017 University of Stuttgart
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.dto.ResourceSupport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;

@Path("/")
@Api("/")
@SwaggerDefinition(
        info = @Info(
        		title = "Swagger API for OpenTOSCA Container",
                description = "This API provides access to the REST-based opearations that allow to query and manipulate various entities managed by the OpenTOSCA container, as well as to execute certain operations on it.",
                version = "2.0",  
                termsOfService = "",
                contact = @Contact(name = "OpenTOSCA", url = "http://opentosca.org"),
                license = @License(name = "Apache License, Version 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")),
        basePath = "/",
        host = "localhost:1337"
)
public class RootController {
	
	@Context
	private UriInfo uriInfo;


	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Gets the root resource", response = ResourceSupport.class, responseContainer = "List")
	public Response getRoot() {
		final ResourceSupport links = new ResourceSupport();
		links.add(Link.fromResource(RootController.class).rel("self").baseUri(this.uriInfo.getBaseUri()).build());
		links.add(Link.fromResource(CsarController.class).rel("csars").baseUri(this.uriInfo.getBaseUri()).build());
		
		return Response.ok(links).build();
	}
}
