package org.opentosca.bus.management.plugins.rest.service.impl.util;

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
	
	private static final String BUNDLE_NAME = "org.opentosca.bus.management.plugins.rest.service.impl.util.messages"; //$NON-NLS-1$
	public static String RestSIEnginePlugin_types;
	static {
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}
	
	
	private Messages() {
	}
}
