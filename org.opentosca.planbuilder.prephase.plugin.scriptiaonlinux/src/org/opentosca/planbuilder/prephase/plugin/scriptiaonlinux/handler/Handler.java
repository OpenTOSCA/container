package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.handler;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains logic to upload files to a linux machine. Those files
 * must be available trough a openTOSCA Container
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Handler {
	
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	
	private ResourceHandler res;
	
	
	/**
	 * Constructor
	 */
	public Handler() {
		try {
			this.res = new ResourceHandler();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize internal ResourceHandler", e);
		}
	}
	
	/**
	 * Adds necessary BPEL logic trough the given context that can upload the
	 * given DA unto the given InfrastructureNode
	 * 
	 * @param context a TemplateContext
	 * @param da the DeploymentArtifact to deploy
	 * @param nodeTemplate the NodeTemplate which is used as InfrastructureNode
	 * @return true iff adding logic was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractDeploymentArtifact da, AbstractNodeTemplate nodeTemplate) {
		List<AbstractArtifactReference> refs = da.getArtifactRef().getArtifactReferences();
		return this.handle(context, refs, da.getName(), nodeTemplate);
	}
	
	/**
	 * Adds necessary BPEL logic through the given context that can upload the
	 * given IA unto the given InfrastructureNode
	 * 
	 * @param context a TemplateContext
	 * @param ia the ImplementationArtifact to deploy
	 * @param nodeTemplate the NodeTemplate which is used as InfrastructureNode
	 * @return true iff adding logic was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractImplementationArtifact ia, AbstractNodeTemplate nodeTemplate) {
		// fetch references
		List<AbstractArtifactReference> refs = ia.getArtifactRef().getArtifactReferences();
		return this.handle(context, refs, ia.getArtifactType().getLocalPart() + "_" + ia.getOperationName() + "_IA", nodeTemplate);
		
	}
	
	/**
	 * Adds necessary BPEL logic through the given Context, to deploy the given
	 * ArtifactReferences unto the specified InfrastructureNode
	 * 
	 * @param context a TemplateContext
	 * @param refs the ArtifactReferences to deploy
	 * @param artifactName the name of the artifact, where the references
	 *            originate from
	 * @param nodeTemplate a NodeTemplate which is a InfrastructureNode to
	 *            deploy the AbstractReferences on
	 * @return true iff adding the logic was successful
	 */
	private boolean handle(TemplatePlanContext context, List<AbstractArtifactReference> refs, String artifactName, AbstractNodeTemplate nodeTemplate) {
		// register linux file upload webservice in plan
		QName portType = null;
		try {
			portType = context.registerPortType(this.res.getPortTypeFromLinuxUploadWSDL(), this.res.getLinuxFileUploadWSDLFile());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't fetch internal WSDL file", e);
			return false;
		}
		
		// register partnerlink
		String partnerLinkTypeName = "ec2linuxPLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "server", portType);
		String partnerLinkName = "ec2linuxPL" + context.getIdForNames();
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, null, "server", true);
		
		// register request- and response-message variables
		// {http://ec2linux.aws.ia.opentosca.org}transferLocalFileRequest
		// {http://ec2linux.aws.ia.opentosca.org}transferLocalFileResponse
		String fileTransferRequestVarName = "local_linuxEc2FileTransferRequest" + context.getIdForNames();
		context.addVariable(fileTransferRequestVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "transferRemoteFileRequest", "ec2linuxIA"));
		String fileTransferResponseVarName = "local_linuxEc2FileTransferResponse" + context.getIdForNames();
		context.addVariable(fileTransferResponseVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "transferRemoteFileResponse", "ec2linuxIA"));
		
		context.addStringValueToPlanRequest("sshKey");
		context.addStringValueToPlanRequest("csarEntrypoint");
		// fetch serverip property variable name, planrequestmsgname and
		// assemble remotefilepath
		String varNameServerIp = context.getVariableNameOfProperty(nodeTemplate.getId(), "ServerIp");
		String varNamePlanRequestMsg = context.getPlanRequestMessageName();
		
		// TODO /home/ec2-user/ is a huge assumption
		String remoteFilePath = "/home/ec2-user/" + context.getCSARFileName() + "/" + refs.get(0).getReference();
		
		/* begin create folder request */
		
		// register linux file upload webservice in plan
		
		String runScriptRequestVarName = "runScriptRequest" + context.getIdForNames();
		context.addVariable(runScriptRequestVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptRequest"));
		String runScriptResponseVarName = "runScriptResponse" + context.getIdForNames();
		context.addVariable(runScriptResponseVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptResponse"));
		String script = "string('mkdir -p " + this.fileReferenceToFolder(remoteFilePath) + "')";
		
		// assign request
		Node assignNode = null;
		try {
			assignNode = this.res.generateAssignRequestMsgAsNode("assignRunScript_" + runScriptRequestVarName, portType.getPrefix(), runScriptRequestVarName, varNameServerIp, "input", script);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		}
		
		assignNode = context.importNode(assignNode);
		context.getPrePhaseElement().appendChild(assignNode);
		
		Node invokeNode = null;
		try {
			invokeNode = this.res.generateInvokeAsNode("invoke_" + runScriptRequestVarName, partnerLinkName, "runScript", this.res.getPortTypeFromLinuxUploadWSDL(), runScriptRequestVarName, runScriptResponseVarName);
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL Invoke element", e);
			return false;
		}
		
		invokeNode = context.importNode(invokeNode);
		context.getPrePhaseElement().appendChild(invokeNode);
		
		/* end create folder request */
		
		// context.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight",true);
		// register opentosca file reference type as variable
		// try {
		// context.registerType(new QName(
		// "http://opentosca.org/openTOSCAReferencesSchema",
		// "tReferences", "opentoscaref"), this.res
		// .getOpenToscaReferencesSchema());
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		
		// context.addVariable("local_refOfAbsPath",
		// BuildPlan.VariableType.TYPE,
		// new QName("http://opentosca.org/openTOSCAReferencesSchema",
		// "tReferences", "opentoscaref"));
		
		// add rest extension get to fetch absolute path on local machine
		// Node restExtensionNode;
		// try {
		// restExtensionNode = this.res.getRESTExtensionGETAsNode(context
		// .getCSARFileName(), "local_refOfAbsPath", refs.get(0)
		// .getReference());
		// } catch (SAXException e) {
		//
		// e.printStackTrace();
		// return false;
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		//
		// // import the node
		// restExtensionNode = context.importNode(restExtensionNode);
		// // append to prov sequence
		// context.getPrePhaseElement().appendChild(restExtensionNode);
		
		/* append assign to template */
		// adding sshKey to PlanInputMessage as this Plugin knows there is no
		// SSHKey Property inside the AmazonVM NodeType
		
		Handler.LOG.debug("Building container File Path");
		Handler.LOG.debug("Reference: " + refs.get(0).getReference());
		if ((refs.get(0).getIncludePatterns() != null) && !refs.get(0).getIncludePatterns().isEmpty()) {
			Handler.LOG.debug("IncludePattern: " + refs.get(0).getIncludePatterns().get(0));
		}
		
		String containerFilePath = this.res.getRemoteFilePathString(refs.get(0).getReference());
		
		Node assignRemoteFileNode;
		try {
			assignRemoteFileNode = this.res.getRemoteTransferFileAssignAsNode("assign_" + fileTransferRequestVarName, fileTransferRequestVarName, portType.getPrefix(), varNameServerIp, varNamePlanRequestMsg, containerFilePath, remoteFilePath);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL Assign element", e);
			return false;
		}
		
		// generate and add fragment
		// Node assignTransferFileNode;
		// try {
		// assignTransferFileNode = this.res.getTransferFileAssignAsNode(
		// "assign_" + artifactName + "_to_" + nodeTemplate.getId(),
		// "local_linuxEc2FileTransferRequest", "ec2linuxIA",
		// varNameServerIp, varNamePlanRequestMsg,
		// "local_refOfAbsPath", remoteFilePath);
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// return false;
		// } catch (SAXException e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		
		// assignTransferFileNode = context.importNode(assignTransferFileNode);
		// context.getPrePhaseElement().appendChild(assignTransferFileNode);
		
		assignRemoteFileNode = context.importNode(assignRemoteFileNode);
		context.getPrePhaseElement().appendChild(assignRemoteFileNode);
		
		/* generate invoke */
		Node invokeNode2;
		try {
			invokeNode2 = this.res.getTransferFileInvokeAsNode("invoke_" + artifactName + "_transferTo_" + nodeTemplate.getId() + context.getIdForNames(), partnerLinkName, "ec2linuxport", fileTransferRequestVarName, fileTransferResponseVarName, "transferRemoteFile");
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL invoke element", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL invoke element", e);
			return false;
		}
		
		invokeNode2 = context.importNode(invokeNode2);
		context.getPrePhaseElement().appendChild(invokeNode2);
		
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
		
		int lastIndexSlash = ref.lastIndexOf("/");
		int lastIndexDot = ref.lastIndexOf(".");
		if (lastIndexSlash < lastIndexDot) {
			ref = ref.substring(0, lastIndexSlash);
		}
		Handler.LOG.debug("Returning ref: " + ref);
		return ref;
	}
	
}
