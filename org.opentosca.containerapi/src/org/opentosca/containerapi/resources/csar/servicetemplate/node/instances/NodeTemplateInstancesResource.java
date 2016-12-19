package org.opentosca.containerapi.resources.csar.servicetemplate.node.instances;

import java.net.URI;
import java.net.URLEncoder;
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

import org.opentosca.containerapi.instancedata.ExistenceChecker;
import org.opentosca.containerapi.instancedata.LinkBuilder;
import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.NodeInstanceList;
import org.opentosca.containerapi.instancedata.model.ServiceInstanceEntry;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.instancedata.utilities.Constants;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class NodeTemplateInstancesResource {

	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);

	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;


	public NodeTemplateInstancesResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context UriInfo uriInfo, @QueryParam("nodeType") QName nodeType) {

		References idr = this.getRefs(uriInfo, nodeType);

		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return Response.ok(idr.getXMLString()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo, @QueryParam("nodeType") QName nodeType) {

		References idr = this.getRefs(uriInfo, nodeType);

		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return Response.ok(idr.getJSONString()).build();
	}

	public References getRefs(UriInfo uriInfo, QName nodeType) {
		References refs = new References();

		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		URI serviceInstanceIDtoURI = IdConverter.serviceInstanceIDtoURI(this.serviceTemplateInstanceId);

		try {
			// self link is only link at the moment in the main list
			List<SimpleXLink> serviceInstanceLinks = new LinkedList<SimpleXLink>();
			serviceInstanceLinks.add(LinkBuilder.selfLink(uriInfo));

			// its ensured that this serviceInstance exists
			List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIDtoURI, null, null);
			ServiceInstance serviceInstance = serviceInstances.get(0);

			// extract values

			// build nodeInstanceList
			List<NodeInstance> nodeInstances = service.getNodeInstances(null, null, null, serviceInstanceIDtoURI);
			List<SimpleXLink> nodeInstanceLinks = new LinkedList<SimpleXLink>();

			for (NodeInstance nodeInstance : nodeInstances) {
				// URI uriToNodeInstance =
				// LinkBuilder.linkToNodeInstance(uriInfo,
				// nodeInstance.getId());
				// // build simpleXLink with the nodeInstanceID as LinkText
				// nodeInstanceLinks.add(new SimpleXLink(uriToNodeInstance,
				// nodeInstance.getNodeInstanceID().toString()));
				if (nodeType == null) {
					QName nodeId = nodeInstance.getNodeTemplateID();
					int nodeInstanceId = nodeInstance.getId();
					String nodeUrl = "/CSARs/" + this.csarId + "/ServiceTemplates/" + URLEncoder.encode(this.serviceTemplateID.toString(), "UTF-8") + "/Instances/" + this.serviceTemplateInstanceId + "/NodeTemplates/" + nodeInstanceId;
					refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(), nodeUrl), XLinkConstants.REFERENCE, URLEncoder.encode(nodeId.getLocalPart(), "UTF-8")));
					this.log.debug("build node reference {}", nodeUrl);
				} else {
					if (nodeInstance.getNodeType().equals(nodeType)) {
						QName nodeId = nodeInstance.getNodeTemplateID();
						int nodeInstanceId = nodeInstance.getId();
						String nodeUrl = "/CSARs/" + this.csarId + "/ServiceTemplates/" + URLEncoder.encode(this.serviceTemplateID.toString(), "UTF-8") + "/Instances/" + this.serviceTemplateInstanceId + "/NodeTemplates/" + nodeInstanceId;
						refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(), nodeUrl), XLinkConstants.REFERENCE, URLEncoder.encode(nodeId.getLocalPart(), "UTF-8")));
						this.log.debug("build node reference {}", nodeUrl);
					}
				}
			}
			// we dont want a self link because the InstanceList is part of
			// another list already containing a self link
			NodeInstanceList nil = new NodeInstanceList(null, nodeInstanceLinks);

			ServiceInstanceEntry sie = new ServiceInstanceEntry(serviceInstance, serviceInstanceLinks, nil);

			// selflink
			refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

			return refs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createNodeInstance(@QueryParam("nodeTemplateID") String nodeTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @Context UriInfo uriInfo) {

		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();

		if (Utilities.areEmpty(nodeTemplateID, serviceInstanceID)) {
			throw new GenericRestException(Status.BAD_REQUEST, "Missing one of the required parameters: nodeTemplateID, serviceInstanceID");
		}

		URI serviceInstanceIdURI = null;
		QName nodeTemplateIDQName = null;
		try {
			serviceInstanceIdURI = new URI(serviceInstanceID);
			if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
				throw new Exception("Error converting serviceInstanceID: invalid format!");
			}
			nodeTemplateIDQName = QName.valueOf(nodeTemplateID);

		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter: " + e1.getMessage());
		}

		try {
			NodeInstance nodeInstance = service.createNodeInstance(nodeTemplateIDQName, serviceInstanceIdURI);
			SimpleXLink response = new SimpleXLink(LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId()), nodeInstance.getNodeInstanceID().toString());
			return Response.ok(response).build();
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
	}

	@Path("/{" + Constants.NodeInstanceListResource_getNodeInstance_PARAM + "}")
	public Object getNodeInstance(@PathParam(Constants.NodeInstanceListResource_getNodeInstance_PARAM) int nodeTemplateInstanceId, @Context UriInfo uriInfo) {

		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkNodeInstanceWithException(nodeTemplateInstanceId, service);
		return new NodeTemplateInstanceResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId, nodeTemplateInstanceId);
	}
}
