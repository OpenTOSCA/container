package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.core.service.ICoreCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface ICoreCredentialsService.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class CredentialsServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(CredentialsServiceHandler.class);

    private static ICoreCredentialsService credentialsService;


    public static ICoreCredentialsService getCredentialsService() {
        return CredentialsServiceHandler.credentialsService;
    }

    public void bindCoreCredentialsService(final ICoreCredentialsService credentialsService) {
        CredentialsServiceHandler.LOG.debug("ContainerApi: Bind ICoreCredentialsService");
        CredentialsServiceHandler.credentialsService = credentialsService;
    }

    public void unbindCoreCredentialsService(final ICoreCredentialsService credentialsService) {
        CredentialsServiceHandler.LOG.debug("ContainerApi: Unbind ICoreCredentialsService");
        CredentialsServiceHandler.credentialsService = null;
    }

}
