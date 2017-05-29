package org.opentosca.planbuilder.provphase.plugin.wsdloperation.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.TOSCAPlan;
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
 * This class contains the necessary logic for the WSDL Operation Plugin to
 * call, asynchronous WebService Operations
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class AsyncHandler {

	private final static Logger LOG = LoggerFactory.getLogger(AsyncHandler.class);

	private BPELFragments fragments;


	/**
	 * Contructor
	 */
	public AsyncHandler() {
		try {
			this.fragments = new BPELFragments();
		} catch (ParserConfigurationException e) {
			AsyncHandler.LOG.error("Coulnd't initialize internal BPEL Fragment handler", e);
		}
	}

	/**
	 * Adds needed logic to call an asynchronous WebService to the BuildPlan, in
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
		Element wsdlMappingElement = this.fetchWsdlMappingElement(ia);

		// fetch portType and callbackPortType
		QName portType = this.fetchFirstPortTypeFromWsdlMapping(wsdlMappingElement);
		QName callbackPortType = this.fetchSecondPortTypeFromWsdlMapping(wsdlMappingElement);

		String wsdlOperationName = this.fetchFirstOperationNameFromWsdlMapping(wsdlMappingElement);
		String wsdlCallbackOperationName = this.fetchSecondOperationNameFromWsdlMapping(wsdlMappingElement);

		QName InputMessageId = this.fetchInputMessageIdFromWsdlMapping(ia);
		String inputPartName = this.fetchInputMessagePartNameFromWsdlMapping(ia);

		QName OutputMessageId = this.fetchOutputMessageIdFromWsdlMapping(ia);
		String outputPartName = this.fetchOutputMessagePartNameFromWsdlMapping(ia);

		Map<String, String> inputMappingsToscaWsdl = this.fetchInputParamMappingsFromWsdlMapping(ia);
		Map<String, String> outputMappingsToscaWsdl = this.fetchOutputParamMappingsFromWsdlMapping(ia);

		RandomCorrelatorWrapper correlation = this.fetchCorrelation(wsdlMappingElement);

		// we filter out the mappings which didn't have a toscaParam attribute
		Set<String> filteredInputMappingsKeys = new HashSet<String>();
		for (String key : inputMappingsToscaWsdl.keySet()) {
			if (!key.contains("_DUMMY_KEY_")) {
				filteredInputMappingsKeys.add(key);
			}
		}

		Set<String> filteredOutputMappingsKeys = new HashSet<String>();
		for (String key : outputMappingsToscaWsdl.keySet()) {
			if (!key.contains("_DUMMY_KEY_")) {
				filteredOutputMappingsKeys.add(key);
			}
		}

		/* check for external parameters */
		// we check here if there are any properties on the infrastructure path,
		// that match the toscaParameters of the operation
		Map<String, Variable> inputParamPropMappings = context.getInternalExternalParameters(filteredInputMappingsKeys);
		Map<String, Variable> outputParamPropMappings = context.getInternalExternalParameters(filteredOutputMappingsKeys);

		// assign external parameters to plan input message
		for (String inputToscaParam : inputParamPropMappings.keySet()) {
			if (inputParamPropMappings.get(inputToscaParam) == null) {
				// doesn't have a match inside the topology/infrastructure path
				// add it to plan input message
				context.addStringValueToPlanRequest(inputToscaParam);
			}
		}

		// TODO assing external parameters to outputmessage

		// register wsdl, porttype, partnerlinktype, partnerlink
		portType = context.registerPortType(portType, wsdlRef);
		callbackPortType = context.registerPortType(callbackPortType, wsdlRef);
		InputMessageId = context.importQName(InputMessageId);
		OutputMessageId = context.importQName(OutputMessageId);

		String partnerLinkTypeName = portType.getLocalPart() + "PLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "Requester", callbackPortType, "Requestee", portType);
		String partnerLinkName = portType.getLocalPart() + "PL" + context.getIdForNames();

		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

		// register request and response message
		String requestVariableName = portType.getLocalPart() + InputMessageId.getLocalPart() + "Request" + context.getIdForNames();
		context.addVariable(requestVariableName, TOSCAPlan.VariableType.MESSAGE, InputMessageId);
		String responseVariableName = portType.getLocalPart() + InputMessageId.getLocalPart() + "Response" + context.getIdForNames();
		context.addVariable(responseVariableName, TOSCAPlan.VariableType.MESSAGE, OutputMessageId);

		// create correlation set
		/*
		 * String createEc2Property = "createEC2Property" +
		 * context.getIdForNames(); context.addProperty(createEc2Property, new
		 * QName("http://www.w3.org/2001/XMLSchema", "string", "xsd")); // for
		 * request QName createEc2ReqQName = context.importQName(new
		 * QName("http://ec2vm.aws.ia.opentosca.org",
		 * "createEC2InstanceRequest"));
		 * context.addPropertyAlias(createEc2Property, createEc2ReqQName,
		 * "parameters", "/" + createEc2ReqQName.getPrefix() +
		 * ":CorrelationId"); // for response QName createEc2ResQName =
		 * context.importQName(new QName("http://ec2vm.aws.ia.opentosca.org",
		 * "createEC2InstanceResponse"));
		 * context.addPropertyAlias(createEc2Property, createEc2ResQName,
		 * "parameters", "/" + createEc2ResQName.getPrefix() +
		 * ":CorrelationId");
		 */
		String correlationSetName = null;
		if (correlation != null) {
			// setup correlation property and aliases for request and response
			String correlationPropertyName = portType.getLocalPart() + "Property" + context.getIdForNames();
			context.addProperty(correlationPropertyName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
			context.addPropertyAlias(correlationPropertyName, InputMessageId, inputPartName, "/" + InputMessageId.getPrefix() + ":" + correlation.outCorrelationElement);
			context.addPropertyAlias(correlationPropertyName, OutputMessageId, outputPartName, "/" + OutputMessageId.getPrefix() + ":" + correlation.inCorrelationElement);
			// register correlationsets
			correlationSetName = portType.getLocalPart() + "CorrelationSet" + context.getIdForNames();
			context.addCorrelationSet(correlationSetName, correlationPropertyName);
		}

		// add assign for request
		try {
			Node requestAssignNode = this.fragments.getGenericAssignAsNode(InputMessageId, requestVariableName, inputPartName, inputMappingsToscaWsdl, inputParamPropMappings, "assign_" + requestVariableName, context.getPlanRequestMessageName(), "payload");

			requestAssignNode = context.importNode(requestAssignNode);
			// check whether some correlation must be done
			if (correlation != null) {
				// add a copy from a literal, which contains some random digits
				// to correlate
				Node correlationCopy = this.fragments.getGenericRandomLiteralCopyAsNode(InputMessageId, requestVariableName, inputPartName, correlation.outCorrelationElement);
				correlationCopy = context.importNode(correlationCopy);
				requestAssignNode.appendChild(correlationCopy);
			}

			Node addressingCopyInit = this.fragments.generateAddressingInitAsNode(requestVariableName);
			addressingCopyInit = context.importNode(addressingCopyInit);
			requestAssignNode.appendChild(addressingCopyInit);

			Node addressingCopyNode = this.fragments.generateAddressingCopyAsNode(partnerLinkName, requestVariableName);
			addressingCopyNode = context.importNode(addressingCopyNode);
			requestAssignNode.appendChild(addressingCopyNode);

			AsyncHandler.LOG.debug("Trying to append Node: " + requestAssignNode.toString());

			context.getProvisioningPhaseElement().appendChild(requestAssignNode);
		} catch (SAXException e) {
			AsyncHandler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (IOException e) {
			AsyncHandler.LOG.error("Error reading/writing File", e);
			return false;
		}

		// add invoke
		try {
			Node invokeNode = this.fragments.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, wsdlOperationName, portType, requestVariableName);
			AsyncHandler.LOG.debug("Trying to ImportNode: " + invokeNode.toString());
			invokeNode = context.importNode(invokeNode);

			if ((correlation != null) & (correlationSetName != null)) {
				Node correlationSetsNode = this.fragments.generateCorrelationSetsAsNode(correlationSetName, true);
				correlationSetsNode = context.importNode(correlationSetsNode);
				invokeNode.appendChild(correlationSetsNode);
			}

			context.getProvisioningPhaseElement().appendChild(invokeNode);
		} catch (SAXException e) {
			AsyncHandler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (IOException e) {
			AsyncHandler.LOG.error("Error reading/writing File", e);
			return false;
		}

		// add receive

		try {
			Node receiveNode = this.fragments.generateReceiveAsNode("receive_" + responseVariableName, partnerLinkName, wsdlCallbackOperationName, callbackPortType, responseVariableName);
			receiveNode = context.importNode(receiveNode);

			if ((correlation != null) & (correlationSetName != null)) {
				Node correlationSetsNode = this.fragments.generateCorrelationSetsAsNode(correlationSetName, false);
				correlationSetsNode = context.importNode(correlationSetsNode);
				receiveNode.appendChild(correlationSetsNode);
			}
			context.getProvisioningPhaseElement().appendChild(receiveNode);
		} catch (SAXException e1) {
			AsyncHandler.LOG.error("Error reading/writing XML File", e1);
			return false;
		} catch (IOException e1) {
			AsyncHandler.LOG.error("Error reading/writing File", e1);
			return false;
		}

		// add assign for response
		try {
			Node responseAssignNode = this.fragments.generateResponseAssignAsNode(responseVariableName, outputPartName, outputMappingsToscaWsdl, outputParamPropMappings, "assign_" + responseVariableName, OutputMessageId);
			AsyncHandler.LOG.debug("Trying to ImportNode: " + responseAssignNode.toString());
			responseAssignNode = context.importNode(responseAssignNode);
			context.getProvisioningPhaseElement().appendChild(responseAssignNode);
		} catch (SAXException e) {
			AsyncHandler.LOG.error("Error reading/writing XML File", e);
			return false;
		} catch (IOException e) {
			AsyncHandler.LOG.error("Error reading/writing File", e);
			return false;
		}

		try {
			Node waitNode = this.fragments.generateWaitAsNode(String.valueOf(System.currentTimeMillis()), 0, (int) (System.currentTimeMillis() % 60));
			waitNode = context.importNode(waitNode);
			context.getProvisioningPhaseElement().appendChild(waitNode);
		} catch (SAXException e) {
			AsyncHandler.LOG.warn("Couldn't generate BPEL wait element", e);
		} catch (IOException e) {
			AsyncHandler.LOG.warn("Couldn't generate BPEL wait element", e);
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
					if ((toscaParamNode == null) & (wsdlParamNode == null)) {
						// error, but continue
						continue;
					}
					if (toscaParamNode == null) {
						// this means that there is no mapping between this
						// wsdlElement and a toscaParameter
						paramMappings.put("_DUMMY_KEY_" + index, wsdlParamNode.getNodeValue());
					} else {
						paramMappings.put(toscaParamNode.getNodeValue(), wsdlParamNode.getNodeValue());
					}
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
					if ((toscaParamNode == null) & (wsdlParamNode == null)) {
						// error, but continue
						continue;
					}
					if (toscaParamNode == null) {
						// this means that there is no mapping between this
						// wsdlElement and a toscaParameter
						paramMappings.put("_DUMMY_KEY_" + index, wsdlParamNode.getNodeValue());
					} else {
						paramMappings.put(toscaParamNode.getNodeValue(), wsdlParamNode.getNodeValue());
					}
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
	 * <p>
	 * This is a wrapper class, for defined random correlations inside a
	 * wsdlMapping. Random correlation means the value for the particular
	 * elements which will be used for correlation can be set by the handler
	 * </p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 *
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 *
	 */
	private class RandomCorrelatorWrapper {

		protected String inCorrelationElement;
		protected String outCorrelationElement;


		/**
		 * Contructor
		 *
		 * @param inCorrelationElementName a xml element localName of an
		 *            response message
		 * @param outCorrelationElementName a xml element localName of an
		 *            request message
		 */
		private RandomCorrelatorWrapper(String inCorrelationElementName, String outCorrelationElementName) {
			this.inCorrelationElement = inCorrelationElementName;
			this.outCorrelationElement = outCorrelationElementName;
		}
	}


	/**
	 * Generates a RandomCorrelationWrapper which can be used to correlate a
	 * request and response message
	 *
	 * @param wsdlMappingElement a DOM Element which contains a wsdlMapping
	 * @return a RandomCorrelatorWrapper which contains two localNames of
	 *         elements inside the request and response message, if no
	 *         correlation defined null
	 */
	private RandomCorrelatorWrapper fetchCorrelation(Element wsdlMappingElement) {
		NodeList nodeList = wsdlMappingElement.getElementsByTagNameNS("http://example.com/ba", "correlation");
		if (nodeList.getLength() != 1) {
			return null;
		} else if (nodeList.getLength() == 1) {
			String type = nodeList.item(0).getAttributes().getNamedItem("type").getNodeValue();
			if (type.equals("random")) {
				String inElementName = nodeList.item(0).getAttributes().getNamedItem("in").getNodeValue();
				String outElementName = nodeList.item(0).getAttributes().getNamedItem("out").getNodeValue();
				return new RandomCorrelatorWrapper(inElementName, outElementName);
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
	 * @return a QName representing the XSD type of a WSDL output message
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
	 * Returns the operationName for the async request
	 *
	 * @param wsdlMappingElement a DOM element containing a wsdlMapping
	 * @return a String containing an WSDL operation name, if not specified null
	 */
	private String fetchFirstOperationNameFromWsdlMapping(Element wsdlMappingElement) {
		Node child = wsdlMappingElement.getFirstChild();
		while (child != null) {
			if (child.getLocalName().equals("wsdlOperation") && child.getNamespaceURI().equals("http://example.com/ba")) {
				String operationString = child.getTextContent();
				return (operationString.equals("") || operationString.isEmpty()) ? null : operationString;
			} else {
				child = child.getNextSibling();
			}
		}
		return null;
	}

	/**
	 * Returns the callback operation of a wsdlMapping
	 *
	 * @param wsdlMappingElement a DOM Element containing a wsdlMapping
	 * @return a String containing a WSDL Operation name, if no
	 *         wsdlCallbackOperation element is found null
	 */
	private String fetchSecondOperationNameFromWsdlMapping(Element wsdlMappingElement) {
		Node child = wsdlMappingElement.getFirstChild();
		while (child != null) {
			if (child.getLocalName().equals("wsdlCallbackOperation") && child.getNamespaceURI().equals("http://example.com/ba")) {
				String operationString = child.getTextContent();
				return (operationString.equals("") || operationString.isEmpty()) ? null : operationString;
			} else {
				child = child.getNextSibling();
			}
		}
		return null;
	}

	/**
	 * Returns the wsdlMapping of an ImplementationArtifact
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a DOM Element containing a wsdlMapping, if no such element was
	 *         found null
	 */
	private Element fetchWsdlMappingElement(AbstractImplementationArtifact ia) {
		for (AbstractProperties prop : ia.getAdditionalElements()) {
			Element domElement = prop.getDOMElement();
			if (domElement.getLocalName().equals("wsdlMapping") && domElement.getNamespaceURI().equals("http://example.com/ba")) {
				return domElement;
			}
		}
		return null;
	}

	/**
	 * Returns the portType of the WSDL Service
	 *
	 * @param wsdlMappingElement a DOM Element containing a wsdlMapping
	 * @return a QName denoting a WSDL PortType, if no portType is found inside
	 *         wsdlMapping null
	 */
	private QName fetchFirstPortTypeFromWsdlMapping(Element wsdlMappingElement) {
		Node child = wsdlMappingElement.getFirstChild();
		while (child != null) {
			if (child.getLocalName().equals("portType") && child.getNamespaceURI().equals("http://example.com/ba")) {
				// found first portType
				String portTypeString = child.getTextContent();
				String[] portTypeParts = portTypeString.split("}");
				if (portTypeParts.length != 2) {
					// portType declaration not in form {ns}local
					return null;
				}
				String portTypeNs = portTypeParts[0].substring(1);
				String portTypeLocal = portTypeParts[1];
				return new QName(portTypeNs, portTypeLocal);
			} else {
				child = child.getNextSibling();
			}
		}
		return null;
	}

	/**
	 * Returns the callback portType inside the wsdlMapping
	 *
	 * @param wsdlMappingElement a DOM Element that should be a wsdlMapping
	 * @return a QName denoting a WSDL portType, if no callbackPortTpe is
	 *         specified in the wsdlMapping null
	 */
	private QName fetchSecondPortTypeFromWsdlMapping(Element wsdlMappingElement) {
		Node child = wsdlMappingElement.getFirstChild();
		while (child != null) {
			if (child.getLocalName().equals("callbackPortType") && child.getNamespaceURI().equals("http://example.com/ba")) {
				// found first portType
				String portTypeString = child.getTextContent();
				String[] portTypeParts = portTypeString.split("}");
				if (portTypeParts.length != 2) {
					// portType declaration not in form {ns}local
					return null;
				}
				String portTypeNs = portTypeParts[0].substring(1);
				String portTypeLocal = portTypeParts[1];
				return new QName(portTypeNs, portTypeLocal);
			} else {
				child = child.getNextSibling();
			}

		}
		return null;
	}

	/**
	 * Returns a Node with the specified localName
	 *
	 * @param list a List of DOM Nodes
	 * @param localName a localName
	 * @return a Node with the specified localName, else null
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
