package org.opentosca.bus.management.servicehandler;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that handles all needed services for MBUtils. <br>
 * <br>
 *
 *
 *
 * @see IManagementBusPluginService
 * @see IToscaEngineService
 * @see ICoreEndpointService
 *
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */

public class ServiceHandler {

    public static IInstanceDataService instanceDataService, oldInstanceDataService;
    public static IToscaEngineService toscaEngineService, oldToscaEngineService;

    private final static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);


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
                ServiceHandler.oldToscaEngineService = toscaEngineService;
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

        ServiceHandler.LOG.debug("ToscaEngineService unbound.");
    }

    /**
     * Bind InstanceDataService
     *
     * @param instanceDataService
     */
    public void bindInstanceDataService(final IInstanceDataService instanceDataService) {
        if (instanceDataService != null) {
            if (ServiceHandler.instanceDataService == null) {
                ServiceHandler.instanceDataService = instanceDataService;
            } else {
                ServiceHandler.oldInstanceDataService = instanceDataService;
                ServiceHandler.instanceDataService = instanceDataService;
            }

            ServiceHandler.LOG.debug("Bind InstanceDataServiceInterface: {} bound.",
                ServiceHandler.instanceDataService.toString());
        } else {
            ServiceHandler.LOG.error("Bind InstanceDataServiceInterface: Supplied parameter is null!");
        }
    }

    /**
     * Unbind InstanceDataServiceInterface
     *
     * @param instanceDataService
     */
    public void unbindInstanceDataService(IInstanceDataService instanceDataService) {
        if (ServiceHandler.oldInstanceDataService == null) {
            instanceDataService = null;
        } else {
            ServiceHandler.oldInstanceDataService = null;
        }

        ServiceHandler.LOG.debug("InstanceDataServiceInterface unbound.");
    }

}
