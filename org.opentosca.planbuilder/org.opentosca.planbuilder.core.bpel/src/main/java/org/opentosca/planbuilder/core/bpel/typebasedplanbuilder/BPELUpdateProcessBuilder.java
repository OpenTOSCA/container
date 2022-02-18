package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.AbstractUpdatePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.DeployTechDescriptorHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.DeployTechDescriptorMapping;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELUpdateProcessBuilder extends AbstractUpdatePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELUpdateProcessBuilder.class);
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing deployment technology properties in the build plan
    private final DeployTechDescriptorHandler deployTechDescriptorHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;
    private BPELPluginHandler bpelPluginHandler;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;

    private CorrelationIDInitializer correlationHandler;

    private BPELProcessFragments bpelFragments;

    public BPELUpdateProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
            this.bpelFragments = new BPELProcessFragments();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing UpdatePlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
        this.deployTechDescriptorHandler = new DeployTechDescriptorHandler(this.planHandler);
    }

    private BPELPlan buildPlan(Csar csar, TDefinitions definitions, TServiceTemplate serviceTemplate) {
        if (!this.isUpdatableService(serviceTemplate, csar)) {
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_updatePlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_updatePlan";

        // we take the overall flow of an termination plan, basically with the goal of
        // saving state from the top to the bottom
        final AbstractPlan newAbstractUpdatePlan =
            generateUOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

        newAbstractUpdatePlan.setType(PlanType.MANAGEMENT);
        final BPELPlan newUpdatePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractUpdatePlan, "update");

        this.planHandler.initializeBPELSkeleton(newUpdatePlan, csar);

        newUpdatePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
        newUpdatePlan.setTOSCAOperationname("update");

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newUpdatePlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newUpdatePlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newUpdatePlan, serviceTemplate);

        DeployTechDescriptorMapping descriptorMap =
            this.deployTechDescriptorHandler.initializeDescriptorsAsVariables(newUpdatePlan, serviceTemplate);

        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newUpdatePlan);

        // initialize instanceData handling, add
        // instanceDataAPI/serviceInstanceID into input, add global
        // variables to hold the value for plugins
        this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newUpdatePlan);
        final String serviceTemplateURLVarName =
            this.serviceInstanceVarsHandler.getServiceTemplateURLVariableName(newUpdatePlan);
        this.serviceInstanceVarsHandler.appendInitPropertyVariablesFromServiceInstanceData(newUpdatePlan, propMap,
            serviceTemplateURLVarName,
            serviceTemplate,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // fetch all node instances that are running

//        this.instanceVarsHandler.addNodeInstanceFindLogic(newUpdatePlan,
//            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
//            serviceTemplate);AbstractUpdatePlanBuilder

        for (BPELScope scope : newUpdatePlan.getTemplateBuildPlans()) {
            if (scope.getNodeTemplate() != null &&
                !(scope.getActivity().getType().equals(ActivityType.PROVISIONING) ||
                    scope.getActivity().getType().equals(ActivityType.DEFROST))) {
                this.instanceVarsHandler.addNodeInstanceFindLogic(scope, serviceTemplateURLVarName,
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                    serviceTemplate);
            }
        }

        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newUpdatePlan, propMap,
            serviceTemplate);

        runPlugins(newUpdatePlan, propMap, descriptorMap, csar);

        final String serviceInstanceURLVarName =
            this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(newUpdatePlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newUpdatePlan,
            newUpdatePlan.getBpelMainSequenceOutputAssignElement(),
            "UPDATED", serviceInstanceURLVarName);

        this.correlationHandler.addCorrellationID(newUpdatePlan);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newUpdatePlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newUpdatePlan),
            "ERROR", serviceInstanceURLVarName);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newUpdatePlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newUpdatePlan), "FAILED", this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newUpdatePlan));

        String planInstanceUrlVarName = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newUpdatePlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newUpdatePlan,
            newUpdatePlan.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newUpdatePlan,
            newUpdatePlan.getBpelMainSequenceOutputAssignElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newUpdatePlan);

        // add for each loop over found node instances to terminate each running
        // instance
        /*
         * for (final BPELScope activ : changedActivities) { if (activ.getNodeTemplate() != null) {
         * final BPELPlanContext context = new BPELPlanContext(activ, propMap,
         * newTerminationPlan.getServiceTemplate());
         * this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
         * "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED"); } }
         */

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newUpdatePlan.getBpelDocument()));

        return newUpdatePlan;
    }

    @Override
    public List<AbstractPlan> buildPlans(Csar csar, TDefinitions definitions) {
        LOG.debug("Building the Update Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            LOG.debug("ServiceTemplate {} has no Update Plan, generating Update Plan",
                serviceTemplate.getId());

            if (this.isUpdatableService(serviceTemplate, csar)) {
                final BPELPlan newUpdatePlan = buildPlan(csar, definitions, serviceTemplate);

                if (newUpdatePlan != null) {
                    LOG.debug("Created Update Plan " + newUpdatePlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newUpdatePlan);
                }
            }
        }
        if (!plans.isEmpty()) {
            LOG.info("Created {} update plans for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    private TInterface getSaveStateInterface(final TNodeTemplate nodeTemplate, Csar csar) {
        List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();

        return interfaces != null ?
            ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces().stream()
                .filter(iface -> iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE))
                .findFirst().orElse(null)
            : null;
    }

    private TOperation getSaveStateOperation(final TNodeTemplate nodeTemplate, Csar csar) {
        final TInterface iface = getSaveStateInterface(nodeTemplate, csar);
        if (iface != null) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE)) {
                    return op;
                }
            }
        }
        return null;
    }

    private TParameter getSaveStateParameter(final TOperation op) {
        return op.getInputParameters().stream()
            .filter(param -> param.getName()
                .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT))
            .findFirst().orElse(null);
    }

    /**
     * This Methods Finds out if a Service Template Container a update method and then creates a update plan out of this
     * method
     *
     * @param plan    the plan to execute the plugins on
     * @param propMap a PropertyMapping from NodeTemplate to Properties to BPELVariables
     * @param csar    the csar
     */
    private void runPlugins(final BPELPlan plan, final Property2VariableMapping propMap,
                            final DeployTechDescriptorMapping descriptorMap, final Csar csar) {

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);
        final String planInstanceUrl = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), plan, templatePlan, propMap, descriptorMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
            if (templatePlan.getNodeTemplate() != null) {

                // create a context for the node

                final TNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                if (templatePlan.getActivity().getType().equals(ActivityType.FREEZE)) {
                    final String targetServiceTemplateUrlVar = context.getServiceTemplateURLVar();

                    final String saveStateUrlVarName =
                        this.planHandler.addGlobalStringVariable("nodeTemplateStateSaveURL", plan);

                    final String xpathQuery = "concat($" + targetServiceTemplateUrlVar
                        + ",'/nodetemplates/" + nodeTemplate.getId() + "/uploadDA')";
                    try {
                        Node assignSaveStateURL =
                            this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignNodeTemplate"
                                    + nodeTemplate.getId() + "state" + System.currentTimeMillis(),
                                targetServiceTemplateUrlVar,
                                saveStateUrlVarName,
                                xpathQuery);
                        assignSaveStateURL = context.importNode(assignSaveStateURL);
                        context.getPrePhaseElement().appendChild(assignSaveStateURL);
                    } catch (final IOException | SAXException e) {
                        LOG.error("Error while assinging save URL...", e);
                    }

                    final Variable saveStateUrlVar = BPELPlanContext.getVariable(saveStateUrlVarName);

                    final Map<TParameter, Variable> inputs = new HashMap<>();

                    TOperation saveStateOperation = getSaveStateOperation(nodeTemplate, csar);
                    if (saveStateOperation != null) {
                        inputs.put(getSaveStateParameter(saveStateOperation), saveStateUrlVar);
                    } else {
                        LOG.warn("Could not determine save state operation for Node Template {}", nodeTemplate.getId());
                    }

                    context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);
                }

                this.bpelPluginHandler.handleActivity(context, templatePlan, nodeTemplate);
            }
        }
    }
}
