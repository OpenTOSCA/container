package org.opentosca.container.integration.tests;

import org.opentosca.deployment.tests.TestExecutorTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceTrackerUtil {

  private static Logger logger = LoggerFactory.getLogger(ServiceTrackerUtil.class);

  public static <T> T getService(final Class<T> clazz) {
    final Bundle bundle = FrameworkUtil.getBundle(TestExecutorTest.class);
    if (bundle == null) {
      logger.warn("Could not resolve bundle for class {}, returning null...", clazz);
      return null;
    }
    final ServiceTracker<T, T> st =
        new ServiceTracker<T, T>(bundle.getBundleContext(), clazz, null);
    st.open();
    try {
      return st.waitForService(500); // Give the runtime some time to startup
    } catch (InterruptedException e) {
      logger.error("Error getting instance of class {}: {}", clazz.getName(), e.getMessage());
    }
    return null;
  }

  private ServiceTrackerUtil() {
    throw new UnsupportedOperationException();
  }
}
