package org.opentosca.planbuilder.bpel.fragments;

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
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELProcessFragments {
	
	private final static Logger LOG = LoggerFactory.getLogger(BPELProcessFragments.class);
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	
	public static class Util {
		
		private static DocumentBuilderFactory docFactory;
		private static DocumentBuilder docBuilder;
		
		static {
			docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			try {
				docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public static Node string2dom(String xmlString) throws SAXException, IOException {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlString));
			Document doc = docBuilder.parse(is);
			return doc.getFirstChild();
		}
	}
	
	
	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers fails
	 */
	public BPELProcessFragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
	
	public String createBPEL4RESTLightNodeInstancesGETAsString(String nodeTemplateId, String serviceInstanceIdVarName, String responseVarName) throws IOException {
		// <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightGET_NodeInstances_InstanceDataAPI.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
		template = template.replace("$ResponseVarName", responseVarName);
		template = template.replace("$nodeTemplateId", nodeTemplateId);
		return template;
	}
	
	public Node createBPEL4RESTLightNodeInstancesGETAsNode(String nodeTemplateId, String serviceInstanceIdVarName, String responseVarName) throws IOException, SAXException {
		String templateString = this.createBPEL4RESTLightNodeInstancesGETAsString(nodeTemplateId, serviceInstanceIdVarName, responseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	public String createBPEL4RESTLightRelationInstancesGETAsString(String relationshipTemplateId, String serviceInstanceIdVarName, String responseVarName) throws IOException {
		// <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightGET_RelationInstances_InstanceDataAPI.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
		template = template.replace("$ResponseVarName", responseVarName);
		template = template.replace("relationshipTemplateId", relationshipTemplateId);
		return template;
	}
	
	public Node createBPEL4RESTLightRelationInstancesGETAsNode(String relationshipTemplateId, String serviceInstanceIdVarName, String responseVarName) throws IOException, SAXException {
		String templateString = this.createBPEL4RESTLightRelationInstancesGETAsString(relationshipTemplateId, serviceInstanceIdVarName, responseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	
	public String createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(String serviceInstanceIdVarName, String relationshipTemplateId, String responseVarName, String nodeInstanceIdVarName) throws IOException {
		// BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml
		// <!-- $serviceInstanceURLVar, $relationshipTemplateId, $ResponseVarName, $nodeInstanceIdVarName  -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
		template = template.replace("$relationshipTemplateId", relationshipTemplateId);
		template = template.replace("$ResponseVarName", responseVarName);
		template = template.replace("$nodeInstanceIdVarName", nodeInstanceIdVarName);
		return template;
	}
	
	public Node createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(String serviceInstanceIdVarName, String relationshipTemplateId, String responseVarName, String nodeInstanceIdVarName) throws IOException, SAXException {
		String templateString = this.createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(serviceInstanceIdVarName, relationshipTemplateId, responseVarName, nodeInstanceIdVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	public String createBPEL4RESTLightPlanInstanceLOGsPOST(String urlVarName, String requestVarName, String correlationIdVarName) throws IOException {
		// BPEL4RESTLightPOST_PlanInstance_Logs.xml
		// <!-- $urlVarName, $requestVar, $correlationId -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightPOST_PlanInstance_Logs.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$urlVarName", urlVarName);
		template = template.replace("$requestVar", requestVarName);
		template = template.replace("$correlationId", correlationIdVarName);
		return template;
	}
	
	public Node createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(String urlVarName, String requestVarName, String correlationIdVarName) throws IOException, SAXException {
		String templateString = this.createBPEL4RESTLightPlanInstanceLOGsPOST(urlVarName, requestVarName, correlationIdVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName the name of the BPEL assign
	 * @param xpath2Query the csarEntryPoint XPath query
	 * @param stringVarName the variable to load the queries results into
	 * @return a String containing a BPEL Assign element
	 * @throws IOException is thrown when reading the BPEL fragment form the
	 *             resources fails
	 */
	public String createAssignXpathQueryToStringVarFragmentAsString(String assignName, String xpath2Query, String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignStringVarWithXpath2Query.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{xpath2query}", xpath2Query);
		template = template.replace("{stringVarName}", stringVarName);
		return template;
	}
	

	public String createBPEL4RESTLightPUTState(String instanceURLVarName, String requestVarName) throws IOException {
		//<!-- $urlVarName, $requestVar  -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightPUTInstanceState.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$urlVarName", instanceURLVarName);
		template = template.replace("$requestVar", requestVarName);
		return template;
	}
	
	public Node createBPEL4RESTLightPutStateAsNode(String instanceURLVarName, String requestVarName) throws IOException, SAXException {
		String templateString = this.createBPEL4RESTLightPUTState(instanceURLVarName, requestVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public Node createIfTrueThrowsError(String xpath, QName faultName) {
		Document doc = this.docBuilder.newDocument();
		
		Element ifElement = doc.createElementNS(BPELPlan.bpelNamespace, "if");
		
		Element conditionElement = doc.createElementNS(BPELPlan.bpelNamespace, "condition");
		
		conditionElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);
		
		
		Text textSectionValue = doc.createTextNode(xpath);
		conditionElement.appendChild(textSectionValue);
		
		ifElement.appendChild(conditionElement);
		
		
		Element throwElement = doc.createElementNS(BPELPlan.bpelNamespace, "throw");
		
		String nsPrefix = "ns" + System.currentTimeMillis();
		
		throwElement.setAttribute("xmlns:" + nsPrefix, faultName.getNamespaceURI());
		
		throwElement.setAttribute("faultName", nsPrefix + ":" + faultName.getLocalPart());
		
		ifElement.appendChild(throwElement);
		
		return ifElement;
	}

	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName
	 *            the name of the BPEL assign
	 * @param xpath2Query
	 *            the xPath query
	 * @param stringVarName
	 *            the variable to load the queries results into
	 * @return a DOM Node representing a BPEL assign element
	 * @throws IOException is thrown when loading internal bpel fragments fails
	 * @throws SAXException is thrown when parsing internal format into DOM
	 *             fails
	 */
	public Node createAssignXpathQueryToStringVarFragmentAsNode(String assignName, String xpath2Query, String stringVarName) throws IOException, SAXException {
		String templateString = this.createAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Returns an XPath Query which contructs a valid String, to GET a File from
	 * the openTOSCA API
	 *
	 * @param artifactPath a path inside an ArtifactTemplate
	 * @return a String containing an XPath query
	 */
	public String createXPathQueryForURLRemoteFilePath(String artifactPath) {
		BPELProcessFragments.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
		String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
		return filePath;
	}
	
	public String generateBPEL4RESTLightGETonURL(String urlVarName, String responseVarName) throws IOException {		
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightGET_URL_ApplicationXML.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		// <!-- $ResponseVarName, $urlVar  -->
		bpelAssignString = bpelAssignString.replace("$ResponseVarName", responseVarName);
		bpelAssignString = bpelAssignString.replace("$urlVar", urlVarName);
		return bpelAssignString;
	}
	
	public Node generateBPEL4RESTLightGETonURLAsNode(String urlVarName, String reponseVarName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightGETonURL(urlVarName, reponseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates an Assign Activity that writes the content of a String variable
	 * into the first element specified by prefix and localname
	 *
	 * @param assignName the name of the assign
	 * @param variableName the name of the string variable to take the value
	 *            from
	 * @param outputVarName the name of the output message variable
	 * @param outputVarPartName the name of the part inside the message variable
	 * @param outputVarPrefix the prefix of the element inside the message part
	 * @param outputVarLocalName the localname of the element inside the message
	 *            part
	 * @return a String containing a BPEL assign activitiy
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String generateCopyFromStringVarToOutputVariableAsString(String variableName, String outputVarName, String outputVarPartName, String outputVarLocalName) throws IOException {
		// BpelAssignOutputVarFromStringVariable.xml
		// <!-- ${assignName}, ${variableName}, ${outputVarName},
		// ${outputVarPartName}, ${outputVarPrefix}, ${outputVarLocalName} -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BpelCopyOutputVarFromStringVariable.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		bpelAssignString = bpelAssignString.replace("${variableName}", variableName);
		bpelAssignString = bpelAssignString.replace("${outputVarName}", outputVarName);
		bpelAssignString = bpelAssignString.replace("${outputVarPartName}", outputVarPartName);
		bpelAssignString = bpelAssignString.replace("${outputVarLocalName}", outputVarLocalName);
		return bpelAssignString;
	}
	
	/**
	 * Generates an Assign Acitivity that writes the content of a Strig variable
	 * into the first element specified by prefix and localname
	 *
	 * @param assignName the name of the assign
	 * @param variableName the name of the string variable to take the value
	 *            from
	 * @param outputVarName the name of the output message variable
	 * @param outputVarPartName the name of the part inside the message variable
	 * @param outputVarPrefix the prefix of the element inside the message part
	 * @param outputVarLocalName the localName of the element inside the message
	 *            part
	 * @return a DOM Node containing a BPEL Assign Activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node generateCopyFromStringVarToOutputVariableAsNode(String variableName, String outputVarName, String outputVarPartName, String outputVarLocalName) throws IOException, SAXException {
		String templateString = this.generateCopyFromStringVarToOutputVariableAsString(variableName, outputVarName, outputVarPartName, outputVarLocalName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates an assign activity that fetches the value of the input message
	 * and writes it into a string variable
	 *
	 * @param inputMessageElementLocalName the localName of the element inside
	 *            the input message
	 * @param stringVariableName the name of the variable to assign the value to
	 * @return a String containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String generateAssignFromInputMessageToStringVariable(String inputMessageElementLocalName, String stringVariableName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BpelAssignFromInputToStringVar.xml");
		File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
		// <!-- $inputElementLocalName, $StringVariableName, $assignName -->
		bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
		bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
		bpelAssignString = bpelAssignString.replace("$assignName", "assignFromInputToString" + System.currentTimeMillis());
		return bpelAssignString;
	}
	
	/**
	 * Generates an assign activity that fetches the value of the input message
	 * and writes it into a string variable
	 *
	 * @param inputMessageElementLocalName the localName of the element inside
	 *            the input message
	 * @param stringVariableName the name of the variable to assign the value to
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node generateAssignFromInputMessageToStringVariableAsNode(String inputMessageElementLocalName, String stringVariableName) throws IOException, SAXException {
		String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName, stringVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Creates a BPEL assign activity that reads the property values from a
	 * NodeInstance Property response and sets the given variables
	 *
	 * @param assignName the name of the assign activity
	 * @param nodeInstancePropertyResponseVarName the name of the variable
	 *            holding the property data
	 * @param propElement2BpelVarNameMap a Map from DOM Elements (representing
	 *            Node Properties) to BPEL variable names
	 * @return a String containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String createAssignFromNodeInstancePropertyToBPELVariableAsString(String assignName, String nodeInstancePropertyResponseVarName, Map<Element, String> propElement2BpelVarNameMap) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);


		String assignString = "<bpel:assign name=\"" + assignName + "\" xmlns:bpel=\"" + BPELPlan.bpelNamespace
				+ "\" >";

		// <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
		// $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
		for (Element propElement : propElement2BpelVarNameMap.keySet()) {
			String copyString = template.replace("$PropertyVarName", propElement2BpelVarNameMap.get(propElement));
			copyString = copyString.replace("$NodeInstancePropertyRequestVarName", nodeInstancePropertyResponseVarName);
			copyString = copyString.replace("$NodeInstancePropertyLocalName", propElement.getLocalName());
			copyString = copyString.replace("$NodeInstancePropertyNamespace", propElement.getNamespaceURI());
			assignString += copyString;
		}
		
		assignString += "</bpel:assign>";
		
		BPELProcessFragments.LOG.debug("Generated following assign string:");
		BPELProcessFragments.LOG.debug(assignString);
		
		return assignString;
	}
	
	/**
	 * Creates a BPEL assign activity that reads the property values from a
	 * NodeInstance Property response and sets the given variables
	 *
	 * @param assignName the name of the assign activity
	 * @param nodeInstancePropertyResponseVarName the name of the variable
	 *            holding the property data
	 * @param propElement2BpelVarNameMap a Map from DOM Elements (representing
	 *            Node Properties) to BPEL variable names
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node createAssignFromNodeInstancePropertyToBPELVariableAsNode(String assignName, String nodeInstancePropertyResponseVarName, Map<Element, String> propElement2BpelVarNameMap) throws IOException, SAXException {
		String templateString = this.createAssignFromNodeInstancePropertyToBPELVariableAsString(assignName, nodeInstancePropertyResponseVarName, propElement2BpelVarNameMap);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Creates a RESTExtension GET to fetch properties of NodeInstance
	 *
	 * @param nodeInstanceIDUrl the name of the variable holding the address to
	 *            the nodeInstance
	 * @param responseVarName the name of the variable to store the response
	 *            into
	 * @return a String containing a BPEL RESTExtension Activity
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String createRESTExtensionGETForNodeInstancePropertiesAsString(String nodeInstanceIDUrl, String responseVarName) throws IOException {
		// <!-- $urlVarName, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightGET_NodeInstance_Properties.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("$urlVarName", nodeInstanceIDUrl);
		template = template.replace("$ResponseVarName", responseVarName);
		
		return template;
	}
	
	/**
	 * Creates a RESTExtension GET to fetch properties of NodeInstance
	 *
	 * @param nodeInstanceIDUrl the name of the variable holding the address to
	 *            the nodeInstance
	 * @param responseVarName the name of the variable to store the response
	 *            into
	 * @return a Node containing a BPEL RESTExtension Activity
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 */
	public Node createRESTExtensionGETForNodeInstancePropertiesAsNode(String nodeInstanceIDUrl, String responseVarName) throws IOException, SAXException {
		String templateString = this.createRESTExtensionGETForNodeInstancePropertiesAsString(nodeInstanceIDUrl, responseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Create a BPEL assign that copies the NodeInstanceURL from a NodeInstances
	 * Query (See
	 * {@link #createRESTExtensionGETForNodeInstanceDataAsNode(String, String, String, String, boolean)}
	 *
	 * @param assignName the name of the assign
	 * @param stringVarName the name of the xsd:string variable to write the
	 *            NodeInstanceId into
	 * @param nodeInstanceResponseVarName the instanceDataAPI response to fetch
	 *            the NodeInstanceId from
	 * @return a String containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(String assignName, String stringVarName, String nodeInstanceResponseVarName) throws IOException {
		// <!-- $assignName, $stringVarName, $NodeInstanceResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BpelAssignFromNodeInstanceRequestToStringVar.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("$assignName", assignName);
		template = template.replace("$stringVarName", stringVarName);
		template = template.replace("$NodeInstanceResponseVarName", nodeInstanceResponseVarName);
		
		return template;
	}
	
	/**
	 * Create a BPEL assign that copies the NodeInstanceURL from a NodeInstances
	 * Query (See
	 * {@link #createRESTExtensionGETForNodeInstanceDataAsNode(String, String, String, String, boolean)}
	 *
	 * @param assignName the name of the assign
	 * @param stringVarName the name of the xsd:string variable to write the
	 *            NodeInstanceId into
	 * @param nodeInstanceResponseVarName the instanceDataAPI response to fetch
	 *            the NodeInstanceId from
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 */
	public Node createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode(String assignName, String stringVarName, String nodeInstanceResponseVarName) throws SAXException, IOException {
		String templateString = this.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(assignName, stringVarName, nodeInstanceResponseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Creates a String containing a BPEL fragment which uses the
	 * BPELRESTExtension to fetch the InstanceData from an OpenTOSCA Container
	 * instanceDataAPI
	 *
	 * @param serviceInstanceUrlVar the name of the variable holding an URL to a
	 *            serviceInstance
	 * @param responseVarName the name of the variable holding the response of
	 *            the request (must be xsd:anyType)
	 * @param templateId the id of the template the instance belongs to
	 * @param serviceInstanceUrlVarName the name of the variable holding the
	 *            id/link of the serviceInstance
	 * @param isNodeTemplate whether the given tmeplateId belongs to a
	 *            NodeTemplate or RelationshipTemplate
	 * @return a String containing a BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String createRESTExtensionGETForNodeInstanceDataAsString(String serviceInstanceUrlVar, String responseVarName,
			String templateId, String query) throws IOException {
		// <!-- $InstanceDataURLVar, $ResponseVarName, $TemplateId,
		// $serviceInstanceUrlVarName, $templateType -->

		// <!-- $InstanceDataURLVar, $ResponseVarName, $nodeType -->

		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replaceAll("\\$InstanceDataURLVar", serviceInstanceUrlVar);
		template = template.replaceAll("\\$ResponseVarName", responseVarName);
		template = template.replaceAll("\\$templateId", templateId);
		
		if(query != null) {
			template = template.replace("?query", query);
		} else {
			template = template.replace("?query", "");
		}

		return template;
	}
	
	/**
	 * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to
	 * request on.
	 *
	 * @param bpelVarName the variable containing an URL
	 * @param responseVarName the variable to hold the response
	 * @return a String containing a BPEL4RESTLight Activity
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String createRESTDeleteOnURLBPELVarAsString(String bpelVarName, String responseVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightDELETE.xml");
		// <!-- $urlVarName, $ResponseVarName -->
		
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("$urlVarName", bpelVarName);
		template = template.replace("$ResponseVarName", responseVarName);
		
		return template;
	}
	
	/**
	 * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to
	 * request on.
	 *
	 * @param bpelVarName the variable containing an URL
	 * @param responseVarName the variable to hold the response
	 * @return a String containing a BPEL4RESTLight Activity
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 */
	public Node createRESTDeleteOnURLBPELVarAsNode(String bpelVarName, String responseVarName) throws IOException, SAXException {
		String templateString = this.createRESTDeleteOnURLBPELVarAsString(bpelVarName, responseVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	public String createAssignSelectFirstReferenceAndAssignToStringVar(String referencesResponseVarName,
			String stringVarName) throws IOException {
		// BpelAssignSelectFromNodeInstancesRequestToStringVar.xml
		// <!-- $assignName, $stringVarName, $NodeInstancesResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignSelectFromNodeInstancesRequestToStringVar.xml");
		File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);

		bpelAssignString = bpelAssignString.replaceAll("\\$assignName",
				"assignSelectFirstReference" + System.currentTimeMillis());
		bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", stringVarName);
		bpelAssignString = bpelAssignString.replaceAll("\\$NodeInstancesResponseVarName", referencesResponseVarName);
		return bpelAssignString;
	}

	public Node createAssignSelectFirstReferenceAndAssignToStringVarAsNode(String referencesResponseVarName,
			String stringVarName) throws IOException, SAXException {
		String templateString = this.createAssignSelectFirstReferenceAndAssignToStringVar(referencesResponseVarName, stringVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Creates a Node containing a BPEL fragment which uses the
	 * BPELRESTExtension to fetch the InstanceData from an OpenTOSCA Container
	 * instanceDataAPI
	 *
	 * @param serviceInstanceUrlVar the name of the variable holding an URL to a
	 *            serviceInstance
	 * @param responseVarName the name of the variable holding the response of
	 *            the request (must be xsd:anyType)
	 * @param templateId the id of the template the instance belongs to
	 * @param serviceInstanceUrlVarName the name of the variable holding the
	 *            id/link of the serviceInstance
	 * @param isNodeTemplate whether the given tmeplateId belongs to a
	 *            NodeTemplate or RelationshipTemplate
	 * @return a Node containing a BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 */
	public Node createRESTExtensionGETForNodeInstanceDataAsNode(String serviceInstanceUrlVar, String responseVarName,
			String templateId, String query) throws SAXException, IOException {
		String templateString = this.createRESTExtensionGETForNodeInstanceDataAsString(serviceInstanceUrlVar,
				responseVarName, templateId, query);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance
	 * POST response
	 *
	 * @param serviceInstanceResponseVarName the var name of the POST response
	 * @param serviceInstanceURLVarName the var name to save the URL/ID into
	 * @return a String containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String generateServiceInstanceURLVarAssignAsString(String serviceInstanceResponseVarName, String serviceInstanceURLVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BpelAssignServiceInstancePOSTResponse.xml");
		File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);
		// <!-- $assignName $ServiceInstanceResponseVarName
		// $ServiceInstanceURLVarName-->
		
		bpelAssignString = bpelAssignString.replace("$assignName", "assignServiceInstance" + System.currentTimeMillis());
		bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
		bpelAssignString = bpelAssignString.replace("$ServiceInstanceURLVarName", serviceInstanceURLVarName);
		return bpelAssignString;
	}
	
	/**
	 * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance
	 * POST response
	 *
	 * @param serviceInstanceResponseVarName the var name of the POST response
	 * @param serviceInstanceURLVarName the var name to save the URL/ID into
	 * @return a String containing a BPEL assign activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node generateServiceInstanceURLVarAssignAsNode(String serviceInstanceResponseVarName, String serviceInstanceURLVarName) throws IOException, SAXException {
		String templateString = this.generateServiceInstanceURLVarAssignAsString(serviceInstanceResponseVarName, serviceInstanceURLVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 *
	 * @param instanceDataAPIUrlVariableName the name of the variable holding
	 *            the address to the instanceDataAPI
	 * @param csarId the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId the id of the serviceTemplate
	 * @param responseVariableName a name of an anyType variable to save the
	 *            response into
	 * @return a String containing a BPEL4RESTLight POST extension activity
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String generateBPEL4RESTLightServiceInstancePOST(String instanceDataAPIUrlVariableName, String csarId, QName serviceTemplateId, String responseVariableName) throws IOException {
		// tags in xml snippet: $InstanceDataURLVar, $CSARName,
		// $serviceTemplateId, $ResponseVarName
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);
		
		bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
		bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
		bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
		bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);
		
		return bpel4RestString;
	}
	
	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 *
	 * @param instanceDataAPIUrlVariableName the name of the variable holding
	 *            the address to the instanceDataAPI
	 * @param csarId the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId the id of the serviceTemplate
	 * @param responseVariableName a name of an anyType variable to save the
	 *            response into
	 * @return a String containing a BPEL4RESTLight POST extension activity
	 * @throws IOException is thrown when reading internal files fail
	 */
	public String generateBPEL4RESTLightServiceInstancePOST(String instanceDataAPIUrlVariableName, String csarId, QName serviceTemplateId, String requestVariableName, String responseVariableName) throws IOException {
		// tags in xml snippet: $InstanceDataURLVar, $CSARName,
		// $serviceTemplateId, $ResponseVarName
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI_WithBody.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);
		
		bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
		bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
		bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
		bpel4RestString = bpel4RestString.replace("$RequestVarName", requestVariableName);
		bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);
		
		return bpel4RestString;
	}
	
	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 *
	 * @param instanceDataAPIUrlVariableName the name of the variable holding
	 *            the address to the instanceDataAPI
	 * @param csarId the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId the id of the serviceTemplate
	 * @param requestVariableName a name of an anyType variable to take the
	 *            request content from
	 * @param responseVariableName a name of an anyType variable to save the
	 *            response into
	 * @return a Node containing a BPEL4RESTLight POST extension activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(String instanceDataAPIUrlVariableName, String csarId, QName serviceTemplateId, String requestVariableName, String responseVariableName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, csarId, serviceTemplateId, requestVariableName, responseVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 *
	 * @param instanceDataAPIUrlVariableName the name of the variable holding
	 *            the address to the instanceDataAPI
	 * @param csarId the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId the id of the serviceTemplate
	 * @param responseVariableName a name of an anyType variable to save the
	 *            response into
	 * @return a Node containing a BPEL4RESTLight POST extension activity
	 * @throws IOException is thrown when reading internal files fail
	 * @throws SAXException is thrown when parsing internal files fail
	 */
	public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(String instanceDataAPIUrlVariableName, String csarId, QName serviceTemplateId, String responseVariableName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, csarId, serviceTemplateId, responseVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a BPEL If activity that throws the given fault when the given
	 * expr evaluates to true at runtime
	 *
	 * @param xpath1Expr a XPath 1.0 expression as String
	 * @param faultQName a QName denoting the fault to be thrown when the if
	 *            evaluates to true
	 * @return a String containing a BPEL If Activity
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateBPELIfTrueThrowFaultAsString(String xpath1Expr, QName faultQName) throws IOException {
		// <!-- $xpath1Expr, $faultPrefix, $faultNamespace, $faultLocalName-->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("BPELIfTrueThrowFault.xml");
		File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
		String bpelIfString = FileUtils.readFileToString(bpel4RestFile);
		
		bpelIfString = bpelIfString.replace("$xpath1Expr", xpath1Expr);
		
		bpelIfString = bpelIfString.replace("$faultPrefix", faultQName.getLocalPart());
		bpelIfString = bpelIfString.replace("$faultLocalName", faultQName.getLocalPart());
		
		return bpelIfString;
	}
	
	/**
	 * Generates a BPEL If activity that throws the given fault when the given
	 * expr evaluates to true at runtime
	 *
	 * @param xpath1Expr a XPath 1.0 expression as String
	 * @param faultQName a QName denoting the fault to be thrown when the if
	 *            evaluates to true
	 * @return a Node containing a BPEL If Activity
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 */
	public Node generateBPELIfTrueThrowFaultAsNode(String xpath1Expr, QName faultQName) throws IOException, SAXException {
		String templateString = this.generateBPELIfTrueThrowFaultAsString(xpath1Expr, faultQName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
}
