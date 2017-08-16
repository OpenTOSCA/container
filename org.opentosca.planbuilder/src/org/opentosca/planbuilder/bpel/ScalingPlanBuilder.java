package org.opentosca.planbuilder.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractPlanBuilder;
import org.opentosca.planbuilder.AbstractScaleOutPlanBuilder;
import org.opentosca.planbuilder.ScalingPlanDefinition;
import org.opentosca.planbuilder.ScalingPlanDefinition.AnnotatedAbstractNodeTemplate;
import org.opentosca.planbuilder.bpel.BPELScopeBuilder.ProvisioningChain;
import org.opentosca.planbuilder.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.bpel.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.bpel.helpers.EmptyPropertyToInputInitializer;
import org.opentosca.planbuilder.bpel.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.bpel.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.bpel.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.utils.Utils;
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
public class ScalingPlanBuilder extends AbstractScaleOutPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(ScalingPlanBuilder.class);
	
	
	// class for initializing properties inside the plan
	private PropertyVariableInitializer propertyInitializer;
	
	// adds serviceInstance Variable and instanceDataAPIUrl to Plans
	private ServiceInstanceInitializer serviceInstanceInitializer;
	
	private NodeInstanceInitializer instanceInitializer;
	
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private BPELFinalizer finalizer;
	
	private CorrelationIDInitializer idInit = new CorrelationIDInitializer();
	
	private BPELPlanHandler planHandler;
	
	private EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();
	
	// accepted operations for provisioning
	private List<String> opNames = new ArrayList<String>();
	
	
	public ScalingPlanBuilder() {
		try {
			this.planHandler = new BPELPlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceInitializer();
			this.instanceInitializer = new NodeInstanceInitializer(planHandler);
		} catch (ParserConfigurationException e) {
			ScalingPlanBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}				
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		
		this.finalizer = new BPELFinalizer();
		this.opNames.add("install");
		this.opNames.add("configure");
		this.opNames.add("start");
	}
	
	@Override
	public BPELPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		throw new RuntimeException("A service Template can have multiple scaling plans, this method is not supported");
	}
	
	@Override
	public List<AbstractPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<AbstractPlan> plans = new ArrayList<AbstractPlan>();
		
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			plans.addAll(this.buildScalingPlans(csarName, definitions, serviceTemplate.getQName()));
		}
		
		return plans;
	}
	
	public List<BPELPlan> buildScalingPlans(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		List<BPELPlan> scalingPlans = new ArrayList<BPELPlan>();
		
		AbstractServiceTemplate serviceTemplate = this.getServiceTemplate(definitions, serviceTemplateId);
		
		if (serviceTemplate == null) {
			return scalingPlans;
		}
		
		// check if the given serviceTemplate has the scaling plans defined as
		// tags
		
		Map<String, String> tags = serviceTemplate.getTags();
		
		if (!tags.containsKey("scalingplans")) {
			return scalingPlans;
		}
		
		List<ScalingPlanDefinition> scalingPlanDefinitions = this.fetchScalingPlansDefinitions(serviceTemplate.getTopologyTemplate(), tags);
		
		for (ScalingPlanDefinition scalingPlanDefinition : scalingPlanDefinitions) {
			
			String processName = serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name;
			String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";
			
			AbstractPlan abstractScaleOutPlan = this.generateSOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, scalingPlanDefinition);
			
			this.printGraph(abstractScaleOutPlan);
			
			BPELPlan bpelScaleOutProcess = this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractScaleOutPlan);
			
			bpelScaleOutProcess.setTOSCAInterfaceName(scalingPlanDefinition.name);
			bpelScaleOutProcess.setTOSCAOperationname("scale-out");
			
			this.planHandler.initializeBPELSkeleton(bpelScaleOutProcess, csarName);
			
			PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(bpelScaleOutProcess);
			
			// instanceDataAPI handling is done solely trough this extension
			this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, bpelScaleOutProcess);
			
			this.serviceInstanceInitializer.initializeInstanceDataAPIandServiceInstanceIDFromInput(bpelScaleOutProcess);
			
			this.instanceInitializer.addInstanceIDVarToTemplatePlans(bpelScaleOutProcess);
			
			this.idInit.addCorrellationID(bpelScaleOutProcess);
			
			List<BPELScopeActivity> provScopeActivities = new ArrayList<BPELScopeActivity>();
			
			for(BPELScopeActivity act : bpelScaleOutProcess.getAbstract2BPEL().values()) {
				if(act.getNodeTemplate() != null && scalingPlanDefinition.nodeTemplates.contains(act.getNodeTemplate())) {
					provScopeActivities.add(act);
				} else if(act.getRelationshipTemplate() != null && scalingPlanDefinition.relationshipTemplates.contains(act.getRelationshipTemplate())) {
					provScopeActivities.add(act);
				}
			}
			
			this.emptyPropInit.initializeEmptyPropertiesAsInputParam(provScopeActivities,bpelScaleOutProcess, propMap);
			
			this.runProvisioningLogicGeneration(bpelScaleOutProcess, propMap, scalingPlanDefinition.nodeTemplates, scalingPlanDefinition.relationshipTemplates);
			
			// add generic instance selection
			
			for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
				this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, relationshipTemplate);
			}
			for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
				this.addRecursiveInstanceSelection(bpelScaleOutProcess, propMap, nodeTemplate);
			}
			
			// TODO add plugin system
			
			for (AnnotatedAbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes) {
				IScalingPlanBuilderSelectionPlugin selectionPlugin = this.findSelectionPlugin(stratNodeTemplate);
				if (selectionPlugin != null) {					
					selectionPlugin.handle(new TemplatePlanContext(this.planHandler.getTemplateBuildPlanById(stratNodeTemplate.getId(), bpelScaleOutProcess), propMap, serviceTemplate), (AbstractNodeTemplate) stratNodeTemplate, new ArrayList<String>(stratNodeTemplate.getAnnotations()));
				}
			}
			
			this.finalizer.finalize(bpelScaleOutProcess);
			
			scalingPlans.add(bpelScaleOutProcess);
			
		}
		
		return scalingPlans;
	}
	
	private IScalingPlanBuilderSelectionPlugin findSelectionPlugin(AnnotatedAbstractNodeTemplate stratNodeTemplate) {
		
		for (IScalingPlanBuilderSelectionPlugin plugin : PluginRegistry.getSelectionPlugins()) {
			List<String> a = new ArrayList<>(stratNodeTemplate.getAnnotations());
			if (plugin.canHandle(stratNodeTemplate, a)) {
				return plugin;
			}
		}
		return null;
	}
	
	private void printGraph(AbstractPlan abstractScaleOutPlan) {
		
		LOG.debug("Scale Out Plan: " + abstractScaleOutPlan.getId());
		
		LOG.debug("Activities: ");
		
		for (AbstractActivity activ : abstractScaleOutPlan.getActivites()) {
			LOG.debug("id: " + activ.getId() + " type: " + activ.getType());
		}
		
		LOG.debug("Links: ");
		for (AbstractActivity source : abstractScaleOutPlan.getLinks().keySet()) {
			String srcId;
			String trgtId;
			
			if (source != null) {
				srcId = source.getId();
			} else {
				srcId = null;
			}
			
			if (abstractScaleOutPlan.getLinks().get(source) != null) {
				trgtId = abstractScaleOutPlan.getLinks().get(source).getId();
			} else {
				trgtId = null;
			}
			
			LOG.debug("(" + srcId + ", " + trgtId + ")");
		}
		
	}
	
	public TemplatePlanContext createContext(AbstractRelationshipTemplate relationshipTemplate, BPELPlan plan, PropertyMap map) {
		return new TemplatePlanContext(this.planHandler.getTemplateBuildPlanById(relationshipTemplate.getId(), plan), map, plan.getServiceTemplate());
	}
	
	public TemplatePlanContext createContext(AbstractNodeTemplate nodeTemplate, BPELPlan plan, PropertyMap map) {
		return new TemplatePlanContext(this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map, plan.getServiceTemplate());
	}
	
	private void addRecursiveInstanceSelection(BPELPlan plan, PropertyMap map, AbstractNodeTemplate nodeTemplate) {
		// fetch nodeInstance Variable to store the result at the end
		TemplatePlanContext nodeContext = this.createContext(nodeTemplate, plan, map);
		
		String nodeInstanceVarName = this.findInstanceVar(nodeContext, nodeTemplate.getId(), true);
		
		// find first relationtemplate which is an infrastructure edge
		AbstractRelationshipTemplate relationshipTemplate = this.getFirstInfrastructureRelation(nodeTemplate);
		if (relationshipTemplate == null) {
			return;
		}
		TemplatePlanContext relationContext = this.createContext(relationshipTemplate, plan, map);
		String relationInstanceVarName = this.findInstanceVar(relationContext, relationshipTemplate.getId(), false);
		
		// create response variable
		QName anyTypeDeclId = nodeContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "any", "xsd"));
		String responseVarName = "recursiveSelection_NodeInstance_" + nodeTemplate.getId() + System.currentTimeMillis() + "_Response";
		nodeContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, anyTypeDeclId);
		
		// fetch relationInstance data
		try {
			Node fetchRelationInstanceData = new BPELProcessFragments().createRESTDeleteOnURLBPELVarAsNode(relationInstanceVarName, responseVarName);
			fetchRelationInstanceData = nodeContext.importNode(fetchRelationInstanceData);
			nodeContext.getPrePhaseElement().appendChild(fetchRelationInstanceData);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// query its source node, which will be nodeInstance for this
		// NodeTemplate
		// set nodeInstance variable
		
		String xpathQuery = "//*[local-name()='Reference' and @*[local-name()='title' and string()='SourceInstanceId']]/@*[local-name()='href']/string()";
		try {
			Node queryNodeInstanceUrl = new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("recursiveSelection_fetchNodeInstance" + System.currentTimeMillis(), xpathQuery, nodeInstanceVarName);
			queryNodeInstanceUrl = nodeContext.importNode(queryNodeInstanceUrl);
			nodeContext.getPrePhaseElement().appendChild(queryNodeInstanceUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private AbstractRelationshipTemplate getFirstInfrastructureRelation(AbstractNodeTemplate nodeTemplate) {
		List<AbstractRelationshipTemplate> relations = Utils.getOutgoingRelations(nodeTemplate, Utils.TOSCABASETYPE_HOSTEDON);
		relations.addAll(Utils.getOutgoingRelations(nodeTemplate, Utils.TOSCABASETYPE_DEPENDSON));
		relations.addAll(Utils.getOutgoingRelations(nodeTemplate, Utils.TOSCABASETYPE_DEPLOYEDON));
		
		if (!relations.isEmpty()) {
			return relations.get(0);
		}
		
		return null;
	}
	
	private void addRecursiveInstanceSelection(BPELPlan plan, PropertyMap map, AbstractRelationshipTemplate relationshipTemplate) {
		// fetch relationInstance variable of relationship template
		TemplatePlanContext relationContext = this.createContext(relationshipTemplate, plan, map);
		String relationshipTemplateInstanceVarName = this.findInstanceVar(relationContext, relationshipTemplate.getId(), false);
		
		// fetch nodeInstance variable of source node template
		TemplatePlanContext nodeContext = this.createContext(relationshipTemplate.getTarget(), plan, map);
		String nodeTemplateInstanceVarName = this.findInstanceVar(nodeContext, relationshipTemplate.getTarget().getId(), true);
		
		String serviceInstanceIdVarName = this.serviceInstanceInitializer.getServiceInstanceVariableName(plan);
		
		// find relationshipTemplate instance that has the node template
		// instance as source
		
		QName stringTypeDeclId = relationContext.importQName(new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		String requestVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId() + System.currentTimeMillis() + "_Request";
		String responseVarName = "recursiveSelection_RelationInstance_" + relationshipTemplate.getId() + System.currentTimeMillis() + "_Response";
		
		relationContext.addVariable(requestVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);
		relationContext.addVariable(responseVarName, BPELPlan.VariableType.TYPE, stringTypeDeclId);
		
		try {
			Node requestRelationInstance = new BPELProcessFragments().createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(serviceInstanceIdVarName, relationshipTemplate.getId(), responseVarName, nodeTemplateInstanceVarName);
			requestRelationInstance = relationContext.importNode(requestRelationInstance);
			relationContext.getPrePhaseElement().appendChild(requestRelationInstance);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String xpathQuery = "//*[local-name()='Reference' and @*[local-name()='title' and string()!='Self']]/@*[local-name()='href']/string()";
		
		// set relationInstnace variable of the relationship templates' scope
		try {
			new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("recursiveSelection_fetchRelationInstance" + System.currentTimeMillis(), xpathQuery, relationshipTemplateInstanceVarName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String findInstanceVar(TemplatePlanContext context, String templateId, boolean isNode) {
		String instanceURLVarName = ((isNode) ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
		for (String varName : context.getMainVariableNames()) {
			if (varName.contains(instanceURLVarName)) {
				return varName;
			}
		}
		return null;
	}
	
	private void runProvisioningLogicGeneration(BPELPlan plan, PropertyMap map, List<AbstractNodeTemplate> nodeTemplates, List<AbstractRelationshipTemplate> relationshipTemplates) {
		for (AbstractNodeTemplate node : nodeTemplates) {
			this.runProvisioningLogicGeneration(plan, node, map);
		}
		for (AbstractRelationshipTemplate relation : relationshipTemplates) {
			this.runProvisioningLogicGeneration(plan, relation, map);
		}
	}
	
	private void runProvisioningLogicGeneration(BPELPlan plan, AbstractRelationshipTemplate relationshipTemplate, PropertyMap map) {
		// handling relationshiptemplate
		
		TemplatePlanContext context = this.createContext(relationshipTemplate, plan, map);
		
		// check if we have a generic plugin to handle the template
		// Note: if a generic plugin fails during execution the
		// TemplateBuildPlan is broken here!
		// TODO implement fallback
		if (this.findTypePlugin(relationshipTemplate) == null) {
			ScalingPlanBuilder.LOG.debug("Handling RelationshipTemplate {} with ProvisioningChains", relationshipTemplate.getId());
			ProvisioningChain sourceChain = BPELScopeBuilder.createProvisioningChain(relationshipTemplate, true);
			ProvisioningChain targetChain = BPELScopeBuilder.createProvisioningChain(relationshipTemplate, false);
			
			// first execute provisioning on target, then on source
			if (targetChain != null) {
				ScalingPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for TargetInterface of RelationshipTemplate {}", relationshipTemplate.getId());
				targetChain.executeIAProvisioning(context);
				targetChain.executeOperationProvisioning(context, this.opNames);
			}
			
			if (sourceChain != null) {
				ScalingPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for SourceInterface of RelationshipTemplate {}", relationshipTemplate.getId());
				sourceChain.executeIAProvisioning(context);
				sourceChain.executeOperationProvisioning(context, this.opNames);
			}
		} else {
			ScalingPlanBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin", relationshipTemplate.getId());
			this.handleWithTypePlugin(context, relationshipTemplate);
		}
		
		for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
			if (postPhasePlugin.canHandle(relationshipTemplate)) {
				postPhasePlugin.handle(context, relationshipTemplate);
			}
		}
	}
	
	private void runProvisioningLogicGeneration(BPELPlan plan, AbstractNodeTemplate nodeTemplate, PropertyMap map) {
		// handling nodetemplate
		TemplatePlanContext context = new TemplatePlanContext(this.planHandler.getTemplateBuildPlanById(nodeTemplate.getId(), plan), map, plan.getServiceTemplate());
		// check if we have a generic plugin to handle the template
		// Note: if a generic plugin fails during execution the
		// TemplateBuildPlan is broken!
		IPlanBuilderTypePlugin plugin = this.findTypePlugin(nodeTemplate);
		if (plugin == null) {
			ScalingPlanBuilder.LOG.debug("Handling NodeTemplate {} with ProvisioningChain", nodeTemplate.getId());
			ProvisioningChain chain = BPELScopeBuilder.createProvisioningChain(nodeTemplate);
			if (chain == null) {
				ScalingPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}", nodeTemplate.getId());
			} else {
				ScalingPlanBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}", nodeTemplate.getId());
				chain.executeIAProvisioning(context);
				chain.executeDAProvisioning(context);
				chain.executeOperationProvisioning(context, this.opNames);
			}
		} else {
			ScalingPlanBuilder.LOG.info("Handling NodeTemplate {} with generic plugin", nodeTemplate.getId());
			plugin.handle(context);
		}
		
		for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
			if (postPhasePlugin.canHandle(nodeTemplate)) {
				postPhasePlugin.handle(context, nodeTemplate);
			}
		}
	}
	
	private List<ScalingPlanDefinition> fetchScalingPlansDefinitions(AbstractTopologyTemplate topology, Map<String, String> tags) {
		List<ScalingPlanDefinition> scalingPlanDefinitions = new ArrayList<ScalingPlanDefinition>();
		
		// fetch scaling plan names
		String scalingPlanNamesRawValue = tags.get("scalingplans").trim();
		
		List<String> scalingPlanNamesRaw = this.getElementsFromCSV(scalingPlanNamesRawValue);
		
		for (String scalingPlanName : scalingPlanNamesRaw) {
			
			if (!scalingPlanName.trim().isEmpty() && tags.containsKey(scalingPlanName.trim())) {
				String scalingPlanNodesNEdgesRawValue = tags.get(scalingPlanName.trim());
				
				String[] scalingPlanNodesNEdgesRawValueSplit = scalingPlanNodesNEdgesRawValue.split(";");
				
				if (scalingPlanNodesNEdgesRawValueSplit.length != 3) {
					LOG.error("Scaling Plan Definition '" + scalingPlanName + "' couldn't be parsed properly, skipping");
					continue;
				}
				
				List<AbstractNodeTemplate> nodeTemplates = this.fetchNodeTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[0]);
				
				if (nodeTemplates == null) {
					LOG.error("Nodes of Scaling Plan Definition '" + scalingPlanName + "' couldn't be parsed properly, skipping");
					continue;
				}
				
				List<AbstractRelationshipTemplate> relationshipTemplates = this.fetchRelationshipTemplates(topology, scalingPlanNodesNEdgesRawValueSplit[1]);
				
				if (relationshipTemplates == null) {
					LOG.error("Relations of Scaling Plan Definition '" + scalingPlanName + "' couldn't be parsed properly, skipping");
					continue;
				}
				
				List<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes = this.fetchSelectionStrategy2BorderNodes(topology, scalingPlanNodesNEdgesRawValueSplit[2]);
				
				scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, topology, nodeTemplates, relationshipTemplates, selectionStrategy2BorderNodes));
			}
			
		}
		
		return scalingPlanDefinitions;
	}
	
	private List<AnnotatedAbstractNodeTemplate> fetchSelectionStrategy2BorderNodes(AbstractTopologyTemplate topologyTemplate, String selectionStrategyBorderNodesCSV) {
		
		selectionStrategyBorderNodesCSV = this.cleanCSVString(selectionStrategyBorderNodesCSV);
		
		List<String> selectionStrategyBorderNodes = this.getElementsFromCSV(selectionStrategyBorderNodesCSV);
		
		Map<String, String> selectionStrategyBorderNodesMap = this.transformSelectionStrategyListToMap(selectionStrategyBorderNodes);
		
		Map<String, AbstractNodeTemplate> selectionStrategyNodeTemplatesMap = new HashMap<String, AbstractNodeTemplate>();
		
		
		List<AnnotatedAbstractNodeTemplate> annotNodes = new ArrayList<AnnotatedAbstractNodeTemplate>();
		
		for (String selectionStrategy : selectionStrategyBorderNodesMap.keySet()) {
			AbstractNodeTemplate node = this.fetchNodeTemplate(topologyTemplate, selectionStrategyBorderNodesMap.get(selectionStrategy));
			if (node != null) {
				if(this.findAnnotNode(annotNodes, node) != null) {
					this.findAnnotNode(annotNodes, node).getAnnotations().add(selectionStrategy);
				} else {
					List<String> annot = new ArrayList<>();
					annot.add(selectionStrategy);					
					annotNodes.add(new AnnotatedAbstractNodeTemplate(node, annot));
				}				
			}
		}
		
		return annotNodes;
	}
	
	private AnnotatedAbstractNodeTemplate findAnnotNode(List<AnnotatedAbstractNodeTemplate> annotNodes, AbstractNodeTemplate node) {
		for(AnnotatedAbstractNodeTemplate annotNode : annotNodes) {
			if(annotNode.getId().equals(node.getId())) {
				return annotNode;
			}
		}
		return null;
	}
	
	private AbstractNodeTemplate fetchNodeTemplate(AbstractTopologyTemplate topologyTemplate, String nodeTemplateId) {
		for (AbstractNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
			if (nodeTemplate.getId().equals(nodeTemplateId)) {
				return nodeTemplate;
			}
		}
		return null;
	}
	
	private Map<String, String> transformSelectionStrategyListToMap(List<String> selectionStrategyBorderNodes) {
		Map<String, String> selectionStrategyBorderNodesMap = new HashMap<String, String>();
		for (String selectionStrategyBorderNode : selectionStrategyBorderNodes) {
			if (selectionStrategyBorderNode.split("\\[").length == 2 && selectionStrategyBorderNode.endsWith("]")) {
				String selectionStrategy = selectionStrategyBorderNode.split("\\[")[0];
				String borderNode = selectionStrategyBorderNode.split("\\[")[1].replace("]", "");
				selectionStrategyBorderNodesMap.put(selectionStrategy, borderNode);
			} else {
				LOG.error("Parsing Selection Strategies and border Node Templates had an error. Couldn't parse \"" + selectionStrategyBorderNode + "\" properly.");
			}
		}
		return selectionStrategyBorderNodesMap;
	}
	
	private String cleanCSVString(String commaSeperatedList) {
		while (commaSeperatedList.endsWith(";") | commaSeperatedList.endsWith(",")) {
			commaSeperatedList = commaSeperatedList.substring(0, commaSeperatedList.length() - 2);
		}
		return commaSeperatedList;
	}
	
	private List<AbstractRelationshipTemplate> fetchRelationshipTemplates(AbstractTopologyTemplate topology, String scalingPlanRelationsRawValue) {
		List<AbstractRelationshipTemplate> relationshipTemplates = new ArrayList<AbstractRelationshipTemplate>();
		
		scalingPlanRelationsRawValue = this.cleanCSVString(scalingPlanRelationsRawValue);
		
		// fetch nodeTemplateIds from raw value
		List<String> scalingPlanRelationNames = this.getElementsFromCSV(scalingPlanRelationsRawValue);
		
		for (AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
			for (String scalingNodeName : scalingPlanRelationNames) {
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
	
	private List<String> getElementsFromCSV(String csvString) {
		csvString = this.cleanCSVString(csvString);
		String[] scalingPlanRelationNamesRawSplit = csvString.split(",");
		return Arrays.asList(scalingPlanRelationNamesRawSplit);
	}
	
	private List<AbstractNodeTemplate> fetchNodeTemplates(AbstractTopologyTemplate topology, String scalingPlanNodesRawValue) {
		List<AbstractNodeTemplate> nodeTemplates = new ArrayList<AbstractNodeTemplate>();
		
		scalingPlanNodesRawValue = this.cleanCSVString(scalingPlanNodesRawValue);
		
		// fetch nodeTemplateIds from raw value
		
		List<String> scalingPlanNodeNames = this.getElementsFromCSV(scalingPlanNodesRawValue);
		
		for (String scalingNodeName : scalingPlanNodeNames) {
			AbstractNodeTemplate node = this.fetchNodeTemplate(topology, scalingNodeName);
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
	
	private AbstractServiceTemplate getServiceTemplate(AbstractDefinitions defs, QName serviceTemplateId) {
		for (AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {
			if (serviceTemplate.getTargetNamespace().equals(serviceTemplateId.getNamespaceURI()) && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
				return serviceTemplate;
			}
		}
		return null;
	}
	
}
