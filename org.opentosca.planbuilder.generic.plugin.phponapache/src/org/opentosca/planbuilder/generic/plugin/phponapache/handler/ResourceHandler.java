package org.opentosca.planbuilder.generic.plugin.phponapache.handler;

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
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class holds all BPEL Fragments and other Artifacts for the Generic
 * ApacheHTTP Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ResourceHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(ResourceHandler.class);
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	
	/**
	 * Constructor
	 * 
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers fails
	 */
	public ResourceHandler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
	}
	
	/**
	 * Returns the WSDL file of the EC2Linux IA WebService
	 * 
	 * @return a File containing the absolute path to the WSDL file
	 * @throws IOException is thrown when reading internal files fails
	 */
	public File getLinuxFileUploadWSDLFile() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("EC2LinuxIAService.wsdl");
		File wsdlFile = new File(FileLocator.toFileURL(url).getPath());
		return wsdlFile;
	}
	
	/**
	 * Loads a bash file into a string and returns it. The bash file contains
	 * logic to configure an apache http server on ubuntu
	 * 
	 * @return a String containing a complete bash script
	 */
	public String getConfigureShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("configure.sh");
		File configureShFile;
		try {
			configureShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't load configure.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(configureShFile);
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't read string from file configure.sh", e);
			return null;
		}
	}
	
	public String getPhpStartShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("php_start.sh");
		File phpStartShFile;
		try {
			phpStartShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't load php_start.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(phpStartShFile);
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't read string from file php_start.sh", e);
			return null;
		}
	}
	
	/**
	 * Loads a bash file into a string and returns it. The bash file contains
	 * logic to configure an apache http server on ubuntu
	 * 
	 * @return a String containing a complete bash script
	 */
	public String getStartShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("start.sh");
		File startShFile;
		try {
			startShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't load start.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(startShFile);
		} catch (IOException e) {
			ResourceHandler.LOG.error("Couldn't read string from file start.sh", e);
			return null;
		}
	}
	
	/**
	 * Returns the WSDL PortType of the EC2Linux IA WebService
	 * 
	 * @return a QName denoting the PortType of the EC2Linux IA WebService
	 */
	public QName getPortTypeFromLinuxUploadWSDL() {
		return new QName("http://ec2linux.aws.ia.opentosca.org", "EC2LinuxIAService", "ec2linuxport");
	}
	
	/**
	 * Returns the openTOSCA References Schema file
	 * 
	 * @return a File containing the absolute path to the openTOSCA References
	 *         Schema file
	 * @throws IOException is thrown when reading internal files fails
	 */
	public File getOpenToscaReferencesSchema() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("openTOSCAReferencesSchema.xsd");
		File xsdFile = new File(FileLocator.toFileURL(url).getPath());
		return xsdFile;
	}
	
	/**
	 * Returns a String containing a BPEL Fragment to fetch Data from the
	 * openTOSCA ContainerAPI with the BPEL4RESTLight Extension
	 * 
	 * @param csarFileName the file name of the csar the build plan belongs to
	 * @param responseName the variable name of the response variable
	 * @param relativeFilePath a relative path on the containerAPI
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String getRESTExtensionGETAsString(String csarFileName, String responseName, String relativeFilePath) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("restExtensionGetFragment.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("{CSAR_filename}", csarFileName);
		template = template.replace("{response_var_name}", responseName);
		template = template.replace("{relative_path_to_file}", relativeFilePath);
		return template;
	}
	
	/**
	 * 
	 * Returns a DOM Node containing a BPEL Fragment to fetch Data from the
	 * openTOSCA ContainerAPI with the BPEL4RESTLight Extension
	 * 
	 * @param csarFileName the file name of the csar the build plan belongs to
	 * @param responseName the variable name of the response variable
	 * @param relativeFilePath a relative path on the containerAPI
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when parsing internal files fails
	 * 
	 */
	public Node getRESTExtensionGETAsNode(String csarFileName, String responseName, String relativeFilePath) throws SAXException, IOException {
		String templateString = this.getRESTExtensionGETAsString(csarFileName, responseName, relativeFilePath);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * <p>
	 * Returns a String containing a BPEL Fragment that assigns values to Ec2
	 * Linux RemoteFileTransfer Request
	 * </p>
	 * 
	 * @param assignName the name of the assign
	 * @param requestVarName the name of the FileTransferRequest variable
	 * @param prefix the prefix for the EC2 Linux Service
	 * @param serverIpVarName the name of a variable holding an address to a
	 *            linux machine
	 * @param planRequestName the name of BuildPlan input variable
	 * @param remoteFilePath the path of the file to be transfered
	 * @param remotePath the path for the file to upload unto the linux machine
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown if reading internal files fails
	 */
	public String getRemoteTransferFileAssignAsString(String assignName, String requestVarName, String prefix, String serverIpVarName, String planRequestName, String remoteFilePath, String remotePath) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignRemoteTransferFileRequestFragment.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{RequestVarName}", requestVarName);
		template = template.replace("{ServerIpPropVarName}", serverIpVarName);
		template = template.replace("{prefix}", prefix);
		template = template.replace("{remoteFilePath}", remoteFilePath);
		template = template.replace("{remotePath}", remotePath);
		template = template.replace("{planRequestName}", planRequestName);
		return template;
	}
	
	/**
	 * Generates a String containing a BPEL Fragment that assigns an EC2 Linux
	 * RunScript request
	 * 
	 * @param assignName the name for the assign
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param requestVarName the name of the RunScript request variable
	 * @param serverIpName the name of variable containing an address to a linux
	 *            machine
	 * @param inputMessageVarName the name of the BuildPlan input message
	 * @param script the script to execute on the remote machine
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
	 * Generates a DOM Node containing a BPEL Fragment that assigns an EC2 Linux
	 * RunScript request
	 * 
	 * @param assignName the name for the assign
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param requestVarName the name of the RunScript request variable
	 * @param serverIpName the name of variable containing an address to a linux
	 *            machine
	 * @param inputMessageVarName the name of the BuildPlan input message
	 * @param script the script to execute on the remote machine
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when transforming the internal files to a
	 *             DOM Node
	 */
	public Node generateAssignRequestMsgAsNode(String assignName, String prefix, String requestVarName, String serverIpName, String inputMessageVarName, String script) throws IOException, SAXException {
		String templateString = this.generateAssignRequestMsgAsString(assignName, prefix, requestVarName, serverIpName, inputMessageVarName, script);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * <p>
	 * Returns a DOM Node containing a BPEL Fragment that assigns values to Ec2
	 * Linux FileTransfer Request
	 * </p>
	 * 
	 * @param assignName the name of the assign
	 * @param requestVarName the name of the FileTransferRequest variable
	 * @param prefix the prefix for the EC2 Linux Service
	 * @param serverIpVarName the name of a variable holding an address to a
	 *            linux machine
	 * @param planRequestName the name of BuildPlan input variable
	 * @param remoteFilePath the path of the file to be transfered
	 * @param remotePath the path for the file to upload unto the linux machine
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown if reading internal files fails
	 * @throws SAXException is thrown if transforming internal files to DOM
	 *             fails
	 */
	public Node getRemoteTransferFileAssignAsNode(String assignName, String requestVarName, String prefix, String serverIpVarName, String planRequestName, String remoteFilePath, String remotePath) throws SAXException, IOException {
		String templateString = this.getRemoteTransferFileAssignAsString(assignName, requestVarName, prefix, serverIpVarName, planRequestName, remoteFilePath, remotePath);
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
	public String getRemoteFilePathString(String artifactPath) {
		ResourceHandler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
		String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
		return filePath;
	}
	
	/**
	 * Generates a String containing a BPEL Fragment that assigns values to an
	 * EC2 Linux TransferFile request
	 * 
	 * @param assignName the name for the assign
	 * @param requestVarName the name of the TranferFile request variable
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param serverIpVarName the name of a variable containing an address to a
	 *            linux machine
	 * @param planRequestName the name of the BuildPlan input message
	 * @param localPathVarName a local path of a file on the machine the
	 *            BuildPlan will be executed
	 * @param remotePath the remote path where the file must be uploaded to, on
	 *            the remote machine
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String getTransferFileAssignAsString(String assignName, String requestVarName, String prefix, String serverIpVarName, String planRequestName, String localPathVarName, String remotePath) throws IOException {
		// <!--
		// {AssignName},{RequestVarName},{ServerIpPropVarName},{prefix},{localPath},{remotePath},{SSHKey}
		// -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignTransferFileRequestFragment.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{RequestVarName}", requestVarName);
		template = template.replace("{ServerIpPropVarName}", serverIpVarName);
		template = template.replace("{prefix}", prefix);
		template = template.replace("{localFilePathVarName}", localPathVarName);
		template = template.replace("{remotePath}", remotePath);
		template = template.replace("{planRequestName}", planRequestName);
		return template;
	}
	
	/**
	 * Generates a DOM Node containing a BPEL Fragment that assigns values to an
	 * EC2 Linux TransferFile request
	 * 
	 * @param assignName the name for the assign
	 * @param requestVarName the name of the TranferFile request variable
	 * @param prefix the prefix of the EC2 Linux Service
	 * @param serverIpVarName the name of a variable containing an address to a
	 *            linux machine
	 * @param planRequestName the name of the BuildPlan input message
	 * @param localPathVarName a local path of a file on the machine the
	 *            BuildPlan will be executed
	 * @param remotePath the remote path where the file must be uploaded to, on
	 *            the remote machine
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when transforming internal data to DOM
	 *             fails
	 */
	public Node getTransferFileAssignAsNode(String assignName, String requestVarName, String prefix, String serverIpVarName, String planRequestName, String localPathVarName, String remotePath) throws IOException, SAXException {
		String templateString = this.getTransferFileAssignAsString(assignName, requestVarName, prefix, serverIpVarName, planRequestName, localPathVarName, remotePath);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Generates a String containing a BPEL Fragment that invokes an EC2 Linux
	 * Service with the transferFile operation
	 * 
	 * @param invokeName the name of the invoke
	 * @param partnerLinkName the name of the partnerLink
	 * @param portTypeprefix the prefix of the portType
	 * @param inputVarName the name of the input variable
	 * @param outputVarName the name of the output variable
	 * @param operationName the name of the operation
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String getTransferFileInvokeAsString(String invokeName, String partnerLinkName, String portTypeprefix, String inputVarName, String outputVarName, String operationName) throws IOException {
		// <!-- {InvokeName} {partnerlinkName} {portTypePrefix} {inputVarName}
		// {outputVarName}-->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("invokeTransferFile.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{InvokeName}", invokeName);
		template = template.replace("{partnerlinkName}", partnerLinkName);
		template = template.replace("{portTypePrefix}", portTypeprefix);
		template = template.replace("{inputVarName}", inputVarName);
		template = template.replace("{outputVarName}", outputVarName);
		template = template.replace("{operation}", operationName);
		return template;
	}
	
	/**
	 * Generates a DOM Node containing a BPEL Fragment that invokes an EC2 Linux
	 * Service with the transferFile operation
	 * 
	 * @param invokeName the name of the invoke
	 * @param partnerLinkName the name of the partnerLink
	 * @param portTypeprefix the prefix of the portType
	 * @param inputVarName the name of the input variable
	 * @param outputVarName the name of the output variable
	 * @param operationName the name of the operation
	 * @return a DOM Node containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when transforming internal data to DOM
	 *             fails
	 */
	public Node getTransferFileInvokeAsNode(String invokeName, String partnerLinkName, String portTypeprefix, String inputVarName, String outputVarName, String operationName) throws SAXException, IOException {
		String templateString = this.getTransferFileInvokeAsString(invokeName, partnerLinkName, portTypeprefix, inputVarName, outputVarName, operationName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
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
	
	/**
	 * Returns a String containing a BPEL Fragment to assign a request message
	 * of this plugin
	 * 
	 * @param assignName the name for the assign
	 * @param requestVarName the name of the request message
	 * @param publicDNSVarName the name of a variable holding an endpoint
	 * @param prefix the prefix for the request message
	 * @param packages a string containing packages names e.g "mysql php .."
	 * @param messageInputVarname the name of the plan input message
	 * @param partName the name of the part of the input message
	 * @param inputPrefix the prefix of the input message
	 * @return a String containing a complete BPELFragment
	 * @throws IOException is thrown when reading internal files fails
	 */
	public String generateRequestAssignAsString(String assignName, String requestVarName, String publicDNSVarName, String prefix, String packages, String messageInputVarname, String partName, String inputPrefix) throws IOException {
		// <!--
		// {prefix}{varName}{sshKeyVarName}{publicDNSVarName}{packages}{assignName}
		// -->
		
		/*
		 * <bpel:from variable="{messageInputVarName}" part="{partName}">
		 * <bpel:query
		 * queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0">
		 * <![CDATA[{inputPrefix}:sshKey]]> </bpel:query> </bpel:from>
		 */
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("packageAssign.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("{prefix}", prefix);
		template = template.replace("{varName}", requestVarName);
		template = template.replace("{assignName}", assignName);
		template = template.replace("{messageInputVarName}", messageInputVarname);
		template = template.replace("{partName}", partName);
		template = template.replace("{inputPrefix}", inputPrefix);
		template = template.replace("{publicDNSVarName}", publicDNSVarName);
		template = template.replace("{packages}", packages);
		return template;
	}
	
	/**
	 * 
	 * Returns a DOM Node containing a BPEL Fragment to assign a request message
	 * of this plugin
	 * 
	 * @param assignName the name for the assign
	 * @param requestVarName the name of the request message
	 * @param publicDNSVarName the name of a variable holding an endpoint
	 * @param prefix the prefix for the request message
	 * @param packages a string containing packages names e.g "mysql php .."
	 * @param messageInputVarname the name of the plan input message
	 * @param partName the name of the part of the input message
	 * @param inputPrefix the prefix of the input message
	 * @return a DOM Node containing a complete BPELFragment
	 * @throws IOException is thrown when reading internal files fails
	 * @throws SAXException is thrown when transforming from the internal
	 *             representation to the DOM representation fails
	 */
	public Node generateRequestAssignAsNode(String assignName, String requestVarName, String publicDNSVarName, String prefix, String packages, String messageInputVarname, String partName, String inputPrefix) throws IOException, SAXException {
		String template = this.generateRequestAssignAsString(assignName, requestVarName, publicDNSVarName, prefix, packages, messageInputVarname, partName, inputPrefix);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	/**
	 * Returns a String containing a BPEL Fragment which invokes the EC2 Linux
	 * IA Service to install a package
	 * 
	 * @param invokeName the name of the invoke
	 * @param partnerLinkName the name of the partnerLink
	 * @param portTypeprefix the prefix of the portType
	 * @param inputVarName the name of the request message
	 * @param outputVarName the name of the response message
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading the internal file fails
	 */
	public String getPackageInstallInvokeAsString(String invokeName, String partnerLinkName, String portTypeprefix, String inputVarName, String outputVarName) throws IOException {
		// <!-- {InvokeName} {partnerlinkName} {portTypePrefix} {inputVarName}
		// {outputVarName}-->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("invokePackageInstall.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{InvokeName}", invokeName);
		template = template.replace("{partnerlinkName}", partnerLinkName);
		template = template.replace("{portTypePrefix}", portTypeprefix);
		template = template.replace("{inputVarName}", inputVarName);
		template = template.replace("{outputVarName}", outputVarName);
		return template;
	}
	
	/**
	 * Returns a DOM Node containing a BPEL Fragment which invokes the EC2 Linux
	 * IA Service to install a package
	 * 
	 * @param invokeName the name of the invoke
	 * @param partnerLinkName the name of the partnerLink
	 * @param portTypeprefix the prefix of the portType
	 * @param inputVarName the name of the request message
	 * @param outputVarName the name of the response message
	 * @return a String containing a complete BPEL Fragment
	 * @throws IOException is thrown when reading the internal file fails
	 * @throws SAXException is thrown when transforming the internal
	 *             representation to DOM Node fails
	 */
	public Node getPackageInstallInvokeAsNode(String invokeName, String partnerLinkName, String portTypeprefix, String inputVarName, String outputVarName) throws SAXException, IOException {
		String templateString = this.getPackageInstallInvokeAsString(invokeName, partnerLinkName, portTypeprefix, inputVarName, outputVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
}
