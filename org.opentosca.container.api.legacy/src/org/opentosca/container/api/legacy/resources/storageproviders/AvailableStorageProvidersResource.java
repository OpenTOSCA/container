package org.opentosca.container.api.legacy.resources.storageproviders;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.storageproviders.jaxb.JaxbFactory;
import org.opentosca.container.api.legacy.resources.storageproviders.jaxb.StorageProvidersJaxb;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource of all available storage providers of the Core File Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class AvailableStorageProvidersResource {

    private static final Logger LOG = LoggerFactory.getLogger(AvailableStorageProvidersResource.class);

    private final ICoreFileService FILE_SERVICE = FileRepositoryServiceHandler.getFileHandler();


    /**
     * Creates a {@link AvailableStorageProvidersResource}.
     */
    public AvailableStorageProvidersResource() {
        AvailableStorageProvidersResource.LOG.debug("{} created: {}", this.getClass(), this);
    }

    /**
     * Gets the storage providers that will be showed on this resource. It depends on the query
     * parameter {@code type} passed by the user.
     *
     * @param type - optional query parameter that determines if only storage providers with a certain
     *        property ( {@link StorageProviderProperties}) should be showed. If type is invalid all
     *        storage providers will be showed.
     * @return {@link StorageProvidersJaxb} containing the storage providers that will be showed.
     *
     * @see StorageProviderProperties
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public StorageProvidersJaxb getStorageProviders(@QueryParam("type") final String type) {

        if (StorageProviderProperties.ACTIVE.toString().equalsIgnoreCase(type)) {
            return JaxbFactory.createStorageProvidersJaxbOfActiveStorageProvider();
        } else if (StorageProviderProperties.READY.toString().equalsIgnoreCase(type)) {
            return JaxbFactory.createStorageProvidersJaxbOfReadyStorageProviders();
        } else if (StorageProviderProperties.DEFAULT.toString().equalsIgnoreCase(type)) {
            return JaxbFactory.createStorageProvidersJaxbOfDefaultStorageProvider();
        }

        return JaxbFactory.createStorageProvidersJaxbOfAllStorageProviders();

    }

    /**
     *
     * Creates a {@link AvailableStorageProviderResource} of storage provider {@code storageProviderID}.
     * If storage provider {@code storageProviderID} is not available it will be created with
     * {@code null}.
     *
     * @param storageProviderID of storage provider
     * @return {@link AvailableStorageProviderResource}
     */
    @Path("{id}")
    public AvailableStorageProviderResource getStorageProviderResource(
                    @PathParam("id") final String storageProviderID) {

        if (this.FILE_SERVICE.getStorageProviders().contains(storageProviderID)) {
            return new AvailableStorageProviderResource(storageProviderID);
        }

        return new AvailableStorageProviderResource(null);

    }

}
