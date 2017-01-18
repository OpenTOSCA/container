package org.opentosca.containerapi.resources.csar.servicetemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TServiceTemplate;
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
	public Response getReferencesXML(@Context UriInfo uriInfo, @DefaultValue("false")@QueryParam("main") boolean onlyMainServiceTemplate) throws UnsupportedEncodingException, UserException, SystemException {
		this.uriInfo = uriInfo;
		return Response.ok(getRefs(onlyMainServiceTemplate).getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo, @QueryParam("main") boolean onlyMainServiceTemplate) throws UnsupportedEncodingException, UserException, SystemException {
		this.uriInfo = uriInfo;
		return Response.ok(getRefs(onlyMainServiceTemplate).getJSONString()).build();
	}
	
	public References getRefs(boolean onlyMainServiceTemplate) throws UnsupportedEncodingException, UserException, SystemException {
		
		if (csarContent == null) {
			return null;
		}
		
		References refs = new References();
		
		if (onlyMainServiceTemplate) {
			log.debug("Only reference to main Service Template is requested.");
			String st = getEntryServiceTemplateName();
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), st), XLinkConstants.SIMPLE, st));
			
		} else {
			for (QName qname : ToscaServiceHandler.getToscaEngineService().getServiceTemplatesInCSAR(csarContent.getCSARID())) {
				String name = URLEncoder.encode(qname.toString(), "UTF-8");
				refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), name), XLinkConstants.SIMPLE, name));
			}
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	private String getEntryServiceTemplateName() throws UserException, SystemException, UnsupportedEncodingException {
		
		AbstractFile root = FileRepositoryServiceHandler.getFileHandler().getCSAR(csarContent.getCSARID()).getRootTOSCA();
		Definitions def = ToscaServiceHandler.getIXMLSerializer().unmarshal(root.getFileAsInputStream());
		
		for (TExtensibleElements el : def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()){
			if (el instanceof TServiceTemplate){
				TServiceTemplate st = (TServiceTemplate) el;
				QName qn = new QName(st.getTargetNamespace(), st.getId());
				return URLEncoder.encode(qn.toString(), "UTF-8");
			}
		}
		
		return null;
	}
	
	@Path("{qname}")
	public ServiceTemplateResource getServiceTemplate(@PathParam("qname") String qname) throws UnsupportedEncodingException {
		log.debug("Create Service Template resource for {}", qname);
		return new ServiceTemplateResource(csarContent, URLDecoder.decode(qname, "UTF-8"));
	}
}
