package org.opentosca.planbuilder.fragments;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

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

		String assignString = "<bpel:assign name=\"" + assignName + "\"xmlns:bpel=\"" + BuildPlan.bpelNamespace + "\">";

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
			String templateId, String serviceInstanceUrlVarName, boolean isNodeTemplate) throws IOException {
		// <!-- $InstanceDataURLVar, $ResponseVarName, $TemplateId,
		// $serviceInstanceUrlVarName, $templateType -->

		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("$InstanceDataURLVar", instanceDataUrlVar);
		template = template.replace("$ResponseVarName", responseVarName);
		template = template.replace("$TemplateId", templateId);
		template = template.replace("$serviceInstanceUrlVarName", serviceInstanceUrlVarName);
		if (isNodeTemplate) {
			template = template.replace("$templateType", "nodeTemplate");
		} else {
			template = template.replace("$templateType", "relationshipTemplate");
		}
		return template;
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
			String templateId, String serviceInstanceUrlVarName, boolean isNodeTemplate)
			throws SAXException, IOException {
		String templateString = this.createRESTExtensionGETForNodeInstanceDataAsString(instanceDataUrlVar,
				responseVarName, templateId, serviceInstanceUrlVarName, isNodeTemplate);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
