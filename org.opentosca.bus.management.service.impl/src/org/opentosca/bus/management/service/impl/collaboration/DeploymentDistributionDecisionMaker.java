package org.opentosca.bus.management.service.impl.collaboration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.impl.Activator;
import org.opentosca.bus.management.service.impl.collaboration.model.BodyType;
import org.opentosca.bus.management.service.impl.collaboration.model.CollaborationMessage;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueMap;
import org.opentosca.bus.management.service.impl.collaboration.model.KeyValueType;
import org.opentosca.bus.management.service.impl.collaboration.model.RemoteOperations;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
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
     * Get the deployment location for IAs which are attached to the NodeTemplateInstance. If the
     * collaboration mode is turned on, this method performs an instance data matching to determine
     * the deployment location. Therefore, the infrastructure NodeTemplateInstance is searched in
     * the topology. Afterwards, its type and properties are matched against local and remote
     * instance data to get the correct deployment location for the IAs. If the matching is not
     * successful, the local OpenTOSCA Container is returned as default deployment location.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the IAs have to be deployed
     * @return the location where the IAs should be deployed
     */
    public static String getDeploymentLocation(final NodeTemplateInstance nodeTemplateInstance) {

        // only perform matching if collaboration mode is turned on
        if (Settings.OPENTOSCA_COLLABORATION_MODE.equals("true")) {

            DeploymentDistributionDecisionMaker.LOG.debug("Deployment distribution decision for IAs from NodeTemplateInstance with ID: {}",
                                                          nodeTemplateInstance.getId());

            DeploymentDistributionDecisionMaker.LOG.debug("Looking for infrastructure NodeTemplateInstance that corresponds to this NodeTemplateInstance...");

            // get infrastructure NodeTemplate
            final NodeTemplateInstance infrastructureNodeTemplateInstance =
                searchInfrastructureNode(nodeTemplateInstance);

            // check if "managingContainer" is already set for the NodeTemplateInstance
            if (infrastructureNodeTemplateInstance.getManagingContainer() != null) {

                // no instance data matching needed, as it was already performed for the
                // infrastructure NodeTemplateInstance
                final String managingContainer = infrastructureNodeTemplateInstance.getManagingContainer();

                DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has set managingContainer attribute.");
                DeploymentDistributionDecisionMaker.LOG.debug("Result of deployment distribution decision: {}",
                                                              managingContainer);

                // current NodeTemplateInstance is managed by the same Container as the
                // infrastructure instance
                nodeTemplateInstance.setManagingContainer(managingContainer);
                return managingContainer;
            } else {

                // instance data matching has to be performed for the NodeTemplateInstance
                DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has ID: {}",
                                                              infrastructureNodeTemplateInstance.getId());

                // retrieve type and properties for the matching
                final QName infrastructureNodeType = infrastructureNodeTemplateInstance.getTemplateType();
                final Map<String, String> infrastructureProperties =
                    infrastructureNodeTemplateInstance.getPropertiesAsMap();

                DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has NodeType: {}",
                                                              infrastructureNodeType);
                DeploymentDistributionDecisionMaker.LOG.debug("Infrastructure NodeTemplateInstance has properties:");
                for (final String key : infrastructureProperties.keySet()) {
                    DeploymentDistributionDecisionMaker.LOG.debug("Key: {}; Value: {}", key,
                                                                  infrastructureProperties.get(key));
                }

                // match NodeType and properties against local instance data
                DeploymentDistributionDecisionMaker.LOG.debug("Performing local instance data matching...");
                if (performInstanceDataMatching(infrastructureNodeType, infrastructureProperties)) {
                    DeploymentDistributionDecisionMaker.LOG.debug("Found matching local instance data. Deployment will be done locally.");

                    // infrastructure and current instance are both managed locally
                    infrastructureNodeTemplateInstance.setManagingContainer(Settings.OPENTOSCA_CONTAINER_HOSTNAME);
                    nodeTemplateInstance.setManagingContainer(Settings.OPENTOSCA_CONTAINER_HOSTNAME);

                    return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
                }

                // match against instance data at remote OpenTOSCA Containers
                DeploymentDistributionDecisionMaker.LOG.debug("Local instance data matching had no success. Performing matching with remote instance data...");
                final String remoteLocation =
                    performRemoteInstanceDataMatching(infrastructureNodeType, infrastructureProperties);
                if (remoteLocation != null) {
                    DeploymentDistributionDecisionMaker.LOG.debug("Found matching remote instance data. Deployment will be done on OpenTOSCA Container with host name: {}",
                                                                  remoteLocation);

                    // infrastructure and current instance are both managed by a remote Container
                    infrastructureNodeTemplateInstance.setManagingContainer(remoteLocation);
                    nodeTemplateInstance.setManagingContainer(remoteLocation);

                    return remoteLocation;
                }

                DeploymentDistributionDecisionMaker.LOG.debug("Remote instance data matching had no success. Returning local host name as default deployment location.");
            }
        } else {
            DeploymentDistributionDecisionMaker.LOG.debug("Distributed IA deployment disabled. Using local deployment.");
        }

        // default (no matching): return host name of local container
        nodeTemplateInstance.setManagingContainer(Settings.OPENTOSCA_CONTAINER_HOSTNAME);
        return Settings.OPENTOSCA_CONTAINER_HOSTNAME;
    }

    /**
     * Search for the infrastructure NodeTemplateInstance on which the given NodeTemplateInstance is
     * hosted/deployed/based. In the context of instance data matching the infrastructure Node
     * should always be the Node at the bottom of a stack in the topology. If an OpenTOSCA Container
     * manages this bottom Node, it can be used to deploy all IAs attached to Nodes that are above
     * the infrastructure Node in the topology.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance for which the infrastructure is searched
     * @return the infrastructure NodeTemplateInstance
     */
    private static NodeTemplateInstance searchInfrastructureNode(final NodeTemplateInstance nodeTemplateInstance) {
        DeploymentDistributionDecisionMaker.LOG.debug("Looking for infrastructure NodeTemplate at NodeTemplate {} and below...",
                                                      nodeTemplateInstance.getTemplateId());

        final Collection<RelationshipTemplateInstance> outgoingRelationships =
            nodeTemplateInstance.getOutgoingRelations();

        // terminate search if bottom NodeTemplate is found
        if (outgoingRelationships.isEmpty()) {
            DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplate {} is the infrastructure NodeTemplate",
                                                          nodeTemplateInstance.getTemplateId());
            return nodeTemplateInstance;
        } else {
            DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplate {} has outgoing RelationshipTemplates...",
                                                          nodeTemplateInstance.getTemplateId());

            for (final RelationshipTemplateInstance relation : outgoingRelationships) {
                final QName relationType = relation.getTemplateType();
                DeploymentDistributionDecisionMaker.LOG.debug("Found outgoing RelationshipTemplate of type: {}",
                                                              relationType);

                // traverse topology stack downwards
                if (relationType.equals(Types.hostedOnRelationType) || relationType.equals(Types.deployedOnRelationType)
                    || relationType.equals(Types.dependsOnRelationType)) {
                    DeploymentDistributionDecisionMaker.LOG.debug("Continue search with the target of the RelationshipTemplate...");
                    return searchInfrastructureNode(relation.getTarget());
                } else {
                    DeploymentDistributionDecisionMaker.LOG.debug("RelationshipType is not valid for infrastructure search (e.g. hostedOn).");
                }
            }
        }

        // if all outgoing relationships are not of the searched types, the NodeTemplate is the
        // bottom one
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
    protected static boolean performInstanceDataMatching(final QName infrastructureNodeType,
                                                         final Map<String, String> infrastructureProperties) {

        if (infrastructureNodeType != null) {

            // retrieve all instances with matching type from instance data
            final Collection<NodeTemplateInstance> typeMatchingInstances =
                nodeTemplateInstanceRepository.findByTemplateType(infrastructureNodeType);

            for (final NodeTemplateInstance typeMatchingInstance : typeMatchingInstances) {
                DeploymentDistributionDecisionMaker.LOG.debug("Found NodeTemplateInstance with matching type. ID: {}",
                                                              typeMatchingInstance.getId());

                // avoid matching with currently starting or already deleted NodeTemplateInstances
                if (!typeMatchingInstance.getServiceTemplateInstance().getState()
                                         .equals(ServiceTemplateInstanceState.DELETED)) {
                    if (typeMatchingInstance.getState().equals(NodeTemplateInstanceState.STARTED)) {

                        // get the build plan instance that belongs to this NodeTemplateInstance
                        PlanInstance buildPlan = null;
                        for (final PlanInstance plan : typeMatchingInstance.getServiceTemplateInstance()
                                                                           .getPlanInstances()) {
                            if (plan.getType().equals(PlanType.BUILD)) {
                                buildPlan = plan;
                                break;
                            }
                        }

                        // only match with NodeTemplateInstances for which the build process is
                        // already finished (avoids self matching)
                        if (buildPlan != null && buildPlan.getState().equals(PlanInstanceState.FINISHED)) {

                            // Only match with NodeTemplateInstances which are managed by this
                            // Container. It is managed by this Container if the corresponding
                            // attribute is set accordingly. However, it is also managed by this
                            // Container, if the corresponding attribute is null and the build plan
                            // is finished. This means that there was no IA invocation in the build
                            // plan and therefore also no remote deployment which means it is
                            // managed locally. But this is a bit hacky at the moment...
                            if (typeMatchingInstance.getManagingContainer() == null
                                || typeMatchingInstance.getManagingContainer()
                                                       .equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {

                                DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplateInstance is valid candidate for property matching...");

                                final Map<String, String> matchingProperties =
                                    typeMatchingInstance.getPropertiesAsMap();

                                // match the property maps
                                if (matchingProperties.entrySet().equals(infrastructureProperties.entrySet())) {

                                    DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplateInstance matches with given type and properties!");
                                    return true;
                                } else {
                                    DeploymentDistributionDecisionMaker.LOG.debug("Properties have different entry sets.");
                                }
                            } else {
                                DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplateInstance is managed by remote Container: {}",
                                                                              typeMatchingInstance.getManagingContainer());
                            }
                        } else {
                            DeploymentDistributionDecisionMaker.LOG.debug("No build plan instance found or not yet finished!");
                        }
                    } else {
                        DeploymentDistributionDecisionMaker.LOG.debug("NodeTemplateInstance state is not appropriate for matching: {}",
                                                                      typeMatchingInstance.getState());
                    }
                } else {
                    DeploymentDistributionDecisionMaker.LOG.debug("Corresponding ServiceTemplateInstance is already deleted!");
                }
            }
        }

        // no matching found
        return false;
    }

    /**
     * Match the given NodeType and properties against instance data from remote OpenTOSCA
     * Containers. The matching is successful if a NodeTemplateInstance with the same NodeType and
     * the same values for the properties is found in their instance data. The method sends a
     * request via MQTT to all subscribed OpenTOSCA Containers. Afterwards, it waits for a reply
     * which contains the host name of the OpenTOSCA Container that found matching instance data. If
     * it receives a reply in time, it returns the host name. Otherwise, it returns null.
     *
     * @param infrastructureNodeType the NodeType of the NodeTemplate which has to be matched
     * @param infrastructureProperties the set of properties of the NodeTemplate which has to be
     *        matched
     * @return the host name of the OpenTOSCA Container which found a matching NodeTemplateInstance
     *         if one is found, <tt>null</tt> otherwise.
     */
    private static String performRemoteInstanceDataMatching(final QName infrastructureNodeType,
                                                            final Map<String, String> infrastructureProperties) {

        DeploymentDistributionDecisionMaker.LOG.debug("Creating collaboration message for remote instance data matching...");

        // transform infrastructureProperties for the message body
        final KeyValueMap properties = new KeyValueMap();
        final List<KeyValueType> propertyList = properties.getKeyValuePair();

        for (final Entry<String, String> entry : infrastructureProperties.entrySet()) {
            propertyList.add(new KeyValueType(entry.getKey(), entry.getValue()));
        }

        // create collaboration message
        final BodyType content = new BodyType(infrastructureNodeType, properties);
        final CollaborationMessage collaborationMessage = new CollaborationMessage(new KeyValueMap(), content);

        // create an unique correlation ID for the request
        final String correlationID = UUID.randomUUID().toString();

        DeploymentDistributionDecisionMaker.LOG.debug("Publishing instance matching request to MQTT broker at {} with topic {} and correlation ID {}",
                                                      Constants.LOCAL_MQTT_BROKER, Constants.REQUEST_TOPIC,
                                                      correlationID);

        // create exchange containing the collaboration message and the needed headers
        final Exchange request =
            new ExchangeBuilder(Activator.camelContext).withBody(collaborationMessage)
                                                       .withHeader(MBHeader.MQTTBROKERHOSTNAME_STRING.toString(),
                                                                   Constants.LOCAL_MQTT_BROKER)
                                                       .withHeader(MBHeader.MQTTTOPIC_STRING.toString(),
                                                                   Constants.REQUEST_TOPIC)
                                                       .withHeader(MBHeader.REPLYTOTOPIC_STRING.toString(),
                                                                   Constants.RESPONSE_TOPIC)
                                                       .withHeader(MBHeader.CORRELATIONID_STRING.toString(),
                                                                   correlationID)
                                                       .withHeader(MBHeader.REMOTEOPERATION_STRING.toString(),
                                                                   RemoteOperations.invokeInstanceDataMatching)
                                                       .build();

        // publish the exchange over the camel route
        final Thread thread = new Thread(() -> {

            // By using an extra thread and waiting some time before sending the request, the
            // consumer can be started in time to avoid loosing replies.
            try {
                Thread.sleep(300);
            }
            catch (final InterruptedException e) {
            }

            Activator.producer.send("direct:SendMQTT", request);
        });
        thread.start();

        final String callbackEndpoint = "direct:Callback-" + correlationID;
        DeploymentDistributionDecisionMaker.LOG.debug("Waiting for response at endpoint: {}", callbackEndpoint);

        // wait for a response and consume it or timeout after 10s
        final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();
        final Exchange response = consumer.receive(callbackEndpoint, 10000);

        if (response != null) {
            DeploymentDistributionDecisionMaker.LOG.debug("Received a response in time.");

            // read the deployment location from the reply
            return response.getIn().getHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), String.class);
        } else {
            DeploymentDistributionDecisionMaker.LOG.debug("No response received within the timeout interval.");
            return null;
        }
    }
}
