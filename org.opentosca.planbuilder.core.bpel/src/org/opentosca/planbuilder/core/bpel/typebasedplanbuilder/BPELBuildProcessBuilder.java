package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.OperationChain;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.EmptyPropertyToInputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.SituationTriggerRegistration;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL
 * 2.0 BuildPlans fuer OpenTOSCA</a>. It is responsible for generating the Build
 * Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 * </p>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELBuildProcessBuilder extends AbstractBuildPlanBuilder {

	final static Logger LOG = LoggerFactory.getLogger(BPELBuildProcessBuilder.class);

	// class for initializing properties inside the plan
	private final PropertyVariableInitializer propertyInitializer;
	// class for initializing output with boundarydefinitions of a
	// serviceTemplate
	private final PropertyMappingsToOutputInitializer propertyOutputInitializer;
	// adds serviceInstance Variable and instanceDataAPIUrl to buildPlans

	private ServiceInstanceVariablesHandler serviceInstanceInitializer;

	private SituationTriggerRegistration sitRegistrationPlugin;

	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private final BPELFinalizer finalizer;

	private BPELPlanHandler planHandler;

	private BPELPluginHandler bpelPluginHandler = new BPELPluginHandler();

	private NodeRelationInstanceVariablesHandler instanceInit;

	private final EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();

	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	public BPELBuildProcessBuilder() {
		try {
			this.planHandler = new BPELPlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceVariablesHandler();
			this.instanceInit = new NodeRelationInstanceVariablesHandler(this.planHandler);
			this.sitRegistrationPlugin = new SituationTriggerRegistration();
		} catch (final ParserConfigurationException e) {
			BPELBuildProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		// TODO seems ugly
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		this.propertyOutputInitializer = new PropertyMappingsToOutputInitializer();
		this.finalizer = new BPELFinalizer();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions,
	 * javax.xml.namespace.QName)
	 */
	@Override
	public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
			final QName serviceTemplateId) {
		// create empty plan from servicetemplate and add definitions

		for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			String namespace;
			if (serviceTemplate.getTargetNamespace() != null) {
				namespace = serviceTemplate.getTargetNamespace();
			} else {
				namespace = definitions.getTargetNamespace();
			}

			if (namespace.equals(serviceTemplateId.getNamespaceURI())
					&& serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {

				final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_buildPlan");
				final String processNamespace = serviceTemplate.getTargetNamespace() + "_buildPlan";

				final AbstractPlan buildPlan = this.generatePOG(new QName(processNamespace, processName).toString(),
						definitions, serviceTemplate);

				LOG.debug("Generated the following abstract prov plan: ");
				LOG.debug(buildPlan.toString());

				final BPELPlan newBuildPlan = this.planHandler.createEmptyBPELPlan(processNamespace, processName,
						buildPlan, "initiate");

				newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
				newBuildPlan.setTOSCAOperationname("initiate");

				this.planHandler.initializeBPELSkeleton(newBuildPlan, csarName);

				this.instanceInit.addInstanceURLVarToTemplatePlans(newBuildPlan);
				this.instanceInit.addInstanceIDVarToTemplatePlans(newBuildPlan);

				// newBuildPlan.setCsarName(csarName);

				this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
						newBuildPlan);

				final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan);
				// init output
				this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap);

				// instanceDataAPI handling is done solely trough this extension

				// initialize instanceData handling
				this.serviceInstanceInitializer.initializeInstanceDataFromInput(newBuildPlan);

				this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newBuildPlan, propMap);

				runPlugins(newBuildPlan, propMap);

				this.serviceInstanceInitializer.addCorrellationID(newBuildPlan);

				this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
						newBuildPlan.getBpelMainFlowElement(), "CREATING");
				this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
						newBuildPlan.getBpelMainSequenceOutputAssignElement(), "CREATED");

				this.sitRegistrationPlugin.handle(serviceTemplate, newBuildPlan);

				this.finalizer.finalize(newBuildPlan);
				return newBuildPlan;
			}
		}
		BPELBuildProcessBuilder.LOG.warn(
				"Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
				serviceTemplateId.toString(), definitions.getId(), csarName);
		return null;
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
			QName serviceTemplateId;
			// targetNamespace attribute doesn't has to be set, so we check it
			if (serviceTemplate.getTargetNamespace() != null) {
				serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
			} else {
				serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
			}

			if (!serviceTemplate.hasBuildPlan()) {
				BPELBuildProcessBuilder.LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
						serviceTemplateId.toString());
				final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

				if (newBuildPlan != null) {
					BPELBuildProcessBuilder.LOG
							.debug("Created BuildPlan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
					plans.add(newBuildPlan);
				}
			} else {
				BPELBuildProcessBuilder.LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
						serviceTemplateId.toString());
			}
		}
		return plans;
	}

	private boolean isRunning(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
		final Variable state = context.getPropertyVariable(nodeTemplate, "State");
		if (state != null) {
			if (BPELPlanContext.getVariableContent(state, context).equals("Running")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method assigns plugins to the already initialized BuildPlan and its
	 * TemplateBuildPlans. First there will be checked if any generic plugin can
	 * handle a template of the TopologyTemplate
	 * </p>
	 *
	 * @param buildPlan a BuildPlan which is alread initialized
	 * @param map       a PropertyMap which contains mappings from Template to
	 *                  Property and to variable name of inside the BuidlPlan
	 */
	private void runPlugins(final BPELPlan buildPlan, final PropertyMap map) {

		for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {
			final BPELPlanContext context = new BPELPlanContext(bpelScope, map, buildPlan.getServiceTemplate());
			if (bpelScope.getNodeTemplate() != null) {

				final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();

				// if this nodeTemplate has the label running (Property: State=Running), skip
				// provisioning and just generate instance data handling
				if (isRunning(context, nodeTemplate)) {
					BPELBuildProcessBuilder.LOG.debug("Skipping the provisioning of NodeTemplate "
							+ bpelScope.getNodeTemplate().getId() + "  beacuse state=running is set.");
					for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
						if (postPhasePlugin.canHandleCreate(bpelScope.getNodeTemplate())) {
							postPhasePlugin.handleCreate(context, bpelScope.getNodeTemplate());
						}
					}
					continue;
				}

				// generate code for the activity
				this.bpelPluginHandler.handleActivity(context, bpelScope, nodeTemplate, this
						.findNodeTemplateActivity(buildPlan.getActivites(), nodeTemplate, ActivityType.PROVISIONING));
			} else if (bpelScope.getRelationshipTemplate() != null) {
				// handling relationshiptemplate
				final AbstractRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

				this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate,
						this.findRelationshipTemplateActivity(buildPlan.getActivites(), relationshipTemplate,
								ActivityType.PROVISIONING));
			}

		}
	}

}
