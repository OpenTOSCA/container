package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;

/**
 * <p>
 * This Class is a wrapper for operations that provision a particular template. This is realized by
 * a mapping between operations, IA's and ProvPhasePlugins
 * </p>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
class OperationNodeTypeImplCandidate {

    // lists which hold the various data, the mapping is enforced with the
    // positions inside the lists
    List<AbstractOperation> ops = new ArrayList<>();
    List<AbstractImplementationArtifact> ias = new ArrayList<>();
    List<IPlanBuilderProvPhaseOperationPlugin> plugins = new ArrayList<>();

    /**
     * <p>
     * Adds a mapping for a operation, an IA and ProvPhasePlugin
     * </p>
     *
     * @param op an AbstractOperation of Template
     * @param ia an AbstractImplementationArtifact which implements the given operation
     * @param plugin a ProvPhasePlugin that can execute on the given Operation and
     *        ImplementationArtifact
     */
    void add(final AbstractOperation op, final AbstractImplementationArtifact ia,
             final IPlanBuilderProvPhaseOperationPlugin plugin) {
        this.ops.add(op);
        this.ias.add(ia);
        this.plugins.add(plugin);
    }

    /**
     * <p>
     * Checks if any Interface of the given NodeTemplate can be executed completely by this
     * ProvisioningCandidate
     * </p>
     *
     * @param nodeTemplate an AbtractNodeTemplate
     * @return true if all Interfaces of the NodeTemplate can be provisioned, else false
     */
    boolean isValid(final AbstractNodeTemplate nodeTemplate, final String interfaceName, final String operationName) {

        for (final AbstractImplementationArtifact ia : this.ias) {
            if (ia.getInterfaceName() != null) {
                if (!ia.getInterfaceName().equals(interfaceName)) {
                    continue;
                }
                if (ia.getOperationName() != null && !ia.getOperationName().equals(operationName)) {
                    continue;
                }
                if (ia.getOperationName() != null) {
                    // ia implements some single operation
                    if (ia.getOperationName().equals(operationName)) {
                        return true;
                    }
                } else {
                    // we have to find the interface and count the
                    // operations in it
                    for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
                        if (iface.getName().equals(ia.getInterfaceName())) {
                            for (final AbstractOperation op : iface.getOperations()) {
                                if (op.getName().equals(operationName)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * <p>
     * Checks if any Interface of the given NodeTemplate can be executed completely by this
     * ProvisioningCandidate
     * </p>
     *
     * @param nodeTemplate an AbtractNodeTemplate
     * @return true if all Interfaces of the NodeTemplate can be provisioned, else false
     */
    boolean isValid(final AbstractNodeTemplate nodeTemplate) {
        // calculate the size of implemented operations by the IAs

        int implementedOpsByIAsCount = 0;

        for (final AbstractImplementationArtifact ia : this.ias) {
            if (ia.getInterfaceName() != null) {
                if (ia.getOperationName() != null) {
                    // ia implements some single operation
                    implementedOpsByIAsCount++;
                } else {
                    // we have to find the interface and count the
                    // operations in it
                    for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
                        if (iface.getName().equals(ia.getInterfaceName())) {
                            implementedOpsByIAsCount += iface.getOperations().size();
                        }
                    }
                }
            }
        }

        int operationsToImplementCount = 0;

        for (final AbstractOperation op : this.ops) {
            if (op instanceof InterfaceDummy) {
                final String ifaceName = ((InterfaceDummy) op).getIA().getInterfaceName();
                for (final AbstractInterface iface : ((InterfaceDummy) op).getNodeTemplate().getType()
                                                                          .getInterfaces()) {
                    if (iface.getName().equals(ifaceName)) {
                        operationsToImplementCount += iface.getOperations().size();
                    }
                }
            } else {
                operationsToImplementCount++;
            }
        }

        if (operationsToImplementCount != implementedOpsByIAsCount) {
            return false;
        }

        if (this.ias.size() != this.plugins.size() && this.ops.size() != this.plugins.size()) {
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Checks whether the mapping of operations/IA/ProvPhasePlugin is valid for this.
     * </p>
     * <p>
     * <b>INFO:</b> It is assumed that the selected mappings are either for a TargetInterface or a
     * SourceInterface
     * </p>
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate to check it Interfaces with the
     *        Mappings
     * @return true if the Mappings are valid for a Source- or TargetInterface of the given
     *         RelationshipTemplate, else false
     */
    boolean isValid(final AbstractRelationshipTemplate relationshipTemplate) {
        BPELScopeBuilder.LOG.debug("Checking if the selected provisioning for relationshipTemplate {}",
                                   relationshipTemplate.getId());
        BPELScopeBuilder.LOG.debug(" with type {} is valid whether on the source or target interface",
                                   relationshipTemplate.getRelationshipType().getId().toString());
        // check if any source interface matches the selected prov plugins
        for (final AbstractInterface iface : relationshipTemplate.getRelationshipType().getSourceInterfaces()) {
            final int interfaceSize = iface.getOperations().size();
            if (interfaceSize == this.ops.size() && interfaceSize == this.ias.size()
                && interfaceSize == this.plugins.size()) {
                int counter = 0;
                for (final AbstractOperation iFaceOp : iface.getOperations()) {
                    for (final AbstractOperation op : this.ops) {
                        if (iFaceOp.equals(op)) {
                            counter++;
                        }
                    }
                }
                if (counter == interfaceSize) {
                    return true;
                }
            }
        }
        // same check for target interfaces
        for (final AbstractInterface iface : relationshipTemplate.getRelationshipType().getTargetInterfaces()) {
            final int interfaceSize = iface.getOperations().size();
            if (interfaceSize == this.ops.size() && interfaceSize == this.ias.size()
                && interfaceSize == this.plugins.size()) {
                int counter = 0;
                for (final AbstractOperation iFaceOp : iface.getOperations()) {
                    for (final AbstractOperation op : this.ops) {
                        if (iFaceOp.equals(op)) {
                            counter++;
                        }
                    }
                }
                if (counter == interfaceSize) {
                    return true;
                }
            }
        }
        return false;
    }

}
