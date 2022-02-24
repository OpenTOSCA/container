package org.opentosca.planbuilder.type.plugin.createnodeinstance.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeCreateInstancePlugin;
import org.opentosca.planbuilder.model.plan.InstanceState;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;

// it makes sense to implement IPlanBuilderTypePlugin rather than DockerContainerTypePlugin
// because BPMN invokes script task which already takes care of the IA and DA complexity
public class BPMNCreateNodeInstancePlugin implements IPlanBuilderTypeCreateInstancePlugin<BPMNPlanContext> {

    private static Logger LOG = LoggerFactory.getLogger(BPMNCreateNodeInstancePlugin.class);
    private final BPMNScopeHandler bpmnScopeHandler;

    public BPMNCreateNodeInstancePlugin() throws ParserConfigurationException {
        bpmnScopeHandler = new BPMNScopeHandler();
    }

    /**
     * The method creates BPMN Activity with CREATE_NODE_INSTANCE_TASK type and
     * @param templateContext a TemplateContext of a Template
     * @param nodeTemplate
     * @return
     */
    @Override
    public boolean handleCreate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        BPMNScope subprocess = templateContext.getBpmnScope();
        BPMNPlan buildPlan = subprocess.getBuildPlan();
        final BPMNScope createNodeInstanceTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.CREATE_NODE_INSTANCE_TASK);

        // TODO: Handle Property2Variable Mapping

        // id is unique
        String nodeId = nodeTemplate.getId();
        // unique id for node instance instance
        // used as resultVariable
        String nodeInstanceUrl = nodeId + "_NodeInstanceURL";
        // <id : variable name for url>, MyTinyToDoDockerContainer : MyTinyToDoDockerContainer_0_NodeInstanceURL
        LOG.debug("Creating  NodeTemplate {} with InstanceUrlVariableName {}", nodeId, nodeInstanceUrl);
        if (buildPlan.addInstanceUrlVariableNameToNodeTemplate(nodeTemplate, nodeInstanceUrl)) {
            LOG.debug("Successfully Adding Url {}", nodeInstanceUrl);
            LOG.debug("Url is {}", buildPlan.getNodeTemplateInstanceUrlVariableName(nodeTemplate));
            LOG.debug("Url is {}", createNodeInstanceTask.getInstanceUrlVariableName());
        } else {
            LOG.debug("NodeTemplate {} has URL {}", nodeId, buildPlan.getNodeTemplateInstanceUrlVariableName(nodeTemplate));
        }
        // TODO: State are depending on NodeTemplate, ex. DockerEngine
        createNodeInstanceTask.setInstanceState(InstanceState.STARTING.toString());

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
