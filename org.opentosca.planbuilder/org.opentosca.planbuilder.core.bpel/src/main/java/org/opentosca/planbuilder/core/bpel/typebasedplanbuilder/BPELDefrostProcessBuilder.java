package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.AbstractDefrostPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPELDefrostProcessBuilder extends AbstractDefrostPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELDefrostProcessBuilder.class);
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    private final EmptyPropertyToInputHandler emptyPropInit;
    private final BPELPluginHandler bpelPluginHandler;
    // handler for abstract buildplan operations
    private BPELPlanHandler planHandler;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    private CorrelationIDInitializer correlationHandler;

    public BPELDefrostProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        emptyPropInit = new EmptyPropertyToInputHandler(new BPELScopeBuilder(pluginRegistry));
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            new BPELProcessFragments();
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            BPELDefrostProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    private BPELPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {
        BPELDefrostProcessBuilder.LOG.info("Making Concrete Plans");

        if (!this.isDefrostable(serviceTemplate, csar)) {
            BPELDefrostProcessBuilder.LOG.warn("Couldn't create DeFreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                serviceTemplate.getId(), definitions.getId(), csar.id().csarName());
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_defrostPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_defrostPlan";

        final AbstractPlan newAbstractBackupPlan =
            generateDOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

        final BPELPlan newDefreezePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "defrost");

        newDefreezePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
        newDefreezePlan.setTOSCAOperationname("defrost");
        newDefreezePlan.setType(PlanType.BUILD);

        this.planHandler.initializeBPELSkeleton(newDefreezePlan, csar);

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newDefreezePlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newDefreezePlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newDefreezePlan, serviceTemplate);

        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newDefreezePlan);

        // initialize instanceData handling
        this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newDefreezePlan);

        final String serviceInstanceUrl = this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newDefreezePlan);
        final String serviceInstanceId = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newDefreezePlan);
        final String serviceTemplateUrl =
            this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newDefreezePlan);
        final String planInstanceUrl = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newDefreezePlan);

        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newDefreezePlan, propMap, serviceInstanceUrl,
            serviceInstanceId, serviceTemplateUrl, planInstanceUrl, serviceTemplate,
            csar);

        runPlugins(newDefreezePlan, propMap, serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);

        this.correlationHandler.addCorrellationID(newDefreezePlan);

        final String serviceInstanceURLVarName =
            this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newDefreezePlan);

        this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
            newDefreezePlan.getBpelMainFlowElement(),
            "CREATING", serviceInstanceURLVarName);
        this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
            newDefreezePlan.getBpelMainSequenceOutputAssignElement(),
            "CREATED", serviceInstanceURLVarName);

        this.serviceInstanceInitializer.appendSetServiceInstanceStateAsChild(newDefreezePlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newDefreezePlan),
            "ERROR", serviceInstanceURLVarName);
        this.serviceInstanceInitializer.appendSetServiceInstanceStateAsChild(newDefreezePlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newDefreezePlan), "FAILED", this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newDefreezePlan));

        String planInstanceUrlVarName = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newDefreezePlan);
        this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
            newDefreezePlan.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
            newDefreezePlan.getBpelMainSequenceOutputAssignElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newDefreezePlan);

        BPELDefrostProcessBuilder.LOG.debug("Created Plan:");
        BPELDefrostProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newDefreezePlan.getBpelDocument()));

        return newDefreezePlan;
    }

    private boolean isDefrostable(final TServiceTemplate serviceTemplate, Csar csar) {

        for (final TNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (this.isDefrostable(nodeTemplate, csar)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDefrostable(final TNodeTemplate nodeTemplate, Csar csar) {
        return Objects.nonNull(this.getLoadStateOperation(nodeTemplate, csar))
            && hasFreezeableComponentPolicy(nodeTemplate);
    }

    private TInterface getLoadStateInterface(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TInterface iface : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            if (!iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE)) {
                continue;
            }

            return iface;
        }
        return null;
    }

    private TOperation getLoadStateOperation(final TNodeTemplate nodeTemplate, Csar csar) {
        final TInterface iface = this.getLoadStateInterface(nodeTemplate, csar);
        if (iface != null) {
            for (final TOperation op : iface.getOperations()) {
                if (!op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE)) {
                    continue;
                }

                return op;
            }
        }
        return null;
    }

    private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceId,
                            final String serviceTemplateUrl, String planInstanceUrl, final Csar csar) {

        for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), buildPlan, bpelScope, map, buildPlan.getServiceTemplate(), serviceInstanceUrl,
                serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
            if (bpelScope.getNodeTemplate() != null) {

                final TNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();

                if (isRunning(nodeTemplate)) {
                    BPELBuildProcessBuilder.LOG.debug("Skipping the provisioning of NodeTemplate "
                        + nodeTemplate.getId() + "  beacuse state=running is set.");
                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleCreate(context, bpelScope.getNodeTemplate())) {
                            postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
                        }
                    }
                    continue;
                }
                // generate code for the activity
                this.bpelPluginHandler.handleActivity(context, bpelScope, nodeTemplate);
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handle relationshiptemplate
                final TRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            }
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        BPELDefrostProcessBuilder.LOG.debug("Building the Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (!this.isDefrostable(serviceTemplate, csar)) {
                continue;
            }

            BPELDefrostProcessBuilder.LOG.debug("ServiceTemplate {} has no DefreezePlan, generating a new plan",
                serviceTemplate.getId());
            final BPELPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);

            if (newBuildPlan != null) {
                BPELDefrostProcessBuilder.LOG.debug("Created Defreeze Plan "
                    + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }
        }
        if (!plans.isEmpty()) {
        	LOG.info("Created {} defrost plan for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }
}
