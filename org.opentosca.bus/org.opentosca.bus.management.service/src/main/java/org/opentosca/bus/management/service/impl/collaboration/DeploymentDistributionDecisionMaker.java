package org.opentosca.bus.management.service.impl.collaboration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultMessage;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.collaboration.model.BodyType;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.InstanceDataMatchingRequest;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class determines on which OpenTOSCA Container instance an Implementation Artifact for a certain service instance
 * has to be deployed. It returns the host name of this Container instance which can then be used by the deployment and
 * invocation plug-ins to perform operations with the Implementation Artifact.<br>
 * <br>
 * <p>
 * To determine the responsible OpenTOSCA Container, a matching with the instance data of the different available
 * Containers is performed. Therefore, the infrastructure NodeTemplateInstance of the topology stack of the IA is
 * retrieved. Afterwards the matching of this NodeTemplateInstance with the instance data of the local OpenTOSCA
 * Container is done. If this is not successful, a matching request is distributed to other Containers via MQTT. In case
 * there is also no match, the local Container is used as default deployment location.<br>
 * <br>
 * <p>
 * {@link Settings#OPENTOSCA_COLLABORATION_MODE} and the respective config.ini entry can be used to control the
 * matching. If the property is <tt>true</tt>, matching is performed. If it is set to
 * <tt>false</tt>, all IA deployments will be performed locally. Therefore, the performance can be
 * increased by disabling this setting if distributed IA deployment is not needed.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart
 */
@Service
public class DeploymentDistributionDecisionMaker {

    private final static Logger LOG = LoggerFactory.getLogger(DeploymentDistributionDecisionMaker.class);

    // repository to access instance data via NodeTemplate identifiers
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;

    private final RequestSender sender;

    private final CamelContext camelContext;

    public DeploymentDistributionDecisionMaker(CamelContext camelContext, NodeTemplateInstanceRepository nodeTemplateInstanceRepository, RequestSender sender) {
        this.nodeTemplateInstanceRepository = nodeTemplateInstanceRepository;
        this.sender = sender;
        this.camelContext = camelContext;
    }

    /**
     * Filter out the 'State' property from the given properties Map if it is defined and return the corresponding entry
     * Set.
     *
     * @param properties the properties as Map
     * @return the properties as entry Set without 'State' property
     */
    private static Set<Entry<String, String>> getEntrySetWithoutState(final Map<String, String> properties) {
        return properties.entrySet().stream().filter((entry) -> !entry.getKey().equals("State"))
            .collect(Collectors.toSet());
    }

    /**
     * Check whether the build plan that corresponds to the given NodeTemplateInstance is finished.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the build plan is checked
     * @return <tt>true</tt> if the build plan is found and terminated, <tt>false</tt> otherwise
     */
    private static boolean isBuildPlanFinished(final NodeTemplateInstance nodeTemplateInstance) {
        if (Objects.isNull(nodeTemplateInstance)) {
            return false;
        }

        final PlanInstance buildPlan =
            nodeTemplateInstance.getServiceTemplateInstance().getPlanInstances().stream()
                .filter((plan) -> plan.getType().equals(PlanType.BUILD)).findFirst().orElse(null);

        return Objects.nonNull(buildPlan) && buildPlan.getState().equals(PlanInstanceState.FINISHED);
    }

    /**
     * Check whether a given Relationship Type is used to connect parts of a topology stack (infrastructure type) or
     * different topology stacks.
     *
     * @param relationType The Relationship Type to check
     * @return <tt>true</tt> if the Relationship Type is hostedOn, deployedOn or dependsOn and
     * <tt>false</tt> otherwise
     */
    private static boolean isInfrastructureRelationshipType(final QName relationType) {
        return relationType.equals(Types.hostedOnRelationType) || relationType.equals(Types.deployedOnRelationType)
            || relationType.equals(Types.dependsOnRelationType);
    }

    /**
     * Get the deployment location for IAs which are attached to the NodeTemplateInstance. If the collaboration mode is
     * turned on, this method performs an instance data matching to determine the deployment location. Therefore, the
     * infrastructure NodeTemplateInstance is searched in the topology. Afterwards, its type and properties are matched
     * against local and remote instance data to get the correct deployment location for the IAs. If the matching is not
     * successful, the local OpenTOSCA Container is returned as default deployment location.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the IAs have to be deployed
     * @return the location where the IAs should be deployed
     */
    public String getDeploymentLocation(final NodeTemplateInstance nodeTemplateInstance) {

        if (Objects.isNull(nodeTemplateInstance)) {
            LOG.error("NodeTemplateInstance object is null. Using local deployment.");
            return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        }

        if (!Boolean.parseBoolean(Settings.OPENTOSCA_COLLABORATION_MODE)) {
            // only perform matching if collaboration mode is turned on
            LOG.debug("Distributed IA deployment disabled. Using local deployment.");
            return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        }

        LOG.debug("Deployment distribution decision for IAs from NodeTemplateInstance with ID: {}",
            nodeTemplateInstance.getId());

        // check if decision is already made for this instance
        if (Objects.nonNull(nodeTemplateInstance.getManagingContainer())) {
            LOG.debug("ManagingContainer attribute is already set for this NodeTemplateInstance: {}",
                nodeTemplateInstance.getManagingContainer());
            return nodeTemplateInstance.getManagingContainer();
        }

        // get infrastructure NodeTemplate
        LOG.debug("Looking for infrastructure NodeTemplateInstance that corresponds to this NodeTemplateInstance...");
        final NodeTemplateInstance infrastructureNodeTemplateInstance = searchInfrastructureNode(nodeTemplateInstance);

        // check if "managingContainer" is already set for the infrastructure NodeTemplateInstance
        if (Objects.nonNull(infrastructureNodeTemplateInstance.getManagingContainer())) {

            // no instance data matching needed, as it was already performed for the
            // infrastructure NodeTemplateInstance
            final String managingContainer = infrastructureNodeTemplateInstance.getManagingContainer();

            LOG.debug("Infrastructure NodeTemplateInstance has set managingContainer attribute.");
            LOG.debug("Result of deployment distribution decision: {}", managingContainer);

            // current NodeTemplateInstance is managed by the same Container as the
            // infrastructure instance
            nodeTemplateInstance.setManagingContainer(managingContainer);
            nodeTemplateInstanceRepository.save(nodeTemplateInstance);
            return managingContainer;
        }

        // instance data matching has to be performed for the NodeTemplateInstance
        LOG.debug("Infrastructure NodeTemplateInstance has ID: {}", infrastructureNodeTemplateInstance.getId());

        // retrieve type and properties for the matching
        final QName infrastructureNodeType = infrastructureNodeTemplateInstance.getTemplateType();
        final Map<String, String> infrastructureProperties = infrastructureNodeTemplateInstance.getPropertiesAsMap();

        LOG.debug("Infrastructure NodeTemplateInstance has NodeType: {}", infrastructureNodeType);
        LOG.debug("Infrastructure NodeTemplateInstance has properties:");
        infrastructureProperties.entrySet().stream()
            .forEach(entry -> LOG.debug("Key: {}; Value: {}", entry.getKey(), entry.getValue()));

        // match NodeType and properties against local instance data
        LOG.debug("Performing local instance data matching...");
        String deploymentLocation = performInstanceDataMatching(infrastructureNodeType, infrastructureProperties);
        if (Objects.nonNull(deploymentLocation)) {
            LOG.debug("Found matching local instance data. Deployment will be done at: {}", deploymentLocation);

            // set property to speed up future matching
            infrastructureNodeTemplateInstance.setManagingContainer(deploymentLocation);
            nodeTemplateInstance.setManagingContainer(deploymentLocation);

            // update stored entities
            nodeTemplateInstanceRepository.save(nodeTemplateInstance);
            nodeTemplateInstanceRepository.save(infrastructureNodeTemplateInstance);

            return deploymentLocation;
        }

        // match against instance data at remote OpenTOSCA Containers
        LOG.debug("Local instance data matching had no success. Performing matching with remote instance data...");
        deploymentLocation = performRemoteInstanceDataMatching(infrastructureNodeType, infrastructureProperties);
        if (Objects.nonNull(deploymentLocation)) {
            LOG.debug("Found matching remote instance data. Deployment will be done on OpenTOSCA Container with host name: {}",
                deploymentLocation);

            // set property to speed up future matching
            infrastructureNodeTemplateInstance.setManagingContainer(deploymentLocation);
            nodeTemplateInstance.setManagingContainer(deploymentLocation);

            // update stored entities
            nodeTemplateInstanceRepository.save(nodeTemplateInstance);
            nodeTemplateInstanceRepository.save(infrastructureNodeTemplateInstance);

            return deploymentLocation;
        }

        // default (no matching): return host name of local container
        LOG.debug("Remote instance data matching had no success. Returning local host name as default deployment location.");
        nodeTemplateInstance.setManagingContainer(Settings.OPENTOSCA_CONTAINER_HOSTNAME);
        nodeTemplateInstanceRepository.save(nodeTemplateInstance);
        return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
    }

    /**
     * Search for the infrastructure NodeTemplateInstance on which the given NodeTemplateInstance is
     * hosted/deployed/based. In the context of instance data matching the infrastructure Node should always be the Node
     * at the bottom of a stack in the topology. If an OpenTOSCA Container manages this bottom Node, it can be used to
     * deploy all IAs attached to Nodes that are above the infrastructure Node in the topology.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the infrastructure is searched
     * @return the infrastructure NodeTemplateInstance
     */
    private NodeTemplateInstance searchInfrastructureNode(final NodeTemplateInstance nodeTemplateInstance) {
        LOG.debug("Looking for infrastructure NodeTemplate at NodeTemplate {} and below...",
            nodeTemplateInstance.getTemplateId());

        final Collection<RelationshipTemplateInstance> outgoingRelationships =
            nodeTemplateInstance.getOutgoingRelations();

        // terminate search if bottom NodeTemplate is found
        if (outgoingRelationships.isEmpty()) {
            LOG.debug("NodeTemplate {} is the infrastructure NodeTemplate", nodeTemplateInstance.getTemplateId());
            return nodeTemplateInstance;
        }

        LOG.debug("NodeTemplate {} has outgoing RelationshipTemplates...", nodeTemplateInstance.getTemplateId());

        for (final RelationshipTemplateInstance relation : outgoingRelationships) {
            final QName relationType = relation.getTemplateType();
            LOG.debug("Found outgoing RelationshipTemplate of type: {}", relationType);

            // traverse topology stack downwards
            if (isInfrastructureRelationshipType(relationType)) {
                LOG.debug("Continue search with the target of the RelationshipTemplate...");
                return searchInfrastructureNode(relation.getTarget());
            } else {
                LOG.debug("RelationshipType is not valid for infrastructure search (e.g. hostedOn).");
            }
        }

        // if all outgoing relationships are not of the searched types, the NodeTemplate is the
        // bottom one
        return nodeTemplateInstance;
    }

    /**
     * Match the given NodeType and properties against instance data from the local repository. The matching is
     * successful if a NodeTemplateInstance with the same NodeType and the same values for the properties is found in
     * the instance data.
     *
     * @param infrastructureNodeType   the NodeType of the NodeTemplate which has to be matched
     * @param infrastructureProperties the set of properties of the NodeTemplate which has to be matched
     * @return the deployment location if a matching NodeTemplateInstance is found, <tt>null</tt> otherwise.
     */
    protected String performInstanceDataMatching(final QName infrastructureNodeType,
                                                 final Map<String, String> infrastructureProperties) {

        Objects.requireNonNull(infrastructureNodeType,
            "QName for NodeType of infrastructure node must not be null for instance data matching");

        // get the infrastructure properties without 'state' property for comparison
        final Set<Entry<String, String>> infrastructureEntrySet = getEntrySetWithoutState(infrastructureProperties);

        // search NodeTemplateInstance with matching NodeType and Properties which is already
        // provisioned completely
        final NodeTemplateInstance matchingInstance =
            nodeTemplateInstanceRepository.findByTemplateType(infrastructureNodeType).stream()
                .filter(instance -> instance.getServiceTemplateInstance().getState()
                    .equals(ServiceTemplateInstanceState.CREATED))
                .filter(instance -> instance.getState()
                    .equals(NodeTemplateInstanceState.STARTED))
                .filter(instance -> isBuildPlanFinished(instance))
                .filter(instance -> getEntrySetWithoutState(instance.getPropertiesAsMap()).equals(infrastructureEntrySet))
                .findFirst().orElse(null);

        if (Objects.nonNull(matchingInstance)) {
            // check whether the matching NodeTemplateInstance is managed by this Container
            if (Objects.isNull(matchingInstance.getManagingContainer())) {
                // If no Container is set and the build plan is finished, this means that there
                // was no IA invocation in the build plan and therefore also no remote
                // deployment which means it is managed locally.
                return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
            } else {
                return matchingInstance.getManagingContainer();
            }
        }

        // no matching found
        return null;
    }

    /**
     * Match the given NodeType and properties against instance data from remote OpenTOSCA Containers. The matching is
     * successful if a NodeTemplateInstance with the same NodeType and the same values for the properties is found in
     * their instance data. The method sends a request via MQTT to all subscribed OpenTOSCA Containers. Afterwards, it
     * waits for a reply which contains the host name of the OpenTOSCA Container that found matching instance data. If
     * it receives a reply in time, it returns the host name. Otherwise, it returns null.
     *
     * @param infrastructureNodeType   the NodeType of the NodeTemplate which has to be matched
     * @param infrastructureProperties the set of properties of the NodeTemplate which has to be matched
     * @return the host name of the OpenTOSCA Container which found a matching NodeTemplateInstance if one is found,
     * <tt>null</tt> otherwise.
     */
    private String performRemoteInstanceDataMatching(final QName infrastructureNodeType,
                                                     final Map<String, String> infrastructureProperties) {

        LOG.debug("Creating collaboration message for remote instance data matching...");

        // transform infrastructureProperties for the message body
        final KeyValueMap properties = new KeyValueMap();
        final List<KeyValueType> propertyList = properties.getKeyValuePair();
        infrastructureProperties.entrySet().forEach((entry) -> propertyList.add(new KeyValueType(entry.getKey(),
            entry.getValue())));

        // create collaboration message
        final BodyType content = new BodyType(new InstanceDataMatchingRequest(infrastructureNodeType, properties));
        final CollaborationMessage collaborationMessage = new CollaborationMessage(new KeyValueMap(), content);

        // perform remote instance data matching and wait 10s for a response
        final Exchange response =
            this.sender.sendRequestToRemoteContainer(new DefaultMessage(camelContext),
                RemoteOperations.INVOKE_INSTANCE_DATA_MATCHING,
                collaborationMessage, 10000);

        if (Objects.nonNull(response)) {
            LOG.debug("Received a response in time.");

            // read the deployment location from the reply
            return response.getIn().getHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), String.class);
        } else {
            LOG.debug("No response received within the timeout interval.");
            return null;
        }
    }
}
