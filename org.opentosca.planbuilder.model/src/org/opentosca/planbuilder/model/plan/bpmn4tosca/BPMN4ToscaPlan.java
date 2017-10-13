package org.opentosca.planbuilder.model.plan.bpmn4tosca;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class BPMN4ToscaPlan extends AbstractPlan {
	private LinkedList<BPMN4ToscaElement> elements = new LinkedList<>();

	public BPMN4ToscaPlan(String id, PlanType type, AbstractDefinitions definitions,
			AbstractServiceTemplate serviceTemplate, Collection<AbstractActivity> activities, Set<Link> links) {
		super(id, type, definitions, serviceTemplate, activities, links);
	}

	public LinkedList<BPMN4ToscaElement> getElements() {
		return this.elements;
	}

	public void setElements(LinkedList<BPMN4ToscaElement> elements) {
		this.elements = elements;
	}

}
