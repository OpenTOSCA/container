package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;

public class LifecyclePatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo) {

        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, context.getCsar());
        nodesForMatching = this.filterForNodesInCreation(context, nodesForMatching);

        TOperation op = null;
        boolean result = true;

        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        return result;
    }

    public boolean handleTerminate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo) {

        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, context.getCsar());

        TOperation op = null;
        boolean result = true;

        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching, elementToAppendTo);
        }

        return result;
    }

    public boolean handleUpdate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo, Csar csar) {

        TInterface iface = null;
        for (final TInterface ifacei : nodeTemplate.getType().getInterfaces()) {
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

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        return invokeWithMatching(context, nodeTemplate, iface, updateOperation, nodesForMatching, elementToAppendTo);
    }

    private boolean isImplementedAsScript(TInterface iface, TOperation op,
                                          AbstractNodeTemplate nodeTemplate) {
        for (AbstractNodeTypeImplementation impl : nodeTemplate.getImplementations()) {
            for (AbstractImplementationArtifact implArtifact : impl.getImplementationArtifacts()) {
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
    private boolean checkForRunScriptAndTransferFile(AbstractNodeTemplate nodeTemplate, Csar csar) {

        Set<AbstractNodeTemplate> nodeTemplates = this.getNodesForMatching(nodeTemplate, csar);

        boolean foundRunScript = false;
        boolean foundTransferFile = false;
        for (AbstractNodeTemplate node : nodeTemplates) {
            for (TInterface iface : node.getType().getInterfaces()) {
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

    private Set<AbstractNodeTemplate> getNodesForMatching(AbstractNodeTemplate nodeTemplate, Csar csar) {
        Set<AbstractNodeTemplate> nodesForMatching = new HashSet<AbstractNodeTemplate>();

        nodesForMatching.add(nodeTemplate);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.dependsOnRelationType, nodesForMatching, csar);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.hostedOnRelationType, nodesForMatching, csar);
        return nodesForMatching;
    }

    private Set<AbstractNodeTemplate> filterForNodesInCreation(BPELPlanContext context, Set<AbstractNodeTemplate> nodes) {
        Set<AbstractNodeTemplate> result = new HashSet<AbstractNodeTemplate>();
        Collection<AbstractNodeTemplate> nodesInCreation = context.getNodesInCreation();

        for (AbstractNodeTemplate node : nodes) {
            if (nodesInCreation.contains(node)) {
                result.add(node);
            }
        }

        return result;
    }

    public boolean isProvisionableByLifecyclePattern(final AbstractNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return false;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate);

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate);

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate);

        if (hasScriptImplementation) {
            return this.checkForRunScriptAndTransferFile(nodeTemplate, csar);
        }

        return true;
    }

    public boolean isDeprovisionableByLifecyclePattern(final AbstractNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return false;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate);

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            return false;
        }

        hasScriptImplementation |= this.isImplementedAsScript(iface, op, nodeTemplate);

        if (hasScriptImplementation) {
            return this.checkForRunScriptAndTransferFile(nodeTemplate, csar);
        }

        return true;
    }

    public Collection<AbstractNodeTemplate> getMatchedNodesForProvisioning(AbstractNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return null;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
        if (((op = this.getLifecyclePatternInstallMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternConfigureMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternStartMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }
        return nodesForMatching;
    }

    public Collection<AbstractNodeTemplate> getMatchedNodesForDeprovisioning(AbstractNodeTemplate nodeTemplate, Csar csar) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return null;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate, csar);

        // check if the lifecycle operations can be matched against the nodes
        TOperation op = null;
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate)) != null)
            && !hasCompleteMatching(nodesForMatching, iface, op)) {
            OperationMatching matching = this.createPropertyToParameterMatching(nodesForMatching, iface, op);
            nodesForMatching.addAll(matching.matchedNodes);
        }

        return nodesForMatching;
    }

    private boolean hasLifecycleProvisioningMethods(AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternInstallMethod(nodeTemplate) != null
            || this.getLifecyclePatternConfigureMethod(nodeTemplate) != null
            || this.getLifecyclePatternStartMethod(nodeTemplate) != null;
    }

    protected TInterface getLifecyclePatternInterface(final AbstractNodeTemplate nodeTemplate) {
        for (final TInterface iface : nodeTemplate.getType().getInterfaces()) {
            switch (iface.getName()) {
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE:
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE2:
                case Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE3:
                    return iface;
            }
        }
        return null;
    }

    protected TOperation getLifecyclePatternStartMethod(final AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START);
    }

    protected TOperation getLifecyclePatternInstallMethod(final AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_INSTALL);
    }

    protected TOperation getLifecyclePatternConfigureMethod(final AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_CONFIGURE);
    }

    protected TOperation getLifecyclePatternStopMethod(final AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternMethod(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP);
    }

    protected TOperation getLifecyclePatternUninstallMethod(final AbstractNodeTemplate nodeTemplate) {
        return this.getLifecyclePatternMethod(nodeTemplate,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_UNINSTALL);
    }

    private TOperation getLifecyclePatternMethod(AbstractNodeTemplate nodeTemplate, String lifecycleMethod) {
        TInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
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
