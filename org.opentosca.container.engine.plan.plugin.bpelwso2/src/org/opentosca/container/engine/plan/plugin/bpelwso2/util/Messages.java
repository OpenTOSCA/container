package org.opentosca.container.engine.plan.plugin.bpelwso2.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.opentosca.planengine.plugin.bpelwso2.util.messages";
	public static String BpsPlanEnginePlugin_description;
	public static String BpsPlanEnginePlugin_bpsAddress;
	public static String BpsPlanEnginePlugin_bpsLoginName;
	public static String BpsPlanEnginePlugin_bpsLoginPw;
	public static String BpsPlanEnginePlugin_language;
	public static String BpsPlanEnginePlugin_capabilities;
	public static String BpsPlanEnginPlugin_bpsServiceRootAddress;
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}


	private Messages() {
	}
}
