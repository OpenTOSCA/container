package org.opentosca.planbuilder.type.plugin.ubuntuvm.core;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
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
public abstract class UbuntuVmTypePlugin<T extends PlanContext>
                                        implements IPlanBuilderTypePlugin<T>, IPlanBuilderPolicyAwareTypePlugin<T> {

    private static final String PLUGIN_ID = "OpenTOSCA PlanBuilder VM and Cloud Provider Declarative Type Plugin";
    private final static Logger LOG = LoggerFactory.getLogger(UbuntuVmTypePlugin.class);

    public static final QName noPublicAccessPolicyType = new QName("http://opentosca.org/policytypes",
        "NoPublicAccessPolicy");
    public static final QName publicAccessPolicyType = new QName("http://opentosca.org/policytypes",
        "PublicAccessPolicy");
    public static final QName onlyModeledPortsPolicyType = new QName("http://opentosca.org/policytypes",
        "OnlyModeledPortsPolicyType");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(final AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            UbuntuVmTypePlugin.LOG.debug("NodeTemplate is null");
        }
        if (nodeTemplate.getType() == null) {
            UbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
        }
        if (nodeTemplate.getType().getId() == null) {
            UbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType id is null");
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
                return this.checkIfConnectedToVMandCloudProvider(nodeTemplate);
            } else {

                // here we assume that a specific ubuntu image is selected as
                // the nodeType e.g. ubuntu13.10server NodeType
                // so we check only for a cloud provider
                return this.checkIfConnectedToCloudProvider(nodeTemplate);
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
    public boolean canHandlePolicyAware(final AbstractNodeTemplate nodeTemplate) {
        boolean canHandle = this.canHandle(nodeTemplate);

        for (final AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getID().equals(this.noPublicAccessPolicyType)
                | policy.getType().getID().equals(this.publicAccessPolicyType)) {
                if (policy.getProperties() != null
                    && policy.getProperties().getDOMElement().getLocalName().equals("SecurityGroup")) {
                    canHandle &= true;
                }
            } else if (policy.getType().getID().equals(this.onlyModeledPortsPolicyType)) {
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
    public boolean canHandle(final AbstractRelationshipTemplate relationshipTemplate) {
        // this plugin doesn't handle relations
        return false;
    }

    /**
     * <p>
     * Checks whether the given NodeTemplate is connected to another node of some Cloud Provider
     * NodeType
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
     * Checks whether there is a path from the given NodeTemplate of length 3 with the following
     * nodes:<br>
     * The NodeTemplate itself<br>
     * A NodeTemplate of type {http://opentosca.org/types/declarative}VM <br>
     * A NodeTemplate of type {http://opentosca.org/types/declarative}EC2 or OpenStack
     * </p>
     *
     * @param nodeTemplate any AbstractNodeTemplate
     * @return true if the there exists a path from the given NodeTemplate to a Cloud Provider node,
     *         else false
     */
    private boolean checkIfConnectedToVMandCloudProvider(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
            if (relationshipTemplate.getTarget().getType().getId().equals(Types.vmNodeType)) {
                if (this.checkIfConnectedToCloudProvider(relationshipTemplate.getTarget())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getID() {
        return UbuntuVmTypePlugin.PLUGIN_ID;
    }
}
