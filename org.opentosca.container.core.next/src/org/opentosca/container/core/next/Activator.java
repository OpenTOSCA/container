package org.opentosca.container.core.next;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

  private static BundleContext context;

  private static Logger logger = LoggerFactory.getLogger(Activator.class);


  static BundleContext getContext() {
    return context;
  }

  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    logger.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());
    context = bundleContext;
  }

  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());
    context = null;
  }
}
