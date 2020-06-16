package org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents a generic plugin containing bpel logic to start a virtual machine instance with the OpenTOSCA
 * Container Invoker Service
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELUbuntuVmTypePlugin implements IPlanBuilderTypePlugin<BPELPlanContext>,
    IPlanBuilderPolicyAwareTypePlugin<BPELPlanContext> {
    public static final QName noPublicAccessPolicyType = new QName("http://opentosca.org/policytypes",
        "NoPublicAccessPolicy");
    public static final QName publicAccessPolicyType = new QName("http://opentosca.org/policytypes",
        "PublicAccessPolicy");
    public static final QName onlyModeledPortsPolicyType = new QName("http://opentosca.org/policytypes",
        "OnlyModeledPortsPolicyType");

    private static final Logger LOG = LoggerFactory.getLogger(BPELUbuntuVmTypePlugin.class);
    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder VM and Cloud Provider Declarative Type Plugin";

    private final BPELUbuntuVmTypePluginHandler handler = new BPELUbuntuVmTypePluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return allDependenciesAreMet(nodeTemplate);
    }

    @Override
    public boolean canHandleTerminate(final AbstractNodeTemplate nodeTemplate) {
        return allDependenciesAreMet(nodeTemplate);
    }

    private boolean allDependenciesAreMet(final AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate is null");
            return false;
        }
        if (nodeTemplate.getType() == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
            return false;
        }
        if (nodeTemplate.getType().getId() == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType id is null");
            return false;
        }
        // this plugin can handle all referenced nodeTypes
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType().getId())) {
            return true;
        } else if (Utils.isSupportedVMNodeType(nodeTemplate.getType().getId())) {
            // checking if this vmNode is connected to a nodeTemplate of Type
            // cloud provider (ec2, openstack) or docker engine, if not this
            // plugin can't handle
            // this node
            for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
                if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())
                    | Utils.isSupportedDockerEngineNodeType(relationshipTemplate.getTarget().getType().getId())) {
                    return true;
                }
            }
            return false;
        } else if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType().getId())) {
            // checking whether this GENERIC ubuntu NodeTemplate is connected to
            // a VM
            // Node, after this checking whether the VM Node is connected to a
            // EC2 Node

            // check for generic UbuntuNodeType
            if (nodeTemplate.getType().getId().equals(Types.ubuntuNodeType)) {
                // here we check for a 3 node stack ubuntu -> vm -> cloud
                // provider(ec2,openstack)
                return checkIfConnectedToVMandCloudProvider(nodeTemplate);
            } else {

                // here we assume that a specific ubuntu image is selected as
                // the nodeType e.g. ubuntu13.10server NodeType
                // so we check only for a cloud provider
                return checkIfConnectedToCloudProvider(nodeTemplate);
            }
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwareTypePlugin#
     * canHandlePolicyAware(org.opentosca.planbuilder.model.tosca. AbstractNodeTemplate)
     */
    @Override
    public boolean canHandlePolicyAwareCreate(final AbstractNodeTemplate nodeTemplate) {
        boolean canHandle = this.canHandleCreate(nodeTemplate);

        for (final AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().equals(this.noPublicAccessPolicyType)
                | policy.getType().getId().equals(this.publicAccessPolicyType)) {
                if (policy.getProperties() != null
                    && policy.getProperties().getDOMElement().getLocalName().equals("SecurityGroup")) {
                    canHandle &= true;
                }
            } else if (policy.getType().getId().equals(this.onlyModeledPortsPolicyType)) {
                canHandle &= true;
            } else {
                // ALL policies must be supported
                canHandle &= false;
            }
        }

        return canHandle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // this plugin doesn't handle relations
        return false;
    }

    @Override
    public boolean canHandleTerminate(final AbstractRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    /**
     * <p>
     * Checks whether the given NodeTemplate is connected to another node of some Cloud Provider NodeType
     * </p>
     *
     * @param nodeTemplate any AbstractNodeTemplate
     * @return true iff connected to Cloud Provider Node
     */
    private boolean checkIfConnectedToCloudProvider(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
            if (Utils.isSupportedCloudProviderNodeType(relationshipTemplate.getTarget().getType().getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks whether there is a path from the given NodeTemplate of length 3 with the following nodes:<br> The
     * NodeTemplate itself<br> A NodeTemplate of type {http://opentosca.org/types/declarative}VM <br> A NodeTemplate of
     * type {http://opentosca.org/types/declarative}EC2 or OpenStack
     * </p>
     *
     * @param nodeTemplate any AbstractNodeTemplate
     * @return true if the there exists a path from the given NodeTemplate to a Cloud Provider node, else false
     */
    private boolean checkIfConnectedToVMandCloudProvider(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
            if (relationshipTemplate.getTarget().getType().getId().equals(Types.vmNodeType)) {
                if (checkIfConnectedToCloudProvider(relationshipTemplate.getTarget())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getID() {
        return BPELUbuntuVmTypePlugin.PLUGIN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, final AbstractNodeTemplate nodeTemplate) {
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
                    final QName nodeType = relation.getTarget().getType().getId();
                    if (nodeType.equals(Types.openStackLiberty12NodeType)
                        || nodeType.equals(Types.openStackTrainNodeType)
                        || nodeType.equals(Types.vmWareVsphere55NodeType)
                        || nodeType.equals(Types.amazonEc2NodeType)
                        || nodeType.getNamespaceURI()
                        .equals(Types.openStackLiberty12NodeTypeGenerated.getNamespaceURI())
                        && (nodeType.getLocalPart()
                        .startsWith(Types.openStackLiberty12NodeTypeGenerated.getLocalPart())
                        || nodeType.getLocalPart().startsWith(Types.openStackTrainNodeType.getLocalPart()))) {
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
                    final QName nodeType = relation.getTarget().getType().getId();
                    if (nodeType.equals(Types.openStackLiberty12NodeType)
                        || nodeType.equals(Types.openStackTrainNodeType)
                        || nodeType.equals(Types.vmWareVsphere55NodeType)
                        || nodeType.equals(Types.amazonEc2NodeType)
                        || nodeType.getNamespaceURI()
                        .equals(Types.openStackLiberty12NodeTypeGenerated.getNamespaceURI())
                        && (nodeType.getLocalPart()
                        .startsWith(Types.openStackLiberty12NodeTypeGenerated.getLocalPart())
                        || nodeType.getLocalPart().startsWith(Types.openStackTrainNodeType.getLocalPart()))) {
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
    public boolean handleTerminate(final BPELPlanContext templateContext, final AbstractNodeTemplate nodeTemplate) {
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
                    final QName nodeType = relation.getTarget().getType().getId();
                    if (nodeType.equals(Types.openStackLiberty12NodeType)
                        || nodeType.equals(Types.openStackTrainNodeType)
                        || nodeType.equals(Types.vmWareVsphere55NodeType)
                        || nodeType.equals(Types.amazonEc2NodeType)
                        || nodeType.getNamespaceURI()
                        .equals(Types.openStackLiberty12NodeTypeGenerated.getNamespaceURI())
                        && (nodeType.getLocalPart()
                        .startsWith(Types.openStackLiberty12NodeTypeGenerated.getLocalPart())
                        || nodeType.getLocalPart().startsWith(Types.openStackTrainNodeType.getLocalPart()))) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleTerminateWithCloudProviderInterface(templateContext, nodeTemplate,
                            templateContext.getProvisioningPhaseElement());
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
    public boolean handleCreate(final BPELPlanContext templateContext,
                                final AbstractRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext templateContext,
                                   final AbstractRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    @Override
    public int getPriority() {
        //
        return 0;
    }
}
