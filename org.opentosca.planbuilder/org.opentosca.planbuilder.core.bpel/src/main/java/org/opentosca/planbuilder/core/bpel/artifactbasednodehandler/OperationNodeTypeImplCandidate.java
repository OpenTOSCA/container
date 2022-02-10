package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.container.core.model.ModelUtils;

/**
 * <p>
 * This Class is a wrapper for operations that provision a particular template. This is realized by a mapping between
 * operations, IA's and ProvPhasePlugins
 * </p>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
class OperationNodeTypeImplCandidate {

    // lists which hold the various data, the mapping is enforced with the
    // positions inside the lists
    List<TOperation> ops = new ArrayList<>();
    List<TImplementationArtifact> ias = new ArrayList<>();
    List<IPlanBuilderProvPhaseOperationPlugin<?>> plugins = new ArrayList<>();

    /**
     * <p>
     * Adds a mapping for a operation, an IA and ProvPhasePlugin
     * </p>
     *
     * @param op     an AbstractOperation of Template
     * @param ia     an TImplementationArtifact which implements the given operation
     * @param plugin a ProvPhasePlugin that can execute on the given Operation and ImplementationArtifact
     */
    void add(final TOperation op, final TImplementationArtifact ia,
             final IPlanBuilderProvPhaseOperationPlugin<?> plugin) {
        this.ops.add(op);
        this.ias.add(ia);
        this.plugins.add(plugin);
    }

    /**
     * <p>
     * Checks if any Interface of the given NodeTemplate can be executed completely by this ProvisioningCandidate
     * </p>
     *
     * @param nodeTemplate an AbtractNodeTemplate
     * @return true if all Interfaces of the NodeTemplate can be provisioned, else false
     */
    boolean isValid(final TNodeTemplate nodeTemplate, final String interfaceName, final String operationName, Csar csar) {

        for (final TImplementationArtifact ia : this.ias) {
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
                    // we have to find the interface and count the operations in it
                    final TInterface iface = ModelUtils.getInterfaceOfNode(nodeTemplate, ia.getInterfaceName(), csar);
                    if (Objects.nonNull(iface) && iface.getName().equals(ia.getInterfaceName())) {
                        for (final TOperation op : iface.getOperations()) {
                            if (op.getName().equals(operationName)) {
                                return true;
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
     * Checks if any Interface of the given NodeTemplate can be executed completely by this ProvisioningCandidate
     * </p>
     *
     * @param nodeTemplate an AbtractNodeTemplate
     * @return true if all Interfaces of the NodeTemplate can be provisioned, else false
     */
    boolean isValid(final TNodeTemplate nodeTemplate, Csar csar) {
        // calculate the size of implemented operations by the IAs

        int implementedOpsByIAsCount = 0;

        for (final TImplementationArtifact ia : this.ias) {
            if (ia.getInterfaceName() != null) {
                if (ia.getOperationName() != null) {
                    // ia implements some single operation
                    implementedOpsByIAsCount++;
                } else {
                    // we have to find the interface and count the
                    // operations in it
                    List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
                    if (interfaces != null) {
                        for (final TInterface iface : interfaces) {
                            if (iface.getName().equals(ia.getInterfaceName())) {
                                implementedOpsByIAsCount += iface.getOperations().size();
                            }
                        }
                    }
                }
            }
        }

        int operationsToImplementCount = 0;

        for (final TOperation op : this.ops) {
            if (op instanceof InterfaceDummy) {
                final String ifaceName = ((InterfaceDummy) op).getIA().getInterfaceName();
                TNodeType type = ModelUtils.findNodeType(((InterfaceDummy) op).getNodeTemplate(), csar);
                for (final TInterface iface : type.getInterfaces()) {
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

        return this.ias.size() == this.plugins.size() || this.ops.size() == this.plugins.size();
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
     * @param relationshipTemplate an TRelationshipTemplate to check it Interfaces with the Mappings
     * @return true if the Mappings are valid for a Source- or TargetInterface of the given RelationshipTemplate, else
     * false
     */
    boolean isValid(final TRelationshipTemplate relationshipTemplate, Csar csar) {
        BPELScopeBuilder.LOG.debug("Checking if the selected provisioning for relationshipTemplate {}",
            relationshipTemplate.getId());
        BPELScopeBuilder.LOG.debug(" with type {} is valid whether on the source or target interface",
            relationshipTemplate.getType().toString());

        // check if any source interface matches the selected prov plugins
        List<TInterface> sourceInterfaces = ModelUtils.findRelationshipType(relationshipTemplate, csar).getSourceInterfaces();
        if (sourceInterfaces != null) {
            return checkInterfaces(sourceInterfaces);
        }
        // same check for target interfaces
        List<TInterface> targetInterfaces = ModelUtils.findRelationshipType(relationshipTemplate, csar).getTargetInterfaces();
        if (targetInterfaces != null) {
            return checkInterfaces(targetInterfaces);
        }
        return false;
    }

    private boolean checkInterfaces(List<TInterface> sourceInterfaces) {
        for (final TInterface iface : sourceInterfaces) {
            final int interfaceSize = iface.getOperations().size();
            if (interfaceSize == this.ops.size() && interfaceSize == this.ias.size()
                && interfaceSize == this.plugins.size()) {
                int counter = 0;
                for (final TOperation iFaceOp : iface.getOperations()) {
                    for (final TOperation op : this.ops) {
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
