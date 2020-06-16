package org.opentosca.bus.management.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.container.core.tosca.convention.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class MBUtils {

    final private static Logger LOG = LoggerFactory.getLogger(MBUtils.class);

    // repository to access ServiceTemplateInstance data
    final private static ServiceTemplateInstanceRepository serviceTemplateInstanceRepository =
        new ServiceTemplateInstanceRepository();

    /**
     *
     * Returns the OperatingSystem NodeTemplate.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     *
     * @return name of the OperatingSystem NodeTemplate.
     */
    public static String getOperatingSystemNodeTemplateID(final CSARID csarID, final QName serviceTemplateID,
                                                          final String nodeTemplateID, final boolean mustHaveInstance,
                                                          final Long serviceTemplateInstanceID) {

        MBUtils.LOG.debug("Searching the OperatingSystemNode of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
                          nodeTemplateID, serviceTemplateID, csarID);

        // fetch all possible hosting nodes
        final List<String> hostingNodeTemplateIDs = new ArrayList<>();
        hostingNodeTemplateIDs.add(nodeTemplateID);
        hostingNodeTemplateIDs.addAll(ServiceHandler.toscaEngineService.getTransitiveNodeTemplateIDs(csarID,
                                                                                                     serviceTemplateID,
                                                                                                     nodeTemplateID,
                                                                                                     Types.hostedOnRelationType));
        hostingNodeTemplateIDs.addAll(ServiceHandler.toscaEngineService.getTransitiveNodeTemplateIDs(csarID,
                                                                                                     serviceTemplateID,
                                                                                                     nodeTemplateID,
                                                                                                     Types.deployedOnRelationType));
        hostingNodeTemplateIDs.addAll(ServiceHandler.toscaEngineService.getTransitiveNodeTemplateIDs(csarID,
                                                                                                     serviceTemplateID,
                                                                                                     nodeTemplateID,
                                                                                                     Types.dependsOnRelationType));

        for (final String hostingNodeTemplateId : hostingNodeTemplateIDs) {
            final QName nodeType =
                ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
                                                                            hostingNodeTemplateId);

            // check if Operating System Node and if instance is needed
            if (isOperatingSystemNodeType(csarID, nodeType)) {
                if (mustHaveInstance) {
                    if (getNodeTemplateInstance(serviceTemplateInstanceID, hostingNodeTemplateId) != null) {
                        return hostingNodeTemplateId;
                    }
                } else {
                    return hostingNodeTemplateId;
                }
            }
        }

        return null;
    }

    public static NodeTemplateInstance getAbstractOSReplacementInstance(NodeTemplateInstance nodeTemplateInstance) {
        final Map<String, String> propMap = nodeTemplateInstance.getPropertiesAsMap();
        if (Objects.nonNull(propMap)) {
            for (final String key : propMap.keySet()) {
                // if node templ instance is of OS node type + prop is instanceRef, check for selected
                // instance
                if (key.equals(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEREF)) {
                    final String value = propMap.get(key);
                    /*
                     * values are sent from frontend delimited by "," in following format:
                     * service-template-instance-id,node-template-id
                     */
                    final String[] setOfValues = value.split(",");
                    // get selected service template instance id
                    final Long serviceTemplateInstanceId = Long.parseLong(setOfValues[0]);
                    // get selected node template id
                    final String nodeTemplateId = setOfValues[1];
                    LOG.debug("Found instanceRef Property: " + key + " with value: " + propMap.get(key));

                    // replace OS node template instance by selected node template instance
                    nodeTemplateInstance = MBUtils.getNodeTemplateInstance(serviceTemplateInstanceId, nodeTemplateId);
                    return nodeTemplateInstance;
                }
            }
        }
        // return original node template instance
        return nodeTemplateInstance;
    }

    /**
     *
     * Checks if the specified NodeType is the OperatingSystem NodeType.
     *
     * @param csarID
     * @param nodeType
     * @return true if the specified NodeType is the OperatingSystem NodeType. Otherwise false.
     */
    private static boolean isOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
            return true;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
                                                                                         return true;
                                                                                     }
        return false;
    }

    /**
     * Returns the OS interface of the given OS Node Type
     *
     * @param csarID the CSAR Id where the referenced Node Type is declared
     * @param nodeType a QName of the Node Type to check
     * @return a String containing the name of the OS interface, or if the given Node Type is not an OS
     *         Node Type null
     */
    public static String getInterfaceForOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                  Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfTypeContainOperation(csarID, nodeType,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
                                                                                         return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER;
                                                                                     }
        return null;
    }

    /**
     *
     * Returns the name of the OperatingSystem ImplementationArtifact.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param osNodeTemplateID
     *
     *
     * @return name of the OperatingSystem ImplementationArtifact.
     */
    public static String getOperatingSystemIA(final CSARID csarID, final QName serviceTemplateID,
                                              final String osNodeTemplateID) {

        MBUtils.LOG.debug("Searching the OperatingSystem-IA of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
                          osNodeTemplateID, serviceTemplateID, csarID);

        final QName osNodeType =
            ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, osNodeTemplateID);

        final List<QName> osNodeTypeImpls =
            ServiceHandler.toscaEngineService.getTypeImplementationsOfType(csarID, osNodeType);

        for (final QName osNodeTypeImpl : osNodeTypeImpls) {

            MBUtils.LOG.debug("NodeTypeImpl: {} ", osNodeTypeImpl);

            final List<String> osIANames =
                ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfTypeImplementation(csarID,
                                                                                                     osNodeTypeImpl);

            for (final String osIAName : osIANames) {

                MBUtils.LOG.debug("IA: {} ", osIAName);

                final String osIAInterface =
                    ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfATypeImplementation(csarID,
                                                                                                                 osNodeTypeImpl,
                                                                                                                 osIAName);

                MBUtils.LOG.debug("Interface: {} ", osIAInterface);

                if (osIAInterface == null
                    || osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
                    || osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER)) {
                    return osIAName;
                }
            }
        }

        return null;
    }

    /**
     * Traverses the topology and searches for the specified property. If found, the value from the
     * instance data is returned.
     *
     * @param nodeTemplateInstanceID the ID of the NodeTemplateInstance where the search should be
     *        started in downwards direction
     * @param property the name of the property that is searched
     * @return instance data value of searched property if found, <tt>null</tt> otherwise.
     */
    public static String searchProperty(NodeTemplateInstance nodeTemplateInstance, final String property) {

        MBUtils.LOG.debug("Searching the Property: {} in or under the NodeTemplateInstance ID: {} ...", property,
                          nodeTemplateInstance.getId());

        // check if property is already defined at this NodeTemplateInstance
        String propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);

        // search until property is found or no new NodeTemplateInstance is found
        boolean moreNodeTemplateInstances = true;
        while (propertyValue == null && moreNodeTemplateInstances) {
            MBUtils.LOG.debug("Property not found at NodeTemplate: {}", nodeTemplateInstance.getTemplateId());
            moreNodeTemplateInstances = false;

            // perform search in downwards direction in the topology
            final Collection<RelationshipTemplateInstance> outgoingRelations =
                nodeTemplateInstance.getOutgoingRelations();

            for (final RelationshipTemplateInstance relation : outgoingRelations) {
                final QName relationType = relation.getTemplateType();
                MBUtils.LOG.debug("Found outgoing relation of Type: {}", relationType);

                // only follow relations of kind hostedOn, deployedOn and dependsOn
                if (relationType.equals(Types.hostedOnRelationType) || relationType.equals(Types.deployedOnRelationType)
                    || relationType.equals(Types.dependsOnRelationType)) {

                    nodeTemplateInstance = relation.getTarget();
                    moreNodeTemplateInstances = true;

                    MBUtils.LOG.debug("Found new NodeTemplate: {}. Continue property search.",
                                      nodeTemplateInstance.getTemplateId());

                    // check if new NodeTemplateInstance contains property
                    propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);
                    break;
                } else {
                    MBUtils.LOG.debug("RelationshipType is not valid for property search (e.g. hostedOn).");
                }
            }
        }

        if (propertyValue != null) {
            MBUtils.LOG.debug("Searched property: {} with value: {} found in NodeTemplate: {}.", property,
                              propertyValue, nodeTemplateInstance.getTemplateId());
        } else {
            MBUtils.LOG.debug("Searched property: {} not found!", property);
        }

        return propertyValue;

    }

    /**
     * Returns the value of a certain property of a certain NodeTemplateInstance.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance
     * @param property the name of the property
     * @return the value of the property if found, <tt>null</tt> otherwise.
     */
    public static String getInstanceDataPropertyValue(final NodeTemplateInstance nodeTemplateInstance,
                                                      final String property) {
        final Map<String, String> propertiesMap = nodeTemplateInstance.getPropertiesAsMap();

        if (propertiesMap != null) {
            return propertiesMap.get(property);
        } else {
            return null;
        }
    }

    /**
     * Retrieve the NodeTemplateInstance which is contained in a certain ServiceTemplateInstance and has
     * a certain template ID.
     *
     * @param serviceTemplateInstanceID this ID identifies the ServiceTemplateInstance
     * @param nodeTemplateID the template ID to identify the correct instance
     * @return the found NodeTemplateInstance or <tt>null</tt> if no instance was found that matches the
     *         parameters
     */
    public static NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceID,
                                                               final String nodeTemplateID) {
        MBUtils.LOG.debug("Trying to retrieve NodeTemplateInstance for ServiceTemplateInstance ID {} and NodeTemplate ID {} ...",
                          serviceTemplateInstanceID, nodeTemplateID);

        final Optional<ServiceTemplateInstance> serviceTemplateInstance =
            serviceTemplateInstanceRepository.find(serviceTemplateInstanceID);

        if (serviceTemplateInstance.isPresent()) {
            return serviceTemplateInstance.get().getNodeTemplateInstances().stream()
                                          .filter((nodeInstance) -> nodeInstance.getTemplateId().getLocalPart()
                                                                                .equals(nodeTemplateID))
                                          .filter((nodeInstance) -> nodeInstance.getState()
                                                                                .equals(NodeTemplateInstanceState.CREATED)
                                              || nodeInstance.getState().equals(NodeTemplateInstanceState.STARTED))
                                          .findFirst().orElse(null);
        } else {
            MBUtils.LOG.warn("Unable to find ServiceTemplateInstance!");
            return null;
        }
    }

    /**
     * Get the next NodeTemplateInstance connected with a HostedOn/DeployedOn/... Relation.
     *
     * @param currentNode the current NodeTemplateInstance
     * @return an Optional containing the next NodeTemplateInstance if one is connected with one of the
     *         supported Relation Types or an empty Optional otherwise
     */
    public static Optional<NodeTemplateInstance> getNextNodeTemplateInstance(final NodeTemplateInstance currentNode) {

        Optional<NodeTemplateInstance> nextNode =
            getConnectedNodeTemplateInstance(currentNode, Types.hostedOnRelationType);

        if (!nextNode.isPresent()) {
            nextNode = getConnectedNodeTemplateInstance(currentNode, Types.deployedOnRelationType);
        }

        return nextNode;
    }

    /**
     * Get the next NodeTemplateInstance connected with a Relation of the given type.
     *
     * @param currentNode the current NodeTemplateInstance
     * @param relationshipType the type of the Relation as QName
     * @return an Optional containing the next NodeTemplateInstance if one is connected with a Relation
     *         of the specified type or an empty Optional otherwise
     */
    private static Optional<NodeTemplateInstance> getConnectedNodeTemplateInstance(final NodeTemplateInstance currentNode,
                                                                                   final QName relationshipType) {
        return currentNode.getOutgoingRelations().stream()
                          .filter(relation -> relation.getTemplateType().equals(relationshipType)).findFirst()
                          .map(relation -> relation.getTarget());
    }

    /**
     * Retrieve the RelationshipTemplateInstance which is contained in a certain ServiceTemplateInstance
     * and has a certain template ID.
     *
     * @param serviceTemplateInstanceID this ID identifies the ServiceTemplateInstance
     * @param relationshipTemplateID the template ID to identify the correct instance
     * @return the found RelationshipTemplateInstance or <tt>null</tt> if no instance was found that
     *         matches the parameters
     */
    public static RelationshipTemplateInstance getRelationshipTemplateInstance(final Long serviceTemplateInstanceID,
                                                                               final String relationshipTemplateID) {
        MBUtils.LOG.debug("Trying to retrieve RelationshipTemplateInstance for ServiceTemplateInstance ID {} and RelationshipTemplate ID {} ...",
                          serviceTemplateInstanceID, relationshipTemplateID);

        final Optional<ServiceTemplateInstance> serviceTemplateInstance =
            serviceTemplateInstanceRepository.find(serviceTemplateInstanceID);

        if (serviceTemplateInstance.isPresent()) {
            return serviceTemplateInstance.get().getNodeTemplateInstances().stream()
                                          .flatMap(nodeInstance -> nodeInstance.getOutgoingRelations().stream())
                                          .filter(relationshipInstance -> relationshipInstance.getTemplateId()
                                                                                              .getLocalPart()
                                                                                              .equals(relationshipTemplateID))
                                          .findFirst().orElse(null);
        } else {
            MBUtils.LOG.warn("Unable to find ServiceTemplateInstance!");
            return null;
        }
    }

    /**
     * Transfers the properties document to a map.
     *
     * @param propertiesDocument to be transfered to a map.
     * @return transfered map.
     */
    public static HashMap<String, String> docToMap(final Document propertiesDocument, final boolean allowEmptyEntries) {
        final HashMap<String, String> reponseMap = new HashMap<>();

        final DocumentTraversal traversal = (DocumentTraversal) propertiesDocument;
        final NodeIterator iterator =
            traversal.createNodeIterator(propertiesDocument.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);

        for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

            final String name = ((Element) node).getLocalName();
            final StringBuilder content = new StringBuilder();
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    content.append(child.getTextContent());
                }
            }

            if (allowEmptyEntries) {
                reponseMap.put(name, content.toString());
            } else {
                if (!content.toString().trim().isEmpty()) {
                    reponseMap.put(name, content.toString());
                }
            }
        }

        return reponseMap;
    }
}
