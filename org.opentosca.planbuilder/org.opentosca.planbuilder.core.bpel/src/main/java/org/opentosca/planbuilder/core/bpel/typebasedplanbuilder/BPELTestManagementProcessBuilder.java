package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.AbstractManagementFeaturePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.DeployTechDescriptorHandler;
import org.opentosca.planbuilder.core.bpel.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.DeployTechDescriptorMapping;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_ManagementFeatureInterface;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_TestPlanOperation;

/**
 * <p>
 * This process builder creates a test management plan if one of the NodeTemplates in the topology is of a type that
 * defines the test interface.
 * </p>
 * <p>
 * Copyright 2019-2022 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPELTestManagementProcessBuilder extends AbstractManagementFeaturePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELTestManagementProcessBuilder.class);
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing deployment technology properties in the build plan
    private final DeployTechDescriptorHandler deployTechDescriptorHandler;
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
        this.deployTechDescriptorHandler = new DeployTechDescriptorHandler(this.planHandler);
    }

    private BPELPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {

        LOG.debug("Creating Test Management Plan...");

        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to generate Test Plan with ServiceTempolate equal to null.");
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_testManagementPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_testManagementPlan";

        final AbstractPlan abstractTestPlan =
            generateMOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST, ActivityType.TEST, false, csar);

        LOG.debug("Generated the following abstract test plan: ");
        LOG.debug(abstractTestPlan.toString());

        abstractTestPlan.setType(PlanType.MANAGEMENT);
        final BPELPlan newTestPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractTestPlan, OpenTOSCA_TestPlanOperation);

        this.planHandler.initializeBPELSkeleton(newTestPlan, csar);

        newTestPlan.setTOSCAInterfaceName(OpenTOSCA_ManagementFeatureInterface);
        newTestPlan.setTOSCAOperationname(OpenTOSCA_TestPlanOperation);

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newTestPlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newTestPlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newTestPlan, serviceTemplate);

        DeployTechDescriptorMapping descriptorMap =
            this.deployTechDescriptorHandler.initializeDescriptorsAsVariables(newTestPlan, serviceTemplate);

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
        final String planInstanceUrl = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newTestPlan);

        this.instanceVarsHandler.addNodeInstanceFindLogic(newTestPlan,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
            serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newTestPlan, propMap, serviceTemplate);

        runPlugins(newTestPlan, propMap, descriptorMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);

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
            newTestPlan.getBpelMainSequenceCallbackInvokeElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newTestPlan);

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newTestPlan.getBpelDocument()));

        return newTestPlan;
    }

    @Override
    public List<AbstractPlan> buildPlans(final Csar csar) {
        LOG.debug("Building the Test Management Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        TDefinitions definitions = csar.entryDefinitions();

        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (containsManagementInterface(serviceTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST, csar)) {
                LOG.debug("ServiceTemplate {} contains NodeTypes with defined test interface.",
                    serviceTemplate.getName());
                final BPELPlan newTestPlan = buildPlan(csar, definitions, serviceTemplate);
                if (Objects.nonNull(newTestPlan)) {
                    LOG.debug("Created Test Management Plan "
                        + newTestPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newTestPlan);
                }
            } else {
                LOG.debug("No test interface defined in ServiceTemplate {}", serviceTemplate.getName());
            }
        }
        if (!plans.isEmpty()) {
            LOG.info("Created {} test management plans for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    private void runPlugins(final BPELPlan testPlan, final Property2VariableMapping map,
                            final DeployTechDescriptorMapping descriptorMap, final String serviceInstanceUrl,
                            final String serviceInstanceID, final String serviceTemplateUrl,
                            final String planInstanceUrl, final Csar csar) {

        for (final BPELScope bpelScope : testPlan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), testPlan, bpelScope, map, descriptorMap, testPlan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);
            if (Objects.nonNull(bpelScope.getNodeTemplate())) {

                // retrieve NodeTemplate and corresponding test interface
                final TNodeTemplate nodeTemplate = bpelScope.getNodeTemplate();
                final TInterface testInterface =
                    ModelUtils.getInterfaceOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_TEST, csar);

                if (Objects.nonNull(testInterface)) {

                    // retrieve input parameters from all nodes which are downwards in the same topology stack
                    final List<TNodeTemplate> nodesForMatching = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching, csar);

                    LOG.debug("NodeTemplate {} has {} test operations defined.", nodeTemplate.getName(),
                        testInterface.getOperations().size());
                    for (final TOperation testOperation : testInterface.getOperations()) {

                        final Map<TParameter, Variable> inputMapping = new HashMap<>();
                        final Map<TParameter, Variable> outputMapping = new HashMap<>();

                        if (testOperation.getInputParameters() != null) {
                            // search for input parameters
                            LOG.debug("Test {} on NodeTemplate {} needs the following input parameters:",
                                testOperation.getName(), nodeTemplate.getName());
                            for (final TParameter param : testOperation.getInputParameters()) {
                                LOG.debug("Input param: {}", param.getName());
                                boolean found = false;
                                // search in deployment technology descriptors
                                Optional<PropertyVariable> var = descriptorMap.getVariableByNodeAndProp(nodeTemplate, param.getName());
                                if (var.isPresent()) {
                                    inputMapping.put(param, var.get());
                                    LOG.debug("Found variable |{}|", var.get().getVariableName());
                                    found = true;
                                }
                                // search in the topology stack
                                for (int i = 0; i < nodesForMatching.size() && !found; i++) {
                                    TNodeTemplate nodeForMatching = nodesForMatching.get(i);
                                    for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                                        if (param.getName().equals(propName)) {
                                            PropertyVariable propVar = context.getPropertyVariable(nodeForMatching, propName);
                                            inputMapping.put(param, propVar);
                                            LOG.debug("Found variable |{}|", propVar.getVariableName());
                                            found = true;
                                            break;
                                        }
                                    }
                                }

                                if (param.getRequired() && (
                                    (!found && !Utils.isSupportedProperty(param.getName())) // we assume, the bus finds the correct prop
                                        ||
                                        (found && inputMapping.get(param) instanceof PropertyVariable propertyVariable
                                            && (propertyVariable.getContent() == null || propertyVariable.getContent().isBlank()))
                                )) {
                                    String inputPropertyName = nodeTemplate.getId() + "-" + param.getName();
                                    String inputPropertyVarName = this.planHandler.addGlobalStringVariable(inputPropertyName, testPlan);

                                    Variable bpelVar = BPELPlanContext.getVariable(inputPropertyVarName);
                                    inputMapping.put(param, bpelVar);

                                    new EmptyPropertyToInputHandler(null)
                                        .addToPlanInput(testPlan, inputPropertyName, bpelVar, context);
                                }
                            }
                        } else {
                            LOG.debug("Test {} on NodeTemplate {} does not need input parameters!",
                                testOperation.getName(), nodeTemplate.getName());
                        }

                        if (testOperation.getOutputParameters() == null) {
                            LOG.error("Test {} on NodeTemplate {} does not define output parameters! Cannot proceed...",
                                testOperation.getName(), nodeTemplate.getName());
                            return;
                        }
                        // create output variable if 'Result' is defined as output parameter
                        final Optional<TParameter> optional =
                            testOperation.getOutputParameters().stream()
                                .filter(param -> param.getName().equals("Result")).findFirst();
                        if (optional.isPresent()) {
                            final TParameter resultParam = optional.get();

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
                                "Tests-" + nodeTemplate.getName() + "-" + testOperation.getName() + "-Result";
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
                    LOG.warn("Unable to find test interface for NodeTemplate \"{}\"", nodeTemplate.getId());
                }
            } else if (bpelScope.getRelationshipTemplate() != null) {
                // handling relationshiptemplate
                final TRelationshipTemplate relationshipTemplate = bpelScope.getRelationshipTemplate();

                this.bpelPluginHandler.handleActivity(context, bpelScope, relationshipTemplate);
            }
        }
    }
}
