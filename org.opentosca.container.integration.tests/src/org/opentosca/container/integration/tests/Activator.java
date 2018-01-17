package org.opentosca.container.integration.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  private static BundleContext context;

  // Define your test CSARs here
  private static String[] testCsars = {};

  static BundleContext getContext() {
    return context;
  }

  public void start(BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;

    // Upload test CSARs if required
    for (String csar : testCsars) {
      if (!CsarActions.hasCsar(csar)) {
        CsarActions.uploadCsar(TestingUtil.pathToURL(csar));
      }
    }
  }

  public void stop(BundleContext bundleContext) throws Exception {
    Activator.context = null;

    // Clean-up test CSARs
    for (String csar : testCsars) {
      if (!CsarActions.hasCsar(csar)) {
        CsarActions.removeCsar(csar);
      }
    }
  }
}
