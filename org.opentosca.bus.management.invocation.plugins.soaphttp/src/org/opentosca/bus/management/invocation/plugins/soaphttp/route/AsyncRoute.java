package org.opentosca.bus.management.invocation.plugins.soaphttp.route;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.invocation.plugins.soaphttp.processor.CallbackProcessor;
import org.opentosca.bus.management.invocation.plugins.soaphttp.processor.HeaderProcessor;
import org.opentosca.container.core.common.Settings;

/**
 * Asynchronous route of SOAP/HTTP-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * This class manages the asynchronous communication with a service. Both invoking and handling the
 * callback are done here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class AsyncRoute extends RouteBuilder {


    public final static String PUBLIC_CALLBACKADDRESS =
        "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8087/callback";
    private final static String CALLBACKADDRESS = "http://0.0.0.0:8087/callback";

    @Override
    public void configure() throws Exception {
        final String ENDPOINT = "cxf:${header[endpoint]}?dataFormat=PAYLOAD&loggingFeatureEnabled=true";

        final Processor headerProcessor = new HeaderProcessor();

        this.from("direct:Async-WS-Invoke").process(headerProcessor).recipientList(this.simple(ENDPOINT)).end();

        final Processor callbackProcessor = new CallbackProcessor();

        this.from("jetty:" + AsyncRoute.CALLBACKADDRESS).to("stream:out").process(callbackProcessor).choice()
            .when(header("AvailableMessageID").isEqualTo("true"))
            .recipientList(this.simple("direct:Async-WS-Callback${header.MessageID}")).end();
    }

}
