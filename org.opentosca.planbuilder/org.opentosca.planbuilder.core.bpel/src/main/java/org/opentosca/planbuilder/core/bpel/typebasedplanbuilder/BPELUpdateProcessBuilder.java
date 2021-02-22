package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.AbstractUpdatePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELUpdateProcessBuilder extends AbstractUpdatePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELUpdateProcessBuilder.class);

    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

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
    }

    @Override
    public BPELPlan buildPlan(String csarName, AbstractDefinitions definitions, AbstractServiceTemplate serviceTemplate) {
        LOG.info("Creating Update Plan...");

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_updatePlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_updatePlan";

        // we take the overall flow of an termination plan, basically with the goal of
        // saving state from the top to the bottom
        final AbstractPlan newAbstractUpdatePlan =
            generateUOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

        newAbstractUpdatePlan.setType(PlanType.MANAGEMENT);
        final BPELPlan newUpdatePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractUpdatePlan, "update");

        this.planHandler.initializeBPELSkeleton(newUpdatePlan, csarName);

        newUpdatePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
        newUpdatePlan.setTOSCAOperationname("update");

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newUpdatePlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newUpdatePlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newUpdatePlan, serviceTemplate);

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

        runPlugins(newUpdatePlan, propMap, csarName);

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
    public List<AbstractPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
        LOG.info("Building the Update Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            LOG.debug("ServiceTemplate {} has no Update Plan, generating Update Plan",
                serviceTemplate.getQName().toString());
            final BPELPlan newUpdatePlan = buildPlan(csarName, definitions, serviceTemplate);

            if (newUpdatePlan != null) {
                LOG.debug("Created Update Plan " + newUpdatePlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newUpdatePlan);
            }
        }
        return plans;
    }

    private AbstractInterface getSaveStateInterface(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getType().getInterfaces().stream()
            .filter(iface -> iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE))
            .findFirst().orElse(null);
    }

    private AbstractOperation getSaveStateOperation(final AbstractNodeTemplate nodeTemplate) {
        final AbstractInterface iface = getSaveStateInterface(nodeTemplate);
        if (iface != null) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE)) {
                    return op;
                }
            }
        }
        return null;
    }

    private AbstractParameter getSaveStateParameter(final AbstractOperation op) {
        return op.getInputParameters().stream()
            .filter(param -> param.getName()
                .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT))
            .findFirst().orElse(null);
    }

    /**
     * This Methods Finds out if a Service Template Container a update method and then creates a update plan out of this
     * method
     *
     * @param plan     the plan to execute the plugins on
     * @param propMap  a PropertyMapping from NodeTemplate to Properties to BPELVariables
     * @param csarName name of csar
     */
    private void runPlugins(final BPELPlan plan, final Property2VariableMapping propMap,
                            final String csarName) {

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);
        final String planInstanceUrl = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), plan, templatePlan, propMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csarName);
            if (templatePlan.getNodeTemplate() != null) {

                // create a context for the node

                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

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

                    final Map<AbstractParameter, Variable> inputs = new HashMap<>();

                    AbstractOperation saveStateOperation = getSaveStateOperation(nodeTemplate);
                    if (saveStateOperation != null) {
                        inputs.put(getSaveStateParameter(saveStateOperation), saveStateUrlVar);
                    } else {
                        LOG.warn("Could not determine save state operation for Node Template {}", nodeTemplate.getId());
                    }

                    context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);
                }

                // TODO add termination logic

                /*
                 * generic save state code
                 */
                final AbstractOperation updateOp =
                    ModelUtils.getOperationOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE_RUNUPDATE);
                if (this.isUpdatableComponent(nodeTemplate) && updateOp != null) {

                    final Map<AbstractParameter, Variable> inputs = new HashMap<>();

                    // retrieve input parameters from all nodes which are downwards in the same topology stack
                    final List<AbstractNodeTemplate> nodesForMatching = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching);

                    LOG.debug("Update on NodeTemplate {} needs the following input parameters:",
                        nodeTemplate.getName());
                    for (final AbstractParameter param : updateOp.getInputParameters()) {
                        LOG.debug("Input param: {}", param.getName());
                        found:
                        for (final AbstractNodeTemplate nodeForMatching : nodesForMatching) {
                            for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                                if (param.getName().equals(propName)) {
                                    inputs.put(param, context.getPropertyVariable(nodeForMatching, propName));
                                    break found;
                                }
                            }
                        }
                    }

                    LOG.debug("Found {} of {} input parameters.", inputs.size(), updateOp.getInputParameters().size());
                }
                this.bpelPluginHandler.handleActivity(context, templatePlan, nodeTemplate);
            }
        }
    }
}
