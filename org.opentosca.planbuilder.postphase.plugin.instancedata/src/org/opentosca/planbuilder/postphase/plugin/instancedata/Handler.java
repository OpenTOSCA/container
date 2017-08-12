package org.opentosca.planbuilder.postphase.plugin.instancedata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.SourceVersion;
import javax.swing.text.Utilities;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.planbuilder.fragments.Fragments.Util;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all logic to append BPEL code which updates the
 * InstanceData of a NodeTemplate
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {
	
	private Fragments fragments;
	
	private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
	private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
	private XPathFactory xPathfactory = XPathFactory.newInstance();;
	
	
	public Handler() {
		
		try {
			this.fragments = new Fragments();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private String getServiceInstanceVarName(TemplatePlanContext context) {
		// check whether main sequence already contains service instance calls
		// to container API
		List<String> mainVarNames = context.getMainVariableNames();
		String serviceInstanceVarName = null;
		String instanceDataUrlVarName = null;
		for (String varName : mainVarNames) {
			// pretty lame but should work
			if (varName.contains(Handler.ServiceInstanceVarKeyword)) {
				serviceInstanceVarName = varName;
			}
			if (varName.contains(Handler.InstanceDataAPIUrlKeyword)) {
				instanceDataUrlVarName = varName;
			}
		}
		
		// if at least one is null we need to init the whole
		
		if (instanceDataUrlVarName == null) {
			return null;
		}
		
		if (serviceInstanceVarName == null) {
			return null;
		}
		return serviceInstanceVarName;
	}
	
	private String createRESTResponseVar(TemplatePlanContext context) {
		String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
		QName restCallResponseDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
		if (!context.addGlobalVariable(restCallResponseVarName, TOSCAPlan.VariableType.TYPE, restCallResponseDeclId)) {
			return null;
		}
		return restCallResponseVarName;
	}
	
	private String createStateVar(TemplatePlanContext context, String templateId) {
		// create state variable inside scope
		String stateVarName = templateId + "_state_" + context.getIdForNames();
		QName stringTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(stateVarName, TOSCAPlan.VariableType.TYPE, stringTypeDeclId)) {
			return null;
		}
		
		return stateVarName;
	}
	
	private String findInstanceVar(TemplatePlanContext context, String templateId, boolean isNode) {
		String instanceURLVarName = ((isNode) ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
		for (String varName : context.getMainVariableNames()) {
			if (varName.contains(instanceURLVarName)) {
				return varName;
			}
		}
		return null;
	}
	
	private String createInstanceVar(TemplatePlanContext context, String templateId) {
		String instanceURLVarName = ((context.getRelationshipTemplate() == null) ? "node" : "relationship") + "InstanceURL_" + templateId + "_" + context.getIdForNames();
		QName stringTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(instanceURLVarName, TOSCAPlan.VariableType.TYPE, stringTypeDeclId)) {
			return null;
		}
		
		return instanceURLVarName;
	}
	
	
	public boolean handleTerminate(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
boolean hasProps = this.checkProperties(nodeTemplate.getProperties());
		
		String serviceInstanceVarName = this.getServiceInstanceVarName(context);
		if (serviceInstanceVarName == null) {
			return false;
		}
		
		/*
		 * Pre Phase code
		 */
		
		// create variable for all responses
		String restCallResponseVarName = this.createRESTResponseVar(context);
		
		if (restCallResponseVarName == null) {
			return false;
		}
		
		// create state variable inside scope
		String stateVarName = this.createStateVar(context, context.getNodeTemplate().getId());
		
		if (stateVarName == null) {
			return false;
		}
		
		
		String nodeInstanceURLVarName = "";
		
		if (this.findInstanceVar(context, context.getNodeTemplate().getId(), true) == null) {
			// generate String var for nodeInstance URL
			nodeInstanceURLVarName = this.createInstanceVar(context, context.getNodeTemplate().getId());
		} else {
			nodeInstanceURLVarName = this.findInstanceVar(context, context.getNodeTemplate().getId(), true);
		}
		
		if (nodeInstanceURLVarName == null) {
			return false;
		}
		
	
		
		
		
		// we'll use this later when we determine that the handle Node doesn't
		// have lifecycle operations. Without this check all nodes without
		// lifecycle (or cloud prov operations) will be in an uninstalled state
		String lastSetState = "uninstalled";
		
		/*
		 * Prov Phase code
		 */
		
		// fetch all assigns that assign an invoke async operation request
		
		Element provisioningPhaseElement = context.getProvisioningPhaseElement();
		List<Element> assignContentElements = this.fetchInvokerCallAssigns(provisioningPhaseElement);
		
		// for each assign element we fetch the operation name, determine the
		// pre and post states, and append the pre state before the found assign
		// and the post state after the receive of the invoker iteraction
		for (Element assignContentElement : assignContentElements) {
			
			// fetch operationName from literal contents
			String operationName = this.fetchOperationName(assignContentElement);
			// determine pre and post state for operation
			String preState = InstanceStates.getOperationPreState(operationName);
			String postState = InstanceStates.getOperationPostState(operationName);
			
			if (preState != null) {
				
				try {
					
					// assign prestate to state variable
					org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeStateFor_" + operationName + "_" + System.currentTimeMillis(), "string('" + preState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					lastSetState = preState;
					
					// assign the state before the assign of the invoker request
					// is made
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
					bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);
					
					// create REST Put activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
					Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
					extActiv = context.importNode(extActiv);
					
					// send the state before the assign of the invoker request
					// is made
					bpelAssignNode.getParentNode().insertBefore(extActiv, bpelAssignNode);
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
			if (postState != null) {
				try {
					// create state assign activity
					org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeState_" + operationName + "_" + System.currentTimeMillis(), "string('" + postState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					
					lastSetState = postState;
					
					/*
					 * assign the state after the receiving the response of the
					 */
					
					// fetch assign node
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
					
					// fetch the variable name which is used as request body
					String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);
					
					// from the assign element search for the receive element
					// that is witing for the response
					Element invokerReceiveElement = this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);
					
					// insert assign after the receive
					assignNode = invokerReceiveElement.getParentNode().insertBefore(assignNode, invokerReceiveElement.getNextSibling());
					
					// create PUT activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
					Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
					extActiv = context.importNode(extActiv);
					
					// insert REST call after the assign
					invokerReceiveElement.getParentNode().insertBefore(extActiv, assignNode.getNextSibling());
					
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		// needs property update only if the node has properties
		if (hasProps) {
			// make a GET on the nodeInstance properties
			
			try {
				// fetch properties
				Node nodeInstancePropsGETNode = this.fragments.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
				nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
				context.getPostPhaseElement().appendChild(nodeInstancePropsGETNode);
			} catch (SAXException e1) {
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
			// assign the values from the property variables into REST/HTTP
			// Request
			// and send
			// first build a mapping from property variable names to dom element
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context, nodeTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName, propertyVarNameToDOMMapping);
				assignNode = context.importNode(assignNode);
				context.getPostPhaseElement().appendChild(assignNode);
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			// generate BPEL4RESTLight PUT request to update the instance data
			try {
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName, nodeInstanceURLVarName);
				bpel4restPUTNode = context.importNode(bpel4restPUTNode);
				context.getPostPhaseElement().appendChild(bpel4restPUTNode);
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
	
	/**
	 * Appends BPEL Code that updates InstanceData for the given NodeTemplate.
	 * Needs initialization code on the global level in the plan. This will be
	 * checked and appended if needed.
	 *
	 * @param context the TemplateContext of the NodeTemplate
	 * @param nodeTemplate the NodeTemplate to handle
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handleBuild(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		boolean hasProps = this.checkProperties(nodeTemplate.getProperties());
		
		String serviceInstanceVarName = this.getServiceInstanceVarName(context);
		if (serviceInstanceVarName == null) {
			return false;
		}
		
		/*
		 * Pre Phase code
		 */
		
		// create variable for all responses
		String restCallResponseVarName = this.createRESTResponseVar(context);
		
		if (restCallResponseVarName == null) {
			return false;
		}
		
		// create state variable inside scope
		String stateVarName = this.createStateVar(context, context.getNodeTemplate().getId());
		
		if (stateVarName == null) {
			return false;
		}
		
		/*
		 * (i) append bpel code to create the nodeInstance (ii) append bpel code
		 * to fetch nodeInstanceURL
		 */
		
		try {
			// create bpel extension activity and append
			String bpelString = this.fragments.generateBPEL4RESTLightNodeInstancePOST(serviceInstanceVarName, context.getNodeTemplate().getId(), restCallResponseVarName);
			Node createNodeInstanceExActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			createNodeInstanceExActiv = context.importNode(createNodeInstanceExActiv);
			context.getPrePhaseElement().appendChild(createNodeInstanceExActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		String nodeInstanceURLVarName = "";
		
		if (this.findInstanceVar(context, context.getNodeTemplate().getId(), true) == null) {
			// generate String var for nodeInstance URL
			nodeInstanceURLVarName = this.createInstanceVar(context, context.getNodeTemplate().getId());
		} else {
			nodeInstanceURLVarName = this.findInstanceVar(context, context.getNodeTemplate().getId(), true);
		}
		
		if (nodeInstanceURLVarName == null) {
			return false;
		}
		
		try {
			// save nodeInstance url from response
			String bpelString = this.fragments.generateAssignFromNodeInstancePOSTResponseToStringVar(nodeInstanceURLVarName, restCallResponseVarName);
			Node assignNodeInstanceUrl = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			assignNodeInstanceUrl = context.importNode(assignNodeInstanceUrl);
			context.getPrePhaseElement().appendChild(assignNodeInstanceUrl);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		try {
			// update state variable to uninstalled
			org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignInitNodeState" + System.currentTimeMillis(), "string('uninstalled')", stateVarName);
			assignNode = context.importNode(assignNode);
			context.getPrePhaseElement().appendChild(assignNode);
			
			// send state to api
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
			Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			extActiv = context.importNode(extActiv);
			context.getPrePhaseElement().appendChild(extActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		// we'll use this later when we determine that the handle Node doesn't
		// have lifecycle operations. Without this check all nodes without
		// lifecycle (or cloud prov operations) will be in an uninstalled state
		String lastSetState = "uninstalled";
		
		/*
		 * Prov Phase code
		 */
		
		// fetch all assigns that assign an invoke async operation request
		
		Element provisioningPhaseElement = context.getProvisioningPhaseElement();
		List<Element> assignContentElements = this.fetchInvokerCallAssigns(provisioningPhaseElement);
		
		// for each assign element we fetch the operation name, determine the
		// pre and post states, and append the pre state before the found assign
		// and the post state after the receive of the invoker iteraction
		for (Element assignContentElement : assignContentElements) {
			
			// fetch operationName from literal contents
			String operationName = this.fetchOperationName(assignContentElement);
			// determine pre and post state for operation
			String preState = InstanceStates.getOperationPreState(operationName);
			String postState = InstanceStates.getOperationPostState(operationName);
			
			if (preState != null) {
				
				try {
					
					// assign prestate to state variable
					org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeStateFor_" + operationName + "_" + System.currentTimeMillis(), "string('" + preState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					lastSetState = preState;
					
					// assign the state before the assign of the invoker request
					// is made
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
					bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);
					
					// create REST Put activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
					Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
					extActiv = context.importNode(extActiv);
					
					// send the state before the assign of the invoker request
					// is made
					bpelAssignNode.getParentNode().insertBefore(extActiv, bpelAssignNode);
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
			if (postState != null) {
				try {
					// create state assign activity
					org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignNodeState_" + operationName + "_" + System.currentTimeMillis(), "string('" + postState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					
					lastSetState = postState;
					
					/*
					 * assign the state after the receiving the response of the
					 */
					
					// fetch assign node
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode().getParentNode();
					
					// fetch the variable name which is used as request body
					String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);
					
					// from the assign element search for the receive element
					// that is witing for the response
					Element invokerReceiveElement = this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);
					
					// insert assign after the receive
					assignNode = invokerReceiveElement.getParentNode().insertBefore(assignNode, invokerReceiveElement.getNextSibling());
					
					// create PUT activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
					Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
					extActiv = context.importNode(extActiv);
					
					// insert REST call after the assign
					invokerReceiveElement.getParentNode().insertBefore(extActiv, assignNode.getNextSibling());
					
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		/*
		 * Post Phase code
		 */
		
		if (lastSetState.equals("uninstalled")) {
			try {
				// set state
				org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
				Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignFinalNodeState" + System.currentTimeMillis(), "string('" + InstanceStates.getNextStableOperationState(lastSetState) + "')", stateVarName);
				assignNode = context.importNode(assignNode);
				
				// create PUT activity
				String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName, stateVarName);
				Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
				extActiv = context.importNode(extActiv);
				
				context.getPostPhaseElement().appendChild(assignNode);
				context.getPostPhaseElement().appendChild(extActiv);
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		// needs property update only if the node has properties
		if (hasProps) {
			// make a GET on the nodeInstance properties
			
			try {
				// fetch properties
				Node nodeInstancePropsGETNode = this.fragments.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
				nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
				context.getPostPhaseElement().appendChild(nodeInstancePropsGETNode);
			} catch (SAXException e1) {
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
			// assign the values from the property variables into REST/HTTP
			// Request
			// and send
			// first build a mapping from property variable names to dom element
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context, nodeTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName, propertyVarNameToDOMMapping);
				assignNode = context.importNode(assignNode);
				context.getPostPhaseElement().appendChild(assignNode);
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			// generate BPEL4RESTLight PUT request to update the instance data
			try {
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName, nodeInstanceURLVarName);
				bpel4restPUTNode = context.importNode(bpel4restPUTNode);
				context.getPostPhaseElement().appendChild(bpel4restPUTNode);
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
	
	public boolean handle(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		
		String serviceInstanceVarName = this.getServiceInstanceVarName(context);
		if (serviceInstanceVarName == null) {
			return false;
		}
		
		/*
		 * Pre Phase code
		 */
		
		// create variable for all responses
		String restCallResponseVarName = this.createRESTResponseVar(context);
		
		if (restCallResponseVarName == null) {
			return false;
		}
		
		// create state variable inside scope
		String stateVarName = this.createStateVar(context, context.getRelationshipTemplate().getId());
		
		if (stateVarName == null) {
			return false;
		}
		
		// based on the relatioships baseType we add the logic into different
		// phases of relations AND nodes
		// connectsTo = own phases
		// else = source node phasesl
		
		Element injectionPreElement = null;
		Element injectionPostElement = null;
		String sourceInstanceVarName = this.findInstanceVar(context, context.getRelationshipTemplate().getSource().getId(), true);
		String targetInstanceVarName = this.findInstanceVar(context, context.getRelationshipTemplate().getTarget().getId(), true);
		
		if (Utils.getRelationshipTypeHierarchy(context.getRelationshipTemplate().getRelationshipType()).contains(Utils.TOSCABASETYPE_CONNECTSTO)) {
			injectionPreElement = context.getPrePhaseElement();
			injectionPostElement = context.getPostPhaseElement();
		} else {
			// fetch nodeTemplate
			AbstractNodeTemplate sourceNodeTemplate = context.getRelationshipTemplate().getSource();
			injectionPreElement = context.createContext(sourceNodeTemplate).getPrePhaseElement();
			injectionPostElement = context.createContext(sourceNodeTemplate).getPostPhaseElement();
		}
		
		if (injectionPostElement == null | injectionPreElement == null | sourceInstanceVarName == null | targetInstanceVarName == null) {
			return false;
		}
		
		/*
		 * (i) append bpel code to create the nodeInstance (ii) append bpel code
		 * to fetch nodeInstanceURL
		 */
		
		try {
			// create bpel extension activity and append
			String bpelString = this.fragments.generateBPEL4RESTLightRelationInstancePOST(serviceInstanceVarName, context.getRelationshipTemplate().getId(), restCallResponseVarName, sourceInstanceVarName, targetInstanceVarName);
			Node createRelationInstanceExActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			createRelationInstanceExActiv = context.importNode(createRelationInstanceExActiv);
			injectionPreElement.appendChild(createRelationInstanceExActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		// generate String var for relationInstance URL
		String relationInstanceURLVarName = "";
		
		if (this.findInstanceVar(context, context.getRelationshipTemplate().getId(), false) == null) {
			// generate String var for nodeInstance URL
			relationInstanceURLVarName = this.createInstanceVar(context, context.getRelationshipTemplate().getId());
		} else {
			relationInstanceURLVarName = this.findInstanceVar(context, context.getRelationshipTemplate().getId(), false);
		}
		
		if (relationInstanceURLVarName == null) {
			return false;
		}
		
		try {
			// save relationInstance url from response
			String bpelString = this.fragments.generateAssignFromRelationInstancePOSTResponseToStringVar(relationInstanceURLVarName, restCallResponseVarName);
			Node assignRelationInstanceUrl = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			assignRelationInstanceUrl = context.importNode(assignRelationInstanceUrl);
			injectionPreElement.appendChild(assignRelationInstanceUrl);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		// we'll use this later when we determine that the handle Node doesn't
		// have lifecycle operations. Without this check all nodes without
		// lifecycle (or cloud prov operations) will be in an uninstalled state
		String lastSetState = "initial";
		
		try {
			// update state variable to uninstalled
			org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignInitRelationState" + System.currentTimeMillis(), "string('" + lastSetState + "')", stateVarName);
			assignNode = context.importNode(assignNode);
			injectionPreElement.appendChild(assignNode);
			
			// send state to api
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
			Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			extActiv = context.importNode(extActiv);
			injectionPreElement.appendChild(extActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			// set state
			org.opentosca.planbuilder.fragments.Fragments frag = new org.opentosca.planbuilder.fragments.Fragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode("assignFinalNodeState" + System.currentTimeMillis(), "string('initialized')", stateVarName);
			assignNode = context.importNode(assignNode);
			
			// create PUT activity
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName, stateVarName);
			Node extActiv = org.opentosca.planbuilder.fragments.Fragments.Util.string2dom(bpelString);
			extActiv = context.importNode(extActiv);
			
			injectionPostElement.appendChild(assignNode);
			injectionPostElement.appendChild(extActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		// needs property update only if the relation has properties
		if (this.checkProperties(relationshipTemplate.getProperties())) {
			// make a GET on the nodeInstance properties
			
			try {
				// fetch properties
				Node nodeInstancePropsGETNode = this.fragments.generateInstancePropertiesGETAsNode(relationInstanceURLVarName, restCallResponseVarName);
				nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
				injectionPostElement.appendChild(nodeInstancePropsGETNode);
			} catch (SAXException e1) {
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
			// assign the values from the property variables into REST/HTTP
			// Request
			// and send
			// first build a mapping from property variable names to dom element
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context, relationshipTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName, propertyVarNameToDOMMapping);
				assignNode = context.importNode(assignNode);
				injectionPostElement.appendChild(assignNode);
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			// generate BPEL4RESTLight PUT request to update the instance data
			try {
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName, relationInstanceURLVarName);
				bpel4restPUTNode = context.importNode(bpel4restPUTNode);
				injectionPostElement.appendChild(bpel4restPUTNode);
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
	
	private Element fetchInvokerReceive(Element invokerAssign, String requestVarName) {
		
		Node sibling = invokerAssign.getNextSibling();
		
		while (sibling != null & !sibling.getNodeName().contains("invoke")) {
			sibling = sibling.getNextSibling();
		}
		
		if (sibling.getNodeType() == Node.ELEMENT_NODE & sibling.getAttributes().getNamedItem("inputVariable").getTextContent().equals(requestVarName)) {
			return (Element) sibling.getNextSibling();
		}
		
		return null;
	}
	
	private String fetchRequestVarNameFromInvokerAssign(Element assignContentElement) {
		String reqVarName = null;
		
		Node fromNode = this.fetchFromNode(assignContentElement);
		
		Node toNode = this.fetchNextNamedNodeRecursively(fromNode, "to");
		
		reqVarName = toNode.getAttributes().getNamedItem("variable").getTextContent();
		
		return reqVarName;
	}
	
	private Node fetchNextNamedNodeRecursively(Node node, String name) {
		Node sibling = node.getNextSibling();
		
		while (sibling != null & !sibling.getNodeName().contains(name)) {
			sibling = sibling.getNextSibling();
		}
		
		return sibling;
	}
	
	private Node fetchFromNode(Element assignContentElement) {
		Node parent = assignContentElement.getParentNode();
		
		while (parent != null & !parent.getNodeName().contains("from")) {
			parent = parent.getParentNode();
		}
		
		return parent;
	}
	
	private String fetchOperationName(Element assignElement) {
		XPath xpath = this.xPathfactory.newXPath();
		String operationName = null;
		
		try {
			
			operationName = (String) xpath.evaluate(".//*[local-name()='OperationName']/node()", assignElement, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return operationName;
	}
	
	private List<Element> fetchInvokerCallAssigns(Element provisioningPhaseElement) {
		XPath xpath = this.xPathfactory.newXPath();
		List<Element> assignElements = new ArrayList<Element>();
		String xpathQuery = ".//*[local-name()='invokeOperationAsync']";
		try {
			NodeList nodeList = (NodeList) xpath.evaluate(xpathQuery, provisioningPhaseElement, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					assignElements.add((Element) nodeList.item(i));
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return assignElements;
	}
	
	/**
	 * <p>
	 * This method is initializing a Map from BpelVariableName to a DomElement
	 * of the given Properties and Context.
	 * </p>
	 *
	 * @param context TemplatePlanContext
	 * @param properties AbstractProperties with proper DOM Element
	 * @return a Map<String,Node> of BpelVariableName to DOM Node. Maybe null if
	 *         the mapping is not complete, e.g. some bpel variable was not
	 *         found or the properties weren't parsed right.
	 */
	private Map<String, Node> buildMappingsFromVarNameToDomElement(TemplatePlanContext context, AbstractProperties properties) {
		Element propRootElement = properties.getDOMElement();
		
		Map<String, Node> mapping = new HashMap<String, Node>();
		
		// get list of child elements
		NodeList childList = propRootElement.getChildNodes();
		
		for (int i = 0; i < childList.getLength(); i++) {
			Node child = childList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String propertyName = child.getLocalName();
				String propVarName = context.getVarNameOfTemplateProperty(propertyName);
				mapping.put(propVarName, child);
			}
			
		}
		return mapping;
	}
	
	/**
	 * <p>
	 * Checks the given AbstractProperties against following criteria:
	 * Nullpointer-Check for properties itself and its given DOM Element,
	 * followed by whether the dom element has any child elements (if not, we
	 * have no properties/bpel-variables defined)
	 * </p>
	 * 
	 * @param properties AbstractProperties of an AbstractNodeTemplate or
	 *            AbstractRelationshipTemplate
	 * @return true iff properties and properties.getDomElement() != null and
	 *         DomElement.hasChildNodes() == true
	 */
	private boolean checkProperties(AbstractProperties properties) {
		if (properties == null) {
			return false;
		}
		
		if (properties.getDOMElement() == null) {
			return false;
		}
		
		Element propertiesRootElement = properties.getDOMElement();
		
		if (!propertiesRootElement.hasChildNodes()) {
			return false;
		}
		
		return true;
	}
}
