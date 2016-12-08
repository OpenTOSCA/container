package org.opentosca.containerapi.resources.csar.servicetemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
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
public class ServiceTemplatesResource {
	
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplatesResource.class);
	private final CSARContent csarContent;
	UriInfo uriInfo;
	
	
	public ServiceTemplatesResource(CSARContent csar) {
		
		csarContent = csar;
		log.info("{} created: {}", this.getClass(), this);
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
		
		for (QName qname : ToscaServiceHandler.getToscaEngineService().getServiceTemplatesInCSAR(csarContent.getCSARID())){
			String name = URLEncoder.encode(qname.toString(), "UTF-8");
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), name), XLinkConstants.SIMPLE, name));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	@Path("{qname}")
	public ServiceTemplateResource getServiceTemplate(@PathParam("qname") String qname) throws UnsupportedEncodingException{
		return new ServiceTemplateResource(csarContent, URLDecoder.decode(qname, "UTF-8"));
	}
}
