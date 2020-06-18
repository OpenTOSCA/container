package org.opentosca.bus.management.invocation.plugin.soaphttp.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.invocation.plugin.soaphttp.processor.HeaderProcessor;

/**
 * Request-only route of SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This class manages the request-only invocation of an service.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class RequestOnlyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";

        final Processor headerProcessor = new HeaderProcessor();
        this.from("direct:RequestOnly-WS-Invoke").process(headerProcessor).recipientList(this.simple(ENDPOINT));
    }
}
