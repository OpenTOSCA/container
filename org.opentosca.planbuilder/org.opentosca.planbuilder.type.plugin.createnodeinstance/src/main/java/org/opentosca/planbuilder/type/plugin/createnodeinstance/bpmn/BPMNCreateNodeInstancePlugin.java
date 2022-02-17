package org.opentosca.planbuilder.type.plugin.createnodeinstance.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCreateInstancePlugin;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;

// it makes sense to implement IPlanBuilderTypePlugin rather than DockerContainerTypePlugin
// because BPMN invokes script task which already takes care of the IA and DA complexity
public class BPMNCreateNodeInstancePlugin implements IPlanBuilderTypeCreateInstancePlugin<BPMNPlanContext> {

    /**
     * The method creates BPMM Activity with CREATE_NODE_INSTANCE_TASK type and
     * @param templateContext a TemplateContext of a Template
     * @param nodeTemplate
     * @return
     */
    @Override
    public boolean handleCreate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        BPMNScope subprocess = templateContext.getBpmnScope();
        BPMNPlan buildPlan = subprocess.getBuildPlan();
        String idPrefix = BPMNScopeType.CREATE_NODE_INSTANCE_TASK.toString();
        final BPMNScope createNodeInstanceTask = new BPMNScope(
          BPMNScopeType.CREATE_NODE_INSTANCE_TASK,
          idPrefix + "_" + buildPlan.getIdForNamesAndIncrement()
        );

        // TODO: Handle Property2Variable Mapping

        subprocess.setSubProCreateNodeInstanceTask(createNodeInstanceTask);
        subprocess.addScopeToSubprocess(createNodeInstanceTask);
        createNodeInstanceTask.setParentProcess(subprocess);
        createNodeInstanceTask.setBuildPlan(buildPlan);
        return createNodeInstanceTask != null;
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

    @Override
    public String getID() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
