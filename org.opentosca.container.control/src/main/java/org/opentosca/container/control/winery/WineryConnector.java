package org.opentosca.container.control.winery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.namespace.QName;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
 * Copyright 2016 IAAS University of Stuttgart
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class WineryConnector {

    final private static Logger LOG = LoggerFactory.getLogger(WineryConnector.class);

    private static final String FEATURE_ENRICHMENT_SUFFIX = "/topologytemplate/availablefeatures";
    private static final String SERVICE_TEMPLATES_SUFFIX = "servicetemplates";

    private final String wineryPath;

    public WineryConnector() {
        String configurationValue = Settings.getSetting("org.opentosca.container.connector.winery.url");
        if (configurationValue == null) {
            configurationValue = "";
        }
        try {
            new URI(configurationValue);
        } catch (URISyntaxException e) {
            LOG.error("Winery Connector configuration is not valid", e);
        }
        this.wineryPath = configurationValue.endsWith("/") ? configurationValue : configurationValue + "/";
        LOG.debug("Initialized Winery Connector for endpoint " + this.wineryPath);
    }

    public boolean isWineryRepositoryAvailable() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final URI serviceTemplatesUri = new URI(this.wineryPath + SERVICE_TEMPLATES_SUFFIX);
            LOG.debug("Checking if winery is available at " + serviceTemplatesUri);

            final HttpGet get = new HttpGet();
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setURI(serviceTemplatesUri);
            final CloseableHttpResponse resp = httpClient.execute(get);
            resp.close();

            return resp.getStatusLine().getStatusCode() < 400;
        } catch (URISyntaxException | IOException e) {
            LOG.error("Exception while checking for availability of Container Repository: ");
            return false;
        }
    }

    public URI getServiceTemplateURI(final QName serviceTemplateId) {
        return this.getServiceTemplateURI(serviceTemplateId, this.wineryPath);
    }

    public URI getServiceTemplateURI(final QName serviceTemplateId, String wineryPath) {
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

    public void clearRepository(String wineryUrl) {
        String clearUrl = wineryUrl + "/admin/repository";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // get all available features for the given CSAR
            final HttpDelete delete = new HttpDelete();
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setURI(new URI(clearUrl));
            CloseableHttpResponse resp = httpClient.execute(delete);
            resp.close();
        } catch (Exception e) {
            LOG.error("Couldn't delete repository contents", e);
        }
    }

    public Collection<QName> getServiceTemplates(String wineryUrl) {
        Collection<QName> result = Lists.newArrayList();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // get all available features for the given CSAR
            final HttpGet get = new HttpGet();
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setURI(new URI(wineryUrl + "/servicetemplates"));
            CloseableHttpResponse resp = httpClient.execute(get);
            JsonElement jsonResponse = new JsonParser().parse(EntityUtils.toString(resp.getEntity()));
            Iterator<JsonElement> iter = jsonResponse.getAsJsonArray().iterator();
            while (iter.hasNext()) {
                String qnameString = iter.next().getAsJsonObject().get("qName").getAsString();
                result.add(QName.valueOf(qnameString));
            }
            resp.close();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
        return result;
    }

    private String uploadCSARToWinery(final File file, final boolean overwrite, String url) throws URISyntaxException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        final ContentBody fileBody = new FileBody(file);
        final ContentBody overwriteBody = new StringBody(String.valueOf(overwrite), ContentType.TEXT_PLAIN);
        final FormBodyPart filePart = FormBodyPartBuilder.create("file", fileBody).build();
        final FormBodyPart overwritePart = FormBodyPartBuilder.create("overwrite", overwriteBody).build();
        builder.addPart(filePart);
        builder.addPart(overwritePart);

        final HttpEntity entity = builder.build();

        final HttpPost wineryPost = new HttpPost();

        wineryPost.setURI(new URI(url));
        wineryPost.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final CloseableHttpResponse wineryResp = httpClient.execute(wineryPost);
            String location = getHeaderValue(wineryResp, HttpHeaders.LOCATION);
            wineryResp.close();

            if (Objects.nonNull(location) && location.endsWith("/")) {
                location = location.substring(0, location.length() - 1);
            }
            return location;
        } catch (final IOException e) {
            LOG.error("Exception while uploading CSAR to the Container Repository: ", e);
            return "";
        }
    }

    public QName uploadCSAR(final File file, final boolean overwrite) throws URISyntaxException, IOException {
        return this.uploadCSAR(file, overwrite, this.wineryPath);
    }

    public QName uploadCSAR(final File file, final boolean overwrite, String url) throws URISyntaxException, IOException {
        final String location = uploadCSARToWinery(file, overwrite, url);
        if (Objects.isNull(location)) {
            return null;
        }
        // create QName of the created serviceTemplate resource
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
        return Arrays.stream(response.getAllHeaders())
            .filter(header -> header.getName().equals(headerName))
            .findFirst()
            .map(header -> header.getValue())
            .orElse(null);
    }

    /**
     * Performs management feature enrichment for the CSAR represented by the given file.
     *
     * @param file the file containing the CSAR for the management feature enrichment
     */
    public void performManagementFeatureEnrichment(final File file) {
        performManagementFeatureEnrichment(file, false, this.wineryPath);
    }

    public void performManagementFeatureEnrichment(final File file, boolean overwrite, String wineryLocation) {
        if (!isWineryRepositoryAvailable()) {
            LOG.error("Management feature enrichment enabled, but Container Repository is not available!");
            return;
        }
        LOG.debug("Container Repository is available. Uploading file {} to repo...", file.getName());
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // upload CSAR to enable enrichment in Winery
            final String location = uploadCSARToWinery(file, overwrite, wineryLocation);

            if (Objects.isNull(location)) {
                LOG.error("Upload return location equal to null!");
                return;
            }

            LOG.debug("Stored CSAR at location: {}", location);

            // get all available features for the given CSAR
            final HttpGet get = new HttpGet();
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
            CloseableHttpResponse resp = httpClient.execute(get);
            final String jsonResponse = EntityUtils.toString(resp.getEntity());
            resp.close();

            LOG.debug("Container Repository returned the following features: {}", jsonResponse);

            // apply the found features to the CSAR
            final HttpPut put = new HttpPut();
            put.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            put.setURI(new URI(location + FEATURE_ENRICHMENT_SUFFIX));
            final StringEntity stringEntity = new StringEntity(jsonResponse);
            put.setEntity(stringEntity);
            resp = httpClient.execute(put);
            resp.close();

            LOG.debug("Feature enrichment returned status line: {}", resp.getStatusLine());

            // retrieve updated CSAR from winery
            final URL url = new URL(location + "/?csar");
            Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOG.debug("Updated CSAR file in the Container with enriched topology.");
        } catch (final Exception e) {
            LOG.error("{} while performing management feature enrichment: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    public void downloadServiceTemplate(Path targetPath, QName serviceTemplateId, String wineryRepository) throws IOException {
        URI serviceTemplateUri = this.getServiceTemplateURI(serviceTemplateId, wineryRepository);
        final URL url = new URL(serviceTemplateUri.toString() + "/?csar");
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
