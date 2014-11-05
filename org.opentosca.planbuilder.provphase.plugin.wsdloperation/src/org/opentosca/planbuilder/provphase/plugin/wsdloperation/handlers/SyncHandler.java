package org.opentosca.planbuilder.provphase.plugin.wsdloperation.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains the logic to handle synchronous WebService calls
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class SyncHandler {

	private final static Logger LOG = LoggerFactory.getLogger(SyncHandler.class);

	private BPELFragments fragments;


	/**
	 * Contructor
	 */
	public SyncHandler() {
		try {
			this.fragments = new BPELFragments();
		} catch (ParserConfigurationException e) {
			SyncHandler.LOG.error("Couldn't initialize internal BPEL Fragments Handler", e);
		}
	}

	/**
	 * Adds needed logic to call an synchronous WebService to the BuildPlan, in
	 * the ProvPhase of the given TemplatePlanContext
	 *
	 * @param context a TemplatePlanContext where the logical provisioning
	 *            operation should be called
	 * @param operation the operation to call
	 * @param ia the ia implementing the operation
	 * @return true iff adding the BPEL logic was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {

		// fetch mappings
		File wsdlRef = context.getFileFromArtifactReference(this.fetchWsdlRefFromIA(ia));
		QName portType = this.fetchPortTypeFromWsdlMapping(ia);
		String wsdlOperationName = this.fetchOperationNameFromWsdlMapping(ia);
		QName InputMessageId = this.fetchInputMessageIdFromWsdlMapping(ia);
		String inputPartName = this.fetchInputMessagePartNameFromWsdlMapping(ia);
		QName OutputMessageId = this.fetchOutputMessageIdFromWsdlMapping(ia);
		String outputPartName = this.fetchOutputMessagePartNameFromWsdlMapping(ia);
		Map<String, String> inputMappingsToscaWsdl = this.fetchInputParamMappingsFromWsdlMapping(ia);
		Map<String, String> outputMappingsToscaWsdl = this.fetchOutputParamMappingsFromWsdlMapping(ia);

		/* check for external parameters */
		// we check here if there are any properties on the infrastructure path,
		// that match the toscaParameters of the operation
		Map<String, Variable> inputParamPropMappings = context.getInternalExternalParameters(inputMappingsToscaWsdl.keySet());
		Map<String, Variable> outputParamPropMappings = context.getInternalExternalParameters(outputMappingsToscaWsdl.keySet());

		// assign external parameters to plan input message
		for (String inputToscaParam : inputParamPropMappings.keySet()) {
			if (inputParamPropMappings.get(inputToscaParam) == null) {
				context.addStringValueToPlanRequest(inputToscaParam);
			}
		}

		// TODO assing external parameters to outputmessage

		// register wsdl, porttype, partnerlinktype, partnerlink
		portType = context.registerPortType(portType, wsdlRef);
		InputMessageId = context.importQName(InputMessageId);
		OutputMessageId = context.importQName(OutputMessageId);

		String partnerLinkTypeName = portType.getLocalPart() + "PLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "Server", portType);
		String partnerLinkName = portType.getLocalPart() + "PL" + context.getIdForNames();
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, null, "Server", true);

		// register request and response message

		String requestVariableName = portType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
		context.addVariable(requestVariableName, BuildPlan.VariableType.MESSAGE, InputMessageId);
		String responseVariableName = portType.getLocalPart() + InputMessageId.getLocalPart() + "Response" + context.getIdForNames();
		context.addVariable(responseVariableName, BuildPlan.VariableType.MESSAGE, OutputMessageId);

		// add assign for request
		try {
			Node requestAssignNode = this.fragments.getGenericAssignAsNode(InputMessageId, requestVariableName, inputPartName, inputMappingsToscaWsdl, inputParamPropMappings, "assign_" + requestVariableName, context.getPlanRequestMessageName(), "payload");
			SyncHandler.LOG.debug("Trying to ImportNode: " + requestAssignNode.toString());
			requestAssignNode = context.importNode(requestAssignNode);
			context.getProvisioningPhaseElement().appendChild(requestAssignNode);
		} catch (SAXException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		} catch (IOException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		}

		// add invoke
		try {
			Node invokeNode = this.fragments.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, wsdlOperationName, portType, requestVariableName, responseVariableName);
			SyncHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
			invokeNode = context.importNode(invokeNode);
			context.getProvisioningPhaseElement().appendChild(invokeNode);
		} catch (SAXException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		} catch (IOException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		}

		// add assign for response
		try {
			Node responseAssignNode = this.fragments.generateResponseAssignAsNode(responseVariableName, outputPartName, outputMappingsToscaWsdl, outputParamPropMappings, "assign_" + responseVariableName, OutputMessageId);
			SyncHandler.LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
			responseAssignNode = context.importNode(responseAssignNode);
			context.getProvisioningPhaseElement().appendChild(responseAssignNode);
		} catch (SAXException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		} catch (IOException e) {
			SyncHandler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		}

		return true;
	}

	/**
	 * Returns an ArtifactReference which contains a WebService description
	 *
	 * @param ia an AbstractImplementaionArtifact
	 * @return an AbstractArtifactReference if the given IA contains a reference
	 *         to a WSDL file, else null
	 */
	private AbstractArtifactReference fetchWsdlRefFromIA(AbstractImplementationArtifact ia) {
		for (AbstractArtifactReference refs : ia.getArtifactRef().getArtifactReferences()) {
			if (refs.getReference().endsWith(".wsdl")) {
				return refs;
			}
		}
		return null;
	}

	/**
	 * Returns mappings from TOSCA input parameters to WSDL input message
	 * localNames
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a Map from String to String, where the key is a TOSCA Parameters
	 *         and the value a localName of a WSDL message
	 */
	private Map<String, String> fetchInputParamMappingsFromWsdlMapping(AbstractImplementationArtifact ia) {
		Map<String, String> paramMappings = new HashMap<String, String>();
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {
				NodeList nodeList = domElement.getElementsByTagNameNS("http://example.com/ba", "inputMappings");
				if (nodeList.getLength() != 1) {
					// error
					return paramMappings;
				}
				Node inputMappingsNode = nodeList.item(0);
				NodeList childNodesList = inputMappingsNode.getChildNodes();
				if (childNodesList.getLength() <= 0) {
					// error
					return paramMappings;
				}

				for (int index = 0; index < childNodesList.getLength(); index++) {
					Node inputMappingNode = childNodesList.item(index);
					if (!inputMappingNode.hasAttributes() || !inputMappingNode.getLocalName().equals("inputMapping")) {
						// error, but continue
						continue;
					}
					Node toscaParamNode = inputMappingNode.getAttributes().getNamedItem("toscaParam");
					Node wsdlParamNode = inputMappingNode.getAttributes().getNamedItem("wsdlParam");
					if ((toscaParamNode == null) || (wsdlParamNode == null)) {
						// error, but continue
						continue;
					}
					paramMappings.put(toscaParamNode.getNodeValue(), wsdlParamNode.getNodeValue());
				}
			}
		}
		return paramMappings;
	}

	/**
	 * Returns mappings from TOSCA output parameters to WSDL output message
	 * localNames
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a Map from String to String, where the key is a TOSCA Parameters
	 *         and the value a localName of a WSDL message
	 */
	private Map<String, String> fetchOutputParamMappingsFromWsdlMapping(AbstractImplementationArtifact ia) {
		Map<String, String> paramMappings = new HashMap<String, String>();
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {
				NodeList nodeList = domElement.getElementsByTagNameNS("http://example.com/ba", "outputMappings");
				if (nodeList.getLength() != 1) {
					// error
					return paramMappings;
				}
				Node outputMappingsNode = nodeList.item(0);
				NodeList childNodesList = outputMappingsNode.getChildNodes();
				if (childNodesList.getLength() <= 0) {
					// error
					return paramMappings;
				}

				for (int index = 0; index < childNodesList.getLength(); index++) {
					Node outputMappingNode = childNodesList.item(index);
					if (!outputMappingNode.hasAttributes() || !outputMappingNode.getLocalName().equals("outputMapping")) {
						// error, but continue
						continue;
					}
					Node toscaParamNode = outputMappingNode.getAttributes().getNamedItem("toscaParam");
					Node wsdlParamNode = outputMappingNode.getAttributes().getNamedItem("wsdlParam");
					if ((toscaParamNode == null) || (wsdlParamNode == null)) {
						// error, but continue
						continue;
					}
					paramMappings.put(toscaParamNode.getNodeValue(), wsdlParamNode.getNodeValue());
				}
			}
		}
		return paramMappings;
	}

	/**
	 * Returns the XSD Type of the used WSDL input message
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a QName representing the XSD type of a WSDL input message
	 */
	private QName fetchInputMessageIdFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {

				Node inputNode = this.getNodeFromNodeList(domElement.getChildNodes(), "wsdlInputMessage");
				if (inputNode == null) {
					return null;
				}

				String inputNodeValue = inputNode.getTextContent();
				String[] inputNodeValueParts = inputNodeValue.split("}");
				if (inputNodeValueParts.length != 2) {
					return null;
				}
				String inputNs = inputNodeValueParts[0].substring(1);
				String inputLocal = inputNodeValueParts[1];
				return new QName(inputNs, inputLocal);
			}
		}
		return null;
	}

	/**
	 * Returns the partName of the response message
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a String containing a partName, if not specified null
	 */
	private String fetchOutputMessagePartNameFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {

				Node inputNode = this.getNodeFromNodeList(domElement.getChildNodes(), "wsdlOutputMessage");
				if (inputNode == null) {
					return null;
				}
				Node partNameNode = inputNode.getAttributes().getNamedItem("partName");
				if (partNameNode == null) {
					return null;
				} else {
					return partNameNode.getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the partName of the request message
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a String containing a partName, if not specified null
	 */
	private String fetchInputMessagePartNameFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {

				Node inputNode = this.getNodeFromNodeList(domElement.getChildNodes(), "wsdlInputMessage");
				if (inputNode == null) {
					return null;
				}
				Node partNameNode = inputNode.getAttributes().getNamedItem("partName");
				if (partNameNode == null) {
					return null;
				} else {
					return partNameNode.getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the XSD Type of the used WSDL output message
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a QName representing the XSD type of a WSDL input message
	 */
	private QName fetchOutputMessageIdFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {

				Node outputNode = this.getNodeFromNodeList(domElement.getChildNodes(), "wsdlOutputMessage");
				if (outputNode == null) {
					return null;
				}
				String outputNodeValue = outputNode.getTextContent();
				String[] outputNodeValueParts = outputNodeValue.split("}");
				if (outputNodeValueParts.length != 2) {
					return null;
				}
				String outputNs = outputNodeValueParts[0].substring(1);
				String outputLocal = outputNodeValueParts[1];
				return new QName(outputNs, outputLocal);
			}
		}
		return null;
	}

	/**
	 * Returns the sychronous WSDL operation name of the given IA
	 *
	 * @param ia an AbstractImplementationArtifact containing a wsdlMapping
	 * @return a String containing a WSDL operation name, if not specified null
	 */
	private String fetchOperationNameFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {
				Node operationNode = this.getNodeFromNodeList(domElement.getChildNodes(), "wsdlOperation");
				if (operationNode == null) {
					return null;
				}
				String operationString = operationNode.getTextContent();
				return (operationString.equals("") || operationString.isEmpty()) ? null : operationString;
			}
		}
		return null;
	}

	/**
	 * Returns the WSDL PortType from the given IA
	 *
	 * @param ia an AbstractImplementationArtifact containing a wsdlMapping
	 * @return a QName containing a WSDL portType, if not specified null
	 */
	private QName fetchPortTypeFromWsdlMapping(AbstractImplementationArtifact ia) {
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {
				// found wsdlMapping
				NodeList nodeList = domElement.getChildNodes();

				Node portTypeNode = this.getNodeFromNodeList(nodeList, "portType");
				if (portTypeNode == null) {
					return null;
				}
				String portTypeString = portTypeNode.getTextContent();
				String[] portTypeParts = portTypeString.split("}");
				if (portTypeParts.length != 2) {
					// portType declaration not in form {ns}local
					return null;
				}
				String portTypeNs = portTypeParts[0].substring(1);
				String portTypeLocal = portTypeParts[1];
				return new QName(portTypeNs, portTypeLocal);
			}
		}
		return null;
	}

	/**
	 * Returns the first DOM Node with the given localName inside the given
	 * NodeList
	 *
	 * @param list a DOM NodeList
	 * @param localName a String representing a localName
	 * @return a DOM Node if the list contained a Node with the given localName,
	 *         else null
	 */
	private Node getNodeFromNodeList(NodeList list, String localName) {
		for (int index = 0; index < list.getLength(); index++) {
			Node candidate = list.item(index);
			if (candidate.getLocalName() == null) {
				continue;
			}
			if (candidate.getLocalName().equals(localName)) {
				return candidate;
			}
		}
		return null;
	}

}
