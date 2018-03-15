package org.opentosca.bus.management.api.resthttp;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.api.resthttp.route.DeleteRoute;
import org.opentosca.bus.management.api.resthttp.route.GetResultRoute;
import org.opentosca.bus.management.api.resthttp.route.InvocationRoute;
import org.opentosca.bus.management.api.resthttp.route.IsFinishedRoute;
import org.opentosca.bus.management.api.resthttp.route.OptionsRoute;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Management Bus REST-API.<br>
 * <br>
 *
 * The activator is needed to add and start the camel routes.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    public static String apiID;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {

        Activator.apiID = bundleContext.getBundle().getSymbolicName();

        final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);

        final DefaultCamelContext camelContext = new OsgiDefaultCamelContext(bundleContext, reg);

        camelContext.addRoutes(new InvocationRoute());
        camelContext.addRoutes(new GetResultRoute());
        camelContext.addRoutes(new IsFinishedRoute());
        camelContext.addRoutes(new DeleteRoute());
        camelContext.addRoutes(new OptionsRoute());

        camelContext.start();

        Activator.LOG.info("Management Bus REST API started!");
    }

    @Override
    public void stop(final BundleContext arg0) throws Exception {

        Activator.LOG.info("Management Bus REST API stopped!");
    }

}
