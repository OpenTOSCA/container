package org.opentosca.container.core.service;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;

/**
 * Interface of the InstanceDataService. The interface specifies methods to manage instances of ServiceTemplates
 * (=ServiceInstances) and NodeTemplates (NodeInstances) and properties of NodeInstances.
 */
public interface IInstanceDataService {

    /**
     * Queries for all ServiceInstances identified by the given parameters. It then returns a List of the matching
     * serviceInstances.
     *
     * @param serviceInstanceID : ID to identify the serviceInstance
     * @return List containing all corresponding ServiceInstances
     * @TODO: additional parameters in JDOC
     */
    public List<ServiceInstance> getServiceInstances(URI serviceInstanceID, String serviceTemplateName,
                                                     QName serviceTemplateID);

    /**
     * returns all NodeInstances matching the given parameters the parameters are ANDed therefore a nodeInstance has to
     * match all parameters to be returned
     *
     * @return all matching nodeInstances
     */
    public List<NodeInstance> getNodeInstances(URI nodeInstanceID, String nodeTemplateID, String nodeTemplateName,
                                               URI serviceInstanceID);

    /**
     * returns all RelationInstances matching the given parameters the parameters are ANDed therefore a relationInstance
     * has to match all parameters to be returned
     *
     * @param relationInstanceID the relationInstanceId
     * @return all matching nodeInstances
     */
    public List<RelationInstance> getRelationInstances(URI relationInstanceID, QName relationshipTemplateID,
                                                       String relationshipTemplateName, URI serviceInstanceID);

    public List<ServiceInstance> getServiceInstancesWithDetails(CsarId csarId, String serviceTemplateId,
                                                                Integer serviceTemplateInstanceID);
}
