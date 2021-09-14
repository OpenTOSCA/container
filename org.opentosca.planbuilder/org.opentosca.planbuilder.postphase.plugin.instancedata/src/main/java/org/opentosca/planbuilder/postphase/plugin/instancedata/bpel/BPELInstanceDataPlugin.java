package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.springframework.stereotype.Component;

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
@Component
public class BPELInstanceDataPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext>,
    IPlanBuilderPolicyAwarePrePhasePlugin<BPELPlanContext> {

    private static final String PLAN_ID = "OpenTOSCA InstanceData Post Phase Plugin";

    private final Handler handler = new Handler();

    private final QName securePasswordPolicyType =
        new QName("http://opentosca.org/policytypes", "SecurePasswordPolicyType");

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // we can handle nodes
        return true;
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        // we can handle relations
        return true;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return true;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        // TODO FIXME this is a huge assumption right now! Not all management plans need
        //  instance handling for provisioning
        return this.handler.handleCreate(context, nodeTemplate);
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        return this.handler.handleCreate(context, relationshipTemplate);
    }

    @Override
    public boolean handlePolicyAwareCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate,
                                           final TPolicy policy) {
        return this.handler.handlePasswordCheck(context, nodeTemplate);
    }

    @Override
    public boolean canHandlePolicyAwareCreate(final TNodeTemplate nodeTemplate, final TPolicy policy) {
        if (!policy.getPolicyType().equals(this.securePasswordPolicyType)) {
            return false;
        }
        return ModelUtils.asMap(nodeTemplate.getProperties()).containsKey("Password");
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return this.handler.handleTerminate(context, nodeTemplate);
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return this.handler.handleTerminate(context, relationshipTemplate);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        if (this.canHandleUpdate(sourceNodeTemplate, targetNodeTemplate)) {
            return this.handler.handleUpdate(sourceContext, targetContext, sourceNodeTemplate, targetNodeTemplate);
        }
        return false;
    }

    @Override
    public boolean canHandleUpdate(TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        // this plugin can create instance data for only equal nodeTemplates as of now
        return sourceNodeTemplate.getType().equals(targetNodeTemplate.getType());
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                TRelationshipTemplate sourceRelationshipTemplate,
                                TRelationshipTemplate targetRelationshipTemplate) {

        if (this.canHandleUpdate(sourceRelationshipTemplate, targetRelationshipTemplate)) {
            return this.handler.handleUpdate(sourceContext, targetContext, sourceRelationshipTemplate, targetRelationshipTemplate);
        }
        return false;
    }

    @Override
    public boolean canHandleUpdate(TRelationshipTemplate sourceRelationshipTemplate,
                                   TRelationshipTemplate targetRelationshipTemplate) {
        return sourceRelationshipTemplate.getType().equals(targetRelationshipTemplate.getType());
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return this.handler.handleUpgrade(context, nodeTemplate);
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
