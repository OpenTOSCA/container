package org.opentosca.bus.management.api.resthttp.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route of the Management Bus REST-API to handle OPTIONS requests.<br>
 * <br>
 * 
 * The "options" endpoint of the REST-API is created here.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 * 
 */
public class OptionsRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // options route
    // from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.INVOKE_ENDPOINT
    // + "?restletMethods=options");
    //
    // from("restlet:" + InvocationRoute.BASE_ENDPOINT + InvocationRoute.GET_RESULT_ENDPOINT
    // + "?restletMethods=options");
  }
}
