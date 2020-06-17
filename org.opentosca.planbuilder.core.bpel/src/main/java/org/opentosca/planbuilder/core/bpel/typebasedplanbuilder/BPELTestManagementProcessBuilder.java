package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractManagementFeaturePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This process builder creates a test management plan if one of the NodeTemplates in the topology is of a type that
 * defines the test interface.
 * </p>
 * <p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPELTestManagementProcessBuilder extends AbstractManagementFeaturePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELTestManagementProcessBuilder.class);
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    private final BPELPluginHandler bpelPluginHandler;
    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    private CorrelationIDInitializer correlationHandler;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELTestManagementProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
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

        abstractTestPlan.setType(PlanType.MANAGEMENT);
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
            serviceTemplate, null);

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

        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newTestPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newTestPlan),
            "ERROR", serviceInstanceUrl);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newTestPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newTestPlan),
            "FAILED",
            this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newTestPlan));
        
        String planInstanceUrlVarName = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newTestPlan);
        this.serviceInstanceInitializer.appendSetServiceInstanceState(newTestPlan,
        		newTestPlan.getBpelMainFlowElement(),
                "RUNNING", planInstanceUrlVarName);
        
        this.serviceInstanceInitializer.appendSetServiceInstanceState(newTestPlan,
        		newTestPlan.getBpelMainSequenceOutputAssignElement(),
                "FINISHED", planInstanceUrlVarName);
        

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

    private void runPlugins(final BPELPlan testPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceID,
                            final String serviceTemplateUrl, final String csarFileName) {

        for (final BPELScope bpelScope : testPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), testPlan, bpelScope, map, testPlan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csarFileName);
            if (Objects.nonNull(bpelScope.getNodeTemplate())) {

                // retrieve NodeTemplate and corresponding test interface
                final AbstractNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();
                final AbstractInterface testInterface =
                    ModelUtils.getInterfaceOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST);

                if (Objects.nonNull(testInterface)) {

                    // retrieve input parameters from all nodes which are downwards in the same topology stack
                    final List<AbstractNodeTemplate> nodesForMatching = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching);

                    LOG.debug("NodeTemplate {} has {} test operations defined.", nodeTemplate.getName(),
                        testInterface.getOperations().size());
                    for (final AbstractOperation testOperation : testInterface.getOperations()) {

                        final Map<AbstractParameter, Variable> inputMapping = new HashMap<>();
                        final Map<AbstractParameter, Variable> outputMapping = new HashMap<>();

                        // search for input parameters in the topology stack
                        LOG.debug("Test {} on NodeTemplate {} needs the following input parameters:",
                            testOperation.getName(), nodeTemplate.getName());
                        for (final AbstractParameter param : testOperation.getInputParameters()) {
                            LOG.debug("Input param: {}", param.getName());
                            found:
                            for (final AbstractNodeTemplate nodeForMatching : nodesForMatching) {
                                for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                                    if (param.getName().equals(propName)) {
                                        inputMapping.put(param, context.getPropertyVariable(nodeForMatching, propName));
                                        break found;
                                    }
                                }
                            }
                        }

                        // create output variable if 'Result' is defined as output parameter
                        final Optional<AbstractParameter> optional =
                            testOperation.getOutputParameters().stream()
                                .filter(param -> param.getName().equals("Result")).findFirst();
                        if (optional.isPresent()) {
                            final AbstractParameter resultParam = optional.get();

                            final String xsdPrefix = "xsd" + System.currentTimeMillis();
                            final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
                            final String resultVarName = nodeTemplate.getName() + "-" + testOperation.getName()
                                + "-result" + System.currentTimeMillis();
                            context.addGlobalVariable(resultVarName, VariableType.TYPE,
                                new QName(xsdNamespace, "anyType", xsdPrefix));

                            LOG.debug("Name of result variable: " + resultVarName);

                            outputMapping.put(resultParam, BPELPlanContext.getVariable(resultVarName));

                            // add result to the plan output message
                            final String outputName =
                                "Tests-" + nodeTemplate.getName() + "-" + testOperation.getName() + "-result";
                            this.planHandler.addStringElementToPlanResponse(outputName, testPlan);
                            this.planHandler.assginOutputWithVariableValue(resultVarName, outputName, testPlan);
                        } else {
                            LOG.error("Result property is not defined for test operation.");
                        }

                        // execute the test
                        context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST,
                            testOperation.getName(), inputMapping, outputMapping);
                    }
                } else {
                    LOG.error("Unable to find test interface for NodeTemplate {}", nodeTemplate.getName());
                }
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handling relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            }
        }
    }
}
