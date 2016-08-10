package org.opentosca.planbuilder.type.plugin.connectsto;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
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
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
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

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

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

	public boolean handle(TemplatePlanContext templateContext) {
		AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
		AbstractNodeTemplate sourceNodeTemplate = relationTemplate.getSource();
		AbstractNodeTemplate targetNodeTemplate = relationTemplate.getTarget();

		// fetch the connectsTo Operation of the source node and it's parameters
		AbstractInterface connectsToIface = null;
		AbstractOperation connectsToOp = null;
		for (AbstractInterface iface : sourceNodeTemplate.getType().getInterfaces()) {
			for (AbstractOperation op : iface.getOperations()) {
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
		Map<AbstractParameter, Variable> param2propertyMapping = new HashMap<AbstractParameter, Variable>();

		for (AbstractParameter param : connectsToOp.getInputParameters()) {
			AbstractNodeTemplate currentNode = targetNodeTemplate;
			while (currentNode != null) {
				Variable property = templateContext.getPropertyVariable(currentNode, param.getName());
				if (property != null) {
					// found property with matching name
					param2propertyMapping.put(param, property);
					break;
				} else {
					currentNode = this.fetchNodeConnectedWithHostedOn(currentNode);
				}
			}
		}

		if (param2propertyMapping.size() != connectsToOp.getInputParameters().size()) {
			LOG.error(
					"Didn't find necessary matchings from param to property. Can't initialize connectsTo relationship");
			return false;
		}

		// TODO HERE YOU HAVE TO HACK FURTHER, NOT FINISHED! Missing: the call
		// to the script (invoker? scriptoperation? may be brutal boi...)
		/*
		 * Assumption: As the Service Invoker only supports WebServices right
		 * now, we'll have to determine here whether to use the script or
		 * invoker plugin.
		 */
		for (AbstractNodeTypeImplementation nodeImpl : sourceNodeTemplate.getImplementations()) {
			// take the first valid impl
			for (AbstractImplementationArtifact ia : nodeImpl.getImplementationArtifacts()) {
				if (ia.getInterfaceName().equals(connectsToIface.getName()) && ia.getOperationName() != null
						&& ia.getOperationName().equals(connectsToOp.getName())) {
					// found valid ia for the operation -> determine script or invoker
					templateContext.executeOperation(sourceNodeTemplate, connectsToOp.getName(), param2propertyMapping);
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
	private AbstractNodeTemplate fetchNodeConnectedWithHostedOn(AbstractNodeTemplate nodeTemplate) {
		for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
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
	public String loadAssignXpathQueryToStringVarFragmentAsString(String assignName, String xpath2Query,
			String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("assignStringVarWithXpath2Query.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
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
	public Node loadAssignXpathQueryToStringVarFragmentAsNode(String assignName, String xpath2Query,
			String stringVarName) throws IOException, SAXException {
		String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query,
				stringVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
