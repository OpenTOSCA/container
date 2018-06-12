package org.opentosca.container.core.service;

import java.util.Set;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.credentials.Credentials;

/**
 * This interface of the Core Internal Credentials Service defines methods for storing, getting and
 * deleting credentials, checking if credentials are set in a storage provider, setting credentials
 * in a storage provider, getting the name of the credentials identity and key of a storage
 * provider, and getting all available storage providers.
 */
public interface ICoreCredentialsService {

    /**
     * Stores credentials {@code credentials}.<br />
     * Also injects the credentials in the appropriate storage provider if the following conditions are
     * fulfilled:<br />
     * - Storage provider is available.<br />
     * - Storage provider has no injected credentials.<br />
     * <br />
     * Note: Credentials will be only stored if storage provider of credentials needs credentials. If
     * storage provider is not available, we can't check if this requirement is fulfilled.
     *
     * @param credentials to store.
     *
     * @return Generated ID of stored credentials.
     * @throws UserException if {@code credentials} contains missing required data, are already stored
     *         according to unique constraints defined in {@link Credentials} or storage provider of
     *         credentials needs no credentials.
     */
    public long storeCredentials(Credentials credentials) throws UserException;

    /**
     * @param credentialsID of credentials.
     * @return {@link Credentials} with ID {@code credentialsID}.
     * @throws UserException if credentials were not found.
     */
    public Credentials getCredentials(long credentialsID) throws UserException;

    /**
     * @return IDs of all stored credentials.
     */
    public Set<Long> getCredentialsIDs();

    /**
     * @param storageProviderID of storage provider
     * @return All stored {@link Credentials} for storage provider {@code storageProviderID}.
     */
    public Set<Credentials> getAllCredentialsOfStorageProvider(String storageProviderID);

    /**
     * @return All stored {@link Credentials}.
     */
    public Set<Credentials> getAllCredentials();

    /**
     * Deletes credentials {@code credentialsID}.<br />
     * If necessary, also deletes these credentials in their storage provider.
     *
     * @param credentialsID of credentials.
     *
     * @throws UserException if credentials to delete were not found.
     */
    public void deleteCredentials(long credentialsID) throws UserException;

    /**
     * Deletes all stored credentials.<br />
     * Also deletes the credentials in their storage providers, if necessary.
     */
    public void deleteAllCredentials();

    /**
     * Sets / injects stored credentials {@code credentialsID} in their storage provider.
     *
     * @param credentialsID of credentials.
     * @throws SystemException if storage provider of credentials is not available.
     * @throws UserException if credentials were not found or storage provider of credentials
     *         {@code credentialsID} needs no credentials.
     */
    public void setCredentialsInStorageProvider(long credentialsID) throws UserException, SystemException;

    /**
     * Deletes credentials in storage provider {@code storageProviderID}, if necessary.
     *
     * @param storageProviderID of storage provider.
     * @throws SystemException if storage provider {@code storageProviderID} is not available.
     */
    public void deleteCredentialsInStorageProvider(String storageProviderID) throws SystemException;

    /**
     * @param storageProviderID of storage provider
     * @return {@code true} if storage provider {@code storageProviderID} has currently credentials,
     *         otherwise {@code false}.
     * @throws SystemException if storage provider {@code storageProviderID} is not available.
     */
    public boolean hasStorageProviderCredentials(String storageProviderID) throws SystemException;

    /**
     * @param credentialsID of credentials.
     * @return {@code true} if storage provider of credentials {@code credentialsID} has these
     *         credentials, otherwise {@code false}.<br />
     *         Also {@code false} will be returned if storage provider of credentials
     *         {@code credentialsID} is not available.
     *
     * @UserException if credentials {@code credentialsID} were not found.
     */
    public boolean hasStorageProviderCredentials(long credentialsID) throws UserException;

    /**
     * @param storageProviderID of storage provider
     * @return {@code true} if storage provider {@code storageProviderID} needs credentials, otherwise
     *         {@code false}.
     * @throws SystemException if storage provider {@code storageProviderID} is not available.
     */
    public boolean needsStorageProviderCredentials(String storageProviderID) throws SystemException;

    /**
     * @param storageProviderID of storage provider
     * @return Name of the credentials identity of storage provider {@code storageProviderID}, e.g. for
     *         AWS S3 it's the Access Key ID.
     * @throws SystemException if storage provider {@code storageProviderID} is not available.
     */
    public String getCredentialsIdentityName(String storageProviderID) throws SystemException;

    /**
     * @param storageProviderID of storage provider
     * @return Name of the credentials key of storage provider {@code storageProviderID}, e.g. for AWS
     *         S3 it's the Secret Access Key.
     * @throws SystemException if storage provider {@code storageProviderID} is not available.
     */
    public String getCredentialsKeyName(String storageProviderID) throws SystemException;

    /**
     * @return IDs of available storage providers.<br />
     *         A storage provider is defined as available if it's bundle is installed and is in bundle
     *         state {@code ACTIVE}.
     */
    public Set<String> getStorageProviders();

}
