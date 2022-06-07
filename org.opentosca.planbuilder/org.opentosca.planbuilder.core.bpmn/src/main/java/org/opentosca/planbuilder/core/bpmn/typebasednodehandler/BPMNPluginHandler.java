package org.opentosca.planbuilder.core.bpmn.typebasednodehandler;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCallNodeOperationPlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCreateInstancePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeSetPropertyPlugin;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

@Component
public class BPMNPluginHandler {

    final static Logger LOG = LoggerFactory.getLogger(BPMNPluginHandler.class);
    private final PluginRegistry pluginRegistry;
    private final BPMNScopeHandler scopeHandler;

    @Inject
    public BPMNPluginHandler(PluginRegistry pluginRegistry) throws ParserConfigurationException {
        this.pluginRegistry = pluginRegistry;
        this.scopeHandler = new BPMNScopeHandler();
    }


    public boolean handleActivity(BPMNPlanContext context, BPMNScope bpmnScope, TNodeTemplate nodeTemplate) {
        LOG.debug("Handling BPMN Activity with {}", bpmnScope.getActivity());
        boolean result = false;
        switch (bpmnScope.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpmnScope, nodeTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, bpmnScope, nodeTemplate);
                break;
        }
        return result;
    }

    // TODO: implement for termination
    private boolean handleTerminationActivity(BPMNPlanContext context, BPMNScope bpmnScope, TNodeTemplate nodeTemplate) {
        boolean result = false;
        return result;
    }

    // TODO: implement for termination
    private boolean handleTerminationActivity(BPMNPlanContext context, BPMNScope bpmnScope, TRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        return result;
    }

    // TODO: implement for relationship template
    private boolean handleProvisioningActivity(BPMNPlanContext context, BPMNScope subprocess, TRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        return result;
    }

    /**
     * Handle BPMN Provision Activity by instantiating BPMN Elements for the input subprocess
     * @param context
     * @param subprocess
     * @param nodeTemplate
     * @return
     */
    private boolean handleProvisioningActivity(BPMNPlanContext context, BPMNScope subprocess, TNodeTemplate nodeTemplate) {
        boolean result = true;
        LOG.debug("Processing NodeTemplate {} with activityType {}", nodeTemplate.getId(),
            subprocess.getActivity().getType());
        BPMNScope startEvent = scopeHandler.createStartEventSubprocess(subprocess.getBuildPlan(), subprocess);

        BPMNScope createNodeInstanceTask = null;
        BPMNScope callNodeOperationTask = null;
        BPMNScope setNodePropertyTask = null;

        for (IPlanBuilderTypeCreateInstancePlugin nodeInstancePlugin : pluginRegistry.getCreateInstancePlugins()) {
            if (nodeInstancePlugin.canHandleCreate(context.getCsar(), nodeTemplate, PlanLanguage.BPMN)) {
                LOG.debug("Generating BPMN Script Task Creating Instance with Plugin {}", nodeInstancePlugin.getClass());
                result &= nodeInstancePlugin.handleCreate(context, nodeTemplate);
                createNodeInstanceTask = subprocess.getSubProCreateNodeInstanceTask();
                scopeHandler.createSequenceFlowSubprocess(startEvent, createNodeInstanceTask, subprocess);
            }
        }

        for (IPlanBuilderTypeCallNodeOperationPlugin nodeOperationPlugin : pluginRegistry.getCallNodeOperationPlugins()) {
            if (nodeOperationPlugin.canHandleCreate(context.getCsar(), nodeTemplate, PlanLanguage.BPMN)) {
                LOG.debug("Generating BPMN Script Task for Call Node Operation Task with Plugin {}", nodeOperationPlugin.getClass());
                result &= nodeOperationPlugin.handleCreate(context, nodeTemplate);
                callNodeOperationTask = subprocess.getSubProCallOperationTask();
                scopeHandler.createSequenceFlowSubprocess(createNodeInstanceTask, callNodeOperationTask, subprocess);
            }
        }


        for (IPlanBuilderTypeSetPropertyPlugin nodePropertyPlugin : pluginRegistry.getSetPropertyPlugins()) {
            if (nodePropertyPlugin.canHandleCreate(context.getCsar(), nodeTemplate, PlanLanguage.BPMN)) {
                LOG.debug("Generating BPMN Script Task for Set Property with Plugin {}", nodePropertyPlugin);
                result &= nodePropertyPlugin.handleCreate(context, nodeTemplate);
                setNodePropertyTask = subprocess.getSubProSetNodePropertyTask();
                scopeHandler.createSequenceFlowSubprocess(callNodeOperationTask, setNodePropertyTask, subprocess);
            }
        }

        BPMNScope endEvent = scopeHandler.createEndEventSubprocess(subprocess.getBuildPlan(), subprocess);
        scopeHandler.createSequenceFlowSubprocess(setNodePropertyTask, endEvent, subprocess);
        return result;
    }

    public boolean handleActivity(BPMNPlanContext context, BPMNScope bpmnScope, TRelationshipTemplate relationshipTemplate) {
        boolean result = false;
        switch (bpmnScope.getActivity().getType()) {
            case PROVISIONING:
                result = this.handleProvisioningActivity(context, bpmnScope, relationshipTemplate);
                break;
            case TERMINATION:
                result = this.handleTerminationActivity(context, bpmnScope, relationshipTemplate);
                break;
        }
        return result;
    }
}
