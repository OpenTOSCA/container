package org.opentosca.planbuilder.model.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactType;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
 *
 */
public class ModelUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ModelUtils.class);

    // these are the baseTypes of the PlanBuilder -> TODO refactor into some
    // kind of baseType-Configuration
    public static final QName TOSCABASETYPE_CONNECTSTO =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ConnectsTo");
    public static final QName TOSCABASETYPE_HOSTEDON =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "HostedOn");
    public static final QName TOSCABASETYPE_DEPLOYEDON =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DeployedOn");
    public static final QName TOSCABASETYPE_DEPENDSON =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DependsOn");
    public static final QName TOSCABASETYPE_SERVER =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "Server");
    public static final QName TOSCABASETYPE_OS =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "OperatingSystem");

    // this is a BRUTAL hack for the new nodetypes
    public final static QName ubuntu1404ServerVmNodeType =
        new QName("http://opentosca.org/nodetypes", "Ubuntu-14.04-VM");
    public final static QName raspbianJessieOSNodeType = new QName("http://opentosca.org/nodetypes", "RaspbianJessie");
    public final static QName externalResourceNodeType =
        new QName("http://opentosca.org/nodetypes", "ExternalResource");



    public static String makeValidNCName(final String string) {
        return string.replaceAll("\\.", "_").replaceAll(" ", "_");
    }

    /**
     * Returns true if the given QName type denotes to a NodeType in the type hierarchy of the given
     * NodeTemplate
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @param type the Type as a QName to check against
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
     * Returns true if the given QName type denotes to a RelationshipType in the type hierarchy of the
     * given RelationshipTemplate
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     * @param type the Type as a QName to check against
     * @return true iff the given RelationshipTemplate contains the given type in its type hierarchy
     */
    public static boolean checkForTypeInHierarchy(final AbstractRelationshipTemplate relationshipTemplate,
                                                  final QName type) {
        final List<QName> typeHierarchy =
            ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType());
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
    private static void cleanDuplciates(final List<AbstractNodeTemplate> nodeTemplates) {
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
        }
        catch (final TransformerException ex) {
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

    public static Set<AbstractDeploymentArtifact> computeEffectiveDeploymentArtifacts(final AbstractNodeTemplate nodeTemplate,
                                                                                      final AbstractNodeTypeImplementation nodeImpl) {
        final Set<AbstractDeploymentArtifact> effectiveDAs = new HashSet<>();
        effectiveDAs.addAll(nodeTemplate.getDeploymentArtifacts());
        for (final AbstractDeploymentArtifact da : nodeImpl.getDeploymentArtifacts()) {
            if (!effectiveDAs.contains(da)) {
                effectiveDAs.add(da);
            }
        }

        return effectiveDAs;
    }

    public static List<QName> getArtifactTypeHierarchy(final AbstractArtifactTemplate artifactTemplate) {
        final List<QName> qnames = new ArrayList<>();

        qnames.add(artifactTemplate.getAbstractArtifactType().getId());

        AbstractArtifactType ref = artifactTemplate.getAbstractArtifactType().getTypeRef();

        while (ref != null) {
            qnames.add(ref.getId());
            ref = ref.getTypeRef();
        }

        return qnames;
    }

    /**
     * Adds the InfrastructureEdges of the given NodeTemplate to the given List
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @param infrastructureEdges a List of AbstractRelationshipTemplate to add the InfrastructureEdges
     *        to
     */
    public static void getInfrastructureEdges(final AbstractNodeTemplate nodeTemplate,
                                              final List<AbstractRelationshipTemplate> infrastructureEdges) {

        // fetch all infrastructureNodes
        final List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes);

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
     * @param infraEdges a List of AbstractRelationshipTemplate to add the InfrastructureEdges to
     * @param forSource whether to search for InfrastructureEdges along the SourceInterface or
     *        TargetInterface
     */
    public static void getInfrastructureEdges(final AbstractRelationshipTemplate relationshipTemplate,
                                              final List<AbstractRelationshipTemplate> infraEdges,
                                              final boolean forSource) {
        if (forSource) {
            ModelUtils.getInfrastructureEdges(relationshipTemplate.getSource(), infraEdges);
        } else {
            ModelUtils.getInfrastructureEdges(relationshipTemplate.getTarget(), infraEdges);
        }
    }

    /**
     * Calculates all Infrastructure Nodes of all Infrastructure Paths originating from the given
     * NodeTemplate
     *
     * @param nodeTemplate AbstractNodeTemplate from where the search for Infrstructure Nodes begin
     * @param infrastructureNodes a List of AbstractNodeTemplates which represent Infrastructure Nodes
     *        of the given NodeTemplate (including itself when applicable as an infrastructure node)
     * @Info the infrastructureNodes List must be empty
     */
    public static void getInfrastructureNodes(final AbstractNodeTemplate nodeTemplate,
                                              final List<AbstractNodeTemplate> infrastructureNodes) {
        ModelUtils.LOG.debug("BaseType of NodeTemplate " + nodeTemplate.getId() + " is "
            + ModelUtils.getNodeBaseType(nodeTemplate));

        if (org.opentosca.container.core.tosca.convention.Utils.isSupportedInfrastructureNodeType(ModelUtils.getNodeBaseType(nodeTemplate))
            || org.opentosca.container.core.tosca.convention.Utils.isSupportedCloudProviderNodeType(ModelUtils.getNodeBaseType(nodeTemplate))) {
            infrastructureNodes.add(nodeTemplate);
        }

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            ModelUtils.LOG.debug("Checking if relation is infrastructure edge, relation: " + relation.getId());
            if (ModelUtils.getRelationshipBaseType(relation).equals(ModelUtils.TOSCABASETYPE_DEPENDSON)
                || ModelUtils.getRelationshipBaseType(relation).equals(ModelUtils.TOSCABASETYPE_HOSTEDON)
                || ModelUtils.getRelationshipBaseType(relation).equals(ModelUtils.TOSCABASETYPE_DEPLOYEDON)) {
                ModelUtils.LOG.debug("traversing edge to node: " + relation.getTarget().getId());

                if (org.opentosca.container.core.tosca.convention.Utils.isSupportedInfrastructureNodeType(ModelUtils.getNodeBaseType(relation.getTarget()))
                    || org.opentosca.container.core.tosca.convention.Utils.isSupportedCloudProviderNodeType(ModelUtils.getNodeBaseType(relation.getTarget()))) {
                    ModelUtils.LOG.debug("Found infrastructure node: " + relation.getTarget().getId());
                    infrastructureNodes.add(relation.getTarget());
                }
                ModelUtils.getInfrastructureNodes(relation.getTarget(), infrastructureNodes);
            }
        }
        ModelUtils.cleanDuplciates(infrastructureNodes);
    }

    /**
     * Adds InfrastructureNodes of the given RelaitonshipTemplate to the given List of NodeTemplates
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate to search its InfrastructureNodes
     * @param infrastructureNodes a List of AbstractNodeTemplate where the InfrastructureNodes will be
     *        added
     * @param forSource whether to search for InfrastructureNodes along the SourceInterface or
     *        TargetInterface
     */
    public static void getInfrastructureNodes(final AbstractRelationshipTemplate relationshipTemplate,
                                              final List<AbstractNodeTemplate> infrastructureNodes,
                                              final boolean forSource) {

        if (forSource) {
            ModelUtils.getInfrastructureNodes(relationshipTemplate.getSource(), infrastructureNodes);
        } else {
            ModelUtils.getInfrastructureNodes(relationshipTemplate.getTarget(), infrastructureNodes);
        }

    }

    public static List<AbstractRelationshipTemplate> getIngoingRelations(final AbstractNodeTemplate nodeTemplate,
                                                                         final QName... relationshipTypes) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();
        for (final AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
            for (final QName relationshipTypeHierarchyMember : ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType())) {
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
            if (type.equals(ModelUtils.TOSCABASETYPE_SERVER)) {
                return type;
            } else if (type.equals(ModelUtils.TOSCABASETYPE_OS)) {
                return type;
            }
        }
        // FIXME: when there are no basetypes we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    /**
     * Returns all NodeTemplates from the given NodeTemplate going along the path of relation following
     * the target interfaces
     *
     * @param nodeTemplate an AbstractNodeTemplate
     * @param nodes a List of AbstractNodeTemplate to add the result to
     */
    public static void getNodesFromNodeToSink(final AbstractNodeTemplate nodeTemplate,
                                              final List<AbstractNodeTemplate> nodes) {
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
            if (outgoingTemplate.getType().equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
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
                                              final List<AbstractNodeTemplate> nodes) {
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(outgoingTemplate.getRelationshipType())
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
            if (ingoingTemplate.getType().equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
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
     * Returns all NodeTemplates from the given RelationshipTemplate going along all occuring
     * Relationships using the Target
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     * @param nodes a List of AbstractNodeTemplate to add the result to
     */
    public static void getNodesFromRelationToSink(final AbstractRelationshipTemplate relationshipTemplate,
                                                  final List<AbstractNodeTemplate> nodes) {
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

    public static void getNodesFromRelationToSink(final AbstractRelationshipTemplate relationshipTemplate,
                                                  final QName relationshipType,
                                                  final List<AbstractNodeTemplate> nodes) {
        final AbstractNodeTemplate nodeTemplate = relationshipTemplate.getTarget();
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {

            if (ModelUtils.getRelationshipTypeHierarchy(outgoingTemplate.getRelationshipType())
                          .contains(relationshipType)) {

                ModelUtils.getNodesFromRelationToSink(outgoingTemplate, relationshipType, nodes);
            }

        }
        ModelUtils.cleanDuplciates(nodes);
    }

    private static void getNodesFromRelationToSources(final AbstractRelationshipTemplate ingoingTemplate,
                                                      final List<AbstractNodeTemplate> nodes) {
        final AbstractNodeTemplate nodeTemplate = ingoingTemplate.getSource();
        nodes.add(nodeTemplate);
        for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getIngoingRelations()) {
            if (outgoingTemplate.getType().equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
                continue;
            }
            ModelUtils.getNodesFromRelationToSources(outgoingTemplate, nodes);
        }
        ModelUtils.cleanDuplciates(nodes);
    }

    /**
     * Returns a ordered list of QNames. The order represents the inheritance of NodeTypes defining the
     * given NodeType. E.g. NodeType "someNodeType" inherits properties from "someOtherNodeType". The
     * returns list would have {someNs}someNodeType,{someNs}someOtherNodeType inside, in the exact same
     * order.
     *
     * @param nodeType the nodeType to get the hierarchy for
     * @return a List containing an order of inheritance of NodeTypes for this NodeType with itself at
     *         the first spot in the list.
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

    public static List<AbstractRelationshipTemplate> getOutgoingInfrastructureEdges(final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            final List<QName> types = ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType());
            if (types.contains(TOSCABASETYPE_DEPENDSON) | types.contains(TOSCABASETYPE_DEPLOYEDON)
                | types.contains(TOSCABASETYPE_HOSTEDON)) {
                relations.add(relation);
            }
        }

        return relations;
    }

    public static List<AbstractRelationshipTemplate> getOutgoingRelations(final AbstractNodeTemplate nodeTemplate,
                                                                          final QName... relationshipTypes) {
        final List<AbstractRelationshipTemplate> relations = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            for (final QName relationshipTypeHierarchyMember : ModelUtils.getRelationshipTypeHierarchy(relation.getRelationshipType())) {
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
    public static QName getRelationshipBaseType(final AbstractRelationshipTemplate relationshipTemplate) {
        ModelUtils.LOG.debug("Beginning search for basetype of: " + relationshipTemplate.getId());
        final List<QName> typeHierarchy =
            ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType());
        for (final QName type : typeHierarchy) {
            ModelUtils.LOG.debug("Checking Type QName: " + type.toString());
            if (type.equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
                return type;
            } else if (type.equals(ModelUtils.TOSCABASETYPE_DEPENDSON)) {
                return type;
            } else if (type.equals(ModelUtils.TOSCABASETYPE_HOSTEDON)) {
                return type;
            } else if (type.equals(ModelUtils.TOSCABASETYPE_DEPLOYEDON)) {
                return type;
            }
        }
        // FIXME: when there are no basetypes we're screwed
        return typeHierarchy.get(typeHierarchy.size() - 1);
    }

    /**
     * Returns a ordered list of QNames. The order represents the inheritance of RelationshipTypes
     * defining the given RelationshipType. E.g. Relationship "someRelationType" and it inherits
     * properties from "someOtherRelationType". The returns list would have
     * {someNs}someRelationType,{someNs}someOtherRelationType inside, in the exact same order. Var
     *
     * @param definitions the Definitions to look in
     * @param relationshipType the RelationshipType to get the hierarchy for
     * @return a List containing an order of inheritance of RelationshipTypes of the given
     *         RelationshipType
     */
    public static List<QName> getRelationshipTypeHierarchy(final AbstractRelationshipType relationshipType) {
        final List<QName> typeHierarchy = new ArrayList<>();
        typeHierarchy.add(relationshipType.getId());

        boolean wasNotNull = true;
        AbstractRelationshipType lastFoundRelationshipType = relationshipType;
        while (wasNotNull) {
            final AbstractRelationshipType referencedRelationshipType = lastFoundRelationshipType.getReferencedType();
            if (referencedRelationshipType == null) {
                wasNotNull = false;
            } else {
                typeHierarchy.add(referencedRelationshipType.getId());
                lastFoundRelationshipType = referencedRelationshipType;
            }
        }
        return typeHierarchy;
    }

    /**
     * Looks for a childelement with an attribute with the given name and value
     *
     * @param element the element to look in
     * @param attributeName the name of the attribute
     * @param attributeValue the value of the attribute
     * @return true if the given element has a child element with an attribute where
     *         attrname.equals(attributeName) & attr.value(attributeValue), else false
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
        return relationshipType.equals(ModelUtils.TOSCABASETYPE_CONNECTSTO);
    }

    public static boolean isInfrastructureRelationshipType(final QName relationshipType) {
        return relationshipType.equals(ModelUtils.TOSCABASETYPE_DEPENDSON)
            | relationshipType.equals(ModelUtils.TOSCABASETYPE_HOSTEDON)
            | relationshipType.equals(ModelUtils.TOSCABASETYPE_DEPLOYEDON);
    }

    public static List<String> getPropertyNames(final AbstractNodeTemplate nodeTemplate) {
        final List<String> propertyNames = new ArrayList<>();
        final NodeList propertyNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
        for (int index = 0; index < propertyNodes.getLength(); index++) {
            final Node propertyNode = propertyNodes.item(index);
            if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                propertyNames.add(propertyNode.getLocalName());
            }
        }
        return propertyNames;
    }

    /**
     * Transforms the given string to a DOM node
     *
     * @param xmlString the xml to transform as String
     * @return a DOM Node representing the given string
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
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

    public static Node string2domQuietly(final String xmlString) {
        try {
            return string2dom(xmlString);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
