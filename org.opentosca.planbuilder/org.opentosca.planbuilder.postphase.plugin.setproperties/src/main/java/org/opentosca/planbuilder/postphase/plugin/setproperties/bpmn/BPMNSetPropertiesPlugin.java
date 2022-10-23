package org.opentosca.planbuilder.postphase.plugin.setproperties.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPostPhasePlugin;

import org.springframework.stereotype.Component;

/**
 * This class represents a POST-Phase Plugin to set the Properties of the NodeTemplate
 */
@Component
public class BPMNSetPropertiesPlugin implements IPlanBuilderBPMNPostPhasePlugin<BPMNPlanContext> {

    private static final String PLAN_ID = "BPMN OpenTOSCA SetProperties Post Phase Plugin";
    private final BPMNSetPropertiesHandler handler = new BPMNSetPropertiesHandler();

    @Override
    public boolean handleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return this.handler.handleCreate(context, nodeTemplate);
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean canHandleCreate(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleTerminate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleTerminate(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpdate(final BPMNPlanContext sourceContext, final BPMNPlanContext targetContext, final TNodeTemplate sourceNodeTemplate, final TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(final TNodeTemplate sourceNodeTemplate, final TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpdate(final BPMNPlanContext sourceContext, final BPMNPlanContext targetContext, final TRelationshipTemplate sourceRelationshipTemplate, final TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(final TRelationshipTemplate sourceRelationshipTemplate, final TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
