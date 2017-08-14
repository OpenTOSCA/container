package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.utils.Utils;


public abstract class AbstractBuildPlanBuilder extends AbstractPlanBuilder {

	public AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions, final AbstractServiceTemplate serviceTemplate, Collection<AbstractNodeTemplate> nodeTemplates, Collection<AbstractRelationshipTemplate> relationshipTemplates) {
		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		Map<AbstractActivity, AbstractActivity> links = new HashMap<AbstractActivity, AbstractActivity>();
		
		Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
				
		this.generatePOGActivitesAndLinks(activities, links, mapping, nodeTemplates, relationshipTemplates);
				
		AbstractPlan plan = new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities, links) {
			
		};
		return plan;
	}

	public AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions, final AbstractServiceTemplate serviceTemplate) {
		
		Collection<AbstractActivity> activities = new ArrayList<AbstractActivity>();
		Map<AbstractActivity, AbstractActivity> links = new HashMap<AbstractActivity, AbstractActivity>();
		
		Map<AbstractNodeTemplate, AbstractActivity> mapping = new HashMap<AbstractNodeTemplate, AbstractActivity>();
		
		final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();
		
		this.generatePOGActivitesAndLinks(activities, links, mapping, topology.getNodeTemplates(), topology.getRelationshipTemplates());
		
		AbstractPlan plan = new AbstractPlan(id, AbstractPlan.PlanType.BUILD, definitions, serviceTemplate, activities, links) {
			
		};
		return plan;
	}
	
	private void generatePOGActivitesAndLinks(Collection<AbstractActivity> activities, Map<AbstractActivity, AbstractActivity> links, Map<AbstractNodeTemplate, AbstractActivity> mapping, Collection<AbstractNodeTemplate> nodeTemplates, Collection<AbstractRelationshipTemplate> relationshipTemplates) {
		for (AbstractNodeTemplate nodeTemplate : nodeTemplates) {
			AbstractActivity activity = new ANodeTemplateActivity(nodeTemplate.getId() + "_provisioning_activity", "PROVISIONING", nodeTemplate);
			activities.add(activity);
			mapping.put(nodeTemplate, activity);
		}
		
		for (AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
			AbstractActivity activity = new ARelationshipTemplateActivity(relationshipTemplate.getId() + "_provisioning_activity", "PROVISIONING", relationshipTemplate);
			activities.add(activity);
			
			QName baseType = Utils.getRelationshipBaseType(relationshipTemplate);
			if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				links.put(mapping.get(relationshipTemplate.getSource()), activity);
				links.put(mapping.get(relationshipTemplate.getTarget()), activity);
			} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
				links.put(mapping.get(relationshipTemplate.getTarget()), activity);
				links.put(activity, mapping.get(relationshipTemplate.getSource()));
			}
			
		}
	}


		
	
}
