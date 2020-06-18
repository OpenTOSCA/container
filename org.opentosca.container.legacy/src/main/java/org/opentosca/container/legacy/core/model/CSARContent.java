package org.opentosca.container.legacy.core.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;

import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.IBrowseable;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.legacy.core.model.jpa.FileSystemDirectoryConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the meta data of a CSAR and makes the content the CSAR available. It provides structured access to all
 * files and directories of the CSAR. For CSAR browsing this represents the CSAR root. Access to information contained
 * in the TOSCA meta file of the CSAR, e.g. the author. Additionally, it resolves artifact references respectively gives
 * access to the artifact content.
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
     * For storing / updating the storage provider ID of a file in CSAR we must use a native SQL query, because JPQL
     * update queries doesn't work on Maps.
     */
    static final String TABLE_NAME = "CSAR";
    static final String GET_CSAR_IDS_QUERY = "SELECT t.csarID FROM " + CSARContent.TABLE_NAME + " t";
    static final String GET_CSAR_CONTENTS_BY_CSARID_QUERY = "SELECT t FROM " + CSARContent.TABLE_NAME + " t WHERE t.csarID = :csarID";

    private static final Logger LOG = LoggerFactory.getLogger(CSARContent.class);

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
     * For CSAR browsing this class represents the CSAR root.<br /> Browsing methods in this class redirecting to the
     * same methods of this {@link AbstractDirectory} by delegation.
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
     * @return Root TOSCA file of this CSAR as {@code AbstractFile}.<br /> If no root TOSCA path is specified in the
     * TOSCA meta file (attribute "Entry-Definitions") or path points to a non-existent file {@code null}.
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
