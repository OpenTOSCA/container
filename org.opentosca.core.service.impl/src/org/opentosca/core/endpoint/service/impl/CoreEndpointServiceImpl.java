package org.opentosca.core.endpoint.service.impl;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.internal.endpoint.service.ICoreInternalEndpointService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;

/**
 * {@inheritDoc}
 * 
 * This implementation currently acts as a Proxy to the Internal Endpoint
 * service. It can in future be used to modify the incoming parameters to fit
 * another backend interface/implementation
 * 
 * @see ICoreInternalEndpointService
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public class CoreEndpointServiceImpl implements ICoreEndpointService {
	
	// Internal Endpoint service.
	private ICoreInternalEndpointService endpointService;
	
	
	public void bind(ICoreInternalEndpointService serv) {
		this.endpointService = serv;
	}
	
	public void unbind(ICoreInternalEndpointService serv) {
		this.endpointService = null;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public List<WSDLEndpoint> getWSDLEndpoints(QName portType, CSARID csarId) {
		return this.endpointService.getWSDLEndpoints(portType, csarId);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public WSDLEndpoint getWSDLEndpoint(QName portType, CSARID csarId) {
		return this.endpointService.getWSDLEndpoint(portType, csarId);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public void storeWSDLEndpoint(WSDLEndpoint endpoint) {
		this.endpointService.storeWSDLEndpoint(endpoint);
		
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public List<RESTEndpoint> getRestEndpoints(URI anyURI, CSARID csarId) {
		return this.endpointService.getRestEndpoints(anyURI, csarId);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public RESTEndpoint getRestEndpoint(URI anyURI, restMethod method, CSARID csarId) {
		return this.endpointService.getRestEndpoint(anyURI, method, csarId);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public void storeRESTEndpoint(RESTEndpoint endpoint) {
		this.endpointService.storeRESTEndpoint(endpoint);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	public boolean endpointExists(URI uri, CSARID csarId) {
		return this.endpointService.endpointExists(uri, csarId);
	}
	
	@Override
	public void removeEndpoints(CSARID csarId) {
		this.endpointService.removeEndpoints(csarId);
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForPlanId(CSARID csarId, QName planId) {
		return this.endpointService.getWSDLEndpointForPlanId(csarId, planId);
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForIa(CSARID csarId, QName nodeTypeImpl, String iaName) {
		return this.endpointService.getWSDLEndpointForIa(csarId, nodeTypeImpl, iaName);
	}
	
	@Override
	public List<WSDLEndpoint> getWSDLEndpointsForCSARID(CSARID csarId) {
		return this.endpointService.getWSDLEndpointsForCSARID(csarId);
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
	public boolean removeWSDLEndpoint(CSARID csarId, WSDLEndpoint endpoint) {
		return this.endpointService.removeWSDLEndpoint(csarId, endpoint);
	}
	
}
