package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface ICoreFileService
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class FileRepositoryServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(FileRepositoryServiceHandler.class);

    private static ICoreFileService fileHandler;


    public static ICoreFileService getFileHandler() {
        return FileRepositoryServiceHandler.fileHandler;
    }

    public void bindFileRepository(final ICoreFileService is) {
        FileRepositoryServiceHandler.LOG.debug("ContainerApi: Bind ICoreFileService");
        FileRepositoryServiceHandler.fileHandler = is;
    }

    public void unbindFileRepository(final ICoreFileService is) {
        FileRepositoryServiceHandler.LOG.debug("ContainerApi: Unbind ICoreFileService");
        FileRepositoryServiceHandler.fileHandler = null;
    }
}
