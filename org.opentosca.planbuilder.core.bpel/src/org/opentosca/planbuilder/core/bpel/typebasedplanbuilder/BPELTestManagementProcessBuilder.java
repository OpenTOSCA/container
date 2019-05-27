package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractManagementFeaturePlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This process builder creates a test management plan if one of the NodeTemplates in the topology
 * is of a type that defines the test interface.
 * </p>
 *
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPELTestManagementProcessBuilder extends AbstractManagementFeaturePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELTestManagementProcessBuilder.class);

    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;

    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;

    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;

    private CorrelationIDInitializer correlationHandler;

    private final BPELPluginHandler bpelPluginHandler = new BPELPluginHandler();

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELTestManagementProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.correlationHandler = new CorrelationIDInitializer();
        }
        catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing TestPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {

        LOG.debug("Creating Test Management Plan...");

        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to generate Test Plan with ServiceTempolate equal to null.");
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_testManagementPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_testManagementPlan";

        final AbstractPlan abstractTestPlan =
            generateMOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST, ActivityType.TEST, false);

        LOG.debug("Generated the following abstract test plan: ");
        LOG.debug(abstractTestPlan.toString());

        abstractTestPlan.setType(PlanType.MANAGE);
        final BPELPlan newTestPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractTestPlan, "test");

        this.planHandler.initializeBPELSkeleton(newTestPlan, csarName);

        newTestPlan.setTOSCAInterfaceName("OpenTOSCA-Management-Feature-Interface");
        newTestPlan.setTOSCAOperationname("test");

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newTestPlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newTestPlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newTestPlan, serviceTemplate);

        // initialize instanceData handling
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                                           newTestPlan);
        this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newTestPlan);
        final String serviceTemplateURLVarName =
            this.serviceInstanceVarsHandler.getServiceTemplateURLVariableName(newTestPlan);
        this.serviceInstanceVarsHandler.appendInitPropertyVariablesFromServiceInstanceData(newTestPlan, propMap,
                                                                                           serviceTemplateURLVarName,
                                                                                           serviceTemplate);

        final String serviceInstanceUrl =
            this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newTestPlan);
        final String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newTestPlan);
        final String serviceTemplateUrl =
            this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newTestPlan);

        this.instanceVarsHandler.addNodeInstanceFindLogic(newTestPlan,
                                                          "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                                                          serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newTestPlan, propMap, serviceTemplate);

        runPlugins(newTestPlan, propMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csarName);

        this.correlationHandler.addCorrellationID(newTestPlan);
        this.finalizer.finalize(newTestPlan);

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newTestPlan.getBpelDocument()));

        return newTestPlan;
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        LOG.info("Building the Test Management Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (containsManagementInterface(serviceTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST)) {
                LOG.debug("ServiceTemplate {} contains NodeTypes with defined test interface.",
                          serviceTemplate.getName());
                final BPELPlan newTestPlan = buildPlan(csarName, definitions, serviceTemplate);
                if (Objects.nonNull(newTestPlan)) {
                    LOG.debug("Created Test Management Plan "
                        + newTestPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newTestPlan);
                }
            } else {
                LOG.debug("No test interface defined in ServiceTemplate {}", serviceTemplate.getName());
            }
        }
        return plans;
    }

    private void runPlugins(final BPELPlan buildPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceID,
                            final String serviceTemplateUrl, final String csarFileName) {

        for (final BPELScope bpelScope : buildPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context =
                new BPELPlanContext(buildPlan, bpelScope, map, buildPlan.getServiceTemplate(), serviceInstanceUrl,
                    serviceInstanceID, serviceTemplateUrl, csarFileName);
            if (Objects.nonNull(bpelScope.getNodeTemplate())) {

                // generate code for the activity
                final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();
                this.bpelPluginHandler.handleActivity(context, bpelScope, nodeTemplate);
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handling relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            }
        }
    }
}
