package org.opentosca.bus.management.invocation.plugin.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.service.impl.collaboration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management Bus-Plug-in for invoking an IA on a remote OpenTOSCA Container. <br>
 * <br>
 *
 * The plug-in gets all needed information for the invocation and forwards it to the remote
 * Container over MQTT. When it gets the response, it copies the result body to the exchange and
 * returns it to the Management Bus.
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder- st100495@stud.uni-stuttgart.de
 *
 */
public class ManagementBusInvocationPluginRemote implements IManagementBusInvocationPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginRemote.class);

    @Override
    public Exchange invoke(final Exchange exchange) {

        ManagementBusInvocationPluginRemote.LOG.debug("Invoking IA on remote OpenTOSCA Container.");
        final Message message = exchange.getIn();

        // TODO: invoke IA by communicating with the remote Management Bus

        return exchange;
    }

    @Override
    public List<String> getSupportedTypes() {

        // This plug-in supports only the special type 'remote' which is used to forward invocation
        // requests to other OpenTOSCA Containers.
        final List<String> types = new ArrayList<>();
        types.add(Constants.REMOTE_TYPE);

        return types;
    }
}
