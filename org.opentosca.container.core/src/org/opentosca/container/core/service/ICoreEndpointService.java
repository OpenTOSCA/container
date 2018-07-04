package org.opentosca.container.core.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.internal.ICoreInternalEndpointService;

/**
 * This interface provides methods to retrieve and store endpoints. It is meant to be used by the
 * Engines.
 */
public interface ICoreEndpointService {

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpoints
     */
    public List<WSDLEndpoint> getWSDLEndpoints(QName portType, String triggeringContainer, CSARID csarId);

    /**
     * @see ICoreInternalEndpointService#storeWSDLEndpoint
     */
    public void storeWSDLEndpoint(WSDLEndpoint endpoint);

    /**
     * @see ICoreInternalEndpointService#getRestEndpoints
     */
    public List<RESTEndpoint> getRestEndpoints(URI anyURI, String triggeringContainer, CSARID csarId);

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpointForPlanId
     */
    public WSDLEndpoint getWSDLEndpointForPlanId(String triggeringContainer, CSARID csarId, QName planId);

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpointsForCSARID
     */
    public List<WSDLEndpoint> getWSDLEndpointsForCSARID(String triggeringContainer, CSARID csarId);

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpointsForSTID()
     */
    public List<WSDLEndpoint> getWSDLEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID);

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpointsForNTImplAndIAName
     */
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(String triggeringContainer, String managingContainer,
                                                                 QName nodeTypeImpl, String iaName);

    /**
     * @see ICoreInternalEndpointService#getWSDLEndpoints
     */
    public List<WSDLEndpoint> getWSDLEndpoints();

    /**
     * @see ICoreInternalEndpointService#storeRESTEndpoint
     */
    public void storeRESTEndpoint(RESTEndpoint endpoint);

    /**
     * @see ICoreInternalEndpointService#removePlanEndpoints
     */
    public void removePlanEndpoints(String triggeringContainer, CSARID csarId);

    /**
     * Removes the given WSDL Endpoint if found
     *
     * @param endpoint the WSDL Endpoint to remove
     * @return true if removing the endpoint was successful, else false
     */
    public boolean removeWSDLEndpoint(WSDLEndpoint endpoint);

    /**
     * Debug print of plan endpoints.
     */
    public void printPlanEndpoints();

}
