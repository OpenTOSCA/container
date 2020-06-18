package org.opentosca.bus.application.service.impl.servicehandler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.bus.application.plugin.service.IApplicationBusPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to bind interface {@link IApplicationBusPluginService}.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
@Service
@NonNullByDefault
public class ApplicationBusPluginRegistry {

    // HashMap that stores available plug-ins. The supported InvocationType is
    // used as key and the corresponding routing endpoint as value.
    private static ConcurrentHashMap<String, String> pluginServices = new ConcurrentHashMap<>();

    final private static Logger LOG = LoggerFactory.getLogger(ApplicationBusPluginRegistry.class);

    @Inject
    public ApplicationBusPluginRegistry(@Autowired(required = false) Collection<IApplicationBusPluginService> plugins) {
        if (plugins != null) {
            for (IApplicationBusPluginService plugin : plugins) {
                final String routingEndpoint = plugin.getRoutingEndpoint();
                plugin.getSupportedInvocationTypes().forEach(invocationType -> pluginServices.put(invocationType, routingEndpoint));
            }
        }
    }

    /**
     * @return BundleID of the matching ApplicationBusPlugin
     */
    @Nullable
    public String getApplicationBusPluginBundleID(final String invocationType) {
        return pluginServices.get(invocationType);
    }
}
