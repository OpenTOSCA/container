package org.opentosca.bus.management.service.impl.servicehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.service.ICoreEndpointService;
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

  public static Map<String, IManagementBusInvocationPluginService> invocationPluginServices =
    Collections.synchronizedMap(new HashMap<String, IManagementBusInvocationPluginService>());
  public static Map<String, IManagementBusDeploymentPluginService> deploymentPluginServices =
    Collections.synchronizedMap(new HashMap<String, IManagementBusDeploymentPluginService>());
  public static ICoreEndpointService endpointService;
  public static IToscaEngineService toscaEngineService;
  public static ICoreCapabilityService capabilityService;

  private final static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

  /**
   * Bind EndpointService.
   *
   * @param endpointService - The endpointService to register.
   */
  public void bindEndpointService(final ICoreEndpointService endpointService) {
    if (endpointService != null) {
      ServiceHandler.endpointService = endpointService;
      LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
    } else {
      LOG.error("Bind Endpoint Service: Supplied parameter is null!");
    }
  }

  /**
   * Unbind EndpointService.
   *
   * @param endpointService - The endpointService to unregister.
   */
  public void unbindEndpointService(final ICoreEndpointService endpointService) {
    ServiceHandler.endpointService = null;
    LOG.debug("Unbind Endpoint Service unbound.");
  }

  /**
   * Bind ToscaEngineService
   *
   * @param toscaEngineService
   */
  public void bindToscaService(final IToscaEngineService toscaEngineService) {
    if (toscaEngineService != null) {
      ServiceHandler.toscaEngineService = toscaEngineService;
      LOG.debug("Bind ToscaEngineService: {} bound.", toscaEngineService.toString());
    } else {
      LOG.error("Bind ToscaEngineService: Supplied parameter is null!");
    }
  }

  /**
   * Unbind ToscaEngineService
   *
   * @param toscaEngineService
   */
  public void unbindToscaService(final IToscaEngineService toscaEngineService) {
    ServiceHandler.toscaEngineService = null;
    LOG.debug("Unbind ToscaEngineService unbound.");
  }

  /**
   * Bind CapabilityService
   *
   * @param capabilityService
   */
  public void bindCapabilityService(final ICoreCapabilityService capabilityService) {
    if (capabilityService != null) {
      ServiceHandler.capabilityService = capabilityService;
      LOG.debug("Bind ICoreCapabilityService: {} bound.", ServiceHandler.capabilityService.toString());
    } else {
      LOG.error("Bind ICoreCapabilityService: Supplied parameter is null!");
    }
  }

  /**
   * Unbind CapabilityService
   *
   * @param capabilityService
   */
  public void unbindCapabilityService(final ICoreCapabilityService capabilityService) {
    ServiceHandler.capabilityService = null;
    LOG.debug("Unbind ICoreCapabilityService unbound.");
  }

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
