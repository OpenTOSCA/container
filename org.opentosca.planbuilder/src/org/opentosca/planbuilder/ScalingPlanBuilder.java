package org.opentosca.planbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.TemplatePlanBuilder.ProvisioningChain;
import org.opentosca.planbuilder.fragments.Fragments;
import org.opentosca.planbuilder.handlers.PlanHandler;
import org.opentosca.planbuilder.handlers.ScopeHandler;
import org.opentosca.planbuilder.helpers.BPELFinalizer;
import org.opentosca.planbuilder.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
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
public class ScalingPlanBuilder extends IPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(ScalingPlanBuilder.class);
	
	// handler for abstract templatebuildplan operations
	private ScopeHandler scopeHandler;
	
	// class for initializing properties inside the plan
	private PropertyVariableInitializer propertyInitializer;
	
	// adds serviceInstance Variable and instanceDataAPIUrl to Plans
	private ServiceInstanceInitializer serviceInstanceInitializer;
	
	private NodeInstanceInitializer nodeInstanceInitializer;
	
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private BPELFinalizer finalizer;
	
	private CorrelationIDInitializer idInit = new CorrelationIDInitializer();
	
	// accepted operations for provisioning
	private List<String> opNames = new ArrayList<String>();
	
	
	public ScalingPlanBuilder() {
		try {
			this.planHandler = new PlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceInitializer();
		} catch (ParserConfigurationException e) {
			ScalingPlanBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		this.scopeHandler = new ScopeHandler();
		// TODO seems ugly
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		
		this.finalizer = new BPELFinalizer();
		this.opNames.add("install");
		this.opNames.add("configure");
		this.opNames.add("start");
	}
	
	
	private class ScalingPlanDefinition {
		
		// topology
		String name;
		AbstractTopologyTemplate topology;
		
		// region
		List<AbstractNodeTemplate> nodeTemplates;
		List<AbstractRelationshipTemplate> relationshipTemplates;
		
		// nodes with selection strategies
		Map<String, AbstractNodeTemplate> selectionStrategy2BorderNodes;
		
		// recursive selections
		List<AbstractNodeTemplate> nodeTemplatesRecursiveSelection;
		List<AbstractRelationshipTemplate> relationshipTemplatesRecursiveSelection;
		
		// border crossing relations
		Set<AbstractRelationshipTemplate> borderCrossingRelations;
		
		
		public ScalingPlanDefinition(String name, AbstractTopologyTemplate topology, List<AbstractNodeTemplate> nodeTemplates, List<AbstractRelationshipTemplate> relationshipTemplate, Map<String, AbstractNodeTemplate> selectionStrategy2BorderNodes) {
			this.name = name;
			this.topology = topology;
			this.nodeTemplates = nodeTemplates;
			this.relationshipTemplates = relationshipTemplate;
			this.selectionStrategy2BorderNodes = selectionStrategy2BorderNodes;
			
			this.nodeTemplatesRecursiveSelection = new ArrayList<AbstractNodeTemplate>();
			this.relationshipTemplatesRecursiveSelection = new ArrayList<AbstractRelationshipTemplate>();
			
			this.init();
			
			this.borderCrossingRelations = this.calculateBorderCrossingRelations();
		}
		
		private void init() {
			
			this.isValid();
			
			// calculate recursive nodes
			for (AbstractNodeTemplate nodeTemplate : selectionStrategy2BorderNodes.values()) {
				List<AbstractNodeTemplate> sinkNodes = new ArrayList<AbstractNodeTemplate>();
				
				Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_HOSTEDON, sinkNodes);
				Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_DEPENDSON, sinkNodes);
				Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_DEPLOYEDON, sinkNodes);
				
				List<AbstractRelationshipTemplate> outgoing = Utils.getOutgoingRelations(nodeTemplate, Utils.TOSCABASETYPE_HOSTEDON, Utils.TOSCABASETYPE_DEPENDSON, Utils.TOSCABASETYPE_DEPLOYEDON);
				
				this.nodeTemplatesRecursiveSelection.addAll(sinkNodes);
				this.relationshipTemplatesRecursiveSelection.addAll(outgoing);
			}
		}
		
		private Set<AbstractRelationshipTemplate> calculateBorderCrossingRelations() {
			Set<AbstractRelationshipTemplate> borderCrossingRelations = new HashSet<AbstractRelationshipTemplate>();
			
			for (AbstractRelationshipTemplate relationshipTemplate : this.relationshipTemplates) {
				AbstractNodeTemplate nodeStratSelection = this.crossesBorder(relationshipTemplate, nodeTemplates);
				if (nodeStratSelection != null && this.selectionStrategy2BorderNodes.values().contains(nodeStratSelection)) {
					borderCrossingRelations.add(relationshipTemplate);
				}
			}
			
			for (AbstractNodeTemplate nodeTemplate : this.nodeTemplates) {
				List<AbstractRelationshipTemplate> relations = this.getBorderCrossingRelations(nodeTemplate, nodeTemplates);
				borderCrossingRelations.addAll(relations);
			}
			return borderCrossingRelations;
		}
		
		private boolean isValid() {
			// check if all nodes at the border are attached with a selection
			// strategy
			/* calculate all border crossing relations */
			Set<AbstractRelationshipTemplate> borderCrossingRelations = this.calculateBorderCrossingRelations();
			
			for (AbstractRelationshipTemplate relation : borderCrossingRelations) {
				AbstractNodeTemplate nodeStratSelection = this.crossesBorder(relation, nodeTemplates);
				if (nodeStratSelection == null) {
					// these edges MUST be connected to a strategically selected
					// node
					return false;
				}
				
				if (!this.selectionStrategy2BorderNodes.values().contains(nodeStratSelection)) {
					return false;
				}
			}
			
			return true;
		}
		
		private List<AbstractRelationshipTemplate> getBorderCrossingRelations(AbstractNodeTemplate nodeTemplate, List<AbstractNodeTemplate> nodesToScale) {
			List<AbstractRelationshipTemplate> borderCrossingRelations = new ArrayList<AbstractRelationshipTemplate>();
			
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (this.crossesBorder(relation, nodesToScale) != null) {
					borderCrossingRelations.add(relation);
				}
			}
			
			for (AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
				if (this.crossesBorder(relation, nodesToScale) != null) {
					borderCrossingRelations.add(relation);
				}
			}
			
			return borderCrossingRelations;
		}
		
		private AbstractNodeTemplate crossesBorder(AbstractRelationshipTemplate relationship, List<AbstractNodeTemplate> nodesToScale) {
			
			AbstractNodeTemplate source = relationship.getSource();
			AbstractNodeTemplate target = relationship.getTarget();
			
			QName baseType = Utils.getRelationshipBaseType(relationship);
			
			if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				// if either the source or target is not in the nodesToScale
				// list =>
				// relation crosses border
				if (!nodesToScale.contains(source)) {
					return source;
				} else if (!nodesToScale.contains(target)) {
					return target;
				}
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				// if target is not in the nodesToScale list => relation crosses
				// border
				if (!nodesToScale.contains(target)) {
					return target;
				}
				
			}
			
			return null;
		}
	}
	
	
	@Override
	public BPELPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		throw new RuntimeException("A service Template can have multiple scaling plans, this method is not supported");
	}
	
	@Override
	public List<BPELPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<BPELPlan> plans = new ArrayList<BPELPlan>();
		
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
			
			
			AbstractPlan abstractScaleOutPlan =this.generateSOG(new QName(processNamespace,processName).toString(), definitions, serviceTemplate, scalingPlanDefinition);
			
			
			BPELPlan scalingPlan = this.planHandler.createBPELPlan(processNamespace, processName, abstractScaleOutPlan);
			
			this.addNodeAndRelationScopes(scalingPlan, scalingPlanDefinition);
			
			this.initializeScaleOrderGraph(scalingPlan, scalingPlanDefinition);
			
			PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(scalingPlan);
			
			// instanceDataAPI handling is done solely trough this extension
			this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, scalingPlan);
			
			this.serviceInstanceInitializer.initializeInstanceDataAPIandServiceInstanceIDFromInput(scalingPlan);
			
			this.nodeInstanceInitializer.addNodeInstanceIDVarToTemplatePlans(scalingPlan);
			
			this.idInit.addCorrellationID(scalingPlan);
			
			this.runProvisioningLogicGeneration(scalingPlan, propMap, scalingPlanDefinition.nodeTemplates, scalingPlanDefinition.relationshipTemplates);
			
			// add generic instance selection
			
			for(AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
				this.addRecursiveInstanceSelection(scalingPlan, propMap, relationshipTemplate);
			}			
			for(AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
				this.addRecursiveInstanceSelection(scalingPlan, propMap, nodeTemplate);
			}
			
			// TODO add plugin system
			
		}
		
		return scalingPlans;
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
			Node fetchRelationInstanceData = new Fragments().createRESTDeleteOnURLBPELVarAsNode(relationInstanceVarName, responseVarName);
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
			Node queryNodeInstanceUrl = new Fragments().createAssignXpathQueryToStringVarFragmentAsNode("recursiveSelection_fetchNodeInstance" + System.currentTimeMillis(), xpathQuery, nodeInstanceVarName);
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
			Node requestRelationInstance = new Fragments().createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(serviceInstanceIdVarName, relationshipTemplate.getId(), responseVarName, nodeTemplateInstanceVarName);
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
			new Fragments().createAssignXpathQueryToStringVarFragmentAsNode("recursiveSelection_fetchRelationInstance" + System.currentTimeMillis(), xpathQuery, relationshipTemplateInstanceVarName);
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
			ProvisioningChain sourceChain = TemplatePlanBuilder.createProvisioningChain(relationshipTemplate, true);
			ProvisioningChain targetChain = TemplatePlanBuilder.createProvisioningChain(relationshipTemplate, false);
			
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
			ProvisioningChain chain = TemplatePlanBuilder.createProvisioningChain(nodeTemplate);
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
	
	private AbstractPlan generateSOG(String id, AbstractDefinitions defintions, AbstractServiceTemplate serviceTemplate, ScalingPlanDefinition scalingPlanDefinition) {
		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		
		Map<AbstractActivity, AbstractActivity> links = new HashMap<AbstractActivity, AbstractActivity>();
		
		Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
		
		BuildPlanBuilder buildPlanBuilder = new BuildPlanBuilder();
		
		AbstractPlan abstractScaleOutPlan = buildPlanBuilder.generatePOG(id, defintions, serviceTemplate, scalingPlanDefinition.nodeTemplates, scalingPlanDefinition.relationshipTemplates);
		
		abstractScaleOutPlan.setType(org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType.MANAGE);
		
		for (AbstractNodeTemplate stratNodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes.values()) {
			AbstractActivity activity = new ANodeTemplateActivity(stratNodeTemplate.getId() + "_strategicselection_activity", "STRATEGICSELECTION", stratNodeTemplate) {
			};
			abstractScaleOutPlan.getActivites().add(activity);
			mapping.put(stratNodeTemplate, activity);
			
			// TODO here we create recursive selection and connect everything
			Collection<List<AbstractRelationshipTemplate>> paths = new HashSet<List<AbstractRelationshipTemplate>>();
			
			this.findPaths(paths, stratNodeTemplate);
			
			for (List<AbstractRelationshipTemplate> path : paths) {
				for (AbstractRelationshipTemplate relationshipTemplate : path) {
					AbstractActivity recursiveRelationActivity = new ARelationshipTemplateActivity(relationshipTemplate.getId() + "recursiveselection_activity", "RECURSIVESELECTION", relationshipTemplate) {
					};
					AbstractActivity recursiveTargetNodeActivity = new ANodeTemplateActivity(relationshipTemplate.getTarget().getId() + "_recursiveselection_activity", "RECURSIVESELECTION", relationshipTemplate.getTarget());
					AbstractActivity recursiveSourceNodeActivity = new ANodeTemplateActivity(relationshipTemplate.getSource().getId() + "_recursiveselection_activity", "RECURSIVESELECTION", relationshipTemplate.getSource());
					
					abstractScaleOutPlan.getActivites().add(recursiveRelationActivity);
					abstractScaleOutPlan.getActivites().add(recursiveSourceNodeActivity);
					abstractScaleOutPlan.getActivites().add(recursiveTargetNodeActivity);
					
					links.put(recursiveRelationActivity, recursiveTargetNodeActivity);
					links.put(recursiveSourceNodeActivity, recursiveRelationActivity);
				}
				
				for (AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
					if (relationshipTemplate.getSource().equals(stratNodeTemplate) | relationshipTemplate.getTarget().equals(stratNodeTemplate)) {
						
						AbstractActivity provRelationActivity = this.findRelationshipTemplateActivity(new ArrayList<AbstractActivity>(abstractScaleOutPlan.getActivites()), relationshipTemplate, "PROVISIONING");
						if (provRelationActivity == null) {
							provRelationActivity = new ARelationshipTemplateActivity(relationshipTemplate + "provisioning_acvtivity", "PROVISIONING", relationshipTemplate);
						}
						
						AbstractActivity recursiveRelationActivity = this.findRelationshipTemplateActivity(new ArrayList<AbstractActivity>(abstractScaleOutPlan.getActivites()), path.get(path.size() - 1), "RECURSIVESELECTION");
						
						links.put(recursiveRelationActivity, provRelationActivity);
					}
				}
			}
		}
		
		return abstractScaleOutPlan;
	}
	
	private AbstractActivity findRelationshipTemplateActivity(List<AbstractActivity> activities, AbstractRelationshipTemplate relationshipTemplate, String type) {
		for (AbstractActivity activity : activities) {
			if (activity.getType().equals(type)) {
				if (activity instanceof ARelationshipTemplateActivity) {
					if (((ARelationshipTemplateActivity) activity).getRelationshipTemplate().equals(relationshipTemplate)) {
						return activity;
					}
				}
			}
		}
		return null;
	}
	
	private void findPaths(Collection<List<AbstractRelationshipTemplate>> paths, AbstractNodeTemplate nodeTemplate) {
		List<AbstractRelationshipTemplate> infrastructureEdges = new ArrayList<AbstractRelationshipTemplate>();
		Utils.getInfrastructureEdges(nodeTemplate, infrastructureEdges);
		
		for (AbstractRelationshipTemplate infrastructureEdge : infrastructureEdges) {
			List<AbstractRelationshipTemplate> pathToAdd = null;
			for (Iterator<List<AbstractRelationshipTemplate>> iter = paths.iterator(); iter.hasNext();) {
				List<AbstractRelationshipTemplate> path = iter.next();
				if (path.get(path.size() - 1).getTarget().equals(infrastructureEdge.getSource())) {
					pathToAdd = path;
					break;
				}
			}
			
			if (pathToAdd == null) {
				// we didn't find a path where this infrastructureEdge is
				// connected to => create a new path
				pathToAdd = new ArrayList<AbstractRelationshipTemplate>();
				paths.add(pathToAdd);
			}
			
			pathToAdd.add(infrastructureEdge);
			this.findPaths(paths, infrastructureEdge.getTarget());
		}
		
	}
	
	private void initializeScaleOrderGraph(BPELPlan plan, ScalingPlanDefinition scalingPlanDefinition) {
		// connect nodes and relation scopes that are going to provision a new
		// instance, except the connections to border nodes
		for (AbstractRelationshipTemplate relation : scalingPlanDefinition.relationshipTemplates) {
			if (this.connectedToRegionOnly(relation, scalingPlanDefinition)) {
				BPELScopeActivity relationPlan = this.planHandler.getTemplateBuildPlanById(relation.getId(), plan);
				BPELScopeActivity sourcePlan = this.planHandler.getTemplateBuildPlanById(relation.getSource().getId(), plan);
				BPELScopeActivity targetPlan = this.planHandler.getTemplateBuildPlanById(relation.getTarget().getId(), plan);
				
				this.createProvisioningConnection(plan, relationPlan, sourcePlan, targetPlan);
			}
		}
		
		// create instance selection order for each bordering node and store the
		// last recursively selected Node
		for (AbstractNodeTemplate strategicallySelectedNode : scalingPlanDefinition.selectionStrategy2BorderNodes.values()) {
			List<BPELScopeActivity> recursiveSelectionScopes = this.connectInstanceSelectionPaths(plan, strategicallySelectedNode, scalingPlanDefinition);
			
			// connect these scopes to the edge connecting region and strat
			// selected node
			for (AbstractRelationshipTemplate borderCrossingRelation : scalingPlanDefinition.borderCrossingRelations) {
				if (borderCrossingRelation.getSource().equals(strategicallySelectedNode) || borderCrossingRelation.getTarget().equals(strategicallySelectedNode)) {
					for (BPELScopeActivity recursiveSelectionTemplatePlan : recursiveSelectionScopes) {
						
						BPELScopeActivity crossingRelationScope = this.planHandler.getTemplateBuildPlanById(borderCrossingRelation.getId(), plan);
						
						String linkName = strategicallySelectedNode.getId() + "_InstanceRegion2ProvisioningRegionLink_";
						
						this.scopeHandler.connect(recursiveSelectionTemplatePlan, crossingRelationScope, linkName);
						
					}
				}
			}
			
		}
		
	}
	
	private List<BPELScopeActivity> connectInstanceSelectionPaths(BPELPlan plan, AbstractNodeTemplate strategicallySelectedNode, ScalingPlanDefinition scalingPlanDefinition) {
		List<BPELScopeActivity> templateBuildPlans = new ArrayList<BPELScopeActivity>();
		AbstractNodeTemplate currentNode = strategicallySelectedNode;
		BPELScopeActivity currentScope = this.planHandler.getTemplateBuildPlanById(strategicallySelectedNode.getId(), plan);
		
		if (currentNode.getOutgoingRelations().isEmpty()) {
			templateBuildPlans.add(currentScope);
			return templateBuildPlans;
		}
		
		for (AbstractRelationshipTemplate relation : currentNode.getOutgoingRelations()) {
			if (scalingPlanDefinition.relationshipTemplatesRecursiveSelection.contains(relation)) {
				String linkNameSourceToRel = "instanceSelectionLink_" + currentNode.getId() + "_" + relation.getId();
				String linkNameRelToTarget = "instanceSelectionLink_" + relation.getId() + "_" + relation.getTarget().getId();
				
				// connect currentNode with Rel
				this.scopeHandler.connect(currentScope, this.planHandler.getTemplateBuildPlanById(relation.getId(), plan), linkNameSourceToRel);
				this.scopeHandler.connect(this.planHandler.getTemplateBuildPlanById(relation.getId(), plan), this.planHandler.getTemplateBuildPlanById(relation.getTarget().getId(), plan), linkNameRelToTarget);
				
				templateBuildPlans.addAll(this.connectInstanceSelectionPaths(plan, relation.getTarget(), scalingPlanDefinition));
			}
		}
		
		return templateBuildPlans;
	}
	
	private void createProvisioningConnection(BPELPlan buildPlan, BPELScopeActivity relationshipPlan, BPELScopeActivity source, BPELScopeActivity target) {
		
		// determine base type of relationshiptemplate
		QName baseType = Utils.getRelationshipBaseType(relationshipPlan.getRelationshipTemplate());
		
		// determine source and target of relationshiptemplate AND REVERSE
		// the edge !
		
		// set dependencies inside buildplan (the links in the flow)
		// according to the basetype
		if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
			// with a connectsto relation we have first build the
			// nodetemplates and then the relationshiptemplate
			
			// first: generate global link for the source to relation
			// dependency
			String sourceToRelationlinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
			this.planHandler.addLink(sourceToRelationlinkName, buildPlan);
			
			// second: connect source with relationship as target
			ScalingPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
			this.scopeHandler.connect(source, relationshipPlan, sourceToRelationlinkName);
			
			// third: generate global link for the target to relation
			// dependency
			String targetToRelationlinkName = target.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
			this.planHandler.addLink(targetToRelationlinkName, buildPlan);
			
			// fourth: connect target with relationship as target
			ScalingPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
			this.scopeHandler.connect(target, relationshipPlan, targetToRelationlinkName);
			
		} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
			
			// with the other relations we have to build first the source,
			// then the relation and at last the target
			
			// first: generate global link for the source to relation
			// dependeny
			String sourceToRelationLinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
			this.planHandler.addLink(sourceToRelationLinkName, buildPlan);
			
			// second: connect source to relation
			ScalingPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
			this.scopeHandler.connect(source, relationshipPlan, sourceToRelationLinkName);
			
			// third: generate global link for the relation to target
			// dependency
			String relationToTargetLinkName = relationshipPlan.getRelationshipTemplate().getId() + "_BEFORE_" + target.getNodeTemplate().getId();
			this.planHandler.addLink(relationToTargetLinkName, buildPlan);
			
			// fourth: connect relation to target
			ScalingPlanBuilder.LOG.debug("Connecting RelationshipTemplate {} -> NodeTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
			this.scopeHandler.connect(relationshipPlan, target, relationToTargetLinkName);
		}
		
	}
	
	private boolean connectedToRegionOnly(AbstractRelationshipTemplate relation, ScalingPlanDefinition scalingPlanDefinition) {
		AbstractNodeTemplate source = relation.getSource();
		AbstractNodeTemplate target = relation.getTarget();
		
		if (scalingPlanDefinition.nodeTemplates.contains(source) & scalingPlanDefinition.nodeTemplates.contains(target)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void addNodeAndRelationScopes(BPELPlan plan, ScalingPlanDefinition scalingPlanDefinition) {
		
		// add scopes for the region
		for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplates) {
			BPELScopeActivity newTemplate = this.scopeHandler.createTemplateBuildPlan(nodeTemplate, plan);
			newTemplate.setNodeTemplate(nodeTemplate);
			plan.addTemplateBuildPlan(newTemplate);
		}
		
		for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplates) {
			BPELScopeActivity newTemplate = this.scopeHandler.createTemplateBuildPlan(relationshipTemplate, plan);
			newTemplate.setRelationshipTemplate(relationshipTemplate);
			plan.addTemplateBuildPlan(newTemplate);
		}
		
		// add scopes for each node selected strategically at runtime
		for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.selectionStrategy2BorderNodes.values()) {
			BPELScopeActivity newTemplate = this.scopeHandler.createTemplateBuildPlan(nodeTemplate, plan);
			newTemplate.setNodeTemplate(nodeTemplate);
			plan.addTemplateBuildPlan(newTemplate);
		}
		
		// add scopes for all templates selected recursively at runtime
		for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplatesRecursiveSelection) {
			BPELScopeActivity newTemplate = this.scopeHandler.createTemplateBuildPlan(nodeTemplate, plan);
			newTemplate.setNodeTemplate(nodeTemplate);
			plan.addTemplateBuildPlan(newTemplate);
		}
		
		for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplatesRecursiveSelection) {
			BPELScopeActivity newTemplate = this.scopeHandler.createTemplateBuildPlan(relationshipTemplate, plan);
			newTemplate.setRelationshipTemplate(relationshipTemplate);
			plan.addTemplateBuildPlan(newTemplate);
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
				
				Map<String, AbstractNodeTemplate> selectionStrategy2BorderNodes = this.fetchSelectionStrategy2BorderNodes(topology, scalingPlanNodesNEdgesRawValueSplit[3]);
				
				scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, topology, nodeTemplates, relationshipTemplates, selectionStrategy2BorderNodes));
			}
			
		}
		
		return scalingPlanDefinitions;
	}
	
	private Map<String, AbstractNodeTemplate> fetchSelectionStrategy2BorderNodes(AbstractTopologyTemplate topologyTemplate, String selectionStrategyBorderNodesCSV) {
		
		selectionStrategyBorderNodesCSV = this.cleanCSVString(selectionStrategyBorderNodesCSV);
		
		List<String> selectionStrategyBorderNodes = this.getElementsFromCSV(selectionStrategyBorderNodesCSV);
		
		Map<String, String> selectionStrategyBorderNodesMap = this.transformSelectionStrategyListToMap(selectionStrategyBorderNodes);
		
		Map<String, AbstractNodeTemplate> selectionStrategyNodeTemplatesMap = new HashMap<String, AbstractNodeTemplate>();
		
		for (String selectionStrategy : selectionStrategyBorderNodesMap.keySet()) {
			AbstractNodeTemplate node = this.fetchNodeTemplate(topologyTemplate, selectionStrategyBorderNodesMap.get(selectionStrategy));
			if (node != null) {
				selectionStrategyNodeTemplatesMap.put(selectionStrategy, node);
			}
		}
		
		return selectionStrategyNodeTemplatesMap;
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
			if (selectionStrategyBorderNode.split("[").length == 2 && selectionStrategyBorderNode.endsWith("]")) {
				String selectionStrategy = selectionStrategyBorderNode.split("[")[0];
				String borderNode = selectionStrategyBorderNode.split("[")[1].replace("]", "");
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
