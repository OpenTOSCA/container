package org.opentosca.deployment.checks.camel;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.bus.management.service.IManagementBusService;
import org.springframework.stereotype.Component;

@Component
public class RouteConfiguration extends RouteBuilder {

    private final IManagementBusService managementBusService;

    @Inject
    public RouteConfiguration(IManagementBusService managementBusService) {
        this.managementBusService = managementBusService;
    }

    @Override
    public void configure() throws Exception {
        this.from("direct-vm:" + "org.opentosca.deployment.checks").recipientList(this.simple("direct:response-${id}")).end();
    }
}
