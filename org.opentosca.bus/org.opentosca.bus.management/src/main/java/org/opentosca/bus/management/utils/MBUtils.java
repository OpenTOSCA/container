package org.opentosca.bus.management.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
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

//FIXME this piece of ... needs to do actual transaction management, because Hibernate actually follows the JPA spec!
@NonNullByDefault
public class MBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MBUtils.class);

    // repository to access ServiceTemplateInstance data
    private static final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository = new ServiceTemplateInstanceRepository();
    private static final NodeTemplateInstanceRepository nodeTemplateInstanceRepository = new NodeTemplateInstanceRepository();

    /**
     * Finds the operating system node template, optionally requiring that it has a NodeInstance associated with a given
     * serviceTemplateInstanceId.
     *
     * @return The OperatingSystem NodeTemplate.
     */
    @Nullable
    public static TNodeTemplate getOperatingSystemNodeTemplate(final Csar csar,
                                                               final TServiceTemplate serviceTemplate,
                                                               final TNodeTemplate nodeTemplate,
                                                               boolean mustHaveNodeInstance,
                                                               Long serviceTemplateInstanceId) {

        // Need to do exhaustive checking of all osNodeTypes for NodeInstance criteria
        final Queue<TNodeTemplate> osNodeTemplates = new LinkedList<>();
        final Queue<TNodeTemplate> nodeTemplateGraph = new LinkedList<>();
        final Set<TNodeTemplate> traversedTemplates = new HashSet<>();
        nodeTemplateGraph.add(nodeTemplate);
        while (!nodeTemplateGraph.isEmpty()) {
            final TNodeTemplate current = nodeTemplateGraph.poll();
            if (!traversedTemplates.add(current)) {
                // skip templates we already traversed
                continue;
            }
            final TNodeType currentNodeType = ToscaEngine.resolveNodeType(csar, current);
            if (isOperatingSystemNodeType(currentNodeType)) {
                // just return the first result if we don't need to check for a node instance
                if (!mustHaveNodeInstance) {
                    return current;
                }
                osNodeTemplates.add(current);
                continue;
            }
            // nodeType was not an OS node type, therefore traverse the Graph "downwards"
            ToscaEngine.getRelatedNodeTemplates(serviceTemplate, current,
                Types.hostedOnRelationType, Types.deployedOnRelationType, Types.dependsOnRelationType)
                // avoid cycles in the graph
                .filter(t -> !traversedTemplates.contains(t))
                .forEach(nodeTemplateGraph::add);
        }
        // return the first result that has an instance
        for (TNodeTemplate osTemplate : osNodeTemplates) {
            if (getNodeTemplateInstance(serviceTemplateInstanceId, osTemplate) != null) {
                return osTemplate;
            }
        }
        return null;
    }

    @Nullable // contaminated by MBUtils#getNodeTemplateInstances
    public static NodeTemplateInstance getAbstractOSReplacementInstance(NodeTemplateInstance nodeTemplateInstance) {
        final Map<String, String> propMap = nodeTemplateInstance.getPropertiesAsMap();
        if (propMap == null) {
            // return original node template instance
            return nodeTemplateInstance;
        }
        if (!propMap.containsKey(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEREF)) {
            // no instance reference stored in the node template instance, so no replacement available
            return nodeTemplateInstance;
        }
        LOG.debug("Found instanceRef Property with value: {}", propMap.get(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEREF));
        /*
         * values are sent from fontend delimited by "," in the following format:
         * service-template-instance-id,node-template-id
         */
        final String[] values = propMap.get(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEREF).split(",");
        if (values.length != 2) {
            LOG.warn("input format for instanceref was incorrect. Received value {}", values);
            // to avoid messing this up
            return nodeTemplateInstance;
        }
        final Long serviceTemplateInstanceId = Long.parseLong(values[0]);
        final String nodeTemplateId = values[1];

        // return the actual replacement
        return MBUtils.getNodeTemplateInstance(serviceTemplateInstanceId, nodeTemplateId);
    }

    /**
     * Checks if the specified NodeType is the OperatingSystem NodeType.
     *
     * @return true if the specified NodeType is one of the OperatingSystem NodeTypes. Otherwise false.
     */
    private static boolean isOperatingSystemNodeType(final TNodeType nodeType) {
        TInterfaces exposedInterfaces = nodeType.getInterfaces();
        boolean isOs = exposedInterfaces.getInterface().stream()
            .filter(tInterface -> Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM.equals(tInterface.getName()))
            .anyMatch(os -> doesInterfaceContainOperation(os, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
                && doesInterfaceContainOperation(os, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE));
        boolean isDocker = exposedInterfaces.getInterface().stream()
            .filter(tInterface -> Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER.equals(tInterface.getName()))
            .anyMatch(os -> doesInterfaceContainOperation(os, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
                && doesInterfaceContainOperation(os, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE));
        return isOs || isDocker;
    }

    private static boolean doesInterfaceContainOperation(TNodeType nodeType, String interfaceName, String operationName) {
        try {
            return doesInterfaceContainOperation(ToscaEngine.resolveInterface(nodeType, interfaceName), operationName);
        } catch (NotFoundException e) {
            return false;
        }
    }

    private static boolean doesInterfaceContainOperation(TInterface tInterface, String operationName) {
        return tInterface.getOperation().stream().anyMatch(op -> operationName.equals(op.getName()));
    }

    /**
     * Returns the OS interface of the given OS Node Type
     *
     * @param nodeType The Node Type to check
     * @return a String containing the name of the OS interface, or if the given Node Type is not an OS Node Type null
     */
    @Nullable
    public static String getInterfaceForOperatingSystemNodeType(final TNodeType nodeType) {
        if (doesInterfaceContainOperation(nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && doesInterfaceContainOperation(nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM;
        } else if (doesInterfaceContainOperation(nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && doesInterfaceContainOperation(nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER;
        }
        return null;
    }

    /**
     * Returns the name of the OperatingSystem ImplementationArtifact.
     *
     * @return name of the OperatingSystem ImplementationArtifact.
     */
    @Nullable
    public static TImplementationArtifact getOperatingSystemIA(final Csar csar, final TServiceTemplate serviceTemplate, final TNodeType osNodeType) {
        LOG.debug("Searching the OperatingSystem-IA of OS-NodeType: {}, ServiceTemplate: {} & CSAR: {} ...", osNodeType, serviceTemplate, csar);

        for (final TNodeTypeImplementation implementation : ToscaEngine.getNodeTypeImplementations(csar, osNodeType)) {
            for (final TImplementationArtifact artifact : ToscaEngine.implementationArtifacts(implementation)) {
                @Nullable final String interfaceName = artifact.getInterfaceName();
                if (interfaceName != null &&
                    (interfaceName.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
                        || interfaceName.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER))) {
                    return artifact;
                }
            }
        }
        return null;
    }

    /**
     * Traverses the topology and searches for the specified property. If found, the value from the instance data is
     * returned.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance where the search should be started in downwards direction
     * @param property             the name of the property that is searched
     * @return instance data value of searched property if found, <tt>null</tt> otherwise.
     */
    @Nullable
    public static String searchProperty(NodeTemplateInstance nodeTemplateInstance, final String property) {

        LOG.debug("Searching the Property: {} in or under the NodeTemplateInstance ID: {} ...", property, nodeTemplateInstance.getId());

        // check if property is already defined at this NodeTemplateInstance
        @Nullable
        String propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);

        // search until property is found or no new NodeTemplateInstance is found
        boolean moreNodeTemplateInstances = true;
        while (propertyValue == null && moreNodeTemplateInstances) {
            LOG.debug("Property not found at NodeTemplate: {}", nodeTemplateInstance.getTemplateId());
            moreNodeTemplateInstances = false;

            // perform search in downwards direction in the topology
            final Collection<RelationshipTemplateInstance> outgoingRelations = nodeTemplateInstance.getOutgoingRelations();

            for (final RelationshipTemplateInstance relation : outgoingRelations) {
                final QName relationType = relation.getTemplateType();
                LOG.debug("Found outgoing relation of Type: {}", relationType);

                // only follow relations of kind hostedOn, deployedOn and dependsOn
                if (!relationType.equals(Types.hostedOnRelationType)
                    && !relationType.equals(Types.deployedOnRelationType)
                    && !relationType.equals(Types.dependsOnRelationType)) {
                    LOG.debug("RelationshipType is not valid for property search (e.g. hostedOn).");
                    continue;
                }

                nodeTemplateInstance = relation.getTarget();
                moreNodeTemplateInstances = true;
                LOG.debug("Found new NodeTemplate: {}. Continue property search.", nodeTemplateInstance.getTemplateId());
                // check if new NodeTemplateInstance contains property
                propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);
                break;
            }
        }

        if (propertyValue != null) {
            LOG.debug("Searched property: {} with value: {} found in NodeTemplate: {}.", property, propertyValue, nodeTemplateInstance.getTemplateId());
        } else {
            LOG.debug("Searched property: {} not found!", property);
        }

        return propertyValue;
    }

    /**
     * Returns the value of a certain property of a certain NodeTemplateInstance.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance
     * @param property             the name of the property
     * @return the value of the property if found, <tt>null</tt> otherwise.
     */
    @Nullable
    public static String getInstanceDataPropertyValue(final NodeTemplateInstance nodeTemplateInstance,
                                                      final String property) {
        final Map<String, String> propertiesMap = nodeTemplateInstance.getPropertiesAsMap();

        if (propertiesMap != null) {
            return propertiesMap.get(property);
        } else {
            return null;
        }
    }

    @Nullable
    public static NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceId, final TNodeTemplate nodeTemplate) {
        return getNodeTemplateInstance(serviceTemplateInstanceId, nodeTemplate.getId());
    }

    /**
     * Retrieve the NodeTemplateInstance which is contained in a certain ServiceTemplateInstance and has a certain
     * template ID.
     *
     * @param serviceTemplateInstanceID this ID identifies the ServiceTemplateInstance
     * @param nodeTemplateID            the template ID to identify the correct instance
     * @return the found NodeTemplateInstance or <tt>null</tt> if no instance was found that matches the parameters
     */
    @Nullable
    public static NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceID,
                                                               final String nodeTemplateID) {
        LOG.debug("Trying to retrieve NodeTemplateInstance for ServiceTemplateInstance ID {} and NodeTemplate ID {} ...",
            serviceTemplateInstanceID, nodeTemplateID);

        final Optional<ServiceTemplateInstance> serviceTemplateInstance = serviceTemplateInstanceRepository.find(serviceTemplateInstanceID);

        if (serviceTemplateInstance.isPresent()) {
            return nodeTemplateInstanceRepository.find(serviceTemplateInstance.get(), nodeTemplateID)
                .stream().filter(nti -> nti.getState() == NodeTemplateInstanceState.CREATED || nti.getState() == NodeTemplateInstanceState.STARTED)
                .findFirst().orElse(null);
        } else {
            LOG.warn("Unable to find ServiceTemplateInstance!");
            return null;
        }
    }

    /**
     * Get the next NodeTemplateInstance connected with a HostedOn/DeployedOn/... Relation.
     *
     * @param currentNode the current NodeTemplateInstance
     * @return an Optional containing the next NodeTemplateInstance if one is connected with one of the supported
     * Relation Types or an empty Optional otherwise
     */
    public static Optional<NodeTemplateInstance> getNextNodeTemplateInstance(final NodeTemplateInstance currentNode) {

        Optional<NodeTemplateInstance> nextNode = getConnectedNodeTemplateInstance(currentNode, Types.hostedOnRelationType);

        if (!nextNode.isPresent()) {
            nextNode = getConnectedNodeTemplateInstance(currentNode, Types.deployedOnRelationType);
        }

        // quick hack to ensure instantiated properties - this should be done somehow in the RelationshipTemplates
        if (nextNode.isPresent()) {
            return nodeTemplateInstanceRepository.find(nextNode.get().getId());
        }

        return Optional.empty();
    }

    /**
     * Get the next NodeTemplateInstance connected with a Relation of the given type.
     *
     * @param currentNode      the current NodeTemplateInstance
     * @param relationshipType the type of the Relation as QName
     * @return an Optional containing the next NodeTemplateInstance if one is connected with a Relation of the specified
     * type or an empty Optional otherwise
     */
    private static Optional<NodeTemplateInstance> getConnectedNodeTemplateInstance(final NodeTemplateInstance currentNode,
                                                                                   final QName relationshipType) {
        return currentNode.getOutgoingRelations().stream()
            .filter(relation -> relation.getTemplateType().equals(relationshipType)).findFirst()
            .map(RelationshipTemplateInstance::getTarget);
    }

    /**
     * Retrieve the RelationshipTemplateInstance which is contained in a certain ServiceTemplateInstance and has a
     * certain template ID.
     *
     * @param serviceTemplateInstanceID this ID identifies the ServiceTemplateInstance
     * @param relationshipTemplateName  the template ID to identify the correct instance
     * @return the found RelationshipTemplateInstance or <tt>null</tt> if no instance was found that matches the
     * parameters
     */
    @Nullable
    public static RelationshipTemplateInstance getRelationshipTemplateInstance(final Long serviceTemplateInstanceID,
                                                                               final String relationshipTemplateName) {
        LOG.debug("Trying to retrieve RelationshipTemplateInstance for ServiceTemplateInstance ID {} and RelationshipTemplate ID {} ...",
            serviceTemplateInstanceID, relationshipTemplateName);

        final Optional<ServiceTemplateInstance> serviceTemplateInstance =
            serviceTemplateInstanceRepository.find(serviceTemplateInstanceID);

        if (serviceTemplateInstance.isPresent()) {
            return serviceTemplateInstance.get().getNodeTemplateInstances().stream()
                .flatMap(nodeInstance -> nodeInstance.getOutgoingRelations().stream())
                .filter(relationshipInstance -> relationshipInstance.getTemplateId().equals(relationshipTemplateName))
                .findFirst().orElse(null);
        } else {
            LOG.warn("Unable to find ServiceTemplateInstance!");
            return null;
        }
    }

    public static QName findPlanByOperation(Csar csar, String ifaceName, String opName) {
        TExportedOperation op = csar.entryServiceTemplate().getBoundaryDefinitions().getInterfaces().getInterface().stream().filter(iface -> iface.getName().equals(ifaceName)).collect(Collectors.toList())
            .stream().flatMap(iface -> iface.getOperation().stream()).filter(ope -> ope.getName().equals(opName)).findFirst().orElse(null);
        if (op != null) {
            return new QName(csar.entryServiceTemplate().getTargetNamespace(), ((TPlan) op.getPlan().getPlanRef()).getId());
        }

        return null;
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



        /*
         Element envelope =  (Element)propertiesDocument.getFirstChild();
        Element body = (Element) envelope.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/","Body").item(0);


        NodeList childNodes = body.getChildNodes();


        Collection<Node> childNodesCollection = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++){
            childNodesCollection.add(childNodes.item(i));
        }

        Iterator<Node> iter = childNodesCollection.iterator();
         */

        for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

            final String name = node.getLocalName();
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

    /**
     * Transfers the paramsMap into a Document.
     *
     * @return the created Document.
     */
    public static Document mapToDoc(final String rootElementNamespaceURI, final String rootElementName,
                                    final HashMap<String, String> paramsMap) {
        LOG.debug("Mapping to doc for element {} and namespace {}.", rootElementName, rootElementNamespaceURI);
        Document document;

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            LOG.error("Some error occured.");
            e.printStackTrace();
        }

        document = documentBuilder.newDocument();

        final Element rootElement = document.createElementNS(rootElementNamespaceURI, rootElementName);
        document.appendChild(rootElement);

        Element mapElement;
        for (final Entry<String, String> entry : paramsMap.entrySet()) {
            mapElement = document.createElement(entry.getKey());
            mapElement.setTextContent(entry.getValue());
            rootElement.appendChild(mapElement);
        }

        return document;
    }
}
