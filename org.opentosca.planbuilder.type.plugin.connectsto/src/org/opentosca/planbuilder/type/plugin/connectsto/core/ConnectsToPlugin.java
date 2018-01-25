package org.opentosca.planbuilder.type.plugin.connectsto.core;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the RelationshipType
 * ConnectsTo. This plugin searches for a connection interface on the source
 * node, which implements a connectsTo operation with any kind of parameter.
 * These parameters will be wired against properties of the stack connected to
 * as target to this relation.
 * </p>
 *
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class ConnectsToPlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {
	public static final String PLUGIN_ID = "OpenTOSCA PlanBuilder Type Plugin Client connects to Mosquitto Broker";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
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
	 * @see org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin#canHandle(org.
	 * opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate)
	 */
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {

		// check the relationshipType
		if (!ModelUtils.getRelationshipTypeHierarchy(relationshipTemplate.getRelationshipType())
				.contains(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
			return false;
		}

		// look for a connectTo operation on the source node
		AbstractNodeTemplate sourceNode = relationshipTemplate.getSource();

		for (AbstractInterface iface : sourceNode.getType().getInterfaces()) {
			for (AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals("connectTo")) {
					// found needed operation
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.opentosca.planbuilder.plugins.IPlanBuilderPlugin#getID()
	 */
	@Override
	public String getID() {
		return ConnectsToPlugin.PLUGIN_ID;
	}

}
