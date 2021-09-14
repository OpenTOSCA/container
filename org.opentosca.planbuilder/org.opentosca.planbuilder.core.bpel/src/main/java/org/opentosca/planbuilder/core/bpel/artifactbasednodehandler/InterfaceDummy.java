package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * As some IAs may implement a whole interface we mock the matching of these kind of IAs with this dummy class
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
class InterfaceDummy extends TOperation {

    private final TImplementationArtifact ia;
    private final TNodeTemplate nodeTemplate;
    private final Csar csar;

    public InterfaceDummy(final TNodeTemplate nodeTemplate, final TImplementationArtifact ia, Csar csar) {
        this.ia = ia;
        this.nodeTemplate = nodeTemplate;
        this.csar = csar;
    }

    public TOperation getOperation(final String opName) {
        for (final TInterface iface : ModelUtils.findNodeType(this.nodeTemplate, this.csar).getInterfaces()) {
            if (iface.getName().equals(this.ia.getInterfaceName())) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(opName)) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    public List<String> getOperationNames() {
        for (final TInterface iface : ModelUtils.findNodeType(this.nodeTemplate, this.csar).getInterfaces()) {
            if (iface.getName().equals(this.ia.getInterfaceName())) {
                final List<String> opNames = new ArrayList<>();
                for (final TOperation op : iface.getOperations()) {
                    opNames.add(op.getName());
                }
                return opNames;
            }
        }
        return new ArrayList<>();
    }

    public TNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }

    public TImplementationArtifact getIA() {
        return this.ia;
    }

    @Override
    public String getName() {
        return this.ia.getInterfaceName();
    }

    @Override
    public List<TParameter> getInputParameters() {
        return null;
    }

    @Override
    public List<TParameter> getOutputParameters() {
        return null;
    }
}
