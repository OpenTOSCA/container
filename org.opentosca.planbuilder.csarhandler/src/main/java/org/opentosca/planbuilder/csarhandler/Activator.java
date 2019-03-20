package org.opentosca.planbuilder.csarhandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Activator implements BundleActivator {

  protected static BundleContext bundleContext;

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
   */
  @Override
  public void start(final BundleContext arg0) throws Exception {
    if (Activator.bundleContext == null) {
      Activator.bundleContext = arg0;
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext arg0) throws Exception {
    if (Activator.bundleContext.equals(arg0)) {
      Activator.bundleContext = null;
    }
  }

}
