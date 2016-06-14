package org.opentosca.planbuilder.type.plugin.apachewebserver.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class contains all logic needed to add BPEL Code which uses the
 * OpenTOSCA Container Invoker service to install an Apache HTTP Server on an
 * Ubuntu OS
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {
	
	private Plugin invokerPlugin = new Plugin();
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	
	
	/**
	 * Adds the needed BPEL Code with the given TemplateContext to install an
	 * Apache HTTP Server on an Ubuntu OS which is along the Infrastructure Path
	 * of the given Apache HTTP Server NodeTemplate
	 *
	 * @param templateContext an initialized TemplateContext belong to an Apache
	 *            HTTP Server NodeTemplate
	 * @return true iff appending all the BPEL Code was successful
	 */
	public boolean handle(TemplatePlanContext templateContext) {
		/*
		 * fetch relevant variables/properties
		 */
		// fetch httpdport property, so we assure that we can execute the
		// scripts later
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
			if (properties.item(i).getNodeName().contains("httpdport")) {
				httpdport = this.getValueOfElement(properties.item(i));
				Handler.LOG.debug("Found httpdport property with value " + httpdport);
			}
		}
		
		if (httpdport.equals("")) {
			Handler.LOG.warn("No httpdport is set in nodeTemplate " + templateContext.getNodeTemplate().getId());
			return false;
		}
		
		// fetch server ip of the vm this apache http will be installed on
		
		Variable serverIpPropWrapper = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}
		
		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("No Infrastructure Node available with ServerIp property");
			return false;
		}
		
		// find sshUser and sshKey
		Variable sshUserVariable = templateContext.getPropertyVariable("SSHUser");
		if (sshUserVariable == null) {
			sshUserVariable = templateContext.getPropertyVariable("SSHUser", true);
			if (sshUserVariable == null) {
				sshUserVariable = templateContext.getPropertyVariable("SSHUser", false);
			}
		}
		
		// if the variable is null now -> the property isn't set properly
		if (sshUserVariable == null) {
			return false;
		} else {
			if (Utils.isVariableValueEmpty(sshUserVariable, templateContext)) {
				// the property isn't set in the topology template -> we set it
				// null here so it will be handled as an external parameter
				sshUserVariable = null;
			}
		}
		
		Variable sshKeyVariable = templateContext.getPropertyVariable("SSHPrivateKey");
		if (sshKeyVariable == null) {
			sshKeyVariable = templateContext.getPropertyVariable("SSHPrivateKey", true);
			if (sshKeyVariable == null) {
				sshKeyVariable = templateContext.getPropertyVariable("SSHPrivateKey", false);
			}
		}
		
		// if variable null now -> the property isn't set according to schema
		if (sshKeyVariable == null) {
			return false;
		} else {
			if (Utils.isVariableValueEmpty(sshKeyVariable, templateContext)) {
				// see sshUserVariable..
				sshKeyVariable = null;
			}
		}
		// add sshUser and sshKey to the input message of the build plan, if
		// needed
		if (sshUserVariable == null) {
			LOG.debug("Adding sshUser field to plan input");
			templateContext.addStringValueToPlanRequest("sshUser");
			
		}
		
		if (sshKeyVariable == null) {
			LOG.debug("Adding sshKey field to plan input");
			templateContext.addStringValueToPlanRequest("sshKey");
		}
		
		// find the ubuntu node and its nodeTemplateId
		String templateId = "";
		
		for (AbstractNodeTemplate nodeTemplate : templateContext.getNodeTemplates()) {
			if (org.opentosca.model.tosca.conventions.Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
				templateId = nodeTemplate.getId();
			}
		}
		
		if (templateId.equals("")) {
			Handler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
			return false;
		}
		
		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		// generate string variable for "httpd" package. This way it's easier to
		// program (no assigns by hand, etc.)
		String httpdPackageVarName = "httpdPackageVar" + templateContext.getIdForNames();
		// use the legacy engine
		Variable httpdPackageVar = templateContext.createGlobalStringVariable(httpdPackageVarName, "apache2 apache2-mpm-prefork");
		
		/*
		 * Install httpd package
		 */
		// used for the invokerPlugin. This map contains mappings from internal
		// variables or data which must be fetched form the input message (value
		// of map == null)
		Map<String, Variable> installPackageRequestInputParams = new HashMap<String, Variable>();
		
		/*
		 * methodName: installPackage params: "hostname", "sshKey", "sshUser",
		 * "packageNames"
		 */
		installPackageRequestInputParams.put("hostname", serverIpPropWrapper);
		installPackageRequestInputParams.put("sshKey", sshKeyVariable);
		installPackageRequestInputParams.put("sshUser", sshUserVariable);
		installPackageRequestInputParams.put("packageNames", httpdPackageVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "installPackage", "InterfaceUbuntu", "planCallbackAddress_invoker", installPackageRequestInputParams, new HashMap<String, Variable>(), true);
		
		/*
		 * Execute install, configure and start scripts
		 */
		
		/*
		 * methodname: runScript params: "hostname","script","sshKey","sshUser"
		 */
		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
		
		runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
		runScriptRequestInputParams.put("sshKey", sshKeyVariable);
		runScriptRequestInputParams.put("sshUser", sshUserVariable);
		
		// create install bash script and put into string var
		String installSh = "echo \"sudo chkconfig httpd\"; sudo chkconfig httpd on";
		String installShVarName = "installShScript" + templateContext.getIdForNames();
		Variable installShVar = templateContext.createGlobalStringVariable(installShVarName, installSh);
		
		runScriptRequestInputParams.put("script", installShVar);
		
		// execute install script (formerly it was the install.sh script)
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		
		/* execute configure.sh */
		String configureShScriptVarName = "configureShScript" + templateContext.getIdForNames();
		String configureShScript = this.getConfigureShAsString();
		// adds some final touches. This creates a bash file with the script
		// contents and executes it
		configureShScript = "touch ~/configure.sh; echo \"" + configureShScript.replace("\"", "\\\"").replace("$", "\\$") + "\" > ~/configure.sh; sudo httpdport=" + httpdport + " sh ~/configure.sh";
		Variable configureShVar = templateContext.createGlobalStringVariable(configureShScriptVarName, configureShScript);
		runScriptRequestInputParams.put("script", configureShVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		
		/* execute start.sh */
		String startShScriptVarName = "startShScript" + templateContext.getIdForNames();
		String startShScript = this.getStartShAsString();
		startShScript = "touch ~/start.sh; echo \"" + startShScript.replace("\"", "\\\"").replace("$", "\\$") + "\" > ~/start.sh; sudo sh ~/start.sh";
		Variable startShVar = templateContext.createGlobalStringVariable(startShScriptVarName, startShScript);
		runScriptRequestInputParams.put("script", startShVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		return true;
	}
	
	/**
	 * Small helper method to retrieve all DOM Node contents
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
	 * Loads a bash file into a string and returns it. The bash file contains
	 * logic to configure an apache http server on ubuntu
	 *
	 * @return a String containing a complete bash script
	 */
	private String getConfigureShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("configure.sh");
		File configureShFile;
		try {
			configureShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't load configure.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(configureShFile);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't read string from file configure.sh", e);
			return null;
		}
	}
	
	/**
	 * Loads a bash file into a string and returns it. The bash file contains
	 * logic to configure an apache http server on ubuntu
	 *
	 * @return a String containing a complete bash script
	 */
	private String getStartShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("start.sh");
		File startShFile;
		try {
			startShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't load start.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(startShFile);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't read string from file start.sh", e);
			return null;
		}
	}
}
