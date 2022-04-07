package org.opentosca.bus.management.utils;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.apache.camel.Exchange;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.bus.management.Constants;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Properties;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

@NonNullByDefault
@Service
public class MBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MBUtils.class);

    // repository to access ServiceTemplateInstance data
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;
    private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository;

    public MBUtils(NodeTemplateInstanceRepository nodeTemplateInstanceRepository, RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository) {
        this.nodeTemplateInstanceRepository = nodeTemplateInstanceRepository;
        this.relationshipTemplateInstanceRepository = relationshipTemplateInstanceRepository;
    }

    /**
     * Checks if the specified NodeType is the OperatingSystem NodeType.
     *
     * @return true if the specified NodeType is one of the OperatingSystem NodeTypes. Otherwise, false.
     */
    private static boolean isOperatingSystemNodeType(Csar csar, final TNodeType nodeType) {
        if (nodeType.getInterfaces() == null) {
            return false;
        }

        TInterface interfaceOfNodeType = ModelUtils.getInterfaceOfNodeType(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
        boolean isOs = interfaceOfNodeType != null
            && doesInterfaceContainOperation(interfaceOfNodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && doesInterfaceContainOperation(interfaceOfNodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);

        interfaceOfNodeType = ModelUtils.getInterfaceOfNodeType(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER);
        boolean isDocker = interfaceOfNodeType != null
            && doesInterfaceContainOperation(interfaceOfNodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && doesInterfaceContainOperation(interfaceOfNodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE);

        return isOs || isDocker;
    }

    private static boolean doesInterfaceContainOperation(Csar csar, TNodeType nodeType, String interfaceName, String operationName) {
        TInterface tInterface = ToscaEngine.resolveInterface(csar, nodeType, interfaceName);
        return tInterface != null && doesInterfaceContainOperation(tInterface, operationName);
    }

    private static boolean doesInterfaceContainOperation(TInterface tInterface, String operationName) {
        return tInterface.getOperations().stream().anyMatch(op -> operationName.equalsIgnoreCase(op.getName()));
    }

    /**
     * Returns the OS interface of the given OS Node Type
     *
     * @param nodeType The Node Type to check
     * @return a String containing the name of the OS interface, or if the given Node Type is not an OS Node Type null
     */
    @Nullable
    public static String getInterfaceForOperatingSystemNodeType(Csar csar, final TNodeType nodeType) {
        if (doesInterfaceContainOperation(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && doesInterfaceContainOperation(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM;
        } else if (doesInterfaceContainOperation(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && doesInterfaceContainOperation(csar, nodeType, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
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
                if (propertyValue != null) {
                    break;
                }
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

    @Nullable
    public static QName findPlanByOperation(Csar csar, String interfaceName, String opName) {
        TExportedOperation op = csar.entryServiceTemplate().getBoundaryDefinitions().getInterfaces().stream()
            .filter(anInterface -> anInterface.getName().equals(interfaceName))
            .flatMap(anInterface -> anInterface.getOperation().stream())
            .filter(ope -> ope.getName().equals(opName))
            .findFirst()
            .orElse(null);
        if (op != null) {
            return new QName(csar.entryServiceTemplate().getTargetNamespace(), ((TPlan) op.getPlan().getPlanRef()).getId());
        }

        return null;
    }

    /**
     * Transfers the properties document to a map.
     *
     * @param propertiesDocument to be transferred to a map.
     * @return transferred map.
     */
    public static HashMap<String, String> docToMap(final Document propertiesDocument, final boolean allowEmptyEntries) {
        final HashMap<String, String> responseMap = new HashMap<>();

        final DocumentTraversal traversal = (DocumentTraversal) propertiesDocument;
        final NodeIterator iterator =
            traversal.createNodeIterator(propertiesDocument.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);

        for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

            final String name = node.getLocalName();
            final StringBuilder content = createStringFromNode(node);

            if (allowEmptyEntries) {
                responseMap.put(name, content.toString());
            } else {
                if (!content.toString().trim().isEmpty()) {
                    responseMap.put(name, content.toString());
                }
            }
        }

        return responseMap;
    }

    public static StringBuilder createStringFromNode(Node node) {
        final StringBuilder content = new StringBuilder();
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                content.append(child.getTextContent());
            }
        }
        return content;
    }

    /**
     * Transfers the paramsMap into a Document.
     *
     * @return the created Document.
     */
    @Nullable
    public static Document mapToDoc(final String rootElementNamespaceURI, final String rootElementName,
                                    final HashMap<String, String> paramsMap) {
        LOG.debug("Mapping to doc for element {} and namespace {}.", rootElementName, rootElementNamespaceURI);
        Document document;

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
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
        } catch (final ParserConfigurationException e) {
            LOG.error("Some error occurred.", e);
        }

        return null;
    }

    /**
     * Transform the given XML Document to a String
     *
     * @param document the document to transform
     * @return the transformed document as String
     */
    public static String docToString(Document document) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            LOG.error("Failed to transform document to string:", e);
            return null;
        }
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
    public RelationshipTemplateInstance getRelationshipTemplateInstance(final Long serviceTemplateInstanceID,
                                                                        final String relationshipTemplateName) {
        LOG.debug("Trying to retrieve RelationshipTemplateInstance for ServiceTemplateInstance ID {} and RelationshipTemplate ID {} ...",
            serviceTemplateInstanceID, relationshipTemplateName);
        return this.relationshipTemplateInstanceRepository.findByTemplateId(relationshipTemplateName).stream().filter(rel -> rel.getServiceTemplateInstance().getId().equals(serviceTemplateInstanceID)).findFirst().orElse(null);
    }

    @Nullable // contaminated by MBUtils#getNodeTemplateInstances
    public NodeTemplateInstance getAbstractOSReplacementInstance(NodeTemplateInstance nodeTemplateInstance) {
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
         * values are sent from frontend delimited by "," in the following format:
         * service-template-instance-id,node-template-id
         */
        final String[] values = propMap.get(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEREF).split(",");
        if (values.length != 2) {
            LOG.warn("input format for instance reference was incorrect. Received value {}", String.join(", ", values));
            // to avoid messing this up
            return nodeTemplateInstance;
        }
        final Long serviceTemplateInstanceId = Long.parseLong(values[0]);
        final String nodeTemplateId = values[1];

        // return the actual replacement
        return getNodeTemplateInstance(serviceTemplateInstanceId, nodeTemplateId);
    }

    @Nullable
    public NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceId, final TNodeTemplate nodeTemplate) {
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
    public NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceID,
                                                        final String nodeTemplateID) {
        LOG.debug("Trying to retrieve NodeTemplateInstance for ServiceTemplateInstance ID {} and NodeTemplate ID {} ...",
            serviceTemplateInstanceID, nodeTemplateID);
        return this.nodeTemplateInstanceRepository.findWithPropertiesAndOutgoingByTemplateId(nodeTemplateID).stream().filter(node -> node.getServiceTemplateInstance().getId().equals(serviceTemplateInstanceID)).findFirst().orElse(null);
    }

    /**
     * Get the next NodeTemplateInstance connected with a HostedOn/DeployedOn/... Relation.
     *
     * @param currentNode the current NodeTemplateInstance
     * @return an Optional containing the next NodeTemplateInstance if one is connected with one of the supported
     * Relation Types or an empty Optional otherwise
     */
    public Optional<NodeTemplateInstance> getNextNodeTemplateInstance(final NodeTemplateInstance currentNode) {

        Optional<NodeTemplateInstance> nextNode = getConnectedNodeTemplateInstance(currentNode, Types.hostedOnRelationType);

        if (nextNode.isEmpty()) {
            nextNode = getConnectedNodeTemplateInstance(currentNode, Types.deployedOnRelationType);
        }

        // quick hack to ensure instantiated properties - this should be done somehow in the RelationshipTemplates
        if (nextNode.isPresent()) {
            return nodeTemplateInstanceRepository.findWithPropertiesAndOutgoingById(nextNode.get().getId());
        }

        return Optional.empty();
    }

    /**
     * Finds the operating system node template, optionally requiring that it has a NodeInstance associated with a given
     * serviceTemplateInstanceId.
     *
     * @return The OperatingSystem NodeTemplate.
     */
    @Nullable
    public TNodeTemplate getOperatingSystemNodeTemplate(final Csar csar,
                                                        final TServiceTemplate serviceTemplate,
                                                        final TNodeTemplate nodeTemplate,
                                                        boolean mustHaveNodeInstance,
                                                        Long serviceTemplateInstanceId) throws NotFoundException {

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
            if (isOperatingSystemNodeType(csar, currentNodeType)) {
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

    public List<String> findAbsoluteArtifactReferences(Csar csar, TArtifactTemplate artifactTemplate) {
        final List<TArtifactReference> artifacts =
            Optional.ofNullable(artifactTemplate.getArtifactReferences())
                .orElse(Collections.emptyList());

        // convert relative references to absolute references to enable access to the IA
        // files from other OpenTOSCA Container nodes
        final List<String> artifactReferences = new ArrayList<>();
        for (final TArtifactReference artifact : artifacts) {
            // XML validated to be anyUri, therefore must be parsable as URI
            final URI reference = URI.create(artifact.getReference().trim());
            if (reference.getScheme() != null) {
                continue;
            }
            // artifact is exposed via the content endpoint
            final String absoluteArtifactReference =
                Settings.OPENTOSCA_CONTAINER_CONTENT_API_ARTIFACTREFERENCE.replace("{csarid}", csar.id().csarName())
                    // reference here is relative to CSAR basedirectory, with
                    // spaces being URLEncoded
                    .replace("{artifactreference}",
                        artifact.getReference().trim().replaceAll(" ",
                            "%20"));

            artifactReferences.add(absoluteArtifactReference);
        }
        return artifactReferences;
    }

    /**
     * Replaces placeholder with a matching instance data value. Placeholder is defined like
     * "/PLACEHOLDER_VMIP_IP_PLACEHOLDER/"
     *
     * @param endpoint             the endpoint URI containing the placeholder
     * @param nodeTemplateInstance the NodeTemplateInstance where the endpoint belongs to
     * @return the endpoint URI with replaced placeholder if matching instance data was found, the unchanged endpoint
     * URI otherwise
     */
    public URI replacePlaceholderWithInstanceData(URI endpoint, final NodeTemplateInstance nodeTemplateInstance) {

        if (nodeTemplateInstance == null) {
            return endpoint;
        }
        final String placeholder =
            endpoint.toString().substring(endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_START),
                endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_END)
                    + Constants.PLACEHOLDER_END.length());

        final String[] placeholderProperties =
            placeholder.replace(Constants.PLACEHOLDER_START, "").replace(Constants.PLACEHOLDER_END, "").split("_");

        for (final String placeholderProperty : placeholderProperties) {
            final String propertyValue = searchProperty(nodeTemplateInstance, placeholderProperty);
            if (propertyValue == null) {
                continue;
            }
            try {
                endpoint = new URI(endpoint.toString().replace(placeholder, propertyValue));
                break;
            } catch (final URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return endpoint;
    }

    /**
     * Add the specific content of the ImplementationArtifact to the Exchange headers if defined.
     * @param exchange
     * @param implementationArtifact
     */
    public Exchange addSpecificContent(final Exchange exchange, final TImplementationArtifact implementationArtifact) {
        final Object any = implementationArtifact.getAny();
        final Document specificContent = any instanceof Element ? XMLHelper.fromRootNode((Element) any) : null;
        if (specificContent != null) {
            exchange.getIn().setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
        }
        return exchange;
    }

    public boolean iaProvidesRequestedOperation(Csar csar, TImplementationArtifact ia, TEntityType type,
                                                String neededInterface, String neededOperation) {
        final String providedOperation = ia.getOperationName();
        final String providedInterface = ia.getInterfaceName();

        LOG.debug("Needed interface: {}. Provided interface: {}", neededInterface, providedInterface);
        LOG.debug("Needed operation: {}. Provided operation: {}", neededOperation, providedOperation);

        if (providedInterface == null && providedOperation == null) {
            // IA implements all operations of all interfaces defined in the node type
            LOG.debug("Correct IA found. IA: {} implements all operations of all interfaces defined in NodeType.",
                ia.getName());
            return true;
        }

        // IA implements all operations of one interface defined in NodeType
        if (providedInterface != null && providedOperation == null && providedInterface.equals(neededInterface)) {
            LOG.debug("Correct IA found. IA: {} implements all operations of one interface defined in NodeType.",
                ia.getName());
            return true;
        }

        // IA implements one operation of an interface defined in NodeType
        if (providedInterface != null && providedOperation != null && providedInterface.equals(neededInterface)
            && providedOperation.equals(neededOperation)) {
            LOG.debug("Correct IA found. IA: {} implements one operation of an interface defined in NodeType.",
                ia.getName());
            return true;
        }

        // In this case - if there is no interface specified - the operation
        // should be unique within the NodeType
        if (neededInterface == null && neededOperation != null && providedInterface != null
            && providedOperation == null) {
            return ToscaEngine.isOperationUniqueInType(csar, type, providedInterface, neededOperation);
        }

        LOG.debug("ImplementationArtifact {} does not provide needed interface/operation", ia.getName());
        return false;
    }

    public Document createInputDocFromInputMap(HashMap<String,String> inputMap, HashMap<String, String> params) {
        final Document inputDoc =
            mapToDoc(Constants.BUS_WSDL_NAMESPACE, Constants.RECEIVE_NOTIFY_PARTNER_OPERATION, inputMap);

        final Element root = inputDoc.getDocumentElement();
        final Element paramsWrapper = inputDoc.createElement(Constants.PARAMS_PARAM);
        root.appendChild(paramsWrapper);
        for (final Entry<String, String> entry : params.entrySet()) {
            final Element paramElement = inputDoc.createElement("Param");
            paramsWrapper.appendChild(paramElement);

            final Element keyElement = inputDoc.createElement("key");
            keyElement.setTextContent(entry.getKey());
            paramElement.appendChild(keyElement);

            final Element valueElement = inputDoc.createElement("value");
            valueElement.setTextContent(entry.getValue());
            paramElement.appendChild(valueElement);
        }
        return inputDoc;
    }
}
