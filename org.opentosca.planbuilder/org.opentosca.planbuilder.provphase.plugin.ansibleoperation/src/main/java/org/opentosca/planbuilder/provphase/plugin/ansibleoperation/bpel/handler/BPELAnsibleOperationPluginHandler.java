package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.apache.commons.io.FilenameUtils;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Properties;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes Ansible Playbooks on remote machine. The class
 * assumes that the playbook that must be called are already uploaded to the appropriate path. For example by the
 * ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 */
public class BPELAnsibleOperationPluginHandler {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BPELAnsibleOperationPluginHandler.class);

    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    private final DocumentBuilderFactory docFactory;

    public BPELAnsibleOperationPluginHandler() {

        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
    }

    private Variable appendBPELAssignOperationShScript(final BPELPlanContext templateContext,
                                                       final TArtifactReference reference) {

        final String runShScriptStringVarName = "runShFile" + templateContext.getIdForNames();

        // install ansible
        String runShScriptString =
            "sudo apt-add-repository -y ppa:ansible/ansible && sudo apt-get update && sudo apt-get install -y ansible";

        // install unzip
        runShScriptString += " && sudo apt-get install unzip";

        final String ansibleZipPath = templateContext.getCSARFileName() + "/" + reference.getReference();
        final String ansibleZipFileName = FilenameUtils.getName(ansibleZipPath);
        final String ansibleZipFolderName = FilenameUtils.getBaseName(ansibleZipFileName);
        final String ansibleZipParentPath = FilenameUtils.getFullPathNoEndSeparator(ansibleZipPath);

        // go into directory of the ansible zip
        runShScriptString += " && cd " + ansibleZipParentPath;

        // unzip
        runShScriptString += " && unzip " + ansibleZipFileName;

        final String playbookPath = getAnsiblePlaybookFilePath(templateContext);

        if (playbookPath == null) {

            LOG.error("No specified Playbook found in the corresponding ArtifactTemplate!");
        } else {

            LOG.debug("Found Playbook: {}", playbookPath);

            final String completePlaybookPath =
                ansibleZipFolderName + "/" + FilenameUtils.separatorsToUnix(playbookPath);
            final String playbookFolder = FilenameUtils.getFullPathNoEndSeparator(completePlaybookPath);
            final String playbookFile = FilenameUtils.getName(completePlaybookPath);

            // go into the unzipped directory
            runShScriptString += " && cd " + playbookFolder;

            // execute ansible playbook
            runShScriptString += " && ansible-playbook " + playbookFile;
        }

        final Variable runShScriptStringVar =
            templateContext.createGlobalStringVariable(runShScriptStringVarName, runShScriptString);

        return runShScriptStringVar;
    }

    /**
     * Append logic for executing a script on a remote machine with the invoker plugin
     *
     * @param templateContext      the context with a bpel templateBuildPlan
     * @param templateId           the id of the template inside the context
     * @param runShScriptStringVar the bpel variable containing the script call
     * @param sshUserVariable      the user name for the remote machine as a bpel variable
     * @param sshKeyVariable       the pass for the remote machine as a bpel variable
     * @param serverIpPropWrapper  the ip of the remote machine as a bpel variable
     * @return true if appending the bpel logic was successful else false
     */
    private boolean appendExecuteScript(final BPELPlanContext templateContext, final String templateId,
                                        final Variable runShScriptStringVar, final Variable sshUserVariable,
                                        final Variable sshKeyVariable, final Variable serverIpPropWrapper) {

        final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();
        // dirty check if we use old style properties
        final String cleanPropName =
            serverIpPropWrapper.getVariableName().substring(serverIpPropWrapper.getVariableName().lastIndexOf("_") + 1);
        switch (cleanPropName) {
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
                runScriptRequestInputParams.put("hostname", serverIpPropWrapper);
                runScriptRequestInputParams.put("sshKey", sshKeyVariable);
                runScriptRequestInputParams.put("sshUser", sshUserVariable);
                runScriptRequestInputParams.put("script", runShScriptStringVar);
                this.invokerPlugin.handle(templateContext, templateId, true, "runScript", "InterfaceUbuntu", runScriptRequestInputParams,
                    new HashMap<String, Variable>(), templateContext.getProvisioningPhaseElement());

                break;
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
                runScriptRequestInputParams.put("VMIP", serverIpPropWrapper);
                runScriptRequestInputParams.put("VMPrivateKey", sshKeyVariable);
                runScriptRequestInputParams.put("VMUserName", sshUserVariable);
                runScriptRequestInputParams.put("Script", runShScriptStringVar);
                this.invokerPlugin.handle(templateContext, templateId, true, "runScript",
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, runScriptRequestInputParams,
                    new HashMap<String, Variable>(), templateContext.getProvisioningPhaseElement());
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Returns the first occurrence of *.zip file, inside the given ImplementationArtifact
     *
     * @param ia an TImplementationArtifact
     * @return a String containing a relative file path to a *.zip file, if no *.zip file inside the given IA is found
     * null
     */
    private TArtifactReference fetchAnsiblePlaybookRefFromIA(final TImplementationArtifact ia, Csar csar) {
        final Collection<TArtifactReference> refs = ModelUtils.findArtifactTemplate(ia.getArtifactRef(), csar).getArtifactReferences();
        for (final TArtifactReference ref : refs) {
            if (ref.getReference().endsWith(".zip")) {
                return ref;
            }
        }
        return null;
    }

    private TNodeTemplate findInfrastructureNode(final List<TNodeTemplate> nodes) {
        for (final TNodeTemplate nodeTemplate : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType())) {
                return nodeTemplate;
            }
        }
        return null;
    }

    /**
     * Searches for the Playbook Mapping in the ArtifactTemplate
     *
     * @return Path to the specified Ansible Playbook within the .zip
     */
    private String getAnsiblePlaybookFilePath(final BPELPlanContext templateContext) {

        final Collection<TNodeTypeImplementation> abstractNodeTypeImpls =
            ModelUtils.findNodeTypeImplementation(templateContext.getNodeTemplate(), templateContext.getCsar());

        for (final TNodeTypeImplementation abstractNodeTypeImpl : abstractNodeTypeImpls) {
            final Collection<TImplementationArtifact> abstractIAs = abstractNodeTypeImpl.getImplementationArtifacts();
            for (final TImplementationArtifact abstractIA : abstractIAs) {
                final String value =

                    ModelUtils.asMap(ModelUtils.findArtifactTemplate(abstractIA.getArtifactRef(), templateContext.getCsar()).getProperties()).get("Playbook");
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Adds logic to the BuildPlan to call a Ansible Playbook on a remote machine
     *
     * @param templateContext the BPELPlanContext where the logical provisioning operation is called
     * @param operation       the operation to call
     * @param ia              the ia that implements the operation
     * @return true iff adding BPEL Fragment was successful
     */
    public boolean handle(final BPELPlanContext templateContext, final TOperation operation,
                          final TImplementationArtifact ia) {

        LOG.debug("Handling Ansible Playbook IA operation: " + operation.getName());
        final TArtifactReference ansibleRef = fetchAnsiblePlaybookRefFromIA(ia, templateContext.getCsar());
        if (ansibleRef == null) {
            return false;
        }
        LOG.debug("Ref: " + ansibleRef.getReference());

        // calculate relevant nodeTemplates for this operation call (the node
        // itself and infraNodes)
        final List<TNodeTemplate> nodes = templateContext.getInfrastructureNodes();

        // add the template itself
        nodes.add(templateContext.getNodeTemplate());

        // find the ubuntu node and its nodeTemplateId
        final TNodeTemplate infrastructureNodeTemplate = findInfrastructureNode(nodes);

        if (infrastructureNodeTemplate == null) {
            BPELAnsibleOperationPluginHandler.LOG.warn("Couldn't determine NodeTemplateId of Ubuntu Node");
            return false;
        }

        /*
         * fetch relevant variables/properties
         */
        if (templateContext.getNodeTemplate() == null) {
            BPELAnsibleOperationPluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }

        // fetch server ip of the vm this apache http php module will be
        // installed on
        PropertyVariable serverIpPropWrapper = null;
        for (final String serverIp : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = templateContext.getPropertyVariable(infrastructureNodeTemplate, serverIp);
            if (serverIpPropWrapper != null) {
                break;
            }
        }

        if (serverIpPropWrapper == null) {
            BPELAnsibleOperationPluginHandler.LOG.warn("No Infrastructure Node available with ServerIp property");
            return false;
        }

        // find sshUser and sshKey
        PropertyVariable sshUserVariable = null;
        for (final String vmUserName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            sshUserVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserName);
            if (sshUserVariable != null) {
                break;
            }
        }

        // if the variable is null now -> the property isn't set properly
        if (sshUserVariable == null) {
            return false;
        } else {
            if (sshUserVariable.getContent() == null || sshUserVariable.getContent().isEmpty()) {
                // the property isn't set in the topology template -> we set it
                // null here so it will be handled as an external parameter
                sshUserVariable = null;
            }
        }
        PropertyVariable sshKeyVariable = null;
        for (final String vmUserPassword : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserPassword);
            if (sshKeyVariable != null) {
                break;
            }
        }

        // if variable null now -> the property isn't set according to schema
        if (sshKeyVariable == null) {
            return false;
        } else {
            if (sshKeyVariable.getContent() == null || sshKeyVariable.getContent().isEmpty()) {
                // see sshUserVariable..
                sshKeyVariable = null;
            }
        }
        // add sshUser and sshKey to the input message of the build plan, if
        // needed
        if (sshUserVariable == null) {
            // dirty check if we use old style properties
            final String cleanPropName =
                serverIpPropWrapper.getVariableName()
                    .substring(serverIpPropWrapper.getVariableName().lastIndexOf("_") + 1);
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
            final String cleanPropName =
                serverIpPropWrapper.getVariableName()
                    .substring(serverIpPropWrapper.getVariableName().lastIndexOf("_") + 1);
            switch (cleanPropName) {
                case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP:
                    LOG.debug("Adding sshUser field to plan input");
                    templateContext.addStringValueToPlanRequest("sshKey");
                    break;
                case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
                    LOG.debug("Adding sshUser field to plan input");
                    templateContext.addStringValueToPlanRequest(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD);
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

        final Variable runShScriptStringVar =
            this.appendBPELAssignOperationShScript(templateContext, ansibleRef);

        return appendExecuteScript(templateContext, infrastructureNodeTemplate.getId(), runShScriptStringVar,
            sshUserVariable, sshKeyVariable, serverIpPropWrapper);
    }

    public boolean handle(final BPELPlanContext templateContext, final TOperation operation,
                          final TImplementationArtifact ia,
                          final Map<TParameter, Variable> param2propertyMapping) {

        if (operation.getInputParameters().size() != param2propertyMapping.size()) {
            return false;
        }

        final TNodeTemplate infrastructureNodeTemplate =
            findInfrastructureNode(templateContext.getInfrastructureNodes());
        if (infrastructureNodeTemplate == null) {
            return false;
        }

        Variable runShScriptStringVar = null;
        final TArtifactReference scriptRef = fetchAnsiblePlaybookRefFromIA(ia, templateContext.getCsar());
        if (scriptRef == null) {
            return false;
        }

        Variable ipStringVariable = null;
        for (final String serverIp : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            ipStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, serverIp);
            if (ipStringVariable != null) {
                break;
            }
        }

        Variable userStringVariable = null;
        for (final String vmUserName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            userStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserName);
            if (userStringVariable != null) {
                break;
            }
        }

        Variable passwdStringVariable = null;
        for (final String vmUserPassword : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            passwdStringVariable = templateContext.getPropertyVariable(infrastructureNodeTemplate, vmUserPassword);
            if (passwdStringVariable != null) {
                break;
            }
        }

        if (isNull(runShScriptStringVar, ipStringVariable, userStringVariable, passwdStringVariable)) {
            // if either of the variables is null -> abort
            return false;
        }

        return appendExecuteScript(templateContext, infrastructureNodeTemplate.getId(), runShScriptStringVar,
            userStringVariable, passwdStringVariable, ipStringVariable);
    }

    private boolean isNull(final Variable... vars) {
        for (final Variable var : vars) {
            if (var == null) {
                return true;
            }
        }
        return false;
    }
}
