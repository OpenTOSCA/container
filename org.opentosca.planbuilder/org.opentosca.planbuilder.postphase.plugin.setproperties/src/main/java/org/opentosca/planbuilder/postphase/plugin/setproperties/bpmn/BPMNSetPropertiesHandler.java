package org.opentosca.planbuilder.postphase.plugin.setproperties.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNComponentType;

public class BPMNSetPropertiesHandler {

    private BPMNSubprocessHandler bpmnSubprocessHandler;

    public BPMNSetPropertiesHandler() {
        this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
    }

    public boolean handleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        BPMNSubprocess subprocess = context.getSubprocessElement();
        bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNComponentType.SET_NODE_PROPERTY_TASK);
        return true;
    }

    public boolean handleCreate(final BPMNPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
