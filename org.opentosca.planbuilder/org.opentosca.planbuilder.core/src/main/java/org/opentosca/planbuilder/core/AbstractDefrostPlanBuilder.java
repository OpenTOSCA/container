package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.common.collect.Sets;
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
import org.opentosca.container.core.model.ModelUtils;

public abstract class AbstractDefrostPlanBuilder extends AbstractSimplePlanBuilder {

    static QName freezableComponentPolicy = new QName("http://opentosca.org/policytypes", "FreezableComponent");

    public AbstractDefrostPlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    public static AbstractPlan generatePOG(final String id, final TDefinitions definitions,
                                           final TServiceTemplate serviceTemplate,
                                           final Collection<TNodeTemplate> nodeTemplates,
                                           final Collection<TRelationshipTemplate> relationshipTemplates, Csar csar) {
        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        generateDOGActivitesAndLinks(activities, links, new HashMap<>(), nodeTemplates, new HashMap<>(),
            relationshipTemplates, csar);

        return new AbstractPlan(id, PlanType.BUILD, definitions, serviceTemplate, activities, links) {

        };
    }

    public static AbstractPlan generateDOG(final String id, final TDefinitions definitions,
                                           final TServiceTemplate serviceTemplate, Csar csar) {
        return generatePOG(id, definitions, serviceTemplate,
            serviceTemplate.getTopologyTemplate().getNodeTemplates(),
            serviceTemplate.getTopologyTemplate().getRelationshipTemplates(), csar
        );
    }

    private static void generateDOGActivitesAndLinks(final Collection<AbstractActivity> activities,
                                                     final Set<Link> links,
                                                     final Map<TNodeTemplate, AbstractActivity> nodeActivityMapping,
                                                     final Collection<TNodeTemplate> nodeTemplates,
                                                     final Map<TRelationshipTemplate, AbstractActivity> relationActivityMapping,
                                                     final Collection<TRelationshipTemplate> relationshipTemplates, Csar csar) {
        Collection<TNodeTemplate> nodeToStart = AbstractDefrostPlanBuilder.calculateNodesToStart(nodeTemplates, csar);

        for (final TNodeTemplate nodeTemplate : nodeTemplates) {

            if (nodeToStart.contains(nodeTemplate)) {
                final AbstractActivity activity =
                    new NodeTemplateActivity(nodeTemplate.getId() + "_no_activity", ActivityType.NONE, nodeTemplate);
                activities.add(activity);
                nodeActivityMapping.put(nodeTemplate, activity);
            } else if (AbstractDefrostPlanBuilder.hasFreezeableComponentPolicy(nodeTemplate)) {
                final AbstractActivity activity = new NodeTemplateActivity(nodeTemplate.getId() + "_defrost_activity",
                    ActivityType.DEFROST, nodeTemplate);
                activities.add(activity);
                nodeActivityMapping.put(nodeTemplate, activity);
            } else {
                final AbstractActivity activity = new NodeTemplateActivity(
                    nodeTemplate.getId() + "_provisioning_activity", ActivityType.PROVISIONING, nodeTemplate);
                activities.add(activity);
                nodeActivityMapping.put(nodeTemplate, activity);
            }
        }

        for (final TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity =
                new RelationshipTemplateActivity(relationshipTemplate.getId() + "_provisioning_activity",
                    ActivityType.PROVISIONING, relationshipTemplate);
            activities.add(activity);
            relationActivityMapping.put(relationshipTemplate, activity);
        }

        connectActivities(links, nodeActivityMapping, relationActivityMapping, relationshipTemplates, csar);
    }

    static void connectActivities(Set<Link> links, Map<TNodeTemplate, AbstractActivity> nodeActivityMapping, Map<TRelationshipTemplate, AbstractActivity> relationActivityMapping, Collection<TRelationshipTemplate> relationshipTemplates, Csar csar) {
        for (final TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity = relationActivityMapping.get(relationshipTemplate);
            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate, csar);
            TNodeTemplate source = ModelUtils.getSource(relationshipTemplate, csar);
            TNodeTemplate target = ModelUtils.getTarget(relationshipTemplate, csar);
            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(nodeActivityMapping.get(source), activity));
                links.add(new Link(nodeActivityMapping.get(target), activity));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(nodeActivityMapping.get(target), activity));
                links.add(new Link(activity, nodeActivityMapping.get(source)));
            }
        }
    }

    private static Collection<TNodeTemplate> calculateNodesToStart(Collection<TNodeTemplate> nodes, Csar csar) {
        Collection<TNodeTemplate> nodesToStart = new HashSet<>();

        for (TNodeTemplate node : nodes) {
            Set<TNodeTemplate> nodesToSink = Sets.newHashSet();
            ModelUtils.getNodesFromNodeToSink(node, Types.hostedOnRelationType, nodesToSink, csar);
            for (TNodeTemplate nodeToSink : nodesToSink) {
                if (!nodeToSink.equals(node) && AbstractDefrostPlanBuilder.hasFreezeableComponentPolicy(nodeToSink)) {
                    nodesToStart.add(node);
                    break;
                }
            }
        }

        return nodesToStart;
    }

    protected static boolean hasFreezeableComponentPolicy(TNodeTemplate nodeTemplate) {
        if (Objects.nonNull(nodeTemplate.getPolicies())) {
            for (TPolicy policy : nodeTemplate.getPolicies()) {
                if (policy.getPolicyType().equals(AbstractDefrostPlanBuilder.freezableComponentPolicy)) {
                    return true;
                }
            }
        }
        return false;
    }
}
