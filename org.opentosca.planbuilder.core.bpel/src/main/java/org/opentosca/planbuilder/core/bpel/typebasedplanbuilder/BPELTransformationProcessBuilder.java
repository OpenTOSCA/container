package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.opentosca.planbuilder.AbstractTransformingPlanbuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SituationTriggerRegistration;
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
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;

public class BPELTransformationProcessBuilder extends AbstractTransformingPlanbuilder {

    // class for initializing properties inside the plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans

    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private BPELPlanHandler planHandler;

    private BPELPluginHandler bpelPluginHandler = new BPELPluginHandler();

    private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;

    private final EmptyPropertyToInputHandler emptyPropInit = new EmptyPropertyToInputHandler();

    private CorrelationIDInitializer correlationHandler;

    public BPELTransformationProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        }
        catch (final ParserConfigurationException e) {
            BPELBuildProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        // TODO seems ugly
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
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
                                                                                       sourceServiceTemplate);

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
                final BPELPlanContext context = new BPELPlanContext(transformationBPELPlan, scope, sourcePropMap,
                    transformationBPELPlan.getServiceTemplate(), sourceServiceInstanceURL, sourceServiceInstanceID,
                    sourceServiceTemplateURL, sourceCsarName);
                this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getNodeTemplate(),
                                                                           "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
            } else {
                final BPELPlanContext context = new BPELPlanContext(transformationBPELPlan, scope, sourcePropMap,
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
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
        }
        for (BPELScope scope : this.getProvisioningScopes(plan)) {
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
        }

        for (BPELScope scope : this.getMigrationScopes(plan)) {
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, targetServiceTemplate);
            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, targetServiceTemplate);
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
                    final BPELPlanContext context = new BPELPlanContext(buildPlan, bpelScope, targetServiceTemplateMap,
                        targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
                        targetServiceTemplateUrl, targetCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate());
                } else if (activity.getType().equals(ActivityType.TERMINATION)) {
                    final BPELPlanContext context = new BPELPlanContext(buildPlan, bpelScope, sourceServiceTemplateMap,
                        sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
                        sourceServiceTemplateUrl, sourceCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate());
                } else if (activity.getType().equals(ActivityType.MIGRATION)) {

                    AbstractNodeTemplate sourceNodeTemplate = bpelScope.getNodeTemplate();
                    AbstractNodeTemplate targetNodeTemplate =
                        this.getCorrespondingNode(bpelScope.getNodeTemplate(),
                                                  targetServiceTemplate.getTopologyTemplate().getNodeTemplates());

                    final BPELPlanContext sourceContext = new BPELPlanContext(buildPlan, bpelScope,
                        sourceServiceTemplateMap, sourceServiceTemplate, sourceServiceInstanceUrl,
                        sourceServiceInstanceId, sourceServiceTemplateUrl, sourceCsarName);

                    final BPELPlanContext targetContext = new BPELPlanContext(buildPlan, bpelScope,
                        targetServiceTemplateMap, targetServiceTemplate, targetServiceInstanceUrl,
                        targetServiceInstanceId, targetServiceTemplateUrl, targetCsarName);

                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleUpdate(sourceNodeTemplate, targetNodeTemplate)) {
                            postPhasePlugin.handleUpdate(sourceContext, targetContext, sourceNodeTemplate,
                                                         targetNodeTemplate);
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
                    final BPELPlanContext context = new BPELPlanContext(buildPlan, bpelScope, targetServiceTemplateMap,
                        targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
                        targetServiceTemplateUrl, targetCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate());
                } else if (activity.getType().equals(ActivityType.TERMINATION)) {
                    final BPELPlanContext context = new BPELPlanContext(buildPlan, bpelScope, sourceServiceTemplateMap,
                        sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
                        sourceServiceTemplateUrl, sourceCsarName);
                    this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate());
                } else if (activity.getType().equals(ActivityType.MIGRATION)) {

                    AbstractRelationshipTemplate sourceNodeTemplate = bpelScope.getRelationshipTemplate();
                    AbstractRelationshipTemplate targetNodeTemplate =
                        this.getCorrespondingEdge(bpelScope.getRelationshipTemplate(),
                                                  targetServiceTemplate.getTopologyTemplate()
                                                                       .getRelationshipTemplates());

                    final BPELPlanContext sourceContext = new BPELPlanContext(buildPlan, bpelScope,
                        sourceServiceTemplateMap, sourceServiceTemplate, sourceServiceInstanceUrl,
                        sourceServiceInstanceId, sourceServiceTemplateUrl, sourceCsarName);

                    final BPELPlanContext targetContext = new BPELPlanContext(buildPlan, bpelScope,
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
