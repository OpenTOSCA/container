package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;

public class RemoteManagerPatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate, Element elementToAppendTo) {
        final TInterface iface = getRemoteManagerInterface(nodeTemplate, context.getCsar());
        final TOperation createOperation = getRemoteManagerInstallOperation(nodeTemplate, context.getCsar());

        final Set<TNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate, context.getCsar());

        // For the future we should think about integrating the fileupload plugin into the pattern plugin or refactoring it, cause:
        // The fileupload plugin implicitly works according to the lifecycle/container pattern as in that case it can just traverse the the topology downward along the hostedOn relations.
        // The remote manager pattern uses a dependsOn relation to a managing node which used to find the operation for uploading files/DAs
        TNodeTemplate infraNode = this.getRemoteManagerNode(nodeTemplate, context.getCsar());

        nodeTemplate.getDeploymentArtifacts().forEach(da -> ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences().forEach(ref -> this.invokeArtifactReferenceUpload(context, ref, infraNode)));

        return invokeWithMatching(context, nodeTemplate, iface, createOperation, nodesForMatching, elementToAppendTo);
    }

    public boolean isProvisionableByRemoteManagerPattern(TNodeTemplate node, Csar csar) {

        if (this.getRemoteManagerInstallOperation(node, csar) == null) {
            return false;
        }

        return this.getRemoteManagerNode(node, csar) != null;
    }

    public Set<TNodeTemplate> getNodeDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        return this.calculateNodesForMatching(nodeTemplate, csar);
    }

    private Set<TNodeTemplate> calculateNodesForMatching(final TNodeTemplate nodeTemplate, Csar csar) {
        final Set<TNodeTemplate> nodesForMatching = new HashSet<>();
        nodesForMatching.add(nodeTemplate);

        TNodeTemplate mngrNode = getRemoteManagerNode(nodeTemplate, csar);

        for (TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            nodesForMatching.add(ModelUtils.getTarget(relation, csar));
        }

        nodesForMatching.add(mngrNode);

        return nodesForMatching;
    }

    private TNodeTemplate getRemoteManagerNode(TNodeTemplate node, Csar csar) {

        for (TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(node, csar)) {
            if (relation.getType().equals(Types.dependsOnRelationType)) {
                TNodeTemplate remoteMngrNode = ModelUtils.getTarget(relation, csar);
                if (Utils.isSupportedOSNodeType(remoteMngrNode.getType())) {
                    return remoteMngrNode;
                }
            }
        }

        return null;
    }

    private TOperation getRemoteManagerInstallOperation(TNodeTemplate node, Csar csar) {
        TInterface iface = this.getRemoteManagerInterface(node, csar);
        if (iface != null) {
            for (TOperation op : iface.getOperations()) {
                if (op.getName().equals("install")) {
                    return op;
                }
            }
        }
        return null;
    }

    private TOperation getRemoteManagerResetOperation(TNodeTemplate node, Csar csar) {
        TInterface iface = this.getRemoteManagerInterface(node, csar);
        if (iface != null) {
            for (TOperation op : iface.getOperations()) {
                if (op.getName().equals("reset")) {
                    return op;
                }
            }
        }
        return null;
    }

    private TInterface getRemoteManagerInterface(TNodeTemplate node, Csar csar) {
        for (TInterface iface : ModelUtils.findNodeType(node, csar).getInterfaces()) {
            if (iface.getName().equals("http://opentosca.org/interfaces/pattern/remotemanager")) {
                return iface;
            }
        }
        return null;
    }

    public boolean isDeprovisionableByRemoteManagerPattern(TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection<? extends TNodeTemplate> getMatchedNodesForDeprovisioning(TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate,
                                   Element provisioningPhaseElement) {
        // TODO Auto-generated method stub
        return false;
    }

    public TOperation getRemoteManagerPatternResetMethod(TNodeTemplate nodeTemplate, Csar csar) {
        return this.getRemoteManagerResetOperation(nodeTemplate, csar);
    }

    public TOperation getRemoteManagerPatternInstallMethod(TNodeTemplate nodeTemplate, Csar csar) {
        return this.getRemoteManagerInstallOperation(nodeTemplate, csar);
    }
}
