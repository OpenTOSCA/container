package org.opentosca.planbuilder.core.plugins.artifactbased;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;

/**
 * <p>
 * This interface should be implemented by Plugin which can add PrePhase Fragments for IA's. The plugin should be able
 * to deploy at least one ArtifactType to a specific NodeType
 * <p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderBPMNPrePhaseIAPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method is used to determin whether the plugin can deploy the given ArtifactType to the given
     * InfrastructureNodeType
     *
     * @param ia                     an ImplementationArtifact to deploy
     * @param infrastructureNodeType a NodeType which should be a InfrastructureNodeType
     * @return true iff this plugin can deploy the given ArtifactTypes to the given InfrastructureNodeType
     */
    boolean canHandle(T context, TImplementationArtifact ia, TNodeType infrastructureNodeType);

    /**
     * This method is used add the fragment this plugin can generate for the given IA which must be deployed unto the
     * given InfrastructureNodeTemplate
     *
     * @param context                    a TemplatePlanContext of the Template for which the fragment has to be
     *                                   generated
     * @param ia                         an ImplementationArtifact of the Template inside the context
     * @param infrastructureNodeTemplate an InfrastructureNodeTemplate of the template inside the context
     * @return true iff generating and adding the fragment was successful
     */
    boolean handle(T context, TImplementationArtifact ia,
                   TNodeTemplate infrastructureNodeTemplate);
}
