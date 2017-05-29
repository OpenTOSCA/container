package org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.resources.csar.servicetemplate.nodetemplate.instances.NodeTemplateInstancesResource;
import org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate.instances.RelationshipTemplateInstancesResource;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RelationshipTemplateResource  {
	private final Logger log = LoggerFactory.getLogger(RelationshipTemplateResource.class);
	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final QName relationshipTemplateID;
	private UriInfo uriInfo;
	
	
	public RelationshipTemplateResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId, String planIdLocalPart) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		relationshipTemplateID = new QName(serviceTemplateID.getNamespaceURI(), planIdLocalPart);
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		this.uriInfo = uriInfo;
		return Response.ok(getRefs().getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		this.uriInfo = uriInfo;
		return Response.ok(getRefs().getJSONString()).build();
	}
	
	public References getRefs() throws UnsupportedEncodingException {
		
		if (csarId == null) {
			return null;
		}
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Instances"), XLinkConstants.SIMPLE, "Instances"));
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	@Path("Instances")
	public RelationshipTemplateInstancesResource getNodeTemplateInstances() {
		return new RelationshipTemplateInstancesResource(csarId, serviceTemplateID, serviceTemplateInstanceId, relationshipTemplateID);
	}
}
