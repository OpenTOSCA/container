package org.opentosca.planbuilder.prephase.plugin.fileupload.bpel.handler;

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
 * This class holds all BPEL Fragments and other Artifacts for the ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class ResourceHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceHandler.class);

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public ResourceHandler() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    }

    /**
     * Generates a DOM Node containing a BPEL Fragment that assigns an EC2 Linux RunScript request
     *
     * @param assignName the name for the assign
     * @param prefix the prefix of the EC2 Linux Service
     * @param requestVarName the name of the RunScript request variable
     * @param serverIpName the name of variable containing an address to a linux machine
     * @param inputMessageVarName the name of the BuildPlan input message
     * @param script the script to execute on the remote machine
     * @return a DOM Node containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when transforming the internal files to a DOM Node
     */
    public Node generateAssignRequestMsgAsNode(final String assignName, final String prefix,
                                               final String requestVarName, final String serverIpName,
                                               final String inputMessageVarName,
                                               final String script) throws IOException, SAXException {
        final String templateString = this.generateAssignRequestMsgAsString(assignName, prefix, requestVarName,
                                                                            serverIpName, inputMessageVarName, script);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a String containing a BPEL Fragment that assigns an EC2 Linux RunScript request
     *
     * @param assignName the name for the assign
     * @param prefix the prefix of the EC2 Linux Service
     * @param requestVarName the name of the RunScript request variable
     * @param serverIpName the name of variable containing an address to a linux machine
     * @param inputMessageVarName the name of the BuildPlan input message
     * @param script the script to execute on the remote machine
     * @return a String containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateAssignRequestMsgAsString(final String assignName, final String prefix,
                                                   final String requestVarName, final String serverIpName,
                                                   final String inputMessageVarName,
                                                   final String script) throws IOException {
        // <!--
        // {assignName}{prefix}{requestVarName}{serverIpVarName}{requestVarName}{inputMessageVarName}{script}
        // -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assRunScriptRequest.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
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
    public Node generateInvokeAsNode(final String invokeName, final String partnerLinkName, final String operationname,
                                     final QName portType, final String inputVarName,
                                     final String outputVarName) throws SAXException, IOException {
        final String templateString = this.generateInvokeAsString(invokeName, partnerLinkName, operationname, portType,
                                                                  inputVarName, outputVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
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
    public String generateInvokeAsString(final String invokeName, final String partnerLinkName,
                                         final String operationName, final QName portType, final String inputVarName,
                                         final String outputVarName) {
        // Example:
        // <bpel:invoke name="getPublicDNS" partnerLink="EC2VMIAServicePL"
        // operation="getPublicDNS" portType="ns:EC2VMIAService"
        // inputVariable="webapp_getPublicDNS_Request"
        // outputVariable="webapp_getPublicDNS_Response"></bpel:invoke>
        final String invokeAsString =
            "<bpel:invoke xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + invokeName
                + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\"" + " portType=\""
                + portType.getPrefix() + ":" + portType.getLocalPart() + "\"" + " inputVariable=\"" + inputVarName
                + "\"" + " outputVariable=\"" + outputVarName + "\"></bpel:invoke>";
        return invokeAsString;
    }

    /**
     * Returns the WSDL file of the EC2Linux IA WebService
     *
     * @return a File containing the absolute path to the WSDL file
     * @throws IOException is thrown when reading internal files fails
     */
    public File getLinuxFileUploadWSDLFile() throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("EC2LinuxIAService.wsdl");
        final File wsdlFile = new File(FileLocator.toFileURL(url).getPath());
        return wsdlFile;
    }

    /**
     * Returns the openTOSCA References Schema file
     *
     * @return a File containing the absolute path to the openTOSCA References Schema file
     * @throws IOException is thrown when reading internal files fails
     */
    public File getOpenToscaReferencesSchema() throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("openTOSCAReferencesSchema.xsd");
        final File xsdFile = new File(FileLocator.toFileURL(url).getPath());
        return xsdFile;
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
     * Returns an XPath Query which contructs a valid String, to GET a File from the openTOSCA API
     *
     * @param artifactPath a path inside an ArtifactTemplate
     * @return a String containing an XPath query
     */
    public String getRemoteFilePathString(final String artifactPath) {
        ResourceHandler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
        final String filePath =
            "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
        return filePath;
    }

    /**
     * <p>
     * Returns a DOM Node containing a BPEL Fragment that assigns values to Ec2 Linux FileTransfer
     * Request
     * </p>
     *
     * @param assignName the name of the assign
     * @param requestVarName the name of the FileTransferRequest variable
     * @param prefix the prefix for the EC2 Linux Service
     * @param serverIpVarName the name of a variable holding an address to a linux machine
     * @param planRequestName the name of BuildPlan input variable
     * @param remoteFilePath the path of the file to be transfered
     * @param remotePath the path for the file to upload unto the linux machine
     * @return a DOM Node containing a complete BPEL Fragment
     * @throws IOException is thrown if reading internal files fails
     * @throws SAXException is thrown if transforming internal files to DOM fails
     */
    public Node getRemoteTransferFileAssignAsNode(final String assignName, final String requestVarName,
                                                  final String prefix, final String serverIpVarName,
                                                  final String planRequestName, final String remoteFilePath,
                                                  final String remotePath) throws SAXException, IOException {
        final String templateString =
            this.getRemoteTransferFileAssignAsString(assignName, requestVarName, prefix, serverIpVarName,
                                                     planRequestName, remoteFilePath, remotePath);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * <p>
     * Returns a String containing a BPEL Fragment that assigns values to Ec2 Linux RemoteFileTransfer
     * Request
     * </p>
     *
     * @param assignName the name of the assign
     * @param requestVarName the name of the FileTransferRequest variable
     * @param prefix the prefix for the EC2 Linux Service
     * @param serverIpVarName the name of a variable holding an address to a linux machine
     * @param planRequestName the name of BuildPlan input variable
     * @param remoteFilePath the path of the file to be transfered
     * @param remotePath the path for the file to upload unto the linux machine
     * @return a String containing a complete BPEL Fragment
     * @throws IOException is thrown if reading internal files fails
     */
    public String getRemoteTransferFileAssignAsString(final String assignName, final String requestVarName,
                                                      final String prefix, final String serverIpVarName,
                                                      final String planRequestName, final String remoteFilePath,
                                                      final String remotePath) throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignRemoteTransferFileRequestFragment.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
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
     *
     * Returns a DOM Node containing a BPEL Fragment to fetch Data from the openTOSCA ContainerAPI with
     * the BPEL4RESTLight Extension
     *
     * @param csarFileName the file name of the csar the build plan belongs to
     * @param responseName the variable name of the response variable
     * @param relativeFilePath a relative path on the containerAPI
     * @return a DOM Node containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     *
     */
    public Node getRESTExtensionGETAsNode(final String csarFileName, final String responseName,
                                          final String relativeFilePath) throws SAXException, IOException {
        final String templateString = this.getRESTExtensionGETAsString(csarFileName, responseName, relativeFilePath);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Returns a String containing a BPEL Fragment to fetch Data from the openTOSCA ContainerAPI with
     * the BPEL4RESTLight Extension
     *
     * @param csarFileName the file name of the csar the build plan belongs to
     * @param responseName the variable name of the response variable
     * @param relativeFilePath a relative path on the containerAPI
     * @return a String containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     */
    public String getRESTExtensionGETAsString(final String csarFileName, final String responseName,
                                              final String relativeFilePath) throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("restExtensionGetFragment.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelfragmentfile);
        template = template.replace("{CSAR_filename}", csarFileName);
        template = template.replace("{response_var_name}", responseName);
        template = template.replace("{relative_path_to_file}", relativeFilePath);
        return template;
    }

    /**
     * Generates a DOM Node containing a BPEL Fragment that assigns values to an EC2 Linux TransferFile
     * request
     *
     * @param assignName the name for the assign
     * @param requestVarName the name of the TranferFile request variable
     * @param prefix the prefix of the EC2 Linux Service
     * @param serverIpVarName the name of a variable containing an address to a linux machine
     * @param planRequestName the name of the BuildPlan input message
     * @param localPathVarName a local path of a file on the machine the BuildPlan will be executed
     * @param remotePath the remote path where the file must be uploaded to, on the remote machine
     * @return a DOM Node containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when transforming internal data to DOM fails
     */
    public Node getTransferFileAssignAsNode(final String assignName, final String requestVarName, final String prefix,
                                            final String serverIpVarName, final String planRequestName,
                                            final String localPathVarName,
                                            final String remotePath) throws IOException, SAXException {
        final String templateString =
            this.getTransferFileAssignAsString(assignName, requestVarName, prefix, serverIpVarName, planRequestName,
                                               localPathVarName, remotePath);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a String containing a BPEL Fragment that assigns values to an EC2 Linux TransferFile
     * request
     *
     * @param assignName the name for the assign
     * @param requestVarName the name of the TranferFile request variable
     * @param prefix the prefix of the EC2 Linux Service
     * @param serverIpVarName the name of a variable containing an address to a linux machine
     * @param planRequestName the name of the BuildPlan input message
     * @param localPathVarName a local path of a file on the machine the BuildPlan will be executed
     * @param remotePath the remote path where the file must be uploaded to, on the remote machine
     * @return a String containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     */
    public String getTransferFileAssignAsString(final String assignName, final String requestVarName,
                                                final String prefix, final String serverIpVarName,
                                                final String planRequestName, final String localPathVarName,
                                                final String remotePath) throws IOException {
        // <!--
        // {AssignName},{RequestVarName},{ServerIpPropVarName},{prefix},{localPath},{remotePath},{SSHKey}
        // -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignTransferFileRequestFragment.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
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
     * Generates a DOM Node containing a BPEL Fragment that invokes an EC2 Linux Service with the
     * transferFile operation
     *
     * @param invokeName the name of the invoke
     * @param partnerLinkName the name of the partnerLink
     * @param portTypeprefix the prefix of the portType
     * @param inputVarName the name of the input variable
     * @param outputVarName the name of the output variable
     * @param operationName the name of the operation
     * @return a DOM Node containing a complete BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when transforming internal data to DOM fails
     */
    public Node getTransferFileInvokeAsNode(final String invokeName, final String partnerLinkName,
                                            final String portTypeprefix, final String inputVarName,
                                            final String outputVarName,
                                            final String operationName) throws SAXException, IOException {
        final String templateString = this.getTransferFileInvokeAsString(invokeName, partnerLinkName, portTypeprefix,
                                                                         inputVarName, outputVarName, operationName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a String containing a BPEL Fragment that invokes an EC2 Linux Service with the
     * transferFile operation
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
    public String getTransferFileInvokeAsString(final String invokeName, final String partnerLinkName,
                                                final String portTypeprefix, final String inputVarName,
                                                final String outputVarName,
                                                final String operationName) throws IOException {
        // <!-- {InvokeName} {partnerlinkName} {portTypePrefix} {inputVarName}
        // {outputVarName}-->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("invokeTransferFile.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("{InvokeName}", invokeName);
        template = template.replace("{partnerlinkName}", partnerLinkName);
        template = template.replace("{portTypePrefix}", portTypeprefix);
        template = template.replace("{inputVarName}", inputVarName);
        template = template.replace("{outputVarName}", outputVarName);
        template = template.replace("{operation}", operationName);
        return template;
    }

}
