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
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.tosca.handlers.SituationTriggerRegistration;
import org.opentosca.planbuilder.core.tosca.handlers.PropertyVariableHandler.Property2VariableMapping;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;

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
		} catch (final ParserConfigurationException e) {
			BPELBuildProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		// TODO seems ugly
		this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
		this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
		this.finalizer = new BPELFinalizer();
	}

	
	
	
	@Override
	public BPELPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			QName sourceServiceTemplateId, String targetCsarName, AbstractDefinitions targetDefinitions,
			QName targetServiceTemplateId) {

		AbstractServiceTemplate sourceServiceTemplate = null;
		AbstractServiceTemplate targetServiceTemplate = null;
		sourceServiceTemplate = this.getServiceTemplate(sourceDefinitions, sourceServiceTemplateId);
		targetServiceTemplate = this.getServiceTemplate(targetDefinitions, targetServiceTemplateId);

		// generate abstract plan
		AbstractPlan transformationPlan = this.generateTFOG(sourceCsarName, sourceDefinitions, sourceServiceTemplate,
				targetCsarName, targetDefinitions, targetServiceTemplate);

		// transform to bpel skeleton
		final String processName = ModelUtils.makeValidNCName(
				sourceServiceTemplate.getId() + "_transformTo_" + targetServiceTemplate.getId() + "_plan");
		final String processNamespace = sourceServiceTemplate.getTargetNamespace() + "_transformPlan";

		BPELPlan transformationBPELPlan = this.planHandler.createEmptyBPELPlan(processNamespace, processName,
				transformationPlan, "transform");

		transformationBPELPlan.setTOSCAInterfaceName("OpenTOSCA-Transformation-Interface");
		transformationBPELPlan.setTOSCAOperationname("transform");

		this.planHandler.initializeBPELSkeleton(transformationBPELPlan, sourceCsarName);
		// instanceDataAPI handling is done solely trough this extension
		this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
				transformationBPELPlan);

		// set instance ids for relationships and nodes
		this.addNodeRelationInstanceVariables(transformationBPELPlan, sourceServiceTemplate, targetServiceTemplate);		

		// generate variables for properties
		final Property2VariableMapping sourcePropMap = this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan,sourceServiceTemplate);
		final Property2VariableMapping targetPropMap = this.propertyInitializer.initializePropertiesAsVariables(transformationBPELPlan,targetServiceTemplate);

		// add correlation id and handling for input and output
		this.correlationHandler.addCorrellationID(transformationBPELPlan);

		// service instance handling
		String sourceServiceInstancesURL = this.serviceInstanceHandler
				.addInstanceDataAPIURLVariable(transformationBPELPlan);
		String targetServiceInstancesURL = this.serviceInstanceHandler
				.addInstanceDataAPIURLVariable(transformationBPELPlan);
		String sourceServiceTemplateURL = this.serviceInstanceHandler
				.addServiceTemplateURLVariable(transformationBPELPlan);
		String targetServiceTemplateURL = this.serviceInstanceHandler
				.addServiceTemplateURLVariable(transformationBPELPlan);
		String sourceServiceInstanceID = this.serviceInstanceHandler
				.addServiceInstanceIDVariable(transformationBPELPlan);
		String targetServiceInstanceID = this.serviceInstanceHandler
				.addServiceInstanceIDVariable(transformationBPELPlan);
		String sourceServiceInstanceURL = this.serviceInstanceHandler
				.addServiceInstanceURLVariable(transformationBPELPlan);
		String targetServiceInstanceURL = this.serviceInstanceHandler
				.addServiceInstanceURLVariable(transformationBPELPlan);

		String planInstanceURL = this.serviceInstanceHandler.addPlanInstanceURLVariable(transformationBPELPlan);

		// handle sourceinstance information, e.g., load instance url/, template url and
		// properties
		// append reading source service instance from input and setting created
		// variables
		this.serviceInstanceHandler.addServiceInstanceHandlingFromInput(transformationBPELPlan,
				sourceServiceInstancesURL, sourceServiceInstanceURL, sourceServiceTemplateURL, sourceServiceInstanceID,
				planInstanceURL);

		// load nodeTemplate properties from source service instance
		Collection<BPELScope> terminationScopes = this.getTerminationScopes(transformationBPELPlan);
		this.serviceInstanceHandler.appendInitPropertyVariablesFromServiceInstanceData(transformationBPELPlan, sourcePropMap,
				sourceServiceTemplateURL, terminationScopes,sourceServiceTemplate);

		// handle target service instance information
		this.serviceInstanceHandler.initServiceInstancesURLVariableFromAvailableServiceInstanceUrlVar(
				transformationBPELPlan, sourceServiceInstancesURL, targetServiceTemplateId, targetCsarName,
				targetServiceInstancesURL);
		// create service instance for target
		this.serviceInstanceHandler.appendCreateServiceInstance(transformationBPELPlan, targetServiceInstancesURL,
				targetServiceInstanceURL, targetServiceInstanceID, targetServiceTemplateURL, planInstanceURL, true);

		// return created service instance
		this.serviceInstanceHandler.appendAssignServiceInstanceIdToOutput(transformationBPELPlan,
				targetServiceInstanceID);
		
		// we need only input for instances that will be created in the target, deleted or migrated node instances should never get data from the input
		this.emptyPropInit.initializeEmptyPropertiesAsInputParam(this.getProvisioningScopes(transformationBPELPlan), transformationBPELPlan, targetPropMap, targetServiceInstanceURL, targetServiceInstanceID, targetServiceTemplateURL, targetServiceTemplate, targetCsarName);

		for(BPELScope scope : terminationScopes) {
			if(scope.getNodeTemplate() != null) {
				this.nodeRelationInstanceHandler.addNodeInstanceFindLogic(scope, sourceServiceTemplateURL,  "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED", sourceServiceTemplate);
				this.nodeRelationInstanceHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(scope, sourcePropMap, sourceServiceTemplate);
			}
		}
		
		this.runPlugins(transformationBPELPlan, sourcePropMap,targetPropMap, sourceCsarName, sourceServiceTemplate, sourceServiceInstanceURL, sourceServiceInstanceID,
				sourceServiceTemplateURL,targetCsarName, targetServiceTemplate, targetServiceInstanceURL, targetServiceInstanceID, targetServiceTemplateURL);


		this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
				transformationBPELPlan.getBpelMainSequenceOutputAssignElement(), "MIGRATED", sourceServiceInstanceURL);
		this.serviceInstanceHandler.appendSetServiceInstanceState(transformationBPELPlan,
				transformationBPELPlan.getBpelMainSequenceOutputAssignElement(), "CREATED", targetServiceInstanceURL);

		this.finalizer.finalize(transformationBPELPlan);

		// TODO iterate over terminated nodes and create for each loop per instance @See
		// Termination Plan Builder		
		for (BPELScope scope : terminationScopes) {
			if (scope.getNodeTemplate() != null) {
				final BPELPlanContext context = new BPELPlanContext(scope, sourcePropMap,
						transformationBPELPlan.getServiceTemplate(), sourceServiceInstanceURL, sourceServiceInstanceID,
						sourceServiceTemplateURL, sourceCsarName);
				this.nodeRelationInstanceHandler.appendCountInstancesLogic(context, scope.getNodeTemplate(),
						"?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
			}
		}

		return transformationBPELPlan;
	}

	private Collection<BPELScope> getTerminationScopes(BPELPlan plan) {
		Collection<BPELScope> terminationScopes = new HashSet<BPELScope>();
		for (AbstractActivity act : plan.getAbstract2BPEL().keySet()) {
			if (act.getType().equals(ActivityType.TERMINATION)) {
				terminationScopes.add(plan.getAbstract2BPEL().get(act));
			}
		}
		return terminationScopes;
	}
	
	private Collection<BPELScope> getProvisioningScopes(BPELPlan plan) {
		Collection<BPELScope> provisioningScopes = new HashSet<BPELScope>();
		for (AbstractActivity act : plan.getAbstract2BPEL().keySet()) {
			if (act.getType().equals(ActivityType.PROVISIONING)) {
				provisioningScopes.add(plan.getAbstract2BPEL().get(act));
			}
		}
		return provisioningScopes;
	}

	private AbstractServiceTemplate getServiceTemplate(AbstractDefinitions defs, QName serviceTemplateId) {
		for (AbstractServiceTemplate servTemplate : defs.getServiceTemplates()) {
			if (servTemplate.getQName().equals(serviceTemplateId)) {
				return servTemplate;
			}
		}
		return null;
	}
	
	private void addNodeRelationInstanceVariables(BPELPlan plan, AbstractServiceTemplate sourceServiceTemplate, AbstractServiceTemplate targetServiceTemplate) {
		for(BPELScope scope : this.getTerminationScopes(plan)) {
			this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlan(scope, sourceServiceTemplate);
			this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlan(scope, sourceServiceTemplate);
		}
		for(BPELScope scope : this.getProvisioningScopes(plan)) {
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

	private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping sourceServiceTemplateMap,final Property2VariableMapping targetServiceTemplateMap, String sourceCsarName, AbstractServiceTemplate sourceServiceTemplate, String sourceServiceInstanceUrl,
			String sourceServiceInstanceId, String sourceServiceTemplateUrl, String targetCsarName, AbstractServiceTemplate targetServiceTemplate, String targetServiceInstanceUrl,
			String targetServiceInstanceId, String targetServiceTemplateUrl) {

		for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {

			if (bpelScope.getNodeTemplate() != null) {
				
				for (AbstractActivity activity : buildPlan.findNodeTemplateActivities(bpelScope.getNodeTemplate())) {
					if (activity.getType().equals(ActivityType.PROVISIONING)) {
						final BPELPlanContext context = new BPELPlanContext(bpelScope, targetServiceTemplateMap,
								targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
								targetServiceTemplateUrl, targetCsarName);
						this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate(),
								activity);
					} else {
						final BPELPlanContext context = new BPELPlanContext(bpelScope, sourceServiceTemplateMap,
								sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
								sourceServiceTemplateUrl, sourceCsarName);
						this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getNodeTemplate(),
								activity);
					}
				}
				// if this nodeTemplate has the label running (Property: State=Running), skip
				// provisioning and just generate instance data handlin

				// generate code for the activity
			} else if (bpelScope.getRelationshipTemplate() != null) {
				// handling relationshiptemplate

				for (AbstractActivity activity : buildPlan
						.findRelationshipTemplateActivities(bpelScope.getRelationshipTemplate())) {
					if (activity.getType().equals(ActivityType.PROVISIONING)) {
						final BPELPlanContext context = new BPELPlanContext(bpelScope, targetServiceTemplateMap,
								targetServiceTemplate, targetServiceInstanceUrl, targetServiceInstanceId,
								targetServiceTemplateUrl,targetCsarName);
						this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate(),
								activity);
					} else {
						final BPELPlanContext context = new BPELPlanContext(bpelScope, sourceServiceTemplateMap,
								sourceServiceTemplate, sourceServiceInstanceUrl, sourceServiceInstanceId,
								sourceServiceTemplateUrl, sourceCsarName);
						this.bpelPluginHandler.handleActivity(context, bpelScope, bpelScope.getRelationshipTemplate(),
								activity);
					}
				}
			}

		}
	}

}
