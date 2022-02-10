package org.opentosca.planbuilder.core.bpmn.context;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;

// TODO: implement detail
public class BPMNPlanContext extends PlanContext {

    public BPMNScope getBpmnScope() {
        return bpmnScope;
    }

    // scope could be script task or subprocess containing
    private BPMNScope bpmnScope;
    public BPMNPlanContext(BPMNPlan buildPlan, BPMNScope bpmnScope, Property2VariableMapping map, TServiceTemplate serviceTemplate, String serviceInstanceUrl, String serviceInstanceID, String serviceTemplateUrl, String planInstanceUrl, Csar csar) {
        super(buildPlan, serviceTemplate, map, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);
        this.bpmnScope = bpmnScope;
    }
}
