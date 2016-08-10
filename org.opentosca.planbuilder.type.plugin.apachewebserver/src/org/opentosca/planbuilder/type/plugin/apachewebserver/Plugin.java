package org.opentosca.planbuilder.type.plugin.apachewebserver;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.model.tosca.conventions.Utils;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.apachewebserver.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents the Apache HTTP WebServer Generic Plugin using the
 * Invoker Service of the OpenTOSCA Container
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderTypePlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	
	// these are the nodeTypes used in some test CSAR's
	private final static QName apacheNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	
	// these are the official nodeTypes	
	private final static QName apacheNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApacheWebServer");
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin ApacheWebServer";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		// a slight check before we start adding logic
		if (templateContext.getNodeTemplate() == null) {
			return false;
		}
		
		if (!this.canHandle(templateContext.getNodeTemplate())) {
			return false;
		}
		Plugin.LOG.debug("Starting to handle OpenTOSCA ApacheWebServer Node");
		return this.handler.handle(templateContext);
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// check whether the nodeTemplate has the openTosca apacheWebServer
		// nodeType
		Plugin.LOG.debug("Checking whether nodeTemplate " + nodeTemplate.getId() + " can be handled");
		if (!Plugin.isApacheNodeTypeCompatible(nodeTemplate.getType().getId())) {
			Plugin.LOG.debug("NodeTemplate is no " + Plugin.apacheNodeType + " nodeType. Can't handle NodeTemplate");
			return false;
		}
		// now check whether this nodeTemplate is connected to NodeTemplate with
		// the Ubuntu NodeType
		Plugin.LOG.debug("Checking whether the NodeTemplate is connected to a NodeTemplate of NodeType " + Types.ubuntuNodeType.toString());
		for (AbstractRelationshipTemplate relations : nodeTemplate.getOutgoingRelations()) {
			Plugin.LOG.debug("Traversing relationshipType " + relations.getId());
			Plugin.LOG.debug("Checking NodeTemplate " + relations.getTarget().getId());
			if (Utils.isSupportedInfrastructureNodeType(relations.getTarget().getType().getId())) {
				// the opentosca apacheWebServer template is connected to a
				// template with Ubuntu nodeType, so we can handle it
				return true;
			}
		}
		// no connection to a ubuntu template
		Plugin.LOG.debug("Can't handle the given NodeTemplate");
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we only handle NodeTemplates
		return false;
	}
	
	/**
	 * Checks whether the given QName represents a Apache Web Server NodeType
	 * compatible with this plugin
	 * 
	 * @param nodeTypeId a QName denoting a TOSCA NodeType
	 * @return true iff the given QName is a NodeType this plugin can handle
	 */
	public static boolean isApacheNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.equals(Plugin.apacheNodeType)) {
			return true;
		}
		if (nodeTypeId.equals(Plugin.apacheNodeTypeTOSCASpecificType)) {
			return true;
		}
		return false;
	}
	
}
