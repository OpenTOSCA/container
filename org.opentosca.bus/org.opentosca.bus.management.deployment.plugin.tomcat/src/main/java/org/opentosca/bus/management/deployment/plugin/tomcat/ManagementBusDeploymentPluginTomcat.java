package org.opentosca.bus.management.deployment.plugin.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.IHTTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Management Bus-Plug-in for the deployment of WAR IAs on an Apache Tomcat web server.<br>
 * <br>
 * <p>
 * <p>
 * <p>
 * This Plug-in is able to deploy and undeploy WAR Artifacts on an Apache Tomcat. It gets a camel exchange object from
 * the Management Bus which contains all information that is needed for the deployment/undeployment. <br>
 * <br>
 *
 * <b>Tomcat config:</b> Tomcat location, username and password for this Plug-in are defined in the
 * class {@link Settings} or the corresponding config.ini file.<br>
 * <br>
 *
 * <b>Deployment:</b> The {@link MBHeader#ARTIFACTREFERENCES_LIST_STRING} header field contains a
 * list with all ArtifactReferences for the current IA. This list is used to find the reference to the WAR-File that has
 * to be deployed. When a reference is found, the respective file ist retrieved. The {@link
 * MBHeader#ARTIFACTSERVICEENDPOINT_STRING} header field determines whether the deployment is done on the management
 * infrastructure or as part of the topology to which this IA belongs. If the header contains a placeholder the IA is
 * deployed as part of the topology and this Plug-in just returns an endpoint. Otherwise the deployment is done via a
 * HTTP request to the Apache Tomcat.<br>
 * <br>
 *
 * <b>Undeployment:</b> The {@link MBHeader#ENDPOINT_URI} header field contains the endpoint of the
 * deployed IA. This endpoint is used to calculate the deployment path of the IA and to send an undeployment request to
 * the Tomcat.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */
@Component
public class ManagementBusDeploymentPluginTomcat implements IManagementBusDeploymentPluginService {

    // In messages.properties defined plugin types and capabilities
    private static final String TYPES = "{http://www.example.com/ToscaTypes}WAR,{http://opentosca.org/artifacttypes}WAR";
    private static final String CAPABILITIES = "http://tomcat.apache.org/tomcat7.0, http://www.jcp.org/javaserverpages2.2 , http://www.jcp.org/servlet3.0";

    private static final Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginTomcat.class);

    private final IHTTPService httpService;

    @Inject
    public ManagementBusDeploymentPluginTomcat(IHTTPService httpService) {
        this.httpService = httpService;
    }

    @Override
    public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {

        LOG.debug("Trying to deploy IA on Tomcat.");
        final Message message = exchange.getIn();
        @SuppressWarnings("unchecked") final List<String> artifactReferences =
            message.getHeader(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(), List.class);
        CsarId csarId = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);

        // get URL of the WAR-File that has to be deployed
        final URL warURL = getWARFileReference(artifactReferences);
        if (warURL == null) {
            LOG.error("Deployment failed: no referenced WAR-File found");
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), null);
            return exchange;
        }

        // get the WAR artifact as file
        final File warFile = getWarFile(warURL);
        if (warFile == null) {
            LOG.error("Deployment failed: unable to retrieve WAR-File from URL");
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), null);
            return exchange;
        }

        // get file name of the WAR-File
        final String fileName = FilenameUtils.getBaseName(warURL.getPath());
        // retrieve ServiceEndpoint property from exchange headers
        final String endpointSuffix =
            message.getHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), "", String.class);

        if (endpointSuffix.equals("")) {
            LOG.info("No endpoint suffix defined.");
        } else {
            LOG.info("Endpoint suffix from header: {}", endpointSuffix);
        }

        // if placeholder is defined the deployment is done in the topology
        final String placeholderBegin = "/PLACEHOLDER_";
        final String placeholderEnd = "_PLACEHOLDER/";
        String endpoint = null;
        if (endpointSuffix.contains(placeholderBegin)
            && endpointSuffix.contains(placeholderEnd)) {

            // just return a created endpoint and do not perform deployment
            final String placeholder =
                endpointSuffix.substring(endpointSuffix.indexOf(placeholderBegin),
                    endpointSuffix.indexOf(placeholderEnd) + placeholderEnd.length());

            LOG.info("Placeholder defined: {}. Deployment is done as part of the topology and not on the management infrastructure. ",
                placeholder);

            final String endpointBegin = endpointSuffix.substring(0, endpointSuffix.indexOf(placeholderBegin));
            final String endpointEnd =
                endpointSuffix.substring(endpointSuffix.lastIndexOf(placeholderEnd) + placeholderEnd.length());

            // We assume that the WAR-File in the topology is deployed at the default port
            // 8080 and only with the file name as path. Find a better solution which looks
            // into the topology and determines the correct endpoint.
            endpoint = endpointBegin + placeholder + ":8080/" + fileName + "/" + endpointEnd;
        } else {

            // check if Tomcat is running to continue deployment
            if (!isRunning()) {
                LOG.error("Deployment failed: Tomcat is not running or can´t be accessed");
                message.setHeader(MBHeader.ENDPOINT_URI.toString(), null);
                return exchange;
            }
            LOG.info("Tomcat is running and can be accessed.");

            final QName typeImplementation =
                message.getHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), QName.class);

            final String triggeringContainer =
                message.getHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), String.class);

            // perform deployment on management infrastructure
            endpoint = deployWAROnTomcat(csarId, warFile, triggeringContainer, typeImplementation, fileName);

            if (endpoint != null) {
                // add endpoint suffix to endpoint of deployed WAR
                endpoint = endpoint.concat(endpointSuffix);
                LOG.info("Complete endpoint of IA {}: {}", fileName, endpoint);
            }
        }

        // delete the temporary file
        // it's not terrible if we don't get to clean this up, it will be deleted once the JVM terminates or we overwrite it
        warFile.delete();

        // set endpoint and pass camel exchange back to caller
        message.setHeader(MBHeader.ENDPOINT_URI.toString(), getURI(endpoint));
        return exchange;
    }

    @Override
    public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {
        LOG.debug("Trying to undeploy IA from Tomcat.");
        final Message message = exchange.getIn();
        // set operation state to false and only change after successful undeployment
        message.setHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), false);
        // get endpoint from header to calculate deployment path
        final URI endpointURI = message.getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

        if (endpointURI == null) {
            LOG.error("No endpoint defined. Undeployment not possible!");
            return exchange;
        }

        final String endpoint = endpointURI.toString();
        LOG.debug("Endpoint for undeployment: {}", endpoint);
        // delete Tomcat URL prefix from endpoint
        String deployPath = endpoint.replace(Settings.ENGINE_IA_TOMCAT_URL, "");
        // delete ServiceEndpoint suffix from endpoints
        deployPath = deployPath.substring(0, StringUtils.ordinalIndexOf(deployPath, "/", 4));

        // command to perform deployment on Tomcat from local file
        final String undeploymentURL = Settings.ENGINE_IA_TOMCAT_URL + "/manager/text/undeploy?path=" + deployPath;
        LOG.debug("Undeployment command: {}", undeploymentURL);

        try {
            // perform undeployment request on Tomcat
            final HttpResponse httpResponse = this.httpService.Get(undeploymentURL, Settings.ENGINE_IA_TOMCAT_USERNAME, Settings.ENGINE_IA_TOMCAT_PASSWORD);
            final String response = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);

            LOG.debug("Tomcat response: {}", response);

            // check if WAR-File was undeployed successfully
            if (response.contains("OK - Undeployed application at context path [" + deployPath + "]")) {
                LOG.debug("IA successfully undeployed from Tomcat!");
                message.setHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), true);
            } else {
                LOG.error("Undeployment not successfully!");
            }
        } catch (final IOException e) {
            LOG.error("IOException occured while undeploying the WAR-File: {}!", e);
        }
        return exchange;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {
        LOG.debug("Getting Types: {}.", TYPES);
        final List<String> types = new ArrayList<>();

        for (final String type : TYPES.split("[,;]")) {
            types.add(type.trim());
        }
        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {
        LOG.debug("Getting Plugin-Capabilities: {}.", CAPABILITIES);
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

    /**
     * Check if the Tomcat which is references as the IA-engine in the container config.ini is running.
     *
     * @return true if Tomcat is running and can be accessed, false otherwise
     */
    private boolean isRunning() {
        LOG.info("Checking if Tomcat is running on {} and can be accessed...",
            Settings.ENGINE_IA_TOMCAT_URL);

        // URL to get serverinfo from Tomcat
        final String url = Settings.ENGINE_IA_TOMCAT_URL + "/manager/text/serverinfo";

        // execute HTPP GET on URL and check the response
        try {
            final HttpResponse httpResponse = this.httpService.Get(url, Settings.ENGINE_IA_TOMCAT_USERNAME, Settings.ENGINE_IA_TOMCAT_PASSWORD);
            final String response = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);

            LOG.debug(response);
            if (response.contains("OK - Server info")) {
                return true;
            }
        } catch (final Exception e) {
            LOG.error("Error while checking for availability of the Tomcat: {}",
                e.getMessage());
        }
        return false;
    }

    /**
     * Check if the artifact references contain a WAR-File and return the URL to the file if so.
     *
     * @param artifactReferences the references to check if a WAR-File is available
     * @return the URL to the file or <tt>null</tt> if no file is found
     */
    private URL getWARFileReference(final List<String> artifactReferences) {
        LOG.info("Searching for a reference to a WAR-File...");

        if (artifactReferences == null) {
            return null;
        }
        for (final String reference : artifactReferences) {
            // check if reference targets a WAR-File
            if (reference.toLowerCase().endsWith(".war")) {
                LOG.info("Found WAR-File reference: {}", reference);
                try {
                    return new URL(reference);
                } catch (final MalformedURLException e) {
                    LOG.error("Failed to convert the reference to a URL: {}",
                        e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Retrieve the WAR-File from the given URL and store it as local temp file.
     *
     * @param warURL the URL to the WAR-File that shall be retrieved
     * @return the file if retrieval was successful, <tt>null</tt> otherwise
     */
    private File getWarFile(final URL warURL) {
        LOG.info("Trying to retrieve WAR-File from URL: {}", warURL);

        if (warURL == null) {
            return null;
        }
        try {
            // store WAR artifact as temporary file
            final File tempFile = File.createTempFile("Artifact", ".war");
            tempFile.deleteOnExit();
            FileUtils.copyURLToFile(warURL, tempFile);
            return tempFile;
        } catch (final IOException e) {
            LOG.error("Failed to retrieve WAR-File: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Deploy the given WAR-File on the Tomcat. As path on Tomcat the host name of the triggering OpenTOSCA Container
     * and the NodeTypeImplementation with removed special characters (except '-' and '_') concatenated with the name of
     * the WAR-File (without ".war") is used:
     * <tt>/[Container-Hostname]/[TypeImplementationID]/[File-Name]</tt>
     *
     * @param csarId              the name of the CSAR the IA belongs to
     * @param warFile             the WAR artifact that has to be deployed
     * @param triggeringContainer the host name of the OpenTOSCA Container that triggered the IA deployment
     * @param typeImplementation  the NodeTypeImplementation or RelationshipTypeImplementation which is used to create a
     *                            unique path where the WAR is deployed
     * @param fileName            the file name which is part of the deployment path
     */
    private String deployWAROnTomcat(final CsarId csarId, final File warFile, final String triggeringContainer,
                                     final QName typeImplementation, final String fileName) {

        if (triggeringContainer == null) {
            LOG.warn("Triggering Container host name is null. Deployment aborted because it is part of the deployment path on Tomcat");
            return null;
        }
        if (typeImplementation == null) {
            LOG.warn("NodeTypeImplementation ID is null. Deployment aborted because the ID is part of the deployment path on Tomcat");
            return null;
        }
        // path where the WAR is deployed on the Tomcat
        final String deployPath = "/" + getConvertedString(triggeringContainer) + "/" + csarId.csarName() + "/"
            + getConvertedString(typeImplementation.toString()) + "/" + fileName;

        // command to perform deployment on Tomcat from local file
        final String deploymentURL =
            Settings.ENGINE_IA_TOMCAT_URL + "/manager/text/deploy?update=true&path=" + deployPath;

        // create HttpEntity which contains the WAR-File
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        final FileBody fileBody = new FileBody(warFile);
        builder.addPart(fileName + ".war", fileBody);
        final HttpEntity entity = builder.build();

        try {
            // perform deployment request on Tomcat
            final HttpResponse httpResponse =
                this.httpService.Put(deploymentURL, entity, Settings.ENGINE_IA_TOMCAT_USERNAME,
                    Settings.ENGINE_IA_TOMCAT_PASSWORD);

            final String response = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);

            LOG.info("Tomcat response to deployment request: {}", response);

            // check if WAR-File was deployed successfully.
            if (response.contains("OK - Deployed application at context path " + deployPath)
                || response.contains("OK - Deployed application at context path [" + deployPath + "]")) {
                LOG.info("Deployment was successful.");

                // concatenate service endpoint
                String endpoint = Settings.ENGINE_IA_TOMCAT_URL + deployPath;
                LOG.info("Endpoint of deployed service: {}", endpoint);
                return endpoint;
            } else {
                LOG.error("Deployment was not successful.");
            }
        } catch (final IOException e) {
            LOG.error("IOException occured while deploying the WAR-File: {}!",
                e);
        }
        return null;
    }

    /**
     * Remove invalid characters from the provided String.
     *
     * @param string the String to convert
     * @return String with replaced '.' by "-" and removed remaining special characters (except '-' and '_').
     */
    private String getConvertedString(final String string) {
        LOG.debug("Converting String: {}", string);

        // replace '.' by '-' to leave IPs unique
        String convertedString = string.replace(".", "-");
        // remove all special characters except '-' and '_'
        convertedString = convertedString.replaceAll("[^-a-zA-Z0-9_]", "");
        LOG.debug("Converted string: {}", convertedString);
        return convertedString;
    }

    /**
     * Convert a String to an URI
     *
     * @param string the String that has to be converted to URI
     * @return URI representation of the String if convertible, null otherwise
     */
    private URI getURI(final String string) {
        if (string != null) {
            try {
                return new URI(string);
            } catch (final URISyntaxException e) {
                LOG.error("Failed to transform String to URI: {} ", string);
            }
        }
        return null;
    }
}
