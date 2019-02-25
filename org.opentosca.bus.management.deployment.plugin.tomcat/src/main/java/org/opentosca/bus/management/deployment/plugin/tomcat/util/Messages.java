package org.opentosca.bus.management.deployment.plugin.tomcat.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
    public static String DeploymentPluginTomcat_types;
    public static String DeploymentPluginTomcat_capabilities;
    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
