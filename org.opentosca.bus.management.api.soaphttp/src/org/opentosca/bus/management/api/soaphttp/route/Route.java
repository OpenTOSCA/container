package org.opentosca.bus.management.api.soaphttp.route;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.api.soaphttp.Activator;
import org.opentosca.bus.management.api.soaphttp.processor.RequestProcessor;
import org.opentosca.bus.management.api.soaphttp.processor.ResponseProcessor;
import org.opentosca.container.core.common.Settings;

/**
 * Route of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * Here the route an incoming invoke-request has to pass is defined. Also the web services to
 * consume and produce a SOAP message are created here. An incoming SOAP message will be
 * unmarshalled and with the request-processor transformed. After that the message will be given the
 * Management Bus for further execution. The response will be transformed, marshalled and send to
 * the recipient. Supported are both synchronous request-response communication and asynchronous
 * communication with callback. MessageID and ReplyTo-address can be passed as parameter of the SOAP
 * body or as WS-A header.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Route extends RouteBuilder {


    public final static String PUBLIC_ENDPOINT = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8081/invoker";
    private final static String ENDPOINT = "http://0.0.0.0:8081/invoker";
    public final static QName PORT = new QName("http://siserver.org/wsdl", "InvokePort");
    public final static QName PORTTYPE = new QName("http://siserver.org/wsdl", "InvokePortType");


    @Override
    public void configure() throws Exception {

        final URL wsdlURL = this.getClass().getClassLoader().getResource("META-INF/wsdl/invoker.wsdl");

        // CXF Endpoints
        final String INVOKE_ENDPOINT = "cxf:" + ENDPOINT + "?wsdlURL=" + wsdlURL.toString()
            + "&serviceName={http://siserver.org/wsdl}InvokerService&portName=" + Route.PORT.toString()
            + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true";
        final String CALLBACK_ENDPOINT = "cxf:${header[ReplyTo]}?wsdlURL=" + wsdlURL.toString()
            + "&serviceName={http://siserver.org/wsdl}CallbackService&portName={http://siserver.org/wsdl}CallbackPort&dataFormat=PAYLOAD&loggingFeatureEnabled=true&headerFilterStrategy=#"
            + CxfHeaderFilterStrategy.class.getName();

        // Management Bus Endpoints
        final String MANAGEMENT_BUS_IA =
            "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA";
        final String MANAGEMENT_BUS_PLAN =
            "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokePlan";
        final String MANAGEMENT_BUS_NOTIFY_PARTNER =
            "bean:org.opentosca.bus.management.service.IManagementBusService?method=notifyPartner";
        final String MANAGEMENT_BUS_NOTIFY_PARTNERS =
            "bean:org.opentosca.bus.management.service.IManagementBusService?method=notifyPartners";

        // Check required operation
        final Predicate INVOKE_IA = header(CxfConstants.OPERATION_NAME).isEqualTo("invokeIA");
        final Predicate INVOKE_PLAN = header(CxfConstants.OPERATION_NAME).isEqualTo("invokePlan");
        final Predicate NOTIFY_PARTNER = header(CxfConstants.OPERATION_NAME).isEqualTo("notifyPartner");
        final Predicate NOTIFY_PARTNERS = header(CxfConstants.OPERATION_NAME).isEqualTo("notifyPartners");
        final Predicate RECEIVE_NOTIFY_FROM_BUS = header(CxfConstants.OPERATION_NAME).isEqualTo("receiveNotifyFromBus");

        // Checks if invoke is sync or async
        final Predicate MESSAGEID = header("MessageID").isNotNull();
        final Predicate REPLYTO = header("ReplyTo").isNotNull();
        final Predicate ASYNC = PredicateBuilder.and(MESSAGEID, REPLYTO);

        final ClassLoader cl = org.opentosca.bus.management.api.soaphttp.model.ObjectFactory.class.getClassLoader();
        final JAXBContext jc = JAXBContext.newInstance("org.opentosca.bus.management.api.soaphttp.model", cl);
        final JaxbDataFormat requestJaxb = new JaxbDataFormat(jc);
        final JaxbDataFormat responseJaxb = new JaxbDataFormat(jc);
        responseJaxb.setPartClass("org.opentosca.bus.management.api.soaphttp.model.InvokeResponse");
        responseJaxb.setPartNamespace(new QName("http://siserver.org/schema", "invokeResponse"));

        final Processor requestProcessor = new RequestProcessor();
        final Processor responseProcessor = new ResponseProcessor();

        this.from(INVOKE_ENDPOINT).unmarshal(requestJaxb).process(requestProcessor).choice().when(INVOKE_IA)
            .to(MANAGEMENT_BUS_IA).when(INVOKE_PLAN).to(MANAGEMENT_BUS_PLAN).when(NOTIFY_PARTNER)
            .to(MANAGEMENT_BUS_NOTIFY_PARTNER).when(NOTIFY_PARTNERS).to(MANAGEMENT_BUS_NOTIFY_PARTNERS)
            .when(RECEIVE_NOTIFY_FROM_BUS).to(MANAGEMENT_BUS_PLAN).end();

        this.from("direct-vm:" + Activator.apiID).process(responseProcessor).marshal(responseJaxb).choice().when(ASYNC)
            .recipientList(this.simple(CALLBACK_ENDPOINT)).end();
    }
}
