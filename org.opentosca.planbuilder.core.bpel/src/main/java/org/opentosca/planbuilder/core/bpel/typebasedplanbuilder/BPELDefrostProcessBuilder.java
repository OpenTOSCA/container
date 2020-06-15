package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractDefrostPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
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

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {
        BPELDefrostProcessBuilder.LOG.info("Making Concrete Plans");

        if (!this.isDefrostable(serviceTemplate)) {
            BPELDefrostProcessBuilder.LOG.warn("Couldn't create DeFreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                serviceTemplate.getQName().toString(), definitions.getId(), csarName);
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_defrostPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_defrostPlan";

        final AbstractPlan newAbstractBackupPlan =
            generateDOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

        final BPELPlan newDefreezePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "defrost");

        newDefreezePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
        newDefreezePlan.setTOSCAOperationname("defrost");
        newDefreezePlan.setType(PlanType.BUILD);

        this.planHandler.initializeBPELSkeleton(newDefreezePlan, csarName);

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
        final String serviceTemplateUrl = this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newDefreezePlan);

        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newDefreezePlan, propMap, serviceInstanceUrl,
            serviceInstanceId, serviceTemplateUrl, serviceTemplate,
            csarName);

        runPlugins(newDefreezePlan, propMap, serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

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

        this.finalizer.finalize(newDefreezePlan);

        BPELDefrostProcessBuilder.LOG.debug("Created Plan:");
        BPELDefrostProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newDefreezePlan.getBpelDocument()));

        return newDefreezePlan;
    }

    private boolean isDefrostable(final AbstractServiceTemplate serviceTemplate) {

        for (final AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (this.isDefrostable(nodeTemplate)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDefrostable(final AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(this.getLoadStateOperation(nodeTemplate))
            && this.hasFreezeableComponentPolicy(nodeTemplate);
    }

    private AbstractInterface getLoadStateInterface(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (!iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE)) {
                continue;
            }

            return iface;
        }
        return null;
    }

    private AbstractOperation getLoadStateOperation(final AbstractNodeTemplate nodeTemplate) {
        final AbstractInterface iface = this.getLoadStateInterface(nodeTemplate);
        if (iface != null) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (!op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE)) {
                    continue;
                }

                return op;
            }
        }
        return null;
    }

    private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping propMap,
                            final String serviceInstanceUrl, final String serviceInstanceId,
                            final String serviceTemplateUrl, final String csarFileName) {
        for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), buildPlan, bpelScope, propMap, buildPlan.getServiceTemplate(), serviceInstanceUrl,
                serviceInstanceId, serviceTemplateUrl, csarFileName);
            if (bpelScope.getNodeTemplate() != null) {

                final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();

                if (isRunning(nodeTemplate)) {
                    BPELBuildProcessBuilder.LOG.debug("Skipping the provisioning of NodeTemplate "
                        + nodeTemplate.getId() + "  beacuse state=running is set.");
                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleCreate(nodeTemplate)) {
                            postPhasePlugin.handleCreate(context, nodeTemplate);
                        }
                    }
                    continue;
                }
                // generate code for the activity
                this.bpelPluginHandler.handleActivity(context, bpelScope, nodeTemplate);
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handle relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            }
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        BPELDefrostProcessBuilder.LOG.info("Building the Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (!this.isDefrostable(serviceTemplate)) {
                continue;
            }

            BPELDefrostProcessBuilder.LOG.debug("ServiceTemplate {} has no DefreezePlan, generating a new plan",
                serviceTemplate.getQName().toString());
            final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

            if (newBuildPlan != null) {
                BPELDefrostProcessBuilder.LOG.debug("Created Defreeze Plan "
                    + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }
        }
        return plans;
    }
}
