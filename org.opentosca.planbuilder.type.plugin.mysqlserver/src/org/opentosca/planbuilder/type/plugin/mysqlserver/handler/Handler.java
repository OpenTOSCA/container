package org.opentosca.planbuilder.type.plugin.mysqlserver.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.commons.PluginUtils;
import org.opentosca.planbuilder.plugins.commons.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.mysqlserver.Constants;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Handler {
	
	private Plugin invokerPlugin = new Plugin();
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	
	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers fails
	 */
	public Handler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
	}
	
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
		
		Handler.LOG.debug("Searching for RootPassword property");
		String mySqlServerRootPass = "";
		for (int i = 0; i < properties.getLength(); i++) {
			Handler.LOG.debug("Found property element with name " + properties.item(i).getNodeName());
			if (properties.item(i).getNodeName().contains("RootPassword")) {
				mySqlServerRootPass = this.getValueOfElement(properties.item(i));
				Handler.LOG.debug("Found RootPassword property with value " + mySqlServerRootPass);
			}
		}
		
		if (mySqlServerRootPass.equals("")) {
			Handler.LOG.warn("No RootPassword is set in nodeTemplate " + templateContext.getNodeTemplate().getId());
			return false;
		}
		
		// fetch server ip of the vm this apache http will be installed on
		
		Variable mySqlServerIp = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (mySqlServerIp == null) {
			mySqlServerIp = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (mySqlServerIp == null) {
				mySqlServerIp = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}
		
		if (mySqlServerIp == null) {
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
			if (PluginUtils.isSupportedUbuntuVMNodeType(nodeTemplate.getType().getId())) {
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
		String mySqlServerPackageVarName = "mySqlServerPackageVar" + templateContext.getIdForNames();
		Variable mySqlServerPackageVar = templateContext.createGlobalStringVariable(mySqlServerPackageVarName, "mysql-server");
		
		// used for the invokerPlugin. This map contains mappings from internal
		// variables or data which must be fetched form the input message (value
		// of map == null)
		Map<String, Variable> installPackageRequestInputParams = new HashMap<String, Variable>();
		
		/*
		 * methodName: installPackage params: "hostname", "sshKey", "sshUser",
		 * "packageNames"
		 */
		installPackageRequestInputParams.put("hostname", mySqlServerIp);
		installPackageRequestInputParams.put("sshKey", sshKeyVariable);
		installPackageRequestInputParams.put("sshUser", sshUserVariable);
		installPackageRequestInputParams.put("packageNames", mySqlServerPackageVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "installPackage", "InterfaceUbuntu", "planCallbackAddress_invoker", installPackageRequestInputParams, new HashMap<String, Variable>(), true);
		
		/*
		 * Execute install, configure and start scripts
		 */
		
		/*
		 * methodname: runScript params: "hostname","script","sshKey","sshUser"
		 */
		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
		
		runScriptRequestInputParams.put("hostname", mySqlServerIp);
		runScriptRequestInputParams.put("sshKey", sshKeyVariable);
		runScriptRequestInputParams.put("sshUser", sshUserVariable);
		
		String startMysqlServerShVarName = "startMysqlServerSh" + templateContext.getIdForNames();
		long tempFolder = System.currentTimeMillis();
		Variable startMysqlServerShVar = templateContext.createGlobalStringVariable(startMysqlServerShVarName, "mkdir ~/" + tempFolder + ";touch ~/" + tempFolder + "/dump.txt; sudo service mysqld start > ~/" + tempFolder + "/dump.txt");
		runScriptRequestInputParams.put("script", startMysqlServerShVar);
		
		// execute script to start mysql server
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		
		// create script to set the mysql rootpw
		String setRootPwShVarName = "setRootPwShScript" + templateContext.getIdForNames();
		Variable setRootPwShVar = templateContext.createGlobalStringVariable(setRootPwShVarName, "");
		String xpathQuery = "concat('sudo mysqladmin -u root password ',$" + templateContext.getVariableNameOfProperty(templateContext.getNodeTemplate().getId(), "RootPassword") + ")";
		runScriptRequestInputParams.put("script", setRootPwShVar);
		
		try {
			Node assignNode = this.loadAssignStringFromXpathQueryFragmentAsNode("assignMySqlRootPassword", xpathQuery, setRootPwShVar.getName());
			assignNode = templateContext.importNode(assignNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}
		
		// execute install script (formerly it was the install.sh script)
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);
		
		if (!this.executeConfigureDBSh(mySqlServerIp, sshKeyVariable, sshUserVariable, templateContext, templateId)) {
			return false;
		}
		
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
	
	private String loadAssignStringFromXpathQueryFragmentAsString(String assignName, String xpathQuery, String stringVarName) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getResource("assignStringVarWithXpathQuery.xml");
		File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelFragmentFile);
		
		// <!-- {AssignName},{XpathQuery}, {stringVarName} -->
		template = template.replace("{AssignName}", assignName);
		template = template.replace("{XpathQuery}", xpathQuery);
		template = template.replace("{stringVarName}", stringVarName);
		return template;
	}
	
	private Node loadAssignStringFromXpathQueryFragmentAsNode(String assignName, String xpathQuery, String stringVarName) throws IOException, SAXException {
		String templateString = this.loadAssignStringFromXpathQueryFragmentAsString(assignName, xpathQuery, stringVarName);
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
		
		LOG.debug("Trying to fetch parameters for configureDB.sh");
		for (String param : Constants.configureDBScriptParameters) {
			LOG.debug("Begin fetching parameter " + param);
			String[] split = param.split("_");
			String paramName = split[1];
			
			Variable var = templateContext.getPropertyVariable(paramName);
			if (var == null) {
				LOG.debug("Didn't find variable, looking for source");
				var = templateContext.getPropertyVariable(paramName, true);
				if (var == null) {
					LOG.debug("Didn't find variable, looking for target");
					var = templateContext.getPropertyVariable(paramName, false);
				}
			}
			
			envVarString += param + "=$" + param + "$ ";
			xpathQueryPrefix += "replace(";
			xpathQuerySuffix += ",'\\$" + param + "\\$',";
			if (var == null) {
				LOG.debug("Param " + param + " is external");
				// param is external
				xpathQuerySuffix += "$" + templateContext.getPlanRequestMessageName() + ".payload//*[local-name()='" + param + "']/text())";
				templateContext.addStringValueToPlanRequest(paramName);
			} else {
				// param is internal
				LOG.debug("Param " + param + " is internal");
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
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", inputMappings, new HashMap<String, Variable>(),false);
		
		return true;
	}
	
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
	
}
