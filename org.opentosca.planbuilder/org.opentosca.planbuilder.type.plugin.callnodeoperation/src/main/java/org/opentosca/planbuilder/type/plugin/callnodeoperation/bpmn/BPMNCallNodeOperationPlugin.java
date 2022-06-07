package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCallNodeOperationPlugin;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BPMNCallNodeOperationPlugin implements IPlanBuilderTypeCallNodeOperationPlugin<BPMNPlanContext> {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNCallNodeOperationPlugin.class);

    private static final ContainerPatternHandler containerPatternHandler = new ContainerPatternHandler();

    private static final EnginePatternHandler enginePatternHandler = new EnginePatternHandler();

    private static final DockerEngineHandler dockerEngineHandler = new DockerEngineHandler();

    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin DockerContainer";

    private  final BPMNScopeHandler bpmnScopeHandler;

    public BPMNCallNodeOperationPlugin() throws ParserConfigurationException {
        bpmnScopeHandler = new BPMNScopeHandler();
    }

    @Override
    public String getID() {
        return PLUGIN_ID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean handleCreate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        BPMNScope subprocess = context.getBpmnScope();
        BPMNPlan buildPlan = subprocess.getBuildPlan();
        boolean check = true;

        // Step-1: decides which pattern is applied to current NodeTemplate
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, context.getCsar())) {
            LOG.debug("Handling by container pattern");

            // TODO: consider adding compensation operation
        } else if (enginePatternHandler.isProvisionableByEnginePattern(nodeTemplate, context.getCsar())) {

            LOG.debug("Handling by engine pattern");
            BPMNScope callNodeOperationTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.CALL_NODE_OPERATION_TASK);
            check &= enginePatternHandler.handleCreate(callNodeOperationTask, context);

        } else if (dockerEngineHandler.isProvisionableByDockerEngine(nodeTemplate, context.getCsar())) {

            LOG.debug("Handling Docker Engine NodeTemplate");
            BPMNScope activateDataObjectTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.ACTIVATE_DATA_OBJECT_TASK);
            check &= dockerEngineHandler.handleCreate(activateDataObjectTask, context.getCsar());

        } else {
            check &= false;
        }
        // TODO: Handle Property2Variable Mapping
        return check;
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
    public boolean canHandleCreate(Csar csar, TNodeTemplate nodeTemplate, PlanLanguage language) {
        // TODO: may need nodeTemplate type check if multiple plugins are implemented
        return language == PlanLanguage.BPMN;
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
