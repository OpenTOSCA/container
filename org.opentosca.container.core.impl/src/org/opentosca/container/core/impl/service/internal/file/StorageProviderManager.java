package org.opentosca.container.core.impl.service.internal.file;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.internal.CoreInternalFileServiceImpl;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.internal.ICoreInternalFileStorageProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the storage providers of the Core Internal File Service and makes them available.
 *
 * It provides CSAR specific methods for using a storage provider. This means, for e.g. storing a
 * file of a CSAR the CSAR ID, the relative file path to CSAR root and the storage provider ID must
 * be passed. With CSAR ID and path the store path on storage provider will be built by using
 * {@link #buildFileOfCSARStorageProviderPath(CSARID, Path)}.
 *
 * Additionally, it provides methods for choosing the storage provider for a operation, getting all
 * available and ready storage providers, getting the default storage provider and a getter and
 * setter for the active storage provider.
 */
public class StorageProviderManager {

    private final static Logger LOG = LoggerFactory.getLogger(StorageProviderManager.class);

    /**
     * Bound, available storage providers.<br />
     * <br />
     * Note: Must be {@code static}, because otherwise, if we instantiate this class manually with
     * {@code new} and not (automatically) by the OSGi framework, this variable would be not set.
     */
    private static final Map<String, ICoreInternalFileStorageProviderService> STORAGE_PROVIDERS = Collections.synchronizedMap(
        new HashMap<String, ICoreInternalFileStorageProviderService>());

    /**
     * Active storage provider.<br />
     * After the start of OpenTOSCA, no active storage provider is set.
     *
     * @see #getActiveStorageProvider()
     * @see #setActiveStorageProvider(String)
     *
     */
    private static String activeStorageProviderID = null;

    /**
     * Default storage provider is hard-coded and will be used as a last attempt if the active storage
     * provider is not available and ready or not set.
     *
     * @see #getDefaultStorageProvider()
     *
     */
    private final String DEFAULT_STORAGE_PROVIDER_ID = "filesystem";


    /**
     * Chooses a storage provider for an operation.<br />
     * First, it checks if a active storage provider is set and if it's ready. If this is the case, it's
     * ID will be returned. Otherwise it will be checked if the default storage provider is available
     * and ready and if so, it's ID will be returned.
     *
     * @return ID of the active storage provider respectively default storage provider.
     * @throws SystemException if active storage provider is not set or not ready and default storage
     *         provider also can't be used, because it's not available and ready.
     */
    public String chooseStorageProvider() throws SystemException {

        StorageProviderManager.LOG.debug("Choosing storage provider...");

        final String activeStorageProviderID = this.getActiveStorageProvider();

        if (activeStorageProviderID != null) {

            if (this.isStorageProviderReady(activeStorageProviderID)) {
                StorageProviderManager.LOG.debug("Active storage provider \"{}\" will be used.",
                    activeStorageProviderID);
                return activeStorageProviderID;
            }
            StorageProviderManager.LOG.debug("Active storage provider \"{}\" can't be used.", activeStorageProviderID);

        } else {

            StorageProviderManager.LOG.debug("No active storage provider is set.");

        }

        final String defaultStorageProviderID = this.getDefaultStorageProvider();

        StorageProviderManager.LOG.debug("Switching to the default storage provider \"{}\".", defaultStorageProviderID);

        if (this.isStorageProviderReady(defaultStorageProviderID)) {
            StorageProviderManager.LOG.debug("Default storage provider \"{}\" will be used.", defaultStorageProviderID);
            return defaultStorageProviderID;
        }

        throw new SystemException(
            "Active storage provider is not set / ready and default storage provider is also not available and ready.");

    }

    /**
     * @see CoreInternalFileServiceImpl#getStorageProviders()
     */
    public Set<String> getStorageProviders() {

        Set<String> storageProviderIDs = null;

        synchronized (StorageProviderManager.STORAGE_PROVIDERS) {
            storageProviderIDs = StorageProviderManager.STORAGE_PROVIDERS.keySet();
        }

        return storageProviderIDs;

    }

    /**
     * @see CoreInternalFileServiceImpl#setActiveStorageProvider(String)
     */
    public void setActiveStorageProvider(final String storageProviderID) throws UserException {
        if (storageProviderID == null) {
            StorageProviderManager.activeStorageProviderID = null;
            StorageProviderManager.LOG.debug("No active storage provider is set now.");
        } else if (this.getStorageProvider(storageProviderID) != null) {
            StorageProviderManager.activeStorageProviderID = storageProviderID;
            StorageProviderManager.LOG.debug("\"{}\" was set as active storage provider.", storageProviderID);
        } else {
            throw new UserException("Storage provider \"" + storageProviderID
                + "\" is not available. Only available storage providers can be set as active storage provider.");
        }
    }

    /**
     * @see CoreInternalFileServiceImpl#getActiveStorageProvider()
     */
    public String getActiveStorageProvider() {
        if (StorageProviderManager.activeStorageProviderID == null) {
            StorageProviderManager.LOG.debug("No active storage provider is set.");
        } else {
            StorageProviderManager.LOG.debug("Active storage provider: {}",
                StorageProviderManager.activeStorageProviderID);
        }
        return StorageProviderManager.activeStorageProviderID;
    }

    /**
     * @see CoreInternalFileServiceImpl#getDefaultStorageProvider()
     */
    public String getDefaultStorageProvider() {
        StorageProviderManager.LOG.debug("Default storage provider: {}", this.DEFAULT_STORAGE_PROVIDER_ID);
        return this.DEFAULT_STORAGE_PROVIDER_ID;
    }

    /**
     * @see CoreInternalFileServiceImpl#getReadyStorageProviders()
     */
    public Set<String> getReadyStorageProviders() {
        StorageProviderManager.LOG.debug("Retrieving all ready storage providers...");
        final Set<String> readyStorageProviders = new HashSet<>();
        for (final String storageProviderID : this.getStorageProviders()) {
            if (this.isStorageProviderReady(storageProviderID)) {
                readyStorageProviders.add(storageProviderID);
            }
        }
        return readyStorageProviders;
    }

    /**
     * @see CoreInternalFileServiceImpl#getStorageProviderName(String)
     */
    public String getStorageProviderName(final String storageProviderID) {
        String storageProviderName = null;
        final ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
        if (storageProvider != null) {
            storageProviderName = storageProvider.getStorageProviderName();
        }
        return storageProviderName;
    }

    /**
     *
     * @see CoreInternalFileServiceImpl#isReadyStorageProvider(String)
     */
    public boolean isStorageProviderReady(final String storageProviderID) {

        if (this.getReadyStorageProvider(storageProviderID) != null) {
            return true;
        }

        return false;

    }

    /**
     * Checks if storage providers with {@code storageProviderIDs} are available and have no unsatisfied
     * requirements.
     *
     * @param storageProviderIDs of storage providers
     * @return IDs of storage providers that are not ready.
     *
     * @see #isStorageProviderReady(String)
     *
     */
    public Set<String> areStorageProvidersReady(final Set<String> storageProviderIDs) {

        final Set<String> idsOfNotReadyStorageProviders = new HashSet<>();

        for (final String storageProviderID : storageProviderIDs) {
            if (!this.isStorageProviderReady(storageProviderID)) {
                idsOfNotReadyStorageProviders.add(storageProviderID);
            }
        }

        return idsOfNotReadyStorageProviders;

    }

    /**
     * Retrieves file {@code fileRelToCSARRoot} of CSAR {@code csarID} on storage provider
     * {@code storageProviderID} to file {@code targetAbsPath}.
     *
     * @param csarID of CSAR
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @param targetAbsPath - local absolute path
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready,
     *         file {@code fileRelToCSARRoot} of CSAR {@code csarID} was not found on storage provider,
     *         creating or writing to file {@code targetAbsPath} failed or an error occurred during
     *         getting.
     */
    public void getFileOfCSAR(final CSARID csarID, final Path fileRelToCSARRoot, final String storageProviderID,
                    final Path targetAbsPath)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            storageProvider.getFile(relFilePathOnStorageProvider, targetAbsPath);

        } else {

            throw new SystemException("Can't get file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID
                + "\", because storage provider \"" + storageProviderID + "\" is not available and ready.");

        }

    }

    /**
     *
     * @param csarID of CSAR
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @return File {@code fileRelToCSARRoot} of CSAR {@code csarID} on storage provider
     *         {@code storageProviderID} as input stream.
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready,
     *         file {@code fileRelToCSARRoot} of CSAR {@code csarID} was not found on storage provider
     *         or an error occurred during getting.
     */
    public InputStream getFileOfCSARAsInputStream(final CSARID csarID, final Path fileRelToCSARRoot,
                    final String storageProviderID)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            final InputStream fileInputStream = storageProvider.getFileAsInputStream(relFilePathOnStorageProvider);

            return fileInputStream;

        } else {

            throw new SystemException("Can't get file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID
                + "\" as input stream, because storage provider \"" + storageProviderID
                + "\" is not available and ready.");

        }

    }

    /**
     * @param csarID of CSAR
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @return Size of file {@code fileRelToCSARRoot} of CSAR {@code csarID} on storage provider
     *         {@code storageProviderID} in bytes.
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready,
     *         file {@code fileRelToCSARRoot} of CSAR {@code csarID} was not found on storage provider
     *         or an error occurred during getting size.
     */
    public long getFileOfCSARSize(final CSARID csarID, final Path fileRelToCSARRoot, final String storageProviderID)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            final long fileSize = storageProvider.getFileSize(relFilePathOnStorageProvider);

            return fileSize;

        } else {

            throw new SystemException("Can't get file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID
                + "\" as input stream, because storage provider \"" + storageProviderID
                + "\" is not available and ready.");

        }

    }

    /**
     * Stores the file {@code absFilePath} as file {@code fileRelToCSARRoot} of CSAR {@code csarID} on
     * storage provider {@code storageProviderID}.
     *
     * @param absFilePath - absolute path of file
     * @param csarID of CSAR
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready
     *         or an error occurred during storing.
     */
    public void storeFileOfCSAR(final Path absFilePath, final CSARID csarID, final Path fileRelToCSARRoot,
                    final String storageProviderID)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            storageProvider.storeFile(absFilePath, relFilePathOnStorageProvider);

        } else {

            throw new SystemException("Can't store file \"" + absFilePath.toString() + "\" as file \""
                + fileRelToCSARRoot.toString() + "\" of CSAR \"" + csarID.toString() + "\", because storage provider \""
                + storageProviderID + "\" is not available and ready.");

        }

    }

    /**
     * Deletes the file {@code fileRelToCSARRoot} of CSAR {@code csarID} on storage provider
     * {@code storageProviderID}, if it exists.
     *
     * @param csarID of CSAR
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready
     *         or an error occurred during deleting.
     */
    public void deleteFileOfCSAR(final CSARID csarID, final Path fileRelToCSARRoot, final String storageProviderID)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            storageProvider.deleteFile(relFilePathOnStorageProvider);

        } else {

            throw new SystemException("Can't delete file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID.toString()
                + "\", because storage provider \"" + storageProviderID + "\" is not available and ready.");

        }

    }

    /**
     * Stores the file input stream {@code fileInputStream} as file {@code fileRelToCSARRoot} of CSAR
     * {@code csarID} on storage provider {@code storageProviderID}.
     *
     * @param csarID of CSAR
     * @param fileInputStream of file
     * @param fileSize - size of file
     * @param fileRelToCSARRoot - relative path to CSAR root of file
     * @param storageProviderID of storage provider
     * @throws SystemException if storage provider {@code storageProviderID} is not available and ready
     *         or an error occurred during storing.
     */
    public void storeFileOfCSAR(final CSARID csarID, final InputStream fileInputStream, final long fileSize,
                    final Path fileRelToCSARRoot, final String storageProviderID)
        throws SystemException {

        final ICoreInternalFileStorageProviderService storageProvider = this.getReadyStorageProvider(storageProviderID);

        if (storageProvider != null) {

            final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(csarID,
                fileRelToCSARRoot);

            storageProvider.storeFile(fileInputStream, fileSize, relFilePathOnStorageProvider);
        } else {

            throw new SystemException(
                "Can't store file input stream as file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID.toString()
                    + "\", because storage provider \"" + storageProviderID + "\" is not available and ready.");

        }

    }

    /**
     * @param storageProviderID of storage provider.
     * @return Storage provider {@code storageProvider} if it's ready, otherwise {@code null}.
     */
    private ICoreInternalFileStorageProviderService getReadyStorageProvider(final String storageProviderID) {

        StorageProviderManager.LOG.debug("Checking if storage provider \"{}\" is ready...", storageProviderID);

        final ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);

        if (storageProvider != null) {

            if (storageProvider.isStorageProviderReady()) {
                StorageProviderManager.LOG.debug("Storage provider \"{}\" is ready.", storageProviderID);
                return storageProvider;
            }

            StorageProviderManager.LOG.warn("Storage provider \"{}\" is not ready.", storageProviderID);

        }

        return null;

    }

    /**
     * @param storageProviderID of storage provider.
     * @return Storage provider {@code storageProviderID} if it's available, otherwise {@code null}.
     */
    private ICoreInternalFileStorageProviderService getStorageProvider(final String storageProviderID) {

        ICoreInternalFileStorageProviderService storageProvider;

        synchronized (StorageProviderManager.STORAGE_PROVIDERS) {
            storageProvider = StorageProviderManager.STORAGE_PROVIDERS.get(storageProviderID);
        }

        if (storageProvider != null) {
            StorageProviderManager.LOG.debug("Storage provider \"{}\" is available.", storageProviderID);
        } else {
            StorageProviderManager.LOG.debug("Storage provider \"{}\" is not available.", storageProviderID);
        }

        return storageProvider;

    }

    /**
     * Builds the relative path where file {@code relFilePathToCSARRoot} of CSAR {@code csarID} should
     * be stored / is located on a storage provider.<br />
     * <br />
     * Location of a file of a CSAR on a storage provider:<br />
     * {@code <csarID>/<relPathToCSARRootOfFile>}<br />
     * <br />
     * Note: On a blob store usually this path will be created in a container. Name of the container is
     * defined by the storage provider.
     *
     * @param csarID of CSAR
     * @param relFilePathToCSARRoot - relative path to CSAR root of file
     * @return Relative path where the CSAR file should be stored / is located.
     */
    private String buildFileOfCSARStorageProviderPath(final CSARID csarID, final Path relFilePathToCSARRoot) {
        final String relFilePathToStore = Paths.get(csarID.toString()).resolve(relFilePathToCSARRoot).toString();
        return relFilePathToStore;
    }

    /**
     * Binds a storage provider.
     *
     * @param storageProvider to bind
     */
    protected void bindCoreInternalFileStorageProviderService(
                    final ICoreInternalFileStorageProviderService storageProvider) {
        if (storageProvider != null) {

            final String storageProviderID = storageProvider.getStorageProviderID();

            StorageProviderManager.STORAGE_PROVIDERS.put(storageProviderID, storageProvider);
            StorageProviderManager.LOG.debug("Storage provider \"{}\" ({}) bound.", storageProviderID,
                storageProvider.getStorageProviderName());
        } else {
            StorageProviderManager.LOG.warn("Binding a storage provider failed.");
        }
    }

    /**
     * Unbinds a storage provider.<br />
     * If the active storage provider will be unbound, no active storage provider is set (setting will
     * be cleared).
     *
     * @param storageProvider to unbind
     * @see CoreInternalFileServiceImpl#setActiveStorageProvider(String)
     */
    protected void unbindCoreInternalFileStorageProviderService(
                    final ICoreInternalFileStorageProviderService storageProvider) {

        if (storageProvider != null) {

            final String storageProviderID = storageProvider.getStorageProviderID();

            StorageProviderManager.STORAGE_PROVIDERS.remove(storageProviderID);
            StorageProviderManager.LOG.debug("Storage provider \"{}\" ({}) unbound.", storageProviderID,
                storageProvider.getStorageProviderName());

            // if active storage provider was unbound clear active storage
            // provider setting
            if (this.getActiveStorageProvider() != null && this.getActiveStorageProvider().equals(storageProviderID)) {
                StorageProviderManager.activeStorageProviderID = null;
                StorageProviderManager.LOG.debug(
                    "Active storage provider \"{}\" is not more available. Thus, active storage provider was unset.",
                    storageProviderID);
            }

        } else {
            StorageProviderManager.LOG.warn("Unbinding a storage provider failed.");
        }

    }
}
