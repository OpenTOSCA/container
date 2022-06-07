package org.opentosca.planbuilder.type.plugin.setnodeproperty.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypeSetPropertyPlugin;
import org.opentosca.planbuilder.model.plan.InstanceState;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;

public class BPMNSetNodePropertyPlugin implements IPlanBuilderTypeSetPropertyPlugin<BPMNPlanContext> {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNSetNodePropertyPlugin.class);

    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin DockerContainer";

    // fixed with groovy script: "SetProperties"
    private static final String INPUT_PREFIX = "Input_";

    private static final String INPUT_PROPERTIES_NAME = "Properties";

    private final BPMNScopeHandler bpmnScopeHandler;
    private final PropertyVariableHandler propVarHandler;

    public BPMNSetNodePropertyPlugin() throws ParserConfigurationException {
        this.bpmnScopeHandler = new BPMNScopeHandler();
        this.propVarHandler = new PropertyVariableHandler();
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
    public boolean handleCreate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        BPMNScope subprocess = templateContext.getBpmnScope();
        final BPMNScope setNodePropertyTask = bpmnScopeHandler.createBPMNScopeWithinSubprocess(subprocess, BPMNScopeType.SET_NODE_PROPERTY_TASK);

        // TODO: Handle Property2Variable Mapping
        // Step-1: Setting common parameter State, NodeInstanceURL, NodeTemplate
        // TODO: define state precisely
        setNodePropertyTask.setInstanceState(InstanceState.STARTED.name());
        // Step-2: Read and collection all properties from NodeTemplate and set Properties

        Map<String, String> propMap = ModelUtils.asMap(nodeTemplate.getProperties());

        propVarHandler.createInputParameterFromProperties(propMap, setNodePropertyTask);

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
        return false;
    }
}
