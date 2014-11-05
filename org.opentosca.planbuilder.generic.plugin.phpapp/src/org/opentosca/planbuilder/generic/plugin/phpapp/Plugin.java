/**
 *
 */
package org.opentosca.planbuilder.generic.plugin.phpapp;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.generic.plugin.phpapp.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

/**
 * 
 * <p>
 * Plugin class for Php Application deployment.
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderGenericPlugin {
	
	private final QName phpApp = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAPhpApplicationApacheHTTP");
	private final QName apacheWebServer = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	private final QName phpModule = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServerPhpModule");
	
	private final QName phpAppNodeTypePlanBuilder = new QName("http://opentosca.org/types/declarative", "PhpApplication");
	private final QName apacheWebServerNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApacheWebServer");
	private final QName phpModuleNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApachePHPModule");
	
	private final QName zipArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA Php Application Declarative Plugin";
	}
	
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		if ((templateContext.getNodeTemplate() != null) && this.canHandle(templateContext.getNodeTemplate())) {
			return this.handler.handle(templateContext);
		}
		
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		QName testQname = nodeTemplate.getType().getId();
		if (this.isCompatiblePhpAppNodeType(nodeTemplate.getType().getId())) {
			// nodeType is okay, check if connected to PhpModule and ApacheHTTP
			// TODO should we also check if this node is connected exclusively
			// to apache and php ?
			int check = 0;
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (this.isCompatibleApacheWebServerNodeType(relation.getTarget().getType().getId())) {
					check++;
				}
				if (this.isCompatiblePhpModuleNodeType(relation.getTarget().getType().getId())) {
					check++;
				}
			}
			if (check == 2) {
				// node is connected with proper nodes, check if there is a
				// proper deployment artifact
				for (AbstractDeploymentArtifact artifact : nodeTemplate.getDeploymentArtifacts()) {
					if (artifact.getArtifactType().toString().equals(this.zipArtifactType.toString())) {
						// check reference
						for (AbstractArtifactReference ref : artifact.getArtifactRef().getArtifactReferences()) {
							if (ref.getReference().endsWith(".zip")) {
								return true;
							}
						}
						
					}
					
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
	
	private boolean isCompatibleApacheWebServerNodeType(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.apacheWebServer.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(this.apacheWebServerNodeTypeTOSCASpecificType.toString())) {
			return true;
		}
		return false;
	}
	
	private boolean isCompatiblePhpModuleNodeType(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.phpModule.toString())) {
			return true;
		}
		
		if (nodeTypeId.toString().equals(this.phpModuleNodeTypeTOSCASpecificType.toString())) {
			return true;
		}
		return false;
	}
	
	private boolean isCompatiblePhpAppNodeType(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.phpApp.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(this.phpAppNodeTypePlanBuilder.toString())) {
			return true;
		}
		return false;
	}
	
}
