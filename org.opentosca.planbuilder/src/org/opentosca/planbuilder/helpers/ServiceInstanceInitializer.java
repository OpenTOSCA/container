package org.opentosca.planbuilder.helpers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.handlers.BPELProcessHandler;
import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * Appends init code to the given BuildPlan to instantiate a serviceInstance at
 * the responsible OpenTOSCA Container
 * 
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ServiceInstanceInitializer {

	private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
	private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";

	private BuildPlanHandler planHandler;
	private BPELProcessHandler bpelProcessHandler;

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	public ServiceInstanceInitializer() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.planHandler = new BuildPlanHandler();
		this.bpelProcessHandler = new BPELProcessHandler();
	}

	public void initializeInstanceData(BuildPlan buildPlan) {
		this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, buildPlan);
		String instanceDataAPIURlVarName = this.appendInstanceDataAPIVariableInitCode(buildPlan);
		this.appendServiceInstanceInitCode(buildPlan, instanceDataAPIURlVarName);
	}

	/**
	 * Appends BPEL code to the given context which sets the InstanceData API
	 * URL globally on the plan.
	 * 
	 * @param context
	 *            a TemplateContext
	 * @return a String containing the Variable Name of the Variable holding the
	 *         InstanceData API URL
	 */
	private String appendInstanceDataAPIVariableInitCode(BuildPlan buildPlan) {
		// add instancedata api url element to plan input message
		this.planHandler.addStringElementToPlanRequest(ServiceInstanceInitializer.InstanceDataAPIUrlKeyword, buildPlan);

		// generate single string variable for InstanceDataAPI HTTP calls, as
		// REST BPEL PLugin
		// can only handle simple xsd types (no queries from input message)
		String instanceDataAPIUrlVarName = ServiceInstanceInitializer.InstanceDataAPIUrlKeyword
				+ System.currentTimeMillis();

		QName instanceDataAPIUrlDeclId = new QName("http://www.w3.org/2001/XMLSchema", "string",
				"xsd" + System.currentTimeMillis());
		this.bpelProcessHandler.addNamespaceToBPELDoc(instanceDataAPIUrlDeclId.getPrefix(),
				instanceDataAPIUrlDeclId.getNamespaceURI(), buildPlan);

		if (!this.bpelProcessHandler.addVariable(instanceDataAPIUrlVarName, BuildPlan.VariableType.TYPE,
				instanceDataAPIUrlDeclId, buildPlan)) {
			return null;
		}

		// TODO Missing assign of simple string variable and return of
		// variablename
		try {
			Node assignNode = this.generateAssignFromInputMessageToStringVariableAsNode(
					ServiceInstanceInitializer.InstanceDataAPIUrlKeyword, instanceDataAPIUrlVarName);

			assignNode = buildPlan.getBpelDocument().importNode(assignNode, true);
			this.appendToInitSequence(assignNode, buildPlan);
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

	private String appendServiceInstanceInitCode(BuildPlan buildPlan, String instanceDataAPIUrlVarName) {
		// here we'll add code to:
		// instantiate a full instance of the serviceTemplate at the container
		// instancedata api

		// get csar and serviceTemplate
		String csarId = buildPlan.getCsarName();
		QName serviceTemplateId = buildPlan.getServiceTemplate();

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
		String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
		QName rescalResponseVarDeclId = new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd" + System.currentTimeMillis());
		this.bpelProcessHandler.addNamespaceToBPELDoc(rescalResponseVarDeclId.getPrefix(),
				rescalResponseVarDeclId.getNamespaceURI(), buildPlan);
		
		if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BuildPlan.VariableType.TYPE,
				rescalResponseVarDeclId, buildPlan)) {
			return null;
		}

		try {
			Node serviceInstancePOSTNode = this.generateBPEL4RESTLightServiceInstancePOSTAsNode(
					instanceDataAPIUrlVarName, csarId, serviceTemplateId, restCallResponseVarName);
			serviceInstancePOSTNode = buildPlan.getBpelDocument().importNode(serviceInstancePOSTNode,true);
			this.appendToInitSequence(serviceInstancePOSTNode,buildPlan);
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
			 * namespace-uri()='http://opentosca.org/api/pp']/@[local-name()='
			 * href' and namespace-uri()='http://www.w3.org/1999/xlink']
			 */

		// create serviceInstanceVariable

		// TemplatePropWrapper serviceInstanceVariable =
		// context.createGlobalStringVariable(Handler.ServiceInstanceVarKeyword,
		// "-1");

		String serviceInstanceUrlVarName = ServiceInstanceInitializer.ServiceInstanceVarKeyword
				+ System.currentTimeMillis();
		QName serviceInstanceUrlDeclId = new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd" + System.currentTimeMillis());
		this.bpelProcessHandler.addNamespaceToBPELDoc(serviceInstanceUrlDeclId.getPrefix(),
				serviceInstanceUrlDeclId.getNamespaceURI(), buildPlan);
		
		if (!this.bpelProcessHandler.addVariable(serviceInstanceUrlVarName, BuildPlan.VariableType.TYPE,
				serviceInstanceUrlDeclId, buildPlan)) {
			return null;
		}

		try {
			Node serviceInstanceURLAssignNode = this.generateServiceInstanceURLVarAssignAsNode(restCallResponseVarName,
					serviceInstanceUrlVarName);
			serviceInstanceURLAssignNode = buildPlan.getBpelDocument().importNode(serviceInstanceURLAssignNode,true);
			this.appendToInitSequence(serviceInstanceURLAssignNode,buildPlan);
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
	 * Appends the given node the the main sequence of the buildPlan this
	 * context belongs to
	 * 
	 * @param node
	 *            a XML DOM Node
	 * @return true if adding the node to the main sequence was successfull
	 */
	private boolean appendToInitSequence(Node node, BuildPlan buildPlan) {

		Element flowElement = buildPlan.getBpelMainFlowElement();

		Node mainSequenceNode = flowElement.getParentNode();

		mainSequenceNode.insertBefore(node, flowElement);

		return true;
	}

	private String generateAssignFromInputMessageToStringVariable(String inputMessageElementLocalName,
			String stringVariableName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignFromInputToStringVar.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		// <!-- $inputElementLocalName, $StringVariableName, $assignName -->
		bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
		bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
		bpelAssignString = bpelAssignString.replace("$assignName",
				"assignFromInputToString" + System.currentTimeMillis());
		return bpelAssignString;
	}

	private Node generateAssignFromInputMessageToStringVariableAsNode(String inputMessageElementLocalName,
			String stringVariableName) throws IOException, SAXException {
		String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName,
				stringVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	private String generateBPEL4RESTLightServiceInstancePOST(String instanceDataAPIUrlVariableName, String csarId,
			QName serviceTemplateId, String responseVariableName) throws IOException {
		// tags in xml snippet: $InstanceDataURLVar, $CSARName,
		// $serviceTemplateId, $ResponseVarName
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

		bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
		bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
		bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
		bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

		return bpel4RestString;
	}

	private Node generateBPEL4RESTLightServiceInstancePOSTAsNode(String instanceDataAPIUrlVariableName, String csarId,
			QName serviceTemplateId, String responseVariableName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, csarId,
				serviceTemplateId, responseVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	private String generateServiceInstanceURLVarAssign(String serviceInstanceResponseVarName,
			String serviceInstanceURLVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignServiceInstancePOSTResponse.xml");
		File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);
		// <!-- $assignName $ServiceInstanceResponseVarName
		// $ServiceInstanceURLVarName-->

		bpelAssignString = bpelAssignString.replace("$assignName",
				"assignServiceInstance" + System.currentTimeMillis());
		bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
		bpelAssignString = bpelAssignString.replace("$ServiceInstanceURLVarName", serviceInstanceURLVarName);
		return bpelAssignString;
	}

	private Node generateServiceInstanceURLVarAssignAsNode(String serviceInstanceResponseVarName,
			String serviceInstanceURLVarName) throws IOException, SAXException {
		String templateString = this.generateServiceInstanceURLVarAssign(serviceInstanceResponseVarName,
				serviceInstanceURLVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
