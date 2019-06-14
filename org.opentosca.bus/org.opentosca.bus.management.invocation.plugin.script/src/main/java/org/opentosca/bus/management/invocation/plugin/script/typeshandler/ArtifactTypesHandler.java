package org.opentosca.bus.management.invocation.plugin.script.typeshandler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.opentosca.bus.management.invocation.plugin.script.model.artifacttypes.Artifacttype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles the config files (located in artifacttypes folder) for the different supported
 * ArtifactTypes.
 *
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 */
@Service
@Singleton
public class ArtifactTypesHandler {
  private static final String ARTIFACT_TYPES_DEFINTION_FOLDER = "artifacttypes";
  private static final Logger LOG = LoggerFactory.getLogger(ArtifactTypesHandler.class);

  private final Map<QName, Artifacttype> artifactTypes = new HashMap<>();

  public ArtifactTypesHandler() {
    LOG.debug("Registering the supported ArtifactTypes...");
    try {
      URL artifactTypeFolder = getClass().getClassLoader().getResource(ARTIFACT_TYPES_DEFINTION_FOLDER);
      LOG.info("Artifact Type Folder is: {}", artifactTypeFolder.toString());
      if (artifactTypeFolder.getProtocol().startsWith("jar")) {
        // split resolved jar-URL into jarfile and entry path
        String[] parts = artifactTypeFolder.toString().split("!");
        assert (parts.length == 2);
        try (FileSystem jarRelativeFileSystem = FileSystems.newFileSystem(URI.create(parts[0]),Collections.emptyMap())) {
          Path typesFolder = jarRelativeFileSystem.getPath(parts[1]);
          readArtifactTypes(typesFolder);
        } catch (IOException e) {
          LOG.error("Failed to create filesystem for jar file {} to read Artifact Type definitions", artifactTypeFolder, e);
          return;
        }
      } else {
        readArtifactTypes(Paths.get(artifactTypeFolder.toURI()));
      }
    } catch (final URISyntaxException e) {
      LOG.error("Failed to transform resource URL to File reference", e);
      // Do not under any circumstances blow up the containing JVM by throwing something here
      return;
    }
    LOG.info("Registered {} Artifact Types", artifactTypes.size());
  }

  private void readArtifactTypes(Path baseDirectory) {
    Path[] xmlFiles;
    try {
      xmlFiles = Files.find(baseDirectory, 1, (path, attrs) -> path.getFileName().toString().endsWith(".xml")).toArray(Path[]::new);
    } catch (IOException e) {
      LOG.warn("Failed to iterate artifact type containers", e);
      return;
    }

    if (xmlFiles.length == 0) {
      LOG.debug("No supported ArtifactTypes found.");
      return;
    }

    final JAXBContext jaxbContext;
    final Unmarshaller jaxbUnmarshaller;
    try {
      jaxbContext = JAXBContext.newInstance(Artifacttype.class);
      jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    } catch (final JAXBException e) {
      LOG.error("Coule not create JAXBContext for Artifacttype deserialization", e);
      return;
    }
    for (final Path typeDefinition : xmlFiles) {
      final Artifacttype artifactType;
      try (InputStream is = Files.newInputStream(typeDefinition)){
        artifactType = (Artifacttype) jaxbUnmarshaller.unmarshal(is);
      } catch (JAXBException e) {
        LOG.warn("Failed to deserialize type definition {} with JAXBException", typeDefinition, e);
        continue;
      } catch (IOException e) {
        LOG.warn("Failed to read typeDefinition {} with IOException", typeDefinition, e);
        continue;
      }
      final String artifactTypeName = artifactType.getName();
      final String artifactTypeNamespace = artifactType.getNamespace();
      final QName qName = new QName(artifactTypeNamespace, artifactTypeName);

      LOG.debug("Supported ArtifactType found: {}", artifactType);
      artifactTypes.put(qName, artifactType);
    }
  }


  /**
   * Returns the required packages of the specified ArtifactType.
   *
   * @param artifactType
   * @return the required packages of the specified ArtifactType.
   */
  public List<String> getRequiredPackages(final QName artifactType) {
    if (!artifactTypes.containsKey(artifactType)) {
      LOG.warn("ArtifactType: {} is not supported!", artifactType);
      return Collections.emptyList();
    }
    List<String> requiredPackages = artifactTypes.get(artifactType).getPackages().getPackage();
    LOG.debug("Required packages of artifactType: {} : {}", artifactType, requiredPackages);
    return requiredPackages;
  }

  /**
   * Returns the defined commands of the specified ArtifactType.
   *
   * @param artifactType
   * @return the defined commands of the specified ArtifactType.
   */
  public List<String> getCommands(final QName artifactType) {
    if (!artifactTypes.containsKey(artifactType)) {
      LOG.warn("ArtifactType: {} is not supported!", artifactType);
      return Collections.emptyList();
    }
    List<String> commands = artifactTypes.get(artifactType).getCommands().getCommand();
    LOG.debug("Commands to run for artifactType: {} : {}", artifactType, commands);
    return commands;
  }

  /**
   * @return the supported Types of the plugin. Based on the available *.xml files.
   */
  public List<QName> getSupportedTypes() {
    final ArrayList<QName> supportedTypes = new ArrayList<>(artifactTypes.keySet());
    LOG.debug("SupportedTypes: {}", supportedTypes);
    return supportedTypes;
  }
}
