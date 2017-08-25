package org.opentosca.planbuilder.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
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
public class Utils {

	private final static Logger LOG = LoggerFactory.getLogger(Utils.class);

	// these are the baseTypes of the PlanBuilder -> TODO refactor into some
	// kind of baseType-Configuration
	public static final QName TOSCABASETYPE_CONNECTSTO = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ConnectsTo");
	public static final QName TOSCABASETYPE_HOSTEDON = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "HostedOn");
	public static final QName TOSCABASETYPE_DEPLOYEDON = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DeployedOn");
	public static final QName TOSCABASETYPE_DEPENDSON = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "DependsOn");
	public static final QName TOSCABASETYPE_SERVER = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "Server");
	public static final QName TOSCABASETYPE_OS = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes",
			"OperatingSystem");

	// this is a BRUTAL hack for the new nodetypes
	public final static QName ubuntu1404ServerVmNodeType = new QName("http://opentosca.org/nodetypes",
			"Ubuntu-14.04-VM");
	public final static QName raspbianJessieOSNodeType = new QName("http://opentosca.org/nodetypes", "RaspbianJessie");
	public final static QName externalResourceNodeType = new QName("http://opentosca.org/nodetypes",
			"ExternalResource");

	public static Set<AbstractDeploymentArtifact> computeEffectiveDeploymentArtifacts(
			final AbstractNodeTemplate nodeTemplate, final AbstractNodeTypeImplementation nodeImpl) {
		final Set<AbstractDeploymentArtifact> effectiveDAs = new HashSet<>();
		effectiveDAs.addAll(nodeTemplate.getDeploymentArtifacts());
		for (final AbstractDeploymentArtifact da : nodeImpl.getDeploymentArtifacts()) {
			if (!effectiveDAs.contains(da)) {
				effectiveDAs.add(da);
			}
		}

		return effectiveDAs;
	}

	/**
	 * Looks for a childelement with an attribute with the given name and value
	 *
	 * @param element
	 *            the element to look in
	 * @param attributeName
	 *            the name of the attribute
	 * @param attributeValue
	 *            the value of the attribute
	 * @return true if the given element has a child element with an attribute where
	 *         attrname.equals(attributeName) & attr.value(attributeValue), else
	 *         false
	 */
	public static boolean hasChildElementWithAttribute(final Element element, final String attributeName,
			final String attributeValue) {
		if (element == null) {
			return false;
		}
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			final Node child = element.getChildNodes().item(i);
			if ((child.getAttributes().getNamedItem(attributeName) != null)
					&& child.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue)) {
				return true;
			}
		}
		return false;
	}

	public static List<QName> getArtifactTypeHierarchy(final AbstractArtifactTemplate artifactTemplate) {
		List<QName> qnames = new ArrayList<>();

		qnames.add(artifactTemplate.getAbstractArtifactType().getId());

		AbstractArtifactType ref = artifactTemplate.getAbstractArtifactType().getTypeRef();

		while (ref != null) {
			qnames.add(ref.getId());
			ref = ref.getTypeRef();
		}

		return qnames;
	}

	/**
	 * Returns a ordered list of QNames. The order represents the inheritance of
	 * NodeTypes defining the given NodeType. E.g. NodeType "someNodeType" inherits
	 * properties from "someOtherNodeType". The returns list would have
	 * {someNs}someNodeType,{someNs}someOtherNodeType inside, in the exact same
	 * order.
	 *
	 * @param nodeType
	 *            the nodeType to get the hierarchy for
	 * @return a List containing an order of inheritance of NodeTypes for this
	 *         NodeType with itself at the first spot in the list.
	 */
	public static List<QName> getNodeTypeHierarchy(final AbstractNodeType nodeType) {
		Utils.LOG.debug("Beginning calculating NodeType Hierarchy for: " + nodeType.getId().toString());
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
				Utils.LOG.debug("Found referenced NodeType: " + referencedNodeType.getId().toString());
				typeHierarchy.add(referencedNodeType.getId());
				lastFoundNodeType = referencedNodeType;
			}
		}

		return typeHierarchy;
	}

	/**
	 * Returns a ordered list of QNames. The order represents the inheritance of
	 * RelationshipTypes defining the given RelationshipType. E.g. Relationship
	 * "someRelationType" and it inherits properties from "someOtherRelationType".
	 * The returns list would have
	 * {someNs}someRelationType,{someNs}someOtherRelationType inside, in the exact
	 * same order.
	 *
	 * @param definitions
	 *            the Definitions to look in
	 * @param relationshipType
	 *            the RelationshipType to get the hierarchy for
	 * @return a List containing an order of inheritance of RelationshipTypes of the
	 *         given RelationshipType
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
	 * Returns the baseType of the given NodeTemplate
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @return a QName which represents the baseType of the given NodeTemplate
	 */
	public static QName getNodeBaseType(final AbstractNodeTemplate nodeTemplate) {
		Utils.LOG.debug("Beginning search for basetype of: " + nodeTemplate.getId());
		final List<QName> typeHierarchy = Utils.getNodeTypeHierarchy(nodeTemplate.getType());
		for (final QName type : typeHierarchy) {
			Utils.LOG.debug("Checking Type in Hierarchy, type: " + type.toString());
			if (type.equals(Utils.TOSCABASETYPE_SERVER)) {
				return type;
			} else if (type.equals(Utils.TOSCABASETYPE_OS)) {
				return type;
			}
		}
		// FIXME: when there are no basetypes we're screwed
		return typeHierarchy.get(typeHierarchy.size() - 1);
	}

	/**
	 * Returns the baseType of the given RelationshipTemplate
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 * @return a QName representing the baseType of the given RelationshipTemplate
	 */
	public static QName getRelationshipBaseType(final AbstractRelationshipTemplate relationshipTemplate) {
		Utils.LOG.debug("Beginning search for basetype of: " + relationshipTemplate.getId());
		final List<QName> typeHierarchy = Utils
				.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType());
		for (final QName type : typeHierarchy) {
			Utils.LOG.debug("Checking Type QName: " + type.toString());
			if (type.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				return type;
			} else if (type.equals(Utils.TOSCABASETYPE_DEPENDSON)) {
				return type;
			} else if (type.equals(Utils.TOSCABASETYPE_HOSTEDON)) {
				return type;
			} else if (type.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				return type;
			}
		}
		// FIXME: when there are no basetypes we're screwed
		return typeHierarchy.get(typeHierarchy.size() - 1);
	}

	/**
	 * Calculates all Infrastructure Nodes of all Infrastructure Paths originating
	 * from the given NodeTemplate
	 *
	 * @param nodeTemplate
	 *            AbstractNodeTemplate from where the search for Infrstructure Nodes
	 *            begin
	 * @param infrastructureNodes
	 *            a List of AbstractNodeTemplates which represent Infrastructure
	 *            Nodes of the given NodeTemplate
	 * @Info the infrastructureNodes List must be empty
	 */
	public static void getInfrastructureNodes(final AbstractNodeTemplate nodeTemplate,
			final List<AbstractNodeTemplate> infrastructureNodes) {
		Utils.LOG.debug(
				"BaseType of NodeTemplate " + nodeTemplate.getId() + " is " + Utils.getNodeBaseType(nodeTemplate));

		if (org.opentosca.container.core.tosca.convention.Utils
				.isSupportedInfrastructureNodeType(Utils.getNodeBaseType(nodeTemplate))
				|| org.opentosca.container.core.tosca.convention.Utils
						.isSupportedCloudProviderNodeType(Utils.getNodeBaseType(nodeTemplate))) {
			Utils.LOG.debug("Found infrastructure node: " + nodeTemplate.getId());
			infrastructureNodes.add(nodeTemplate);
		}
		for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
			Utils.LOG.debug("Checking if relation is infrastructure edge, relation: " + relation.getId());
			if (Utils.getRelationshipBaseType(relation).equals(Utils.TOSCABASETYPE_DEPENDSON)
					|| Utils.getRelationshipBaseType(relation).equals(Utils.TOSCABASETYPE_HOSTEDON)
					|| Utils.getRelationshipBaseType(relation).equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				Utils.LOG.debug("traversing edge to node: " + relation.getTarget().getId());
				Utils.getInfrastructureNodes(relation.getTarget(), infrastructureNodes);
			}
		}
		Utils.cleanDuplciates(infrastructureNodes);
	}

	/**
	 * Adds InfrastructureNodes of the given RelaitonshipTemplate to the given List
	 * of NodeTemplates
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate to search its InfrastructureNodes
	 * @param infrastructureNodes
	 *            a List of AbstractNodeTemplate where the InfrastructureNodes will
	 *            be added
	 * @param forSource
	 *            whether to search for InfrastructureNodes along the
	 *            SourceInterface or TargetInterface
	 */
	public static void getInfrastructureNodes(final AbstractRelationshipTemplate relationshipTemplate,
			final List<AbstractNodeTemplate> infrastructureNodes, final boolean forSource) {

		if (forSource) {
			Utils.getInfrastructureNodes(relationshipTemplate.getSource(), infrastructureNodes);
		} else {
			Utils.getInfrastructureNodes(relationshipTemplate.getTarget(), infrastructureNodes);
		}

	}

	/**
	 * Removes duplicates from the given List
	 *
	 * @param nodeTemplates
	 *            a List of AbstractNodeTemplate
	 */
	private static void cleanDuplciates(final List<AbstractNodeTemplate> nodeTemplates) {
		final List<AbstractNodeTemplate> list = new ArrayList<>();
		for (final AbstractNodeTemplate template : nodeTemplates) {
			boolean match = false;
			for (final AbstractNodeTemplate template2 : list) {
				if (template.getId().equals(template2.getId()) & (template == template2)) {
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
	 * Removes duplicates from the given List
	 *
	 * @param relationshipTemplates
	 *            a List of AbstractRelationshipTemplate
	 */
	private static void cleanDuplicates(final List<AbstractRelationshipTemplate> relationshipTemplates) {
		final List<AbstractRelationshipTemplate> list = new ArrayList<>();
		for (final AbstractRelationshipTemplate template : relationshipTemplates) {
			boolean match = false;
			for (final AbstractRelationshipTemplate template2 : list) {
				if (template.getId().equals(template2.getId()) & (template == template2)) {
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

	
	public static List<AbstractRelationshipTemplate> getOutgoingInfrastructureEdges(final AbstractNodeTemplate nodeTemplate) {
		List<AbstractRelationshipTemplate> relations = new ArrayList<AbstractRelationshipTemplate>();
		
		for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
			List<QName> types = Utils.getRelationshipTypeHierarchy(relation.getRelationshipType());
			if (types.contains(TOSCABASETYPE_DEPENDSON) | types.contains(TOSCABASETYPE_DEPLOYEDON) | types.contains(TOSCABASETYPE_HOSTEDON)) {
				relations.add(relation);
			}
		}
		
		return relations;
	}
	

	/**
	 * Adds the InfrastructureEdges of the given NodeTemplate to the given List
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @param infrastructureEdges
	 *            a List of AbstractRelationshipTemplate to add the
	 *            InfrastructureEdges to
	 */
	public static void getInfrastructureEdges(final AbstractNodeTemplate nodeTemplate,
			final List<AbstractRelationshipTemplate> infrastructureEdges) {

		// fetch all infrastructureNodes
		final List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
		Utils.getInfrastructureNodes(nodeTemplate, infraNodes);

		// check all outgoing edges on those nodes, if they are infrastructure
		// edges
		for (final AbstractNodeTemplate infraNode : infraNodes) {
			for (final AbstractRelationshipTemplate outgoingEdge : infraNode.getOutgoingRelations()) {

				if (isInfrastructureRelationshipType(outgoingEdge.getType())) {

					infrastructureEdges.add(outgoingEdge);
				}
			}
		}
		Utils.cleanDuplicates(infrastructureEdges);
	}

	public static boolean isInfrastructureRelationshipType(QName relationshipType) {
		if (relationshipType.equals(Utils.TOSCABASETYPE_DEPENDSON) | relationshipType.equals(Utils.TOSCABASETYPE_HOSTEDON) | relationshipType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isCommunicationRelationshipType(QName relationshipType) {
		if(relationshipType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
			return true;
		} else {
			return false;
		}
	}
	

	/**
	 * Returns all NodeTemplates from the given RelationshipTemplate going along all
	 * occuring Relationships using the Target
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 * @param nodes
	 *            a List of AbstractNodeTemplate to add the result to
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
			Utils.getNodesFromRelationToSink(outgoingTemplate, nodes);
		}
		Utils.cleanDuplciates(nodes);
	}

	public static void getNodesFromRelationToSink(final AbstractRelationshipTemplate relationshipTemplate, QName relationshipType, final List<AbstractNodeTemplate> nodes) {
		final AbstractNodeTemplate nodeTemplate = relationshipTemplate.getTarget();
		nodes.add(nodeTemplate);
		for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
			
			if (Utils.getRelationshipTypeHierarchy(outgoingTemplate.getRelationshipType()).contains(relationshipType)) {
				
				Utils.getNodesFromRelationToSink(outgoingTemplate, relationshipType, nodes);
			}
			
		}
		Utils.cleanDuplciates(nodes);
	}
	

	/**
	 * Returns all NodeTemplates from the given NodeTemplate going along the path of
	 * relation following the target interfaces
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @param nodes
	 *            a List of AbstractNodeTemplate to add the result to
	 */
	public static void getNodesFromNodeToSink(final AbstractNodeTemplate nodeTemplate,
			final List<AbstractNodeTemplate> nodes) {
		nodes.add(nodeTemplate);
		for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
			if (outgoingTemplate.getType().equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				// we skip connectTo relations, as they are connecting stacks
				// and
				// make the result even more ambigious
				continue;
			}
			Utils.getNodesFromRelationToSink(outgoingTemplate, nodes);
		}
		Utils.cleanDuplciates(nodes);
	}

	
	public static List<AbstractRelationshipTemplate> getOutgoingRelations(AbstractNodeTemplate nodeTemplate, QName... relationshipTypes) {
		List<AbstractRelationshipTemplate> relations = new ArrayList<AbstractRelationshipTemplate>();
		
		for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
			for (QName relationshipTypeHierarchyMember : Utils.getRelationshipTypeHierarchy(relation.getRelationshipType())) {
				boolean match = false;
				for (QName relationshipType : relationshipTypes) {
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
	
	public static List<AbstractRelationshipTemplate> getIngoingRelations(AbstractNodeTemplate nodeTemplate, QName... relationshipTypes) {
		List<AbstractRelationshipTemplate> relations = new ArrayList<AbstractRelationshipTemplate>();						
		for (AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
			for (QName relationshipTypeHierarchyMember : Utils.getRelationshipTypeHierarchy(relation.getRelationshipType())) {				
				boolean match = false;
				for (QName relationshipType : relationshipTypes) {
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
	
	public static void getNodesFromNodeToSink(final AbstractNodeTemplate nodeTemplate, QName relationshipType, final List<AbstractNodeTemplate> nodes) {
		nodes.add(nodeTemplate);
		for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getOutgoingRelations()) {
			if (Utils.getRelationshipTypeHierarchy(outgoingTemplate.getRelationshipType()).contains(relationshipType)) {
				// we skip connectTo relations, as they are connecting stacks
				// and
				// make the result even more ambigious
				Utils.getNodesFromRelationToSink(outgoingTemplate, nodes);
			}
		}
		Utils.cleanDuplciates(nodes);
	}
	

	/**
	 * Adds the InfrastructureEdges of the given RelationshipTemplate to the given
	 * List
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 * @param infraEdges
	 *            a List of AbstractRelationshipTemplate to add the
	 *            InfrastructureEdges to
	 * @param forSource
	 *            whether to search for InfrastructureEdges along the
	 *            SourceInterface or TargetInterface
	 */
	public static void getInfrastructureEdges(final AbstractRelationshipTemplate relationshipTemplate,
			final List<AbstractRelationshipTemplate> infraEdges, final boolean forSource) {
		if (forSource) {
			Utils.getInfrastructureEdges(relationshipTemplate.getSource(), infraEdges);
		} else {
			Utils.getInfrastructureEdges(relationshipTemplate.getTarget(), infraEdges);
		}
	}

	/**
	 * Returns true if the given QName type denotes to a RelationshipType in the
	 * type hierarchy of the given RelationshipTemplate
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 * @param type
	 *            the Type as a QName to check against
	 * @return true iff the given RelationshipTemplate contains the given type in
	 *         its type hierarchy
	 */
	public static boolean checkForTypeInHierarchy(final AbstractRelationshipTemplate relationshipTemplate,
			final QName type) {
		final List<QName> typeHierarchy = Utils
				.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType());
		// as somehow contains won't work here, we must cycle trough
		for (final QName qname : typeHierarchy) {
			if (qname.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given QName type denotes to a NodeType in the type
	 * hierarchy of the given NodeTemplate
	 *
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @param type
	 *            the Type as a QName to check against
	 * @return true iff the given NodeTemplate contains the given type in its type
	 *         hierarchy
	 */
	public static boolean checkForTypeInHierarchy(final AbstractNodeTemplate nodeTemplate, final QName type) {
		final List<QName> typeHierarchy = Utils.getNodeTypeHierarchy(nodeTemplate.getType());
		// as somehow contains won't work here, we must cycle trough
		for (final QName qname : typeHierarchy) {
			if (qname.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Transforms the given string to a DOM node
	 *
	 * @param xmlString
	 *            the xml to transform as String
	 * @return a DOM Node representing the given string
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Node string2dom(final String xmlString)
			throws ParserConfigurationException, SAXException, IOException {

		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		final InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));
		final Document doc = docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Checks whether the property of the given variable is empty in the
	 * TopologyTemplate
	 *
	 * @param variable
	 *            a property variable (var must belong to a topology template
	 *            property) to check
	 * @param context
	 *            the context the variable belongs to
	 * @return true iff the content of the given variable is empty in the topology
	 *         template property
	 */
	public static boolean isVariableValueEmpty(final Variable variable, final TemplatePlanContext context) {
		final String content = Utils.getVariableContent(variable, context);

		if ((content == null) || content.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getVariableContent(final Variable variable, final TemplatePlanContext context) {
		// check whether the property is empty --> external parameter
		for (final AbstractNodeTemplate node : context.getNodeTemplates()) {
			if (node.getId().equals(variable.getTemplateId())) {
				if (node.getProperties() == null) {
					continue;
				}
				final NodeList children = node.getProperties().getDOMElement().getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					final Node child = children.item(i);
					if (child.getNodeType() != 1) {
						continue;
					}
					if (variable.getName().contains(child.getLocalName())) {
						// check if content is empty
						return children.item(i).getTextContent();
					}
				}
			}
		}

		for (final AbstractRelationshipTemplate relation : context.getRelationshipTemplates()) {
			if (relation.getId().equals(variable.getTemplateId())) {
				final NodeList children = relation.getProperties().getDOMElement().getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (variable.getName().contains(children.item(i).getLocalName())) {
						// check if content is empty
						return children.item(i).getTextContent();
					}
				}
			}
		}
		return null;
	}

	public static void getNodesFromNodeToSource(final AbstractNodeTemplate nodeTemplate,
			final List<AbstractNodeTemplate> nodes) {
		nodes.add(nodeTemplate);
		for (final AbstractRelationshipTemplate ingoingTemplate : nodeTemplate.getIngoingRelations()) {
			if (ingoingTemplate.getType().equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				// we skip connectTo relations, as they are connecting stacks
				// and
				// make the result even more ambigious
				continue;
			}
			Utils.getNodesFromRelationToSources(ingoingTemplate, nodes);
		}
		Utils.cleanDuplciates(nodes);
	}

	private static void getNodesFromRelationToSources(final AbstractRelationshipTemplate ingoingTemplate,
			final List<AbstractNodeTemplate> nodes) {
		final AbstractNodeTemplate nodeTemplate = ingoingTemplate.getSource();
		nodes.add(nodeTemplate);
		for (final AbstractRelationshipTemplate outgoingTemplate : nodeTemplate.getIngoingRelations()) {
			if (outgoingTemplate.getType().equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				continue;
			}
			Utils.getNodesFromRelationToSources(outgoingTemplate, nodes);
		}
		Utils.cleanDuplciates(nodes);
	}
}
