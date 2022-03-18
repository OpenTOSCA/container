package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;

import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;

/**
 * <p>
 * This Class represents mappings from IA's to InfrastructureNodes with PrePhaseIAPlugins
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
class IANodeTypeImplCandidate {

    TNodeTypeImplementation nodeImpl;
    List<TImplementationArtifact> ias = new ArrayList<>();
    List<TNodeTemplate> infraNodes = new ArrayList<>();
    List<IPlanBuilderPrePhaseIAPlugin> plugins = new ArrayList<>();

    private TRelationshipTypeImplementation relationImpl;

    /**
     * Constructor for a NodeTypeImplementation
     *
     * @param impl a TNodeTypeImplementation which should be used for provisioning
     */
    IANodeTypeImplCandidate(final TNodeTypeImplementation impl) {
        this.nodeImpl = impl;
    }

    /**
     * Constructor for a RelationshipTypeImplementation
     *
     * @param impl a TRelationshipTypeImplementation which should be used for provisioning
     */
    IANodeTypeImplCandidate(final TRelationshipTypeImplementation impl) {
        this.relationImpl = impl;
    }

    /**
     * Adds a mapping from IA to InfrastructureNode with a PrePhaseIAPlugin
     *
     * @param ia           the IA to deploy
     * @param nodeTemplate the InfrastructureNode to deploy the IA on
     * @param plugin       the PrePhaseIAPlugin which can deploy the IA unto the InfrastructureNode
     */
    void add(final TImplementationArtifact ia, final TNodeTemplate nodeTemplate,
             final IPlanBuilderPrePhaseIAPlugin plugin) {

        for (final TImplementationArtifact candidateIa : this.ias) {
            if (candidateIa.equals(ia)) {
                return;
            }
        }
        this.ias.add(ia);
        this.infraNodes.add(nodeTemplate);
        this.plugins.add(plugin);
    }

    /**
     * Checks whether all IA's can be deployed of Implementation
     *
     * @return true if all IA's can be deployed, else false
     */
    boolean isValid() {
        if (this.nodeImpl != null) {

            for (final TImplementationArtifact ia : this.nodeImpl.getImplementationArtifacts()) {
                boolean matched = false;
                for (final TImplementationArtifact handledIa : this.ias) {
                    if (ia.equals(handledIa)) {
                        matched = true;
                    }
                }
                if (!matched) {
                    return false;
                }
            }

            return true;
        } else {

            for (final TImplementationArtifact ia : this.relationImpl.getImplementationArtifacts()) {
                boolean matched = false;
                for (final TImplementationArtifact handledIa : this.ias) {
                    if (ia.equals(handledIa)) {
                        matched = true;
                    }
                }
                if (!matched) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Checks whether all IA's can be deployed of Implementation
     *
     * @return true if all IA's can be deployed, else false
     */
    boolean isValid(final String interfaceName, final String operationName) {
        if (this.nodeImpl != null) {

            for (final TImplementationArtifact ia : this.nodeImpl.getImplementationArtifacts()) {
                if (ia.getInterfaceName() != interfaceName) {
                    continue;
                }
                if (ia.getOperationName() != null && ia.getOperationName() != operationName) {
                    continue;
                }
                boolean matched = false;
                for (final TImplementationArtifact handledIa : this.ias) {
                    if (ia.equals(handledIa)) {
                        matched = true;
                    }
                }
                if (!matched) {
                    return false;
                }
            }

            return true;
        } else {

            for (final TImplementationArtifact ia : this.relationImpl.getImplementationArtifacts()) {
                if (ia.getInterfaceName() != interfaceName) {
                    continue;
                }
                if (ia.getOperationName() != null && ia.getOperationName() != operationName) {
                    continue;
                }
                boolean matched = false;
                for (final TImplementationArtifact handledIa : this.ias) {
                    if (ia.equals(handledIa)) {
                        matched = true;
                    }
                }
                if (!matched) {
                    return false;
                }
            }

            return true;
        }
    }
}
