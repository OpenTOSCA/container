package org.opentosca.core.model;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.core.common.SystemException;
import org.opentosca.core.model.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.service.IFileAccessService;
import org.opentosca.core.service.internal.ICoreInternalFileStorageProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a file in a CSAR. This file can be stored at an arbitrary storage
 * provider. Provides methods to get it's meta data using the local stored meta
 * data and fetch the file from the appropriate storage provider.
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
	 * ID of the storage provider on which this file is stored.
	 *
	 * @see ICoreInternalFileStorageProviderService
	 *
	 */
	private final String STORAGE_PROVIDER_ID;

	/**
	 * Bound, available storage providers.<br />
	 * <br />
	 * Note: Must be {@code static}, because otherwise, if we instantiate this
	 * class manually with {@code new} and not (automatically) by the OSGi
	 * framework, this variable would be not set.
	 */
	private static Map<String, ICoreInternalFileStorageProviderService> STORAGE_PROVIDERS = Collections.synchronizedMap(new HashMap<String, ICoreInternalFileStorageProviderService>());

	/**
	 * Must be {@code static}, because this class will be instantiated with
	 * {@code new}.
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
	 * @param storageProviderID of storage provider on which this file is
	 *            stored.
	 *
	 * @see ICoreInternalFileStorageProviderService
	 */
	public CSARFile(final String relFilePathToCSARRoot, final CSARID csarID, final String storageProviderID) {
		super(relFilePathToCSARRoot);
		this.CSAR_ID = csarID;
		this.STORAGE_PROVIDER_ID = storageProviderID;
	}

	/**
	 * @param storageProviderID of storage provider to check if it's ready.
	 * @return {@code true} if storage provider {@code storageProviderID} is
	 *         available and has no unsatisfied requirements, so it can be used.
	 *         Otherwise {@code false} will be returned.<br />
	 *         Usually a requirement of a storage provider are the credentials
	 *         (exception: file system storage provider) which must be set in
	 *         the storage provider.
	 */
	private boolean isStorageProviderReady(final String storageProviderID) {

		CSARFile.LOG.debug("Checking if storage provider \"{}\" is ready...", storageProviderID);

		final ICoreInternalFileStorageProviderService storageProvider = CSARFile.STORAGE_PROVIDERS.get(storageProviderID);

		if (storageProvider != null) {
			if (storageProvider.isStorageProviderReady()) {
				CSARFile.LOG.debug("Storage provider \"{}\" is ready.", storageProviderID);
				return true;
			} else {
				CSARFile.LOG.warn("Storage provider \"{}\" is not ready.", storageProviderID);
			}
		} else {
			CSARFile.LOG.warn("Storage provider \"{}\" is not available.", storageProviderID);
		}

		return false;

	}

	/**
	 * @throws SystemException if required storage provider is not available and
	 *             ready, file was not found on storage provider or an error
	 *             occurred during retrieving.
	 */
	@Override
	public Path getFile() throws SystemException {

		if (this.isStorageProviderReady(this.STORAGE_PROVIDER_ID)) {

			final ICoreInternalFileStorageProviderService storageProvider = CSARFile.STORAGE_PROVIDERS.get(this.STORAGE_PROVIDER_ID);

			final Path targetFile = CSARFile.fileAccessService.getTemp().toPath().resolve(this.getName());

			final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(this.CSAR_ID, this.getPath());

			storageProvider.getFile(relFilePathOnStorageProvider, targetFile);

			return targetFile;

		} else {
			throw new SystemException("Can't retrieve file \"" + this.getPath() + "\" of CSAR \"" + this.CSAR_ID + "\", because storage provider \"" + this.STORAGE_PROVIDER_ID + "\" is not available and ready.");
		}

	}

	/**
	 * @throws SystemException if required storage provider is not available and
	 *             ready, file was not found on storage provider or an error
	 *             occurred during getting.
	 */
	@Override
	public InputStream getFileAsInputStream() throws SystemException {

		if (this.isStorageProviderReady(this.STORAGE_PROVIDER_ID)) {

			final ICoreInternalFileStorageProviderService storageProvider = CSARFile.STORAGE_PROVIDERS.get(this.STORAGE_PROVIDER_ID);

			final String relFilePathOnStorageProvider = this.buildFileOfCSARStorageProviderPath(this.CSAR_ID, this.getPath());

			InputStream fileInputStream;

			fileInputStream = storageProvider.getFileAsInputStream(relFilePathOnStorageProvider);

			return fileInputStream;

		} else {
			throw new SystemException("Can't retrieve file \"" + this.getPath() + "\" of CSAR \"" + this.CSAR_ID + "\" as input stream, because storage provider \"" + this.STORAGE_PROVIDER_ID + "\" is not ready.");
		}

	}

	/**
	 * Builds the relative path where file {@code relFilePathToCSARRoot} of CSAR
	 * {@code csarID} should be stored / is located on a storage provider.<br />
	 * <br />
	 * Location of a file of a CSAR on a storage provider:<br />
	 * {@code <csarID>/<relPathToCSARRootOfFile>}<br />
	 * <br />
	 * Note: On a blob store usually this path will be created in a container.
	 * Name of the container is defined by the storage provider.
	 *
	 * @param csarID of CSAR
	 * @param relFilePathToCSARRoot - relative path to CSAR root of file
	 * @return Relative path where the CSAR file should be stored / is located.
	 */
	private String buildFileOfCSARStorageProviderPath(final CSARID csarID, final String relFilePathToCSARRoot) {
		final String relFilePathToStore = Paths.get(csarID.toString()).resolve(relFilePathToCSARRoot).toString();
		return relFilePathToStore;
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
		return "File \"" + this.getPath() + "\" of CSAR \"" + this.CSAR_ID + "\" on storage provider \"" + this.STORAGE_PROVIDER_ID + "\".";
	}

	@Override
	public boolean equals(final Object file) {
		if (file instanceof CSARFile) {
			final CSARFile csarFile = (CSARFile) file;
			if (this.getPath().equals(csarFile.getPath()) && this.CSAR_ID.equals(csarFile.CSAR_ID) && this.STORAGE_PROVIDER_ID.equals(csarFile.STORAGE_PROVIDER_ID)) {
				return true;
			}
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

	/**
	 * Binds a File Storage Provider.
	 *
	 * @param storageProvider to bind
	 */
	protected void bindCoreInternalFileStorageProviderService(final ICoreInternalFileStorageProviderService storageProvider) {
		if (storageProvider != null) {
			CSARFile.STORAGE_PROVIDERS.put(storageProvider.getStorageProviderID(), storageProvider);
			CSARFile.LOG.debug("Storage provider \"{}\" ({}) bound.", storageProvider.getStorageProviderID(), storageProvider.getStorageProviderName());
		} else {
			CSARFile.LOG.warn("Binding a storage provider failed.");
		}
	}

	/**
	 * Unbinds a File Storage Provider.
	 *
	 * @param storageProvider to unbind
	 */
	protected void unbindCoreInternalFileStorageProviderService(final ICoreInternalFileStorageProviderService storageProvider) {
		if (storageProvider != null) {
			final String storageProviderID = storageProvider.getStorageProviderID();
			CSARFile.STORAGE_PROVIDERS.remove(storageProviderID);
			CSARFile.LOG.debug("Storage provider \"{}\" ({}) unbound.", storageProvider.getStorageProviderID(), storageProvider.getStorageProviderName());
		} else {
			CSARFile.LOG.warn("Unbinding a storage provider failed.");
		}
	}

}
