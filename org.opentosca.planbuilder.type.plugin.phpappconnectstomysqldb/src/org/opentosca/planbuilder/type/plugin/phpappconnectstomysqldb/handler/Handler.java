package org.opentosca.planbuilder.type.plugin.phpappconnectstomysqldb.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.phpappconnectstomysqldb.Constants;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
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
		// fetch server ip of the app
		Variable appServerIp = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
		// fetch server ip of the db
		Variable dbServerIp = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
		
		if (appServerIp == null) {
			LOG.error("Couldn't find appropiate property for application ip");
			return false;
		}
		if (dbServerIp == null) {
			LOG.error("Couldn't find appropiate property for db ip");
			return false;
		}
		
		// find the ubuntu node and its nodeTemplateId
		String templateId = "";
		
		// we need the target ubuntu as we execute the script on that machine
		List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getInfrastructureNodes(templateContext.getRelationshipTemplate().getSource(), infraNodes);
		
		for (AbstractNodeTemplate nodeTemplate : infraNodes) {
			if (Utils.checkForTypeInHierarchy(nodeTemplate, Types.ubuntuNodeType)) {
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
		LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		Map<String, Variable> inputMappings = new HashMap<String, Variable>();
		
		inputMappings.put("hostname", appServerIp);
		
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
		
		inputMappings.put("sshKey", sshKeyVariable);
		inputMappings.put("sshUser", sshUserVariable);
		
		// load script:
		// we will generate a script on the app vm
		// for that we load the placeholders from the relationshiptemplate
		// with that we search for the values (e.g. db ip, db name)
		// at last we generate a bpel query which will replace some new
		// placeholders in the bashcommand with runtime value (especially the db
		// ip)
		long tempFolderName = System.currentTimeMillis();
		Map<String, String> placeholderNameToValueMap = this.fetchPlaceholderMappings(templateContext);
		String configPath = "/var/www/" + this.fetchConfigPath(templateContext);
		String bashCommand = "mkdir ~/" + tempFolderName + ";";
		bashCommand += "touch ~/" + tempFolderName + "/connectToDb.sh;";
		bashCommand += "echo \"" + this.createSedScript(placeholderNameToValueMap.values(), configPath).replace("\"", "\\\"").replace("$", "\\$").replace("`", "\\`") + "\" > ~/" + tempFolderName + "/connectToDb.sh;";
		
		// create env var string
		String envVarString = "";
		String xpathQueryPrefix = "";
		String xpathQuerySuffix = "";
		
		for (String placeholder : placeholderNameToValueMap.keySet()) {
			
			Variable var = null;
			switch (placeholder) {
			case "DBAddressPlaceHolder":
				var = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
				break;
			case "DBUserPlaceHolder":
				var = templateContext.getPropertyVariable("DBUser");
				break;
			case "DBPasswordPlaceHolder":
				var = templateContext.getPropertyVariable("DBPassword");
				break;
			case "DBNamePlaceHolder":
				var = templateContext.getPropertyVariable("DBName");
				break;
			}
			
			envVarString += placeholderNameToValueMap.get(placeholder) + "=$" + placeholder + "$ ";
			xpathQueryPrefix += "replace(";
			xpathQuerySuffix += ",'\\$" + placeholder + "\\$',";
			if (var == null) {
				// param is external
				xpathQuerySuffix += "$" + templateContext.getPlanRequestMessageName() + ".payload//*[local-name()='" + placeholder + "']/text())";
				templateContext.addStringValueToPlanRequest(placeholder);
			} else {
				// param is internal
				if (placeholder.equals("DBAddressPlaceHolder") && this.checkIfRelationConnectsAppAndDBOnSameVM(templateContext)) {
					// we make this check, because some apps won't work if the
					// DB is on the same VM and we set the public dns instead of
					// localhost
					xpathQuerySuffix += "'localhost')";
				} else {
					xpathQuerySuffix += "$" + var.getName() + ")";
				}
				
			}
		}
		
		bashCommand += "sudo " + envVarString + " sh ~/" + tempFolderName + "/connectToDb.sh";
		
		// generate string var with the bashcommand
		String connectToDbShVarName = "connectToDbShScript" + templateContext.getIdForNames();
		Variable connectToDBShStringVar = templateContext.createGlobalStringVariable(connectToDbShVarName, bashCommand);
		String xpathQuery = xpathQueryPrefix + "$" + connectToDBShStringVar.getName() + xpathQuerySuffix;
		
		try {
			// create assign and append
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assignShCallScriptVar", xpathQuery, connectToDBShStringVar.getName());
			assignNode = templateContext.importNode(assignNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			LOG.error("Couldn't load fragment from file", e);
			return false;
		} catch (SAXException e) {
			LOG.error("Couldn't parse fragment to DOM", e);
			return false;
		}
		
		inputMappings.put("script", connectToDBShStringVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", inputMappings, new HashMap<String, Variable>(),false);
		
		return true;
	}
	
	private boolean checkIfRelationConnectsAppAndDBOnSameVM(TemplatePlanContext templateContext) {
		LOG.debug("Checking if both App and Db is deployed on the same VM");
		AbstractRelationshipTemplate relationshipTemplate = templateContext.getRelationshipTemplate();
		AbstractNodeTemplate sourceNodeTemplate = relationshipTemplate.getSource();
		AbstractNodeTemplate targetNodeTemplate = relationshipTemplate.getTarget();
		String sourceNodeVmId = "";
		String targetNodeVmId = "";
		LOG.debug("Checking with source node " + sourceNodeTemplate.getId() + " and target node " + targetNodeTemplate.getId());
		
		List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<AbstractNodeTemplate>();
		
		Utils.getInfrastructureNodes(sourceNodeTemplate, infrastructureNodes);
		
		// find the vm node of the source
		for (AbstractNodeTemplate infraNode : infrastructureNodes) {
			LOG.debug("Found infrastructure node of source node: " + infraNode.getId());
			if (Utils.checkForTypeInHierarchy(infraNode, Types.vmNodeType)) {
				sourceNodeVmId = infraNode.getId();
				LOG.debug("Found source node VM with id: " + sourceNodeVmId);
			}
		}
		
		infrastructureNodes.clear();
		
		Utils.getInfrastructureNodes(targetNodeTemplate, infrastructureNodes);
		
		// find the vm node of the target
		for (AbstractNodeTemplate infraNode : infrastructureNodes) {
			LOG.debug("Found infrastructure node of target node: " + infraNode.getId());
			if (Utils.checkForTypeInHierarchy(infraNode, Types.vmNodeType)) {
				targetNodeVmId = infraNode.getId();
				LOG.debug("Found target node VM with id: " + targetNodeVmId);
			}
		}
		
		if (sourceNodeVmId.trim().isEmpty()) {
			LOG.warn("source node vm id is empty");
			return false;
		}
		
		if (targetNodeVmId.trim().isEmpty()) {
			LOG.warn("targetnode vm id is empty");
			return false;
		}
		
		if (sourceNodeVmId.trim().equals(targetNodeVmId.trim())) {
			LOG.debug("App and DB are deployed on the same VM");
			return true;
		}
		
		return false;
	}
	
	private Map<String, String> fetchPlaceholderMappings(TemplatePlanContext templateContext) {
		Map<String, String> placeholders = new HashMap<String, String>();
		NodeList children = templateContext.getRelationshipTemplate().getProperties().getDOMElement().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String localName = child.getLocalName();
			if (Constants.placeholderNames.contains(localName)) {
				String value = this.getValueOfElement(child);
				placeholders.put(localName, value);
			}
			
		}
		return placeholders;
	}
	
	private String fetchConfigPath(TemplatePlanContext templateContext) {
		NodeList children = templateContext.getRelationshipTemplate().getProperties().getDOMElement().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String localName = child.getLocalName();
			if (localName.equals("ConfigFilePath")) {
				String value = this.getValueOfElement(child);
				return value;
			}
			
		}
		return null;
	}
	
	private String createSedScript(Collection<String> stringsToReplace, String configPath) {
		String script = "";
		for (String stringToReplace : stringsToReplace) {
			script += this.createSedCommand(stringToReplace, configPath) + ";";
		}
		return script;
	}
	
	private String createSedCommand(String stringToReplace, String configPath) {
		// sed -i -e 's#YOURDBHOST#'${Target_PublicIP}'#' $Target_ConfigPath
		return "sed -i -e 's#" + stringToReplace + "#'${" + stringToReplace + "}'#' " + configPath;
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
