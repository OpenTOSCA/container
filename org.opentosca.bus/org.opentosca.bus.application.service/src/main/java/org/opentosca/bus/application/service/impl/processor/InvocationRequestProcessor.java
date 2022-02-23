package org.opentosca.bus.application.service.impl.processor;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.ContainerProxy;
import org.opentosca.bus.application.service.impl.route.InvokeOperationRoute;
import org.opentosca.bus.application.service.impl.servicehandler.ApplicationBusPluginRegistry;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

/**
 * InvocationRequestProcessor of the Application Bus.<br>
 * <br>
 * <p>
 * This processor handles "invokeOperation" requests. Needed information are collected in order to determine the
 * endpoint of the NodeTemplate of which the specified method should be invoked. The effective invocation is done by the
 * Application Bus plugins depending on their supporting invocation types.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component(InvocationRequestProcessor.BEAN_NAME)
@NonNullByDefault
public class InvocationRequestProcessor implements Processor {

    public static final String BEAN_NAME = "appBusInvocationRequestProcessor";
    final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);

    private final ContainerProxy containerProxy;
    private final ApplicationBusPluginRegistry pluginRegistry;
    private final CsarStorageService csarStorageService;

    @Inject
    public InvocationRequestProcessor(ContainerProxy containerProxy, ApplicationBusPluginRegistry pluginRegistry, CsarStorageService csarStorageService) {
        this.containerProxy = containerProxy;
        this.pluginRegistry = pluginRegistry;
        this.csarStorageService = csarStorageService;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        LOG.debug("InvokeOperation request processing started...");
        final Message message = exchange.getIn();

        @Nullable final Integer serviceInstanceID =
            message.getHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(), Integer.class);
        LOG.trace("serviceInstanceID: {}", serviceInstanceID);

        @Nullable
        String nodeTemplateID = message.getHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), String.class);
        LOG.trace("nodeTemplateID: {}", nodeTemplateID);

        @Nullable final Integer nodeInstanceID =
            message.getHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), Integer.class);
        LOG.trace("nodeInstanceID: {}", nodeInstanceID);

        @Nullable final String interfaceName = message.getHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), String.class);
        LOG.trace("interfaceName: {}", interfaceName);

        @Nullable final String operationName = message.getHeader(ApplicationBusConstants.OPERATION_NAME.toString(), String.class);
        LOG.trace("operationName: {}", operationName);

        final NodeTemplateInstance nodeInstance =
            containerProxy.getNodeInstance(serviceInstanceID, nodeInstanceID, nodeTemplateID);
        if (nodeInstance == null) {
            throw new ApplicationBusInternalException("NodeInstance could not be found");
        }

        final QName nodeType = nodeInstance.getTemplateType();
        final ServiceTemplateInstance serviceInstance = nodeInstance.getServiceTemplateInstance();
        final CsarId csarID = serviceInstance.getCsarId();
        final QName serviceTemplateID = new QName(this.csarStorageService.findById(csarID).entryServiceTemplate().getTargetNamespace(), this.csarStorageService.findById(csarID).entryServiceTemplate().getId());

        if (nodeTemplateID == null) {
            nodeTemplateID = nodeInstance.getTemplateId();
        }

        LOG.trace("Matching NodeInstance found: ID: " + nodeInstance.getId()
            + " CSAR-ID: " + csarID + " ServiceTemplateID: " + serviceTemplateID + " NodeTemplateID: "
            + nodeTemplateID + " of type: " + nodeType);

        final Node properties = containerProxy.getPropertiesNode(csarID, nodeType, interfaceName);
        if (properties == null) {
            throw new ApplicationBusInternalException("Property Node was not found. Could not read Application Properties");
        }

        final String relativeHostEndpoint = containerProxy.getRelativeEndpoint(properties);
        final Integer port = containerProxy.getPort(properties);
        final String invocationType = containerProxy.getInvocationType(properties);
        final String className = containerProxy.getClass(properties, interfaceName);
        if (relativeHostEndpoint == null || port == null || invocationType == null || className == null) {
            throw new ApplicationBusInternalException("Could not gather all necessary information from Application Properties");
        }

        final String hostedOnNodeTemplateID = containerProxy.getHostedOnNodeTemplateWithSpecifiedIPProperty(csarID, serviceTemplateID, nodeTemplateID);
        if (hostedOnNodeTemplateID == null) {
            throw new ApplicationBusInternalException("Could not find hosting NodeTemplate");
        }

        // get the Namespace from the serviceTemplate
        final URL hostedOnNodeURL = containerProxy.getIpFromInstanceDataProperties(serviceInstance.getId(), hostedOnNodeTemplateID);
        if (hostedOnNodeURL == null) {
            throw new ApplicationBusInternalException("Could not find node URL in instanceDataProperties");
        }

        final URL endpoint;
        LOG.debug("Generating endpoint for Node: {}", nodeTemplateID);
        try {
            endpoint = new URL(hostedOnNodeURL.getProtocol(), hostedOnNodeURL.getAuthority(), port, relativeHostEndpoint);
            LOG.debug("Generated endpoint: " + endpoint);
        } catch (final MalformedURLException e) {
            LOG.error("Generating endpoint for Node: {} failed!", nodeTemplateID);
            e.printStackTrace();
            throw new ApplicationBusInternalException("Generating endpoint for Node " + nodeTemplateID + " failed!", e);
        }

        message.setHeader(ApplicationBusConstants.CLASS_NAME.toString(), className);
        message.setHeader(ApplicationBusConstants.INVOCATION_ENDPOINT_URL.toString(), endpoint.toString());
        LOG.debug("Searching an Application Bus Plugin for InvocationType: {}", invocationType);

        // set ID of the matching Application Bus Plugin bundle. Needed for routing.
        final String appBusPluginEndpoint = pluginRegistry.getApplicationBusPluginBundleID(invocationType);
        if (appBusPluginEndpoint != null) {
            LOG.debug("Application Bus Plugin with matching InvocationType: {} found. Endpoint: {}", invocationType, appBusPluginEndpoint);
            exchange.getIn().setHeader(InvokeOperationRoute.APPLICATION_BUS_PLUGIN_ENDPOINT_HEADER, appBusPluginEndpoint);
        }
    }
}
