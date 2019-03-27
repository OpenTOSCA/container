package org.opentosca.container.legacy.core.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.common.jpa.PathConverter;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.CSARArtifact;
import org.opentosca.container.core.model.CSARDirectory;
import org.opentosca.container.core.model.IBrowseable;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the meta data of a CSAR and makes the content the CSAR available. It provides
 * structured access to all files and directories of the CSAR. For CSAR browsing this represents the
 * CSAR root. Access to information contained in the TOSCA meta file of the CSAR, e.g. the author.
 * Additionally, it resolves artifact references respectively gives access to the artifact content.
 *
 * @deprecated Instead use {@link Csar}
 */
@NamedQueries({
  @NamedQuery(name = CSARContent.getCSARIDs, query = CSARContent.getCSARIDsQuery),
  @NamedQuery(name = CSARContent.getFileToStorageProviderIDMapByCSARID, query = CSARContent.getFileToStorageProviderIDMapByCSARIDQuery),
  @NamedQuery(name = CSARContent.getDirectoriesByCSARID, query = CSARContent.getDirectoriesByCSARIDQuery),
  @NamedQuery(name = CSARContent.csarsByCSARID, query = CSARContent.getCSARContentsByCSARIDQuery),
})
@NamedNativeQuery(name = CSARContent.storeStorageProviderIDByFileAndCSARID, query = CSARContent.storeStorageProviderIDByFileAndCSARIDQuery)
@Entity(name = CSARContent.CSAR_TABLE_NAME)
@Table(name = CSARContent.CSAR_TABLE_NAME)
@Deprecated
public class CSARContent implements IBrowseable {
  /*
   * JPQL Queries
   */
  public static final String getCSARIDs = "CSARContent.getCSARIDs";
  public static final String getFileToStorageProviderIDMapByCSARID = CSARContent.CSAR_TABLE_NAME + ".getFileToStorageProviderIDMapByCSARID";
  public static final String csarsByCSARID = "CSARContent.byCSARID";
  /**
   * For storing / updating the storage provider ID of a file in CSAR we must use a native SQL query,
   * because JPQL update queries doesn't work on Maps.
   */
  public static final String storeStorageProviderIDByFileAndCSARID =
    CSARContent.CSAR_TABLE_NAME + ".storeStorageProviderIDByFileAndCSARID";
  public static final String getDirectoriesByCSARID = CSARContent.CSAR_TABLE_NAME + ".getDirectoriesByCSARID";

  protected static final String CSAR_TABLE_NAME = "CSAR";
  protected static final String CSAR_FILES_TABLE_NAME = "CSAR_Files";
  protected static final String CSAR_DIRECTORIES_TABLE_NAME = "CSAR_Directories";
  protected static final String getCSARIDsQuery = "SELECT t.csarID FROM " + CSARContent.CSAR_TABLE_NAME + " t";
  protected static final String getFileToStorageProviderIDMapByCSARIDQuery = "SELECT KEY(u), VALUE(u) FROM "
    + CSARContent.CSAR_TABLE_NAME + " t JOIN t.fileToStorageProviderIDMap u WHERE t.csarID = :csarID";
  protected static final String storeStorageProviderIDByFileAndCSARIDQuery =
    "UPDATE " + CSARContent.CSAR_FILES_TABLE_NAME + " SET storageProviderID = ? WHERE file = ? AND csarID = ?";


  protected static final String getDirectoriesByCSARIDQuery =
    "SELECT t FROM " + CSARContent.CSAR_TABLE_NAME + " t WHERE t.csarID = :csarID";
  static final String getCSARContentsByCSARIDQuery = "SELECT t FROM " + CSARContent.CSAR_TABLE_NAME + " t WHERE t.csarID = :csarID";

  private static final Logger LOG = LoggerFactory.getLogger(CSARContent.class);

  private static final String IMPORTS_DIR_REL_PATH = "IMPORTS";
  private static final String CSAR_DEFINITIONS_DIR_REL_PATH = "Definitions";
  private static final String[] TOSCA_FILE_EXTENSIONS = new String[] {"xml", "tosca", "ste"};

  /**
   * Identifies this CSAR file.
   */
  @Id
  @GeneratedValue
  // need a surrogate id, because we can't convert the id column with hibernate
    long surrogateId;

  @Convert(converter = CsarIdConverter.class)
  @Column(name = "csarID", unique = true, nullable = false)
  private CsarId csarID;

  /**
   * File to storage provider ID mapping of all files in this CSAR. Each file path is given relative
   * to the CSAR root.
   */
  @ElementCollection
  @CollectionTable(name = CSARContent.CSAR_FILES_TABLE_NAME, joinColumns = @JoinColumn(name = "csarID"))
  @MapKeyColumn(name = "file")
  @Convert(converter = PathConverter.class, attributeName = "key")
  @Column(name = "storageProviderID")
  private Map<Path, String> fileToStorageProviderIDMap;

  /**
   * Directories in this CSAR.<br />
   * Each directory is given relative to the CSAR root.
   */
  @ElementCollection
  @CollectionTable(name = CSARContent.CSAR_DIRECTORIES_TABLE_NAME, joinColumns = @JoinColumn(name = "csarID"))
  @Column(name = "directory")
  @Convert(converter = PathConverter.class)
  private Set<Path> directories;

  /**
   * Contains the content of the TOSCA meta file of this CSAR.
   */
  @Column(name = "toscaMetaFile")
  private TOSCAMetaFile toscaMetaFile = null;

  /**
   * For CSAR browsing this class represents the CSAR root.<br />
   * Browsing methods in this class redirecting to the same methods of this {@link CSARDirectory} by
   * delegation.
   */
  @Transient
  private AbstractDirectory csarRoot = null;


  /**
   * Needed by Eclipse Link.
   */
  public CSARContent() {
    super();
  }

  public CSARContent(final CSARID csarID, final AbstractDirectory csarRoot, final TOSCAMetaFile toscaMetaFile) {
    this.directories = Collections.emptySet();
    this.fileToStorageProviderIDMap = Collections.emptyMap();
    this.toscaMetaFile = toscaMetaFile;
    this.csarID = new CsarId(csarID);
    this.csarRoot = csarRoot;
  }

  /**
   * Creates a {@link CSARDirectory} that represents the CSAR root. This is necessary for CSAR
   * browsing.<br />
   * Method will be automatically called by Eclipse Link after this entity was retrieved.
   */
  @PostLoad
  private void setUpBrowsing() {
    this.csarRoot = new CSARDirectory("", this.csarID.toOldCsarId(), this.directories, this.fileToStorageProviderIDMap);
  }

  /**
   * @return CSAR ID of this CSAR.
   */
  public CSARID getCSARID() {
    return this.csarID.toOldCsarId();
  }

  @Override
  public AbstractFile getFile(final String relPathOfFile) {
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
  public AbstractDirectory getDirectory(final String relPathOfDirectory) {
    return this.csarRoot.getDirectory(relPathOfDirectory);
  }

  @Override
  public Set<AbstractDirectory> getDirectories() {
    return this.csarRoot.getDirectories();
  }

  public Set<Path> getDirectoriesJpa() {
    return this.directories;
  }

  /**
   * @param fileExtension
   * @return All files with extension {@code fileExtension} in directory "IMPORTS" of this CSAR as Set
   * of {@code AbstractFile}.
   */
  private Set<AbstractFile> getImportFiles(final String fileExtension) {

    final Set<AbstractFile> importFiles = new HashSet<>();
    LOG.debug("Retrieving import file(s) with extension \"{}\" in CSAR \"{}\"...", fileExtension, this.csarID);

    final AbstractDirectory importsDirectory = getDirectory(IMPORTS_DIR_REL_PATH);
    if (importsDirectory != null) {
      for (final AbstractFile file : importsDirectory.getFilesRecursively()) {
        if (file.getName().toLowerCase().endsWith("." + fileExtension)) {
          importFiles.add(file);
        }
      }
    }

    LOG.debug("{} import file(s) with extension \"{}\" were found in CSAR \"{}\".", importFiles.size(), fileExtension, this.csarID);
    return importFiles;
  }

  /**
   * Resolves the artifact reference {@code artifactReference} and makes the content of the artifact
   * available.<br />
   *
   * @param artifactReference - relative or absolute URI that points to a file or directory. A
   *                          relative URI points to a file / directory in this CSAR. If you pass an empty string it's
   *                          means the CSAR root, so you get an artifact that represents the complete CSAR.
   * @return {@code AbstractArtifact} that makes the content of the artifact available.
   * @throws UserException   if {@code artifactReference} points to a non-existent file / directory or
   *                         is an invalid URI.
   * @throws SystemException if type of {@code artifactReference} respectively artifact is not
   *                         supported.
   */
  public AbstractArtifact resolveArtifactReference(final String artifactReference) throws UserException, SystemException {
    // no patterns, so we pass empty sets (immutable to avoid unnecessary object creations)
    return this.resolveArtifactReference(artifactReference, Collections.emptySet(), Collections.emptySet());
  }

  /**
   * Resolves the artifact reference {@code artifactReference} and makes the content of the artifact
   * available. Optionally {@code includePatterns} and {@code excludePatterns} can be passed.<br />
   * <br />
   * An include pattern includes only certain files at the reference in the artifact. By analogy, an
   * exclude pattern excludes files from the artifact. A pattern must be given as a regular
   * expression. <br />
   * Note, patterns will be only applied if artifact reference points to a directory (or root).
   * Furthermore, they will be only matched against files at the artifact reference. These
   * restrictions are in accordance with the TOSCA specification CS01.
   *
   * @param artifactReference - relative or absolute URI that points to a file or directory. A
   *                          relative URI points to a file / directory in this CSAR. If you pass an empty string it's
   *                          means the CSAR root, so you get an artifact that represents the complete CSAR.
   * @param includePatterns   to include only certain files in the artifact. No include patterns must be
   *                          passed by an empty set.
   * @param excludePatterns   to exclude certain files from the artifact. No exclude patterns must be
   *                          passed by an empty set.
   * @return {@code AbstractArtifact} that makes the content of the artifact available.
   * @throws UserException   if {@code artifactReference} points to a non-existent file / directory or
   *                         is not a valid URI.
   * @throws SystemException if type of {@code artifactReference} respectively artifact is not
   *                         supported.
   */
  public AbstractArtifact resolveArtifactReference(final String artifactReference, final Set<String> includePatterns,
                                                   final Set<String> excludePatterns) throws UserException, SystemException {
    LOG.debug("Resolving artifact reference \"{}\"...", artifactReference);
    String artifactReferenceTrimed = artifactReference.trim();
    // spaces are allowed in XSD anyURI => we must encode spaces
    artifactReferenceTrimed = artifactReferenceTrimed.replaceAll("[ ]", "%20");

    try {
      new URI(artifactReferenceTrimed);
      LOG.debug("Artifact reference \"{}\" is a valid URI.", artifactReferenceTrimed.toString());
      AbstractArtifact artifact = null;

      if (CSARArtifact.fitsArtifactReference(artifactReferenceTrimed)) {
        artifact = new CSARArtifact(artifactReferenceTrimed, includePatterns, excludePatterns, this.csarID.toOldCsarId(),
          this.directories, this.fileToStorageProviderIDMap);
        // if further AbstractArtifact implementations exists, we
        // can check here if they fits
      } else {
        throw new SystemException("Artifact reference \"" + artifactReferenceTrimed + "\" is not supported.");
      }
      LOG.debug("Resolving artifact reference \"{}\" completed.", artifactReferenceTrimed);
      return artifact;
    } catch (final URISyntaxException exc) {
      throw new UserException("Artifact reference \"" + artifactReference + "\" is not a valid URI.", exc);
    }
  }

  /**
   * @return All files in directory "Definitions" of this CSAR as Set of {@code AbstractFile}.
   */
  public Set<AbstractFile> getTOSCAsInDefinitionsDir() {
    LOG.debug("Retrieving TOSCA files in directory \"{}\" of CSAR \"{}\"...", CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
    final AbstractDirectory definitionsDir = getDirectory(CSAR_DEFINITIONS_DIR_REL_PATH);
    if (definitionsDir == null) {
      LOG.warn("Directory \"{}\" was not found in CSAR \"{}\".", CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
      return Collections.emptySet();
    }
    Set<AbstractFile> toscasInDefinitionsDir = definitionsDir.getFilesRecursively();
    LOG.debug("{} TOSCA files were found in directory \"{}\" of CSAR \"{}\".", toscasInDefinitionsDir.size(), CSAR_DEFINITIONS_DIR_REL_PATH, this.csarID);
    return toscasInDefinitionsDir;
  }

  /**
   * @return Root TOSCA file of this CSAR as {@code AbstractFile}.<br />
   * If no root TOSCA path is specified in the TOSCA meta file (attribute "Entry-Definitions")
   * or path points to a non-existent file {@code null}.
   */
  public AbstractFile getRootTOSCA() {
    LOG.debug("Retrieving root TOSCA of CSAR \"{}\"...", this.csarID);
    String relPathOfRootTOSCA = this.toscaMetaFile.getEntryDefinitions();
    if (relPathOfRootTOSCA == null) {
      LOG.warn("Root TOSCA path is not specified in TOSCA meta file of CSAR \"{}\".", this.csarID);
      return null;
    }

    AbstractFile rootTOSCA = getFile(relPathOfRootTOSCA);
    if (rootTOSCA == null) {
      LOG.warn("Root TOSCA path \"{}\" specified in TOSCA meta file of CSAR \"{}\" points to a non-existing file.", relPathOfRootTOSCA, this.csarID);
      return null;
    }
    LOG.debug("Root TOSCA exists at \"{}\" in CSAR \"{}\".", rootTOSCA.getPath(), this.csarID);
    return rootTOSCA;
  }

  /**
   * @return XML files in directory "IMPORTS" of this CSAR as Set of {@code AbstractFile}.
   */
  public Set<AbstractFile> getXMLImports() {
    return getImportFiles("xml");
  }

  /**
   * @return WSDL files in directory "IMPORTS" of this CSAR as Set of {@code AbstractFile}.
   */
  public Set<AbstractFile> getWSDLImports() {
    return getImportFiles("wsdl");
  }

  /**
   * @return XSD files in directory "IMPORTS" of this CSAR as Set of {@code AbstractFile}.
   */
  public Set<AbstractFile> getXSDImports() {
    return getImportFiles("xsd");
  }

  /**
   * @return Picture that visualizes the topology of this CSAR as {@code AbstractFile}. If no topology
   * picture path is specified in TOSCA meta file (attribute "Topology") or path points to a
   * non-existent file {@code null}.
   */
  public AbstractFile getTopologyPicture() {
    String topologyPictureRelPath = this.toscaMetaFile.getTopology();
    if (topologyPictureRelPath == null) {
      LOG.warn("Topology picture path is not specified in TOSCA meta file of CSAR \"{}\".", this.csarID);
      return null;
    }
    AbstractFile topologyPicture = getFile(topologyPictureRelPath);
    if (topologyPicture == null) {
      LOG.warn("Topology picture path specified in TOSCA meta file of CSAR \"{}\" points to a non-existing file.", this.csarID);
      return null;
    }
    LOG.debug("Topology picture exists at \"{}\" in CSAR \"{}\".", topologyPicture.getPath(), this.csarID);
    return topologyPicture;
  }

  public AbstractDirectory getCsarRoot() {
    return this.csarRoot;
  }
}
