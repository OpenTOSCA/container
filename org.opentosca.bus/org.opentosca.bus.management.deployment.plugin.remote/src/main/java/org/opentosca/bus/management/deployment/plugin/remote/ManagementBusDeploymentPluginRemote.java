package org.opentosca.bus.management.deployment.plugin.remote;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.bus.management.service.impl.collaboration.RequestSender;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Management Bus-Plug-in for the deployment of IAs on a remote OpenTOSCA Container.<br>
 * <br>
 * <p>
 * This Plug-in is able to deploy and undeploy all kind of IAs which are supported by one of the
 * other available deployment plug-ins on a remote OpenTOSCA Container. It gets a camel exchange
 * object from the Management Bus which contains all information that is needed for the
 * deployment/undeployment. Afterwards it forwards the information via MQTT to the remote Container
 * and waits for a response. When the response arrives the result of the deployment/undeployment is
 * extracted, added to the incoming exchange and passed back to the caller.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
@Component
public class ManagementBusDeploymentPluginRemote implements IManagementBusDeploymentPluginService {

  static final private Logger LOG = LoggerFactory.getLogger(ManagementBusDeploymentPluginRemote.class);

  private final RequestSender requestSender;

  @Inject
  public ManagementBusDeploymentPluginRemote(RequestSender requestSender) {
    this.requestSender = requestSender;
  }

  @Override
  public Exchange invokeImplementationArtifactDeployment(final Exchange exchange) {
    LOG.debug("Trying to deploy IA on remote OpenTOSCA Container.");
    final Message message = exchange.getIn();

    // create empty request message (only headers needed)
    final CollaborationMessage requestBody = new CollaborationMessage(new KeyValueMap(), null);
    // perform remote deployment
    final Exchange response = requestSender.sendRequestToRemoteContainer(message, RemoteOperations.INVOKE_IA_DEPLOYMENT, requestBody, 0);

    // extract the endpoint URI from the response
    final URI endpointURI = response.getIn().getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);
    LOG.debug("Result of remote deployment: Endpoint URI: {}", endpointURI);

    // add the header to the incoming exchange and return result
    message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);
    return exchange;
  }

  @Override
  public Exchange invokeImplementationArtifactUndeployment(final Exchange exchange) {
    LOG.debug("Trying to undeploy IA on remote OpenTOSCA Container.");
    final Message message = exchange.getIn();

    // create empty request message (only headers needed)
    final CollaborationMessage requestBody = new CollaborationMessage(new KeyValueMap(), null);
    // perform remote undeployment
    final Exchange response = requestSender.sendRequestToRemoteContainer(message, RemoteOperations.INVOKE_IA_UNDEPLOYMENT, requestBody,
        0);

    // extract the undeployment state from the response
    final boolean state = response.getIn().getHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), boolean.class);
    LOG.debug("Result of remote undeployment: Success: {}", state);

    // add the header to the incoming exchange and return result
    message.setHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), state);
    return exchange;
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public List<String> getSupportedTypes() {
    // This plug-in supports only the special type 'remote' which is used to forward deployment
    // requests to other OpenTOSCA Containers.
    return Collections.singletonList(Constants.REMOTE_TYPE);
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public List<String> getCapabilties() {
    // This plug-in is intended to move deployment requests from one OpenTOSCA Container to
    // another one. At the destination OpenTOSCA Container the deployment is done by one of the
    // other available deployment plug-ins. Therefore, it has to be checked if this other
    // deployment plug-in provides all needed capabilities before moving the request to the
    // other Container. So, as this plug-in is only a redirection it does not provide any
    // capabilities.
    return new ArrayList<>();
  }
}
