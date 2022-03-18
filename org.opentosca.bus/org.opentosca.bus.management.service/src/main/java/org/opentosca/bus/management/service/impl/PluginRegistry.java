package org.opentosca.bus.management.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Singleton
@Named("managementBusPluginRegistry")
public class PluginRegistry {

    private final Map<String, IManagementBusInvocationPluginService> invocationPluginServices =
        Collections.synchronizedMap(new HashMap<>());
    private final Map<String, IManagementBusDeploymentPluginService> deploymentPluginServices =
        Collections.synchronizedMap(new HashMap<>());

    @Inject
    public PluginRegistry(@Autowired(required = false) Collection<IManagementBusDeploymentPluginService> deploymentPlugins,
                          @Autowired(required = false) Collection<IManagementBusInvocationPluginService> invocationPlugins) {
        // must be marked as not required to allow having no plugin at all discovered
        // unfortunately Spring then injects null instead of an empty collection
        if (deploymentPlugins != null) {
            deploymentPlugins.forEach(plugin -> plugin.getSupportedTypes()
                .forEach(type -> this.deploymentPluginServices.put(type,
                    plugin)));
        }
        if (invocationPlugins != null) {
            invocationPlugins.forEach(plugin -> plugin.getSupportedTypes()
                .forEach(type -> this.invocationPluginServices.put(type,
                    plugin)));
        }
    }

    public Map<String, IManagementBusInvocationPluginService> getInvocationPluginServices() {
        return this.invocationPluginServices;
    }

    public Map<String, IManagementBusDeploymentPluginService> getDeploymentPluginServices() {
        return this.deploymentPluginServices;
    }
}
