package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;

/**
 * <p>
 * This Class represents a Mapping of DA's of an Implementation Plugins which can handle that with matching
 * InfrastructureNode
 * </p>
 * <p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
class DANodeTypeImplCandidate {

    final List<TDeploymentArtifact> das = new ArrayList<>();
    final TNodeTypeImplementation impl;
    final List<TNodeTemplate> infraNodes = new ArrayList<>();
    final List<IPlanBuilderPrePhaseDAPlugin<?>> plugins = new ArrayList<>();
    final TNodeTemplate nodeTemplate;

    private final Csar csar;

    /**
     * Constructor determines which NodeTypeImplementation is used
     *
     * @param impl an TNodeTypeImplementation with a DA
     */
    DANodeTypeImplCandidate(final TNodeTemplate nodeTemplate, final TNodeTypeImplementation impl, Csar csar) {
        this.impl = impl;
        this.nodeTemplate = nodeTemplate;
        this.csar = csar;
    }

    /**
     * Adds a mapping from DA to NodeTemplate with a PrePhaseDAPlugin
     *
     * @param da           the DeploymentArtifact which should be provisioned
     * @param nodeTemplate an InfrastructureNode on which the DA should be deployed
     * @param plugin       the PrePhaseDAPlugin which can deploy the DA unto the given NodeTemplate
     */
    void add(final TDeploymentArtifact da, final TNodeTemplate nodeTemplate,
             final IPlanBuilderPrePhaseDAPlugin<?> plugin) {
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
        return ModelUtils.calculateEffectiveDAs(this.nodeTemplate, this.impl, this.csar).size() == this.das.size();
    }
}
