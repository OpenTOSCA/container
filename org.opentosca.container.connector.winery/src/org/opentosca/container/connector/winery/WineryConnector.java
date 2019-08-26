package org.opentosca.container.connector.winery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class WineryConnector {

    final private static Logger LOG = LoggerFactory.getLogger(WineryConnector.class);

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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            final URI serviceTemplatesUri = new URI(this.wineryPath + "servicetemplates");
            LOG.debug("Checking if winery is available at " + serviceTemplatesUri.toString());

            final HttpGet get = new HttpGet();
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setURI(serviceTemplatesUri);

            final CloseableHttpResponse resp = httpClient.execute(get);
            resp.close();

            return resp.getStatusLine().getStatusCode() < 400;
        }
        catch (IOException | URISyntaxException e) {
            LOG.error("Exception while checking for availability of Container Repository: " + e.getMessage());
            return false;
        }
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
            LOG.error("Exception while parsing URI for ServiceTemplate: " + e.getMessage());
            return null;
        }
    }

    private String uploadCSARToWinery(final File file, final boolean overwrite) throws URISyntaxException, IOException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        final ContentBody fileBody = new FileBody(file);
        final ContentBody overwriteBody = new StringBody(String.valueOf(overwrite), ContentType.TEXT_PLAIN);
        final FormBodyPart filePart = FormBodyPartBuilder.create().setName("file").setBody(fileBody).build();
        final FormBodyPart overwritePart =
            FormBodyPartBuilder.create().setName("overwrite").setBody(overwriteBody).build();
        builder.addPart(filePart);
        builder.addPart(overwritePart);

        final HttpEntity entity = builder.build();

        final HttpPost wineryPost = new HttpPost();
        wineryPost.setURI(new URI(this.wineryPath));
        wineryPost.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final CloseableHttpResponse wineryResp = httpClient.execute(wineryPost);
            String location = getHeaderValue(wineryResp, HttpHeaders.LOCATION);
            wineryResp.close();

            if (Objects.nonNull(location) && location.endsWith("/")) {
                location = location.substring(0, location.length() - 1);
            }
            return location;
        }
        catch (final IOException e) {
            LOG.error("Exception while uploading CSAR to the Container Repository: " + e.getMessage());
            return "";
        }
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
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // file
        final ContentBody fileBody = new FileBody(file);
        final FormBodyPart filePart = FormBodyPartBuilder.create().setName("file").setBody(fileBody).build();
        builder.addPart(filePart);

        // artefactType
        final ContentBody artefactTypeBody = new StringBody(artifactType.toString(), ContentType.TEXT_PLAIN);
        final FormBodyPart artefactTypePart =
            FormBodyPartBuilder.create().setName("artefactType").setBody(artefactTypeBody).build();
        builder.addPart(artefactTypePart);

        // nodeTypes
        if (!nodeTypes.isEmpty()) {
            String nodeTypesAsString = "";
            for (final QName nodeType : nodeTypes) {
                nodeTypesAsString += nodeType.toString() + ",";
            }

            final ContentBody nodeTypesBody =
                new StringBody(nodeTypesAsString.substring(0, nodeTypesAsString.length() - 1), ContentType.TEXT_PLAIN);
            final FormBodyPart nodeTypesPart =
                FormBodyPartBuilder.create().setName("nodeTypes").setBody(nodeTypesBody).build();
            builder.addPart(nodeTypesPart);
        }

        // infrastructureNodeType
        if (infrastructureNodeType != null) {
            final ContentBody infrastructureNodeTypeBody =
                new StringBody(infrastructureNodeType.toString(), ContentType.TEXT_PLAIN);
            final FormBodyPart infrastructureNodeTypePart =
                FormBodyPartBuilder.create().setName("infrastructureNodeType").setBody(infrastructureNodeTypeBody)
                                   .build();
            builder.addPart(infrastructureNodeTypePart);
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

            final ContentBody tagsBody =
                new StringBody(tagsString.substring(0, tagsString.length() - 1), ContentType.TEXT_PLAIN);
            final FormBodyPart tagsPart = FormBodyPartBuilder.create().setName("tags").setBody(tagsBody).build();
            builder.addPart(tagsPart);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // POST to XaaSPackager
            final HttpPost xaasPOST = new HttpPost();
            xaasPOST.setURI(new URI(this.wineryPath + "servicetemplates/"));
            xaasPOST.setEntity(builder.build());
            final CloseableHttpResponse xaasResp = httpClient.execute(xaasPOST);
            xaasResp.close();

            // create QName of the created serviceTemplate resource
            String location = getHeaderValue(xaasResp, HttpHeaders.LOCATION);

            if (location.endsWith("/")) {
                location = location.substring(0, location.length() - 1);
            }

            final String localPart = getLastPathFragment(location);
            final String namespaceDblEnc = getLastPathFragment(location.substring(0, location.lastIndexOf("/")));
            final String namespace = URLDecoder.decode(URLDecoder.decode(namespaceDblEnc));

            return new QName(namespace, localPart);
        }
        catch (final IOException e) {
            LOG.error("Exception while calling XaaS packager: " + e.getMessage());
            return null;
        }
    }

    private String getLastPathFragment(final String url) {
        if (url.endsWith("/")) {
            return getLastPathFragment(url.subSequence(0, url.length() - 1).toString());
        } else {
            return url.substring(url.lastIndexOf("/") + 1);
        }
    }

    private String getHeaderValue(final HttpResponse response, final String headerName) {
        return Arrays.stream(response.getAllHeaders()).filter(header -> header.getName().equals(headerName)).findFirst()
                     .map(header -> header.getValue()).orElse(null);
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

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // upload CSAR to enable enrichment in Winery
            final String location = uploadCSARToWinery(file, false);

            if (!Objects.nonNull(location)) {
                LOG.error("Upload returned location equal to null!");
                return;
            }

            LOG.debug("Stored CSAR at location: {}", location.toString());

            // get all available features for the given CSAR
            final HttpGet get = new HttpGet();
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
            CloseableHttpResponse resp = httpClient.execute(get);
            final String jsonResponse = EntityUtils.toString(resp.getEntity());
            resp.close();

            LOG.debug("Container Repository returned the follow features: {}", jsonResponse);

            // apply the found features to the CSAR
            final HttpPut put = new HttpPut();
            put.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            put.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
            final StringEntity stringEntity = new StringEntity(jsonResponse);
            put.setEntity(stringEntity);
            resp = httpClient.execute(put);
            resp.close();

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
}
