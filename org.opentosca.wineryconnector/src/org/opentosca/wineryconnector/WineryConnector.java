package org.opentosca.wineryconnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.opentosca.settings.Settings;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class WineryConnector {

	DefaultHttpClient client = new DefaultHttpClient();
	String wineryPath;


	public WineryConnector() {
		this.wineryPath = Settings.getSetting("openTOSCAWineryPath");
		if (!this.wineryPath.endsWith("/")) {
			this.wineryPath = this.wineryPath + "/";
		}
	}
	
	public String getWineryPath() {
		return this.wineryPath;
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
