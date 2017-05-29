package org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages access to a specific nodeInstance. It also checks the
 * existance of the nodeInstance with the given ID (also for all children
 * resources before passing the request along)
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
public class RelationshipTemplateInstanceResource {

	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);

	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final QName relationshipTemplateID;
	private final int relationshipTemplateInstanceId;


	public RelationshipTemplateInstanceResource(final CSARID csarId, final QName serviceTemplateID, final int serviceTemplateInstanceId, final QName relationshipTemplateID, final int id) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.relationshipTemplateID = relationshipTemplateID;
		this.relationshipTemplateInstanceId = id;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {

		final References idr = this.getRefs(uriInfo);

		return Response.ok(idr.getXMLString()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {

		final References idr = this.getRefs(uriInfo);

		return Response.ok(idr.getJSONString()).build();
	}

	public References getRefs(final UriInfo uriInfo) throws UnsupportedEncodingException {

		final References refs = new References();

		this.log.debug("try to build relation template instance resource");
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		final List<RelationInstance> relationInstances = service.getRelationInstances(IdConverter.relationInstanceIDtoURI(this.relationshipTemplateInstanceId), null, null, null);

		// existence of instance is already checked before invoking this class
		// and its methods
		final RelationInstance relationInstance = relationInstances.get(0);

		final QName relationshipTypeQName = relationInstance.getRelationshipType();
		final List<String> relationshipType = new ArrayList<>();
		relationshipType.add(relationshipTypeQName.toString());

		final List<SimpleXLink> links = new LinkedList<>();
		links.add(LinkBuilder.selfLink(uriInfo));

		final URI serviceInstanceID = relationInstance.getServiceInstance().getServiceInstanceID();
		// URI linkToServiceInstance =
		// LinkBuilder.linkToServiceInstance(uriInfo,
		// IdConverter.serviceInstanceUriToID(serviceInstanceID));

		// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
		// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
		// "/Instances/" + serviceTemplateInstanceId;
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(),
		// nodeUrl), XLinkConstants.REFERENCE,
		// "ParentServiceTemplateInstance"));

		// links.add(new SimpleXLink(linkToServiceInstance, "ServiceInstance"));
		// // properties link
		// URI linkToProperties =
		// LinkBuilder.linkToNodeInstanceProperties(uriInfo,
		// nodeTemplateInstanceId);
		// links.add(new SimpleXLink(linkToProperties, "Properties"));
		// // state link
		// links.add(new
		// SimpleXLink(LinkBuilder.linkToNodeInstanceState(uriInfo,
		// nodeTemplateInstanceId), "State"));
		// NodeInstanceEntry nie = new NodeInstanceEntry(nodeInstance, links);

		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo, "Properties"), XLinkConstants.SIMPLE, "Properties"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo, "State"), XLinkConstants.SIMPLE, "State"));

		if (relationInstance.getSourceInstance() != null) {
			refs.getReference().add(new Reference(relationInstance.getSourceInstance().getNodeInstanceID().toString(), XLinkConstants.SIMPLE, "SourceInstanceId"));
		}

		if (relationInstance.getTargetInstance() != null) {
			refs.getReference().add(new Reference(relationInstance.getTargetInstance().getNodeInstanceID().toString(), XLinkConstants.SIMPLE, "TargetInstanceId"));
		}

		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

		return refs;
	}

	@DELETE
	public Response deleteRelationInstance() {
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		service.deleteRelationInstance(IdConverter.relationInstanceIDtoURI(this.relationshipTemplateInstanceId));
		return Response.noContent().build();

	}

	@Path("/Properties")
	public Object getProperties() {
		return new RelationshipTemplateInstancePropertiesResource(this.relationshipTemplateInstanceId);
	}

	@Path("/State")
	public Object getState() {
		return new RelationshipTemplateInstanceStateResource(this.relationshipTemplateInstanceId);
	}

}