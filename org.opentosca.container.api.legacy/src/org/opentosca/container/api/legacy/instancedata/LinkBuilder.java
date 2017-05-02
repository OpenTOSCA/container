package org.opentosca.container.api.legacy.instancedata;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.instancedata.utilities.Constants;
import org.opentosca.container.api.legacy.portability.PortabilityRoot;
import org.opentosca.container.api.legacy.resources.csar.CSARResource;
import org.opentosca.container.api.legacy.resources.csar.CSARsResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.ServiceTemplateResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.ServiceTemplateInstancesResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate.instances.NodeTemplateInstanceListResource;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate.instances.NodeTemplateInstanceResource;

/**
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * 
 */
public class LinkBuilder {
	
	public static SimpleXLink selfLink(UriInfo uriInfo) {
		return new SimpleXLink(uriInfo.getAbsolutePath().toString(), "self");
	}
	
	// TODO: move to own linkBuilder
	public static URI linkToArtifactList(UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(PortabilityRoot.class).path(PortabilityRoot.class, "getArtifacts")
				.build(new Object[0]);
	}
	
	// TODO: move to own linkBuilder
	public static URI linkToPoliciesList(UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(PortabilityRoot.class).path(PortabilityRoot.class, "getPolicies")
				.build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceList(UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(InstanceDataRoot.class).path(InstanceDataRoot.class, "getNodeInstances")
				.build(new Object[0]);
	}
	
	public static URI linkToServiceInstanceList(UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(InstanceDataRoot.class)
				.path(ServiceTemplateResource.class, "getInstances").build(new Object[0]);
	}
	
	public static URI linkToCSAR(UriInfo uriInfo, String csarID) {
		return uriInfo.getBaseUriBuilder().path(CSARsResource.class).path(CSARsResource.class, "getCSAR")
				.build(new Object[] { csarID });
	}
	
	public static URI linkToCSARContent(UriInfo uriInfo, String csarID) {
		return UriBuilder.fromUri(linkToCSAR(uriInfo, csarID)).path(CSARResource.class, "getContent")
				.build(new Object[0]);
	}
	
	public static URI linkToNodeInstance(UriInfo uriInfo, int id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(Constants.NodeInstanceListResource_getNodeInstance_PARAM, Integer.toString(id));
		return UriBuilder.fromUri(linkToNodeInstanceList(uriInfo))
				.path(NodeTemplateInstanceListResource.class, "getNodeInstance").buildFromMap(paramMap);
	}
	
	public static URI linkToServiceInstance(UriInfo uriInfo, int id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(Constants.ServiceInstanceListResource_getServiceInstance_PARAM, Integer.toString(id));
		return UriBuilder.fromUri(linkToServiceInstanceList(uriInfo))
				.path(ServiceTemplateInstancesResource.class, "getServiceInstance").buildFromMap(paramMap);
	}
	
	public static URI linkToFile(UriInfo uriInfo, String csarID, String relativePath) {
		
		// get base path to csar content
		URI uri = linkToCSARContent(uriInfo, csarID);
		String[] parts = relativePath.split("/");
		for (String part : parts) {
			uri = UriBuilder.fromUri(uri).path(part).build();
		}
		
		return uri;
		
	}
	
	public static URI linkToServiceInstanceProperties(UriInfo uriInfo, int serviceInstanceID) {
		return UriBuilder.fromUri(linkToServiceInstance(uriInfo, serviceInstanceID))
				.path(ServiceTemplateInstanceResource.class, "getProperties").build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceProperties(UriInfo uriInfo, int nodeInstanceID) {
		return UriBuilder.fromUri(linkToNodeInstance(uriInfo, nodeInstanceID))
				.path(NodeTemplateInstanceResource.class, "getProperties").build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceState(UriInfo uriInfo, int nodeInstanceID) {
		return UriBuilder.fromUri(linkToNodeInstance(uriInfo, nodeInstanceID))
				.path(NodeTemplateInstanceResource.class, "getState").build(new Object[0]);
	}
}