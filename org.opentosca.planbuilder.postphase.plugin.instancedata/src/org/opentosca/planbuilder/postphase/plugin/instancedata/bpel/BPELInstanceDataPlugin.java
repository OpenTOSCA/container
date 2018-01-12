package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.postphase.plugin.instancedata.bpel.handler.BPELHandler;
import org.opentosca.planbuilder.postphase.plugin.instancedata.core.InstanceDataPlugin;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of
 * NodeTemplate Instances to the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELInstanceDataPlugin extends InstanceDataPlugin<BPELPlanContext> {

    private BPELHandler handler = new BPELHandler();

    @Override
    public boolean handle(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
	// TODO FIXME this is a huge assumption right now! Not all management plans need
	// instance handling for provisioning
	if (context.getPlanType().equals(AbstractPlan.PlanType.BUILD)
		|| context.getPlanType().equals(AbstractPlan.PlanType.MANAGE)) {
	    return this.handler.handleBuild(context, nodeTemplate);
	} else {
	    return this.handler.handleTerminate(context, nodeTemplate);
	}
    }

    @Override
    public boolean handle(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
	return this.handler.handle(context, relationshipTemplate);
    }

}
