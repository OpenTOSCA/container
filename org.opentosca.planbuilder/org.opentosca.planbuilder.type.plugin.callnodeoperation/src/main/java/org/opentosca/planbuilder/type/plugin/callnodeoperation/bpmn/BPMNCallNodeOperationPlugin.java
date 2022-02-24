package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCallNodeOperationPlugin;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;

import javax.xml.parsers.ParserConfigurationException;

public class BPMNCallNodeOperationPlugin implements IPlanBuilderTypeCallNodeOperationPlugin<BPMNPlanContext> {
    private final BPMNScopeHandler bpmnScopeHandler;

    public BPMNCallNodeOperationPlugin() throws ParserConfigurationException {
        this.bpmnScopeHandler = new BPMNScopeHandler();
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean handleCreate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        BPMNScope subprocess = templateContext.getBpmnScope();
        BPMNPlan buildPlan = subprocess.getBuildPlan();
        final BPMNScope callNodeOperationTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.CALL_NODE_OPERATION_TASK);/*new BPMNScope(
            BPMNScopeType.CALL_NODE_OPERATION_TASK,
            idPrefix + "_" + buildPlan.getIdForNamesAndIncrement()
        );*/

        // TODO: Handle Property2Variable Mapping
        return callNodeOperationTask != null;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleCreate(BPMNPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(Csar csar, TNodeTemplate nodeTemplate) {
        // TODO: may need nodeTemplate type check if multiple plugins are implemented
        return true;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // TODO: may need nodeTemplate type check if multiple plugins are implemented
        return false;
    }
}
