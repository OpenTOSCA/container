package org.opentosca.container.engine.ia.plugin.script.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.opentosca.iaengine.plugins.script.service.impl.util.messages"; //$NON-NLS-1$
	public static String ScriptIAEnginePlugin_types;
	public static String ScriptIAEnginePlugin_capabilities;
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}


	private Messages() {
	}
}
