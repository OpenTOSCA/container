package org.opentosca.bus.application.plugin.jsonhttp.service.impl;

import java.util.Arrays;
import java.util.List;

import org.opentosca.bus.application.plugin.service.IApplicationBusPluginService;

/**
 * JSON/HTTP-Plugin of the Application Bus.<br>
 * <br>
 * <p>
 * The supported invocationTypes and the plugin routing endpoint are defined here. During the bind process of the
 * Application Bus, this information are handed over.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see IApplicationBusPluginService
 */
public class ApplicationBusJsonHttpPluginServiceImpl implements IApplicationBusPluginService {

    // Routing endpoint of the Application Bus bundle
    public static final String ENDPOINT = "direct-vm:" + "org.opentosca.bus.application.plugin.jsonhttp.service.impl";

    private static final List<String> invocationTypes = Arrays.asList("JSON/HTTP");

    @Override
    public List<String> getSupportedInvocationTypes() {
        return invocationTypes;
    }

    @Override
    public String getRoutingEndpoint() {
        return ENDPOINT;
    }
}
