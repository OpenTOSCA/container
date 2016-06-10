package org.opentosca.planbuilder.type.plugin.mysqldatabase.handler;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.type.plugin.mysqldatabase.Util;
import org.opentosca.planbuilder.utils.Utils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class SQLFileHandler extends AbstractHandler {
	
	public SQLFileHandler() {
		try {
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docFactory.setNamespaceAware(true);
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			SQLFileHandler.LOG.error("Couldn't initialize XML Parser", e);
		}
	}
	
	public boolean handle(TemplatePlanContext templateContext, AbstractNodeTypeImplementation impl) {
		// fetch a list of DA's which are SQL Scripts to be executed on the
		// MySQL Server we're hostedOn
		
		List<AbstractDeploymentArtifact> sqlDAs = Util.getSQLScriptArtifactDAs(templateContext.getNodeTemplate().getDeploymentArtifacts());
		if (impl != null) {
			sqlDAs.addAll(Util.getSQLScriptArtifactDAs(impl.getDeploymentArtifacts()));
			sqlDAs = Util.removeDuplicates(sqlDAs);
		}
		
		if (sqlDAs.size() < 1) {
			// we need at least one proper DA
			return false;
		}
		
		// fetch proper ip of "our own" infrastructure
		Variable mySqlServerIpVar = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (mySqlServerIpVar == null) {
			mySqlServerIpVar = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (mySqlServerIpVar == null) {
				mySqlServerIpVar = templateContext.getPropertyVariable(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}
		
		if (mySqlServerIpVar == null) {
			SQLFileHandler.LOG.warn("No Infrastructure Node available with ServerIp property");
			return false;
		}
		
		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		SQLFileHandler.LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		// add csarEntryPoint to plan input message
		SQLFileHandler.LOG.debug("Adding csarEntryPoint field to plan input");
		templateContext.addStringValueToPlanRequest("csarEntrypoint");
		
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
		
		// find the ubuntu node and its nodeTemplateId
		String templateId = "";
		
		for (AbstractNodeTemplate node : templateContext.getNodeTemplates()) {
			if (org.opentosca.model.tosca.conventions.Utils.isSupportedUbuntuVMNodeType(node.getType().getId())) {
				templateId = node.getId();
			}
		}
		
		if (templateId.equals("")) {
			AbstractHandler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
			return false;
		}
		
		// add sshUser and sshKey to the input message of the build plan, if
		// needed
		if (sshUserVariable == null) {
			SQLFileHandler.LOG.debug("Adding sshUser field to plan input");
			templateContext.addStringValueToPlanRequest("sshUser");
			
		}
		
		if (sshKeyVariable == null) {
			SQLFileHandler.LOG.debug("Adding sshKey field to plan input");
			templateContext.addStringValueToPlanRequest("sshKey");
		}
		
		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		SQLFileHandler.LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		if (!this.executeConfigureDBSh(mySqlServerIpVar, sshKeyVariable, sshUserVariable, templateContext, templateId)) {
			return false;
		}
		
		// if we don't find the variable here, the property variable is not set,
		// the invoker plugin will add it to the plan input as parameter
		Variable dbPwVar = templateContext.getPropertyVariable("RootPassword");
		Variable dbPortVar = templateContext.getPropertyVariable("DBPort");
		
		/* call invoker for each DA to call the MySQLDB Web Service */
		// setup the parameters
		Map<String, Variable> inputMappings = new HashMap<String, Variable>();
		inputMappings.put("mySqlServerIp", mySqlServerIpVar);
		inputMappings.put("mySqlServerPort", dbPortVar);
		// TODO this is a hack, until the property RootUser is set in the
		// NodeType
		inputMappings.put("mySqlServerRootUser", templateContext.createGlobalStringVariable("mySqlServerRootUserVar" + templateContext.getIdForNames(), "root"));
		inputMappings.put("mySqlServerRootPassword", dbPwVar);
		
		for (AbstractDeploymentArtifact da : sqlDAs) {
			for (AbstractArtifactReference ref : da.getArtifactRef().getArtifactReferences()) {
				
				// assemble fileRef value at bpel runtime
				
				String containerAPIAbsoluteURIXPathQuery = this.createXPathQueryForURLRemoteFilePath(ref.getReference());
				String containerAPIAbsoluteURIVarName = "mySqlDatabaseDAcontainerApiFileURL" + templateContext.getIdForNames();
				/*
				 * create a string variable with a complete URL to the file we
				 * want to upload
				 */
				
				Variable containerAPIAbsoluteURIVar = templateContext.createGlobalStringVariable(containerAPIAbsoluteURIVarName, "");
				
				try {
					Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assign" + templateContext.getIdForNames(), containerAPIAbsoluteURIXPathQuery, containerAPIAbsoluteURIVar.getName());
					assignNode = templateContext.importNode(assignNode);
					templateContext.getProvisioningPhaseElement().appendChild(assignNode);
				} catch (IOException e) {
					SQLFileHandler.LOG.error("Couldn't read internal file", e);
					return false;
				} catch (SAXException e) {
					SQLFileHandler.LOG.error("Couldn't parse internal xml file");
					return false;
				}
				
				inputMappings.put("mySqlFileRef", containerAPIAbsoluteURIVar);
				
				this.invokerPlugin.handle(templateContext, "executeSQLFile", "http://opentosca.org/declarative/mysqldb/", "planCallbackAddress_invoker", inputMappings, new HashMap<String, Variable>());
			}
		}
		
		return true;
	}
	
	/**
	 * Returns an XPath Query which contructs a valid String, to GET a File from
	 * the openTOSCA API
	 *
	 * @param artifactPath a path inside an ArtifactTemplate
	 * @return a String containing an XPath query
	 */
	public String createXPathQueryForURLRemoteFilePath(String artifactPath) {
		SQLFileHandler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
		String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
		return filePath;
	}
	
}
