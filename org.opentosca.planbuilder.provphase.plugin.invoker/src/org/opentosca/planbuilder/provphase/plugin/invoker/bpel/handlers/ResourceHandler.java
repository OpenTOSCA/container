/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
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

    public File createNewTempFile(final File file, final int id) throws IOException {
        final File tempFile = Files.createTempFile(file.getName().split("\\.")[0] + id,
            "." + file.getName().split("\\.")[1]).toFile();

        FileUtils.copyFile(file, tempFile);
        return tempFile;
    }

    /**
     * Generates a BPEL Copy element to use in BPEL Assigns, which sets the WS-Addressing ReplyTo Header
     * for the specified request variable
     *
     * @param partnerLinkName the name of the BPEL partnerLink that will be used as String
     * @param requestVariableName the name of the BPEL Variable used for an asynchronous request as
     *        String
     * @return a String containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateAddressingCopy(final String partnerLinkName, final String requestVariableName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("addressingCopy.xml");
        final File addressingFile = new File(FileLocator.toFileURL(url).getPath());
        String addressingFileString = FileUtils.readFileToString(addressingFile);
        /*
         * "{partnerLinkName}" "{requestVarName}"
         */
        addressingFileString = addressingFileString.replace("{requestVarName}", requestVariableName);
        addressingFileString = addressingFileString.replace("{partnerLinkName}", partnerLinkName);

        return addressingFileString;

    }

    /**
     * Generates a BPEL Copy element to use in BPEL Assigns, which sets the WS-Addressing ReplyTo Header
     * for the specified request variable
     *
     * @param partnerLinkName the name of the BPEL partnerLink that will be used as String
     * @param requestVariableName the name of the BPEL Variable used for an asynchronous request as
     *        String
     * @return a DOM Node containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal data to DOM fails
     */
    public Node generateAddressingCopyAsNode(final String partnerLinkName, final String requestVariableName)
        throws IOException, SAXException {
        final String addressingCopyString = this.generateAddressingCopy(partnerLinkName, requestVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL Copy which sets a dummy WS-Addressing ReplyTo Header on the given request
     * variable
     *
     * @param requestVariableName the name of a BPEL Variable as String
     * @return a String containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateAddressingInit(final String requestVariableName) throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("addressingInit.xml");
        final File addressingFile = new File(FileLocator.toFileURL(url).getPath());
        String addressingFileString = FileUtils.readFileToString(addressingFile);
        /*
         * "{partnerLinkName}" "{requestVarName}"
         */
        addressingFileString = addressingFileString.replace("{requestVarName}", requestVariableName);
        return addressingFileString;

    }

    /**
     * Generates a BPEL Copy which sets a dummy WS-Addressing ReplyTo Header on the given request
     * variable
     *
     * @param requestVariableName the name of a BPEL Variable as String
     * @return a DOM Node containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal data to DOM fails
     */
    public Node generateAddressingInitAsNode(final String requestVariableName) throws IOException, SAXException {
        final String addressingCopyString = this.generateAddressingInit(requestVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node generateCopyFromExternalParamToInvokerNode(final String requestVarName, final String requestVarPartName,
                    final String paramName, final String invokerParamName)
        throws IOException, SAXException {
        final String addressingCopyString = this.generateCopyFromExternalParamToInvokerString(requestVarName,
            requestVarPartName, paramName, invokerParamName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private String generateCopyFromExternalParamToInvokerString(final String requestVarName,
                    final String requestVarPartName, final String paramName, final String invokerParamName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("externalParamCopy2.xml");
        final File copyTemplateFile = new File(FileLocator.toFileURL(url).getPath());
        String copyTemplateString = FileUtils.readFileToString(copyTemplateFile);

        // {paramName}, {requestVarName}, {requestVarPartName}
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);
        copyTemplateString = copyTemplateString.replace("{invokerParamName}", invokerParamName);

        return copyTemplateString;
    }

    /**
     * Generates a BPEL Correlations element to us with BPEL Invoke and Receive elements
     *
     * @param correlationSetName the name of the correlationSet to use
     * @param initiate whether the correlationSet must be initialized or not
     * @return a DOM Node containing a complete BPEL Correlations element
     * @throws SAXException is thrown when parsing internal data fails
     * @throws IOException is thrown when reading internal data fails
     */
    public Node generateCorrelationSetsAsNode(final String correlationSetName, final boolean initiate)
        throws SAXException, IOException {
        final String correlationSetsString = "<bpel:correlations xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:correlation set=\""
            + correlationSetName + "\" initiate=\"" + (initiate ? "yes" : "no") + "\"/></bpel:correlations>";
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(correlationSetsString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a copy from a partnerLink myRole EPR to a invoker request param such as ReplyTo
     *
     * @param partnerLinkName the name of the partnerLink to use
     * @param invokerRequestVarName the name of the invoker request message
     * @param invokerRequestVarPartName the name of the message part of the referenced invoker request
     *        message variable
     * @param invokerParamName the name of the invoker param to assign
     * @return a DOM node containing a BPEL copy element
     * @throws SAXException is thrown when parsing internal files fail
     * @throws IOException is thrown when reading internal files fail
     */
    public Node generateEPRMyRoleCopyToInvokerParamAsNode(final String partnerLinkName,
                    final String invokerRequestVarName, final String invokerRequestVarPartName,
                    final String invokerParamName)
        throws SAXException, IOException {
        final String addressingCopyString = this.generateEPRMyRoleCopyToInvokerParamAsString(partnerLinkName,
            invokerRequestVarName, invokerRequestVarPartName, invokerParamName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a copy from a partnerLink myRole EPR to a invoker request param such as ReplyTo
     *
     * @param partnerLinkName the name of the partnerLink to use
     * @param invokerRequestVarName the name of the invoker request message
     * @param invokerRequestVarPartName the name of the message part of the referenced invoker request
     *        message variable
     * @param invokerParamName the name of the invoker param to assign
     * @return a String containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateEPRMyRoleCopyToInvokerParamAsString(final String partnerLinkName,
                    final String invokerRequestVarName, final String invokerRequestVarPartName,
                    final String invokerParamName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("EPRCopyToInvokerReplyTo.xml");
        final File eprCopyFile = new File(FileLocator.toFileURL(url).getPath());
        String eprCopyFileString = FileUtils.readFileToString(eprCopyFile);

        // <!--{partnerLinkName} {requestVarName} {requestVarPartName}
        // {invokerParamName}-->
        eprCopyFileString = eprCopyFileString.replace("{partnerLinkName}", partnerLinkName);
        eprCopyFileString = eprCopyFileString.replace("{requestVarName}  ", invokerRequestVarName);
        eprCopyFileString = eprCopyFileString.replace("{requestVarPartName}", invokerRequestVarPartName);
        eprCopyFileString = eprCopyFileString.replace("{invokerParamName}", invokerParamName);

        return eprCopyFileString;
    }

    /**
     * Generates a DOM Node containing a BPEL invoke element
     *
     * @param invokeName the name of the invoke as String
     * @param partnerLinkName the name of the partnerLink used as String
     * @param operationName the name of the WSDL operation as String
     * @param portType a QName denoting the WSDL portType
     * @param inputVarName the name of the BPEL Variable to use as Input, given as String
     * @return a DOM Node containing a complete BPEL Invoke element
     */
    public Node generateInvokeAsNode(final String invokeName, final String partnerLinkName, final String operationName,
                    final QName portType, final String inputVarName)
        throws SAXException, IOException {
        final String invokeString = this.generateInvokeAsString(invokeName, partnerLinkName, operationName, portType,
            inputVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(invokeString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a String containing a BPEL invoke element
     *
     * @param invokeName the name of the invoke as String
     * @param partnerLinkName the name of the partnerLink used as String
     * @param operationName the name of the WSDL operation as String
     * @param portType a QName denoting the WSDL portType
     * @param inputVarName the name of the BPEL Variable to use as Input, given as String
     * @return a String containing a complete BPEL Invoke element
     */
    public String generateInvokeAsString(final String invokeName, final String partnerLinkName,
                    final String operationName, final QName portType, final String inputVarName) {
        return "<bpel:invoke xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\""
            + invokeName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\""
            + " portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart() + "\"" + " inputVariable=\""
            + inputVarName + "\"></bpel:invoke>";
    }

    public String generateInvokerRequestMessageInitAssignTemplate(final String csarName, final QName serviceTemplateId,
                    final String serviceInstanceIdVarName, final String operationName, final String messageId,
                    final String requestVarName, final String requestVarPartName, final String iface,
                    final boolean isNodeTemplate, final String templateId,
                    final Map<String, Variable> internalExternalProps)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignInvokerAsyncMessage.xml");
        final File assignTemplateFile = new File(FileLocator.toFileURL(url).getPath());
        String assignTemplateString = FileUtils.readFileToString(assignTemplateFile);

        /*
         * String values must replace: {csarName}, {serviceTemplateNS}, {serviceTemplateLocalName},
         * {operationName}, {messageID}, {requestVarName}, {requestVarPartName}
         *
         * These must be xml snippets again -> more complicated: {copies} {interface}, {templateID},
         * {paramsMap},
         */

        // first the easy ones
        assignTemplateString = assignTemplateString.replace("{csarName}", csarName);
        assignTemplateString = assignTemplateString.replace("{serviceInstanceID}", "");
        assignTemplateString = assignTemplateString.replace("{serviceTemplateNS}", serviceTemplateId.getNamespaceURI());
        assignTemplateString = assignTemplateString.replace("{serviceTemplateLocalName}",
            serviceTemplateId.getLocalPart());
        assignTemplateString = assignTemplateString.replace("{operationName}", operationName);
        assignTemplateString = assignTemplateString.replace("{messageID}", messageId);
        assignTemplateString = assignTemplateString.replace("{requestVarName}", requestVarName);
        assignTemplateString = assignTemplateString.replace("{requestVarPartName}", requestVarPartName);

        if (iface != null) {
            final String ifaceString = "<impl:InterfaceName>" + iface + "</impl:InterfaceName>";
            assignTemplateString = assignTemplateString.replace("{interface}", ifaceString);
        } else {
            assignTemplateString = assignTemplateString.replace("{interface}", "");
        }

        String templateString = "";
        if (isNodeTemplate) {
            templateString = "<impl:NodeTemplateID>" + templateId + "</impl:NodeTemplateID>";
        } else {
            templateString = "<impl:RelationshipTemplateID>" + templateId + "</impl:RelationshipTemplateID>";
        }

        assignTemplateString = assignTemplateString.replace("{templateID}", templateString);

        assignTemplateString = assignTemplateString.replace("{paramsMap}",
            this.generateServiceInvokerParamsMap(internalExternalProps));

        // add copy elements to the assign according to the given map of
        // parameters
        for (final String propertyName : internalExternalProps.keySet()) {
            if (internalExternalProps.get(propertyName) == null) {
                // parameter is external, fetch value from plan input message
                String copyString = this.generateServiceInvokerExternalParamCopyString(requestVarName,
                    requestVarPartName, propertyName);
                copyString = copyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                assignTemplateString = assignTemplateString.replace("{copies}", copyString + "{copies}");
            } else {
                // parameter is internal, fetch value from bpel variable
                String copyString = this.generateServiceInvokerInternalParamCopyString(
                    internalExternalProps.get(propertyName).getName(), requestVarName, requestVarPartName,
                    propertyName);
                copyString = copyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                assignTemplateString = assignTemplateString.replace("{copies}", copyString + "{copies}");
            }
        }

        // assign serviceInstanceID
        String serviceInstanceCopyString = this.generateServiceInstanceIDCopy(serviceInstanceIdVarName, requestVarName,
            requestVarPartName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        assignTemplateString = assignTemplateString.replace("{copies}", serviceInstanceCopyString + "{copies}");

        assignTemplateString = assignTemplateString.replace("{copies}", "");

        // TODO REPLACE THIS PART <?xml version="1.0" encoding="UTF-8"?>

        ResourceHandler.LOG.debug("Generated Invoker Operation Call:");
        ResourceHandler.LOG.debug(assignTemplateString);
        return assignTemplateString;
    }

    public Node generateInvokerRequestMessageInitAssignTemplateAsNode(final String csarName,
                    final QName serviceTemplateId, final String serviceInstanceIdVarName, final String operationName,
                    final String messageId, final String requestVarName, final String requestVarPartName,
                    final String iface, final boolean isNodeTemplate, final String templateId,
                    final Map<String, Variable> internalExternalProps)
        throws IOException, SAXException {
        final String templateString = this.generateInvokerRequestMessageInitAssignTemplate(csarName, serviceTemplateId,
            serviceInstanceIdVarName, operationName, messageId, requestVarName, requestVarPartName, iface,
            isNodeTemplate, templateId, internalExternalProps);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL Copy which sets the MessageId of an Invoker Message Body to a given prefix and
     * the current date
     *
     * @param requestVariableName the name of the request variable with an invoker message body
     * @param requestVariabelPartName the name of the part which has the invoker message body
     * @param messageIdPrefix a prefix to be used inside the message id
     * @return a String containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateMessageIdInit(final String requestVariableName, final String requestVariabelPartName,
                    final String messageIdPrefix)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("initMessageId.xml");
        final File initMessageIdFile = new File(FileLocator.toFileURL(url).getPath());
        String initMessageIdFileString = FileUtils.readFileToString(initMessageIdFile);

        // <!-- {requestVarName}, {requestVarPartName}, {messageIdPrefix} -->
        initMessageIdFileString = initMessageIdFileString.replace("{requestVarName}", requestVariableName);
        initMessageIdFileString = initMessageIdFileString.replace("{requestVarPartName}", requestVariabelPartName);
        initMessageIdFileString = initMessageIdFileString.replace("{messageIdPrefix}", messageIdPrefix);
        return initMessageIdFileString;
    }

    /**
     * Generates a BPEL Copy which sets the MessageId of an Invoker Message Body to a given prefix and
     * the current date
     *
     * @param requestVariableName the name of the request variable with an invoker message body
     * @param requestVariabelPartName the name of the part which has the invoker message body
     * @param messageIdPrefix a prefix to be used inside the message id
     * @return a DOM Node containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node generateMessageIdInitAsNode(final String requestVariableName, final String requestVariabelPartName,
                    final String messageIdPrefix)
        throws IOException, SAXException {
        final String addressingCopyString = this.generateMessageIdInit(requestVariableName, requestVariabelPartName,
            messageIdPrefix);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
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
    public Node generateReceiveAsNode(final String receiveName, final String partnerLinkName,
                    final String operationName, final QName portType, final String variableName)
        throws SAXException, IOException {
        final String receiveString = "<bpel:receive xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\""
            + receiveName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName + "\" portType=\""
            + portType.getPrefix() + ":" + portType.getLocalPart() + "\" variable=\"" + variableName + "\"/>";
        /*
         * <bpel:receive name="ReceiveCreateEC2Instance" operation="createEC2InstanceCallback"
         * partnerLink="ec2VmPl1" portType="ns0:EC2VMIAAsyncServiceCallback" variable="createEc2Response3">
         * <bpel:correlations> <bpel:correlation initiate="no" set="createEc2CorrelationSet8"/>
         * </bpel:correlations> </bpel:receive>
         */
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(receiveString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateReplyToCopy(final String partnerLinkName, final String requestVarName,
                    final String requestVarPartName, final String paramName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("copyReplyTo.xml");
        final File copyTemplateFile = new File(FileLocator.toFileURL(url).getPath());
        String copyTemplateString = FileUtils.readFileToString(copyTemplateFile);

        // {paramName}, {partnerLinkName}, {requestVarName},
        // {requestVarPartName}
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        copyTemplateString = copyTemplateString.replace("{partnerLinkName}", partnerLinkName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);

        return copyTemplateString;
    }

    public Node generateReplyToCopyAsNode(final String partnerLinkName, final String requestVarName,
                    final String requestVarPartName, final String paramName)
        throws IOException, SAXException {
        final String addressingCopyString = this.generateReplyToCopy(partnerLinkName, requestVarName,
            requestVarPartName, paramName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates an BPEL Assign Element as String, which reads Response Message Data into internal
     * PropertyVariables
     *
     * @param variableName the Response Message variable name
     * @param part the part name of response message
     * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL Response message Elements
     * @param paramPropertyMappings Mappings from TOSCA Output Parameters to Properties
     * @param assignName the name attribute of the assign
     * @param MessageDeclId the XML Schema Declaration of the Response Message as QName
     * @return BPEL Assign Element as DOM Node
     */
    public Node generateResponseAssignAsNode(final String variableName, final String part,
                    final Map<String, Variable> paramPropertyMappings, final String assignName,
                    final QName MessageDeclId, final String planOutputMsgName, final String planOutputMsgPartName)
        throws SAXException, IOException {
        final String templateString = this.generateResponseAssignAsString(variableName, part, paramPropertyMappings,
            assignName, MessageDeclId, planOutputMsgName, planOutputMsgPartName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates an BPEL Assign Element as String, which reads Response Message Data into internal
     * PropertyVariables
     *
     * @param variableName the Response Message variable name
     * @param part the part name of response message
     * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL Response message Elements
     * @param paramPropertyMappings Mappings from TOSCA Output Parameters to Properties
     * @param assignName the name attribute of the assign
     * @param MessageDeclId the XML Schema Declaration of the Response Message as QName
     * @return BPEL Assign Element as String
     */
    public String generateResponseAssignAsString(final String variableName, final String part,
                    final Map<String, Variable> paramPropertyMappings, final String assignName,
                    final QName MessageDeclId, final String planOutputMsgName, final String planOutputMsgPartName) {
        String assignAsString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\""
            + assignName + "\">";

        for (final String toscaParam : paramPropertyMappings.keySet()) {
            final Variable propWrapper = paramPropertyMappings.get(toscaParam);
            if (propWrapper == null) {

                final String internalCopyString = "<bpel:copy><bpel:from variable=\"" + variableName + "\" part=\""
                    + part + "\">";
                final String internalQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\""
                    + toscaParam + "\"]/following-sibling::*[local-name()=\"value\"]]]></bpel:query>";
                final String internalToString = "</bpel:from><bpel:to variable=\"" + planOutputMsgName + "\" part=\""
                    + planOutputMsgPartName + "\">";
                final String internalQueryStringToOutput = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\""
                    + toscaParam + "\"]]]></bpel:query></bpel:to></bpel:copy>";
                assignAsString += internalCopyString;
                assignAsString += internalQueryString;
                assignAsString += internalToString;
                assignAsString += internalQueryStringToOutput;

            } else {
                // interal parameter, assign response message element value to
                // internal property variable

                final String internalCopyString = "<bpel:copy><bpel:from variable=\"" + variableName + "\" part=\""
                    + part + "\">";
                final String internalQueryString = "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\""
                    + toscaParam + "\"]/following-sibling::*[local-name()=\"value\"]]]></bpel:query>";
                final String internalToString = "</bpel:from><bpel:to variable=\"" + propWrapper.getName()
                    + "\"/></bpel:copy>";
                assignAsString += internalCopyString;
                assignAsString += internalQueryString;
                assignAsString += internalToString;
            }
        }
        assignAsString += "</bpel:assign>";
        ResourceHandler.LOG.debug("Generated following assign element:");
        ResourceHandler.LOG.debug(assignAsString);
        return assignAsString;
    }

    /**
     * Generates a BPEL Copy snippet from a single variable to a invoker message body, where the value
     * of the variable is added as ServiceInstanceID to the invoker message.
     *
     * @param bpelVarName the Name of the BPEL variable to use
     * @param requestVarName the name of the request variable holding a invoker request
     * @param requestVarPartName the name of part inside the invoker request message
     * @return a String containing a BPEL copy element
     * @throws IOException when the reading of an internal file fails
     * @throws SAXException when parsing the internal file fails
     */
    public Node generateServiceInstanceCopyAsNode(final String bpelVarName, final String requestVarName,
                    final String requestVarPartName)
        throws IOException, SAXException {
        final String serviceInstanceCopyString = this.generateServiceInstanceIDCopy(bpelVarName, requestVarName,
            requestVarPartName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(serviceInstanceCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL Copy snippet from a single variable to a invoker message body, where the value
     * of the variable is added as ServiceInstanceID to the invoker message.
     *
     * @param bpelVarName the Name of the BPEL variable to use
     * @param requestVarName the name of the request variable holding a invoker request
     * @param requestVarPartName the name of part inside the invoker request message
     * @return a String containing a BPEL copy element
     * @throws IOException when reading internal files fail
     */
    public String generateServiceInstanceIDCopy(final String bpelVarName, final String requestVarName,
                    final String requestVarPartName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("serviceInstanceCopy.xml");
        final File serviceInstanceCopy = new File(FileLocator.toFileURL(url).getPath());
        String serviceInstanceCopyString = FileUtils.readFileToString(serviceInstanceCopy);

        serviceInstanceCopyString = serviceInstanceCopyString.replace("{bpelVarName}", bpelVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarName}", requestVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarPartName}", requestVarPartName);

        return serviceInstanceCopyString;
    }

    private String generateServiceInvokerExternalParamCopyString(final String requestVarName,
                    final String requestVarPartName, final String paramName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("externalParamCopy.xml");
        final File copyTemplateFile = new File(FileLocator.toFileURL(url).getPath());
        String copyTemplateString = FileUtils.readFileToString(copyTemplateFile);

        // {paramName}, {requestVarName}, {requestVarPartName}
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);

        return copyTemplateString;
    }

    private String generateServiceInvokerInternalParamCopyString(final String bpelVarName, final String requestVarName,
                    final String requestVarPartName, final String paramName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("internalParamCopy.xml");
        final File copyTemplateFile = new File(FileLocator.toFileURL(url).getPath());
        String copyTemplateString = FileUtils.readFileToString(copyTemplateFile);

        // {bpelVarName}, {requestVarName}, {requestVarPartName}, {paramName}
        copyTemplateString = copyTemplateString.replace("{bpelVarName}", bpelVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        return copyTemplateString;
    }

    private String generateServiceInvokerParamsMap(final Map<String, Variable> internalExternalProps) {
        String paramsMapString = "<impl:Params>";
        for (final String key : internalExternalProps.keySet()) {
            paramsMapString += "<impl:Param><impl:key>" + key
                + "</impl:key><impl:value>value</impl:value></impl:Param>";
        }
        paramsMapString += "</impl:Params>";
        return paramsMapString;
    }

    public String getServiceInvokerAsyncRequestMessagePart() {
        return "invokeOperationAsync";
    }

    public QName getServiceInvokerAsyncRequestMessageType() {
        return new QName("http://siserver.org/wsdl", "invokeOperationAsyncMessage");
    }

    public QName getServiceInvokerAsyncRequestXSDType() {
        return new QName("http://siserver.org/schema", "invokeOperationAsync");
    }

    public String getServiceInvokerAsyncResponseMessagePart() {
        return "invokeResponse";
    }

    public QName getServiceInvokerAsyncResponseMessageType() {
        return new QName("http://siserver.org/wsdl", "invokeResponse");
    }

    public QName getServiceInvokerAsyncResponseXSDType() {
        return new QName("http://siserver.org/schema", "invokeResponse");
    }

    public QName getServiceInvokerCallbackPortType() {
        return new QName("http://siserver.org/wsdl", "CallbackPortType");
    }

    public QName getServiceInvokerPortType() {
        return new QName("http://siserver.org/wsdl", "InvokePortType");
    }

    public File getServiceInvokerWSDLFile(final File invokerXsdFile, final int id) throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("invoker.wsdl");
        final File wsdlFile = new File(FileLocator.toFileURL(url).getPath());

        final File tempFile = this.createNewTempFile(wsdlFile, id);

        final String fileName = invokerXsdFile.getName();

        FileUtils.write(tempFile, FileUtils.readFileToString(tempFile).replaceAll("invoker.xsd", fileName));

        return tempFile;
    }

    public File getServiceInvokerXSDFile(final int id) throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("invoker.xsd");
        final File xsdFile = new File(FileLocator.toFileURL(url).getPath());
        final File tempFile = this.createNewTempFile(xsdFile, id);
        return tempFile;

    }

}
