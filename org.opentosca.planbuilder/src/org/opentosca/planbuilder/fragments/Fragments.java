package org.opentosca.planbuilder.fragments;

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
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Fragments {

	private final static Logger LOG = LoggerFactory.getLogger(Fragments.class);

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	/**
	 * Constructor
	 * 
	 * @throws ParserConfigurationException
	 *             is thrown when initializing the DOM Parsers fails
	 */
	public Fragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	/**
	 * Generates an assign activity that fetches the value of the input message
	 * and writes it into a string variable
	 * 
	 * @param inputMessageElementLocalName
	 *            the localName of the element inside the input message
	 * @param stringVariableName
	 *            the name of the variable to assign the value to
	 * @return a String containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 */
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

	/**
	 * Generates an assign activity that fetches the value of the input message
	 * and writes it into a string variable
	 * 
	 * @param inputMessageElementLocalName
	 *            the localName of the element inside the input message
	 * @param stringVariableName
	 *            the name of the variable to assign the value to
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 * @throws SAXException
	 *             is thrown when parsing internal files fail
	 */
	public Node generateAssignFromInputMessageToStringVariableAsNode(String inputMessageElementLocalName,
			String stringVariableName) throws IOException, SAXException {
		String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName,
				stringVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Creates a BPEL assign activity that reads the property values from a
	 * NodeInstance Property response and sets the given variables
	 * 
	 * @param assignName
	 *            the name of the assign activity
	 * @param nodeInstancePropertyResponseVarName
	 *            the name of the variable holding the property data
	 * @param propElement2BpelVarNameMap
	 *            a Map from DOM Elements (representing Node Properties) to BPEL
	 *            variable names
	 * @return a String containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 */
	public String createAssignFromNodeInstancePropertyToBPELVariableAsString(String assignName,
			String nodeInstancePropertyResponseVarName, Map<Element, String> propElement2BpelVarNameMap)
			throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);

		String assignString = "<bpel:assign name=\"" + assignName + "\" xmlns:bpel=\"" + BuildPlan.bpelNamespace
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

		LOG.debug("Generated following assign string:");
		LOG.debug(assignString);

		return assignString;
	}

	/**
	 * Creates a BPEL assign activity that reads the property values from a
	 * NodeInstance Property response and sets the given variables
	 * 
	 * @param assignName
	 *            the name of the assign activity
	 * @param nodeInstancePropertyResponseVarName
	 *            the name of the variable holding the property data
	 * @param propElement2BpelVarNameMap
	 *            a Map from DOM Elements (representing Node Properties) to BPEL
	 *            variable names
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 * @throws SAXException
	 *             is thrown when parsing internal files fail
	 */
	public Node createAssignFromNodeInstancePropertyToBPELVariableAsNode(String assignName,
			String nodeInstancePropertyResponseVarName, Map<Element, String> propElement2BpelVarNameMap)
			throws IOException, SAXException {
		String templateString = this.createAssignFromNodeInstancePropertyToBPELVariableAsString(assignName,
				nodeInstancePropertyResponseVarName, propElement2BpelVarNameMap);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Creates a RESTExtension GET to fetch properties of NodeInstance
	 * 
	 * @param nodeInstanceIDUrl
	 *            the name of the variable holding the address to the
	 *            nodeInstance
	 * @param responseVarName
	 *            the name of the variable to store the response into
	 * @return a String containing a BPEL RESTExtension Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 */
	public String createRESTExtensionGETForNodeInstancePropertiesAsString(String nodeInstanceIDUrl,
			String responseVarName) throws IOException {
		// <!-- $urlVarName, $ResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_Properties.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("$urlVarName", nodeInstanceIDUrl);
		template = template.replace("$ResponseVarName", responseVarName);

		return template;
	}

	/**
	 * Creates a RESTExtension GET to fetch properties of NodeInstance
	 * 
	 * @param nodeInstanceIDUrl
	 *            the name of the variable holding the address to the
	 *            nodeInstance
	 * @param responseVarName
	 *            the name of the variable to store the response into
	 * @return a Node containing a BPEL RESTExtension Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 * @throws SAXException
	 *             is thrown when parsing internal files fails
	 */
	public Node createRESTExtensionGETForNodeInstancePropertiesAsNode(String nodeInstanceIDUrl, String responseVarName)
			throws IOException, SAXException {
		String templateString = this.createRESTExtensionGETForNodeInstancePropertiesAsString(nodeInstanceIDUrl,
				responseVarName);
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
	 * @param assignName
	 *            the name of the assign
	 * @param stringVarName
	 *            the name of the xsd:string variable to write the
	 *            NodeInstanceId into
	 * @param nodeInstanceResponseVarName
	 *            the instanceDataAPI response to fetch the NodeInstanceId from
	 * @return a String containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 */
	public String createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(String assignName,
			String stringVarName, String nodeInstanceResponseVarName) throws IOException {
		// <!-- $assignName, $stringVarName, $NodeInstanceResponseVarName -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BpelAssignFromNodeInstanceRequestToStringVar.xml");
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
	 * @param assignName
	 *            the name of the assign
	 * @param stringVarName
	 *            the name of the xsd:string variable to write the
	 *            NodeInstanceId into
	 * @param nodeInstanceResponseVarName
	 *            the instanceDataAPI response to fetch the NodeInstanceId from
	 * @return a Node containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 * @throws SAXException
	 *             is thrown when parsing internal files fails
	 */
	public Node createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode(String assignName,
			String stringVarName, String nodeInstanceResponseVarName) throws SAXException, IOException {
		String templateString = this.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(assignName,
				stringVarName, nodeInstanceResponseVarName);
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
	 * @param instanceDataUrlVar
	 *            the name of the variable holding an URL to a instanceDataAPI
	 * @param responseVarName
	 *            the name of the variable holding the response of the request
	 *            (must be xsd:anyType)
	 * @param templateId
	 *            the id of the template the instance belongs to
	 * @param serviceInstanceUrlVarName
	 *            the name of the variable holding the id/link of the
	 *            serviceInstance
	 * @param isNodeTemplate
	 *            whether the given tmeplateId belongs to a NodeTemplate or
	 *            RelationshipTemplate
	 * @return a String containing a BPEL Fragment
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 */
	public String createRESTExtensionGETForNodeInstanceDataAsString(String instanceDataUrlVar, String responseVarName,
			QName templateId, String serviceInstanceUrlVarName, boolean isNodeTemplate) throws IOException {
		// <!-- $InstanceDataURLVar, $ResponseVarName, $TemplateId,
		// $serviceInstanceUrlVarName, $templateType -->

		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("$InstanceDataURLVar", instanceDataUrlVar);
		template = template.replace("$ResponseVarName", responseVarName);
		template = template.replace("$TemplateId", templateId.toString());
		template = template.replace("$serviceInstanceUrlVarName", serviceInstanceUrlVarName);
		if (isNodeTemplate) {
			template = template.replace("$templateType", "nodeTemplateID");
		} else {
			template = template.replace("$templateType", "relationshipTemplateID");
		}
		return template;
	}

	/**
	 * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to
	 * request on.
	 * 
	 * @param bpelVarName
	 *            the variable containing an URL
	 * @param responseVarName
	 *            the variable to hold the response
	 * @return a String containing a BPEL4RESTLight Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 */
	public String createRESTDeleteOnURLBPELVarAsString(String bpelVarName, String responseVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightDELETE.xml");
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
	 * @param bpelVarName
	 *            the variable containing an URL
	 * @param responseVarName
	 *            the variable to hold the response
	 * @return a String containing a BPEL4RESTLight Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 * @throws SAXException
	 *             is thrown when parsing internal files fails
	 */
	public Node createRESTDeleteOnURLBPELVarAsNode(String bpelVarName, String responseVarName)
			throws IOException, SAXException {
		String templateString = this.createRESTDeleteOnURLBPELVarAsString(bpelVarName, responseVarName);
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
	 * @param instanceDataUrlVar
	 *            the name of the variable holding an URL to a instanceDataAPI
	 * @param responseVarName
	 *            the name of the variable holding the response of the request
	 *            (must be xsd:anyType)
	 * @param templateId
	 *            the id of the template the instance belongs to
	 * @param serviceInstanceUrlVarName
	 *            the name of the variable holding the id/link of the
	 *            serviceInstance
	 * @param isNodeTemplate
	 *            whether the given tmeplateId belongs to a NodeTemplate or
	 *            RelationshipTemplate
	 * @return a Node containing a BPEL Fragment
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 * @throws SAXException
	 *             is thrown when parsing internal files fails
	 */
	public Node createRESTExtensionGETForNodeInstanceDataAsNode(String instanceDataUrlVar, String responseVarName,
			QName templateId, String serviceInstanceUrlVarName, boolean isNodeTemplate)
			throws SAXException, IOException {
		String templateString = this.createRESTExtensionGETForNodeInstanceDataAsString(instanceDataUrlVar,
				responseVarName, templateId, serviceInstanceUrlVarName, isNodeTemplate);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance
	 * POST response
	 * 
	 * @param serviceInstanceResponseVarName
	 *            the var name of the POST response
	 * @param serviceInstanceURLVarName
	 *            the var name to save the URL/ID into
	 * @return a String containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 */
	public String generateServiceInstanceURLVarAssignAsString(String serviceInstanceResponseVarName,
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

	/**
	 * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance
	 * POST response
	 * 
	 * @param serviceInstanceResponseVarName
	 *            the var name of the POST response
	 * @param serviceInstanceURLVarName
	 *            the var name to save the URL/ID into
	 * @return a String containing a BPEL assign activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 * @throws SAXException
	 *             is thrown when parsing internal files fail
	 */
	public Node generateServiceInstanceURLVarAssignAsNode(String serviceInstanceResponseVarName,
			String serviceInstanceURLVarName) throws IOException, SAXException {
		String templateString = this.generateServiceInstanceURLVarAssignAsString(serviceInstanceResponseVarName,
				serviceInstanceURLVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 * 
	 * @param instanceDataAPIUrlVariableName
	 *            the name of the variable holding the address to the
	 *            instanceDataAPI
	 * @param csarId
	 *            the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId
	 *            the id of the serviceTemplate
	 * @param responseVariableName
	 *            a name of an anyType variable to save the response into
	 * @return a String containing a BPEL4RESTLight POST extension activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 */
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

	/**
	 * Generates a BPEL POST at the given InstanceDataAPI with the given
	 * ServiceTemplate id to create a Service Instance
	 * 
	 * @param instanceDataAPIUrlVariableName
	 *            the name of the variable holding the address to the
	 *            instanceDataAPI
	 * @param csarId
	 *            the name of the csar the serviceTemplate belongs to
	 * @param serviceTemplateId
	 *            the id of the serviceTemplate
	 * @param responseVariableName
	 *            a name of an anyType variable to save the response into
	 * @return a Node containing a BPEL4RESTLight POST extension activity
	 * @throws IOException
	 *             is thrown when reading internal files fail
	 * @throws SAXException
	 *             is thrown when parsing internal files fail
	 */
	public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(String instanceDataAPIUrlVariableName, String csarId,
			QName serviceTemplateId, String responseVariableName) throws IOException, SAXException {
		String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, csarId,
				serviceTemplateId, responseVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL If activity that throws the given fault when the given
	 * expr evaluates to true at runtime
	 * 
	 * @param xpath1Expr
	 *            a XPath 1.0 expression as String
	 * @param faultQName
	 *            a QName denoting the fault to be thrown when the if evaluates
	 *            to true
	 * @return a String containing a BPEL If Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 */
	public String generateBPELIfTrueThrowFaultAsString(String xpath1Expr, QName faultQName) throws IOException {
		// <!-- $xpath1Expr, $faultPrefix, $faultNamespace, $faultLocalName-->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPELIfTrueThrowFault.xml");
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
	 * @param xpath1Expr
	 *            a XPath 1.0 expression as String
	 * @param faultQName
	 *            a QName denoting the fault to be thrown when the if evaluates
	 *            to true
	 * @return a Node containing a BPEL If Activity
	 * @throws IOException
	 *             is thrown when reading internal files fails
	 * @throws SAXException
	 *             is thrown when parsing internal files fails
	 */
	public Node generateBPELIfTrueThrowFaultAsNode(String xpath1Expr, QName faultQName)
			throws IOException, SAXException {
		String templateString = this.generateBPELIfTrueThrowFaultAsString(xpath1Expr, faultQName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
