package org.opentosca.planbuilder;

import java.util.Collection;
import java.util.List;

import org.opentosca.planbuilder.model.plan.ANodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.ARelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanBuilder {

	protected final PluginRegistry pluginRegistry = new PluginRegistry();

	private final static Logger LOG = LoggerFactory.getLogger(AbstractPlanBuilder.class);

	abstract public PlanType createdPlanType();

	protected AbstractActivity findRelationshipTemplateActivity(final Collection<AbstractActivity> activities,
			final AbstractRelationshipTemplate relationshipTemplate, final ActivityType type) {
		for (final AbstractActivity activity : activities) {
			if (activity.getType().equals(type)) {
				if (activity instanceof ARelationshipTemplateActivity) {
					if (((ARelationshipTemplateActivity) activity).getRelationshipTemplate()
							.equals(relationshipTemplate)) {
						return activity;
					}
				}
			}
		}
		return null;
	}

	protected AbstractActivity findNodeTemplateActivity(final Collection<AbstractActivity> activities,
			final AbstractNodeTemplate nodeTemplate, final ActivityType type) {
		for (final AbstractActivity activity : activities) {
			if (activity.getType().equals(type)) {
				if (activity instanceof ANodeTemplateActivity) {
					if (((ANodeTemplateActivity) activity).getNodeTemplate().equals(nodeTemplate)) {
						return activity;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the number of the plugins registered with this planbuilder
	 *
	 * @return integer denoting the count of plugins
	 */
	public int registeredPlugins() {
		return this.pluginRegistry.getTypePlugins().size() + this.pluginRegistry.getDaPlugins().size()
				+ this.pluginRegistry.getIaPlugins().size() + this.pluginRegistry.getPostPlugins().size()
				+ this.pluginRegistry.getProvPlugins().size();
	}
}
