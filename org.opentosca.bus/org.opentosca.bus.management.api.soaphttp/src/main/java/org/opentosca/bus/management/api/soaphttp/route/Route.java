package org.opentosca.bus.management.api.soaphttp.route;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.api.soaphttp.model.InvokeResponse;
import org.opentosca.bus.management.api.soaphttp.processor.RequestProcessor;
import org.opentosca.bus.management.api.soaphttp.processor.ResponseProcessor;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.next.ContainerEngine;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.endpoints.Endpoint;
import org.opentosca.container.core.plan.ChoreographyHandler;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.springframework.stereotype.Component;

/**
 * Route of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Here the route an incoming invoke-request has to pass is defined. Also the web services to consume and produce a SOAP
 * message are created here. An incoming SOAP message will be unmarshalled and with the request-processor transformed.
 * After that the message will be given the Management Bus for further execution. The response will be transformed,
 * marshalled and send to the recipient. Supported are both synchronous request-response communication and asynchronous
 * communication with callback. MessageID and ReplyTo-address can be passed as parameter of the SOAP body or as WS-A
 * header.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
// named to avoid clashing with other RouteBuilders just called Route across the project
// @Named("management-bus-soaphttp-route")
public class Route extends RouteBuilder {

    public final static String PUBLIC_ENDPOINT = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8081/invoker";
    public final static QName PORT = new QName("http://siserver.org/wsdl", "InvokePort");
    public final static QName PORTTYPE = new QName("http://siserver.org/wsdl", "InvokePortType");

    private final static String ENDPOINT = "http://0.0.0.0:8081/invoker";

    // Checks if invoking a IA
    final Predicate IS_INVOKE_IA = header(CxfConstants.OPERATION_NAME).isEqualTo("invokeIA");

    // Checks if invoking a Plan
    final Predicate IS_INVOKE_PLAN = header(CxfConstants.OPERATION_NAME).isEqualTo("invokePlan");

    // Checks if notifying a partner
    final Predicate IS_NOTIFY_PARTNER = header(CxfConstants.OPERATION_NAME).isEqualTo("notifyPartner");

    // Checks if notifying partners
    final Predicate IS_NOTIFY_PARTNERS = header(CxfConstants.OPERATION_NAME).isEqualTo("notifyPartners");

    private final CsarStorageService csarStorageService;
    private final IManagementBusService managementBusService;
    private final ICoreEndpointService endpointService;
    private final ContainerEngine containerEngine;
    private final ChoreographyHandler choreoHandler;

    @Inject
    public Route(CsarStorageService csarStorageService, IManagementBusService managementBusService,
                 ICoreEndpointService endpointService, ContainerEngine containerEngine,
                 ChoreographyHandler choreoHandler) {
        this.csarStorageService = csarStorageService;
        this.managementBusService = managementBusService;
        this.endpointService = endpointService;
        this.containerEngine = containerEngine;
        this.choreoHandler = choreoHandler;

        storeManagementEndpoint();
    }

    private void storeManagementEndpoint() {
        try {
            final URI uri = new URI(Route.PUBLIC_ENDPOINT);
            final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
            final Endpoint endpoint = new Endpoint(uri, localContainer, localContainer,
                new CsarId("***"), null, new HashMap<>(), Route.PORTTYPE, null, null, null);
            this.endpointService.storeEndpoint(endpoint);
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configure() throws Exception {
        final URL wsdlURL = this.getClass().getClassLoader().getResource("wsdl/invoker.wsdl");

        // CXF Endpoints
        final String INVOKE_ENDPOINT = "cxf:" + ENDPOINT + "?wsdlURL=" + wsdlURL.toString()
            + "&serviceName={http://siserver.org/wsdl}InvokerService&portName=" + Route.PORT.toString()
            + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true&loggingSizeLimit=-1";
        final String CALLBACK_ENDPOINT = "cxf:${header[ReplyTo]}?wsdlURL=" + wsdlURL.toString()
            + "&headerFilterStrategy=#dropAllMessageHeadersStrategy"
            + "&serviceName={http://siserver.org/wsdl}CallbackService&portName={http://siserver.org/wsdl}CallbackPort"
            + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true&loggingSizeLimit=-1";

        // Checks if invoke is sync or async
        final Predicate MESSAGEID = header("MessageID").isNotNull();
        final Predicate REPLYTO = header("ReplyTo").isNotNull();
        final Predicate ASYNC = PredicateBuilder.and(MESSAGEID, REPLYTO);

        final ClassLoader cl = org.opentosca.bus.management.api.soaphttp.model.ObjectFactory.class.getClassLoader();
        final JAXBContext jc = JAXBContext.newInstance("org.opentosca.bus.management.api.soaphttp.model", cl);
        final JaxbDataFormat requestJaxb = new JaxbDataFormat(jc);
        final JaxbDataFormat responseJaxb = new JaxbDataFormat(jc);
        responseJaxb.setPartClass(InvokeResponse.class);
        responseJaxb.setPartNamespace(new QName("http://siserver.org/schema", "invokeResponse"));

        final Processor requestProcessor = new RequestProcessor(this.csarStorageService, this.containerEngine,
            this.managementBusService, this.choreoHandler);
        final Processor responseProcessor = new ResponseProcessor();

        this.from(INVOKE_ENDPOINT).unmarshal(requestJaxb).process(requestProcessor).choice().when(this.IS_INVOKE_IA)
            .bean(this.managementBusService, "invokeIA").when(this.IS_INVOKE_PLAN)
            .bean(this.managementBusService, "invokePlan").when(this.IS_NOTIFY_PARTNER)
            .bean(this.managementBusService, "notifyPartner").when(this.IS_NOTIFY_PARTNERS)
            .bean(this.managementBusService, "notifyPartners").end();

        this.from("direct-vm:" + RequestProcessor.MB_MANAGEMENT_SOAPHTTP_API_ID).process(responseProcessor)
            .marshal(responseJaxb).choice().when(ASYNC).recipientList(this.simple(CALLBACK_ENDPOINT)).end();
    }
}
