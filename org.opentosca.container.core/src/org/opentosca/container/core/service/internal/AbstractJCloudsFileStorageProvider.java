package org.opentosca.container.core.service.internal;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.domain.Location;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.osgi.ApiListener;
import org.jclouds.osgi.MetadataBundleListener;
import org.jclouds.osgi.ProviderListener;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.credentials.Credentials;
import org.opentosca.container.core.service.PathUtils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Abstract storage provider implementation for all jclouds providers / APIs (blobstores). A storage
 * provider realized with this implementation is ready, if it has credentials (requirement only
 * applies if method {@link #needsCredentials()} returns {@code true}) and the appropriate jclouds
 * Provider / API bundle is available (bundle state {@code ACTIVE}).
 *
 * A CSAR file will be stored as follows on the blobstore:
 * {@code <containerName>/<csarID>/<relPathOfFileToCSARRoot>} The container is a namespace for the
 * files, on Amazon S3 it's called bucket. By default, the container name is
 * {@code org.opentosca.csars}. Setting a new bucket name or getting the current one is possible
 * with appropriate methods.
 *
 * If a storage provider will be implemented that is supported by jclouds it should be realized by
 * extending from this class.
 */
public abstract class AbstractJCloudsFileStorageProvider implements ICoreInternalFileStorageProviderService,
                                                         ProviderListener, ApiListener {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractJCloudsFileStorageProvider.class);

    private Long credentialsID = null;

    /**
     * Identity as part of the credentials of a jclouds storage provider. By default no identity is set.
     */
    private String credentialsIdentity = null;

    /**
     * Key as part of the credentials of a jclouds storage provider. By default no key is set.
     */
    private String credentialsKey = null;

    /**
     * Name of the container of a jclouds storage provider. It's predefined, but can be changed using
     * {@link #setContainerName(String)}.
     */
    private String containerName = "org.opentosca.csars";

    /**
     * jclouds {@code BlobStoreContext} and {@code BlobStore}. These variables are global, as we do not
     * want to create them each time a method is called.
     */
    private BlobStoreContext blobStoreContext;
    private BlobStore blobStore;

    /**
     * {@code true} when storage provider is currently initialized.
     */
    private boolean isInitialized = false;

    /**
     * {@code true} when required jclouds BlobStore API / Provider bundle is currently available.
     */
    private boolean isJCloudsBundleAvailable = false;

    /**
     * jclouds module to use SLF4J facade for logging.
     */
    Module slf4jLoggingModule = new SLF4JLoggingModule();

    /**
     * jclouds modules that should be loaded beside the default modules during initialization of the
     * storage provider.
     */
    ImmutableSet<Module> modules = new ImmutableSet.Builder<Module>().add(this.slf4jLoggingModule)
                                                                     .addAll(this.getJCloudsModules()).build();


    /**
     * Constructor.<br />
     * Adds this class as a jclouds Provider and API listener. We need these listeners, because we want
     * to know when the necessary jclouds Provider / API bundle is available.
     */
    public AbstractJCloudsFileStorageProvider() {
        final MetadataBundleListener jcloudsBundleListener = new MetadataBundleListener();
        jcloudsBundleListener.addProviderListener(this);
        jcloudsBundleListener.addApiListenerListener(this);
        // Manually stopping bundle listener is not necessary, because it will
        // be automatically stopped when bundle is stopped.
        jcloudsBundleListener.start(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
    }

    /**
     * Initializes this storage provider, if necessary.<br />
     * Builds the {@link BlobStoreContext} and {@link BlobStore} and finally creates the container on
     * the storage provider, if necessary.
     *
     * @throws SystemException if storage provider is not ready, credentials are invalid or an other
     *         jclouds error occurred.
     */
    private void initialize() throws SystemException {

        if (!this.isInitialized) {

            if (this.isStorageProviderReady()) {

                AbstractJCloudsFileStorageProvider.LOG.debug("Initialize storage provider \"{}\"...",
                    this.getStorageProviderID());

                try {
                    final ContextBuilder contextBuilder = ContextBuilder.newBuilder(this.getStorageProviderID());
                    if (this.needsCredentials()) {
                        contextBuilder.credentials(this.credentialsIdentity, this.credentialsKey);
                    }
                    contextBuilder.modules(this.modules);
                    contextBuilder.overrides(this.overwriteJCloudsProperties());
                    this.blobStoreContext = contextBuilder.build(BlobStoreContext.class);

                    this.blobStore = this.blobStoreContext.getBlobStore();
                    AbstractJCloudsFileStorageProvider.LOG.debug(
                        "Creating container \"{}\" on storage provider \"{}\"...", this.getContainerName(),
                        this.getStorageProviderID());
                    final boolean isContainerCreated = this.blobStore.createContainerInLocation(
                        this.getContainerLocation(), this.getContainerName());

                    if (isContainerCreated) {
                        AbstractJCloudsFileStorageProvider.LOG.debug(
                            "Container \"{}\" was created on storage provider \"{}\".", this.getContainerName(),
                            this.getStorageProviderID());
                    } else {
                        AbstractJCloudsFileStorageProvider.LOG.debug(
                            "Container \"{}\" already exists on storage provider \"{}\" and was created with the provided credentials.",
                            this.getContainerName(), this.getStorageProviderID());
                    }

                    this.isInitialized = true;
                    AbstractJCloudsFileStorageProvider.LOG.debug("Initialization of storage provider \"{}\" completed.",
                        this.getStorageProviderID());

                } catch (final AuthorizationException exc) {
                    this.close();
                    throw new SystemException(
                        "Credentials of storage provider \"" + this.getStorageProviderID() + "\" are invalid.", exc);
                } catch (final Exception exc) {
                    this.close();
                    throw new SystemException("A jclouds error occured.", exc);
                }

            } else {

                throw new SystemException("Can't initialize storage provider \"" + this.getStorageProviderID()
                    + "\", because it's not ready!");

            }

        } else {
            AbstractJCloudsFileStorageProvider.LOG.debug("Storage provider \"{}\" is already initialized.",
                this.getStorageProviderID());
        }

    }

    @Override
    public final void storeFile(final Path absFilePath, final String relFilePathOnProvider) throws SystemException {

        this.initialize();

        AbstractJCloudsFileStorageProvider.LOG.debug("Storing file \"{}\" as \"{}\" on storage provider \"{}\"...",
            absFilePath, relFilePathOnProvider, this.getStorageProviderID());

        if (Files.isRegularFile(absFilePath)) {

            try {

                BlobBuilder blobBuilder = this.blobStore.blobBuilder(PathUtils.separatorsToUnix(relFilePathOnProvider));
                blobBuilder = blobBuilder.payload(absFilePath.toFile());
                final Blob blob = blobBuilder.build();
                this.blobStore.putBlob(this.getContainerName(), blob);

                AbstractJCloudsFileStorageProvider.LOG.debug(
                    "Storing file \"{}\" as \"{}\" on storage provider \"{}\" completed.", absFilePath,
                    relFilePathOnProvider, this.getStorageProviderID());

            } catch (final Exception exc) {

                throw new SystemException("A jclouds error occured.", exc);

            }

        } else {

            throw new SystemException("\"{}\" is not an absolute path to an existing file.");

        }

    }

    @Override
    public final void storeFile(final InputStream fileInputStream, final long fileSize,
                    final String relFilePathOnProvider)
        throws SystemException {

        AbstractJCloudsFileStorageProvider.LOG.debug(
            "Storing input stream as file \"{}\" on storage provider \"{}\"...", relFilePathOnProvider,
            this.getStorageProviderID());

        this.initialize();

        try {

            final BlobBuilder blobBuilder = this.blobStore.blobBuilder(
                PathUtils.separatorsToUnix(relFilePathOnProvider));
            // setting content length is necessary if payload is given by an
            // input stream
            blobBuilder.payload(fileInputStream).contentLength(fileSize);

            final Blob blob = blobBuilder.build();

            this.blobStore.putBlob(this.getContainerName(), blob);

            AbstractJCloudsFileStorageProvider.LOG.debug(
                "Storing input stream as file \"{}\" on storage provider \"{}\" completed.", relFilePathOnProvider,
                this.getStorageProviderID());

        } catch (final Exception exc) {
            throw new SystemException("A jclouds error occured.", exc);
        }

    }

    /**
     * Uninitializes this storage provider by closing the {@link BlobStoreContext}.<br />
     */
    private void close() {

        AbstractJCloudsFileStorageProvider.LOG.debug("Closing storage provider \"{}\"...", this.getStorageProviderID());

        if (this.blobStoreContext != null) {
            this.blobStoreContext.close();
        }

        this.isInitialized = false;

        AbstractJCloudsFileStorageProvider.LOG.debug("Closing storage provider \"{}\" completed.",
            this.getStorageProviderID());

    }

    @Override
    public final void deleteCredentials() {

        if (this.isInitialized) {
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "Storage provider \"{}\" is initialized. For deleting credentials it will be closed now.");
            this.close();
        }

        AbstractJCloudsFileStorageProvider.LOG.debug("Deleting credentials in storage provider \"{}\"...",
            this.getStorageProviderID());
        this.credentialsID = null;
        this.credentialsIdentity = null;
        this.credentialsKey = null;
        AbstractJCloudsFileStorageProvider.LOG.debug("Deleting credentials in storage provider \"{}\" completed.",
            this.getStorageProviderID());

    }

    @Override
    public final void deleteFile(final String relFilePathOnProvider) throws SystemException {

        AbstractJCloudsFileStorageProvider.LOG.debug("Deleting file \"{}\" on storage provider \"{}\"...",
            relFilePathOnProvider, this.getStorageProviderID());

        this.initialize();

        try {

            this.blobStore.removeBlob(this.getContainerName(), PathUtils.separatorsToUnix(relFilePathOnProvider));
            AbstractJCloudsFileStorageProvider.LOG.debug("Deleting file \"{}\" on storage provider \"{}\" completed.",
                relFilePathOnProvider, this.getStorageProviderID());

        } catch (final Exception exc) {

            throw new SystemException("A jclouds error occured.", exc);

        }

    }

    @Override
    public final InputStream getFileAsInputStream(final String relFilePathOnProvider) throws SystemException {

        AbstractJCloudsFileStorageProvider.LOG.debug(
            "Getting input stream of file \"{}\" on storage provider \"{}\" ...", relFilePathOnProvider,
            this.getStorageProviderID());

        this.initialize();

        try {

            final Blob blob = this.blobStore.getBlob(this.getContainerName(),
                PathUtils.separatorsToUnix(relFilePathOnProvider));

            if (blob == null) {
                throw new SystemException("File \"" + relFilePathOnProvider + "\" was not found on storage provider \""
                    + this.getStorageProviderID() + "\".");
            }

            final InputStream blobInputStream = blob.getPayload().getInput();

            AbstractJCloudsFileStorageProvider.LOG.debug(
                "Getting input stream of \"{}\" on storage provider \"{}\" completed.", relFilePathOnProvider,
                this.getStorageProviderID());

            return blobInputStream;

        } catch (final Exception exc) {

            throw new SystemException("A jclouds error occured.", exc);

        }

    }

    @Override
    public final void getFile(final String relFilePathOnProvider, final Path targetAbsFilePath) throws SystemException {

        AbstractJCloudsFileStorageProvider.LOG.debug("Retrieving file \"{}\" on storage provider \"{}\" ...",
            relFilePathOnProvider, this.getStorageProviderID());

        this.initialize();

        AbstractJCloudsFileStorageProvider.LOG.debug("Location of file after it's fetched: {}", targetAbsFilePath);

        OutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {

            fileOutputStream = Files.newOutputStream(targetAbsFilePath);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            try {

                final Blob blob = this.blobStore.getBlob(this.getContainerName(),
                    PathUtils.separatorsToUnix(relFilePathOnProvider));

                if (blob == null) {
                    throw new SystemException("File \"" + relFilePathOnProvider
                        + "\" was not found on storage provider \"" + this.getStorageProviderID() + "\".");
                }

                blob.getPayload().writeTo(bufferedOutputStream);

            } catch (final Exception exc) {
                throw new SystemException("A jclouds error occured.", exc);
            }

            AbstractJCloudsFileStorageProvider.LOG.debug("Retrieving file \"{}\" on storage provider \"{}\" completed.",
                relFilePathOnProvider, this.getStorageProviderID());

        } catch (final FileNotFoundException exc) {

            throw new SystemException("Can't create file \"" + targetAbsFilePath.toString() + "\".", exc);

        } catch (final IOException exc) {

            throw new SystemException("An IO Exception occured.", exc);

        } finally {

            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (final IOException exc) {
                    AbstractJCloudsFileStorageProvider.LOG.warn("An IO Exception occured.", exc);
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (final IOException exc) {
                    AbstractJCloudsFileStorageProvider.LOG.warn("An IO Exception occured.", exc);
                }
            }

        }

    }

    @Override
    public final long getFileSize(final String relFilePathOnProvider) throws SystemException {

        AbstractJCloudsFileStorageProvider.LOG.debug("Getting size of file \"{}\" on storage provider \"{}\" ...",
            relFilePathOnProvider, this.getStorageProviderID());

        this.initialize();

        long contentLength;

        try {

            final BlobMetadata blobMetadata = this.blobStore.blobMetadata(this.getContainerName(),
                PathUtils.separatorsToUnix(relFilePathOnProvider));

            if (blobMetadata == null) {
                throw new SystemException("File \"" + relFilePathOnProvider + "\" was not found on storage provider \""
                    + this.getStorageProviderID() + "\".");
            }

            contentLength = blobMetadata.getContentMetadata().getContentLength();

        } catch (final Exception exc) {
            throw new SystemException("A jclouds error occured.", exc);
        }

        AbstractJCloudsFileStorageProvider.LOG.debug("Size of file \"{}\" on storage provider \"{}\": {} bytes",
            relFilePathOnProvider, this.getStorageProviderID(), contentLength);

        return contentLength;
    }

    /**
     * @return {@inheritDoc}<br />
     *         Note: The ID must be equal to the ID of the used jclouds Provider / API, otherwise the
     *         initialization of the storage provider fails.
     */
    @Override
    public abstract String getStorageProviderID();

    /**
     * @return {@code true} when required jclouds Provider / API bundle is available (bundle state
     *         {@code ACTIVE}), otherwise {@code false}.
     */
    private boolean isJCloudsBundleAvailable() {
        if (this.isJCloudsBundleAvailable) {
            AbstractJCloudsFileStorageProvider.LOG.debug("jclouds bundle for storage provider \"{}\" is available.",
                this.getStorageProviderID());
        } else {
            AbstractJCloudsFileStorageProvider.LOG.warn("jclouds bundle for storage provider \"{}\" is not available.",
                this.getStorageProviderID());
        }
        return this.isJCloudsBundleAvailable;
    }

    @Override
    public final boolean isStorageProviderReady() {

        if ((!this.needsCredentials() || this.getCredentialsID() != null) && this.isJCloudsBundleAvailable()) {
            return true;
        }

        return false;

    }

    @Override
    public final void setCredentials(final Credentials credentials) {

        if (this.isInitialized) {
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "Storage provider \"{}\" is initialized. For storing credentials it will be closed now.",
                this.getStorageProviderID());
            this.close();
        }

        this.credentialsID = credentials.getID();
        this.credentialsIdentity = credentials.getIdentity();
        this.credentialsKey = credentials.getKey();

    }

    /**
     * Sets a new container name in this storage provider.<br />
     * <br />
     * Note: If you have stored files on this storage provider and then set a new container name, these
     * files can't be found anymore, because the storage provider searches for the files in the new
     * bucket. Thus, for getting access to these files again, you must set the previous container name.
     *
     * @param containerName to set
     */
    protected final void setContainerName(final String containerName) {
        if (this.isInitialized) {
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "Storage provider \"{}\" is initialized. For setting new container name it will be closed now.",
                this.getStorageProviderID());
            this.close();
        }
        AbstractJCloudsFileStorageProvider.LOG.debug("Setting container name \"{}\" in storage provider \"{}\"...",
            containerName, this.getStorageProviderID());
        this.containerName = containerName;
        AbstractJCloudsFileStorageProvider.LOG.debug(
            "Setting container name \"{}\" in storage provider \"{}\" completed.", containerName,
            this.getStorageProviderID());
    }

    /**
     *
     * @return Container name of this storage provider.
     */
    protected String getContainerName() {
        return this.containerName;
    }

    /**
     * @return Location of the container of this storage provider.<br />
     *         If you not overwrite this method, the default container location (defined by the
     *         provider) will be used.
     */
    protected Location getContainerLocation() {
        return null;
    }

    @Override
    public final Long getCredentialsID() {
        return this.credentialsID;
    }

    /**
     * @return jclouds modules that should be loaded beside the default modules during initialization of
     *         the storage provider. The SLF4J logging module will be loaded always.<br />
     *         If you not overwrite this method, no further modules will be loaded.
     */
    protected Iterable<? extends Module> getJCloudsModules() {

        return Collections.<Module>emptyList();

    }

    /**
     *
     * @return jclouds properties to overwrite the pre-defined jclouds properties. These properties will
     *         be passed during initialization of the storage provider.<br />
     *         If you not overwrite this method, no jclouds properties will be overwritten.
     */
    protected Properties overwriteJCloudsProperties() {
        return new Properties();
    }

    /**
     * Called when ANY jclouds API bundle goes available respectively is in bundle state
     * {@code ACTIVE}.<br />
     * If the bundle is the required bundle for this storage provider, {@link #isJCloudsBundleAvailable}
     * will be set to {@code true}.
     */
    @Override
    public final <A extends ApiMetadata> void added(final A apiMetadata) {
        final String jcloudsApiID = apiMetadata.getId();

        if (jcloudsApiID.equals(this.getStorageProviderID())) {
            this.isJCloudsBundleAvailable = true;
            AbstractJCloudsFileStorageProvider.LOG.debug("jclouds API bundle of storage provider \"{}\" is available.",
                this.getStorageProviderID());
        }

    }

    /**
     * Called when ANY jclouds API bundle goes unavailable.<br />
     * If the bundle is the required bundle for this storage provider, {@link #isJCloudsBundleAvailable}
     * will be set to {@code false}.
     */
    @Override
    public final <A extends ApiMetadata> void removed(final A apiMetadata) {
        final String jcloudsApiID = apiMetadata.getId();

        if (jcloudsApiID.equals(this.getStorageProviderID())) {
            this.isJCloudsBundleAvailable = false;
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "JClouds API bundle of storage provider \"{}\" is not more available.", this.getStorageProviderID());
        }

    }

    /**
     * Called when ANY jclouds Provider bundle goes available respectively is in bundle state
     * {@code ACTIVE}.<br />
     * If the bundle is the required bundle for this storage provider, {@link #isJCloudsBundleAvailable}
     * will be set to {@code true}.
     */
    @Override
    public final <P extends ProviderMetadata> void added(final P providerMetadata) {
        final String jcloudsProviderID = providerMetadata.getId();

        if (jcloudsProviderID.equals(this.getStorageProviderID())) {
            this.isJCloudsBundleAvailable = true;
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "JClouds provider bundle of storage provider \"{}\" is available.", this.getStorageProviderID());
        }

    }

    /**
     * Called when ANY jclouds Provider bundle goes unavailable.<br />
     * If the bundle is the required bundle for this storage provider, {@link #isJCloudsBundleAvailable}
     * will be set to {@code false}.
     */
    @Override
    public final <P extends ProviderMetadata> void removed(final P providerMetadata) {
        final String jcloudsProviderID = providerMetadata.getId();

        if (jcloudsProviderID.equals(this.getStorageProviderID())) {
            this.isJCloudsBundleAvailable = false;
            AbstractJCloudsFileStorageProvider.LOG.debug(
                "JClouds provider bundle of storage provider \"{}\" is not more available.",
                this.getStorageProviderID());
        }

    }

}
