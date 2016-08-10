package org.opentosca.core.model.csar;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.eclipse.persistence.annotations.MapKeyConvert;
import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.IBrowseable;
import org.opentosca.core.model.artifact.directory.AbstractDirectory;
import org.opentosca.core.model.artifact.directory.impl.CSARDirectory;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.artifact.impl.CSARArtifact;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.core.model.csar.toscametafile.TOSCAMetaFileAttributes;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.settings.Settings;
import org.opentosca.util.jpa.converters.CSARIDConverter;
import org.opentosca.util.jpa.converters.PathConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the meta data of a CSAR and makes the content the CSAR available.<br />
 * It provides:<br />
 * - Structured access to all files and directories of the CSAR. For CSAR
 * browsing this represents the CSAR root.<br />
 * - Access to information contained in the TOSCA meta file of the CSAR, e.g.
 * the author.<br />
 * - Additionally, it resolves artifact references respectively gives access to
 * the artifact content.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
@NamedQueries({@NamedQuery(name = CSARContent.getCSARIDs, query = CSARContent.getCSARIDsQuery), @NamedQuery(name = CSARContent.getFileToStorageProviderIDMapByCSARID, query = CSARContent.getFileToStorageProviderIDMapByCSARIDQuery), @NamedQuery(name = CSARContent.getDirectoriesByCSARID, query = CSARContent.getDirectoriesByCSARIDQuery)})
@NamedNativeQuery(name = CSARContent.storeStorageProviderIDByFileAndCSARID, query = CSARContent.storeStorageProviderIDByFileAndCSARIDQuery)
@Entity(name = CSARContent.CSAR_TABLE_NAME)
@Table(name = CSARContent.CSAR_TABLE_NAME)
@Converters({@Converter(name = "CSARIDConverter", converterClass = CSARIDConverter.class), @Converter(name = "PathConverter", converterClass = PathConverter.class)})
public class CSARContent implements IBrowseable {
	
	final private static Logger LOG = LoggerFactory.getLogger(CSARContent.class);
	
	protected static final String CSAR_TABLE_NAME = "CSAR";
	protected static final String CSAR_FILES_TABLE_NAME = "CSAR_Files";
	protected static final String CSAR_DIRECTORIES_TABLE_NAME = "CSAR_Directories";
	
	/**
	 * JPQL Queries
	 */
	
	public static final String getCSARIDs = "CSARContent.getCSARIDs";
	protected static final String getCSARIDsQuery = "SELECT t.csarID FROM " + CSARContent.CSAR_TABLE_NAME + " t";
	
	public static final String getFileToStorageProviderIDMapByCSARID = CSARContent.CSAR_TABLE_NAME + ".getFileToStorageProviderIDMapByCSARID";
	protected static final String getFileToStorageProviderIDMapByCSARIDQuery = "SELECT KEY(u), VALUE(u) FROM " + CSARContent.CSAR_TABLE_NAME + " t JOIN t.fileToStorageProviderIDMap u WHERE t.csarID = :csarID";
	
	/**
	 * For storing / updating the storage provider ID of a file in CSAR we must
	 * use a native SQL query, because JPQL update queries doesn't work on Maps.
	 */
	public static final String storeStorageProviderIDByFileAndCSARID = CSARContent.CSAR_TABLE_NAME + ".storeStorageProviderIDByFileAndCSARID";
	protected static final String storeStorageProviderIDByFileAndCSARIDQuery = "UPDATE " + CSARContent.CSAR_FILES_TABLE_NAME + " SET storageProviderID = ? WHERE file = ? AND csarID = ?";
	
	public static final String getDirectoriesByCSARID = CSARContent.CSAR_TABLE_NAME + ".getDirectoriesByCSARID";
	protected static final String getDirectoriesByCSARIDQuery = "SELECT t.directories FROM " + CSARContent.CSAR_TABLE_NAME + " t WHERE t.csarID = :csarID";
	
	/**
	 * Relative path to CSAR root of the {@code IMPORTS} directory.
	 * 
	 * @see org.opentosca.settings.Settings
	 */
	@Transient
	private final String IMPORTS_DIR_REL_PATH = Settings.getSetting("csarImportsRelPath");
	
	/**
	 * Relative path to CSAR root of the {@code Definitions} directory.
	 * 
	 * @see org.opentosca.settings.Settings
	 */
	@Transient
	private final String CSAR_DEFINITIONS_DIR_REL_PATH = Settings.getSetting("csarDefinitionsRelPath");
	
	/**
	 * Possible file extensions of a TOSCA file.
	 * 
	 * @see org.opentosca.settings.Settings
	 */
	@Transient
	private final String[] TOSCA_FILE_EXTENSIONS = Settings.getSetting("toscaFileExtensions").split(";");
	
	/**
	 * Identifies this CSAR file.
	 */
	@Id
	@Convert("CSARIDConverter")
	@Column(name = "csarID")
	private CSARID csarID;
	
	/**
	 * File to storage provider ID mapping of all files in this CSAR. Each file
	 * path is given relative to the CSAR root.
	 */
	@ElementCollection
	@CollectionTable(name = CSARContent.CSAR_FILES_TABLE_NAME, joinColumns = @JoinColumn(name = "csarID"))
	@MapKeyColumn(name = "file")
	@MapKeyConvert("PathConverter")
	@Column(name = "storageProviderID")
	private Map<Path, String> fileToStorageProviderIDMap;
	
	/**
	 * Directories in this CSAR.<br />
	 * Each directory is given relative to the CSAR root.
	 */
	@ElementCollection
	@CollectionTable(name = CSARContent.CSAR_DIRECTORIES_TABLE_NAME, joinColumns = @JoinColumn(name = "csarID"))
	@Column(name = "directory")
	@Convert("PathConverter")
	private Set<Path> directories;
	
	/**
	 * Contains the content of the TOSCA meta file of this CSAR.
	 */
	@Column(name = "toscaMetaFile")
	private TOSCAMetaFile toscaMetaFile = null;
	
	/**
	 * For CSAR browsing this class represents the CSAR root.<br />
	 * Browsing methods in this class redirecting to the same methods of this
	 * {@link CSARDirectory} by delegation.
	 */
	@Transient
	private AbstractDirectory csarRoot = null;
	
	
	/**
	 * Needed by Eclipse Link.
	 */
	protected CSARContent() {
	}
	
	/**
	 * Creates a {@code CSARContent}.
	 * 
	 * @param csarID of CSAR
	 * @param directories in the CSAR.
	 * @param fileToStorageProviderID - Files in the CSAR. A file given relative
	 *            to the CSAR root is mapped to the ID of the storage provider
	 *            on which the file is stored.
	 * @param toscaMetaFile - contains the content of the TOSCA meta file of the
	 *            CSAR.
	 */
	public CSARContent(CSARID csarID, Set<Path> directories, Map<Path, String> fileToStorageProviderIDMap, TOSCAMetaFile toscaMetaFile) {
		this.directories = directories;
		this.fileToStorageProviderIDMap = fileToStorageProviderIDMap;
		this.toscaMetaFile = toscaMetaFile;
		this.csarID = csarID;
	}
	
	/**
	 * Creates a {@link CSARDirectory} that represents the CSAR root. This is
	 * necessary for CSAR browsing.<br />
	 * Method will be automatically called by Eclipse Link after this entity was
	 * retrieved.
	 */
	@PostLoad
	protected void setUpBrowsing() {
		this.csarRoot = new CSARDirectory("", this.csarID, this.directories, this.fileToStorageProviderIDMap);
	}
	
	/**
	 * @return CSAR ID of this CSAR.
	 */
	public CSARID getCSARID() {
		return this.csarID;
	}
	
	@Override
	public AbstractFile getFile(String relPathOfFile) {
		return this.csarRoot.getFile(relPathOfFile);
	}
	
	@Override
	public Set<AbstractFile> getFiles() {
		return this.csarRoot.getFiles();
	}
	
	@Override
	public Set<AbstractFile> getFilesRecursively() {
		return this.csarRoot.getFilesRecursively();
	}
	
	@Override
	public AbstractDirectory getDirectory(String relPathOfDirectory) {
		return this.csarRoot.getDirectory(relPathOfDirectory);
	}
	
	@Override
	public Set<AbstractDirectory> getDirectories() {
		return this.csarRoot.getDirectories();
	}
	
	/**
	 * @param fileExtension
	 * @return All files with extension {@code fileExtension} in directory
	 *         "IMPORTS" of this CSAR as Set of {@code AbstractFile}.
	 */
	private Set<AbstractFile> getImportFiles(String fileExtension) {
		
		Set<AbstractFile> importFiles = new HashSet<AbstractFile>();
		
		CSARContent.LOG.debug("Retrieving import file(s) with extension \"{}\" in CSAR \"{}\"...", fileExtension, this.csarID);
		
		AbstractDirectory importsDirectory = this.getDirectory(this.IMPORTS_DIR_REL_PATH);
		
		if (importsDirectory != null) {
			for (AbstractFile file : importsDirectory.getFilesRecursively()) {
				if (file.getName().toLowerCase().endsWith("." + fileExtension)) {
					importFiles.add(file);
				}
			}
		}
		
		CSARContent.LOG.debug("{} import file(s) with extension \"{}\" were found in CSAR \"{}\".", importFiles.size(), fileExtension, this.csarID);
		
		return importFiles;
	}
	
	/**
	 * Resolves the artifact reference {@code artifactReference} and makes the
	 * content of the artifact available.<br />
	 * 
	 * @param artifactReference - relative or absolute URI that points to a file
	 *            or directory. A relative URI points to a file / directory in
	 *            this CSAR. If you pass an empty string it's means the CSAR
	 *            root, so you get an artifact that represents the complete
	 *            CSAR.
	 * @return {@code AbstractArtifact} that makes the content of the artifact
	 *         available.
	 * @throws UserException if {@code artifactReference} points to a
	 *             non-existent file / directory or is an invalid URI.
	 * @throws SystemException if type of {@code artifactReference} respectively
	 *             artifact is not supported.
	 */
	public AbstractArtifact resolveArtifactReference(String artifactReference) throws UserException, SystemException {
		
		// no patterns, so we pass empty sets (immutable to avoid unnecessary
		// object creations)
		return this.resolveArtifactReference(artifactReference, Collections.<String> emptySet(), Collections.<String> emptySet());
		
	}
	
	/**
	 * Resolves the artifact reference {@code artifactReference} and makes the
	 * content of the artifact available. Optionally {@code includePatterns} and
	 * {@code excludePatterns} can be passed.<br />
	 * <br />
	 * An include pattern includes only certain files at the reference in the
	 * artifact. By analogy, an exclude pattern excludes files from the
	 * artifact. A pattern must be given as a regular expression. <br />
	 * Note, patterns will be only applied if artifact reference points to a
	 * directory (or root). Furthermore, they will be only matched against files
	 * at the artifact reference. These restrictions are in accordance with the
	 * TOSCA specification CS01.
	 * 
	 * @param artifactReference - relative or absolute URI that points to a file
	 *            or directory. A relative URI points to a file / directory in
	 *            this CSAR. If you pass an empty string it's means the CSAR
	 *            root, so you get an artifact that represents the complete
	 *            CSAR.
	 * @param includePatterns to include only certain files in the artifact. No
	 *            include patterns must be passed by an empty set.
	 * @param excludePatterns to exclude certain files from the artifact. No
	 *            exclude patterns must be passed by an empty set.
	 * @return {@code AbstractArtifact} that makes the content of the artifact
	 *         available.
	 * @throws UserException if {@code artifactReference} points to a
	 *             non-existent file / directory or is not a valid URI.
	 * @throws SystemException if type of {@code artifactReference} respectively
	 *             artifact is not supported.
	 */
	public AbstractArtifact resolveArtifactReference(String artifactReference, Set<String> includePatterns, Set<String> excludePatterns) throws UserException, SystemException {
		
		CSARContent.LOG.debug("Resolving artifact reference \"{}\"...", artifactReference);
		
		String artifactReferenceTrimed = artifactReference.trim();
		
		// spaces are allowed in XSD anyURI => we must encode spaces
		artifactReferenceTrimed = artifactReferenceTrimed.replaceAll("[ ]", "%20");
		
		try {
			
			new URI(artifactReferenceTrimed);
			CSARContent.LOG.debug("Artifact reference \"{}\" is a valid URI.", artifactReferenceTrimed.toString());
			
			AbstractArtifact artifact = null;
			
			if (CSARArtifact.fitsArtifactReference(artifactReferenceTrimed)) {
				artifact = new CSARArtifact(artifactReferenceTrimed, includePatterns, excludePatterns, this.csarID, this.directories, this.fileToStorageProviderIDMap);
				// if further AbstractArtifact implementations exists, we
				// can check here if they fits
			} else {
				throw new SystemException("Artifact reference \"" + artifactReferenceTrimed + "\" is not supported.");
			}
			
			CSARContent.LOG.debug("Resolving artifact reference \"{}\" completed.", artifactReferenceTrimed);
			
			return artifact;
			
		} catch (URISyntaxException exc) {
			throw new UserException("Artifact reference \"" + artifactReference + "\" is not a valid URI.", exc);
		}
		
	}
	
	/**
	 * @return All files in directory "Definitions" of this CSAR as Set of
	 *         {@code AbstractFile}.
	 */
	public Set<AbstractFile> getTOSCAsInDefinitionsDir() {
		
		CSARContent.LOG.debug("Retrieving TOSCA files in directory \"{}\" of CSAR \"{}\"...", this.CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
		
		Set<AbstractFile> toscasInDefinitionsDir = new HashSet<AbstractFile>();
		
		AbstractDirectory definitionsDir = this.getDirectory(this.CSAR_DEFINITIONS_DIR_REL_PATH);
		
		if (definitionsDir != null) {
			
			toscasInDefinitionsDir = definitionsDir.getFilesRecursively();
			
			// for (AbstractFile fileInDefinitionDir :
			// definitionsDir.getFilesRecursively()) {
			// if (this.hasFileExtension(fileInDefinitionDir.getPath(),
			// this.TOSCA_FILE_EXTENSIONS)) {
			// toscasInDefinitionsDir.add(fileInDefinitionDir);
			// }
			// }
			
		} else {
			CSARContent.LOG.warn("Directory \"{}\" was not found in CSAR \"{}\".", this.CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
		}
		
		CSARContent.LOG.debug("{} TOSCA files were found in directory \"{}\" of CSAR \"{}\".", toscasInDefinitionsDir.size(), this.CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
		
		return toscasInDefinitionsDir;
		
	}
	
	// /**
	// * @param file
	// * @param extensions of a file.
	// * @return {@code true}, if file {@code file} has any of the file
	// extensions
	// * {@code extensions}, otherwise {@code false}.
	// */
	// private boolean hasFileExtension(String file, String... extensions) {
	// for (String extension : extensions) {
	// if (file.toLowerCase().endsWith("." + extension)) {
	// return true;
	// }
	// }
	// return false;
	// }
	
	/**
	 * @return Root TOSCA file of this CSAR as {@code AbstractFile}.<br />
	 *         If no root TOSCA path is specified in the TOSCA meta file
	 *         (attribute "Entry-Definitions") or path points to a non-existent
	 *         file {@code null}.
	 */
	public AbstractFile getRootTOSCA() {
		
		CSARContent.LOG.debug("Retrieving root TOSCA of CSAR \"{}\"...", this.csarID);
		
		String relPathOfRootTOSCA = null;
		AbstractFile rootTOSCA = null;
		
		relPathOfRootTOSCA = this.toscaMetaFile.getEntryDefinitions();
		
		if (relPathOfRootTOSCA != null) {
			
			rootTOSCA = this.getFile(relPathOfRootTOSCA);
			
			if (rootTOSCA != null) {
				CSARContent.LOG.debug("Root TOSCA exists at \"{}\" in CSAR \"{}\".", rootTOSCA.getPath(), this.csarID);
			} else {
				CSARContent.LOG.warn("Root TOSCA path \"{}\" specified in TOSCA meta file of CSAR \"{}\" points to a non-existing file.", relPathOfRootTOSCA, this.csarID);
			}
			
		} else {
			CSARContent.LOG.warn("Root TOSCA path is not specified in TOSCA meta file of CSAR \"{}\".", this.csarID);
		}
		
		return rootTOSCA;
	}
	
	/**
	 * @return XML files in directory "IMPORTS" of this CSAR as Set of
	 *         {@code AbstractFile}.
	 */
	public Set<AbstractFile> getXMLImports() {
		return this.getImportFiles("xml");
	}
	
	/**
	 * @return WSDL files in directory "IMPORTS" of this CSAR as Set of
	 *         {@code AbstractFile}.
	 */
	public Set<AbstractFile> getWSDLImports() {
		return this.getImportFiles("wsdl");
	}
	
	/**
	 * @return XSD files in directory "IMPORTS" of this CSAR as Set of
	 *         {@code AbstractFile}.
	 */
	public Set<AbstractFile> getXSDImports() {
		return this.getImportFiles("xsd");
	}
	
	/**
	 * @return Author of this CSAR. If no author is specified in TOSCA meta file
	 *         {@code null}.
	 */
	public String getCSARAuthor() {
		
		String author = this.toscaMetaFile.getCreatedBy();
		
		if (author == null) {
			CSARContent.LOG.debug("Author is not specified in TOSCA meta file of CSAR \"{}\".", TOSCAMetaFileAttributes.CREATED_BY, this.csarID);
		} else {
			CSARContent.LOG.debug("Author of CSAR \"{}\": {}", this.csarID, author);
		}
		
		return author;
		
	}
	
	/**
	 * @return Description of this CSAR. If no description is specified in TOSCA
	 *         meta file {@code null}.
	 */
	public String getCSARDescription() {
		
		String description = this.toscaMetaFile.getDescription();
		
		if (description == null) {
			CSARContent.LOG.debug("Description is not specified in TOSCA meta file of CSAR \"{}\".", TOSCAMetaFileAttributes.DESCRIPTION, this.csarID);
		} else {
			CSARContent.LOG.debug("Description of CSAR \"{}\": {}", this.csarID, description);
		}
		
		return description;
		
	}
	
	/**
	 * @return Picture that visualizes the topology of this CSAR as
	 *         {@code AbstractFile}. If no topology picture path is specified in
	 *         TOSCA meta file (attribute "Topology") or path points to a
	 *         non-existent file {@code null}.
	 */
	public AbstractFile getTopologyPicture() {
		
		String topologyPictureRelPath = null;
		AbstractFile topologyPicture = null;
		
		topologyPictureRelPath = this.toscaMetaFile.getTopology();
		
		if (topologyPictureRelPath != null) {
			topologyPicture = this.getFile(topologyPictureRelPath);
			if (topologyPicture != null) {
				CSARContent.LOG.debug("Topology picture exists at \"{}\" in CSAR \"{}\".", topologyPicture.getPath(), this.csarID);
			} else {
				CSARContent.LOG.warn("Topology picture path specified in TOSCA meta file of CSAR \"{}\" points to a non-existing file.", this.csarID);
			}
		} else {
			CSARContent.LOG.warn("Topology picture path is not specified in TOSCA meta file of CSAR \"{}\".", this.csarID);
		}
		
		return topologyPicture;
		
	}
}
