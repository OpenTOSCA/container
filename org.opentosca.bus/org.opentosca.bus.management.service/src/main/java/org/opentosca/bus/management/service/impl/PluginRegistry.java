package org.opentosca.bus.management.service.impl;

import java.util.*;

import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

@Component
@Singleton
public class PluginRegistry {
  private final Map<String, IManagementBusInvocationPluginService> invocationPluginServices = Collections.synchronizedMap(new HashMap<>());
  private final Map<String, IManagementBusDeploymentPluginService> deploymentPluginServices = Collections.synchronizedMap(new HashMap<>());

  private final static Logger LOG = LoggerFactory.getLogger(PluginRegistry.class);

  @Inject
  public PluginRegistry(@Autowired(required = false)Collection<IManagementBusDeploymentPluginService> deploymentPlugins,
                        @Autowired(required = false)Collection<IManagementBusInvocationPluginService> invocationPlugins) {
    // must be marked as not required to allow having no plugin at all discovered
    // unfortunately Spring then injects null instead of an empty collection
    if (deploymentPlugins != null) {
      deploymentPlugins.forEach(plugin -> plugin.getSupportedTypes().forEach(type -> deploymentPluginServices.put(type, plugin)));
    }
    if (invocationPlugins != null) {
      invocationPlugins.forEach(plugin -> plugin.getSupportedTypes().forEach(type -> invocationPluginServices.put(type, plugin)));
    }
  }

  public Map<String, IManagementBusInvocationPluginService> getInvocationPluginServices() {
    return invocationPluginServices;
  }

  public Map<String, IManagementBusDeploymentPluginService> getDeploymentPluginServices() {
    return deploymentPluginServices;
  }
}
