package org.opentosca.settings;

import java.io.File;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Global OpenTOSCA Settings.
 * 
 */
public class Settings implements BundleActivator {
	
	public final static String CONTAINER_API = "http://localhost:1337/containerapi";
	
	
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
		
		// /////////////////// PATHS ///////////////////
		
		// contains data of OpenTOSCA that should be stored permanently
		String openTOSCAPath = System.getProperty("java.io.tmpdir") + File.separator + "openTOSCA";
		
		// contains data of OpenTOSCA that should be stored temporarily
		Settings.setSetting("temp", openTOSCAPath + File.separator + "Temp");
		
		// Derby database location
		Settings.setSetting("databaseLocation", openTOSCAPath + File.separator + "DB");
		
		// relative path where CSARs will be stored locally; used by the
		// Filesystem storage provider
		Settings.setSetting("csarStorePath", openTOSCAPath + File.separator + "CSARs");
		
		// /////////////////// URLS ///////////////////
		
		// URI of the ContainerAPI
		Settings.setSetting("containerUri", CONTAINER_API);
		
		// URI of the DataInstanceAPI
		Settings.setSetting("datainstanceUri", "http://localhost:1337/datainstance");
		
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
