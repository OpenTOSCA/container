package org.opentosca.planbuilder.type.plugin.mysqldatabase;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.mysqldatabase.handler.LifecycleHandler;
import org.opentosca.planbuilder.type.plugin.mysqldatabase.handler.SQLFileHandler;
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
public class Plugin implements IPlanBuilderTypePlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	
	private LifecycleHandler lifecycleHandler;
	private SQLFileHandler sqlFileHandler;
	
	
	public Plugin() {
		try {
			this.lifecycleHandler = new LifecycleHandler();
		} catch (ParserConfigurationException e) {
			Plugin.LOG.error("Couldn't instantiate internal handler object", e);
		}
		
		this.sqlFileHandler = new SQLFileHandler();
	}
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin MySQL Database";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		LOG.debug("Handling NodeTemplate " + templateContext.getNodeTemplate().getId());
		if ((templateContext.getNodeTemplate() != null) && this.canHandle(templateContext.getNodeTemplate())) {
			// determine the handler to use
			
			AbstractNodeTypeImplementation nodeImpl = Util.selectSQLFileNodeTypeImplementation(templateContext.getNodeTemplate());
			// this should ensure that atleast one sqlFile will be available to
			// the handler
			if (nodeImpl != null | Util.hasSqlScriptArtifact(templateContext.getNodeTemplate().getDeploymentArtifacts())) {
				LOG.debug("Handling NodeTemplate with SQLFileHandler");
				return this.sqlFileHandler.handle(templateContext, nodeImpl);
			}
			
			LOG.debug("Handling NodeTemplate with LifecycleHandler");
			// we fallback to lifecycle handling if no sqlFile is available
			nodeImpl = Util.selectLifecycleInterfaceNodeTypeImplementation(templateContext.getNodeTemplate());
			return this.lifecycleHandler.handle(templateContext, nodeImpl);
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		LOG.debug("Checking if given NodeTemplate " + nodeTemplate.getId() + " can be handled");
		// check for the mysql db type and whether it's connected to some mysql
		// server with connected vm
		
		if (Utils.checkForTypeInHierarchy(nodeTemplate, Constants.mySqlDbType)) {
			LOG.debug("Is valid nodeType");
			for (AbstractRelationshipTemplate relationship : nodeTemplate.getOutgoingRelations()) {
				AbstractNodeTemplate target = relationship.getTarget();
				LOG.debug("Checking target NodeTemplate " + target.getId());
				
				if (Utils.checkForTypeInHierarchy(target, Constants.mySqlServerType)) {
					LOG.debug("Found connection to mysql server");
					// found a mysql server connection
					
					boolean isConnectedToVm = Util.isConnectedToVM(relationship.getTarget());
					if (isConnectedToVm) {
						LOG.debug("Found connection to VM");
						if (Util.canDeployNodeTemplate(nodeTemplate)) {
							LOG.debug("NodeTemplate " + nodeTemplate.getId() + " has proper implementation provided");
							return true;
						}
					} else {
						LOG.debug("MySQLServer is not connected to VM type");
						return false;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// can't handle relationshipTemplates
		return false;
	}
	
}
