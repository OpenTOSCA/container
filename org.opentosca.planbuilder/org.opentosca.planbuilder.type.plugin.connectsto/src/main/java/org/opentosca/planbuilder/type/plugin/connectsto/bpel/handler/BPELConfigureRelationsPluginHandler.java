package org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.type.plugin.connectsto.core.ConfigureRelationsPlugin;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;

public class BPELConfigureRelationsPluginHandler implements ConnectsToPluginHandler<BPELPlanContext> {

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final TRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        final TNodeTemplate sourceNodeTemplate = ModelUtils.getSource(relationTemplate, templateContext.getCsar());
        final TNodeTemplate targetNodeTemplate = ModelUtils.getTarget(relationTemplate, templateContext.getCsar());

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE, templateContext.getCsar())) {
            final TOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE, templateContext.getCsar());
            final Map<TParameter, Variable> input =
                findInputParameters(templateContext, op, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_SOURCE, input, null, null);
        }

        if (hasOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET, templateContext.getCsar())) {
            final TOperation op =
                getOperation(relationTemplate, ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET, templateContext.getCsar());
            final Map<TParameter, Variable> input =
                findInputParameters(templateContext, op, sourceNodeTemplate, targetNodeTemplate);
            templateContext.executeOperation(relationTemplate, ConfigureRelationsPlugin.INTERFACE_NAME,
                ConfigureRelationsPlugin.OPERATION_POST_CONFIGURE_TARGET, input, null, null);
        }

        return true;
    }

    private boolean hasOperation(final TRelationshipTemplate template, final String name, Csar csar) {
        return getOperation(template, name, csar) != null;
    }

    private TOperation getOperation(final TRelationshipTemplate template, final String name, Csar csar) {
        for (final TInterface i : ModelUtils.findRelationshipType(template, csar).getInterfaces()) {
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
                                                          final TNodeTemplate sourceNodeTemplate,
                                                          final TNodeTemplate targetNodeTemplate) {
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
                        v = findPropertyInTopology(templateContext, targetNodeTemplate, p.getName(), templateContext.getCsar());
                        if (v != null) {
                            parameters.put(p, v);
                        }
                    }
                }
            }
        }
        return parameters;
    }

    private Variable findPropertyInTopology(final PlanContext templateContext, final TNodeTemplate node,
                                            final String name, Csar csar) {
        TNodeTemplate n = node;
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

    private TNodeTemplate getNextNodeTemplate(final TNodeTemplate node, Csar csar) {
        for (final TRelationshipTemplate r : ModelUtils.getOutgoingRelations(node, csar)) {
            if (ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(r, csar), csar).contains(Types.hostedOnRelationType)) {
                return ModelUtils.getTarget(r, csar);
            }
        }
        return null;
    }
}
