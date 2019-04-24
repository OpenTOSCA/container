package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.Set;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class AbstractTransformationPlan extends AbstractPlan {
	
	private final AbstractDefinitions targetDefinitions;
	private final AbstractServiceTemplate targetServiceTemplate;


	public AbstractTransformationPlan(String id, PlanType type, AbstractDefinitions sourceDefinitions,
			AbstractServiceTemplate sourceServiceTemplate, AbstractDefinitions targetDefinitions,
			AbstractServiceTemplate targetServiceTemplate, Collection<AbstractActivity> activities, Collection<Link> links) {
		super(id, type, sourceDefinitions, sourceServiceTemplate, activities, links);
		this.targetDefinitions = targetDefinitions;
		this.targetServiceTemplate = targetServiceTemplate;
	}
	
    public AbstractDefinitions getTargetDefinitions() {
        return this.targetDefinitions;
    }

    public AbstractServiceTemplate getTargetServiceTemplate() {
        return this.targetServiceTemplate;
    }

}
