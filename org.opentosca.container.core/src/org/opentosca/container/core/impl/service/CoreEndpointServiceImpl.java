package org.opentosca.container.core.impl.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.internal.ICoreInternalEndpointService;

/**
 * This implementation currently acts as a Proxy to the Internal Endpoint service. It can in future
 * be used to modify the incoming parameters to fit another backend interface/implementation
 *
 * @see ICoreInternalEndpointService
 */
public class CoreEndpointServiceImpl implements ICoreEndpointService {

    // Internal Endpoint service.
    private ICoreInternalEndpointService endpointService;


    public void bind(final ICoreInternalEndpointService serv) {
        this.endpointService = serv;
    }

    public void unbind(final ICoreInternalEndpointService serv) {
        this.endpointService = null;
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public List<WSDLEndpoint> getWSDLEndpoints(final QName portType, final CSARID csarId) {
        return this.endpointService.getWSDLEndpoints(portType, csarId);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public WSDLEndpoint getWSDLEndpoint(final QName portType, final CSARID csarId) {
        return this.endpointService.getWSDLEndpoint(portType, csarId);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public void storeWSDLEndpoint(final WSDLEndpoint endpoint) {
        this.endpointService.storeWSDLEndpoint(endpoint);

    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public List<RESTEndpoint> getRestEndpoints(final URI anyURI, final CSARID csarId) {
        return this.endpointService.getRestEndpoints(anyURI, csarId);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public RESTEndpoint getRestEndpoint(final URI anyURI, final restMethod method, final CSARID csarId) {
        return this.endpointService.getRestEndpoint(anyURI, method, csarId);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public void storeRESTEndpoint(final RESTEndpoint endpoint) {
        this.endpointService.storeRESTEndpoint(endpoint);
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public boolean endpointExists(final URI uri, final CSARID csarId) {
        return this.endpointService.endpointExists(uri, csarId);
    }

    @Override
    public void removeEndpoints(final CSARID csarId) {
        this.endpointService.removeEndpoints(csarId);
    }

    @Override
    public WSDLEndpoint getWSDLEndpointForPlanId(final CSARID csarId, final QName planId) {
        return this.endpointService.getWSDLEndpointForPlanId(csarId, planId);
    }

    @Override
    public WSDLEndpoint getWSDLEndpointForIa(final CSARID csarId, final QName nodeTypeImpl, final String iaName) {
        return this.endpointService.getWSDLEndpointForIa(csarId, nodeTypeImpl, iaName);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForCSARID(final CSARID csarId) {
        return this.endpointService.getWSDLEndpointsForCSARID(csarId);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(final QName nodeTypeImpl, final String iaName) {
        return this.endpointService.getWSDLEndpointsForNTImplAndIAName(nodeTypeImpl, iaName);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpoints() {
        return this.endpointService.getWSDLEndpoints();
    }

    @Override
    public void printPlanEndpoints() {
        this.endpointService.printPlanEndpoints();
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * This currently acts as a proxy
     */
    public boolean removeWSDLEndpoint(final CSARID csarId, final WSDLEndpoint endpoint) {
        return this.endpointService.removeWSDLEndpoint(csarId, endpoint);
    }

}
