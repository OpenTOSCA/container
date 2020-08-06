package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author kalmankepes
 */
public class BPELNotifyHandler extends PluginHandler {

    private static final String correlationSetPrefix = "NotificationCorrelation";
    private static final String correlationPropertyPrefix = "notifcationProperty";

    private String getGloblaNotifyCorrelationSetName(final BPELPlanContext context) {
        for (final String name : context.getGlobalCorrelationSetNames()) {
            if (name.startsWith(correlationSetPrefix)) {
                return name;
            }
        }
        return null;
    }

    private String getNotifcationProperty(final BPELPlanContext context) {
        for (final String propName : context.getCorrelationProperties()) {
            if (propName.startsWith(correlationPropertyPrefix)) {
                return propName;
            }
        }
        return null;
    }

    private String addNotifyCorrelationProperty(final BPELPlanContext context) {
        // setup correlation property and aliases for request and response
        final String correlationPropertyName = correlationPropertyPrefix + context.getIdForNames();
        context.addProperty(correlationPropertyName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        return correlationPropertyName;
    }

    private String addNotifyCorrelationSet(final BPELPlanContext context, final String correlationPropertyName) {
        final String correlationSetName = correlationSetPrefix + context.getIdForNames();
        context.addGlobalCorrelationSet(correlationSetName, correlationPropertyName);
        return correlationSetName;
    }

    private boolean isNotifcationCorrelationSet(final BPELPlanContext context) {
        return getGloblaNotifyCorrelationSetName(context) != null & getNotifcationProperty(context) != null;
    }

    public boolean handleNotifyPartners(final BPELPlanContext context) throws SAXException, IOException {

        final Path xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final Path wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        // register wsdls and xsd
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);
        final QName invokerCallbackPortType =
            context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), wsdlFile);
        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerNotifyPartnersMessageXSDType(), xsdFile);

        final QName InputMessageId =
            context.importQName(this.resHandler.getServiceInvokerNotifyPartnersMessageXSDType());
        final String InputMessagePartName = this.resHandler.getServiceInvokerNotifyPartnersMessagePart();

        // generate partnerlink from the two porttypes
        final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();

        context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee",
            invokerPortType);

        final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();

        context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

        final Map<String, Variable> params = new HashMap<>();

        final String myPartnerId = getMyPartnerId(context);
        final Variable myPartnerIdVar = context.createGlobalStringVariable("myPartnerId", myPartnerId);
        params.put("SendingPartner", myPartnerIdVar);

        // register request and response message
        final String requestVariableName =
            invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
        context.addVariable(requestVariableName, BPELPlan.VariableType.MESSAGE, InputMessageId);

        // setup a correlation set for the messages
        String correlationSetName = null;
        String correlationPropertyName = null;

        if (isNotifcationCorrelationSet(context)) {
            correlationPropertyName = getNotifcationProperty(context);
            correlationSetName = getGloblaNotifyCorrelationSetName(context);
        } else {
            correlationPropertyName = addNotifyCorrelationProperty(context);
            correlationSetName = addNotifyCorrelationSet(context, correlationPropertyName);
        }

        final String query =
            "//*[local-name()=\"PlanCorrelationID\" and namespace-uri()=\"http://siserver.org/schema\"]";
        // "/" + InputMessageId.getPrefix() + ":" + "MessageID"
        context.addPropertyAlias(correlationPropertyName, InputMessageId, InputMessagePartName, query);
        // register correlationsets

        // fetch serviceInstanceId

        final String serviceInstanceIdVarName = context.getServiceInstanceURLVarName();

        if (serviceInstanceIdVarName == null) {
            return false;
        }

        // add request message assign to prov phase scope

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
                InputMessagePartName, params);

        assignNode = context.importNode(assignNode);

        Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
        addressingCopyInit = context.importNode(addressingCopyInit);
        assignNode.appendChild(addressingCopyInit);

        Node addressingCopyNode = this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
        addressingCopyNode = context.importNode(addressingCopyNode);
        assignNode.appendChild(addressingCopyNode);

        Node messageIdInit =
            this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, "notifyPartners_"
                + context.getServiceTemplateId().getLocalPart());
        messageIdInit = context.importNode(messageIdInit);
        assignNode.appendChild(messageIdInit);

        context.getProvisioningPhaseElement().appendChild(assignNode);

        this.appendLOGMessageActivity(context, "Executing notify all partners", context.getProvisioningPhaseElement());

        // invoke service invoker
        // add invoke

        Node invokeNode =
            this.resHandler.generateInvokeAsNode("sendNotifyPartners_" + requestVariableName, partnerLinkName,
                "notifyPartners", invokerPortType, requestVariableName);
        BPELInvokeOperationHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
        invokeNode = context.importNode(invokeNode);

        Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
        correlationSetsNode = context.importNode(correlationSetsNode);
        invokeNode.appendChild(correlationSetsNode);

        context.getProvisioningPhaseElement().appendChild(invokeNode);

        return true;
    }

    private String getMyPartnerId(final BPELPlanContext context) {
        return context.getServiceTemplate().getTags().get("participant");
    }

    public boolean addChoreographyParameters(final BPELPlanContext context, final Map<String, Variable> params) {
        final AbstractRelationshipTemplate connectingRelationshipTemplate =
            (AbstractRelationshipTemplate) context.getActivity().getMetadata().get("ConnectingRelationshipTemplate");

        String sendingPartner = null;
        String receivingPartner = null;
        if (connectingRelationshipTemplate != null) {
            sendingPartner = getMyPartnerId(context);
            receivingPartner = getPartnerLocation(connectingRelationshipTemplate.getSource());
            final Variable connectingRelationIdVar = context.createGlobalStringVariable("connectingRelationId_"
                + sendingPartner + "_IDVar_" + context.getIdForNames(), connectingRelationshipTemplate.getId());
            params.put("ConnectingRelationshipTemplate", connectingRelationIdVar);
        } else {
            return false;
        }

        if (sendingPartner != null & receivingPartner != null) {
            final Variable sendingPartnerIdVar =
                context.createGlobalStringVariable("partner_" + sendingPartner + "_IDVar_" + context.getIdForNames(),
                    sendingPartner);
            params.put("SendingPartner", sendingPartnerIdVar);

            final Variable recevingPartnerIdVar =
                context.createGlobalStringVariable("partner_" + receivingPartner + "_IDVar_" + context.getIdForNames(),
                    receivingPartner);
            params.put("ReceivingPartner", recevingPartnerIdVar);
        } else {
            return false;
        }

        return true;
    }

    public boolean handleReceiveNotify(final BPELPlanContext context,
                                       final Map<String, Variable> internalExternalPropsOutput,
                                       final Element elementToAppendTo) throws IOException, SAXException {

        // register wsdls and xsd
        final Path xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final Path wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);
        final QName invokerCallbackPortType =
            context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), wsdlFile);

        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerReceiveNotifyXSDType(), xsdFile);

        final QName OutputMessageId = context.importQName(this.resHandler.getServiceInvokerReceiveNotifyMessageType());
        final String OutputMessagePartName = this.resHandler.getServiceInvokerReceiveNotifyMessagePart();

        // generate partnerlink from the two porttypes
        final String partnerLinkTypeName = invokerCallbackPortType.getLocalPart() + "PLT" + context.getIdForNames();

        context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee",
            invokerPortType);

        final String partnerLinkName = invokerCallbackPortType.getLocalPart() + "PL" + context.getIdForNames();

        context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

        // register request and response message

        final String responseVariableName = invokerCallbackPortType.getLocalPart() + OutputMessageId.getLocalPart()
            + "Response" + context.getIdForNames();
        context.addVariable(responseVariableName, BPELPlan.VariableType.MESSAGE, OutputMessageId);

        // setup a correlation set for the messages
        String correlationSetName = null;
        String correlationPropertyName = null;

        if (isNotifcationCorrelationSet(context)) {
            correlationPropertyName = getNotifcationProperty(context);
            correlationSetName = getGloblaNotifyCorrelationSetName(context);
        } else {
            correlationPropertyName = addNotifyCorrelationProperty(context);
            correlationSetName = addNotifyCorrelationSet(context, correlationPropertyName);
        }

        final String query =
            "//*[local-name()=\"PlanCorrelationID\" and namespace-uri()=\"http://siserver.org/schema\"]";
        // "/" + InputMessageId.getPrefix() + ":" + "MessageID" ageId, InputMessagePartName, query);
        context.addPropertyAlias(correlationPropertyName, OutputMessageId, OutputMessagePartName, query);

        // fetch serviceInstanceId

        final String serviceInstanceIdVarName = context.getServiceInstanceURLVarName();

        if (serviceInstanceIdVarName == null) {
            return false;
        }

        if (context.isNodeTemplate()) {

            appendLOGMessageActivity(context,
                "Executing receive of notify of NodeTemplate " + context.getNodeTemplate().getId(),
                PlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context, "Executing receive of notify  of RelationshipTemplate "
                + context.getRelationshipTemplate().getId() + "", PlanContext.Phase.PROV);
        }

        // add receive for service invoker callback

        Node receiveNode =
            this.resHandler.generateReceiveAsNode("receiveNotify_" + responseVariableName, partnerLinkName,
                "receiveNotify", invokerCallbackPortType, responseVariableName);
        receiveNode = context.importNode(receiveNode);

        Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, false);
        correlationSetsNode = context.importNode(correlationSetsNode);
        receiveNode.appendChild(correlationSetsNode);

        elementToAppendTo.appendChild(receiveNode);

        Node responseAssignNode = null;

        // process response message
        // add assign for response

        responseAssignNode =
            this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName,
                internalExternalPropsOutput, "assign_" + responseVariableName,
                OutputMessageId, context.getPlanResponseMessageName(),
                "payload");
        responseAssignNode = context.importNode(responseAssignNode);

        elementToAppendTo.appendChild(responseAssignNode);

        Node checkForFault =
            this.resHandler.generateBPELIfTrueThrowFaultAsNode("boolean($" + responseVariableName
                    + "//*[local-name()=\"Param\" and namespace-uri()=\"http://siserver.org/schema\"]/*[local-name()=\"key\" and text()=\"Fault\"])",
                new QName("http://opentosca.org/plans/invocationfault",
                    "receiveNotifyFault",
                    "fault"
                        + String.valueOf(System.currentTimeMillis())),
                responseVariableName);

        checkForFault = context.importNode(checkForFault);
        elementToAppendTo.insertBefore(checkForFault, responseAssignNode);

        // elementToAppendTo.appendChild(checkForFault);

        return true;
    }

    public boolean handleSendNotify(final BPELPlanContext context,
                                    final Map<String, Variable> internalExternalPropsInput,
                                    final Element elementToAppendTo) throws IOException, SAXException {
        final boolean isNodeTemplate = context.isNodeTemplate();
        final String templateId =
            isNodeTemplate ? context.getNodeTemplate().getId() : context.getRelationshipTemplate().getId();

        final Path xsdFile = this.resHandler.getServiceInvokerXSDFile(context.getIdForNames());
        final Path wsdlFile = this.resHandler.getServiceInvokerWSDLFile(xsdFile, context.getIdForNames());
        // register wsdls and xsd
        final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), wsdlFile);
        final QName invokerCallbackPortType =
            context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), wsdlFile);
        // atleast the xsd should be imported now in the plan
        context.registerType(this.resHandler.getServiceInvokerNotifyPartnerMessageXSDType(), xsdFile);

        final QName InputMessageId =
            context.importQName(this.resHandler.getServiceInvokerNotifyPartnerMessageXSDType());
        final String InputMessagePartName = this.resHandler.getServiceInvokerNotifyPartnerMessagePart();

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

        // add request message assign to prov phase scope

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

        Node addressingCopyNode = this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
        addressingCopyNode = context.importNode(addressingCopyNode);
        assignNode.appendChild(addressingCopyNode);

        Node messageIdInit =
            this.resHandler.generateMessageIdInitAsNode(requestVariableName, InputMessagePartName, "notify_ "
                + templateId + "_" + context.getServiceTemplateId().getLocalPart());
        messageIdInit = context.importNode(messageIdInit);
        assignNode.appendChild(messageIdInit);

        elementToAppendTo.appendChild(assignNode);

        if (isNodeTemplate) {

            appendLOGMessageActivity(context, "Executing notify  of NodeTemplate " + context.getNodeTemplate().getId(),
                PlanContext.Phase.PROV);
        } else {
            appendLOGMessageActivity(context, "Executing notify RelationshipTemplate "
                + context.getRelationshipTemplate().getId() + "", PlanContext.Phase.PROV);
        }
        // invoke service invoker
        // add invoke

        Node invokeNode = this.resHandler.generateInvokeAsNode("sendNotify_" + requestVariableName, partnerLinkName,
            "notifyPartner", invokerPortType, requestVariableName);
        BPELInvokeOperationHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
        invokeNode = context.importNode(invokeNode);

        Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
        correlationSetsNode = context.importNode(correlationSetsNode);
        invokeNode.appendChild(correlationSetsNode);

        elementToAppendTo.appendChild(invokeNode);

        return true;
    }

    public Collection<AbstractParameter> getAllOperationParameters(final BPELPlanContext context) {
        final Collection<AbstractParameter> parameters = new HashSet<>();
        if (context.isNodeTemplate()) {
            for (final AbstractInterface iface : context.getNodeTemplate().getType().getInterfaces()) {
                for (final AbstractOperation op : iface.getOperations()) {
                    parameters.addAll(op.getInputParameters());
                }
            }
        }
        return parameters;
    }

    public Collection<PropertyVariable> getPartnerPropertyVariables(final BPELPlanContext context) {
        final List<AbstractNodeTemplate> nodes = new ArrayList<>();
        final Collection<PropertyVariable> props = new HashSet<>();

        final AbstractRelationshipTemplate relationshipTemplate =
            (AbstractRelationshipTemplate) context.getActivity().getMetadata().get("ConnectingRelationshipTemplate");

        ModelUtils.getNodesFromNodeToSink(relationshipTemplate.getTarget(), nodes);

        for (final AbstractNodeTemplate infraNode : nodes) {
            for (final PropertyVariable propVar : context.getPropertyVariables(infraNode)) {
                // TODO/FIXME this shouldn't be necessary here..
                if (!propVar.getPropertyName().equals("State")) {
                    props.add(propVar);
                }
            }
        }
        return props;
    }

    public boolean isValidForReceiveNotify(final BPELPlanContext context) {
        // basically we are always valid for receives as we only expect the properties of the partner's
        // nodes
        if (!context.isNodeTemplate()) {
            return false;
        }
        return true;
    }

    public boolean isValidForSendNotify(final BPELPlanContext context) {

        if (!context.getActivity().getType().equals(ActivityType.SENDNODENOTIFY)) {
            return false;
        }
        // for now we'll just return all parameters
        // Collection<AbstractParameter> parameters = this.getAllOperationParameters(context);
        // Map<String, PropertyVariable> paramMacthing = this.matchOperationParamertsToProperties(context);
        // return parameters.size() == paramMacthing.size();
        return true;
    }

    public Map<String, Variable> mapToParamMap(final Collection<PropertyVariable> propertyVariables) {
        final Map<String, Variable> params = new HashMap<>();

        for (final PropertyVariable propVar : propertyVariables) {
            if (propVar != null & propVar.isNodeTemplatePropertyVariable()) {
                params.put(propVar.getPropertyName(), propVar);
            }
        }

        return params;
    }

    public String getPartnerLocation(final AbstractNodeTemplate node) {
        for (final QName qName : node.getOtherAttributes().keySet()) {
            if (qName.getLocalPart().equals("location")) {
                return node.getOtherAttributes().get(qName);
            }
        }
        return null;
    }

    public Map<String, PropertyVariable> matchOperationParamertsToProperties(final BPELPlanContext context) {
        final Map<String, PropertyVariable> params = new HashMap<>();

        // TODO/FIXME right now we match all operation params against the available properties and send them
        // over, maybe too much ?
        final Collection<AbstractParameter> parameters = getAllOperationParameters(context);

        // try to match param against a property and add it to the input of the notify call
        for (final AbstractParameter param : parameters) {
            final PropertyVariable propVar = context.getPropertyVariable(param.getName());
            if (propVar != null) {
                params.put(propVar.getNodeTemplate().getId() + "_" + propVar.getPropertyName(), propVar);
            }
        }
        return params;
    }
}
