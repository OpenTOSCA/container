package org.opentosca.planbuilder.core.bpel.typebasednodehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.choreography.IPlanBuilderChoreographyPlugin;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BPELPluginHandler {

    final static Logger LOG = LoggerFactory.getLogger(BPELPluginHandler.class);
    private final PluginRegistry pluginRegistry;

    @Inject
    public BPELPluginHandler(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public boolean handleActivity(final BPELPlanContext context, final BPELScope bpelScope) {
        boolean result = false;
        if (bpelScope.getActivity().getType() == ActivityType.NOTIFYALLPARTNERS) {
            result = handleNotifyAllPartnersActivity(context);
        }
        return result;
    }

    public boolean handleActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                  final TNodeTemplate nodeTemplate) {
        boolean result = false;
        switch (bpelScope.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpelScope, nodeTemplate);
                break;
            case TERMINATION:
            case FREEZE:
                result = this.handleTerminationActivity(context, nodeTemplate);
                break;
            case DEFROST:
                result = handleDefrostActivity(context, bpelScope, nodeTemplate);
                break;
            case SENDNODENOTIFY:
                result = handleSendNotifyActivity(context, nodeTemplate);
                break;
            case RECEIVENODENOTIFY:
                result = handleReceiveNotifyActivity(context, nodeTemplate);
                break;
            case UPDATE:
                handleUpdateActivity(context, bpelScope, nodeTemplate);
        }

        return result;
    }

    public boolean handleActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                  final TRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        switch (bpelScope.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpelScope, relationshipTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, relationshipTemplate);
                break;
        }
        return result;
    }

    private boolean handleNotifyAllPartnersActivity(final BPELPlanContext context) {
        boolean result = true;

        for (final IPlanBuilderChoreographyPlugin plugin : this.pluginRegistry.getChoreographyPlugins()) {
            if (plugin.canHandleNotifyPartners(context)) {
                result = plugin.handleNotifyPartners(context);
            }
        }

        return result;
    }

    private boolean handleSendNotifyActivity(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        boolean result = true;

        for (final IPlanBuilderChoreographyPlugin plugin : this.pluginRegistry.getChoreographyPlugins()) {
            if (plugin.canHandleSendNotify(context)) {
                result = plugin.handleSendNotify(context);
            }
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, nodeTemplate)) {
                result &= postPhasePlugin.handleCreate(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleReceiveNotifyActivity(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        boolean result = true;

        for (final IPlanBuilderChoreographyPlugin plugin : this.pluginRegistry.getChoreographyPlugins()) {
            if (plugin.canHandleReceiveNotify(context)) {
                result = plugin.handleReceiveNotify(context);
            }
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, nodeTemplate)) {
                result &= postPhasePlugin.handleCreate(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleTerminationActivity(final BPELPlanContext context,
                                              final TRelationshipTemplate relationshipTemplate) {
        boolean result = true;
        // generate code for the termination, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(relationshipTemplate, context.getCsar());
        if (plugin != null) {
            LOG.debug("Handling RelationshipTemplate {} with type plugin {}", relationshipTemplate.getId(),
                plugin.getID());
            result &= plugin.handleTerminate(context, relationshipTemplate);
        } else {
            LOG.debug("Couldn't handle termination code generation RelationshipTemplate {} with type plugin", relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(context, relationshipTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, relationshipTemplate);
            }
        }

        return result;
    }

    private boolean handleTerminationActivity(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        boolean result = true;

        // generate code for the termination, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(nodeTemplate, context.getCsar());
        if (plugin != null) {
            LOG.debug("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
            result &= plugin.handleTerminate(context, nodeTemplate);
        } else {
            LOG.debug("Couldn't handle termination code generation of NodeTemplate {} with type plugin", nodeTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(context, nodeTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleProvisioningActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                               final TNodeTemplate nodeTemplate) {
        boolean result = true;

        if (bpelScope.getActivity().getMetadata().get("ignoreProvisioning") == null) {
            LOG.debug("Processing NodeTemplate {} with activityType {}", nodeTemplate.getId(),
                bpelScope.getActivity().getType());

            // generate code for the pre handling, e.g., upload DAs
            for (final IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
                if (prePlugin.canHandleCreate(context, nodeTemplate)) {
                    LOG.debug("Handling NodeTemplate {} with pre plugin {}", nodeTemplate.getId(), prePlugin.getID());
                    result &= prePlugin.handleCreate(context, nodeTemplate);
                }
            }

            // generate code for the provisioning, e.g., call install, start or create
            // methods
            final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(nodeTemplate, context.getCsar());
            if (plugin != null) {
                LOG.debug("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
                result &= plugin.handleCreate(context, nodeTemplate);
            } else {
                LOG.debug("Couldn't handle provisioning code generation of NodeTemplate {} with type plugin", nodeTemplate.getId());
            }
        }
        // generate code the post handling, e.g., update instance data, logs etc.
        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getNodeTemplate())) {
                LOG.debug("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
            }
        }
        return result;
    }

    private boolean handleProvisioningActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                               final TRelationshipTemplate relationshipTemplate) {
        boolean result = true;

        if (bpelScope.getActivity().getMetadata().get("ignoreProvisioning") == null) {

            LOG.debug("Ignoring RelationshipTemplate {} with activityType {}", relationshipTemplate.getId(),
                bpelScope.getActivity().getType());

            if (this.pluginRegistry.canTypePluginHandleCreate(relationshipTemplate, context.getCsar())) {
                final IPlanBuilderTypePlugin plugin = this.pluginRegistry
                    .findTypePluginForCreation(relationshipTemplate, context.getCsar());
                LOG.debug("Handling RelationshipTemplate {} with generic plugin", relationshipTemplate.getId());
                result &= this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);
            } else {
                LOG.debug("Couldn't handle provisioning code generation RelationshipTemplate {} with type plugin", relationshipTemplate.getId());
            }
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getRelationshipTemplate())) {
                result &= postPhasePlugin.handleCreate(context, bpelScope.getRelationshipTemplate());
            }
        }
        return result;
    }

    private boolean handleUpdateActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                         final TNodeTemplate nodeTemplate) {
        boolean result = true;

        // generate code for the provisioning, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForUpdate(nodeTemplate, context.getCsar());
        if (plugin != null) {
            LOG.debug("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
            result &= plugin.handleUpdate(context, nodeTemplate);
        } else {
            LOG.debug("Couldn't handle update code generation NodeTemplate {} with type plugin", nodeTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleUpgrade(context, nodeTemplate)) {
                result &= postPhasePlugin.handleUpgrade(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleDefrostActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                          final TNodeTemplate nodeTemplate) {
        boolean result = true;

        final Map<TParameter, Variable> param2propertyMapping = new HashMap<>();

        // retrieve input parameters from all nodes which are downwards in the same
        // topology stack
        final List<TNodeTemplate> nodesForMatching = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching, context.getCsar());

        final TOperation defrostOp = ModelUtils.getOperationOfNode(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE, context.getCsar());

        // generate code for the pre handling, e.g., upload DAs
        for (final IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
            if (prePlugin.canHandleCreate(context, nodeTemplate)) {
                LOG.debug("Handling NodeTemplate {} with pre plugin {}", nodeTemplate.getId(), prePlugin.getID());
                result &= prePlugin.handleCreate(context, nodeTemplate);
            }
        }

        LOG.debug("Defrost on NodeTemplate {} needs the following input parameters:", nodeTemplate.getName());
        for (final TParameter param : defrostOp.getInputParameters()) {
            LOG.debug("Input param: {}", param.getName());
            found:
            for (final TNodeTemplate nodeForMatching : nodesForMatching) {
                for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                    if (param.getName().equals(propName)) {
                        param2propertyMapping.put(param, context.getPropertyVariable(nodeForMatching, propName));
                        break found;
                    }
                }
            }
        }
        LOG.debug("Found {} of {} input parameters.", param2propertyMapping.size(),
            defrostOp.getInputParameters().size());

        result &= context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE, param2propertyMapping, null);

        // generate code the post handling, e.g., update instance data, logs etc.
        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            LOG.debug("Checking if post plugin {} is suited for handling {}", postPhasePlugin.getID(),
                nodeTemplate.getName());
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getNodeTemplate())) {
                LOG.debug("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
            }
        }
        return result;
    }
}
