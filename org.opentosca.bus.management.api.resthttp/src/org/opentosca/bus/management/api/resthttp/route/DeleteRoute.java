package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.resthttp.model.QueueMap;
import org.opentosca.bus.management.api.resthttp.model.ResultMap;

/**
 * InvocationRoute of the Management Bus REST-API.<br>
 * <br>
 *
 * The "getResult" endpoint of the REST-API is created here.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class DeleteRoute extends RouteBuilder {

    // true => invocation results will be deleted automatically after fetching
    // the result
    // false => invocation result needs to be deleted manually
    public static final boolean AUTO_DELETE = false;

    @Override
    public void configure() throws Exception {
        from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.GET_RESULT_ENDPOINT
            + "?restletMethods=delete").bean(QueueMap.class, "remove(${header." + InvocationRoute.ID + "})")
                                       .bean(ResultMap.class, "remove(${header." + InvocationRoute.ID + "})")
                                       .removeHeaders("*");
    }
}
