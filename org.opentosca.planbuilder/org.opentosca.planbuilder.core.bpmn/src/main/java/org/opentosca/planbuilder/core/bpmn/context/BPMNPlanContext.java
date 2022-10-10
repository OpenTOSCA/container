package org.opentosca.planbuilder.core.bpmn.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.w3c.dom.Node;

/**
 * This class is used for all Plugins. All actions on TemplateBuildPlans and BuildPlans should be done with the
 * operations of this class. It is basically a Facade to Template and its TemplateBuildPlan
 */
public class BPMNPlanContext extends PlanContext {

    private BPMNSubprocess bpmnSubprocess;

    public BPMNPlanContext(final BPMNPlan plan, final BPMNSubprocess templateBuildPlan, final Property2VariableMapping map,
                           final TServiceTemplate serviceTemplate, String serviceInstanceURLVarName,
                           String serviceInstanceIDVarName, String serviceTemplateURLVarName, Csar csar) {
        super(plan, serviceTemplate, map, null, serviceInstanceURLVarName, serviceInstanceIDVarName, serviceTemplateURLVarName, "", csar);

        this.bpmnSubprocess = templateBuildPlan;
    }

    public static Variable getVariable(String varName) {
        return new Variable(varName);
    }

    public String getTemplateId() {
        if (getNodeTemplate() != null) {
            return getNodeTemplate().getId();
        } else {
            return getRelationshipTemplate().getId();
        }
    }

    /**
     * Returns whether this context is for a nodeTemplate
     *
     * @return true if this context is for a nodeTemplate, else false
     */
    public boolean isNodeTemplate() {
        return this.bpmnSubprocess.getNodeTemplate() != null;
    }

    /**
     * Returns whether this context is for a relationshipTemplate
     *
     * @return true if this context is for a relationshipTemplate, else false
     */
    public boolean isRelationshipTemplate() {
        return this.bpmnSubprocess.getRelationshipTemplate() != null;
    }

    /**
     * Returns a set of nodes that will be provisioned in the plan of this context
     */
    public Collection<TNodeTemplate> getNodesInCreation() {
        Collection<AbstractActivity> activities = this.bpmnSubprocess.getBuildPlan().getActivites();
        Collection<TNodeTemplate> result = new HashSet<>();
        for (AbstractActivity activity : activities) {
            if ((activity instanceof NodeTemplateActivity) &&
                (activity.getType().equals(ActivityType.PROVISIONING) || activity.getType().equals(ActivityType.MIGRATION))) {
                result.add(((NodeTemplateActivity) activity).getNodeTemplate());
            }
        }
        return result;
    }

    /**
     * Returns all InfrastructureNodes of the Template this context belongs to
     *
     * @return a List of TNodeTemplate which are InfrastructureNodeTemplate of the template this context handles
     */

    public List<TNodeTemplate> getInfrastructureNodes() {
        final List<TNodeTemplate> infrastructureNodes = new ArrayList<>();
        if (this.bpmnSubprocess.getNodeTemplate() != null) {
            ModelUtils.getInfrastructureNodes(getNodeTemplate(), infrastructureNodes, this.getCsar());
        } else {
            final TRelationshipTemplate template = this.bpmnSubprocess.getRelationshipTemplate();
            if (ModelUtils.getRelationshipBaseType(template, this.getCsar()).equals(Types.connectsToRelationType)) {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, true, this.getCsar());
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false, this.getCsar());
            } else {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false, this.getCsar());
            }
        }
        return infrastructureNodes;
    }

    /**
     * returns subprocess
     *
     * @return scope element
     */
    public BPMNSubprocess getSubprocessElement() {
        return this.bpmnSubprocess;
    }

    /**
     * Returns the NodeTemplate of this BPMNPlanContext
     *
     * @return an TNodeTemplate if this BPMNPlanContext handles a NodeTemplate, else null
     */
    public TNodeTemplate getNodeTemplate() {
        return this.bpmnSubprocess.getNodeTemplate();
    }

    /**
     * Returns the RelationshipTemplate this context handles
     *
     * @return an TRelationshipTemplate if this context handle a RelationshipTemplate, else null
     */
    public TRelationshipTemplate getRelationshipTemplate() {
        return this.bpmnSubprocess.getRelationshipTemplate();
    }

    /**
     * Imports the given Node into the BuildPlan Document, to be able to append it to the Phases
     *
     * @param node the Node to import into the Document
     * @return the imported Node
     */
    public Node importNode(final Node node) {
        return this.bpmnSubprocess.getBuildPlan().getBpmnDocument().importNode(node, true);
    }
}

