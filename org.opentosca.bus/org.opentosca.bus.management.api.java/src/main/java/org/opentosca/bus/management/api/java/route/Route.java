package org.opentosca.bus.management.api.java.route;

import javax.inject.Named;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.api.java.ExposedManagementBusOperations;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.springframework.stereotype.Component;

/**
 * Route of the Management Bus Java API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Incoming events are given here from the EventHandler to be routed to the Management Bus for further processing. The
 * response message is given back to the EventHandler.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Component
@Named("management-bus-java-route")
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
            .when(header("OPERATION").isEqualTo(ExposedManagementBusOperations.NOTIFY_PARTNER.getHeaderValue()))
            .to("direct:invokeNotifyPartner")
            .when(header("OPERATION").isEqualTo(ExposedManagementBusOperations.NOTIFY_PARTNERS.getHeaderValue()))
            .to("direct:invokeNotifyPartners")
            .end();

        this.from("direct:invokeIA").to("stream:out").bean(managementBusService, "invokeIA").end();
        this.from("direct:invokePlan").to("stream:out").bean(managementBusService, "invokePlan").end();
        this.from("direct:invokeNotifyPartner").to("stream:out").bean(managementBusService, "notifyPartner").end();
        this.from("direct:invokeNotifyPartners").to("stream:out").bean(managementBusService, "notifyPartners").end();
    }
}
