package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBuildPlanBuilder extends AbstractSimplePlanBuilder {

    public class PlanbuilderRuntimeException extends RuntimeException {

        public PlanbuilderRuntimeException (Exception e) {
            super(e);
        }

        public PlanbuilderRuntimeException (String s) {
            super(s);
        }

        public PlanbuilderRuntimeException (String s, Exception e) {
            super(s,e);
        }
    }

    private final static Logger LOG = LoggerFactory.getLogger(AbstractBuildPlanBuilder.class);

    public AbstractBuildPlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    protected static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
                                              final AbstractServiceTemplate serviceTemplate,
                                              final Collection<TNodeTemplate> nodeTemplates,
                                              final Collection<TRelationshipTemplate> relationshipTemplates, Csar csar) {
        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        generatePOGActivitiesAndLinks(activities, links, new HashMap<>(), nodeTemplates, new HashMap<>(),
            relationshipTemplates, csar);
        return new AbstractPlan(id, PlanType.BUILD, definitions, serviceTemplate, activities, links) { };
    }

    protected static AbstractPlan generatePOG(final String id, final AbstractDefinitions definitions,
                                              final AbstractServiceTemplate serviceTemplate, Csar csar) {

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<TNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
        final Map<TRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();

        final AbstractTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        generatePOGActivitiesAndLinks(activities, links, nodeMapping, topology.getNodeTemplates(), relationMapping,
            topology.getRelationshipTemplates(), csar);

        final AbstractPlan plan =
            new AbstractPlan(id, PlanType.BUILD, definitions, serviceTemplate, activities, links) { };

        LOG.debug("Generated the following plan: ");
        LOG.debug(plan.toString());
        return plan;

    }

    // Generate TOG and POG are too similar and are detected as duplicates.
    @SuppressWarnings("Duplicates")
    private static void generatePOGActivitiesAndLinks(final Collection<AbstractActivity> activities,
                                                      final Set<Link> links,
                                                      final Map<TNodeTemplate, AbstractActivity> nodeActivityMapping,
                                                      final Collection<TNodeTemplate> nodeTemplates,
                                                      final Map<TRelationshipTemplate, AbstractActivity> relationActivityMapping,
                                                      final Collection<TRelationshipTemplate> relationshipTemplates, Csar csar) {
        for (final TNodeTemplate nodeTemplate : nodeTemplates) {
            final AbstractActivity activity = new NodeTemplateActivity(nodeTemplate.getId() + "_provisioning_activity",
                ActivityType.PROVISIONING, nodeTemplate);
            activities.add(activity);
            nodeActivityMapping.put(nodeTemplate, activity);
        }

        for (final TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity = new RelationshipTemplateActivity(
                relationshipTemplate.getId() + "_provisioning_activity", ActivityType.PROVISIONING, relationshipTemplate);
            activities.add(activity);
            relationActivityMapping.put(relationshipTemplate, activity);

            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate, csar);
            AbstractActivity sourceActivity = nodeActivityMapping.get(ModelUtils.getSource(relationshipTemplate, csar));
            AbstractActivity targetActivity = nodeActivityMapping.get(ModelUtils.getSource(relationshipTemplate, csar));

            if (baseType.equals(Types.connectsToRelationType)) {
                if (sourceActivity != null) {
                    links.add(new Link(sourceActivity, activity));
                }
                if (targetActivity != null) {
                    links.add(new Link(targetActivity, activity));
                }
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                if (targetActivity != null) {
                    links.add(new Link(targetActivity, activity));
                }
                if (sourceActivity != null) {
                    links.add(new Link(activity, sourceActivity));
                }
            }
        }
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.BUILD;
    }
}
