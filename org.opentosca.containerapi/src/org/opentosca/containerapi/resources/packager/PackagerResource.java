package org.opentosca.containerapi.resources.packager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.wineryconnector.WineryConnector;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
@Path("/packager")
public class PackagerResource {
	
	@Context
	UriInfo uriInfo;
	
	private WineryConnector connector = new WineryConnector();
	
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML() {
		return Response.ok(this.getRefs().getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON() {
		return Response.ok(this.getRefs().getJSONString()).build();
	}
	
	public References getRefs() {
		References refs = new References();
		
		if (this.connector.isWineryRepositoryAvailable()) {
			refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "packages"), XLinkConstants.SIMPLE, "servicetemplates"));
		}
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	@Path("/packages")
	public PackagerPackagesResource getPackages() {
		return new PackagerPackagesResource();
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createFromArtefact(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body, @FormDataParam("artefactType") QName artifactType, @FormDataParam("nodeTypes") Set<QName> nodeTypes, @FormDataParam("infrastructureNodeType") QName infrastructureNodeType, @FormDataParam("tags") Set<String> tags, @Context UriInfo uriInfo) throws IllegalArgumentException, JAXBException, IOException {

		if (this.connector.isWineryRepositoryAvailable()) {
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}

		File tempFile = this.inputStream2File(uploadedInputStream, fileDetail.getFileName());
		
		try {
			QName xaasServiceTemplate = this.connector.createServiceTemplateFromXaaSPackage(tempFile, artifactType, nodeTypes, infrastructureNodeType, this.createTagMapFromTagSet(tags));
			String redirectUrl = Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "servicetemplates/" + Utilities.URLencode(xaasServiceTemplate.toString())).replace("packager", "marketplace");
			return Response.created(URI.create(redirectUrl)).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
	private Map<String, String> createTagMapFromTagSet(Set<String> tags) {
		Map<String, String> tagMap = new HashMap<String, String>();
		
		for (String tag : tags) {
			if (tag.contains(":")) {
				String key = tag.split(":")[0];
				String value = tag.split(":")[1];
				tagMap.put(key, value);
			} else {
				tagMap.put(tag, null);
			}
		}
		
		return tagMap;
	}
	
	private File inputStream2File(InputStream is, String fileName) {
		OutputStream out = null;
		File tempFile = null;
		try {
			;
			
			tempFile = new File(Files.createTempDirectory("XaaSPackager").toFile(), fileName);
			tempFile.createNewFile();
			out = new FileOutputStream(tempFile);
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = is.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			
			is.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tempFile;
	}
	
}
