package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.AbstractScaleOutPlanBuilder;
import org.opentosca.planbuilder.ScalingPlanDefinition;
import org.opentosca.planbuilder.ScalingPlanDefinition.AnnotatedAbstractNodeTemplate;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.OperationChain;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.typebased.IScalingPlanBuilderSelectionPlugin;
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
    private final PropertyVariableHandler propertyInitializer;

    // adds serviceInstance Variable and instanceDataAPIUrl to Plans

    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;

    private NodeRelationInstanceVariablesHandler instanceInitializer;


    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private BPELPlanHandler planHandler;

    private final EmptyPropertyToInputHandler emptyPropInit = new EmptyPropertyToInputHandler();

    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    private CorrelationIDInitializer correlationHandler;
    
    private BPELPluginHandler pluginhandler = new BPELPluginHandler();

    public BPELScaleOutProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceInitializer = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        }
        catch (final ParserConfigurationException e) {
            BPELScaleOutProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);

        this.finalizer = new BPELFinalizer();
        this.opNames.add("install");
        this.opNames.add("configure");
        this.opNames.add("start");
    }

    private boolean addInstanceIdToOutput(final BPELScope activ, AbstractServiceTemplate serviceTemplate) {
        String outputName = "";
        if (activ.getNodeTemplate() != null) {
            outputName = "CreatedInstance_" + activ.getNodeTemplate().getId();
        } else {
            outputName = "CreatedInstance_" + activ.getRelationshipTemplate().getId();
        }

        this.planHandler.addStringElementToPlanResponse(outputName, activ.getBuildPlan());

        final String varName = this.instanceInitializer.findInstanceUrlVarName(activ, serviceTemplate);
        this.planHandler.assginOutputWithVariableValue(varName, outputName, activ.getBuildPlan());

        return true;
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final Property2VariableMapping map,
                                               final AbstractNodeTemplate nodeTemplate,
                                               AbstractServiceTemplate serviceTemplate, String csarFileName) {
        // fetch nodeInstance Variable to store the result at the end
        final BPELPlanContext nodeContext = this.createContext(nodeTemplate, plan, map, csarFileName);

        String serviceInstanceIdVarName = nodeContext.getServiceInstanceIDVarName();
        
        final String nodeInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, nodeTemplate.getId(), true);

        // find first relationtemplate which is an infrastructure edge
        
        final AbstractRelationshipTemplate relationshipTemplate = this.getFristIngoingInfrastructureRelation(nodeTemplate);
        if (relationshipTemplate == null) {
            return;
        }        
        final String relationInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, relationshipTemplate.getId(), false);

        
        
        // create response variable
        final QName anyTypeDeclId =
            nodeContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
        String responseVarName =
            "recursiveSelection_NodeInstance_" + nodeTemplate.getId() + System.currentTimeMillis() + "_Response";
        boolean added  = nodeContext.addGlobalVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);
        
      

        final String serviceTemplateUrlVarName = serviceInstanceHandler.getServiceTemplateURLVariableName(plan);

        // fetch relationInstance data
        try {
            // createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode
            Node fetchNodeInstanceData =
                new BPELProcessFragments().createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
                                                                                           responseVarName,
                                                                                           nodeTemplate.getId(),
                                                                                           "?target=$bpelvar["
                                                                                               + relationInstanceVarName
                                                                                               + "]&amp;serviceInstanceId=$bpelvar[" + serviceInstanceIdVarName + "]");
            
            
            fetchNodeInstanceData = nodeContext.importNode(fetchNodeInstanceData);
            nodeContext.getPrePhaseElement().appendChild(fetchNodeInstanceData);
        }
        catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // query its source node, which will be nodeInstance for this
        // NodeTemplate
        // set nodeInstance variable

        final String xpathQuery =
            "//*[local-name()='NodeTemplateInstanceResources']/*[local-name()='NodeTemplateInstances']/*[local-name()='NodeTemplateInstance'][1]/*[local-name()='Links']/*[local-name()='Link']/@*[local-name()='href']/string()";
        try {

            Node queryNodeInstanceUrl =
                new BPELProcessFragments().createAssignVarToVarWithXpathQueryAsNode("recursiveSelection_fetchNodeInstance"
                    + System.currentTimeMillis(), responseVarName, nodeInstanceVarName, xpathQuery);
            queryNodeInstanceUrl = nodeContext.importNode(queryNodeInstanceUrl);
            nodeContext.getPrePhaseElement().appendChild(queryNodeInstanceUrl);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addRecursiveInstanceSelection(final BPELPlan plan, final Property2VariableMapping map,
                                               final AbstractRelationshipTemplate relationshipTemplate,
                                               AbstractServiceTemplate serviceTemplate, String csarFileName) {
        // fetch relationInstance variable of relationship template
        final BPELPlanContext relationContext = this.createContext(relationshipTemplate, plan, map, csarFileName);
        final String relationshipTemplateInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate, relationshipTemplate.getId(), false);

        // fetch nodeInstance variable of source node template
        final PlanContext nodeContext =
            this.createContext(relationshipTemplate.getSource(), plan, map, csarFileName);
        final String nodeTemplateInstanceVarName =
            this.instanceInitializer.findInstanceUrlVarName(plan, serviceTemplate,
                                                            relationshipTemplate.getSource().getId(), true);
                

        final String serviceInstanceIdVarName = nodeContext.getServiceInstanceIDVarName();

        final String serviceTemplateUrlVarName = serviceInstanceHandler.getServiceTemplateURLVariableName(plan);

        // find relationshipTemplate instance that has the node template
        // instance as source

        final QName stringTypeDeclId =
            relationContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd"));
       
        final String responseVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId() + "_"
            + System.currentTimeMillis() + "_Response";

        relationContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);

        try {
            Node requestRelationInstance = new BPELProcessFragments().createRESTExtensionGETForRelationInstanceDataAsNode(serviceTemplateUrlVarName,
                                                                                       responseVarName,
                                                                                       relationshipTemplate.getId(),
                                                                                       "?source=$bpelvar["
                                                                                           + nodeTemplateInstanceVarName
                                                                                           + "]&amp;serviceInstanceId=$bpelvar[" + serviceInstanceIdVarName + "]");
            
            requestRelationInstance = relationContext.importNode(requestRelationInstance);
            relationContext.getPrePhaseElement().appendChild(requestRelationInstance);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String xpathQuery =
            "//*[local-name()='RelationshipTemplateInstanceResources']/*[local-name()='RelationshipTemplateInstances']/*[local-name()='RelationshipTemplateInstance'][1]/*[local-name()='Links']/*[local-name()='Link']/@*[local-name()='href']/string()";

        // set relationInstnace variable of the relationship templates' scope
        try {                     
            Node queryRelationInstance = new BPELProcessFragments().createAssignVarToVarWithXpathQueryAsNode("recursiveSelection_fetchRelationInstance"
                + System.currentTimeMillis(), responseVarName, relationshipTemplateInstanceVarName, xpathQuery);
            queryRelationInstance = relationContext.importNode(queryRelationInstance);
            relationContext.getPrePhaseElement().appendChild(queryRelationInstance);

        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {
        throw new RuntimeException("A service Template can have multiple scaling plans, this method is not supported");
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            plans.addAll(buildScalingPlans(csarName, definitions, serviceTemplate.getQName()));
        }

        return plans;
    }

    public List<BPELPlan> buildScalingPlans(final String csarName, final AbstractDefinitions definitions,
                                            final QName serviceTemplateId) {
        final List<BPELPlan> scalingPlans = new ArrayList<>();



        final AbstractServiceTemplate serviceTemplate = getServiceTemplate(definitions, serviceTemplateId);

        if (serviceTemplate == null) {
            return scalingPlans;
        }

        // check if the given serviceTemplate has the scaling plans defined as
        // tags

        final Map<String, String> tags = serviceTemplate.getTags();

        if (!tags.containsKey("scalingplans")) {
            return scalingPlans;
        }

        final List<ScalingPlanDefinition> scalingPlanDefinitions =
            fetchScalingPlansDefinitions(serviceTemplate.getTopologyTemplate(), tags);

        for (final ScalingPlanDefinition scalingPlanDefinition : scalingPlanDefinitions) {

            final String processName =
                ModelUtils.makeValidNCName(serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name);
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";

            final AbstractPlan abstractScaleOutPlan = generateSOG(new QName(processNamespace, processName).toString(),
                                                                  definitions, serviceTemplate, scalingPlanDefinition);


            final BPELPlan bpelScaleOutProcess =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractScaleOutPlan, "scale-out");

            bpelScaleOutProcess.setTOSCAInterfaceName(scalingPlanDefinition.name);
            bpelScaleOutProcess.setTOSCAOperationname("scale-out");

            this.planHandler.initializeBPELSkeleton(bpelScaleOutProcess, csarName);

            final Property2VariableMapping propMap =
                this.propertyInitializer.initializePropertiesAsVariables(bpelScaleOutProcess, serviceTemplate);

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

            this.emptyPropInit.initializeEmptyPropertiesAsInputParam(provScopeActivities, bpelScaleOutProcess, propMap,
                                                                     serviceInstanceUrl, serviceInstanceId,
                                                                     serviceTemplateUrl, serviceTemplate, csarName);

            this.runProvisioningLogicGeneration(bpelScaleOutProcess, propMap, scalingPlanDefinition.nodeTemplates,
                                                scalingPlanDefinition.relationshipTemplates, serviceInstanceUrl,
                                                serviceInstanceId, serviceTemplateUrl, csarName);

            // add generic instance selection

            for (final AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
                this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, relationshipTemplate, serviceTemplate,
                                                   csarName);
            }
            for (final AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
                this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, nodeTemplate, serviceTemplate,
                                                   csarName);
            }

            for (final AnnotatedAbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {                
                this.pluginhandler.handleActivity(this.createContext(stratNodeTemplate, bpelScaleOutProcess, propMap, csarName), bpelScaleOutProcess.getTemplateBuildPlan(stratNodeTemplate), stratNodeTemplate);                
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
                                         final Property2VariableMapping map, String csarFileName) {

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(plan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);

        return new BPELPlanContext(plan,this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map,
            plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarFileName);
    }

    public BPELPlanContext createContext(final AbstractRelationshipTemplate relationshipTemplate, final BPELPlan plan,
                                         final Property2VariableMapping map, String csarFileName) {

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(plan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);

        return new BPELPlanContext(plan,this.planHandler.getTemplateBuildPlanById(relationshipTemplate.getId(), plan), map,
            plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarFileName);
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

        scalingPlanNodesRawValue = cleanCSVString(scalingPlanNodesRawValue);

        // fetch nodeTemplateIds from raw value

        final List<String> scalingPlanNodeNames = getElementsFromCSV(scalingPlanNodesRawValue);

        for (final String scalingNodeName : scalingPlanNodeNames) {
            final AbstractNodeTemplate node = fetchNodeTemplate(topology, scalingNodeName);
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

        scalingPlanRelationsRawValue = cleanCSVString(scalingPlanRelationsRawValue);

        // fetch nodeTemplateIds from raw value
        final List<String> scalingPlanRelationNames = getElementsFromCSV(scalingPlanRelationsRawValue);

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

                final List<AbstractNodeTemplate> nodeTemplates =
                    fetchNodeTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[0]);

                if (nodeTemplates == null) {
                    LOG.error("Nodes of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AbstractRelationshipTemplate> relationshipTemplates =
                    fetchRelationshipTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[1]);

                if (relationshipTemplates == null) {
                    LOG.error("Relations of Scaling Plan Definition '" + scalingPlanName
                        + "' couldn't be parsed properly, skipping");
                    continue;
                }

                final List<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes =
                    fetchSelectionStrategy2BorderNodes(topology, scalingPlanNodesNEdgesRawValueSplit[2]);

                scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, topology, nodeTemplates,
                    relationshipTemplates, selectionStrategy2BorderNodes));
            }

        }

        return scalingPlanDefinitions;
    }

    private List<AnnotatedAbstractNodeTemplate> fetchSelectionStrategy2BorderNodes(final AbstractTopologyTemplate topologyTemplate,
                                                                                   String selectionStrategyBorderNodesCSV) {

        selectionStrategyBorderNodesCSV = cleanCSVString(selectionStrategyBorderNodesCSV);

        final List<String> selectionStrategyBorderNodes = getElementsFromCSV(selectionStrategyBorderNodesCSV);

        final Map<String, String> selectionStrategyBorderNodesMap =
            transformSelectionStrategyListToMap(selectionStrategyBorderNodes);                

        final List<AnnotatedAbstractNodeTemplate> annotNodes = new ArrayList<>();

        for (final String annotatedNode : selectionStrategyBorderNodesMap.keySet()) {
            final AbstractNodeTemplate node =
                fetchNodeTemplate(topologyTemplate, annotatedNode);
            if (node != null) {
                if (findAnnotNode(annotNodes, node) != null) {
                    findAnnotNode(annotNodes, node).getAnnotations().add(selectionStrategyBorderNodesMap.get(annotatedNode));
                } else {
                    final List<String> annot = new ArrayList<>();
                    annot.add(selectionStrategyBorderNodesMap.get(annotatedNode));
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

    private List<String> getElementsFromCSV(String csvString) {
        csvString = cleanCSVString(csvString);
        final String[] scalingPlanRelationNamesRawSplit = csvString.split(",");
        return Arrays.asList(scalingPlanRelationNamesRawSplit);
    }
    
    private AbstractRelationshipTemplate getFristIngoingInfrastructureRelation(final AbstractNodeTemplate nodeTemplate) {
        final List<AbstractRelationshipTemplate> relations =
            ModelUtils.getIngoingRelations(nodeTemplate, Types.hostedOnRelationType);
        relations.addAll(ModelUtils.getIngoingRelations(nodeTemplate, Types.dependsOnRelationType));
        relations.addAll(ModelUtils.getIngoingRelations(nodeTemplate, Types.deployedOnRelationType));

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

    private void runProvisioningLogicGeneration(final BPELPlan plan, final Property2VariableMapping map,
                                                final List<AbstractNodeTemplate> nodeTemplates,
                                                final List<AbstractRelationshipTemplate> relationshipTemplates,
                                                String serviceInstanceUrl, String serviceInstanceId,
                                                String serviceTemplateUrl, String csarFileName) {
        for (final AbstractNodeTemplate node : nodeTemplates) {
            // handling nodetemplate
            final BPELPlanContext context =
                new BPELPlanContext(plan,this.planHandler.getTemplateBuildPlanById(node.getId(), plan), map,
                    plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarFileName);
            // check if we have a generic plugin to handle the template
            // Note: if a generic plugin fails during execution the
            // TemplateBuildPlan is broken!
            
            this.pluginhandler.handleActivity(context, plan.getTemplateBuildPlan(node), node);
        }
        for (final AbstractRelationshipTemplate relation : relationshipTemplates) {
            // handling nodetemplate
            final BPELPlanContext context =
                new BPELPlanContext(plan,this.planHandler.getTemplateBuildPlanById(relation.getId(), plan), map,
                    plan.getServiceTemplate(), serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarFileName);
            // check if we have a generic plugin to handle the template
            // Note: if a generic plugin fails during execution the
            // TemplateBuildPlan is broken!
            
            this.pluginhandler.handleActivity(context, plan.getTemplateBuildPlan(relation), relation);
        }
    }

    private Map<String, String> transformSelectionStrategyListToMap(final List<String> selectionStrategyBorderNodes) {
        final Map<String, String> selectionStrategyBorderNodesMap = new HashMap<>();
        for (final String selectionStrategyBorderNode : selectionStrategyBorderNodes) {
            if (selectionStrategyBorderNode.split("\\[").length == 2 && selectionStrategyBorderNode.endsWith("]")) {
                final String selectionStrategy = selectionStrategyBorderNode.split("\\[")[0];
                final String borderNode = selectionStrategyBorderNode.split("\\[")[1].replace("]", "");
                selectionStrategyBorderNodesMap.put(borderNode, selectionStrategy);
            } else {
                LOG.error("Parsing Selection Strategies and border Node Templates had an error. Couldn't parse \""
                    + selectionStrategyBorderNode + "\" properly.");
            }
        }
        return selectionStrategyBorderNodesMap;
    }

}
