package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.AbstractTerminationPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELTerminationProcessBuilder extends AbstractTerminationPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELTerminationProcessBuilder.class);
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    private final BPELPluginHandler bpelPluginHandler;
    private final BPELScopeBuilder scopeBuilder;
    // handler for abstract buildplan operations
    private BPELPlanHandler planHandler;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    private CorrelationIDInitializer correlationHandler;

    public BPELTerminationProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        this.bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        this.scopeBuilder = new BPELScopeBuilder(pluginRegistry);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions, javax.xml.namespace.QName)
     */
    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_terminationPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_terminationPlan";

        final AbstractPlan newAbstractTerminationPlan =
            generateTOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

        final BPELPlan newTerminationPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractTerminationPlan,
                "terminate");

        newTerminationPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
        newTerminationPlan.setTOSCAOperationname("terminate");

        this.planHandler.initializeBPELSkeleton(newTerminationPlan, csarName);

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newTerminationPlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newTerminationPlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newTerminationPlan, serviceTemplate);

        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newTerminationPlan);

        // initialize instanceData handling, add
        // instanceDataAPI/serviceInstanceID into input, add global
        // variables to hold the value for plugins
        this.serviceInstanceHandler.addServiceInstanceHandlingFromInput(newTerminationPlan);
        String serviceTemplateURLVarName =
            this.serviceInstanceHandler.getServiceTemplateURLVariableName(newTerminationPlan);

        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(newTerminationPlan);
        String planInstanceUrl = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(newTerminationPlan);

        this.serviceInstanceHandler.appendInitPropertyVariablesFromServiceInstanceData(newTerminationPlan, propMap,
            serviceTemplateURLVarName,
            serviceTemplate, "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // fetch all nodeinstances that are running
        this.instanceVarsHandler.addNodeInstanceFindLogic(newTerminationPlan,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED&amp;serviceInstanceId=$bpelvar[" + serviceInstanceId + "]",
            serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newTerminationPlan, propMap,
            serviceTemplate);

        this.instanceVarsHandler.addRelationInstanceFindLogic(newTerminationPlan, "?state=CREATED&amp;state=INITIAL&amp;serviceInstanceId=$bpelvar[" + serviceInstanceId + "]",
            serviceTemplate);

        final List<BPELScope> changedActivities = runPlugins(newTerminationPlan, propMap, csarName);

        String serviceInstanceURLVarName =
            this.serviceInstanceHandler.findServiceInstanceUrlVariableName(newTerminationPlan);

        this.serviceInstanceHandler.appendSetServiceInstanceState(newTerminationPlan,
            newTerminationPlan.getBpelMainFlowElement(),
            "DELETING", serviceInstanceURLVarName);

        this.serviceInstanceHandler.appendSetServiceInstanceState(newTerminationPlan,
            newTerminationPlan.getBpelMainSequenceCallbackInvokeElement(),
            "DELETED", serviceInstanceURLVarName);

        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(newTerminationPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newTerminationPlan),
            "ERROR", serviceInstanceURLVarName);
        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(newTerminationPlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newTerminationPlan), "FAILED", this.serviceInstanceHandler.findPlanInstanceUrlVariableName(newTerminationPlan));

        this.correlationHandler.addCorrellationID(newTerminationPlan);

        String planInstanceUrlVarName = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(newTerminationPlan);
        this.serviceInstanceHandler.appendSetServiceInstanceState(newTerminationPlan,
            newTerminationPlan.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceHandler.appendSetServiceInstanceState(newTerminationPlan,
            newTerminationPlan.getBpelMainSequenceOutputAssignElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newTerminationPlan);

        // add for each loop over found node and relation instances to terminate each running
        // instance
        for (final BPELScope activ : changedActivities) {
            if (activ.getNodeTemplate() != null) {
                final BPELPlanContext context =
                    new BPELPlanContext(scopeBuilder, newTerminationPlan, activ, propMap, newTerminationPlan.getServiceTemplate(),
                        serviceInstanceURLVarName, serviceInstanceId, serviceTemplateURLVarName, planInstanceUrl, csarName);
                this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED&amp;serviceInstanceId=$bpelvar[" + serviceInstanceId + "]");
            } else {
                final BPELPlanContext context =
                    new BPELPlanContext(scopeBuilder, newTerminationPlan, activ, propMap, newTerminationPlan.getServiceTemplate(),
                        serviceInstanceURLVarName, serviceInstanceId, serviceTemplateURLVarName, planInstanceUrl, csarName);
                this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getRelationshipTemplate(),
                    "?state=CREATED&amp;state=INITIAL&amp;serviceInstanceId=$bpelvar[" + serviceInstanceId + "]");
            }
        }

        return newTerminationPlan;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions)
     */
    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (!serviceTemplate.hasBuildPlan()) {
                LOG.debug("ServiceTemplate {} has no TerminationPlan, generating TerminationPlan",
                    serviceTemplate.getQName().toString());
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

                if (newBuildPlan != null) {
                    LOG.debug("Created TerminationPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                LOG.debug("ServiceTemplate {} has TerminationPlan, no generation needed",
                    serviceTemplate.getQName().toString());
            }
        }
        if(!plans.isEmpty()) {
        	LOG.info("Created {} termination plans for CSAR {}", String.valueOf(plans.size()), csarName);
        }
        return plans;
    }

    /**
     * This method will execute plugins on each TemplatePlan inside the given plan for termination of each node and
     * relation.
     *
     * @param plan            the plan to execute the plugins on
     * @param serviceTemplate the serviceTemplate the plan belongs to
     * @param propMap         a PropertyMapping from NodeTemplate to Properties to BPELVariables
     */
    private List<BPELScope> runPlugins(final BPELPlan plan, final Property2VariableMapping propMap, String csarName) {

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(plan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);
        String planInstanceUrl = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(plan);

        final List<BPELScope> changedActivities = new ArrayList<>();
        for (final BPELScope bpelScope : plan.getTemplateBuildPlans()) {
            boolean result = false;
            if (bpelScope.getNodeTemplate() != null) {
                final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, plan, bpelScope, propMap, plan.getServiceTemplate(),
                    serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csarName);
                result = this.bpelPluginHandler.handleActivity(context, bpelScope, nodeTemplate);
            } else {
                AbstractRelationshipTemplate relationshipTempalte = bpelScope.getRelationshipTemplate();
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, plan, bpelScope, propMap, plan.getServiceTemplate(),
                    serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csarName);
                result = this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTempalte);
            }

            if (result) {
                changedActivities.add(bpelScope);
            }
        }
        return changedActivities;
    }
}
