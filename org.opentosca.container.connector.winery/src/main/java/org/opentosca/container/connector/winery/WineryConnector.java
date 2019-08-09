package org.opentosca.container.connector.winery;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javax.xml.namespace.QName;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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
 * Copyright 2016 IAAS University of Stuttgart
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class WineryConnector {

  final private static Logger LOG = LoggerFactory.getLogger(WineryConnector.class);

  private static final String FEATURE_ENRICHMENT_SUFFIX = "/topologytemplate/availablefeatures";

  private final DefaultHttpClient client = new DefaultHttpClient();
  private final String wineryPath;

  public WineryConnector() {
    String configurationValue = Settings.getSetting("org.opentosca.container.connector.winery.url");
    if (!configurationValue.endsWith("/")) {
      configurationValue = configurationValue + "/";
    }
    try {
      new URI(configurationValue);
    } catch (URISyntaxException e) {
      LOG.error("Winery Connector configuration is not valid", e);
    }
    this.wineryPath = configurationValue;
    LOG.debug("Initialized Winery Connector for endpoint " + this.wineryPath);
  }

  public boolean isWineryRepositoryAvailable() {

    final HttpGet get = new HttpGet();
    get.setHeader("Accept", "application/json");
    try {
      final URI serviceTemplatesUri = new URI(this.wineryPath + "servicetemplates");
      LOG.debug("Checking if winery is available at " + serviceTemplatesUri.toString());
      get.setURI(serviceTemplatesUri);
      final HttpResponse resp = this.client.execute(get);

      EntityUtils.consume(resp.getEntity());
      if (resp.getStatusLine().getStatusCode() < 400) {
        return true;
      }
    } catch (URISyntaxException | IOException e) {
      LOG.info("Exception when checking for winery availability. Assuming winery is not available");
      LOG.debug("Details: ", e);
    }
    return false;
  }

  public String getWineryPath() {
    return this.wineryPath;
  }

  public URI getServiceTemplateURI(final QName serviceTemplateId) {
    try {
      LOG.debug("Trying to fetch URI to Service Template" + serviceTemplateId.toString());
      String uri = String.format("%sservicetemplates/%s/%s", wineryPath,
        URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())),
        serviceTemplateId.getLocalPart());
      return new URI(uri);
    } catch (final URISyntaxException e) {
      LOG.warn("URI created from serviceTemplateId {} was malformed", serviceTemplateId, e);
      return null;
    }
  }

  public QName uploadCSAR(final File file) throws URISyntaxException, IOException {
    return uploadCSAR(file, false);
  }

  private String uploadCSARToWinery(final File file, final boolean overwrite) throws URISyntaxException, IOException {
    final MultipartEntity entity = new MultipartEntity();

    final ContentBody fileBody = new FileBody(file);
    final ContentBody overwriteBody = new StringBody(String.valueOf(overwrite));

    final FormBodyPart filePart = new FormBodyPart("file", fileBody);
    final FormBodyPart overwritePart = new FormBodyPart("overwrite", overwriteBody);
    entity.addPart(filePart);
    entity.addPart(overwritePart);

    final HttpPost wineryPost = new HttpPost();

    wineryPost.setURI(new URI(this.wineryPath));
    wineryPost.setEntity(entity);
    final HttpResponse wineryResp = this.client.execute(wineryPost);
    String location = getHeaderValue(wineryResp, "Location");
    closeConnection(wineryResp);

    if (Objects.nonNull(location) && location.endsWith("/")) {
      location = location.substring(0, location.length() - 1);
    }
    return location;
  }

  public QName uploadCSAR(final File file, final boolean overwrite) throws URISyntaxException, IOException {
    final String location = uploadCSARToWinery(file, overwrite);
    if (Objects.isNull(location)) {
      return null;
    }
    // create QName of the created serviceTemplate resource
    final String localPart = getLastPathFragment(location);
    final String namespaceDblEnc = getLastPathFragment(location.substring(0, location.lastIndexOf("/")));
    final String namespace = URLDecoder.decode(URLDecoder.decode(namespaceDblEnc));

    return new QName(namespace, localPart);
  }

  public QName createServiceTemplateFromXaaSPackage(final File file, final QName artifactType,
                                                    final Set<QName> nodeTypes, final QName infrastructureNodeType,
                                                    final Map<String, String> tags) throws URISyntaxException,
    IOException {
    final MultipartEntity entity = new MultipartEntity();

    // file
    final ContentBody fileBody = new FileBody(file);
    final FormBodyPart filePart = new FormBodyPart("file", fileBody);
    entity.addPart(filePart);

    // artefactType
    final ContentBody artefactTypeBody = new StringBody(artifactType.toString());
    final FormBodyPart artefactTypePart = new FormBodyPart("artefactType", artefactTypeBody);
    entity.addPart(artefactTypePart);

    // nodeTypes
    if (!nodeTypes.isEmpty()) {
      String nodeTypesAsString = "";
      for (final QName nodeType : nodeTypes) {
        nodeTypesAsString += nodeType.toString() + ",";
      }

      final ContentBody nodeTypesBody =
        new StringBody(nodeTypesAsString.substring(0, nodeTypesAsString.length() - 1));
      final FormBodyPart nodeTypesPart = new FormBodyPart("nodeTypes", nodeTypesBody);
      entity.addPart(nodeTypesPart);
    }

    // infrastructureNodeType
    if (infrastructureNodeType != null) {
      final ContentBody infrastructureNodeTypeBody = new StringBody(infrastructureNodeType.toString());
      final FormBodyPart infrastructureNodeTypePart =
        new FormBodyPart("infrastructureNodeType", infrastructureNodeTypeBody);
      entity.addPart(infrastructureNodeTypePart);
    }

    // tags
    if (!tags.isEmpty()) {
      String tagsString = "";
      for (final String key : tags.keySet()) {
        if (tags.get(key) == null) {
          tagsString += key + ",";
        } else {
          tagsString += key + ":" + tags.get(key) + ",";
        }
      }

      final ContentBody tagsBody = new StringBody(tagsString.substring(0, tagsString.length() - 1));
      final FormBodyPart tagsPart = new FormBodyPart("tags", tagsBody);
      entity.addPart(tagsPart);
    }

    // POST to XaaSPackager
    final HttpPost xaasPOST = new HttpPost();
    xaasPOST.setURI(new URI(this.wineryPath + "servicetemplates/"));
    xaasPOST.setEntity(entity);
    final HttpResponse xaasResp = this.client.execute(xaasPOST);

    // create QName of the created serviceTemplate resource
    String location = getHeaderValue(xaasResp, "Location");

    if (location.endsWith("/")) {
      location = location.substring(0, location.length() - 1);
    }

    final String localPart = getLastPathFragment(location);
    final String namespaceDblEnc = getLastPathFragment(location.substring(0, location.lastIndexOf("/")));
    final String namespace = URLDecoder.decode(URLDecoder.decode(namespaceDblEnc));

    return new QName(namespace, localPart);
  }

  private String getLastPathFragment(final String url) {
    if (url.endsWith("/")) {
      return getLastPathFragment(url.subSequence(0, url.length() - 1).toString());
    } else {
      return url.substring(url.lastIndexOf("/") + 1);
    }
  }

  private String getHeaderValue(final HttpResponse response, final String headerName) {
    for (final Header header : response.getAllHeaders()) {
      if (header.getName().equals(headerName)) {
        return header.getValue();
      }
    }
    return null;
  }

  public List<QName> getServiceTemplates(final List<String> tags) {
    final List<QName> qnames = new ArrayList<>();
    final ObjectMapper mapper = new ObjectMapper();

    for (final QName serviceTemplateId : this.getServiceTemplates()) {
      WineryConnector.LOG.debug("Querying Winery Repository at " + this.wineryPath + " for ServiceTemplate "
        + serviceTemplateId);
      try {

        final HttpGet serviceTemplateTagsGET = new HttpGet();
        serviceTemplateTagsGET.setHeader("Accept", "application/json");
        serviceTemplateTagsGET.setURI(new URI(this.wineryPath + "servicetemplates/"
          + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())) + "/"
          + serviceTemplateId.getLocalPart() + "/tags"));

        final HttpResponse serviceTemplateTagsGETResp = this.client.execute(serviceTemplateTagsGET);
        final String tagsJsonResponse = EntityUtils.toString(serviceTemplateTagsGETResp.getEntity());

        final JsonNode tagsJsonNode = mapper.readTree(tagsJsonResponse);

        int matched = 0;

        if (!tagsJsonNode.isArray()) {
          continue;
        }
        for (final Iterator<JsonNode> iter = tagsJsonNode.elements(); iter.hasNext(); ) {
          final JsonNode key = iter.next();

          final HttpGet serviceTemplateTagGET = new HttpGet();
          serviceTemplateTagGET.setHeader("Accept", "application/json");
          serviceTemplateTagGET.setURI(new URI(
            serviceTemplateTagsGET.getURI().toString() + "/" + key.textValue()));

          final HttpResponse serviceTemplateTagGETResp = this.client.execute(serviceTemplateTagGET);
          final String tagJsonResponse = EntityUtils.toString(serviceTemplateTagGETResp.getEntity());

          final JsonNode tagJsonNode = mapper.readTree(tagJsonResponse);

          if (!tagJsonNode.isObject() || !tagJsonNode.has("name")) {
            continue;
          }
          if (tags.contains(tagJsonNode.get("name").textValue())) {
            matched++;
          }
        }

        if (matched == tags.size()) {
          qnames.add(serviceTemplateId);
        }
      } catch (URISyntaxException | IOException e) {
        e.printStackTrace();
      }
    }
    return qnames;
  }

  public List<QName> getServiceTemplates() {
    final List<QName> qnames = new ArrayList<>();

    try {
      final HttpGet get = new HttpGet();
      get.setHeader("Accept", "application/json");
      get.setURI(new URI(this.wineryPath + "servicetemplates"));
      final HttpResponse resp = this.client.execute(get);
      final String jsonResponse = EntityUtils.toString(resp.getEntity());

      final ObjectMapper mapper = new ObjectMapper();
      final List<Object> obj = mapper.readValue(jsonResponse, ArrayList.class);
      for (final Object jsonObj : obj) {
        // FIXME this depends on the internal representation of json objects in the deserializer
        final LinkedHashMap<String, String> hashMap = (LinkedHashMap<String, String>) jsonObj;

        final String id = hashMap.get("id");
        final String namespace = hashMap.get("namespace");

        qnames.add(new QName(namespace, id));
      }
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }

    return qnames;
  }

  /**
   * Performs management feature enrichment for the CSAR represented by the given file.
   *
   * @param file the file containing the CSAR for the management feature enrichment
   */
  public void performManagementFeatureEnrichment(final File file) {
    if (!isWineryRepositoryAvailable()) {
      LOG.error("Management feature enrichment enabled, but Container Repository is not available!");
      return;
    }
    LOG.debug("Container Repository is available. Uploading file {} to repo...", file.getName());
    try {
      // upload CSAR to enable enrichment in Winery
      final String location = uploadCSARToWinery(file, false);

      if (Objects.isNull(location)) {
        LOG.error("Upload return location equal to null!");
        return;
      }

      LOG.debug("Stored CSAR at location: {}", location.toString());

      // get all available features for the given CSAR
      final HttpGet get = new HttpGet();
      get.setHeader("Accept", "application/json");
      get.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
      HttpResponse resp = this.client.execute(get);
      final String jsonResponse = EntityUtils.toString(resp.getEntity());
      closeConnection(resp);

      LOG.debug("Container Repository returned the following features:", jsonResponse);

      // apply the found features to the CSAR
      final HttpPut put = new HttpPut();
      put.setHeader("Content-Type", "application/json");
      put.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
      final StringEntity stringEntity = new StringEntity(jsonResponse);
      put.setEntity(stringEntity);
      resp = client.execute(put);

      LOG.debug("Feature enrichment returned status line: {}", resp.getStatusLine());

      // retrieve updated CSAR from winery
      final URL url = new URL(location + "/?csar");
      Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      LOG.debug("Updated CSAR file in the Container with enriched topology.");
    } catch (final Exception e) {
      LOG.error("{} while performing management feature enrichment: {}", e.getClass().getSimpleName(), e.getMessage(), e);
    }
  }

  /**
   * Closes the InputStream of the given HTTP response to release all related resources.
   *
   * @param response the response to close
   */
  private void closeConnection(final HttpResponse response) {
    try {
      if (Objects.nonNull(response.getEntity())) {
        response.getEntity().getContent().close();
      }
    } catch (final Exception e) {
      LOG.error("Unable to close stream of HTTP response to release resources.", e);
    }
  }
}
