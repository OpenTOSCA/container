package org.opentosca.siengine.api.soaphttp.route;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.siengine.api.soaphttp.Activator;
import org.opentosca.siengine.api.soaphttp.processor.RequestProcessor;
import org.opentosca.siengine.api.soaphttp.processor.ResponseProcessor;

/**
 * Route of the SIEngine-SOAP/HTTP-API.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Here the route an incoming invoke-request has to pass is defined. Also the
 * web services to consume and produce a SOAP message are created here. An
 * incoming SOAP message will be unmarshalled and with the request-processor
 * transformed. After that the message will be given the SIEngine for further
 * execution. The response will be transformed, marshalled and send to the
 * recipient. Supported are both synchronous request-response communication and
 * asynchronous communication with callback. MessageID and ReplyTo-address can
 * be passed as parameter of the SOAP body or as WS-A header.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class Route extends RouteBuilder {
	
	public final static String ENDPOINT = "http://0.0.0.0:8081/invoker";
	public final static QName PORT = new QName("http://siserver.org/wsdl", "InvokePort");
	public final static QName PORTTYPE = new QName("http://siserver.org/wsdl", "InvokePortType");
	
	
	@Override
	public void configure() throws Exception {
		
		URL wsdlURL = this.getClass().getClassLoader().getResource("META-INF/wsdl/invoker.wsdl");
		
		// CXF Endpoints
		final String INVOKE_ENDPOINT = "cxf:" + Route.ENDPOINT + "?wsdlURL=" + wsdlURL.toString() + "&serviceName={http://siserver.org/wsdl}InvokerService&portName=" + Route.PORT.toString() + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		final String CALLBACK_ENDPOINT = "cxf:${header[ReplyTo]}?wsdlURL=" + wsdlURL.toString() + "&serviceName={http://siserver.org/wsdl}InvokerService&portName={http://siserver.org/wsdl}CallbackPort&dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		
		// SI-Engine Endpoints
		final String SI_ENGINE_IA = "bean:org.opentosca.siengine.service.ISIEngineService?method=invokeIA";
		final String SI_ENGINE_PLAN = "bean:org.opentosca.siengine.service.ISIEngineService?method=invokePlan";
		
		// Checks if invoking a IA
		final Predicate INVOKE_IA = this.header(CxfConstants.OPERATION_NAME).isEqualTo("invokeIA");
		
		// Checks if invoking a Plan
		final Predicate INVOKE_PLAN = this.header(CxfConstants.OPERATION_NAME).isEqualTo("invokePlan");
		
		// Checks if invoke is sync or async
		final Predicate MESSAGEID = this.header("MessageID").isNotNull();
		final Predicate REPLYTO = this.header("ReplyTo").isNotNull();
		final Predicate ASYNC = PredicateBuilder.and(MESSAGEID, REPLYTO);
		
		ClassLoader cl = org.opentosca.siengine.api.soaphttp.model.ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("org.opentosca.siengine.api.soaphttp.model", cl);
		JaxbDataFormat requestJaxb = new JaxbDataFormat(jc);
		JaxbDataFormat responseJaxb = new JaxbDataFormat(jc);
		responseJaxb.setPartClass("org.opentosca.siengine.api.soaphttp.model.InvokeResponse");
		responseJaxb.setPartNamespace(new QName("http://siserver.org/schema", "invokeResponse"));
		
		Processor requestProcessor = new RequestProcessor();
		Processor responseProcessor = new ResponseProcessor();
		
		this.from(INVOKE_ENDPOINT).unmarshal(requestJaxb).process(requestProcessor).choice().when(INVOKE_IA).to(SI_ENGINE_IA).when(INVOKE_PLAN).to(SI_ENGINE_PLAN).end();
		this.from("direct-vm:" + Activator.apiID).process(responseProcessor).marshal(responseJaxb).to("stream:out").choice().when(ASYNC).recipientList(this.simple(CALLBACK_ENDPOINT)).end();
	}
}
