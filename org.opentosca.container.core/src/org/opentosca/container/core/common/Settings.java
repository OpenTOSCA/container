package org.opentosca.container.core.common;

import java.util.Properties;

/**
 * Global OpenTOSCA Settings.
 */
public class Settings {

    public final static String OPENTOSCA_CONTAINER_HOSTNAME =
        System.getProperty("org.opentosca.container.hostname", "localhost");
    public final static String OPENTOSCA_CONTAINER_PORT = System.getProperty("org.opentosca.container.port", "1337");

    public final static String OPENTOSCA_PUBLIC_IP = System.getProperty("org.opentosca.container.engine.ia.publicip");

    public final static String CONTAINER_API =
        "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/containerapi";
    public final static String CONTAINER_INSTANCEDATA_API =
        "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT
            + "/containerapi/CSARs/{csarid}/ServiceTemplates/{servicetemplateid}/Instances/";

    public final static String PERSISTENCE_UNIT_NAME = "OpenTOSCA";

    public final static String OPENTOSCA_DEPLOYMENT_TESTS =
        System.getProperty("org.opentosca.deployment.tests", "false");


    // TODO: Use public static final variables instead, as in
    // StaticTOSCANamespaces. The problems with the current approach is: (i)
    // Full-text search to find usage instead of Java Reference Search. (ii) It
    // is possible to references non-existing settings, which is not possible
    // with static variables which are checked on compile time.

    private static Properties settings = new Properties();

    // Container Capabilities
    public final static String containerCapabilities =
        "http://opentosca/planportabilityapi/rest, http://opentosca/containerapi";


    /**
     * @param setting - name of the setting
     * @return the value of setting with name <code>setting</code>
     */
    public static String getSetting(final String setting) {
        return Settings.settings.getProperty(setting);
    }

    /**
     * Stores a setting.
     *
     * @param setting - name of the setting
     * @param value - value of the setting
     */
    public static void setSetting(final String setting, final String value) {
        Settings.settings.setProperty(setting, value);
    }
}
