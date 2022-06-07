package org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
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
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate, PlanLanguage language) {
        return allDependenciesAreMet(nodeTemplate, csar);
    }

    @Override
    public boolean canHandleTerminate(Csar csar, final TNodeTemplate nodeTemplate) {
        return allDependenciesAreMet(nodeTemplate, csar);
    }

    private boolean allDependenciesAreMet(final TNodeTemplate nodeTemplate, Csar csar) {
        if (nodeTemplate == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate is null");
            return false;
        }
        if (nodeTemplate.getType() == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType is null. NodeTemplate Id:" + nodeTemplate.getId());
            return false;
        }
        if (nodeTemplate.getType() == null) {
            BPELUbuntuVmTypePlugin.LOG.debug("NodeTemplate NodeType id is null");
            return false;
        }
        // this plugin can handle all referenced nodeTypes
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType())) {
            return true;
        } else if (Utils.isSupportedVMNodeType(nodeTemplate.getType())) {
            // checking if this vmNode is connected to a nodeTemplate of Type
            // cloud provider (ec2, openstack) or docker engine, if not this
            // plugin can't handle
            // this node
            for (final TRelationshipTemplate relationshipTemplate : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
                TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
                if (Utils.isSupportedCloudProviderNodeType(target.getType())
                    | Utils.isSupportedDockerEngineNodeType(target.getType())) {
                    return true;
                }
            }
            return false;
        } else if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType())) {
            // checking whether this GENERIC ubuntu NodeTemplate is connected to
            // a VM
            // Node, after this checking whether the VM Node is connected to a
            // EC2 Node

            // check for generic UbuntuNodeType
            if (nodeTemplate.getType().equals(Types.ubuntuNodeType)) {
                // here we check for a 3 node stack ubuntu -> vm -> cloud
                // provider(ec2,openstack)
                return checkIfConnectedToVMandCloudProvider(nodeTemplate, csar);
            } else {

                // here we assume that a specific ubuntu image is selected as
                // the nodeType e.g. ubuntu13.10server NodeType
                // so we check only for a cloud provider
                return checkIfConnectedToCloudProvider(nodeTemplate, csar);
            }
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwareTypePlugin#
     * canHandlePolicyAware(org.opentosca.planbuilder.model.tosca. TNodeTemplate)
     */
    @Override
    public boolean canHandlePolicyAwareCreate(Csar csar, final TNodeTemplate nodeTemplate) {
        boolean canHandle = this.canHandleCreate(csar, nodeTemplate, PlanLanguage.BPEL);

        for (final TPolicy policy : nodeTemplate.getPolicies()) {
            // ALL policies must be supported
            if (policy.getPolicyType().equals(noPublicAccessPolicyType)
                | policy.getPolicyType().equals(publicAccessPolicyType)) {
                if (policy.getProperties() != null
                    && ModelUtils.asMap(policy.getProperties()).containsKey("SecurityGroup")) {
                    canHandle &= true;
                }
            } else canHandle &= policy.getPolicyType().equals(onlyModeledPortsPolicyType);
        }

        return canHandle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // this plugin doesn't handle relations
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    /**
     * <p>
     * Checks whether the given NodeTemplate is connected to another node of some Cloud Provider NodeType
     * </p>
     *
     * @param nodeTemplate any TNodeTemplate
     * @return true iff connected to Cloud Provider Node
     */
    private boolean checkIfConnectedToCloudProvider(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TRelationshipTemplate relationshipTemplate : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            if (Utils.isSupportedCloudProviderNodeType(ModelUtils.getTarget(relationshipTemplate, csar).getType())) {
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
     * @param nodeTemplate any TNodeTemplate
     * @return true if the there exists a path from the given NodeTemplate to a Cloud Provider node, else false
     */
    private boolean checkIfConnectedToVMandCloudProvider(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TRelationshipTemplate relationshipTemplate : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
            if (target.getType().equals(Types.vmNodeType) && checkIfConnectedToCloudProvider(target, csar)) {
                return true;
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
    public boolean handleCreate(final BPELPlanContext templateContext, final TNodeTemplate nodeTemplate) {
        BPELUbuntuVmTypePlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

        // cloudprovider node is handled by doing nothing
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType())) {
            return true;
        }

        // docker engine node is handled by doing nothing
        if (Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType())) {
            return true;
        }

        // when infrastructure node arrives start handling
        if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType())) {
            // check if this node is connected to a cloud provider node type, if
            // true -> append code
            for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(nodeTemplate, templateContext.getCsar())) {
                TNodeTemplate target = ModelUtils.getTarget(relation, templateContext.getCsar());
                if (Utils.isSupportedCloudProviderNodeType(target.getType())) {
                    final QName nodeType = target.getType();
                    if (Utils.isCloudProvider(nodeType)) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleCreateWithCloudProviderInterface(templateContext, nodeTemplate);
                    } else if (target.getType().equals(Types.localHypervisor)) {
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
        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
        if (nodeTemplate == null) {
            return false;
        }
        return this.handleCreate(templateContext, nodeTemplate);
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext templateContext, final TNodeTemplate nodeTemplate) {
        BPELUbuntuVmTypePlugin.LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");

        // cloudprovider node is handled by doing nothing
        if (Utils.isSupportedCloudProviderNodeType(nodeTemplate.getType())) {
            return true;
        }

        // docker engine node is handled by doing nothing
        if (Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType())) {
            return true;
        }

        // when infrastructure node arrives start handling
        if (Utils.isSupportedInfrastructureNodeType(nodeTemplate.getType())) {
            // check if this node is connected to a cloud provider node type, if
            // true -> append code
            for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(nodeTemplate, templateContext.getCsar())) {
                TNodeTemplate target = ModelUtils.getTarget(relation, templateContext.getCsar());
                if (Utils.isSupportedCloudProviderNodeType(target.getType())) {
                    final QName nodeType = target.getType();
                    if (Utils.isCloudProvider(nodeType)) {
                        // bit hacky now, but until the nodeType cleanup is
                        // finished this should be enough right now
                        return this.handler.handleTerminateWithCloudProviderInterface(templateContext, nodeTemplate,
                            templateContext.getProvisioningPhaseElement());
                    } else if (target.getType().equals(Types.localHypervisor)) {
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
                                final TRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext templateContext,
                                   final TRelationshipTemplate relationshipTemplate) {
        // never handles a relationship
        return false;
    }

    @Override
    public int getPriority() {
        //
        return 0;
    }
}
