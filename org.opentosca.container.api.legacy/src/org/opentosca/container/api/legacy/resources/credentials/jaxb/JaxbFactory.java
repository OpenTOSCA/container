package org.opentosca.container.api.legacy.resources.credentials.jaxb;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.container.api.legacy.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.container.api.legacy.resources.credentials.AllCredentialsResource;
import org.opentosca.container.api.legacy.resources.credentials.CredentialsResource;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.credentials.Credentials;
import org.opentosca.container.core.service.ICoreCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the JAXB objects for the {@link AllCredentialsResource} and
 * {@link CredentialsResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class JaxbFactory {

    private final static Logger LOG = LoggerFactory.getLogger(JaxbFactory.class);

    private static ICoreCredentialsService credentialsService = CredentialsServiceHandler.getCredentialsService();


    /**
     * @return {@link AllCredentialsJaxb} containing stored credentials for storage provider
     *         {@code storageProviderID}. If {@code null} or an empty string is passed instead all
     *         stored credentials are contained.<br />
     *         Necessary data will be fetched from {@link ICoreCredentialsService}.<br />
     *
     * @see #createCredentialsJaxb(Credentials)
     */
    public static AllCredentialsJaxb createAllCredentialsJaxb(final String storageProviderID) {

        Set<Credentials> allCredentials;

        if (storageProviderID == null || storageProviderID.isEmpty()) {
            allCredentials = JaxbFactory.credentialsService.getAllCredentials();
        } else {
            allCredentials = JaxbFactory.credentialsService.getAllCredentialsOfStorageProvider(storageProviderID);
        }

        final Set<CredentialsJaxb> credentialsJaxbSet = new HashSet<>();

        for (final Credentials credentials : allCredentials) {
            final CredentialsJaxb credentialsJaxb = JaxbFactory.createCredentialsJaxb(credentials);
            credentialsJaxbSet.add(credentialsJaxb);
        }

        final AllCredentialsJaxb allCredentialsJaxb = new AllCredentialsJaxb();
        allCredentialsJaxb.setCredentials(credentialsJaxbSet);

        return allCredentialsJaxb;

    }

    /**
     * Builds {@link CredentialsJaxb} of credentials {@code credentials}. It contains additionally the
     * information if these credentials are currently set in their storage provider (if storage provider
     * is not available it's {@code false}).<br />
     * Necessary data will be fetched from {@link ICoreCredentialsService}.<br />
     *
     * @param credentials
     * @return {@link CredentialsJaxb} of credentials {@code credentials}
     */
    public static CredentialsJaxb createCredentialsJaxb(final Credentials credentials) {

        final CredentialsJaxb credentialsJaxb = new CredentialsJaxb(credentials);

        final long credentialsID = credentials.getID();

        try {
            final boolean injectedInStorageProvider =
                JaxbFactory.credentialsService.hasStorageProviderCredentials(credentialsID);
            credentialsJaxb.setInjectedInStorageProvider(injectedInStorageProvider);
        }
        catch (final UserException exc) {
            JaxbFactory.LOG.warn("An User Exception occured.", exc);
        }

        return credentialsJaxb;

    }

}
