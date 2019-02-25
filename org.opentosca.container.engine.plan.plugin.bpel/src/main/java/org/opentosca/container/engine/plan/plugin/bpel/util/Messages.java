package org.opentosca.container.engine.plan.plugin.bpel.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
	public static String BpelPlanEnginePlugin_engine;
	public static String BpelPlanEnginePlugin_description;
	public static String BpelPlanEnginePlugin_engineAddress;
	public static String BpelPlanEnginePlugin_engineLoginName;
	public static String BpelPlanEnginePlugin_engineLoginPw;
	public static String BpelPlanEnginePlugin_language;
	public static String BpelPlanEnginePlugin_capabilities;
	public static String BpelPlanEnginPlugin_engineServiceRootAddress;
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}
	
	
	private Messages() {
	}
}
