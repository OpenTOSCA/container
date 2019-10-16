package org.opentosca.planbuilder.core.bpel.handlers;

import java.util.List;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.BPMN4TOSCATemplate;
import org.opentosca.planbuilder.model.tosca.Parameter;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;

public class bpmn4toscaInputHandler {
	// TODO
	public void initializePropertiesFromWorkflow(final BPELPlan plan, final Property2VariableMapping propMap,
			String serviceInstanceUrl, String serviceInstanceId, String serviceTemplateUrl,
			AbstractServiceTemplate serviceTemplate, String csarName, final List<BPMN4TOSCATemplate> bpmnWorkflow) {
		
		for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
			final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
			final BPELPlanContext context = new BPELPlanContext(plan, templatePlan, propMap,
					plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

			System.out.println(propMap.getNodePropertyVariables(serviceTemplate, nodeTemplate));
			/**
			 * for (BPMN4TOSCATemplate bpmn4toscaTemplate : bpmnWorkflow) { if
			 * (bpmn4toscaTemplate.getInput() != null) { for (Parameter inputParameter :
			 * bpmn4toscaTemplate.getInput()) { if
			 * (inputParameter.getValue().startsWith("get_input:")) { String content =
			 * inputParameter.getValue().replace("get_input:", "").trim();
			 * addToPlanInput(plan, content, var, context); } } } }
			 */
		}
	}

}
