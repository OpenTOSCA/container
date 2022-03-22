package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.ChoreographyBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SituationTriggerRegistration;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_BuildPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_LifecycleInterface;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>. It
 * is responsible for generating the Build Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 * </p>
 * <p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPELBuildProcessBuilder.class);

    // class for initializing properties inside the plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    private final BPELScopeBuilder scopeBuilder;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    private final BPELPluginHandler bpelPluginHandler;
    private final EmptyPropertyToInputHandler emptyPropInit;
    private final ChoreographyBuilder choreoBuilder = new ChoreographyBuilder();
    private final SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    private final CorrelationIDInitializer correlationHandler;
    private final SituationTriggerRegistration sitRegistrationPlugin;
    private final BPELPlanHandler planHandler;
    private final NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELBuildProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        this.bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        this.scopeBuilder = new BPELScopeBuilder(pluginRegistry);
        this.emptyPropInit = new EmptyPropertyToInputHandler(scopeBuilder);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.sitRegistrationPlugin = new SituationTriggerRegistration();
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
            throw new PlanbuilderRuntimeException("Error while initializing BuildPlanHandler", e);
        }
        // TODO seems ugly
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions, javax.xml.namespace.QName)
     */
    private BPELPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {
        // create empty plan from servicetemplate and add definitions
        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }

        QName serviceTemplateQname = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
        if (namespace.equals(serviceTemplateQname.getNamespaceURI())
            && serviceTemplate.getId().equals(serviceTemplateQname.getLocalPart())) {

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_buildPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_buildPlan";

            AbstractPlan buildPlan =
                AbstractBuildPlanBuilder.generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            if (this.choreoBuilder.isChoreographyPartner(serviceTemplate)) {
                LOG.debug("Transforming plan to be part of a choreography: ");
                buildPlan = this.choreoBuilder.transformToChoreography(buildPlan, csar);
            }

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPELPlan newBuildPlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, buildPlan, OpenTOSCA_BuildPlanOperation);

            newBuildPlan.setTOSCAInterfaceName(OpenTOSCA_LifecycleInterface);
            newBuildPlan.setTOSCAOperationname(OpenTOSCA_BuildPlanOperation);

            this.planHandler.initializeBPELSkeleton(newBuildPlan, csar);

            this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlans(newBuildPlan, serviceTemplate);
            this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlans(newBuildPlan, serviceTemplate);

            // newBuildPlan.setCsarName(csarName);

            this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                newBuildPlan);

            final Property2VariableMapping propMap =
                this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan, serviceTemplate);
            // init output
            this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap,
                serviceTemplate);

            // instanceDataAPI handling is done solely trough this extension

            // initialize instanceData handling
            this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newBuildPlan);

            String serviceInstanceUrl =
                this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newBuildPlan);
            String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newBuildPlan);
            String serviceTemplateUrl =
                this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newBuildPlan);
            String planInstanceUrl = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newBuildPlan);

            this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newBuildPlan, propMap, serviceInstanceUrl,
                serviceInstanceID, serviceTemplateUrl, planInstanceUrl,
                serviceTemplate, csar);

            runPlugins(newBuildPlan, propMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);

            this.correlationHandler.addCorrellationID(newBuildPlan);

            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainFlowElement(),
                "CREATING", serviceInstanceUrl);
            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainSequenceOutputAssignElement(),
                "CREATED", serviceInstanceUrl);

            this.serviceInstanceInitializer.appendSetServiceInstanceStateAsChild(newBuildPlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newBuildPlan), "ERROR", serviceInstanceUrl);
            this.serviceInstanceInitializer.appendSetServiceInstanceStateAsChild(newBuildPlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newBuildPlan), "FAILED", this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newBuildPlan));

            String planInstanceUrlVarName = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newBuildPlan);
            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainFlowElement(),
                "RUNNING", planInstanceUrlVarName);

            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainSequenceCallbackInvokeElement(),
                "FINISHED", planInstanceUrlVarName);

            this.sitRegistrationPlugin.handle(serviceTemplate, newBuildPlan);

            this.finalizer.finalize(newBuildPlan);
            return newBuildPlan;
        }

        LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplateQname, definitions.getId(), csar.id().csarName());
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions)
     */
    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (ModelUtils.findServiceTemplateOperation(definitions,OpenTOSCA_LifecycleInterface, OpenTOSCA_BuildPlanOperation) == null) {
                LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
                    serviceTemplate.getId());
                final BPELPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);

                if (newBuildPlan != null) {
                    LOG.debug("Created BuildPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
                    serviceTemplate.getId());
            }
        }
        if (!plans.isEmpty()) {
            LOG.info("Created {} build plans for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    /**
     * <p>
     * This method assigns plugins to the already initialized BuildPlan and its TemplateBuildPlans. First there will be
     * checked if any generic plugin can handle a template of the TopologyTemplate
     * </p>
     *
     * @param buildPlan a BuildPlan which is already initialized
     * @param map       a PropertyMap which contains mappings from Template to Property and to variable name of inside
     *                  the BuildPlan
     */
    private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceID,
                            final String serviceTemplateUrl, final String planInstanceUrl, final Csar csar) {
        for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, bpelScope, map, buildPlan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);
            if (bpelScope.getNodeTemplate() != null) {

                final TNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();

                // if this nodeTemplate has the label running (Property: State=Running), skip
                // provisioning and just generate instance data handling
                // extended check for OperatingSystem node type
                if (isRunning(nodeTemplate)
                    || ModelUtils.findNodeType(nodeTemplate, csar).getName().equals(Types.abstractOperatingSystemNodeType.getLocalPart())) {
                    LOG.debug("Skipping the provisioning of NodeTemplate "
                        + bpelScope.getNodeTemplate().getId() + "  because state=running is set.");
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
                // handling relationshiptemplate
                final TRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            } else {
                this.bpelPluginHandler.handleActivity(context, bpelScope);
            }
        }
    }
}
