package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;

public abstract class AbstractBPMN4TOSCAPlanBuilder extends AbstractSimplePlanBuilder {

	@Override
	public PlanType createdPlanType() {
		return PlanType.BPMN4TOSCA;
	}

	protected static AbstractPlan generatePlan(final String id, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate) {
		final Collection<AbstractActivity> activities = new ArrayList<>();
		final Set<Link> links = new HashSet<>();
		final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
		final Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();

		final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

		final AbstractPlan plan = new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate,
				activities, links) {

		};
		return plan;

	}

}
