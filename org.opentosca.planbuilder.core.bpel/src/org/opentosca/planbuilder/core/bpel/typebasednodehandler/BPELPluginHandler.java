package org.opentosca.planbuilder.core.bpel.typebasednodehandler;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPELPluginHandler {

    final static Logger LOG = LoggerFactory.getLogger(BPELPluginHandler.class);
    protected final PluginRegistry pluginRegistry = new PluginRegistry();

    public boolean handleActivity(BPELPlanContext context, BPELScope bpelScope, AbstractNodeTemplate nodeTemplate,
                                  AbstractActivity activity) {
        boolean result = false;
        switch (activity.getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpelScope, nodeTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, bpelScope, nodeTemplate);
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    public boolean handleActivity(BPELPlanContext context, BPELScope bpelScope,
                                  AbstractRelationshipTemplate relationshipTemplate, AbstractActivity activity) {
        boolean result = false;
        switch (activity.getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpelScope, relationshipTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, bpelScope, relationshipTemplate);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private boolean handleTerminationActivity(BPELPlanContext context, BPELScope bpelScope,
                                              AbstractRelationshipTemplate relationshipTemplate) {
        boolean result = false;

        // generate code for the termination, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(relationshipTemplate);
        if (plugin != null) {
            LOG.info("Handling RelationshipTemplate {} with type plugin {}", relationshipTemplate.getId(),
                     plugin.getID());
            result &= plugin.handleTerminate(context, relationshipTemplate);

        } else {
            LOG.info("Couldn't handle RelationshipTemplate {} with type plugin", relationshipTemplate.getId());

        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(relationshipTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, relationshipTemplate);
            }
        }

        return result;
    }

    private boolean handleTerminationActivity(BPELPlanContext context, BPELScope bpelScope,
                                              AbstractNodeTemplate nodeTemplate) {
        boolean result = false;

        // generate code for the termination, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(nodeTemplate);
        if (plugin != null) {
            LOG.info("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
            result &= plugin.handleTerminate(context, nodeTemplate);

        } else {
            LOG.info("Couldn't handle NodeTemplate {} with type plugin", nodeTemplate.getId());

        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(nodeTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleProvisioningActivity(BPELPlanContext context, BPELScope bpelScope,
                                               AbstractNodeTemplate nodeTemplate) {
        boolean result = true;
        // generate code for the pre handling, e.g., upload DAs
        for (IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
            if (prePlugin.canHandleCreate(nodeTemplate)) {
                LOG.info("Handling NodeTemplate {} with pre plugin {}", nodeTemplate.getId(), prePlugin.getID());
                result &= prePlugin.handleCreate(context, nodeTemplate);
            }
        }

        // generate code for the provisioning, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(nodeTemplate);
        if (plugin != null) {
            LOG.info("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
            result &= plugin.handleCreate(context, nodeTemplate);

        } else {
            LOG.info("Couldn't handle NodeTemplate {} with type plugin", nodeTemplate.getId());

        }

        // generate code the post handling, e.g., update instance data, logs etc.
        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(bpelScope.getNodeTemplate())) {
                LOG.info("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
            }
        }
        return result;
    }

    private boolean handleProvisioningActivity(BPELPlanContext context, BPELScope bpelScope,
                                               AbstractRelationshipTemplate relationshipTemplate) {
        boolean result = true;

        if (this.pluginRegistry.canTypePluginHandleCreate(relationshipTemplate)) {
            IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(relationshipTemplate);
            LOG.info("Handling RelationshipTemplate {} with generic plugin", relationshipTemplate.getId());
            result &= this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);
        } else {
            LOG.debug("Couldn't handle RelationshipTemplate {}", relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(bpelScope.getRelationshipTemplate())) {
                result &= postPhasePlugin.handleCreate(context, bpelScope.getRelationshipTemplate());
            }
        }
        return result;
    }

}
