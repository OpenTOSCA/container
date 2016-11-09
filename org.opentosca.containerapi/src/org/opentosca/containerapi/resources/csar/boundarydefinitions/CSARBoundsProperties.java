package org.opentosca.containerapi.resources.csar.boundarydefinitions;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.JSONUtils;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.opentosca.model.tosca.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CSARBoundsProperties {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARBoundsInterfacesResource.class);
	CSARID csarID = null;
	
	UriInfo uriInfo;
	
	
	public CSARBoundsProperties(CSARID csarID) {
		this.csarID = csarID;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			LOG.error("The ToscaEngineService is not alive.");
		}
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getPropertiesXML(@Context UriInfo uriInfo) {
		
		if (csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions Properties for CSAR {}.", csarID);
		
		References refs = getRefs(uriInfo);
		
		return Response.ok(refs.getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getPropertiesJSON(@Context UriInfo uriInfo) {
		
		if (csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions Properties for CSAR {}.", csarID);
		
		References refs = getRefs(uriInfo);
		
		return Response.ok(refs.getJSONString()).build();
	}
	
	private References getRefs(UriInfo uriInfo) {
		References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "XMLFragments"), XLinkConstants.SIMPLE, "XMLFragments"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "PropertyMappings"), XLinkConstants.SIMPLE, "PropertyMappings"));
		return refs;
	}
	
	/**
	 * Returns the Boundary Definitions Properties XML fragment content as XML.
	 * TODO This resource is not scoped under a Service Template, thus, return
	 * all Bounds Properties of all STs.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("XMLFragments")
	@Produces(ResourceConstants.TOSCA_XML)
	public Response getPropertiesContentXML(@Context UriInfo uriInfo) {
		
		if (csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions Properties XML for CSAR {}.", csarID);
		
		StringBuilder builder = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><XMLFragments>");
		List<String> props = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getServiceTemplateBoundsPropertiesContent(csarID);
		
		for (String str : props) {
			builder.append(str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", ""));
		}
		
		builder.append("</XMLFragments>");
		
		return Response.ok(builder.toString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Properties JSON fragment content as XML.
	 * TODO This resource is not scoped under a Service Template, thus, return
	 * all Bounds Properties of all STs.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("XMLFragments")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getPropertiesContentJSON(@Context UriInfo uriInfo) {
		
		if (csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions Properties XML for CSAR {}.", csarID);
		
		JsonObject ret = new JsonObject();
		JsonArray array = new JsonArray();
		ret.add("XMLFragments", array);
		List<String> props = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getServiceTemplateBoundsPropertiesContent(csarID);
		
		for (String xml : props) {
			
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource source = new InputSource();
				source.setCharacterStream(new StringReader(xml));
				Document doc = db.parse(source);
				
				array.addAll(new JSONUtils().xmlToJsonArray(doc.getElementsByTagName("Properties").item(0).getChildNodes()));
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return Response.ok(ret.toString()).build();
	}
	
	@GET
	@Path("PropertyMappings")
	@Produces(ResourceConstants.TOSCA_XML)
	public PropertyMappings getMappingsXML() {
		return ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getServiceTemplateBoundsPropertyMappings(csarID).get(0);
	}
	
	@GET
	@Path("PropertyMappings")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getMappingsJSON() {
		
		List<PropertyMappings> mappingsList = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getServiceTemplateBoundsPropertyMappings(csarID);
		JsonObject ret = new JsonObject();
		JsonArray array = new JsonArray();
		ret.add("XMLFragments", array);
		
		for (PropertyMappings mappings : mappingsList) {
			JsonObject jMappings = new JsonObject();
			JsonArray mappingArray = new JsonArray();
			jMappings.add("PropertyMappings", mappingArray);
			for (TPropertyMapping mapping : mappings.getPropertyMapping()) {
				JsonObject mappingObj = new JsonObject();
				mappingObj.addProperty("serviceTemplatePropertyRef", mapping.getServiceTemplatePropertyRef());
				mappingObj.addProperty("targetObjectRef", ToscaServiceHandler.getIXMLSerializer().marshalToString(mapping.getTargetObjectRef()));
				mappingObj.addProperty("targetPropertyRef", mapping.getTargetPropertyRef());
				mappingArray.add(mappingObj);
			}
			array.add(jMappings);
		}
		
		return Response.ok(ret.toString()).build();
	}
	
}
