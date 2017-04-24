package org.opentosca.container.engine.plan.plugin.camunda.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.opentosca.planengine.plugin.camunda.service.impl.util.messages";
	public static String CamundaPlanEnginePlugin_description;
	public static String CamundaPlanEnginePlugin_CamundaAddress;
	public static String CamundaPlanEnginePlugin_CamundaLoginName;
	public static String CamundaPlanEnginePlugin_CamundaLoginPw;
	public static String CamundaPlanEnginePlugin_language;
	public static String CamundaPlanEnginePlugin_capabilities;

	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}


	private Messages() {
	}
}
