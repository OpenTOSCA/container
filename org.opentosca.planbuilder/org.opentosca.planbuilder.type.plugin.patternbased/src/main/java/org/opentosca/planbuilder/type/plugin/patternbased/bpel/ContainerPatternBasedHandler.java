package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;

public class ContainerPatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate, csar);

        final TInterface iface = getContainerPatternInterface(hostingContainer);
        final TOperation createOperation = getContainerPatternCreateMethod(hostingContainer);

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, hostingContainer, iface, createOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean handleTerminate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate, csar);

        final TInterface iface = getContainerPatternInterface(hostingContainer);
        final TOperation terminateOperation = getContainerPatternTerminateMethod(hostingContainer);

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, hostingContainer, iface, terminateOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean isProvisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate, Csar csar) {
        // find hosting node
        final AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternCreateMethod(hostingNode)) {
            return false;
        }

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode),
            getContainerPatternCreateMethod(hostingNode));
    }

    public boolean isDeprovisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate, Csar csar) {
        // find hosting node
        final AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternTerminateMethod(hostingNode)) {
            return false;
        }

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, csar);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode),
            getContainerPatternTerminateMethod(hostingNode));
    }

    private boolean hasContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(getContainerPatternCreateMethod(nodeTemplate));
    }

    private boolean hasContainerPatternTerminateMethod(final AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(getContainerPatternTerminateMethod(nodeTemplate));
    }

    protected TOperation getContainerPatternTerminateMethod(final AbstractNodeTemplate nodeTemplate) {
        for (final TInterface iface : nodeTemplate.getType().getInterfaces()) {
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

    protected TOperation getContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        for (final TInterface iface : nodeTemplate.getType().getInterfaces()) {
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

    private TInterface getContainerPatternInterface(final AbstractNodeTemplate nodeTemplate) {
        for (final TInterface iface : nodeTemplate.getType().getInterfaces()) {
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

    private Set<AbstractNodeTemplate> calculateNodesForMatching(final AbstractNodeTemplate nodeTemplate, Csar csar) {
        final Set<AbstractNodeTemplate> nodesForMatching = new HashSet<>();
        nodesForMatching.add(nodeTemplate);

        AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate, csar);
        while (Objects.nonNull(hostingNode)) {
            nodesForMatching.add(hostingNode);
            hostingNode = getHostingNode(hostingNode, csar);
        }

        return nodesForMatching;
    }

    protected AbstractNodeTemplate getHostingNode(final AbstractNodeTemplate nodeTemplate, Csar csar) {
        for (final AbstractRelationshipTemplate rel : nodeTemplate.getOutgoingRelations()) {
            for (final QName typeInHierarchy : ModelUtils.getRelationshipTypeHierarchy(rel.getRelationshipType(), csar)) {
                if (ModelUtils.isInfrastructureRelationshipType(typeInHierarchy)) {
                    return rel.getTarget();
                }
            }
        }
        return null;
    }
}
