package org.opentosca.bus.management.service.impl.util;

import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class determines on which OpenTOSCA Container instance an Implementation Artifact for a
 * certain service instance has to be deployed. It returns the host name of this Container instance
 * which can then be used by the deployment and invocation plug-ins to perform operations with the
 * Implementation Artifact.<br>
 * <br>
 *
 * To determine the correct Container, a matching with the instance data of the different available
 * Container instances is performed. Therefore, the infrastructure NodeTemplate of the topology
 * stack of the IA is retrieved. Afterwards the matching of this NodeTemplate with the instance data
 * of the local OpenTOSCA Container is done. If this is not successful, the matching request is
 * distributed to other Container via MQTT. In case there is also no match, the local Container is
 * used as default deployment location.<br>
 * <br>
 *
 * {@link Settings#OPENTOSCA_COLLABORATION_MODE} and the respective config.ini entry can be used to
 * control the matching. If the property is <tt>true</tt>, matching is performed. If it is set to
 * <tt>false</tt>, all IA deployments will be performed locally. Therefore, the performance can be
 * increased by this setting if distributed IA deployment is not needed.<br>
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
     * Get the deployment location for IAs which are attached to the NodeTemplate that is identified
     * by the given ID. If the collaboration mode is turned on, this method performs an instance
     * data matching to determine the deployment location. Therefore, the infrastructure
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
     * hosted/deployed/based. Infrastructure NodeTemplates are for example of type
     * <tt>DockerEngine</tt> or <tt>RaspbianJessie</tt>.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the infrastructure is searched
     * @return the infrastructure NodeTemplateInstance
     */
    private static NodeTemplateInstance searchInfrastructureNode(final NodeTemplateInstance nodeTemplateInstance) {
        // TODO: search for the infrastructure NodeTemplate
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
     * @return <tt>true</tt> if matching NodeTemplateInstance is found, <tt>false</tt> otherwise.
     */
    private static boolean performInstanceDataMatching(final QName infrastructureNodeType,
                                                       final Map<String, String> infrastructureProperties) {
        // TODO: perform local matching

        // no matching found
        return false;
    }

    /**
     * Match the given NodeType and properties against instance data from remote OpenTOSCA Container
     * instances. The matching is successful if a NodeTemplateInstance with the same NodeType and
     * the same values for the properties is found in their instance data. The method sends a
     * request via MQTT to all subscribed OpenTOSCA Container instances. Afterwards, it waits for a
     * reply which contains the host name of the OpenTOSCA Container that found matching instance
     * data. If it receives a reply in time, it returns the hostname. Otherwise, it returns null.
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
}
