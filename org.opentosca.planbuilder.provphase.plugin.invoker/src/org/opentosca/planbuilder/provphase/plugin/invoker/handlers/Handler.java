package org.opentosca.planbuilder.provphase.plugin.invoker.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
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
import org.opentosca.planbuilder.fragments.Fragments;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Handler {
	
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	private ResourceHandler resHandler;
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
	
	
	public Handler() {
		try {
			this.resHandler = new ResourceHandler();
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docFactory.setNamespaceAware(true);
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize ResourceHandler", e);
		}
	}
	
	private void appendLOGActivity(final TemplatePlanContext context, final String operation) {
		String logMessageVarName = null;
		String logMessageContent = null;
		if (context.getNodeTemplate() != null) {
			logMessageVarName = "instanceDataLogMsg_" + context.getNodeTemplate().getId() + "_" + operation + "_" + System.currentTimeMillis();
			logMessageContent = "<log>Executing operation " + operation + " of NodeTemplate " + context.getNodeTemplate().getId() + "</log>";
		} else {
			logMessageVarName = "instanceDataLogMsg_" + context.getRelationshipTemplate().getId() + (operation != null ? "_" + operation : "") + "_" + System.currentTimeMillis();
			logMessageContent = "<log>Executing " + (operation != null ? "operation " + operation + " of " : "") + "RelationshipTemplate " + context.getRelationshipTemplate().getId() + "</log>";
		}
		
		// create variable
		logMessageVarName = context.createGlobalStringVariable(logMessageVarName, logMessageContent).getName();
		final Variable correlationIdVar = context.generateVariableWithRandomValue();
		String serviceInstanceURLVar = null;
		for (final String varName : context.getMainVariableNames()) {
			if (varName.contains(Handler.ServiceInstanceVarKeyword)) {
				serviceInstanceURLVar = varName;
			}
		}
		
		try {
			Node assignInputToVar = new Fragments().generateAssignFromInputMessageToStringVariableAsNode("CorrelationID", correlationIdVar.getName());
			Node logPOSTNode = new Fragments().createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(serviceInstanceURLVar, logMessageVarName, correlationIdVar.getName());
			assignInputToVar = context.importNode(assignInputToVar);
			logPOSTNode = context.importNode(logPOSTNode);
			
			context.getProvisioningPhaseElement().appendChild(assignInputToVar);
			context.getProvisioningPhaseElement().appendChild(logPOSTNode);
			
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean handle(final TemplatePlanContext context, final AbstractOperation operation, final AbstractImplementationArtifact ia) throws IOException {
		// register wsdls and xsd
		final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), this.resHandler.getServiceInvokerWSDLFile());
		final QName invokerCallbackPortType = context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), this.resHandler.getServiceInvokerWSDLFile());
		
		// atleast the xsd should be imported now in the plan
		context.registerType(this.resHandler.getServiceInvokerAsyncRequestXSDType(), this.resHandler.getServiceInvokerXSDFile());
		context.registerType(this.resHandler.getServiceInvokerAsyncResponseXSDType(), this.resHandler.getServiceInvokerXSDFile());
		
		final QName InputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncRequestMessageType());
		final String InputMessagePartName = this.resHandler.getServiceInvokerAsyncRequestMessagePart();
		final QName OutputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncResponseMessageType());
		final String OutputMessagePartName = this.resHandler.getServiceInvokerAsyncResponseMessagePart();
		
		// generate partnerlink from the two porttypes
		final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee", invokerPortType);
		final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();
		
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);
		
		// register request and response message
		final String requestVariableName = invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
		context.addVariable(requestVariableName, TOSCAPlan.VariableType.MESSAGE, InputMessageId);
		final String responseVariableName = invokerCallbackPortType.getLocalPart() + OutputMessageId.getLocalPart() + "Response" + context.getIdForNames();
		context.addVariable(responseVariableName, TOSCAPlan.VariableType.MESSAGE, OutputMessageId);
		
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
		
		final String interfaceName = this.findInterfaceForOperation(context, operation);
		final String operationName = operation.getName();
		
		// fetch the input parameters of the operation and check whether their
		// internal or external
		
		// map to store input parameter names to bpel variable names, if value
		// is null
		// -> parameter is external
		final Map<String, Variable> internalExternalPropsInput = new HashMap<>();
		final Map<String, Variable> internalExternalPropsOutput = new HashMap<>();
		
		for (final AbstractParameter para : operation.getInputParameters()) {
			Variable propWrapper = context.getPropertyVariable(para.getName());
			if (propWrapper == null) {
				propWrapper = context.getPropertyVariable(para.getName(), true);
				if (propWrapper == null) {
					propWrapper = context.getPropertyVariable(para.getName(), false);
				}
			}
			
			internalExternalPropsInput.put(para.getName(), propWrapper);
		}
		
		for (final AbstractParameter para : operation.getOutputParameters()) {
			Variable propWrapper = context.getPropertyVariable(para.getName());
			if (propWrapper == null) {
				propWrapper = context.getPropertyVariable(para.getName(), true);
				if (propWrapper == null) {
					propWrapper = context.getPropertyVariable(para.getName(), false);
				}
			}
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
		
		String serviceInstanceIdVarName = null;
		
		for (final String varName : context.getMainVariableNames()) {
			if (varName.contains("OpenTOSCAContainerAPIServiceInstanceID")) {
				serviceInstanceIdVarName = varName;
			}
		}
		
		if (serviceInstanceIdVarName == null) {
			return false;
		}
		
		// add request message assign to prov phase scope
		try {
			Node assignNode = this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(csarId, serviceTemplateId, serviceInstanceIdVarName, operationName, String.valueOf(System.currentTimeMillis()), requestVariableName, InputMessagePartName, interfaceName, isNodeTemplate, templateId, internalExternalPropsInput);
			assignNode = context.importNode(assignNode);
			
			Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
			addressingCopyInit = context.importNode(addressingCopyInit);
			assignNode.appendChild(addressingCopyInit);
			
			Node addressingCopyNode = this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
			addressingCopyNode = context.importNode(addressingCopyNode);
			assignNode.appendChild(addressingCopyNode);
			
			// adds field into plan input message to give the plan it's own
			// address
			// for the invoker PortType (callback etc.). This is needed as WSO2
			// BPS
			// 2.x can't give that at runtime (bug)
			Handler.LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");
			
			/*
			 * Will be needed when we start to switch to a new bpel engine
			 * String callbackAddressVarName =
			 * this.inputHasCallbackAddressDefined(context);
			 *
			 * if (callbackAddressVarName == null) { // if the plan doesn't have
			 * an input message for the address of // the plan itself (for
			 * callback/bps2.1.2) we get the address at // runtime Node
			 * replyToCopy =
			 * this.resHandler.generateReplyToCopyAsNode(partnerLinkName,
			 * requestVariableName, InputMessagePartName, "ReplyTo");
			 * replyToCopy = context.importNode(replyToCopy);
			 * assignNode.appendChild(replyToCopy); } else { // else the address
			 * is provided in the input message
			 */
			
			Node replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName, InputMessagePartName, "ReplyTo");
			replyToCopy = context.importNode(replyToCopy);
			assignNode.appendChild(replyToCopy);
			
			context.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (final SAXException e) {
			Handler.LOG.error("Couldn't generate DOM node for the request message assign element", e);
			return false;
		}

		this.appendLOGActivity(context, operation.getName());
		
		// invoke service invoker
		// add invoke
		try {
			Node invokeNode = this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, "invokeOperationAsync", invokerPortType, requestVariableName);
			Handler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
			invokeNode = context.importNode(invokeNode);
			
			Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
			correlationSetsNode = context.importNode(correlationSetsNode);
			invokeNode.appendChild(correlationSetsNode);
			
			context.getProvisioningPhaseElement().appendChild(invokeNode);
		} catch (final SAXException e) {
			Handler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (final IOException e) {
			Handler.LOG.error("Error reading/writing File", e);
			return false;
		}
		
		// add receive for service invoker callback
		try {
			Node receiveNode = this.resHandler.generateReceiveAsNode("receive_" + responseVariableName, partnerLinkName, "callback", invokerCallbackPortType, responseVariableName);
			receiveNode = context.importNode(receiveNode);
			
			Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, false);
			correlationSetsNode = context.importNode(correlationSetsNode);
			receiveNode.appendChild(correlationSetsNode);
			
			context.getProvisioningPhaseElement().appendChild(receiveNode);
		} catch (final SAXException e1) {
			Handler.LOG.error("Error reading/writing XML File", e1);
			return false;
		} catch (final IOException e1) {
			Handler.LOG.error("Error reading/writing File", e1);
			return false;
		}
		
		// process response message
		// add assign for response
		try {
			
			Node responseAssignNode = this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName, internalExternalPropsOutput, "assign_" + responseVariableName, OutputMessageId, context.getPlanResponseMessageName(), "payload");
			Handler.LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
			responseAssignNode = context.importNode(responseAssignNode);
			context.getProvisioningPhaseElement().appendChild(responseAssignNode);
		} catch (final SAXException e) {
			Handler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (final IOException e) {
			Handler.LOG.error("Error reading/writing File", e);
			return false;
		}
		
		return true;
	}
	
	public boolean handle(final TemplatePlanContext context, final String templateId, final boolean isNodeTemplate, final String operationName, final String interfaceName, final String callbackAddressVarName, final Map<String, Variable> internalExternalPropsInput, final Map<String, Variable> internalExternalPropsOutput, final boolean appendToPrePhase) throws IOException {
		// register wsdls and xsd
		final QName invokerPortType = context.registerPortType(this.resHandler.getServiceInvokerPortType(), this.resHandler.getServiceInvokerWSDLFile());
		final QName invokerCallbackPortType = context.registerPortType(this.resHandler.getServiceInvokerCallbackPortType(), this.resHandler.getServiceInvokerWSDLFile());
		
		// atleast the xsd should be imported now in the plan
		context.registerType(this.resHandler.getServiceInvokerAsyncRequestXSDType(), this.resHandler.getServiceInvokerXSDFile());
		context.registerType(this.resHandler.getServiceInvokerAsyncResponseXSDType(), this.resHandler.getServiceInvokerXSDFile());
		
		final QName InputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncRequestMessageType());
		final String InputMessagePartName = this.resHandler.getServiceInvokerAsyncRequestMessagePart();
		final QName OutputMessageId = context.importQName(this.resHandler.getServiceInvokerAsyncResponseMessageType());
		final String OutputMessagePartName = this.resHandler.getServiceInvokerAsyncResponseMessagePart();
		
		// generate partnerlink from the two porttypes
		final String partnerLinkTypeName = invokerPortType.getLocalPart() + "PLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "Requester", invokerCallbackPortType, "Requestee", invokerPortType);
		final String partnerLinkName = invokerPortType.getLocalPart() + "PL" + context.getIdForNames();
		
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);
		
		// register request and response message
		final String requestVariableName = invokerPortType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
		context.addVariable(requestVariableName, TOSCAPlan.VariableType.MESSAGE, InputMessageId);
		final String responseVariableName = invokerCallbackPortType.getLocalPart() + OutputMessageId.getLocalPart() + "Response" + context.getIdForNames();
		context.addVariable(responseVariableName, TOSCAPlan.VariableType.MESSAGE, OutputMessageId);
		
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
		
		String serviceInstanceIdVarName = null;
		
		for (final String varName : context.getMainVariableNames()) {
			if (varName.contains("OpenTOSCAContainerAPIServiceInstanceID")) {
				serviceInstanceIdVarName = varName;
			}
		}
		
		if (serviceInstanceIdVarName == null) {
			return false;
		}
		
		// add request message assign to prov phase scope
		try {
			Node assignNode = this.resHandler.generateInvokerRequestMessageInitAssignTemplateAsNode(context.getCSARFileName(), context.getServiceTemplateId(), serviceInstanceIdVarName, operationName, String.valueOf(System.currentTimeMillis()), requestVariableName, InputMessagePartName, interfaceName, isNodeTemplate, templateId, internalExternalPropsInput);
			assignNode = context.importNode(assignNode);
			
			Node addressingCopyInit = this.resHandler.generateAddressingInitAsNode(requestVariableName);
			addressingCopyInit = context.importNode(addressingCopyInit);
			assignNode.appendChild(addressingCopyInit);
			
			Node addressingCopyNode = this.resHandler.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
			addressingCopyNode = context.importNode(addressingCopyNode);
			assignNode.appendChild(addressingCopyNode);
			
			// if (callbackAddressVarName == null) {
			// if the plan doesn't have an input message for the address of
			// the plan itself (for callback/bps2.1.2) we get the address at
			// runtime
			Node replyToCopy = this.resHandler.generateReplyToCopyAsNode(partnerLinkName, requestVariableName, InputMessagePartName, "ReplyTo");
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
			
			if (appendToPrePhase) {
				context.getPrePhaseElement().appendChild(assignNode);
			} else {
				context.getProvisioningPhaseElement().appendChild(assignNode);
			}
			
		} catch (final SAXException e) {
			Handler.LOG.error("Couldn't generate DOM node for the request message assign element", e);
			return false;
		}
		
		this.appendLOGActivity(context, operationName);
		// invoke service invoker
		// add invoke
		try {
			Node invokeNode = this.resHandler.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, "invokeOperationAsync", invokerPortType, requestVariableName);
			Handler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
			invokeNode = context.importNode(invokeNode);
			
			Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, true);
			correlationSetsNode = context.importNode(correlationSetsNode);
			invokeNode.appendChild(correlationSetsNode);
			
			if (appendToPrePhase) {
				context.getPrePhaseElement().appendChild(invokeNode);
			} else {
				context.getProvisioningPhaseElement().appendChild(invokeNode);
			}
			
		} catch (final SAXException e) {
			Handler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (final IOException e) {
			Handler.LOG.error("Error reading/writing File", e);
			return false;
		}
		
		// add receive for service invoker callback
		try {
			Node receiveNode = this.resHandler.generateReceiveAsNode("receive_" + responseVariableName, partnerLinkName, "callback", invokerCallbackPortType, responseVariableName);
			receiveNode = context.importNode(receiveNode);
			
			Node correlationSetsNode = this.resHandler.generateCorrelationSetsAsNode(correlationSetName, false);
			correlationSetsNode = context.importNode(correlationSetsNode);
			receiveNode.appendChild(correlationSetsNode);
			
			if (appendToPrePhase) {
				context.getPrePhaseElement().appendChild(receiveNode);
			} else {
				context.getProvisioningPhaseElement().appendChild(receiveNode);
			}
			
		} catch (final SAXException e1) {
			Handler.LOG.error("Error reading/writing XML File", e1);
			return false;
		} catch (final IOException e1) {
			Handler.LOG.error("Error reading/writing File", e1);
			return false;
		}
		
		// process response message
		// add assign for response
		try {
			
			Node responseAssignNode = this.resHandler.generateResponseAssignAsNode(responseVariableName, OutputMessagePartName, internalExternalPropsOutput, "assign_" + responseVariableName, OutputMessageId, context.getPlanResponseMessageName(), "payload");
			Handler.LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
			responseAssignNode = context.importNode(responseAssignNode);
			
			if (appendToPrePhase) {
				context.getPrePhaseElement().appendChild(responseAssignNode);
			} else {
				context.getProvisioningPhaseElement().appendChild(responseAssignNode);
			}
			
		} catch (final SAXException e) {
			Handler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (final IOException e) {
			Handler.LOG.error("Error reading/writing File", e);
			return false;
		}
		
		return true;
	}
	
	public boolean handle(final TemplatePlanContext context, final String operationName, final String interfaceName, final String callbackAddressVarName, final Map<String, Variable> internalExternalPropsInput, final Map<String, Variable> internalExternalPropsOutput, final boolean appendToPrePhase) throws IOException {
		
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
		return this.handle(context, templateId, isNodeTemplate, operationName, interfaceName, callbackAddressVarName, internalExternalPropsInput, internalExternalPropsOutput, appendToPrePhase);
	}
	
	public boolean handleArtifactReferenceUpload(final AbstractArtifactReference ref, final TemplatePlanContext templateContext, final Variable serverIp, final Variable sshUser, final Variable sshKey, final String templateId, final boolean appendToPrePhase) throws IOException {
		Handler.LOG.debug("Handling DA " + ref.getReference());
		/*
		 * Contruct all needed data (paths, url, scripts)
		 */
		// TODO /home/ec2-user/ or ~ is a huge assumption
		// the path to the file on the ubuntu vm being uploaded
		final String ubuntuFilePath = "~/" + templateContext.getCSARFileName() + "/" + ref.getReference();
		final String ubuntuFilePathVarName = "ubuntuFilePathVar" + templateContext.getIdForNames();
		final Variable ubuntuFilePathVar = templateContext.createGlobalStringVariable(ubuntuFilePathVarName, ubuntuFilePath);
		// the folder which has to be created on the ubuntu vm
		final String ubuntuFolderPathScript = "sleep 5 && mkdir -p " + this.fileReferenceToFolder(ubuntuFilePath);
		final String containerAPIAbsoluteURIXPathQuery = this.createXPathQueryForURLRemoteFilePath(ref.getReference());
		final String containerAPIAbsoluteURIVarName = "containerApiFileURL" + templateContext.getIdForNames();
		/*
		 * create a string variable with a complete URL to the file we want to
		 * upload
		 */
		
		final Variable containerAPIAbsoluteURIVar = templateContext.createGlobalStringVariable(containerAPIAbsoluteURIVarName, "");
		
		try {
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assign" + templateContext.getIdForNames(), containerAPIAbsoluteURIXPathQuery, containerAPIAbsoluteURIVar.getName());
			assignNode = templateContext.importNode(assignNode);
			
			if (appendToPrePhase) {
				templateContext.getPrePhaseElement().appendChild(assignNode);
			} else {
				templateContext.getProvisioningPhaseElement().appendChild(assignNode);
			}
		} catch (final IOException e) {
			Handler.LOG.error("Couldn't read internal file", e);
			return false;
		} catch (final SAXException e) {
			Handler.LOG.error("Couldn't parse internal xml file");
			return false;
		}
		
		/*
		 * create the folder the file must be uploaded into
		 */
		
		final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();
		
		final String mkdirScriptVarName = "mkdirScript" + templateContext.getIdForNames();
		final Variable mkdirScriptVar = templateContext.createGlobalStringVariable(mkdirScriptVarName, ubuntuFolderPathScript);
		
		// quick and dirty hack to check if we're using old or new properties
		final String cleanName = serverIp.getName().substring(serverIp.getName().lastIndexOf("_") + 1);
		
		switch (cleanName) {
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
			// old nodetype properties
			runScriptRequestInputParams.put("hostname", serverIp);
			runScriptRequestInputParams.put("sshKey", sshKey);
			runScriptRequestInputParams.put("sshUser", sshUser);
			runScriptRequestInputParams.put("script", mkdirScriptVar);
			this.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);
			break;
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
			// new nodetype properties
			runScriptRequestInputParams.put("VMIP", serverIp);
			runScriptRequestInputParams.put("VMUserName", sshUser);
			runScriptRequestInputParams.put("VMPrivateKey", sshKey);
			runScriptRequestInputParams.put("Script", mkdirScriptVar);
			this.handle(templateContext, templateId, true, "runScript", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);
			
			break;
		
		default:
			return false;
		}
		
		/*
		 * append transferFile logic with method: methodname: transferFile
		 * params: hostname sshUser sshKey targetAbsolutePath
		 * sourceURLorLocalAbsolutePath
		 */
		final Map<String, Variable> transferFileRequestInputParams = new HashMap<>();
		
		final String cleanName2 = serverIp.getName().substring(serverIp.getName().lastIndexOf("_") + 1);
		
		switch (cleanName2) {
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
			transferFileRequestInputParams.put("hostname", serverIp);
			transferFileRequestInputParams.put("sshUser", sshUser);
			transferFileRequestInputParams.put("sshKey", sshKey);
			transferFileRequestInputParams.put("targetAbsolutePath", ubuntuFilePathVar);
			transferFileRequestInputParams.put("sourceURLorLocalAbsolutePath", containerAPIAbsoluteURIVar);
			this.handle(templateContext, templateId, true, "transferFile", "InterfaceUbuntu", "planCallbackAddress_invoker", transferFileRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);
			break;
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
			transferFileRequestInputParams.put("VMIP", serverIp);
			transferFileRequestInputParams.put("VMUserName", sshUser);
			transferFileRequestInputParams.put("VMPrivateKey", sshKey);
			transferFileRequestInputParams.put("TargetAbsolutePath", ubuntuFilePathVar);
			transferFileRequestInputParams.put("SourceURLorLocalPath", containerAPIAbsoluteURIVar);
			this.handle(templateContext, templateId, true, "transferFile", Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker", transferFileRequestInputParams, new HashMap<String, Variable>(), appendToPrePhase);
			
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	/**
	 * Removes trailing slashes
	 *
	 * @param ref a path
	 * @return a String without trailing slashes
	 */
	private String fileReferenceToFolder(String ref) {
		Handler.LOG.debug("Getting ref to change to folder ref: " + ref);
		
		final int lastIndexSlash = ref.lastIndexOf("/");
		final int lastIndexDot = ref.lastIndexOf(".");
		if (lastIndexSlash < lastIndexDot) {
			ref = ref.substring(0, lastIndexSlash);
		}
		Handler.LOG.debug("Returning ref: " + ref);
		return ref;
	}
	
	/**
	 * Returns an XPath Query which contructs a valid String, to GET a File from
	 * the openTOSCA API
	 *
	 * @param artifactPath a path inside an ArtifactTemplate
	 * @return a String containing an XPath query
	 */
	public String createXPathQueryForURLRemoteFilePath(final String artifactPath) {
		Handler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
		final String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
		return filePath;
	}
	
	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName the name of the BPEL assign
	 * @param xpath2Query the csarEntryPoint XPath query
	 * @param stringVarName the variable to load the queries results into
	 * @return a String containing a BPEL Assign element
	 * @throws IOException is thrown when reading the BPEL fragment form the
	 *             resources fails
	 */
	public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query, final String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignStringVarWithXpath2Query.xml");
		final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{xpath2query}", xpath2Query);
		template = template.replace("{stringVarName}", stringVarName);
		return template;
	}
	
	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName the name of the BPEL assign
	 * @param csarEntryXpathQuery the csarEntryPoint XPath query
	 * @param stringVarName the variable to load the queries results into
	 * @return a DOM Node representing a BPEL assign element
	 * @throws IOException is thrown when loading internal bpel fragments fails
	 * @throws SAXException is thrown when parsing internal format into DOM
	 *             fails
	 */
	public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query, final String stringVarName) throws IOException, SAXException {
		final String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
		final InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		final Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	private String findInterfaceForOperation(final TemplatePlanContext context, final AbstractOperation operation) {
		List<AbstractInterface> interfaces = null;
		if (context.getNodeTemplate() != null) {
			interfaces = context.getNodeTemplate().getType().getInterfaces();
		} else {
			interfaces = context.getRelationshipTemplate().getRelationshipType().getSourceInterfaces();
			interfaces.addAll(context.getRelationshipTemplate().getRelationshipType().getTargetInterfaces());
		}
		
		if ((interfaces != null) && (interfaces.size() > 0)) {
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
}
