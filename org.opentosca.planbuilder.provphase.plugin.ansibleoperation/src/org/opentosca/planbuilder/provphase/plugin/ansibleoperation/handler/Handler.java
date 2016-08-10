package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes
 * Ansible Playbooks on remote machine. The class assumes that the playbook that
 * must be called are already uploaded to the appropriate path. For example by
 * the ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public class Handler {

	private Plugin invokerPlugin = new Plugin();

	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(Handler.class);

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	public Handler() {
		try {
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docFactory.setNamespaceAware(true);
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds logic to the BuildPlan to call a Ansible Playbook on a remote
	 * machine
	 *
	 * @param context
	 *            the TemplatePlanContext where the logical provisioning
	 *            operation is called
	 * @param operation
	 *            the operation to call
	 * @param ia
	 *            the ia that implements the operation
	 * @return true iff adding BPEL Fragment was successful
	 */
	public boolean handle(TemplatePlanContext templateContext, AbstractOperation operation,
			AbstractImplementationArtifact ia) {

		LOG.debug("Handling Ansible Playbook IA operation: " + operation.getName());
		AbstractArtifactReference ansibleRef = this.fetchAnsiblePlaybookRefFromIA(ia);
		if (ansibleRef == null) {
			return false;
		}
		LOG.debug("Ref: " + ansibleRef.getReference());

		// calculate relevant nodeTemplates for this operation call (the node
		// itself and infraNodes)
		List<AbstractNodeTemplate> nodes = templateContext.getInfrastructureNodes();

		// add the template itself
		nodes.add(templateContext.getNodeTemplate());

		// find the ubuntu node and its nodeTemplateId
		AbstractNodeTemplate infrastructureNodeTemplate = this.findInfrastructureNode(nodes);

		if (infrastructureNodeTemplate == null) {
			Handler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
			return false;
		}

		/*
		 * fetch relevant variables/properties
		 */
		if (templateContext.getNodeTemplate() == null) {
			Handler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
			return false;
		}

		// fetch server ip of the vm this apache http php module will be
		// installed on
		Variable serverIpPropWrapper = null;
		for (String serverIp : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
			serverIpPropWrapper = templateContext.getPropertyVariable(infrastructureNodeTemplate, serverIp);
			if (serverIpPropWrapper != null) {
				break;
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("No Infrastructure Node available with ServerIp property");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (String vmUserName : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
			sshUserVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserName);
			if (sshUserVariable != null) {
				break;
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
		Variable sshKeyVariable = null;
		for (String vmUserPassword : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
			sshKeyVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserPassword);
			if (sshKeyVariable != null) {
				break;
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
			// dirty check if we use old style properties
			String cleanPropName = serverIpPropWrapper.getName()
					.substring(serverIpPropWrapper.getName().lastIndexOf("_") + 1);
			switch (cleanPropName) {
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
				LOG.debug("Adding sshUser field to plan input");
				templateContext.addStringValueToPlanRequest("sshUser");
				break;
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
				LOG.debug("Adding sshUser field to plan input");
				templateContext.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME);
				break;
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
				LOG.debug("Adding User fiel to plan input");
				templateContext.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANUSER);
				break;
			default:
				return false;

			}
		}

		if (sshKeyVariable == null) {
			// dirty check if we use old style properties
			String cleanPropName = serverIpPropWrapper.getName()
					.substring(serverIpPropWrapper.getName().lastIndexOf("_") + 1);
			switch (cleanPropName) {
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
				LOG.debug("Adding sshUser field to plan input");
				templateContext.addStringValueToPlanRequest("sshKey");
				break;
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
				LOG.debug("Adding sshUser field to plan input");
				templateContext
						.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
				break;
			case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
				LOG.debug("Adding User fiel to plan input");
				templateContext
						.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANPASSWD);
				break;
			default:
				return false;

			}
		}

		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// add csarEntryPoint to plan input message
		LOG.debug("Adding csarEntryPoint field to plan input");
		templateContext.addStringValueToPlanRequest("csarEntrypoint");

		Variable runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, operation, ansibleRef,
				ia);

		return this.appendExecuteScript(templateContext, infrastructureNodeTemplate.getId(), runShScriptStringVar,
				sshUserVariable, sshKeyVariable, serverIpPropWrapper);
	}

	public boolean handle(TemplatePlanContext templateContext, AbstractOperation operation,
			AbstractImplementationArtifact ia, Map<AbstractParameter, Variable> param2propertyMapping) {

		if (operation.getInputParameters().size() != param2propertyMapping.size()) {
			return false;
		}

		AbstractNodeTemplate infrastructureNodeTemplate = this
				.findInfrastructureNode(templateContext.getInfrastructureNodes());
		if (infrastructureNodeTemplate == null) {
			return false;
		}

		Variable runShScriptStringVar = null;
		AbstractArtifactReference scriptRef = this.fetchAnsiblePlaybookRefFromIA(ia);
		if (scriptRef == null) {
			return false;
		}
		runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, operation, scriptRef, ia,
				param2propertyMapping);

		Variable ipStringVariable = null;
		for (String serverIp : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineIPPropertyNames()) {
			ipStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, serverIp);
			if (ipStringVariable != null) {
				break;
			}
		}

		Variable userStringVariable = null;
		for (String vmUserName : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
			userStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserName);
			if (userStringVariable != null) {
				break;
			}
		}

		Variable passwdStringVariable = null;
		for (String vmUserPassword : org.opentosca.model.tosca.conventions.Utils
				.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
			passwdStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserPassword);
			if (passwdStringVariable != null) {
				break;
			}
		}

		if (this.isNull(runShScriptStringVar, ipStringVariable, userStringVariable, passwdStringVariable)) {
			// if either of the variables is null -> abort
			return false;
		}

		return this.appendExecuteScript(templateContext, infrastructureNodeTemplate.getId(), runShScriptStringVar,
				userStringVariable, passwdStringVariable, ipStringVariable);
	}

	private boolean isNull(Variable... vars) {
		for (Variable var : vars) {
			if (var == null) {
				return true;
			}
		}
		return false;
	}

	private AbstractNodeTemplate findInfrastructureNode(List<AbstractNodeTemplate> nodes) {
		for (AbstractNodeTemplate nodeTemplate : nodes) {
			if (org.opentosca.model.tosca.conventions.Utils
					.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
				return nodeTemplate;
			}
		}
		return null;
	}

	/**
	 * Append logic for executing a script on a remote machine with the invoker
	 * plugin
	 * 
	 * @param templateContext
	 *            the context with a bpel templateBuildPlan
	 * @param templateId
	 *            the id of the template inside the context
	 * @param runShScriptStringVar
	 *            the bpel variable containing the script call
	 * @param sshUserVariable
	 *            the user name for the remote machine as a bpel variable
	 * @param sshKeyVariable
	 *            the pass for the remote machine as a bpel variable
	 * @param serverIpPropWrapper
	 *            the ip of the remote machine as a bpel variable
	 * @return true if appending the bpel logic was successful else false
	 */
	private boolean appendExecuteScript(TemplatePlanContext templateContext, String templateId,
			Variable runShScriptStringVar, Variable sshUserVariable, Variable sshKeyVariable,
			Variable serverIpPropWrapper) {

		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
		// dirty check if we use old style properties
		String cleanPropName = serverIpPropWrapper.getName()
				.substring(serverIpPropWrapper.getName().lastIndexOf("_") + 1);
		switch (cleanPropName) {
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
			runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
			runScriptRequestInputParams.put("sshKey", sshKeyVariable);
			runScriptRequestInputParams.put("sshUser", sshUserVariable);
			runScriptRequestInputParams.put("script", runShScriptStringVar);
			this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu",
					"planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>(), false);

			break;
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
			runScriptRequestInputParams.put("VMIP", serverIpPropWrapper);
			runScriptRequestInputParams.put("VMPrivateKey", sshKeyVariable);
			runScriptRequestInputParams.put("VMUserName", sshUserVariable);
			runScriptRequestInputParams.put("Script", runShScriptStringVar);
			this.invokerPlugin.handle(templateContext, templateId, true, "runScript",
					Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
					runScriptRequestInputParams, new HashMap<String, Variable>(), false);
			break;
		default:
			return false;

		}
		return true;
	}

	private Variable appendBPELAssignOperationShScript(TemplatePlanContext templateContext, AbstractOperation operation,
			AbstractArtifactReference reference, AbstractImplementationArtifact ia) {

		String runShScriptStringVarName = "runShFile" + templateContext.getIdForNames();

		// install ansible
		String runShScriptString = "sudo apt-add-repository -y ppa:ansible/ansible && sudo apt-get update && sudo apt-get install -y ansible";

		// install unzip
		runShScriptString += " && sudo apt-get install unzip";

		String ansibleZipPath = templateContext.getCSARFileName() + "/" + reference.getReference();
		String ansibleZipFileName = FilenameUtils.getName(ansibleZipPath);
		String ansibleZipFolderName = FilenameUtils.getBaseName(ansibleZipFileName);
		String ansibleZipParentPath = FilenameUtils.getFullPathNoEndSeparator(ansibleZipPath);

		// go into directory of the ansible zip
		runShScriptString += " && cd " + ansibleZipParentPath;

		// unzip
		runShScriptString += " && unzip " + ansibleZipFileName;

		String playbookPath = getAnsiblePlaybookFilePath(templateContext);

		if (playbookPath == null) {

			LOG.error("No specified Playbook found in the corresponding ArtifactTemplate!");

		} else {

			LOG.debug("Found Playbook: {}", playbookPath);

			String completePlaybookPath = ansibleZipFolderName + "/" + FilenameUtils.separatorsToUnix(playbookPath);
			String playbookFolder = FilenameUtils.getFullPathNoEndSeparator(completePlaybookPath);
			String playbookFile = FilenameUtils.getName(completePlaybookPath);

			// go into the unzipped directory
			runShScriptString += " && cd " + playbookFolder;

			// execute ansible playbook
			runShScriptString += " && ansible-playbook " + playbookFile;
		}

		Variable runShScriptStringVar = templateContext.createGlobalStringVariable(runShScriptStringVarName,
				runShScriptString);

		return runShScriptStringVar;
	}

	private Variable appendBPELAssignOperationShScript(TemplatePlanContext templateContext, AbstractOperation operation,
			AbstractArtifactReference reference, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> inputMappings) {

		LOG.error("Not supported!");

		return null;
	}

	/**
	 * Searches for the Playbook Mapping in the ArtifactTemplate
	 * 
	 * @param templateContext
	 * @return Path to the specified Ansible Playbook within the .zip
	 */
	private String getAnsiblePlaybookFilePath(TemplatePlanContext templateContext) {

		List<AbstractNodeTypeImplementation> abstractNodeTypeImpls = templateContext.getNodeTemplate()
				.getImplementations();

		for (AbstractNodeTypeImplementation abstractNodeTypeImpl : abstractNodeTypeImpls) {
			List<AbstractImplementationArtifact> abstractIAs = abstractNodeTypeImpl.getImplementationArtifacts();
			for (AbstractImplementationArtifact abstractIA : abstractIAs) {
				NodeList nodeList = abstractIA.getArtifactRef().getProperties().getDOMElement()
						.getElementsByTagName("Playbook");
				if (nodeList.getLength() > 0) {
					return nodeList.item(0).getTextContent();
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first occurrence of *.zip file, inside the given
	 * ImplementationArtifact
	 *
	 * @param ia
	 *            an AbstractImplementationArtifact
	 * @return a String containing a relative file path to a *.zip file, if no
	 *         *.zip file inside the given IA is found null
	 */
	private AbstractArtifactReference fetchAnsiblePlaybookRefFromIA(AbstractImplementationArtifact ia) {
		List<AbstractArtifactReference> refs = ia.getArtifactRef().getArtifactReferences();
		for (AbstractArtifactReference ref : refs) {
			if (ref.getReference().endsWith(".zip")) {
				return ref;
			}
		}
		return null;
	}

	/**
	 * Loads a BPEL Assign fragment which queries the csarEntrypath from the
	 * input message into String variable.
	 *
	 * @param assignName
	 *            the name of the BPEL assign
	 * @param xpath2Query
	 *            the csarEntryPoint XPath query
	 * @param stringVarName
	 *            the variable to load the queries results into
	 * @return a String containing a BPEL Assign element
	 * @throws IOException
	 *             is thrown when reading the BPEL fragment form the resources
	 *             fails
	 */
	public String loadAssignXpathQueryToStringVarFragmentAsString(String assignName, String xpath2Query,
			String stringVarName) throws IOException {
		// <!-- {AssignName},{xpath2query}, {stringVarName} -->
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("assignStringVarWithXpath2Query.xml");
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
	 * @param assignName
	 *            the name of the BPEL assign
	 * @param csarEntryXpathQuery
	 *            the csarEntryPoint XPath query
	 * @param stringVarName
	 *            the variable to load the queries results into
	 * @return a DOM Node representing a BPEL assign element
	 * @throws IOException
	 *             is thrown when loading internal bpel fragments fails
	 * @throws SAXException
	 *             is thrown when parsing internal format into DOM fails
	 */
	public Node loadAssignXpathQueryToStringVarFragmentAsNode(String assignName, String xpath2Query,
			String stringVarName) throws IOException, SAXException {
		String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query,
				stringVarName);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
