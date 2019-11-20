package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractBPMN4TOSCAPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPMN4TOSCAInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.BPMN4TOSCATemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN4TOSCABuilder extends AbstractBPMN4TOSCAPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPMN4TOSCABuilder.class);

    private BPELPlanHandler planHandler;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;
    private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;
    // private final PropertyVariableHandler propertyInitializer;
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    private final BPMN4TOSCAInputHandler propertyInit = new BPMN4TOSCAInputHandler();
    private final BPELFinalizer finalizer;
    private final BPELPluginHandler bpelPluginHandler = new BPELPluginHandler();

    public BPMN4TOSCABuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
    }

    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate, final String name,
                              final List<BPMN4TOSCATemplate> bpmnWorkflow) {
        setInstanceData(bpmnWorkflow);
        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }
        if (namespace.equals(serviceTemplate.getQName().getNamespaceURI())
            && serviceTemplate.getId().equals(serviceTemplate.getQName().getLocalPart())) {
            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_BPMN4TOSCAPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_BPMN4TOSCAPlan";

            final AbstractPlan BPMN4TOSCAPlan = generatePlan(new QName(processNamespace, processName).toString(),
                                                             definitions, serviceTemplate, bpmnWorkflow);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(BPMN4TOSCAPlan.toString());
            final BPELPlan newPlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, BPMN4TOSCAPlan, "bpmn4tosca");

            newPlan.setTOSCAInterfaceName("BPMN4TOSCA Plans");
            newPlan.setTOSCAOperationname("start");

            this.planHandler.intializeBPMNActivityToBPELSkeleton(newPlan, csarName, bpmnWorkflow);

            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlans(newPlan, serviceTemplate);
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlans(newPlan, serviceTemplate);

            final Property2VariableMapping propMap =
                this.propertyInit.initializePropertiesFromWorkflow(newPlan, serviceTemplate, csarName, bpmnWorkflow);

            this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                                               newPlan);

            // TODO: outputs aus BPMN
            /**
             * this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newPlan, propMap,
             * serviceTemplate);
             */
            this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newPlan);
            final String serviceInstanceUrl =
                this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newPlan);
            final String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newPlan);
            final String serviceTemplateUrl =
                this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newPlan);

            runPlugins(newPlan, propMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csarName,
                       bpmnWorkflow);
            if (!newInstance) {
                this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newPlan);
            }

            this.finalizer.finalize(newPlan);
            return newPlan;
        }
        BPELBuildProcessBuilder.LOG.warn("Couldn't create BPMN4TOSCAPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                                         serviceTemplate.getQName().toString(), definitions.getId(), csarName);
        return null;

    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            for (final Entry<String, List<BPMN4TOSCATemplate>> template : serviceTemplate.getBPMN4TOSCAPlans()
                                                                                         .entrySet()) {
                final BPELPlan newPlan =
                    buildPlan(csarName, definitions, serviceTemplate, template.getKey(), template.getValue());
                if (newPlan != null) {
                    BPELBuildProcessBuilder.LOG.debug("Created BPMN4TOSCAPlan "
                        + newPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newPlan);
                }

            }
        }
        return plans;
    }

    @Override
    public AbstractPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                                  final AbstractServiceTemplate serviceTemplateId) {
        // TODO Auto-generated method stub
        return null;
    }

    private void runPlugins(final BPELPlan plan, final Property2VariableMapping map, final String serviceInstanceUrl,
                            final String serviceInstanceID, final String serviceTemplateUrl, final String csarFileName,
                            final List<BPMN4TOSCATemplate> bpmnWorkflow) {
        for (final BPELScope bpelScope : plan.getTemplateBuildPlans()) {
            final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();
            final BPELPlanContext context = new BPELPlanContext(plan, bpelScope, map, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csarFileName);

            final Map<AbstractParameter, Variable> inputs = new HashMap<>();
            for (final BPMN4TOSCATemplate bpmn4toscaTemplate : bpmnWorkflow) {
                if (!bpmn4toscaTemplate.getType().equals("StartEvent")
                    && !bpmn4toscaTemplate.getType().equals("EndEvent")) {
                    if (bpmn4toscaTemplate.getTemplate().getId().equals(nodeTemplate.getId())) {
                        context.executeOperation(nodeTemplate, bpmn4toscaTemplate.getTemplate().getNodeInterface(),
                                                 bpmn4toscaTemplate.getTemplate().getOperation(), inputs);
                    }
                    // TODO: debug and check with BPELInvokerPlugin
                }
            }

        }

    }

    private void setInstanceData(final List<BPMN4TOSCATemplate> bpmnWorkflow) {
        final BPMN4TOSCATemplate StartEvent =
            bpmnWorkflow.stream().filter(node -> node.getType().equals("StartEvent")).findAny().orElse(null);

        if (StartEvent.getInstanceType().equals("newInstance")) {
            newInstance = true;
        } else if (StartEvent.getInstanceType().equals("selectInstance")) {
            newInstance = false;
        }
    }
}
