package org.opentosca.bus.management.invocation.plugin.rest.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart<br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class Messages extends NLS {

  public static String RestSIEnginePlugin_types;

  private static final String BUNDLE_NAME = "org.opentosca.bus.management.invocation.plugin.rest.util.messages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
  }


  private Messages() {
  }
}
