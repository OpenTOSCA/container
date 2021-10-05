package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractScaleOutPlanBuilder;
import org.opentosca.planbuilder.core.ScalingPlanDefinition;
import org.opentosca.planbuilder.core.ScalingPlanDefinition.AnnotatedTNodeTemplate;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.container.core.model.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELScaleOutProcessBuilder extends AbstractScaleOutPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELScaleOutProcessBuilder.class);

    // class for initializing properties inside the plan
    private final PropertyVariableHandler propertyInitializer;

    // adds serviceInstance Variable and instanceDataAPIUrl to Plans
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    private final EmptyPropertyToInputHandler emptyPropInit;
    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();
    private final BPELScopeBuilder scopeBuilder;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;
    private NodeRelationInstanceVariablesHandler instanceInitializer;
    private BPELPlanHandler planHandler;
    private CorrelationIDInitializer correlationHandler;

    public BPELScaleOutProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        this.scopeBuilder = new BPELScopeBuilder(pluginRegistry);
        this.emptyPropInit = new EmptyPropertyToInputHandler(scopeBuilder);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceInitializer = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);

        this.finalizer = new BPELFinalizer();
        this.opNames.add("install");
        this.opNames.add("configure");
        this.opNames.add("start");
    }

    private boolean addInstanceIdToOutput(final BPELScope activ, TServiceTemplate serviceTemplate) {
        String outputName;
        if (activ.getNodeTemplate() != null) {
            outputName = "CreatedInstance_" + activ.getNodeTemplate().getId();
        } else {
            outputName = "CreatedInstance_" + activ.getRelationshipTemplate().getId();
        }

        this.planHandler.addStringElementToPlanResponse(outputName, activ.getBuildPlan());

        final String varName = this.instanceInitializer.findInstanceVarName(activ, serviceTemplate, true);
        this.planHandler.assginOutputWithVariableValue(varName, outputName, activ.getBuildPlan());

        return true;
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final Property2VariableMapping map,
                                               final TNodeTemplate nodeTemplate,
                                               TServiceTemplate serviceTemplate, Csar csar) {
        // fetch nodeInstance Variable to store the result at the end
        final BPELPlanContext nodeContext = this.createContext(nodeTemplate, plan, map, csar);

        final String nodeInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, nodeTemplate.getId(), true);

        // find first relationtemplate which is an infrastructure edge
        final TRelationshipTemplate relationshipTemplate = getFirstOutgoingInfrastructureRelation(nodeTemplate, csar);
        if (relationshipTemplate == null) {
            return;
        }
        final String relationInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, relationshipTemplate.getId(), false);

        // create response variable
        final QName anyTypeDeclId =
            nodeContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "any", "xsd"));
        final String responseVarName =
            "recursiveSelection_NodeInstance_" + nodeTemplate.getId() + System.currentTimeMillis() + "_Response";
        nodeContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);

        final String serviceTemplateUrlVarName = serviceInstanceHandler.getServiceTemplateURLVariableName(plan);

        // fetch relationInstance data
        try {
            // createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode
            Node fetchNodeInstanceData =
                new BPELProcessFragments().createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
                    responseVarName,
                    nodeTemplate.getId(),
                    "source=$bpelvar[$"
                        + relationInstanceVarName
                        + "]");
            fetchNodeInstanceData = nodeContext.importNode(fetchNodeInstanceData);
            nodeContext.getPrePhaseElement().appendChild(fetchNodeInstanceData);
        } catch (final IOException | SAXException | ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // query its source node, which will be nodeInstance for this
        // NodeTemplate
        // set nodeInstance variable

        final String xpathQuery =
            "//*[local-name()='NodeTemplateInstanceResources']/*[local-name()='NodeTemplateInstances']/*[local-name()='NodeTemplateInstance']/*[1]/*[local-name()='Links']/*[local-name()='Link']/@*[local-name()='href']/string()";
        try {

            Node queryNodeInstanceUrl =
                new BPELProcessFragments().createAssignVarToVarWithXpathQueryAsNode("recursiveSelection_fetchNodeInstance"
                    + System.currentTimeMillis(), responseVarName, nodeInstanceVarName, xpathQuery);
            queryNodeInstanceUrl = nodeContext.importNode(queryNodeInstanceUrl);
            nodeContext.getPrePhaseElement().appendChild(queryNodeInstanceUrl);
        } catch (final IOException | SAXException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final Property2VariableMapping map,
                                               final TRelationshipTemplate relationshipTemplate,
                                               TServiceTemplate serviceTemplate, Csar csar) {
        // fetch relationInstance variable of relationship template
        final BPELPlanContext relationContext = this.createContext(relationshipTemplate, plan, map, csar);
        final String relationshipTemplateInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, relationshipTemplate.getId(), false);

        // fetch nodeInstance variable of source node template
        final String nodeTemplateInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate,
                ModelUtils.getTarget(relationshipTemplate, csar).getId(), true);

        final String serviceTemplateUrlVarName = serviceInstanceHandler.getServiceTemplateURLVariableName(plan);

        // find relationshipTemplate instance that has the node template
        // instance as source

        final QName stringTypeDeclId =
            relationContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        final String requestVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId()
            + System.currentTimeMillis() + "_Request";
        final String responseVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId()
            + System.currentTimeMillis() + "_Response";

        relationContext.addVariable(requestVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);
        relationContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);

        try {
            Node requestRelationInstance =
                new BPELProcessFragments().createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(serviceTemplateUrlVarName,
                    relationshipTemplate.getId(),
                    responseVarName,
                    nodeTemplateInstanceVarName);
            requestRelationInstance = relationContext.importNode(requestRelationInstance);
            relationContext.getPrePhaseElement().appendChild(requestRelationInstance);
        } catch (final IOException | SAXException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String xpathQuery =
            "//*[local-name()='RelationshipTemplateInstanceResources']/*[local-name()='RelationshipTemplateInstances']/*[local-name()='RelationshipTemplateInstance']/[0]/*[local-name()='Links']/*[local-name()='Link']/@*[local-name()='href']/string()";

        // set relationInstnace variable of the relationship templates' scope
        try {
            new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("recursiveSelection_fetchRelationInstance"
                + System.currentTimeMillis(), xpathQuery, relationshipTemplateInstanceVarName);
        } catch (final IOException | SAXException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            plans.addAll(buildScalingPlans(csar, definitions, new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId())));
        }

        if (!plans.isEmpty()) {
            LOG.info("Created {} scaling plans for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    public List<BPELPlan> buildScalingPlans(final Csar csar, final TDefinitions definitions,
                                            final QName serviceTemplateId) {
        final List<BPELPlan> scalingPlans = new ArrayList<>();

        final TServiceTemplate serviceTemplate = getServiceTemplate(definitions, serviceTemplateId);

        if (serviceTemplate == null) {
            return scalingPlans;
        }

        // check if the given serviceTemplate has the scaling plans defined as
        // tags

        final Collection<TTag> tags = serviceTemplate.getTags();


        if (tags == null || tags.stream().filter(x -> x.getName().equals("scalingplans")).findFirst().orElse(null) == null) {
            return scalingPlans;
        }

        final Map<String, String> tagMap = new HashMap<>();
        tags.forEach(x -> tagMap.put(x.getName(), x.getValue()));

        final List<ScalingPlanDefinition> scalingPlanDefinitions =
            fetchScalingPlansDefinitions(serviceTemplate.getTopologyTemplate(), tagMap, csar);

        for (final ScalingPlanDefinition scalingPlanDefinition : scalingPlanDefinitions) {

            final BPELPlan bpelScaleOutProcess = this.createScalingPlan(csar,definitions,serviceTemplate,scalingPlanDefinition);

            scalingPlans.add(bpelScaleOutProcess);
        }

        return scalingPlans;
    }

    private BPELPlan createScalingPlan(Csar csar, TDefinitions definitions, TServiceTemplate serviceTemplate, ScalingPlanDefinition scalingPlanDefinition){
        final String processName =
            ModelUtils.makeValidNCName(serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name);
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";

        final AbstractPlan abstractScaleOutPlan = generateSOG(new QName(processNamespace, processName).toString(),
            definitions, serviceTemplate, scalingPlanDefinition, csar);

        printGraph(abstractScaleOutPlan);

        final BPELPlan bpelScaleOutProcess =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractScaleOutPlan, "scale-out");

        bpelScaleOutProcess.setTOSCAInterfaceName(scalingPlanDefinition.name);
        bpelScaleOutProcess.setTOSCAOperationname("scale-out");

        this.planHandler.initializeBPELSkeleton(bpelScaleOutProcess, csar);

        Collection<TNodeTemplate> nodes = bpelScaleOutProcess.getTemplateBuildPlans().stream().map(BPELScope::getNodeTemplate).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Collection<TRelationshipTemplate> relations = bpelScaleOutProcess.getTemplateBuildPlans().stream().map(BPELScope::getRelationshipTemplate).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        final Property2VariableMapping propMap = this.propertyInitializer.initializePropertiesAsVariables(bpelScaleOutProcess, serviceTemplate, nodes, relations);

        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            bpelScaleOutProcess);

        this.serviceInstanceHandler.addServiceInstanceHandlingFromInput(bpelScaleOutProcess);

        this.instanceInitializer.addInstanceURLVarToTemplatePlans(bpelScaleOutProcess, serviceTemplate);
        this.instanceInitializer.addInstanceIDVarToTemplatePlans(bpelScaleOutProcess, serviceTemplate);

        this.correlationHandler.addCorrellationID(bpelScaleOutProcess);

        final List<BPELScope> provScopeActivities = new ArrayList<>();

        for (final BPELScope act : bpelScaleOutProcess.getAbstract2BPEL().values()) {
            if (act.getNodeTemplate() != null
                && scalingPlanDefinition.nodeTemplates.contains(act.getNodeTemplate())) {
                provScopeActivities.add(act);
            } else if (act.getRelationshipTemplate() != null
                && scalingPlanDefinition.relationshipTemplates.contains(act.getRelationshipTemplate())) {
                provScopeActivities.add(act);
            }
        }

        String serviceInstanceUrl =
            this.serviceInstanceHandler.findServiceInstanceUrlVariableName(bpelScaleOutProcess);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(bpelScaleOutProcess);
        String serviceTemplateUrl =
            this.serviceInstanceHandler.findServiceTemplateUrlVariableName(bpelScaleOutProcess);
        String planInstanceUrl = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(bpelScaleOutProcess);

        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(provScopeActivities, bpelScaleOutProcess, propMap,
            serviceInstanceUrl, serviceInstanceId,
            serviceTemplateUrl, serviceTemplate, planInstanceUrl, csar);

        this.runProvisioningLogicGeneration(bpelScaleOutProcess, propMap, scalingPlanDefinition.nodeTemplates,
            scalingPlanDefinition.relationshipTemplates, serviceInstanceUrl,
            serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);

        // add generic instance selection

        for (final TRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
            this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, relationshipTemplate, serviceTemplate,
                csar);
        }
        for (final TNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
            this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, nodeTemplate, serviceTemplate,
                csar);
        }

        for (final AnnotatedTNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
            final IScalingPlanBuilderSelectionPlugin selectionPlugin = findSelectionPlugin(stratNodeTemplate);
            if (selectionPlugin != null) {
                final BPELScope scope =
                    this.planHandler.getTemplateBuildPlanById(stratNodeTemplate.getId(), bpelScaleOutProcess);
                selectionPlugin.handle(new BPELPlanContext(scopeBuilder, bpelScaleOutProcess, scope, propMap, serviceTemplate, serviceInstanceUrl,
                        serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar), stratNodeTemplate,
                    new ArrayList<>(stratNodeTemplate.getAnnotations()));
            }
        }

        for (final AbstractActivity activ : bpelScaleOutProcess.getActivites()) {
            if (activ.getType().equals(ActivityType.PROVISIONING)) {
                addInstanceIdToOutput(bpelScaleOutProcess.getAbstract2BPEL().get(activ), serviceTemplate);
            }
        }

        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(bpelScaleOutProcess,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(bpelScaleOutProcess),
            "ERROR", serviceInstanceUrl);
        this.serviceInstanceHandler.appendSetServiceInstanceStateAsChild(bpelScaleOutProcess, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(bpelScaleOutProcess), "FAILED", this.serviceInstanceHandler.findPlanInstanceUrlVariableName(bpelScaleOutProcess));

        String planInstanceUrlVarName = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(bpelScaleOutProcess);
        this.serviceInstanceHandler.appendSetServiceInstanceState(bpelScaleOutProcess,
            bpelScaleOutProcess.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceHandler.appendSetServiceInstanceState(bpelScaleOutProcess,
            bpelScaleOutProcess.getBpelMainSequenceOutputAssignElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(bpelScaleOutProcess);
        return bpelScaleOutProcess;
    }

    private String cleanCSVString(String commaSeperatedList) {
        while (commaSeperatedList.endsWith(";") | commaSeperatedList.endsWith(",")) {
            commaSeperatedList = commaSeperatedList.substring(0, commaSeperatedList.length() - 2);
        }
        return commaSeperatedList;
    }

    public BPELPlanContext createContext(final TNodeTemplate nodeTemplate, final BPELPlan plan,
                                         final Property2VariableMapping map, Csar csar) {

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(plan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);
        String planInstanceUrl = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(plan);

        return new BPELPlanContext(scopeBuilder, plan, this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map,
            plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
    }

    public BPELPlanContext createContext(final TRelationshipTemplate relationshipTemplate, final BPELPlan plan,
                                         final Property2VariableMapping map, Csar csar) {

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(plan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);
        String planInstanceUrl = this.serviceInstanceHandler.findPlanInstanceUrlVariableName(plan);

        return new BPELPlanContext(scopeBuilder, plan, this.planHandler.getTemplateBuildPlanById(relationshipTemplate.getId(), plan), map,
            plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
    }

    private TNodeTemplate fetchNodeTemplate(final TTopologyTemplate topologyTemplate,
                                            final String nodeTemplateId) {
        for (final TNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            if (nodeTemplate.getId().equals(nodeTemplateId)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    private List<TNodeTemplate> fetchNodeTemplates(final TTopologyTemplate topology,
                                                   String scalingPlanNodesRawValue) {
        final List<TNodeTemplate> nodeTemplates = new ArrayList<>();

        scalingPlanNodesRawValue = cleanCSVString(scalingPlanNodesRawValue);

        // fetch nodeTemplateIds from raw value

        final List<String> scalingPlanNodeNames = getElementsFromCSV(scalingPlanNodesRawValue);

        for (final String scalingNodeName : scalingPlanNodeNames) {
            final TNodeTemplate node = fetchNodeTemplate(topology, scalingNodeName);
            if (node != null) {
                nodeTemplates.add(node);
            }
        }

        if (nodeTemplates.size() != scalingPlanNodeNames.size()) {
            return null;
        } else {
            return nodeTemplates;
        }
    }

    private List<TRelationshipTemplate> fetchRelationshipTemplates(final TTopologyTemplate topology,
                                                                   String scalingPlanRelationsRawValue) {
        final List<TRelationshipTemplate> relationshipTemplates = new ArrayList<>();

        scalingPlanRelationsRawValue = cleanCSVString(scalingPlanRelationsRawValue);

        // fetch nodeTemplateIds from raw value
        final List<String> scalingPlanRelationNames = getElementsFromCSV(scalingPlanRelationsRawValue);

        for (final TRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
            for (final String scalingNodeName : scalingPlanRelationNames) {
                if (relationshipTemplate.getId().equals(scalingNodeName.trim())) {
                    relationshipTemplates.add(relationshipTemplate);
                }
            }
        }

        if (relationshipTemplates.size() != scalingPlanRelationNames.size()) {
            return null;
        } else {
            return relationshipTemplates;
        }
    }

    private List<ScalingPlanDefinition> fetchScalingPlansDefinitions(final TTopologyTemplate topology,
                                                                     final Map<String, String> tags, Csar csar) {
        final List<ScalingPlanDefinition> scalingPlanDefinitions = new ArrayList<>();

        // fetch scaling plan names
        final String scalingPlanNamesRawValue = tags.get("scalingplans").trim();

        final List<String> scalingPlanNamesRaw = getElementsFromCSV(scalingPlanNamesRawValue);

        for (final String scalingPlanName : scalingPlanNamesRaw) {

            if (!scalingPlanName.trim().isEmpty() && tags.containsKey(scalingPlanName.trim())) {
                final String scalingPlanNodesNEdgesRawValue = tags.get(scalingPlanName.trim());

                final String[] scalingPlanNodesNEdgesRawValueSplit = scalingPlanNodesNEdgesRawValue.split(";");

                if (scalingPlanNodesNEdgesRawValueSplit.length != 3) {
                    LOG.error("Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<TNodeTemplate> nodeTemplates =
                    fetchNodeTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[0]);

                if (nodeTemplates == null) {
                    LOG.error("Nodes of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<TRelationshipTemplate> relationshipTemplates =
                    fetchRelationshipTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[1]);

                if (relationshipTemplates == null) {
                    LOG.error("Relations of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AnnotatedTNodeTemplate> selectionStrategy2BorderNodes =
                    fetchSelectionStrategy2BorderNodes(topology, scalingPlanNodesNEdgesRawValueSplit[2]);

                scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, topology, nodeTemplates,
                    relationshipTemplates, selectionStrategy2BorderNodes, csar));
            }
        }

        return scalingPlanDefinitions;
    }

    private List<AnnotatedTNodeTemplate> fetchSelectionStrategy2BorderNodes(final TTopologyTemplate topologyTemplate,
                                                                            String selectionStrategyBorderNodesCSV) {

        selectionStrategyBorderNodesCSV = cleanCSVString(selectionStrategyBorderNodesCSV);

        final List<String> selectionStrategyBorderNodes = getElementsFromCSV(selectionStrategyBorderNodesCSV);

        final Map<String, String> selectionStrategyBorderNodesMap =
            transformSelectionStrategyListToMap(selectionStrategyBorderNodes);

        final List<AnnotatedTNodeTemplate> annotNodes = new ArrayList<>();

        for (final String selectionStrategy : selectionStrategyBorderNodesMap.keySet()) {
            final TNodeTemplate node =
                fetchNodeTemplate(topologyTemplate, selectionStrategyBorderNodesMap.get(selectionStrategy));
            if (node != null) {
                if (findAnnotNode(annotNodes, node) != null) {
                    Objects.requireNonNull(findAnnotNode(annotNodes, node), "annotation node may not be null")
                        .getAnnotations()
                        .add(selectionStrategy);
                } else {
                    final List<String> annot = new ArrayList<>();
                    annot.add(selectionStrategy);
                    annotNodes.add(new AnnotatedTNodeTemplate(node, annot));
                }
            }
        }

        return annotNodes;
    }

    private AnnotatedTNodeTemplate findAnnotNode(final List<AnnotatedTNodeTemplate> annotNodes,
                                                 final TNodeTemplate node) {
        for (final AnnotatedTNodeTemplate annotNode : annotNodes) {
            if (annotNode.getId().equals(node.getId())) {
                return annotNode;
            }
        }
        return null;
    }

    private IScalingPlanBuilderSelectionPlugin findSelectionPlugin(final AnnotatedTNodeTemplate stratNodeTemplate) {

        for (final IScalingPlanBuilderSelectionPlugin plugin : this.pluginRegistry.getSelectionPlugins()) {
            final List<String> a = new ArrayList<>(stratNodeTemplate.getAnnotations());
            if (plugin.canHandle(stratNodeTemplate, a)) {
                return plugin;
            }
        }
        return null;
    }

    private List<String> getElementsFromCSV(String csvString) {
        csvString = cleanCSVString(csvString);
        final String[] scalingPlanRelationNamesRawSplit = csvString.split(",");
        return Arrays.asList(scalingPlanRelationNamesRawSplit);
    }

    private TRelationshipTemplate getFirstOutgoingInfrastructureRelation(final TNodeTemplate nodeTemplate, Csar csar) {

        // TODO FIXME: If you see this, I forgot to refactor this in someting better, as I was refactoring the whole model at this point.. sry
        Collection<QName> hostedOn = Collections.emptyList();
        hostedOn.add(Types.hostedOnRelationType);
        Collection<QName> dependsOn = Collections.emptyList();
        hostedOn.add(Types.dependsOnRelationType);
        Collection<QName> deployedOn = Collections.emptyList();
        hostedOn.add(Types.deployedOnRelationType);

        final List<TRelationshipTemplate> relations =
            ModelUtils.getOutgoingRelations(nodeTemplate, hostedOn, csar);
        relations.addAll(ModelUtils.getOutgoingRelations(nodeTemplate, dependsOn, csar));
        relations.addAll(ModelUtils.getOutgoingRelations(nodeTemplate, deployedOn, csar));

        if (!relations.isEmpty()) {
            return relations.get(0);
        }

        return null;
    }

    private void printGraph(final AbstractPlan abstractScaleOutPlan) {

        LOG.debug("Scale Out Plan: " + abstractScaleOutPlan.getId());

        LOG.debug("Activities: ");

        for (final AbstractActivity activ : abstractScaleOutPlan.getActivites()) {
            LOG.debug("id: " + activ.getId() + " type: " + activ.getType());
        }

        LOG.debug("Links: ");
        for (final Link link : abstractScaleOutPlan.getLinks()) {
            String srcId = null;
            String trgtId = null;

            if (link.getSrcActiv() != null) {
                srcId = link.getSrcActiv().getId();
            }

            if (link.getTrgActiv() != null) {
                trgtId = link.getTrgActiv().getId();
            }

            LOG.debug("(" + srcId + ", " + trgtId + ")");
        }
    }

    private void runProvisioningLogicGeneration(final BPELPlan plan, final TNodeTemplate nodeTemplate,
                                                final Property2VariableMapping map, String serviceInstanceUrl,
                                                String serviceInstanceId, String serviceTemplateUrl, String planInstanceUrl,
                                                Csar csar) {
        // handling nodetemplate
        final BPELPlanContext context =
            new BPELPlanContext(scopeBuilder, plan, this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map,
                plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken!

        for (IPlanBuilderPrePhasePlugin prePhasePlugin : this.pluginRegistry.getPrePlugins()) {
            if (prePhasePlugin.canHandleCreate(context, nodeTemplate)) {
                prePhasePlugin.handleCreate(context, nodeTemplate);
            }
        }

        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(nodeTemplate, context.getCsar());
        if (plugin != null) {

            LOG.info("Handling NodeTemplate {} with type plugin {}", nodeTemplate.getId(),
                plugin.getID());
            plugin.handleCreate(context, nodeTemplate);
        } else {
            LOG.debug("Can't handle NodeTemplate {} with type plugin", nodeTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, nodeTemplate)) {
                postPhasePlugin.handleCreate(context, nodeTemplate);
            }
        }
    }

    private void runProvisioningLogicGeneration(final BPELPlan plan,
                                                final TRelationshipTemplate relationshipTemplate,
                                                final Property2VariableMapping map, Csar csar) {
        // handling relationshiptemplate

        final BPELPlanContext context = this.createContext(relationshipTemplate, plan, map, csar);

        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken here!
        // TODO implement fallback
        if (this.pluginRegistry.findTypePluginForCreation(relationshipTemplate, context.getCsar()) != null) {
            LOG.info("Handling RelationshipTemplate {} with type plugin",
                relationshipTemplate.getId());
            IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(relationshipTemplate, context.getCsar());
            this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);
        } else {
            LOG.debug("Couldn't handle RelationshipTemplate {} with type plugin",
                relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(context, relationshipTemplate)) {
                postPhasePlugin.handleCreate(context, relationshipTemplate);
            }
        }
    }

    private void runProvisioningLogicGeneration(final BPELPlan plan, final Property2VariableMapping map,
                                                final List<TNodeTemplate> nodeTemplates,
                                                final List<TRelationshipTemplate> relationshipTemplates,
                                                String serviceInstanceUrl, String serviceInstanceId,
                                                String serviceTemplateUrl, String planInstanceUrl, Csar csar) {
        for (final TNodeTemplate node : nodeTemplates) {
            this.runProvisioningLogicGeneration(plan, node, map, serviceInstanceUrl, serviceInstanceId,
                serviceTemplateUrl, planInstanceUrl, csar);
        }
        for (final TRelationshipTemplate relation : relationshipTemplates) {
            this.runProvisioningLogicGeneration(plan, relation, map, csar);
        }
    }

    private Map<String, String> transformSelectionStrategyListToMap(final List<String> selectionStrategyBorderNodes) {
        final Map<String, String> selectionStrategyBorderNodesMap = new HashMap<>();
        for (final String selectionStrategyBorderNode : selectionStrategyBorderNodes) {
            if (selectionStrategyBorderNode.split("\\[").length == 2 && selectionStrategyBorderNode.endsWith("]")) {
                final String selectionStrategy = selectionStrategyBorderNode.split("\\[")[0];
                final String borderNode = selectionStrategyBorderNode.split("\\[")[1].replace("]", "");
                selectionStrategyBorderNodesMap.put(selectionStrategy, borderNode);
            } else {
                LOG.error("Parsing Selection Strategies and border Node Templates had an error. Couldn't parse \""
                    + selectionStrategyBorderNode + "\" properly.");
            }
        }
        return selectionStrategyBorderNodesMap;
    }
}
