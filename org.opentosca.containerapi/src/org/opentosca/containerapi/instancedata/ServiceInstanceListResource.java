package org.opentosca.containerapi.instancedata;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.ServiceInstanceList;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.instancedata.utilities.Constants;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.ServiceInstance;

/**
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceInstanceListResource {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getServiceInstances(
			@Context UriInfo uriInfo,
			@QueryParam("serviceInstanceID") String serviceInstanceID,
			@QueryParam("serviceTemplateName") String serviceTemplateName,
			@QueryParam("serviceTemplateID") String serviceTemplateID) {
		
		URI serviceInstanceIdURI = null;
		QName serviceTemplateIDQName = null;
		try {
			if (serviceInstanceID != null) {
				serviceInstanceIdURI = new URI(serviceInstanceID);
				if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
					throw new Exception("Error converting serviceInstanceID: invalid format!");
				}
			}
			if (serviceTemplateID != null) {
				serviceTemplateIDQName = QName.valueOf(serviceTemplateID);
			}
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due to bad variable content: " + e1.getMessage());
		}
		
		try {
			IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIdURI, serviceTemplateName, serviceTemplateIDQName);
			
			List<SimpleXLink> links = new LinkedList<SimpleXLink>();
			for (ServiceInstance serviceInstance : serviceInstances) {
				URI urlToServiceInstance = LinkBuilder.linkToServiceInstance(uriInfo, serviceInstance.getDBId());
				
				//build simpleXLink with the internalID as LinkText
				//TODO: is the id the correct linkText?
				links.add(new SimpleXLink(urlToServiceInstance, serviceInstance.getDBId() + ""));
			}
			
			ServiceInstanceList sil = new ServiceInstanceList(LinkBuilder.selfLink(uriInfo), links);
			return Response.ok(sil).build();
		} catch (Exception e) {
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createServiceInstance(
			@QueryParam("csarID") String csarID,
			@QueryParam("serviceTemplateID") QName serviceTemplateID,
			@Context UriInfo uriInfo
			) {
		//null and empty checks for csarID and serviceTemplateID
		if (serviceTemplateID == null || Utilities.areEmpty(csarID, serviceTemplateID.toString())) {
			throw new GenericRestException(Status.BAD_REQUEST, "Missing one of the required parameters: csarID, serviceTemplateID");
		}
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		CSARID csarIDcsarID = new CSARID(csarID);
		try {
			ServiceInstance createServiceInstance = service.createServiceInstance(csarIDcsarID, serviceTemplateID);
			
			//create xlink with the link to the newly created serviceInstance, the link text is the internal serviceInstanceID
			SimpleXLink response = new SimpleXLink(LinkBuilder.linkToServiceInstance(uriInfo, createServiceInstance.getDBId()), createServiceInstance.getServiceInstanceID().toString());
			return Response.ok(response).build();
		} catch (Exception e) {
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		
	}
	
	@Path("{" + Constants.ServiceInstanceListResource_getServiceInstance_PARAM + "}")
	public Object getServiceInstance(
			@PathParam(Constants.ServiceInstanceListResource_getServiceInstance_PARAM) int id) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkServiceInstanceWithException(id, service);
		return new ServiceInstanceResource(id);
	}
}