package org.opentosca.bus.application.plugin.jsonhttp.service.impl;

import org.apache.camel.component.directvm.DirectVmComponent;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.application.plugin.jsonhttp.service.impl.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the JSON/HTTP-Application Bus-Plugin.<br>
 * <br>
 *
 * The activator is needed to add and start the camel routes. The bundleID is used for generating
 * the routing endpoint of this plugin.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    private static DefaultCamelContext camelContext;

    private static String bundleID;

    static String getBundleID() {
        return bundleID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework. BundleContext)
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {

        bundleID = bundleContext.getBundle().getSymbolicName();

        final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);
        camelContext = new OsgiDefaultCamelContext(bundleContext, reg);
        
        // This explicitly binds the required components, fixing the OSGI startup
        camelContext.addComponent("direct-vm", new DirectVmComponent());
        camelContext.addComponent("http", new HttpComponent());

        camelContext.addRoutes(new Route());

        camelContext.start();
        Activator.LOG.info("Application Bus JSON-HTTP plugin started!");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = null;
        Activator.LOG.info("Application Bus JSON-HTTP plugin stopped!");
    }

}
