package org.opentosca.planbuilder.type.plugin.php.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

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

/**
 * <p>
 * This class contains all the logic to add BPEL Code which installs a PhpModule
 * on an Apache HTTP Server
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
	 * Adds BPEL code to the given TemplateContext which installs an PhpModule
	 * to an Apache HTTP Server
	 *
	 * @param templateContext the TemplateContext the code should be added to
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handle(TemplatePlanContext templateContext) {
		/*
		 * fetch relevant variables/properties
		 */
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}
		
		// fetch server ip of the vm this apache http php module will be
		// installed on
		
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
		
		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		// generate string variable for "httpd" package. This way it's easier to
		// program (no assigns by hand, etc.)
		String phpPackagesVarName = "phpPackagesVar" + templateContext.getIdForNames();
		
		Variable phpPackagesVar = templateContext.createGlobalStringVariable(phpPackagesVarName, "php5 php5-cli php5-common php5-mysql php5-json php5-curl php5-gd libapache2-mod-php5");
		
		/*
		 * Install php packages
		 */
		Map<String, Variable> installPackageRequestInputParams = new HashMap<String, Variable>();
		
		installPackageRequestInputParams.put("hostname", serverIpPropWrapper);
		installPackageRequestInputParams.put("sshUser", sshUserVariable);
		installPackageRequestInputParams.put("sshKey", sshKeyVariable);
		installPackageRequestInputParams.put("packageNames", phpPackagesVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "installPackage", "InterfaceUbuntu", "planCallbackAddress_invoker", installPackageRequestInputParams, new HashMap<String, Variable>(), true);
		
		/*
		 * Execute start script (restart httpd)
		 */
		
		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
		String startShScript = this.getPhpStartShAsString();
		long tempFolderName = System.currentTimeMillis();
		startShScript = "mkdir -p ~/" + tempFolderName + ";touch ~/" + tempFolderName + "/php_start.sh; echo \"" + startShScript.replace("\"", "\\\"").replace("$", "\\$").replace("`", "\\`") + "\" > ~/" + tempFolderName + "/php_start.sh; sudo sh ~/" + tempFolderName + "/php_start.sh";
		String startShScriptVarName = "phpModuleStartSh" + templateContext.getIdForNames();
		Variable startShVar = templateContext.createGlobalStringVariable(startShScriptVarName, startShScript);
		
		runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
		runScriptRequestInputParams.put("sshKey", sshKeyVariable);
		runScriptRequestInputParams.put("sshUser", sshUserVariable);
		runScriptRequestInputParams.put("script", startShVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		
		return true;
	}
	
	/**
	 * Returns a bash script to restart the apache http server
	 *
	 * @return a String containing a bash script
	 */
	private String getPhpStartShAsString() {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("php_start.sh");
		File phpStartShFile;
		try {
			phpStartShFile = new File(FileLocator.toFileURL(url).getPath());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't load php_start.sh file", e);
			return null;
		}
		
		try {
			return FileUtils.readFileToString(phpStartShFile);
		} catch (IOException e) {
			Handler.LOG.error("Couldn't read string from file php_start.sh", e);
			return null;
		}
	}
}
