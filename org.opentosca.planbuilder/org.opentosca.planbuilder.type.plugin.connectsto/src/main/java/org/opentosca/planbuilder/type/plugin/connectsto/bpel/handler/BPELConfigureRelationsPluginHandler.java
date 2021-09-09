package org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPluginHandler implements ConnectsToPluginHandler<BPELPlanContext> {

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final AbstractRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        final AbstractNodeTemplate sourceNodeTemplate = relationTemplate.getSource();
        final AbstractNodeTemplate targetNodeTemplate = relationTemplate.getTarget();

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE)) {
            final TOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE);
            final Map<TParameter, Variable> input =
                findInputParameters(templateContext, op, relationTemplate, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE, input, null);
        }

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET)) {
            final TOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET);
            final Map<TParameter, Variable> input =
                findInputParameters(templateContext, op, relationTemplate, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET, input, null);
        }

        return true;
    }

    private boolean hasOperation(final AbstractRelationshipTemplate template, final String name) {
        return getOperation(template, name) != null;
    }

    private TOperation getOperation(final AbstractRelationshipTemplate template, final String name) {
        for (final TInterface i : template.getRelationshipType().getInterfaces()) {
            for (final TOperation op : i.getOperations()) {
                if (op.getName().equals(name)) {
                    return op;
                }
            }
        }
        return null;
    }

    private Map<TParameter, Variable> findInputParameters(final BPELPlanContext templateContext,
                                                                 final TOperation op,
                                                                 final AbstractRelationshipTemplate relationshipTemplate,
                                                                 final AbstractNodeTemplate sourceNodeTemplate,
                                                                 final AbstractNodeTemplate targetNodeTemplate) {
        final Map<TParameter, Variable> parameters = new HashMap<>();
        for (final TParameter p : op.getInputParameters()) {
            // Search parameter in RelationshipTemplate
            Variable v = templateContext.getPropertyVariable(templateContext.getRelationshipTemplate(), p.getName());
            if (v != null) {
                parameters.put(p, v);
            } else {
                // Search parameter in NodeTemplate
                if (!parameters.containsKey(p)) {
                    // Try source stack first
                    v = findPropertyInTopology(templateContext, sourceNodeTemplate, p.getName(), templateContext.getCsar());
                    if (v != null) {
                        parameters.put(p, v);
                    } else {
                        // Try target stack
                        v = findPropertyInTopology(templateContext, targetNodeTemplate, p.getName(),templateContext.getCsar());
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
                                            final String name, Csar csar) {
        AbstractNodeTemplate n = node;
        while (n != null) {
            final Variable v = templateContext.getPropertyVariable(n, name);
            if (v != null) {
                return v;
            } else {
                n = getNextNodeTemplate(n, csar);
            }
        }
        return null;
    }

    private AbstractNodeTemplate getNextNodeTemplate(final AbstractNodeTemplate node, Csar csar) {
        for (final AbstractRelationshipTemplate r : node.getOutgoingRelations()) {
            if (ModelUtils.getRelationshipTypeHierarchy(r.getRelationshipType(), csar).contains(Types.hostedOnRelationType)) {
                return r.getTarget();
            }
        }
        return null;
    }
}
