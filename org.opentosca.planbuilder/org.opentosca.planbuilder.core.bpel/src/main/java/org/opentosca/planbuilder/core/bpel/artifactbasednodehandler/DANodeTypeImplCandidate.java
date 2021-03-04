package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;

/**
 * <p>
 * This Class represents a Mapping of DA's of an Implementation Plugins which can handle that with matching
 * InfrastructureNode
 * </p>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
class DANodeTypeImplCandidate {

    private final AbstractNodeTemplate nodeTemplate;
    AbstractNodeTypeImplementation impl;
    List<AbstractDeploymentArtifact> das = new ArrayList<>();
    List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
    List<IPlanBuilderPrePhaseDAPlugin> plugins = new ArrayList<>();

    /**
     * Constructor determines which NodeTypeImplementation is used
     *
     * @param impl an AbstractNodeTypeImplementation with a DA
     */
    DANodeTypeImplCandidate(final AbstractNodeTemplate nodeTemplate, final AbstractNodeTypeImplementation impl) {
        this.impl = impl;
        this.nodeTemplate = nodeTemplate;
    }

    /**
     * Adds a mapping from DA to NodeTemplate with a PrePhaseDAPlugin
     *
     * @param da           the DeploymentArtifact which should be provisioned
     * @param nodeTemplate an InfrastructureNode on which the DA should be deployed
     * @param plugin       the PrePhaseDAPlugin which can deploy the DA unto the given NodeTemplate
     */
    void add(final AbstractDeploymentArtifact da, final AbstractNodeTemplate nodeTemplate,
             final IPlanBuilderPrePhaseDAPlugin plugin) {
        this.das.add(da);
        this.infraNodes.add(nodeTemplate);
        this.plugins.add(plugin);
    }

    /**
     * Checks whether the mappings are valid
     *
     * @return true if all DA's of the NodeTypeImplementation can be deployed, else false
     */
    boolean isValid() {
        return BPELScopeBuilder.calculateEffectiveDAs(this.nodeTemplate, this.impl).size() == this.das.size();
    }
}
