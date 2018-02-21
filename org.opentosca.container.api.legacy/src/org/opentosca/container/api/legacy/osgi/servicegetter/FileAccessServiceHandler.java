package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.core.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IFileAccessService
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class FileAccessServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(FileAccessServiceHandler.class);

    private static IFileAccessService fileAccessService;


    public static IFileAccessService getFileAccessService() {
        return FileAccessServiceHandler.fileAccessService;
    }

    public void bind(final IFileAccessService fa) {
        FileAccessServiceHandler.LOG.debug("ContainerApi: Bind IFileAccessService");
        FileAccessServiceHandler.fileAccessService = fa;
    }

    public void unbind(final IFileAccessService fa) {
        FileAccessServiceHandler.LOG.debug("ContainerApi: Unbind IFileAccessService");
        FileAccessServiceHandler.fileAccessService = null;
    }
}
