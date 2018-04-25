package org.opentosca.planbuilder.postphase.plugin.monitoring.bpel.impl;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of NodeTemplate Instances to
 * the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELMonitoringPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext> {

    @Override
    public String getID() {
        return "PlanBuilder POSTPhase Plugin BPEL Monitoring";
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // a double check basically
        // FIXME somehow the canHandle method should already include the planType but not with context
        // object itself as it allows to manipulate the plan already
        if (!this.canHandle(nodeTemplate) && context.getPlanType().equals(PlanType.TERMINATE)) {
            return false;
        }

        return context.executeOperation(nodeTemplate, "Monitor", "deployAgent", null);
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandle(final AbstractNodeTemplate nodeTemplate) {
        // what we are basically looking for:
        // <Interface name="Monitor">
        // <Operation name="deployAgent"/>
        // </Interface>
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals("Monitor")) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals("deployAgent")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canHandle(final AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }



}
