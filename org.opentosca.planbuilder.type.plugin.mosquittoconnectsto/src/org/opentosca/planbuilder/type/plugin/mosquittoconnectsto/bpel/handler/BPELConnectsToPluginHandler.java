package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.handler;

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
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.core.handler.ConnectsToTypePluginHandler;
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
public class BPELConnectsToPluginHandler implements ConnectsToTypePluginHandler<BPELPlanContext> {

    private final static Logger LOG = LoggerFactory.getLogger(BPELConnectsToPluginHandler.class);
    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPELConnectsToPluginHandler() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    }

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();

        // fetch topic
        final Variable topicName = templateContext.getPropertyVariable(relationTemplate.getTarget(), "Name");

        /* fetch ip of mosquitto */
        Variable mosquittoVmIp = null;

        // find infrastructure nodes of mosquitto
        List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(relationTemplate.getTarget(), infrastructureNodes);

        ModelUtils.getNodesFromNodeToSink(relationTemplate.getTarget(), infrastructureNodes);

        for (final AbstractNodeTemplate infraNode : infrastructureNodes) {

            for (final String ipPropName : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
                // fetch mosquitto ip
                if (templateContext.getPropertyVariable(infraNode, ipPropName) != null) {
                    mosquittoVmIp = templateContext.getPropertyVariable(infraNode, ipPropName);
                    break;
                }

            }

            if (mosquittoVmIp != null) {
                break;
            }
        }

        /* fetch user, key, ip and ubuntuTemplateId of client stack */
        Variable clientVmIp = null;
        Variable clientVmUser = null;
        Variable clientVmPass = null;
        String ubuntuTemplateId = null;

        infrastructureNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(relationTemplate.getSource(), infrastructureNodes);

        for (final AbstractNodeTemplate infraNode : infrastructureNodes) {

            for (final String ipPropName : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, ipPropName) != null) {
                    clientVmIp = templateContext.getPropertyVariable(infraNode, ipPropName);
                    break;
                }

            }

            for (final String loginNameProp : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, loginNameProp) != null) {
                    ubuntuTemplateId = infraNode.getId();
                    clientVmUser = templateContext.getPropertyVariable(infraNode, loginNameProp);
                }
            }

            for (final String loginPwProp : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, loginPwProp) != null) {
                    ubuntuTemplateId = infraNode.getId();
                    clientVmPass = templateContext.getPropertyVariable(infraNode, loginPwProp);
                }

            }
        }

        /* create skript */
        // the script itself
        final String bashCommand = "echo \"topicName = hostName\" > $(find ~ -maxdepth 1 -path \"*.csar\")/mosquitto_connections.txt;";

        // add it as a var to the plan
        final Variable bashCommandVariable = templateContext.createGlobalStringVariable("addMosquittoConnection",
            bashCommand);

        // create bpel query which replaces topicName and hostName with real
        // values
        final String xpathQuery = "replace(replace($" + bashCommandVariable.getName() + ",'topicName',$"
            + topicName.getName() + "),'hostName',$" + mosquittoVmIp.getName() + ")";

        // create bpel assign with created query
        try {
            // create assign and append
            Node assignNode = this.loadAssignXpathQueryToStringVarFragmentAsNode(
                "assignValuesToAddConnection" + System.currentTimeMillis(), xpathQuery, bashCommandVariable.getName());
            assignNode = templateContext.importNode(assignNode);
            templateContext.getProvisioningPhaseElement().appendChild(assignNode);
        } catch (final IOException e) {
            BPELConnectsToPluginHandler.LOG.error("Couldn't load fragment from file", e);
            return false;
        } catch (final SAXException e) {
            BPELConnectsToPluginHandler.LOG.error("Couldn't parse fragment to DOM", e);
            return false;
        }

        /* add logic to execute script on client machine */
        final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();

        runScriptRequestInputParams.put("VMIP", clientVmIp);

        // these two are requested from the input message if they are not set
        if (!BPELPlanContext.isVariableValueEmpty(clientVmUser, templateContext)) {
            runScriptRequestInputParams.put("VMUserName", clientVmUser);
        } else {
            runScriptRequestInputParams.put("VMUserName", null);
        }

        if (!BPELPlanContext.isVariableValueEmpty(clientVmPass, templateContext)) {
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
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param csarEntryXpathQuery the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a DOM Node representing a BPEL assign element
     * @throws IOException is thrown when loading internal bpel fragments fails
     * @throws SAXException is thrown when parsing internal format into DOM fails
     */
    public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                    final String stringVarName)
        throws IOException, SAXException {
        final String templateString = this.loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query,
            stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param xpath2Query the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                    final String stringVarName)
        throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignStringVarWithXpath2Query.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }

}
