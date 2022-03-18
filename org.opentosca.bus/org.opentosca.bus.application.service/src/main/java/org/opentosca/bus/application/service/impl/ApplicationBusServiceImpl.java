package org.opentosca.bus.application.service.impl;

import org.opentosca.bus.application.service.IApplicationBusService;
import org.springframework.stereotype.Service;

/**
 * Application Bus implementation.<br>
 * <br>
 * <p>
 * The routing endpoint is defined here. The Application Bus APIs need this endpoint to send requests to the Application
 * Bus. The endpoint is handed over during the bind process in the respective API implementation.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see IApplicationBusService
 */
@Service
public class ApplicationBusServiceImpl implements IApplicationBusService {

    // Routing endpoint of the Application Bus bundle
    public static final String ENDPOINT = "direct-vm:org.opentosca.bus.application.service";

    @Override
    public String getRoutingEndpoint() {
        return ENDPOINT;
    }
}
