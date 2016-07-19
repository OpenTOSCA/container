package org.opentosca.planbuilder.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.fragments.Fragments;
import org.opentosca.planbuilder.handlers.BPELProcessHandler;
import org.opentosca.planbuilder.handlers.BPELTemplateScopeHandler;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.BuildPlan.VariableType;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class NodeInstanceInitializer {

	private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
	private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";

	private BPELProcessHandler bpelProcessHandler;

	private BPELTemplateScopeHandler bpelTemplateScopeHandler;

	private Fragments bpelFragments;

	public NodeInstanceInitializer() throws ParserConfigurationException {
		this.bpelProcessHandler = new BPELProcessHandler();
		this.bpelTemplateScopeHandler = new BPELTemplateScopeHandler();
		this.bpelFragments = new Fragments();
	}

	/**
	 * Appends logic to all NodeTemplate TemplatePlans inside the given plan to
	 * fetch their NodeInstance trough the already set ServiceInstanceVariables
	 * 
	 * @param plan
	 *            a plan
	 * @param propMap
	 *            a PropertyMap holding mappings from NodeTemplates to
	 *            Properties to BPEL Variables
	 * @return true iff adding the logic was successful
	 */
	public boolean initializeNodeInstanceData(BuildPlan plan, PropertyMap propMap) {

		// find serviceInstanceID Variable
		String serviceInstanceIdVarName = null;
		String instanceDataUrlVarName = null;
		for (String mainVariableName : this.bpelProcessHandler.getMainVariableNames(plan)) {
			if (mainVariableName.contains(NodeInstanceInitializer.ServiceInstanceVarKeyword)) {
				serviceInstanceIdVarName = mainVariableName;
			}
			if (mainVariableName.contains(NodeInstanceInitializer.InstanceDataAPIUrlKeyword)) {
				instanceDataUrlVarName = mainVariableName;
			}
		}

		// add XMLSchema Namespace for the logic
		String xsdPrefix = "xsd" + System.currentTimeMillis();
		String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
		this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

		if (serviceInstanceIdVarName == null | instanceDataUrlVarName == null) {
			return false;
		}

		// fetch NodeTemplate TemplatePlans and append logic
		for (TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() == null) {
				// we handle only nodes right now
				continue;
			}
			AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

			// create Response Variable for interaction
			String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
			this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
					new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);

			// create NodeInstanceID Variable for the nodeInstance
			String nodeInstanceIDVarName = "nodeInstanceID";
			this.bpelTemplateScopeHandler.addVariable(nodeInstanceIDVarName, VariableType.TYPE,
					new QName(xsdNamespace, "string", xsdPrefix), templatePlan);

			/* append logic to fetch nodeInstanceID */

			// find nodeInstance with query at instanceDataAPI
			try {
				Node nodeInstanceGETNode = this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(
						instanceDataUrlVarName, instanceDataAPIResponseVarName, nodeTemplate.getId(),
						serviceInstanceIdVarName, true);
				nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstanceGETNode);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// fetch nodeInstanceID from nodeInstance query
			try {
				Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse = this.bpelFragments
						.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode(
								"assignNodeInstanceIDFromInstanceDataAPIResponse" + System.currentTimeMillis(),
								nodeInstanceIDVarName, instanceDataAPIResponseVarName);
				assignNodeInstanceIDFromInstanceDataAPIQueryResponse = templatePlan.getBpelDocument()
						.importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
				templatePlan.getBpelSequencePrePhaseElement()
						.appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// check whether the nodeTemplate has properties, if not, only the
			// nodeInstanceId will suffice
			if (nodeTemplate.getProperties() == null) {
				continue;
			}

			// fetch properties from nodeInstance
			try {
				Node nodeInstancePropertiesGETNode = this.bpelFragments
						.createRESTExtensionGETForNodeInstancePropertiesAsNode(nodeInstanceIDVarName,
								instanceDataAPIResponseVarName);
				nodeInstancePropertiesGETNode = templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode,
						true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}

			// assign bpel variables from the requested properties
			// create mapping from property dom nodes to bpelvariable
			Map<Element, String> element2BpelVarNameMap = new HashMap<Element, String>();
			NodeList propChildNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < propChildNodes.getLength(); index++) {
				if (propChildNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) propChildNodes.item(index);
					// find bpelVariable
					String bpelVarName = propMap.getPropertyMappingMap(nodeTemplate.getId())
							.get(childElement.getLocalName());
					if (bpelVarName != null) {
						element2BpelVarNameMap.put(childElement, bpelVarName);
					}
				}
			}

			try {
				Node assignPropertiesToVariables = this.bpelFragments
						.createAssignFromNodeInstancePropertyToBPELVariableAsNode(
								"assignPropertiesFromResponseToBPELVariable" + System.currentTimeMillis(),
								instanceDataAPIResponseVarName, element2BpelVarNameMap);
				assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables,
						true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}

		}

		return true;
	}

}
