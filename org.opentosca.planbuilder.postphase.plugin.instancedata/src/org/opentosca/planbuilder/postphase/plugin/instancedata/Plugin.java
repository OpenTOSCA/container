package org.opentosca.planbuilder.postphase.plugin.instancedata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwarePostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.utils.Utils;

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
public class Plugin implements IPlanBuilderPostPhasePlugin, IPlanBuilderPolicyAwarePostPhasePlugin {
	
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA InstanceData Post Phase Plugin";
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		// TODO FIXME this is a huge assumption right now! Not all management
		// plans need instance handling for provisioning
		if (context.getPlanType().equals(AbstractPlan.PlanType.BUILD) || context.getPlanType().equals(AbstractPlan.PlanType.MANAGE)) {
			return this.handler.handleBuild(context, nodeTemplate);
		} else {
			return this.handler.handleTerminate(context, nodeTemplate);
		}
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		return this.handler.handle(context, relationshipTemplate);
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// we can handle nodes
		return true;
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can't handle relations
		return true;
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate, Collection<String> policies) {
		// TODO Auto-generated method stub
		if (context.getPlanType().equals(AbstractPlan.PlanType.BUILD) || context.getPlanType().equals(AbstractPlan.PlanType.MANAGE)) {
			return this.handler.handleBuild(context, nodeTemplate) & this.handler.handlePasswordCheck(context, nodeTemplate);
		} else {
			return this.handler.handleTerminate(context, nodeTemplate);
		}
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate, Collection<String> policies) {
		
		// find policy
		if (!policies.contains("SecurePasswordPolicy")) {
			return false;
		} 
		
		// can handle only this type of policy
		if (policies.size() != 1) {
			return false;
		}
		
		// find runscript operation to be able to execute password check at runtime
		if (this.handler.findRunScriptNode(nodeTemplate) != null) {
			return true;
		}
		
		return false;
	}
	
}
