package ba.prephase.plugin.linuxpackageda;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

import ba.prephase.plugin.linuxpackageda.handlers.Handler;

/**
 * <p>
 * This class implements the PrePhase DA Plugin Interface. This routes the given
 * Data to the Handlers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderPrePhaseDAPlugin {
	
	private QName nodeType = new QName("http://tempuri.org", "OpenToscaAmazonVM");
	private QName nodeType2 = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "VM");
	private QName packageDAType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "OsPackageArtifact");
	
	private Handler handler = new Handler();
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "OpenTOSCA Linux Package DA Plugin v0.0.1";
	}
	
	private boolean canHandle(QName type, AbstractNodeType nodeType) {
		if (this.packageDAType.equals(type) && this.nodeType.equals(nodeType.getId())) {
			return true;
		}
		if (this.packageDAType.equals(type) && this.nodeType2.equals(nodeType.getId())) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractDeploymentArtifact da, AbstractNodeTemplate infrastructureNodeTemplate) {
		return this.handler.handle(context, da, infrastructureNodeTemplate);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractDeploymentArtifact deploymentArtifact, AbstractNodeType infrastructureNodeType) {
		return this.canHandle(deploymentArtifact.getArtifactType(), infrastructureNodeType);
	}
	
}
