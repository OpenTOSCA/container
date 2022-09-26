package org.opentosca.container.core.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.LoggerFactory;

/**
 * Global OpenTOSCA Settings.
 */
public class Settings {

    // TODO: Use public static final variables instead, as in StaticTOSCANamespaces.
    //  The problems with the current approach is:
    //  (i) Full-text search to find usage instead of Java Reference Search.
    //  (ii) It is possible to references non-existing settings, which is not possible with static variables which are checked on compile time.

    private static final Properties settings = new Properties();

    static {
        // Initialize settings with defaults we know
        try {
            settings.load(Settings.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            LoggerFactory.getLogger(Settings.class).error("Could not load defaults shipped with the application", e);
        }
    }

    public final static String OPENTOSCA_CONTAINER_HOSTNAME = settings.getProperty("org.opentosca.container.hostname", "localhost");
    public final static String OPENTOSCA_CONTAINER_PORT = settings.getProperty("org.opentosca.container.port", "1337");

    public final static String CONTAINER_API = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT;
    public final static String CONTAINER_INSTANCEDATA_API = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/csars/{csarid}/servicetemplates/{servicetemplateid}/instances";
    public final static String OPENTOSCA_CONTAINER_CONTENT_API = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/csars/{csarid}/content/";
    public final static String OPENTOSCA_CONTAINER_CONTENT_API_ARTIFACTREFERENCE = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/csars/{csarid}/content/{artifactreference}";

    public final static String ENGINE_PLAN_BPMN_ENGINE = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpmn.engine", "Camunda");
    public final static String ENGINE_PLAN_BPMN_URL = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpmn.url", "http://localhost:8092/engine-rest");
    public final static String ENGINE_PLAN_BPMN_USERNAME = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpmn.username", "admin");
    public final static String ENGINE_PLAN_BPMN_PASSWORD = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpmn.password", "admin");

    public final static String ENGINE_PLAN_BPEL_ENGINE = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpel.engine", "ODE");
    public final static String ENGINE_PLAN_BPEL_URL = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpel.url", "http://localhost:9763/ode");
    public final static String ENGINE_PLAN_BPEL_URL_SERVICES = settings.getProperty("org.opentosca.container.engine.plan.plugin.bpel.services.url", "http://localhost:9763/ode/processes");

    public final static String ENGINE_IA_TOMCAT_URL = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.url", "http://localhost:8090");
    public final static String ENGINE_IA_TOMCAT_USERNAME = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.username", "admin");
    public final static String ENGINE_IA_TOMCAT_PASSWORD = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.password", "admin");
    public final static String ENGINE_IA_TOMCAT_JAVA17_URL = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.java17.url", "http://localhost:8093");
    public final static String ENGINE_IA_TOMCAT_JAVA17_USERNAME = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.java17.username", "admin");
    public final static String ENGINE_IA_TOMCAT_JAVA17_PASSWORD = settings.getProperty("org.opentosca.container.engine.ia.plugin.tomcat.java17.password", "admin");
    public final static String OPENTOSCA_ENGINE_IA_KEEPFILES = settings.getProperty("org.opentosca.engine.ia.keepfiles", "false");

    public final static String PERSISTENCE_UNIT_NAME = "OpenTOSCA";

    public final static String OPENTOSCA_DEPLOYMENT_TESTS = settings.getProperty("org.opentosca.deployment.checks", "false");
    public final static String OPENTOSCA_BUS_MANAGEMENT_MOCK = settings.getProperty("org.opentosca.bus.management.mocking", "false");
    public final static String OPENTOSCA_TEST_LOCAL_REPOSITORY_PATH = settings.getProperty("org.opentosca.test.local.repository.path");
    public final static String OPENTOSCA_TEST_REMOTE_REPOSITORY_URL = settings.getProperty("org.opentosca.test.remote.repository.url");
    public final static Path CONTAINER_STORAGE_BASEPATH = Paths.get(System.getProperty("java.io.tmpdir"), "opentosca", "container", "csar-storage");

    /**
     * OpenTOSCA Container database location
     */
    public static final Path DBDIR = Paths.get(System.getProperty("java.io.tmpdir"), "opentosca", "db");

    public final static String OPENTOSCA_COLLABORATION_MODE = settings.getProperty("org.opentosca.container.collaboration.mode", "false");
    public final static String OPENTOSCA_COLLABORATION_HOSTNAMES = settings.getProperty("org.opentosca.container.collaboration.hostnames");
    public final static String OPENTOSCA_COLLABORATION_PORTS = settings.getProperty("org.opentosca.container.collaboration.ports");

    public final static String OPENTOSCA_BROKER_MQTT_PORT = settings.getProperty("org.opentosca.container.broker.mqtt.port", "1883");
    public final static String OPENTOSCA_BROKER_MQTT_USERNAME = settings.getProperty("org.opentosca.container.broker.mqtt.username", "admin");
    public final static String OPENTOSCA_BROKER_MQTT_PASSWORD = settings.getProperty("org.opentosca.container.broker.mqtt.password", "admin");
    // Container Capabilities
    public final static String containerCapabilities = "http://opentosca/planportabilityapi/rest, http://opentosca/containerapi";
    public static final String TOSCA_META_FILE_REL_PATH = "TOSCA-Metadata" + File.separator + "TOSCA.meta";
    // enable bpmn / bpel build plans
    public static final String BUILD_PLANLANGUAGE = settings.getProperty("org.opentosca.planbuilder.buildplan.language", "BPEL");
    /**
     * @param setting - name of the setting
     * @return the value of setting with name <code>setting</code>
     */
    public static String getSetting(final String setting) {
        return settings.getProperty(setting);
    }

    /**
     * Retrieves a setting value, or a supplied default value if the setting is unknown
     *
     * @param setting      Name of the setting
     * @param defaultValue A default value to use if the setting has not been set.
     * @return The value of the setting with name <code>setting</code> or the default value if the setting was unknown.
     */
    public static String getSetting(final String setting, final String defaultValue) {
        return settings.getProperty(setting, defaultValue);
    }

    /**
     * Stores a setting.
     *
     * @param setting - name of the setting
     * @param value   - value of the setting
     */
    public static void setSetting(final String setting, final String value) {
        settings.setProperty(setting, value);
    }
}
