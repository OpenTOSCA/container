package org.opentosca.containerapi.resources.marketplace;

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

import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.wineryconnector.WineryConnector;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class MarketplaceServiceTemplatesResource {

	UriInfo uriInfo;

	private WineryConnector connector = new WineryConnector();


	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getXMLString()).build();
	}

	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getJSONString()).build();
	}

	@Path("/{serviceTemplateId}")
	public MarketplaceServiceTemplateResource getServiceTemplate(@PathParam("serviceTemplateId") String serviceTemplateId) {
		
		QName serviceTemplate = null;
		try {
			String decodedServiceTemplateId = URLDecoder.decode(serviceTemplateId, "UTF-8");
			String namespace = decodedServiceTemplateId.substring(1).split("}")[0];
			String localPart = decodedServiceTemplateId.substring(1).split("}")[1];
			serviceTemplate = new QName(namespace, localPart);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (serviceTemplate == null) {
			throw new IllegalArgumentException("ServiceTemplateId couldn't be read");
		}
		
		return new MarketplaceServiceTemplateResource(serviceTemplate);
	}
	
	public References getRefs() {
		References refs = new References();

		for (QName serviceTemplateId : this.connector.getServiceTemplates()) {
			refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), URLEncoder.encode(serviceTemplateId.toString())), XLinkConstants.SIMPLE, serviceTemplateId.toString()));
		}

		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
}
