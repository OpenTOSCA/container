package org.opentosca.containerapi.resources.csar.servicetemplate;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.resources.csar.servicetemplate.boundarydefinitions.BoundsResource;
import org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstancesResource;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class ServiceTemplateResource {
	
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateResource.class);
	private final CSARContent csarContent;
	private final QName serviceTemplateID;
	private UriInfo uriInfo;
	
	
	public ServiceTemplateResource(CSARContent csarContent, String serviceTemplateID) {
		
		this.csarContent = csarContent;
		String namespace = serviceTemplateID.substring(1, serviceTemplateID.indexOf("}"));
		String localName = serviceTemplateID.substring(serviceTemplateID.indexOf("}") + 1);
		this.serviceTemplateID = new QName(namespace, localName);
		log.info("{} created: \"{}\":\"{}\"; out of \"{}\"", this.getClass(), this.serviceTemplateID.getNamespaceURI(), this.serviceTemplateID.getLocalPart(), serviceTemplateID);
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
		
		if (csarContent == null) {
			return null;
		}
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "BoundaryDefinitions"), XLinkConstants.SIMPLE, "BoundaryDefinitions"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Instances"), XLinkConstants.SIMPLE, "Instances"));
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	@Path("BoundaryDefinitions")
	public BoundsResource getBoundaryDefs() {
		return new BoundsResource(csarContent.getCSARID(), serviceTemplateID);
	}
	
	@Path("Instances")
	public ServiceTemplateInstancesResource getInstances() {
		log.debug("Create ST instances list resource for {}", serviceTemplateID);
		return new ServiceTemplateInstancesResource(csarContent.getCSARID(), serviceTemplateID);
	}
}
