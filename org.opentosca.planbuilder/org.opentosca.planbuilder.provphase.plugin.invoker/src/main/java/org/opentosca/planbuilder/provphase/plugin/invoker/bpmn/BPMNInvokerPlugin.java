package org.opentosca.planbuilder.provphase.plugin.invoker.bpmn;

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationBPMNPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseParamOperationBPMNPlugin;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers.BPMNInvokerPluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class BPMNInvokerPlugin implements IPlanBuilderProvPhaseOperationBPMNPlugin<BPMNPlanContext>,
    IPlanBuilderProvPhaseParamOperationBPMNPlugin<BPMNPlanContext> {

    private static final Logger LOG = LoggerFactory.getLogger(org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin.class);
    private static final String PLUGIN_ID = "OpenTOSCA ProvPhase BPMNInvokerPlugin";

    private final BPMNInvokerPluginHandler handler = new BPMNInvokerPluginHandler();

    /**
     * Method for adding a single call to the invoker with the given context and specified nodeTemplate
     *
     * @param context                     the TemplateContext of the Template to call the Operation on
     * @param templateId                  the id of the Template the operation belongs to
     * @param isNodeTemplate              whether the template is a NodeTemplate or RelationshipTemplate
     * @param operationName               the Operation to call on the Template
     * @param interfaceName               the name of the interface the operation belongs to
     * @param internalExternalPropsInput  Mappings from TOSCA Input Parameters to Invoker Parameters
     * @param internalExternalPropsOutput Mappings from TOSCA Output Parameters to Invoker Parameters
     * @return true iff adding logic for Invoker call was successful
     */
    public boolean handle(final BPMNPlanContext context, final TNodeTemplate templateId, final boolean isNodeTemplate,
                          final String operationName, final String interfaceName,
                          final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput, Element elementToAppendTo) {
        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operationName, interfaceName,
                internalExternalPropsInput, internalExternalPropsOutput, elementToAppendTo);
        } catch (final Exception e) {
            LOG.error("Couldn't append logic to provisioning phase of Template: {}",
                context.getNodeTemplate() != null
                    ? context.getNodeTemplate().getId()
                    : context.getRelationshipTemplate().getId(),
                e);
            return false;
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean canHandle(final QName operationArtifactType) {
        return true;
    }

    @Override
    public boolean handle(BPMNPlanContext context, TOperation operation, TImplementationArtifact ia) {
        return false;
    }

    @Override
    public String getID() {
        return PLUGIN_ID;
    }

    @Override
    public boolean handle(BPMNPlanContext context, TOperation operation, TImplementationArtifact ia, Map<TParameter, Variable> param2propertyMapping) {
        return false;
    }

    @Override
    public boolean handle(BPMNPlanContext context, TOperation operation, TImplementationArtifact ia, Map<TParameter, Variable> param2propertyMapping, Element elementToAppendTo) {
        return false;
    }

    @Override
    public boolean handle(BPMNPlanContext context, TOperation operation, TImplementationArtifact ia, Map<TParameter, Variable> param2propertyMapping, Map<TParameter, Variable> param2PropertyOutputMapping) {
        return false;
    }

    @Override
    public boolean handle(BPMNPlanContext context, TOperation operation, TImplementationArtifact ia, Map<TParameter, Variable> param2propertyMapping, Map<TParameter, Variable> param2PropertyOutputMapping, Element elementToAppendTo) {
        return false;
    }

    /**
     * Adds bpel code to the given templateContext, which uploads the given ArtifactReference ref to the
     * given server ip. The destination of the artifact will be a replica of the given csar on the home
     * folder of the selected user. The file must be available from the openTosca container api.
     *
     * @param ref the reference to upload
     * @param templateContext the templateContext to use
     * @param serverIp the ip to upload the file to
     * @param sshUser a variable containing the sshUser value, if null the user will be requested from
     *        the planInput
     * @param sshKey a variable containing the sshKey value, if null the key will be requested from the
     *        planInput
     * @param infraTemplate the templateId the serverIp belongs to
     * @return true iff appending all bpel code was successful
     */
    public boolean handleArtifactReferenceUpload(final TArtifactReference ref,
                                                 final BPMNPlanContext templateContext, final PropertyVariable serverIp,
                                                 final PropertyVariable sshUser, final PropertyVariable sshKey,
                                                 final TNodeTemplate infraTemplate, Element elementToAppendTo) {
        try {
            return this.handler.handleArtifactReferenceUpload(ref, templateContext, serverIp, sshUser, sshKey,
                infraTemplate, elementToAppendTo);
        } catch (final Exception e) {
            LOG.error("Couldn't load internal files", e);
            return false;
        }
    }
}

