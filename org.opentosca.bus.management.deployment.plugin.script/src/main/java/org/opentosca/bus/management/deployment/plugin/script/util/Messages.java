package org.opentosca.bus.management.deployment.plugin.script.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.
 */
public class Messages extends NLS {

  public static String DeploymentPluginScript_types;
  public static String DeploymentPluginScript_capabilities;

  private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
