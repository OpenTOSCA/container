package org.opentosca.bus.application.service.impl.processor;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.ContainerProxy;
import org.opentosca.bus.application.service.impl.route.InvokeOperationRoute;
import org.opentosca.bus.application.service.impl.servicehandler.ApplicationBusPluginServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * InvocationRequestProcessor of the Application Bus.<br>
 * <br>
 *
 * This processor handles "invokeOperation" requests. Needed information are collected in order to
 * determine the endpoint of the NodeTemplate of which the specified method should be invoked. The
 * effective invocation is done by the Application Bus plugins depending on their supporting
 * invocation types.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class InvocationRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);


    @Override
    public void process(final Exchange exchange) throws Exception {

        InvocationRequestProcessor.LOG.info("InvokeOperation request processing started...");

        final Message message = exchange.getIn();

        final Integer serviceInstanceID = message.getHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(),
            Integer.class);
        InvocationRequestProcessor.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

        String nodeTemplateID = message.getHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), String.class);
        InvocationRequestProcessor.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

        final Integer nodeInstanceID = message.getHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(),
            Integer.class);
        InvocationRequestProcessor.LOG.debug("nodeInstanceID: {}", nodeInstanceID);

        final String interfaceName = message.getHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), String.class);
        InvocationRequestProcessor.LOG.debug("interfaceName: {}", interfaceName);

        final String operationName = message.getHeader(ApplicationBusConstants.OPERATION_NAME.toString(), String.class);
        InvocationRequestProcessor.LOG.debug("operationName: {}", operationName);

        String invocationType = null;
        String className = null;
        URL endpoint = null;

        final NodeInstance nodeInstance = ContainerProxy.getNodeInstance(serviceInstanceID, nodeInstanceID,
            nodeTemplateID);

        if (nodeInstance != null) {

            final QName nodeType = nodeInstance.getNodeType();
            final ServiceInstance serviceInstance = nodeInstance.getServiceInstance();
            final CSARID csarID = serviceInstance.getCSAR_ID();
            final QName serviceTemplateID = serviceInstance.getServiceTemplateID();

            if (nodeTemplateID == null) {
                nodeTemplateID = nodeInstance.getNodeTemplateID().getLocalPart();
            }

            InvocationRequestProcessor.LOG.debug("Matching NodeInstance found: ID: " + nodeInstance.getNodeInstanceID()
                + " CSAR-ID: " + csarID + " ServiceTemplateID: " + serviceTemplateID + " NodeTemplateID: "
                + nodeTemplateID + " of type: " + nodeType);

            final Node properties = ContainerProxy.getPropertiesNode(csarID, nodeType, interfaceName);

            if (properties != null) {

                final String relativeHostEndpoint = ContainerProxy.getRelativeEndpoint(properties);
                final Integer port = ContainerProxy.getPort(properties);
                invocationType = ContainerProxy.getInvocationType(properties);
                className = ContainerProxy.getClass(properties, interfaceName);

                if (relativeHostEndpoint != null && port != null && invocationType != null && className != null) {

                    final String hostedOnNodeTemplateID = ContainerProxy.getHostedOnNodeTemplateWithSpecifiedIPProperty(
                        csarID, serviceTemplateID, nodeTemplateID);

                    if (hostedOnNodeTemplateID != null) {

                        // get the Namespace from the
                        // serviceTemplate
                        final QName hostedOnNodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(),
                            hostedOnNodeTemplateID);

                        final URL hostedOnNodeURL = ContainerProxy.getIpFromInstanceDataProperties(
                            serviceInstance.getServiceInstanceID(), hostedOnNodeTemplateQName);

                        if (hostedOnNodeURL != null) {

                            InvocationRequestProcessor.LOG.debug("Generating endpoint for Node: {}", nodeTemplateID);

                            try {
                                endpoint = new URL(hostedOnNodeURL.getProtocol(), hostedOnNodeURL.getAuthority(), port,
                                    relativeHostEndpoint);
                                InvocationRequestProcessor.LOG.debug("Generated endpoint: " + endpoint);

                            } catch (final MalformedURLException e) {
                                InvocationRequestProcessor.LOG.error("Generating endpoint for Node: {} failed!",
                                    nodeTemplateID);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (endpoint != null) {

            message.setHeader(ApplicationBusConstants.CLASS_NAME.toString(), className);

            message.setHeader(ApplicationBusConstants.INVOCATION_ENDPOINT_URL.toString(), endpoint.toString());

            InvocationRequestProcessor.LOG.debug("Searching an Application Bus Plugin for InvocationType: {}",
                invocationType);
            // set ID of the matching Application Bus Plugin bundle. Needed for
            // routing.
            final String appBusPluginEndpoint = ApplicationBusPluginServiceHandler.getApplicationBusPluginBundleID(
                invocationType);

            if (appBusPluginEndpoint != null) {

                InvocationRequestProcessor.LOG.debug(
                    "Application Bus Plugin with matching InvocationType: {} found. Endpoint: {}", invocationType,
                    appBusPluginEndpoint);
                exchange.getIn().setHeader(InvokeOperationRoute.APPLICATION_BUS_PLUGIN_ENDPOINT_HEADER,
                    appBusPluginEndpoint);

            }

        } else {

            throw new ApplicationBusInternalException("Couldn't gather all needed information.");
        }

    }

}
