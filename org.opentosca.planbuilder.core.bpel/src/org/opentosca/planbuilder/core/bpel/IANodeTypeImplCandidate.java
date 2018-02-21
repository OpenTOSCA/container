package org.opentosca.planbuilder.core.bpel;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;

/**
 * <p>
 * This Class represents mappings from IA's to InfrastructureNodes with PrePhaseIAPlugins
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
class IANodeTypeImplCandidate {

    AbstractNodeTypeImplementation nodeImpl;
    private AbstractRelationshipTypeImplementation relationImpl;
    List<AbstractImplementationArtifact> ias = new ArrayList<>();
    List<AbstractNodeTemplate> infraNodes = new ArrayList<>();
    List<IPlanBuilderPrePhaseIAPlugin> plugins = new ArrayList<>();

    /**
     * Constructor for a NodeTypeImplementation
     *
     * @param impl a AbstractNodeTypeImplementation which should be used for provisioning
     */
    IANodeTypeImplCandidate(final AbstractNodeTypeImplementation impl) {
        this.nodeImpl = impl;
    }

    /**
     * Constructor for a RelationshipTypeImplementation
     *
     * @param impl a AbstractRelationshipTypeImplementation which should be used for provisioning
     */
    IANodeTypeImplCandidate(final AbstractRelationshipTypeImplementation impl) {
        this.relationImpl = impl;
    }

    /**
     * Adds a mapping from IA to InfrastructureNode with a PrePhaseIAPlugin
     *
     * @param ia the IA to deploy
     * @param nodeTemplate the InfrastructureNode to deploy the IA on
     * @param plugin the PrePhaseIAPlugin which can deploy the IA unto the InfrastructureNode
     */
    void add(final AbstractImplementationArtifact ia, final AbstractNodeTemplate nodeTemplate,
                    final IPlanBuilderPrePhaseIAPlugin plugin) {

        for (final AbstractImplementationArtifact candidateIa : this.ias) {
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

            for (final AbstractImplementationArtifact ia : this.nodeImpl.getImplementationArtifacts()) {
                boolean matched = false;
                for (final AbstractImplementationArtifact handledIa : this.ias) {
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

            for (final AbstractImplementationArtifact ia : this.relationImpl.getImplementationArtifacts()) {
                boolean matched = false;
                for (final AbstractImplementationArtifact handledIa : this.ias) {
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

            for (final AbstractImplementationArtifact ia : this.nodeImpl.getImplementationArtifacts()) {
                if (ia.getInterfaceName() != interfaceName) {
                    continue;
                }
                if (ia.getOperationName() != null && ia.getOperationName() != operationName) {
                    continue;
                }
                boolean matched = false;
                for (final AbstractImplementationArtifact handledIa : this.ias) {
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

            for (final AbstractImplementationArtifact ia : this.relationImpl.getImplementationArtifacts()) {
                if (ia.getInterfaceName() != interfaceName) {
                    continue;
                }
                if (ia.getOperationName() != null && ia.getOperationName() != operationName) {
                    continue;
                }
                boolean matched = false;
                for (final AbstractImplementationArtifact handledIa : this.ias) {
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
