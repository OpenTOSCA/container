package org.opentosca.planbuilder.postphase.plugin.instancedata.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPrePhasePlugin;

import org.springframework.stereotype.Component;

/**
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to the OpenTOSCA
 * Container InstanceData API
 */
@Component
public class BPMNInstanceDataPlugin implements IPlanBuilderBPMNPrePhasePlugin<BPMNPlanContext> {

    private static final String PLAN_ID = "BPMN OpenTOSCA InstanceData Pre Phase Plugin";
    private final Handler handler = new Handler();

    @Override
    public boolean canHandleCreate(BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        // we can handle nodes
        return true;
    }

    @Override
    public boolean canHandleCreate(BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        // we can handle relations
        return true;
    }

    public boolean canHandleTerminate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    public boolean canHandleTerminate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    public boolean handleUpdate(BPMNPlanContext sourceContext, BPMNPlanContext targetContext, TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        // TODO FIXME this is a huge assumption right now! Not all management plans need
        //  instance handling for provisioning
        return this.handler.handleCreate(context, nodeTemplate);
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        return this.handler.handleCreate(context, relationshipTemplate);
    }

    public boolean handleTerminate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    public boolean handleTerminate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public boolean canHandleUpdate(TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        return false;
    }

    public boolean handleUpdate(BPMNPlanContext sourceContext, BPMNPlanContext targetContext, TRelationshipTemplate sourceRelationshipTemplate, TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    public boolean canHandleUpdate(TRelationshipTemplate sourceRelationshipTemplate,
                                   TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    public boolean handleUpgrade(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    public boolean handleUpgrade(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    public boolean canHandleUpgrade(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    public boolean canHandleUpgrade(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
