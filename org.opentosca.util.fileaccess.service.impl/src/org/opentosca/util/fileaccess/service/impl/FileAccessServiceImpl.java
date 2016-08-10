package org.opentosca.util.fileaccess.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.opentosca.settings.Settings;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.opentosca.util.fileaccess.service.impl.zip.ZipManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store for files that are needed by the Container and its tests.<br />
 * Every file can be accessed by a separate method.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
public class FileAccessServiceImpl implements IFileAccessService {
	
	final private static Logger LOG = LoggerFactory.getLogger(FileAccessServiceImpl.class);
	
	private final Bundle BUNDLE = FrameworkUtil.getBundle(FileAccessServiceImpl.class);
	
	
	/**
	 * @param relFilePath
	 * @return the file at the file path <code>relFilePath</code> (relative to
	 *         <code>META-INF/res</code> in this bundle)
	 * 
	 */
	private File getResource(String relFilePath) {
		
		URL bundleResURL = null;
		URL fileResURL = null;
		File fileRes = null;
		
		try {
			bundleResURL = this.BUNDLE.getEntry("/META-INF/res/" + relFilePath);
			// convert bundle resource URL to file URL
			fileResURL = FileLocator.toFileURL(bundleResURL);
			fileRes = new File(fileResURL.getPath());
		} catch (IOException e) {
			FileAccessServiceImpl.LOG.error("", e);
		}
		
		if (fileRes == null) {
			FileAccessServiceImpl.LOG.error("Can't get file at relative path {}.", relFilePath);
		} else {
			FileAccessServiceImpl.LOG.debug("Absolute File path: {}", fileRes.getPath());
		}
		
		return fileRes;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getNotebookAppBuildPlan() {
		FileAccessServiceImpl.LOG.debug("Retrieving NotebookApp build plan");
		return this.getResource("test/notebookbuildPlan.zip");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getOpenToscaSchemaFile() {
		FileAccessServiceImpl.LOG.debug("Get the TOSCA XML schema v1.0-cs02.");
		return this.getResource("xsds/TOSCA-v1.0-cs02.xsd");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getXMLSchemaDatatypesSchema() {
		FileAccessServiceImpl.LOG.debug("Get the XML schema data types schema file.");
		return this.getResource("xsds/XMLSchemaDatatypes.xsd");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getDeploymentArtifactXMLSchemaFile() {
		FileAccessServiceImpl.LOG.debug("Get the Deployment Artifact XML schema file.");
		return this.getResource("xsds/DeploymentArtifactXMLSchema.xsd");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getToscaTestXMLFileA() {
		FileAccessServiceImpl.LOG.debug("Get the TOSCA test A XML file.");
		return this.getResource("test/TOSCA-TestXML-A.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getToscaTestXMLFileB() {
		FileAccessServiceImpl.LOG.debug("Get the TOSCA test B XML file.");
		return this.getResource("test/TOSCA-TestXML-B.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getToscaTestXMLFileMergeURI() {
		FileAccessServiceImpl.LOG.debug("Get the TOSCA test merge XML file.");
		return this.getResource("test/TOSCA-TestXML-Merge.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getTHORSuperApplicationToscaFile() {
		FileAccessServiceImpl.LOG.debug("Get the Super Application TOSCA file.");
		return this.getResource("test/SuperApplicationTOSCAFile.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getTestWarImplementationArtifact() {
		FileAccessServiceImpl.LOG.debug("Get the WAR test Implementation Artifact.");
		return this.getResource("test/TestImplementationArtifact.war");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getAWSDeployer() {
		FileAccessServiceImpl.LOG.debug("Get the IA WAR-File AWSDeployer.");
		return this.getResource("test/AWSDeployer.war");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getDBCreator() {
		FileAccessServiceImpl.LOG.debug("Get the IA WAR-File DBCreator.");
		return this.getResource("test/AmazonDBCreator.war");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getTestAarImplementationArtifact() {
		FileAccessServiceImpl.LOG.debug("Get the AAR test Implementation Artifact.");
		return this.getResource("test/TestImplementationArtifact.aar");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getTestWSDLFile() {
		FileAccessServiceImpl.LOG.debug("Get the test WSDL file.");
		return this.getResource("test/wsdl/Deploy.wsdl");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getResolverTest_MainTOSCA() {
		FileAccessServiceImpl.LOG.debug("Get the Service Template Resolver test main TOSCA file.");
		return this.getResource("test/ResolverTest_MainTOSCAFile.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getResolverTest_ImportTOSCA() {
		FileAccessServiceImpl.LOG.debug("Get the Service Template Resolver test import TOSCA file.");
		return this.getResource("test/ResolverTest_ImportTOSCA.xml");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getResolverTest_ImportWSDL() {
		FileAccessServiceImpl.LOG.debug("Get the Service Template Resolver test import WSDL file.");
		return this.getResource("test/ResolverTest_ImportWSDL.wsdl");
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getResolverThorFile() {
		FileAccessServiceImpl.LOG.debug("Getting the Service Templatr Resolver test THOR file.");
		return this.getResource("test/ResolverExampleThorFile.thor");
	}
	
	@Override
	public File getCSARSugarCRM3() {
		FileAccessServiceImpl.LOG.debug("Get the SugarCRM3 CSAR file.");
		return this.getResource("test/SugarCRM3.csar");
	}
	
	@Override
	public File getCSARSuperApplicationNotebook() {
		FileAccessServiceImpl.LOG.debug("Get the SuperApplicationNotebook CSAR file.");
		return this.getResource("test/SuperApplicationNotebook.csar");
	}
	
	@Override
	public File getCSARWithInvalidFileExtension() {
		FileAccessServiceImpl.LOG.debug("Get a CSAR with invalid file extension.");
		return this.getResource("test/CSARWithInvalidFileExtension.csar1");
	}
	
	@Override
	public File getCSARMoodle() {
		FileAccessServiceImpl.LOG.debug("Get the Moodle CSAR file.");
		return this.getResource("test/Moodle.csar");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid1() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 1.");
		return this.getResource("test/TOSCAMetaFileInvalid1.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid2() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 2.");
		return this.getResource("test/TOSCAMetaFileInvalid2.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid3() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 3.");
		return this.getResource("test/TOSCAMetaFileInvalid3.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid4() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 4.");
		return this.getResource("test/TOSCAMetaFileInvalid4.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid5() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 5.");
		return this.getResource("test/TOSCAMetaFileInvalid5.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid6() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 6.");
		return this.getResource("test/TOSCAMetaFileInvalid6.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid7() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 7.");
		return this.getResource("test/TOSCAMetaFileInvalid7.meta");
	}
	
	@Override
	public File getTOSCAMetaFileInvalid8() {
		FileAccessServiceImpl.LOG.debug("Get the invalid TOSCA meta file 8.");
		return this.getResource("test/TOSCAMetaFileInvalid8.meta");
	}
	
	@Override
	public File getTOSCAMetaFileValid1() {
		FileAccessServiceImpl.LOG.debug("Get the valid TOSCA meta file 1.");
		return this.getResource("test/TOSCAMetaFileValid1.meta");
	}
	
	@Override
	public File getTOSCAMetaFileValid2() {
		FileAccessServiceImpl.LOG.debug("Get the valid TOSCA meta file 2.");
		return this.getResource("test/TOSCAMetaFileValid2.meta");
	}
	
	@Override
	public File getTOSCAMetaFileValid3() {
		FileAccessServiceImpl.LOG.debug("Get the valid TOSCA meta file 3.");
		return this.getResource("test/TOSCAMetaFileValid3.meta");
	}
	
	@Override
	public File getTestCSAR() {
		FileAccessServiceImpl.LOG.debug("Get the Test CSAR for Core File Service test cases.");
		return this.getResource("test/TestCSAR.csar");
	}
	
	@Override
	public File getTestCSAR2() {
		FileAccessServiceImpl.LOG.debug("Get the Test CSAR 2 for Core File Service test cases (invalid, contains no Definitions files).");
		return this.getResource("test/TestCSAR2.csar");
	}
	
	@Override
	public File getStorageProviderTestFile1() {
		FileAccessServiceImpl.LOG.debug("Get a test file for storage provider test cases.");
		return this.getResource("test/Storage Provider Test File.txt");
	}
	
	@Override
	public File getStorageProviderTestFile2() {
		FileAccessServiceImpl.LOG.debug("Get a empty test file for storage provider test cases.");
		return this.getResource("test/StorageProviderTestFile2.war");
	}
	
	/**
	 * -----------------------------------------------------------------
	 */
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File getTemp() {
		
		Path tempDir;
		
		do {
			tempDir = Paths.get(Settings.getSetting("temp") + File.separator + System.nanoTime());
		} while (Files.exists(tempDir));
		
		try {
			Files.createDirectories(tempDir);
		} catch (IOException exc) {
			FileAccessServiceImpl.LOG.warn("An IO Exception occured.", exc);
			return null;
		}
		
		return tempDir.toFile();
		
		// Path opentoscaTemp = Paths.get(Settings.getSetting("temp"));
		//
		// try {
		// Files.createDirectories(opentoscaTemp);
		// Path tempDir = Files.createTempDirectory(opentoscaTemp, null);
		// return tempDir.toFile();
		// } catch (IOException exc) {
		// FileAccessServiceImpl.LOG.warn("An IO Exception occured.", exc);
		// }
		//
		// return null;
		
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File unpackToTemp(File zipFile) {
		File tempDir = this.getTemp();
		ZipManager.getInstance().unzip(zipFile, tempDir);
		return tempDir;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public File zip(File directory, File archive) {
		return ZipManager.getInstance().zip(directory, archive);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<File> unzip(File file, File toTarget) {
		return ZipManager.getInstance().unzip(file, toTarget);
	}
	
}
