package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.core.service.ICoreModelRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface ICoreModelRepositoryService
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class ModelRepositoryServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(ModelRepositoryServiceHandler.class);

    private static ICoreModelRepositoryService modelHandler;


    public static ICoreModelRepositoryService getModelHandler() {
        return ModelRepositoryServiceHandler.modelHandler;
    }

    public void bindModelRepository(final ICoreModelRepositoryService ms) {
        ModelRepositoryServiceHandler.LOG.debug("ContainerApi: Bind ICoreModelRepositoryService");
        ModelRepositoryServiceHandler.modelHandler = ms;
    }

    public void unbindModelRepository(final ICoreModelRepositoryService ms) {
        ModelRepositoryServiceHandler.LOG.debug("ContainerApi: Unbind ICoreModelRepositoryService");
        ModelRepositoryServiceHandler.modelHandler = null;
    }
}
