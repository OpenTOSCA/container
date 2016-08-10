package ba.prephase.plugin.linuxpackageda.handlers;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class holds BPEL Fragments, PortTypes and WSDL files for the Handler
 * inside this Linux Package DA Plugin
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
	 * Contructor
	 * 
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers failed
	 */
	public BPELFragments() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
	}
	
	/**
	 * Returns the WSDL file for the EC2 Linux IA WebService
	 * 
	 * @return a File with an absolute path to the WSDL
	 * @throws IOException if reading the file failed
	 */
	public File getLinuxFileUploadWSDLFile() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("EC2LinuxIAService.wsdl");
		File wsdlFile = new File(FileLocator.toFileURL(url).getPath());
		return wsdlFile;
	}
	
	/**
	 * Returns the WSDL PortType of the EC2 Linux IA WebService
	 * 
	 * @return a QName containing the PortType of the WebService
	 */
	public QName getPortTypeFromLinuxUploadWSDL() {
		return new QName("http://ec2linux.aws.ia.opentosca.org", "EC2LinuxIAService", "ec2linuxport");
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
