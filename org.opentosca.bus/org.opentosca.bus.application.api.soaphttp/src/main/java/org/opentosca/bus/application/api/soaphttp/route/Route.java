package org.opentosca.bus.application.api.soaphttp.route;

import java.net.URL;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.opentosca.bus.application.api.soaphttp.processor.RequestProcessor;
import org.opentosca.bus.application.api.soaphttp.processor.ResponseProcessor;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.springframework.stereotype.Component;

/**
 * Route of the Application Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * The endpoint of the SOAP/HTTP-API is created here. Incoming requests will be un/marshalled, routed to processors or
 * the application bus in order to handle the requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
@Named("application-bus-soaphttp-route")
public class Route extends RouteBuilder {

    private final static String ENDPOINT = "http://0.0.0.0:8082/appBus";

    private final static QName PORT = new QName("http://opentosca.org/appinvoker/", "AppInvokerSoapWebServicePort");

    @Override
    public void configure() throws Exception {

        final URL wsdlURL = this.getClass().getClassLoader().getResource("wsdl/SoapAPI.wsdl");

        // CXF Endpoint
        final String SOAP_ENDPOINT = "cxf:" + ENDPOINT + "?wsdlURL=" + wsdlURL.toString()
            + "&serviceName={http://opentosca.org/appinvoker/}AppInvokerSoapWebServiceService&portName="
            + PORT.toString() + "&dataFormat=PAYLOAD&loggingFeatureEnabled=true";

        final ClassLoader cl = org.opentosca.bus.application.api.soaphttp.model.ObjectFactory.class.getClassLoader();
        final JAXBContext jc = JAXBContext.newInstance("org.opentosca.bus.application.api.soaphttp.model", cl);
        final JaxbDataFormat jaxb = new JaxbDataFormat(jc);

        final Processor requestProcessor = new RequestProcessor();
        final Processor responseProcessor = new ResponseProcessor();

        from(SOAP_ENDPOINT).unmarshal(jaxb).process(requestProcessor)
            .to("direct-vm:org.opentosca.bus.application.service").to("direct:handleResponse");

        // handle exception if Application Bus is not running or wasn't bound
        from("direct:handleException").throwException(new ApplicationBusInternalException(
            "It seems like the Application Bus is not running.")).to("direct:handleResponse");

        // handle response
        from("direct:handleResponse").process(responseProcessor).marshal(jaxb);
    }
}
