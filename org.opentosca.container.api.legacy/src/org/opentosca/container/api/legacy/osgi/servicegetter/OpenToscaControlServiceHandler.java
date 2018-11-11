package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.control.OpenToscaControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IOpenToscaControlService
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class OpenToscaControlServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(OpenToscaControlServiceHandler.class);

    private static OpenToscaControlService openToscaControl;


    public static OpenToscaControlService getOpenToscaControlService() {
        return OpenToscaControlServiceHandler.openToscaControl;
    }

    public void bind(final OpenToscaControlService tm) {
        OpenToscaControlServiceHandler.LOG.debug("ContainerApi: Bind IOpenToscaControlService");
        OpenToscaControlServiceHandler.openToscaControl = tm;
    }

    public void unbind(final OpenToscaControlService tm) {
        OpenToscaControlServiceHandler.LOG.debug("ContainerApi: Unbind IOpenToscaControlService");
        OpenToscaControlServiceHandler.openToscaControl = null;
    }
}
