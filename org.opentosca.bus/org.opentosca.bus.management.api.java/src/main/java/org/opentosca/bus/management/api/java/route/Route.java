package org.opentosca.bus.management.api.java.route;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.java.ExposedManagementBusOperations;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;

/**
 * Route of the Management Bus Java API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Incoming events are given here from the EventHandler to be routed to the Management Bus for
 * further processing. The response message is given back to the EventHandler.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class Route extends RouteBuilder {

  private final IManagementBusService managementBusService;

  public Route(IManagementBusService managementBusService) {
    this.managementBusService = managementBusService;
  }

  @Override
  public void configure() throws Exception {
    this.from("direct:invoke").to("stream:out").process(exchange -> {
      final String messageID =
        exchange.getIn().getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
      if (messageID != null) {
        exchange.getIn().setMessageId(messageID);
        exchange.getIn().setHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), false);
      } else {
        exchange.getIn().setHeader(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), true);
      }
    }).to("stream:out")
      .choice()
      .when(header("OPERATION").isEqualTo(ExposedManagementBusOperations.INVOKE_IA.getHeaderValue()))
        .to("direct:invokeIA")
      .when(header("OPERATION").isEqualTo(ExposedManagementBusOperations.INVOKE_PLAN.getHeaderValue()))
        .to("direct:invokePlan")
      .end();

    this.from("direct:invokeIA").to("stream:out").bean(managementBusService, "invokeIA").end();
    this.from("direct:invokePlan").to("stream:out").bean(managementBusService, "invokePlan").end();
  }

}
