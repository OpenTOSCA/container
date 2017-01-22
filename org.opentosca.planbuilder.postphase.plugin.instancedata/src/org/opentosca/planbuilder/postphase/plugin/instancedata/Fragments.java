/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.instancedata;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the BPEL Fragments
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Fragments {

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException
	 *             is thrown when initializing the internal DocumentBuild fails
	 */
	public Fragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	/**
	 * Generates a BPEL4RESTLight extension activity that sets the instance
	 * state of the given nodeInstance with the contents of the given string
	 * variable
	 * 
	 * @param nodeInstanceURLVar
	 *            the variable holding the url to the node instance
	 * @param RequestVarName
	 *            the variable to take the request body contents from
	 * @return a String containing a single BPEL extension activity
	 * @throws IOException
	 *             is thrown when reading a internal file fails
	 */
	public String generateBPEL4RESTLightPUTNodeInstanceState(String nodeInstanceURLVar, String RequestVarName)
			throws IOException {
		// BPEL4RESTLightPUT_NodeInstance_State_InstanceDataAPI.xml
		// <!-- $RequestVarName,$nodeInstanceURLVar -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightPUT_NodeInstance_State_InstanceDataAPI.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4restString = FileUtils.readFileToString(bpel4RestFile);

		bpel4restString = bpel4restString.replace("$nodeInstanceURLVar", nodeInstanceURLVar);
		bpel4restString = bpel4restString.replace("$RequestVarName", RequestVarName);

		return bpel4restString;
	}

	/**
	 * Generates a String containing a BPEL assign that reads the value of a
	 * NodeInstance create response and writes it into the referenced string
	 * variable
	 * 
	 * @param stringVarName
	 *            the string variable to write the data into
	 * @param nodeInstancePOSTResponseVarName
	 *            the response variable of a nodeInstance create POST
	 * @return a String containing a BPEL assign
	 * @throws IOException
	 *             is thrown when reading a internal file fails
	 */
	public String generateAssignFromNodeInstancePOSTResponseToStringVar(String stringVarName,
			String nodeInstancePOSTResponseVarName) throws IOException {
		// BPELAssignFromNodeInstancePOSTResponseToStringVar.xml
		// <!-- $stringVarName, $NodeInstanceResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPELAssignFromNodeInstancePOSTResponseToStringVar.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpel4RestFile);

		bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
		bpelAssignString = bpelAssignString.replace("$NodeInstanceResponseVarName", nodeInstancePOSTResponseVarName);

		return bpelAssignString;
	}

	/**
	 * Generates a String containing a BPEL4RESTLight extension activity which
	 * create a nodeTemplate instance on the given serviceTemplate instance
	 * 
	 * @param serviceInstanceURLVar
	 *            the variable holding the serviceInstanceUrl
	 * @param nodeTemplateId
	 *            the id of the nodeTemplate to instantiate
	 * @param responseVariableName
	 *            the variable to store the response into
	 * @return a String containing a BPEL extension activity
	 * @throws IOException
	 *             is thrown when reading the internal file fails
	 */
	public String generateBPEL4RESTLightNodeInstancePOST(String serviceInstanceURLVar, String nodeTemplateId,
			String responseVariableName) throws IOException {
		// <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightPOST_NodeInstance_InstanceDataAPI.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

		bpel4RestString = bpel4RestString.replace("$serviceInstanceURLVar", serviceInstanceURLVar);
		bpel4RestString = bpel4RestString.replace("$nodeTemplateId", nodeTemplateId);
		bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

		return bpel4RestString;
	}

	public String generateBPEL4RESTLightServiceInstancePOST(String instanceDataAPIUrlVariableName, String csarId,
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

	public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(String instanceDataAPIUrlVariableName, String csarId,
			QName serviceTemplateId, String responseVariableName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, csarId,
				serviceTemplateId, responseVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateServiceInstanceURLVarAssign(String serviceInstanceResponseVarName,
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

	public Node generateServiceInstanceURLVarAssignAsNode(String serviceInstanceResponseVarName,
			String serviceInstanceURLVarName) throws IOException, SAXException {
		String templateString = this.generateServiceInstanceURLVarAssign(serviceInstanceResponseVarName,
				serviceInstanceURLVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public Node generateNodeInstancesQueryGETasNode(String instanceDataUrlVarName, String responseVarName,
			QName nodeType) throws IOException, SAXException {
		String templateString = this.generateNodeInstancePropertiesGET(instanceDataUrlVarName, responseVarName,
				nodeType);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateNodeInstancePropertiesGET(String nodeInstanceUrlVarName, String bpel4RestLightResponseVarName)
			throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_Properties.xml");
		File bpel4restLightGETFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4restLightGETString = FileUtils.readFileToString(bpel4restLightGETFile);
		// <!-- $urlVarName, $ResponseVarName -->
		bpel4restLightGETString = bpel4restLightGETString.replace("$urlVarName", nodeInstanceUrlVarName);
		bpel4restLightGETString = bpel4restLightGETString.replace("$ResponseVarName", bpel4RestLightResponseVarName);
		return bpel4restLightGETString;
	}

	public Node generateNodeInstancePropertiesGETAsNode(String nodeInstanceUrlVarName,
			String bpel4RestLightResponseVarName) throws SAXException, IOException {
		String templateString = this.generateNodeInstancePropertiesGET(nodeInstanceUrlVarName,
				bpel4RestLightResponseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateAssignFromNodeInstanceResonseToStringVar(String stringVarName,
			String nodeInstanceResponseVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignFromNodeInstanceRequestToStringVar.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		// <!-- $stringVarName, $NodeInstanceResponseVarName -->
		bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
		bpelAssignString = bpelAssignString.replace("$NodeInstanceResponseVarName", nodeInstanceResponseVarName);
		return bpelAssignString;
	}

	public Node generateAssignFromNodeInstanceResponseToStringVarAsNode(String stringVarName,
			String nodeInstanceResponseVarName) throws IOException, SAXException {
		String templateString = this.generateAssignFromNodeInstanceResonseToStringVar(stringVarName,
				nodeInstanceResponseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateNodeInstancePropertiesGET(String instanceDataUrlVarName, String responseVarName,
			QName nodeType) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);

		// $InstanceDataURLVar, $ResponseVarName, $nodeType

		bpelAssignString = bpelAssignString.replace("$InstanceDataURLVar", instanceDataUrlVarName);
		bpelAssignString = bpelAssignString.replace("$ResponseVarName", responseVarName);
		bpelAssignString = bpelAssignString.replace("$nodeType", nodeType.toString());
		return bpelAssignString;
	}

	public String generateServiceInstanceRequestToStringVarAssign(String stringVarName,
			String serviceInstanceResponseVarName, int nodeInstanceIndex) throws IOException {
		// <!-- $stringVarName, $ServiceInstanceResponseVarName,
		// $nodeInstanceIndex -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignFromServiceInstanceRequestToStringVar.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);

		bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
		bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
		bpelAssignString = bpelAssignString.replace("$nodeInstanceIndex", String.valueOf(nodeInstanceIndex));

		return bpelAssignString;
	}

	public Node generateServiceInstanceRequestToStringVarAssignAsNode(String stringVarName,
			String serviceInstanceResponseVarName, int nodeInstanceIndex) throws IOException, SAXException {
		String templateString = this.generateServiceInstanceRequestToStringVarAssign(stringVarName,
				serviceInstanceResponseVarName, nodeInstanceIndex);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateBPEL4RESTLightGET(String urlVarName, String responseVarName) throws IOException {
		// BPEL4RESTLightGET_ServiceInstance_InstanceDataAPI.xml
		// <!-- $serviceInstanceUrlVarName, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET.xml");
		File bpelServiceInstanceGETFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelServiceInstanceGETString = FileUtils.readFileToString(bpelServiceInstanceGETFile);

		bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$urlVarName", urlVarName);
		bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$ResponseVarName", responseVarName);
		return bpelServiceInstanceGETString;
	}

	public Node generateBPEL4RESTLightGETAsNode(String serviceInstanceUrlVarName, String responseVarName)
			throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightGET(serviceInstanceUrlVarName, responseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateAssignFromInputMessageToStringVariable(String inputMessageElementLocalName,
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

	public Node generateAssignFromInputMessageToStringVariableAsNode(String inputMessageElementLocalName,
			String stringVariableName) throws IOException, SAXException {
		String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName,
				stringVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String generateCopyFromStringVarToAnyTypeVar(String propertyVarName,
			String nodeInstancePropertyRequestVarName, String nodeInstancePropertyLocalName,
			String nodeInstancePropertyNamespace) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		// <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
		// $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
		bpelAssignString = bpelAssignString.replace("$PropertyVarName", propertyVarName);
		bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyRequestVarName",
				nodeInstancePropertyRequestVarName);
		bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyLocalName", nodeInstancePropertyLocalName);
		bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyNamespace", nodeInstancePropertyNamespace);
		return bpelAssignString;
	}

	public Node generateCopyFromStringVarToAnyTypeVarAsNode(String propertyVarName,
			String nodeInstancePropertyRequestVarName, String nodeInstancePropertyLocalName,
			String nodeInstancePropertyNamespace) throws IOException, SAXException {
		String templateString = this.generateCopyFromStringVarToAnyTypeVar(propertyVarName,
				nodeInstancePropertyRequestVarName, nodeInstancePropertyLocalName, nodeInstancePropertyNamespace);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public Node generateAssignFromPropertyVarToDomMapping(String nodeInstancePropertyRequestVarName,
			Map<String, Node> propertyVarToDomMapping) throws SAXException, IOException {
		// create empty bpel:assign
		String bpelAssignString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignPropertyVarsToAnyElement"
				+ System.currentTimeMillis() + "\" />";
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(bpelAssignString));
		Document doc = this.docBuilder.parse(is);

		Node assignNode = doc.getFirstChild();
		for (String propertyVarName : propertyVarToDomMapping.keySet()) {
			Node propertyNode = propertyVarToDomMapping.get(propertyVarName);
			Node copyNode = this.generateCopyFromStringVarToAnyTypeVarAsNode(propertyVarName,
					nodeInstancePropertyRequestVarName, propertyNode.getLocalName(), propertyNode.getNamespaceURI());

			copyNode = doc.importNode(copyNode, true);
			assignNode.appendChild(copyNode);
		}

		return assignNode;
	}

	public String generateNodeInstancesBPEL4RESTLightPUT(String requestVarName, String nodeInstanceURLVarName)
			throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightPUT_NodeInstance_InstanceDataAPI.xml");
		File bpel4RESTLightPUTFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4RESTLightPut = FileUtils.readFileToString(bpel4RESTLightPUTFile);

		// <!-- $RequestVarName,$nodeInstanceURLVar -->
		bpel4RESTLightPut = bpel4RESTLightPut.replace("$RequestVarName", requestVarName);
		bpel4RESTLightPut = bpel4RESTLightPut.replace("$nodeInstanceURLVar", nodeInstanceURLVarName);
		return bpel4RESTLightPut;
	}

	public Node generateNodeInstancesBPEL4RESTLightPUTAsNode(String requestVarName, String nodeInstanceURLVarName)
			throws IOException, SAXException {
		String templateString = this.generateNodeInstancesBPEL4RESTLightPUT(requestVarName, nodeInstanceURLVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
