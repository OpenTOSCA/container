package org.opentosca.deployment.checks.camel;

import org.apache.camel.builder.RouteBuilder;
import org.opentosca.deployment.checks.Activator;

public class RouteConfiguration extends RouteBuilder {

    private static final String MANAGEMENT_BUS =
        "bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA";

    @Override
    public void configure() throws Exception {
        this.from("direct:invokeIA").to("stream:out").to(MANAGEMENT_BUS).end();
        this.from("direct-vm:" + Activator.ID).recipientList(this.simple("direct:response-${id}")).end();
    }
}
