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
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * In this class the from the Management Bus needed services are binded an unbinded.
 *
 *
 * @see IManagementBusInvocationPluginService
 * @see IManagementBusDeploymentPluginService
 * @see ICoreEndpointService
 * @see IToscaEngineService
 * @see ICoreCapabilityService
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */

public class ServiceHandler {

    public static Map<String, IManagementBusInvocationPluginService> invocationPluginServices =
        Collections.synchronizedMap(new HashMap<String, IManagementBusInvocationPluginService>());
    public static Map<String, IManagementBusDeploymentPluginService> deploymentPluginServices =
        Collections.synchronizedMap(new HashMap<String, IManagementBusDeploymentPluginService>());
    public static ICoreEndpointService endpointService, oldEndpointService;
    public static IToscaEngineService toscaEngineService, oldToscaEngineService;
    public static ICoreCapabilityService capabilityService, oldCapabilityService;

    private final static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);


    /**
     * Bind EndpointService.
     *
     * @param endpointService - The endpointService to register.
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService != null) {
            if (ServiceHandler.endpointService == null) {
                ServiceHandler.endpointService = endpointService;
            } else {
                ServiceHandler.oldEndpointService = ServiceHandler.endpointService;
                ServiceHandler.endpointService = endpointService;
            }

            ServiceHandler.LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
        } else {
            ServiceHandler.LOG.error("Bind Endpoint Service: Supplied parameter is null!");
        }

    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        if (ServiceHandler.oldEndpointService == null) {
            endpointService = null;
        } else {
            ServiceHandler.oldEndpointService = null;
        }

        ServiceHandler.LOG.debug("Unbind Endpoint Service unbound.");
    }

    /**
     * Bind ToscaEngineService
     *
     * @param toscaEngineService
     */
    public void bindToscaService(final IToscaEngineService toscaEngineService) {
        if (toscaEngineService != null) {
            if (ServiceHandler.toscaEngineService == null) {
                ServiceHandler.toscaEngineService = toscaEngineService;
            } else {
                ServiceHandler.oldToscaEngineService = ServiceHandler.toscaEngineService;
                ServiceHandler.toscaEngineService = toscaEngineService;
            }

            ServiceHandler.LOG.debug("Bind ToscaEngineService: {} bound.", toscaEngineService.toString());
        } else {
            ServiceHandler.LOG.error("Bind ToscaEngineService: Supplied parameter is null!");
        }
    }

    /**
     * Unbind ToscaEngineService
     *
     * @param toscaEngineService
     */
    public void unbindToscaService(IToscaEngineService toscaEngineService) {
        if (ServiceHandler.oldToscaEngineService == null) {
            toscaEngineService = null;
        } else {
            ServiceHandler.oldToscaEngineService = null;
        }

        ServiceHandler.LOG.debug("Unbind ToscaEngineService unbound.");
    }

    /**
     * Bind CapabilityService
     *
     * @param capabilityService
     */
    public void bindCapabilityService(final ICoreCapabilityService capabilityService) {
        if (capabilityService != null) {
            if (ServiceHandler.capabilityService == null) {
                ServiceHandler.capabilityService = capabilityService;
            } else {
                ServiceHandler.oldCapabilityService = ServiceHandler.capabilityService;
                ServiceHandler.capabilityService = capabilityService;
            }

            ServiceHandler.LOG.debug("Bind ICoreCapabilityService: {} bound.",
                                     ServiceHandler.capabilityService.toString());
        } else {
            ServiceHandler.LOG.error("Bind ICoreCapabilityService: Supplied parameter is null!");
        }
    }

    /**
     * Unbind CapabilityService
     *
     * @param capabilityService
     */
    public void unbindCapabilityService(ICoreCapabilityService capabilityService) {
        if (ServiceHandler.oldCapabilityService == null) {
            capabilityService = null;
        } else {
            ServiceHandler.oldCapabilityService = null;
        }

        ServiceHandler.LOG.debug("Unbind ICoreCapabilityService unbound.");
    }

    /**
     * Bind Management Bus Invocation Plugin Services and store them in local HashMap.
     *
     * @param plugin - A Management Bus Invocation Plugin to register.
     */
    public void bindInvocationPluginService(final IManagementBusInvocationPluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                ServiceHandler.invocationPluginServices.put(type, plugin);
                ServiceHandler.LOG.debug("Bound Management Bus Invocation Plugin: {} for Type: {}", plugin.toString(),
                                         type);
            }

        } else {
            ServiceHandler.LOG.error("Bind Management Bus Invocation Plugin: Supplied parameter is null!");
        }
    }

    /**
     * Unbind Management Bus Invocation Plugin Services and delete them from local Map.
     *
     * @param plugin - A Management Bus Invocation Plugin to unregister.
     */
    public void unbindInvocationPluginService(final IManagementBusInvocationPluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                final Object deletedObject = ServiceHandler.invocationPluginServices.remove(type);
                if (deletedObject != null) {
                    ServiceHandler.LOG.debug("Unbound Management Bus Invocation Plugin Service: {} for Type: {}",
                                             plugin.toString(), type);
                } else {
                    ServiceHandler.LOG.debug("Management Bus Invocation Plugin {} could not be unbound, because it is not bound!",
                                             plugin.toString());
                }
            }
        }

        else {
            ServiceHandler.LOG.error("Unbind Management Bus Invocation Plugin: Supplied parameter is null!");
        }
    }

    /**
     * Bind Management Bus Deployment Plugin Services and store them in local HashMap.
     *
     * @param plugin - A Management Bus Deployment Plugin to register.
     */
    public void bindDeploymentPluginService(final IManagementBusDeploymentPluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                ServiceHandler.deploymentPluginServices.put(type, plugin);
                ServiceHandler.LOG.debug("Bound Management Bus Deployment Plugin: {} for Type: {}", plugin.toString(),
                                         type);
            }

        } else {
            ServiceHandler.LOG.error("Bind Management Bus Deployment Plugin: Supplied parameter is null!");
        }
    }

    /**
     * Unbind Management Bus Deployment Plugin Services and delete them from local Map.
     *
     * @param plugin - A Management Bus Invocation Plugin to unregister.
     */
    public void unbindDeploymentPluginService(final IManagementBusDeploymentPluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                final Object deletedObject = ServiceHandler.deploymentPluginServices.remove(type);
                if (deletedObject != null) {
                    ServiceHandler.LOG.debug("Unbound Management Bus Deployment Plugin Service: {} for Type: {}",
                                             plugin.toString(), type);
                } else {
                    ServiceHandler.LOG.debug("Management Bus Deployment Plugin {} could not be unbound, because it is not bound!",
                                             plugin.toString());
                }
            }
        }

        else {
            ServiceHandler.LOG.error("Unbind Management Bus Deployment Plugin: Supplied parameter is null!");
        }
    }
}
