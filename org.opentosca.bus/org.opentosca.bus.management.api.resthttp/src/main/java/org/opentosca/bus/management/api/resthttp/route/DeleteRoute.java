package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;
import org.springframework.stereotype.Component;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * The "getResult" endpoint of the REST-API is created here.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class DeleteRoute extends RouteBuilder {

    // true => invocation results will be deleted automatically after fetching
    // the result
    // false => invocation result needs to be deleted manually
    public static final boolean AUTO_DELETE = false;

    @Override
    public void configure() throws Exception {

        restConfiguration().component("jetty").host("0.0.0.0").port(8086).bindingMode(RestBindingMode.auto);
        from("rest:delete:"
            + InvocationRoute.GET_RESULT_ENDPOINT).bean(QueueMap.class, "remove(${header." + InvocationRoute.ID + "})")
            .bean(ResultMap.class, "remove(${header." + InvocationRoute.ID + "})")
            .removeHeaders("*");
    }
}
