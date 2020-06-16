package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;

public class ContainerPatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo) {

        final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate);

        final AbstractInterface iface = getContainerPatternInterface(hostingContainer);
        final AbstractOperation createOperation = getContainerPatternCreateMethod(hostingContainer);

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate);

        return invokeWithMatching(context, hostingContainer, iface, createOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean handleTerminate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo) {

        final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate);

        final AbstractInterface iface = getContainerPatternInterface(hostingContainer);
        final AbstractOperation terminateOperation = getContainerPatternTerminateMethod(hostingContainer);

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate);

        return invokeWithMatching(context, hostingContainer, iface, terminateOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean isProvisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate) {
        // find hosting node
        final AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternCreateMethod(hostingNode)) {
            return false;
        }

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode),
                                   getContainerPatternCreateMethod(hostingNode));
    }

    public boolean isDeprovisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate) {
        // find hosting node
        final AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate);
        if (Objects.isNull(hostingNode)) {
            return false;
        }

        if (!hasContainerPatternTerminateMethod(hostingNode)) {
            return false;
        }

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate);

        return hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode),
                                   getContainerPatternTerminateMethod(hostingNode));
    }
        

    private boolean hasContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(getContainerPatternCreateMethod(nodeTemplate));
    }

    private boolean hasContainerPatternTerminateMethod(final AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(getContainerPatternTerminateMethod(nodeTemplate));
    }

    protected AbstractOperation getContainerPatternTerminateMethod(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_TERMINATE)) {
                        return op;
                    }
                }
            }
            // backwards compatibility
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER)) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM)) {
                        return op;
                    }
                }
            }
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE)) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER)) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    protected AbstractOperation getContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_CREATE)) {
                        return op;
                    }
                }
            }
            // possible backwards compatibility through interfaces/operations of Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER and Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE      
        }
        return null;
    }

    private AbstractInterface getContainerPatternInterface(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
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

    private Set<AbstractNodeTemplate> calculateNodesForMatching(final AbstractNodeTemplate nodeTemplate) {
        final Set<AbstractNodeTemplate> nodesForMatching = new HashSet<>();
        nodesForMatching.add(nodeTemplate);

        AbstractNodeTemplate hostingNode = getHostingNode(nodeTemplate);
        while (Objects.nonNull(hostingNode)) {
            nodesForMatching.add(hostingNode);
            hostingNode = getHostingNode(hostingNode);
        }

        return nodesForMatching;
    }

    protected AbstractNodeTemplate getHostingNode(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractRelationshipTemplate rel : nodeTemplate.getOutgoingRelations()) {
            for (final QName typeInHierarchy : ModelUtils.getRelationshipTypeHierarchy(rel.getRelationshipType())) {
                if (ModelUtils.isInfrastructureRelationshipType(typeInHierarchy)) {
                    return rel.getTarget();
                }
            }
        }
        return null;
    }
}
