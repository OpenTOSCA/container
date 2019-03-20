package org.opentosca.container.engine.plan.plugin.camunda.iaenginecopies;

import org.eclipse.osgi.util.NLS;

/**
 * !!!!!!!!!!!!!!!!!!!!! This is a dirty copy of code of the IAEngine! Instructed by Uwe.
 * !!!!!!!!!!!!!!!!!!!!!
 * <p>
 * Utility class to define Strings in messages.properties.<br>
 * <br>
 * <p>
 * Copyright 2012 IAAS University of Stuttgart<br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class CopyOfMessages extends NLS {

  public static String TomcatIAEnginePlugin_tomcatUsername;
  public static String TomcatIAEnginePlugin_tomcatPassword;
  public static String TomcatIAEnginePlugin_url;
  public static String TomcatIAEnginePlugin_types;
  public static String TomcatIAEnginePlugin_capabilities;

  private static final String BUNDLE_NAME =
    "org.opentosca.planengine.plugin.camunda.service.impl.iaenginecopies.CopyOfmessages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(CopyOfMessages.BUNDLE_NAME, CopyOfMessages.class);
  }


  private CopyOfMessages() {
  }
}
