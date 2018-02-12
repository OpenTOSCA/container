package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.core.handler;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

/**
 * <p>
 * This class contains logic to upload files to a linux machine. Those files
 * must be available trough a openTOSCA Container
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public interface PrePhasePluginHandler<T extends PlanContext> {

	/**
	 * Adds necessary BPEL logic trough the given context that can upload the given
	 * DA unto the given InfrastructureNode
	 *
	 * @param context
	 *            a TemplateContext
	 * @param da
	 *            the DeploymentArtifact to deploy
	 * @param nodeTemplate
	 *            the NodeTemplate which is used as InfrastructureNode
	 * @return true iff adding logic was successful
	 */
	public boolean handle(final T context, final AbstractDeploymentArtifact da,
			final AbstractNodeTemplate nodeTemplate);

	/**
	 * Adds necessary BPEL logic through the given context that can upload the given
	 * IA unto the given InfrastructureNode
	 *
	 * @param context
	 *            a TemplateContext
	 * @param ia
	 *            the ImplementationArtifact to deploy
	 * @param nodeTemplate
	 *            the NodeTemplate which is used as InfrastructureNode
	 * @return true iff adding logic was successful
	 */
	public boolean handle(final T context, final AbstractImplementationArtifact ia,
			final AbstractNodeTemplate nodeTemplate);
}
