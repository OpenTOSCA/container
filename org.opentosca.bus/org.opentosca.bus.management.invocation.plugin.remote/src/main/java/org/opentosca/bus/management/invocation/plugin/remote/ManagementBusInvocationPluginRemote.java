package org.opentosca.bus.management.invocation.plugin.remote;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.bus.management.service.impl.collaboration.RequestSender;
import org.opentosca.bus.management.service.impl.collaboration.model.BodyType;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.Doc;
import org.opentosca.bus.management.service.impl.collaboration.model.IAInvocationRequest;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Management Bus-Plug-in for invoking an IA on a remote OpenTOSCA Container. <br>
 * <br>
 * <p>
 * The plug-in gets all needed information for the invocation and forwards it to the remote Container over MQTT. When it
 * gets the response, it copies the result body to the exchange and returns it to the Management Bus.
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
@Service
public class ManagementBusInvocationPluginRemote extends IManagementBusInvocationPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginRemote.class);

    private final RequestSender requestSender;

    @Inject
    public ManagementBusInvocationPluginRemote(RequestSender requestSender) {
        this.requestSender = requestSender;
    }

    @Override
    public Exchange invoke(final Exchange exchange) {

        LOG.debug("Invoking IA on remote OpenTOSCA Container.");
        final Message message = exchange.getIn();
        final Object body = message.getBody();

        // IA invocation request containing the input parameters
        final IAInvocationRequest invocationRequest = parseBodyToInvocationRequest(body);

        // create request message and add the input parameters as body
        final BodyType requestBody = new BodyType(invocationRequest);
        final CollaborationMessage request = new CollaborationMessage(new KeyValueMap(), requestBody);

        // perform remote IA operation
        final Exchange responseExchange = requestSender.sendRequestToRemoteContainer(message, RemoteOperations.INVOKE_IA_OPERATION, request, 0);

        LOG.debug("Received a response for the invocation request!");

        if (!(responseExchange.getIn().getBody() instanceof CollaborationMessage)) {
            LOG.error("Received message has invalid class: {}", responseExchange.getIn().getBody().getClass());
            return exchange;
        }

        // extract the body and process the contained response
        final CollaborationMessage responseMessage = responseExchange.getIn().getBody(CollaborationMessage.class);
        final BodyType responseBody = responseMessage.getBody();

        if (Objects.isNull(responseBody)) {
            LOG.error("Collaboration message contains no body.");
            return exchange;
        }

        final IAInvocationRequest invocationResponse = responseBody.getIAInvocationRequest();

        if (Objects.isNull(invocationResponse)) {
            LOG.error("Body contains no IAInvocationRequest object with the result.");
            return exchange;
        }

        // process output of the response
        if (invocationResponse.getParams() != null) {
            LOG.debug("Response contains output as HashMap:");

            final HashMap<String, String> outputParamMap = new HashMap<>();

            for (final KeyValueType outputParam : invocationResponse.getParams().getKeyValuePair()) {
                LOG.debug("Key: {}, Value: {}", outputParam.getKey(), outputParam.getValue());
                outputParamMap.put(outputParam.getKey(), outputParam.getValue());
            }
            message.setBody(outputParamMap, HashMap.class);
        } else {
            if (invocationResponse.getDoc() != null) {
                LOG.debug("Response contains output as Document");

                try {
                    final DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
                    final DocumentBuilder build = dFact.newDocumentBuilder();
                    final Document document = build.newDocument();

                    final Element element = invocationResponse.getDoc().getAny();

                    document.adoptNode(element);
                    document.appendChild(element);

                    message.setBody(document, Document.class);
                } catch (final Exception e) {
                    LOG.error("Unable to parse Document: {}", e.getMessage());
                }
            } else {
                LOG.warn("Response contains no output.");
                message.setBody(null);
            }
        }

        return exchange;
    }

    @Override
    public List<String> getSupportedTypes() {
        // This plug-in supports only the special type 'remote' which is used to forward invocation
        // requests to other OpenTOSCA Containers.
        return Collections.singletonList(Constants.REMOTE_TYPE);
    }

    /**
     * Reads the input parameters of the invocation from the exchange body and adds them to a IAInvocationRequest
     * object.
     *
     * @param body the body of the exchange containing the invocation request
     * @return IAInvocationRequest object with given parameters in the Doc or Params element, if input parameters are
     * given as Hash Map or as Document.
     */
    private IAInvocationRequest parseBodyToInvocationRequest(final Object body) {

        LOG.debug("Parsing input parameters for the invocation...");

        final IAInvocationRequest invocationRequest = new IAInvocationRequest();

        if (body instanceof HashMap) {
            LOG.debug("Adding input params from incoming HashMap to the request.");

            @SuppressWarnings("unchecked") final HashMap<String, String> paramsMap = (HashMap<String, String>) body;

            final KeyValueMap invocationRequestMap = new KeyValueMap();
            final List<KeyValueType> invocationRequestPairs = invocationRequestMap.getKeyValuePair();

            for (final Entry<String, String> param : paramsMap.entrySet()) {
                invocationRequestPairs.add(new KeyValueType(param.getKey(), param.getValue()));
            }

            invocationRequest.setParams(invocationRequestMap);
        } else {
            if (body instanceof Document) {
                LOG.debug("Adding input params from incoming Document to the request.");

                final Document document = (Document) body;
                invocationRequest.setDoc(new Doc(document.getDocumentElement()));
            } else {
                LOG.warn("No input parameters defined!");
            }
        }

        return invocationRequest;
    }
}
