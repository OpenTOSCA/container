package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public class LifecyclePatternBasedHandler extends PatternBasedHandler {

	public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

		AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);

		Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

		AbstractOperation op = null;
		boolean result = true;
	
		if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate)) != null)
				&& hasCompleteMatching(nodesForMatching, iface, op)) {			
			result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching);
		}

		if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate)) != null)
				&& hasCompleteMatching(nodesForMatching, iface, op)) {			
			result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching);
		}

		if (((op = this.getLifecyclePatternStartMethod(nodeTemplate)) != null)
				&& hasCompleteMatching(nodesForMatching, iface, op)) {			
			result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching);
		}

		return result;
	}

	private Set<AbstractNodeTemplate> getNodesForMatching(AbstractNodeTemplate nodeTemplate) {
		Set<AbstractNodeTemplate> nodesForMatching = new HashSet<AbstractNodeTemplate>();
		
		nodesForMatching.add(nodeTemplate);
		ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.dependsOnRelationType, nodesForMatching);
		ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.hostedOnRelationType, nodesForMatching);
		return nodesForMatching;
	}

	public boolean isProvisionableByLifecyclePattern(final AbstractNodeTemplate nodeTemplate) {

		if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
			return false;
		}

		Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

		// check if the lifecycle operations can be matched against the nodes
		AbstractOperation op = null;
		AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
		if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate)) != null)
				&& !hasCompleteMatching(nodesForMatching, iface, op)) {
			return false;
		}

		if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate)) != null)
				&& !hasCompleteMatching(nodesForMatching, iface, op)) {
			return false;
		}

		if (((op = this.getLifecyclePatternStartMethod(nodeTemplate)) != null)
				&& !hasCompleteMatching(nodesForMatching, iface, op)) {
			return false;
		}

		return true;
	}

	private boolean hasLifecycleProvisioningMethods(AbstractNodeTemplate nodeTemplate) {
		if (this.getLifecyclePatternInstallMethod(nodeTemplate) != null
				|| this.getLifecyclePatternConfigureMethod(nodeTemplate) != null
				|| this.getLifecyclePatternStartMethod(nodeTemplate) != null) {
			return true;
		} else {
			return false;
		}
	}

	private AbstractInterface getLifecyclePatternInterface(final AbstractNodeTemplate nodeTemplate) {
		for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
			if (iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE)) {
				return iface;
			}
		}
		return null;
	}

	private AbstractOperation getLifecyclePatternStartMethod(final AbstractNodeTemplate nodeTemplate) {
		return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START);
	}


	private AbstractOperation getLifecyclePatternInstallMethod(final AbstractNodeTemplate nodeTemplate) {
		return this.getLifecyclePatternMethod(nodeTemplate,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_INSTALL);
	}


	private AbstractOperation getLifecyclePatternConfigureMethod(final AbstractNodeTemplate nodeTemplate) {
		return this.getLifecyclePatternMethod(nodeTemplate,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_CONFIGURE);
	}

	private AbstractOperation getLifecyclePatternStopMethod(final AbstractNodeTemplate nodeTemplate) {
		return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP);
	}
	private AbstractOperation getLifecyclePatternUninstallMethod(final AbstractNodeTemplate nodeTemplate) {
		return this.getLifecyclePatternMethod(nodeTemplate,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_UNINSTALL);
	}
	
	private AbstractOperation getLifecyclePatternMethod(AbstractNodeTemplate nodeTemplate, String lifecycleMethod) {
		AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
		if (iface != null) {
			for (final AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(lifecycleMethod)) {
					return op;
				}
			}
		}
		return null;
	}
}
