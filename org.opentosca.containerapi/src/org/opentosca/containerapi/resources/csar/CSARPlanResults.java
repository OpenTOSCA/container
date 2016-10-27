package org.opentosca.containerapi.resources.csar;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Temporary class until instance data api is merged into the csars api
 * 
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author User
 *
 */
public class CSARPlanResults {
	
	
	private final Logger LOG = LoggerFactory.getLogger(CSARPlanResults.class);
	
	CSARContent csar;
	
	
	public CSARPlanResults(CSARContent csar) {
		this.csar = csar;
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		if (csar == null) {
			return Response.status(404).build();
		}
		
		References refs = new References();
		
		for (String corr : CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID())) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), corr), XLinkConstants.SIMPLE, corr));
		}
		// LOG.info("Number of References in Root: {}",
		// refs.getReference().size());
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	//	@GET
	//	@Path("{corr}")
	//	@Consumes(ResourceConstants.TOSCA_XML)
	//	@Produces("application/xml")
	//	public Response getStatusOfPlanXML(@PathParam("corr") String corr) {
	//		
	//		if (CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()).contains(corr)) {
	//			Map<String, String> map = CSARInstanceManagementHandler.csarInstanceManagement.getOutputForCorrelation(corr);
	//			LOG.trace("Response for correlation {}", corr);
	//			StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response>");
	//			for (String str : map.keySet()) {
	//				xml.append("<var><name>" + str + "</name><val>" + map.get(str) + "</val></var>");
	//			}
	//			xml.append("</response>");
	//			return Response.ok(xml.toString()).build();
	//		} else {
	//			LOG.warn("Correlation not known for corr ", corr);
	//			return Response.status(Response.Status.BAD_REQUEST).entity("<response>Given correlation is not known.</response>").build();
	//		}
	//	}
	
	@GET
	@Path("{corr}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatusOfPlanJSON(@PathParam("corr") String corr) {
		
		if (CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csar.getCSARID()).contains(corr)) {
			Map<String, String> map = CSARInstanceManagementHandler.csarInstanceManagement.getOutputForCorrelation(corr);
			LOG.trace("Response for correlation {}", corr);
			
			JsonObject ret = new JsonObject();
			
			StringBuilder json = new StringBuilder("{");
			for (String str : map.keySet()) {
				json.append("\"name\":\"" + str + "\",\"val\":\"" + map.get(str) + "\"");
				ret.addProperty(str, map.get(str));
			}
			json.append("}");
			String str = "{\"json\":" + ret.toString() + "}";
			LOG.trace("Return: {}", str);
			return Response.ok(str).build();
		} else {
			LOG.warn("Correlation not known for corr ", corr);
			return Response.status(Response.Status.BAD_REQUEST).entity("<response>Given correlation is not known.</response>").build();
		}
	}
}
