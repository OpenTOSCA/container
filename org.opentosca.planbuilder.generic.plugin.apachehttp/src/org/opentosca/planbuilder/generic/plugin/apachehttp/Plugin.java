package org.opentosca.planbuilder.generic.plugin.apachehttp;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.generic.plugin.apachehttp.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents the generic plugin for Apache HTTP Servers hosted on a
 * Ubuntu Operating System
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderGenericPlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	
	// these are the nodeTypes used in some test CSAR's
	private final QName ubuntuNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "Ubuntu");
	private final QName apacheNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	
	// these are the official nodeTypes
	private final QName ubuntuNodeTypeOpenTOSCAPlanBuilder = new QName("http://opentosca.org/types/declarative", "Ubuntu");
	private final QName apacheNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApacheWebServer");
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA ApacheWebServer on Ubuntu Generic Plugin";
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
		if (!this.isApacheNodeTypeCompatible(nodeTemplate.getType().getId())) {
			Plugin.LOG.debug("NodeTemplate is no " + this.apacheNodeType + " nodeType. Can't handle NodeTemplate");
			return false;
		}
		// now check whether this nodeTemplate is connected to NodeTemplate with
		// the Ubuntu NodeType
		Plugin.LOG.debug("Checking whether the NodeTemplate is connected to a NodeTemplate of NodeType " + this.ubuntuNodeType.toString());
		for (AbstractRelationshipTemplate relations : nodeTemplate.getOutgoingRelations()) {
			Plugin.LOG.debug("Traversing relationshipType " + relations.getId());
			Plugin.LOG.debug("Checking NodeTemplate " + relations.getTarget().getId());
			if (this.isUbuntuNodeTypeCompatible(relations.getTarget().getType().getId())) {
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
	private boolean isApacheNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.apacheNodeType.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(this.apacheNodeTypeTOSCASpecificType.toString())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the given QName represents a Ubuntu OS NodeType compatible
	 * with this plugin
	 * 
	 * @param nodeTypeId a QName denoting a TOSCA NodeType
	 * @return true iff the given QName is a NodeType this plugin can handle
	 */
	private boolean isUbuntuNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.ubuntuNodeType.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(this.ubuntuNodeTypeOpenTOSCAPlanBuilder.toString())) {
			return true;
		}
		return false;
	}
	
}
