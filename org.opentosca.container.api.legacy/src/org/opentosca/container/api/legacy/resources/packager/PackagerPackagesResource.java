package org.opentosca.container.api.legacy.resources.packager;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.connector.winery.WineryConnector;

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
	
	private final WineryConnector connector = new WineryConnector();
	
	private static final List<String> tags = new ArrayList<>();
	
	static {
		PackagerPackagesResource.tags.add("xaasPackageDeploymentArtefact");
		PackagerPackagesResource.tags.add("xaasPackageNode");
		PackagerPackagesResource.tags.add("xaasPackageArtefactType");
	}
	
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getRefs().getJSONString()).build();
	}
	
	public References getRefs() {
		final References refs = new References();
		
		if (this.connector.isWineryRepositoryAvailable()) {
			for (final QName xaasPackageServiceTemplateId : this.connector.getServiceTemplates(PackagerPackagesResource.tags)) {
				final String uri = Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), Utilities.URLencode(xaasPackageServiceTemplateId.toString())).replace("packager/packages", "marketplace/servicetemplates");
				refs.getReference().add(new Reference(uri, XLinkConstants.SIMPLE, xaasPackageServiceTemplateId.toString()));
			}
		}
		
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
}
