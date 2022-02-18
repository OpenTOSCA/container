package org.opentosca.bus.management.invocation.plugin.soaphttp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import com.google.common.collect.Lists;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.soaphttp.route.AsyncRoute;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 * Management Bus-Plug-in for invoking a service with a SOAP message over HTTP. <br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The Plug-in gets needed information (like endpoint of the service or operation to invoke) from the Management Bus and
 * creates a SOAP message out of it. If needed the Plug-in parses the WSDL of the service. The Plug-in supports
 * synchronous request-response communication, asynchronous communication with callbacks and one-way invocation.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class ManagementBusInvocationPluginSoapHttp extends IManagementBusInvocationPluginService {

    private static final Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginSoapHttp.class);

    static final private String[] TYPES = {"SOAP/HTTP"};

    private static final Map<String, Exchange> EXCHANGE_MAP = Collections.synchronizedMap(new HashMap<>());
    private final CamelContext camelContext;
    private final CsarStorageService storage;

    @Inject
    public ManagementBusInvocationPluginSoapHttp(CamelContext camelContext, CsarStorageService storage) {
        this.camelContext = camelContext;
        this.storage = storage;
    }

    /**
     * @return the keys of the map containing stored messageIds and exchange objects.
     */
    public static Set<String> getMessageIDs() {
        return EXCHANGE_MAP.keySet();
    }

    @Override
    public Exchange invoke(Exchange exchange) {
        MessagingPattern messagingPattern = null;

        final Message message = exchange.getIn();

        final Object params = message.getBody();
        final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        final String endpoint = message.getHeader(MBHeader.ENDPOINT_URI.toString(), String.class);

        final Boolean hasOutputParams = message.getHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), Boolean.class);
        final CsarId csarID = new CsarId(message.getHeader(MBHeader.CSARID.toString(), String.class));

        final Map<String, Object> headers = new HashMap<>();
        // Self defined header should be part of the outgoing soap messages.
        for (final MBHeader header : MBHeader.values()) {
            if (message.getHeader(header.name()) != null) {
                headers.put(header.name(), message.getHeader(header.name()));
            }
        }
        headers.put("endpoint", endpoint.replace("?wsdl", ""));

        // if mocking is turned on, we just fake the SOAP call
        if (Boolean.parseBoolean(Settings.OPENTOSCA_BUS_MANAGEMENT_MOCK) && exchange.getMessage().getHeader(MBHeader.PLANID_QNAME.toString()) == null) {
            LOG.info("Mocking following SOAP call:");
            LOG.info("Headers:");
            LOG.info(headers.toString());
            LOG.info("Body:");
            LOG.info(params.toString());
            return respondViaMocking(exchange, this.storage);
        }

        Document document = null;
        final Definition wsdl = pullWsdlDefinitions(endpoint);
        final BindingOperation operation = findOperation(wsdl, operationName);

        if (params instanceof HashMap) {

            if (operation == null) {
                LOG.error("Invoked operation was not exposed on the given endpoint. Aborting invocation!");
                return null;
            }

            final QName messagePayloadType =
                ((javax.wsdl.Part) operation.getOperation().getInput().getMessage().getOrderedParts(null)
                    .get(0)).getElementName();
            headers.put("SOAPEndpoint", endpoint);

            // add the operation header for the cxf endpoint explicitly if invoking an IA
            if (Objects.nonNull(message.getHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(),
                String.class))) {
                headers.put("operationNamespace", wsdl.getTargetNamespace());
                headers.put("operationName", operationName);
            }

            messagingPattern = determineMP(message, operationName, operation, hasOutputParams);
            if (messagingPattern == null) {
                LOG.error("No invokable operation found. Invocation aborted!");
                return null;
            }
            message.setHeader("ParamsMode", "HashMap");
            @SuppressWarnings("unchecked") final HashMap<String, String> paramsMap = (HashMap<String, String>) params;
            // special handling for CALLBACK messages
            if (messagingPattern == MessagingPattern.CALLBACK) {
                String messageId = message.getMessageId();
                if (paramsMap.containsKey("CorrelationID")) {
                    if (paramsMap.get("CorrelationID") != null) {
                        messageId = paramsMap.get("CorrelationID");
                    } else {
                        paramsMap.put("CorrelationID", messageId);
                    }
                    message.setMessageId(messageId);
                }
                LOG.debug("Message ID: {}", messageId);
                if (paramsMap.containsKey("MessageID")) {
                    paramsMap.put("MessageID", messageId);
                } else {
                    headers.put("MessageID", messageId);
                }
                if (paramsMap.containsKey("ReplyTo")) {
                    paramsMap.put("ReplyTo", AsyncRoute.PUBLIC_CALLBACKADDRESS);
                } else {
                    headers.put("ReplyTo", AsyncRoute.PUBLIC_CALLBACKADDRESS);
                }
                if (paramsMap.containsKey("planCallbackAddress_invoker")) {
                    paramsMap.put("planCallbackAddress_invoker",
                        "http://localhost:9763/services/" + csarID.csarName() + "InvokerService/");
                } else {
                    headers.put("planCallbackAddress_invoker",
                        "http://localhost:9763/services/" + csarID.csarName() + "InvokerService/");
                }
            }

            document =
                MBUtils.mapToDoc(messagePayloadType.getNamespaceURI(), messagePayloadType.getLocalPart(), paramsMap);
        }

        if (params instanceof Document) {
            document = (Document) params;
            messagingPattern = determineMP(message, operationName, operation, hasOutputParams);
        }

        if (messagingPattern == null) {
            LOG.error("Can't determine which kind of invocation is needed. Invocation aborted.");
            return null;
        }

        LOG.info("Invoking the web service with headers:\n{}\nand content:\n{}",
            headers.keySet().stream()
                .map(key -> key + "=" + headers.get(key))
                .collect(Collectors.joining(", ", "{", "}")),
            document != null ? MBUtils.docToString(document) : "");

        final ProducerTemplate template = this.camelContext.createProducerTemplate();
        final ConsumerTemplate consumer = this.camelContext.createConsumerTemplate();

        Document response = null;
        LOG.debug("Messaging pattern: {}", messagingPattern);

        switch (messagingPattern) {
            case REQUEST_RESPONSE:
                LOG.debug("Sync invocation.");
                response = template.requestBodyAndHeaders("direct:Sync-WS-Invoke", document, headers, Document.class);
                break;
            case REQUEST_ONLY:
                LOG.debug("Request-only invocation.");
                template.sendBodyAndHeaders("direct:RequestOnly-WS-Invoke", document, headers);
                return null;
            case CALLBACK:
                LOG.debug("Async invocation.");
                final String messageID = message.getMessageId();
                LOG.debug("Storing exchange message with MessageID: {}", messageID);
                EXCHANGE_MAP.put(messageID, exchange);

                template.sendBodyAndHeaders("direct:Async-WS-Invoke", document, headers);
                Exchange ex = null;
                while (response == null) {
                    try {
                        consumer.start();
                        ex = consumer.receive("direct:Async-WS-Callback" + messageID);
                        consumer.stop();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                    final Message mes = ex.getIn();
                    LOG.debug("Got Message with ID: {}", messageID);
                    LOG.debug("Stored MessageIDs: {}", EXCHANGE_MAP.keySet().toString());
                    if (EXCHANGE_MAP.containsKey(messageID)) {
                        LOG.debug("MessageID found");
                        exchange = EXCHANGE_MAP.get(messageID);
                        response = mes.getBody(Document.class);
                        EXCHANGE_MAP.remove(messageID);
                    }
                }
                break;
            default:
                LOG.error("Unhandled messaging pattern \"{}\" in management bus soaphttp invocation plugin!",
                    messagingPattern);
                return null;
        }

        if (exchange.getIn().getHeader("ParamsMode") != null
            && exchange.getIn().getHeader("ParamsMode").equals("HashMap")) {
            LOG.debug("Transforming Document to HashMap...");
            final HashMap<String, String> responseMap = MBUtils.docToMap(response, false);
            exchange.getIn().setBody(responseMap);
        } else {
            exchange.getIn().setBody(response);
        }

        LOG.debug("Returning exchange with MessageID: {}", exchange.getIn().getMessageId());
        LOG.debug("Returning body: {}", exchange.getIn().getBody().toString());

        return exchange;
    }

    private Definition pullWsdlDefinitions(String endpoint) {
        if (!endpoint.endsWith("?wsdl")) {
            endpoint = endpoint + "?wsdl";
        }
        LOG.debug("Parsing WSDL at: {}.", endpoint);
        WSDLFactory wsdlFactory = null;
        try {
            wsdlFactory = WSDLFactory.newInstance();
            final WSDLReader wsdlDefinitionReader = wsdlFactory.newWSDLReader();
            // deactives logging of 'Retrieving documant at...'
            wsdlDefinitionReader.setFeature("javax.wsdl.verbose", false);
            return wsdlDefinitionReader.readWSDL(endpoint);
        } catch (final WSDLException e) {
            LOG.warn("Could not read WSDL definitions from endpoint {} due to WSDLException", endpoint, e);
        }
        return null;
    }

    private BindingOperation findOperation(final Definition wsdl, final String operationName) {
        if (wsdl == null) {
            return null;
        }
        final Map<QName, ?> bindings = wsdl.getBindings();
        for (final Map.Entry<QName, ?> entry : bindings.entrySet()) {
            final Binding binding = wsdl.getBinding(entry.getKey());
            final List<BindingOperation> definedOperations = binding.getBindingOperations();
            for (final BindingOperation operation : definedOperations) {
                if (operation.getName().equalsIgnoreCase(operationName)) {
                    return operation;
                }
            }
        }
        return null;
    }

    /**
     * Determine if the specified operation of the specified wsdl defines output parameter.
     *
     * @return <code>true</code> if operation returns output params. Otherwise <code>false</code>. If
     * operation can't be found <code>null</code> is returned.
     */
    private boolean hasOutputDefined(final BindingOperation operation) {
        // If wsdl is not accessible, try again (max wait 5 min)
        return operation.getBindingOutput() != null;
    }

    /**
     * Determines which kind of invocation is needed for this operation.
     *
     * @return messagingPattern as String.
     */
    private MessagingPattern determineMP(final Message message, final String operationName,
                                         final BindingOperation operation, final Boolean hasOutputParams) {

        // Plan should be invoked
        if (message.getHeader(MBHeader.PLANID_QNAME.toString()) != null) {
            LOG.debug("Invoking a plan with document as input.");
            // Caller already knows if invocation is sync or async.
            if (message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString()) != null) {
                if (!message.getHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), Boolean.class)) {
                    return MessagingPattern.CALLBACK;
                } else {
                    return MessagingPattern.REQUEST_RESPONSE;
                }
            } else if (operationName != null) {
                // Plug-in needs to determine with wsdl.
                if (operationName.equals("receiveNotify")) {
                    LOG.debug("ReceiveNotify is executed. Using Request_Only MP!");
                    return MessagingPattern.REQUEST_ONLY;
                }
                final boolean hasOutputDefinedInWSDL = hasOutputDefined(operation);
                if (hasOutputDefinedInWSDL) {
                    return MessagingPattern.REQUEST_RESPONSE;
                } else {
                    return MessagingPattern.CALLBACK;
                }
            }
            return null;
        } else {
            // Operation of IA should be invoked
            LOG.debug("Invoking an operation of an implementation artifact.");

            final boolean hasOutputDefinedInWSDL = hasOutputDefined(operation);
            if (hasOutputDefinedInWSDL) {
                return MessagingPattern.REQUEST_RESPONSE;
            } else if (hasOutputParams) {
                return MessagingPattern.CALLBACK;
            } else {
                return MessagingPattern.REQUEST_ONLY;
            }
        }
    }

    @Override
    public List<String> getSupportedTypes() {
        LOG.debug("Getting Types: {}.", TYPES);
        return Lists.newArrayList(TYPES);
    }

    private enum MessagingPattern {
        CALLBACK, REQUEST_RESPONSE, REQUEST_ONLY
    }
}
