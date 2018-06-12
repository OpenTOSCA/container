package org.opentosca.bus.application.service.impl.route;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.opentosca.bus.application.model.exception.ApplicationBusInternalException;
import org.opentosca.bus.application.service.impl.model.QueueMap;
import org.opentosca.bus.application.service.impl.model.RequestID;
import org.opentosca.bus.application.service.impl.model.ResultMap;
import org.opentosca.bus.application.service.impl.processor.InvocationRequestProcessor;
import org.opentosca.bus.application.service.impl.processor.ParameterCheckProcessor;

/**
 * InvokeOperationRoute of the Application Bus.<br>
 * <br>
 *
 * "invokeOperation" requests are handled here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class InvokeOperationRoute extends RouteBuilder {

    public final static String APPLICATION_BUS_PLUGIN_ENDPOINT_HEADER = "ApplicationBusPluginEndpoint";
    private final static String APPLICATION_BUS_REQUEST_ID_HEADER = "ApplicationBusRequestID";

    @Override
    public void configure() throws Exception {

        final ParameterCheckProcessor checkProcessor = new ParameterCheckProcessor();
        final InvocationRequestProcessor requestProcessor = new InvocationRequestProcessor();

        // handle exceptions
        onException(Exception.class).setBody(property(Exchange.EXCEPTION_CAUGHT)).to("direct:handleResponse");

        // check if all needed parameters are specified. If this is the case set
        // requestID (for the response) and send request to further processing.
        from(MainRoute.INVOKE_ENDPOINT).doTry().process(checkProcessor).doCatch(ApplicationBusExternalException.class)
                                       .end().choice().when(property(Exchange.EXCEPTION_CAUGHT).isNull())
                                       .setHeader(APPLICATION_BUS_REQUEST_ID_HEADER,
                                                  method(RequestID.class, "getNextID"))
                                       .wireTap("direct:invokeProcess").end().to("direct:init").otherwise()
                                       .setBody(property(Exchange.EXCEPTION_CAUGHT));

        // set "isFinsihed"-flag to false for this request
        from("direct:init").bean(QueueMap.class, "notFinished(${header." + APPLICATION_BUS_REQUEST_ID_HEADER + "})")
                           .setBody(simple("${header." + APPLICATION_BUS_REQUEST_ID_HEADER + "}"));

        // check if matching plugin is available and send request to it.
        // Otherwise throw exception.
        from("direct:invokeProcess").setExchangePattern(ExchangePattern.InOut).process(requestProcessor).choice()
                                    .when(header(APPLICATION_BUS_PLUGIN_ENDPOINT_HEADER).isNotNull())
                                    .to("direct:toPlugin").endChoice().otherwise()
                                    .throwException(new ApplicationBusInternalException(
                                        "No matching Application Bus Plugin found."));

        // send to plugin
        from("direct:toPlugin").doTry().recipientList(header(APPLICATION_BUS_PLUGIN_ENDPOINT_HEADER)).end()
                               .doCatch(Exception.class).setBody(property(Exchange.EXCEPTION_CAUGHT)).doFinally()
                               .to("direct:handleResponse").end();

        // handle response: set "isFinsihed"-flag to true and store result in
        // ResultMap
        from("direct:handleResponse").bean(QueueMap.class,
                                           "finished(${header." + APPLICATION_BUS_REQUEST_ID_HEADER + "})")
                                     .bean(ResultMap.class,
                                           "put(${header." + APPLICATION_BUS_REQUEST_ID_HEADER + "}, ${body})")
                                     .stop();

    }

}
