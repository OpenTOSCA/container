package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.core.handler.InvokerPluginHandler;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BPELInvokerPluginHandler implements InvokerPluginHandler<BPELPlanContext> {

    private final static Logger LOG = LoggerFactory.getLogger(BPELInvokerPluginHandler.class);
    private static final String PlanInstanceURLVarKeyword = "OpenTOSCAContainerAPIPlanInstanceURL";
    private static final String ServiceInstanceURLVarKeyword = "OpenTOSCAContainerAPIServiceInstanceURL";

    private ResourceHandler resHandler;
    private BPELProcessFragments bpelFrags;
    private DocumentBuilderFactory docFactory;

    private DocumentBuilder docBuilder;

    public BPELInvokerPluginHandler() {
        try {
            this.resHandler = new ResourceHandler();
            this.bpelFrags = new BPELProcessFragments();
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            BPELInvokerPluginHandler.LOG.error("Couldn't initialize ResourceHandler", e);
        }
    }

    public void appendLOGMessageActivity(final BPELPlanContext context, final String message,
                                         final BPELPlanContext.Phase phase) {
        String logMessageTempStringVarName = null;
        String logMessageContent = null;
        logMessageTempStringVarName = "instanceDataLogMsg_" + System.currentTimeMillis();
        logMessageContent = message;

        // create variables
        logMessageTempStringVarName =
            context.createGlobalStringVariable(logMessageTempStringVarName, logMessageContent).getName();

        final String logMessageReqVarName = createLogRequestMsgVar(context);
        final String planInstanceURLVar = findPlanInstanceURLVar(context);

        try {


            Node logPOSTNode =
                new BPELProcessFragments().createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(planInstanceURLVar,
                                                                                          logMessageTempStringVarName,
                                                                                          logMessageReqVarName);
            logPOSTNode = context.importNode(logPOSTNode);

            switch (phase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(logPOSTNode);
                    break;
                case PROV:
                    context.getProvisioningPhaseElement().appendChild(logPOSTNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(logPOSTNode);
                    break;

            }


        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String findPlanInstanceURLVar(final BPELPlanContext context) {
        String planInstanceURLVar = null;
        for (final String varName : context.getMainVariableNames()) {
            if (varName.contains(BPELInvokerPluginHandler.PlanInstanceURLVarKeyword)) {
                planInstanceURLVar = varName;
            }
        }
        return planInstanceURLVar;
    }

    private String createLogRequestMsgVar(final BPELPlanContext context) {
        final String logMsgReqVarName = "logMessage" + context.getIdForNames();

        try {
            final File opentoscaApiSchemaFile = this.bpelFrags.getOpenTOSCAAPISchemaFile();
            QName logMsgRequestQName = this.bpelFrags.getOpenToscaApiLogMsgReqElementQName();
            context.registerType(logMsgRequestQName, opentoscaApiSchemaFile);
            logMsgRequestQName = context.importQName(logMsgRequestQName);

            context.addGlobalVariable(logMsgReqVarName, BPELPlan.VariableType.ELEMENT, logMsgRequestQName);
        }
        catch (final IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        return logMsgReqVarName;
    }

    /**
     * Removes trailing slashes
     *
     * @param ref a path
     * @return a String without trailing slashes
     */
    private String fileReferenceToFolder(String ref) {
        LOG.debug("Getting ref to change to folder ref: " + ref);

        final int lastIndexSlash = ref.lastIndexOf("/");
        final int lastIndexDot = ref.lastIndexOf(".");
        if (lastIndexSlash < lastIndexDot) {
            ref = ref.substring(0, lastIndexSlash);
        }
        LOG.debug("Returning ref: " + ref);
        return ref;
    }

    private String findInterfaceForOperation(final BPELPlanContext context, final AbstractOperation operation) {
        List<AbstractInterface> interfaces = null;
        if (context.getNodeTemplate() != null) {
            interfaces = context.getNodeTemplate().getType().getInterfaces();
        } else {
            interfaces = context.getRelationshipTemplate().getRelationshipType().getSourceInterfaces();
            interfaces.addAll(context.getRelationshipTemplate().getRelationshipType().getTargetInterfaces());
        }

        if (interfaces != null && interfaces.size() > 0) {
            for (final AbstractInterface iface : interfaces) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.equals(operation)) {
                        return iface.getName();
                    }
                }
            }
        }
        return null;

    }

    private Variable findVar(final BPELPlanContext context, final String propName) {
        Variable propWrapper = context.getPropertyVariable(propName);
        if (propWrapper == null) {
            propWrapper = context.getPropertyVariable(propName, true);
            if (propWrapper == null) {
                propWrapper = context.getPropertyVariable(propName, false);
            }
        }
        return propWrapper;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia) throws IOException {
        final File xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final File wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        // register wsdls and xsd
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);
        final QName invokerCallbackPortType =
            context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), wsdlFile);

        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerAsyncRequestXSDType(), xsdFile);
        context.registerType(this.resHandler.getServiceInvokerAsyncResponseXSDType(), xsdFile);

        final QName InputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncRequestMessageType());
        final String InputMessagePartName = this.resHandler.getServiceInvokerAsyncRequestMessagePart();
        final QName OutputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncResponseMessageType());
        final String OutputMessagePartName = this.resHandler.getServiceInvokerAsyncResponseMessagePart();

        // generate partnerlink from the two porttypes
        final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();
        context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee",
                                   invokerPortType);
        final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();

        context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

        // register request and response message
        final String requestVariableName =
            invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
        context.addVariable(requestVariableName, BPELPlan.VariableType.MESSAGE, InputMessageId);
        final String responseVariableName = invokerCallbackPortType.getLocalPart() + OutputMessageId.getLocalPart()
            + "Response" + context.getIdForNames();
        context.addVariable(responseVariableName, BPELPlan.VariableType.MESSAGE, OutputMessageId);

        // setup a correlation set for the messages
        String correlationSetName = null;

        // setup correlation property and aliases for request and response
        final String query = "//*[local-name()=\"MessageID\" and namespace-uri()=\"http://siserver.org/schema\"]";
        final String correlationPropertyName = invokerPortType.getLocalPart() + "Property" + context.getIdForNames();
        context.addProperty(correlationPropertyName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        context.addPropertyAlias(correlationPropertyName, InputMessageId, InputMessagePartName, query);
        context.addPropertyAlias(correlationPropertyName, OutputMessageId, OutputMessagePartName, query);
        // register correlationsets
        correlationSetName = invokerPortType.getLocalPart() + "CorrelationSet" + context.getIdForNames();
        context.addCorrelationSet(correlationSetName, correlationPropertyName);

        // fetch "meta"-data for invoker message (e.g. csarid, nodetemplate
        // id..)
        final String csarId = context.getCSARFileName();
        final QName serviceTemplateId = context.getServiceTemplateId();
        boolean isNodeTemplate = true;
        String templateId = "";
        if (context.getNodeTemplate() != null) {
            templateId = context.getNodeTemplate().getId();
        } else {
            templateId = context.getRelationshipTemplate().getId();
            isNodeTemplate = false;
        }

        final String interfaceName = findInterfaceForOperation(context, operation);
        final String operationName = operation.getName();

        // fetch the input parameters of the operation and check whether their
        // internal or external

        // map to store input parameter names to bpel variable names, if value
        // is null
        // -> parameter is external
        final Map<String, Variable> internalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> internalExternalPropsOutput = new HashMap<>();

        for (final AbstractParameter para : operation.getInputParameters()) {
            Variable propWrapper = null;
            // if this param is ambigious, search for the alternatives to match against
            if (Utils.isSupportedVirtualMachineIPProperty(para.getName())) {
                for (final String propAlt : Utils.getSupportedVirtualMachineIPPropertyNames()) {
                    propWrapper = findVar(context, propAlt);
                    if (propWrapper != null) {
                        break;
                    }
                }
            } else {
                propWrapper = findVar(context, para.getName());
            }
            internalExternalPropsInput.put(para.getName(), propWrapper);
        }

        for (final AbstractParameter para : operation.getOutputParameters()) {
            final Variable propWrapper = findVar(context, para.getName());
            internalExternalPropsOutput.put(para.getName(), propWrapper);
        }

        // add external props to plan input message
        for (final String paraName : internalExternalPropsInput.keySet()) {
            if (internalExternalPropsInput.get(paraName) == null) {
                context.addStringValueToPlanRequest(paraName);
            }
        }

        // add external props to plan output message
        for (final String paraName : internalExternalPropsOutput.keySet()) {
            if (internalExternalPropsOutput.get(paraName) == null) {
                context.addStringValueToPlanResponse(paraName);
            }
        }

        // fetch serviceInstanceId

        final String serviceInstanceIdVarName = context.getServiceInstanceURLVarName();

        if (serviceInstanceIdVarName == null) {
            return false;
        }

        final String nodeInstanceUrlVarName = context.findInstanceURLVar(templateId, isNodeTemplate);

        if (nodeInstanceUrlVarName == null) {
            return false;
        }

        // add request message assign to prov phase scope
        try {
            Node assignNode = null;
            // TODO FIXME See line 570
            if (context.getPlanType().equals(PlanType.TERMINATE)) {
                assignNode =
                    this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(csarId, serviceTemplateId,
                                                                                          serviceInstanceIdVarName,
                                                                                          null, operationName,
                                                                                          String.valueOf(System.currentTimeMillis()),
                                                                                          requestVariableName,
                                                                                          InputMessagePartName,
                                                                                          interfaceName, isNodeTemplate,
                                                                                          templateId,
                                                                                          internalExternalPropsInput);

            } else {
                assignNode =
                    this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(csarId, serviceTemplateId,
                                                                                          serviceInstanceIdVarName,
                                                                                          nodeInstanceUrlVarName,
                                                                                          operationName,
                                                                                          String.valueOf(System.currentTimeMillis()),
                                                                                          requestVariableName,
                                                                                          InputMessagePartName,
                                                                                          interfaceName, isNodeTemplate,
                                                                                          templateId,
                                                                                          internalExternalPropsInput);
            }

            assignNode = context.importNode(assignNode);

            Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
            addressingCopyInit = context.importNode(addressingCopyInit);
            assignNode.appendChild(addressingCopyInit);

            Node addressingCopyNode =
                this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
            addressingCopyNode = context.importNode(addressingCopyNode);
            assignNode.appendChild(addressingCopyNode);

            // adds field into plan input message to give the plan it's own
            // address
            // for the invoker PortType (callback etc.). This is needed as WSO2
            // BPS
            // 2.x can't give that at runtime (bug)
            LOG.debug("Adding plan callback address field to plan input");
            context.addStringValueToPlanRequest("planCallbackAddress_invoker");

            /*
             * Will be needed when we start to switch to a new bpel engine String callbackAddressVarName =
             * this.inputHasCallbackAddressDefined(context);
             *
             * if (callbackAddressVarName == null) { // if the plan doesn't have an input message for the
             * address of // the plan itself (for callback/bps2.1.2) we get the address at // runtime Node
             * replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName,
             * InputMessagePartName, "ReplyTo"); replyToCopy = context.importNode(replyToCopy);
             * assignNode.appendChild(replyToCopy); } else { // else the address is provided in the input
             * message
             */

            Node messageIdInit =
                this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, templateId + ":"
                    + interfaceName + ":" + operationName + ":");
            messageIdInit = context.importNode(messageIdInit);
            assignNode.appendChild(messageIdInit);

            Node replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName,
                                                                         InputMessagePartName, "ReplyTo");
            replyToCopy = context.importNode(replyToCopy);
            assignNode.appendChild(replyToCopy);

            context.getProvisioningPhaseElement().appendChild(assignNode);
        }
        catch (final SAXException e) {
            LOG.error("Couldn't generate DOM node for the request message assign element", e);
            return false;
        }


        if (context.getNodeTemplate() != null) {

            appendLOGMessageActivity(context, "Executing operation " + operation + " of NodeTemplate "
                + context.getNodeTemplate().getId(), BPELPlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context,
                                     "Executing " + (operation != null ? "operation " + operation + " of " : "")
                                         + "RelationshipTemplate " + context.getRelationshipTemplate().getId() + "",
                                     BPELPlanContext.Phase.PROV);
        }

        // invoke service invoker
        // add invoke
        try {
            Node invokeNode =
                this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName,
                                                     "invokeOperationAsync", invokerPortType, requestVariableName);
            LOG.debug("Trying to ImportNode: " + invokeNode.toString());
            invokeNode = context.importNode(invokeNode);

            Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
            correlationSetsNode = context.importNode(correlationSetsNode);
            invokeNode.appendChild(correlationSetsNode);

            context.getProvisioningPhaseElement().appendChild(invokeNode);
        }
        catch (final SAXException e) {
            LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            LOG.error("Error reading/writing File", e);
            return false;
        }

        // add receive for service invoker callback
        try {
            Node receiveNode =
                this.resHandler.generateReceiveAsNode("receive_" + responseVariableName, partnerLinkName, "callback",
                                                      invokerCallbackPortType, responseVariableName);
            receiveNode = context.importNode(receiveNode);

            Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, false);
            correlationSetsNode = context.importNode(correlationSetsNode);
            receiveNode.appendChild(correlationSetsNode);

            context.getProvisioningPhaseElement().appendChild(receiveNode);
        }
        catch (final SAXException e1) {
            LOG.error("Error reading/writing XML File", e1);
            return false;
        }
        catch (final IOException e1) {
            LOG.error("Error reading/writing File", e1);
            return false;
        }

        // process response message
        // add assign for response
        try {

            Node responseAssignNode =
                this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName,
                                                             internalExternalPropsOutput,
                                                             "assign_" + responseVariableName, OutputMessageId,
                                                             context.getPlanResponseMessageName(), "payload");
            LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
            responseAssignNode = context.importNode(responseAssignNode);
            context.getProvisioningPhaseElement().appendChild(responseAssignNode);
        }
        catch (final SAXException e) {
            LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            LOG.error("Error reading/writing File", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final String templateId, final boolean isNodeTemplate,
                          final String operationName, final String interfaceName, final String callbackAddressVarName,
                          final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput,
                          final BPELScopePhaseType appendToPrePhase) throws IOException {
        final File xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final File wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        // register wsdls and xsd
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);
        final QName invokerCallbackPortType =
            context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), wsdlFile);

        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerAsyncRequestXSDType(), xsdFile);
        context.registerType(this.resHandler.getServiceInvokerAsyncResponseXSDType(), xsdFile);

        final QName InputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncRequestMessageType());
        final String InputMessagePartName = this.resHandler.getServiceInvokerAsyncRequestMessagePart();
        final QName OutputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncResponseMessageType());
        final String OutputMessagePartName = this.resHandler.getServiceInvokerAsyncResponseMessagePart();

        // generate partnerlink from the two porttypes
        final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();
        context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee",
                                   invokerPortType);
        final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();

        context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

        // register request and response message
        final String requestVariableName =
            invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
        context.addVariable(requestVariableName, BPELPlan.VariableType.MESSAGE, InputMessageId);
        final String responseVariableName = invokerCallbackPortType.getLocalPart() + OutputMessageId.getLocalPart()
            + "Response" + context.getIdForNames();
        context.addVariable(responseVariableName, BPELPlan.VariableType.MESSAGE, OutputMessageId);

        // setup a correlation set for the messages
        String correlationSetName = null;

        // setup correlation property and aliases for request and response
        final String correlationPropertyName = invokerPortType.getLocalPart() + "Property" + context.getIdForNames();
        context.addProperty(correlationPropertyName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));

        final String query = "//*[local-name()=\"MessageID\" and namespace-uri()=\"http://siserver.org/schema\"]";
        // "/" + InputMessageId.getPrefix() + ":" + "MessageID"
        context.addPropertyAlias(correlationPropertyName, InputMessageId, InputMessagePartName, query);
        context.addPropertyAlias(correlationPropertyName, OutputMessageId, OutputMessagePartName, query);
        // register correlationsets
        correlationSetName = invokerPortType.getLocalPart() + "CorrelationSet" + context.getIdForNames();
        context.addCorrelationSet(correlationSetName, correlationPropertyName);

        // add external props to plan input message
        for (final String paraName : internalExternalPropsInput.keySet()) {
            if (internalExternalPropsInput.get(paraName) == null) {
                context.addStringValueToPlanRequest(paraName);
            }
        }

        // add external props to plan output message
        for (final String paraName : internalExternalPropsOutput.keySet()) {
            if (internalExternalPropsOutput.get(paraName) == null) {
                context.addStringValueToPlanResponse(paraName);
            }
        }

        // fetch serviceInstanceId

        final String serviceInstanceIdVarName = context.getServiceInstanceURLVarName();

        if (serviceInstanceIdVarName == null) {
            return false;
        }

        final String nodeInstanceUrlVarName = context.findInstanceURLVar(templateId, isNodeTemplate);

        if (nodeInstanceUrlVarName == null) {
            return false;
        }


        // add request message assign to prov phase scope
        try {
            Node assignNode = null;
            if (context.getPlanType().equals(PlanType.TERMINATE)) {
                // TODO FIXME, right now the termination plans are able to call operations of node Instances for
                // that the instanceID can be null at runtime e.g. when removing a DockerContainer the operation
                // removeContainer of the DockerEngine is called for that the nodeInstanceId is not fetched at the
                // time
                // of removal
                // TIP this issue theoretically happens only with the "container deployment pattern" were a hosting
                // node has the operations needed to manage a component => different termination handling for such
                // components is needed
                assignNode =
                    this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(context.getCSARFileName(),
                                                                                          context.getServiceTemplateId(),
                                                                                          serviceInstanceIdVarName,
                                                                                          null, operationName,
                                                                                          String.valueOf(System.currentTimeMillis()),
                                                                                          requestVariableName,
                                                                                          InputMessagePartName,
                                                                                          interfaceName, isNodeTemplate,
                                                                                          templateId,
                                                                                          internalExternalPropsInput);

            } else {

                assignNode =
                    this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(context.getCSARFileName(),
                                                                                          context.getServiceTemplateId(),
                                                                                          serviceInstanceIdVarName,
                                                                                          nodeInstanceUrlVarName,
                                                                                          operationName,
                                                                                          String.valueOf(System.currentTimeMillis()),
                                                                                          requestVariableName,
                                                                                          InputMessagePartName,
                                                                                          interfaceName, isNodeTemplate,
                                                                                          templateId,
                                                                                          internalExternalPropsInput);
            }
            assignNode = context.importNode(assignNode);

            Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
            addressingCopyInit = context.importNode(addressingCopyInit);
            assignNode.appendChild(addressingCopyInit);

            Node addressingCopyNode =
                this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
            addressingCopyNode = context.importNode(addressingCopyNode);
            assignNode.appendChild(addressingCopyNode);

            // if (callbackAddressVarName == null) {
            // if the plan doesn't have an input message for the address of
            // the plan itself (for callback/bps2.1.2) we get the address at
            // runtime
            Node replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName,
                                                                         InputMessagePartName, "ReplyTo");
            replyToCopy = context.importNode(replyToCopy);
            assignNode.appendChild(replyToCopy);
            // } else {
            // // else the address is provided in the input message
            // Node replyToCopy =
            // this.resHandler.generateCopyFromExternalParamToInvokerNode(requestVariableName,
            // InputMessagePartName, callbackAddressVarName, "ReplyTo");
            // replyToCopy = context.importNode(replyToCopy);
            // assignNode.appendChild(replyToCopy);
            // }

            Node messageIdInit =
                this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, templateId + ":"
                    + interfaceName + ":" + operationName + ":");
            messageIdInit = context.importNode(messageIdInit);
            assignNode.appendChild(messageIdInit);


            switch (appendToPrePhase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(assignNode);
                    break;
                case PROVISIONING:
                    context.getProvisioningPhaseElement().appendChild(assignNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(assignNode);
                    break;
            }

        }
        catch (final SAXException e) {
            BPELInvokerPluginHandler.LOG.error("Couldn't generate DOM node for the request message assign element", e);
            return false;
        }

        if (context.getNodeTemplate() != null) {

            appendLOGMessageActivity(context, "Executing operation " + operationName + " of NodeTemplate "
                + context.getNodeTemplate().getId(), BPELPlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context,
                                     "Executing " + (operationName != null ? "operation " + operationName + " of " : "")
                                         + "RelationshipTemplate " + context.getRelationshipTemplate().getId() + "",
                                     BPELPlanContext.Phase.PROV);
        }
        // invoke service invoker
        // add invoke
        try {
            Node invokeNode =
                this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName,
                                                     "invokeOperationAsync", invokerPortType, requestVariableName);
            BPELInvokerPluginHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
            invokeNode = context.importNode(invokeNode);

            Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
            correlationSetsNode = context.importNode(correlationSetsNode);
            invokeNode.appendChild(correlationSetsNode);


            switch (appendToPrePhase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(invokeNode);
                    break;
                case PROVISIONING:
                    context.getProvisioningPhaseElement().appendChild(invokeNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(invokeNode);
                    break;
            }
        }
        catch (final SAXException e) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing File", e);
            return false;
        }

        // add receive for service invoker callback
        try {
            Node receiveNode =
                this.resHandler.generateReceiveAsNode("receive_" + responseVariableName, partnerLinkName, "callback",
                                                      invokerCallbackPortType, responseVariableName);
            receiveNode = context.importNode(receiveNode);

            Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, false);
            correlationSetsNode = context.importNode(correlationSetsNode);
            receiveNode.appendChild(correlationSetsNode);

            switch (appendToPrePhase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(receiveNode);
                    break;
                case PROVISIONING:
                    context.getProvisioningPhaseElement().appendChild(receiveNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(receiveNode);
                    break;
            }
        }
        catch (final SAXException e1) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing XML File", e1);
            return false;
        }
        catch (final IOException e1) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing File", e1);
            return false;
        }

        // process response message
        // add assign for response
        try {

            Node responseAssignNode =
                this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName,
                                                             internalExternalPropsOutput,
                                                             "assign_" + responseVariableName, OutputMessageId,
                                                             context.getPlanResponseMessageName(), "payload");
            BPELInvokerPluginHandler.LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
            responseAssignNode = context.importNode(responseAssignNode);

            switch (appendToPrePhase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(responseAssignNode);
                    break;
                case PROVISIONING:
                    context.getProvisioningPhaseElement().appendChild(responseAssignNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(responseAssignNode);
                    break;
            }

        }
        catch (final SAXException e) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            BPELInvokerPluginHandler.LOG.error("Error reading/writing File", e);
            return false;
        }

        return true;
    }

    public boolean handle(final BPELPlanContext context, final String operationName, final String interfaceName,
                          final String callbackAddressVarName, final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput,
                          final BPELScopePhaseType appendToPrePhase) throws Exception {

        // fetch "meta"-data for invoker message (e.g. csarid, nodetemplate
        // id..)
        boolean isNodeTemplate = true;
        String templateId = "";
        if (context.getNodeTemplate() != null) {
            templateId = context.getNodeTemplate().getId();
        } else {
            templateId = context.getRelationshipTemplate().getId();
            isNodeTemplate = false;
        }
        return this.handle(context, templateId, isNodeTemplate, operationName, interfaceName, callbackAddressVarName,
                           internalExternalPropsInput, internalExternalPropsOutput, appendToPrePhase);
    }

    private List<String> getRunScriptParams(final AbstractNodeTemplate nodeTemplate) {
        final List<String> inputParams = new ArrayList<>();

        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)) {
                    for (final AbstractParameter param : op.getInputParameters()) {
                        inputParams.add(param.getName());
                    }
                }
            }
        }

        return inputParams;
    }

    private List<String> getTransferFileParams(final AbstractNodeTemplate nodeTemplate) {
        final List<String> inputParams = new ArrayList<>();

        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
                    for (final AbstractParameter param : op.getInputParameters()) {
                        inputParams.add(param.getName());
                    }
                }
            }
        }

        return inputParams;
    }

    public boolean handleArtifactReferenceUpload(final AbstractArtifactReference ref,
                                                 final BPELPlanContext templateContext, final Variable serverIp,
                                                 final Variable sshUser, final Variable sshKey,
                                                 final AbstractNodeTemplate infraTemplate,
                                                 final BPELScopePhaseType appendToPrePhase) throws Exception {
        BPELInvokerPluginHandler.LOG.debug("Handling DA " + ref.getReference());


        /*
         * Contruct all needed data (paths, url, scripts)
         */
        // TODO /home/ec2-user/ or ~ is a huge assumption
        // the path to the file on the ubuntu vm being uploaded
        final String ubuntuFilePath = "~/" + templateContext.getCSARFileName() + "/" + ref.getReference();
        final String ubuntuFilePathVarName = "ubuntuFilePathVar" + templateContext.getIdForNames();
        final Variable ubuntuFilePathVar =
            templateContext.createGlobalStringVariable(ubuntuFilePathVarName, ubuntuFilePath);
        // the folder which has to be created on the ubuntu vm
        final String ubuntuFolderPathScript = "sleep 1 && mkdir -p " + fileReferenceToFolder(ubuntuFilePath);
        final String containerAPIAbsoluteURIXPathQuery =
            this.bpelFrags.createXPathQueryForURLRemoteFilePath(ref.getReference());
        final String containerAPIAbsoluteURIVarName = "containerApiFileURL" + templateContext.getIdForNames();
        /*
         * create a string variable with a complete URL to the file we want to upload
         */

        final Variable containerAPIAbsoluteURIVar =
            templateContext.createGlobalStringVariable(containerAPIAbsoluteURIVarName, "");

        try {
            Node assignNode = loadAssignXpathQueryToStringVarFragmentAsNode("assign" + templateContext.getIdForNames(),
                                                                            containerAPIAbsoluteURIXPathQuery,
                                                                            containerAPIAbsoluteURIVar.getName());
            assignNode = templateContext.importNode(assignNode);

            switch (appendToPrePhase) {
                case PRE:
                    templateContext.getPrePhaseElement().appendChild(assignNode);
                    break;
                case PROVISIONING:
                    templateContext.getProvisioningPhaseElement().appendChild(assignNode);
                    break;
                case POST:
                    templateContext.getPostPhaseElement().appendChild(assignNode);
                    break;
            }
        }
        catch (final IOException e) {
            BPELInvokerPluginHandler.LOG.error("Couldn't read internal file", e);
            return false;
        }
        catch (final SAXException e) {
            BPELInvokerPluginHandler.LOG.error("Couldn't parse internal xml file");
            return false;
        }

        /*
         * create the folder the file must be uploaded into
         */

        final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();

        final String mkdirScriptVarName = "mkdirScript" + templateContext.getIdForNames();
        final Variable mkdirScriptVar =
            templateContext.createGlobalStringVariable(mkdirScriptVarName, ubuntuFolderPathScript);

        // quick and dirty hack to check if we're using old or new properties
        final String cleanName = serverIp.getName().substring(serverIp.getName().lastIndexOf("_") + 1);

        final List<String> runScriptInputParams = getRunScriptParams(infraTemplate);


        switch (cleanName) {
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
                // old nodetype properties
                if (serverIp != null && runScriptInputParams.contains("hostname")) {
                    runScriptRequestInputParams.put("hostname", serverIp);
                }
                if (sshKey != null && runScriptInputParams.contains("sshKey")) {
                    runScriptRequestInputParams.put("sshKey", sshKey);
                }
                if (sshUser != null && runScriptInputParams.contains("sshUser")) {
                    runScriptRequestInputParams.put("sshUser", sshUser);
                }
                runScriptRequestInputParams.put("script", mkdirScriptVar);
                this.handle(templateContext, infraTemplate.getId(), true, "runScript", "InterfaceUbuntu",
                            "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(),
                            appendToPrePhase);
                break;
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
                // new nodetype properties
                if (serverIp != null && runScriptInputParams.contains("VMIP")) {
                    runScriptRequestInputParams.put("VMIP", serverIp);
                }
                if (sshUser != null && runScriptInputParams.contains("VMUserName")) {
                    runScriptRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && runScriptInputParams.contains("VMPrivateKey")) {
                    runScriptRequestInputParams.put("VMPrivateKey", sshKey);
                }
                runScriptRequestInputParams.put("Script", mkdirScriptVar);
                this.handle(templateContext, infraTemplate.getId(), true, "runScript",
                            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
                            runScriptRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);

                break;

            default:
                return false;
        }

        /*
         * append transferFile logic with method: methodname: transferFile params: hostname sshUser sshKey
         * targetAbsolutePath sourceURLorLocalAbsolutePath
         */
        final Map<String, Variable> transferFileRequestInputParams = new HashMap<>();

        final String cleanName2 = serverIp.getName().substring(serverIp.getName().lastIndexOf("_") + 1);

        final List<String> transferFileInputParams = getTransferFileParams(infraTemplate);

        switch (cleanName2) {
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
                if (serverIp != null && transferFileInputParams.contains("hostname")) {
                    transferFileRequestInputParams.put("hostname", serverIp);
                }
                if (sshUser != null && transferFileInputParams.contains("sshUser")) {
                    transferFileRequestInputParams.put("sshUser", sshUser);
                }
                if (sshKey != null && transferFileInputParams.contains("sshKey")) {
                    transferFileRequestInputParams.put("sshKey", sshKey);
                }
                transferFileRequestInputParams.put("targetAbsolutePath", ubuntuFilePathVar);
                transferFileRequestInputParams.put("sourceURLorLocalAbsolutePath", containerAPIAbsoluteURIVar);
                this.handle(templateContext, infraTemplate.getId(), true, "transferFile", "InterfaceUbuntu",
                            "planCallbackAddress_invoker", transferFileRequestInputParams,
                            new HashMap<String, Variable>(), appendToPrePhase);
                break;
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
                if (serverIp != null && transferFileInputParams.contains("VMIP")) {
                    transferFileRequestInputParams.put("VMIP", serverIp);
                }
                if (sshUser != null && transferFileInputParams.contains("VMUserName")) {
                    transferFileRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && transferFileInputParams.contains("VMPrivateKey")) {
                    transferFileRequestInputParams.put("VMPrivateKey", sshKey);
                }
                transferFileRequestInputParams.put("TargetAbsolutePath", ubuntuFilePathVar);
                transferFileRequestInputParams.put("SourceURLorLocalPath", containerAPIAbsoluteURIVar);
                this.handle(templateContext, infraTemplate.getId(), true, "transferFile",
                            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
                            transferFileRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);

                break;
            default:
                return false;
        }

        return true;
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param csarEntryXpathQuery the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a DOM Node representing a BPEL assign element
     * @throws IOException is thrown when loading internal bpel fragments fails
     * @throws SAXException is thrown when parsing internal format into DOM fails
     */
    public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                                                              final String stringVarName) throws IOException,
                                                                                          SAXException {
        final String templateString =
            loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param xpath2Query the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                                                                  final String stringVarName) throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignStringVarWithXpath2Query.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }
}
