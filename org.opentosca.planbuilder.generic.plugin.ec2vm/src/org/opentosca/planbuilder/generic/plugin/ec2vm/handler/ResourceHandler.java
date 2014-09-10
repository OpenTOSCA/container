package org.opentosca.planbuilder.generic.plugin.ec2vm.handler;

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

public class ResourceHandler {
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	
	/**
	 * Constructor
	 * 
	 * @throws ParserConfigurationException is thrown when initializing the
	 *             internal DocumentBuild fails
	 */
	public ResourceHandler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
	
	/**
	 * Returns the QName of the EC2VMIA-Async Service PortType
	 * 
	 * @return a QName denoting the EC2VM-Async Service PortType
	 */
	public QName getPortType() {
		return new QName("http://ec2vm.aws.ia.opentosca.org", "EC2VMIAAsyncService");
	}
	
	/**
	 * Returns the QName of the EC2VMIA-Async Service CallbackPortType
	 * 
	 * @return a QName denoting the EC2VM-Async Service CallbackPortType
	 */
	public QName getCallbackPortType() {
		return new QName("http://ec2vm.aws.ia.opentosca.org", "EC2VMIAAsyncServiceCallback");
	}
	
	public File getEC2WSDLFile() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("EC2VMIAAsyncService.wsdl");
		File wsdlFile = new File(FileLocator.toFileURL(url).getPath());
		return wsdlFile;
	}
	
	/**
	 * Returns the complete fragment to provision an EC2VM Stack as a DOM Node
	 * 
	 * @param ec2RequestVarName the name of createEc2 request variable
	 * @param ec2ResponseVarName the name of the createEc2 response variable
	 * @param ec2CorrelationSetName the name of the createEc2 correlatio set
	 * @param dnsRequestVarName the name of the getPublicDns request variable
	 * @param dnsResponseVarName the name of the getPublicDns response variable
	 * @param dnsCorrelationSetName the name of the getPublicDns correlationSet
	 * @param partnerLinkName the name of the ec2 partnerLink
	 * @param inputVarName the name of the BuildPlan input message
	 * @param inputVarPayloadName the name of the part name of the BuildPlan
	 *            input message
	 * @param ec2NamespacePrefix the prefix for the EC2VM namespaceh
	 * @param serverIpVarName the name of the variable holding a serverip
	 * @return a DOM Node with complete provisioning fragment for a EC2 Stack
	 * @throws SAXException is thrown when parsing the fragment fails
	 * @throws IOException is thrown when either parsing or reading the fragment
	 *             fails
	 */
	public Node getBPELFragmentAsNode(String ec2RequestVarName, String ec2ResponseVarName, String ec2CorrelationSetName, String dnsRequestVarName, String dnsResponseVarName, String dnsCorrelationSetName, String partnerLinkName, String inputVarName, String inputVarPayloadName, String ec2NamespacePrefix, String serverIpVarName) throws SAXException, IOException {
		String templateString = this.getBPELFragmentAsString(ec2RequestVarName, ec2ResponseVarName, ec2CorrelationSetName, dnsRequestVarName, dnsResponseVarName, dnsCorrelationSetName, partnerLinkName, inputVarName, inputVarPayloadName, ec2NamespacePrefix, serverIpVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	public String getBPELFragmentAsString(String ec2RequestVarName, String ec2ResponseVarName, String ec2CorrelationSetName, String dnsRequestVarName, String dnsResponseVarName, String dnsCorrelationSetName, String partnerLinkName, String inputVarName, String inputVarPayloadName, String ec2NamespacePrefix, String serverIpVarName) throws IOException {
		// TODO change method signature, e.g. "DTO"
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("asyncEc2InstanceGeneratorFragment.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		
		// {createEC2VarName} <=> ec2RequestVarName
		template = template.replace("{createEC2VarName}", ec2RequestVarName);
		// {createEc2ResponseVarName} <=> ec2ResponseVarName
		template = template.replace("{createEc2ResponseVarName}", ec2ResponseVarName);
		// {createEc2CorrelationSet} <=> ec2CorrelationSetName
		template = template.replace("{createEc2CorrelationSet}", ec2CorrelationSetName);
		// {getPublicDNSVarName} <=> dnsRequestVarName
		template = template.replace("{getPublicDNSVarName}", dnsRequestVarName);
		// {getPublicDNSResponseVarName} <=> dnsResponseVarName
		template = template.replace("{getPublicDNSResponseVarName}", dnsResponseVarName);
		// {getPublicDNSCorrelationSet} <=> dnsCorrelationSetName
		template = template.replace("{getPublicDNSCorrelationSet}", dnsCorrelationSetName);
		// {partnerLinkName} <=> partnerLinkName
		template = template.replace("{partnerLinkName}", partnerLinkName);
		// {inputVarName} <=> inputVarName
		template = template.replace("{inputVarName}", inputVarName);
		// {payloadInputVar} <=> inputVarPayloadName
		template = template.replace("{payloadInputVar}", inputVarPayloadName);
		// {ec2NamespacePrefix} <=> ec2NamespacePrefix
		template = template.replace("{ec2NamespacePrefix}", ec2NamespacePrefix);
		// {serverIpProp} <=> serverIpVarName
		template = template.replace("{serverIpProp}", serverIpVarName);
		
		// template = template.replace("{CSAR_filename}", csarFileName);
		// template = template.replace("{response_var_name}", responseName);
		// template = template.replace("{relative_path_to_file}",
		// relativeFilePath);
		
		return template;
	}
}
