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
import org.opentosca.planbuilder.utils.Utils;
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

	private final QName scriptArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes",
			"ScriptArtifact");
	private final QName archiveArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes",
			"ArchiveArtifact");
	private final QName bpelArchiveArtifactType = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable",
			"BPEL");
	private final QName warArtifactType = new QName("http://www.example.com/ToscaTypes", "WAR");
	private final QName sqlArtifactType = new QName("http://opentosca.org/artifacttypes", "SQLArtifact");
	private final QName configurationArtifactType = new QName("http://opentosca.org/artifacttypes",
			"ConfigurationArtifact");

	private final QName ansibleArtifactType = new QName("http://opentosca.org/artifacttypes", "Ansible");
	private final QName chefArtifactType = new QName("http://opentosca.org/artifacttypes", "Chef");

	private Handler handler = new Handler();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "openTOSCA DA/IA On Linux Plugin v0.1";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractDeploymentArtifact deploymentArtifact, AbstractNodeType infrastructureNodeType) {
		QName type = deploymentArtifact.getArtifactType();
		PrePhasePlugin.LOG.debug("Checking if type: " + type.toString() + " and infrastructure nodeType: "
				+ infrastructureNodeType.getId().toString() + " can be handled");

		return this.isSupportedDeploymentPair(type, infrastructureNodeType.getId(), true);
	}

	@Override
	public boolean canHandle(AbstractImplementationArtifact ia, AbstractNodeType infrastructureNodeType) {
		QName type = ia.getArtifactType();
		PrePhasePlugin.LOG.debug("Checking if type: " + type.toString() + " and infrastructure nodeType: "
				+ infrastructureNodeType.getId().toString() + " can be handled");

		return this.isSupportedDeploymentPair(type, infrastructureNodeType.getId(), false);
	}

	/**
	 * Checks whether this Plugin can handle deploying artifacts of the given
	 * artfiactType to a given InfrastructureNode of the given
	 * infrastructureNodeType
	 * 
	 * @param scriptArtifactType
	 *            a QName denoting an scriptArtifactType
	 * @param infrastructureNodeType
	 *            a QName denoting an infrastructureNodeType
	 * @param isDA
	 *            indicates whether this check is on an IA or DA with the given
	 *            artifactType
	 * @return a Boolean. True if given pair of QName's denotes a pair which
	 *         this plugin can handle
	 */
	private boolean isSupportedDeploymentPair(QName artifactType, QName infrastructureNodeType, boolean isDA) {

		if (!isDA && this.warArtifactType.equals(artifactType) && infrastructureNodeType
				.equals(new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure"))) {
			// WARs are deployed as environment-centric artifacts -> doesn't
			// need to be deployed on a node inside the topology, instead we
			// install it inside the management infrastructure
			return true;
		}

		boolean isSupportedArtifactType = false;

		if (this.archiveArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.scriptArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.ansibleArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.chefArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.bpelArchiveArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.warArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.sqlArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		if (this.configurationArtifactType.equals(artifactType)) {
			isSupportedArtifactType |= true;
		}

		// we can deploy only on ubuntu nodes
		if (!org.opentosca.model.tosca.conventions.Utils.isSupportedInfrastructureNodeType(infrastructureNodeType)) {
			return false;
		}

		return isSupportedArtifactType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractImplementationArtifact ia,
			AbstractNodeTemplate nodeTemplate) {
		if (ia.getArtifactType().equals(this.warArtifactType)) {
			// provisioning of IA that are webservice war files, is in the
			// responsibility of
			// the opentosca IA Engine. We just let the planbuilder know that
			// some ias where provisioned
			return true;
		}
		return true;
		// this.handler.handle(context, ia, nodeTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractDeploymentArtifact da,
			AbstractNodeTemplate nodeTemplate) {
		return this.handler.handle(context, da, nodeTemplate);
	}

}
