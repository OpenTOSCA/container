package org.opentosca.bus.management.service.impl.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.tosca.convention.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class determines on which OpenTOSCA Container instance an Implementation Artifact for a
 * certain service instance has to be deployed. It returns the host name of this Container instance
 * which can then be used by the deployment and invocation plug-ins to perform operations with the
 * Implementation Artifact.<br>
 * <br>
 *
 * To determine the responsible OpenTOSCA Container, a matching with the instance data of the
 * different available Containers is performed. Therefore, the infrastructure NodeTemplateInstance
 * of the topology stack of the IA is retrieved. Afterwards the matching of this
 * NodeTemplateInstance with the instance data of the local OpenTOSCA Container is done. If this is
 * not successful, a matching request is distributed to other Containers via MQTT. In case there is
 * also no match, the local Container is used as default deployment location.<br>
 * <br>
 *
 * {@link Settings#OPENTOSCA_COLLABORATION_MODE} and the respective config.ini entry can be used to
 * control the matching. If the property is <tt>true</tt>, matching is performed. If it is set to
 * <tt>false</tt>, all IA deployments will be performed locally. Therefore, the performance can be
 * increased by disabling this setting if distributed IA deployment is not needed.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class DeploymentDistributionDecisionMaker {

    private final static Logger LOG = LoggerFactory.getLogger(DeploymentDistributionDecisionMaker.class);

    // repository to access instance data via NodeTemplate identifiers
    private final static NodeTemplateInstanceRepository nodeTemplateInstanceRepository =
        new NodeTemplateInstanceRepository();

    /**
     * Get the deployment location for IAs which are attached to the NodeTemplateInstance that is
     * identified by the given ID. If the collaboration mode is turned on, this method performs an
     * instance data matching to determine the deployment location. Therefore, the infrastructure
     * NodeTemplateInstance is searched in the topology. Afterwards, its type and properties are
     * matched against local and remote instance data to get the correct deployment location for the
     * IAs. If the matching is not successful, the local OpenTOSCA Container is returned as default
     * deployment location.
     *
     * @param nodeTemplateInstanceID the ID of the NodeTemplate for which the IAs have to be
     *        deployed
     * @return the location where the IAs should be deployed
     */
    public static String getDeploymentLocation(final Long nodeTemplateInstanceID) {

        // only perform matching if collaboration mode is turned on
        if (Settings.OPENTOSCA_COLLABORATION_MODE.equals("true")) {

            if (nodeTemplateInstanceID != null) {

                DeploymentDistributionDecisionMaker.LOG.debug("Deployment distribution decision for IAs from NodeTemplateInstance with ID: {}",
                                                              nodeTemplateInstanceID);

                // get the NodeTemplateInstance for the ID
                final Optional<NodeTemplateInstance> instanceOptional =
                    nodeTemplateInstanceRepository.find(nodeTemplateInstanceID);
                if (instanceOptional.isPresent()) {

                    DeploymentDistributionDecisionMaker.LOG.debug("Found corresponding NodeTemplateInstance in the instance data.");
                    final NodeTemplateInstance nodeTemplateInstance = instanceOptional.get();

                    DeploymentDistributionDecisionMaker.LOG.debug("Looking for infrastructure NodeTemplateInstance that corresponds to this NodeTemplateInstance...");

                    // get infrastructure NodeTemplate and NodeType for the matching
                    final NodeTemplateInstance infrastructureNodeTemplateInstance =
                        searchInfrastructureNode(nodeTemplateInstance);
                    final QName infrastructureNodeType = infrastructureNodeTemplateInstance.getTemplateType();

                    DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has ID: {} and NodeType: {}",
                                                                  infrastructureNodeTemplateInstance.getId(),
                                                                  infrastructureNodeType);

                    // retrieve properties for the instance
                    final Map<String, String> infrastructureProperties =
                        infrastructureNodeTemplateInstance.getPropertiesAsMap();

                    DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has properties:");
                    for (final String key : infrastructureProperties.keySet()) {
                        DeploymentDistributionDecisionMaker.LOG.debug("Key: {}; Value: {}", key,
                                                                      infrastructureProperties.get(key));
                    }

                    // match NodeType and properties against local instance data
                    DeploymentDistributionDecisionMaker.LOG.debug("Performing local instance data matching...");
                    if (performInstanceDataMatching(infrastructureNodeType, infrastructureProperties)) {
                        DeploymentDistributionDecisionMaker.LOG.debug("Found matching local instance data. Deployment will be done locally.");
                        return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
                    }

                    // match NodeType and properties against instance data on remote OpenTOSCA
                    // Containers
                    DeploymentDistributionDecisionMaker.LOG.debug("Local instance data matching had no success. Performing matching with remote instance data...");
                    final String remoteLocation =
                        performRemoteInstanceDataMatching(infrastructureNodeType, infrastructureProperties);
                    if (remoteLocation != null) {
                        DeploymentDistributionDecisionMaker.LOG.debug("Found matching remote instance data. Deployment will be done on OpenTOSCA Container with host name: {}",
                                                                      remoteLocation);
                        return remoteLocation;
                    }

                    DeploymentDistributionDecisionMaker.LOG.debug("Remote instance data matching had no success. Returning local host name as default deployment location.");
                } else {
                    DeploymentDistributionDecisionMaker.LOG.error("Unable to find NodeTemplateInstance with ID: {}",
                                                                  nodeTemplateInstanceID);
                }
            } else {
                DeploymentDistributionDecisionMaker.LOG.error("NodeTemplateInstanceID is null.");
            }
        } else {
            DeploymentDistributionDecisionMaker.LOG.debug("Distributed IA deployment disabled. Using local deployment.");
        }

        // default (no matching): return host name of local container
        return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
    }

    /**
     * Search for the infrastructure NodeTemplateInstance on which the given NodeTemplateInstance is
     * hosted/deployed/based. In the context of instance data matching the infrastructure Node
     * should always be the Node at the bottom of a stack in the topology. If an OpenTOSCA Container
     * manages this bottom Node, it can be used to deploy all IAs attached to Nodes that are above
     * the infrastructure Node in the topology.<br>
     * <br>
     *
     * <b>Caution</b>: To match infrastructure Nodes, they must have properties which clearly
     * identify a device (e.g. a Raspberry Pi) or a virtual infrastructure (e.g a Docker Engine or a
     * Cloud Provider). Unfortunately, for Raspberry Pi topologies the bottom Node (RaspberryPI3)
     * has no such properties. Instead the RaspbianJessie Node which is located above has these
     * properties. Therefore, the search has to be interrupted for such topologies before the bottom
     * Node is reached. This is a bit hacky at the moment.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the infrastructure is searched
     * @return the infrastructure NodeTemplateInstance
     */
    private static NodeTemplateInstance searchInfrastructureNode(final NodeTemplateInstance nodeTemplateInstance) {
        DeploymentDistributionDecisionMaker.LOG.debug("Looking for infrastructure NodeTemplate at NodeTemplate {} and below...",
                                                      nodeTemplateInstance.getTemplateId());

        // terminate search if a RaspbianJessie NodeTemplate is reached
        if (isRaspbianJessieNodeType(nodeTemplateInstance.getTemplateType())) {
            DeploymentDistributionDecisionMaker.LOG.debug("RaspbianJessie NodeTemplate found. Terminating search.");
            return nodeTemplateInstance;
        } else {
            final Collection<RelationshipTemplateInstance> outgoingRelationships =
                nodeTemplateInstance.getOutgoingRelations();

            // terminate search if bottom NodeTemplate is found
            if (outgoingRelationships.isEmpty()) {
                DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplate {} is the infrastructure NodeTemplate",
                                                              nodeTemplateInstance.getTemplateId());
                return nodeTemplateInstance;
            } else {

                DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplate {} is not of type RaspbianJessie and not the bottom NodeTemplate of the topology stack.",
                                                              nodeTemplateInstance.getTemplateId());
                for (final RelationshipTemplateInstance relation : outgoingRelationships) {
                    final QName relationType = relation.getTemplateType();
                    DeploymentDistributionDecisionMaker.LOG.debug("Found outgoing RelationshipTemplate of type: {}",
                                                                  relationType);

                    // traverse topology stack downwards
                    if (relationType.equals(Types.hostedOnRelationType)
                        || relationType.equals(Types.deployedOnRelationType)
                        || relationType.equals(Types.dependsOnRelationType)) {
                        DeploymentDistributionDecisionMaker.LOG.debug("Continue search with the target of the RelationshipTemplate...");
                        return relation.getTarget();
                    } else {
                        DeploymentDistributionDecisionMaker.LOG.debug("RelationshipType is not valid for infrastructure search (e.g. hostedOn).");
                    }
                }
            }
        }

        return nodeTemplateInstance;
    }

    /**
     * Match the given NodeType and properties against instance data from the local repository. The
     * matching is successful if a NodeTemplateInstance with the same NodeType and the same values
     * for the properties is found in the instance data.
     *
     * @param infrastructureNodeType the NodeType of the NodeTemplate which has to be matched
     * @param infrastructureProperties the set of properties of the NodeTemplate which has to be
     *        matched
     * @return <tt>true</tt> if a matching NodeTemplateInstance is found, <tt>false</tt> otherwise.
     */
    private static boolean performInstanceDataMatching(final QName infrastructureNodeType,
                                                       final Map<String, String> infrastructureProperties) {

        // retrieve all instances with matching type from instance data
        final Collection<NodeTemplateInstance> typeMatchingInstances =
            nodeTemplateInstanceRepository.findByTemplateType(infrastructureNodeType);

        for (final NodeTemplateInstance typeMatchingInstance : typeMatchingInstances) {
            DeploymentDistributionDecisionMaker.LOG.debug("Found NodeTemplateInstance with matching type. ID: {}",
                                                          typeMatchingInstance.getId());

            // TODO: Only match with special instances which are created to represent managed
            // infrastructure. Property <State>Infrastructure</State>?

            // TODO: perform property matching --> return true if successful
        }

        // no matching found
        return false;
    }

    /**
     * Match the given NodeType and properties against instance data from remote OpenTOSCA Container
     * instances. The matching is successful if a NodeTemplateInstance with the same NodeType and
     * the same values for the properties is found in their instance data. The method sends a
     * request via MQTT to all subscribed OpenTOSCA Container instances. Afterwards, it waits for a
     * reply which contains the host name of the OpenTOSCA Container that found matching instance
     * data. If it receives a reply in time, it returns the host name. Otherwise, it returns null.
     *
     * @param infrastructureNodeType the NodeType of the NodeTemplate which has to be matched
     * @param infrastructureProperties the set of properties of the NodeTemplate which has to be
     *        matched
     * @return the host name of the OpenTOSCA Container which found a matching NodeTemplateInstance
     *         if one is found, <tt>null</tt> otherwise.
     */
    private static String performRemoteInstanceDataMatching(final QName infrastructureNodeType,
                                                            final Map<String, String> infrastructureProperties) {
        // TODO: perform remote matching
        return null;
    }

    /**
     * Checks whether the given NodeType is of Type RaspbianJessie:
     * {@link org.opentosca.container.core.tosca.convention.Types#raspbianJessieOSNodeType}
     *
     * @param nodeType the NodeType to check
     * @return <tt>true</tt> if the NodeType is of type RasbianJessie, <tt>false</tt> otherwise.
     */
    private static boolean isRaspbianJessieNodeType(final QName nodeType) {
        return nodeType.equals(Types.raspbianJessieOSNodeType);
    }
}
