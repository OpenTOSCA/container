package org.opentosca.bus.management.deployment.plugin.tomcat.servicehandler;

import org.opentosca.container.core.service.IHTTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that handles all needed services for the Management-Bus-Tomcat-Deployment-Plug-in.
 *
 * @see IHTTPService
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class ServiceHandler {

    public static IHTTPService httpService;

    private final static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

    /**
     * Register IHTTPService.
     *
     * @param service - A IHTTPService to register.
     */
    public void bindHTTPService(final IHTTPService httpService) {
        if (httpService != null) {
            ServiceHandler.httpService = httpService;
            ServiceHandler.LOG.debug("Register IHTTPService: {} registered.", httpService.toString());
        } else {
            ServiceHandler.LOG.error("Register IHTTPService: Supplied parameter is null!");
        }
    }

    /**
     * Unregister IHTTPService.
     *
     * @param service - A IHTTPService to unregister.
     */
    public void unbindHTTPService(final IHTTPService httpService) {
        ServiceHandler.httpService = null;
        ServiceHandler.LOG.debug("Unregister IHTTPService: {} unregistered.", httpService.toString());
    }
}
