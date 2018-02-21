package org.opentosca.planbuilder.core.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractScaleOutPlanBuilder;
import org.opentosca.planbuilder.ScalingPlanDefinition;
import org.opentosca.planbuilder.ScalingPlanDefinition.AnnotatedAbstractNodeTemplate;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.EmptyPropertyToInputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELScaleOutProcessBuilder extends AbstractScaleOutPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELScaleOutProcessBuilder.class);

    // class for initializing properties inside the plan
    private final PropertyVariableInitializer propertyInitializer;

    // adds serviceInstance Variable and instanceDataAPIUrl to Plans
    private ServiceInstanceInitializer serviceInstanceInitializer;

    private NodeInstanceInitializer instanceInitializer;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private final CorrelationIDInitializer idInit = new CorrelationIDInitializer();

    private BPELPlanHandler planHandler;

    private final EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();

    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    public BPELScaleOutProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new ServiceInstanceInitializer();
            this.instanceInitializer = new NodeInstanceInitializer(this.planHandler);
        } catch (final ParserConfigurationException e) {
            BPELScaleOutProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);

        this.finalizer = new BPELFinalizer();
        this.opNames.add("install");
        this.opNames.add("configure");
        this.opNames.add("start");
    }

    private boolean addNodeInstanceIdToOutput(final BPELScopeActivity activ) {
        String inputName = "";
        if (activ.getNodeTemplate() != null) {
            inputName = "ProvisionedInstance_" + activ.getNodeTemplate().getId();
        } else {
            inputName = "ProvisionedInstance_" + activ.getRelationshipTemplate().getId();
        }

        this.planHandler.addStringElementToPlanRequest(inputName, activ.getBuildPlan());

        try {
            final String varName = new NodeInstanceInitializer(this.planHandler).findInstanceIdVarName(activ);
            this.planHandler.assginOutputWithVariableValue(varName, inputName, activ.getBuildPlan());
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final PropertyMap map,
                    final AbstractNodeTemplate nodeTemplate) {
        // fetch nodeInstance Variable to store the result at the end
        final BPELPlanContext nodeContext = this.createContext(nodeTemplate, plan, map);

        final String nodeInstanceVarName = this.findInstanceVar(nodeContext, nodeTemplate.getId(), true);

        // find first relationtemplate which is an infrastructure edge
        final AbstractRelationshipTemplate relationshipTemplate = this.getFirstInfrastructureRelation(nodeTemplate);
        if (relationshipTemplate == null) {
            return;
        }
        final BPELPlanContext relationContext = this.createContext(relationshipTemplate, plan, map);
        final String relationInstanceVarName = this.findInstanceVar(relationContext, relationshipTemplate.getId(),
            false);

        // create response variable
        final QName anyTypeDeclId = nodeContext.importQName(
            new QName("http://www.w3.org/2001/XMLSchema", "any", "xsd"));
        final String responseVarName = "recursiveSelection_NodeInstance_" + nodeTemplate.getId()
            + System.currentTimeMillis() + "_Response";
        nodeContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);

        // fetch relationInstance data
        try {
            Node fetchRelationInstanceData = new BPELProcessFragments().createRESTDeleteOnURLBPELVarAsNode(
                relationInstanceVarName, responseVarName);
            fetchRelationInstanceData = nodeContext.importNode(fetchRelationInstanceData);
            nodeContext.getPrePhaseElement().appendChild(fetchRelationInstanceData);
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (final ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // query its source node, which will be nodeInstance for this
        // NodeTemplate
        // set nodeInstance variable

        final String xpathQuery = "//*[local-name()='Reference' and @*[local-name()='title' and string()='SourceInstanceId']]/@*[local-name()='href']/string()";
        try {
            Node queryNodeInstanceUrl = new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode(
                "recursiveSelection_fetchNodeInstance" + System.currentTimeMillis(), xpathQuery, nodeInstanceVarName);
            queryNodeInstanceUrl = nodeContext.importNode(queryNodeInstanceUrl);
            nodeContext.getPrePhaseElement().appendChild(queryNodeInstanceUrl);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final PropertyMap map,
                    final AbstractRelationshipTemplate relationshipTemplate) {
        // fetch relationInstance variable of relationship template
        final BPELPlanContext relationContext = this.createContext(relationshipTemplate, plan, map);
        final String relationshipTemplateInstanceVarName = this.findInstanceVar(relationContext,
            relationshipTemplate.getId(), false);

        // fetch nodeInstance variable of source node template
        final BPELPlanContext nodeContext = this.createContext(relationshipTemplate.getTarget(), plan, map);
        final String nodeTemplateInstanceVarName = this.findInstanceVar(nodeContext,
            relationshipTemplate.getTarget().getId(), true);

        final String serviceInstanceIdVarName = this.serviceInstanceInitializer.getServiceInstanceVariableName(plan);

        // find relationshipTemplate instance that has the node template
        // instance as source

        final QName stringTypeDeclId = relationContext.importQName(
            new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
        final String requestVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId()
            + System.currentTimeMillis() + "_Request";
        final String responseVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId()
            + System.currentTimeMillis() + "_Response";

        relationContext.addVariable(requestVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);
        relationContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);

        try {
            Node requestRelationInstance = new BPELProcessFragments().createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(
                serviceInstanceIdVarName, relationshipTemplate.getId(), responseVarName, nodeTemplateInstanceVarName);
            requestRelationInstance = relationContext.importNode(requestRelationInstance);
            relationContext.getPrePhaseElement().appendChild(requestRelationInstance);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String xpathQuery = "//*[local-name()='Reference' and @*[local-name()='title' and string()!='Self']]/@*[local-name()='href']/string()";

        // set relationInstnace variable of the relationship templates' scope
        try {
            new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode(
                "recursiveSelection_fetchRelationInstance" + System.currentTimeMillis(), xpathQuery,
                relationshipTemplateInstanceVarName);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                    final QName serviceTemplateId) {
        throw new RuntimeException("A service Template can have multiple scaling plans, this method is not supported");
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            plans.addAll(this.buildScalingPlans(csarName, definitions, serviceTemplate.getQName()));
        }

        return plans;
    }

    public List<BPELPlan> buildScalingPlans(final String csarName, final AbstractDefinitions definitions,
                    final QName serviceTemplateId) {
        final List<BPELPlan> scalingPlans = new ArrayList<>();

        final AbstractServiceTemplate serviceTemplate = this.getServiceTemplate(definitions, serviceTemplateId);

        if (serviceTemplate == null) {
            return scalingPlans;
        }

        // check if the given serviceTemplate has the scaling plans defined as
        // tags

        final Map<String, String> tags = serviceTemplate.getTags();

        if (!tags.containsKey("scalingplans")) {
            return scalingPlans;
        }

        final List<ScalingPlanDefinition> scalingPlanDefinitions = this.fetchScalingPlansDefinitions(
            serviceTemplate.getTopologyTemplate(), tags);

        for (final ScalingPlanDefinition scalingPlanDefinition : scalingPlanDefinitions) {

            final String processName = serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name;
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";

            final AbstractPlan abstractScaleOutPlan = this.generateSOG(
                new QName(processNamespace, processName).toString(), definitions, serviceTemplate,
                scalingPlanDefinition);

            this.printGraph(abstractScaleOutPlan);

            final BPELPlan bpelScaleOutProcess = this.planHandler.createEmptyBPELPlan(processNamespace, processName,
                abstractScaleOutPlan, "scale-out");

            bpelScaleOutProcess.setTOSCAInterfaceName(scalingPlanDefinition.name);
            bpelScaleOutProcess.setTOSCAOperationname("scale-out");

            this.planHandler.initializeBPELSkeleton(bpelScaleOutProcess, csarName);

            final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(bpelScaleOutProcess);

            // instanceDataAPI handling is done solely trough this extension
            this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true,
                bpelScaleOutProcess);

            this.serviceInstanceInitializer.initializeInstanceDataAPIandServiceInstanceIDFromInput(bpelScaleOutProcess);

            this.instanceInitializer.addInstanceIDVarToTemplatePlans(bpelScaleOutProcess);

            this.idInit.addCorrellationID(bpelScaleOutProcess);

            final List<BPELScopeActivity> provScopeActivities = new ArrayList<>();

            for (final BPELScopeActivity act : bpelScaleOutProcess.getAbstract2BPEL().values()) {
                if (act.getNodeTemplate() != null
                    && scalingPlanDefinition.nodeTemplates.contains(act.getNodeTemplate())) {
                    provScopeActivities.add(act);
                } else if (act.getRelationshipTemplate() != null
                    && scalingPlanDefinition.relationshipTemplates.contains(act.getRelationshipTemplate())) {
                    provScopeActivities.add(act);
                }
            }

            this.emptyPropInit.initializeEmptyPropertiesAsInputParam(provScopeActivities, bpelScaleOutProcess, propMap);

            this.runProvisioningLogicGeneration(bpelScaleOutProcess, propMap, scalingPlanDefinition.nodeTemplates,
                scalingPlanDefinition.relationshipTemplates);

            // add generic instance selection

            for (final AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
                this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, relationshipTemplate);
            }
            for (final AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
                this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, nodeTemplate);
            }

            // TODO add plugin system

            for (final AnnotatedAbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
                final IScalingPlanBuilderSelectionPlugin selectionPlugin = this.findSelectionPlugin(stratNodeTemplate);
                if (selectionPlugin != null) {
                    final BPELScopeActivity scope = this.planHandler.getTemplateBuildPlanById(stratNodeTemplate.getId(),
                        bpelScaleOutProcess);
                    selectionPlugin.handle(new BPELPlanContext(scope, propMap, serviceTemplate), stratNodeTemplate,
                        new ArrayList<>(stratNodeTemplate.getAnnotations()));
                    this.addNodeInstanceIdToOutput(scope);
                }
            }

            this.finalizer.finalize(bpelScaleOutProcess);

            scalingPlans.add(bpelScaleOutProcess);

        }

        return scalingPlans;
    }

    private String cleanCSVString(String commaSeperatedList) {
        while (commaSeperatedList.endsWith(";") | commaSeperatedList.endsWith(",")) {
            commaSeperatedList = commaSeperatedList.substring(0, commaSeperatedList.length() - 2);
        }
        return commaSeperatedList;
    }

    public BPELPlanContext createContext(final AbstractNodeTemplate nodeTemplate, final BPELPlan plan,
                    final PropertyMap map) {
        return new BPELPlanContext(this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map,
            plan.getServiceTemplate());
    }

    public BPELPlanContext createContext(final AbstractRelationshipTemplate relationshipTemplate, final BPELPlan plan,
                    final PropertyMap map) {
        return new BPELPlanContext(this.planHandler.getTemplateBuildPlanById(relationshipTemplate.getId(), plan), map,
            plan.getServiceTemplate());
    }

    private AbstractNodeTemplate fetchNodeTemplate(final AbstractTopologyTemplate topologyTemplate,
                    final String nodeTemplateId) {
        for (final AbstractNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            if (nodeTemplate.getId().equals(nodeTemplateId)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    private List<AbstractNodeTemplate> fetchNodeTemplates(final AbstractTopologyTemplate topology,
                    String scalingPlanNodesRawValue) {
        final List<AbstractNodeTemplate> nodeTemplates = new ArrayList<>();

        scalingPlanNodesRawValue = this.cleanCSVString(scalingPlanNodesRawValue);

        // fetch nodeTemplateIds from raw value

        final List<String> scalingPlanNodeNames = this.getElementsFromCSV(scalingPlanNodesRawValue);

        for (final String scalingNodeName : scalingPlanNodeNames) {
            final AbstractNodeTemplate node = this.fetchNodeTemplate(topology, scalingNodeName);
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

    private List<AbstractRelationshipTemplate> fetchRelationshipTemplates(final AbstractTopologyTemplate topology,
                    String scalingPlanRelationsRawValue) {
        final List<AbstractRelationshipTemplate> relationshipTemplates = new ArrayList<>();

        scalingPlanRelationsRawValue = this.cleanCSVString(scalingPlanRelationsRawValue);

        // fetch nodeTemplateIds from raw value
        final List<String> scalingPlanRelationNames = this.getElementsFromCSV(scalingPlanRelationsRawValue);

        for (final AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
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

    private List<ScalingPlanDefinition> fetchScalingPlansDefinitions(final AbstractTopologyTemplate topology,
                    final Map<String, String> tags) {
        final List<ScalingPlanDefinition> scalingPlanDefinitions = new ArrayList<>();

        // fetch scaling plan names
        final String scalingPlanNamesRawValue = tags.get("scalingplans").trim();

        final List<String> scalingPlanNamesRaw = this.getElementsFromCSV(scalingPlanNamesRawValue);

        for (final String scalingPlanName : scalingPlanNamesRaw) {

            if (!scalingPlanName.trim().isEmpty() && tags.containsKey(scalingPlanName.trim())) {
                final String scalingPlanNodesNEdgesRawValue = tags.get(scalingPlanName.trim());

                final String[] scalingPlanNodesNEdgesRawValueSplit = scalingPlanNodesNEdgesRawValue.split(";");

                if (scalingPlanNodesNEdgesRawValueSplit.length != 3) {
                    LOG.error(
                        "Scaling Plan Definition '" + scalingPlanName + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AbstractNodeTemplate> nodeTemplates = this.fetchNodeTemplates(topology,
                    scalingPlanNodesNEdgesRawValueSplit[0]);

                if (nodeTemplates == null) {
                    LOG.error("Nodes of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AbstractRelationshipTemplate> relationshipTemplates = this.fetchRelationshipTemplates(
                    topology, scalingPlanNodesNEdgesRawValueSplit[1]);

                if (relationshipTemplates == null) {
                    LOG.error("Relations of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes = this.fetchSelectionStrategy2BorderNodes(
                    topology, scalingPlanNodesNEdgesRawValueSplit[2]);

                scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, topology, nodeTemplates,
                    relationshipTemplates, selectionStrategy2BorderNodes));
            }

        }

        return scalingPlanDefinitions;
    }

    private List<AnnotatedAbstractNodeTemplate> fetchSelectionStrategy2BorderNodes(
                    final AbstractTopologyTemplate topologyTemplate, String selectionStrategyBorderNodesCSV) {

        selectionStrategyBorderNodesCSV = this.cleanCSVString(selectionStrategyBorderNodesCSV);

        final List<String> selectionStrategyBorderNodes = this.getElementsFromCSV(selectionStrategyBorderNodesCSV);

        final Map<String, String> selectionStrategyBorderNodesMap = this.transformSelectionStrategyListToMap(
            selectionStrategyBorderNodes);

        final Map<String, AbstractNodeTemplate> selectionStrategyNodeTemplatesMap = new HashMap<>();

        final List<AnnotatedAbstractNodeTemplate> annotNodes = new ArrayList<>();

        for (final String selectionStrategy : selectionStrategyBorderNodesMap.keySet()) {
            final AbstractNodeTemplate node = this.fetchNodeTemplate(topologyTemplate,
                selectionStrategyBorderNodesMap.get(selectionStrategy));
            if (node != null) {
                if (this.findAnnotNode(annotNodes, node) != null) {
                    this.findAnnotNode(annotNodes, node).getAnnotations().add(selectionStrategy);
                } else {
                    final List<String> annot = new ArrayList<>();
                    annot.add(selectionStrategy);
                    annotNodes.add(new AnnotatedAbstractNodeTemplate(node, annot));
                }
            }
        }

        return annotNodes;
    }

    private AnnotatedAbstractNodeTemplate findAnnotNode(final List<AnnotatedAbstractNodeTemplate> annotNodes,
                    final AbstractNodeTemplate node) {
        for (final AnnotatedAbstractNodeTemplate annotNode : annotNodes) {
            if (annotNode.getId().equals(node.getId())) {
                return annotNode;
            }
        }
        return null;
    }

    private String findInstanceVar(final BPELPlanContext context, final String templateId, final boolean isNode) {
        final String instanceURLVarName = (isNode ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
        for (final String varName : context.getMainVariableNames()) {
            if (varName.contains(instanceURLVarName)) {
                return varName;
            }
        }
        return null;
    }

    private IScalingPlanBuilderSelectionPlugin findSelectionPlugin(
                    final AnnotatedAbstractNodeTemplate stratNodeTemplate) {

        for (final IScalingPlanBuilderSelectionPlugin plugin : this.pluginRegistry.getSelectionPlugins()) {
            final List<String> a = new ArrayList<>(stratNodeTemplate.getAnnotations());
            if (plugin.canHandle(stratNodeTemplate, a)) {
                return plugin;
            }
        }
        return null;
    }

    private List<String> getElementsFromCSV(String csvString) {
        csvString = this.cleanCSVString(csvString);
        final String[] scalingPlanRelationNamesRawSplit = csvString.split(",");
        return Arrays.asList(scalingPlanRelationNamesRawSplit);
    }

    private AbstractRelationshipTemplate getFirstInfrastructureRelation(final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractRelationshipTemplate> relations = ModelUtils.getOutgoingRelations(nodeTemplate,
            ModelUtils.TOSCABASETYPE_HOSTEDON);
        relations.addAll(ModelUtils.getOutgoingRelations(nodeTemplate, ModelUtils.TOSCABASETYPE_DEPENDSON));
        relations.addAll(ModelUtils.getOutgoingRelations(nodeTemplate, ModelUtils.TOSCABASETYPE_DEPLOYEDON));

        if (!relations.isEmpty()) {
            return relations.get(0);
        }

        return null;
    }

    private AbstractServiceTemplate getServiceTemplate(final AbstractDefinitions defs, final QName serviceTemplateId) {
        for (final AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {
            if (serviceTemplate.getTargetNamespace().equals(serviceTemplateId.getNamespaceURI())
                && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
                return serviceTemplate;
            }
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

    private void runProvisioningLogicGeneration(final BPELPlan plan, final AbstractNodeTemplate nodeTemplate,
                    final PropertyMap map) {
        // handling nodetemplate
        final BPELPlanContext context = new BPELPlanContext(
            this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map, plan.getServiceTemplate());
        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken!
        final IPlanBuilderTypePlugin plugin = this.findTypePlugin(nodeTemplate);
        if (plugin == null) {
            BPELScaleOutProcessBuilder.LOG.debug("Handling NodeTemplate {} with ProvisioningChain",
                nodeTemplate.getId());
            final OperationChain chain = BPELScopeBuilder.createOperationChain(nodeTemplate);
            if (chain == null) {
                BPELScaleOutProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}",
                    nodeTemplate.getId());
            } else {
                BPELScaleOutProcessBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}",
                    nodeTemplate.getId());
                chain.executeIAProvisioning(context);
                chain.executeDAProvisioning(context);
                chain.executeOperationProvisioning(context, this.opNames);
            }
        } else {
            BPELScaleOutProcessBuilder.LOG.info("Handling NodeTemplate {} with generic plugin", nodeTemplate.getId());
            plugin.handle(context);
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandle(nodeTemplate)) {
                postPhasePlugin.handle(context, nodeTemplate);
            }
        }
    }

    private void runProvisioningLogicGeneration(final BPELPlan plan,
                    final AbstractRelationshipTemplate relationshipTemplate, final PropertyMap map) {
        // handling relationshiptemplate

        final BPELPlanContext context = this.createContext(relationshipTemplate, plan, map);

        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken here!
        // TODO implement fallback
        if (this.findTypePlugin(relationshipTemplate) == null) {
            BPELScaleOutProcessBuilder.LOG.debug("Handling RelationshipTemplate {} with ProvisioningChains",
                relationshipTemplate.getId());
            final OperationChain sourceChain = BPELScopeBuilder.createOperationChain(relationshipTemplate, true);
            final OperationChain targetChain = BPELScopeBuilder.createOperationChain(relationshipTemplate, false);

            // first execute provisioning on target, then on source
            if (targetChain != null) {
                BPELScaleOutProcessBuilder.LOG.warn(
                    "Couldn't create ProvisioningChain for TargetInterface of RelationshipTemplate {}",
                    relationshipTemplate.getId());
                targetChain.executeIAProvisioning(context);
                targetChain.executeOperationProvisioning(context, this.opNames);
            }

            if (sourceChain != null) {
                BPELScaleOutProcessBuilder.LOG.warn(
                    "Couldn't create ProvisioningChain for SourceInterface of RelationshipTemplate {}",
                    relationshipTemplate.getId());
                sourceChain.executeIAProvisioning(context);
                sourceChain.executeOperationProvisioning(context, this.opNames);
            }
        } else {
            BPELScaleOutProcessBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin",
                relationshipTemplate.getId());
            this.handleWithTypePlugin(context, relationshipTemplate);
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandle(relationshipTemplate)) {
                postPhasePlugin.handle(context, relationshipTemplate);
            }
        }
    }

    private void runProvisioningLogicGeneration(final BPELPlan plan, final PropertyMap map,
                    final List<AbstractNodeTemplate> nodeTemplates,
                    final List<AbstractRelationshipTemplate> relationshipTemplates) {
        for (final AbstractNodeTemplate node : nodeTemplates) {
            this.runProvisioningLogicGeneration(plan, node, map);
        }
        for (final AbstractRelationshipTemplate relation : relationshipTemplates) {
            this.runProvisioningLogicGeneration(plan, relation, map);
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
