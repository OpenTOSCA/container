package org.opentosca.container.core.impl.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.opentosca.container.core.service.IFileAccessService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Store for files that are needed by the Container and its tests. Every file can be accessed by a
 * separate method.
 */
@Service
public class FileAccessServiceImpl implements IFileAccessService {

  final private static Logger LOG = LoggerFactory.getLogger(FileAccessServiceImpl.class);

  private final Bundle BUNDLE = FrameworkUtil.getBundle(FileAccessServiceImpl.class);


  /**
   * @param relFilePath
   * @return the file at the file path <code>relFilePath</code> (relative to <code>META-INF/res</code>
   * in this bundle)
   */
  private File getResource(final String relFilePath) {

    URL bundleResURL = null;
    URL fileResURL = null;
    File fileRes = null;

    try {
      bundleResURL = this.BUNDLE.getEntry("/META-INF/resources/" + relFilePath);
      // convert bundle resource URL to file URL
      fileResURL = FileLocator.toFileURL(bundleResURL);
      fileRes = new File(fileResURL.toURI());
    } catch (final Exception e) {
      LOG.error("", e);
    }

    if (fileRes == null) {
      LOG.error("Can't get file at relative path {}.", relFilePath);
    } else {
      LOG.debug("Absolute File path: {}", fileRes.getAbsolutePath());
    }

    return fileRes;
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getNotebookAppBuildPlan() {
    LOG.debug("Retrieving NotebookApp build plan");
    return this.getResource("test/notebookbuildPlan.zip");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getOpenToscaSchemaFile() {
    LOG.debug("Get the TOSCA XML schema");
    return this.getResource("TOSCA-v1.0.xsd");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getXMLSchemaDatatypesSchema() {
    LOG.debug("Get the XML schema data types schema file.");
    return this.getResource("xsds/XMLSchemaDatatypes.xsd");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getDeploymentArtifactXMLSchemaFile() {
    LOG.debug("Get the Deployment Artifact XML schema file.");
    return this.getResource("xsds/DeploymentArtifactXMLSchema.xsd");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getToscaTestXMLFileA() {
    LOG.debug("Get the TOSCA test A XML file.");
    return this.getResource("test/TOSCA-TestXML-A.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getToscaTestXMLFileB() {
    LOG.debug("Get the TOSCA test B XML file.");
    return this.getResource("test/TOSCA-TestXML-B.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getToscaTestXMLFileMergeURI() {
    LOG.debug("Get the TOSCA test merge XML file.");
    return this.getResource("test/TOSCA-TestXML-Merge.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getTHORSuperApplicationToscaFile() {
    LOG.debug("Get the Super Application TOSCA file.");
    return this.getResource("test/SuperApplicationTOSCAFile.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getTestWarImplementationArtifact() {
    LOG.debug("Get the WAR test Implementation Artifact.");
    return this.getResource("test/TestImplementationArtifact.war");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getAWSDeployer() {
    LOG.debug("Get the IA WAR-File AWSDeployer.");
    return this.getResource("test/AWSDeployer.war");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getDBCreator() {
    LOG.debug("Get the IA WAR-File DBCreator.");
    return this.getResource("test/AmazonDBCreator.war");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getTestAarImplementationArtifact() {
    LOG.debug("Get the AAR test Implementation Artifact.");
    return this.getResource("test/TestImplementationArtifact.aar");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getTestWSDLFile() {
    LOG.debug("Get the test WSDL file.");
    return this.getResource("test/wsdl/Deploy.wsdl");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getResolverTest_MainTOSCA() {
    LOG.debug("Get the Service Template Resolver test main TOSCA file.");
    return this.getResource("test/ResolverTest_MainTOSCAFile.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getResolverTest_ImportTOSCA() {
    LOG.debug("Get the Service Template Resolver test import TOSCA file.");
    return this.getResource("test/ResolverTest_ImportTOSCA.xml");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getResolverTest_ImportWSDL() {
    LOG.debug("Get the Service Template Resolver test import WSDL file.");
    return this.getResource("test/ResolverTest_ImportWSDL.wsdl");
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File getResolverThorFile() {
    LOG.debug("Getting the Service Templatr Resolver test THOR file.");
    return this.getResource("test/ResolverExampleThorFile.thor");
  }

  @Override
  public File getCSARSugarCRM3() {
    LOG.debug("Get the SugarCRM3 CSAR file.");
    return this.getResource("test/SugarCRM3.csar");
  }

  @Override
  public File getCSARSuperApplicationNotebook() {
    LOG.debug("Get the SuperApplicationNotebook CSAR file.");
    return this.getResource("test/SuperApplicationNotebook.csar");
  }

  @Override
  public File getCSARWithInvalidFileExtension() {
    LOG.debug("Get a CSAR with invalid file extension.");
    return this.getResource("test/CSARWithInvalidFileExtension.csar1");
  }

  @Override
  public File getCSARMoodle() {
    LOG.debug("Get the Moodle CSAR file.");
    return this.getResource("test/Moodle.csar");
  }

  @Override
  public File getTOSCAMetaFileInvalid1() {
    LOG.debug("Get the invalid TOSCA meta file 1.");
    return this.getResource("test/TOSCAMetaFileInvalid1.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid2() {
    LOG.debug("Get the invalid TOSCA meta file 2.");
    return this.getResource("test/TOSCAMetaFileInvalid2.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid3() {
    LOG.debug("Get the invalid TOSCA meta file 3.");
    return this.getResource("test/TOSCAMetaFileInvalid3.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid4() {
    LOG.debug("Get the invalid TOSCA meta file 4.");
    return this.getResource("test/TOSCAMetaFileInvalid4.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid5() {
    LOG.debug("Get the invalid TOSCA meta file 5.");
    return this.getResource("test/TOSCAMetaFileInvalid5.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid6() {
    LOG.debug("Get the invalid TOSCA meta file 6.");
    return this.getResource("test/TOSCAMetaFileInvalid6.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid7() {
    LOG.debug("Get the invalid TOSCA meta file 7.");
    return this.getResource("test/TOSCAMetaFileInvalid7.meta");
  }

  @Override
  public File getTOSCAMetaFileInvalid8() {
    LOG.debug("Get the invalid TOSCA meta file 8.");
    return this.getResource("test/TOSCAMetaFileInvalid8.meta");
  }

  @Override
  public File getTOSCAMetaFileValid1() {
    LOG.debug("Get the valid TOSCA meta file 1.");
    return this.getResource("test/TOSCAMetaFileValid1.meta");
  }

  @Override
  public File getTOSCAMetaFileValid2() {
    LOG.debug("Get the valid TOSCA meta file 2.");
    return this.getResource("test/TOSCAMetaFileValid2.meta");
  }

  @Override
  public File getTOSCAMetaFileValid3() {
    LOG.debug("Get the valid TOSCA meta file 3.");
    return this.getResource("test/TOSCAMetaFileValid3.meta");
  }

  @Override
  public File getTestCSAR() {
    LOG.debug("Get the Test CSAR for Core File Service test cases.");
    return this.getResource("test/TestCSAR.csar");
  }

  @Override
  public File getTestCSAR2() {
    LOG.debug("Get the Test CSAR 2 for Core File Service test cases (invalid, contains no Definitions files).");
    return this.getResource("test/TestCSAR2.csar");
  }

  @Override
  public File getStorageProviderTestFile1() {
    LOG.debug("Get a test file for storage provider test cases.");
    return this.getResource("test/Storage Provider Test File.txt");
  }

  @Override
  public File getStorageProviderTestFile2() {
    LOG.debug("Get a empty test file for storage provider test cases.");
    return this.getResource("test/StorageProviderTestFile2.war");
  }

  /**
   * -----------------------------------------------------------------
   */

  /**
   * {@inheritDoc}
   */
  @Override
  public File getTemp() {
    Path tempDir;
    do {
      tempDir = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + System.nanoTime());
    } while (Files.exists(tempDir));

    try {
      Files.createDirectories(tempDir);
    } catch (final IOException exc) {
      LOG.warn("An IO Exception occured.", exc);
      return null;
    }
    return tempDir.toFile();
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File unpackToTemp(final File zipFile) {
    final File tempDir = this.getTemp();
    ZipManager.getInstance().unzip(zipFile, tempDir);
    return tempDir;
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public File zip(final File directory, final File archive) {
    return ZipManager.getInstance().zip(directory, archive);
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public List<File> unzip(final File file, final File toTarget) {
    return ZipManager.getInstance().unzip(file, toTarget);
  }

}
