/**
 *
 */
package org.opentosca.planbuilder.service;

import java.util.Dictionary;
import java.util.Hashtable;

import org.glassfish.jersey.servlet.ServletContainer;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IHTTPService;
import org.osgi.service.http.HttpService;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class ServiceRegistry {

    private static IHTTPService openToscaHttpService = null;
    private static ICoreFileService openToscaCoreFileService = null;


    public static IHTTPService getHTTPService() {
        return ServiceRegistry.openToscaHttpService;
    }

    public static ICoreFileService getCoreFileService() {
        return ServiceRegistry.openToscaCoreFileService;
    }

    protected void bindOpenToscaHttpService(final IHTTPService httpService) {
        ServiceRegistry.openToscaHttpService = httpService;
    }

    protected void unbindOpenToscaHttpService(final IHTTPService httpService) {
        ServiceRegistry.openToscaHttpService = null;
    }

    protected void bindOpenToscaCoreFileService(final ICoreFileService coreFileService) {
        ServiceRegistry.openToscaCoreFileService = coreFileService;
    }

    protected void unbindOpenToscaCoreFileService(final ICoreFileService coreFileService) {
        ServiceRegistry.openToscaCoreFileService = null;
    }

    protected void bindHttpService(final HttpService httpService) {

        final Dictionary<String, String> initParams = new Hashtable<>();
        initParams.put("javax.ws.rs.Application", PlanBuilderService.class.getName());
        // initParams.put("com.sun.jersey.api.json.POJOMappingFeature",
        // "true");

        // TODO: Temporary workaround
        // This is a workaround related to issue JERSEY-2093; grizzly
        // (1.9.5)
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            httpService.registerServlet("/planbuilder", new ServletContainer(), initParams, null);
        }
        catch (final Exception ex) {
            ex.printStackTrace();

        }
        finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    protected void unbindHttpService(final HttpService httpService) {
        httpService.unregister("/planbuilder");
    }

}
