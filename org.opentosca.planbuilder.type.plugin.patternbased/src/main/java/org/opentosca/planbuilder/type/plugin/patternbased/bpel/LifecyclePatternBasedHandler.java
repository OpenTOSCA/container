package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public class LifecyclePatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);
        nodesForMatching = this.filterForNodesInCreation(context, nodesForMatching);
        

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

    public boolean handleTerminate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

        AbstractOperation op = null;
        boolean result = true;

        if (((op = this.getLifecyclePatternStopMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching);
        }

        if (((op = this.getLifecyclePatternUninstallMethod(nodeTemplate)) != null)
            && hasCompleteMatching(nodesForMatching, iface, op)) {
            result &= invokeWithMatching(context, nodeTemplate, iface, op, nodesForMatching);
        }

        return result;
    }

    private boolean isImplementedAsScript(AbstractInterface iface, AbstractOperation op,
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
    private boolean checkForRunScriptAndTransferFile(AbstractNodeTemplate nodeTemplate) {

        Set<AbstractNodeTemplate> nodeTemplates = this.getNodesForMatching(nodeTemplate);

        boolean foundRunScript = false;
        boolean foundTransferFile = false;
        for (AbstractNodeTemplate node : nodeTemplates) {
            for (AbstractInterface iface : node.getType().getInterfaces()) {
                for (AbstractOperation op : iface.getOperations()) {
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

    private Set<AbstractNodeTemplate> getNodesForMatching(AbstractNodeTemplate nodeTemplate) {
        Set<AbstractNodeTemplate> nodesForMatching = new HashSet<AbstractNodeTemplate>();

        nodesForMatching.add(nodeTemplate);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.dependsOnRelationType, nodesForMatching);
        ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.hostedOnRelationType, nodesForMatching);
        return nodesForMatching;
    }
    
    private Set<AbstractNodeTemplate> filterForNodesInCreation(BPELPlanContext context, Set<AbstractNodeTemplate> nodes) {
        Set<AbstractNodeTemplate> result = new HashSet<AbstractNodeTemplate>();
        Collection<AbstractNodeTemplate> nodesInCreation = context.getNodesInCreation();
        
        for(AbstractNodeTemplate node : nodes) {
            if(nodesInCreation.contains(node)) {
                result.add(node);
            }
        }
        
        return result;
    }

    public boolean isProvisionableByLifecyclePattern(final AbstractNodeTemplate nodeTemplate) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return false;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        AbstractOperation op = null;
        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
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
            return this.checkForRunScriptAndTransferFile(nodeTemplate);
        }

        return true;
    }

    public boolean isDeprovisionableByLifecyclePattern(final AbstractNodeTemplate nodeTemplate) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return false;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

        // Small check if we have to find runScript and transferFile operations
        boolean hasScriptImplementation = false;

        // check if the lifecycle operations can be matched against the nodes
        AbstractOperation op = null;
        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
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
            return this.checkForRunScriptAndTransferFile(nodeTemplate);
        }

        return true;
    }

    public Collection<AbstractNodeTemplate> getMatchedNodesForProvisioning(AbstractNodeTemplate nodeTemplate) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return null;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

        // check if the lifecycle operations can be matched against the nodes
        AbstractOperation op = null;
        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
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

    public Collection<AbstractNodeTemplate> getMatchedNodesForDeprovisioning(AbstractNodeTemplate nodeTemplate) {

        if (!hasLifecycleProvisioningMethods(nodeTemplate)) {
            return null;
        }

        Set<AbstractNodeTemplate> nodesForMatching = this.getNodesForMatching(nodeTemplate);

        // check if the lifecycle operations can be matched against the nodes
        AbstractOperation op = null;
        AbstractInterface iface = this.getLifecyclePatternInterface(nodeTemplate);
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
