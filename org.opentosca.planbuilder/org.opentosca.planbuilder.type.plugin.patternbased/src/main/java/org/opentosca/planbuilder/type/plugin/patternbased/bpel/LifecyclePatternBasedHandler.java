package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.springframework.ui.Model;
import org.w3c.dom.Element;

public class LifecyclePatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo) {

        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, context.getCsar());

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, context.getCsar());
        nodesForMatching = this.filterForNodesInCreation(context, nodesForMatching);

        TOperation op = null;
        boolean result = true;

        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate, context.getCsar())) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate, context.getCsar())) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate, context.getCsar())) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        return result;
    }

    public boolean handleTerminate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo) {

        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, context.getCsar());

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, context.getCsar());

        TOperation op = null;
        boolean result = true;

        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate, context.getCsar())) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate, context.getCsar())) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        return result;
    }

    public boolean handleUpdate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        TInterface iface = null;
        for (final TInterface ifacei : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            if (ifacei.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE)) {
                iface = ifacei;
            }
        }
        if (iface == null) return false;

        TOperation updateOperation = null;
        for (final TOperation op : iface.getOperations()) {
            if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_UPDATE_RUNUPDATE)) {
                updateOperation = op;
            }
        }

        if (updateOperation == null) return false;

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, nodeTemplate, iface, updateOperation, nodesForMatching, elementToAppendTo);
    }

    private boolean isImplementedAsScript(TInterface iface, TOperation op,
                                          TNodeTemplate nodeTemplate, Csar csar) {
        for (TNodeTypeImplementation impl : ModelUtils.findNodeTypeImplementation(nodeTemplate, csar)) {
            for (TImplementationArtifact implArtifact : impl.getImplementationArtifacts()) {
                if (implArtifact.getInterfaceName().equals(iface.getName())) {
                    if (implArtifact.getArtifactType().equals(Types.scriptArtifactType)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // This method looks for runScript and transferFile operation on the hosting infrastructure
    private boolean checkForRunScriptAndTransferFile(TNodeTemplate nodeTemplate, Csar csar) {

        Set<TNodeTemplate> nodeTemplates = this.getNodesForMatching(nodeTemplate, csar);

        boolean foundRunScript = false;
        boolean foundTransferFile = false;
        for (TNodeTemplate node : nodeTemplates) {
            for (TInterface iface : ModelUtils.findNodeType(node, csar).getInterfaces()) {
                for (TOperation op : iface.getOperations()) {
                    if (op.getName().equals("runScript")) {
                        foundRunScript = true;
                    }
                    if (op.getName().equals("transferFile")) {
                        foundTransferFile = true;
                    }
                }
            }
        }

        return foundRunScript & foundTransferFile;
    }

    private Set<TNodeTemplate> getNodesForMatching(TNodeTemplate nodeTemplate, Csar csar) {
        Set<TNodeTemplate> nodesForMatching = new HashSet<TNodeTemplate>();

        nodesForMatching.add(nodeTemplate);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.dependsOnRelationType, nodesForMatching, csar);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.hostedOnRelationType, nodesForMatching, csar);
        return nodesForMatching;
    }

    private Set<TNodeTemplate> filterForNodesInCreation(BPELPlanContext context, Set<TNodeTemplate> nodes) {
        Set<TNodeTemplate> result = new HashSet<TNodeTemplate>();
        Collection<TNodeTemplate> nodesInCreation = context.getNodesInCreation();

        for (TNodeTemplate node : nodes) {
            if (nodesInCreation.contains(node)) {
                result.add(node);
            }
        }

        return result;
    }

    public boolean isProvisionableByLifecyclePattern(final TNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate, csar)) {
            return false;
        }

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, csar);
        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate, csar);

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate, csar);

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate, csar);

        if (hasScriptImplementation) {
            return this.checkForRunScriptAndTransferFile(nodeTemplate, csar);
        }

        return true;
    }

    public boolean isDeprovisionableByLifecyclePattern(final TNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate, csar)) {
            return false;
        }

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, csar);
        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate, csar);

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate, csar);

        if (hasScriptImplementation) {
            return this.checkForRunScriptAndTransferFile(nodeTemplate, csar);
        }

        return true;
    }

    public Collection<TNodeTemplate> getMatchedNodesForProvisioning(TNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate, csar)) {
            return null;
        }

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, csar);
        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }
        return nodesForMatching;
    }

    public Collection<TNodeTemplate> getMatchedNodesForDeprovisioning(TNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate, csar)) {
            return null;
        }

        Set<TNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, csar);
        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate, csar)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        return nodesForMatching;
    }

    private boolean hasLifecycleProvisioningMethods(TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternInstallMethod(nodeTemplate, csar) != null
            || this.getLifecyclePatternConfigureMethod(nodeTemplate, csar) != null
            || this.getLifecyclePatternStartMethod(nodeTemplate, csar) != null;
    }

    protected TInterface getLifecyclePatternInterface(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TInterface iface : ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces()) {
            switch (iface.getName()) {
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE:
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE2:
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE3:
                    return iface;
            }
        }
        return null;
    }

    protected TOperation getLifecyclePatternStartMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, csar);
    }

    protected TOperation getLifecyclePatternInstallMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_INSTALL, csar);
    }

    protected TOperation getLifecyclePatternConfigureMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_CONFIGURE, csar);
    }

    protected TOperation getLifecyclePatternStopMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, csar);
    }

    protected TOperation getLifecyclePatternUninstallMethod(final TNodeTemplate nodeTemplate, Csar csar) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_UNINSTALL, csar);
    }

    private TOperation getLifecyclePatternMethod(TNodeTemplate nodeTemplate, String lifecycleMethod, Csar csar) {
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate, csar);
        if (iface != null) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(lifecycleMethod)) {
                    return op;
                }
            }
        }
        return null;
    }
}
