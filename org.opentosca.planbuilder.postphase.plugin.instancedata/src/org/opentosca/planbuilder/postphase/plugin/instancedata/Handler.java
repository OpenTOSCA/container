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
			// TODO Auto-generated catch block
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
		
		// register bpel4restlight extension
		context.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true);
		
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
		// framework/serviceInstanceVars/nodeInstanceVars
		// boolean noNodeInstanceVariablesPresent = false;
		
		if (instanceDataUrlVarName == null) {
			// noNodeInstanceVariablesPresent = true;
			instanceDataUrlVarName = this.appendInstanceDataAPIVariableInitCode(context);
		}
		
		if (serviceInstanceVarName == null) {
			// noNodeInstanceVariablesPresent = true;
			serviceInstanceVarName = this.appendServiceInstanceInitCode(context, instanceDataUrlVarName);
		}
		
		// if (noNodeInstanceVariablesPresent) {
		// this.appendNodeInstanceVariablesInit(instanceDataUrlVarName,
		// serviceInstanceVarName, context);
		// }
		
		String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
		QName restCallResponseDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
		if (!context.addVariable(restCallResponseVarName, BuildPlan.VariableType.TYPE, restCallResponseDeclId)) {
			return false;
		}
		
		QName templateId = null;
		if (context.isNodeTemplate()) {
			templateId = new QName(context.getServiceTemplateId().getNamespaceURI(), context.getNodeTemplate().getId());
		} else {
			templateId = new QName(context.getServiceTemplateId().getNamespaceURI(), context.getRelationshipTemplate().getId());
		}
		
		/*
		 * append bpel code to find the right nodeInstanceResponse
		 */
		// first fetch all nodeInstanceResponse Variables
		// List<String> nodeInstanceResponseVarNames = new ArrayList<String>();
		// for (String varName : context.getMainVariableNames()) {
		// if (varName.contains("nodeInstanceResponseVar_")) {
		// nodeInstanceResponseVarNames.add(varName);
		// }
		// }
		
		// for (String nodeInstanceResponseVarName :
		// nodeInstanceResponseVarNames) {
		
		try {
			Node bpelRESTLightGETNode = this.fragments.generateNodeInstancesQueryGETasNode(instanceDataUrlVarName, restCallResponseVarName, templateId, context.isNodeTemplate(), serviceInstanceVarName);
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		
		// make a GET on the nodeInstance properties
		
		try {
			Node nodeInstancePropsGETNode = this.fragments.generateNodeInstancePropertiesGETAsNode(nodeInstanceURLVarName, restCallResponseVarName);
			nodeInstancePropsGETNode = context.importNode(nodeInstancePropsGETNode);
			context.getPostPhaseElement().appendChild(nodeInstancePropsGETNode);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		// generate BPEL4RESTLight PUT request to update the instance data
		try {
			Node bpel4restPUTNode = this.fragments.generateNodeInstancesBPEL4RESTLightPUTAsNode(restCallResponseVarName, nodeInstanceURLVarName);
			bpel4restPUTNode = context.importNode(bpel4restPUTNode);
			context.getPostPhaseElement().appendChild(bpel4restPUTNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// }
		return true;
	}
	
	/**
	 * Appends BPEL code to the given context which sets the InstanceData API
	 * URL globally on the plan.
	 * 
	 * @param context a TemplateContext
	 * @return a String containing the Variable Name of the Variable holding the
	 *         InstanceData API URL
	 */
	private String appendInstanceDataAPIVariableInitCode(TemplatePlanContext context) {
		// add instancedata api url element to plan input message
		context.addStringValueToPlanRequest(Handler.InstanceDataAPIUrlKeyword);
		
		// generate single string variable for InstanceDataAPI HTTP calls, as
		// REST BPEL PLugin
		// can only handle simple xsd types (no queries from input message)
		String instanceDataAPIUrlVarName = Handler.InstanceDataAPIUrlKeyword + context.getIdForNames();
		QName instanceDataAPIUrlDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(instanceDataAPIUrlVarName, BuildPlan.VariableType.TYPE, instanceDataAPIUrlDeclId)) {
			return null;
		}
		
		// TODO Missing assign of simple string variable and return of
		// variablename
		try {
			Node assignNode = this.fragments.generateAssignFromInputMessageToStringVariableAsNode(Handler.InstanceDataAPIUrlKeyword, instanceDataAPIUrlVarName);
			assignNode = context.importNode(assignNode);
			context.appendToInitSequence(assignNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return instanceDataAPIUrlVarName;
	}
	
	/**
	 * Appends BPEL code to the plan on a global level which initializes a
	 * ServiceInstance on the OpenTOSCA Container InstanceData API
	 * 
	 * @param context a TemplateContext
	 * @param instanceDataAPIUrlVarName the name of the BPEL variable holding
	 *            the URL of the InstanceData API
	 * @return a String containing the variable name of the URL to the
	 *         ServiceInstance
	 */
	private String appendServiceInstanceInitCode(TemplatePlanContext context, String instanceDataAPIUrlVarName) {
		// here we'll add code to:
		// instantiate a full instance of the serviceTemplate at the container
		// instancedata api
		
		// get csar and serviceTemplate
		String csarId = context.getCSARFileName();
		QName serviceTemplateId = context.getServiceTemplateId();
		
		// Our Goal with the REST Extension:
		// POST
		// http://localhost:1337/containerapi/instancedata/serviceInstances?csarID=csarId&serviceTemplateID={ns}LocalName
		// then use the response the set the proper id in the
		// serviceInstanceVariable
		
		/*
		 * <bpel:extensionActivity> <bpel4RestLight:POST uri=
		 * "$bpelvar[ContainerURL]/instancedata/serviceInstances?csarID=$bpelvar[CSARName]&amp;serviceTemplateID={http://www.example.com/tosca/ServiceTemplates/Moodle}Moodle"
		 * accept="application/xml"
		 * response="instanceAPIResponse"></bpel4RestLight:POST>
		 * </bpel:extensionActivity>
		 */
		
		// generate any type variable for REST call response
		String restCallResponseVarName = "bpel4restlightVarResponse" + context.getIdForNames();
		QName rescalResponseVarDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
		if (!context.addGlobalVariable(restCallResponseVarName, BuildPlan.VariableType.TYPE, rescalResponseVarDeclId)) {
			return null;
		}
		
		try {
			Node serviceInstancePOSTNode = this.fragments.generateBPEL4RESTLightServiceInstancePOSTAsNode(instanceDataAPIUrlVarName, csarId, serviceTemplateId, restCallResponseVarName);
			serviceInstancePOSTNode = context.importNode(serviceInstancePOSTNode);
			context.appendToInitSequence(serviceInstancePOSTNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		// assign the serviceInstance REST POST Response into global service
		// instance variable
		
		/*
		 * Sample: <?xml version="1.0" encoding="UTF-8"?> <ns2:link ns1:href=
		 * "http://localhost:1337/containerapi/instancedata/serviceInstances/1"
		 * ns1:title=
		 * "http://localhost:1337/containerapi/instancedata/serviceInstances/1"
		 * ns1:type="simple" xmlns:ns2="http://opentosca.org/api/pp"
		 * xmlns:ns1="http://www.w3.org/1999/xlink"/>
		 */
		
		/*
		 *//*
			 * [local-name()='link' and
			 * namespace-uri()='http://opentosca.org/api/pp']/@[local-name()='href'
			 * and namespace-uri()='http://www.w3.org/1999/xlink']
			 */
		
		// create serviceInstanceVariable
		
		// TemplatePropWrapper serviceInstanceVariable =
		// context.createGlobalStringVariable(Handler.ServiceInstanceVarKeyword,
		// "-1");
		
		String serviceInstanceUrlVarName = Handler.ServiceInstanceVarKeyword + context.getIdForNames();
		QName serviceInstanceUrlDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		if (!context.addGlobalVariable(serviceInstanceUrlVarName, BuildPlan.VariableType.TYPE, serviceInstanceUrlDeclId)) {
			return null;
		}
		
		try {
			Node serviceInstanceURLAssignNode = this.fragments.generateServiceInstanceURLVarAssignAsNode(restCallResponseVarName, serviceInstanceUrlVarName);
			serviceInstanceURLAssignNode = context.importNode(serviceInstanceURLAssignNode);
			context.appendToInitSequence(serviceInstanceURLAssignNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return serviceInstanceUrlVarName;
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
