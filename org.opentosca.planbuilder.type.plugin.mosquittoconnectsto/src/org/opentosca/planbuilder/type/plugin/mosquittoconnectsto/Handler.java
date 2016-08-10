package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
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
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
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
	 * @throws ParserConfigurationException
	 *             is thrown when initializing the DOM Parsers fails
	 */
	public Handler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	}

	public boolean handle(TemplatePlanContext templateContext) {
		AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();

		// fetch topic
		Variable topicName = templateContext.getPropertyVariable(relationTemplate.getTarget(), "Name");

		/* fetch ip of mosquitto */
		Variable mosquittoVmIp = null;

		// find infrastructure nodes of mosquitto
		List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getInfrastructureNodes(relationTemplate.getTarget(), infrastructureNodes);

		for (AbstractNodeTemplate infraNode : infrastructureNodes) {
			// fetch mosquitto ip
			if (templateContext.getPropertyVariable(infraNode,
					Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP) != null) {
				mosquittoVmIp = templateContext.getPropertyVariable(infraNode,
						Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP);
			}
		}

		/* fetch user, key, ip and ubuntuTemplateId of client stack */
		Variable clientVmIp = null;
		Variable clientVmUser = null;
		Variable clientVmPass = null;
		String ubuntuTemplateId = null;

		infrastructureNodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getInfrastructureNodes(relationTemplate.getSource(), infrastructureNodes);

		for (AbstractNodeTemplate infraNode : infrastructureNodes) {

			for (String ipPropName : org.opentosca.model.tosca.conventions.Utils
					.getSupportedVirtualMachineIPPropertyNames()) {
				if (templateContext.getPropertyVariable(infraNode, ipPropName) != null) {
					clientVmIp = templateContext.getPropertyVariable(infraNode, ipPropName);
					break;
				}

			}

			for (String loginNameProp : org.opentosca.model.tosca.conventions.Utils
					.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
				if (templateContext.getPropertyVariable(infraNode, loginNameProp) != null) {
					ubuntuTemplateId = infraNode.getId();
					clientVmUser = templateContext.getPropertyVariable(infraNode, loginNameProp);
				}
			}
			
			for (String loginPwProp : org.opentosca.model.tosca.conventions.Utils
					.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
				if (templateContext.getPropertyVariable(infraNode, loginPwProp) != null) {
					ubuntuTemplateId = infraNode.getId();
					clientVmPass = templateContext.getPropertyVariable(infraNode, loginPwProp);
				}

			}
		}

		/* create skript */
		// the script itself
		String bashCommand = "echo \"topicName = hostName\" > $(find ~ -maxdepth 1 -path \"*.csar\")/mosquitto_connections.txt;";

		// add it as a var to the plan
		Variable bashCommandVariable = templateContext.createGlobalStringVariable("addMosquittoConnection",
				bashCommand);

		// create bpel query which replaces topicName and hostName with real
		// values
		String xpathQuery = "replace(replace($" + bashCommandVariable.getName() + ",'topicName',$" + topicName.getName()
				+ "),'hostName',$" + mosquittoVmIp.getName() + ")";

		// create bpel assign with created query
		try {
			// create assign and append
			Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode(
					"assignValuesToAddConnection" + System.currentTimeMillis(), xpathQuery,
					bashCommandVariable.getName());
			assignNode = templateContext.importNode(assignNode);
			templateContext.getProvisioningPhaseElement().appendChild(assignNode);
		} catch (IOException e) {
			LOG.error("Couldn't load fragment from file", e);
			return false;
		} catch (SAXException e) {
			LOG.error("Couldn't parse fragment to DOM", e);
			return false;
		}

		/* add logic to execute script on client machine */
		Map<String, Variable> runScriptRequestInputParams = new HashMap<String, Variable>();

		runScriptRequestInputParams.put("VMIP", clientVmIp);

		// these two are requested from the input message if they are not set
		if (!Utils.isVariableValueEmpty(clientVmUser, templateContext)) {
			runScriptRequestInputParams.put("VMUserName", clientVmUser);
		} else {
			runScriptRequestInputParams.put("VMUserName", null);
		}

		if (!Utils.isVariableValueEmpty(clientVmPass, templateContext)) {
			runScriptRequestInputParams.put("VMPrivateKey", clientVmPass);
		} else {
			runScriptRequestInputParams.put("VMPrivateKey", null);
		}

		runScriptRequestInputParams.put("Script", bashCommandVariable);

		this.invokerPlugin.handle(templateContext, ubuntuTemplateId, true, "runScript",
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, "planCallbackAddress_invoker",
				runScriptRequestInputParams, new HashMap<String, Variable>(), false);

		return true;
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
