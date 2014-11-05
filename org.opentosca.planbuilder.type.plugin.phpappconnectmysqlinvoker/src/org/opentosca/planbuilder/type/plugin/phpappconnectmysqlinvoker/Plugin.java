/**
 *
 */
package org.opentosca.planbuilder.type.plugin.phpappconnectmysqlinvoker;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.phpappconnectmysqlinvoker.handler.Handler;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderGenericPlugin {

	private Handler handler;
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);


	public Plugin() {
		try {
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			LOG.error("Couldn't initialize internal handler", e);
		}
	}

	@Override
	public String getID() {
		return Constants.pluginId;
	}

	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		LOG.debug("Handling RelationshipTemplate " + templateContext.getRelationshipTemplate().getId());
		return this.handler.handle(templateContext);
	}

	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// we can't handle nodeTemplates
		return false;
	}

	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		LOG.debug("Checking if relationshipTemplate " + relationshipTemplate.getId() + " can be handled");
		if (Utils.checkForTypeInHierarchy(relationshipTemplate, Constants.phpAppConnectsToMySqlType)) {
			LOG.debug("RelationshipTemplate " + relationshipTemplate.getId() + " has proper type");
			// check the source and the target
			AbstractNodeTemplate sourceNodeTemplate = relationshipTemplate.getSource();
			AbstractNodeTemplate targetNodeTemplate = relationshipTemplate.getTarget();
			LOG.debug("Checking with:");
			LOG.debug("SourceNodeTemplate: " + sourceNodeTemplate.getId() + " type:" + sourceNodeTemplate.getType().getId().toString());
			LOG.debug("TargetNodeTemplate: " + targetNodeTemplate.getId() + " type:" + targetNodeTemplate.getType().getId().toString());
			if (!Utils.checkForTypeInHierarchy(sourceNodeTemplate, Constants.phpAppType)) {
				LOG.debug("RelationshipTemplate " + relationshipTemplate.getId() + " isn't connected to a NodeTemplate of type " + Constants.phpAppType.toString());
				return false;
			}
			if (!Utils.checkForTypeInHierarchy(targetNodeTemplate, Constants.mySqlDbType)) {
				LOG.debug("RelationshipTemplate " + relationshipTemplate.getId() + " isn't connected to a NodeTemplate of type " + Constants.mySqlDbType.toString());
				return false;
			}
			// TODO make the check more rigid
			LOG.debug("Check was successful");
			return true;
		}
		return false;
	}

}
