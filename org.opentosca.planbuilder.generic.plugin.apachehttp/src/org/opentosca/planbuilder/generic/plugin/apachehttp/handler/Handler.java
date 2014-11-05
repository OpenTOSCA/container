package org.opentosca.planbuilder.generic.plugin.apachehttp.handler;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the logic to add the needed BPEL Code to instantiate
 * an Apache HTTP Server on a Ubuntu OS
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {

	private ResourceHandler res;
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);


	public Handler() {
		try {
			this.res = new ResourceHandler();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize handler", e);
		}
	}

	/**
	 * Appends BPEL code unto the TemplateBuildPlan given by the TemplateContext
	 *
	 * @param templateContext an initialized TemplateContext belonging to a
	 *            ApacheWebServer NodeType
	 * @return true iff appending all logic was successful
	 */
	public boolean handle(TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}

		Handler.LOG.debug("Fetching properties of NodeTemplate " + templateContext.getNodeTemplate());
		NodeList properties = templateContext.getNodeTemplate().getProperties().getDOMElement().getChildNodes();

		Handler.LOG.debug("Searching for httpdport property");
		String httpdport = "";
		for (int i = 0; i < properties.getLength(); i++) {
			Handler.LOG.debug("Found property element with name " + properties.item(i).getNodeName());
			if (properties.item(i).getNodeName().equals("httpdport")) {
				httpdport = this.getValueOfElement(properties.item(i));
				Handler.LOG.debug("Found httpdport property with value " + httpdport);
			}
		}

		if (httpdport.equals("")) {
			Handler.LOG.warn("No httpdport is set in nodeTemplate " + templateContext.getNodeTemplate().getId());
			return false;
		}

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

		/* install httpd package */
		if (!this.appendInstallHttpdPackageLogic(templateContext, portType, partnerLinkName)) {
			Handler.LOG.warn("Couldn't append logic for installing httpd package");
			return false;
		}

		/* execute install.sh */
		String installSh = "echo \"sudo chkconfig httpd\"; sudo chkconfig httpd on";
		if (!this.appendScript(templateContext, portType, partnerLinkName, installSh)) {
			Handler.LOG.warn("Couldn't append logic for installing httpd");
			return false;
		}

		/* execute configure.sh */
		String configureSh = "";
		String configureShScript = this.res.getConfigureShAsString();

		Handler.LOG.debug("ConfigureScript that will be send:");
		Handler.LOG.debug(configureShScript);
		Handler.LOG.debug("With replacement:");
		Handler.LOG.debug(configureShScript.replace("\"", "\\\"").replace("$", "\\$"));

		// .replace("\'", "\\\'")

		configureSh += "touch ~/configure.sh; echo \"" + configureShScript.replace("\"", "\\\"").replace("$", "\\$") + "\" > ~/configure.sh; sudo httpdport=" + httpdport + " sh ~/configure.sh";
		if (!this.appendScript(templateContext, portType, partnerLinkName, configureSh)) {
			Handler.LOG.warn("Cloudn't append logic for configuring httpd");
			return false;
		}

		/* execute start.sh */
		String startSh = "";
		String startShScript = this.res.getStartShAsString();

		Handler.LOG.debug("StartScript that will be send:");
		Handler.LOG.debug(startShScript);
		Handler.LOG.debug("With replacement:");
		Handler.LOG.debug(startShScript.replace("\"", "\\\""));

		startSh += "touch ~/start.sh; echo \"" + startShScript.replace("\"", "\\\"").replace("$", "\\$") + "\" > ~/start.sh; sudo sh ~/start.sh";
		if (!this.appendScript(templateContext, portType, partnerLinkName, startSh)) {
			Handler.LOG.warn("Cloudn't append logic for starting httpd");
			return false;
		}

		return true;
	}

	/**
	 * Simple helper method to assemble DOM Node Values
	 *
	 * @param node a DOM Node
	 * @return a String containing all contents of the given DOM Node
	 */
	private String getValueOfElement(Node node) {
		String value = "";
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			if (node.getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
				value += node.getChildNodes().item(j).getNodeValue();
			}
		}
		return value;
	}

	/**
	 * Adds httpd package installation logic to the BPEL Plan. This method uses
	 * the EC2Linux Service PortType for installing packages unto the given
	 * Ubuntu server.
	 *
	 * @param templateContext an initialized TemplateContext
	 * @param portType an WSDL PortType as QName
	 * @param partnerLinkName the name of the partnerlink to use
	 * @return true iff appending all bpel logic was successful
	 */
	private boolean appendInstallHttpdPackageLogic(TemplatePlanContext templateContext, QName portType, String partnerLinkName) {

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
		String varNameServerIp = templateContext.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

		String packages = "httpd";

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

	/**
	 * Adds BPEL code for executing the given script on a Ubuntu OS
	 *
	 * @param context an initialized TemplateContext
	 * @param portType a EC2Linux WSDL PortType
	 * @param partnerLinkName the partnerLink that uses the given portType
	 * @param script the script to execute
	 * @return true iff append all code to the BPEL Plan was successful
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

}
