/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
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

    private final BPELProcessFragments fragments;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public ResourceHandler() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        this.fragments = new BPELProcessFragments();
    }

    public Path touchNewTempFile(final Path file, final int id) throws IOException {
        final String filename = file.getFileName().toString();
        final String[] segments = filename.split("\\.", 2);
        // we assume the given filename had a . in it!
        assert (segments.length == 2);
        final Path tempFile = Files.createTempFile(segments[0] + id, "." + segments[1]);

        return tempFile;
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at
     * runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a Node containing a BPEL If Activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node generateBPELIfTrueThrowFaultAsNode(final String xpath1Expr, final QName faultQName,
                                                   final String faultVariableName) throws IOException, SAXException {
        final String templateString = generateBPELIfTrueThrowFaultAsString(xpath1Expr, faultQName, faultVariableName);
        return this.fragments.transformStringToNode(templateString);
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at
     * runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a String containing a BPEL If Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateBPELIfTrueThrowFaultAsString(final String xpath1Expr,
                                                       final QName faultQName, String faultVariableName) throws IOException {
        // <!-- $xpath1Expr, $faultPrefix, $faultNamespace, $faultLocalName-->
        String bpelIfString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("invoker-plugin/ifFaultMessageThrowFault.xml"));

        bpelIfString = bpelIfString.replace("$xpath1Expr", xpath1Expr);

        bpelIfString = bpelIfString.replace("$faultPrefix", faultQName.getPrefix());
        bpelIfString = bpelIfString.replace("$faultLocalName", faultQName.getLocalPart());
        bpelIfString = bpelIfString.replace("$faultNamespace", faultQName.getNamespaceURI());
        bpelIfString = bpelIfString.replace("$faultVariable", faultVariableName);

        return bpelIfString;
    }

    /**
     * Generates a BPEL Copy element to use in BPEL Assigns, which sets the WS-Addressing ReplyTo
     * Header for the specified request variable
     *
     * @param partnerLinkName the name of the BPEL partnerLink that will be used as String
     * @param requestVariableName the name of the BPEL Variable used for an asynchronous request as
     *        String
     * @return a String containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateAddressingCopy(final String partnerLinkName,
                                         final String requestVariableName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/addressingCopy.xml");
        String addressingFileString = ResourceAccess.readResourceAsString(url);
        /*
         * "{partnerLinkName}" "{requestVarName}"
         */
        addressingFileString = addressingFileString.replace("{requestVarName}", requestVariableName);
        addressingFileString = addressingFileString.replace("{partnerLinkName}", partnerLinkName);

        return addressingFileString;
    }

    /**
     * Generates a BPEL Copy element to use in BPEL Assigns, which sets the WS-Addressing ReplyTo
     * Header for the specified request variable
     *
     * @param partnerLinkName the name of the BPEL partnerLink that will be used as String
     * @param requestVariableName the name of the BPEL Variable used for an asynchronous request as
     *        String
     * @return a DOM Node containing a complete BPEL Copy element
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal data to DOM fails
     */
    public Node generateAddressingCopyAsNode(final String partnerLinkName,
                                             final String requestVariableName) throws IOException, SAXException {
        final String addressingCopyString = generateAddressingCopy(partnerLinkName, requestVariableName);
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
        final URL url = getClass().getClassLoader().getResource("invoker-plugin/addressingInit.xml");
        String addressingFileString = ResourceAccess.readResourceAsString(url);
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
        final String addressingCopyString = generateAddressingInit(requestVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node generateCopyFromExternalParamToInvokerNode(final String requestVarName, final String requestVarPartName,
                                                           final String paramName,
                                                           final String invokerParamName) throws IOException,
        SAXException {
        final String addressingCopyString =
            generateCopyFromExternalParamToInvokerString(requestVarName, requestVarPartName, paramName,
                invokerParamName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(addressingCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private String generateCopyFromExternalParamToInvokerString(final String requestVarName,
                                                                final String requestVarPartName, final String paramName,
                                                                final String invokerParamName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/externalParamCopy2.xml");
        String copyTemplateString = ResourceAccess.readResourceAsString(url);

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
    public Node generateCorrelationSetsAsNode(final String correlationSetName,
                                              final boolean initiate) throws SAXException, IOException {
        final String correlationSetsString =
            "<bpel:correlations xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:correlation set=\""
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
     * @param invokerRequestVarPartName the name of the message part of the referenced invoker
     *        request message variable
     * @param invokerParamName the name of the invoker param to assign
     * @return a DOM node containing a BPEL copy element
     * @throws SAXException is thrown when parsing internal files fail
     * @throws IOException is thrown when reading internal files fail
     */
    public Node generateEPRMyRoleCopyToInvokerParamAsNode(final String partnerLinkName,
                                                          final String invokerRequestVarName,
                                                          final String invokerRequestVarPartName,
                                                          final String invokerParamName) throws SAXException,
        IOException {
        final String addressingCopyString =
            generateEPRMyRoleCopyToInvokerParamAsString(partnerLinkName, invokerRequestVarName,
                invokerRequestVarPartName, invokerParamName);
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
     * @param invokerRequestVarPartName the name of the message part of the referenced invoker
     *        request message variable
     * @param invokerParamName the name of the invoker param to assign
     * @return a String containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateEPRMyRoleCopyToInvokerParamAsString(final String partnerLinkName,
                                                              final String invokerRequestVarName,
                                                              final String invokerRequestVarPartName,
                                                              final String invokerParamName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/EPRCopyToInvokerReplyTo.xml");
        String eprCopyFileString = ResourceAccess.readResourceAsString(url);

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
                                     final QName portType, final String inputVarName) throws SAXException, IOException {
        final String invokeString =
            generateInvokeAsString(invokeName, partnerLinkName, operationName, portType, inputVarName);
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
                                                                  final String serviceInstanceIdVarName,
                                                                  final String nodeInstanceIdVarName,
                                                                  final String operationName, final String messageId,
                                                                  final String requestVarName,
                                                                  final String requestVarPartName, final String iface,
                                                                  final boolean isNodeTemplate, final String templateId,
                                                                  final Map<String, Variable> internalExternalProps) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/assignInvokerAsyncMessage.xml");
        String assignTemplateString = ResourceAccess.readResourceAsString(url);

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
        assignTemplateString = assignTemplateString.replace("{planCorrelation}", "");

        if (nodeInstanceIdVarName != null) {
            assignTemplateString = assignTemplateString.replace("{nodeInstanceID}", "");
        } else {
            assignTemplateString =
                assignTemplateString.replace("<impl:NodeInstanceID>{nodeInstanceID}</impl:NodeInstanceID>", "");
        }
        assignTemplateString = assignTemplateString.replace("{serviceTemplateNS}", serviceTemplateId.getNamespaceURI());
        assignTemplateString =
            assignTemplateString.replace("{serviceTemplateLocalName}", serviceTemplateId.getLocalPart());
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

        assignTemplateString =
            assignTemplateString.replace("{paramsMap}", generateServiceInvokerParamsMap(internalExternalProps));

        // add copy elements to the assign according to the given map of
        // parameters
        for (final String propertyName : internalExternalProps.keySet()) {
            if (internalExternalProps.get(propertyName) == null) {
                // parameter is external, fetch value from plan input message
                String copyString =
                    generateServiceInvokerExternalParamCopyString(requestVarName, requestVarPartName, propertyName);
                copyString = copyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                assignTemplateString = assignTemplateString.replace("{copies}", copyString + "{copies}");
            } else {
                // parameter is internal, fetch value from bpel variable
                String copyString =
                    generateServiceInvokerInternalParamCopyString(internalExternalProps.get(propertyName)
                            .getVariableName(),
                        requestVarName, requestVarPartName, propertyName);
                copyString = copyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                assignTemplateString = assignTemplateString.replace("{copies}", copyString + "{copies}");
            }
        }

        // assign correlation id
        String correlationIdCopyString = generateCorrelationIdCopy(requestVarName, requestVarPartName);
        correlationIdCopyString = correlationIdCopyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        assignTemplateString = assignTemplateString.replace("{copies}", correlationIdCopyString + "{copies}");

        // assign serviceInstanceID
        String serviceInstanceCopyString =
            generateServiceInstanceIDCopy(serviceInstanceIdVarName, requestVarName, requestVarPartName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        assignTemplateString = assignTemplateString.replace("{copies}", serviceInstanceCopyString + "{copies}");

        if (nodeInstanceIdVarName != null) {
            String nodeInstanceCopyString =
                generateNodeInstanceIdCopy(nodeInstanceIdVarName, requestVarName, requestVarPartName);
            nodeInstanceCopyString = nodeInstanceCopyString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            assignTemplateString = assignTemplateString.replace("{copies}", nodeInstanceCopyString + "{copies}");
        }

        assignTemplateString = assignTemplateString.replace("{copies}", "");

        // TODO REPLACE THIS PART <?xml version="1.0" encoding="UTF-8"?>

        LOG.debug("Generated Invoker Operation Call:");
        LOG.debug(assignTemplateString);
        return assignTemplateString;
    }

    public Node generateInvokerRequestMessageInitAssignTemplateAsNode(final String csarName,
                                                                      final QName serviceTemplateId,
                                                                      final String serviceInstanceIdVarName,
                                                                      final String nodeInstanceIdVarName,
                                                                      final String operationName,
                                                                      final String messageId,
                                                                      final String requestVarName,
                                                                      final String requestVarPartName,
                                                                      final String iface, final boolean isNodeTemplate,
                                                                      final String templateId,
                                                                      final Map<String, Variable> internalExternalProps) throws IOException,
        SAXException {
        final String templateString =
            generateInvokerRequestMessageInitAssignTemplate(csarName, serviceTemplateId, serviceInstanceIdVarName,
                nodeInstanceIdVarName, operationName, messageId,
                requestVarName, requestVarPartName, iface, isNodeTemplate,
                templateId, internalExternalProps);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL Copy which sets the MessageId of an Invoker Message Body to a given prefix
     * and the current date
     *
     * @param requestVariableName the name of the request variable with an invoker message body
     * @param requestVariabelPartName the name of the part which has the invoker message body
     * @param messageIdPrefix a prefix to be used inside the message id
     * @return a String containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateMessageIdInit(final String requestVariableName, final String requestVariabelPartName,
                                        final String messageIdPrefix) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/initMessageId.xml");
        String initMessageIdFileString = ResourceAccess.readResourceAsString(url);

        // <!-- {requestVarName}, {requestVarPartName}, {messageIdPrefix} -->
        initMessageIdFileString = initMessageIdFileString.replace("{requestVarName}", requestVariableName);
        initMessageIdFileString = initMessageIdFileString.replace("{requestVarPartName}", requestVariabelPartName);
        initMessageIdFileString = initMessageIdFileString.replace("{messageIdPrefix}", messageIdPrefix);
        return initMessageIdFileString;
    }

    /**
     * Generates a BPEL Copy which sets the MessageId of an Invoker Message Body to a given prefix
     * and the current date
     *
     * @param requestVariableName the name of the request variable with an invoker message body
     * @param requestVariabelPartName the name of the part which has the invoker message body
     * @param messageIdPrefix a prefix to be used inside the message id
     * @return a DOM Node containing a BPEL copy element
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node generateMessageIdInitAsNode(final String requestVariableName, final String requestVariabelPartName,
                                            final String messageIdPrefix) throws IOException, SAXException {
        final String addressingCopyString =
            generateMessageIdInit(requestVariableName, requestVariabelPartName, messageIdPrefix);
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
                                      final String operationName, final QName portType,
                                      final String variableName) throws SAXException, IOException {
        final String receiveString =
            "<bpel:receive xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\""
                + receiveName + "\" partnerLink=\"" + partnerLinkName + "\" operation=\"" + operationName
                + "\" portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart() + "\" variable=\""
                + variableName + "\"/>";
        /*
         * <bpel:receive name="ReceiveCreateEC2Instance" operation="createEC2InstanceCallback"
         * partnerLink="ec2VmPl1" portType="ns0:EC2VMIAAsyncServiceCallback"
         * variable="createEc2Response3"> <bpel:correlations> <bpel:correlation initiate="no"
         * set="createEc2CorrelationSet8"/> </bpel:correlations> </bpel:receive>
         */
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(receiveString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateReplyToCopy(final String partnerLinkName, final String requestVarName,
                                      final String requestVarPartName, final String paramName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/copyReplyTo.xml");
        String copyTemplateString = ResourceAccess.readResourceAsString(url);

        // {paramName}, {partnerLinkName}, {requestVarName},
        // {requestVarPartName}
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        copyTemplateString = copyTemplateString.replace("{partnerLinkName}", partnerLinkName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);

        return copyTemplateString;
    }

    public Node generateReplyToCopyAsNode(final String partnerLinkName, final String requestVarName,
                                          final String requestVarPartName,
                                          final String paramName) throws IOException, SAXException {
        final String addressingCopyString =
            generateReplyToCopy(partnerLinkName, requestVarName, requestVarPartName, paramName);
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
     * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL Response message
     *        Elements
     * @param paramPropertyMappings Mappings from TOSCA Output Parameters to Properties
     * @param assignName the name attribute of the assign
     * @param MessageDeclId the XML Schema Declaration of the Response Message as QName
     * @return BPEL Assign Element as DOM Node
     */
    public Node generateResponseAssignAsNode(final String variableName, final String part,
                                             final Map<String, Variable> paramPropertyMappings, final String assignName,
                                             final QName MessageDeclId, final String planOutputMsgName,
                                             final String planOutputMsgPartName) throws SAXException, IOException {
        final String templateString =
            generateResponseAssignAsString(variableName, part, paramPropertyMappings, assignName, MessageDeclId,
                planOutputMsgName, planOutputMsgPartName);
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
     * @param toscaWsdlMappings Mappings from TOSCA Output Parameters to WSDL Response message
     *        Elements
     * @param paramPropertyMappings Mappings from TOSCA Output Parameters to Properties
     * @param assignName the name attribute of the assign
     * @param MessageDeclId the XML Schema Declaration of the Response Message as QName
     * @return BPEL Assign Element as String
     */
    public String generateResponseAssignAsString(final String variableName, final String part,
                                                 final Map<String, Variable> paramPropertyMappings,
                                                 final String assignName, final QName MessageDeclId,
                                                 final String planOutputMsgName, final String planOutputMsgPartName) {
        String assignAsString =
            "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"" + assignName
                + "\">";

        for (final String toscaParam : paramPropertyMappings.keySet()) {
            final Variable propWrapper = paramPropertyMappings.get(toscaParam);
            if (propWrapper == null) {

                final String internalCopyString =
                    "<bpel:copy><bpel:from variable=\"" + variableName + "\" part=\"" + part + "\">";
                final String internalQueryString =
                    "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\""
                        + toscaParam + "\"]/following-sibling::*[local-name()=\"value\"]]]></bpel:query>";
                final String internalToString = "</bpel:from><bpel:to variable=\"" + planOutputMsgName + "\" part=\""
                    + planOutputMsgPartName + "\">";
                final String internalQueryStringToOutput =
                    "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\""
                        + toscaParam + "\"]]]></bpel:query></bpel:to></bpel:copy>";
                assignAsString += internalCopyString;
                assignAsString += internalQueryString;
                assignAsString += internalToString;
                assignAsString += internalQueryStringToOutput;
            } else {
                // interal parameter, assign response message element value to
                // internal property variable

                final String internalCopyString =
                    "<bpel:copy><bpel:from variable=\"" + variableName + "\" part=\"" + part + "\">";
                final String internalQueryString =
                    "<bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\""
                        + toscaParam + "\"]/following-sibling::*[local-name()=\"value\"]]]></bpel:query>";
                final String internalToString =
                    "</bpel:from><bpel:to variable=\"" + propWrapper.getVariableName() + "\"/></bpel:copy>";
                assignAsString += internalCopyString;
                assignAsString += internalQueryString;
                assignAsString += internalToString;
            }
        }
        assignAsString += "</bpel:assign>";
        LOG.debug("Generated following assign element:");
        LOG.debug(assignAsString);
        return assignAsString;
    }

    /**
     * Generates a BPEL Copy snippet from a single variable to a invoker message body, where the
     * value of the variable is added as ServiceInstanceID to the invoker message.
     *
     * @param bpelVarName the Name of the BPEL variable to use
     * @param requestVarName the name of the request variable holding a invoker request
     * @param requestVarPartName the name of part inside the invoker request message
     * @return a String containing a BPEL copy element
     * @throws IOException when the reading of an internal file fails
     * @throws SAXException when parsing the internal file fails
     */
    public Node generateServiceInstanceCopyAsNode(final String bpelVarName, final String requestVarName,
                                                  final String requestVarPartName) throws IOException, SAXException {
        final String serviceInstanceCopyString =
            generateServiceInstanceIDCopy(bpelVarName, requestVarName, requestVarPartName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(serviceInstanceCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL Copy snippet from the plan input message 'CorrelationID' property to the
     * invoker message
     *
     * @param requestVarName the name of the request variable holding a invoker request
     * @param requestVarPartName the name of part inside the invoker request message
     * @return a String containing a BPEL copy element
     * @throws IOException when reading internal files fails
     */
    private String generateCorrelationIdCopy(final String requestVarName,
                                             final String requestVarPartName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/correlationIdCopy.xml");
        String correlationIdCopyString = ResourceAccess.readResourceAsString(url);

        correlationIdCopyString = correlationIdCopyString.replace("{requestVarName}", requestVarName);
        correlationIdCopyString = correlationIdCopyString.replace("{requestVarPartName}", requestVarPartName);

        return correlationIdCopyString;
    }

    /**
     * Generates a BPEL Copy snippet from a single variable to a invoker message body, where the
     * value of the variable is added as ServiceInstanceID to the invoker message.
     *
     * @param bpelVarName the Name of the BPEL variable to use
     * @param requestVarName the name of the request variable holding a invoker request
     * @param requestVarPartName the name of part inside the invoker request message
     * @return a String containing a BPEL copy element
     * @throws IOException when reading internal files fail
     */
    public String generateServiceInstanceIDCopy(final String bpelVarName, final String requestVarName,
                                                final String requestVarPartName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/serviceInstanceCopy.xml");
        String serviceInstanceCopyString = ResourceAccess.readResourceAsString(url);

        serviceInstanceCopyString = serviceInstanceCopyString.replace("{bpelVarName}", bpelVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarName}", requestVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarPartName}", requestVarPartName);

        return serviceInstanceCopyString;
    }

    public String generateNodeInstanceIdCopy(final String bpelVarName, final String requestVarName,
                                             final String requestVarPartName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/nodeInstanceCopy.xml");
        String serviceInstanceCopyString = ResourceAccess.readResourceAsString(url);

        serviceInstanceCopyString = serviceInstanceCopyString.replace("{bpelVarName}", bpelVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarName}", requestVarName);
        serviceInstanceCopyString = serviceInstanceCopyString.replace("{requestVarPartName}", requestVarPartName);

        return serviceInstanceCopyString;
    }

    public Node generateNodeInstanceIdCopyAsNode(final String bpelVarName, final String requestVarName,
                                                 final String requestVarPartName) throws IOException, SAXException {
        final String nodeInstanceCopyString =
            generateNodeInstanceIdCopy(bpelVarName, requestVarName, requestVarPartName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(nodeInstanceCopyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private String generateServiceInvokerExternalParamCopyString(final String requestVarName,
                                                                 final String requestVarPartName,
                                                                 final String paramName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/externalParamCopy.xml");
        String copyTemplateString = ResourceAccess.readResourceAsString(url);

        // {paramName}, {requestVarName}, {requestVarPartName}
        copyTemplateString = copyTemplateString.replace("{paramName}", paramName);
        copyTemplateString = copyTemplateString.replace("{requestVarName}", requestVarName);
        copyTemplateString = copyTemplateString.replace("{requestVarPartName}", requestVarPartName);

        return copyTemplateString;
    }

    private String generateServiceInvokerInternalParamCopyString(final String bpelVarName, final String requestVarName,
                                                                 final String requestVarPartName,
                                                                 final String paramName) throws IOException {
        URL url = getClass().getClassLoader().getResource("invoker-plugin/internalParamCopy.xml");
        String copyTemplateString = ResourceAccess.readResourceAsString(url);

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
            paramsMapString +=
                "<impl:Param><impl:key>" + key + "</impl:key><impl:value>value</impl:value></impl:Param>";
        }
        paramsMapString += "</impl:Params>";
        return paramsMapString;
    }

    public String getServiceInvokerAsyncRequestMessagePart() {
        return "invokeOperationAsync";
    }

    // FIXME replace public getters for constants with public static finals
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

    public Path getServiceInvokerWSDLFile(final Path invokerXsdFile, final int id) throws IOException {
        final URL url = getClass().getClassLoader().getResource("invoker-plugin/invoker.wsdl");

        final Path wsdlFile = ResourceAccess.resolveUrl(url);
        final Path tempFile = touchNewTempFile(wsdlFile, id);
        final String fileName = invokerXsdFile.getFileName().toString();

        Files.write(tempFile, new String(Files.readAllBytes(wsdlFile)).replaceAll("invoker.xsd", fileName).getBytes());

        return tempFile;
    }

    public Path getServiceInvokerXSDFile(final int id) throws IOException {
        final URL url = getClass().getClassLoader().getResource("invoker-plugin/invoker.xsd");

        final Path xsdFile = ResourceAccess.resolveUrl(url);
        final Path tempFile = touchNewTempFile(xsdFile, id);

        Files.copy(xsdFile, Files.newOutputStream(tempFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
        return tempFile;
    }
}
