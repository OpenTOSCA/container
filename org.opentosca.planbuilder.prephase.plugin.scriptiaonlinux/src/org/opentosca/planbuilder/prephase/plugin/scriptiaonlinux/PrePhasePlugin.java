package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a PrePhase Plugin for IAs of type
 * {http://docs.oasis-open.org/tosca
 * /ns/2011/12/ToscaBaseTypes}ScriptArtifact,{http
 * ://www.example.com/ToscaTypes}WAR and DAs of type
 * {http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ArchiveArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PrePhasePlugin implements IPlanBuilderPrePhaseIAPlugin, IPlanBuilderPrePhaseDAPlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(PrePhasePlugin.class);
	
	private QName artifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ScriptArtifact");
	private QName daArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");
	private QName warArtifactType = new QName("http://www.example.com/ToscaTypes", "WAR");
	private QName nodeType = new QName("http://tempuri.org", "OpenToscaAmazonVM");
	private QName nodeType2 = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "VM");
	
	private Handler handler = new Handler();
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "openTOSCA ScriptZIPDAIAOnLinux Plugin v0.1";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractDeploymentArtifact deploymentArtifact, AbstractNodeType infrastructureNodeType) {
		QName type = deploymentArtifact.getArtifactType();
		PrePhasePlugin.LOG.debug("Checking if type: " + type.toString() + " and infrastructure nodeType: " + infrastructureNodeType.getId().toString() + " can be handled");
		
		if (this.artifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		
		if (this.daArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		
		if (this.warArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		if (this.artifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		
		if (this.daArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		
		if (this.warArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractImplementationArtifact ia, AbstractNodeType infrastructureNodeType) {
		QName type = ia.getArtifactType();
		PrePhasePlugin.LOG.debug("Checking if type: " + type.toString() + " and infrastructure nodeType: " + infrastructureNodeType.getId().toString() + " can be handled");
		
		if (this.artifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		
		if (this.daArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		
		if (this.warArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType.toString())) {
			return true;
		}
		if (this.artifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		
		if (this.daArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		
		if (this.warArtifactType.toString().equals(type.toString()) && infrastructureNodeType.getId().toString().equals(this.nodeType2.toString())) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractImplementationArtifact ia, AbstractNodeTemplate nodeTemplate) {
		if (ia.getArtifactType().toString().equals(this.warArtifactType.toString())) {
			// provisioning of webservice war files, is in the responsibility of
			// the opentosca IA Engine. We just let the planbuilder know that
			// some ias where provisioned
			return true;
		}
		return this.handler.handle(context, ia, nodeTemplate);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractDeploymentArtifact da, AbstractNodeTemplate nodeTemplate) {
		return this.handler.handle(context, da, nodeTemplate);
	}
	
}
