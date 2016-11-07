package org.opentosca.containerapi.instancedata;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;
import org.w3c.dom.Document;

/**
 * Properties
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class NodeInstancePropertiesResource {
	
	
	private int nodeInstanceID;
	
	
	public NodeInstancePropertiesResource(int id) {
		nodeInstanceID = id;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@QueryParam("property") List<String> propertiesList) {
		
		Document idr = getProperties(propertiesList);
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@QueryParam("property") List<String> propertiesList) {
		
		Document idr = getProperties(propertiesList);
		
		return Response.ok(new JSONUtils().xmlToGenericJsonObject(idr.getChildNodes()).toString()).build();
	}
	
	public Document getProperties(List<String> propertiesList) {
		List<QName> qnameList = new ArrayList<QName>();
		
		// convert all String in propertyList to qnames
		try {
			if (propertiesList != null) {
				for (String stringValue : propertiesList) {
					qnameList.add(QName.valueOf(stringValue));
				}
			}
		} catch (Exception e) {
			throw new GenericRestException(Status.BAD_REQUEST, "error converting one of the properties-parameters: " + e.getMessage());
		}
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		try {
			Document properties = service.getNodeInstanceProperties(IdConverter.nodeInstanceIDtoURI(nodeInstanceID), qnameList);
			return properties;
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response setProperties(@Context UriInfo uriInfo, Document xml) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		try {
			service.setNodeInstanceProperties(IdConverter.nodeInstanceIDtoURI(nodeInstanceID), xml);
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
		SimpleXLink xLink = new SimpleXLink(LinkBuilder.linkToNodeInstanceProperties(uriInfo, nodeInstanceID), "NodeInstance: " + nodeInstanceID + " Properties");
		return Response.ok(xLink).build();
		
	}
	
}
