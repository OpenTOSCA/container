package org.opentosca.bus.management.api.osgievent.route;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.osgievent.Activator;
import org.opentosca.bus.management.api.osgievent.OsgiEventOperations;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.springframework.stereotype.Component;

/**
 * Route of the Management Bus-OSGiEvent-API.<br>
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
@Component
public class Route extends RouteBuilder {

  @Override
  public void configure() throws Exception {

    // Management Bus Endpoints
    final String MANAGEMENT_BUS_IA =
      "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA";
    final String MANAGEMENT_BUS_PLAN =
      "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokePlan";

    this.from("direct:invoke").to("stream:out").process(exchange -> {

      exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(), Activator.apiID);

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
      .when(header("OPERATION").isEqualTo(OsgiEventOperations.INVOKE_IA.getHeaderValue()))
        .to("direct:invokeIA")
      .when(header("OPERATION").isEqualTo(OsgiEventOperations.INVOKE_PLAN.getHeaderValue()))
        .to("direct:invokePlan")
      .end();

    this.from("direct:invokeIA").to("stream:out").bean(IManagementBusService.class, "invokeIA").end();
    this.from("direct:invokePlan").to("stream:out").bean(IManagementBusService.class, "invokePlan").end();

    this.from("direct-vm:" + Activator.apiID).recipientList(this.simple("direct:response${id}")).end();

  }

}
