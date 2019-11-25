package org.opentosca.bus.management.api.soaphttp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.opentosca.bus.management.api.soaphttp.route.Route;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointServiceHandler {

    public static ICoreEndpointService endpointService, oldEndpointService;
    public static IToscaReferenceMapper toscaReferenceMapper;

    private final static Logger LOG = LoggerFactory.getLogger(EndpointServiceHandler.class);


    /**
     * Bind EndpointService.
     *
     * @param endpointService - The endpointService to register.
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService != null) {
            if (EndpointServiceHandler.endpointService == null) {
                EndpointServiceHandler.endpointService = endpointService;
            } else {
                EndpointServiceHandler.oldEndpointService = endpointService;
                EndpointServiceHandler.endpointService = endpointService;
            }

            EndpointServiceHandler.LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());

            EndpointServiceHandler.LOG.debug("Storing the Management Bus SOAP-API endpoint: {} via EndpointService...",
                                             Route.PUBLIC_ENDPOINT);

            URI uri = null;
            try {
                uri = new URI(Route.PUBLIC_ENDPOINT);

            }
            catch (final URISyntaxException e) {
                e.printStackTrace();
            }
            // Stores the Management Bus endpoint in the endpointDB. "***",
            // cause the MB-endpoint is csar independent.
            final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
            final WSDLEndpoint endpoint = new WSDLEndpoint(uri, Route.PORTTYPE, localContainer, localContainer,
                new CSARID("***"), null, null, null, null, new HashMap<String, String>());
            EndpointServiceHandler.endpointService.storeWSDLEndpoint(endpoint);

        } else {
            EndpointServiceHandler.LOG.error("Bind Endpoint Service: Supplied parameter is null!");
        }

    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        if (EndpointServiceHandler.oldEndpointService == null) {
            endpointService = null;
        } else {
            EndpointServiceHandler.oldEndpointService = null;
        }

        EndpointServiceHandler.LOG.debug("Unbind Endpoint Service unbound.");
    }

    /**
     * Bind IToscaReferenceMapper.
     *
     * @param toscaEngineService - The ToscaReferenceMapper to register.
     */
    public void bindToscaReferenceMapper(final IToscaReferenceMapper toscaReferenceMapper) {
        if (toscaReferenceMapper != null) {
            EndpointServiceHandler.toscaReferenceMapper = toscaReferenceMapper;
            LOG.debug("Bind IToscaReferenceMapper: {} bound.", toscaReferenceMapper.toString());
        } else {
            LOG.error("Bind IToscaReferenceMapper: Supplied parameter is null!");
        }
    }

    /**
     * Unbind IToscaReferenceMapper.
     *
     * @param toscaReferenceMapper - The ToscaReferenceMapper to unregister.
     */
    public void unbindToscaReferenceMapper(final IToscaReferenceMapper toscaReferenceMapper) {
        EndpointServiceHandler.toscaReferenceMapper = null;
        LOG.debug("Unbind IToscaReferenceMapper unbound.");
    }
}
