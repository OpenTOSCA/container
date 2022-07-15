package org.opentosca.container.core.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TOperationDefinition;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TWorkflow;
import org.eclipse.winery.model.tosca.extensions.kvproperties.ParameterDefinition;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class ModelUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    public static String makeValidNCName(final String string) {
        return string.replaceAll("\\.", "_").replaceAll(" ", "_").replace("{", "_").replace("}", "_").replace("/", "_")
            .replace(":", "_");
    }

    /**
     * Converts an xml document to an xml-based property sui/table for service or node template instances
     */
    public static <T extends Property> T convertDocumentToProperty(final Document propertyDoc,
                                                                   final Class<T> type) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        if (propertyDoc == null) {
            final String msg =
                String.format("The set of parameters of an instance of type %s cannot be null", type.getName());
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        final String propertyAsString = new DocumentConverter().convertToDatabaseColumn(propertyDoc);
        final T property = type.newInstance();
        property.setName("xml");
        property.setType("xml");
        property.setValue(propertyAsString);

        return property;
    }

    public static TOperation findNodeOperation(Csar csar, String interfaceName, String operationName) {
        for (TDefinitions defs : csar.definitions()) {
            for (TNodeType nodeType : defs.getNodeTypes()) {
                if (Objects.nonNull(nodeType.getInterfaces())) {
                    for (TInterface anInterface : nodeType.getInterfaces()) {
                        if (anInterface.getName().equals(interfaceName)) {
                            for (TOperation op : anInterface.getOperations()) {
                                if (op.getName().equals(operationName)) {
                                    return op;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static TExportedOperation findServiceTemplateOperation(TDefinitions defs, String interfaceName, String operationName) {
        for (TServiceTemplate serviceTemplate : defs.getServiceTemplates()) {
            TBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();
            if (boundaryDefinitions != null && boundaryDefinitions.getInterfaces() != null) {
                for (TExportedInterface anInterface : boundaryDefinitions.getInterfaces()) {
                    if (anInterface.getName().equals(interfaceName)) {
                        for (TExportedOperation op : anInterface.getOperation()) {
                            if (op.getName().equals(operationName)) {
                                return op;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean doesNotHaveBuildPlan(TServiceTemplate serviceTemplate) {
        return !hasPlansOfType(serviceTemplate, "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan");
    }

    public static boolean hasTerminationPlan(TServiceTemplate serviceTemplate) {
        return hasPlansOfType(serviceTemplate, "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan");
    }

    public static boolean hasPlansOfType(TServiceTemplate serviceTemplate, String planType) {
        if (serviceTemplate.getPlans() != null) {
            return serviceTemplate.getPlans().stream()
                .anyMatch(x -> x.getPlanType().equals(planType));
        } else {
            return false;
        }
    }

    public static Collection<TRelationshipTemplate> getIngoingRelations(TNodeTemplate nodeTemplate, Csar csar) {
        return getAllRelationshipTemplates(csar).stream()
            .filter(x -> x.getTargetElement().getRef() instanceof TNodeTemplate
                && x.getTargetElement().getRef().getId().equals(nodeTemplate.getId())
            ).collect(Collectors.toList());
    }

    public static Collection<TRelationshipTemplate> getOutgoingRelations(TNodeTemplate nodeTemplate, Csar csar) {
        return getAllRelationshipTemplates(csar).stream()
            .filter(x -> x.getSourceElement().getRef() instanceof TNodeTemplate
                && x.getSourceElement().getRef().getId().equals(nodeTemplate.getId())
            ).collect(Collectors.toList());
    }

    public static Collection<TRelationshipTemplate> getAllRelationshipTemplates(Csar csar) {
        return csar.entryServiceTemplate() != null && csar.entryServiceTemplate().getTopologyTemplate() != null
            ? csar.entryServiceTemplate().getTopologyTemplate().getRelationshipTemplates()
            : new ArrayList<>();
    }

    /**
     * Removes duplicates from the given List
     *
     * @param nodeTemplates a List of TNodeTemplate
     */
    private static void cleanDuplicates(final Collection<TNodeTemplate> nodeTemplates) {
        /*Set<TNodeTemplate> cleanedNodes = Sets.newHashSet();
        cleanedNodes.addAll(nodeTemplates);
        nodeTemplates.clear();
        nodeTemplates.addAll(cleanedNodes);*/
        final List<TNodeTemplate> list = new ArrayList<>();
        for (final TNodeTemplate template : nodeTemplates) {
            boolean match = false;
            for (final TNodeTemplate template2 : list) {
                if (template.getId().equals(template2.getId())) {
                    match = true;
                }
            }
            if (!match) {
                list.add(template);
            }
        }
        nodeTemplates.clear();
        nodeTemplates.addAll(list);
    }

    /**
     * <p>
     * Converts the given DOM Document to a String
     * </p>
     *
     * @param doc a DOM Document
     * @return a String representation of the complete Document given
     */
    public static String getStringFromDoc(final Document doc) {
        try {
            final DOMSource domSource = new DOMSource(doc);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(domSource, result);
            writer.flush();
            return writer.toString();
        } catch (final TransformerException ex) {
            ModelUtils.LOG.error("Couldn't transform DOM Document to a String", ex);
            return null;
        }
    }

    /**
     * Removes duplicates from the given List
     *
     * @param relationshipTemplates a List of TRelationshipTemplate
     */
    private static void cleanDuplicates(final List<TRelationshipTemplate> relationshipTemplates) {
        final List<TRelationshipTemplate> list = new ArrayList<>();
        for (final TRelationshipTemplate template : relationshipTemplates) {
            boolean match = false;
            for (final TRelationshipTemplate template2 : list) {
                if (template.getId().equals(template2.getId())) {
                    match = true;
                }
            }
            if (!match) {
                list.add(template);
            }
        }
        relationshipTemplates.clear();
        relationshipTemplates.addAll(list);
    }

    public static List<QName> getArtifactTypeHierarchy(final TArtifactTemplate artifactTemplate, Csar csar) {
        final List<QName> qNames = new ArrayList<>();
        final Collection<TArtifactType> artifactTypes = fetchAllArtifactTypes(csar);
        qNames.add(artifactTemplate.getType());

        TArtifactType type = findArtifactType(artifactTemplate.getType(), artifactTypes);

        TArtifactType ref = null;
        if (Objects.nonNull(type.getDerivedFrom())) {
            ref = findArtifactType(type.getDerivedFrom().getTypeRef(), artifactTypes);
        }

        while (Objects.nonNull(ref) && Objects.nonNull(ref.getDerivedFrom())) {
            qNames.add(ref.getQName());
            ref = findArtifactType(ref.getDerivedFrom().getTypeRef(), artifactTypes);
        }

        return qNames;
    }

    public static TArtifactType findArtifactType(QName id, Collection<TArtifactType> artifactTypes) {
        return artifactTypes.stream().filter(x -> x.getQName().equals(id)).findFirst().orElse(null);
    }

    public static Collection<TArtifactType> fetchAllArtifactTypes(Csar csar) {

        Set<TArtifactType> resultSet = Sets.newHashSet();
        csar.definitions().forEach(x -> resultSet.addAll(x.getArtifactTypes()));
        return resultSet;
    }

    public static String getNamespace(TEntityTemplate.Properties properties) {
        boolean isDOM = properties.getClass().getName()
            .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl");
        boolean isWineryKV = properties.getClass().getName()
            .equals(TEntityTemplate.WineryKVProperties.class.getName());
        boolean isYaml = properties.getClass().getName().equals(TEntityTemplate.YamlProperties.class.getName());

        if (isDOM) {
            return ((Element) properties).getNamespaceURI();
        }

        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getNamespace();
        }

        if (isYaml) {
            // this is probably a super cheap way to handle this... FIXME
            return "tosca_simple_yaml_1_3";
        }

        return null;
    }

    public static String getElementName(TEntityTemplate.Properties properties) {
        boolean isDOM = properties.getClass().getName()
            .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl");
        boolean isWineryKV = properties.getClass().getName()
            .equals(TEntityTemplate.WineryKVProperties.class.getName());
        boolean isYaml = properties.getClass().getName().equals(TEntityTemplate.YamlProperties.class.getName());

        if (isDOM) {
            return ((Element) properties).getLocalName();
        }

        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getElementName();
        }

        if (isYaml) {
            return "yamlProps";
        }

        return null;
    }

    public static Map<String, String> asMap(TBoundaryDefinitions.Properties properties) {
        boolean isDOM = properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl");

        if (isDOM) {
            final PropertyParser parser = new PropertyParser();
            final Element element = (Element) properties;
            return parser.parse(element);
        }
        return new HashMap<>();
    }

    public static Map<String, String> asMap(TEntityTemplate.Properties properties) {
        boolean isDOM = properties.getClass().getName()
            .equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl");
        boolean isWineryKV = properties.getClass().getName()
            .equals(TEntityTemplate.WineryKVProperties.class.getName());
        boolean isYaml = properties.getClass().getName().equals(TEntityTemplate.YamlProperties.class.getName());

        if (isDOM) {
            final PropertyParser parser = new PropertyParser();
            final Element element = (Element) properties;
            return parser.parse(element);
        }
        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getKVProperties();
        }
        if (isYaml) {
            return asMap(((TEntityTemplate.YamlProperties) properties).getProperties());
        }
        return new HashMap<>();
    }

    public static Map<String, String> asMap(Map<String, Object> map) {
        Map<String, String> resultMap = Maps.newHashMap();
        map.forEach((s, o) -> resultMap.put(s, (String) o));
        return resultMap;
    }

    /**
     * Adds the InfrastructureEdges of the given NodeTemplate to the given List
     *
     * @param nodeTemplate        an TNodeTemplate
     * @param infrastructureEdges a List of TRelationshipTemplate to add the InfrastructureEdges to
     */
    public static void getInfrastructureEdges(final TNodeTemplate nodeTemplate,
                                              final List<TRelationshipTemplate> infrastructureEdges, Csar csar) {

        // fetch all infrastructureNodes
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, csar);

        // check all outgoing edges on those nodes, if they are infrastructure edges
        for (final TNodeTemplate infraNode : infraNodes) {
            for (final TRelationshipTemplate outgoingEdge : getOutgoingRelations(infraNode, csar)) {

                if (isInfrastructureRelationshipType(outgoingEdge.getType())) {

                    infrastructureEdges.add(outgoingEdge);
                }
            }
        }

        // check outgoing edges of given node
        for (final TRelationshipTemplate outgoingEdge : getOutgoingRelations(nodeTemplate, csar)) {
            if (isInfrastructureRelationshipType(outgoingEdge.getType())) {
                infrastructureEdges.add(outgoingEdge);
            }
        }
        ModelUtils.cleanDuplicates(infrastructureEdges);
    }

    public static TNodeTemplate getSource(TRelationshipTemplate relationshipTemplate, Csar csar) {
        if (relationshipTemplate.getSourceElement().getRef() instanceof TNodeTemplate) {
            return (TNodeTemplate) relationshipTemplate.getSourceElement().getRef();
        } else {
            return findNodeTemplate((TRequirement) relationshipTemplate.getSourceElement().getRef(), csar);
        }
    }

    public static TNodeTemplate getTarget(TRelationshipTemplate relationshipTemplate, Csar csar) {
        if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
            return (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
        } else {
            return findNodeTemplate((TCapability) relationshipTemplate.getTargetElement().getRef(), csar);
        }
    }

    public static TNodeTemplate findNodeTemplate(TCapability cap, Csar csar) {
        return csar.entryServiceTemplate().getTopologyTemplate().getNodeTemplates()
            .stream()
            .filter(x -> Objects.nonNull(x.getCapabilities()))
            .filter(x -> x.getCapabilities().stream().anyMatch(y -> y.getId().equals(cap.getId())))
            .findFirst()
            .orElse(null);
    }

    public static TNodeTemplate findNodeTemplate(TRequirement req, Csar csar) {
        return csar.entryServiceTemplate().getTopologyTemplate().getNodeTemplates()
            .stream()
            .filter(x -> Objects.nonNull(x.getRequirements()))
            .filter(x -> x.getRequirements().stream().anyMatch(y -> y.getId().equals(req.getId())))
            .findFirst()
            .orElse(null);
    }

    /**
     * Calculates all Infrastructure Nodes of all Infrastructure Paths originating from the given NodeTemplate
     *
     * @param nodeTemplate        TNodeTemplate from where the search for Infrastructure Nodes begin
     * @param infrastructureNodes a List of TNodeTemplates which represent Infrastructure Nodes of the given
     *                            NodeTemplate (including itself when applicable as an infrastructure node). Note: the
     *                            infrastructureNodes List must be empty.
     */
    public static void getInfrastructureNodes(final TNodeTemplate nodeTemplate,
                                              final Collection<TNodeTemplate> infrastructureNodes, Csar csar) {
        ModelUtils.LOG.debug("BaseType of NodeTemplate " + nodeTemplate.getId() + " is "
            + ModelUtils.getNodeBaseType(nodeTemplate, csar));

        List<QName> nodeTypeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType(), csar);
        nodeTypeHierarchy.stream()
            .filter(type ->
                org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(type)
                    || org.opentosca.container.core.convention.Utils.isSupportedCloudProviderNodeType(type)
            )
            .findFirst()
            .ifPresent(type -> {
                LOG.debug("Identified supported Infrastructure Type: {}", type);
                infrastructureNodes.add(nodeTemplate);
            });

        for (final TRelationshipTemplate relation : getOutgoingRelations(nodeTemplate, csar)) {
            ModelUtils.LOG.debug("Checking if relation is infrastructure edge, relation: " + relation.getId());
            TNodeTemplate target = getTarget(relation, csar);
            if (ModelUtils.getRelationshipBaseType(relation, csar).equals(Types.hostedOnRelationType)
                || ModelUtils.getRelationshipBaseType(relation, csar).equals(Types.deployedOnRelationType)) {
                ModelUtils.LOG.debug("traversing edge to node: " + target.getId());

                if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(ModelUtils.getNodeBaseType(target, csar))
                    || org.opentosca.container.core.convention.Utils.isSupportedCloudProviderNodeType(ModelUtils.getNodeBaseType(target, csar))) {
                    ModelUtils.LOG.debug("Found infrastructure node: " + target.getId());
                    infrastructureNodes.add(getTarget(relation, csar));
                }
                ModelUtils.getInfrastructureNodes(target, infrastructureNodes, csar);
            }
        }
        ModelUtils.cleanDuplicates(infrastructureNodes);
    }

    /**
     * Adds InfrastructureNodes of the given RelationshipTemplate to the given List of NodeTemplates
     *
     * @param relationshipTemplate an TRelationshipTemplate to search its InfrastructureNodes
     * @param infrastructureNodes  a List of TNodeTemplate where the InfrastructureNodes will be added
     * @param forSource            whether to search for InfrastructureNodes along the SourceInterface or
     *                             TargetInterface
     */
    public static void getInfrastructureNodes(final TRelationshipTemplate relationshipTemplate,
                                              final List<TNodeTemplate> infrastructureNodes,
                                              final boolean forSource, Csar csar) {

        if (forSource) {
            ModelUtils.getInfrastructureNodes(getSource(relationshipTemplate, csar), infrastructureNodes, csar);
        } else {
            ModelUtils.getInfrastructureNodes(getTarget(relationshipTemplate, csar), infrastructureNodes, csar);
        }
    }

    /**
     * Returns the baseType of the given NodeTemplate
     *
     * @param nodeTemplate an TNodeTemplate
     * @return a QName which represents the baseType of the given NodeTemplate
     */
    public static QName getNodeBaseType(final TNodeTemplate nodeTemplate, Csar csar) {
        ModelUtils.LOG.debug("Beginning search for base type of: " + nodeTemplate.getId());
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType(), csar);
        for (final QName type : typeHierarchy) {
            ModelUtils.LOG.debug("Checking Type in Hierarchy, type: " + type.toString());
            if (type.equals(Types.TOSCABASETYPE_SERVER)) {
                return type;
            } else if (type.equals(Types.TOSCABASETYPE_OS)) {
                return type;
            }
        }
        // FIXME: when there are no base types we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    public static TNodeType getNodeBaseType(Csar csar, final TNodeTemplate nodeTemplate) {
        LOG.debug("Beginning search for base type of: " + nodeTemplate.getId());
        final List<TNodeType> typeHierarchy;
        try {
            typeHierarchy = ToscaEngine.resolveNodeTypeHierarchy(csar, nodeTemplate);
        } catch (NotFoundException e) {
            return null;
        }
        for (final TNodeType type : typeHierarchy) {
            ModelUtils.LOG.debug("Checking Type in Hierarchy, type: " + type.toString());
            if (type.getQName().equals(Types.TOSCABASETYPE_SERVER)) {
                return type;
            } else if (type.getQName().equals(Types.TOSCABASETYPE_OS)) {
                return type;
            }
        }
        // FIXME: when there are no base types we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    public static TArtifactTemplate findArtifactTemplate(QName artifactTemplateId, Csar csar) {
        return csar.artifactTemplates().stream().filter(x -> x.getIdFromIdOrNameField().equals(artifactTemplateId.getLocalPart())).findFirst().orElse(null);
    }

    /**
     * Returns all NodeTemplates from the given NodeTemplate going along the path of relation following the target
     * interfaces
     *
     * @param nodeTemplate an TNodeTemplate
     * @param nodes        a List of TNodeTemplate to add the result to
     */
    public static void getNodesFromNodeToSink(final TNodeTemplate nodeTemplate,
                                              final Collection<TNodeTemplate> nodes, Csar csar) {
        nodes.add(nodeTemplate);
        for (final TRelationshipTemplate outgoingTemplate : getOutgoingRelations(nodeTemplate, csar)) {
            if (outgoingTemplate.getType().equals(Types.connectsToRelationType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and make the result even more ambitious
                continue;
            }
            ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes, csar);
        }
        ModelUtils.cleanDuplicates(nodes);
    }

    public static void getNodesFromNodeToSink(final TNodeTemplate nodeTemplate, final QName relationshipType,
                                              final Set<TNodeTemplate> nodes, Csar csar) {
        nodes.add(nodeTemplate);
        for (final TRelationshipTemplate outgoingTemplate : getOutgoingRelations(nodeTemplate, csar)) {
            if (ModelUtils.getRelationshipTypeHierarchy(outgoingTemplate.getType(), csar)
                .contains(relationshipType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and make the result even more ambitious
                ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes, csar);
            }
        }
        ModelUtils.cleanDuplicates(nodes);
    }

    public static void getNodesFromNodeToSource(final TNodeTemplate nodeTemplate,
                                                final Set<TNodeTemplate> nodes, Csar csar) {
        nodes.add(nodeTemplate);
        for (final TRelationshipTemplate ingoingTemplate : getIngoingRelations(nodeTemplate, csar)) {
            if (ingoingTemplate.getType().equals(Types.connectsToRelationType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and make the result even more ambitious
                continue;
            }
            ModelUtils.getNodesFromRelationToSources(ingoingTemplate, nodes, csar);
        }
        ModelUtils.cleanDuplicates(nodes);
    }

    /**
     * Returns all NodeTemplates from the given RelationshipTemplate going along all occurring Relationships using the
     * Target
     *
     * @param relationshipTemplate an TRelationshipTemplate
     * @param nodes                a List of TNodeTemplate to add the result to
     */
    public static void getNodesFromRelationToSink(final TRelationshipTemplate relationshipTemplate,
                                                  final Collection<TNodeTemplate> nodes, Csar csar) {
        final TNodeTemplate nodeTemplate = getTarget(relationshipTemplate, csar);
        nodes.add(nodeTemplate);
        for (final TRelationshipTemplate outgoingTemplate : getOutgoingRelations(nodeTemplate, csar)) {
            if (isCommunicationRelationshipType(outgoingTemplate.getType())) {
                // we skip connectTo relations, as they are connecting stacks
                // and make the result even more ambitious
                continue;
            }
            ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes, csar);
        }
        ModelUtils.cleanDuplicates(nodes);
    }

    private static void getNodesFromRelationToSources(final TRelationshipTemplate ingoingTemplate,
                                                      final Set<TNodeTemplate> nodes, Csar csar) {
        final TNodeTemplate nodeTemplate = getSource(ingoingTemplate, csar);
        nodes.add(nodeTemplate);
        for (final TRelationshipTemplate outgoingTemplate : getIngoingRelations(nodeTemplate, csar)) {
            if (outgoingTemplate.getType().equals(Types.connectsToRelationType)) {
                continue;
            }
            ModelUtils.getNodesFromRelationToSources(outgoingTemplate, nodes, csar);
        }
        ModelUtils.cleanDuplicates(nodes);
    }

    /**
     * Returns a ordered list of QNames. The order represents the inheritance of NodeTypes defining the given NodeType.
     * E.g. NodeType "someNodeType" inherits properties from "someOtherNodeType". The returns list would have
     * {someNs}someNodeType,{someNs}someOtherNodeType inside, in the exact same order.
     *
     * @param nodeType the nodeType to get the hierarchy for
     * @return a List containing an order of inheritance of NodeTypes for this NodeType with itself at the first spot in
     * the list.
     */
    public static List<QName> getNodeTypeHierarchy(final TNodeType nodeType, Csar csar) {
        ModelUtils.LOG.debug("Beginning calculating NodeType Hierarchy for: " + nodeType.getQName());
        final List<QName> typeHierarchy = new ArrayList<>();
        typeHierarchy.add(nodeType.getQName());

        boolean wasNotNull = true;
        // changed from search with qname to search with abstract classes and type ref
        TNodeType lastFoundNodeType = nodeType;
        while (wasNotNull) {

            TNodeType referencedNodeType = null;

            if (lastFoundNodeType.getDerivedFrom() != null) {
                referencedNodeType = findNodeType(lastFoundNodeType.getDerivedFrom().getTypeRef(), csar.nodeTypes());
            }

            if (referencedNodeType == null) {
                wasNotNull = false;
            } else {
                ModelUtils.LOG.debug("Found referenced NodeType: " + referencedNodeType.getQName());
                typeHierarchy.add(referencedNodeType.getQName());
                lastFoundNodeType = referencedNodeType;
            }
        }

        return typeHierarchy;
    }

    public static List<QName> getNodeTypeHierarchy(final QName nodeType, Csar csar) {
        return getNodeTypeHierarchy(findNodeType(nodeType, csar), csar);
    }

    public static TNodeType findNodeType(TNodeTemplate nodeTemplate, Csar csar) {
        return findNodeType(nodeTemplate.getType(), csar);
    }

    public static TNodeType findNodeType(QName id, Csar csar) {
        return findNodeType(id, csar.nodeTypes());
    }

    public static TNodeType findNodeType(QName id, Collection<TNodeType> nodeTypes) {
        if (id.equals(new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure"))) {
            return new TNodeType.Builder("TOSCAManagmentInfrastructure").setTargetNamespace("http://opentosca.org/nodetypes").build();
        }

        return nodeTypes.stream().filter(x -> x.getQName().equals(id)).findFirst().orElse(null);
    }

    public static List<TRelationshipTemplate> getOutgoingInfrastructureEdges(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TRelationshipTemplate> relations = new ArrayList<>();

        for (final TRelationshipTemplate relation : getOutgoingRelations(nodeTemplate, csar)) {
            final List<QName> types = ModelUtils.getRelationshipTypeHierarchy(relation.getType(), csar);
            if (types.contains(Types.dependsOnRelationType) | types.contains(Types.deployedOnRelationType)
                | types.contains(Types.hostedOnRelationType)) {
                relations.add(relation);
            }
        }

        return relations;
    }

    public static List<TRelationshipTemplate> getOutgoingRelations(final TNodeTemplate nodeTemplate,
                                                                   final Collection<QName> relationshipTypes, Csar csar) {
        final List<TRelationshipTemplate> relations = new ArrayList<>();

        for (final TRelationshipTemplate relation : getOutgoingRelations(nodeTemplate, csar)) {
            for (final QName relationshipTypeHierarchyMember : ModelUtils.getRelationshipTypeHierarchy(relation.getType(), csar)) {
                for (final QName relationshipType : relationshipTypes) {
                    if (relationshipTypeHierarchyMember.equals(relationshipType)) {
                        relations.add(relation);
                        break;
                    }
                }
            }
        }

        return relations;
    }

    /**
     * Returns the baseType of the given RelationshipTemplate
     *
     * @param relationshipTemplate an TRelationshipTemplate
     * @return a QName representing the baseType of the given RelationshipTemplate
     */
    public static QName getRelationshipBaseType(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        ModelUtils.LOG.debug("Beginning search for base type of: " + relationshipTemplate.getId());
        final List<QName> typeHierarchy =
            ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getType(), csar);
        for (final QName type : typeHierarchy) {
            ModelUtils.LOG.debug("Checking Type QName: " + type.toString());
            if (type.equals(Types.connectsToRelationType)) {
                return type;
            } else if (type.equals(Types.dependsOnRelationType)) {
                return type;
            } else if (type.equals(Types.hostedOnRelationType)) {
                return type;
            } else if (type.equals(Types.deployedOnRelationType)) {
                return type;
            }
        }
        // FIXME: when there are no base types we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    /**
     * Returns an ordered list of QNames. The order represents the inheritance of RelationshipTypes defining the given
     * RelationshipType. E.g. Relationship "someRelationType" and it inherits properties from "someOtherRelationType".
     * The returns list would have {someNs}someRelationType,{someNs}someOtherRelationType inside, in the exact same
     * order. Var
     *
     * @param relationshipType the RelationshipType to get the hierarchy for
     * @return a List containing an order of inheritance of RelationshipTypes of the given RelationshipType
     */
    public static List<QName> getRelationshipTypeHierarchy(final TRelationshipType relationshipType, Csar csar) {
        final List<QName> typeHierarchy = new ArrayList<>();
        typeHierarchy.add(relationshipType.getQName());

        boolean wasNotNull = true;
        TRelationshipType lastFoundRelationshipType = relationshipType;
        while (wasNotNull) {
            TRelationshipType referencedRelationshipType = null;
            if (lastFoundRelationshipType.getDerivedFrom() != null) {
                referencedRelationshipType = findRelationshipType(lastFoundRelationshipType.getDerivedFrom().getType(), csar.relationshipTypes());
            }
            if (referencedRelationshipType == null) {
                wasNotNull = false;
            } else {
                typeHierarchy.add(referencedRelationshipType.getQName());
                lastFoundRelationshipType = referencedRelationshipType;
            }
        }
        return typeHierarchy;
    }

    public static List<QName> getRelationshipTypeHierarchy(final QName relationshipType, Csar csar) {
        return getRelationshipTypeHierarchy(findRelationshipType(relationshipType, csar), csar);
    }

    public static TRelationshipType findRelationshipType(TRelationshipTemplate relationshipTemplate, Csar csar) {
        return findRelationshipType(relationshipTemplate.getType(), csar);
    }

    public static TRelationshipType findRelationshipType(QName id, Csar csar) {
        return findRelationshipType(id, csar.relationshipTypes());
    }

    public static TRelationshipType findRelationshipType(QName id, Collection<TRelationshipType> relTypes) {
        return relTypes.stream().filter(x -> x.getQName().equals(id)).findFirst().orElse(null);
    }

    /**
     * Looks for a child element with an attribute with the given name and value
     *
     * @param element        the element to look in
     * @param attributeName  the name of the attribute
     * @param attributeValue the value of the attribute
     * @return true if the given element has a child element with an attribute where attributeName.equals(attributeName)
     * & attribute.value.equals(attributeValue), else false
     */
    public static boolean hasChildElementWithAttribute(final Element element, final String attributeName,
                                                       final String attributeValue) {
        if (element == null) {
            return false;
        }
        for (int index = 0; index < element.getChildNodes().getLength(); index++) {
            final Node child = element.getChildNodes().item(index);
            if (child.getAttributes().getNamedItem(attributeName) != null
                && child.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCommunicationRelationshipType(final QName relationshipType) {
        return relationshipType.equals(Types.connectsToRelationType);
    }

    public static boolean isInfrastructureRelationshipType(final QName relationshipType) {
        return relationshipType.equals(Types.dependsOnRelationType)
            || relationshipType.equals(Types.hostedOnRelationType)
            || relationshipType.equals(Types.deployedOnRelationType);
    }

    public static Collection<String> getPropertyNames(final TNodeTemplate nodeTemplate) {
        if (Objects.nonNull(nodeTemplate.getProperties())) {
            return ModelUtils.asMap(nodeTemplate.getProperties()).keySet();
        } else {
            return new HashSet<>();
        }
    }

    public static Collection<TRelationshipTypeImplementation> findRelationshipTypeImplementation(TRelationshipTemplate relationshipTemplate, Csar csar) {
        return findRelationshipTypeImplementation(findRelationshipType(relationshipTemplate, csar), csar);
    }

    public static Collection<TRelationshipTypeImplementation> findRelationshipTypeImplementation(TRelationshipType relationshipType, Csar csar) {
        return csar.relationshipTypeImplementations().stream().filter(x -> x.getRelationshipType().equals(relationshipType.getQName())).collect(Collectors.toList());
    }

    public static Collection<TNodeTypeImplementation> findNodeTypeImplementation(TNodeTemplate nodeTemplate, Csar csar) {
        return findNodeTypeImplementation(findNodeType(nodeTemplate, csar), csar);
    }

    public static Collection<TNodeTypeImplementation> findNodeTypeImplementation(TNodeType nodeType, Csar csar) {
        return csar.nodeTypeImplementations().stream().filter(x -> x.getNodeType().equals(nodeType.getQName())).collect(Collectors.toList());
    }

    /**
     * Finds all NodeTypeImplementations of a nodeTemplate and its complete hierarchy
     *
     * @param nodeTemplate the nodeTemplate
     * @param csar         the csar it belongs to
     * @return a list of nodetype implementations usable on the hierachy of the nodetype
     */
    public static Collection<TNodeTypeImplementation> findAllNodeTypeImplemenations(TNodeTemplate nodeTemplate, Csar csar) {
        return findAllNodeTypeImplemenations(findNodeType(nodeTemplate, csar), csar);
    }

    /**
     * Finds all NodeTypeImplementations of a nodetype and its complete hierarchy
     *
     * @param nodeType the nodeType
     * @param csar     the csar it belongs to
     * @return a list of nodetype implementations usable on the hierachy of the nodetype
     */
    public static Collection<TNodeTypeImplementation> findAllNodeTypeImplemenations(TNodeType nodeType, Csar csar) {
        return getNodeTypeHierarchy(nodeType, csar).stream().map(typeId -> findNodeType(typeId, csar)).map(type -> findNodeTypeImplementation(type, csar)).flatMap(l -> l.stream()).collect(Collectors.toList());
    }

    /**
     * Transforms the given string to a DOM node
     *
     * @param xmlString the xml to transform as String
     * @return a DOM Node representing the given string
     */
    public static Node string2dom(final String xmlString) throws ParserConfigurationException, SAXException,
        IOException {

        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Get the TInterface with a certain name from a NodeTemplate
     *
     * @param nodeTemplate  the name of the NodeTemplate
     * @param interfaceName the name of the interface
     * @param csar          the CSAR containing the ServiceTemplate with the given NodeTemplate
     * @return the TInterface if found, <code>null</code> otherwise
     */
    public static TInterface getInterfaceOfNode(final TNodeTemplate nodeTemplate,
                                                final String interfaceName, Csar csar) {
        return getInterfaceOfNodeType(csar, findNodeType(nodeTemplate, csar), interfaceName, null);
    }

    /**
     * Check whether a given interface is defined for the given NodeTemplate
     *
     * @param nodeTemplate  the name of the NodeTemplate
     * @param interfaceName the name of the interface
     * @param csar          the CSAR containing the ServiceTemplate with the given NodeTemplate
     * @return true if the NodeType hierarchy of the given NodeTemplate specifies the given interface
     */
    public static Boolean hasInterface(final TNodeTemplate nodeTemplate,
                                       final String interfaceName, Csar csar) {
        return Objects.nonNull(getInterfaceOfNodeType(csar, findNodeType(nodeTemplate, csar), interfaceName, null));
    }

    /**
     * Get the interface definition with the given name for the given NodeType. Hereby, the NodeType hierarchy is
     * traversed to collect inherited operations of the same interface. The list of operations only contains operations
     * that are realized by an ImplementationArtifact.
     *
     * @param csar          the CSAR containing the required TOSCA definitions
     * @param nodeType      the NodeType to retrieve the interface definitions from
     * @param interfaceName the name of the required interface
     * @return the interface definition or null if not found
     */
    public static TInterface getInterfaceOfNodeType(Csar csar, TNodeType nodeType, String interfaceName) {
        return getInterfaceOfNodeType(csar, nodeType, interfaceName, null);
    }

    private static TNodeType getDerivedFrom(Csar csar, TNodeType nodeType) {
        if (nodeType.getDerivedFrom() == null) {
            return null;
        }
        return csar.nodeTypes().stream().filter(x -> x.getQName().equals(nodeType.getDerivedFrom().getTypeAsQName())).findFirst().orElse(null);
    }

    public static boolean isOperationImplemented(Csar csar, TNodeType nodeType, String interfaceName, String operationName) {
        TOperation op = getOperation(csar, nodeType, interfaceName, operationName);

        if (op == null) {
            return false;
        }

        Collection<TNodeTypeImplementation> impls = findAllNodeTypeImplemenations(nodeType, csar);

        for (TNodeTypeImplementation nodeImpl : impls) {
            if (isOperationImplemented(csar, nodeImpl, interfaceName, operationName)) {
                return true;
            }
        }

        return false;
    }

    private static TNodeTypeImplementation getDerivedFrom(Csar csar, TNodeTypeImplementation nodeTypeImpl) {
        return csar.nodeTypeImplementations().stream().filter(impl -> impl.getQName().equals(nodeTypeImpl.getDerivedFrom().getType())).findFirst().orElse(null);
    }

    private static boolean isOperationImplemented(Csar csar, TNodeTypeImplementation nodeImpl, String interfaceName, String operationName) {
        // at some point we didn't find an implementation along the type hierarchy
        if (nodeImpl == null) {
            return false;
        }
        for (TImplementationArtifact ia : nodeImpl.getImplementationArtifacts()) {
            if (isOperationImplemented(ia, interfaceName, operationName)) {
                return true;
            }
        }
        return isOperationImplemented(csar, getDerivedFrom(csar, nodeImpl), interfaceName, operationName);
    }

    private static boolean isOperationImplemented(TImplementationArtifact ia, String interfaceName, String operationName) {
        if (!ia.getInterfaceName().equals(interfaceName)) {
            return false;
        }
        if (ia.getOperationName() == null) {
            // if the ia has no operation defined but the interface names fit -> implemented
            return true;
        } else {
            return ia.getOperationName().equals(operationName);
        }
    }

    private static TOperation getOperation(Csar csar, TNodeType nodeType, String interfaceName, String operationName) {
        TInterface iface = getInterfaceOfNodeType(csar, nodeType, interfaceName);
        if (iface == null) {
            return null;
        }
        return iface.getOperations().stream().filter(op -> op.getName().equals(operationName)).findFirst().orElse(null);
    }

    private static TInterface getInterfaceOfNodeType(Csar csar, TNodeType startingNodeType, String interfaceName, TInterface interfaceOfStartingNodeType) {
        // Search for the interface at the current NodeType
        if (startingNodeType == null) {
            return null;
        }

        TInterface foundInterface = getInterfaceFromNodeTypeWithoutHierarchy(startingNodeType, interfaceName);

        if (foundInterface == null) {
            return getInterfaceOfNodeType(csar, getDerivedFrom(csar, startingNodeType), interfaceName);
        }
        return foundInterface;
        // I really don't know what this here is but I assume it should be part of some
        // isImplemented(TInterface) or isImplemented(TOperation method) which checks
        // whether there is a nodetypeimpl which implements the operation of an interface
        /*
        // Use the interface with the given name at the lowest hierarchy level
        TInterface baseInterface = Objects.nonNull(interfaceOfStartingNodeType)
            ? interfaceOfStartingNodeType
            : foundInterface;

        List<TOperation> overriddenOperations = Objects.nonNull(interfaceOfStartingNodeType)
            // We need a new List, as it is otherwise updated in the loop afterwards
            ? new ArrayList<>(interfaceOfStartingNodeType.getOperations())
            : new ArrayList<>();

        // add operations from NodeTypes in the hierarchy if they are not already defined
        if (Objects.nonNull(foundInterface)) {
            for (TOperation operation : foundInterface.getOperations()) {
                // check if the operation is overwritten by a deriving NodeType and add operation otherwise
                if (baseInterface.getOperations().stream().noneMatch(op -> op.getName().equals(operation.getName()))) {
                    baseInterface.getOperations().add(operation);
                }
            }
        }

        if (Objects.nonNull(baseInterface)) {
            // TODO: add NTI inheritance
            // generate mapping from NodeTypeImplementation name to corresponding ImplementationArtifacts
            Map<QName, List<TImplementationArtifact>> implementations = csar.nodeTypeImplementations().stream()
                .filter(implementation -> implementation.getNodeType().equals(startingNodeType.getQName()))
                .filter(implementation -> Objects.nonNull(implementation.getImplementationArtifacts()))
                .collect(Collectors.toMap(TNodeTypeImplementation::getQName, TNodeTypeImplementation::getImplementationArtifacts));

            List<String> notRealizedOperations = new ArrayList<>();
            List<String> realizedOperations = new ArrayList<>();

            // determine the list of realized and not realized operations by checking all corresponding NodeTypeImplementations
            for (Map.Entry<QName, List<TImplementationArtifact>> nodeTypeImplementationArtifactsMapping : implementations.entrySet()) {
                LOG.debug("Determining operations realized for NodeType '{}' and NodeTypeImplementation '{}'!",
                    startingNodeType.getQName(), nodeTypeImplementationArtifactsMapping.getKey());
                determineOperationsRealizedByImplementationArtifacts(baseInterface, notRealizedOperations,
                    realizedOperations, nodeTypeImplementationArtifactsMapping.getValue());
            }

            // remove operations that are realized by any ImplementationArtifact of any NodeTypeImplementation
            notRealizedOperations.removeIf(realizedOperations::contains);
            LOG.debug("Found {} operations realized by a Node Type Implementation: {}.",
                realizedOperations.size(), String.join(", ", realizedOperations));

            if (!notRealizedOperations.isEmpty()) {
                LOG.debug("There are {} operations that are not realized by a Node Type Implementation: {}. Ignoring them...",
                    notRealizedOperations.size(), String.join(", ", notRealizedOperations));
                baseInterface.getOperations()
                    .removeIf(operation ->
                        isOperationNotRealizedByAnImplementationArtifact(foundInterface, baseInterface, overriddenOperations, notRealizedOperations, operation)
                    );
            } else {
                LOG.debug("Found all required IAs for interface {} at Node Type {}", interfaceName, startingNodeType.getQName());
            }
        }

        // Check if NodeType has a parent and recursively search for further interfaces/operations
        TEntityType.DerivedFrom derivedFrom = startingNodeType.getDerivedFrom();
        if (Objects.nonNull(derivedFrom)) {
            TNodeType parentNodeType = csar.nodeTypes().stream()
                .filter(type -> type.getQName().equals(derivedFrom.getTypeRef()))
                .findFirst()
                .orElse(null);
            if (Objects.nonNull(parentNodeType)) {
                return getInterfaceOfNodeType(csar, parentNodeType, interfaceName, baseInterface);
            }
        }

        return baseInterface;*/
    }

    /**
     * Check if the given operation is not realized by an ImplementationArtifact
     *
     * @param foundInterface        the found interface of the current type
     * @param baseInterface         the interface of the lowest child in the type hierarchy defining the specified
     *                              interface
     * @param overriddenOperations  the list of operations that are overridden by children of the current type
     * @param notRealizedOperations the list of not realized operations
     * @param operation             the operation to check if it is already realized
     * @return true if the operation is not realized, false otherwise
     */
    private static boolean isOperationNotRealizedByAnImplementationArtifact(TInterface foundInterface, TInterface baseInterface, List<TOperation> overriddenOperations, List<String> notRealizedOperations, TOperation operation) {
        return (
            // In case the baseInterface is the foundInterface, we can directly remove the not realized operations
            baseInterface == foundInterface
                || overriddenOperations.stream().noneMatch(overriddenOperation -> operation.getName().equals(operation.getName()))
        )
            && notRealizedOperations.contains(operation.getName());
    }

    /**
     * Check which operations of the given interface definition are realized by the given ImplementationArtifacts and
     * add them to corresponding live lists
     *
     * @param tInterface              the interface definition to determine the operations that are realized by the
     *                                given ImplementationArtifacts
     * @param notRealizedOperations   the list of operations that are not realized by any ImplementationArtifact
     * @param realizedOperations      the list of operations that are already realized by any ImplementationArtifact
     * @param implementationArtifacts the list of ImplementationArtifacts that may realise an operation of the given
     *                                interface definition
     */
    private static void determineOperationsRealizedByImplementationArtifacts(TInterface tInterface, List<String> notRealizedOperations, List<String> realizedOperations, List<TImplementationArtifact> implementationArtifacts) {
        if (implementationArtifacts != null) {
            if (implementationArtifacts.stream().anyMatch(ia -> ia.getInterfaceName() == null && ia.getOperationName() == null)) {
                LOG.debug("Found IA that realizes everything!");
            } else if (implementationArtifacts.stream()
                .anyMatch(ia -> ia.getInterfaceName() != null
                    && ia.getInterfaceName().equals(tInterface.getName())
                    && ia.getOperationName() == null)) {
                LOG.debug("Found IA that realizes the whole interface {}!}", tInterface.getName());
            } else {
                List<TOperation> neededOperations = tInterface.getOperations();
                List<String> operationsRealizedInImplementation = implementationArtifacts.stream()
                    .filter(ia -> ia.getInterfaceName() == null
                        || (ia.getInterfaceName() != null && ia.getInterfaceName().equals(tInterface.getName())))
                    .map(TImplementationArtifact::getOperationName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                neededOperations.forEach(neededOperation -> {
                    if (operationsRealizedInImplementation.stream()
                        .anyMatch(providedOperation -> providedOperation.equals(neededOperation.getName()))
                    ) {
                        notRealizedOperations.remove(neededOperation.getName());
                        realizedOperations.add(neededOperation.getName());
                    } else if (!realizedOperations.contains(neededOperation.getName())) {
                        notRealizedOperations.add(neededOperation.getName());
                    }
                });
            }
        } else {
            LOG.warn("No implementation found for interface {}!", tInterface.getName());
        }
    }

    /**
     * Retrieve the interface with the given name from the given NodeType without considering the NodeType hierarchy
     *
     * @param nodeType      the NodeType to search for the interface
     * @param interfaceName the interface name
     * @return the corresponding interface definition or null if not found
     */
    private static TInterface getInterfaceFromNodeTypeWithoutHierarchy(TNodeType nodeType, String interfaceName) {
        TInterface tInterface = Objects.nonNull(nodeType.getInterfaces()) ?
            nodeType.getInterfaces().stream()
                .filter(anInterface -> anInterface.getName().equals(interfaceName))
                .findFirst()
                .orElse(null)
            : null;
        if (tInterface != null) {
            return null;
        }
        tInterface = Objects.nonNull(nodeType.getInterfaceDefinitions()) ?
        nodeType.getInterfaceDefinitions().stream()
            .filter(anInterface -> anInterface.getName().equals(interfaceName))
            .map(anInterface -> ModelUtils.toTInterface(anInterface))
            .findFirst()
            .orElse(null) : null;
        return tInterface;
    }

    public static TInterface toTInterface(TInterfaceDefinition anInterface) {
        return new TInterface.Builder(anInterface.getName(), anInterface.getOperations().stream()
            .map(iface -> ModelUtils.toTOperation(iface)).collect(Collectors.toList())).build();
    }

    public static TOperation toTOperation(TOperationDefinition operationDefinition) {
        return new TOperation.Builder(operationDefinition.getName())
            .setInputParameters(operationDefinition.getInputs().stream().map(parameterDefinition -> ModelUtils.toTParameter(parameterDefinition)).collect(Collectors.toList()))
            .setOutputParameters(operationDefinition.getOutputs().stream().map(parameterDefinition -> ModelUtils.toTParameter(parameterDefinition)).collect(Collectors.toList()))
            .build();
    }


    /**
     * Get the AbstractOperation with a certain name from a NodeTemplate
     *
     * @param nodeTemplate  the name of the NodeTemplate
     * @param interfaceName the name of the interface containing the operation
     * @param operationName the name of the operation
     * @return the AbstractOperation if found, <code>null>/code> otherwise
     */
    public static TOperation getOperationOfNode(final TNodeTemplate nodeTemplate,
                                                final String interfaceName, final String operationName, Csar csar) {
        final TInterface tInterface = ModelUtils.getInterfaceOfNode(nodeTemplate, interfaceName, csar);
        if (Objects.nonNull(tInterface)) {
            return tInterface.getOperations().stream().filter(op -> op.getName().equals(operationName)).findFirst()
                .orElse(null);
        } else {
            LOG.debug("Unable to find interface {} for NodeTemplate {}", interfaceName, nodeTemplate.getName());
            return null;
        }
    }

    /**
     * Calculates a list of DA's containing an effective set of DA combining the DA's from the given NodeImplementation
     * and NodeTemplates according to the TOSCA specification.
     *
     * @param nodeTemplate           the NodeTemplate the NodeImplementations belongs to
     * @param nodeTypeImplementation a NodeTypeImplementation for the given NodeTemplate
     * @return a possibly empty list of TDeploymentArtifacts
     */
    public static List<TDeploymentArtifact> calculateEffectiveDAs(TNodeTemplate nodeTemplate,
                                                                  TNodeTypeImplementation nodeTypeImplementation,
                                                                  Csar csar) {
        return calculateEffectiveDAs(nodeTemplate, nodeTypeImplementation, csar, true);
    }

    private static List<TDeploymentArtifact> calculateEffectiveDAs(TNodeTemplate nodeTemplate,
                                                                   TNodeTypeImplementation nodeTypeImplementation,
                                                                   Csar csar,
                                                                   boolean traceHierarchy) {
        List<TDeploymentArtifact> nodeImplementationDAs =
            nodeTypeImplementation == null || nodeTypeImplementation.getDeploymentArtifacts() == null
                ? new ArrayList<>()
                : nodeTypeImplementation.getDeploymentArtifacts();
        List<TDeploymentArtifact> nodeTemplateDAs =
            nodeTemplate.getDeploymentArtifacts() == null
                ? new ArrayList<>()
                : nodeTemplate.getDeploymentArtifacts();

        // DAs at the Node Template override the Node Type Implementation, if the name is equal.
        List<TDeploymentArtifact> effectiveDAs = new ArrayList<>(nodeTemplateDAs);
        nodeImplementationDAs.forEach(nodeTypeImplementationDA -> {
            if (effectiveDAs.stream()
                .noneMatch(nodeTemplateDa -> nodeTemplateDa.getName().equals(nodeTypeImplementationDA.getName()))
            ) {
                effectiveDAs.add(nodeTypeImplementationDA);
            }
        });

        if (traceHierarchy) {
            TNodeType nodeType = csar.nodeTypesMap().get(nodeTemplate.getType());
            List<QName> nodeTypeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeType, csar);
            // >1 since the hierarchy always contains the type itself at place 0.
            for (int index = 1; index < nodeTypeHierarchy.size(); index++) {
                QName parentType = nodeTypeHierarchy.get(index);
                csar.nodeTypeImplementations().stream()
                    .filter(implementation -> implementation.getNodeType().equals(parentType))
                    .forEach(parentImplementation ->
                        calculateEffectiveDAs(nodeTemplate, parentImplementation, csar, false).forEach(da -> {
                            if (!effectiveDAs.contains(da)) {
                                effectiveDAs.add(da);
                            }
                        })
                    );
            }
        }

        return effectiveDAs;
    }

    public static boolean hasWorkflows(TServiceTemplate serviceTemplate) {
        return !serviceTemplate.getTopologyTemplate().getWorkflows().isEmpty();
    }

    public static TBoundaryDefinitions getTBoundaryDefinitions(TServiceTemplate serviceTemplate) {

        if (serviceTemplate.getBoundaryDefinitions() != null && !hasWorkflows(serviceTemplate)) {
            // if this service template has no workflows it probably XML based => TODO FIXME
            return serviceTemplate.getBoundaryDefinitions();
        } else {
            final TBoundaryDefinitions boundaryDefinitions = new TBoundaryDefinitions.Builder().build();
            // here we only construct a boundary which has the interfaces and plans at least now
            Map<String, TExportedInterface> ifaces = Maps.newHashMap();
            serviceTemplate.getTopologyTemplate().getWorkflows().forEach(wf -> {
                String interfaceName = wf.getImplementation().getDependencies().stream().filter(dep -> dep.startsWith("interfaceName=")).findFirst().orElse(null);
                String operationName = wf.getImplementation().getDependencies().stream().filter(dep -> dep.startsWith("operationName=")).findFirst().orElse(null);

                if (Objects.nonNull(interfaceName) && Objects.nonNull(operationName)) {
                    interfaceName = interfaceName.substring("interfaceName=".length());
                    operationName = operationName.substring("operationName=".length());
                    TExportedOperation op = new TExportedOperation(operationName);
                    TExportedOperation.Plan plan = new TExportedOperation.Plan();
                    plan.setPlanRef(toTPlan(wf));
                    op.setPlan(plan);
                    if (ifaces.containsKey(interfaceName)) {
                        TExportedInterface iFace = ifaces.get(interfaceName);
                        iFace.getOperation().add(op);
                    } else {
                        TExportedInterface iFace = new TExportedInterface(interfaceName, Lists.newArrayList(op));
                        ifaces.put(interfaceName, iFace);
                    }
                }
            });
            boundaryDefinitions.setInterfaces(Lists.newArrayList(ifaces.values()));
            return boundaryDefinitions;
        }
    }

    public static TWorkflow toTWorkflow(TPlan plan, TExportedOperation operation, String interfaceName) {
        List<ParameterDefinition> inputs = plan.getInputParameters().stream().map(param -> ModelUtils.toParameterDefinition(param)).collect(Collectors.toList());
        List<ParameterDefinition> outputs = plan.getOutputParameters().stream().map(param -> ModelUtils.toParameterDefinition(param)).collect(Collectors.toList());
        TWorkflow wf = new TWorkflow.Builder(plan.getId()).setInputs(inputs).setOutputs(outputs).setImplementation(ModelUtils.toTImplementation(plan, operation, interfaceName)).build();
        // TODO FIXME absolutely insane
        wf.setDescription(plan.getPlanType());
        return wf;
    }

    public static TWorkflow toTWorkflow(TPlan plan) {
        List<ParameterDefinition> inputs = plan.getInputParameters().stream().map(param -> ModelUtils.toParameterDefinition(param)).collect(Collectors.toList());
        List<ParameterDefinition> outputs = plan.getOutputParameters().stream().map(param -> ModelUtils.toParameterDefinition(param)).collect(Collectors.toList());
        TWorkflow wf = new TWorkflow.Builder(plan.getId()).setInputs(inputs).setOutputs(outputs).setImplementation(ModelUtils.toTImplementation(plan)).build();
        // TODO FIXME absolutely insane
        wf.setDescription(plan.getPlanType());
        return wf;
    }

    public static TImplementation toTImplementation(TPlan plan, TExportedOperation operation, String interfaceName) {
        TImplementation  impl = new TImplementation();
        impl.setPrimary(plan.getPlanModelReference().getReference());
        // TODO FIXME this is insane
        impl.setOperationHost(plan.getPlanLanguage());
        List<String> deps = Lists.newArrayList();
        deps.add("operationName=" + operation.getName());
        deps.add("interfaceName=" + interfaceName);
        impl.setDependencies(deps);
        return impl;
    }

    public static TImplementation toTImplementation(TPlan plan) {
        TImplementation  impl = new TImplementation();
        impl.setPrimary(plan.getPlanModelReference().getReference());
        // TODO FIXME this is insane
        impl.setOperationHost(plan.getPlanLanguage());
        return impl;
    }

    public static ParameterDefinition toParameterDefinition(TParameter param) {
        ParameterDefinition paramDef = new ParameterDefinition();
        paramDef.setKey(param.getName());
        paramDef.setType(QName.valueOf(param.getType()));
        paramDef.setRequired(param.getRequired());
        return paramDef;
    }

    public static TParameter toTParameter(ParameterDefinition paramDef) {
        return new TParameter.Builder(paramDef.getKey(), paramDef.getType().toString(), paramDef.getRequired()).build();
    }

    public static TPlan toTPlan(TWorkflow workflow) {
        List<TParameter> inputs = workflow.getInputs().stream().map(param -> ModelUtils.toTParameter(param)).collect(Collectors.toList());
        List<TParameter> outputs = workflow.getOutputs().stream().map(param -> ModelUtils.toTParameter(param)).collect(Collectors.toList());
        TPlan plan = new TPlan.Builder(workflow.getName(), workflow.getDescription(), workflow.getImplementation().getOperationHost()).setInputParameters(inputs).setOutputParameters(outputs).setPlanModelReference(ModelUtils.toPlanModelReference(workflow)).build();
        return plan;
    }

    public static TPlan.PlanModelReference toPlanModelReference(TWorkflow workflow) {
        TPlan.PlanModelReference planModelReference = new TPlan.PlanModelReference();
        planModelReference.setReference(workflow.getImplementation().getPrimary());
        return planModelReference;
    }

    public static List<TPlan> getPlans(TServiceTemplate serviceTemplate) {
        if (serviceTemplate.getPlans() != null && !serviceTemplate.getPlans().isEmpty()) {
            return serviceTemplate.getPlans();
        } else {
            return serviceTemplate.getTopologyTemplate().getWorkflows().stream().map(wf -> ModelUtils.toTPlan(wf)).collect(Collectors.toList());
        }
    }
}
