package org.opentosca.bus.management.service.impl.servicehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that handles all needed services for Management Bus.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * In this class the from the Management Bus needed services are binded an unbinded.
 *
 * @see IManagementBusInvocationPluginService
 * @see IManagementBusDeploymentPluginService
 * @see ICoreEndpointService
 * @see IToscaEngineService
 * @see ICoreCapabilityService
 */

public class ServiceHandler {

  public static Map<String, IManagementBusInvocationPluginService> invocationPluginServices = Collections.synchronizedMap(new HashMap<>());
  public static Map<String, IManagementBusDeploymentPluginService> deploymentPluginServices = Collections.synchronizedMap(new HashMap<>());
  public static ICoreEndpointService endpointService;
  public static IToscaEngineService toscaEngineService;
  public static ICoreCapabilityService capabilityService;

  private final static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

  /**
   * Bind Management Bus Invocation plug-in Services and store them in local HashMap.
   *
   * @param plugin - A Management Bus Invocation plug-in to register.
   */
  public void bindInvocationPluginService(final IManagementBusInvocationPluginService plugin) {
    if (plugin != null) {

      final List<String> types = plugin.getSupportedTypes();

      for (final String type : types) {
        invocationPluginServices.put(type, plugin);
        LOG.debug("Bound Management Bus Invocation Plugin: {} for Type: {}", plugin.toString(), type);
      }
    } else {
      LOG.error("Bind Management Bus Invocation Plugin: Supplied parameter is null!");
    }
  }

  /**
   * Unbind Management Bus Invocation plug-in Services and delete them from local Map.
   *
   * @param plugin - A Management Bus Invocation plug-in to unregister.
   */
  public void unbindInvocationPluginService(final IManagementBusInvocationPluginService plugin) {
    if (plugin != null) {

      final List<String> types = plugin.getSupportedTypes();

      for (final String type : types) {
        final Object deletedObject = invocationPluginServices.remove(type);
        if (deletedObject != null) {
          LOG.debug("Unbound Management Bus Invocation Plugin Service: {} for Type: {}", plugin.toString(),
            type);
        } else {
          LOG.debug("Management Bus Invocation Plugin {} could not be unbound, because it is not bound!",
            plugin.toString());
        }
      }
    } else {
      LOG.error("Unbind Management Bus Invocation Plugin: Supplied parameter is null!");
    }
  }

  /**
   * Bind Management Bus Deployment plug-in Services and store them in local HashMap.
   *
   * @param plugin - A Management Bus Deployment plug-in to register.
   */
  public void bindDeploymentPluginService(final IManagementBusDeploymentPluginService plugin) {
    if (plugin != null) {

      final List<String> types = plugin.getSupportedTypes();

      for (final String type : types) {
        deploymentPluginServices.put(type, plugin);
        LOG.debug("Bound Management Bus Deployment Plugin: {} for Type: {}", plugin.toString(), type);
      }
    } else {
      LOG.error("Bind Management Bus Deployment Plugin: Supplied parameter is null!");
    }
  }

  /**
   * Unbind Management Bus Deployment plug-in Services and delete them from local Map.
   *
   * @param plugin - A Management Bus Deployment plug-in to unregister.
   */
  public void unbindDeploymentPluginService(final IManagementBusDeploymentPluginService plugin) {
    if (plugin != null) {

      final List<String> types = plugin.getSupportedTypes();

      for (final String type : types) {
        final Object deletedObject = deploymentPluginServices.remove(type);
        if (deletedObject != null) {
          LOG.debug("Unbound Management Bus Deployment Plugin Service: {} for Type: {}", plugin.toString(),
            type);
        } else {
          LOG.debug("Management Bus Deployment Plugin {} could not be unbound, because it is not bound!",
            plugin.toString());
        }
      }
    } else {
      LOG.error("Unbind Management Bus Deployment Plugin: Supplied parameter is null!");
    }
  }
}
