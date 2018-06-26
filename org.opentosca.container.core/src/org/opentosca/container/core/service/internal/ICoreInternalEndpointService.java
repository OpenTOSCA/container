package org.opentosca.container.core.service.internal;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;

/**
 * This Interface provides Methods to store and get Endpoints
 *
 * @see RESTEndpoint
 * @see WSDLEndpoint
 */
public interface ICoreInternalEndpointService {

    /**
     * This method queries for all WSDL-Endpoints identified by the given portType and csarId. It
     * then returns a List of the retrieved Endpoints.
     *
     * @see WSDLEndpoint
     *
     * @param portType : PortType to identify the Endpoint
     * @param csarId : csarId to identify the Endpoint
     * @return ArrayList containing all corresponding WSDLEndpoints
     */
    public List<WSDLEndpoint> getWSDLEndpoints(QName portType, CSARID csarId);

    /**
     * This method stores a given WSDLEndpoint object.
     *
     * @see WSDLEndpoint
     *
     * @param endpoint : The WSDL-Endpoint to store
     */
    public void storeWSDLEndpoint(WSDLEndpoint endpoint);

    /**
     * This method queries for RESTEndpoints identified by the given URI and thorID
     *
     * @see RESTEndpoint
     *
     * @param anyURI : Uri to identify the Endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return ArrayList containing all endpoints matching the given parameters
     */
    public List<RESTEndpoint> getRestEndpoints(URI anyURI, CSARID csarId);

    /**
     * This method queries for a WSDLEndpoint identified by the given CSARID and PlanId
     *
     * @param csarId an id of type CSARID
     * @param planId an id of type QName
     * @return a WSDLEndpoint representing a Plan stored in the endpoint db or null if nothing was
     *         found
     */
    public WSDLEndpoint getWSDLEndpointForPlanId(CSARID csarId, QName planId);

    /**
     * This method stores a given RESTEndpoint object.
     *
     * @see WSDLEndpoint
     *
     * @param endpoint : RESTEndpoint to store
     */
    public void storeRESTEndpoint(RESTEndpoint endpoint);

    /**
     * Removes all Endpoints associated with the given CSARID
     *
     * @param csarId the CSARID whose endpoints should be removed
     */
    public void removeEndpoints(CSARID csarId);

    /**
     * Debug print of plan endpoints.
     */
    public void printPlanEndpoints();

    /**
     * This method queries for all WSDLEndpoints identified by the given CSARID
     *
     * @param csarId an id of type CSARID
     * @return List of WSDLEndpoints of the given CSARID if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForCSARID(CSARID csarId);

    /**
     * This method queries for a WSDLEndpoint identified by the managing OpenTOSCA Container, the
     * NodeTypeImplementationId and the ImplementationArtifact name
     *
     * @param managingContainer OpenTOSCA Container which is responsible for handling the Endpoint
     * @param nodeTypeImpl an ID of type QName
     * @param iaName an ID of type String
     * @return a WSDLEndpoint representing the given IA if one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(String managingContainer, QName nodeTypeImpl,
                                                                 String iaName);

    /**
     * This method queries for all WSDLEndpoints
     *
     * @return List of WSDLEndpoints if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpoints();

    /**
     * Removes the given WSDLEndpoint related to the CSAR referenced by the CSARId
     *
     * @param csarId the CSARID of the CSAR the endpoint belongs to
     * @param endpoint the WSDLEndpoint to remove
     * @return true if removal was successful, else false
     */
    public boolean removeWSDLEndpoint(CSARID csarId, WSDLEndpoint endpoint);

}
