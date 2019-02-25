package org.opentosca.planbuilder.csarhandler;

import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is used to bind different services for the CSARHandler
 * </p>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class ServiceRegistry {

    final private static Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

    private static ICoreFileService openTOSCACoreFileService = null;


    protected void bindOpenTOSCACoreFileService(final ICoreFileService fileService) {
        LOG.debug("Binding CoreFileService");
        ServiceRegistry.openTOSCACoreFileService = fileService;
    }

    protected void unbindOpenTOSCACoreFileService(final ICoreFileService fileService) {
        LOG.debug("Unbinding CoreFileService");
        ServiceRegistry.openTOSCACoreFileService = null;
    }

    protected static ICoreFileService getCoreFileService() {
        return ServiceRegistry.openTOSCACoreFileService;
    }
}
