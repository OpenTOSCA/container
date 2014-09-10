package org.opentosca.planbuilder.generic.plugin.phponapache;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.generic.plugin.phponapache.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

public class Plugin implements IPlanBuilderGenericPlugin {
	
	private final QName phpModule = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServerPhpModule");
	private final QName apacheNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA PhpModule on Apache HTTP Web Server Plugin";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			// error
			return false;
		} else {
			if (this.canHandle(templateContext.getNodeTemplate())) {
				return this.handler.handle(templateContext);
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		if (!nodeTemplate.getType().getId().toString().equals(this.phpModule.toString())) {
			// looking for a phpModule here
			return false;
		} else {
			// found phpModule, check whether it's connected to apache
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (relation.getTarget().getType().getId().toString().equals(this.apacheNodeType.toString())) {
					// php module is connected with apache in the topology
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can only handle nodeTemplates
		return false;
	}
	
}
