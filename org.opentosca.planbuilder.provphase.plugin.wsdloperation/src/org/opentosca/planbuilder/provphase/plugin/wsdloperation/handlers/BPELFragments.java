package org.opentosca.planbuilder.provphase.plugin.wsdloperation.handlers;

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
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class holds all fragments used by the WSDL Operation plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELFragments {

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;


	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException is thrown when initializing the
	 *             internal parsers fails
	 */
	public BPELFragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	}

	/**
	 * Generates a BPEL Assign as String. The Assign initializes a WSDL Request
	 * Message from appropriate sources, like from plan input message in case of
	 * external parameters or from internal property variables
	 *
	 * @param MessageDeclId A QName of the XML Schema Definition Element, which
	 *            is used as Request Message
	 * @param variableName the name of the variable for the request message
	 * @param part the part name of the message variable
	 * @param toscaWsdlMappings Mappings of TOSCA Input Parameters to WSDL
	 *            Request Message Elements (local names)
	 * @param paramPropertyMappings Mappings of TOSCA Properties and TOSCA Input
	 *            Parameters
	 * @param assignName the name of the assign to generate
	 * @param planInputMessageName the variable name of the plan input message
	 * @param planInputMessagePartName the part name of the plan input message
	 *            variable
	 * @return a BPEL Assign as DOM Node
	 */
	public Node getGenericAssignAsNode(QName MessageDeclId, String variableName, String part, Map<String, String> toscaWsdlMappings, Map<String, Variable> paramPropertyMappings, String assignName, String planInputMessageName, String planInputMessagePartName) throws SAXException, IOException {
		String templateString = this.getGenericAssignAsString(MessageDeclId, variableName, part, toscaWsdlMappings, paramPropertyMappings, assignName, planInputMessageName, planInputMessagePartName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL copy element which assigns a message element with a
	 * literal consisting of some digits
	 *
	 * @param MessageDeclId the XML Schema Definition
	 * @param variableName the name of the message variable
	 * @param partName the part name
	 * @param localName the localName of the element inside the message
	 * @return a DOM Node
	 * @throws SAXException
	 * @throws IOException
	 */
	public Node getGenericRandomLiteralCopyAsNode(QName MessageDeclId, String variableName, String partName, String localName) throws SAXException, IOException {
		// <bpel:copy
		// xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:from><bpel:literal>SOMEDIGITS</bpel:literal></bpel:from><bpel:to variable=\""+variableName+"\" part=\""+partName+"\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA["+MessageDeclId.getPrefix()
		// + ":" + localName+ "]]></bpel:query></bpel:to></bpel:copy>
		String copyString = "<bpel:copy xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:from><bpel:literal>" + System.currentTimeMillis() + "</bpel:literal></bpel:from><bpel:to variable=\"" + variableName + "\" part=\"" + partName + "\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + MessageDeclId.getPrefix() + ":" + localName + "]]></bpel:query></bpel:to></bpel:copy>";
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(copyString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Assign as String. The Assign initializes a WSDL Request
	 * Message from appropriate sources, like from plan input message in case of
	 * external parameters or from internal property variables
	 *
	 * @param MessageDeclId A QName of the XML Schema Definition Element, which
	 *            is used as Request Message
	 * @param variableName the name of the variable for the request message
	 * @param part the part name of the message variable
	 * @param toscaWsdlMappings Mappings of TOSCA Input Parameters to WSDL
	 *            Request Message Elements (local names)
	 * @param paramPropertyMappings Mappings of TOSCA Properties and TOSCA Input
	 *            Parameters
	 * @param assignName the name of the assign to generate
	 * @param planInputMessageName the variable name of the plan input message
	 * @param planInputMessagePartName the part name of the plan input message
	 *            variable
	 * @return a BPEL Assign as String
	 */
	public String getGenericAssignAsString(QName MessageDeclId, String variableName, String part, Map<String, String> toscaWsdlMappings, Map<String, Variable> paramPropertyMappings, String assignName, String planInputMessageName, String planInputMessagePartName) {
		String genericAssignAsString = "";
		String bpelAssignBeginString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" validate=\"no\" name=\"" + assignName + "\"><bpel:copy><bpel:from><bpel:literal>";
		String bpelAssignLiteralInitBeginString = "";
		// Setup root element of literal
		// Example: <tns:MoodleBuildPlanResponse
		// xmlns:tns="http:///www.opentosca.org/examples/Moodle/BuildPlan"
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		bpelAssignLiteralInitBeginString += "<" + MessageDeclId.getPrefix() + ":" + MessageDeclId.getLocalPart() + " xmlns:" + MessageDeclId.getPrefix() + "=\"" + MessageDeclId.getNamespaceURI() + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

		// setup child elements of message variable
		String bpelAssignLiteralInitChildElementsString = "";
		for (String toscaParam : toscaWsdlMappings.keySet()) {
			String messageChildLocalName = toscaWsdlMappings.get(toscaParam);
			String elementDecl = MessageDeclId.getPrefix() + ":" + messageChildLocalName;
			bpelAssignLiteralInitChildElementsString += "<" + elementDecl + ">" + elementDecl + "</" + elementDecl + ">";
		}

		// end strings
		String bpelAssignLiteralInitEndString = "</" + MessageDeclId.getPrefix() + ":" + MessageDeclId.getLocalPart() + ">";
		String bpelAssignEndInitializeString = "</bpel:literal></bpel:from><bpel:to variable=\"" + variableName + "\"" + ((part != null) ? " part=\"" + part + "\"" : "") + "></bpel:to></bpel:copy>";

		genericAssignAsString += bpelAssignBeginString;
		genericAssignAsString += bpelAssignLiteralInitBeginString;
		genericAssignAsString += bpelAssignLiteralInitChildElementsString;
		genericAssignAsString += bpelAssignLiteralInitEndString;
		genericAssignAsString += bpelAssignEndInitializeString;

		// generate copies for property variable to request and input data to
		// request
		// Example:
		// <bpel:copy>
		// <bpel:from variable="aws_InstanceType"></bpel:from>
		// <bpel:to part="parameters"
		// variable="webapp_createEC2Instance_Request">
		// <bpel:query
		// queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![CDATA[ns:instanceType]]></bpel:query>
		// </bpel:to>
		// </bpel:copy>

		for (String toscaParam : toscaWsdlMappings.keySet()) {

			// check whether toscaParam is a _DUMMY_KEY_, if yes there is no
			// assign to be done here
			if (toscaParam.contains("_DUMMY_KEY_")) {
				continue;
			}

			Variable propWrapper = paramPropertyMappings.get(toscaParam);
			if (propWrapper == null) {
				// toscaParam is external, data comes from main plan input
				// message
				String externalCopyFromString = "<bpel:copy><bpel:from variable=\"" + planInputMessageName + "\" part=\"" + planInputMessagePartName + "\">";
				String fromQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + "tns:" + toscaParam + "]]></bpel:query></bpel:from>";
				String externalCopyToString = "<bpel:to part=\"" + part + "\" variable=\"" + variableName + "\">";
				String ToQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + MessageDeclId.getPrefix() + ":" + toscaWsdlMappings.get(toscaParam) + "]]></bpel:query></bpel:to></bpel:copy>";
				genericAssignAsString += externalCopyFromString;
				genericAssignAsString += fromQueryString;
				genericAssignAsString += externalCopyToString;
				genericAssignAsString += ToQueryString;

			} else {
				// toscaParam is internal, data comes from property variable
				String internalCopyFromString = "<bpel:copy><bpel:from variable=\"" + propWrapper.getName() + "\"></bpel:from>";
				String internalCopyToString = "<bpel:to part=\"" + part + "\" variable=\"" + variableName + "\">";
				String ToQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + MessageDeclId.getPrefix() + ":" + toscaWsdlMappings.get(toscaParam) + "]]></bpel:query></bpel:to></bpel:copy>";
				genericAssignAsString += internalCopyFromString;
				genericAssignAsString += internalCopyToString;
				genericAssignAsString += ToQueryString;
			}

		}
		genericAssignAsString += "</bpel:assign>";

		return genericAssignAsString;
	}

	/**
	 * Generates an BPEL Invoke Element as String.
	 *
	 * @param invokeName the name attribute of the Invoke Element
	 * @param partnerLinkName the partnerLink attribute of the invoke
	 * @param operationName the name of the operation used on the given porttype
	 * @param portType the porttype to call on
	 * @param inputVarName the input variable name
	 * @param outputVarName the output variable name
	 * @return BPEL Invoke Element as String
	 */
	public String generateInvokeAsString(String invokeName, String partnerLinkName, String operationName, QName portType, String inputVarName, String outputVarName) {
		// Example:
		// <bpel:invoke name="getPublicDNS" partnerLink="EC2VMIAServicePL"
		// operation="getPublicDNS" portType="ns:EC2VMIAService"
		// inputVariable="webapp_getPublicDNS_Request"
		// outputVariable="webapp_getPublicDNS_Response"></bpel:invoke>
		String invokeAsString = "<bpel:invoke xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + invokeName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\"" + " portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart() + "\"" + " inputVariable=\"" + inputVarName + "\"" + " outputVariable=\"" + outputVarName + "\"></bpel:invoke>";
		return invokeAsString;
	}

	/**
	 * Generates a String containing a BPEL invoke element
	 *
	 * @param invokeName the name of the invoke as String
	 * @param partnerLinkName the name of the partnerLink used as String
	 * @param operationName the name of the WSDL operation as String
	 * @param portType a QName denoting the WSDL portType
	 * @param inputVarName the name of the BPEL Variable to use as Input, given
	 *            as String
	 * @return a String containing a complete BPEL Invoke element
	 */
	public String generateInvokeAsString(String invokeName, String partnerLinkName, String operationName, QName portType, String inputVarName) {
		return "<bpel:invoke xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + invokeName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\"" + " portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart() + "\"" + " inputVariable=\"" + inputVarName + "\"></bpel:invoke>";
	}

	/**
	 * Generates a DOM Node containing a BPEL invoke element
	 *
	 * @param invokeName the name of the invoke as String
	 * @param partnerLinkName the name of the partnerLink used as String
	 * @param operationName the name of the WSDL operation as String
	 * @param portType a QName denoting the WSDL portType
	 * @param inputVarName the name of the BPEL Variable to use as Input, given
	 *            as String
	 * @return a DOM Node containing a complete BPEL Invoke element
	 */
	public Node generateInvokeAsNode(String invokeName, String partnerLinkName, String operationName, QName portType, String inputVarName) throws SAXException, IOException {
		String invokeString = this.generateInvokeAsString(invokeName, partnerLinkName, operationName, portType, inputVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(invokeString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Receive Element
	 *
	 * @param receiveName a name for the receive as String
	 * @param partnerLinkName the name of a BPEL partnerLink as String
	 * @param operationName the name of a WSDL operation as String
	 * @param portType the reference to a WSDL portType as QName
	 * @param variableName a name of a BPEL Variable as String
	 * @return a DOM Node containing a complete BPEL Receive element
	 * @throws SAXException is thrown when parsing internal data to DOM
	 * @throws IOException is thrown when reading internal files fails
	 */
	public Node generateReceiveAsNode(String receiveName, String partnerLinkName, String operationName, QName portType, String variableName) throws SAXException, IOException {
		String receiveString = "<bpel:receive xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + receiveName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\" portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart() + "\" variable=\"" + variableName + "\"/>";
		/*
		 * <bpel:receive name="ReceiveCreateEC2Instance"
		 * operation="createEC2InstanceCallback" partnerLink="ec2VmPl1"
		 * portType="ns0:EC2VMIAAsyncServiceCallback"
		 * variable="createEc2Response3"> <bpel:correlations> <bpel:correlation
		 * initiate="no" set="createEc2CorrelationSet8"/> </bpel:correlations>
		 * </bpel:receive>
		 */
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(receiveString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates an BPEL Invoke Element as String.
	 *
	 * @param invokeName the name attribute of the Invoke Element
	 * @param partnerLinkName the partnerLink attribute of the invoke
	 * @param operationName the name of the operation used on the given porttype
	 * @param portType the porttype to call on
	 * @param inputVarName the input variable name
	 * @param outputVarName the output variable name
	 * @return BPEL Invoke Element as Node
	 */
	public Node generateInvokeAsNode(String invokeName, String partnerLinkName, String operationname, QName portType, String inputVarName, String outputVarName) throws SAXException, IOException {
		String templateString = this.generateInvokeAsString(invokeName, partnerLinkName, operationname, portType, inputVarName, outputVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Correlations element to us with BPEL Invoke and Receive
	 * elements
	 *
	 * @param correlationSetName the name of the correlationSet to use
	 * @param initiate whether the correlationSet must be initialized or not
	 * @return a DOM Node containing a complete BPEL Correlations element
	 * @throws SAXException is thrown when parsing internal data fails
	 * @throws IOException is thrown when reading internal data fails
	 */
	public Node generateCorrelationSetsAsNode(String correlationSetName, boolean initiate) throws SAXException, IOException {
		String correlationSetsString = "<bpel:correlations xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:correlation set=\"" + correlationSetName + "\" initiate=\"" + ((initiate) ? "yes" : "no") + "\"/></bpel:correlations>";
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(correlationSetsString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Wait element
	 *
	 * @param name the name for the wait element as String
	 * @param minutes int denoting the minutes to wait
	 * @param seconds int denoting the seconds to wait
	 * @return a String containing a complete BPEL Wait element
	 * @throws SAXException is thrown when parsing internal data fails
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateWaitAsString(String name, int minutes, int seconds) {
		return "<bpel:wait xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + name + "\"> <bpel:for>'P0Y0M0DT0H" + minutes + "M" + seconds + "S'</bpel:for></bpel:wait>";
	}

	/**
	 * Generates a BPEL Copy element to use in BPEL Assigns, which sets the
	 * WS-Addressing ReplyTo Header for the specified request variable
	 *
	 * @param partnerLinkName the name of the BPEL partnerLink that will be used
	 *            as String
	 * @param requestVariableName the name of the BPEL Variable used for an
	 *            asynchronous request as String
	 * @return a String containing a complete BPEL Copy element
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateAddressingCopy(String partnerLinkName, String requestVariableName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("addressingCopy.xml");
		File addressingFile = new File(FileLocator.toFileURL(url).getPath());
		String addressingFileString = FileUtils.readFileToString(addressingFile);
		/*
		 * "{partnerLinkName}" "{requestVarName}"
		 */
		addressingFileString = addressingFileString.replace("{requestVarName}", requestVariableName);
		addressingFileString = addressingFileString.replace("{partnerLinkName}", partnerLinkName);

		return addressingFileString;

	}

	/**
	 * Generates a BPEL Copy element to use in BPEL Assigns, which sets the
	 * WS-Addressing ReplyTo Header for the specified request variable
	 *
	 * @param partnerLinkName the name of the BPEL partnerLink that will be used
	 *            as String
	 * @param requestVariableName the name of the BPEL Variable used for an
	 *            asynchronous request as String
	 * @return a DOM Node containing a complete BPEL Copy element
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal data to DOM fails
	 */
	public Node generateAddressingCopyAsNode(String partnerLinkName, String requestVariableName) throws IOException, SAXException {
		String addressingCopyString = this.generateAddressingCopy(partnerLinkName, requestVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(addressingCopyString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Copy which sets a dummy WS-Addressing ReplyTo Header on
	 * the given request variable
	 *
	 * @param requestVariableName the name of a BPEL Variable as String
	 * @return a String containing a complete BPEL Copy element
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateAddressingInit(String requestVariableName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("addressingInit.xml");
		File addressingFile = new File(FileLocator.toFileURL(url).getPath());
		String addressingFileString = FileUtils.readFileToString(addressingFile);
		/*
		 * "{partnerLinkName}" "{requestVarName}"
		 */
		addressingFileString = addressingFileString.replace("{requestVarName}", requestVariableName);
		return addressingFileString;

	}

	/**
	 * Generates a BPEL Copy which sets a dummy WS-Addressing ReplyTo Header on
	 * the given request variable
	 *
	 * @param requestVariableName the name of a BPEL Variable as String
	 * @return a DOM Node containing a complete BPEL Copy element
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal data to DOM fails
	 */
	public Node generateAddressingInitAsNode(String requestVariableName) throws IOException, SAXException {
		String addressingCopyString = this.generateAddressingInit(requestVariableName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(addressingCopyString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Empty element
	 *
	 * @return a String containing a BPEL Empty element
	 */
	public String generateEmptyAsString() {
		return "<bpel:empty xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"/>";
	}

	/**
	 * Generates a BPEL Empty element
	 *
	 * @return a DOM Node containing a BPEL Empty element
	 */
	public Node generateEmptyAsNode() throws SAXException, IOException {
		String templateString = this.generateEmptyAsString();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Extension for wait activities
	 *
	 * @param minutes int denoting the time in minutes to wait
	 * @param seconds int denoting the time in seconds to wait
	 * @return a String containing a complete BPEL Extension element
	 */
	public String generateExtWaitAsString(int minutes, int seconds) {
		// <bpel:extensionActivity>
		// <wait:WAIT minutes="1" />
		// </bpel:extensionActivity>
		return "<bpel:extensionActivity xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><wait:WAIT xmlns:wait=\"http://www.opentosca.org/bpel/extension/wait\" minutes=\"" + minutes + "\" seconds=\"" + seconds + "\" /></bpel:extensionActivity>";
	}

	/**
	 * Generates a BPEL Extension for wait activities
	 *
	 * @param minutes int denoting the time in minutes to wait
	 * @param seconds int denoting the time in seconds to wait
	 * @return a DOM Node containing a complete BPEL Extension element
	 * @throws SAXException is thrown when parsing internal data fails
	 * @throws IOException is thrown when reading internal files fails
	 */
	public Node generateExtWaitAsNode(int minutes, int seconds) throws SAXException, IOException {
		String templateString = this.generateExtWaitAsString(minutes, seconds);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates a BPEL Wait element
	 *
	 * @param name the name for the wait element as String
	 * @param minutes int denoting the minutes to wait
	 * @param seconds int denoting the seconds to wait
	 * @return a DOM Node containing a complete BPEL Wait element
	 * @throws SAXException is thrown when parsing internal data fails
	 * @throws IOException is thrown when reading internal files fails
	 */
	public Node generateWaitAsNode(String name, int minutes, int seconds) throws SAXException, IOException {
		String templateString = this.generateWaitAsString(name, minutes, seconds);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	/**
	 * Generates an BPEL Assign Element as String, which reads Response Message
	 * Data into internal PropertyVariables
	 *
	 * @param variableName the Response Message variable name
	 * @param part the part name of response message
	 * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL
	 *            Response message Elements
	 * @param paramPropertyMappings Mappings from TOSCA Output Parameters to
	 *            Properties
	 * @param assignName the name attribute of the assign
	 * @param MessageDeclId the XML Schema Declaration of the Response Message
	 *            as QName
	 * @return BPEL Assign Element as String
	 */
	public String generateResponseAssignAsString(String variableName, String part, Map<String, String> toscaWsdlMappings, Map<String, Variable> paramPropertyMappings, String assignName, QName MessageDeclId) {
		String assignAsString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + assignName + "\">";

		for (String toscaParam : paramPropertyMappings.keySet()) {
			if (toscaParam.contains("_DUMMY_KEY_")) {
				continue;
			}
			Variable propWrapper = paramPropertyMappings.get(toscaParam);
			if (propWrapper == null) {
				// TODO external parameter, add to plan output message
			} else {
				// interal parameter, assign response message element value to
				// internal property variable
				String internalCopyString = "<bpel:copy><bpel:from variable=\"" + variableName + "\" part=\"" + part + "\">";
				String internalQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + MessageDeclId.getPrefix() + ":" + toscaWsdlMappings.get(toscaParam) + "]]></bpel:query></bpel:from>";
				String internalToString = "<bpel:to variable=\"" + propWrapper.getName() + "\"/></bpel:copy>";
				assignAsString += internalCopyString;
				assignAsString += internalQueryString;
				assignAsString += internalToString;
			}
		}
		assignAsString += "</bpel:assign>";
		return assignAsString;
	}

	/**
	 * Generates an BPEL Assign Element as String, which reads Response Message
	 * Data into internal PropertyVariables
	 *
	 * @param variableName the Response Message variable name
	 * @param part the part name of response message
	 * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL
	 *            Response message Elements
	 * @param paramPropertyMappings Mappings from TOSCA Output Parameters to
	 *            Properties
	 * @param assignName the name attribute of the assign
	 * @param MessageDeclId the XML Schema Declaration of the Response Message
	 *            as QName
	 * @return BPEL Assign Element as DOM Node
	 */
	public Node generateResponseAssignAsNode(String variableName, String part, Map<String, String> toscaWsdlMappings, Map<String, Variable> paramPropertyMappings, String assignName, QName MessageDeclId) throws SAXException, IOException {
		String templateString = this.generateResponseAssignAsString(variableName, part, toscaWsdlMappings, paramPropertyMappings, assignName, MessageDeclId);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
