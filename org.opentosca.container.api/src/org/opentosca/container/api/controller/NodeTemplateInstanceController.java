package org.opentosca.container.api.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opentosca.container.api.dto.NodeTemplateInstanceDTO;
import org.opentosca.container.api.dto.NodeTemplateInstanceListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.api.util.ModelUtil;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
public class NodeTemplateInstanceController {

    @ApiParam("ID of node template")
    @PathParam("nodetemplate")
    String nodetemplate;

    @ApiParam("ID of CSAR")
    @PathParam("csar")
    String csar;

    @ApiParam("qualified name of the service template")
    @PathParam("servicetemplate")
    String servicetemplate;

    @Context
    UriInfo uriInfo;

    private static final Logger logger = LoggerFactory.getLogger(NodeTemplateInstanceController.class);

    private final InstanceService instanceService;

    public NodeTemplateInstanceController(final InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get all instances of a node template", response = NodeTemplateInstanceListDTO.class)
    public Response getNodeTemplateInstances(@QueryParam(value = "state") final List<NodeTemplateInstanceState> states,
                                             @QueryParam(value = "source") final List<Long> relationIds) {
        final QName nodeTemplateQName =
            new QName(QName.valueOf(this.servicetemplate).getNamespaceURI(), this.nodetemplate);
        final Collection<NodeTemplateInstance> nodeInstances =
            this.instanceService.getNodeTemplateInstances(nodeTemplateQName);
        logger.debug("Found <{}> instances of NodeTemplate \"{}\" ", nodeInstances.size(), this.nodetemplate);

        final NodeTemplateInstanceListDTO list = new NodeTemplateInstanceListDTO();

        for (final NodeTemplateInstance i : nodeInstances) {
            if (states != null && !states.isEmpty() && !states.contains(i.getState())) {
                // skip this node instance, as it not has the proper state
                continue;
            }

            if (relationIds != null && !relationIds.isEmpty()) {
                for (final RelationshipTemplateInstance relInstance : i.getOutgoingRelations()) {
                    if (!relationIds.contains(relInstance.getId())) {
                        // skip this node instance, as it is no source of the given relation
                        continue;
                    }
                }
            }
            
            if(!i.getServiceTemplateInstance().getTemplateId().toString().equals(this.servicetemplate)) {
                continue;
            }

            final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(i);
            dto.add(UriUtil.generateSubResourceLink(this.uriInfo, dto.getId().toString(), false, "self"));

            list.add(dto);
        }

        list.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(list).build();
    }

    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response createNodeTemplateInstance(@Context final UriInfo uriInfo, final String serviceTemplateInstanceId) {
        try {
            final NodeTemplateInstance createdInstance =
                this.instanceService.createNewNodeTemplateInstance(this.csar, this.servicetemplate, this.nodetemplate,
                                                                   Long.parseLong(serviceTemplateInstanceId));
            final URI instanceURI = UriUtil.generateSubResourceURI(uriInfo, createdInstance.getId().toString(), false);
            return Response.ok(instanceURI).build();
        }
        catch (final IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        catch (InstantiationException | IllegalAccessException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a node template instance", response = NodeTemplateInstanceDTO.class)
    public Response getNodeTemplateInstance(@ApiParam("ID of node template instance") @PathParam("id") final Long id) {

        final NodeTemplateInstance instance =
            this.instanceService.resolveNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
        final NodeTemplateInstanceDTO dto = NodeTemplateInstanceDTO.Converter.convert(instance);

        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "state", false, "state"));
        dto.add(UriUtil.generateSubResourceLink(this.uriInfo, "properties", false, "properties"));
        dto.add(UriUtil.generateSelfLink(this.uriInfo));

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response deleteNodeTemplateInstance(@PathParam("id") final Long id) {
        this.instanceService.deleteNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/state")
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Get state of a node template instance", response = String.class)
    public Response getNodeTemplateInstanceState(@ApiParam("ID node template instance") @PathParam("id") final Long id) {
        final NodeTemplateInstanceState state =
            this.instanceService.getNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id);
        return Response.ok(state.toString()).build();
    }

    @PUT
    @Path("/{id}/state")
    @Consumes({MediaType.TEXT_PLAIN})
    @ApiOperation(hidden = true, value = "")
    public Response updateNodeTemplateInstanceState(@PathParam("id") final Long id, final String request) {
        try {
            this.instanceService.setNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id, request);
        }
        catch (final IllegalArgumentException e) { // this handles a null request too
            return Response.status(Status.BAD_REQUEST).build();
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("/{id}/managementoperation")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(hidden = true, value = "")
    public Response performManagementOperation(@PathParam("id") final Long id,
    		@ApiParam(required = true,
            value = "operation input parameters") final String parameters) {
    	
    	HttpClient httpclient = HttpClients.createDefault();
    	HttpPost httppost = new HttpPost("http://localhost:8086/ManagementBus/v1/invoker");

    	// Request parameters and other properties.
    	StringEntity params = null;
		try {
			params = new StringEntity(parameters);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		httppost.setEntity(params);
		
		// get state of operation and operation interface for further processing
		String nextState = this.instanceService.getNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id).name();

        //Convert String to JSON Object
        JSONParser parser = new JSONParser();
        JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(parameters);
		} catch (org.json.simple.parser.ParseException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		JSONObject jsonChildObject = null;
		try {
			jsonChildObject = (JSONObject)parser.parse(json.get("invocation-information").toString());
		} catch (org.json.simple.parser.ParseException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		// if lifecycle interface, set status of node accordingly
        if (jsonChildObject.get("interface").toString().equals("http://www.example.com/interfaces/lifecycle")) {
        	String operation = jsonChildObject.get("operation").toString();        	
        	switch (operation) {
        	case "uninstall":
        		// set nextState to DELETED
        		nextState = "DELETED";
        		break;
        	case "configure":
        		// set nextState to CONFIGURED
        		nextState = "CONFIGURED";
        		break;
        	case "install":
        		// set nextState to CREATED
        		nextState = "CREATED";
        		break;
        	}
        }
		
		// if other interface, take state from parameters
    	

    	//Execute and get the response.
    	HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	// get id of request    	
    	String location = response.getFirstHeader("Location").getValue();
    	String requestId = location.substring(location.lastIndexOf("/") + 1);
    	
    	
    	int counter = 0;
    	HttpEntity entity = null;
    	String stringResponse = null;

    	while (counter < 5) {
        	try {
    			TimeUnit.SECONDS.sleep(8);
    		} catch (InterruptedException e1) {
    			e1.printStackTrace();
    		}
        	
        	try {
        		response = null;
        		// check if request has finished
            	HttpGet httpget = new HttpGet("http://localhost:8086/ManagementBus/v1/invoker/activeRequests/" + requestId + "/response");
            	
    			response = httpclient.execute(httpget);
    	    	entity = response.getEntity();
    	    	stringResponse = EntityUtils.toString(entity, "UTF-8");
    	    	if (stringResponse.contains("java.lang.Exception")) {
    	    		counter = counter++;
    	    		//System.out.println(EntityUtils.toString(entity, "UTF-8"));
    	    	} else {
    	    		//System.out.println(EntityUtils.toString(entity, "UTF-8"));
        			counter = 5;
    	    	}
    		} catch (ClientProtocolException e) {
    			System.out.println(e);

    		} catch (IOException e) {
    			System.out.println(e);
    		}
    	}

    	
    	// if so, change state of NodeTemplateInstance

    	if (entity != null) {
    		// Change the state
    		
    	    try (InputStream instream = entity.getContent()) {
    	        // if management operation successful, change the state!
    	    	try {
    	            this.instanceService.setNodeTemplateInstanceState(this.servicetemplate, this.nodetemplate, id, nextState);
    	        }
    	        catch (final IllegalArgumentException e) { // this handles a null request too
    	            return Response.status(Status.BAD_REQUEST).build();
    	        }
    	    } catch (UnsupportedOperationException e) {
    	    	return Response.status(Status.BAD_REQUEST).build();
			} catch (IOException e) {
				return Response.status(Status.BAD_REQUEST).build();
			}
    	}
    	JSONObject jsonResponse = new JSONObject();
    	jsonResponse.put("state", nextState);
    	return Response.ok(jsonResponse).build();

    }

    @GET
    @Path("/{id}/properties")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(hidden = true, value = "")
    public Response getNodeTemplateInstanceProperties(@PathParam("id") final Long id) {
        final Document properties =
            this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

        if (properties == null) {
            return Response.noContent().build();
        } else {
            return Response.ok(properties).build();
        }
    }

    @GET
    @Path("/{id}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Get properties of a node template instance",
        response = Map.class)
    public Map<String, String> getNodeTemplateInstancePropertiesAsJson(@PathParam("id") final Long id) {
        final NodeTemplateInstance instance =
            this.instanceService.resolveNodeTemplateInstance(this.servicetemplate, this.nodetemplate, id);
        return instance.getPropertiesAsMap();
    }

    @GET
    @Path("/{id}/properties/{propname}")
    @Produces({MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response getNodeTemplateInstanceProperty(@PathParam("id") final Long id,
                                                    @PathParam("propname") final String propertyName) {
        final Document properties =
            this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

        if (properties == null && ModelUtil.fetchFirstChildElement(properties, propertyName) == null) {
            return Response.noContent().build();
        } else {
            return Response.ok(ModelUtil.createDocumentFromElement(ModelUtil.fetchFirstChildElement(properties,
                                                                                                    propertyName)))
                           .build();
        }
    }

    @PUT
    @Path("/{id}/properties")
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response updateNodeTemplateInstanceProperties(@PathParam("id") final Long id, final Document request) {

        try {
            this.instanceService.setNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id,
                                                                   request);
        }
        catch (final IllegalArgumentException e) { // this handles a null request too
            return Response.status(Status.BAD_REQUEST).build();
        }
        catch (final ReflectiveOperationException e) {
            return Response.serverError().build();
        }

        return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
    }

    @PUT
    @Path("/{id}/properties/{propname}")
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    @ApiOperation(hidden = true, value = "")
    public Response updateNodeTemplateInstanceProperty(@PathParam("id") final Long id,
                                                       @PathParam("propname") final String propertyName,
                                                       final Document request) {

        try {
            final Document properties =
                this.instanceService.getNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id);

            final Element propElement = ModelUtil.fetchFirstChildElement(properties, propertyName);

            propElement.setTextContent(request.getDocumentElement().getTextContent());

            this.instanceService.setNodeTemplateInstanceProperties(this.servicetemplate, this.nodetemplate, id,
                                                                   properties);
        }
        catch (final IllegalArgumentException e) { // this handles a null request too
            return Response.status(Status.BAD_REQUEST).build();
        }
        catch (final ReflectiveOperationException e) {
            return Response.serverError().build();
        }

        return Response.ok(UriUtil.generateSelfURI(this.uriInfo)).build();
    }
}
