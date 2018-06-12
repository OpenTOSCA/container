package org.opentosca.container.api.legacy.resources.storageproviders;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opentosca.container.api.legacy.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.storageproviders.jaxb.JaxbFactory;
import org.opentosca.container.api.legacy.resources.storageproviders.jaxb.StorageProviderJaxb;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.service.ICoreCredentialsService;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource of a available storage provider of the Core File Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class AvailableStorageProviderResource {

    private static final Logger LOG = LoggerFactory.getLogger(AvailableStorageProviderResource.class);

    private final ICoreFileService FILE_SERVICE = FileRepositoryServiceHandler.getFileHandler();

    private final ICoreCredentialsService CREDENTIALS_SERVICE = CredentialsServiceHandler.getCredentialsService();

    /**
     * If {@code null} it's a not available storage provider and every method returns 404 (not found).
     */
    private final String STORAGE_PROVIDER_ID;


    /**
     * Creates a {@link AvailableStorageProviderResource} of storage provider {@code storageProviderID}.
     *
     * @param storageProviderID of storage provider.
     */
    public AvailableStorageProviderResource(final String storageProviderID) {
        AvailableStorageProviderResource.LOG.debug("{} created: {}", this.getClass(), this);
        this.STORAGE_PROVIDER_ID = storageProviderID;
    }

    /**
     * Gets the storage provider that will be showed on this resource.
     *
     * @return 200 (OK) with entity {@link StorageProviderJaxb} containing the storage provider that
     *         will be showed. If this storage provider is not available 404 (not found).
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getStorageProvider() {

        if (this.STORAGE_PROVIDER_ID == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(JaxbFactory.createStorageProviderJaxb(this.STORAGE_PROVIDER_ID)).build();

    }

    /**
     * Either sets this storage provider as the active one if {@code activate} is passed in
     * {@code input} (body of a POST message) or deletes the credentials in this storage provider if
     * {@code unset} is passed instead.
     *
     * @param input
     *
     * @return 200 (OK) - storage provider was set as the active one or credentials were deleted in this
     *         storage provider.<br />
     *         400 (bad request) - {@code activate} respectively {@code unset} was not passed or storage
     *         provider has no credentials to delete.<br />
     *         500 (internal server error) - setting this storage provider as the active one or deleting
     *         credentials in this storage provider failed.<br />
     *         404 (not found) - this storage provider is not available.
     * @throws UserException
     * @throws SystemException
     * @see ICoreFileService#setActiveStorageProvider(String)
     * @see ICoreCredentialsService#deleteCredentialsInStorageProvider(String)
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response setAsActiveOrUnsetCredentials(final String input) throws UserException, SystemException {

        if (this.STORAGE_PROVIDER_ID == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        // try {

        if (input.equalsIgnoreCase("activate")) {

            this.FILE_SERVICE.setActiveStorageProvider(this.STORAGE_PROVIDER_ID);

            return Response.ok("Storage provider \"" + this.STORAGE_PROVIDER_ID + "\" was set as the active one.")
                           .build();

        } else if (input.equalsIgnoreCase("unset")) {

            final boolean hasCredentials =
                this.CREDENTIALS_SERVICE.hasStorageProviderCredentials(this.STORAGE_PROVIDER_ID);

            if (hasCredentials) {
                this.CREDENTIALS_SERVICE.deleteCredentialsInStorageProvider(this.STORAGE_PROVIDER_ID);
                return Response.ok("Credentials were deleted in storage provider \"" + this.STORAGE_PROVIDER_ID + "\".")
                               .build();
            } else {
                return Response.status(Status.BAD_REQUEST)
                               .entity("Storage provider \"" + this.STORAGE_PROVIDER_ID + "\" has no credentials.")
                               .build();
            }

        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }

        // } catch (SystemException exc) {
        // AvailableStorageProviderResource.LOG.warn("A System Exception
        // occured.",
        // exc);
        // } catch (UserException exc) {
        // AvailableStorageProviderResource.LOG.warn("An User Exception
        // occured.",
        // exc);
        // }

        // return Response.status(Status.INTERNAL_SERVER_ERROR).build();

    }
}
