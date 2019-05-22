package org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents a generic plugin containing bpel logic to start a virtual machine instance
 * with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELUbuntuVmTypePlugin extends UbuntuVmTypePlugin<BPELPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPELUbuntuVmTypePlugin.class);
    private final BPELUbuntuVmTypePluginHandler handler = new BPELUbuntuVmTypePluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        BPELUbuntuVmTypePlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

        // cloudprovider node is handled by doing nothing
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // docker engine node is handled by doing nothing
        if (Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // when infrastructure node arrives start handling
        if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
            // check if this node is connected to a cloud provider node type, if
            // true -> append code
            for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
                if (Utils.isSupportedCloudProviderNodeType(relation.getTarget().getType().getId())) {
                    if (relation.getTarget().getType().getId().equals(Types.openStackLiberty12NodeType)
                        | relation.getTarget().getType().getId().equals(Types.vmWareVsphere55NodeType)
                        | relation.getTarget().getType().getId().equals(Types.amazonEc2NodeType)) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleCreateWithCloudProviderInterface(templateContext, nodeTemplate);
                    } else if (relation.getTarget().getType().getId().equals(Types.localHypervisor)) {
                        return this.handler.handleWithLocalCloudProviderInterface(templateContext, nodeTemplate);
                    } else {
                        return this.handler.handle(templateContext, nodeTemplate);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handlePolicyAwareCreate(final BPELPlanContext templateContext) {
        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
        if (nodeTemplate == null) {
            return false;
        }

        BPELUbuntuVmTypePlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

        // cloudprovider node is handled by doing nothing
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // docker engine node is handled by doing nothing
        if (Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // when infrastructure node arrives start handling
        if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
            // check if this node is connected to a cloud provider node type, if
            // true -> append code
            for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
                if (Utils.isSupportedCloudProviderNodeType(relation.getTarget().getType().getId())) {
                    if (relation.getTarget().getType().getId().equals(Types.openStackLiberty12NodeType)
                        | relation.getTarget().getType().getId().equals(Types.vmWareVsphere55NodeType)
                        | relation.getTarget().getType().getId().equals(Types.amazonEc2NodeType)) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleCreateWithCloudProviderInterface(templateContext, nodeTemplate);
                    } else if (relation.getTarget().getType().getId().equals(Types.localHypervisor)) {
                        return this.handler.handleWithLocalCloudProviderInterface(templateContext, nodeTemplate);
                    } else {
                        return this.handler.handle(templateContext, nodeTemplate);
                    }
                }
            }
            return true;
        }
        return false;
    }



    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        BPELUbuntuVmTypePlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

        // cloudprovider node is handled by doing nothing
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // docker engine node is handled by doing nothing
        if (Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType().getId())) {
            return true;
        }

        // when infrastructure node arrives start handling
        if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
            // check if this node is connected to a cloud provider node type, if
            // true -> append code
            for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
                if (Utils.isSupportedCloudProviderNodeType(relation.getTarget().getType().getId())) {
                    if (relation.getTarget().getType().getId().equals(Types.openStackLiberty12NodeType)
                        | relation.getTarget().getType().getId().equals(Types.vmWareVsphere55NodeType)
                        | relation.getTarget().getType().getId().equals(Types.amazonEc2NodeType)) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleTerminateWithCloudProviderInterface(templateContext, nodeTemplate);
                    } else if (relation.getTarget().getType().getId().equals(Types.localHypervisor)) {
                        return this.handler.handleWithLocalCloudProviderInterface(templateContext, nodeTemplate);
                    } else {
                        return this.handler.handle(templateContext, nodeTemplate);
                    }
                }
            }
            return true;
        }
        return false;

    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }


}
