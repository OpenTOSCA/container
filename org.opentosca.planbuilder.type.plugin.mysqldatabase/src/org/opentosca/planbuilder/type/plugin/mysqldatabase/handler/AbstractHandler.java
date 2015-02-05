package org.opentosca.planbuilder.type.plugin.mysqldatabase.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.mysqldatabase.Constants;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public abstract class AbstractHandler {
	
	protected static final Logger LOG = LoggerFactory.getLogger(LifecycleHandler.class);
	protected DocumentBuilderFactory docFactory;
	protected DocumentBuilder docBuilder;
	protected Plugin invokerPlugin = new Plugin();
	
	
	/**
	 * Loads configureDB.sh from resources. Script generates empty database on a
	 * mysql server
	 *
	 * @return a String containing a bash script
	 * @throws IOException is thrown when reading file from resources fails
	 */
	protected String loadConfigDBSh() throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("configureDB.sh");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		return template;
	}
	
	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName the name of the BPEL assign
	 * @param xpath2Query the csarEntryPoint XPath query
	 * @param stringVarName the variable to load the queries results into
	 * @return a String containing a BPEL Assign element
	 * @throws IOException is thrown when reading the BPEL fragment form the
	 *             resources fails
	 */
	public String loadAssignXpathQueryToStringVarFragmentAsString(String assignName, String xpath2Query, String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignStringVarWithXpath2Query.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{xpath2query}", xpath2Query);
		template = template.replace("{stringVarName}", stringVarName);
		return template;
	}
	
	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName the name of the BPEL assign
	 * @param csarEntryXpathQuery the csarEntryPoint XPath query
	 * @param stringVarName the variable to load the queries results into
	 * @return a DOM Node representing a BPEL assign element
	 * @throws IOException is thrown when loading internal bpel fragments fails
	 * @throws SAXException is thrown when parsing internal format into DOM
	 *             fails
	 */
	public Node loadAssignXpathQueryToStringVarFragmentAsNode(String assignName, String xpath2Query, String stringVarName) throws IOException, SAXException {
		String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
	
	protected boolean executeConfigureDBSh(Variable serverIpPropWrapper, Variable sshKeyVariable, Variable sshUserVariable, TemplatePlanContext templateContext, String templateId) {
		// setup inputmappings
		Map<String, Variable> inputMappings = new HashMap<String, Variable>();
		
		inputMappings.put("hostname", serverIpPropWrapper);
		inputMappings.put("sshKey", sshKeyVariable);
		inputMappings.put("sshUser", sshUserVariable);
		
		// load script
		String configureDBScript;
		try {
			configureDBScript = this.loadConfigDBSh();
		} catch (IOException e) {
			LOG.error("Couldn't read script file from resources", e);
			return false;
		}
		long tempFolderName = System.currentTimeMillis();
		
		// construct first parts of bash command
		String bashCommand = "mkdir ~/" + tempFolderName + "; touch ~/" + tempFolderName + "/configureDB.sh;touch ~/" + tempFolderName + "/dump.txt; echo \"" + configureDBScript.replace("\"", "\\\"").replace("$", "\\$").replace("`", "\\`") + "\" > ~/" + tempFolderName + "/configureDB.sh;";
		
		// now we append env vars to the sh call of the full bash command and
		// while we do this, also construct an xpath query which replaces the
		// generated placeholders with runtime values
		String envVarString = "";
		String xpathQueryPrefix = "";
		String xpathQuerySuffix = "";
		
		for (String param : Constants.configureDBScriptParameters) {
			
			String[] split = param.split("_");
			String paramName = split[1];
			
			Variable var = templateContext.getPropertyVariable(paramName);
			if (var == null) {
				var = templateContext.getPropertyVariable(paramName, true);
				if (var == null) {
					var = templateContext.getPropertyVariable(paramName, false);
				}
			}
			
			envVarString += param + "=$" + param + "$ ";
			xpathQueryPrefix += "replace(";
			xpathQuerySuffix += ",'\\$" + param + "\\$',";
			if (var == null) {
				// param is external
				xpathQuerySuffix += "$" + templateContext.getPlanRequestMessageName() + ".payload//*[local-name()='" + param + "']/text())";
				templateContext.addStringValueToPlanRequest(paramName);
			} else {
				// param is internal
				xpathQuerySuffix += "$" + var.getName() + ")";
				
			}
		}
		
		String shCall = "sudo " + envVarString + " sh ~/" + tempFolderName + "/configureDB.sh";
		bashCommand += shCall + " > ~/" + tempFolderName + "/dump.txt";
		
		// generate string var with the bashcommand
		String configureDBShVarName = "configureDBShScript" + templateContext.getIdForNames();
		Variable configureDBShStringVar = templateContext.createGlobalStringVariable(configureDBShVarName, bashCommand);
		String xpathQuery = xpathQueryPrefix + "$" + configureDBShStringVar.getName() + xpathQuerySuffix;
		
		try {
			// create assign and append
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assignShCallScriptVar", xpathQuery, configureDBShStringVar.getName());
			assignNode = templateContext.importNode(assignNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			LOG.error("Couldn't load fragment from file", e);
			return false;
		} catch (SAXException e) {
			LOG.error("Couldn't parse fragment to DOM", e);
			return false;
		}
		
		// add script to input
		inputMappings.put("script", configureDBShStringVar);
		
		// use invoker now to execute the script
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", inputMappings, new HashMap<String, Variable>());
		
		return true;
	}
	
}
