/**
 *
 */
package org.opentosca.planbuilder.generic.plugin.mysqlserverinvoker;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.generic.plugin.mysqlserverinvoker.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author nyu
 * 
 */
public class Plugin implements IPlanBuilderGenericPlugin {
	
	private static final QName mySqlServerNodeType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "MySQL");
	private static final QName ubuntuNodeTypeOpenTOSCAPlanBuilder = new QName("http://opentosca.org/types/declarative", "Ubuntu");
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
		return "OpenTOSCA PlanBuilder Generic Plugin MySql Server Invoker";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		return this.handler.handle(templateContext);
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// check first the nodeTemplate
		if (this.isCompatibleMySQLServerNodeType(nodeTemplate.getType().getId())) {
			// check whether the mysql server is connected to a Ubuntu
			// NodeTemplate
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (this.isCompatibleUbuntuNodeType(relation.getTarget().getType().getId())) {
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
		return Plugin.mySqlServerNodeType.toString().equals(nodeTypeId.toString());
	}
	
	/**
	 * Checks whether the given QName represents a Ubuntu OS NodeType
	 * 
	 * @param nodeTypeId a QName
	 * @return true iff the QName represents a Ubuntu NodeType
	 */
	public static boolean isCompatibleUbuntuNodeType(QName nodeTypeId) {
		return Plugin.ubuntuNodeTypeOpenTOSCAPlanBuilder.toString().equals(nodeTypeId.toString());
	}
	
}
