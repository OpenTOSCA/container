package org.opentosca.bus.management.plugins.remote.service.impl.typeshandler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.FileLocator;
import org.opentosca.bus.management.plugins.remote.service.impl.model.artifacttypes.Artifacttype;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Handles the config files (located in artifacttypes folder) for the different
 * supported ArtifactTypes.
 * 
 * 
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public class ArtifactTypesHandler {

	private static final String ARTIFACT_TYPES_DEFINTION_FOLDER = "/META-INF/artifacttypes";

	final private static Logger LOG = LoggerFactory.getLogger(ArtifactTypesHandler.class);

	private static HashMap<QName, Artifacttype> artifact_types = new HashMap<QName, Artifacttype>();

	/**
	 * Initially reads all ArtifactTypes config files.
	 * 
	 * @param bundleContext
	 */
	public static void init(BundleContext bundleContext) {

		ArtifactTypesHandler.LOG.debug("Registering the supported ArtifactTypes...");

		File[] types_definitions_files = null;

		URL bundleResURL = null;
		URL fileResURL = null;
		File typesFolder = null;

		try {
			bundleResURL = bundleContext.getBundle().getEntry(ARTIFACT_TYPES_DEFINTION_FOLDER);
			// convert bundle resource URL to file URL
			fileResURL = FileLocator.toFileURL(bundleResURL);
			typesFolder = new File(fileResURL.getPath());
		} catch (IOException e) {
			ArtifactTypesHandler.LOG.error("", e);
		}

		if (typesFolder == null) {
			ArtifactTypesHandler.LOG.error("Can't get ArtifactTypes configuration files.");
		}

		if (typesFolder != null && typesFolder.isDirectory()) {
			types_definitions_files = typesFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName().toLowerCase();
					return name.endsWith(".xml") && pathname.isFile();
				}
			});
		}

		if (types_definitions_files != null) {

			for (File type_defintion_file : types_definitions_files) {

				JAXBContext jaxbContext;

				try {

					jaxbContext = JAXBContext.newInstance(Artifacttype.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					Artifacttype artitacttype = (Artifacttype) jaxbUnmarshaller.unmarshal(type_defintion_file);

					String artifactTypeName = artitacttype.getName();
					String artifactTypeNamespace = artitacttype.getNamespace();

					QName artifactType = new QName(artifactTypeNamespace, artifactTypeName);

					ArtifactTypesHandler.LOG.debug("Supported ArtifactType found: {}", artifactType);

					artifact_types.put(artifactType, artitacttype);

				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			ArtifactTypesHandler.LOG.debug("No supported ArtifactTypes found.");
		}
	}

	/**
	 * Returns the required packages of the specified ArtifactType.
	 * 
	 * @param artifactType
	 * @return the required packages of the specified ArtifactType.
	 */
	public static List<String> getRequiredPackages(QName artifactType) {

		List<String> requiredPackages = new ArrayList<String>();

		if (artifact_types.containsKey(artifactType)) {
			requiredPackages = artifact_types.get(artifactType).getPackages().getPackage();
		} else {
			ArtifactTypesHandler.LOG.warn("ArtifactType: {} is not supported!", artifactType);
		}

		ArtifactTypesHandler.LOG.debug("Required packages of artifactType: {} : {}", artifactType, requiredPackages);

		return requiredPackages;
	}

	/**
	 * Returns the defined commands of the specified ArtifactType.
	 * 
	 * @param artifactType
	 * @return the defined commands of the specified ArtifactType.
	 */
	public static List<String> getCommands(QName artifactType) {

		List<String> commands = new ArrayList<String>();

		if (artifact_types.containsKey(artifactType)) {
			commands = artifact_types.get(artifactType).getCommands().getCommand();
		} else {
			ArtifactTypesHandler.LOG.warn("ArtifactType: {} is not supported!", artifactType);
		}

		ArtifactTypesHandler.LOG.debug("Commands to run for artifactType: {} : {}", artifactType, commands);

		return commands;
	}

	/**
	 * @return the supported Types of the plugin. Based on the available *.xml
	 *         files.
	 */
	public static List<QName> getSupportedTypes() {

		ArrayList<QName> supportedTypes = new ArrayList<QName>(artifact_types.keySet());

		ArtifactTypesHandler.LOG.debug("SupportedTypes: {}", supportedTypes);

		return supportedTypes;

	}

}
