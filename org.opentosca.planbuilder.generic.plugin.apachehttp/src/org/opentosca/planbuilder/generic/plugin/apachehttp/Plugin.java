package org.opentosca.planbuilder.generic.plugin.apachehttp;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.generic.plugin.apachehttp.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Plugin implements IPlanBuilderGenericPlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	
	private final QName ubuntuNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "Ubuntu");
	private final QName apacheNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
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
		if (!nodeTemplate.getType().getId().toString().equals(this.apacheNodeType.toString())) {
			Plugin.LOG.debug("NodeTemplate is no " + this.apacheNodeType + " nodeType. Can't handle NodeTemplate");
			return false;
		}
		// now check whether this nodeTemplate is connected to NodeTemplate with
		// the Ubuntu NodeType
		Plugin.LOG.debug("Checking whether the NodeTemplate is connected to a NodeTemplate of NodeType " + this.ubuntuNodeType.toString());
		for (AbstractRelationshipTemplate relations : nodeTemplate.getOutgoingRelations()) {
			Plugin.LOG.debug("Traversing relationshipType " + relations.getId());
			Plugin.LOG.debug("Checking NodeTemplate " + relations.getTarget().getId());
			if (relations.getTarget().getType().getId().toString().equals(this.ubuntuNodeType.toString())) {
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
	
}
