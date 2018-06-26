package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.control.IOpenToscaControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IOpenToscaControlService
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
// FIXME directly inject the service into the classes that require it
@Deprecated
public class IOpenToscaControlServiceHandler {

    final private static Logger LOG = LoggerFactory.getLogger(IOpenToscaControlServiceHandler.class);

    private static IOpenToscaControlService openToscaControl;


    public static IOpenToscaControlService getOpenToscaControlService() {
        return IOpenToscaControlServiceHandler.openToscaControl;
    }

    public void bind(final IOpenToscaControlService tm) {
        IOpenToscaControlServiceHandler.LOG.debug("ContainerApi: Bind IOpenToscaControlService");
        IOpenToscaControlServiceHandler.openToscaControl = tm;
    }

    public void unbind(final IOpenToscaControlService tm) {
        IOpenToscaControlServiceHandler.LOG.debug("ContainerApi: Unbind IOpenToscaControlService");
        IOpenToscaControlServiceHandler.openToscaControl = null;
    }
}
