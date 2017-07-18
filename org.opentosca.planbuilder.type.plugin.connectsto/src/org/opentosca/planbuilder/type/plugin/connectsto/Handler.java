package org.opentosca.planbuilder.type.plugin.connectsto;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Handler {

	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);

	private final DocumentBuilderFactory docFactory;
	private final DocumentBuilder docBuilder;

	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException
	 *             is thrown when initializing the DOM Parsers fails
	 */
	public Handler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public boolean handle(final TemplatePlanContext templateContext) {
		final AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
		final AbstractNodeTemplate sourceNodeTemplate = relationTemplate.getSource();
		final AbstractNodeTemplate targetNodeTemplate = relationTemplate.getTarget();

		// if the target has connectTo we execute it
		if (this.hasOperation(targetNodeTemplate, "connectTo")) {
			// if we can stop and start the node, stop it
			if (this.hasOperation(targetNodeTemplate, "stop") & this.hasOperation(targetNodeTemplate, "start")) {
				String ifaceName = this.getInterface(targetNodeTemplate, "stop");
				templateContext.executeOperation(targetNodeTemplate, ifaceName, "stop", null);
			}

			// connectTo
			this.executeConnectsTo(templateContext, targetNodeTemplate, sourceNodeTemplate);

			// start the node again
			if (this.hasOperation(targetNodeTemplate, "stop") & this.hasOperation(targetNodeTemplate, "start")) {
				templateContext.executeOperation(targetNodeTemplate, this.getInterface(targetNodeTemplate, "start"),
						"start", null);
			}
		}

		// if the source has connectTo we execute it
		if (this.hasOperation(sourceNodeTemplate, "connectTo")) {

			// if we can stop and start the node, stop it
			if (this.hasOperation(sourceNodeTemplate, "stop") & this.hasOperation(sourceNodeTemplate, "start")) {
				templateContext.executeOperation(sourceNodeTemplate, this.getInterface(sourceNodeTemplate, "stop"),
						"stop", null);
			}

			this.executeConnectsTo(templateContext, sourceNodeTemplate, targetNodeTemplate);

			// start the node again
			if (this.hasOperation(sourceNodeTemplate, "stop") & this.hasOperation(sourceNodeTemplate, "start")) {
				templateContext.executeOperation(sourceNodeTemplate, this.getInterface(sourceNodeTemplate, "start"),
						"start", null);
			}
		}

		return true;
	}

	private boolean hasOperation(final AbstractNodeTemplate nodeTemplate, final String operationName) {
		for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
			for (final AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(operationName)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getInterface(final AbstractNodeTemplate nodeTemplate, final String operationName) {
		for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
			for (final AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(operationName)) {
					return iface.getName();
				}
			}
		}
		return null;
	}

	/**
	 * Executes the connectTo operation on the given connectToNode NodeTemplate,
	 * the parameters for the operation will be searched starting from the given
	 * parametersRootNode Node Template
	 *
	 * E.g.: For a MQTT Client to connect to a MQTT topic it uses the properties
	 * from the topology stack given by the topic itself. These properties are
	 * then mapped to the parameters of the MQTT client connectTo operation.
	 *
	 * @param templateContext
	 *            the context of this operation call
	 * @param connectToNode
	 *            a Node Template with a connectTo operation
	 * @param parametersRootNode
	 *            a Node Template, which should be used as the starting node for
	 *            parameter search
	 */
	private boolean executeConnectsTo(final TemplatePlanContext templateContext,
			final AbstractNodeTemplate connectToNode, final AbstractNodeTemplate parametersRootNode) {
		// fetch the connectsTo Operation of the source node and it's parameters
		AbstractInterface connectsToIface = null;
		AbstractOperation connectsToOp = null;
		for (final AbstractInterface iface : connectToNode.getType().getInterfaces()) {
			for (final AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals("connectTo")) {
					connectsToIface = iface;
					connectsToOp = op;
					break;
				}
			}
			if (connectsToOp != null) {
				break;
			}
		}

		// find properties that match the params on the target nodes' stack
		final Map<AbstractParameter, Variable> param2propertyMapping = new HashMap<>();

		for (final AbstractParameter param : connectsToOp.getInputParameters()) {
			boolean ambiParam = false;
			if (org.opentosca.container.core.tosca.convention.Utils
					.isSupportedVirtualMachineIPProperty(param.getName())) {
				ambiParam = true;
			}

			if (!ambiParam) {
				AbstractNodeTemplate currentNode = parametersRootNode;
				while (currentNode != null) {
					final Variable property = templateContext.getPropertyVariable(currentNode, param.getName());
					if (property != null) {
						// found property with matching name
						param2propertyMapping.put(param, property);
						break;
					} else {
						currentNode = this.fetchNodeConnectedWithHostedOn(currentNode);
					}
				}
			} else {

				for (final String paramName : org.opentosca.container.core.tosca.convention.Utils
						.getSupportedVirtualMachineIPPropertyNames()) {
					boolean found = false;
					AbstractNodeTemplate currentNode = parametersRootNode;
					while (currentNode != null) {
						final Variable property = templateContext.getPropertyVariable(currentNode, paramName);
						if (property != null) {
							// found property with matching name
							param2propertyMapping.put(param, property);
							found = true;
							break;
						} else {
							currentNode = this.fetchNodeConnectedWithHostedOn(currentNode);
						}
					}
					if (found) {
						break;
					}
				}
			}
		}

		if (param2propertyMapping.size() != connectsToOp.getInputParameters().size()) {
			Handler.LOG.error(
					"Didn't find necessary matchings from param to property. Can't initialize connectsTo relationship");
			return false;
		}

		for (final AbstractNodeTypeImplementation nodeImpl : connectToNode.getImplementations()) {
			for (final AbstractImplementationArtifact ia : nodeImpl.getImplementationArtifacts()) {
				if (ia.getInterfaceName().equals(connectsToIface.getName()) && (ia.getOperationName() != null)
						&& ia.getOperationName().equals(connectsToOp.getName())) {
					templateContext.executeOperation(connectToNode, connectsToIface.getName(), connectsToOp.getName(), param2propertyMapping);
				}
			}
		}

		return true;

	}

	/**
	 * Returns the first node found connected trough a hostedOn relation
	 *
	 * @param nodeTemplate
	 *            the node which is a possible source of an hostedOn relation
	 * @return an AbstractNodeTemplate which is a target of an hostedOn
	 *         relation. Null if the given nodeTemplate isn't connected to as a
	 *         source to a hostedOn relation
	 */
	private AbstractNodeTemplate fetchNodeConnectedWithHostedOn(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
			if (Utils.getRelationshipTypeHierarchy(relation.getRelationshipType())
					.contains(Utils.TOSCABASETYPE_HOSTEDON)) {
				return relation.getTarget();
			}
		}

		return null;
	}

	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName
	 *            the name of the BPEL assign
	 * @param xpath2Query
	 *            the csarEntryPoint XPath query
	 * @param stringVarName
	 *            the variable to load the queries results into
	 * @return a String containing a BPEL Assign element
	 * @throws IOException
	 *             is thrown when reading the BPEL fragment form the resources
	 *             fails
	 */
	public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
			final String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("assignStringVarWithXpath2Query.xml");
		final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{xpath2query}", xpath2Query);
		template = template.replace("{stringVarName}", stringVarName);
		return template;
	}

	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName
	 *            the name of the BPEL assign
	 * @param csarEntryXpathQuery
	 *            the csarEntryPoint XPath query
	 * @param stringVarName
	 *            the variable to load the queries results into
	 * @return a DOM Node representing a BPEL assign element
	 * @throws IOException
	 *             is thrown when loading internal bpel fragments fails
	 * @throws SAXException
	 *             is thrown when parsing internal format into DOM fails
	 */
	public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
			final String stringVarName) throws IOException, SAXException {
		final String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query,
				stringVarName);
		final InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		final Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
