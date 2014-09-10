package org.opentosca.planbuilder.generic.plugin.phponapache.handler;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Handler {
	
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	private ResourceHandler res;
	
	
	public Handler() {
		try {
			this.res = new ResourceHandler();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize handler", e);
		}
	}
	
	public boolean handle(TemplatePlanContext templateContext) {
		
		// register ec2linux service, portType and partnerLink
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
		
		// "php php-cli php-common php-mysql php-xml mysql-server"
		if (!this.appendInstallPackageLogic("php", templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing php");
			return false;
		}
		
		if (!this.appendInstallPackageLogic("php-cli", templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing php");
			return false;
		}
		if (!this.appendInstallPackageLogic("php-common", templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing php");
			return false;
		}
		if (!this.appendInstallPackageLogic("php-mysql", templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing php");
			return false;
		}
		if (!this.appendInstallPackageLogic("php-xml", templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing php");
			return false;
		}
		
		// if (!this.appendInstallPackageLogic("mysql-server", templateContext,
		// portType, partnerLinkName)) {
		// Handler.LOG.warn("Couldn't append logic for installing php");
		// return false;
		// }
		
		// append start.sh logic (restart httpd)
		String startSh = "";
		String startShScript = this.res.getPhpStartShAsString();
		
		startSh += "touch ~/php_start.sh; echo \"" + startShScript.replace("\"", "\\\"").replace("$", "\\$").replace("`", "\\`") + "\" > ~/php_start.sh; sudo sh ~/php_start.sh";
		if (!this.appendScript(templateContext, portType, partnerLinkName, startSh)) {
			Handler.LOG.warn("Couldn't append logic for starting php");
			return false;
		}
		
		return true;
	}
	
	private boolean appendInstallPackageLogic(String packageName, TemplatePlanContext templateContext, QName portType, String partnerLinkName) {
		
		// register request- and response-message variables
		// {http://ec2linux.aws.ia.opentosca.org}installPackageRequest
		// {http://ec2linux.aws.ia.opentosca.org}installPackageResponse
		String requestVarName = "local_linuxEc2InstallPackageRequest" + templateContext.getIdForNames();
		templateContext.addVariable(requestVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "installPackageRequest", "ec2linuxIA"));
		String responseVarName = "local_linuxEc2InstallPackageResponse" + templateContext.getIdForNames();
		templateContext.addVariable(responseVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "installPackageResponse", "ec2linuxIA"));
		
		templateContext.addStringValueToPlanRequest("sshKey");
		// fetch serverip property variable name, planrequestmsgname and
		// assemble remotefilepath
		String varNameServerIp = templateContext.getVariableNameOfInfraNodeProperty("ServerIp");
		
		String packages = packageName;
		
		// generate assign
		Node assignNode = null;
		try {
			assignNode = this.res.generateRequestAssignAsNode("assign_local_linuxEc2InstallPackageRequest" + templateContext.getIdForNames(), requestVarName, varNameServerIp, portType.getPrefix(), packages, "input", "payload", "tns");
		} catch (IOException e) {
			Handler.LOG.error("Reading File from FragmentHandler failed", e);
			return false;
		} catch (SAXException e) {
			Handler.LOG.error("Reading Fragment from FragmentHandler failed", e);
			return false;
		}
		
		assignNode = templateContext.importNode(assignNode);
		templateContext.getPrePhaseElement().appendChild(assignNode);
		
		Node invokeNode = null;
		try {
			invokeNode = this.res.getPackageInstallInvokeAsNode("invoke_PackageInstall_" + packages.replace(" ", "_") + templateContext.getIdForNames(), partnerLinkName, portType.getPrefix(), requestVarName, responseVarName);
		} catch (SAXException e) {
			Handler.LOG.error("Reading Fragment from FragmentHandler failed", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Reading File from FragmentHandler failed", e);
			return false;
		}
		invokeNode = templateContext.importNode(invokeNode);
		templateContext.getPrePhaseElement().appendChild(invokeNode);
		
		return true;
	}
	
	private boolean appendScript(TemplatePlanContext context, QName portType, String partnerLinkName, String script) {
		String requestVariableName = "runScriptRequest" + context.getIdForNames();
		context.addVariable(requestVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptRequest"));
		String responseVariableName = "runScriptResponse" + context.getIdForNames();
		context.addVariable(responseVariableName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "runScriptResponse"));
		
		// we search here for the correct address inside the given
		// infrastructure
		String varNameServerIp = context.getVariableNameOfInfraNodeProperty("ServerIp");
		
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
	
}
