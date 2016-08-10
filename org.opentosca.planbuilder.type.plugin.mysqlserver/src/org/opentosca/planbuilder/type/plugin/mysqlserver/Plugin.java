/**
 *
 */
package org.opentosca.planbuilder.type.plugin.mysqlserver;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.model.tosca.conventions.Utils;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.mysqlserver.handler.Handler;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author nyu
 * 
 */
public class Plugin implements IPlanBuilderTypePlugin {
	
	private static final QName mySqlServerNodeType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "MySQL");
	private Handler handler;
	
	
	public Plugin() {
		try {
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin MySQL Server";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		return this.handler.handle(templateContext);
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// check first the nodeTemplate
		if (Plugin.isCompatibleMySQLServerNodeType(nodeTemplate.getType().getId())) {
			// check whether the mysql server is connected to a Ubuntu
			// NodeTemplate
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (Utils.isSupportedInfrastructureNodeType(relation.getTarget().getType().getId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can't handle relationshipTemplates
		return false;
	}
	
	/**
	 * Checks whether the given QName represents a MySQL Server NodeType
	 * understood by this plugin
	 * 
	 * @param nodeTypeId a QName
	 * @return true iff the QName represents a MySQL NodeType
	 */
	public static boolean isCompatibleMySQLServerNodeType(QName nodeTypeId) {
		return Plugin.mySqlServerNodeType.equals(nodeTypeId);
	}
	
	
}
