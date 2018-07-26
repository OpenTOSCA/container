package org.opentosca.bus.management.service.impl;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.service.impl.collaboration.route.ReceiveRequestRoute;
import org.opentosca.bus.management.service.impl.collaboration.route.ReceiveResponseRoute;
import org.opentosca.bus.management.service.impl.collaboration.route.SendRequestResponseRoute;
import org.opentosca.container.core.common.Settings;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Management Bus.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * The activator is needed to start the camel context and add the routes for collaboration between
 * different OpenTOSCA instances. Additionally, a producer template is created which can be used by
 * all classes of this bundle to send camel messages.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    public static DefaultCamelContext camelContext;

    public static ProducerTemplate producer = null;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
        camelContext.setUseBreadcrumb(false);

        // the camel routes are only needed if collaboration is turned on
        if (Boolean.parseBoolean(Settings.OPENTOSCA_COLLABORATION_MODE)) {
            Activator.LOG.info("Collaboration mode is turned on. Starting camel routes...");

            // Create a producer template for all components of the Management Bus implementation.
            // This is recommended by camel to avoid the usage of too many threads.
            producer = Activator.camelContext.createProducerTemplate();

            // route to send requests/responses to other OpenTOSCA Containers
            Activator.camelContext.addRoutes(new SendRequestResponseRoute());

            // route to receive responses by other OpenTOSCA Containers
            Activator.camelContext.addRoutes(new ReceiveResponseRoute());

            // if the setting is null or equals the empty string, this Container does not subscribe
            // for requests of other Containers (acts as 'master')
            if (Settings.OPENTOSCA_COLLABORATION_MASTER == null || Settings.OPENTOSCA_COLLABORATION_MASTER.equals("")) {
                Activator.LOG.info("No other Container defined to subscribe for requests. Only started route to send own requests and receive replies.");
            } else {
                Activator.LOG.info("'Master' Container defined: {}. Starting route to receive requests from this Container...",
                                   Settings.OPENTOSCA_COLLABORATION_MASTER);

                Activator.camelContext.addRoutes(new ReceiveRequestRoute());
            }
        }

        Activator.camelContext.start();
        Activator.LOG.info("Management Bus started!");
    }

    @Override
    public void stop(final BundleContext arg0) throws Exception {
        // release resources
        try {
            if (producer != null) {
                producer.stop();
                producer = null;
            }

            if (camelContext != null) {
                camelContext.stop();
                camelContext = null;
            }
        }
        catch (final Exception e) {
            Activator.LOG.warn("Execption while releasing resources: {}", e.getMessage());
        }

        Activator.LOG.info("Management Bus stopped!");
    }
}
