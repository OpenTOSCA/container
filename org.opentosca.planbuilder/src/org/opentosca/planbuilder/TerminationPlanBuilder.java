package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.handlers.TemplateBuildPlanHandler;
import org.opentosca.planbuilder.helpers.BPELFinalizer;
import org.opentosca.planbuilder.helpers.CorrelationIDInitializer;
import org.opentosca.planbuilder.helpers.NodeInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.helpers.ServiceInstanceInitializer;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class TerminationPlanBuilder implements IPlanBuilder {

	private final static Logger LOG = LoggerFactory.getLogger(TerminationPlanBuilder.class);

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
	// adds nodeInstanceIDs to each templatePlan
	private NodeInstanceInitializer nodeInstanceInitializer;
	// class for finalizing build plans (e.g when some template didn't receive
	// some provisioning logic and they must be filled with empty elements)
	private BPELFinalizer finalizer;
	// accepted operations for provisioning
	private List<String> opNames = new ArrayList<String>();

	private CorrelationIDInitializer idInit = new CorrelationIDInitializer();

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
		} catch (ParserConfigurationException e) {
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
	public BuildPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			String namespace;
			if (serviceTemplate.getTargetNamespace() != null) {
				namespace = serviceTemplate.getTargetNamespace();
			} else {
				namespace = definitions.getTargetNamespace();
			}

			if (namespace.equals(serviceTemplateId.getNamespaceURI())
					&& serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
				String processName = serviceTemplate.getId() + "_terminationPlan";
				String processNamespace = serviceTemplate.getTargetNamespace() + "_terminationPlan";
				BuildPlan newBuildPlan = this.planHandler.createPlan(serviceTemplate, processName, processNamespace, 2);
				newBuildPlan.setDefinitions(definitions);
				newBuildPlan.setCsarName(csarName);

				// create empty templateplans for each template and add them to
				// buildplan
				for (AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
					TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(nodeTemplate,
							newBuildPlan);
					newTemplate.setNodeTemplate(nodeTemplate);
					newBuildPlan.addTemplateBuildPlan(newTemplate);
				}

				for (AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
						.getRelationshipTemplates()) {
					TemplateBuildPlan newTemplate = this.templateHandler.createTemplateBuildPlan(relationshipTemplate,
							newBuildPlan);
					newTemplate.setRelationshipTemplate(relationshipTemplate);
					newBuildPlan.addTemplateBuildPlan(newTemplate);
				}

				// connect the templates
				this.initializeDependenciesInTerminationPlan(newBuildPlan);

				PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan);

				// TODO IMPORTANT! SERVICEINSTANCEURL MUST BE IN THE TERMINATIONPLAN INPUT
				// initialize instanceData handling
				this.serviceInstanceInitializer.initializeInstanceData(newBuildPlan);

				this.nodeInstanceInitializer.initializeNodeInstanceData(newBuildPlan, propMap);

				// this.runPlugins(newBuildPlan, serviceTemplate.getQName(),
				// propMap);

				this.idInit.addCorrellationID(newBuildPlan);

				this.finalizer.finalize(newBuildPlan);

				return newBuildPlan;
			}
		}
		TerminationPlanBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
				serviceTemplateId.toString(), definitions.getId(), csarName);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
	 * org.opentosca.planbuilder.model.tosca.AbstractDefinitions)
	 */
	@Override
	public List<BuildPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<BuildPlan> plans = new ArrayList<BuildPlan>();
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			QName serviceTemplateId;
			// targetNamespace attribute doesn't has to be set, so we check it
			if (serviceTemplate.getTargetNamespace() != null) {
				serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
			} else {
				serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
			}

			if (!serviceTemplate.hasBuildPlan()) {
				TerminationPlanBuilder.LOG.debug(
						"ServiceTemplate {} has no TerminationPlan, generating TerminationPlan",
						serviceTemplateId.toString());
				BuildPlan newBuildPlan = this.buildPlan(csarName, definitions, serviceTemplateId);

				if (newBuildPlan != null) {
					TerminationPlanBuilder.LOG.debug(
							"Created TerminationPlan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
					plans.add(newBuildPlan);
				}
			} else {
				TerminationPlanBuilder.LOG.debug("ServiceTemplate {} has TerminationPlan, no generation needed",
						serviceTemplateId.toString());
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
	 * @param plan
	 *            a TerminationPlan where all TemplateBuildPlans are set for
	 *            each template inside TopologyTemplate the BuildPlan should
	 *            provision
	 */
	private void initializeDependenciesInTerminationPlan(BuildPlan plan) {
		for (TemplateBuildPlan relationshipPlan : this.planHandler.getRelationshipTemplatePlans(plan)) {
			// determine base type of relationshiptemplate
			QName baseType = Utils.getRelationshipBaseType(relationshipPlan.getRelationshipTemplate());

			// determine source and target of relationshiptemplate
			TemplateBuildPlan source = this.planHandler
					.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getSource().getId(), plan);
			TemplateBuildPlan target = this.planHandler
					.getTemplateBuildPlanById(relationshipPlan.getRelationshipTemplate().getTarget().getId(), plan);

			// set dependencies inside buildplan (the links in the flow)
			// according to the basetype
			if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				// with a connectsto relation we have to first terminate the
				// relationshiptemplate and then the nodetemplates

				// first: generate global link for the source to relation
				// dependency
				String sourceToRelationlinkName = source.getNodeTemplate().getId() + "_BEFORE_"
						+ relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationlinkName, plan);

				// second: connect source with relationship as target
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}",
						source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, source, sourceToRelationlinkName);

				// third: generate global link for the target to relation
				// dependency
				String targetToRelationlinkName = target.getNodeTemplate().getId() + "_BEFORE_"
						+ relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(targetToRelationlinkName, plan);

				// fourth: connect target with relationship as target
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}",
						target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, targetToRelationlinkName);

			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON)
					| baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {

				// with the other relations we have to terminate first the
				// source,
				// then the relation and at last the target

				// first: generate global link for the source to relation
				// dependeny
				String sourceToRelationLinkName = source.getNodeTemplate().getId() + "_BEFORE_"
						+ relationshipPlan.getRelationshipTemplate().getId();
				this.planHandler.addLink(sourceToRelationLinkName, plan);

				// second: connect source to relation
				TerminationPlanBuilder.LOG.debug("Connecting NodeTemplate {} -> RelationshipTemplate {}",
						source.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(source, relationshipPlan, sourceToRelationLinkName);

				// third: generate global link for the relation to target
				// dependency
				String relationToTargetLinkName = relationshipPlan.getRelationshipTemplate().getId() + "_BEFORE_"
						+ target.getNodeTemplate().getId();
				this.planHandler.addLink(relationToTargetLinkName, plan);

				// fourth: connect relation to target
				TerminationPlanBuilder.LOG.debug("Connecting RelationshipTemplate {} -> NodeTemplate {}",
						target.getNodeTemplate().getId(), relationshipPlan.getRelationshipTemplate().getId());
				this.templateHandler.connect(relationshipPlan, target, relationToTargetLinkName);
			}

		}
	}

}
