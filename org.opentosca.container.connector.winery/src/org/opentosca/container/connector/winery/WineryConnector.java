package org.opentosca.container.connector.winery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class WineryConnector {

    final private static Logger LOG = LoggerFactory.getLogger(WineryConnector.class);

    private final DefaultHttpClient client = new DefaultHttpClient();;
    private String wineryPath;

    private static final String FEATURE_ENRICHMENT_SUFFIX = "/topologytemplate/availablefeatures";

    public WineryConnector() {
        this.wineryPath = Settings.getSetting("org.opentosca.container.connector.winery.url");
        LOG.debug("Initialized Winery Connector for endpoint " + this.wineryPath);
        if (!this.wineryPath.endsWith("/")) {
            this.wineryPath = this.wineryPath + "/";
        }
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
        }
        catch (final URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public String getWineryPath() {
        return this.wineryPath;
    }

    public URI getServiceTemplateURI(final QName serviceTemplateId) {
        try {
            LOG.debug("Trying to fetch URI to Service Template" + serviceTemplateId.toString());
            return new URI(this.wineryPath + "servicetemplates/"
                + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI())) + "/"
                + serviceTemplateId.getLocalPart());
        }
        catch (final URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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

                if (tagsJsonNode.isArray()) {

                    for (final Iterator<JsonNode> iter = tagsJsonNode.elements(); iter.hasNext();) {
                        final JsonNode key = iter.next();

                        final HttpGet serviceTemplateTagGET = new HttpGet();
                        serviceTemplateTagGET.setHeader("Accept", "application/json");
                        serviceTemplateTagGET.setURI(new URI(
                            serviceTemplateTagsGET.getURI().toString() + "/" + key.textValue()));
                        final HttpResponse serviceTemplateTagGETResp = this.client.execute(serviceTemplateTagGET);
                        final String tagJsonResponse = EntityUtils.toString(serviceTemplateTagGETResp.getEntity());

                        final JsonNode tagJsonNode = mapper.readTree(tagJsonResponse);

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

            }
            catch (final URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (final ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
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

            final ArrayList<Object> obj = mapper.readValue(jsonResponse, ArrayList.class);

            for (final Object jsonObj : obj) {
                final LinkedHashMap<String, String> hashMap = (LinkedHashMap<String, String>) jsonObj;

                final String id = hashMap.get("id");
                final String namespace = hashMap.get("namespace");

                qnames.add(new QName(namespace, id));
            }

        }
        catch (final ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final URISyntaxException e) {
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

            if (!Objects.nonNull(location)) {
                LOG.error("Upload returned location equal to null!");
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

            LOG.debug("Container Repository returned the follow features: {}", jsonResponse);

            // apply the found features to the CSAR
            final HttpPut put = new HttpPut();
            put.setHeader("Content-Type", "application/json");
            put.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
            final StringEntity stringEntity = new StringEntity(jsonResponse);
            put.setEntity(stringEntity);
            resp = this.client.execute(put);

            LOG.debug("Feature enrichment retuned status line: {}", resp.getStatusLine());

            // retrieve updated CSAR from Winery
            final URL url = new URL(location + "/?csar");
            FileUtils.copyInputStreamToFile(url.openStream(), file);
            LOG.debug("Updated CSAR file in the Container with enriched topology.");
        }
        catch (final URISyntaxException e) {
            LOG.error("URISyntaxException while performing management feature enrichment: {}", e.getMessage());
        }
        catch (final IOException e) {
            LOG.error("IOException while performing management feature enrichment: {}", e.getMessage());
        }
        catch (final Exception e) {
            LOG.error("Exception while performing management feature enrichment: {}", e.getMessage());
        }
    }

    /**
     * Closes the InputStream of the given HTTP response to release all related resources.
     *
     * @param response the reponse to close
     */
    private void closeConnection(final HttpResponse response) {
        try {
            if (Objects.nonNull(response.getEntity())) {
                response.getEntity().getContent().close();
            }
        }
        catch (final Exception e) {
            LOG.error("Unable to close stream of HTTP reponse to release resources.");
        }
    }
}
