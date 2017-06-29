package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.handlers.TemplateBuildPlanHandler;
import org.opentosca.planbuilder.helpers.BPELFinalizer;
import org.opentosca.planbuilder.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class TerminationPlanBuilder implements IPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(TerminationPlanBuilder.class);
	
	// handler for abstract buildplan operations
	private BuildPlanHandler planHandler;
	// handler for abstract templatebuildplan operations
	private final TemplateBuildPlanHandler templateHandler;
	// class for initializing properties inside the build plan
	private final PropertyVariableInitializer propertyInitializer;
	// class for initializing output with boundarydefinitions of a
	// serviceTemplate
	private final PropertyMappingsToOutputInitializer propertyOutputInitializer;
	// adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
	private ServiceInstanceInitializer serviceInstanceInitializer;
	// adds nodeInstanceIDs to each templatePlan
	private NodeInstanceInitializer nodeInstanceInitializer;
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private final BPELFinalizer finalizer;
	// accepted operations for provisioning
	private final List<String> opNames = new ArrayList<>();
	
	private final CorrelationIDInitializer idInit = new CorrelationIDInitializer();
	
	
	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	public TerminationPlanBuilder() {
		try {
			this.planHandler = new BuildPlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceInitializer();
			this.nodeInstanceInitializer = new NodeInstanceInitializer();
		} catch (final ParserConfigurationException e) {
			TerminationPlanBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		this.templateHandler = new TemplateBuildPlanHandler();
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		this.propertyOutputInitializer = new PropertyMappingsToOutputInitializer();
		this.finalizer = new BPELFinalizer();
		this.opNames.add("stop");
		this.opNames.add("uninstall");
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions,
	 * javax.xml.namespace.QName)
	 */
	@Override
	public TOSCAPlan buildPlan(final String csarName, final AbstractDefinitions definitions, final QName serviceTemplateId) {
		for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			String namespace;
			if (serviceTemplate.getTargetNamespace() != null) {
				namespace = serviceTemplate.getTargetNamespace();
			} else {
				namespace = definitions.getTargetNamespace();
			}
			
			if (namespace.equals(serviceTemplateId.getNamespaceURI()) && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
				final String processName = serviceTemplate.getId() + "_terminationPlan";
				final String processNamespace = serviceTemplate.getTargetNamespace() + "_terminationPlan";
				final TOSCAPlan newTerminationPlan = this.planHandler.createPlan(serviceTemplate, processName, processNamespace, 2);
				newTerminationPlan.setDefinitions(definitions);
				newTerminationPlan.setCsarName(csarName);
				
				// create empty templateplans for each template and add them to
				// buildplan
				for (final AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
					final TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate, newTerminationPlan);
					newTemplate.setNodeTemplate(nodeTemplate);
					newTerminationPlan.addTemplateBuildPlan(newTemplate);
				}
				
				for (final AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
					final TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(relationshipTemplate, newTerminationPlan);
					newTemplate.setRelationshipTemplate(relationshipTemplate);
					newTerminationPlan.addTemplateBuildPlan(newTemplate);
				}
				
				// connect the templates
				this.initializeConnectionsInTerminationPlan(newTerminationPlan);
				
				final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newTerminationPlan);
				
				// instanceDataAPI handling is done solely trough this extension
				this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, newTerminationPlan);
				
				// initialize instanceData handling, add
				// instanceDataAPI/serviceInstanceID into input, add global
				// variables to hold the value for plugins
				this.serviceInstanceInitializer.initializeInstanceDataAPIandServiceInstanceIDFromInput(newTerminationPlan);
				this.serviceInstanceInitializer.initPropertyVariablesFromInstanceData(newTerminationPlan, propMap);
				
				this.nodeInstanceInitializer.addNodeInstanceIDVarToTemplatePlans(newTerminationPlan);
				this.nodeInstanceInitializer.addNodeInstanceFindLogic(newTerminationPlan);
				this.nodeInstanceInitializer.addPropertyVariableUpdateBasedOnNodeInstanceID(newTerminationPlan, propMap);
				
				// TODO add null/empty check of property variables, as the
				// templatePlan should abort when the properties aren't set with
				// values
				//this.nodeInstanceInitializer.addIfNullAbortCheck(newTerminationPlan, propMap);
				
				this.runPlugins(newTerminationPlan, serviceTemplate.getQName(), propMap);
				
				// TODO we need to wrap the pre-, prov- and post-phase sequences
				// into a forEach activity that iterates over all nodeInstances
				// of a given nodeTemplate. This allows us to generate code for
				// a single nodeInstance which can then be used for all
				// nodeInstances by using the same code on each instance
				
				// add logic at the end of the process to DELETE the
				// serviceInstance with the instanceDataAPI
				
				this.serviceInstanceInitializer.appendServiceInstanceDelete(newTerminationPlan);
				
				this.idInit.addCorrellationID(newTerminationPlan);
				
				this.finalizer.finalize(newTerminationPlan);
				
				return newTerminationPlan;
			}
		}
		TerminationPlanBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}", serviceTemplateId.toString(), definitions.getId(), csarName);
		return null;
	}
	
	/**
	 * This method will execute plugins on each TemplatePlan inside the given
	 * plan for termination of each node and relation.
	 *
	 * @param plan the plan to execute the plugins on
	 * @param serviceTemplate the serviceTemplate the plan belongs to
	 * @param propMap a PropertyMapping from NodeTemplate to Properties to
	 *            BPELVariables
	 */
	private void runPlugins(final TOSCAPlan plan, final QName serviceTemplate, final PropertyMap propMap) {
		/*
		 * TODO/FIXME until we decided whether we allow type plugins that
		 * achieve termination, we just terminate each VM and Docker Container we can find
		 */
		for (final TemplateBuildPlan templatePlan : plan.getTemplateBuildPlans()) {
			// we handle only nodeTemplates..
			if (templatePlan.getNodeTemplate() != null) {
				// .. that are VM nodeTypes
				if (org.opentosca.container.core.tosca.convention.Utils.isSupportedVMNodeType(templatePlan.getNodeTemplate().getType().getId())) {
					// create context for the templatePlan
					final TemplatePlanContext context = new TemplatePlanContext(templatePlan, propMap, serviceTemplate);
					// fetch infrastructure node (cloud provider)
					final List<AbstractNodeTemplate> infraNodes = context.getInfrastructureNodes();
					for (final AbstractNodeTemplate infraNode : infraNodes) {
						if (org.opentosca.container.core.tosca.convention.Utils.isSupportedCloudProviderNodeType(infraNode.getType().getId())) {
							// append logic to call terminateVM method on the
							// node
							
							context.executeOperation(infraNode, org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER, org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM, null);
						}
					}
					
				} else {
					// check whether this node is a docker container
					final TemplatePlanContext context = new TemplatePlanContext(templatePlan, propMap, serviceTemplate);
					
					if(!this.isDockerContainer(context.getNodeTemplate())) {
						continue;
					}
					
					// fetch infrastructure node (cloud provider)
					final List<AbstractNodeTemplate> nodes = new ArrayList<>();
					Utils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes);
					
					for (final AbstractNodeTemplate node : nodes) {
						if (org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(node.getType().getId())) {
							context.executeOperation(node, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,null);
						}
					}
					
				}
				
				AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
				TerminationPlanBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
				TemplatePlanContext context = new TemplatePlanContext(templatePlan, propMap, serviceTemplate);
				
				for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
					if (postPhasePlugin.canHandle(nodeTemplate)) {
						postPhasePlugin.handle(context, nodeTemplate);
					}
				}
			}
			
			
			
		}
		
	}
	
	private boolean isDockerContainer(AbstractNodeTemplate nodeTemplate){
		if(nodeTemplate.getProperties() == null){
			return false;
		}
		Element propertyElement = nodeTemplate.getProperties().getDOMElement();
		NodeList childNodeList = propertyElement.getChildNodes();
		
		int check = 0;
		boolean foundDockerImageProp = false;
		for (int index = 0; index < childNodeList.getLength(); index++) {
			if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (childNodeList.item(index).getLocalName().equals("ContainerPort")) {
				check++;
			} else if (childNodeList.item(index).getLocalName().equals("Port")) {
				check++;
			} else if (childNodeList.item(index).getLocalName().equals("ContainerImage")) {
				foundDockerImageProp = true;
			}
		}
		
		if (check != 2) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions)
	 */
	@Override
	public List<TOSCAPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
		final List<TOSCAPlan> plans = new ArrayList<>();
		for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			QName serviceTemplateId;
			// targetNamespace attribute doesn't has to be set, so we check it
			if (serviceTemplate.getTargetNamespace() != null) {
				serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
			} else {
				serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
			}
			
			if (!serviceTemplate.hasBuildPlan()) {
				TerminationPlanBuilder.LOG.debug("ServiceTemplate {} has no TerminationPlan, generating TerminationPlan", serviceTemplateId.toString());
				final TOSCAPlan newBuildPlan = this.buildPlan(csarName, definitions, serviceTemplateId);
				
				if (newBuildPlan != null) {
					TerminationPlanBuilder.LOG.debug("Created TerminationPlan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
					plans.add(newBuildPlan);
				}
			} else {
				TerminationPlanBuilder.LOG.debug("ServiceTemplate {} has TerminationPlan, no generation needed", serviceTemplateId.toString());
			}
		}
		return plans;
	}
	
	/**
	 * <p>
	 * Initilizes the TemplateBuildPlans inside a TerminationPlan according to
	 * the GenerateBuildPlanSkeleton algorithm in <a href=
	 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
	 * >Konzept und Implementierung eine Java-Komponente zur Generierung von
	 * WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>
	 * </p>
	 *
	 * @param plan a TerminationPlan where all TemplateBuildPlans are set for
	 *            each template inside TopologyTemplate the BuildPlan should
	 *            provision
	 */
	private void initializeConnectionsInTerminationPlan(final TOSCAPlan plan) {
		for (final TemplateBuildPlan relationshipPlan : this.planHandler.getRelationshipTemplatePlans(plan)) {
			// determine base type of relationshiptemplate
			final QName baseType = Utils.getRelationshipBaseType(relationshipPlan.getRelationshipTemplate());
			
			// determine source and target of relationshiptemplate
			final TemplateBuildPlan source = this.planHandler.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getSource().getId(), plan);
			final TemplateBuildPlan target = this.planHandler.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getTarget().getId(), plan);
			
			// set dependencies inside buildplan (the links in the flow)
			// according to the basetype
			if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				// with a connectsto relation we have to first terminate the
				// relationshiptemplate and then the nodetemplates
				
				// first: generate global link for the source to relation
				// dependency
				final String sourceToRelationlinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationlinkName, plan);
				
				// second: connect source with relationship as target
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, source, sourceToRelationlinkName);
				
				// third: generate global link for the target to relation
				// dependency
				final String targetToRelationlinkName = target.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(targetToRelationlinkName, plan);
				
				// fourth: connect target with relationship as target
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, targetToRelationlinkName);
				
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				
				// with the other relations we have to terminate first the
				// source,
				// then the relation and at last the target
				
				// first: generate global link for the source to relation
				// dependeny
				final String sourceToRelationLinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationLinkName, plan);
				
				// second: connect source to relation
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationLinkName);
				
				// third: generate global link for the relation to target
				// dependency
				final String relationToTargetLinkName = relationshipPlan.getRelationshipTemplate().getId() + "_BEFORE_" + target.getNodeTemplate().getId();
				this.planHandler.addLink(relationToTargetLinkName, plan);
				
				// fourth: connect relation to target
				TerminationPlanBuilder.LOG.debug("Connecting RelationshipTemplate {} -> NodeTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, relationToTargetLinkName);
			}
			
		}
	}
	
}
