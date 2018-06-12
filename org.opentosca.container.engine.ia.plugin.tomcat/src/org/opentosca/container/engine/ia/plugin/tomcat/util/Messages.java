package org.opentosca.container.engine.ia.plugin.tomcat.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class to define Strings in messages.properties.
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String TomcatIAEnginePlugin_tomcatUsername;
    public static String TomcatIAEnginePlugin_tomcatPassword;
    public static String TomcatIAEnginePlugin_url;
    public static String TomcatIAEnginePlugin_types;
    public static String TomcatIAEnginePlugin_capabilities;
    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }


    private Messages() {}
}
