package org.opentosca.planbuilder.generic.plugin.phponapacheinvoker.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
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

	private final static QName ubuntuNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "Ubuntu");
	private final static QName ubuntuNodeTypeOpenTOSCAPlanBuilder = new QName("http://opentosca.org/types/declarative", "Ubuntu");


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

		Variable serverIpPropWrapper = templateContext.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = templateContext.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = templateContext.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("No Infrastructure Node available with ServerIp property");
			return false;
		}

		// find the ubuntu node and its nodeTemplateId
		String templateId = "";

		for (AbstractNodeTemplate nodeTemplate : templateContext.getNodeTemplates()) {
			if (Handler.isUbuntuNodeTypeCompatible(nodeTemplate.getType().getId())) {
				templateId = nodeTemplate.getId();
			}
		}

		if (templateId.equals("")) {
			Handler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
			return false;
		}

		// add sshUser and sshKey to the input message of the build plan
		templateContext.addStringValueToPlanRequest("sshUser");
		templateContext.addStringValueToPlanRequest("sshKey");

		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// generate string variable for "httpd" package. This way it's easier to
		// program (no assigns by hand, etc.)
		String phpPackagesVarName = "phpPackagesVar" + templateContext.getIdForNames();
		Variable phpPackagesVar = templateContext.createGlobalStringVariable(phpPackagesVarName, "php5 php5-cli php5-common php5-mysql");

		/*
		 * Check whether the SSH port is open on the VM
		 */
		Map<String, Variable> startRequestInputParams = new HashMap<String, Variable>();

		startRequestInputParams.put("hostname", serverIpPropWrapper);
		startRequestInputParams.put("sshUser", null);
		startRequestInputParams.put("sshKey", null);

		this.invokerPlugin.handle(templateContext, templateId, true, "start", "InterfaceUbuntu", "planCallbackAddress_invoker", startRequestInputParams, new HashMap<String, Variable>());

		/*
		 * Install php packages
		 */
		Map<String, Variable> installPackageRequestInputParams = new HashMap<String, Variable>();

		installPackageRequestInputParams.put("hostname", serverIpPropWrapper);
		installPackageRequestInputParams.put("sshUser", null);
		installPackageRequestInputParams.put("sshKey", null);
		installPackageRequestInputParams.put("packageNames", phpPackagesVar);

		this.invokerPlugin.handle(templateContext, templateId, true, "installPackage", "InterfaceUbuntu", "planCallbackAddress_invoker", installPackageRequestInputParams, new HashMap<String, Variable>());

		/*
		 * Execute start script (restart httpd)
		 */

		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
		String startShScript = this.getPhpStartShAsString();
		startShScript = "touch ~/php_start.sh; echo \"" + startShScript.replace("\"", "\\\"").replace("$", "\\$").replace("`", "\\`") + "\" > ~/php_start.sh; sudo sh ~/php_start.sh";
		String startShScriptVarName = "phpModuleStartSh" + templateContext.getIdForNames();
		Variable startShVar = templateContext.createGlobalStringVariable(startShScriptVarName, startShScript);

		runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
		runScriptRequestInputParams.put("sshKey", null);
		runScriptRequestInputParams.put("sshUser", null);
		runScriptRequestInputParams.put("script", startShVar);

		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>());

		return true;
	}

	/**
	 * Checks whether the given QName represents a Ubuntu OS NodeType compatible
	 * with this plugin
	 *
	 * @param nodeTypeId a QName denoting a TOSCA NodeType
	 * @return true iff the given QName is a NodeType this plugin can handle
	 */
	private static boolean isUbuntuNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(Handler.ubuntuNodeType.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(Handler.ubuntuNodeTypeOpenTOSCAPlanBuilder.toString())) {
			return true;
		}
		return false;
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
