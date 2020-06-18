package org.opentosca.deployment.checks.camel;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.service.IManagementBusService;

public class RouteConfiguration extends RouteBuilder {

    private IManagementBusService managementBusService;

    @Inject
    public RouteConfiguration(IManagementBusService managementBusService) {
        this.managementBusService = managementBusService;
    }

    @Override
    public void configure() throws Exception {
        this.from("direct:invokeIA").to("stream:out").bean(managementBusService, "invokeIA").end();
        this.from("direct-vm:" + "org.opentosca.deployment.checks").recipientList(this.simple("direct:response-${id}")).end();
    }
}
