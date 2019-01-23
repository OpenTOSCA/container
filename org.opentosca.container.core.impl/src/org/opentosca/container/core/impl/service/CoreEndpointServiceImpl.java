package org.opentosca.container.core.impl.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
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
    public List<WSDLEndpoint> getWSDLEndpoints(final QName portType, final String triggeringContainer,
                                               final CSARID csarId) {
        return this.endpointService.getWSDLEndpoints(portType, triggeringContainer, csarId);
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
    public List<RESTEndpoint> getRestEndpoints(final URI anyURI, final String triggeringContainer,
                                               final CSARID csarId) {
        return this.endpointService.getRestEndpoints(anyURI, triggeringContainer, csarId);
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
    public void removePlanEndpoints(final String triggeringContainer, final CSARID csarId) {
        this.endpointService.removePlanEndpoints(triggeringContainer, csarId);
    }

    @Override
    public WSDLEndpoint getWSDLEndpointForPlanId(final String triggeringContainer, final CSARID csarId,
                                                 final QName planId) {
        return this.endpointService.getWSDLEndpointForPlanId(triggeringContainer, csarId, planId);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForCSARID(final String triggeringContainer, final CSARID csarId) {
        return this.endpointService.getWSDLEndpointsForCSARID(triggeringContainer, csarId);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForSTID(final String triggeringContainer,
                                                      final Long serviceTemplateInstanceID) {
        return this.endpointService.getWSDLEndpointsForSTID(triggeringContainer, serviceTemplateInstanceID);
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(final String triggeringContainer,
                                                                 final String managingContainer,
                                                                 final QName nodeTypeImpl, final String iaName) {
        return this.endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer, managingContainer,
                                                                       nodeTypeImpl, iaName);
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
    public boolean removeWSDLEndpoint(final WSDLEndpoint endpoint) {
        return this.endpointService.removeWSDLEndpoint(endpoint);
    }
}
