package org.opentosca.planbuilder.type.plugin.php;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.php.handler.Handler;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP
 * Server with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderTypePlugin {
	
	// these are type for various test CSAR's, leave em here until everything is
	// organized
	private final QName phpModule = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServerPhpModule");
	private final QName apacheNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	
	// these are the types from the TOSCA specific types
	private final QName phpModuleTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApachePHPModule");
	private final QName apacheNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApacheWebServer");
	
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA PlanBuilder Type Plugin PHP";
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
		if (!this.isPhpModuleNodeTypeCompatible(nodeTemplate.getType().getId())) {
			// looking for a phpModule here
			return false;
		} else {
			// found phpModule, check whether it's connected to apache
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (this.isApacheWebServerNodeTypeCompatible(relation.getTarget().getType().getId())) {
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
	
	/**
	 * Checks whether the given QName is accepted as an PhpModule Node
	 * 
	 * @param nodeTypeId a QName
	 * @return true iff the QName represents an PhpModule
	 */
	private boolean isPhpModuleNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.equals(this.phpModule)) {
			return true;
		}
		if (nodeTypeId.equals(this.phpModuleTOSCASpecificType)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether the given QName is accepted as an ApacheWebServer Node
	 * 
	 * @param nodeTypeId a QName
	 * @return true iff the QName represents an ApacheWebServer
	 */
	private boolean isApacheWebServerNodeTypeCompatible(QName nodeTypeId) {
		if (nodeTypeId.equals(this.apacheNodeType)) {
			return true;
		}
		if (nodeTypeId.equals(this.apacheNodeTypeTOSCASpecificType)) {
			return true;
		}
		return false;
	}
	
}
