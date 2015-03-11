package org.opentosca.planbuilder.type.plugin.mysqldatabase.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.commons.PluginUtils;
import org.opentosca.planbuilder.plugins.commons.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.utils.Utils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class LifecycleHandler extends AbstractHandler {
	
	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers fails
	 */
	public LifecycleHandler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
	}
	
	/**
	 * Uploads configureDB.sh (see META-INF/) and executes it on found VM.
	 * Uploads all provided DeploymentArtifacts and ImplementationArtifacts
	 * (plus executing the scripts).
	 * 
	 * @param templateContext the templateContext of the nodeTemplate to
	 *            provision
	 * @param impl the selected noteTypeImplementation
	 * @return true iff adding appropiate bpel constructs was successful
	 */
	public boolean handle(TemplatePlanContext templateContext, AbstractNodeTypeImplementation impl) {
		
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
			AbstractHandler.LOG.warn("No Infrastructure Node available with ServerIp property");
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
		
		// find the ubuntu node and its nodeTemplateId
		String templateId = "";
		
		for (AbstractNodeTemplate node : templateContext.getNodeTemplates()) {
			if (PluginUtils.isSupportedUbuntuVMNodeType(node.getType().getId())) {
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
		LOG.debug("Adding plan callback address field to plan input");
		templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");
		
		if (!this.executeConfigureDBSh(serverIpPropWrapper, sshKeyVariable, sshUserVariable, templateContext, templateId)) {
			return false;
		}
		
		/*
		 * here we upload da's and ia's, afterwards we try to execute the ia's
		 * as operations
		 */
		
		/*
		 * upload DA's
		 */
		List<AbstractArtifactReference> refs = null;
		if (impl == null) {
			refs = this.getDeploymentArtifactRefs(templateContext.getNodeTemplate().getDeploymentArtifacts());
		} else {
			Set<AbstractDeploymentArtifact> das = Utils.computeEffectiveDeploymentArtifacts(templateContext.getNodeTemplate(), impl);
			AbstractHandler.LOG.debug("Found following DA's while computing effective set of DA's");
			for (AbstractDeploymentArtifact da : das) {
				AbstractHandler.LOG.debug(da.getName());
			}
			refs = this.getDeploymentArtifactRefs(das);
		}
		
		// add file upload of DA
		if (refs.isEmpty()) {
			AbstractHandler.LOG.warn("No usable DA provided for NodeTemplate");
		} else {
			AbstractHandler.LOG.debug("Found following ArtifactReferences:");
			for (AbstractArtifactReference ref : refs) {
				AbstractHandler.LOG.debug(ref.getReference());
			}
		}
		
		for (AbstractArtifactReference ref : refs) {
			this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, sshUserVariable, sshKeyVariable, templateId);
		}
		
		/*
		 * upload IA and execute sh script with parameters/property values as
		 * env variables to the script
		 */
		if (impl != null) {
			
			Map<String, Variable> inputMappings = new HashMap<String, Variable>();
			
			inputMappings.put("hostname", serverIpPropWrapper);
			inputMappings.put("sshKey", sshKeyVariable);
			inputMappings.put("sshUser", sshUserVariable);
			
			// call the operations of the lifecycleinterface
			LOG.debug("Handling Lifecycle operations:");
			Map<AbstractOperation, AbstractImplementationArtifact> opIaMap = this.getLifecycleOperations(impl);
			for (AbstractOperation op : opIaMap.keySet()) {
				// upload file
				// fetch parameter values
				// execute script on vm
				LOG.debug("Handling operation: " + op.getName());
				for (AbstractArtifactReference ref : opIaMap.get(op).getArtifactRef().getArtifactReferences()) {
					this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, sshUserVariable, sshKeyVariable, templateId);
					
					Variable runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, op, ref);
					
					// reusing already set mappings
					inputMappings.put("script", runShScriptStringVar);
					
					this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", inputMappings, new HashMap<String, Variable>(), false);
					
				}
			}
		}
		
		return true;
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
			LOG.error("Couldn't load fragment from file", e);
		} catch (SAXException e) {
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
