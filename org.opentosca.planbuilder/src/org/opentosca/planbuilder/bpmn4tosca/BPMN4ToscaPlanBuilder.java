/**
 *
 */
package org.opentosca.planbuilder.bpmn4tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElement;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElementType;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaPlan;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaTask;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * PlanBuilder for BPMN4Tosca Plans
 * </p>
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Alex Frank - st152404@stud.uni-stuttgart.de
 */
public class BPMN4ToscaPlanBuilder extends AbstractBuildPlanBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMN4ToscaPlanBuilder.class);

	@Override
	public AbstractPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId) {
		LOGGER.info("Building bpmn4tosca plan");

		BPMN4ToscaPlan abstractPlan = null;
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

			String namespace;

			if (serviceTemplate.getTargetNamespace() != null) {
				namespace = serviceTemplate.getTargetNamespace();
			} else {
				namespace = definitions.getTargetNamespace();
			}

			if (namespace.equals(serviceTemplateId.getNamespaceURI())
					&& serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
				AbstractPlan generatedPOG = super.generatePOG(serviceTemplate.getName(), definitions, serviceTemplate);
				abstractPlan = new BPMN4ToscaPlan(generatedPOG.getId(), generatedPOG.getType(),
						generatedPOG.getDefinitions(), generatedPOG.getServiceTemplate(), generatedPOG.getActivites(),
						generatedPOG.getLinks());
				this.generateElements(abstractPlan, abstractPlan.getLinks());
			}
		}

		// LOGGER.warn("Could not create BPMN4Tosca plan");
		return abstractPlan;
	}

	@Override
	public List<AbstractPlan> buildPlans(String csarName, AbstractDefinitions definitions) {
		List<AbstractPlan> plans = new ArrayList<AbstractPlan>();
		for (AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			QName serviceTemplateId;
			// targetNamespace attribute doesn't has to be set, so we check it
			if (serviceTemplate.getTargetNamespace() != null) {
				serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
			} else {
				serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
			}

			if (!serviceTemplate.hasBuildPlan()) {
				AbstractPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

				if (newBuildPlan != null) {
					plans.add(newBuildPlan);
				}
			} else {
				LOGGER.info("No Build Plan available");
			}
		}
		return plans;
	}

	private void generateElements(BPMN4ToscaPlan plan, Set<Link> links) {
		List<AbstractActivity> activities = links.stream().map(link -> {
			AbstractActivity src = link.getSrcActiv();
			AbstractActivity trgt = link.getTrgActiv();
			return src instanceof ANodeTemplateActivity ? src : trgt;
		}).collect(Collectors.toList());

		LOGGER.debug("Adding start element");
		final BPMN4ToscaElement startElement = new BPMN4ToscaTask();
		startElement.setId("start-element");
		startElement.setName(BPMN4ToscaElementType.START_TASK.name());
		startElement.setType(BPMN4ToscaElementType.START_TASK);
		plan.getElements().addFirst(startElement);

		LOGGER.debug("Running plugins");
		for (AbstractActivity abstractActivity : activities) {
			this.runPlugins(plan, abstractActivity);
		}

		LOGGER.debug("Adding end element");
		final BPMN4ToscaElement endElement = new BPMN4ToscaTask();
		endElement.setId("end-element");
		endElement.setName(BPMN4ToscaElementType.END_TASK.name());
		endElement.setType(BPMN4ToscaElementType.END_TASK);

		plan.getElements().addLast(endElement);
	}

	private void runPlugins(BPMN4ToscaPlan plan, AbstractActivity abstractActivity) {
		if (abstractActivity instanceof ANodeTemplateActivity) {
			// handling nodetemplate
			AbstractNodeTemplate nodeTemplate = ((ANodeTemplateActivity) abstractActivity).getNodeTemplate();
			LOGGER.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
			// check if we have a generic plugin to handle the template
			// Note: if a generic plugin fails during execution the
			// TemplateBuildPlan is broken!
			IPlanBuilderTypePlugin plugin = this.findTypePlugin(nodeTemplate);
			if (plugin != null) {
				LOGGER.info("Handling NodeTemplate {} with plugin {}", nodeTemplate.getId(), plugin.getID());
				plugin.handle(plan.getElements(), nodeTemplate);
			} else {
				LOGGER.warn("No Plugins for NodeTemplate {}", nodeTemplate.getId());
			}

			for (IPlanBuilderPostPhasePlugin postPhasePlugin : PluginRegistry.getPostPlugins()) {
				if (postPhasePlugin.canHandle(nodeTemplate)) {
					// postPhasePlugin.handle(context, nodeTemplate);
				}
			}

		} else {
			//
		}

	}

	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * RelationshipTemplate
	 * </p>
	 *
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate denoting a RelationshipTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         RelationshipTemplate, else false
	 */
	private boolean canGenericPluginHandle(AbstractRelationshipTemplate relationshipTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			if (plugin.canHandle(relationshipTemplate)) {
				LOGGER.info("Found GenericPlugin {} thath can handle RelationshipTemplate {}", plugin.getID(),
						relationshipTemplate.getId());
				return true;
			}
		}
		return false;
	}
}
