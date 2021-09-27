package org.opentosca.planbuilder.postphase.plugin.monitoring.bpel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to the OpenTOSCA
 * Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELMonitoringPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext> {

    private final String monitoringInterfaceName = "http://opentosca.org/interfaces/monitoring";
    private final String monitoringOperationName = "deployAgent";
    private final QName configArtifactType = new QName("http://opentosca.org/artifacttypes", "ConfigurationArtifact");
    private final BPELInvokerPlugin invokerPlugin = new BPELInvokerPlugin();

    @Override
    public String getID() {
        return "PlanBuilder POSTPhase Plugin BPEL Monitoring";
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // a double check basically
        // FIXME somehow the canHandle method should already include the planType but not with context
        // object itself as it allows to manipulate the plan already
        if (!this.canHandleCreate(context, nodeTemplate)) {
            return false;
        }

        if (context.getPlanType().equals(PlanType.TERMINATION)) {
            return false;
        }

        final TDeploymentArtifact configDeplArti = fetchConfigurationArtifact(nodeTemplate);

        if (configDeplArti != null) {
            uploadConfigurationArtifact(context, configDeplArti, nodeTemplate);
        }

        return context.executeOperation(nodeTemplate, this.monitoringInterfaceName, this.monitoringOperationName, null,
            null, BPELScopePhaseType.POST, context.getPostPhaseElement());
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // what we are basically looking for:
        // <Interface name="Monitor">
        // <Operation name="deployAgent"/>
        // </Interface>
        TInterface iface = ModelUtils.getInterfaceOfNode(nodeTemplate, this.monitoringInterfaceName, context.getCsar());
        if (Objects.nonNull(iface)) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(this.monitoringOperationName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    private void uploadConfigurationArtifact(final BPELPlanContext context, final TDeploymentArtifact deplArti,
                                             final TNodeTemplate nodeTemplate) {
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, context.getCsar());

        TNodeTemplate infraNode = null;
        PropertyVariable sshIpVar = null;
        PropertyVariable sshKeyVar = null;
        PropertyVariable sshUserVar = null;

        for (final TNodeTemplate infraNodeTemplate : infraNodes) {
            int propMatchCount = 0;
            final Collection<String> propNames = ModelUtils.getPropertyNames(infraNodeTemplate);
            for (final String propName : propNames) {
                if (Utils.isSupportedVirtualMachineIPProperty(propName)) {
                    sshIpVar = context.getPropertyVariable(propName);
                    propMatchCount++;
                } else if (Utils.isSupportedSSHKeyProperty(propName)) {
                    sshKeyVar = context.getPropertyVariable(propName);
                    propMatchCount++;
                } else if (Utils.isSupportedSSHUserPropery(propName)) {
                    sshUserVar = context.getPropertyVariable(propName);
                    propMatchCount++;
                }
            }
            if (propMatchCount == 3) {
                infraNode = infraNodeTemplate;
                break;
            } else {
                sshIpVar = null;
                sshKeyVar = null;
                sshUserVar = null;
            }
        }

        this.invokerPlugin.handleArtifactReferenceUpload(ModelUtils.findArtifactTemplate(deplArti.getArtifactRef(), context.getCsar()).getArtifactReferences().stream().findFirst().get(),
            context, sshIpVar, sshUserVar, sshKeyVar, infraNode, context.getProvisioningPhaseElement());
    }

    private TDeploymentArtifact fetchConfigurationArtifact(final TNodeTemplate nodeTemplate) {
        for (final TDeploymentArtifact deplArti : nodeTemplate.getDeploymentArtifacts()) {
            if (deplArti.getArtifactType().equals(this.configArtifactType)) {
                return deplArti;
            }
        }
        return null;
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleTerminate(final BPELPlanContext context,
                                   final TRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handleUpdate(final BPELPlanContext sourceContext, final BPELPlanContext targetContext,
                                final TNodeTemplate sourceNodeTemplate,
                                final TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(final TNodeTemplate sourceNodeTemplate,
                                   final TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpdate(final BPELPlanContext sourceContext, final BPELPlanContext targetContext,
                                final TRelationshipTemplate sourceRelationshipTemplate,
                                final TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(final TRelationshipTemplate sourceRelationshipTemplate,
                                   final TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
