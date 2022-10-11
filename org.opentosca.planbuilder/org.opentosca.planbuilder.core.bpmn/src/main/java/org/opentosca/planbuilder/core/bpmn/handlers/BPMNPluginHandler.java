package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.IOException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

//import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.choreography.IPlanBuilderChoreographyPlugin;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNTypePlugin;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * This class controls which plugin is applied. Note that we have similar to BPEL pre, type and postphase plugins. Since
 * we have only one plugin per phase priority is not a problem for now.
 */
@Component
public class BPMNPluginHandler {

    final static Logger LOG = LoggerFactory.getLogger(BPMNPluginHandler.class);
    private final PluginRegistry pluginRegistry;
    private BPMNSubprocessHandler subprocessHandler;

    @Inject
    public BPMNPluginHandler(PluginRegistry pluginRegistry) throws ParserConfigurationException {
        this.pluginRegistry = pluginRegistry;
        this.subprocessHandler = new BPMNSubprocessHandler();
    }

    public boolean handleActivity(final BPMNPlanContext context, final BPMNSubprocess bpmnSubprocess) {
        boolean result = false;
        if (bpmnSubprocess.getActivity().getType() == ActivityType.NOTIFYALLPARTNERS) {
            result = handleNotifyAllPartnersActivity(context);
        }
        return result;
    }

    public boolean handleActivity(final BPMNPlanContext context, final BPMNSubprocess bpmnSubprocess,
                                  final TNodeTemplate nodeTemplate) {
        boolean result = false;
        switch (bpmnSubprocess.getActivity().getType()) {
            case PROVISIONING:
                try {
                    result = this.handleProvisioningActivity(context, bpmnSubprocess, nodeTemplate);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                break;
            case TERMINATION:
            case FREEZE:
                //result = this.handleTerminationActivity(context, nodeTemplate);
                break;
            case DEFROST:
                //result = handleDefrostActivity(context, bpelScope, nodeTemplate);
                break;
            case SENDNODENOTIFY:
                //result = handleSendNotifyActivity(context, nodeTemplate);
                break;
            case RECEIVENODENOTIFY:
                //result = handleReceiveNotifyActivity(context, nodeTemplate);
                break;
            case UPDATE:
                //handleUpdateActivity(context, bpelScope, nodeTemplate);
                break;
            default:
                break;
        }

        return result;
    }

    public boolean handleActivity(final BPMNPlanContext context, final BPMNSubprocess bpmnSubprocess,
                                  final TRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        switch (bpmnSubprocess.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpmnSubprocess, relationshipTemplate);
                break;
            case TERMINATION:
                //result = this.handleTerminationActivity(context, relationshipTemplate);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + bpmnSubprocess.getActivity().getType());
        }
        return result;
    }

    private boolean handleNotifyAllPartnersActivity(final BPMNPlanContext context) {
        boolean result = true;

        for (final IPlanBuilderChoreographyPlugin plugin : this.pluginRegistry.getChoreographyPlugins()) {
            if (plugin.canHandleNotifyPartners(context)) {
                result = plugin.handleNotifyPartners(context);
            }
        }

        return result;
    }

    /**
     * This methods creates based on the plugins the corresponding elements.
     */
    private boolean handleProvisioningActivity(final BPMNPlanContext context, final BPMNSubprocess bpmnSubprocess,
                                               final TNodeTemplate nodeTemplate) throws IOException, SAXException {
        boolean result = true;

        if (bpmnSubprocess.getActivity().getMetadata().get("ignoreProvisioning") == null) {
            LOG.debug("Processing NodeTemplate {} with activityType {}", nodeTemplate.getId(),
                bpmnSubprocess.getActivity().getType());

            // generate code for the pre handling, e.g., upload DAs, create node instance
            for (final IPlanBuilderBPMNPrePhasePlugin prePlugin : this.pluginRegistry.getPreBPMNPlugins()) {
                if (prePlugin.canHandleCreate(context, nodeTemplate)) {
                    LOG.debug("Handling NodeTemplate {} with pre plugin {}", nodeTemplate.getId(), prePlugin.getID());
                    result &= prePlugin.handleCreate(context, nodeTemplate);
                }
            }

            // generate code for the provisioning, e.g., call install, start or create
            // methods
            final IPlanBuilderBPMNTypePlugin plugin = this.pluginRegistry.findBPMNTypePluginForCreation(nodeTemplate, context.getCsar());
            LOG.info("PLUGINTEST NodeTemplate {}", nodeTemplate.getId());
            if (plugin != null) {
                LOG.info("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
                result &= plugin.handleCreate(context, nodeTemplate);
            } else {
                // if it is empty we didn't apply the invoker plugin, so no operation is called -> State is STARTED
                subprocessHandler.createSetStateTaskInsideSubprocess(bpmnSubprocess.getBuildPlan(), bpmnSubprocess);
                LOG.info("Couldn't handle provisioning code generation of NodeTemplate {} with type plugin", nodeTemplate.getId());
            }
        }
        // generate code the post handling, e.g., set properties
        for (final IPlanBuilderBPMNPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostBPMNPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, bpmnSubprocess.getNodeTemplate())) {
                LOG.debug("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpmnSubprocess.getNodeTemplate());
            }
        }
        return result;
    }

    private boolean handleProvisioningActivity(final BPMNPlanContext context, final BPMNSubprocess bpmnSubprocess,
                                               final TRelationshipTemplate relationshipTemplate) {
        boolean result = true;

        if (bpmnSubprocess.getActivity().getMetadata().get("ignoreProvisioning") == null) {

            LOG.debug("Ignoring RelationshipTemplate {} with activityType {}", relationshipTemplate.getId(),
                bpmnSubprocess.getActivity().getType());

            if (this.pluginRegistry.canTypePluginHandleCreate(relationshipTemplate, context.getCsar())) {
                final IPlanBuilderBPMNTypePlugin plugin = this.pluginRegistry
                    .findBPMNTypePluginForCreation(relationshipTemplate, context.getCsar());
                LOG.debug("Handling RelationshipTemplate {} with generic plugin {}", relationshipTemplate.getId(), plugin.getID());
                result &= this.pluginRegistry.handleCreateWithBPMNTypePlugin(context, relationshipTemplate, plugin);
            } else {
                LOG.debug("Couldn't handle provisioning code generation RelationshipTemplate {} with type plugin", relationshipTemplate.getId());
            }
        }

        for (final IPlanBuilderBPMNPrePhasePlugin prePhasePlugin : this.pluginRegistry.getPreBPMNPlugins()) {
            if (prePhasePlugin.canHandleCreate(context, bpmnSubprocess.getRelationshipTemplate())) {
                result &= prePhasePlugin.handleCreate(context, bpmnSubprocess.getRelationshipTemplate());
            }
        }
        return result;
    }
}
