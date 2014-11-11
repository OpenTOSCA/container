package org.opentosca.planbuilder.type.plugin.mysqldatabase.handler;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.type.plugin.mysqldatabase.Constants;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
	
	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	private Plugin invokerPlugin = new Plugin();
	
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
	
	public boolean handle(TemplatePlanContext templateContext, AbstractNodeTypeImplementation impl) {
		
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
		
		for (AbstractNodeTemplate node : templateContext.getNodeTemplates()) {
			if (Constants.ubuntuNodeTypeOpenTOSCAPlanBuilder.toString().equals(node.getType().getId().toString())) {
				templateId = node.getId();
			}
		}
		
		if (templateId.equals("")) {
			Handler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
			return false;
		}
		
		// add sshUser and sshKey to the input message of the build plan
		LOG.debug("Adding sshUser and sshKey fields to plan input");
		templateContext.addStringValueToPlanRequest("sshUser");
		templateContext.addStringValueToPlanRequest("sshKey");
		
		// adds field into plan input message to give the plan it's own address
		// for the invoker PortType (callback etc.). This is needed as WSO2 BPS
		// 2.x can't give that at runtime (bug)
		LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		// configure db script needs following parameters:
		// Target_RootPassword Source_DBPassword Source_DBUser Source_DBName
		// Source_mySqlPort
		
		// setup inputmappings
		Map<String, Variable> inputMappings = new HashMap<String, Variable>();
		
		inputMappings.put("hostname", serverIpPropWrapper);
		inputMappings.put("sshKey", null);
		inputMappings.put("sshUser", null);
		
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
			
			Variable var = templateContext.getInternalPropertyVariable(paramName);
			if (var == null) {
				var = templateContext.getInternalPropertyVariable(paramName, true);
				if (var == null) {
					var = templateContext.getInternalPropertyVariable(paramName, false);
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
		
		/*
		 * here we upload da's and ia's, afterwards we try to execute the ia's
		 * as operations
		 */
		
		List<AbstractArtifactReference> refs = null;
		if (impl == null) {
			refs = this.getDeploymentArtifactRefs(templateContext.getNodeTemplate().getDeploymentArtifacts());
		} else {
			Set<AbstractDeploymentArtifact> das = Utils.computeEffectiveDeploymentArtifacts(templateContext.getNodeTemplate(), impl);
			Handler.LOG.debug("Found following DA's while computing effective set of DA's");
			for (AbstractDeploymentArtifact da : das) {
				Handler.LOG.debug(da.getName());
			}
			refs = this.getDeploymentArtifactRefs(das);
		}
		
		// add file upload of DA
		if (refs.isEmpty()) {
			Handler.LOG.warn("No usable DA provided for NodeTemplate");
		} else {
			Handler.LOG.debug("Found following ArtifactReferences:");
			for (AbstractArtifactReference ref : refs) {
				Handler.LOG.debug(ref.getReference());
			}
		}
		
		for (AbstractArtifactReference ref : refs) {
			this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, templateId);
		}
		
		if (impl != null) {
			// call the operations of the lifecycleinterface
			LOG.debug("Handling Lifecycle operations:");
			Map<AbstractOperation, AbstractImplementationArtifact> opIaMap = this.getLifecycleOperations(impl);
			for (AbstractOperation op : opIaMap.keySet()) {
				// upload file
				// fetch parameter values
				// execute script on vm
				LOG.debug("Handling operation: " + op.getName());
				for (AbstractArtifactReference ref : opIaMap.get(op).getArtifactRef().getArtifactReferences()) {
					this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, templateId);
					
					Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
					
					runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
					runScriptRequestInputParams.put("sshKey", null);
					runScriptRequestInputParams.put("sshUser", null);
					
					Variable runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, op, ref);
					
					runScriptRequestInputParams.put("script", runShScriptStringVar);
					
					this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>());
					
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Loads configureDB.sh from resources. Script generates empty database on a
	 * mysql server
	 *
	 * @return a String containing a bash script
	 * @throws IOException is thrown when reading file from resources fails
	 */
	private String loadConfigDBSh() throws IOException {
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
	
	/**
	 * Returns a List of ArtifactReferences which point to a ZIP file inside the
	 * the collection of deployment artifacts
	 *
	 * @param das a collection containing DeploymentArtifacts
	 * @return a List of AbstractArtifactReference
	 */
	private List<AbstractArtifactReference> getDeploymentArtifactRefs(Collection<AbstractDeploymentArtifact> das) {
		List<AbstractArtifactReference> result = new ArrayList<AbstractArtifactReference>();
		for (AbstractDeploymentArtifact artifact : das) {
			for (AbstractArtifactReference ref : artifact.getArtifactRef().getArtifactReferences()) {
				result.add(ref);
			}
		}
		return result;
	}
	
	private Variable appendBPELAssignOperationShScript(TemplatePlanContext templateContext, AbstractOperation operation, AbstractArtifactReference reference) {
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
		String runShScriptString = "sudo ";
		String runShScriptStringVarName = "runShFile" + templateContext.getIdForNames();
		String xpathQueryPrefix = "";
		String xpathQuerySuffix = "";
		
		for (AbstractParameter parameter : operation.getInputParameters()) {
			// First compute mappings from operation parameters to
			// property/inputfield
			Variable var = templateContext.getInternalPropertyVariable(parameter.getName());
			if (var == null) {
				var = templateContext.getInternalPropertyVariable(parameter.getName(), true);
				if (var == null) {
					var = templateContext.getInternalPropertyVariable(parameter.getName(), false);
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
				
				xpathQuerySuffix += "$" + templateContext.getPlanRequestMessageName() + ".payload//*[local-name()='" + parameter.getName() + "']/text())";
			} else {
				// param is internal, so just query the bpelvar e.g. $Varname
				xpathQuerySuffix += "$" + var.getName() + ")";
			}
		}
		// add path to script
		runShScriptString += "~/" + templateContext.getCSARFileName() + "/" + reference.getReference();
		
		String chmod = "chmod +x " + "~/" + templateContext.getCSARFileName() + "/" + reference.getReference() + ";";
		
		
		// generate string var with script
		Variable runShScriptStringVar = templateContext.createGlobalStringVariable(runShScriptStringVarName, chmod + runShScriptString);
		
		// Reassign string var with runtime values and replace their
		// placeholders
		try {
			// create xpath query
			String xpathQuery = xpathQueryPrefix + "$" + runShScriptStringVar.getName() + xpathQuerySuffix;
			// create assign and append
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode("assignShCallScriptVar", xpathQuery, runShScriptStringVar.getName());
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
	
	private Map<AbstractOperation, AbstractImplementationArtifact> getLifecycleOperations(AbstractNodeTypeImplementation impl) {
		Map<AbstractOperation, AbstractImplementationArtifact> opIaMap = new HashMap<AbstractOperation, AbstractImplementationArtifact>();
		for (AbstractInterface iface : impl.getNodeType().getInterfaces()) {
			if (iface.getName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
				for (AbstractOperation op : iface.getOperations()) {
					for (AbstractImplementationArtifact ia : impl.getImplementationArtifacts()) {
						if (op.getName().equals(ia.getOperationName()) && ia.getInterfaceName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
							opIaMap.put(op, ia);
						}
					}
				}
			}
		}
		return opIaMap;
	}
	
}
