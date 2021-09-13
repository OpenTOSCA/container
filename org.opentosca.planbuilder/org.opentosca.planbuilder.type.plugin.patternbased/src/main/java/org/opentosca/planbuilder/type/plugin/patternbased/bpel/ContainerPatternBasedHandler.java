package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.springframework.ui.Model;
import org.w3c.dom.Element;

public class ContainerPatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        final TNodeTemplate hostingContainer = getHostingNode(nodeTemplate, csar);

        final TInterface iface = getContainerPatternInterface(hostingContainer, csar);
        final TOperation createOperation = getContainerPatternCreateMethod(hostingContainer, csar);

        final Set<TNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, hostingContainer, iface, createOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean handleTerminate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        final TNodeTemplate hostingContainer = getHostingNode(nodeTemplate, csar);

        final TInterface iface = getContainerPatternInterface(hostingContainer, csar);
        final TOperation terminateOperation = getContainerPatternTerminateMethod(hostingContainer, csar);

        final Set<TNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, hostingContainer, iface, terminateOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean isProvisionableByContainerPattern(final TNodeTemplate nodeTemplate, Csar csar) {
        // find hosting node
        final TNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternCreateMethod(hostingNode, csar)) {
            return false;
        }

        final Set<TNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode, csar),
            getContainerPatternCreateMethod(hostingNode, csar));
    }

    public boolean isDeprovisionableByContainerPattern(final TNodeTemplate nodeTemplate, Csar csar) {
        // find hosting node
        final TNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternTerminateMethod(hostingNode, csar)) {
            return false;
        }

        final Set<TNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode, csar),
            getContainerPatternTerminateMethod(hostingNode, csar));
    }

    private boolean hasContainerPatternCreateMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return Objects.nonNull(getContainerPatternCreateMethod(nodeTemplate, csar));
    }

    private boolean hasContainerPatternTerminateMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return Objects.nonNull(getContainerPatternTerminateMethod(nodeTemplate, csar));
    }

    protected TOperation getContainerPatternTerminateMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TInterface iface : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_TERMINATE)) {
                        return op;
                    }
                }
            }
            // backwards compatibility
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER)) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM)) {
                        return op;
                    }
                }
            }
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE)) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER)) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    protected TOperation getContainerPatternCreateMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TInterface iface : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_CREATE)) {
                        return op;
                    }
                }
            }
            // possible backwards compatibility through interfaces/operations of Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER and Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE
        }
        return null;
    }

    private TInterface getContainerPatternInterface(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TInterface iface : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
                return iface;
            }
            // backwards compatibility
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER)) {
                return iface;
            }
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE)) {
                return iface;
            }
        }
        return null;
    }

    private Set<TNodeTemplate> calculateNodesForMatching(final TNodeTemplate nodeTemplate, Csar csar) {
        final Set<TNodeTemplate> nodesForMatching = new HashSet<>();
        nodesForMatching.add(nodeTemplate);

        TNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        while (Objects.nonNull(hostingNode)) {
            nodesForMatching.add(hostingNode);
            hostingNode = getHostingNode(hostingNode, csar);
        }

        return nodesForMatching;
    }

    protected TNodeTemplate getHostingNode(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TRelationshipTemplate rel : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            for (final QName typeInHierarchy : ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(rel, csar), csar)) {
                if (ModelUtils.isInfrastructureRelationshipType(typeInHierarchy)) {
                    return ModelUtils.getTarget(rel, csar);
                }
            }
        }
        return null;
    }
}
