package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELTransferArtifactHandler extends BPELInvokeOperationHandler {

    /**
     * Removes trailing slashes
     *
     * @param ref a path
     * @return a String without trailing slashes
     */
    public String fileReferenceToFolder(String ref) {
        LOG.debug("Getting ref to change to folder ref: " + ref);

        final int lastIndexSlash = ref.lastIndexOf("/");
        final int lastIndexDot = ref.lastIndexOf(".");
        if (lastIndexSlash < lastIndexDot) {
            ref = ref.substring(0, lastIndexSlash);
        }
        LOG.debug("Returning ref: " + ref);
        return ref;
    }

    public List<String> getRunScriptParams(final AbstractNodeTemplate nodeTemplate) {
        final List<String> inputParams = new ArrayList<>();

        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)) {
                    for (final AbstractParameter param : op.getInputParameters()) {
                        inputParams.add(param.getName());
                    }
                }
            }
        }

        return inputParams;
    }

    public List<String> getTransferFileParams(final AbstractNodeTemplate nodeTemplate) {
        final List<String> inputParams = new ArrayList<>();

        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
                    for (final AbstractParameter param : op.getInputParameters()) {
                        inputParams.add(param.getName());
                    }
                }
            }
        }

        return inputParams;
    }

    public boolean handleArtifactReferenceUpload(final AbstractArtifactReference ref,
                                                 final BPELPlanContext templateContext, final PropertyVariable serverIp,
                                                 final PropertyVariable sshUser, final PropertyVariable sshKey,
                                                 final AbstractNodeTemplate infraTemplate,
                                                 Element elementToAppendTo) throws Exception {
        BPELInvokeOperationHandler.LOG.debug("Handling DA " + ref.getReference());

        if (Objects.isNull(serverIp)) {
            LOG.error("Unable to upload artifact with server IP equal to null.");
            return false;
        }

        /*
         * Contruct all needed data (paths, url, scripts)
         */
        // TODO /home/ec2-user/ or ~ is a huge assumption
        // the path to the file on the ubuntu vm being uploaded
        final String ubuntuFilePath = "~/" + templateContext.getCSARFileName() + "/" + ref.getReference();
        final String ubuntuFilePathVarName = "ubuntuFilePathVar" + templateContext.getIdForNames();
        final Variable ubuntuFilePathVar =
            templateContext.createGlobalStringVariable(ubuntuFilePathVarName, ubuntuFilePath);
        // the folder which has to be created on the ubuntu vm
        final String ubuntuFolderPathScript = "sleep 1 && mkdir -p " + fileReferenceToFolder(ubuntuFilePath);
        final String containerAPIAbsoluteURIXPathQuery =
            this.bpelFrags.createXPathQueryForURLRemoteFilePath(ref.getReference());
        final String containerAPIAbsoluteURIVarName = "containerApiFileURL" + templateContext.getIdForNames();
        /*
         * create a string variable with a complete URL to the file we want to upload
         */

        final Variable containerAPIAbsoluteURIVar =
            templateContext.createGlobalStringVariable(containerAPIAbsoluteURIVarName, "");

        try {
            Node assignNode =
                loadAssignXpathQueryToStringVarFragmentAsNode("assign" + templateContext.getIdForNames(),
                    containerAPIAbsoluteURIXPathQuery,
                    containerAPIAbsoluteURIVar.getVariableName());
            assignNode = templateContext.importNode(assignNode);

            elementToAppendTo.appendChild(assignNode);
        } catch (final IOException e) {
            BPELInvokeOperationHandler.LOG.error("Couldn't read internal file", e);
            return false;
        } catch (final SAXException e) {
            BPELInvokeOperationHandler.LOG.error("Couldn't parse internal xml file");
            return false;
        }

        // create the folder the file must be uploaded into and upload the file afterwards
        final String mkdirScriptVarName = "mkdirScript" + templateContext.getIdForNames();
        final Variable mkdirScriptVar =
            templateContext.createGlobalStringVariable(mkdirScriptVarName, ubuntuFolderPathScript);
        final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();
        runScriptRequestInputParams.put("Script", mkdirScriptVar);
        final List<String> runScriptInputParams = getRunScriptParams(infraTemplate);

        final Map<String, Variable> transferFileRequestInputParams = new HashMap<>();
        transferFileRequestInputParams.put("TargetAbsolutePath", ubuntuFilePathVar);
        transferFileRequestInputParams.put("SourceURLorLocalPath", containerAPIAbsoluteURIVar);
        final List<String> transferFileInputParams = getTransferFileParams(infraTemplate);

        switch (serverIp.getPropertyName()) {
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CONTAINERIP:

                // create the folder
                if (runScriptInputParams.contains(serverIp.getPropertyName())) {
                    runScriptRequestInputParams.put(serverIp.getPropertyName(), serverIp);
                }
                this.handleInvokeOperation(templateContext, infraTemplate.getId(), true, "runScript", "ContainerManagementInterface",
                    runScriptRequestInputParams, new HashMap<String, Variable>(), elementToAppendTo);

                // transfer the file
                if (transferFileInputParams.contains(serverIp.getPropertyName())) {
                    transferFileRequestInputParams.put(serverIp.getPropertyName(), serverIp);
                }
                this.handleInvokeOperation(templateContext, infraTemplate.getId(), true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE,
                    "ContainerManagementInterface", transferFileRequestInputParams,
                    new HashMap<String, Variable>(), elementToAppendTo);
                break;
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
                // create the folder
                if (runScriptInputParams.contains(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
                    runScriptRequestInputParams.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);
                }
                if (sshUser != null && runScriptInputParams.contains("VMUserName")) {
                    runScriptRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && runScriptInputParams.contains("VMPrivateKey")) {
                    runScriptRequestInputParams.put("VMPrivateKey", sshKey);
                }
                this.handleInvokeOperation(templateContext, infraTemplate.getId(), true, "runScript",
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, runScriptRequestInputParams,
                    new HashMap<String, Variable>(), elementToAppendTo);

                // transfer the file
                if (transferFileInputParams.contains(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
                    transferFileRequestInputParams.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);
                }
                if (sshUser != null && transferFileInputParams.contains("VMUserName")) {
                    transferFileRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && transferFileInputParams.contains("VMPrivateKey")) {
                    transferFileRequestInputParams.put("VMPrivateKey", sshKey);
                }
                this.handleInvokeOperation(templateContext, infraTemplate.getId(), true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, transferFileRequestInputParams,
                    new HashMap<String, Variable>(), elementToAppendTo);
                break;
            default:
                return false;
        }

        return true;
    }
}
