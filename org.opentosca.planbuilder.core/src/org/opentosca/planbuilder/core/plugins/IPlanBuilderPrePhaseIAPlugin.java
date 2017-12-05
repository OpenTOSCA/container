package org.opentosca.planbuilder.core.plugins;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;

/**
 * <p>
 * This interface should be implemented by Plugin which can add PrePhase
 * Fragments for IA's. The plugin should be able to deploy at least one
 * ArtifactType to a specific NodeType
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public interface IPlanBuilderPrePhaseIAPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

	/**
	 * This method is used to determin whether the plugin can deploy the given
	 * ArtifactType to the given InfrastructureNodeType
	 * 
	 * @param ia
	 *            an ImplementationArtifact to deploy
	 * @param infrastructureNodeType
	 *            a NodeType which should be a InfrastructureNodeType
	 * @return true iff this plugin can deploy the given ArtifactTypes to the given
	 *         InfrastructureNodeType
	 */
	public boolean canHandle(AbstractImplementationArtifact ia, AbstractNodeType infrastructureNodeType);

	/**
	 * This method is used add the fragment this plugin can generate for the given
	 * IA which must be deployed unto the given InfrastructureNodeTemplate
	 * 
	 * @param context
	 *            a TemplatePlanContext of the Template for which the fragment has
	 *            to be generated
	 * @param ia
	 *            an ImplementationArtifact of the Template inside the context
	 * @param infrastructureNodeTemplate
	 *            an InfrastructureNodeTemplate of the template inside the context
	 * @return true iff generating and adding the fragment was successful
	 */
	public boolean handle(T context, AbstractImplementationArtifact ia,
			AbstractNodeTemplate infrastructureNodeTemplate);
}
