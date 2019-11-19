package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author kalmankepes
 *
 */
public class BPELNotifyHandler extends PluginHandler {


    public boolean handleSendNotify(final BPELPlanContext context,
                                    final Map<String, Variable> internalExternalPropsInput,
                                    Element elementToAppendTo) throws IOException {
        boolean isNodeTemplate = context.isNodeTemplate();
        String templateId =
            isNodeTemplate ? context.getNodeTemplate().getId() : context.getRelationshipTemplate().getId();


        final File xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final File wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        // register wsdls and xsd
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);

        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerNotifyPlanMessageXSDType(), xsdFile);


        final QName InputMessageId = context.importQName(this.resHandler.getServiceInvokerNotifyPlanMessageXSDType());
        final String InputMessagePartName = this.resHandler.getServiceInvokerNotifyPlanMessagePart();

        // generate partnerlink from the two porttypes
        final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();

        context.addPartnerLinkType(partnerLinkTypeName, "Requestee", invokerPortType);

        final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();

        context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

        // register request and response message
        final String requestVariableName =
            invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
        context.addVariable(requestVariableName, BPELPlan.VariableType.MESSAGE, InputMessageId);


        // setup a correlation set for the messages
        String correlationSetName = null;

        // setup correlation property and aliases for request and response
        final String correlationPropertyName = invokerPortType.getLocalPart() + "Property" + context.getIdForNames();
        context.addProperty(correlationPropertyName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));

        final String query = "//*[local-name()=\"MessageID\" and namespace-uri()=\"http://siserver.org/schema\"]";
        // "/" + InputMessageId.getPrefix() + ":" + "MessageID"
        context.addPropertyAlias(correlationPropertyName, InputMessageId, InputMessagePartName, query);
        // register correlationsets
        correlationSetName = invokerPortType.getLocalPart() + "CorrelationSet" + context.getIdForNames();
        context.addCorrelationSet(correlationSetName, correlationPropertyName);

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
                this.resHandler.generateNotifyPlanRequestMessageInitAssignTemplate(context.getCSARFileName(),
                                                                                   context.getServiceTemplateId(),
                                                                                   String.valueOf(System.currentTimeMillis()),
                                                                                   requestVariableName,
                                                                                   InputMessagePartName,
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
                this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, "notify_ "
                    + templateId + "_" + context.getServiceTemplateId().getLocalPart());
            messageIdInit = context.importNode(messageIdInit);
            assignNode.appendChild(messageIdInit);


            elementToAppendTo.appendChild(assignNode);

        }
        catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Couldn't generate DOM node for the request message assign element",
                                                 e);
            return false;
        }

        if (isNodeTemplate) {

            appendLOGMessageActivity(context, "Executing notify  of NodeTemplate " + context.getNodeTemplate().getId(),
                                     PlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context, "Executing notify RelationshipTemplate "
                + context.getRelationshipTemplate().getId() + "", PlanContext.Phase.PROV);
        }
        // invoke service invoker
        // add invoke
        try {
            Node invokeNode = this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName,
                                                                   "notifyPlan", invokerPortType, requestVariableName);
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

        return true;
    }

}
