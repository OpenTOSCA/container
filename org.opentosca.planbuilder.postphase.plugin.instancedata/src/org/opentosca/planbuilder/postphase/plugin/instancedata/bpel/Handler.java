package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.postphase.plugin.instancedata.core.InstanceStates;
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
	private BPELProcessFragments bpelFrags;

	private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
	private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
	private XPathFactory xPathfactory = XPathFactory.newInstance();

	public Handler() {

		try {
			this.fragments = new Fragments();
			this.bpelFrags = new BPELProcessFragments();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private String getServiceInstanceVarName(BPELPlanContext context) {
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

	private String createRESTResponseVar(BPELPlanContext context) {
		String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
		QName restCallResponseDeclId = context
				.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
		if (!context.addGlobalVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE, restCallResponseDeclId)) {
			return null;
		}
		return restCallResponseVarName;
	}

	private String createStateVar(BPELPlanContext context, String templateId) {
		// create state variable inside scope
		String stateVarName = templateId + "_state_" + context.getIdForNames();
		QName stringTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(stateVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
			return null;
		}

		return stateVarName;
	}

	private String findInstanceVar(BPELPlanContext context, String templateId, boolean isNode) {
		String instanceURLVarName = ((isNode) ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
		for (String varName : context.getMainVariableNames()) {
			if (varName.contains(instanceURLVarName)) {
				return varName;
			}
		}
		return null;
	}

	private String createInstanceVar(BPELPlanContext context, String templateId) {
		String instanceURLVarName = ((context.getRelationshipTemplate() == null) ? "node" : "relationship")
				+ "InstanceURL_" + templateId + "_" + context.getIdForNames();
		QName stringTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(instanceURLVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId)) {
			return null;
		}

		return instanceURLVarName;
	}

	public boolean handleTerminate(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
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
		String lastSetState = "deleted";

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
					BPELProcessFragments frag = new BPELProcessFragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
							"assignNodeStateFor_" + operationName + "_" + System.currentTimeMillis(),
							"string('" + preState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					lastSetState = preState;

					// assign the state before the assign of the invoker request
					// is made
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode()
							.getParentNode();
					bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);

					// create REST Put activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
							stateVarName);
					Node extActiv = ModelUtils.string2dom(bpelString);
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
					BPELProcessFragments frag = new BPELProcessFragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
							"assignNodeState_" + operationName + "_" + System.currentTimeMillis(),
							"string('" + postState + "')", stateVarName);
					assignNode = context.importNode(assignNode);

					lastSetState = postState;

					/*
					 * assign the state after the receiving the response of the
					 */

					// fetch assign node
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode()
							.getParentNode();

					// fetch the variable name which is used as request body
					String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);

					// from the assign element search for the receive element
					// that is witing for the response
					Element invokerReceiveElement = this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

					// insert assign after the receive
					assignNode = invokerReceiveElement.getParentNode().insertBefore(assignNode,
							invokerReceiveElement.getNextSibling());

					// create PUT activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
							stateVarName);
					Node extActiv = ModelUtils.string2dom(bpelString);
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
				Node nodeInstancePropsGETNode = this.fragments
						.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
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
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context,
					nodeTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
						propertyVarNameToDOMMapping);
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
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
						nodeInstanceURLVarName);
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

		// try {
		// Node deleteNode =
		// this.fragments.createRESTDeleteOnURLBPELVarAsNode(nodeInstanceURLVarName,
		// restCallResponseVarName);
		//
		// deleteNode = context.importNode(deleteNode);
		//
		// context.getPostPhaseElement().appendChild(deleteNode);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return false;
		// } catch (SAXException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return true;
	}

	/**
	 * Appends BPEL Code that updates InstanceData for the given NodeTemplate. Needs
	 * initialization code on the global level in the plan. This will be checked and
	 * appended if needed.
	 *
	 * @param context
	 *            the TemplateContext of the NodeTemplate
	 * @param nodeTemplate
	 *            the NodeTemplate to handle
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handleBuild(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
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
		 * (i) append bpel code to create the nodeInstance (ii) append bpel code to
		 * fetch nodeInstanceURL
		 */

		try {
			// create bpel extension activity and append
			String bpelString = this.fragments.generateBPEL4RESTLightNodeInstancePOST(serviceInstanceVarName,
					context.getNodeTemplate().getId(), restCallResponseVarName);
			Node createNodeInstanceExActiv = ModelUtils.string2dom(bpelString);
			createNodeInstanceExActiv = context.importNode(createNodeInstanceExActiv);
			context.getPrePhaseElement().appendChild(createNodeInstanceExActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
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
			String bpelString = this.fragments.generateAssignFromNodeInstancePOSTResponseToStringVar(
					nodeInstanceURLVarName, restCallResponseVarName);
			Node assignNodeInstanceUrl = ModelUtils.string2dom(bpelString);
			assignNodeInstanceUrl = context.importNode(assignNodeInstanceUrl);
			context.getPrePhaseElement().appendChild(assignNodeInstanceUrl);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// update state variable to uninstalled
			BPELProcessFragments frag = new BPELProcessFragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
					"assignInitNodeState" + System.currentTimeMillis(), "string('initial')", stateVarName);
			assignNode = context.importNode(assignNode);
			context.getPrePhaseElement().appendChild(assignNode);

			// send state to api
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
					stateVarName);
			Node extActiv = ModelUtils.string2dom(bpelString);
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
		String lastSetState = "initial";

		/*
		 * Prov Phase code
		 */

		// fetch all assigns that assign an invoke async operation request

		Element provisioningPhaseElement = context.getProvisioningPhaseElement();
		List<Element> assignContentElements = this.fetchInvokerCallAssigns(provisioningPhaseElement);

		List<String> operationNames = new ArrayList<>();

		// for each assign element we fetch the operation name, determine the
		// pre and post states, and append the pre state before the found assign
		// and the post state after the receive of the invoker iteraction
		for (Element assignContentElement : assignContentElements) {

			// fetch operationName from literal contents
			String operationName = this.fetchOperationName(assignContentElement);
			operationNames.add(operationName);
			// determine pre and post state for operation
			String preState = InstanceStates.getOperationPreState(operationName);
			String postState = InstanceStates.getOperationPostState(operationName);

			if (preState != null) {

				try {

					// assign prestate to state variable
					BPELProcessFragments frag = new BPELProcessFragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
							"assignNodeStateFor_" + operationName + "_" + System.currentTimeMillis(),
							"string('" + preState + "')", stateVarName);
					assignNode = context.importNode(assignNode);
					lastSetState = preState;

					// assign the state before the assign of the invoker request
					// is made
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode()
							.getParentNode();
					bpelAssignNode.getParentNode().insertBefore(assignNode, bpelAssignNode);

					// create REST Put activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
							stateVarName);
					Node extActiv = ModelUtils.string2dom(bpelString);
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
					BPELProcessFragments frag = new BPELProcessFragments();
					Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
							"assignNodeState_" + operationName + "_" + System.currentTimeMillis(),
							"string('" + postState + "')", stateVarName);
					assignNode = context.importNode(assignNode);

					lastSetState = postState;

					/*
					 * assign the state after the receiving the response of the
					 */

					// fetch assign node
					Node bpelAssignNode = assignContentElement.getParentNode().getParentNode().getParentNode()
							.getParentNode();

					// fetch the variable name which is used as request body
					String reqVarName = this.fetchRequestVarNameFromInvokerAssign(assignContentElement);

					// from the assign element search for the receive element
					// that is witing for the response
					Element invokerReceiveElement = this.fetchInvokerReceive((Element) bpelAssignNode, reqVarName);

					// insert assign after the receive
					assignNode = invokerReceiveElement.getParentNode().insertBefore(assignNode,
							invokerReceiveElement.getNextSibling());

					// create PUT activity
					String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
							stateVarName);
					Node extActiv = ModelUtils.string2dom(bpelString);
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

		if (lastSetState.equals("initial")) {
			try {
				// set state
				String nextState = InstanceStates.getNextStableOperationState(lastSetState);
				// if this node never was handled by lifecycle ops we just set
				// it to started
				if (operationNames.isEmpty()) {
					nextState = "started";
				}
				BPELProcessFragments frag = new BPELProcessFragments();
				Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
						"assignFinalNodeState" + System.currentTimeMillis(), "string('" + nextState + "')",
						stateVarName);
				assignNode = context.importNode(assignNode);

				// create PUT activity
				String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeInstanceURLVarName,
						stateVarName);
				Node extActiv = ModelUtils.string2dom(bpelString);
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
				Node nodeInstancePropsGETNode = this.fragments
						.generateInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
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
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context,
					nodeTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
						propertyVarNameToDOMMapping);
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
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
						nodeInstanceURLVarName);
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

	public boolean handle(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {

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
		String sourceInstanceVarName = this.findInstanceVar(context,
				context.getRelationshipTemplate().getSource().getId(), true);
		String targetInstanceVarName = this.findInstanceVar(context,
				context.getRelationshipTemplate().getTarget().getId(), true);

		if (ModelUtils.getRelationshipTypeHierarchy(context.getRelationshipTemplate().getRelationshipType())
				.contains(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
			injectionPreElement = context.getPrePhaseElement();
			injectionPostElement = context.getPostPhaseElement();
		} else {
			// fetch nodeTemplate
			AbstractNodeTemplate sourceNodeTemplate = context.getRelationshipTemplate().getSource();
			injectionPreElement = context.createContext(sourceNodeTemplate).getPrePhaseElement();
			injectionPostElement = context.createContext(sourceNodeTemplate).getPostPhaseElement();
		}

		if (injectionPostElement == null | injectionPreElement == null | sourceInstanceVarName == null
				| targetInstanceVarName == null) {
			return false;
		}

		/*
		 * (i) append bpel code to create the nodeInstance (ii) append bpel code to
		 * fetch nodeInstanceURL
		 */

		try {
			// create bpel extension activity and append
			String bpelString = this.fragments.generateBPEL4RESTLightRelationInstancePOST(serviceInstanceVarName,
					context.getRelationshipTemplate().getId(), restCallResponseVarName, sourceInstanceVarName,
					targetInstanceVarName);
			Node createRelationInstanceExActiv = ModelUtils.string2dom(bpelString);
			createRelationInstanceExActiv = context.importNode(createRelationInstanceExActiv);
			injectionPreElement.appendChild(createRelationInstanceExActiv);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// generate String var for relationInstance URL
		String relationInstanceURLVarName = "";

		if (this.findInstanceVar(context, context.getRelationshipTemplate().getId(), false) == null) {
			// generate String var for nodeInstance URL
			relationInstanceURLVarName = this.createInstanceVar(context, context.getRelationshipTemplate().getId());
		} else {
			relationInstanceURLVarName = this.findInstanceVar(context, context.getRelationshipTemplate().getId(),
					false);
		}

		if (relationInstanceURLVarName == null) {
			return false;
		}

		try {
			// save relationInstance url from response
			String bpelString = this.fragments.generateAssignFromRelationInstancePOSTResponseToStringVar(
					relationInstanceURLVarName, restCallResponseVarName);
			Node assignRelationInstanceUrl = ModelUtils.string2dom(bpelString);
			assignRelationInstanceUrl = context.importNode(assignRelationInstanceUrl);
			injectionPreElement.appendChild(assignRelationInstanceUrl);
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// we'll use this later when we determine that the handle Node doesn't
		// have lifecycle operations. Without this check all nodes without
		// lifecycle (or cloud prov operations) will be in an uninstalled state
		String lastSetState = "initial";

		try {
			// update state variable to uninstalled
			BPELProcessFragments frag = new BPELProcessFragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
					"assignInitRelationState" + System.currentTimeMillis(), "string('" + lastSetState + "')",
					stateVarName);
			assignNode = context.importNode(assignNode);
			injectionPreElement.appendChild(assignNode);

			// send state to api
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName,
					stateVarName);
			Node extActiv = ModelUtils.string2dom(bpelString);
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
			BPELProcessFragments frag = new BPELProcessFragments();
			Node assignNode = frag.createAssignXpathQueryToStringVarFragmentAsNode(
					"assignFinalNodeState" + System.currentTimeMillis(), "string('initialized')", stateVarName);
			assignNode = context.importNode(assignNode);

			// create PUT activity
			String bpelString = this.fragments.generateBPEL4RESTLightPUTInstanceState(relationInstanceURLVarName,
					stateVarName);
			Node extActiv = ModelUtils.string2dom(bpelString);
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
				Node nodeInstancePropsGETNode = this.fragments
						.generateInstancePropertiesGETAsNode(relationInstanceURLVarName, restCallResponseVarName);
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
			Map<String, Node> propertyVarNameToDOMMapping = this.buildMappingsFromVarNameToDomElement(context,
					relationshipTemplate.getProperties());
			try {
				// then generate an assign to have code that writes the runtime
				// values into the instance data db.
				// we use the restCallResponseVarName from the GET before, as it
				// has
				// proper format
				Node assignNode = this.fragments.generateAssignFromPropertyVarToDomMapping(restCallResponseVarName,
						propertyVarNameToDOMMapping);
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
				Node bpel4restPUTNode = this.fragments.generateInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName,
						relationInstanceURLVarName);
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

		if (sibling.getNodeType() == Node.ELEMENT_NODE
				& sibling.getAttributes().getNamedItem("inputVariable").getTextContent().equals(requestVarName)) {
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

			operationName = (String) xpath.evaluate(".//*[local-name()='OperationName']/node()", assignElement,
					XPathConstants.STRING);
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
	 * This method is initializing a Map from BpelVariableName to a DomElement of
	 * the given Properties and Context.
	 * </p>
	 *
	 * @param context
	 *            BPELPlanContext
	 * @param properties
	 *            AbstractProperties with proper DOM Element
	 * @return a Map<String,Node> of BpelVariableName to DOM Node. Maybe null if the
	 *         mapping is not complete, e.g. some bpel variable was not found or the
	 *         properties weren't parsed right.
	 */
	private Map<String, Node> buildMappingsFromVarNameToDomElement(BPELPlanContext context,
			AbstractProperties properties) {
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
	 * Nullpointer-Check for properties itself and its given DOM Element, followed
	 * by whether the dom element has any child elements (if not, we have no
	 * properties/bpel-variables defined)
	 * </p>
	 * 
	 * @param properties
	 *            AbstractProperties of an AbstractNodeTemplate or
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

	public boolean handlePasswordCheck(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {

		// find properties which store passwords
		// find their variables
		Collection<Variable> pwVariables = new ArrayList<Variable>();
		Collection<Variable> variables = context.getPropertyVariables(nodeTemplate);

		for (Variable var : variables) {
			if (var.getName().contains("Password")) {
				pwVariables.add(var);
			}
		}

		// find runScript method

		AbstractNodeTemplate node = this.findRunScriptNode(nodeTemplate);

		if (node == null) {
			return false;
		}

		Map<AbstractParameter, Variable> inputParams = new HashMap<AbstractParameter, Variable>();

		String cmdStringName = "checkPasswordScript_" + nodeTemplate.getId() + "_" + System.currentTimeMillis();
		String cmdStringVal = this.createPlaceHolderPwCheckCmdString(pwVariables);
		Variable cmdVar = context.createGlobalStringVariable(cmdStringName, cmdStringVal);

		String xPathReplacementCmd = this.createPlaceholderReplaceingXPath(cmdVar.getName(), pwVariables);

		try {
			Node assignPlaceholder = this.bpelFrags.createAssignXpathQueryToStringVarFragmentAsNode(
					"replacePlaceholdersOfPWCheck" + System.currentTimeMillis(), xPathReplacementCmd, cmdVar.getName());
			assignPlaceholder = context.importNode(assignPlaceholder);
			context.getPrePhaseElement().appendChild(assignPlaceholder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inputParams.put(new AbstractParameter() {

			@Override
			public boolean isRequired() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return "xs:String";
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Script";
			}
		}, cmdVar);

		Map<AbstractParameter, Variable> outputParams = new HashMap<AbstractParameter, Variable>();

		String outputVarName = "pwCheckResult" + System.currentTimeMillis();

		Variable outputVar = context.createGlobalStringVariable(outputVarName, "");

		outputParams.put(new AbstractParameter() {

			@Override
			public boolean isRequired() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return "xs:String";
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "ScriptResult";
			}
		}, outputVar);

		// generate call to method
		context.executeOperation(node, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT, inputParams, outputParams, true);

		// check result and eventually throw error

		Node ifTrueThrowError = this.bpelFrags.createIfTrueThrowsError("contains($" + outputVar.getName() + ",'false')",
				new QName("http://opentosca.org/plans/faults", "PasswordWeak"));
		ifTrueThrowError = context.importNode(ifTrueThrowError);
		context.getPrePhaseElement().appendChild(ifTrueThrowError);

		return true;
	}

	private String createPlaceholderReplaceingXPath(String cmdStringName, Collection<Variable> pwVariables) {
		String xpath = "$" + cmdStringName + ",";

		for (Variable var : pwVariables) {
			xpath = "replace(" + xpath;
			xpath += "'" + var.getName() + "'," + "$" + var.getName() + ")";
		}

		return xpath;
	}

	private String createPlaceHolderPwCheckCmdString(Collection<Variable> pwVariables) {
		/*
		 * if echo "$candidate_password" | grep -Eq "$strong_pw_regex"; then echo strong
		 * else echo weak fi
		 */
		String cmdString = "";

		for (Variable var : pwVariables) {
			cmdString += "if echo \"" + var.getName()
					+ "\" | grep -Eq \"(?=^.{8,255}$)((?=.*\\d)(?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))^.*\"; then : else echo \"false\" fi;";
		}

		return cmdString;
	}

	protected AbstractNodeTemplate findRunScriptNode(AbstractNodeTemplate nodeTemplate) {
		List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();

		ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes);

		for (AbstractNodeTemplate node : infraNodes) {
			for (AbstractInterface iface : node.getType().getInterfaces()) {
				if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
						| iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER)) {
					for (AbstractOperation op : iface.getOperations()) {
						if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
								| op.getName()
										.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)) {
							return node;
						}
					}
				}
			}
		}
		return null;
	}
}