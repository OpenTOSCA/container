package org.opentosca.containerapi.resources.packager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
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
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class PackagerPackagesResource {
	
	@Context
	UriInfo uriInfo;
	
	private WineryConnector connector = new WineryConnector();
	
	private static final List<String> tags = new ArrayList<String>();
	
	
	public PackagerPackagesResource() {
		PackagerPackagesResource.tags.add("xaasPackageDeploymentArtefact");
		PackagerPackagesResource.tags.add("xaasPackageNode");
		PackagerPackagesResource.tags.add("xaasPackageArtefactType");
	}
	
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
	
	public References getRefs() {
		References refs = new References();
		
		for (QName xaasPackageServiceTemplateId : this.connector.getServiceTemplates(PackagerPackagesResource.tags)) {
			String uri = Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), URLEncoder.encode(xaasPackageServiceTemplateId.toString())).replace("packager/packages", "marketplace/servicetemplates");
			refs.getReference().add(new Reference(uri, XLinkConstants.SIMPLE, xaasPackageServiceTemplateId.toString()));
		}
		
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
}
