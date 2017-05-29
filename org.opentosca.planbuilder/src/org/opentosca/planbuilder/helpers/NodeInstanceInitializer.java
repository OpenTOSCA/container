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
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TOSCAPlan.VariableType;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
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
	private static final String InstanceIDVarKeyword = "InstanceURL";
	
	private BPELProcessHandler bpelProcessHandler;
	
	private BPELTemplateScopeHandler bpelTemplateScopeHandler;
	
	private Fragments bpelFragments;
	
	
	public NodeInstanceInitializer() throws ParserConfigurationException {
		this.bpelProcessHandler = new BPELProcessHandler();
		this.bpelTemplateScopeHandler = new BPELTemplateScopeHandler();
		this.bpelFragments = new Fragments();
	}
	
	/**
	 * Adds logic to fetch property data from the instanceDataAPI with the
	 * nodeInstanceID variable. The property data is then assigned to
	 * appropriate BPEL variables of the given plan.
	 * 
	 * @param plan a plan containing templatePlans with set nodeInstanceID
	 *            variables
	 * @param propMap a Mapping from NodeTemplate Properties to BPEL Variables
	 * @return true if adding logic described above was successful
	 */
	public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(TOSCAPlan plan, PropertyMap propMap) {
		boolean check = true;
		for (TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null && templatePlan.getNodeTemplate().getProperties().getDOMElement() != null) {
				check &= this.addPropertyVariableUpdateBasedOnNodeInstanceID(templatePlan, propMap);
			}
		}
		return check;
	}
	
	public String findInstanceIdVarName(TemplateBuildPlan templatePlan) {
		String templateId = "";
		
		if (templatePlan.getNodeTemplate() != null) {
			templateId = templatePlan.getNodeTemplate().getId();
		} else {
			templateId = templatePlan.getRelationshipTemplate().getId();
		}
		return this.findInstanceIdVarName(templatePlan.getBuildPlan(), templateId);
	}
	
	public String findInstanceIdVarName(TOSCAPlan plan, String templateId) {
		for (String varName : this.bpelProcessHandler.getMainVariableNames(plan)) {
			// FIXME weak check
			if ((varName.startsWith("node"+ InstanceIDVarKeyword) | varName.startsWith("relation"+ InstanceIDVarKeyword)) & varName.contains(templateId)) {
				return varName;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds logic to fetch property data from the instanceDataAPI with the
	 * nodeInstanceID variable. The property data is then assigned to
	 * appropriate BPEL Variables of the given templatePlan.
	 * 
	 * @param templatePlan a TemplatePlan of a NodeTemplate that has properties
	 * @param propMap a Mapping from NodeTemplate Properties to BPEL Variables
	 * @return true if adding logic described above was successful
	 */
	public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(TemplateBuildPlan templatePlan, PropertyMap propMap) {
		// check if everything is available
		if (templatePlan.getNodeTemplate() == null) {
			return false;
		}
		
		if (templatePlan.getNodeTemplate().getProperties() == null) {
			return false;
		}
		
		if (this.findInstanceIdVarName(templatePlan) == null) {
			return false;
		}
		
		String instanceIdVarName = this.findInstanceIdVarName(templatePlan);
		
		AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
		// add XMLSchema Namespace for the logic
		String xsdPrefix = "xsd" + System.currentTimeMillis();
		String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
		this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
		// create Response Variable for interaction
		String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
		this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
		
		// fetch properties from nodeInstance
		try {
			Node nodeInstancePropertiesGETNode = this.bpelFragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(instanceIdVarName, instanceDataAPIResponseVarName);
			nodeInstancePropertiesGETNode = templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
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
				String bpelVarName = propMap.getPropertyMappingMap(nodeTemplate.getId()).get(childElement.getLocalName());
				if (bpelVarName != null) {
					element2BpelVarNameMap.put(childElement, bpelVarName);
				}
			}
		}
		
		try {
			Node assignPropertiesToVariables = this.bpelFragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable" + System.currentTimeMillis(), instanceDataAPIResponseVarName, element2BpelVarNameMap);
			assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
			templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Adds a NodeInstanceID Variable to each TemplatePlan inside the given Plan
	 * 
	 * @param plan a plan with TemplatePlans
	 * @return
	 */
	public boolean addNodeInstanceIDVarToTemplatePlans(TOSCAPlan plan) {
		boolean check = true;
		for (TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			check &= this.addInstanceIDVarToTemplatePlan(templatePlan);
		}
		return check;
	}
	
	/**
	 * Adds a NodeInstanceID Variable to the given TemplatePlan
	 * 
	 * @param templatePlan a TemplatePlan
	 * @return true iff adding a NodeInstanceID Var was successful
	 */
	public boolean addInstanceIDVarToTemplatePlan(TemplateBuildPlan templatePlan) {
		String xsdPrefix = "xsd" + System.currentTimeMillis();
		String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
		
		this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
		
		String templateId = "";
		String prefix = "";
		
		if (templatePlan.getNodeTemplate() != null) {
			templateId = templatePlan.getNodeTemplate().getId();
			prefix = "node";
		} else {
			templateId = templatePlan.getRelationshipTemplate().getId();
			prefix = "relationship";
		}
		
		String instanceIdVarName = prefix + "InstanceURL_" + templateId + "_" + System.currentTimeMillis();
		
		return this.bpelProcessHandler.addVariable(instanceIdVarName, VariableType.TYPE, new QName(xsdNamespace, "string", xsdPrefix), templatePlan.getBuildPlan());
		
	}
	
	public boolean addNodeInstanceFindLogic(TOSCAPlan plan) {
		boolean check = true;
		
		for (TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null) {
				check &= this.addInstanceFindLogic(templatePlan, ServiceInstanceVarKeyword, InstanceDataAPIUrlKeyword);
			}
		}
		
		return check;
	}
	
	/**
	 * Fetches the correct nodeInstanceID link for the given TemplatePlan and
	 * sets the value inside a NodeInstanceID bpel variable
	 * 
	 * @param templatePlan a templatePlan with set variable with name
	 *            NodeInstanceID
	 * @param serviceInstanceIdVarName the name of the variable holding the url
	 *            to the serviceInstance
	 * @param instanceDataUrlVarName the name of the variable holding the url to
	 *            the instanceDataAPI
	 * @return
	 */
	public boolean addInstanceFindLogic(TemplateBuildPlan templatePlan, String serviceInstanceIdVarName, String instanceDataUrlVarName) {
		// add XML Schema Namespace for the logic
		String xsdPrefix = "xsd" + System.currentTimeMillis();
		String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
		this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
		// create Response Variable for interaction
		String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
		this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
		// find nodeInstance with query at instanceDataAPI
		try {
			Node nodeInstanceGETNode = this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(instanceDataUrlVarName, instanceDataAPIResponseVarName, new QName(templatePlan.getBuildPlan().getServiceTemplate().getNamespaceURI(), templatePlan.getNodeTemplate().getId()), serviceInstanceIdVarName, true);
			nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
			templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstanceGETNode);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String instanceIDVarName = this.findInstanceIdVarName(templatePlan);
		
		// fetch nodeInstanceID from nodeInstance query
		try {
			Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse = this.bpelFragments.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode("assignNodeInstanceIDFromInstanceDataAPIResponse" + System.currentTimeMillis(), instanceIDVarName, instanceDataAPIResponseVarName);
			assignNodeInstanceIDFromInstanceDataAPIQueryResponse = templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
			templatePlan.getBpelSequencePrePhaseElement().appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Appends logic to all NodeTemplate TemplatePlans inside the given plan to
	 * fetch their NodeInstance trough the already set ServiceInstanceVariables
	 * 
	 * @param plan a plan
	 * @param propMap a PropertyMap holding mappings from NodeTemplates to
	 *            Properties to BPEL Variables
	 * @return true iff adding the logic was successful
	 */
	public boolean initializeNodeInstanceData(TOSCAPlan plan, PropertyMap propMap) {
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
			this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
			
			// create NodeInstanceID Variable for the nodeInstance
			String nodeInstanceIDVarName = "nodeInstanceID";
			this.bpelTemplateScopeHandler.addVariable(nodeInstanceIDVarName, VariableType.TYPE, new QName(xsdNamespace, "string", xsdPrefix), templatePlan);
			
			/* append logic to fetch nodeInstanceID */
			
			// find nodeInstance with query at instanceDataAPI
			try {
				Node nodeInstanceGETNode = this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(instanceDataUrlVarName, instanceDataAPIResponseVarName, new QName(plan.getServiceTemplate().getNamespaceURI(), nodeTemplate.getId()), serviceInstanceIdVarName, true);
				nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstanceGETNode);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// fetch nodeInstanceID from nodeInstance query
			try {
				Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse = this.bpelFragments.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode("assignNodeInstanceIDFromInstanceDataAPIResponse" + System.currentTimeMillis(), nodeInstanceIDVarName, instanceDataAPIResponseVarName);
				assignNodeInstanceIDFromInstanceDataAPIQueryResponse = templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
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
				Node nodeInstancePropertiesGETNode = this.bpelFragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(nodeInstanceIDVarName, instanceDataAPIResponseVarName);
				nodeInstancePropertiesGETNode = templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
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
					String bpelVarName = propMap.getPropertyMappingMap(nodeTemplate.getId()).get(childElement.getLocalName());
					if (bpelVarName != null) {
						element2BpelVarNameMap.put(childElement, bpelVarName);
					}
				}
			}
			
			try {
				Node assignPropertiesToVariables = this.bpelFragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable" + System.currentTimeMillis(), instanceDataAPIResponseVarName, element2BpelVarNameMap);
				assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
		}
		
		return true;
	}
	
	public boolean addIfNullAbortCheck(TOSCAPlan plan, PropertyMap propMap) {
		boolean check = true;
		for (TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null) {
				check &= this.addIfNullAbortCheck(templatePlan, propMap);
			}
		}
		return check;
	}
	
	public boolean addIfNullAbortCheck(TemplateBuildPlan templatePlan, PropertyMap propMap) {
		
		for (String propLocalName : propMap.getPropertyMappingMap(templatePlan.getNodeTemplate().getId()).keySet()) {
			String bpelVarName = propMap.getPropertyMappingMap(templatePlan.getNodeTemplate().getId()).get(propLocalName);
			// as the variables are there and only possibly empty we just check
			// the string inside
			String xpathQuery = "string-length(normalize-space($" + bpelVarName + ")) = 0";
			QName propertyEmptyFault = new QName("http://opentosca.org/plans/faults", "PropertyValueEmptyFault");
			try {
				Node bpelIf = this.bpelFragments.generateBPELIfTrueThrowFaultAsNode(xpathQuery, propertyEmptyFault);
				bpelIf = templatePlan.getBpelDocument().importNode(bpelIf, true);
				templatePlan.getBpelSequencePrePhaseElement().appendChild(bpelIf);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
}
