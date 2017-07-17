package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.TemplatePlanBuilder.ProvisioningChain;
import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.handlers.TemplateBuildPlanHandler;
import org.opentosca.planbuilder.helpers.BPELFinalizer;
import org.opentosca.planbuilder.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
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

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ScalingPlanBuilder implements IPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(ScalingPlanBuilder.class);
	
	// handler for abstract plan operations
	private BuildPlanHandler planHandler;
	
	// handler for abstract templatebuildplan operations
	private TemplateBuildPlanHandler templateHandler;
	
	// class for initializing properties inside the plan
	private PropertyVariableInitializer propertyInitializer;
	
	// adds serviceInstance Variable and instanceDataAPIUrl to Plans
	private ServiceInstanceInitializer serviceInstanceInitializer;
	
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private BPELFinalizer finalizer;
	
	private CorrelationIDInitializer idInit = new CorrelationIDInitializer();
	
	// accepted operations for provisioning
	private List<String> opNames = new ArrayList<String>();
	
	
	public ScalingPlanBuilder() {
		try {
			this.planHandler = new BuildPlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceInitializer();
		} catch (ParserConfigurationException e) {
			ScalingPlanBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		this.templateHandler = new TemplateBuildPlanHandler();
		// TODO seems ugly
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		
		this.finalizer = new BPELFinalizer();
		this.opNames.add("install");
		this.opNames.add("configure");
		this.opNames.add("start");
	}
	
	
	private class ScalingPlanDefinition {
		
		String name;
		List<AbstractNodeTemplate> nodeTemplates;
		List<AbstractRelationshipTemplate> relationshipTemplates;
		
		
		public ScalingPlanDefinition(String name, List<AbstractNodeTemplate> nodeTemplates, List<AbstractRelationshipTemplate> relationshipTemplate) {
			this.name = name;
			this.nodeTemplates = nodeTemplates;
			this.relationshipTemplates = relationshipTemplate;
		}
	}
	
	
	@Override
	public List<TOSCAPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<TOSCAPlan> plans = new ArrayList<TOSCAPlan>();
		
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			plans.addAll(this.buildScalingPlans(csarName, definitions, serviceTemplate.getQName()));
		}
		
		return plans;
	}
	
	public List<TOSCAPlan> buildScalingPlans(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		List<TOSCAPlan> scalingPlans = new ArrayList<TOSCAPlan>();
		
		AbstractServiceTemplate serviceTemplate = this.getServiceTemplate(definitions, serviceTemplateId);
		
		if (serviceTemplate == null) {
			return null;
		}
		
		// check if the given serviceTemplate has the scaling plans defined as
		// tags
		
		Map<String, String> tags = serviceTemplate.getTags();
		
		if (!tags.containsKey("scalingplans")) {
			return null;
		}
		
		List<ScalingPlanDefinition> scalingPlanDefinitions = this.fetchScalingPlansDefinitions(serviceTemplate.getTopologyTemplate(), tags);
		
		// check whether the defined scaling plans can be generated
		/*
		 * 1.Case: Scaling Plan = Sub-Graph of Topology without edges beeing
		 * connected to TopoNodes\ScalingPlanNodes => BuildPlan for
		 * ScalingPlanNode with service instance set
		 * 
		 * 2.Case: Scaling Plan = Subgraph of Topology with edges that are
		 * connected to node of the original topology (nodes which aren't in the
		 * subgraph) => build plan for subgraph with set service instance and
		 * selection of node instances
		 */
		for (ScalingPlanDefinition scalingPlanDefinition : scalingPlanDefinitions) {
			if (this.isConnectedToComplement(scalingPlanDefinition) & !this.equalsTopology(serviceTemplate.getTopologyTemplate(), scalingPlanDefinition)) {
				// case 2
				scalingPlans.add(this.createBuildPlanWithServiceInstanceAndNodeInstance(csarName, definitions, serviceTemplate, scalingPlanDefinition));
			} else {
				// case 1
				scalingPlans.add(this.createBuildPlanWithServiceInstance(csarName, definitions, serviceTemplate, scalingPlanDefinition));
			}
			
		}
		
		return scalingPlans;
	}
	
	private boolean equalsTopology(AbstractTopologyTemplate topology, ScalingPlanDefinition scalingPlanDefinition) {
		if (scalingPlanDefinition.nodeTemplates.containsAll(topology.getNodeTemplates()) & scalingPlanDefinition.relationshipTemplates.containsAll(topology.getRelationshipTemplates())) {
			return true;
		}
		
		return false;
	}
	
	private TOSCAPlan createBuildPlanWithServiceInstanceAndNodeInstance(String csarName, AbstractDefinitions definitions, AbstractServiceTemplate serviceTemplate, ScalingPlanDefinition scalingPlanDefinition) {
		String processName = serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name;
		String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";
		TOSCAPlan newScalingPlan = this.planHandler.createPlan(serviceTemplate, processName, processNamespace, 1);
		
		newScalingPlan.setDefinitions(definitions);
		newScalingPlan.setCsarName(csarName);
		
		// find nodeTemplates whose nodeinstances must be found for the edges
		// which are crossing the subgraph
		// boundary
		Map<AbstractRelationshipTemplate, List<AbstractNodeTemplate>> crossingRelations2NodesMap = new HashMap<AbstractRelationshipTemplate, List<AbstractNodeTemplate>>();
		
		for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplates) {
			TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(relationshipTemplate, newScalingPlan);
			newTemplate.setRelationshipTemplate(relationshipTemplate);
			newScalingPlan.addTemplateBuildPlan(newTemplate);
			
			// check if relation is crossing the subgraph that is intended to
			// scale
			if (!scalingPlanDefinition.nodeTemplates.contains(relationshipTemplate.getSource())) {
				if (crossingRelations2NodesMap.containsKey(relationshipTemplate)) {
					List<AbstractNodeTemplate> nodeTemplates = crossingRelations2NodesMap.get(relationshipTemplate);
					nodeTemplates.add(relationshipTemplate.getSource());
					crossingRelations2NodesMap.put(relationshipTemplate, nodeTemplates);
				} else {
					List<AbstractNodeTemplate> nodeTemplates = new ArrayList<AbstractNodeTemplate>();
					nodeTemplates.add(relationshipTemplate.getSource());
					crossingRelations2NodesMap.put(relationshipTemplate, nodeTemplates);
				}
			}
			if (!scalingPlanDefinition.nodeTemplates.contains(relationshipTemplate.getTarget())) {
				if (crossingRelations2NodesMap.containsKey(relationshipTemplate)) {
					List<AbstractNodeTemplate> nodeTemplates = crossingRelations2NodesMap.get(relationshipTemplate);
					nodeTemplates.add(relationshipTemplate.getTarget());
					crossingRelations2NodesMap.put(relationshipTemplate, nodeTemplates);
				} else {
					List<AbstractNodeTemplate> nodeTemplates = new ArrayList<AbstractNodeTemplate>();
					nodeTemplates.add(relationshipTemplate.getTarget());
					crossingRelations2NodesMap.put(relationshipTemplate, nodeTemplates);
				}
			}
		}
		
		for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplates) {
			TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate, newScalingPlan);
			newTemplate.setNodeTemplate(nodeTemplate);
			newScalingPlan.addTemplateBuildPlan(newTemplate);
		}
		
		// create unique set of source and target nodes referenced by the
		// crossing relations
		Set<AbstractNodeTemplate> complementNodes = new HashSet<AbstractNodeTemplate>();
		for (List<AbstractNodeTemplate> nodeTemplates : crossingRelations2NodesMap.values()) {
			for (AbstractNodeTemplate nodeTemplate : nodeTemplates) {
				complementNodes.add(nodeTemplate);
			}
		}
		
		for (AbstractNodeTemplate nodeTemplate : complementNodes) {
			TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate, newScalingPlan);
			newTemplate.setNodeTemplate(nodeTemplate);
			newScalingPlan.addTemplateBuildPlan(newTemplate);
		}
		
		// we can now execute the basic high-level skeleton generation algorithm
		// for build plans
		this.initializeDependenciesInBuildPlan(newScalingPlan);
		
		// init variables
		PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newScalingPlan);
		
		// instanceDataAPI handling is done solely trough this extension
		this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, newScalingPlan);
		
		// initialize instanceData handling, add
		// instanceDataAPI/serviceInstanceID into input, add global
		// variables to hold the value for plugins
		// this.serviceInstanceInitializer.initializeCompleteInstanceDataFromInput(newScalingPlan);
		
		this.runPlugins(newScalingPlan, serviceTemplate.getQName(), propMap, complementNodes);
		
		this.idInit.addCorrellationID(newScalingPlan);
		
		this.finalizer.finalize(newScalingPlan);
		
		return newScalingPlan;
	}
	
	private TOSCAPlan createBuildPlanWithServiceInstance(String csarName, AbstractDefinitions definitions, AbstractServiceTemplate serviceTemplate, ScalingPlanDefinition scalingPlanDefinition) {
		String processName = serviceTemplate.getId() + "_scalingPlan_" + scalingPlanDefinition.name;
		String processNamespace = serviceTemplate.getTargetNamespace() + "_scalingPlan";
		TOSCAPlan newScalingPlan = this.planHandler.createPlan(serviceTemplate, processName, processNamespace, 1);
		
		newScalingPlan.setDefinitions(definitions);
		newScalingPlan.setCsarName(csarName);
		
		for (AbstractNodeTemplate nodeTemplate : scalingPlanDefinition.nodeTemplates) {
			TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate, newScalingPlan);
			newTemplate.setNodeTemplate(nodeTemplate);
			newScalingPlan.addTemplateBuildPlan(newTemplate);
		}
		
		for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplates) {
			TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(relationshipTemplate, newScalingPlan);
			newTemplate.setRelationshipTemplate(relationshipTemplate);
			newScalingPlan.addTemplateBuildPlan(newTemplate);
		}
		
		// we can now execute the basic high-level skeleton generation algorithm
		// for build plans
		this.initializeDependenciesInBuildPlan(newScalingPlan);
		
		// init variables
		PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newScalingPlan);
		
		// instanceDataAPI handling is done solely trough this extension
		this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, newScalingPlan);
		
		// initialize instanceData handling, add
		// instanceDataAPI/serviceInstanceID into input, add global
		// variables to hold the value for plugins
		// this.serviceInstanceInitializer.initializeCompleteInstanceDataFromInput(newScalingPlan);
		
		this.runPlugins(newScalingPlan, serviceTemplate.getQName(), propMap, new HashSet<AbstractNodeTemplate>());
		
		
		
		this.idInit.addCorrellationID(newScalingPlan);
		
		this.finalizer.finalize(newScalingPlan);
		
		return newScalingPlan;
	}
	
	/**
	 * <p>
	 * This method assigns plugins to the already initialized BuildPlan and its
	 * TemplateBuildPlans. First there will be checked if any generic plugin can
	 * handle a template of the TopologyTemplate
	 * </p>
	 *
	 * @param buildPlan a BuildPlan which is alread initialized
	 * @param serviceTemplateName the name of the ServiceTemplate the BuildPlan
	 *            belongs to
	 * @param map a PropertyMap which contains mappings from Template to
	 *            Property and to variable name of inside the BuidlPlan
	 */
	private void runPlugins(TOSCAPlan buildPlan, QName serviceTemplateId, PropertyMap map, Set<AbstractNodeTemplate> complementNodes) {
		
		for (TemplateBuildPlan templatePlan : buildPlan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null) {
				
				// if not a complement node (aka no member of the scaling group)
				// we handle this node for instantiation
				if (!complementNodes.contains(templatePlan.getNodeTemplate())) {
					// handling nodetemplate
					AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
					ScalingPlanBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
					TemplatePlanContext context = new TemplatePlanContext(templatePlan, map, serviceTemplateId);
					// check if we have a generic plugin to handle the template
					// Note: if a generic plugin fails during execution the
					// TemplateBuildPlan is broken!
					IPlanBuilderTypePlugin plugin = this.canGenericPluginHandle(nodeTemplate);
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
				
			} else {
				// handling relationshiptemplate
				AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
				TemplatePlanContext context = new TemplatePlanContext(templatePlan, map, serviceTemplateId);
				
				// check if we have a generic plugin to handle the template
				// Note: if a generic plugin fails during execution the
				// TemplateBuildPlan is broken here!
				// TODO implement fallback
				if (!this.canGenericPluginHandle(relationshipTemplate)) {
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
					this.handleWithGenericPlugin(context, relationshipTemplate);
				}
				
				for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
					if (postPhasePlugin.canHandle(relationshipTemplate)) {
						postPhasePlugin.handle(context, relationshipTemplate);
					}
				}
			}
		}
		
	}
	
	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * NodeTemplate
	 * </p>
	 *
	 * @param nodeTemplate an AbstractNodeTemplate denoting a NodeTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         NodeTemplate, else false
	 */
	private IPlanBuilderTypePlugin canGenericPluginHandle(AbstractNodeTemplate nodeTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			ScalingPlanBuilder.LOG.debug("Checking whether Generic Plugin " + plugin.getID() + " can handle NodeTemplate " + nodeTemplate.getId());
			if (plugin.canHandle(nodeTemplate)) {
				ScalingPlanBuilder.LOG.info("Found GenericPlugin {} that can handle NodeTemplate {}", plugin.getID(), nodeTemplate.getId());
				return plugin;
			}
		}
		return null;
	}
	
	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * RelationshipTemplate
	 * </p>
	 *
	 * @param relationshipTemplate an AbstractRelationshipTemplate denoting a
	 *            RelationshipTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         RelationshipTemplate, else false
	 */
	private boolean canGenericPluginHandle(AbstractRelationshipTemplate relationshipTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			if (plugin.canHandle(relationshipTemplate)) {
				ScalingPlanBuilder.LOG.info("Found GenericPlugin {} thath can handle RelationshipTemplate {}", plugin.getID(), relationshipTemplate.getId());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>
	 * Takes the first occurence of a generic plugin which can handle the given
	 * RelationshipTemplate
	 * </p>
	 *
	 * @param context a TemplatePlanContext which was initialized for the given
	 *            RelationshipTemplate
	 * @param nodeTemplate a RelationshipTemplate as an
	 *            AbstractRelationshipTemplate
	 * @return returns true if there was a generic plugin which could handle the
	 *         given RelationshipTemplate and execution was successful, else
	 *         false
	 */
	private boolean handleWithGenericPlugin(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			if (plugin.canHandle(relationshipTemplate)) {
				ScalingPlanBuilder.LOG.info("Handling relationshipTemplate {} with generic plugin {}", relationshipTemplate.getId(), plugin.getID());
				return plugin.handle(context);
			}
		}
		return false;
	}
	
	private boolean isConnectedToComplement(ScalingPlanDefinition scalingPlanDefinition) {
		
		// if just one edge exists whose source or target is connected to a node
		// which isn't in node set of scaling plan => scaling plan is connected
		// to complement = case 2
		for (AbstractRelationshipTemplate relationshipTemplate : scalingPlanDefinition.relationshipTemplates) {
			if (!scalingPlanDefinition.nodeTemplates.contains(relationshipTemplate.getSource()) | scalingPlanDefinition.nodeTemplates.contains(relationshipTemplate.getTarget())) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<ScalingPlanDefinition> fetchScalingPlansDefinitions(AbstractTopologyTemplate topology, Map<String, String> tags) {
		List<ScalingPlanDefinition> scalingPlanDefinitions = new ArrayList<ScalingPlanDefinition>();
		
		// fetch scaling plan names
		String scalingPlanNamesRawValue = tags.get("scalingplans").trim();
		
		String[] scalingPlanNamesRaw = scalingPlanNamesRawValue.split(",");
		
		for (String scalingPlanName : scalingPlanNamesRaw) {
			
			if (!scalingPlanName.trim().isEmpty() && tags.containsKey(scalingPlanName.trim())) {
				String scalingPlanNodesNEdgesRawValue = tags.get(scalingPlanName.trim());
				
				String[] scalingPlanNodesNEdgesRawValueSplit = scalingPlanNodesNEdgesRawValue.split(";");
				
				if (scalingPlanNodesNEdgesRawValueSplit.length != 2) {
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
				
				scalingPlanDefinitions.add(new ScalingPlanDefinition(scalingPlanName, nodeTemplates, relationshipTemplates));
			}
			
		}
		
		return scalingPlanDefinitions;
	}
	
	private List<AbstractRelationshipTemplate> fetchRelationshipTemplates(AbstractTopologyTemplate topology, String scalingPlanRelationsRawValue) {
		List<AbstractRelationshipTemplate> relationshipTemplates = new ArrayList<AbstractRelationshipTemplate>();
		
		// remove trailing comma and semicolon
		while (scalingPlanRelationsRawValue.endsWith(";") | scalingPlanRelationsRawValue.endsWith(",")) {
			scalingPlanRelationsRawValue = scalingPlanRelationsRawValue.substring(0, scalingPlanRelationsRawValue.length() - 2);
		}
		
		// fetch nodeTemplateIds from raw value
		String[] scalingPlanRelationNamesRawSplit = scalingPlanRelationsRawValue.split(",");
		
		for (AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
			for (String scalingNodeName : scalingPlanRelationNamesRawSplit) {
				if (relationshipTemplate.getId().equals(scalingNodeName.trim())) {
					relationshipTemplates.add(relationshipTemplate);
				}
			}
		}
		
		if (relationshipTemplates.size() != scalingPlanRelationNamesRawSplit.length) {
			return null;
		} else {
			return relationshipTemplates;
		}
	}
	
	private List<AbstractNodeTemplate> fetchNodeTemplates(AbstractTopologyTemplate topology, String scalingPlanNodesRawValue) {
		List<AbstractNodeTemplate> nodeTemplates = new ArrayList<AbstractNodeTemplate>();
		
		// remove trailing comma and semicolon
		while (scalingPlanNodesRawValue.endsWith(";") | scalingPlanNodesRawValue.endsWith(",")) {
			scalingPlanNodesRawValue = scalingPlanNodesRawValue.substring(0, scalingPlanNodesRawValue.length() - 2);
		}
		
		// fetch nodeTemplateIds from raw value
		String[] scalingPlanNodeNamesRawSplit = scalingPlanNodesRawValue.split(",");
		
		for (AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
			for (String scalingNodeName : scalingPlanNodeNamesRawSplit) {
				if (nodeTemplate.getId().equals(scalingNodeName.trim())) {
					nodeTemplates.add(nodeTemplate);
				}
			}
		}
		
		if (nodeTemplates.size() != scalingPlanNodeNamesRawSplit.length) {
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
	
	/**
	 * <p>
	 * Initilizes the TemplateBuildPlans inside a BuildPlan according to the
	 * GenerateBuildPlanSkeleton algorithm in <a href=
	 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
	 * >Konzept und Implementierung eine Java-Komponente zur Generierung von
	 * WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>
	 * </p>
	 *
	 * @param buildPlan a BuildPlan where all TemplateBuildPlans are set for
	 *            each template inside TopologyTemplate the BuildPlan should
	 *            provision
	 */
	private void initializeDependenciesInBuildPlan(TOSCAPlan buildPlan) {
		for (TemplateBuildPlan relationshipPlan : this.planHandler.getRelationshipTemplatePlans(buildPlan)) {
			// determine base type of relationshiptemplate
			QName baseType = Utils.getRelationshipBaseType(relationshipPlan.getRelationshipTemplate());
			
			// determine source and target of relationshiptemplate AND REVERSE
			// the edge !
			TemplateBuildPlan target = this.planHandler.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getSource().getId(), buildPlan);
			TemplateBuildPlan source = this.planHandler.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getTarget().getId(), buildPlan);
			
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
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationlinkName);
				
				// third: generate global link for the target to relation
				// dependency
				String targetToRelationlinkName = target.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(targetToRelationlinkName, buildPlan);
				
				// fourth: connect target with relationship as target
				ScalingPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(target, relationshipPlan, targetToRelationlinkName);
				
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				
				// with the other relations we have to build first the source,
				// then the relation and at last the target
				
				// first: generate global link for the source to relation
				// dependeny
				String sourceToRelationLinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationLinkName, buildPlan);
				
				// second: connect source to relation
				ScalingPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationLinkName);
				
				// third: generate global link for the relation to target
				// dependency
				String relationToTargetLinkName = relationshipPlan.getRelationshipTemplate().getId() + "_BEFORE_" + target.getNodeTemplate().getId();
				this.planHandler.addLink(relationToTargetLinkName, buildPlan);
				
				// fourth: connect relation to target
				ScalingPlanBuilder.LOG.debug("Connecting RelationshipTemplate {} -> NodeTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, relationToTargetLinkName);
			}
			
		}
	}
	
	@Override
	public TOSCAPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		throw new RuntimeException("A service Template can have multiple scaling plans, this method is not supported");
	}
	
}
