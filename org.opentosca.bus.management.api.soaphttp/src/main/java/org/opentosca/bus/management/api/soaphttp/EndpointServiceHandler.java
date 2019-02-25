package org.opentosca.bus.management.api.soaphttp;

import java.net.URI;
import java.net.URISyntaxException;

import org.opentosca.bus.management.api.soaphttp.route.Route;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointServiceHandler {
    private final static Logger LOG = LoggerFactory.getLogger(EndpointServiceHandler.class);

    public ICoreEndpointService endpointService;

    /**
     * Bind EndpointService.
     *
     * @param endpointService - The endpointService to register.
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService == null) {
            LOG.error("Bind Endpoint Service: Supplied parameter is null!");
            return;
        } 
        this.endpointService = endpointService;
        LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
        LOG.debug("Storing the Management Bus SOAP-API endpoint: {} via EndpointService...",
                                         Route.PUBLIC_ENDPOINT);

        URI uri = null;
        try {
            uri = new URI(Route.PUBLIC_ENDPOINT);
        }
        catch (final URISyntaxException e) {
            LOG.error("Could not parse public endpoint as URI", e);
            return;
        }
        // Stores the Management Bus endpoint in the endpointDB. "***",
        // cause the MB-endpoint is csar independent.
        final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        final WSDLEndpoint endpoint = new WSDLEndpoint(uri, Route.PORTTYPE, localContainer, localContainer, new CsarId(""), null, null, null, null);
        endpointService.storeWSDLEndpoint(endpoint);
    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        endpointService = null;
        LOG.debug("Unbind Endpoint Service unbound.");
    }
}
