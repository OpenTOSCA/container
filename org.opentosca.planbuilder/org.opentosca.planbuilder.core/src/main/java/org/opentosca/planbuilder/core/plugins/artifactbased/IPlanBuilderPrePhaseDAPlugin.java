package org.opentosca.planbuilder.core.plugins.artifactbased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;

/**
 * <p>
 * This interface should be implemented by Plugins which are responsible for deploying DA's inside a PrePhase of a
 * TemplateBuildPlan. The deployment should be handle on an appropiate InfrastructureNode
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPrePhaseDAPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method is used to determine whether this plugin can deploy a DA based on its type to a Node of the given
     * type.
     *
     * @param deploymentArtifact     the DA to deploy
     * @param infrastructureNodeType the NodeType of an InfrastructureNode
     * @return true iff when this plugin can deploy a DA of the given artifactType to a Node of the given nodeType
     */
    boolean canHandle(T context, AbstractDeploymentArtifact deploymentArtifact, AbstractNodeType infrastructureNodeType);

    /**
     * This method is used to add a fragment to a prephase of the nodeTemplate declared inside the given context. The
     * fragment should deploy the given DA unto the given InfrastructureNode
     *
     * @param context                    a TemplatePlanContext of the Template which a Provisioning has to be created
     * @param da                         the DeploymentArtifact to deploy
     * @param infrastructureNodeTemplate the InfrastructureNodeTemplate to deploy the DA on
     * @return true iff generating and adding the fragment to the PrePhase of the TemplateContext was successful
     */
    boolean handle(T context, AbstractDeploymentArtifact da, AbstractNodeTemplate infrastructureNodeTemplate);

    boolean canHandleCreate(T context, AbstractNodeTemplate nodeTemplate);

    boolean handleCreate(T context, AbstractNodeTemplate nodeTemplate);
}
