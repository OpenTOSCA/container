package org.opentosca.iaengine.plugins.wartomcat.service.impl.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.<br>
 * <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart<br>
 * <br>
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.opentosca.iaengine.plugins.wartomcat.service.impl.util.messages"; //$NON-NLS-1$
	public static String TomcatIAEnginePlugin_tomcatUsername;
	public static String TomcatIAEnginePlugin_tomcatPassword;
	public static String TomcatIAEnginePlugin_url;
	public static String TomcatIAEnginePlugin_types;
	public static String TomcatIAEnginePlugin_capabilities;
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}
	
	
	private Messages() {
	}
}
