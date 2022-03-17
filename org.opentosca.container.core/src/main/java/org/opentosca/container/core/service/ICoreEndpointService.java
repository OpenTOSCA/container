package org.opentosca.container.core.service;

import java.util.List;

import javax.transaction.Transactional;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;

/**
 * This interface provides methods to retrieve and store endpoints. It is meant to be used by the Engines.
 */
public interface ICoreEndpointService {

    /**
     * This method queries for all Endpoints identified by the given portType. It then returns a List of the retrieved
     * Endpoints.
     *
     * @param portType : PortType to identify the Endpoint
     * @return ArrayList containing all corresponding Endpoints
     * @see Endpoint
     */
    List<Endpoint> getEndpoints(QName portType, String triggeringContainer, CsarId csarId);

    /**
     * This method stores a given Endpoint object.
     *
     * @param endpoint : The Endpoint to store
     * @see Endpoint
     */
    void storeEndpoint(Endpoint endpoint);

    /**
     * This method queries for a Endpoint identified by the given CSARID and PlanId
     *
     * @param csarId an id of type CSARID
     * @param planId an id of type QName
     * @return a ndpoint representing a Plan stored in the endpoint db or null if nothing was found
     */
    List<Endpoint> getEndpointsForPlanId(String triggeringContainer, CsarId csarId, QName planId);

    /**
     * This method queries for all Endpoints identified by the given Container and ServiceTemplateInstance ID
     *
     * @param triggeringContainer       OpenTOSCA Container host name where the ServiceTemplateInstance identified by
     *                                  serviceTemplateInstanceID resides
     * @param serviceTemplateInstanceID an ID which identifies a ServiceTemplateInstance uniquely
     * @return List of Endpoints of the given serviceTemplateInstanceID if one was found, an empty list otherwise
     */
    List<Endpoint> getEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID);

    /**
     * This method queries for a Endpoint identified by NodeTypeImplementationId and ImplementationArtifact Name
     *
     * @param nodeTypeImpl an id of type QName
     * @param iaName       an id of type String
     * @return a Endpoint representing the given IA if one was found else null
     */
    List<Endpoint> getEndpointsForNTImplAndIAName(String triggeringContainer, String managingContainer,
                                                  QName nodeTypeImpl, String iaName);

    /**
     * This method queries for all Endpoints
     *
     * @return List of Endpoints or an emtpy list if none was found
     */
    List<Endpoint> getEndpoints();

    /**
     * Removes the given Endpoint if found
     *
     * @param endpoint the Endpoint to remove
     */
    void removeEndpoint(Endpoint endpoint);

    List<Endpoint> getEndpointsWithMetadata();
}
