package org.opentosca.planbuilder.generic.plugin.phpapp.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the logic to install an PHP Application on an Ubuntu
 * OS with preinstalled Apache HTTP Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {

	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	private ResourceHandler res;
	private QName zipArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");


	public Handler() {
		try {
			this.res = new ResourceHandler();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize resource handler");
		}
	}

	/**
	 * Appends BPEL Logic to the given TemplateContext to install an PHP
	 * Application on an Ubuntu OS with pre-installed Apache HTTP Server
	 *
	 * @param templateContext an initialized TemplateContext belonging to an PHP
	 *            Application NodeTemplate
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handle(TemplatePlanContext templateContext) {
		// register linux service
		// register ec2linux service so that we can install packages and run
		// scripts
		// register linux file upload webservice in plan
		QName portType = null;
		try {
			portType = templateContext.registerPortType(this.res.getPortTypeFromLinuxUploadWSDL(), this.res.getLinuxFileUploadWSDLFile());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't fetch internal WSDL file", e);
			return false;
		}

		// register partnerlink
		String partnerLinkTypeName = "ec2linuxPLT" + templateContext.getIdForNames();
		templateContext.addPartnerLinkType(partnerLinkTypeName, "server", portType);
		String partnerLinkName = "ec2linuxPL" + templateContext.getIdForNames();
		templateContext.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, null, "server", true);

		// add file upload of DA
		List<AbstractArtifactReference> refs = this.getArtifactRefs(templateContext);
		if (refs.isEmpty()) {
			Handler.LOG.warn("Can't add upload file logic, as no valid .zip file is present");
			return false;
		}

		for (AbstractArtifactReference ref : refs) {
			if (!this.assignUploadFileLogic(templateContext, portType, partnerLinkName, ref)) {
				Handler.LOG.warn("Couldn't add upload file logic");
				return false;
			}

			// unzip $SCRIPT_PATH/../../DA/hello.zip -d /var/www/html/

			String scriptString = "sudo unzip ~/" + templateContext.getCSARFileName() + "/" + ref.getReference() + " -d /var/www/html/";
			if (!this.appendScript(templateContext, portType, partnerLinkName, scriptString)) {
				Handler.LOG.warn("Couldn't add unpack script logic");
				return false;
			}

		}
		return true;
	}

	/**
	 * Adds logic to execute the given script on an Ubuntu OS
	 *
	 * @param context the TemplateContext the logic should be added to
	 * @param portType the portType of an EC2Linux PortType
	 * @param partnerLinkName the partnerLink using the given portType
	 * @param script the script to execute on the Ubuntu machine
	 * @return true iff appending all BPEL code was successful
	 */
	private boolean appendScript(TemplatePlanContext context, QName portType, String partnerLinkName, String script) {
		String requestVariableName = "runScriptRequest" + context.getIdForNames();
		context.addVariable(requestVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptRequest"));
		String responseVariableName = "runScriptResponse" + context.getIdForNames();
		context.addVariable(responseVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptResponse"));

		// we search here for the correct address inside the given
		// infrastructure
		String varNameServerIp = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

		// assign request
		Node assignNode = null;
		try {
			assignNode = this.res.generateAssignRequestMsgAsNode("assignRunScript_" + requestVariableName, portType.getPrefix(), requestVariableName, varNameServerIp, "input", script);
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
			invokeNode = this.res.generateInvokeAsNode("invoke_" + requestVariableName, partnerLinkName, "runScript", portType, requestVariableName, responseVariableName);
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
	 * Adds BPEL Code to upload a single ArtifactReference
	 *
	 * @param context the templateContext the code should be added to
	 * @param portType the portType of an EC2Linux Service
	 * @param partnerLinkName the name of the partnerLink which uses the
	 *            portType
	 * @param ref an AbstractArtifactReference containing an relative path to a
	 *            file contained withing the CSAR
	 * @return true iff appending all BPEL code was successful
	 */
	private boolean assignUploadFileLogic(TemplatePlanContext context, QName portType, String partnerLinkName, AbstractArtifactReference ref) {
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

		String varNameServerIp = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		String varNamePlanRequestMsg = context.getPlanRequestMessageName();

		// TODO /home/ec2-user/ or ~ is a huge assumption
		String remoteFilePath = "~/" + context.getCSARFileName() + "/" + ref.getReference();

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

		Handler.LOG.debug("Building container File Path");
		Handler.LOG.debug("Reference: " + ref.getReference());
		if ((ref.getIncludePatterns() != null) && !ref.getIncludePatterns().isEmpty()) {
			Handler.LOG.debug("IncludePattern: " + ref.getIncludePatterns().get(0));
		}

		String containerFilePath = this.res.getRemoteFilePathString(ref.getReference());

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

		assignRemoteFileNode = context.importNode(assignRemoteFileNode);
		context.getPrePhaseElement().appendChild(assignRemoteFileNode);

		/* generate invoke */
		Node invokeNode2;
		try {
			invokeNode2 = this.res.getTransferFileInvokeAsNode("invoke_" + "_transferTo_" + context.getNodeTemplate().getId() + context.getIdForNames(), partnerLinkName, "ec2linuxport", fileTransferRequestVarName, fileTransferResponseVarName, "transferRemoteFile");
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
	 * Returns a List of ArtifactReferences which point to a ZIP file inside the
	 * the NodeTemplate of the TemplateContext
	 *
	 * @param context a TemplateContext containing a NodeTemplate with
	 *            DeploymentArtifacts
	 * @return a List of AbstractArtifactReference
	 */
	private List<AbstractArtifactReference> getArtifactRefs(TemplatePlanContext context) {
		List<AbstractArtifactReference> result = new ArrayList<AbstractArtifactReference>();
		for (AbstractDeploymentArtifact artifact : context.getNodeTemplate().getDeploymentArtifacts()) {
			if (artifact.getArtifactType().toString().equals(this.zipArtifactType.toString())) {
				// check reference
				for (AbstractArtifactReference ref : artifact.getArtifactRef().getArtifactReferences()) {
					if (ref.getReference().endsWith(".zip")) {
						result.add(ref);
					}
				}

			}
		}
		return result;
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
