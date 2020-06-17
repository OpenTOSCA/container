package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public boolean canHandleCreate(BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // we can handle nodes
        return true;
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final AbstractRelationshipTemplate relationshipTemplate) {
        // we can handle relations
        return true;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
        return true;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // TODO FIXME this is a huge assumption right now! Not all management plans need
        //  instance handling for provisioning
        return this.handler.handleCreate(context, nodeTemplate);
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final AbstractRelationshipTemplate relationshipTemplate) {
        return this.handler.handleCreate(context, relationshipTemplate);
    }

    @Override
    public boolean handlePolicyAwareCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                                           final AbstractPolicy policy) {
        return this.handler.handlePasswordCheck(context, nodeTemplate);
    }

    @Override
    public boolean canHandlePolicyAwareCreate(final AbstractNodeTemplate nodeTemplate, final AbstractPolicy policy) {
        if (!policy.getType().getId().equals(this.securePasswordPolicyType)) {
            return false;
        }

        final NodeList nodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();

        for (int index = 0; index < nodes.getLength(); index++) {
            if (nodes.item(index).getNodeType() == Node.ELEMENT_NODE
                && nodes.item(index).getLocalName().contains("Password")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
        return this.handler.handleTerminate(context, nodeTemplate);
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
        return this.handler.handleTerminate(context, relationshipTemplate);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                AbstractNodeTemplate sourceNodeTemplate, AbstractNodeTemplate targetNodeTemplate) {
        if (this.canHandleUpdate(sourceNodeTemplate, targetNodeTemplate)) {
            return this.handler.handleUpdate(sourceContext, targetContext, sourceNodeTemplate, targetNodeTemplate);
        }
        return false;
    }

    @Override
    public boolean canHandleUpdate(AbstractNodeTemplate sourceNodeTemplate, AbstractNodeTemplate targetNodeTemplate) {
        // this plugin can create instance data for only equal nodeTemplates as of now
        return sourceNodeTemplate.getType().getId().equals(targetNodeTemplate.getType().getId());
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                AbstractRelationshipTemplate sourceRelationshipTemplate,
                                AbstractRelationshipTemplate targetRelationshipTemplate) {

        if (this.canHandleUpdate(sourceRelationshipTemplate, targetRelationshipTemplate)) {
            return this.handler.handleUpdate(sourceContext, targetContext, sourceRelationshipTemplate, targetRelationshipTemplate);
        }
        return false;
    }

    @Override
    public boolean canHandleUpdate(AbstractRelationshipTemplate sourceRelationshipTemplate,
                                   AbstractRelationshipTemplate targetRelationshipTemplate) {
        return sourceRelationshipTemplate.getType().equals(targetRelationshipTemplate.getType());
    }
}
