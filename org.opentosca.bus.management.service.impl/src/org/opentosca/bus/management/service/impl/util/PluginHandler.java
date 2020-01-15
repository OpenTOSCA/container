package org.opentosca.bus.management.service.impl.util;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.script.ManagementBusInvocationPluginScript;
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class which contains methods to handle the deployment/invocation plug-ins and their
 * corresponding types.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart
 */
public class PluginHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PluginHandler.class);

    /**
     * Calls the invocation plug-in that supports the specific invocation-type and redirects invocations
     * on remote OpenTOSCA Containers to the 'remote' plug-in.
     *
     * @param exchange the exchange that has to be passed to the plug-in.
     * @param invocationType the invocation type for the IA/Plan invocation
     * @param deploymentLocation the deployment location of the IA/Plan that is invoked
     *
     * @return the response of the called plug-in.
     */
    public static Exchange callMatchingInvocationPlugin(Exchange exchange, String invocationType,
                                                        final String deploymentLocation) {

        LOG.debug("Searching a matching invocation plug-in for InvocationType {} and deployment location {}",
                  invocationType, deploymentLocation);

        // redirect invocation call to 'remote' plug-in if deployment location is not the
        // local Container
        if (!deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {

            // FIXME: find better solution to avoid forwarding of script calls to the
            // remote Container
            if (!(ServiceHandler.invocationPluginServices.get(invocationType) instanceof ManagementBusInvocationPluginScript)) {

                LOG.debug("Deployment location is remote. Redirecting invocation to remote plug-in.");

                invocationType = Constants.REMOTE_TYPE;
            }
        }

        final IManagementBusInvocationPluginService invocationPlugin =
            ServiceHandler.invocationPluginServices.get(invocationType);

        if (invocationPlugin != null) {
            exchange = invocationPlugin.invoke(exchange);
        } else {
            LOG.warn("No matching plug-in found!");
        }

        return exchange;
    }

    /**
     * Calls the deployment plug-in that supports the specific deployment type and redirects deployments
     * on remote OpenTOSCA Containers to the 'remote' plug-in.
     *
     * @param exchange the exchange that has to be passed to the plug-in.
     * @param deploymentType the deployment type of the IA that shall be deployed
     * @param deploymentLocation the deployment location of the IA
     *
     * @return the response of the called plug-in.
     */
    public static Exchange callMatchingDeploymentPlugin(Exchange exchange, String deploymentType,
                                                        final String deploymentLocation) {

        LOG.debug("Searching a matching deployment plug-in for deployment type {} and deployment location {}",
                  deploymentType, deploymentLocation);

        // redirect deployment call to 'remote' plug-in if deployment location is not the
        // local Container
        if (!deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
            LOG.debug("Deployment location is remote. Redirecting deployment to remote plug-in.");

            deploymentType = Constants.REMOTE_TYPE;
        }

        final IManagementBusDeploymentPluginService deploymentPlugin =
            ServiceHandler.deploymentPluginServices.get(deploymentType);

        if (deploymentPlugin != null) {
            exchange = deploymentPlugin.invokeImplementationArtifactDeployment(exchange);
        } else {
            LOG.warn("No matching plug-in found!");
        }

        return exchange;
    }

    /**
     * Checks if an deployment plug-in is available that supports the specified artifact and returns the
     * deployment type.
     *
     * @param artifactType to check if supported.
     * @return the deployment type or otherwise <tt>null</tt>.
     */
    public static String hasSupportedDeploymentType(final String artifactType) {

        LOG.debug("Searching if a deployment plug-in supports the type {}", artifactType);

        LOG.debug("All supported deployment types: {}", ServiceHandler.deploymentPluginServices.toString());

        // Check if the ArtifactType can be deployed by a plug-in
        if (ServiceHandler.deploymentPluginServices.containsKey(artifactType)) {
            return artifactType;
        }

        return null;
    }

    /**
     * Checks if an invocation plug-in is available that supports the specified artifact and returns the
     * invocation type.
     *
     * @param artifactType to check if supported.
     * @param csarID to get properties to check for InvocationType.
     * @param artifactTemplateID to get properties to check for InvocationTyp.
     * @return the invocation type or otherwise <tt>null</tt>.
     */
    public static String hasSupportedInvocationType(final String artifactType, final CSARID csarID,
                                                    final QName artifactTemplateID) {

        LOG.debug("Searching if a invocation plug-in supports the type {}", artifactType);

        LOG.debug("All supported invocation types: {}", ServiceHandler.invocationPluginServices.toString());

        // First check if a plug-in is registered that supports the ArtifactType.
        if (ServiceHandler.invocationPluginServices.containsKey(artifactType)) {
            return artifactType;
        } else {

            final Document properties =
                ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactTemplateID);

            // Second check if a invocation-type is specified in TOSCA definition
            final String invocationType = getInvocationType(properties);

            if (invocationType != null) {

                if (ServiceHandler.invocationPluginServices.containsKey(invocationType)) {
                    return invocationType;
                }
            }
        }

        return null;
    }

    /**
     * Checks if a InvocationType was specified in the Tosca.xml and returns it if so.
     *
     * @param properties to check for InvocationType.
     * @return InvocationType if specified. Otherwise <tt>null</tt>.
     */
    private static String getInvocationType(final Document properties) {

        // checks if there are specified properties at all.
        if (properties != null) {
            final NodeList list = properties.getFirstChild().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);
                final String localName = propNode.getLocalName();

                // check if the node contains the InvocationType
                if (localName != null && localName.equals("InvocationType")) {
                    return propNode.getTextContent().trim();
                }
            }
        }
        LOG.debug("No InvocationType found!");
        return null;
    }
}
