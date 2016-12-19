package org.opentosca.planbuilder.postphase.plugin.instancedata;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
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


	public Handler() {
		try {
			this.fragments = new Fragments();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public boolean handle(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		return false;
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
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		/*
		 * Example of a HTTP Request we want to send:
		 *
		 *
		 * PUT: http://localhost:1337/containerapi/instancedata/nodeInstances/2/
		 * properties
		 *
		 * Body: <?xml version="1.0" encoding="UTF-8"
		 * standalone="no"?><ns2:UbuntuProperties
		 * xmlns:ns2="http://www.example.com/tosca/ubuntu"
		 * xmlns="http://www.example.com/tosca/ubuntu"
		 * xmlns:ns1="http://opentosca.org/self-service"
		 * xmlns:tosca="http://docs.oasis-open.org/tosca/ns/2011/12" xmlns:tst=
		 * "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes"
		 * xmlns
		 * :winery="http://www.opentosca.org/winery/extensions/tosca/2013/02/12"
		 * >
		 * <Address>ec2-54-74-146-235.eu-west-1.compute.amazonaws.com</Address>
		 * <SSHUser>ec2-user-01</SSHUser> <SSHPrivateKey>someKey</SSHPrivateKey>
		 * </ns2:UbuntuProperties>
		 */

		/*
		 * Here we will generate bpel code which instantiates a service instance
		 * at the openTOSCA instancedata API. we add it to the main sequence
		 * element of the build plan so that each TemplateBuildPlan can handle
		 * it's own property update
		 */

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
			return false;
		}

		if (serviceInstanceVarName == null) {
			return false;
		}

		String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
		QName restCallResponseDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
		if (!context.addVariable(restCallResponseVarName, BuildPlan.VariableType.TYPE, restCallResponseDeclId)) {
			return false;
		}

		QName typeId = null;
		if (context.isNodeTemplate()) {
			typeId = context.getNodeTemplate().getType().getId();
		} else {
			typeId = context.getRelationshipTemplate().getType();
		}

		/*
		 * append bpel code to find the right nodeInstanceResponse
		 */
		try {
			Node bpelRESTLightGETNode = this.fragments.generateNodeInstancesQueryGETasNode(serviceInstanceVarName, restCallResponseVarName, typeId);
			bpelRESTLightGETNode = context.importNode(bpelRESTLightGETNode);
			context.getPostPhaseElement().appendChild(bpelRESTLightGETNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// generate String var for nodeInstance URL
		String nodeInstanceURLVarName = "nodeInstanceURLbpel4restlightVarResponse" + context.getIdForNames();
		QName nodeInstanceURLDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addVariable(nodeInstanceURLVarName, BuildPlan.VariableType.TYPE, nodeInstanceURLDeclId)) {
			return false;
		}

		// fetch the nodeinstance URL from the restCallResponse
		try {
			Node assignFromNodeInstancesResponseToURLVar = this.fragments.generateAssignFromNodeInstanceResponseToStringVarAsNode(nodeInstanceURLVarName, restCallResponseVarName);
			assignFromNodeInstancesResponseToURLVar = context.importNode(assignFromNodeInstancesResponseToURLVar);
			context.getPostPhaseElement().appendChild(assignFromNodeInstancesResponseToURLVar);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (SAXException e1) {
			e1.printStackTrace();
			return false;
		}

		// make a GET on the nodeInstance properties

		try {
			Node nodeInstancePropsGETNode = this.fragments.generateNodeInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
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
			Node bpel4restPUTNode = this.fragments.generateNodeInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName, nodeInstanceURLVarName);
			bpel4restPUTNode = context.importNode(bpel4restPUTNode);
			context.getPostPhaseElement().appendChild(bpel4restPUTNode);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}
		// }
		return true;
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
}
