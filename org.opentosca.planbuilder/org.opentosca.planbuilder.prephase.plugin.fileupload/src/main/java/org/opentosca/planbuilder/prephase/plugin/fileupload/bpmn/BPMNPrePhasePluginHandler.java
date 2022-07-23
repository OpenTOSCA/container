package org.opentosca.planbuilder.prephase.plugin.fileupload.bpmn;

import java.util.Collection;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class contains logic to upload files to a linux machine. Those files must be available trough a openTOSCA
 * Container
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPMNPrePhasePluginHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPrePhasePluginHandler.class);

    private final BPMNInvokerPlugin invokerPlugin = new BPMNInvokerPlugin();

    /**
     * Adds necessary BPMN logic through the given context that can upload the given DA unto the given
     * InfrastructureNode
     *
     * @param context           a TemplateContext
     * @param da                the DeploymentArtifact to deploy
     * @param infraNodeTemplate the NodeTemplate which is used as InfrastructureNode
     * @return true iff adding logic was successful
     */
    public boolean handle(final BPMNPlanContext context, final TDeploymentArtifact da,
                          final TNodeTemplate infraNodeTemplate) {
        final Collection<TArtifactReference> refs = ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences();
        return this.handle(context, refs, da.getName(), infraNodeTemplate);
    }

    /**
     * Adds necessary BPMN logic through the given Context, to deploy the given ArtifactReferences unto the specified
     * InfrastructureNode
     *
     * @param templateContext a TemplateContext
     * @param refs            the ArtifactReferences to deploy
     * @param artifactName    the name of the artifact, where the references originate from
     * @param infraTemplate   a NodeTemplate which is a InfrastructureNode to deploy the AbstractReferences on
     * @return true iff adding the logic was successful
     */
    private boolean handle(final BPMNPlanContext templateContext, final Collection<TArtifactReference> refs,
                           final String artifactName, final TNodeTemplate infraTemplate) {

        LOG.debug("Handling DA upload with");
        StringBuilder refsString = new StringBuilder();
        for (final TArtifactReference ref : refs) {
            refsString.append(ref.getReference()).append(", ");
        }
        LOG.debug("Refs:" + refsString.substring(0, refsString.lastIndexOf(",")));
        LOG.debug("ArtifactName: " + artifactName);
        LOG.debug("NodeTemplate: " + infraTemplate.getId() + "(Type: " + infraTemplate.getType().toString()
            + ")");

        // fetch server ip of the vm this artefact will be deployed on

        PropertyVariable serverIpPropWrapper = null;
        for (final String serverIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = templateContext.getPropertyVariable(infraTemplate, serverIpName);
            if (serverIpPropWrapper != null) {
                break;
            }
        }

        if (serverIpPropWrapper == null) {
            BPMNPrePhasePluginHandler.LOG.warn("No Infrastructure Node available with ServerIp property");
            return false;
        }

        // find sshUser and sshKey
        PropertyVariable sshUserVariable = null;
        for (final String vmLoginName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            sshUserVariable = templateContext.getPropertyVariable(infraTemplate, vmLoginName);
            if (sshUserVariable != null) {
                break;
            }
        }

        PropertyVariable sshKeyVariable = null;
        for (final String vmLoginPassword : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = templateContext.getPropertyVariable(infraTemplate, vmLoginPassword);
            if (sshKeyVariable != null) {
                break;
            }
        }

        // adds field into plan input message to give the plan it's own address
        // for the invoker PortType (callback etc.). This is needed as WSO2 BPS
        // 2.x can't give that at runtime (bug)
        LOG.debug("Adding plan callback address field to plan input");
        //templateContext.addStringValueToPlanRequest("planCallbackAddress_invoker");

        // add csarEntryPoint to plan input message
        LOG.debug("Adding csarEntryPoint field to plan input");
        //templateContext.addStringValueToPlanRequest("csarEntrypoint");

        LOG.debug("Handling DA references:");
        //todo element to append is actually never used in bpmn, maybe removable in the future
        // global variables above may need to be added as input parameters too
        for (final TArtifactReference ref : refs) {
            // upload da ref and unzip it
            this.invokerPlugin.handleArtifactReferenceUpload(ref, templateContext, serverIpPropWrapper, sshUserVariable,
                sshKeyVariable, infraTemplate, templateContext.getSubprocessElement().getBpmnSubprocessElement());
        }

        return true;
    }
}
