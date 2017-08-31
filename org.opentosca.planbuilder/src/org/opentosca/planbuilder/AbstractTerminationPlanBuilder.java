package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.utils.Utils;


public abstract class AbstractTerminationPlanBuilder extends AbstractPlanBuilder {

	protected AbstractPlan generateTOG(final String id, final AbstractDefinitions definitions, AbstractServiceTemplate serviceTemplate) {
		
		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		Set<Link> links = new HashSet<Link>();
		
		Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
		
		AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();
		
		for (AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
			ANodeTemplateActivity activity = new ANodeTemplateActivity(nodeTemplate.getId() + "_termination_activity", "TERMINATION", nodeTemplate);
			activities.add(activity);
			mapping.put(nodeTemplate, activity);
		}
		
		for (AbstractRelationshipTemplate relationshipTemplate : topology.getRelationshipTemplates()) {
			ARelationshipTemplateActivity activity = new ARelationshipTemplateActivity(relationshipTemplate.getId() + "_termination_activity", "TERMINATION", relationshipTemplate);
			activities.add(activity);
			
			QName baseType = Utils.getRelationshipBaseType(relationshipTemplate);
			
			if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				links.add(new Link(activity, mapping.get(relationshipTemplate.getSource())));
				links.add(new Link(activity, mapping.get(relationshipTemplate.getTarget())));
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				links.add(new Link(mapping.get(relationshipTemplate.getSource()), activity));
				links.add(new Link(activity, mapping.get(relationshipTemplate.getTarget())));
			}
			
		}
		
		AbstractPlan abstractTerminationPlan = new AbstractPlan(id, AbstractPlan.PlanType.TERMINATE, definitions, serviceTemplate, activities, links) {
		};
		
		return abstractTerminationPlan;
	}
	
	
	
}
