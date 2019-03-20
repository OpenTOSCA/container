package org.opentosca.container.core.service;

import java.io.File;
import java.util.List;

/**
 * Interface that provides methods to access files stored in this File Access Service.
 */
// FIXME: This is a testclass with some legit usecases splattered across the codebase
//  it needs to be spliced into the testclass and the class with legit usecases.
//  For now we deprecated it to replace it with two separate better named classes for the separate purposes. 
@Deprecated
public interface IFileAccessService {

  /**
   * @return file object of the build plan of SuperAppNotebook
   */
  public File getNotebookAppBuildPlan();

  /**
   * @return file object of the TOSCA schema v.1.0-cs02
   */
  public File getOpenToscaSchemaFile();

  /**
   * @return file object of the XLink annotated Deployment Artifact schema
   */
  public File getDeploymentArtifactXMLSchemaFile();

  /**
   * @return file object of the TOSCA test A XML
   */
  public File getToscaTestXMLFileA();

  /**
   * @return file object of the TOSCA test B XML
   */
  public File getToscaTestXMLFileB();

  /**
   * @return file object of the TOSCA test merge XML
   */
  public File getToscaTestXMLFileMergeURI();

  /**
   * @return file object of the test WAR Implementation Artifact
   */
  public File getTestWarImplementationArtifact();

  /**
   * @return IA WAR-File AWSDeployer
   */
  public File getAWSDeployer();

  /**
   * @return IA WAR-File DBCreator
   */
  public File getDBCreator();

  /**
   * @return file object of the test AAR Implementation Artifact
   */
  public File getTestAarImplementationArtifact();

  /**
   * @return file object of the test WSDL
   */
  public File getTestWSDLFile();

  /**
   * @return file object of the resolver test main TOSCA
   */
  public File getResolverTest_MainTOSCA();

  /**
   * @return file object of the resolver test import TOSCA
   */
  public File getResolverTest_ImportTOSCA();

  /**
   * @return file object of the resolver test import WSDL
   */
  public File getResolverTest_ImportWSDL();

  /**
   * @return file object of the resolver test THOR
   */
  public File getResolverThorFile();

  /**
   * @return file object of the XML schema data types schema
   */
  public File getXMLSchemaDatatypesSchema();

  /**
   * @return file object of the Super Application main TOSCA
   */
  public File getTHORSuperApplicationToscaFile();

  /**
   * @return File object of the SugarCRM3 CSAR file.
   */
  public File getCSARSugarCRM3();

  /**
   * @return File object of a CSAR file with invalid file extension.
   */
  public File getCSARWithInvalidFileExtension();

  /**
   * @return File object of the SuperApplicationNotebook CSAR file.
   */
  public File getCSARSuperApplicationNotebook();

  /**
   * @return File object of the Moodle CSAR file.
   */
  public File getCSARMoodle();

  /**
   * @return File object of a invalid TOSCA meta file (meta file version is not "1.0").
   */
  public File getTOSCAMetaFileInvalid1();

  /**
   * @return File object of a invalid TOSCA meta file (CSAR version is empty, Created by is missing).
   */
  public File getTOSCAMetaFileInvalid2();

  /**
   * @return File object of a invalid TOSCA meta file (Created by is empty).
   */
  public File getTOSCAMetaFileInvalid3();

  /**
   * @return File object of a invalid TOSCA meta file (Description is defined, but is missing).
   */
  public File getTOSCAMetaFileInvalid4();

  /**
   * @return File object of a invalid TOSCA meta file (Topology is defined, but is empty).
   */
  public File getTOSCAMetaFileInvalid5();

  /**
   * @return File object of a invalid TOSCA meta file (Entry Definitions is defined, but is empty).
   */
  public File getTOSCAMetaFileInvalid6();

  /**
   * @return File object of a invalid TOSCA meta file (Content-Type in file block is invalid).
   */
  public File getTOSCAMetaFileInvalid7();

  /**
   * @return File object of a invalid TOSCA meta file (Content-Type in file block is missing).
   */
  public File getTOSCAMetaFileInvalid8();

  /**
   * @return File object of a valid TOSCA meta file.
   */
  public File getTOSCAMetaFileValid1();

  /**
   * @return File object of a valid TOSCA meta file (Topology is not defined).
   */
  public File getTOSCAMetaFileValid2();

  /**
   * @return File object of a valid TOSCA meta file (with additional attribute in block 0 and 2 file
   * blocks).
   */
  public File getTOSCAMetaFileValid3();

  /**
   * @return Test CSAR for Core File Service test cases
   */
  public File getTestCSAR();

  /**
   * @return Test CSAR for Core File Service test cases (contains no Definitions).
   */
  public File getTestCSAR2();

  /**
   * @return Test file for storage provider test cases
   */
  public File getStorageProviderTestFile1();

  /**
   * @return Empty test file for storage provider test cases
   */
  public File getStorageProviderTestFile2();

  /**
   * ----------------------------------------------------------------
   */

  /**
   * @return a created Temp directory for storing files temporarily
   */
  public File getTemp();

  /**
   * Unpacks <code>zipFile</code> to a created Temp directory.
   *
   * @param zipFile to unpack
   * @return the created Temp directory containing the unpacked files
   */
  public File unpackToTemp(File zipFile);

  /**
   * Creates a new ZIP archive containing the contents of supplied <code>directory</code>.<br />
   * Existing archives with the same name will be overwritten.
   *
   * @param directory - absolute path to the directory that content (including sub directories) should
   *                  be zipped
   * @param archive   - absolute path to the ZIP archive that should be created
   */
  public File zip(File directory, File archive);

  /**
   * Unpacks the ZIP archive <code>archive</code> to <code>target</code>.
   *
   * @param archive - absolute path to the ZIP archive
   * @param target  - directory where the content of the archive should be unpacked
   * @return a list of files that were unpacked
   */
  public List<File> unzip(File archive, File target);

}
