package org.opentosca.planbuilder.type.plugin.setnodeproperty.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeSetPropertyPlugin;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;

import javax.xml.parsers.ParserConfigurationException;

public class BPMNSetNodePropertyPlugin implements IPlanBuilderTypeSetPropertyPlugin<BPMNPlanContext> {

    private final BPMNScopeHandler bpmnScopeHandler;

    public BPMNSetNodePropertyPlugin() throws ParserConfigurationException {
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
        final BPMNScope setNodePropertyTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.SET_NODE_PROPERTY_TASK);

        // TODO: Handle Property2Variable Mapping

        return setNodePropertyTask != null;
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
        return false;
    }
}
