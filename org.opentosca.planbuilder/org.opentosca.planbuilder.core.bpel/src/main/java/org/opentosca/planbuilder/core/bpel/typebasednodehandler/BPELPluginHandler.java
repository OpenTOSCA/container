package org.opentosca.planbuilder.core.bpel.typebasednodehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BPELPluginHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BPELPluginHandler.class);
    private final PluginRegistry pluginRegistry;

    @Inject
    public BPELPluginHandler(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public boolean handleActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                  final AbstractNodeTemplate nodeTemplate) {
        boolean result = false;
        switch (bpelScope.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpelScope, nodeTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, bpelScope, nodeTemplate);
                break;
            case DEFROST:
                result = handleDefrostActivity(context, bpelScope, nodeTemplate);
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    public boolean handleActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                  final AbstractRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        switch (bpelScope.getActivity().getType()) {
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

    private boolean handleTerminationActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                              final AbstractRelationshipTemplate relationshipTemplate) {
        boolean result = true;
        // generate code for the termination, e.g., call install, start or create methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(relationshipTemplate);
        if (plugin != null) {
            LOG.info("Handling RelationshipTemplate {} with type plugin {}", relationshipTemplate.getId(), plugin.getID());
            result &= plugin.handleTerminate(context, relationshipTemplate);
        } else {
            LOG.warn("Couldn't handle RelationshipTemplate {} with type plugin", relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(context, relationshipTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, relationshipTemplate);
            }
        }

        return result;
    }

    private boolean handleTerminationActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                              final AbstractNodeTemplate nodeTemplate) {
        boolean result = true;

        // generate code for the termination, e.g., call install, start or create
        // methods
        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForTermination(nodeTemplate);
        if (plugin != null) {
            LOG.info("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(), plugin.getID());
            result &= plugin.handleTerminate(context, nodeTemplate);
        } else {
            LOG.warn("Couldn't handle NodeTemplate {} with type plugin", nodeTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleTerminate(context, nodeTemplate)) {
                result &= postPhasePlugin.handleTerminate(context, nodeTemplate);
            }
        }

        return result;
    }

    private boolean handleProvisioningActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                               final AbstractNodeTemplate nodeTemplate) {
        boolean result = true;
        // generate code for the pre handling, e.g., upload DAs
        for (final IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
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
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getNodeTemplate())) {
                LOG.info("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
            }
        }
        return result;
    }

    private boolean handleProvisioningActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                               final AbstractRelationshipTemplate relationshipTemplate) {
        boolean result = true;

        if (this.pluginRegistry.canTypePluginHandleCreate(relationshipTemplate)) {
            final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(relationshipTemplate);
            LOG.info("Handling RelationshipTemplate {} with generic plugin", relationshipTemplate.getId());
            result &= this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);
        } else {
            LOG.debug("Couldn't handle RelationshipTemplate {}", relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getRelationshipTemplate())) {
                result &= postPhasePlugin.handleCreate(context, bpelScope.getRelationshipTemplate());
            }
        }
        return result;
    }

    private boolean handleDefrostActivity(final BPELPlanContext context, final BPELScope bpelScope,
                                          final AbstractNodeTemplate nodeTemplate) {
        boolean result = true;

        final Map<AbstractParameter, Variable> param2propertyMapping = new HashMap<>();

        // retrieve input parameters from all nodes which are downwards in the same topology stack
        final List<AbstractNodeTemplate> nodesForMatching = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching);

        final AbstractOperation defrostOp =
            ModelUtils.getOperationOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE);

        // generate code for the pre handling, e.g., upload DAs
        for (final IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
            if (prePlugin.canHandleCreate(nodeTemplate)) {
                LOG.info("Handling NodeTemplate {} with pre plugin {}", nodeTemplate.getId(), prePlugin.getID());
                result &= prePlugin.handleCreate(context, nodeTemplate);
            }
        }

        LOG.debug("Defrost on NodeTemplate {} needs the following input parameters:", nodeTemplate.getName());
        for (final AbstractParameter param : defrostOp.getInputParameters()) {
            LOG.debug("Input param: {}", param.getName());
            found:
            for (final AbstractNodeTemplate nodeForMatching : nodesForMatching) {
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
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE,
            param2propertyMapping, null);

        // generate code the post handling, e.g., update instance data, logs etc.
        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            LOG.info("Checking if post plugin {} is suited for handling {}", postPhasePlugin.getID(),
                     nodeTemplate.getName());
            if (postPhasePlugin.canHandleCreate(context, bpelScope.getNodeTemplate())) {
                LOG.info("Handling NodeTemplate {} with post plugin {}", nodeTemplate.getId(), postPhasePlugin.getID());
                result &= postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
            }
        }
        return result;
    }
}
