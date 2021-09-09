package org.opentosca.planbuilder.model.utils;

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
import java.util.TreeMap;

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

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import com.google.common.collect.Sets;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class holds utility methods
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class ModelUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    public static String makeValidNCName(final String string) {
        return string.replaceAll("\\.", "_").replaceAll(" ", "_").replace("{", "_").replace("}", "_").replace("/", "_")
            .replace(":", "_");
    }

    /**
     * Returns true if the given QName type denotes to a NodeType in the type hierarchy of the given NodeTemplate
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @param type         the Type as a QName to check against
     * @return true iff the given NodeTemplate contains the given type in its type hierarchy
     */
    public static boolean checkForTypeInHierarchy(final AbstractNodeTemplate nodeTemplate, final QName type) {
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType());
        // as somehow contains won't work here, we must cycle trough
        for (final QName qname : typeHierarchy) {
            if (qname.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes duplicates from the given List
     *
     * @param nodeTemplates a List of AbstractNodeTemplate
     */
    private static void cleanDuplciates(final Collection<AbstractNodeTemplate> nodeTemplates) {
        final List<AbstractNodeTemplate> list = new ArrayList<>();
        for (final AbstractNodeTemplate template : nodeTemplates) {
            boolean match = false;
            for (final AbstractNodeTemplate template2 : list) {
                if (template.getId().equals(template2.getId()) & template == template2) {
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
    public static String getStringFromDoc(final org.w3c.dom.Document doc) {
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
     * @param relationshipTemplates a List of AbstractRelationshipTemplate
     */
    private static void cleanDuplicates(final List<AbstractRelationshipTemplate> relationshipTemplates) {
        final List<AbstractRelationshipTemplate> list = new ArrayList<>();
        for (final AbstractRelationshipTemplate template : relationshipTemplates) {
            boolean match = false;
            for (final AbstractRelationshipTemplate template2 : list) {
                if (template.getId().equals(template2.getId()) & template == template2) {
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
        final List<QName> qnames = new ArrayList<>();
        final Collection<TArtifactType> artifactTypes = fetchAllArtifactTypes(csar);
        qnames.add(artifactTemplate.getType());

        TArtifactType type = findArtifactType(artifactTemplate.getType(), artifactTypes);

        TArtifactType ref = findArtifactType(type.getDerivedFrom().getTypeRef(), artifactTypes);

        while (ref != null) {
            qnames.add(ref.getQName());
            ref = findArtifactType(ref.getDerivedFrom().getTypeRef(), artifactTypes);
        }

        return qnames;
    }

    public static TArtifactType findArtifactType(QName id, Collection<TArtifactType> artifactTypes) {
        return artifactTypes.stream().filter(x -> x.getQName().equals(id)).findFirst().orElse(null);
    }

    public static Collection<TArtifactType> fetchAllArtifactTypes(Csar csar) {

        Set<TArtifactType> resultSet = Sets.newHashSet();
        fetchAllDefs(csar).forEach(x -> resultSet.addAll(x.getArtifactTypes()));
        return resultSet;
    }

    public static Collection<TDefinitions> fetchAllDefs(Csar csar) {
        IRepository repo = RepositoryFactory.getRepository(csar.getSaveLocation());
        Collection<DefinitionsChildId> ids = repo.getAllDefinitionsChildIds();
        Set<TDefinitions> defs = Sets.newHashSet();
        ids.forEach(x -> defs.add(repo.getDefinitions(x)));
        return defs;
    }

    public static String getNamespace(TEntityTemplate.Properties properties) {
        boolean isDOM = false;
        if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            isDOM = true;
        }
        boolean isWineryKV = false;
        if (properties.getClass().getName().equals(TEntityTemplate.WineryKVProperties.class.getName())) {
            isWineryKV = true;
        }

        if (isDOM) {
            return ((Element) properties).getNamespaceURI();
        }

        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getNamespace();
        }

        return null;
    }

    public static String getElementName(TEntityTemplate.Properties properties) {
        boolean isDOM = false;
        if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            isDOM = true;
        }
        boolean isWineryKV = false;
        if (properties.getClass().getName().equals(TEntityTemplate.WineryKVProperties.class.getName())) {
            isWineryKV = true;
        }

        if (isDOM) {
            return ((Element) properties).getLocalName();
        }

        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getElementName();
        }

        return null;
    }

    public static Map<String, String> asMap(TBoundaryDefinitions.Properties properties) {
        boolean isDOM = false;
        if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            isDOM = true;
        }
        boolean isWineryKV = false;
        if (properties.getClass().getName().equals(TEntityTemplate.WineryKVProperties.class.getName())) {
            isWineryKV = true;
        }

        if (isDOM) {
            final PropertyParser parser = new PropertyParser();
            Map<String, String> props = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            final Element element = (Element) properties;
            if (element != null) {
                props = parser.parse(element);
            }
            return props;
        }
        return new HashMap<>();
    }

    public static Map<String, String> asMap(TEntityTemplate.Properties properties) {
        boolean isDOM = false;
        if (properties.getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.ElementNSImpl")) {
            isDOM = true;
        }
        boolean isWineryKV = false;
        if (properties.getClass().getName().equals(TEntityTemplate.WineryKVProperties.class.getName())) {
            isWineryKV = true;
        }

        if (isDOM) {
            final PropertyParser parser = new PropertyParser();
            Map<String, String> props = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            final Element element = (Element) properties;
            if (element != null) {
                props = parser.parse(element);
            }
            return props;
        }
        if (isWineryKV) {
            return ((TEntityTemplate.WineryKVProperties) properties).getKVProperties();
        }
        return new HashMap<>();
    }

    /**
     * Adds the InfrastructureEdges of the given NodeTemplate to the given List
     *
     * @param nodeTemplate        an AbstractNodeTemplate
     * @param infrastructureEdges a List of AbstractRelationshipTemplate to add the InfrastructureEdges to
     */
    public static void getInfrastructureEdges(final AbstractNodeTemplate nodeTemplate,
                                              final List<AbstractRelationshipTemplate> infrastructureEdges, Csar csar) {

        // fetch all infrastructureNodes
        final List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, csar);

        // check all outgoing edges on those nodes, if they are infrastructure
        // edges
        for (final AbstractNodeTemplate infraNode : infraNodes) {
            for (final AbstractRelationshipTemplate outgoingEdge : infraNode.getOutgoingRelations()) {

                if (isInfrastructureRelationshipType(outgoingEdge.getType())) {

                    infrastructureEdges.add(outgoingEdge);
                }
            }
        }

        // check outgoing edges of given node

        for (final AbstractRelationshipTemplate outgoingEdge : nodeTemplate.getOutgoingRelations()) {
            if (isInfrastructureRelationshipType(outgoingEdge.getType())) {
                infrastructureEdges.add(outgoingEdge);
            }
        }
        ModelUtils.cleanDuplicates(infrastructureEdges);
    }

    /**
     * Adds the InfrastructureEdges of the given RelationshipTemplate to the given List
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     * @param infraEdges           a List of AbstractRelationshipTemplate to add the InfrastructureEdges to
     * @param forSource            whether to search for InfrastructureEdges along the SourceInterface or
     *                             TargetInterface
     */
    public static void getInfrastructureEdges(final AbstractRelationshipTemplate relationshipTemplate,
                                              final List<AbstractRelationshipTemplate> infraEdges,
                                              final boolean forSource, Csar csar) {
        if (forSource) {
            ModelUtils.getInfrastructureEdges(relationshipTemplate.getSource(), infraEdges, csar);
        } else {
            ModelUtils.getInfrastructureEdges(relationshipTemplate.getTarget(), infraEdges, csar);
        }
    }

    /**
     * Calculates all Infrastructure Nodes of all Infrastructure Paths originating from the given NodeTemplate
     *
     * @param nodeTemplate        AbstractNodeTemplate from where the search for Infrastructure Nodes begin
     * @param infrastructureNodes a List of AbstractNodeTemplates which represent Infrastructure Nodes of the given
     *                            NodeTemplate (including itself when applicable as an infrastructure node)
     * @Info the infrastructureNodes List must be empty
     */
    public static void getInfrastructureNodes(final AbstractNodeTemplate nodeTemplate,
                                              final Collection<AbstractNodeTemplate> infrastructureNodes, Csar csar) {
        ModelUtils.LOG.debug("BaseType of NodeTemplate " + nodeTemplate.getId() + " is "
            + ModelUtils.getNodeBaseType(nodeTemplate));

        List<QName> nodeTypeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType());
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

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            ModelUtils.LOG.debug("Checking if relation is infrastructure edge, relation: " + relation.getId());
            if (ModelUtils.getRelationshipBaseType(relation, csar).equals(Types.hostedOnRelationType)
                || ModelUtils.getRelationshipBaseType(relation, csar).equals(Types.deployedOnRelationType)) {
                ModelUtils.LOG.debug("traversing edge to node: " + relation.getTarget().getId());

                if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(ModelUtils.getNodeBaseType(relation.getTarget()))
                    || org.opentosca.container.core.convention.Utils.isSupportedCloudProviderNodeType(ModelUtils.getNodeBaseType(relation.getTarget()))) {
                    ModelUtils.LOG.debug("Found infrastructure node: " + relation.getTarget().getId());
                    infrastructureNodes.add(relation.getTarget());
                }
                ModelUtils.getInfrastructureNodes(relation.getTarget(), infrastructureNodes, csar);
            }
        }
        ModelUtils.cleanDuplciates(infrastructureNodes);
    }

    /**
     * Adds InfrastructureNodes of the given RelaitonshipTemplate to the given List of NodeTemplates
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate to search its InfrastructureNodes
     * @param infrastructureNodes  a List of AbstractNodeTemplate where the InfrastructureNodes will be added
     * @param forSource            whether to search for InfrastructureNodes along the SourceInterface or
     *                             TargetInterface
     */
    public static void getInfrastructureNodes(final AbstractRelationshipTemplate relationshipTemplate,
                                              final List<AbstractNodeTemplate> infrastructureNodes,
                                              final boolean forSource, Csar csar) {

        if (forSource) {
            ModelUtils.getInfrastructureNodes(relationshipTemplate.getSource(), infrastructureNodes, csar);
        } else {
            ModelUtils.getInfrastructureNodes(relationshipTemplate.getTarget(), infrastructureNodes, csar);
        }
    }

    public static List<AbstractRelationshipTemplate> getIngoingRelations(final AbstractNodeTemplate nodeTemplate,
                                                                         final Collection<QName> relationshipTypes, Csar csar) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();
        for (final AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
            for (final QName relationshipTypeHierarchyMember : ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType(), csar)) {
                final boolean match = false;
                for (final QName relationshipType : relationshipTypes) {
                    if (relationshipTypeHierarchyMember.equals(relationshipType)) {
                        relations.add(relation);
                        break;
                    }
                }
                if (match) {
                    break;
                }
            }
        }

        return relations;
    }

    /**
     * Returns the baseType of the given NodeTemplate
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @return a QName which represents the baseType of the given NodeTemplate
     */
    public static QName getNodeBaseType(final AbstractNodeTemplate nodeTemplate) {
        ModelUtils.LOG.debug("Beginning search for basetype of: " + nodeTemplate.getId());
        final List<QName> typeHierarchy = ModelUtils.getNodeTypeHierarchy(nodeTemplate.getType());
        for (final QName type : typeHierarchy) {
            ModelUtils.LOG.debug("Checking Type in Hierarchy, type: " + type.toString());
            if (type.equals(Types.TOSCABASETYPE_SERVER)) {
                return type;
            } else if (type.equals(Types.TOSCABASETYPE_OS)) {
                return type;
            }
        }
        // FIXME: when there are no basetypes we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    public static TNodeType getNodeBaseType(Csar csar, final TNodeTemplate nodeTemplate) {
        LOG.debug("Beginning search for basetype of: " + nodeTemplate.getId());
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
        // FIXME: when there are no basetypes we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    /**
     * Returns all NodeTemplates from the given NodeTemplate going along the path of relation following the target
     * interfaces
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @param nodes        a List of AbstractNodeTemplate to add the result to
     */
    public static void getNodesFromNodeToSink(final AbstractNodeTemplate nodeTemplate,
                                              final Collection<AbstractNodeTemplate> nodes) {
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
            if (outgoingTemplate.getType().equals(Types.connectsToRelationType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and
                // make the result even more ambigious
                continue;
            }
            ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes);
        }
        ModelUtils.cleanDuplciates(nodes);
    }

    public static void getNodesFromNodeToSink(final AbstractNodeTemplate nodeTemplate, final QName relationshipType,
                                              final Collection<AbstractNodeTemplate> nodes, Csar csar) {
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(outgoingTemplate.getRelationshipType(), csar)
                .contains(relationshipType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and
                // make the result even more ambigious
                ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes);
            }
        }
        ModelUtils.cleanDuplciates(nodes);
    }

    public static void getNodesFromNodeToSource(final AbstractNodeTemplate nodeTemplate,
                                                final List<AbstractNodeTemplate> nodes) {
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate ingoingTemplate : nodeTemplate.getIngoingRelations()) {
            if (ingoingTemplate.getType().equals(Types.connectsToRelationType)) {
                // we skip connectTo relations, as they are connecting stacks
                // and
                // make the result even more ambigious
                continue;
            }
            ModelUtils.getNodesFromRelationToSources(ingoingTemplate, nodes);
        }
        ModelUtils.cleanDuplciates(nodes);
    }

    /**
     * Returns all NodeTemplates from the given RelationshipTemplate going along all occuring Relationships using the
     * Target
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     * @param nodes                a List of AbstractNodeTemplate to add the result to
     */
    public static void getNodesFromRelationToSink(final AbstractRelationshipTemplate relationshipTemplate,
                                                  final Collection<AbstractNodeTemplate> nodes) {
        final AbstractNodeTemplate nodeTemplate = relationshipTemplate.getTarget();
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
            if (isCommunicationRelationshipType(outgoingTemplate.getType())) {
                // we skip connectTo relations, as they are connecting stacks
                // and
                // make the result even more ambigious
                continue;
            }
            ModelUtils.getNodesFromRelationToSink(outgoingTemplate, nodes);
        }
        ModelUtils.cleanDuplciates(nodes);
    }

    private static void getNodesFromRelationToSources(final AbstractRelationshipTemplate ingoingTemplate,
                                                      final List<AbstractNodeTemplate> nodes) {
        final AbstractNodeTemplate nodeTemplate = ingoingTemplate.getSource();
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getIngoingRelations()) {
            if (outgoingTemplate.getType().equals(Types.connectsToRelationType)) {
                continue;
            }
            ModelUtils.getNodesFromRelationToSources(outgoingTemplate, nodes);
        }
        ModelUtils.cleanDuplciates(nodes);
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
    public static List<QName> getNodeTypeHierarchy(final AbstractNodeType nodeType) {
        ModelUtils.LOG.debug("Beginning calculating NodeType Hierarchy for: " + nodeType.getId().toString());
        final List<QName> typeHierarchy = new ArrayList<>();
        typeHierarchy.add(nodeType.getId());

        boolean wasNotNull = true;
        // changed from search with qname to search with abstract classes and
        // typeref
        AbstractNodeType lastFoundNodeType = nodeType;
        while (wasNotNull) {

            final AbstractNodeType referencedNodeType = lastFoundNodeType.getTypeRef();

            if (referencedNodeType == null) {
                wasNotNull = false;
            } else {
                ModelUtils.LOG.debug("Found referenced NodeType: " + referencedNodeType.getId().toString());
                typeHierarchy.add(referencedNodeType.getId());
                lastFoundNodeType = referencedNodeType;
            }
        }

        return typeHierarchy;
    }

    public static List<AbstractRelationshipTemplate> getOutgoingInfrastructureEdges(final AbstractNodeTemplate nodeTemplate, Csar csar) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            final List<QName> types = ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType(), csar);
            if (types.contains(Types.dependsOnRelationType) | types.contains(Types.deployedOnRelationType)
                | types.contains(Types.hostedOnRelationType)) {
                relations.add(relation);
            }
        }

        return relations;
    }

    public static List<AbstractRelationshipTemplate> getOutgoingRelations(final AbstractNodeTemplate nodeTemplate,
                                                                          final Collection<QName> relationshipTypes, Csar csar) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            for (final QName relationshipTypeHierarchyMember : ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType(), csar)) {
                final boolean match = false;
                for (final QName relationshipType : relationshipTypes) {
                    if (relationshipTypeHierarchyMember.equals(relationshipType)) {
                        relations.add(relation);
                        break;
                    }
                }
                if (match) {
                    break;
                }
            }
        }

        return relations;
    }

    /**
     * Returns the baseType of the given RelationshipTemplate
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     * @return a QName representing the baseType of the given RelationshipTemplate
     */
    public static QName getRelationshipBaseType(final AbstractRelationshipTemplate relationshipTemplate, Csar csar) {
        ModelUtils.LOG.debug("Beginning search for basetype of: " + relationshipTemplate.getId());
        final List<QName> typeHierarchy =
            ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType(), csar);
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
        // FIXME: when there are no basetypes we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    /**
     * Returns a ordered list of QNames. The order represents the inheritance of RelationshipTypes defining the given
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
            final TRelationshipType referencedRelationshipType = findRelationshipType(lastFoundRelationshipType.getDerivedFrom().getType(), fetchAllRelationshipTypes(csar));
            if (referencedRelationshipType == null) {
                wasNotNull = false;
            } else {
                typeHierarchy.add(referencedRelationshipType.getQName());
                lastFoundRelationshipType = referencedRelationshipType;
            }
        }
        return typeHierarchy;
    }

    public static TRelationshipType findRelationshipType(QName id, Collection<TRelationshipType> relTypes) {
        return relTypes.stream().filter(x -> x.getQName().equals(id)).findFirst().orElse(null);
    }

    public static Collection<TRelationshipType> fetchAllRelationshipTypes(Csar csar) {

        Set<TRelationshipType> resultSet = Sets.newHashSet();
        fetchAllDefs(csar).forEach(x -> resultSet.addAll(x.getRelationshipTypes()));
        return resultSet;
    }

    /**
     * Looks for a childelement with an attribute with the given name and value
     *
     * @param element        the element to look in
     * @param attributeName  the name of the attribute
     * @param attributeValue the value of the attribute
     * @return true if the given element has a child element with an attribute where attrname.equals(attributeName) &
     * attr.value(attributeValue), else false
     */
    public static boolean hasChildElementWithAttribute(final Element element, final String attributeName,
                                                       final String attributeValue) {
        if (element == null) {
            return false;
        }
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            final Node child = element.getChildNodes().item(i);
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
            | relationshipType.equals(Types.hostedOnRelationType)
            | relationshipType.equals(Types.deployedOnRelationType);
    }

    public static Collection<String> getPropertyNames(final AbstractNodeTemplate nodeTemplate) {
        if (Objects.nonNull(nodeTemplate.getProperties())) {
            return ModelUtils.asMap(nodeTemplate.getProperties()).keySet();
        } else {
            return new HashSet<>();
        }
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
     * Get the AbstractInterface with a certain name from a NodeTemplate
     *
     * @param nodeTemplate  the name of the NodeTemplate
     * @param interfaceName the name of the interface
     * @return the AbstractInterface if found, <code>null</code> otherwise
     */
    public static TInterface getInterfaceOfNode(final AbstractNodeTemplate nodeTemplate,
                                                final String interfaceName) {
        return nodeTemplate.getType().getInterfaces().stream().filter(iface -> iface.getName().equals(interfaceName))
            .findFirst().orElse(null);
    }

    /**
     * Get the AbstractOperation with a certain name from a NodeTemplate
     *
     * @param nodeTemplate  the name of the NodeTemplate
     * @param interfaceName the name of the interface containing the operation
     * @param operationName the name of the operation
     * @return the AbstractOperation if found, <code>null>/code> otherwise
     */
    public static TOperation getOperationOfNode(final AbstractNodeTemplate nodeTemplate,
                                                final String interfaceName, final String operationName) {
        final TInterface iface = ModelUtils.getInterfaceOfNode(nodeTemplate, interfaceName);
        if (Objects.nonNull(iface)) {
            return iface.getOperations().stream().filter(op -> op.getName().equals(operationName)).findFirst()
                .orElse(null);
        } else {
            LOG.debug("Unable to find interface {} for NodeTemplate {}", interfaceName, nodeTemplate.getName());
            return null;
        }
    }
}
