package org.opentosca.container.engine.plan.plugin.camunda.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  public static String CamundaPlanEnginePlugin_description;
  public static String CamundaPlanEnginePlugin_CamundaAddress;
  public static String CamundaPlanEnginePlugin_CamundaLoginName;
  public static String CamundaPlanEnginePlugin_CamundaLoginPw;
  public static String CamundaPlanEnginePlugin_language;
  public static String CamundaPlanEnginePlugin_capabilities;

  private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

  static {
    // initialize resource bundle
    NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
  }


  private Messages() {
  }
}
