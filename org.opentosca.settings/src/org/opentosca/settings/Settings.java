package org.opentosca.settings;

import java.io.File;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global OpenTOSCA Settings.
 *
 */
public class Settings implements BundleActivator {

	public final static String OPENTOSCA_CONTAINER_HOSTNAME = System.getProperty("org.opentosca.container.hostname", "localhost");
	public final static String OPENTOSCA_CONTAINER_PORT = System.getProperty("org.opentosca.container.port", "1337");

	public final static String CONTAINER_API = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/containerapi";
	public final static String CONTAINER_INSTANCEDATA_API = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/containerapi/CSARs/{csarid}/ServiceTemplates/{servicetemplateid}/Instances/";

	private static Logger logger = LoggerFactory.getLogger(Settings.class);

	// TODO: Use public static final variables instead, as in
	// StaticTOSCANamespaces. The problems with the current approach is: (i)
	// Full-text search to find usage instead of Java Reference Search. (ii) It
	// is possible to references non-existing settings, which is not possible
	// with static variables which are checked on compile time.

	private static Properties settings = new Properties();
	private static BundleContext context;

	// Container Capabilities
	private final static String containerCapabilities = "http://opentosca/planportabilityapi/rest, http://opentosca/containerapi";


	static BundleContext getContext() {
		return Settings.context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Settings.context = bundleContext;

		Settings.logger.info("org.opentosca.container.hostname={}", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
		Settings.logger.info("org.opentosca.container.port={}", Settings.OPENTOSCA_CONTAINER_PORT);

		// /////////////////// PATHS ///////////////////

		// contains data of OpenTOSCA that should be stored permanently
		String openTOSCAPath = "";
		if (System.getProperty("openTOSCAPath") == null) {
			openTOSCAPath = System.getProperty("java.io.tmpdir") + File.separator + "openTOSCA";
		} else {
			openTOSCAPath = System.getProperty("openTOSCAPath") + File.separator + "openTOSCA";
		}

		// contains data of OpenTOSCA that should be stored temporarily
		Settings.setSetting("temp", openTOSCAPath + File.separator + "Temp");

		// Derby database location
		Settings.setSetting("databaseLocation", openTOSCAPath + File.separator + "DB");

		// relative path where CSARs will be stored locally; used by the
		// Filesystem storage provider
		Settings.setSetting("csarStorePath", openTOSCAPath + File.separator + "CSARs");

		// /////////////////// URLS ///////////////////

		if (System.getProperty("openTOSCAWineryPath") == null) {
			//Settings.setSetting("openTOSCAWineryPath", "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8080/winery");
			Settings.setSetting("openTOSCAWineryPath", "http://192.168.99.100:8080/winery");
		} else {
			Settings.setSetting("openTOSCAWineryPath", System.getProperty("openTOSCAWineryPath"));
		}

		// URI of the ContainerAPI
		Settings.setSetting("containerUri", Settings.CONTAINER_API);

		// URI of the DataInstanceAPI
		Settings.setSetting("datainstanceUri", "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":" + Settings.OPENTOSCA_CONTAINER_PORT + "/datainstance");

		// /////////////////// CSAR ///////////////////

		// extension of a CSAR file
		Settings.setSetting("csarExtension", "csar");

		// relative path of IMPORTS directory in a CSAR file
		Settings.setSetting("csarImportsRelPath", "IMPORTS");

		// relative path of Definitions directory in a CSAR file
		Settings.setSetting("csarDefinitionsRelPath", "Definitions");

		// relative path where the TOSCA meta file is located in a CSAR file
		Settings.setSetting("toscaMetaFileRelPath", "TOSCA-Metadata" + File.separator + "TOSCA.meta");

		// possible file extensions of a TOSCA file, separated by character ";"
		Settings.setSetting("toscaFileExtensions", "xml;tosca;ste");

		// /////////////////// OTHERS ///////////////////

		// Container Capabilities
		Settings.setSetting("containerCapabilities", Settings.containerCapabilities);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Settings.context = null;
	}

	/**
	 * @param setting - name of the setting
	 * @return the value of setting with name <code>setting</code>
	 */
	public static String getSetting(String setting) {
		return Settings.settings.getProperty(setting);
	}

	/**
	 * Stores a setting.
	 *
	 * @param setting - name of the setting
	 * @param value - value of the setting
	 */
	public static void setSetting(String setting, String value) {
		Settings.settings.setProperty(setting, value);
	}
}
