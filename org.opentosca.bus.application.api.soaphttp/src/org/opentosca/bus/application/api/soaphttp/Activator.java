package org.opentosca.bus.application.api.soaphttp;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.application.api.soaphttp.route.Route;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the SOAP/HTTP-Application Bus-API.<br>
 * <br>
 *
 * The activator is needed to add and start the camel routes.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(final BundleContext bundleContext) throws Exception {

        final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);
        final DefaultCamelContext camelContext = new OsgiDefaultCamelContext(bundleContext, reg);

        camelContext.addRoutes(new Route());

        camelContext.start();

        Activator.LOG.info("Application Bus SOAP API started!");
    }

    @Override
    public void stop(final BundleContext arg0) throws Exception {

        Activator.LOG.info("Application Bus SOAP API stopped!");
    }

}
