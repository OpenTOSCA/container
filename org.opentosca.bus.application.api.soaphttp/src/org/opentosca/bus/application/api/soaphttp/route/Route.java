package org.opentosca.bus.application.api.soaphttp.route;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.application.api.soaphttp.processor.RequestProcessor;
import org.opentosca.bus.application.api.soaphttp.processor.ResponseProcessor;
import org.opentosca.bus.application.api.soaphttp.servicehandler.ApplicationBusServiceHandler;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.settings.Settings;

/**
 * Route of the Application Bus-SOAP/HTTP-API.<br>
 * <br>
 *
 * The endpoint of the SOAP/HTTP-API is created here. Incoming requests will be
 * un/marshalled, routed to processors or the application bus in order to handle
 * the requests.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Route extends RouteBuilder {
	
	private final static String ENDPOINT = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8084/appBus";
	private final static QName PORT = new QName("http://opentosca.org/appinvoker/", "AppInvokerSoapWebServicePort");
	
	
	@Override
	public void configure() throws Exception {
		
		URL wsdlURL = this.getClass().getClassLoader().getResource("META-INF/wsdl/SoapAPI.wsdl");
		
		// CXF Endpoint
		final String SOAP_ENDPOINT = "cxf:" + Route.ENDPOINT + "?wsdlURL=" + wsdlURL.toString() + "&serviceName={http://opentosca.org/appinvoker/}AppInvokerSoapWebServiceService&portName=" + Route.PORT.toString() + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true";
		
		ValueBuilder APP_BUS_ENDPOINT = new ValueBuilder(this.method(ApplicationBusServiceHandler.class, "getApplicationBusRoutingEndpoint"));
		Predicate APP_BUS_ENDPOINT_EXISTS = PredicateBuilder.isNotNull(APP_BUS_ENDPOINT);
		
		ClassLoader cl = org.opentosca.bus.application.api.soaphttp.model.ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("org.opentosca.bus.application.api.soaphttp.model", cl);
		JaxbDataFormat jaxb = new JaxbDataFormat(jc);
		
		Processor requestProcessor = new RequestProcessor();
		Processor responseProcessor = new ResponseProcessor();
		
		this.from(SOAP_ENDPOINT).unmarshal(jaxb).process(requestProcessor).choice().when(APP_BUS_ENDPOINT_EXISTS).recipientList(APP_BUS_ENDPOINT).to("direct:handleResponse").endChoice().otherwise().to("direct:handleException");
		
		// handle exception if Application Bus is not running or wasn't binded
		this.from("direct:handleException").throwException(new ApplicationBusInternalException("It seems like the Application Bus is not running.")).to("direct:handleResponse");
		
		// handle response
		this.from("direct:handleResponse").process(responseProcessor).marshal(jaxb);
		
	}
	
}