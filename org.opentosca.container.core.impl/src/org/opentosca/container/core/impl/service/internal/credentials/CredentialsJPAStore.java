package org.opentosca.container.core.impl.service.internal.credentials;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.internal.CoreInternalCredentialsServiceImpl;
import org.opentosca.container.core.model.credentials.Credentials;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages credentials in the database by using Eclipse Link (JPA).
 */
public class CredentialsJPAStore {

    private final static Logger LOG = LoggerFactory.getLogger(CredentialsJPAStore.class);

    private EntityManager em;


    /**
     * Initializes JPA.
     */
    private void initJPA() {
        if (this.em == null) {
            this.em = EntityManagerProvider.createEntityManager();
        }
    }

    /**
     * Destructor. This method is called when the garbage collector destroys the class. We will then
     * manually close the EntityManager / Factory and pass control back.
     */
    @Override
    protected void finalize() throws Throwable {
        this.em.close();
        super.finalize();
    }

    /**
     * Persists the credentials {@code credentials}.
     *
     * @param credentials
     *
     * @return Generated ID of credentials.
     * @throws UserException if {@code credentials} are already stored according to unique constraints
     *         defined in {@link Credentials}.
     */
    public long storeCredentials(final Credentials credentials) throws UserException {

        final String storageProviderID = credentials.getStorageProviderID();

        CredentialsJPAStore.LOG.debug("Storing credentials for storage provider \"{}\"...", storageProviderID);

        initJPA();

        try {

            this.em.getTransaction().begin();
            this.em.persist(credentials);
            this.em.getTransaction().commit();

        }
        catch (final Exception exc) {
            // check if exception chain contains exception that indicates a
            // constraint violation
            for (Throwable excCause = exc.getCause(); excCause != null; excCause = excCause.getCause()) {
                if (excCause instanceof SQLIntegrityConstraintViolationException) {
                    throw new UserException(
                        "Credentials for storage provider \"" + storageProviderID + "\" are already stored.");
                }
            }
        }

        final long credentialsID = credentials.getID();

        CredentialsJPAStore.LOG.debug("Storing credentials for storage provider \"{}\" completed. Generated ID of credentials: \"{}\"",
                                      storageProviderID, credentialsID);

        return credentialsID;
    }

    /**
     * @see CoreInternalCredentialsServiceImpl#getCredentials(String, String)
     */
    public Credentials getCredentials(final Long credentialsID) throws UserException {

        CredentialsJPAStore.LOG.debug("Retrieving credentials \"{}\"...", credentialsID);

        Credentials credentials = null;

        initJPA();

        final Query getCredentialsQuery = this.em.createNamedQuery(Credentials.getCredentialsByID);
        getCredentialsQuery.setParameter("id", credentialsID);

        try {
            credentials = (Credentials) getCredentialsQuery.getSingleResult();
            CredentialsJPAStore.LOG.debug("Credentials \"{}\" were found.", credentialsID);
            return credentials;
        }
        catch (final NoResultException exc) {
            throw new UserException("Credentials \"" + credentialsID + "\" were not found.");
        }

    }

    /**
     * @see CoreInternalCredentialsServiceImpl#getAllCredentialsOfStorageProvider(String)
     */
    public Set<Credentials> getAllCredentialsOfStorageProvider(final String storageProviderID) {

        CredentialsJPAStore.LOG.debug("Retrieving all credentials for storage provider \"{}\"...", storageProviderID);

        initJPA();

        final Query getAllCredentialsByStorageProviderIDQuery =
            this.em.createNamedQuery(Credentials.getAllCredentialsByStorageProviderID);
        getAllCredentialsByStorageProviderIDQuery.setParameter("storageProviderID", storageProviderID);

        @SuppressWarnings("unchecked")
        final List<Credentials> allCredentialsOfStorageProvider =
            getAllCredentialsByStorageProviderIDQuery.getResultList();

        CredentialsJPAStore.LOG.debug("{} credentials for storage provider \"{}\" were found.",
                                      allCredentialsOfStorageProvider.size(), storageProviderID);

        return new HashSet<>(allCredentialsOfStorageProvider);

    }

    /**
     * @see CoreInternalCredentialsServiceImpl#getCredentialsIDs()
     */
    public Set<Long> getCredentialsIDs() {

        CredentialsJPAStore.LOG.debug("Retrieving IDs of all stored credentials...");

        initJPA();

        final Query getCredentialsIDsQuery = this.em.createNamedQuery(Credentials.getCredentialsIDs);

        @SuppressWarnings("unchecked")
        final List<Long> credentialsIDs = getCredentialsIDsQuery.getResultList();

        CredentialsJPAStore.LOG.debug("{} credentials ID(s) were found.", credentialsIDs.size());

        return new HashSet<>(credentialsIDs);

    }

    /**
     * @see CoreInternalCredentialsServiceImpl#getAllCredentials()
     */
    public Set<Credentials> getAllCredentials() {

        CredentialsJPAStore.LOG.debug("Retrieving all credentials...");

        initJPA();

        final Query getAllCredentialsQuery = this.em.createNamedQuery(Credentials.getAllCredentials);

        @SuppressWarnings("unchecked")
        final List<Credentials> allCredentials = getAllCredentialsQuery.getResultList();

        CredentialsJPAStore.LOG.debug("{} credentials were found.", allCredentials.size());

        return new HashSet<>(allCredentials);

    }

    /**
     * Deletes credentials {@code credentialsID}.
     *
     * @param credentialsID of credentials.
     *
     * @throws UserException if credentials to delete were not found.
     */
    public void deleteCredentials(final long credentialsID) throws UserException {

        CredentialsJPAStore.LOG.debug("Deleting credentials \"{}\"...", credentialsID);

        initJPA();

        this.em.getTransaction().begin();
        final Query removeCredentialsQuery = this.em.createNamedQuery(Credentials.removeCredentialsByID);
        removeCredentialsQuery.setParameter("id", credentialsID);
        final int numDeletedCredentials = removeCredentialsQuery.executeUpdate();
        this.em.getTransaction().commit();

        if (numDeletedCredentials > 0) {
            CredentialsJPAStore.LOG.debug("Deleting credentials \"{}\" completed.", credentialsID);
        } else {
            throw new UserException("Credentials \"" + credentialsID + "\" to delete were not found.");
        }

    }

    /**
     * Deletes all stored credentials.
     */
    public void deleteAllCredentials() {

        CredentialsJPAStore.LOG.debug("Deleting all credentials...");

        initJPA();

        this.em.getTransaction().begin();
        final Query removeAllCredentialsQuery = this.em.createNamedQuery(Credentials.removeAllCredentials);
        final int numDeletedCredentials = removeAllCredentialsQuery.executeUpdate();
        this.em.getTransaction().commit();

        CredentialsJPAStore.LOG.debug("Deleted {} credentials.", numDeletedCredentials);

    }

}
