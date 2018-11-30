package org.opentosca.container.core.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;

/**
 * This interface provides methods to retrieve and store endpoints. It is meant to be used by the
 * Engines.
 */
public interface ICoreEndpointService {

    /**
     * This method queries for all WSDL-Endpoints identified by the given portType and thorID. It then
     * returns a List of the retrieved Endpoints.
     *
     * @see WSDLEndpoint
     *
     * @param portType : PortType to identify the Endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return ArrayList containing all corresponding WSDLEndpoints
     */
    public List<WSDLEndpoint> getWSDLEndpoints(QName portType, CsarId csarId);

    /**
     * This method queries for a WSDLEndpoint identified by the given portType, addressType and thorID.
     *
     * @see WSDLEndpoint
     *
     * @param portType : PortType to identify the Endpoint
     * @param addressType : AddressType to identify the endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return WSDLEndpoint matching the given parameters
     */
    public WSDLEndpoint getWSDLEndpoint(QName portType, CsarId csarId);

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
    public List<RESTEndpoint> getRestEndpoints(URI anyURI, CsarId csarId);

    /**
     * This method queries for a RESTEndpoint identified by the given URI, RestMethod
     * {GET,PUT,POST,DELETE} and thorID
     *
     * @see RESTEndpoint
     *
     * @param anyURI : Uri to identify the Endpoint
     * @param method : RestMethod {GET, PUT, POST, DELETE} to identify the Endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return RESTEndpoint matching the given parameters
     */
    public RESTEndpoint getRestEndpoint(URI anyURI, restMethod method, CsarId csarId);

    /**
     * This method queries for a WSDLEndpoint identified by the given CSARID and PlanId
     *
     * @param csarId an id of type CSARID
     * @param planId an id of type QName
     * @return a WSDLEndpoint representing a Plan stored in the endpoint db or null if nothing was found
     */
    public WSDLEndpoint getWSDLEndpointForPlanId(CsarId csarId, QName planId);

    /**
     * This method queries for a WSDLEndpoint identified by the given CSARID, NodeTypeImplementationId
     * and ImplementationArtifact Name
     *
     * @param csarId an id of type CSARID
     * @param nodeTypeImpl an id of type QName
     * @param iaName an id of type String
     * @return a WSDLEndpoint representing the given IA if one was found else null
     */
    public WSDLEndpoint getWSDLEndpointForIa(CsarId csarId, QName nodeTypeImpl, String iaName);

    /**
     * This method queries for all WSDLEndpoints identified by the given CSARID
     *
     * @param csarId an id of type CSARID
     * @return List of WSDLEndpoints of the given CSARID if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForCsarId(CsarId csarId);

    /**
     * This method queries for a WSDLEndpoint identified by NodeTypeImplementationId and
     * ImplementationArtifact Name
     *
     * @param nodeTypeImpl an id of type QName
     * @param iaName an id of type String
     * @return a WSDLEndpoint representing the given IA if one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(QName nodeTypeImpl, String iaName);

    /**
     * This method queries for all WSDLEndpoints
     *
     * @return List of WSDLEndpoints if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpoints();

    /**
     * This method stores a given RESTEndpoint object.
     *
     * @see WSDLEndpoint
     *
     * @param endpoint : RESTEndpoint to store
     */
    public void storeRESTEndpoint(RESTEndpoint endpoint);

    /**
     *
     * @param uri
     * @return
     */
    public boolean endpointExists(URI uri, CsarId csarId);

    /**
     * Removes all Endpoints associated with the given CsarId
     *
     * @param csarId the CsarId whose Endpoints should be removed
     */
    public void removeEndpoints(CsarId csarId);

    /**
     * Removes the given WSDL Endpoint stored for the given CSAR instance inside the core
     *
     * @param csarId The Id of the CSAR the WSDLEndpoint to remove relates to
     * @param endpoint the WSDL Endpoint to remove
     * @return true if removing the endpoint was successful, else false
     */
    public boolean removeWSDLEndpoint(CsarId csarId, WSDLEndpoint endpoint);

    /**
     * Debug print of plan endpoints.
     */
    @Deprecated
    public void printPlanEndpoints();

}
