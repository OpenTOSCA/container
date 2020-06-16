package org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPluginHandler implements ConnectsToPluginHandler<BPELPlanContext> {

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        final AbstractNodeTemplate sourceNodeTemplate = relationTemplate.getSource();
        final AbstractNodeTemplate targetNodeTemplate = relationTemplate.getTarget();

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE)) {
            final AbstractOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE);
            final Map<AbstractParameter, Variable> input =
                findInputParameters(templateContext, op, relationTemplate, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE, input, null);
        }

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET)) {
            final AbstractOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET);
            final Map<AbstractParameter, Variable> input =
                findInputParameters(templateContext, op, relationTemplate, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET, input, null);
        }

        return true;
    }

    private boolean hasOperation(final AbstractRelationshipTemplate template, final String name) {
        return getOperation(template, name) != null;
    }

    private AbstractOperation getOperation(final AbstractRelationshipTemplate template, final String name) {
        for (final AbstractInterface i : template.getRelationshipType().getInterfaces()) {
            for (final AbstractOperation op : i.getOperations()) {
                if (op.getName().equals(name)) {
                    return op;
                }
            }
        }
        return null;
    }

    private Map<AbstractParameter, Variable> findInputParameters(final BPELPlanContext templateContext,
                                                                 final AbstractOperation op,
                                                                 final AbstractRelationshipTemplate relationshipTemplate,
                                                                 final AbstractNodeTemplate sourceNodeTemplate,
                                                                 final AbstractNodeTemplate targetNodeTemplate) {
        final Map<AbstractParameter, Variable> parameters = new HashMap<>();
        for (final AbstractParameter p : op.getInputParameters()) {
            // Search parameter in RelationshipTemplate
            Variable v = templateContext.getPropertyVariable(templateContext.getRelationshipTemplate(), p.getName());
            if (v != null) {
                parameters.put(p, v);
            } else {
                // Search parameter in NodeTemplate
                if (!parameters.containsKey(p)) {
                    // Try source stack first
                    v = findPropertyInTopology(templateContext, sourceNodeTemplate, p.getName());
                    if (v != null) {
                        parameters.put(p, v);
                    } else {
                        // Try target stack
                        v = findPropertyInTopology(templateContext, targetNodeTemplate, p.getName());
                        if (v != null) {
                            parameters.put(p, v);
                        }
                    }
                }
            }
        }
        return parameters;
    }

    private Variable findPropertyInTopology(final PlanContext templateContext, final AbstractNodeTemplate node,
                                            final String name) {
        AbstractNodeTemplate n = node;
        while (n != null) {
            final Variable v = templateContext.getPropertyVariable(n, name);
            if (v != null) {
                return v;
            } else {
                n = getNextNodeTemplate(n);
            }
        }
        return null;
    }

    private AbstractNodeTemplate getNextNodeTemplate(final AbstractNodeTemplate node) {
        for (final AbstractRelationshipTemplate r : node.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(r.getRelationshipType()).contains(Types.hostedOnRelationType)) {
                return r.getTarget();
            }
        }
        return null;
    }
}
