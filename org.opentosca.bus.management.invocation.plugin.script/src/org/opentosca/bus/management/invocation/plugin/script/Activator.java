package org.opentosca.bus.management.invocation.plugin.script;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.invocation.plugin.script.typeshandler.ArtifactTypesHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Script-Invocation-Management-Bus-Plug-in.<br>
 * <br>
 *
 *
 * The activator is needed to start the camel context.
 *
 *
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public class Activator implements BundleActivator {

    public static DefaultCamelContext camelContext;

    final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    public static String bundleID;

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {

        Activator.bundleID = bundleContext.getBundle().getSymbolicName();
        Activator.camelContext = new OsgiDefaultCamelContext(bundleContext);
        Activator.camelContext.start();

        ArtifactTypesHandler.init(bundleContext);

        Activator.LOG.info("Script-IA-Management Bus-PLUGIN-STARTED");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        Activator.camelContext = null;
        Activator.LOG.info("Script-IA-Management Bus-PLUGIN-STOPPED");
    }

}
