package org.opentosca.bus.management.invocation.plugin.soaphttp.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.invocation.plugin.soaphttp.processor.HeaderProcessor;
import org.springframework.stereotype.Component;

/**
 * Synchronous route of SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This class manages the synchronous communication with a service.It invokes the service and waits for the response
 * from it.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
public class SyncRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        final String ENDPOINT =
            "cxf:${header[SOAPEndpoint]}?wsdlURL=${header[endpoint]}?wsdl&dataFormat=PAYLOAD&loggingFeatureEnabled=true";

        final Processor headerProcessor = new HeaderProcessor();
        this.from("direct:Sync-WS-Invoke").process(headerProcessor).recipientList(this.simple(ENDPOINT));
    }
}
