package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELInvokeOperationHandler extends PluginHandler {

    public final static Logger LOG = LoggerFactory.getLogger(BPELInvokeOperationHandler.class);

    public BPELInvokeOperationHandler() {
        super();
    }

    public boolean handleInvokeOperation(final BPELPlanContext context, final AbstractOperation operation,
                                         final AbstractImplementationArtifact ia) throws IOException {

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


        return this.handleInvokeOperation(context, templateId, isNodeTemplate, operationName, interfaceName,
                                          internalExternalPropsInput, internalExternalPropsOutput,
                                          context.getProvisioningPhaseElement());
    }



    public boolean handleInvokeOperation(final BPELPlanContext context, final String operationName,
                                         final String interfaceName, final String callbackAddressVarName,
                                         final Map<String, Variable> internalExternalPropsInput,
                                         final Map<String, Variable> internalExternalPropsOutput,
                                         final Element elementToAppendTo) throws Exception {

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
        return this.handleInvokeOperation(context, templateId, isNodeTemplate, operationName, interfaceName,
                                          internalExternalPropsInput, internalExternalPropsOutput, elementToAppendTo);
    }

    public boolean handleInvokeOperation(final BPELPlanContext context, final String templateId,
                                         final boolean isNodeTemplate, final String operationName,
                                         final String interfaceName,
                                         final Map<String, Variable> internalExternalPropsInput,
                                         final Map<String, Variable> internalExternalPropsOutput,
                                         final Element elementToAppendTo) throws IOException {
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
            // if (context.getPlanType().equals(PlanType.TERMINATE)) {
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
                                                                                      serviceInstanceIdVarName, null,
                                                                                      operationName,
                                                                                      String.valueOf(System.currentTimeMillis()),
                                                                                      requestVariableName,
                                                                                      InputMessagePartName,
                                                                                      interfaceName, isNodeTemplate,
                                                                                      templateId,
                                                                                      internalExternalPropsInput);


            assignNode = context.importNode(assignNode);

            Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
            addressingCopyInit = context.importNode(addressingCopyInit);
            assignNode.appendChild(addressingCopyInit);

            Node addressingCopyNode =
                this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
            addressingCopyNode = context.importNode(addressingCopyNode);
            assignNode.appendChild(addressingCopyNode);


            Node replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName,
                                                                         InputMessagePartName, "ReplyTo");
            replyToCopy = context.importNode(replyToCopy);
            assignNode.appendChild(replyToCopy);


            Node messageIdInit =
                this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, templateId + ":"
                    + interfaceName + ":" + operationName + ":");
            messageIdInit = context.importNode(messageIdInit);
            assignNode.appendChild(messageIdInit);


            elementToAppendTo.appendChild(assignNode);

        }
        catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Couldn't generate DOM node for the request message assign element",
                                                 e);
            return false;
        }

        if (context.getNodeTemplate() != null) {

            appendLOGMessageActivity(context, "Executing operation " + operationName + " of NodeTemplate "
                + context.getNodeTemplate().getId(), PlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context,
                                     "Executing " + (operationName != null ? "operation " + operationName + " of " : "")
                                         + "RelationshipTemplate " + context.getRelationshipTemplate().getId() + "",
                                     PlanContext.Phase.PROV);
        }
        // invoke service invoker
        // add invoke
        try {
            Node invokeNode =
                this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName,
                                                     "invokeOperationAsync", invokerPortType, requestVariableName);
            BPELInvokeOperationHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
            invokeNode = context.importNode(invokeNode);

            Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
            correlationSetsNode = context.importNode(correlationSetsNode);
            invokeNode.appendChild(correlationSetsNode);

            elementToAppendTo.appendChild(invokeNode);

        }
        catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing File", e);
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

            elementToAppendTo.appendChild(receiveNode);
        }
        catch (final SAXException e1) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e1);
            return false;
        }
        catch (final IOException e1) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing File", e1);
            return false;
        }


        Node responseAssignNode = null;


        // process response message
        // add assign for response
        try {

            responseAssignNode =
                this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName,
                                                             internalExternalPropsOutput,
                                                             "assign_" + responseVariableName, OutputMessageId,
                                                             context.getPlanResponseMessageName(), "payload");
            responseAssignNode = context.importNode(responseAssignNode);

            elementToAppendTo.appendChild(responseAssignNode);


        }
        catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        }
        catch (final IOException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing File", e);
            return false;
        }


        try {
            Node checkForFault =
                this.resHandler.generateBPELIfTrueThrowFaultAsNode("boolean($" + responseVariableName
                    + "//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\"Fault\"])",
                                                                   new QName(
                                                                       "http://opentosca.org/plans/invocationfault",
                                                                       templateId + "_" + interfaceName + "_"
                                                                           + operationName,
                                                                       "fault"
                                                                           + String.valueOf(System.currentTimeMillis())),
                                                                   responseVariableName);

            checkForFault = context.importNode(checkForFault);
            elementToAppendTo.insertBefore(checkForFault, responseAssignNode);

            // elementToAppendTo.appendChild(checkForFault);
        }
        catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return true;
    }
}
