package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public class ContainerPatternBasedHandler extends PatternBasedHandler {

	public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

		final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate);

		final AbstractInterface iface = getContainerPatternInterface(hostingContainer);
		final AbstractOperation createOperation = getContainerPatternCreateMethod(hostingContainer);

		Set<AbstractNodeTemplate> nodesForMatching = new HashSet<AbstractNodeTemplate>();
		nodesForMatching.add(nodeTemplate);
		nodesForMatching.add(hostingContainer);
				
		return invokeWithMatching(context, hostingContainer, iface, createOperation, nodesForMatching);		
	}

	public boolean isProvisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate) {
		// find hosting node
		AbstractNodeTemplate hostingNode = null;
		if ((hostingNode = getHostingNode(nodeTemplate)) == null) {
			return false;
		}

		if (!hasContainerPatternCreateMethod(hostingNode)) {
			return false;
		}

		Set<AbstractNodeTemplate> nodesForMatching = new HashSet<AbstractNodeTemplate>();
		nodesForMatching.add(nodeTemplate);
		nodesForMatching.add(hostingNode);

		if (!hasCompleteMatching(nodesForMatching, getContainerPatternInterface(hostingNode),
				getContainerPatternCreateMethod(hostingNode))) {
			return false;
		}

		return true;
	}

	private boolean hasContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
		if (getContainerPatternCreateMethod(nodeTemplate) != null) {
			return true;
		} else {
			return false;
		}
	}

	private AbstractOperation getContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
			if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN)) {
				for (final AbstractOperation op : iface.getOperations()) {
					if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONTAINERPATTERN_CREATE)) {
						return op;
					}
				}
			}
			// backwards compatibility
			if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER)) {
				for (final AbstractOperation op : iface.getOperations()) {
					if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_CREATEVM)) {
						return op;
					}
				}
			}
			if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE)) {
				for (final AbstractOperation op : iface.getOperations()) {
					if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER)) {
						return op;
					}
				}
			}
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

	private AbstractNodeTemplate getHostingNode(final AbstractNodeTemplate nodeTemplate) {
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
