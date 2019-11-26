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
     * This method queries for all WSDL-Endpoints identified by the given portType, Container and
     * csarId. It then returns a List of the retrieved Endpoints.
     *
     * @see WSDLEndpoint
     *
     * @param portType : PortType to identify the Endpoint
     * @param triggeringContainer Container where the CSAR identified by csarId resides
     * @param csarId : csarId to identify the Endpoint
     * @return ArrayList containing all corresponding WSDLEndpoints
     */
    public List<WSDLEndpoint> getWSDLEndpoints(QName portType, String triggeringContainer, CSARID csarId);

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
    public List<RESTEndpoint> getRestEndpoints(URI anyURI, String triggeringContainer, CSARID csarId);

    /**
     * This method queries for a List of WSDLEndpoint identified by the given Container, CSARID and
     * PlanId
     *
     * @param triggeringContainer Container where the CSAR identified by csarId resides
     * @param csarId an id of type CSARID
     * @param planId an id of type QName
     * @return a WSDLEndpoint representing a Plan stored in the endpoint db or null if nothing was found
     */
    public List<WSDLEndpoint> getWSDLEndpointsForPlanId(String triggeringContainer, CSARID csarId, QName planId);

    /**
     * This method stores a given RESTEndpoint object.
     *
     * @see WSDLEndpoint
     *
     * @param endpoint : RESTEndpoint to store
     */
    public void storeRESTEndpoint(RESTEndpoint endpoint);

    /**
     * Removes all plan endpoints associated with the CSAR identified by the given OpenTOSCA Container
     * host name and the given CSARID
     *
     * @param triggeringContainer the OpenTOSCA Container where the CSAR is deployed
     * @param csarId the CSARID whose plan endpoints should be removed
     */
    public void removePlanEndpoints(String triggeringContainer, CSARID csarId);

    /**
     * Debug print of plan endpoints.
     */
    public void printPlanEndpoints();

    /**
     * This method queries for all WSDLEndpoints identified by the given Container and CSARID
     *
     * @param triggeringContainer Container where the CSAR identified by csarId resides
     * @param csarId an id of type CSARID
     * @return List of WSDLEndpoints of the given CSARID if min. one was found else null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForCSARID(String triggeringContainer, CSARID csarId);

    /**
     * This method queries for all WSDLEndpoints identified by the given Container and
     * ServiceTemplateInstance ID
     *
     * @param triggeringContainer OpenTOSCA Container host name where the ServiceTemplateInstance
     *        identified by serviceTemplateInstanceID resides
     * @param serviceTemplateInstanceID an ID which identifies a ServiceTemplateInstance uniquely
     * @return List of WSDLEndpoints of the given serviceTemplateInstanceID if min. one was found else
     *         null
     */
    public List<WSDLEndpoint> getWSDLEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID);

    /**
     * This method queries for a WSDLEndpoint identified by the triggering and managing OpenTOSCA
     * Container, the NodeTypeImplementationId and the ImplementationArtifact name
     *
     * @param triggeringContainer OpenTOSCA Container which initiated the creation of the endpoint
     * @param managingContainer OpenTOSCA Container which is responsible for handling the endpoint
     * @param nodeTypeImpl an ID of type QName
     * @param iaName an ID of type String
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
     * Removes the given WSDL Endpoint if found
     *
     * @param endpoint the WSDL Endpoint to remove
     * @return true if removing the endpoint was successful, else false
     */
    public boolean removeWSDLEndpoint(WSDLEndpoint endpoint);

}
