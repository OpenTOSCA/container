package org.opentosca.planbuilder.type.plugin.phpapp.handler;

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

import javax.xml.namespace.QName;
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
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the logic to append BPEL Code which installs a
 * PhpApplication provided as a zip file on an Ubuntu OS
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
	private QName zipArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");
	
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
	
	/**
	 * Adds BPEL code to the given context which installs/unzips a
	 * PhpApplication unto an Ubuntu OS
	 *
	 * @param templateContext a initialized TemplateContext
	 * @param nodeTypeImpl an AbstractNodeTypeImplementation, maybe null
	 * @return true iff appending all BPEL code was successful
	 */
	public boolean handle(TemplatePlanContext templateContext, AbstractNodeTypeImplementation impl) {
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
		
		// find sshUser and sshKey
		Variable sshUserVariable = templateContext.getInternalPropertyVariable("SSHUser");
		if (sshUserVariable == null) {
			sshUserVariable = templateContext.getInternalPropertyVariable("SSHUser", true);
			if (sshUserVariable == null) {
				sshUserVariable = templateContext.getInternalPropertyVariable("SSHUser", false);
			}
		}
		
		// if the variable is null now -> the property isn't set properly
		if (sshUserVariable == null) {
			return false;
		} else {
			if (Utils.isTopoologyTemplatePropertyVariableEmpty(sshUserVariable, templateContext)) {
				// the property isn't set in the topology template -> we set it
				// null here so it will be handled as an external parameter
				sshUserVariable = null;
			}
		}
		
		Variable sshKeyVariable = templateContext.getInternalPropertyVariable("SSHPrivateKey");
		if (sshKeyVariable == null) {
			sshKeyVariable = templateContext.getInternalPropertyVariable("SSHPrivateKey", true);
			if (sshKeyVariable == null) {
				sshKeyVariable = templateContext.getInternalPropertyVariable("SSHPrivateKey", false);
			}
		}
		
		// if variable null now -> the property isn't set according to schema
		if (sshKeyVariable == null) {
			return false;
		} else {
			if (Utils.isTopoologyTemplatePropertyVariableEmpty(sshKeyVariable, templateContext)) {
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
			if (Handler.isUbuntuNodeTypeCompatible(nodeTemplate.getType().getId())) {
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
		
		// add csarEntryPoint to plan input message
		LOG.debug("Adding csarEntryPoint field to plan input");
		templateContext.addStringValueToPlanRequest("csarEntrypoint");
		
		// install unzip
		// used for the invokerPlugin. This map contains mappings from internal
		// variables or data which must be fetched form the input message (value
		// of map == null)
		Map<String, Variable> installPackageRequestInputParams = new HashMap<String, Variable>();
		
		// generate string variable for "unzip" package. This way it's easier to
		// program (no assigns by hand, etc.)
		String unzipPackageVarName = "unzipPackageVar" + templateContext.getIdForNames();
		Variable unzipPackageVar = templateContext.createGlobalStringVariable(unzipPackageVarName, "unzip");
		
		/*
		 * methodName: installPackage params: "hostname", "sshKey", "sshUser",
		 * "packageNames"
		 */
		installPackageRequestInputParams.put("hostname", serverIpPropWrapper);
		// sshKey and user maybe null here, if yes the input will be fetched
		// from the planinput
		installPackageRequestInputParams.put("sshKey", sshKeyVariable);
		installPackageRequestInputParams.put("sshUser", sshUserVariable);
		installPackageRequestInputParams.put("packageNames", unzipPackageVar);
		
		this.invokerPlugin.handle(templateContext, templateId, true, "installPackage", "InterfaceUbuntu", "planCallbackAddress_invoker", installPackageRequestInputParams, new HashMap<String, Variable>());
		
		List<AbstractArtifactReference> refs = null;
		if (impl == null) {
			refs = this.getDeploymentArtifactRefs(templateContext.getNodeTemplate().getDeploymentArtifacts());
		} else {
			Set<AbstractDeploymentArtifact> das = Utils.computeEffectiveDeploymentArtifacts(templateContext.getNodeTemplate(), impl);
			refs = this.getDeploymentArtifactRefs(das);
		}
		
		// add file upload of DA
		if (refs.isEmpty()) {
			Handler.LOG.warn("No usable DA provided for NodeTemplate");
			return false;
		}
		
		LOG.debug("Handling DA references:");
		for (AbstractArtifactReference ref : refs) {
			// upload da ref and unzip it
			this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, sshUserVariable, sshKeyVariable, templateId);
			
			Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
			
			runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
			runScriptRequestInputParams.put("sshKey", sshKeyVariable);
			runScriptRequestInputParams.put("sshUser", sshUserVariable);
			
			String unzipScriptString = "cd /var/www/ && sudo unzip -q -o ~/" + templateContext.getCSARFileName() + "/" + ref.getReference();
			String unzipScriptStringVarName = "unzipZipFile" + templateContext.getIdForNames();
			Variable unzipScriptStringVar = templateContext.createGlobalStringVariable(unzipScriptStringVarName, unzipScriptString);
			
			runScriptRequestInputParams.put("script", unzipScriptStringVar);
			
			this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>());
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
					this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, sshUserVariable, sshKeyVariable, templateId);
					
					Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();
					
					runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
					runScriptRequestInputParams.put("sshKey", sshKeyVariable);
					runScriptRequestInputParams.put("sshUser", sshUserVariable);
					
					Variable runShScriptStringVar = this.appendBPELAssignOperationShScript(templateContext, op, ref);
					
					runScriptRequestInputParams.put("script", runShScriptStringVar);
					
					this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", "planCallbackAddress_invoker", runScriptRequestInputParams, new HashMap<String, Variable>());
					
				}
			}
		}
		
		return true;
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
		runShScriptString += "sh ~/" + templateContext.getCSARFileName() + "/" + reference.getReference();
		
		// generate string var with script
		Variable runShScriptStringVar = templateContext.createGlobalStringVariable(runShScriptStringVarName, runShScriptString);
		
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
	 * Returns a List of ArtifactReferences which point to a ZIP file inside the
	 * the collection of deployment artifacts
	 *
	 * @param das a collection containing DeploymentArtifacts
	 * @return a List of AbstractArtifactReference
	 */
	private List<AbstractArtifactReference> getDeploymentArtifactRefs(Collection<AbstractDeploymentArtifact> das) {
		List<AbstractArtifactReference> result = new ArrayList<AbstractArtifactReference>();
		for (AbstractDeploymentArtifact artifact : das) {
			if (this.isZipArtifact(artifact)) {
				// check reference
				for (AbstractArtifactReference ref : artifact.getArtifactRef().getArtifactReferences()) {
					if (ref.getReference().endsWith(".zip")) {
						result.add(ref);
					}
				}
				
			}
		}
		return result;
	}
	
	private boolean isZipArtifact(AbstractDeploymentArtifact artifact) {
		if (artifact.getArtifactType().toString().equals(this.zipArtifactType.toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns an XPath Query which contructs a valid String, to GET a File from
	 * the openTOSCA API
	 *
	 * @param artifactPath a path inside an ArtifactTemplate
	 * @return a String containing an XPath query
	 */
	public String createXPathQueryForURLRemoteFilePath(String artifactPath) {
		Handler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
		String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/" + artifactPath + "'))";
		return filePath;
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
