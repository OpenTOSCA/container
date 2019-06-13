package org.opentosca.bus.management.api.soaphttp.route;

import java.net.URL;
import java.util.logging.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.management.api.soaphttp.processor.RequestProcessor;
import org.opentosca.bus.management.api.soaphttp.processor.ResponseProcessor;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Route of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Here the route an incoming invoke-request has to pass is defined. Also the web services to
 * consume and produce a SOAP message are created here. An incoming SOAP message will be
 * unmarshalled and with the request-processor transformed. After that the message will be given the
 * Management Bus for further execution. The response will be transformed, marshalled and send to
 * the recipient. Supported are both synchronous request-response communication and asynchronous
 * communication with callback. MessageID and ReplyTo-address can be passed as parameter of the SOAP
 * body or as WS-A header.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
@Named("management-bus-soaphttp-route")
public class Route extends RouteBuilder {

  public final static String PUBLIC_ENDPOINT = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8081/invoker";
  public final static QName PORT = new QName("http://siserver.org/wsdl", "InvokePort");
  public final static QName PORTTYPE = new QName("http://siserver.org/wsdl", "InvokePortType");

  private final static String ENDPOINT = "http://0.0.0.0:8081/invoker";

  // Checks if invoking a IA
  final Predicate IS_INVOKE_IA = header(CxfConstants.OPERATION_NAME).isEqualTo("invokeIA");

  // Checks if invoking a Plan
  final Predicate IS_INVOKE_PLAN = header(CxfConstants.OPERATION_NAME).isEqualTo("invokePlan");

  private final IToscaEngineService toscaEngineService;
  private final IManagementBusService managementBusService;

  @Inject
  public Route(IToscaEngineService toscaEngineService, IManagementBusService managementBusService){
    this.toscaEngineService = toscaEngineService;
    this.managementBusService = managementBusService;
  }

  @Override
  public void configure() throws Exception {
    final URL wsdlURL = this.getClass().getClassLoader().getResource("wsdl/invoker.wsdl");

    // CXF Endpoints
    final String INVOKE_ENDPOINT = "cxf:" + ENDPOINT + "?wsdlURL=" + wsdlURL.toString()
      + "&serviceName={http://siserver.org/wsdl}InvokerService&portName=" + Route.PORT.toString()
      + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true";
    final String CALLBACK_ENDPOINT = "cxf:${header[ReplyTo]}?wsdlURL=" + wsdlURL.toString()
      + "&serviceName={http://siserver.org/wsdl}InvokerService&portName={http://siserver.org/wsdl}CallbackPort&dataFormat=PAYLOAD&loggingFeatureEnabled=true&headerFilterStrategy=#"
      + CxfHeaderFilterStrategy.class.getName();

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

    final Processor requestProcessor = new RequestProcessor(toscaEngineService);
    final Processor responseProcessor = new ResponseProcessor();

    this.from(INVOKE_ENDPOINT)
      .unmarshal(requestJaxb)
      .process(requestProcessor)
      .choice().when(IS_INVOKE_IA)
        .bean(managementBusService, "invokeIA")
      .when(IS_INVOKE_PLAN)
        .bean(managementBusService, "invokePlan")
      .end();

    this.from("direct-vm:" + RequestProcessor.API_ID).process(responseProcessor).marshal(responseJaxb).choice().when(ASYNC)
      .recipientList(this.simple(CALLBACK_ENDPOINT)).end();
  }
}
