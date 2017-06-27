package org.opentosca.container.connector.winery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class WineryConnector {

	final private static Logger LOG = LoggerFactory.getLogger(WineryConnector.class);

	private DefaultHttpClient client = new DefaultHttpClient();;
	String wineryPath;


	public WineryConnector() {
		this.wineryPath = Settings.getSetting("org.opentosca.container.connector.winery.url");
		if (!this.wineryPath.endsWith("/")) {
			this.wineryPath = this.wineryPath + "/";
		}
	}

	public boolean isWineryRepositoryAvailable() {

		HttpGet get = new HttpGet();
		get.setHeader("Accept", "application/json");

		try {
			get.setURI(new URI(this.wineryPath + "servicetemplates"));
			HttpResponse resp = this.client.execute(get);

			EntityUtils.consume(resp.getEntity());
			if (resp.getStatusLine().getStatusCode() < 400) {
				return true;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public String getWineryPath() {
		return this.wineryPath;
	}

	public URI getServiceTemplateURI(QName serviceTemplateId) {
		try {
			return new URI(this.wineryPath + "servicetemplates/" + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())) + "/" + serviceTemplateId.getLocalPart());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public QName uploadCSAR(File file) throws URISyntaxException, IOException {
		MultipartEntity entity = new MultipartEntity();

		ContentBody fileBody = new FileBody(file);
		ContentBody overwriteBody = new StringBody("true");

		FormBodyPart filePart = new FormBodyPart("file", fileBody);
		FormBodyPart overwritePart = new FormBodyPart("overwrite", overwriteBody);
		entity.addPart(filePart);
		entity.addPart(overwritePart);

		HttpPost wineryPost = new HttpPost();

		wineryPost.setURI(new URI(this.wineryPath));
		wineryPost.setEntity(entity);
		HttpResponse wineryResp = this.client.execute(wineryPost);
		// create QName of the created serviceTemplate resource
		String location = this.getHeaderValue(wineryResp, "Location");

		if(location == null){
			return null;
		}

		if (location.endsWith("/")) {
			location = location.substring(0, location.length() - 1);
		}

		String localPart = this.getLastPathFragment(location);
		String namespaceDblEnc = this.getLastPathFragment(location.substring(0, location.lastIndexOf("/")));
		String namespace = URLDecoder.decode(URLDecoder.decode(namespaceDblEnc));

		return new QName(namespace, localPart);
	}

	public QName createServiceTemplateFromXaaSPackage(File file, QName artifactType, Set<QName> nodeTypes, QName infrastructureNodeType, Map<String, String> tags) throws URISyntaxException, IOException {
		MultipartEntity entity = new MultipartEntity();

		// file
		ContentBody fileBody = new FileBody(file);
		FormBodyPart filePart = new FormBodyPart("file", fileBody);
		entity.addPart(filePart);

		// artefactType
		ContentBody artefactTypeBody = new StringBody(artifactType.toString());
		FormBodyPart artefactTypePart = new FormBodyPart("artefactType", artefactTypeBody);
		entity.addPart(artefactTypePart);

		// nodeTypes
		if (!nodeTypes.isEmpty()) {
			String nodeTypesAsString = "";
			for (QName nodeType : nodeTypes) {
				nodeTypesAsString += nodeType.toString() + ",";
			}

			ContentBody nodeTypesBody = new StringBody(nodeTypesAsString.substring(0, nodeTypesAsString.length() - 1));
			FormBodyPart nodeTypesPart = new FormBodyPart("nodeTypes", nodeTypesBody);
			entity.addPart(nodeTypesPart);
		}

		// infrastructureNodeType
		if (infrastructureNodeType != null) {
			ContentBody infrastructureNodeTypeBody = new StringBody(infrastructureNodeType.toString());
			FormBodyPart infrastructureNodeTypePart = new FormBodyPart("infrastructureNodeType", infrastructureNodeTypeBody);
			entity.addPart(infrastructureNodeTypePart);
		}

		// tags
		if (!tags.isEmpty()) {
			String tagsString = "";
			for (String key : tags.keySet()) {
				if (tags.get(key) == null) {
					tagsString += key + ",";
				} else {
					tagsString += key + ":" + tags.get(key) + ",";
				}
			}

			ContentBody tagsBody = new StringBody(tagsString.substring(0, tagsString.length() - 1));
			FormBodyPart tagsPart = new FormBodyPart("tags", tagsBody);
			entity.addPart(tagsPart);
		}

		// POST to XaaSPackager
		HttpPost xaasPOST = new HttpPost();
		xaasPOST.setURI(new URI(this.wineryPath + "servicetemplates/"));
		xaasPOST.setEntity(entity);
		HttpResponse xaasResp = this.client.execute(xaasPOST);

		// create QName of the created serviceTemplate resource
		String location = this.getHeaderValue(xaasResp, "Location");

		if (location.endsWith("/")) {
			location = location.substring(0, location.length() - 1);
		}

		String localPart = this.getLastPathFragment(location);
		String namespaceDblEnc = this.getLastPathFragment(location.substring(0, location.lastIndexOf("/")));
		String namespace = URLDecoder.decode(URLDecoder.decode(namespaceDblEnc));

		return new QName(namespace, localPart);
	}

	private String getLastPathFragment(String url) {
		if (url.endsWith("/")) {
			return this.getLastPathFragment(url.subSequence(0, url.length() - 1).toString());
		} else {

			return url.substring(url.lastIndexOf("/") + 1);
		}
	}

	private String getHeaderValue(HttpResponse response, String headerName) {

		for (Header header : response.getAllHeaders()) {
			if (header.getName().equals(headerName)) {
				return header.getValue();
			}
		}

		return null;
	}

	public List<QName> getServiceTemplates(List<String> tags) {
		List<QName> qnames = new ArrayList<QName>();
		ObjectMapper mapper = new ObjectMapper();

		for (QName serviceTemplateId : this.getServiceTemplates()) {
			WineryConnector.LOG.debug("Querying Winery Repository at " + this.wineryPath + " for ServiceTemplate " + serviceTemplateId);
			try {

				HttpGet serviceTemplateTagsGET = new HttpGet();
				serviceTemplateTagsGET.setHeader("Accept", "application/json");

				serviceTemplateTagsGET.setURI(new URI(this.wineryPath + "servicetemplates/" + URLEncoder.encode(URLEncoder.encode((serviceTemplateId.getNamespaceURI()))) + "/" + serviceTemplateId.getLocalPart() + "/tags"));
				HttpResponse serviceTemplateTagsGETResp = this.client.execute(serviceTemplateTagsGET);
				String tagsJsonResponse = EntityUtils.toString(serviceTemplateTagsGETResp.getEntity());

				JsonNode tagsJsonNode = mapper.readTree(tagsJsonResponse);

				int matched = 0;

				if (tagsJsonNode.isArray()) {

					for (Iterator<JsonNode> iter = tagsJsonNode.elements(); iter.hasNext();) {
						JsonNode key = iter.next();

						HttpGet serviceTemplateTagGET = new HttpGet();
						serviceTemplateTagGET.setHeader("Accept", "application/json");
						serviceTemplateTagGET.setURI(new URI(serviceTemplateTagsGET.getURI().toString() + "/" + key.textValue()));
						HttpResponse serviceTemplateTagGETResp = this.client.execute(serviceTemplateTagGET);
						String tagJsonResponse = EntityUtils.toString(serviceTemplateTagGETResp.getEntity());

						JsonNode tagJsonNode = mapper.readTree(tagJsonResponse);

						if (tagJsonNode.isObject() && tagJsonNode.has("name")) {
							if (tags.contains(tagJsonNode.get("name").textValue())) {
								matched++;

							}
						} else {
							continue;
						}

					}
				} else {
					continue;
				}

				if (matched == tags.size()) {
					qnames.add(serviceTemplateId);
				}

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return qnames;

	}

	public List<QName> getServiceTemplates() {
		List<QName> qnames = new ArrayList<QName>();

		try {

			HttpGet get = new HttpGet();
			get.setHeader("Accept", "application/json");
			get.setURI(new URI(this.wineryPath + "servicetemplates"));
			HttpResponse resp = this.client.execute(get);
			String jsonResponse = EntityUtils.toString(resp.getEntity());

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Object> obj = mapper.readValue(jsonResponse, ArrayList.class);

			for (Object jsonObj : obj) {
				LinkedHashMap<String, String> hashMap = (LinkedHashMap<String, String>) jsonObj;

				String id = hashMap.get("id");
				String namespace = hashMap.get("namespace");

				qnames.add(new QName(namespace, id));
			}

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return qnames;
	}

}