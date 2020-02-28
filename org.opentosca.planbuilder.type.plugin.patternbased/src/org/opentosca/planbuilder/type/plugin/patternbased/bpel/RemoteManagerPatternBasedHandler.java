package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.w3c.dom.Element;

public class RemoteManagerPatternBasedHandler extends PatternBasedHandler {

    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate, Element elementToAppendTo) {    

        final AbstractInterface iface = getRemoteManagerInterface(nodeTemplate);
        final AbstractOperation createOperation = getRemoteManagerInstallOperation(nodeTemplate);

        final Set<AbstractNodeTemplate> nodesForMatching = calculateNodesForMatching(nodeTemplate);

        return invokeWithMatching(context, nodeTemplate, iface, createOperation, nodesForMatching, elementToAppendTo);
    }
    
    public boolean isProvisionableByRemoteManagerPattern(AbstractNodeTemplate node) {
        
        if(this.getRemoteManagerInstallOperation(node) == null) {
            return false;
        }
        
        if(this.getRemoteManagerNode(node) == null) {
            return false;
        }
        
        
        return true;
    }
    
    public Set<AbstractNodeTemplate> getNodeDependencies(AbstractNodeTemplate nodeTemplate) {
        return this.calculateNodesForMatching(nodeTemplate);
    }
    
    private Set<AbstractNodeTemplate> calculateNodesForMatching(final AbstractNodeTemplate nodeTemplate) {
        final Set<AbstractNodeTemplate> nodesForMatching = new HashSet<>();
        nodesForMatching.add(nodeTemplate);

        AbstractNodeTemplate mngrNode = getRemoteManagerNode(nodeTemplate);
        
        for(AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()){
            nodesForMatching.add(relation.getTarget());
        }
        
        nodesForMatching.add(mngrNode);

        return nodesForMatching;
    }
    
    private AbstractNodeTemplate getRemoteManagerNode(AbstractNodeTemplate node) {
        
        for(AbstractRelationshipTemplate relation : node.getOutgoingRelations()) {
            if(relation.getType().equals(Types.dependsOnRelationType)){
                AbstractNodeTemplate remoteMngrNode = relation.getTarget();
                if(Utils.isSupportedOSNodeType(remoteMngrNode.getType().getId())) {
                    return remoteMngrNode;
                }
            }
        }
        
        return null;
    }
    
    private AbstractOperation getRemoteManagerInstallOperation(AbstractNodeTemplate node) {        
        AbstractInterface iface = this.getRemoteManagerInterface(node);        
        if(iface != null) {
            for(AbstractOperation op : iface.getOperations()) {
                if(op.getName().equals("install")) {
                    return op;
                }
            }
        }        
        return null;
    }
    
    private AbstractInterface getRemoteManagerInterface(AbstractNodeTemplate node) {        
        for(AbstractInterface iface : node.getType().getInterfaces()) {
            if( iface.getName().equals("http://opentosca.org/interfaces/pattern/remotemanager")) {
                return iface;
            }
        }        
        return null;
    }
}
