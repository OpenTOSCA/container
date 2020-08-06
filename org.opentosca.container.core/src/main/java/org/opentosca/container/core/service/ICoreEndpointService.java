package org.opentosca.container.core.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;

/**
 * This interface provides methods to retrieve and store endpoints. It is meant to be used by the Engines.
 */
public interface ICoreEndpointService {

    /**
     * This method queries for all WSDL-Endpoints identified by the given portType and thorID. It then returns a List of
     * the retrieved Endpoints.
     *
     * @param portType : PortType to identify the Endpoint
     * @param thorID   : thorID to identify the Endpoint
     * @return ArrayList containing all corresponding WSDLEndpoints
     * @see WSDLEndpoint
     */
    public List<WSDLEndpoint> getWSDLEndpoints(QName portType, String triggeringContainer, CsarId csarId);

    /**
     * This method stores a given WSDLEndpoint object.
     *
     * @param endpoint : The WSDL-Endpoint to store
     * @see WSDLEndpoint
     */
    public void storeWSDLEndpoint(WSDLEndpoint endpoint);

    /**
     * This method queries for RESTEndpoints identified by the given URI and thorID
     *
     * @param anyURI : Uri to identify the Endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return ArrayList containing all endpoints matching the given parameters
     * @see RESTEndpoint
     */
    public List<RESTEndpoint> getRestEndpoints(URI anyURI, String triggeringContainer, CsarId csarId);

    /**
     * This method queries for a RESTEndpoint identified by the given URI, RestMethod {GET,PUT,POST,DELETE} and thorID
     *
     * @param anyURI : Uri to identify the Endpoint
     * @param method : RestMethod {GET, PUT, POST, DELETE} to identify the Endpoint
     * @param thorID : thorID to identify the Endpoint
     * @return RESTEndpoint matching the given parameters
     * @see RESTEndpoint
     */
    public RESTEndpoint getRestEndpoint(URI anyURI, restMethod method, String triggeringContainer, CsarId csarId);

    /**
     * This method queries for a WSDLEndpoint identified by the given CSARID and PlanId
     *
     * @param csarId an id of type CSARID
     * @param planId an id of type QName
     * @return a WSDLEndpoint representing a Plan stored in the endpoint db or null if nothing was found
     */
    public List<WSDLEndpoint> getWSDLEndpointsForPlanId(String triggeringContainer, CsarId csarId, QName planId);

    /**
     * This method queries for a WSDLEndpoint identified by the given CSARID, NodeTypeImplementationId and
     * ImplementationArtifact Name
     *
     * @param csarId       an id of type CSARID
     * @param nodeTypeImpl an id of type QName
     * @param iaName       an id of type String
     * @return a WSDLEndpoint representing the given IA if one was found else null
     */
    public WSDLEndpoint getWSDLEndpointForIa(CsarId csarId, QName nodeTypeImpl, String iaName);

    /**
     * This method queries for all WSDLEndpoints identified by the given CSARID
     *
     * @param csarId an id of type CSARID
     * @return List of WSDLEndpoints of the given CSARID if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForCsarId(String triggeringContainer, CsarId csarId);

    /**
     * This method queries for all WSDLEndpoints identified by the given Container and ServiceTemplateInstance ID
     *
     * @param triggeringContainer       OpenTOSCA Container host name where the ServiceTemplateInstance identified by
     *                                  serviceTemplateInstanceID resides
     * @param serviceTemplateInstanceID an ID which identifies a ServiceTemplateInstance uniquely
     * @return List of WSDLEndpoints of the given serviceTemplateInstanceID if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID);

    /**
     * This method queries for a WSDLEndpoint identified by NodeTypeImplementationId and ImplementationArtifact Name
     *
     * @param nodeTypeImpl an id of type QName
     * @param iaName       an id of type String
     * @return a WSDLEndpoint representing the given IA if one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(String triggeringContainer, String managingContainer,
                                                                 QName nodeTypeImpl, String iaName);

    /**
     * This method queries for all WSDLEndpoints
     *
     * @return List of WSDLEndpoints if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpoints();

    /**
     * This method stores a given RESTEndpoint object.
     *
     * @param endpoint : RESTEndpoint to store
     * @see WSDLEndpoint
     */
    public void storeRESTEndpoint(RESTEndpoint endpoint);

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
    @Deprecated
    public void printPlanEndpoints();
}
