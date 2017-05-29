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
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate.instances.RelationshipTemplateInstanceListResource;

/**
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
public class LinkBuilder {
	
	public static SimpleXLink selfLink(final UriInfo uriInfo) {
		return new SimpleXLink(uriInfo.getAbsolutePath().toString(), "self");
	}
	
	// TODO: move to own linkBuilder
	public static URI linkToArtifactList(final UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(PortabilityRoot.class).path(PortabilityRoot.class, "getArtifacts").build(new Object[0]);
	}
	
	// TODO: move to own linkBuilder
	public static URI linkToPoliciesList(final UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(PortabilityRoot.class).path(PortabilityRoot.class, "getPolicies").build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceList(final UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(InstanceDataRoot.class).path(InstanceDataRoot.class, "getNodeInstances").build(new Object[0]);
	}
	
	public static URI linkToRelationInstanceList(final UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(InstanceDataRoot.class).path(InstanceDataRoot.class, "getRelationInstances").build(new Object[0]);
	}
	
	public static URI linkToServiceInstanceList(final UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().path(InstanceDataRoot.class).path(ServiceTemplateResource.class, "getInstances").build(new Object[0]);
	}
	
	public static URI linkToCSAR(final UriInfo uriInfo, final String csarID) {
		return uriInfo.getBaseUriBuilder().path(CSARsResource.class).path(CSARsResource.class, "getCSAR").build(new Object[] {csarID});
	}
	
	public static URI linkToCSARContent(final UriInfo uriInfo, final String csarID) {
		return UriBuilder.fromUri(linkToCSAR(uriInfo, csarID)).path(CSARResource.class, "getContent").build(new Object[0]);
	}
	
	public static URI linkToNodeInstance(final UriInfo uriInfo, final int id) {
		final Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Constants.NodeInstanceListResource_getNodeInstance_PARAM, Integer.toString(id));
		return UriBuilder.fromUri(linkToNodeInstanceList(uriInfo)).path(NodeTemplateInstanceListResource.class, "getNodeInstance").buildFromMap(paramMap);
	}
	
	public static URI linkToRelationInstance(final UriInfo uriInfo, final int id) {
		final Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Constants.RelationInstanceListResource_getRelationInstance_PARAM, Integer.toString(id));
		return UriBuilder.fromUri(linkToRelationInstanceList(uriInfo)).path(RelationshipTemplateInstanceListResource.class, "getRelationInstance").buildFromMap(paramMap);
	}
	
	public static URI linkToServiceInstance(final UriInfo uriInfo, final int id) {
		final Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Constants.ServiceInstanceListResource_getServiceInstance_PARAM, Integer.toString(id));
		return UriBuilder.fromUri(linkToServiceInstanceList(uriInfo)).path(ServiceTemplateInstancesResource.class, "getServiceInstance").buildFromMap(paramMap);
	}
	
	public static URI linkToFile(final UriInfo uriInfo, final String csarID, final String relativePath) {
		
		// get base path to csar content
		URI uri = linkToCSARContent(uriInfo, csarID);
		final String[] parts = relativePath.split("/");
		for (final String part : parts) {
			uri = UriBuilder.fromUri(uri).path(part).build();
		}
		
		return uri;
		
	}
	
	public static URI linkToServiceInstanceProperties(final UriInfo uriInfo, final int serviceInstanceID) {
		return UriBuilder.fromUri(linkToServiceInstance(uriInfo, serviceInstanceID)).path(ServiceTemplateInstanceResource.class, "getProperties").build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceProperties(final UriInfo uriInfo, final int nodeInstanceID) {
		return UriBuilder.fromUri(linkToNodeInstance(uriInfo, nodeInstanceID)).path(NodeTemplateInstanceResource.class, "getProperties").build(new Object[0]);
	}
	
	public static URI linkToNodeInstanceState(final UriInfo uriInfo, final int nodeInstanceID) {
		return UriBuilder.fromUri(linkToNodeInstance(uriInfo, nodeInstanceID)).path(NodeTemplateInstanceResource.class, "getState").build(new Object[0]);
	}
}