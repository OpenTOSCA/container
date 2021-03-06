package org.opentosca.bus.application.api.jsonhttp.servicehandler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.opentosca.bus.application.service.IApplicationBusService;
import org.springframework.stereotype.Service;

/**
 * Service Handler of the Application Bus-JSON/HTTP-API.<br>
 * <br>
 * <p>
 * Here the implementation of the IApplicationBusService is binded or unbinded. During the binding the routing endpoint
 * of the Application Bus is handed over.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see IApplicationBusService
 */
@Service
@Singleton
@Deprecated
public class ApplicationBusServiceHandler {

    // Routing endpoint of the IApplicationBus implementation.
    private final String applicationBusRoutingEndpoint;

    @Inject
    public ApplicationBusServiceHandler(String applicationBusRoutingEndpoint) {
        this.applicationBusRoutingEndpoint = applicationBusRoutingEndpoint;
    }

    /**
     * @return The Routing endpoint of the ApplicationBus
     */
    public String getApplicationBusRoutingEndpoint() {
        return applicationBusRoutingEndpoint;
    }
}
