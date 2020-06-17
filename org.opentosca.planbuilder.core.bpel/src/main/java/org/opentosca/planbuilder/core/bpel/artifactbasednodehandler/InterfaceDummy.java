package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 * As some IAs may implement a whole interface we mock the matching of these kind of IAs with this dummy class
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
class InterfaceDummy extends AbstractOperation {

    private final AbstractImplementationArtifact ia;
    private final AbstractNodeTemplate nodeTemplate;

    public InterfaceDummy(final AbstractNodeTemplate nodeTemplate, final AbstractImplementationArtifact ia) {
        this.ia = ia;
        this.nodeTemplate = nodeTemplate;
    }

    public AbstractOperation getOperation(final String opName) {
        for (final AbstractInterface iface : this.nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals(this.ia.getInterfaceName())) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(opName)) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    public List<String> getOperationNames() {
        for (final AbstractInterface iface : this.nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals(this.ia.getInterfaceName())) {
                final List<String> opNames = new ArrayList<>();
                for (final AbstractOperation op : iface.getOperations()) {
                    opNames.add(op.getName());
                }
                return opNames;
            }
        }
        return new ArrayList<>();
    }

    public AbstractNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }

    public AbstractImplementationArtifact getIA() {
        return this.ia;
    }

    @Override
    public String getName() {
        return this.ia.getInterfaceName();
    }

    @Override
    public List<AbstractParameter> getInputParameters() {
        return null;
    }

    @Override
    public List<AbstractParameter> getOutputParameters() {
        return null;
    }

    @Override
    public AbstractInterface getInterface() {
        // TODO Auto-generated method stub
        return null;
    }

}
