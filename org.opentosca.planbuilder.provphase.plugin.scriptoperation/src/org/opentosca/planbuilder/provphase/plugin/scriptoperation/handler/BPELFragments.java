package org.opentosca.planbuilder.provphase.plugin.scriptoperation.handler;

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
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.TemplatePropWrapper;
import org.opentosca.planbuilder.provphase.plugin.scriptoperation.handler.Handler.ParamWrapper;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contaings all BPEL fragments the script operation plugins needs,
 * to implement the needed logic
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
	 * @throws ParserConfigurationException is thrown when initializing internal
	 *             parsers fails
	 */
	public BPELFragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
	}
	
	/**
	 * Returns the WSDL PortType of the EC2 Linux Service
	 * 
	 * @return a QName containing a PortType
	 */
	public QName getPortTypeFromLinuxUploadWSDL() {
		return new QName("http://ec2linux.aws.ia.opentosca.org", "EC2LinuxIAService", "ec2linuxport");
	}
	
	/**
	 * Returns the WSDL File for the ServiceInvoker Service
	 * 
	 * @return a File containing an absolute path to a WSDL file
	 * @throws IOException
	 */
	public File getSIInvokerWSDLFile() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("invoker.wsdl");
		return new File(FileLocator.toFileURL(url).getPath());
	}
	
	/**
	 * Returns the ServiceInvoker XSD File
	 * 
	 * @return a File containing an absolute path to a XSD file
	 * @throws IOException is thrown when reading internal files fails
	 */
	public File getSIInvokerXSDFile() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("invoker.xsd");
		return new File(FileLocator.toFileURL(url).getPath());
	}
	
	/**
	 * Returns the WSDL contract of the EC2 Linux Service
	 * 
	 * @return a File containing an absolute path to a WSDL file
	 * @throws IOException is thrown when reading internal files fails
	 */
	public File getEC2LinuxIAWsdl() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("EC2LinuxIAService.wsdl");
		return new File(FileLocator.toFileURL(url).getPath());
	}
	
	/**
	 * Generates a BPEL Fragment inside a String that assign a EC2 Linux
	 * RunScript request
	 * 
	 * @param assignName the name for the assign
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param requestVarName the name of the EC2 Linux RunScript request
	 *            variable
	 * @param serverIpName the name of a variable containing an address to a
	 *            linux machine
	 * @param inputMessageVarName the name of the BuildPlan input message
	 * @param script the script to run on the remote machine
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateAssignRequestMsgAsString(String assignName, String prefix, String requestVarName, String serverIpName, String inputMessageVarName, String script) throws IOException {
		// <!--
		// {assignName}{prefix}{requestVarName}{serverIpVarName}{requestVarName}{inputMessageVarName}{script}
		// -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assRunScriptRequest.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("{assignName}", assignName);
		template = template.replace("{prefix}", prefix);
		template = template.replace("{requestVarName}", requestVarName);
		template = template.replace("{serverIpVarName}", serverIpName);
		template = template.replace("{inputMessageVarName}", inputMessageVarName);
		template = template.replace("{script}", script);
		return template;
	}
	
	/**
	 * Generates a BPEL Fragment inside a DOM Node that assign a EC2 Linux
	 * RunScript request
	 * 
	 * @param assignName the name for the assign
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param requestVarName the name of the EC2 Linux RunScript request
	 *            variable
	 * @param serverIpName the name of a variable containing an address to a
	 *            linux machine
	 * @param inputMessageVarName the name of the BuildPlan input message
	 * @param script the script to run on the remote machine
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when transforming internal data to DOM
	 *             fails
	 */
	public Node generateAssignRequestMsgAsNode(String assignName, String prefix, String requestVarName, String serverIpName, String inputMessageVarName, String script) throws IOException, SAXException {
		String templateString = this.generateAssignRequestMsgAsString(assignName, prefix, requestVarName, serverIpName, inputMessageVarName, script);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a BASH script (call) that executes the given script at the
	 * given path.
	 * 
	 * @param scriptPath the path of the script
	 * @param inputMappings a Map containing mappings from TOSCA Parameter to
	 *            script variable
	 * @param inputParamPropMappings a Map containing mappings from TOSCA
	 *            Parameters to Template Properties
	 * @return a String containing a valid call of the given script
	 */
	public String generateScriptCall(String scriptPath, Map<String, ParamWrapper> inputMappings, Map<String, TemplatePropWrapper> inputParamPropMappings) {
		String scriptCall = "concat('sudo ";
		
		if (inputMappings.keySet().isEmpty()) {
			scriptCall += "sh ','" + scriptPath + " ')";
			return scriptCall;
		}
		
		for (String toscaParamKey : inputMappings.keySet()) {
			if (inputParamPropMappings.get(toscaParamKey) != null) {
				String scriptParam = inputMappings.get(toscaParamKey).getScriptParamName();
				String propVariableName = inputParamPropMappings.get(toscaParamKey).getPropertyLocalName();
				scriptCall += " " + scriptParam + "=',$" + propVariableName + ", '";
			}
		}
		
		scriptCall += " sh " + scriptPath + " ')";
		return scriptCall.replace("  ", " ");
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
	
}
