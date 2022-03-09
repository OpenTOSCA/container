package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto.bpel.handler;

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

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
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
 */
public class BPELConnectsToPluginHandler {

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

    public boolean handle(final BPELPlanContext templateContext) {
        final TRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        Csar csar = templateContext.getCsar();

        // fetch topic
        final Variable topicName = templateContext.getPropertyVariable(ModelUtils.getTarget(relationTemplate, csar), "Name");

        /* fetch ip of mosquitto */
        Variable mosquittoVmIp = null;

        // find infrastructure nodes of mosquitto
        List<TNodeTemplate> infrastructureNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(ModelUtils.getTarget(relationTemplate, csar), infrastructureNodes, csar);

        ModelUtils.getNodesFromNodeToSink(ModelUtils.getTarget(relationTemplate, csar), infrastructureNodes, csar);

        for (final TNodeTemplate infraNode : infrastructureNodes) {

            for (final String ipPropName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
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
        PropertyVariable clientVmIp = null;
        PropertyVariable clientVmUser = null;
        PropertyVariable clientVmPass = null;
        String ubuntuTemplateId = null;

        infrastructureNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(ModelUtils.getSource(relationTemplate, csar), infrastructureNodes, templateContext.getCsar());

        for (final TNodeTemplate infraNode : infrastructureNodes) {

            for (final String ipPropName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, ipPropName) != null) {
                    clientVmIp = templateContext.getPropertyVariable(infraNode, ipPropName);
                    break;
                }
            }

            for (final String loginNameProp : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, loginNameProp) != null) {
                    ubuntuTemplateId = infraNode.getId();
                    clientVmUser = templateContext.getPropertyVariable(infraNode, loginNameProp);
                }
            }

            for (final String loginPwProp : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
                if (templateContext.getPropertyVariable(infraNode, loginPwProp) != null) {
                    ubuntuTemplateId = infraNode.getId();
                    clientVmPass = templateContext.getPropertyVariable(infraNode, loginPwProp);
                }
            }
        }

        /* create skript */
        // the script itself
        final String bashCommand =
            "echo \"topicName = hostName\" > $(find ~ -maxdepth 1 -path \"*.csar\")/mosquitto_connections.txt;";

        // add it as a var to the plan
        final Variable bashCommandVariable =
            templateContext.createGlobalStringVariable("addMosquittoConnection", bashCommand);

        // create bpel query which replaces topicName and hostName with real
        // values
        final String xpathQuery = "replace(replace($" + bashCommandVariable.getVariableName() + ",'topicName',$"
            + topicName.getVariableName() + "),'hostName',$" + mosquittoVmIp.getVariableName() + ")";

        // create bpel assign with created query
        try {
            // create assign and append
            Node assignNode = loadAssignXpathQueryToStringVarFragmentAsNode("assignValuesToAddConnection"
                + System.currentTimeMillis(), xpathQuery, bashCommandVariable.getVariableName());
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
        if (!PluginUtils.isVariableValueEmpty(clientVmUser)) {
            runScriptRequestInputParams.put("VMUserName", clientVmUser);
        } else {
            runScriptRequestInputParams.put("VMUserName", null);
        }

        if (!PluginUtils.isVariableValueEmpty(clientVmPass)) {
            runScriptRequestInputParams.put("VMPrivateKey", clientVmPass);
        } else {
            runScriptRequestInputParams.put("VMPrivateKey", null);
        }

        runScriptRequestInputParams.put("Script", bashCommandVariable);

        this.invokerPlugin.handle(templateContext, ubuntuTemplateId, true, "runScript",
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, runScriptRequestInputParams,
            new HashMap<String, Variable>(), templateContext.getProvisioningPhaseElement());

        return true;
    }

    public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                                                              final String stringVarName) throws IOException,
        SAXException {
        final String templateString =
            loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String variable.
     *
     * @param assignName    the name of the BPEL assign
     * @param xpath2Query   the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                                                                  final String stringVarName) throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        final URL url = getClass().getClassLoader()
            .getResource("mosquittoconnectsto-plugin/assignStringVarWithXpath2Query.xml");
        String template = ResourceAccess.readResourceAsString(url);
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }
}
