package org.opentosca.container.engine.ia.plugin.aar.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.opentosca.iaengine.plugins.aaraxis.service.impl.util.messages"; //$NON-NLS-1$
	public static String AarAxisIAEnginePlugin_axisUsername;
	public static String AarAxisIAEnginePlugin_axisPassword;
	public static String AarAxisIAEnginePlugin_url;
	public static String AarAxisIAEnginePlugin_types;
	public static String AarAxisIAEnginePlugin_capabilities;

	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}


	private Messages() {
	}
}
