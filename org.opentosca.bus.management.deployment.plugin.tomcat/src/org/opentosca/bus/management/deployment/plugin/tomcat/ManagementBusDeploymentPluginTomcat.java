package org.opentosca.bus.management.deployment.plugin.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.deployment.plugin.tomcat.servicehandler.ServiceHandler;
import org.opentosca.bus.management.deployment.plugin.tomcat.util.Messages;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Bus-Plug-in for the deployment of WAR IAs on an Apache Tomcat web server.<br>
 * <br>
 *
 *
 *
 * TODO
 *
 *
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 *
 */
public class ManagementBusDeploymentPluginTomcat implements IManagementBusDeploymentPluginService {

    // In messages.properties defined plugin types and capabilities
    static final private String TYPES = Messages.DeploymentPluginTomcat_types;
    static final private String CAPABILITIES = Messages.DeploymentPluginTomcat_capabilities;

    static final private Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginTomcat.class);

    @Override
    public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {

        final Message message = exchange.getIn();
        ManagementBusDeploymentPluginTomcat.LOG.debug("Trying to deploy IA on tomcat.");

        // check if Tomcat is running to continue deployment
        if (isRunning()) {
            ManagementBusDeploymentPluginTomcat.LOG.info("Tomcat is running and can be accessed.");

            @SuppressWarnings("unchecked")
            final List<String> artifactReferences =
                message.getHeader(MBHeader.ARTIFACTREFERENCES_LIST_STRING.toString(), List.class);

            final File warFile = getWarFile(artifactReferences);

            if (warFile != null) {
                // extract file name from temp file
                // TODO

                // retrieve ServiceEndpoint property from exchange headers
                String endpointSuffix =
                    message.getHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), String.class);

                if (endpointSuffix != null) {
                    ManagementBusDeploymentPluginTomcat.LOG.info("Endpoint suffix from header: {}", endpointSuffix);
                } else {
                    ManagementBusDeploymentPluginTomcat.LOG.info("No endpoint suffix defined.");
                    endpointSuffix = "";
                }

                // if placeholder is defined the deployment is done in the topology
                if (endpointSuffix.toString().contains("/PLACEHOLDER_")
                    && endpointSuffix.toString().contains("_PLACEHOLDER/")) {
                    // just return a created endpoint
                    // TODO: concatenate endpoint
                } else {
                    // TODO: deploy WAR file and return endpoint
                }

                warFile.delete();
            } else {
                ManagementBusDeploymentPluginTomcat.LOG.debug("Deployment failed: no referenced WAR-File found");
                message.setHeader(MBHeader.ENDPOINT_URI.toString(), null);
            }
        } else {
            ManagementBusDeploymentPluginTomcat.LOG.debug("Deployment failed: Tomcat is not running or can´t be accessed");
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), null);
        }

        return exchange;
    }

    @Override
    public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {
        // TODO
        return exchange;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedTypes() {
        ManagementBusDeploymentPluginTomcat.LOG.debug("Getting Types: {}.", ManagementBusDeploymentPluginTomcat.TYPES);
        final List<String> types = new ArrayList<>();

        for (final String type : ManagementBusDeploymentPluginTomcat.TYPES.split("[,;]")) {
            types.add(type.trim());
        }
        return types;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilties() {
        ManagementBusDeploymentPluginTomcat.LOG.debug("Getting Plugin-Capabilities: {}.",
                                                      ManagementBusDeploymentPluginTomcat.CAPABILITIES);
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : ManagementBusDeploymentPluginTomcat.CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

    /**
     * Check if the Tomcat which is references as the IA-engine in the container config.ini is
     * running.
     *
     * @return true if Tomcat is running and can be accessed, false otherwise
     */
    private boolean isRunning() {
        ManagementBusDeploymentPluginTomcat.LOG.info("Checking if Tomcat is running on {} and can be accessed...",
                                                     Settings.ENGINE_IA_TOMCAT_URL);

        // URL to get serverinfo from Tomcat.
        final String url = Settings.ENGINE_IA_TOMCAT_URL + "/manager/text/serverinfo";

        // Execute the Tomcat command and get response message back. If no
        // exception occurs, Tomcat is running.
        try {
            final HttpResponse httpResponse = ServiceHandler.httpService.Get(url, Settings.ENGINE_IA_TOMCAT_USERNAME,
                                                                             Settings.ENGINE_IA_TOMCAT_PASSWORD);

            final String response = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");

            ManagementBusDeploymentPluginTomcat.LOG.debug(response);

            if (response.contains("OK - Server info")) {
                return true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if the artifact references contain a WAR-File and return it if so.
     *
     * @param artifactReferences the references to check if a WAR-File is available
     * @return WAR-File if available. Otherwise <tt>null</tt>.
     */
    private File getWarFile(final List<String> artifactReferences) {
        ManagementBusDeploymentPluginTomcat.LOG.info("Searching for a deployable WAR-File...");

        if (artifactReferences != null) {
            for (final String reference : artifactReferences) {

                // check if reference targets a WAR-File
                if (reference.toLowerCase().endsWith(".war")) {
                    ManagementBusDeploymentPluginTomcat.LOG.info("Found WAR-File reference: {}. Trying to retrieve it.",
                                                                 reference);
                    try {
                        // store WAR artifact as temporary file
                        final File tempFile = File.createTempFile("", ".war");
                        tempFile.deleteOnExit();

                        final URL referenceURL = new URL(reference);

                        FileUtils.copyURLToFile(referenceURL, tempFile);

                        return tempFile;
                    }
                    catch (final IOException e) {
                        ManagementBusDeploymentPluginTomcat.LOG.info("Failed retrieving file: {}", e.getMessage());
                    }
                }
            }
        }
        return null;
    }
}
