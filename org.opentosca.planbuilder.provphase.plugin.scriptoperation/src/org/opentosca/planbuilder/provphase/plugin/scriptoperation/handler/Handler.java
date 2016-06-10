package org.opentosca.planbuilder.provphase.plugin.scriptoperation.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.TemplatePlanBuilder;
import org.opentosca.planbuilder.TemplatePlanBuilder.ProvisioningChain;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes
 * Scripts on remote machine. The class assumes that the script that must be
 * called are already uploaded to the appropiate path. For example by the
 * ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
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
	 * Adds logic to the BuildPlan to call a Script on a remote machine
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
		AbstractArtifactReference scriptRef = this.fetchScriptRefFromIA(ia);
		if (scriptRef == null) {
			return false;
		}

		// calculate relevant nodeTemplates for this operation call (the node
		// itself and infraNodes)
		List<AbstractNodeTemplate> nodes = templateContext.getInfrastructureNodes();
		
		// add the template itself
		nodes.add(templateContext.getNodeTemplate());

		// find the ubuntu node and its nodeTemplateId
		AbstractNodeTemplate ubuntuNodeTemplate = null;
		String templateId = "";

		for (AbstractNodeTemplate nodeTemplate : nodes) {
			if (org.opentosca.model.tosca.conventions.Utils.isSupportedUbuntuVMNodeType(nodeTemplate.getType().getId())) {
				ubuntuNodeTemplate = nodeTemplate;
				templateId = nodeTemplate.getId();
			}
		}

		if (templateId.equals("")) {
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
		for (String serverIp : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineIPPropertyNames()) {
			serverIpPropWrapper = templateContext.getPropertyVariable(ubuntuNodeTemplate, serverIp);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = templateContext.getPropertyVariable(serverIp, true);
				if (serverIpPropWrapper == null) {
					serverIpPropWrapper = templateContext.getPropertyVariable(serverIp, false);
				}else {
					break;
				}
			} else {
				break;
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("No Infrastructure Node available with ServerIp property");
			return false;
		}

		// find sshUser and sshKey
		Variable sshUserVariable = null;
		for (String vmUserName : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
			sshUserVariable = templateContext.getPropertyVariable(ubuntuNodeTemplate, vmUserName);
			if (sshUserVariable == null) {
				sshUserVariable = templateContext.getPropertyVariable(vmUserName, true);
				if (sshUserVariable == null) {
					sshUserVariable = templateContext.getPropertyVariable(vmUserName, false);
				}else {
					break;
				}
			}else {
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
		for (String vmUserPassword : org.opentosca.model.tosca.conventions.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
			sshKeyVariable = templateContext.getPropertyVariable(ubuntuNodeTemplate, vmUserPassword);
			if (sshKeyVariable == null) {
				sshKeyVariable = templateContext.getPropertyVariable(vmUserPassword, true);
				if (sshKeyVariable == null) {
					sshKeyVariable = templateContext.getPropertyVariable(vmUserPassword, false);
				}else {
					break;
				}
			}else {
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
				templateContext.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANPASSWD);
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

		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();

		Variable runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, operation, scriptRef,
				ia);

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
			runScriptRequestInputParams.put("VMIP", serverIpPropWrapper);
			runScriptRequestInputParams.put("VMUserPassword", sshKeyVariable);
			runScriptRequestInputParams.put("VMUserName", sshUserVariable);
			runScriptRequestInputParams.put("Script", runShScriptStringVar);
			this.invokerPlugin.handle(templateContext, templateId, true, "runScript",
					Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
					runScriptRequestInputParams, new HashMap<String, Variable>(), false);

			break;
		case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
			runScriptRequestInputParams.put("VMIP", serverIpPropWrapper);
			runScriptRequestInputParams.put("VMUserPassword", sshKeyVariable);
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

	private String createDANamePathMapEnvVar(TemplatePlanContext templateContext, AbstractImplementationArtifact ia) {
		AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

		// find selected implementation
		ProvisioningChain chain = TemplatePlanBuilder.createProvisioningChain(nodeTemplate);

		List<AbstractDeploymentArtifact> das = chain.getDAsOfCandidate(0);

		String daEnvMap = "";
		if (nodeTemplate != null && !das.isEmpty()) {
			daEnvMap += "DAs=\"";
			for (AbstractDeploymentArtifact da : das) {
				String daName = da.getName();
				// FIXME we assume single artifact references at this point
				String daRef = da.getArtifactRef().getArtifactReferences().get(0).getReference();

				// FIXME / is a brutal assumption
				if (!daRef.startsWith("/")) {
					daRef = "/" + daRef;
				}

				daEnvMap += daName + "," + daRef + ";";
			}
			daEnvMap += "\" ";
		}
		return daEnvMap;
	}

	private Variable appendBPELAssignOperationShScript(TemplatePlanContext templateContext, AbstractOperation operation,
			AbstractArtifactReference reference, AbstractImplementationArtifact ia) {
		/*
		 * First we initialize a bash script of this form: sudo sh
		 * $InputParamName=ValPlaceHolder* referenceShFileName.sh
		 * 
		 * After that we try to generate a xpath 2.0 query of this form:
		 * ..replace
		 * (replace($runShScriptStringVar,"ValPlaceHolder",$PropertyVariableName
		 * ),"ValPlaceHolder",$planInputVar.partName/inputFieldLocalName)..
		 * 
		 * With both we have a string with runtime property values or input
		 * params
		 */
		Map<String, Variable> inputMappings = new HashMap<String, Variable>();
		String runShScriptString = "mkdir -p ~/" + templateContext.getCSARFileName() + "/logs/plans/ && chmod +x ~/"
				+ templateContext.getCSARFileName() + "/" + reference.getReference() + " && sudo -E "
				+ this.createDANamePathMapEnvVar(templateContext, ia);


		String runShScriptStringVarName = "runShFile" + templateContext.getIdForNames();
		String xpathQueryPrefix = "";
		String xpathQuerySuffix = "";

		for (AbstractParameter parameter : operation.getInputParameters()) {
			// First compute mappings from operation parameters to
			// property/inputfield
			Variable var = templateContext.getPropertyVariable(parameter.getName());
			if (var == null) {
				var = templateContext.getPropertyVariable(parameter.getName(), true);
				if (var == null) {
					var = templateContext.getPropertyVariable(parameter.getName(), false);
				}
			}
			inputMappings.put(parameter.getName(), var);

			// Initialize bash script string variable with placeholders
			runShScriptString += parameter.getName() + "=$" + parameter.getName() + "$ ";

			// put together the xpath query
			xpathQueryPrefix += "replace(";
			// set the placeholder to replace
			xpathQuerySuffix += ",'\\$" + parameter.getName() + "\\$',";
			if (var == null) {
				// param is external, query value form input message e.g.
				// $input.payload//*[local-name()='csarEntrypoint']/text()

				xpathQuerySuffix += "$" + templateContext.getPlanRequestMessageName() + ".payload//*[local-name()='"
						+ parameter.getName() + "']/text())";
			} else {
				// param is internal, so just query the bpelvar e.g. $Varname
				xpathQuerySuffix += "$" + var.getName() + ")";
			}
		}
		// add path to script
		runShScriptString += "~/" + templateContext.getCSARFileName() + "/" + reference.getReference();

		// construct log file path
		String logFilePath = "~/" + templateContext.getCSARFileName() + "/logs/plans/"
				+ templateContext.getTemplateBuildPlanName() + "$(date +\"%m_%d_%Y\").log";
		// append command to log the operation call on the machine
		runShScriptString += " > " + logFilePath;
		// and echo the operation call log
		runShScriptString += " && echo " + logFilePath;
		
		// generate string var with script
		Variable runShScriptStringVar = templateContext.createGlobalStringVariable(runShScriptStringVarName,
				runShScriptString);

		// Reassign string var with runtime values and replace their
		// placeholders
		try {
			// create xpath query
			String xpathQuery = xpathQueryPrefix + "$" + runShScriptStringVar.getName() + xpathQuerySuffix;
			// create assign and append
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assignShCallScriptVar", xpathQuery,
					runShScriptStringVar.getName());
			assignNode = templateContext.importNode(assignNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error("Couldn't load fragment from file", e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			LOG.error("Couldn't parse fragment to DOM", e);
		}
		return runShScriptStringVar;
	}

	/**
	 * Returns the first occurence of *.sh file, inside the given
	 * ImplementationArtifact
	 *
	 * @param ia
	 *            an AbstractImplementationArtifact
	 * @return a String containing a relative file path to a *.sh file, if no
	 *         *.sh file inside the given IA is found null
	 */
	private AbstractArtifactReference fetchScriptRefFromIA(AbstractImplementationArtifact ia) {
		List<AbstractArtifactReference> refs = ia.getArtifactRef().getArtifactReferences();
		for (AbstractArtifactReference ref : refs) {
			if (ref.getReference().endsWith(".sh")) {
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
