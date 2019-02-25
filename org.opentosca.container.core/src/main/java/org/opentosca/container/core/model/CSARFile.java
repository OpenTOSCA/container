package org.opentosca.container.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a file in a CSAR. This file can be stored at an arbitrary storage provider. Provides
 * methods to get it's meta data using the local stored meta data and fetch the file from the
 * appropriate storage provider.
 *
 * @see ICoreInternalFileStorageProviderService
 */
public class CSARFile extends AbstractFile {

    private final static Logger LOG = LoggerFactory.getLogger(CSARFile.class);

    /**
     * CSAR ID of CSAR that contains this file.
     */
    private final CSARID CSAR_ID;

    /**
     * Must be {@code static}, because this class will be instantiated with {@code new}.
     */
    private static IFileAccessService fileAccessService;


    /**
     * Default constructor needed by OSGi to instantiate this class.
     */
    public CSARFile() {
        this(null, null, null);
    }

    /**
     * Creates a {@link CSARFile}.
     *
     * @param relFilePathToCSARRoot - relative path to CSAR root of this file.
     * @param csarID of CSAR that contains this file.
     * @param storageProviderID of storage provider on which this file is stored.
     *
     * @see ICoreInternalFileStorageProviderService
     */
    // FIXME: drop storageProviderId
    @Deprecated 
    public CSARFile(final String relFilePathToCSARRoot, final CSARID csarID, final String storageProviderID) {
        super(relFilePathToCSARRoot);
        this.CSAR_ID = csarID;
        
    }

    /**
     * @throws SystemException if required storage provider is not available and ready, file was not
     *         found on storage provider or an error occurred during retrieving.
     */
    @Override
    public Path getFile() throws SystemException {
        final Path targetFile = CSARFile.fileAccessService.getTemp().toPath().resolve(this.getName());
        return targetFile;
    }

    /**
     * @throws SystemException if required storage provider is not available and ready, file was not
     *         found on storage provider or an error occurred during getting.
     */
    @Override
    public InputStream getFileAsInputStream() throws SystemException {
        try {
            return Files.newInputStream(getFile(), StandardOpenOption.READ);
        }
        catch (IOException e) {
            throw new SystemException("Could not read CsarFile", e);
        }
    }

    @Override
    public String getName() {
        final Path filePath = Paths.get(this.getPath());
        return filePath.getFileName().toString();
    }

    /**
     * @return {@inheritDoc} It's the relative path to CSAR root.
     */
    @Override
    public String getPath() {
        return super.getPath();
    }

    @Override
    public String toString() {
        return "File \"" + this.getPath() + "\" of CSAR \"" + this.CSAR_ID + "\".";
    }

    @Override
    public boolean equals(final Object file) {
        if (file instanceof CSARFile) {
            final CSARFile csarFile = (CSARFile) file;
            return this.getPath().equals(csarFile.getPath()) 
                && this.CSAR_ID.equals(csarFile.CSAR_ID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Binds the File Access Service.
     *
     * @param fileAccessService to bind
     */
    protected void bindFileAccessService(final IFileAccessService fileAccessService) {
        if (fileAccessService != null) {
            CSARFile.fileAccessService = fileAccessService;
            CSARFile.LOG.debug("File Access Service bound.");
        } else {
            CSARFile.LOG.warn("Binding File Access Service failed.");
        }
    }

    /**
     * Unbinds the File Access Service.
     *
     * @param fileAccessService to unbind
     */
    protected void unbindFileAccessService(final IFileAccessService fileAccessService) {
        if (fileAccessService != null) {
            CSARFile.fileAccessService = null;
            CSARFile.LOG.debug("File Access Service unbound.");
        } else {
            CSARFile.LOG.warn("Unbinding File Access Service failed.");
        }
    }
}
