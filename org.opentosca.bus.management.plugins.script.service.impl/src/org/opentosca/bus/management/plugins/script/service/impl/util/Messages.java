package org.opentosca.bus.management.plugins.script.service.impl.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart<br>
 * <br>
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.opentosca.bus.management.plugins.script.service.impl.util.messages"; //$NON-NLS-1$
	public static String ScriptSIEnginePlugin_types;
	public static String ScriptSIEnginePlugin_hosted_on_namespace;
	public static String ScriptSIEnginePlugin_hosted_on_localpart;
	public static String ScriptSIEnginePlugin_address;
	public static String ScriptSIEnginePlugin_user;
	public static String ScriptSIEnginePlugin_key;
	public static String ScriptSIEnginePlugin_script_invoker_uri;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}
	
	
	private Messages() {
	}
}
