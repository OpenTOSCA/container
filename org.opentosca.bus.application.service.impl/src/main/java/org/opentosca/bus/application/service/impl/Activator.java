package org.opentosca.bus.application.service.impl;

import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.directvm.DirectVmComponent;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.application.service.impl.route.GetResultRoute;
import org.opentosca.bus.application.service.impl.route.InvokeOperationRoute;
import org.opentosca.bus.application.service.impl.route.IsFinishedRoute;
import org.opentosca.bus.application.service.impl.route.MainRoute;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator of the Application Bus.<br>
 * <br>
 * <p>
 * The activator is needed to add and start the camel routes. The bundleID is used for generating
 * the routing endpoint of the Application Bus.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
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

    // get bundle name, used as routing endpoint
    bundleID = bundleContext.getBundle().getSymbolicName();

    final OsgiServiceRegistry reg = new OsgiServiceRegistry(bundleContext);
    camelContext = new OsgiDefaultCamelContext(bundleContext, reg);

    // This explicitly binds the required components, fixing the OSGI startup
    camelContext.addComponent("direct", new DirectComponent());
    camelContext.addComponent("direct-vm", new DirectVmComponent());

    // register routes
    camelContext.addRoutes(new MainRoute());
    camelContext.addRoutes(new InvokeOperationRoute());
    camelContext.addRoutes(new IsFinishedRoute());
    camelContext.addRoutes(new GetResultRoute());

    // start camel context
    camelContext.start();

    Activator.LOG.info("Application Bus started.");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    camelContext = null;

    Activator.LOG.info("Application Bus stopped.");
  }

}
