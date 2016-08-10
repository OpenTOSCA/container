package org.opentosca.core.endpoint.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;

/**
 * This interface provides methods to retrieve and store endpoints. It is meant
 * to be used by the Engines.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public interface ICoreEndpointService {
	
	/**
	 * @see ICoreInternalEndpointService#getWSDLEndpoints
	 */
	public List<WSDLEndpoint> getWSDLEndpoints(QName portType, CSARID csarId);
	
	/**
	 * @see ICoreInternalEndpointService#getWSDLEndpoint
	 */
	public WSDLEndpoint getWSDLEndpoint(QName portType, CSARID csarId);
	
	/**
	 * @see ICoreInternalEndpointService#storeWSDLEndpoint
	 */
	public void storeWSDLEndpoint(WSDLEndpoint endpoint);
	
	/**
	 * @see ICoreInternalEndpointService#getRestEndpoints
	 */
	public List<RESTEndpoint> getRestEndpoints(URI anyURI, CSARID csarId);
	
	/**
	 * @see ICoreInternalEndpointService#getRestEndpoint
	 */
	public RESTEndpoint getRestEndpoint(URI anyURI, restMethod method, CSARID csarId);
	
	/**
	 * @see ICoreInternalEndpointService#getWSDLEndpointForPlanId
	 */
	public WSDLEndpoint getWSDLEndpointForPlanId(CSARID csarId, QName planId);
	
	/**
	 * @see ICoreInternalEndpointService#getWSDLEndpointForIa
	 */
	public WSDLEndpoint getWSDLEndpointForIa(CSARID csarId, QName nodeTypeImpl, String iaName);
	
	/**
	 * @see ICoreInternalEndpointService#getWSDLEndpointsForCSARID
	 */
	public List<WSDLEndpoint> getWSDLEndpointsForCSARID(CSARID csarId);
	
	/**
	 * @see ICoreInternalEndpointService#storeRESTEndpoint
	 */
	public void storeRESTEndpoint(RESTEndpoint endpoint);
	
	/**
	 * @see ICoreInternalEndpointService#endpointExists
	 */
	public boolean endpointExists(URI uri, CSARID csarId);
	
	/**
	 * Removes all Endpoints associated with the given CSARID
	 * 
	 * @param csarId the CSARID whose Endpoints should be removed
	 */
	public void removeEndpoints(CSARID csarId);
	
	/**
	 * Removes the given WSDL Endpoint stored for the given CSAR instance inside
	 * the core
	 * 
	 * @param csarId The Id of the CSAR the WSDLEndpoint to remove relates to
	 * @param endpoint the WSDL Endpoint to remove
	 * @return true if removing the endpoint was successful, else false
	 */
	public boolean removeWSDLEndpoint(CSARID csarId, WSDLEndpoint endpoint);
	
	/**
	 * Debug print of plan endpoints.
	 */
	public void printPlanEndpoints();
	
}