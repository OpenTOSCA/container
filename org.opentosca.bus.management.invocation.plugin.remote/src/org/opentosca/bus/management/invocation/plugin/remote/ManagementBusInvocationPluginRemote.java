package org.opentosca.bus.management.invocation.plugin.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.opentosca.bus.management.service.impl.collaboration.model.BodyType;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.Doc;
import org.opentosca.bus.management.service.impl.collaboration.model.IAInvocationRequest;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Management Bus-Plug-in for invoking an IA on a remote OpenTOSCA Container. <br>
 * <br>
 *
 * The plug-in gets all needed information for the invocation and forwards it to the remote
 * Container over MQTT. When it gets the response, it copies the result body to the exchange and
 * returns it to the Management Bus.
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder- st100495@stud.uni-stuttgart.de
 *
 */
public class ManagementBusInvocationPluginRemote implements IManagementBusInvocationPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginRemote.class);

    @Override
    public Exchange invoke(final Exchange exchange) {

        ManagementBusInvocationPluginRemote.LOG.debug("Invoking IA on remote OpenTOSCA Container.");
        final Message message = exchange.getIn();
        final Object body = message.getBody();

        // create an unique correlation ID for the request
        final String correlationID = UUID.randomUUID().toString();

        final Map<String, Object> requestHeaders = new HashMap<>();

        // add MB header fields of the incoming message to the outgoing message
        for (final MBHeader header : MBHeader.values()) {
            if (message.getHeader(header.toString()) != null) {
                requestHeaders.put(header.toString(), message.getHeader(header.toString()));
            }
        }

        // create header fields to forward the deployment requests
        requestHeaders.put(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(), Constants.LOCAL_MQTT_BROKER);
        requestHeaders.put(MBHeader.MQTTTOPIC_STRING.toString(), Constants.REQUEST_TOPIC);
        requestHeaders.put(MBHeader.CORRELATIONID_STRING.toString(), correlationID);
        requestHeaders.put(MBHeader.REPLYTOTOPIC_STRING.toString(), Constants.RESPONSE_TOPIC);
        requestHeaders.put(MBHeader.REMOTEOPERATION_STRING.toString(), RemoteOperations.invokeIAOperation);

        // IA invocation request containing the input parameters
        final IAInvocationRequest invocationRequest = parseBodyToInvocationRequest(body);

        // create request message and add the input parameters as body
        final BodyType requestBody = new BodyType(invocationRequest);
        final CollaborationMessage request = new CollaborationMessage(new KeyValueMap(), requestBody);

        ManagementBusInvocationPluginRemote.LOG.debug("Publishing IA invocation request to MQTT broker at {} with topic {} and correlation ID {}",
                                                      Constants.LOCAL_MQTT_BROKER, Constants.REQUEST_TOPIC,
                                                      correlationID);

        // publish the exchange over the camel route
        final Thread thread = new Thread(() -> {

            // By using an extra thread and waiting some time before sending the request, the
            // consumer can be started in time to avoid loosing replies.
            try {
                Thread.sleep(300);
            }
            catch (final InterruptedException e) {
            }

            Activator.producer.sendBodyAndHeaders("direct:SendMQTT", request, requestHeaders);
        });
        thread.start();

        final String callbackEndpoint = "direct:Callback-" + correlationID;
        ManagementBusInvocationPluginRemote.LOG.debug("Waiting for response at endpoint: {}", callbackEndpoint);

        // wait for a response at the created callback
        final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
        final Exchange responseExchange = consumer.receive(callbackEndpoint);

        ManagementBusInvocationPluginRemote.LOG.debug("Received a response for the invocation request!");

        // release resources
        try {
            consumer.stop();
        }
        catch (final Exception e) {
            ManagementBusInvocationPluginRemote.LOG.warn("Unable to stop consumer: {}", e.getMessage());
        }

        // process the response and extract the body
        if (responseExchange.getIn().getBody() instanceof CollaborationMessage) {
            final CollaborationMessage responseMessage = responseExchange.getIn().getBody(CollaborationMessage.class);
            final BodyType responseBody = responseMessage.getBody();

            if (responseBody != null) {
                final IAInvocationRequest invocationResponse = responseBody.getIAInvocationRequest();

                if (invocationResponse != null) {
                    if (invocationResponse.getParams() != null) {
                        ManagementBusInvocationPluginRemote.LOG.debug("Response contains output as HashMap:");

                        final HashMap<String, String> outputParamMap = new HashMap<>();

                        for (final KeyValueType outputParam : invocationResponse.getParams().getKeyValuePair()) {
                            ManagementBusInvocationPluginRemote.LOG.debug("Key: {}, Value: {}", outputParam.getKey(),
                                                                          outputParam.getValue());
                            outputParamMap.put(outputParam.getKey(), outputParam.getValue());
                        }
                        message.setBody(outputParamMap, HashMap.class);
                    } else {
                        if (invocationResponse.getDoc() != null) {
                            ManagementBusInvocationPluginRemote.LOG.debug("Response contains output as Document");

                            try {
                                final DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
                                final DocumentBuilder build = dFact.newDocumentBuilder();
                                final Document document = build.newDocument();

                                final Element element = invocationResponse.getDoc().getAny();

                                document.adoptNode(element);
                                document.appendChild(element);

                                message.setBody(document, Document.class);
                            }
                            catch (final Exception e) {
                                ManagementBusInvocationPluginRemote.LOG.error("Unable to parse Document: {}",
                                                                              e.getMessage());
                            }
                        } else {
                            ManagementBusInvocationPluginRemote.LOG.warn("Response contains no output.");
                            message.setBody(null);
                        }
                    }
                } else {
                    ManagementBusInvocationPluginRemote.LOG.error("Body contains no IAInvocationRequest object with the result.");
                }
            } else {
                ManagementBusInvocationPluginRemote.LOG.error("Collaboration message contains no body.");
            }
        } else {
            ManagementBusInvocationPluginRemote.LOG.error("Message has invalid class: {}",
                                                          responseExchange.getIn().getBody().getClass());
        }

        return exchange;
    }

    @Override
    public List<String> getSupportedTypes() {

        // This plug-in supports only the special type 'remote' which is used to forward invocation
        // requests to other OpenTOSCA Containers.
        final List<String> types = new ArrayList<>();
        types.add(Constants.REMOTE_TYPE);

        return types;
    }

    /**
     * Reads the input parameters of the invocation from the exchange body and adds them to a
     * IAInvocationRequest object.
     *
     * @param body the body of the exchange containing the invocation request
     * @return IAInvocationRequest object with given parameters in the Doc or Params element, if
     *         input parameters are given as Hash Map or as Document.
     */
    private IAInvocationRequest parseBodyToInvocationRequest(final Object body) {

        ManagementBusInvocationPluginRemote.LOG.debug("Parsing input parameters for the invocation...");

        final IAInvocationRequest invocationRequest = new IAInvocationRequest();

        if (body instanceof HashMap) {
            ManagementBusInvocationPluginRemote.LOG.debug("Adding input params from incoming HashMap to the request.");

            @SuppressWarnings("unchecked")
            final HashMap<String, String> paramsMap = (HashMap<String, String>) body;

            final KeyValueMap invocationRequestMap = new KeyValueMap();
            final List<KeyValueType> invocationRequestPairs = invocationRequestMap.getKeyValuePair();

            for (final Entry<String, String> param : paramsMap.entrySet()) {
                invocationRequestPairs.add(new KeyValueType(param.getKey(), param.getValue()));
            }

            invocationRequest.setParams(invocationRequestMap);
        } else {
            if (body instanceof Document) {
                ManagementBusInvocationPluginRemote.LOG.debug("Adding input params from incoming Document to the request.");

                final Document document = (Document) body;
                invocationRequest.setDoc(new Doc(document.getDocumentElement()));
            } else {
                ManagementBusInvocationPluginRemote.LOG.warn("No input parameters defined!");
            }
        }

        return invocationRequest;
    }
}
