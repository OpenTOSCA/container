package org.opentosca.container.legacy.core.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import javax.persistence.*;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.IBrowseable;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectoryArtifact;
import org.opentosca.container.legacy.core.model.jpa.FileSystemDirectoryConverter;
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
@NamedQueries( {
  @NamedQuery(name = CSARContent.GET_CSARIDS, query = CSARContent.GET_CSAR_IDS_QUERY),
  @NamedQuery(name = CSARContent.BY_CSARID, query = CSARContent.GET_CSAR_CONTENTS_BY_CSARID_QUERY),
})
@Entity(name = CSARContent.TABLE_NAME)
@Table(name = CSARContent.TABLE_NAME)
@Deprecated
public class CSARContent implements IBrowseable {
  /*
   * JPQL Queries
   */
  public static final String GET_CSARIDS = "CSARContent.GET_CSARIDS";
  public static final String BY_CSARID = "CSARContent.byCSARID";
  /**
   * For storing / updating the storage provider ID of a file in CSAR we must use a native SQL query,
   * because JPQL update queries doesn't work on Maps.
   */
  static final String TABLE_NAME = "CSAR";
  static final String GET_CSAR_IDS_QUERY = "SELECT t.csarID FROM " + CSARContent.TABLE_NAME + " t";
  static final String GET_CSAR_CONTENTS_BY_CSARID_QUERY = "SELECT t FROM " + CSARContent.TABLE_NAME + " t WHERE t.csarID = :csarID";

  private static final Logger LOG = LoggerFactory.getLogger(CSARContent.class);

  private static final String IMPORTS_DIR_REL_PATH = "IMPORTS";

  /**
   * Identifies this CSAR file.
   */
  @Id
  @GeneratedValue
  @SuppressWarnings("unused")
  // need a surrogate id, because we can't convert the id column with hibernate
  long surrogateId;

  @Convert(converter = CsarIdConverter.class)
  @Column(name = "csarID", unique = true, nullable = false)
  private CsarId csarID;

  /**
   * Contains the content of the TOSCA meta file of this CSAR.
   */
  // store TOSCAMetaFile as TEXT / CLOB
  @Column(name = "toscaMetaFile", columnDefinition = "TEXT")
  private TOSCAMetaFile toscaMetaFile = null;

  /**
   * For CSAR browsing this class represents the CSAR root.<br />
   * Browsing methods in this class redirecting to the same methods of this {@link AbstractDirectory} by
   * delegation.
   */
  @Convert(converter = FileSystemDirectoryConverter.class)
  @Column(name = "csarRoot", nullable = false)
  private FileSystemDirectory csarRoot = null;


  /**
   * Needed by Eclipse Link.
   */
  public CSARContent() {
    super();
  }

  public CSARContent(final CSARID csarID, final FileSystemDirectory csarRoot, final TOSCAMetaFile toscaMetaFile) {
    this.toscaMetaFile = toscaMetaFile;
    this.csarID = new CsarId(csarID);
    this.csarRoot = csarRoot;
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
    artifactReferenceTrimed = artifactReferenceTrimed.replaceAll(" ", "%20");

    try {
      new URI(artifactReferenceTrimed);
      LOG.debug("Artifact reference \"{}\" is a valid URI.", artifactReferenceTrimed.toString());
      AbstractArtifact artifact = null;

      if (new URI(artifactReferenceTrimed).getScheme() == null) {
        // downcast is possible here because we know FileSystemDirectory will always return a FileSystemDirectory from that method
        artifact = new FileSystemDirectoryArtifact((FileSystemDirectory)csarRoot.getDirectory(artifactReferenceTrimed));
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
}
