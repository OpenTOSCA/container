package org.opentosca.planbuilder.provphase.plugin.scriptoperation.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes
 * Scripts on remote machine. The class assumes that the script that must be
 * called are already uploaded to the appropiate path. For example by the
 * ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {

	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(Handler.class);

	private BPELFragments fragments;


	/**
	 * Contructor
	 */
	public Handler() {
		try {
			this.fragments = new BPELFragments();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize internal BPEL Fragment handler", e);
		}
	}

	/**
	 * Adds logic to the BuildPlan to call a Script on a remote machine
	 *
	 * @param context the TemplatePlanContext where the logical provisioning
	 *            operation is called
	 * @param operation the operation to call
	 * @param ia the ia that implements the operation
	 * @return true iff adding BPEL Fragment was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		String scriptRef = this.fetchScriptRefFromIA(ia);
		if (scriptRef == null) {
			return false;
		} else {
			scriptRef = "/home/ec2-user/" + context.getCSARFileName() + "/" + scriptRef;
		}
		Map<String, ParamWrapper> inputMappings = this.fetchInputMappingsFromIA(ia);
		Map<String, ParamWrapper> outputMappings = this.fetchOutputMappingsFromIA(ia);

		// Map<String, Variable> inputParamPropMappings =
		// context.getInternalExternalParameters(inputMappings.keySet());
		Map<String, Variable> inputParamPropMappings = this.getPropertyMappings(context, inputMappings);
		Map<String, Variable> outputParamPropMappings = context.getInternalExternalParameters(outputMappings.keySet());

		// assign external parameters to plan input message
		for (String inputToscaParam : inputParamPropMappings.keySet()) {
			if (inputParamPropMappings.get(inputToscaParam) == null) {
				context.addStringValueToPlanRequest(inputToscaParam);
			}
		}

		// register linux file upload webservice in plan
		QName portType = null;
		try {
			portType = context.registerPortType(this.fragments.getPortTypeFromLinuxUploadWSDL(), this.fragments.getEC2LinuxIAWsdl());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't read internal WSDL file", e);
			return false;
		}

		// register partnerlink
		String partnerLinkTypeName = "ec2linuxPLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "server", this.fragments.getPortTypeFromLinuxUploadWSDL());
		String partnerLinkName = "ec2linuxPL" + context.getIdForNames();
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, null, "server", true);

		String requestVariableName = "runScriptRequest" + context.getIdForNames();
		context.addVariable(requestVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptRequest"));
		String responseVariableName = "runScriptResponse" + context.getIdForNames();
		context.addVariable(responseVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptResponse"));

		// we search here for the correct address inside the given
		// infrastructure
		String varNameServerIp = "";
		if (context.getNodeTemplate() != null) {
			varNameServerIp = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		} else {
			// hard case: relationshipTemplate which is a connectsto
			AbstractRelationshipTemplate template = context.getRelationshipTemplate();
			if (context.getBaseType(template).toString().equals(new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ConnectsTo").toString())) {
				for (AbstractNodeTemplate nodeTemplate : context.getInfrastructureNodes(true)) {
					if (context.getVariableNameOfProperty(nodeTemplate.getId(), PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP) != null) {
						varNameServerIp = context.getVariableNameOfProperty(nodeTemplate.getId(), PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
					}
				}
			} else {
				// any other relationshipTemplate we handle accordingly
				varNameServerIp = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
			}
		}

		String script = this.fragments.generateScriptCall(scriptRef, inputMappings, inputParamPropMappings);

		// assign request
		Node assignNode = null;
		try {
			assignNode = this.fragments.generateAssignRequestMsgAsNode("assignRunScript_" + requestVariableName, portType.getPrefix(), requestVariableName, varNameServerIp, "input", script);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		}

		assignNode = context.importNode(assignNode);
		context.getProvisioningPhaseElement().appendChild(assignNode);

		Node invokeNode = null;
		try {
			invokeNode = this.fragments.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, "runScript", this.fragments.getPortTypeFromLinuxUploadWSDL(), requestVariableName, responseVariableName);
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		}

		invokeNode = context.importNode(invokeNode);
		context.getProvisioningPhaseElement().appendChild(invokeNode);

		return true;
	}

	/**
	 * Returns the first occurence of *.sh file, inside the given
	 * ImplementationArtifact
	 *
	 * @param ia an AbstractImplementationArtifact
	 * @return a String containing a relative file path to a *.sh file, if no
	 *         *.sh file inside the given IA is found null
	 */
	private String fetchScriptRefFromIA(AbstractImplementationArtifact ia) {
		List<AbstractArtifactReference> refs = ia.getArtifactRef().getArtifactReferences();
		for (AbstractArtifactReference ref : refs) {
			if (ref.getReference().endsWith(".sh")) {
				return ref.getReference();
			}
		}
		return null;
	}

	/**
	 * Returns input mappings from TOSCA Parameters to Script Parameters
	 *
	 * @param ia an AbstractImplementationArtifact containing a scriptMapping
	 * @return a Map from String to String, where the key is a TOSCA Parameter
	 *         and the value a Script variable
	 */
	private Map<String, ParamWrapper> fetchInputMappingsFromIA(AbstractImplementationArtifact ia) {
		Map<String, ParamWrapper> inputMappings = new HashMap<String, ParamWrapper>();
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if ((domElement.getLocalName() != null) && domElement.getLocalName().equals("scriptMapping")) {
				NodeList childe = domElement.getChildNodes();

				for (int index = 0; index < childe.getLength(); index++) {
					Node child = childe.item(index);
					if ((child.getLocalName() != null) && child.getLocalName().equals("inputMappings")) {
						NodeList childe2 = child.getChildNodes();

						for (int index2 = 0; index2 < childe2.getLength(); index2++) {
							Node child2 = childe2.item(index2);
							if ((child2.getLocalName() != null) && child2.getLocalName().equals("inputMapping")) {
								NamedNodeMap attr = child2.getAttributes();
								String toscaParam = attr.getNamedItem("toscaParam").getTextContent();
								String scriptParam = attr.getNamedItem("scriptParam").getTextContent();
								String infraPath = null;
								if (attr.getNamedItem("infraPath") != null) {
									infraPath = attr.getNamedItem("infraPath").getTextContent();
								}
								inputMappings.put(toscaParam, new ParamWrapper(scriptParam, infraPath));
							}
						}
					}
				}
			}
		}
		return inputMappings;
	}

	/**
	 * Returns output mappings from TOSCA Parameters to Script Parameters
	 *
	 * @param ia an AbstractImplementationArtifact containing a scriptMapping
	 * @return a Map from String to String, where the key is a TOSCA Parameter
	 *         and the value a Script variable
	 */
	private Map<String, ParamWrapper> fetchOutputMappingsFromIA(AbstractImplementationArtifact ia) {
		Map<String, ParamWrapper> outputMappings = new HashMap<String, ParamWrapper>();
		List<AbstractProperties> props = ia.getAdditionalElements();
		for (AbstractProperties prop : props) {
			Element domElement = prop.getDOMElement();
			if ((domElement.getLocalName() != null) && domElement.getLocalName().equals("scriptMapping")) {
				NodeList childe = domElement.getChildNodes();

				for (int index = 0; index < childe.getLength(); index++) {
					Node child = childe.item(index);
					if ((child.getLocalName() != null) && child.getLocalName().equals("outputMappings")) {
						NodeList childe2 = child.getChildNodes();

						for (int index2 = 0; index2 < childe2.getLength(); index2++) {
							Node child2 = childe2.item(index2);
							if ((child2.getLocalName() != null) && child2.getLocalName().equals("outputMapping")) {
								NamedNodeMap attr = child2.getAttributes();
								String toscaParam = attr.getNamedItem("toscaParam").getTextContent();
								String scriptParam = attr.getNamedItem("scriptParam").getTextContent();
								String infraPath = attr.getNamedItem("infraPath").getTextContent();
								outputMappings.put(toscaParam, new ParamWrapper(scriptParam, infraPath));
							}
						}
					}
				}
			}
		}
		return outputMappings;
	}

	private Map<String, Variable> getPropertyMappings(TemplatePlanContext context, Map<String, ParamWrapper> toscaParams) {
		Map<String, Variable> mappings = new HashMap<String, Variable>();
		for (String toscaParam : toscaParams.keySet()) {
			if (toscaParams.get(toscaParam).isInfraPathSet()) {
				if (toscaParams.get(toscaParam).lookOnSourcePath()) {
					mappings.put(toscaParam, context.getInternalPropertyVariable(toscaParam, true));
				} else {
					mappings.put(toscaParam, context.getInternalPropertyVariable(toscaParam, false));
				}
			} else {
				// no infrapath set look trough whole Topology
				mappings.put(toscaParam, context.getInternalPropertyVariable(toscaParam));
			}
		}
		return mappings;
	}


	public class ParamWrapper {

		private String scriptParam = null;
		private String infraPath = null;


		public ParamWrapper(String scriptParam, String infraPath) {
			this.infraPath = infraPath;
			this.scriptParam = scriptParam;
		}

		public String getScriptParamName() {
			return this.scriptParam;
		}

		public boolean isInfraPathSet() {
			if (this.infraPath != null) {
				return true;
			} else {
				return false;
			}
		}

		public boolean lookOnSourcePath() {
			if (this.infraPath.equals("Target")) {
				return false;
			} else if (this.infraPath.equals("Source")) {
				return true;
			} else {
				throw new IllegalStateException("InfraPath Attribute was not set with Values 'Target' or 'Source'");
			}
		}
	}
}
