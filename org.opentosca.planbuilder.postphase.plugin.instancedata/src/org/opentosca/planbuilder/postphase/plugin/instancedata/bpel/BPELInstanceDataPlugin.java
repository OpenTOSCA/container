package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class BPELInstanceDataPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext>,
		IPlanBuilderPolicyAwarePrePhasePlugin<BPELPlanContext> {

	private static final String PLAN_ID = "OpenTOSCA InstanceData Post Phase Plugin";

	@Override
	public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
		// we can handle nodes
		return true;
	}

	@Override
	public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
		// we can handle relations
		return true;
	}

	@Override
	public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
		return true;
	}

	@Override
	public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
		return false;
	}

	@Override
	public String getID() {
		return PLAN_ID;
	}

	private final Handler handler = new Handler();

	private final QName securePasswordPolicyType = new QName("http://opentosca.org/policytypes",
			"SecurePasswordPolicyType");

	@Override
	public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
		// TODO FIXME this is a huge assumption right now! Not all management plans need
		// instance handling for provisioning
		if (context.getPlanType().equals(AbstractPlan.PlanType.BUILD)
				|| context.getPlanType().equals(AbstractPlan.PlanType.MANAGE)) {
			return this.handler.handleBuild(context, nodeTemplate);
		} else {
			return false;
		}
	}

	@Override
	public boolean handleCreate(final BPELPlanContext context,
			final AbstractRelationshipTemplate relationshipTemplate) {
		return this.handler.handle(context, relationshipTemplate);
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
		return false;
	}

}
