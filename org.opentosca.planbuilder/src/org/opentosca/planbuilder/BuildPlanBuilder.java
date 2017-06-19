package org.opentosca.planbuilder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opentosca.planbuilder.TemplatePlanBuilder.ProvisioningChain;
import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.handlers.TemplateBuildPlanHandler;
import org.opentosca.planbuilder.helpers.BPELFinalizer;
import org.opentosca.planbuilder.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.helpers.EmptyPropertyToInputInitializer;
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
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL
 * 2.0 BuildPlans fuer OpenTOSCA</a>. It is responsible for generating the Build
 * Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 * </p>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BuildPlanBuilder implements IPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(BuildPlanBuilder.class);
	
	// handler for abstract buildplan operations
	private BuildPlanHandler planHandler;
	// handler for abstract templatebuildplan operations
	private TemplateBuildPlanHandler templateHandler;
	// class for initializing properties inside the build plan
	private PropertyVariableInitializer propertyInitializer;
	// class for initializing output with boundarydefinitions of a
	// serviceTemplate
	private PropertyMappingsToOutputInitializer propertyOutputInitializer;
	// adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
	private ServiceInstanceInitializer serviceInstanceInitializer;
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private BPELFinalizer finalizer;
	// accepted operations for provisioning
	private List<String> opNames = new ArrayList<String>();
	
	private NodeInstanceInitializer instanceInit;
	
	private CorrelationIDInitializer idInit = new CorrelationIDInitializer();
	
	private EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();
	
	
	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	public BuildPlanBuilder() {
		try {
			this.planHandler = new BuildPlanHandler();
			this.serviceInstanceInitializer = new ServiceInstanceInitializer();
			this.instanceInit = new NodeInstanceInitializer();
		} catch (ParserConfigurationException e) {
			BuildPlanBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
		}
		this.templateHandler = new TemplateBuildPlanHandler();
		// TODO seems ugly
		this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
		this.propertyOutputInitializer = new PropertyMappingsToOutputInitializer();
		this.finalizer = new BPELFinalizer();
		this.opNames.add("install");
		this.opNames.add("configure");
		this.opNames.add("start");
		// this.opNames.add("connectTo");
		// this.opNames.add("hostOn");
	}
	
	/**
	 * Returns the number of the plugins registered with this planbuilder
	 *
	 * @return integer denoting the count of plugins
	 */
	public int registeredPlugins() {
		return PluginRegistry.getGenericPlugins().size() + PluginRegistry.getDaPlugins().size() + PluginRegistry.getIaPlugins().size() + PluginRegistry.getPostPlugins().size() + PluginRegistry.getProvPlugins().size();
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions,
	 * javax.xml.namespace.QName)
	 */
	@Override
	public TOSCAPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		// create empty plan from servicetemplate and add definitions
		
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			String namespace;
			if (serviceTemplate.getTargetNamespace() != null) {
				namespace = serviceTemplate.getTargetNamespace();
			} else {
				namespace = definitions.getTargetNamespace();
			}
			
			if (namespace.equals(serviceTemplateId.getNamespaceURI()) && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
				String processName = serviceTemplate.getId() + "_buildPlan";
				String processNamespace = serviceTemplate.getTargetNamespace() + "_buildPlan";
				TOSCAPlan newBuildPlan = this.planHandler.createPlan(serviceTemplate, processName, processNamespace, 0);
				newBuildPlan.setDefinitions(definitions);
				newBuildPlan.setCsarName(csarName);
				
				// create empty templateplans for each template and add them to
				// buildplan
				for (AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
					TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate, newBuildPlan);
					newTemplate.setNodeTemplate(nodeTemplate);
					newBuildPlan.addTemplateBuildPlan(newTemplate);
				}
				
				for (AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
					TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(relationshipTemplate, newBuildPlan);
					newTemplate.setRelationshipTemplate(relationshipTemplate);
					newBuildPlan.addTemplateBuildPlan(newTemplate);
				}
				
				// connect the templates
				this.initializeDependenciesInBuildPlan(newBuildPlan);
				
				PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan);
				
				// init output
				this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap);
				
				// instanceDataAPI handling is done solely trough this extension
				this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true, newBuildPlan);
				
				// initialize instanceData handling
				this.serviceInstanceInitializer.initializeInstanceDataFromInput(newBuildPlan);
				
				this.instanceInit.addNodeInstanceIDVarToTemplatePlans(newBuildPlan);
				
				this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newBuildPlan, propMap);
				
				this.runPlugins(newBuildPlan, serviceTemplate.getQName(), propMap);
				
				this.idInit.addCorrellationID(newBuildPlan);
				
				this.finalizer.finalize(newBuildPlan);
				BuildPlanBuilder.LOG.debug("Created BuildPlan:");
				BuildPlanBuilder.LOG.debug(this.getStringFromDoc(newBuildPlan.getBpelDocument()));
				return newBuildPlan;
			}
		}
		BuildPlanBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}", serviceTemplateId.toString(), definitions.getId(), csarName);
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions)
	 */
	@Override
	public List<TOSCAPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<TOSCAPlan> plans = new ArrayList<TOSCAPlan>();
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			QName serviceTemplateId;
			// targetNamespace attribute doesn't has to be set, so we check it
			if (serviceTemplate.getTargetNamespace() != null) {
				serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
			} else {
				serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
			}
			
			if (!serviceTemplate.hasBuildPlan()) {
				BuildPlanBuilder.LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan", serviceTemplateId.toString());
				TOSCAPlan newBuildPlan = this.buildPlan(csarName, definitions, serviceTemplateId);
				
				if (newBuildPlan != null) {
					BuildPlanBuilder.LOG.debug("Created BuildPlan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
					plans.add(newBuildPlan);
				}
			} else {
				BuildPlanBuilder.LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed", serviceTemplateId.toString());
			}
		}
		return plans;
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
	private void runPlugins(TOSCAPlan buildPlan, QName serviceTemplateId, PropertyMap map) {
		
		for (TemplateBuildPlan templatePlan : buildPlan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null) {
				// handling nodetemplate
				AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
				BuildPlanBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
				TemplatePlanContext context = new TemplatePlanContext(templatePlan, map, serviceTemplateId);
				// check if we have a generic plugin to handle the template
				// Note: if a generic plugin fails during execution the
				// TemplateBuildPlan is broken!
				IPlanBuilderTypePlugin plugin = this.canGenericPluginHandle(nodeTemplate);
				if (plugin == null) {
					BuildPlanBuilder.LOG.debug("Handling NodeTemplate {} with ProvisioningChain", nodeTemplate.getId());
					ProvisioningChain chain = TemplatePlanBuilder.createProvisioningChain(nodeTemplate);
					if (chain == null) {
						BuildPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}", nodeTemplate.getId());
					} else {
						BuildPlanBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}", nodeTemplate.getId());
						chain.executeIAProvisioning(context);
						chain.executeDAProvisioning(context);
						chain.executeOperationProvisioning(context, this.opNames);
					}
				} else {
					BuildPlanBuilder.LOG.info("Handling NodeTemplate {} with generic plugin", nodeTemplate.getId());
					plugin.handle(context);
				}
				
				for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
					if (postPhasePlugin.canHandle(nodeTemplate)) {
						postPhasePlugin.handle(context, nodeTemplate);
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
					BuildPlanBuilder.LOG.debug("Handling RelationshipTemplate {} with ProvisioningChains", relationshipTemplate.getId());
					ProvisioningChain sourceChain = TemplatePlanBuilder.createProvisioningChain(relationshipTemplate, true);
					ProvisioningChain targetChain = TemplatePlanBuilder.createProvisioningChain(relationshipTemplate, false);
					
					// first execute provisioning on target, then on source
					if (targetChain != null) {
						BuildPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for TargetInterface of RelationshipTemplate {}", relationshipTemplate.getId());
						targetChain.executeIAProvisioning(context);
						targetChain.executeOperationProvisioning(context, this.opNames);
					}
					
					if (sourceChain != null) {
						BuildPlanBuilder.LOG.warn("Couldn't create ProvisioningChain for SourceInterface of RelationshipTemplate {}", relationshipTemplate.getId());
						sourceChain.executeIAProvisioning(context);
						sourceChain.executeOperationProvisioning(context, this.opNames);
					}
				} else {
					BuildPlanBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin", relationshipTemplate.getId());
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
			BuildPlanBuilder.LOG.debug("Checking whether Generic Plugin " + plugin.getID() + " can handle NodeTemplate " + nodeTemplate.getId());
			if (plugin.canHandle(nodeTemplate)) {
				BuildPlanBuilder.LOG.info("Found GenericPlugin {} that can handle NodeTemplate {}", plugin.getID(), nodeTemplate.getId());
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
				BuildPlanBuilder.LOG.info("Found GenericPlugin {} thath can handle RelationshipTemplate {}", plugin.getID(), relationshipTemplate.getId());
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
				BuildPlanBuilder.LOG.info("Handling relationshipTemplate {} with generic plugin {}", relationshipTemplate.getId(), plugin.getID());
				return plugin.handle(context);
			}
		}
		return false;
	}
	
	// TODO delete this method, or add to utils. is pretty much copied from the
	// net
	/**
	 * <p>
	 * Converts the given DOM Document to a String
	 * </p>
	 *
	 * @param doc a DOM Document
	 * @return a String representation of the complete Document given
	 */
	public String getStringFromDoc(org.w3c.dom.Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(domSource, result);
			writer.flush();
			return writer.toString();
		} catch (TransformerException ex) {
			BuildPlanBuilder.LOG.error("Couldn't transform DOM Document to a String", ex);
			return null;
		}
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
				BuildPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationlinkName);
				
				// third: generate global link for the target to relation
				// dependency
				String targetToRelationlinkName = target.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(targetToRelationlinkName, buildPlan);
				
				// fourth: connect target with relationship as target
				BuildPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(target, relationshipPlan, targetToRelationlinkName);
				
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				
				// with the other relations we have to build first the source,
				// then the relation and at last the target
				
				// first: generate global link for the source to relation
				// dependeny
				String sourceToRelationLinkName = source.getNodeTemplate().getId() + "_BEFORE_" + relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationLinkName, buildPlan);
				
				// second: connect source to relation
				BuildPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}", source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationLinkName);
				
				// third: generate global link for the relation to target
				// dependency
				String relationToTargetLinkName = relationshipPlan.getRelationshipTemplate().getId() + "_BEFORE_" + target.getNodeTemplate().getId();
				this.planHandler.addLink(relationToTargetLinkName, buildPlan);
				
				// fourth: connect relation to target
				BuildPlanBuilder.LOG.debug("Connecting RelationshipTemplate {} -> NodeTemplate {}", target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, relationToTargetLinkName);
			}
			
		}
	}
	
}
