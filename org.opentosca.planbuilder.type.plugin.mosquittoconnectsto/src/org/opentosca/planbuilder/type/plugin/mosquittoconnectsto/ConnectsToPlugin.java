package org.opentosca.planbuilder.type.plugin.mosquittoconnectsto;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.utils.Utils;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * This class implements a PlanBuilder Type Plugin for the RelationshipType
 * MosquittoConnectsTo. The plugin looks for a connection of the given
 * RelationshipTemplate with a Moquitto Stack which entails a Topic and
 * Mosquitto node.
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ConnectsToPlugin implements IPlanBuilderTypePlugin {

	private Handler handler;

	public ConnectsToPlugin() {
		try {
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.plugins.IPlanBuilderPlugin#getID()
	 */
	@Override
	public String getID() {
		return Constants.pluginId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#handle(org.
	 * opentosca.planbuilder.plugins.context.TemplatePlanContext)
	 */
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		return this.handler.handle(templateContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
	 * opentosca.planbuilder.model.tosca.AbstractNodeTemplate)
	 */
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// we can't handle nodeTemplates
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
	 * opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate)
	 */
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {

		// check the relationshipType
		if (!relationshipTemplate.getType().equals(Constants.mosquittoConnectsToRelationshipType)) {
			return false;
		}

		/*
		 * check whether the target is a topic which is on top of an mosquitto
		 * node
		 */
		// check if this relation is connected to a topic
		if (!relationshipTemplate.getTarget().getType().getId().equals(Constants.topicNodeType)) {
			return false;
		}

		// check if the topic is hosted on mosquitto
		boolean check = false;
		for (AbstractRelationshipTemplate relation : relationshipTemplate.getTarget().getOutgoingRelations()) {
			// cycle trough outgoing hostedOn relations
			if (Utils.getRelationshipBaseType(relation).equals(Utils.TOSCABASETYPE_HOSTEDON)) {
				if (relation.getTarget().getType().getId().equals(Constants.mosquittoNodeType)) {
					// found mosquitto -> found stack: topic -hostedOn->
					// mosquitto
					check = true;
				}
			}
		}

		return check;
	}

}
