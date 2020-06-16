package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractTransformingPlanbuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractTransformationPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BPELTransformationProcessBuilder extends AbstractTransformingPlanbuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BPELTransformationProcessBuilder.class);
    // class for initializing properties inside the plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    private final BPELScopeBuilder scopeBuilder;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans

    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private BPELPlanHandler planHandler;

    private BPELPluginHandler bpelPluginHandler;

    private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;

    private final EmptyPropertyToInputHandler emptyPropInit;

    private CorrelationIDInitializer correlationHandler;

    @Inject
    public BPELTransformationProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        this.bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        this.scopeBuilder = new BPELScopeBuilder(pluginRegistry);
        this.emptyPropInit = new EmptyPropertyToInputHandler(scopeBuilder);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        // TODO seems ugly
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
    }

    /**
     * Creates an Adaptation PLan that can change the configuration of a running Service Instance by transforming the
     * current state of nodes and relations (sourceNodeTemplates and -RelationshipTemplates) to a target configuration
     * (targetNodeTemplates and -RelationshipTemplates).
     *
     * @param csarName                    the csar of the service template
     * @param definitions                 the definitions document of th service template
     * @param serviceTemplateId           the id of the serviceTemplate to adapt its service instance
     * @param sourceNodeTemplates         the nodeTemplates to adapt from
     * @param sourceRelationshipTemplates the relationships to adapt from
     * @param targetNodeTemplates         the target configuration of nodes to adapt to
     * @param targetRelationshipTemplates the target configuration of relations to adapt to
     * @return a BPEL Plan that is able to adapt an instance from the given current and target configurations
     */
    public BPELPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId,
                              Collection<AbstractNodeTemplate> sourceNodeTemplates,
                              Collection<AbstractRelationshipTemplate> sourceRelationshipTemplates,
                              Collection<AbstractNodeTemplate> targetNodeTemplates,
                              Collection<AbstractRelationshipTemplate> targetRelationshipTemplates) {
        AbstractServiceTemplate serviceTemplate = this.getServiceTemplate(definitions, serviceTemplateId);

        // generate abstract plan
        AbstractTransformationPlan adaptationPlan =
            this.generateTFOG(csarName, definitions, serviceTemplate, sourceNodeTemplates, sourceRelationshipTemplates,
                csarName, definitions, serviceTemplate, targetNodeTemplates, targetRelationshipTemplates);

        // transform to bpel skeleton
        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_adaptationPlan_" + System.currentTimeMillis());
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_adaptiationPlan";

        BPELPlan transformationBPELPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, adaptationPlan, "adapt");

        transformationBPELPlan.setTOSCAInterfaceName("OpenTOSCA-Transformation-Interface");
        transformationBPELPlan.setTOSCAOperationname("adapt");

        this.planHandler.initializeBPELSkeleton(transformationBPELPlan, csarName);
        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            transformationBPELPlan);

        // set instance ids for relationships and nodes
        this.addNodeRelationInstanceVariables(transformationBPELPlan, serviceTemplate, serviceTemplate);

        // generate variables for properties
        final Property2VariableMapping sourcesProp2VarMap =
            this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan, serviceTemplate,
                adaptationPlan.getHandledSourceServiceTemplateNodes(),
                adaptationPlan.getHandledSourceServiceTemplateRelations());

        final Property2VariableMapping targetsProp2VarMap =
            this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan, serviceTemplate,
                adaptationPlan.getHandledTargetServiceTemplateNodes(),
                adaptationPlan.getHandledTargetServiceTemplateRelations());

        // add correlation id and handling for input and output
        this.correlationHandler.addCorrellationID(transformationBPELPlan);

        // service instance handling
        String sourceServiceInstancesURL =
            this.serviceInstanceHandler.addInstanceDataAPIURLVariable(transformationBPELPlan);

        String serviceTemplateURL = this.serviceInstanceHandler.addServiceTemplateURLVariable(transformationBPELPlan);

        String serviceInstanceID = this.serviceInstanceHandler.addServiceInstanceIDVariable(transformationBPELPlan);

        String serviceInstanceURL = this.serviceInstanceHandler.addServiceInstanceURLVariable(transformationBPELPlan);

        String planInstanceURL = this.serviceInstanceHandler.addPlanInstanceURLVariable(transformationBPELPlan);

        // handle sourceinstance information, e.g., load instance url/, template url and
        // properties
        // append reading source service instance from input and setting created
        // variables
        this.serviceInstanceHandler.addServiceInstanceHandlingFromInput(transformationBPELPlan,
            sourceServiceInstancesURL, serviceInstanceURL,
            serviceTemplateURL, serviceInstanceID,
            planInstanceURL);

        // load nodeTemplate properties from source service instance
        Collection<BPELScope> terminationScopes = this.getTerminationScopes(transformationBPELPlan);
        this.serviceInstanceHandler.appendInitPropertyVariablesFromServiceInstanceData(transformationBPELPlan,
            sourcesProp2VarMap, serviceTemplateURL,
            terminationScopes,
            serviceTemplate, "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // return created service instance
        this.serviceInstanceHandler.appendAssignServiceInstanceIdToOutput(transformationBPELPlan, serviceInstanceID);

        // we need only input for instances that will be created in the target, deleted or migrated node
        // instances should never get data from the input
        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(this.getProvisioningScopes(transformationBPELPlan),
            transformationBPELPlan, sourcesProp2VarMap,
            serviceInstanceURL, serviceInstanceID,
            serviceTemplateURL, serviceTemplate, csarName);

        for (BPELScope scope : terminationScopes) {
            if (scope.getNodeTemplate() != null) {
                this.nodeRelationInstanceHandler.addNodeInstanceFindLogic(scope, serviceTemplateURL,
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                    serviceTemplate);
                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(scope, sourcesProp2VarMap,
                    serviceTemplate);
            } else {
                this.nodeRelationInstanceHandler.addRelationInstanceFindLogic(scope, serviceTemplateURL,
                    "?state=CREATED&amp;state=INITIAL",
                    serviceTemplate);

                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnRelationInstanceID(scope, sourcesProp2VarMap,
                    serviceTemplate);
            }
        }

        for (BPELScope scope : getMigrationScopes(transformationBPELPlan)) {
            if (scope.getNodeTemplate() != null) {
                this.nodeRelationInstanceHandler.addNodeInstanceFindLogic(scope, serviceTemplateURL,
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                    serviceTemplate);
                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(scope, sourcesProp2VarMap,
                    serviceTemplate);
            } else {
                this.nodeRelationInstanceHandler.addRelationInstanceFindLogic(scope, serviceTemplateURL,
                    "?state=CREATED&amp;state=INITIAL",
                    serviceTemplate);

                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnRelationInstanceID(scope, sourcesProp2VarMap,
                    serviceTemplate);
            }
        }

        this.runPlugins(transformationBPELPlan, sourcesProp2VarMap, targetsProp2VarMap, csarName, serviceTemplate, serviceInstanceURL,
            serviceInstanceID, serviceTemplateURL, csarName, serviceTemplate, serviceInstanceURL,
            serviceInstanceID, serviceTemplateURL);

        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainFlowElement(),
            "ADAPTING", serviceInstanceURL);

        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainSequenceOutputAssignElement(),
            "CREATED", serviceInstanceURL);

        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(transformationBPELPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(transformationBPELPlan),
            "ERROR", serviceInstanceURL);
        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(transformationBPELPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(transformationBPELPlan),
            "FAILED",
            this.serviceInstanceHandler.findPlanInstanceUrlVariableName(transformationBPELPlan));

        this.finalizer.finalize(transformationBPELPlan);

        // iterate over terminated nodes and create for each loop per instance
        for (BPELScope scope : terminationScopes) {
            if (scope.getNodeTemplate() != null) {
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, transformationBPELPlan, scope, sourcesProp2VarMap,
                    transformationBPELPlan.getServiceTemplate(), serviceInstanceURL, serviceInstanceID,
                    serviceTemplateURL, csarName);
                this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getNodeTemplate(),
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
            } else {
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, transformationBPELPlan, scope, sourcesProp2VarMap,
                    transformationBPELPlan.getServiceTemplate(), serviceInstanceURL, serviceInstanceID,
                    serviceTemplateURL, csarName);
                this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getRelationshipTemplate(),
                    "?state=CREATED&amp;state=INITIAL");
            }
        }

        return transformationBPELPlan;
    }

    @Override
    public BPELPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                              QName sourceServiceTemplateId, String targetCsarName,
                              AbstractDefinitions targetDefinitions, QName targetServiceTemplateId) {

        AbstractServiceTemplate sourceServiceTemplate = null;
        AbstractServiceTemplate targetServiceTemplate = null;
        sourceServiceTemplate = this.getServiceTemplate(sourceDefinitions, sourceServiceTemplateId);
        targetServiceTemplate = this.getServiceTemplate(targetDefinitions, targetServiceTemplateId);

        // generate abstract plan
        AbstractTransformationPlan transformationPlan =
            this.generateTFOG(sourceCsarName, sourceDefinitions, sourceServiceTemplate, targetCsarName,
                targetDefinitions, targetServiceTemplate);

        // transform to bpel skeleton
        final String processName = ModelUtils.makeValidNCName(sourceServiceTemplate.getId() + "_transformTo_"
            + targetServiceTemplate.getId() + "_plan");
        final String processNamespace = sourceServiceTemplate.getTargetNamespace() + "_transformPlan";

        BPELPlan transformationBPELPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, transformationPlan, "transform");

        transformationBPELPlan.setTOSCAInterfaceName("OpenTOSCA-Transformation-Interface");
        transformationBPELPlan.setTOSCAOperationname("transform");

        this.planHandler.initializeBPELSkeleton(transformationBPELPlan, sourceCsarName);
        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            transformationBPELPlan);

        // set instance ids for relationships and nodes
        this.addNodeRelationInstanceVariables(transformationBPELPlan, sourceServiceTemplate, targetServiceTemplate);

        // generate variables for properties
        final Property2VariableMapping sourcePropMap =
            this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan, sourceServiceTemplate,
                transformationPlan.getHandledSourceServiceTemplateNodes(),
                transformationPlan.getHandledSourceServiceTemplateRelations());
        final Property2VariableMapping targetPropMap =
            this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan, targetServiceTemplate,
                transformationPlan.getHandledTargetServiceTemplateNodes(),
                transformationPlan.getHandledTargetServiceTemplateRelations());

        // add correlation id and handling for input and output
        this.correlationHandler.addCorrellationID(transformationBPELPlan);

        // service instance handling
        String sourceServiceInstancesURL =
            this.serviceInstanceHandler.addInstanceDataAPIURLVariable(transformationBPELPlan);
        String targetServiceInstancesURL =
            this.serviceInstanceHandler.addInstanceDataAPIURLVariable(transformationBPELPlan);
        String sourceServiceTemplateURL =
            this.serviceInstanceHandler.addServiceTemplateURLVariable(transformationBPELPlan);
        String targetServiceTemplateURL =
            this.serviceInstanceHandler.addServiceTemplateURLVariable(transformationBPELPlan);
        String sourceServiceInstanceID =
            this.serviceInstanceHandler.addServiceInstanceIDVariable(transformationBPELPlan);
        String targetServiceInstanceID =
            this.serviceInstanceHandler.addServiceInstanceIDVariable(transformationBPELPlan);
        String sourceServiceInstanceURL =
            this.serviceInstanceHandler.addServiceInstanceURLVariable(transformationBPELPlan);
        String targetServiceInstanceURL =
            this.serviceInstanceHandler.addServiceInstanceURLVariable(transformationBPELPlan);

        String planInstanceURL = this.serviceInstanceHandler.addPlanInstanceURLVariable(transformationBPELPlan);

        // handle sourceinstance information, e.g., load instance url/, template url and
        // properties
        // append reading source service instance from input and setting created
        // variables
        this.serviceInstanceHandler.addServiceInstanceHandlingFromInput(transformationBPELPlan,
            sourceServiceInstancesURL,
            sourceServiceInstanceURL,
            sourceServiceTemplateURL,
            sourceServiceInstanceID, planInstanceURL);

        // load nodeTemplate properties from source service instance
        Collection<BPELScope> terminationScopes = this.getTerminationScopes(transformationBPELPlan);
        this.serviceInstanceHandler.appendInitPropertyVariablesFromServiceInstanceData(transformationBPELPlan,
            sourcePropMap,
            sourceServiceTemplateURL,
            terminationScopes,
            sourceServiceTemplate, "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // handle target service instance information
        this.serviceInstanceHandler.initServiceInstancesURLVariableFromAvailableServiceInstanceUrlVar(transformationBPELPlan,
            sourceServiceInstancesURL,
            targetServiceTemplateId,
            targetCsarName,
            targetServiceInstancesURL);
        // create service instance for target
        this.serviceInstanceHandler.appendCreateServiceInstance(transformationBPELPlan, targetServiceInstancesURL,
            targetServiceInstanceURL, targetServiceInstanceID,
            targetServiceTemplateURL, planInstanceURL, true);

        // return created service instance
        this.serviceInstanceHandler.appendAssignServiceInstanceIdToOutput(transformationBPELPlan,
            targetServiceInstanceID);

        // we need only input for instances that will be created in the target, deleted or migrated node
        // instances should never get data from the input
        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(this.getProvisioningScopes(transformationBPELPlan),
            transformationBPELPlan, targetPropMap,
            targetServiceInstanceURL, targetServiceInstanceID,
            targetServiceTemplateURL, targetServiceTemplate,
            targetCsarName);

        for (BPELScope scope : terminationScopes) {
            if (scope.getNodeTemplate() != null) {
                this.nodeRelationInstanceHandler.addNodeInstanceFindLogic(scope, sourceServiceTemplateURL,
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                    sourceServiceTemplate);
                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(scope, sourcePropMap,
                    sourceServiceTemplate);
            } else {
                this.nodeRelationInstanceHandler.addRelationInstanceFindLogic(scope, sourceServiceTemplateURL,
                    "?state=CREATED&amp;state=INITIAL",
                    sourceServiceTemplate);

                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnRelationInstanceID(scope,
                    sourcePropMap,
                    sourceServiceTemplate);
            }
        }

        for (BPELScope scope : getMigrationScopes(transformationBPELPlan)) {
            if (scope.getNodeTemplate() != null) {
                this.nodeRelationInstanceHandler.addNodeInstanceFindLogic(scope, sourceServiceTemplateURL,
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                    sourceServiceTemplate);
                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(scope, sourcePropMap,
                    sourceServiceTemplate);
            } else {
                this.nodeRelationInstanceHandler.addRelationInstanceFindLogic(scope, sourceServiceTemplateURL,
                    "?state=CREATED&amp;state=INITIAL",
                    sourceServiceTemplate);

                this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnRelationInstanceID(scope,
                    sourcePropMap,
                    sourceServiceTemplate);
            }
        }

        this.runPlugins(transformationBPELPlan, sourcePropMap, targetPropMap, sourceCsarName, sourceServiceTemplate,
            sourceServiceInstanceURL, sourceServiceInstanceID, sourceServiceTemplateURL, targetCsarName,
            targetServiceTemplate, targetServiceInstanceURL, targetServiceInstanceID,
            targetServiceTemplateURL);

        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainFlowElement(),
            "MIGRATING", sourceServiceInstanceURL);
        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainFlowElement(),
            "CREATING", targetServiceInstanceURL);
        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainSequenceOutputAssignElement(),
            "MIGRATED", sourceServiceInstanceURL);
        this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
            transformationBPELPlan.getBpelMainSequenceOutputAssignElement(),
            "CREATED", targetServiceInstanceURL);

        this.finalizer.finalize(transformationBPELPlan);

        // iterate over terminated nodes and create for each loop per instance
        for (BPELScope scope : terminationScopes) {
            if (scope.getNodeTemplate() != null) {
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, transformationBPELPlan, scope, sourcePropMap,
                    transformationBPELPlan.getServiceTemplate(), sourceServiceInstanceURL, sourceServiceInstanceID,
                    sourceServiceTemplateURL, sourceCsarName);
                this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getNodeTemplate(),
                    "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
            } else {
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, transformationBPELPlan, scope, sourcePropMap,
                    transformationBPELPlan.getServiceTemplate(), sourceServiceInstanceURL, sourceServiceInstanceID,
                    sourceServiceTemplateURL, sourceCsarName);
                this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getRelationshipTemplate(),
                    "?state=CREATED&amp;state=INITIAL");
            }
        }

        return transformationBPELPlan;
    }

    private Collection<BPELScope> getMigrationScopes(BPELPlan plan) {
        return this.getScopesByType(plan, ActivityType.MIGRATION);
    }

    private Collection<BPELScope> getScopesByType(BPELPlan plan, ActivityType type) {
        Collection<BPELScope> scopes = new HashSet<BPELScope>();
        for (AbstractActivity act : plan.getAbstract2BPEL().keySet()) {
            if (act.getType().equals(type)) {
                scopes.add(plan.getAbstract2BPEL().get(act));
            }
        }
        return scopes;
    }

    private Collection<BPELScope> getTerminationScopes(BPELPlan plan) {
        return this.getScopesByType(plan, ActivityType.TERMINATION);
    }

    private Collection<BPELScope> getProvisioningScopes(BPELPlan plan) {
        return this.getScopesByType(plan, ActivityType.PROVISIONING);
    }

    private AbstractServiceTemplate getServiceTemplate(AbstractDefinitions defs, QName serviceTemplateId) {
        for (AbstractServiceTemplate servTemplate : defs.getServiceTemplates()) {
            if (servTemplate.getQName().equals(serviceTemplateId)) {
                return servTemplate;
            }
        }
        return null;
    }

    private void addNodeRelationInstanceVariables(BPELPlan plan, AbstractServiceTemplate sourceServiceTemplate,
                                                  AbstractServiceTemplate targetServiceTemplate) {
        for (BPELScope scope : this.getTerminationScopes(plan)) {
            boolean added =
                this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            }

            added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
            }
        }
        for (BPELScope scope : this.getProvisioningScopes(plan)) {
            boolean added =
                this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            }

            added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
            }
        }

        for (BPELScope scope : this.getMigrationScopes(plan)) {
            boolean added =
                this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            }

            added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
            }

            added = this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            }

            added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
            while (!added) {
                added = this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
            }
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                         String targetCsarName, AbstractDefinitions targetDefinitions) {
        // TODO Auto-generated method stub
        return null;
    }

    private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping sourceServiceTemplateMap,
                            final Property2VariableMapping targetServiceTemplateMap, String sourceCsarName,
                            AbstractServiceTemplate sourceServiceTemplate, String sourceServiceInstanceUrl,
                            String sourceServiceInstanceId, String sourceServiceTemplateUrl, String targetCsarName,
                            AbstractServiceTemplate targetServiceTemplate, String targetServiceInstanceUrl,
                            String targetServiceInstanceId, String targetServiceTemplateUrl) {

        for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {

            if (bpelScope.getNodeTemplate() != null) {

                AbstractActivity activity = bpelScope.getActivity();

                if (activity.getType().equals(ActivityType.PROVISIONING)) {
                    final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope, targetServiceTemplateMap,
                        targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
                        targetServiceTemplateUrl, targetCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate());
                } else if (activity.getType().equals(ActivityType.TERMINATION)) {
                    final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope, sourceServiceTemplateMap,
                        sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
                        sourceServiceTemplateUrl, sourceCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate());
                } else if (activity.getType().equals(ActivityType.MIGRATION)) {

                    AbstractNodeTemplate sourceRelationshipTemplate = bpelScope.getNodeTemplate();
                    AbstractNodeTemplate targetRelationshipTemplate =
                        this.getCorrespondingNode(bpelScope.getNodeTemplate(),
                            targetServiceTemplate.getTopologyTemplate().getNodeTemplates());

                    final BPELPlanContext sourceContext = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope,
                        sourceServiceTemplateMap, sourceServiceTemplate, sourceServiceInstanceUrl,
                        sourceServiceInstanceId, sourceServiceTemplateUrl, sourceCsarName);

                    final BPELPlanContext targetContext = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope,
                        targetServiceTemplateMap, targetServiceTemplate, targetServiceInstanceUrl,
                        targetServiceInstanceId, targetServiceTemplateUrl, targetCsarName);

                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleUpdate(sourceRelationshipTemplate, targetRelationshipTemplate)) {
                            postPhasePlugin.handleUpdate(sourceContext, targetContext, sourceRelationshipTemplate,
                                targetRelationshipTemplate);
                        }
                    }
                }
                // if this nodeTemplate has the label running (Property: State=Running), skip
                // provisioning and just generate instance data handlin

                // generate code for the activity
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handling relationshiptemplate

                AbstractActivity activity = bpelScope.getActivity();
                if (activity.getType().equals(ActivityType.PROVISIONING)) {
                    final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope, targetServiceTemplateMap,
                        targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
                        targetServiceTemplateUrl, targetCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate());
                } else if (activity.getType().equals(ActivityType.TERMINATION)) {
                    final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope, sourceServiceTemplateMap,
                        sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
                        sourceServiceTemplateUrl, sourceCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate());
                } else if (activity.getType().equals(ActivityType.MIGRATION)) {

                    AbstractRelationshipTemplate sourceNodeTemplate = bpelScope.getRelationshipTemplate();
                    AbstractRelationshipTemplate targetNodeTemplate =
                        this.getCorrespondingEdge(bpelScope.getRelationshipTemplate(),
                            targetServiceTemplate.getTopologyTemplate()
                                .getRelationshipTemplates());

                    final BPELPlanContext sourceContext = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope,
                        sourceServiceTemplateMap, sourceServiceTemplate, sourceServiceInstanceUrl,
                        sourceServiceInstanceId, sourceServiceTemplateUrl, sourceCsarName);

                    final BPELPlanContext targetContext = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope,
                        targetServiceTemplateMap, targetServiceTemplate, targetServiceInstanceUrl,
                        targetServiceInstanceId, targetServiceTemplateUrl, targetCsarName);

                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleUpdate(sourceNodeTemplate, targetNodeTemplate)) {
                            postPhasePlugin.handleUpdate(sourceContext, targetContext, sourceNodeTemplate,
                                targetNodeTemplate);
                        }
                    }
                }
            }
        }
    }
}
