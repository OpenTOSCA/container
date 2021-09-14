package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
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

    public boolean handleInvokeOperation(final BPELPlanContext context, final String templateId,
                                         final boolean isNodeTemplate, final String operationName,
                                         final String interfaceName,
                                         final Map<String, Variable> internalExternalPropsInput,
                                         final Map<String, Variable> internalExternalPropsOutput,
                                         final Element elementToAppendTo) throws IOException {
        final Path xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final Path wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
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

        // fetch instance data
        final String serviceInstanceIdVarName = context.getServiceInstanceURLVarName();
        if (serviceInstanceIdVarName == null) {
            return false;
        }

        final String nodeInstanceUrlVarName = context.findInstanceURLVar(templateId, isNodeTemplate);
        if (nodeInstanceUrlVarName == null) {
            return false;
        }

        String nodeInstanceIdVarName = context.findInstanceIDVar(templateId, isNodeTemplate);
        if (nodeInstanceIdVarName == null) {
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
            if (context.getActivity().getType().equals(ActivityType.TERMINATION) || context.getActivity().getType().equals(ActivityType.FREEZE)) {
                nodeInstanceIdVarName = null;
            }
            assignNode =
                this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(context.getCSARFileName(),
                    context.getServiceTemplateId(),
                    serviceInstanceIdVarName, nodeInstanceIdVarName,
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
        } catch (final SAXException e) {
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
        } catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        } catch (final IOException e) {
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
        } catch (final SAXException e1) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e1);
            return false;
        } catch (final IOException e1) {
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
        } catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Error reading/writing XML File", e);
            return false;
        } catch (final IOException e) {
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
                            + System.currentTimeMillis()),
                    responseVariableName);

            checkForFault = context.importNode(checkForFault);
            elementToAppendTo.insertBefore(checkForFault, responseAssignNode);

            // elementToAppendTo.appendChild(checkForFault);
        } catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return true;
    }
}
