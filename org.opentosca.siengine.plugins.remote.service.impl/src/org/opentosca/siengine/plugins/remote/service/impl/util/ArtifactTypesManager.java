package org.opentosca.siengine.plugins.remote.service.impl.util;

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
import org.opentosca.siengine.plugins.remote.service.impl.artifacttypes.Artifacttype;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactTypesManager {

	private static final String ARTIFACT_TYPES_DEFINTION_FOLDER = "artifacttypes";

	final private static Logger LOG = LoggerFactory.getLogger(ArtifactTypesManager.class);

	private static HashMap<QName, Artifacttype> artifact_types = new HashMap<QName, Artifacttype>();

	public static void init(BundleContext bundleContext) {

		ArtifactTypesManager.LOG.debug("Registering the supported ArtifactTypes...");

		File[] types_definitions_files = null;

		URL configURL = bundleContext.getBundle().getResource(ARTIFACT_TYPES_DEFINTION_FOLDER);
		File folder = null;
		try {
			folder = new File(FileLocator.toFileURL(configURL).getPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (folder != null && folder.isDirectory()) {
			types_definitions_files = folder.listFiles(new FileFilter() {
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

					ArtifactTypesManager.LOG.debug("Supported ArtifactType found: {}", artifactType);

					artifact_types.put(artifactType, artitacttype);

				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			ArtifactTypesManager.LOG.debug("No supported ArtifactTypes found.");
		}
	}

	public static List<String> getRequiredPackages(QName artifactType) {

		if (artifact_types.containsKey(artifactType)) {
			return artifact_types.get(artifactType).getPackages().getPackage();
		}

		return null;
	}

	public static List<String> getCommands(QName artifactType) {

		if (artifact_types.containsKey(artifactType)) {
			return artifact_types.get(artifactType).getCommands().getCommand();
		}

		return null;
	}

	public static List<QName> getSupportedTypes() {

		ArrayList<QName> supportedTypes = new ArrayList<QName>(artifact_types.keySet());

		ArtifactTypesManager.LOG.debug("SupportedTypes: {}", supportedTypes);

		return supportedTypes;

	}

}
