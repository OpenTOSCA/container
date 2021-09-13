package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractTransformationPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

/**
 * Abstract Class for generating an AbstractPlan implementing a Transformation Function from a Source Model to a Target
 * Model and their respective instances
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public abstract class AbstractTransformingPlanbuilder extends AbstractPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractTransformingPlanbuilder.class);

    public AbstractTransformingPlanbuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    @Override
    public PlanType createdPlanType() {
        return PlanType.MANAGEMENT;
    }

    /**
     * <p>
     * Creates a BuildPlan in WS-BPEL 2.0 by using the the referenced source and target service templates as the
     * transforming function between two models.
     * </p>
     *
     * @param sourceCsar          the name of the source csar
     * @param sourceDefinitions       the id of the source definitions inside the referenced source csar
     * @param sourceServiceTemplateId the id of the source service templates inside the referenced definitions
     * @param targetCsar          the name of the target csar
     * @param targetDefinitions       the id of the target definitions inside the referenced target csar
     * @param targetServiceTemplateId the id of the target service templates inside the referenced definitions
     * @return a single AbstractPlan with a concrete implementation of a transformation function from the source to the
     * target topology
     */
    abstract public AbstractPlan buildPlan(Csar sourceCsar, AbstractDefinitions sourceDefinitions,
                                           QName sourceServiceTemplateId, Csar targetCsar,
                                           AbstractDefinitions targetDefinitions, QName targetServiceTemplateId);

    /**
     * Generates a Set of Plans that is generated based on the given source and target definitions. This generation is
     * done for each Topology Template defined in both definitions therefore for each combination of source and target
     * topology template a plan is generated
     *
     * @param sourceCsar    the name of the source csar
     * @param sourceDefinitions the id of the source definitions inside the referenced source csar
     * @param targetCsar    the name of the target csar
     * @param targetDefinitions the id of the target definitions inside the referenced target csar
     * @return a List of AbstractPlans
     */
    abstract public List<AbstractPlan> buildPlans(Csar sourceCsar, AbstractDefinitions sourceDefinitions,
                                                  Csar targetCsar, AbstractDefinitions targetDefinitions);

    public AbstractTransformationPlan generateTFOG(Csar sourceCsar, AbstractDefinitions sourceDefinitions,
                                                   AbstractServiceTemplate sourceServiceTemplate,
                                                   Collection<TNodeTemplate> sourceNodeTemplates,
                                                   Collection<TRelationshipTemplate> sourceRelationshipTemplates,
                                                   Csar targetCsar, AbstractDefinitions targetDefinitions,
                                                   AbstractServiceTemplate targetServiceTemplate,
                                                   Collection<TNodeTemplate> targetNodeTemplates,
                                                   Collection<TRelationshipTemplate> targetRelationshipTemplates, String idSuffix) {

        Set<TNodeTemplate> maxCommonSubgraph =
            this.getMaxCommonSubgraph(new HashSet<>(sourceNodeTemplates),
                new HashSet<>(sourceNodeTemplates),
                new HashSet<>(targetNodeTemplates),
                new HashSet<>());

        // find valid subset inside common subgraph, i.e.:
        // any component that is a platform node (every node without outgoing
        // hostedOn edges), or is a node in the subgraph where its (transitive) platform
        // nodes are also in the subgraph are valid
        Set<TNodeTemplate> deployableMaxCommonSubgraph = this.getDeployableSubgraph(new HashSet<>(this.getCorrespondingNodes(maxCommonSubgraph, targetNodeTemplates)), sourceCsar, targetCsar);

        // determine steps which have to be deleted from the original topology
        Set<TNodeTemplate> nodesToTerminate = new HashSet<>(sourceNodeTemplates);
        nodesToTerminate.removeAll(deployableMaxCommonSubgraph);
        Collection<TRelationshipTemplate> relationsToTerminate = this.getOutgoingRelations(nodesToTerminate, sourceCsar);

        AbstractPlan termPlan = AbstractTerminationPlanBuilder.generateTOG("transformTerminate"
                + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId() + "_" + idSuffix, sourceDefinitions, sourceServiceTemplate,
            nodesToTerminate, relationsToTerminate, sourceCsar);

        // migrate node instances from old service instance to new service instance
        AbstractPlan migrateInstancePlan =
            this.generateInstanceMigrationPlan(deployableMaxCommonSubgraph,
                this.getConnectingEdges(sourceRelationshipTemplates,
                    deployableMaxCommonSubgraph, sourceCsar),
                sourceDefinitions, targetDefinitions, sourceServiceTemplate,
                targetServiceTemplate, sourceCsar);

        // determine steps which have to be start within the new topology
        Set<TNodeTemplate> nodesToStart = new HashSet<>(targetNodeTemplates);
        nodesToStart.removeAll(this.getCorrespondingNodes(deployableMaxCommonSubgraph, targetNodeTemplates));

        Collection<TRelationshipTemplate> relationsToStart = this.getDeployableSubgraph(targetNodeTemplates, this.getOutgoingRelations(nodesToStart,targetCsar), sourceCsar);

        AbstractPlan startPlan =
            AbstractBuildPlanBuilder.generatePOG("transformStart" + sourceDefinitions.getId() + "_to_"
                + targetDefinitions.getId(), targetDefinitions, targetServiceTemplate, nodesToStart, relationsToStart, sourceCsar);

        AbstractTransformationPlan transPlan =
            this.mergePlans("transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(), PlanType.TRANSFORMATION, termPlan, migrateInstancePlan);

        transPlan = this.mergePlans(
            "transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId() + "_" + idSuffix,
            PlanType.TRANSFORMATION, transPlan, startPlan);

        return transPlan;
    }

    public Collection<TRelationshipTemplate> getDeployableSubgraph(Collection<TNodeTemplate> nodes, Collection<TRelationshipTemplate> relations, Csar csar) {
        Collection<TRelationshipTemplate> result = new HashSet<>();
        for (TRelationshipTemplate rel : relations) {
            if (nodes.contains(ModelUtils.getSource(rel, csar)) && nodes.contains(ModelUtils.getTarget(rel, csar))) {
                result.add(rel);
            }
        }
        return result;
    }

    public AbstractTransformationPlan generateTFOG(Csar sourceCsar, AbstractDefinitions sourceDefinitions,
                                                   AbstractServiceTemplate sourceServiceTemplate, Csar targetCsar,
                                                   AbstractDefinitions targetDefinitions,
                                                   AbstractServiceTemplate targetServiceTemplate, String idSuffix) {
        return this.generateTFOG(sourceCsar, sourceDefinitions, sourceServiceTemplate,
            sourceServiceTemplate.getTopologyTemplate().getNodeTemplates(),
            sourceServiceTemplate.getTopologyTemplate().getRelationshipTemplates(), targetCsar,
            targetDefinitions, targetServiceTemplate,
            targetServiceTemplate.getTopologyTemplate().getNodeTemplates(),
            targetServiceTemplate.getTopologyTemplate().getRelationshipTemplates(), idSuffix);
    }

    /**
     * Generates an abstract order of activities to transform from the source service template to the target service
     * template
     *
     * @param sourceCsarName    the name of the source csar
     * @param sourceDefinitions the id of the source definitions inside the referenced source csar
     * @param targetCsarName    the name of the target csar
     * @param targetDefinitions the id of the target definitions inside the referenced target csar
     * @return a single AbstractPlan containing abstract activities for a transformation function from the source to the
     * target topology
     */
    public AbstractTransformationPlan _generateTFOG(Csar sourceCsarName, AbstractDefinitions sourceDefinitions,
                                                    AbstractServiceTemplate sourceServiceTemplate,
                                                    Csar targetCsarName, AbstractDefinitions targetDefinitions,
                                                    AbstractServiceTemplate targetServiceTemplate) {
        AbstractTopologyTemplate sourceTopology = sourceServiceTemplate.getTopologyTemplate();
        AbstractTopologyTemplate targetTopology = targetServiceTemplate.getTopologyTemplate();

        Set<TNodeTemplate> maxCommonSubgraph =
            this.getMaxCommonSubgraph(new HashSet<>(sourceTopology.getNodeTemplates()),
                new HashSet<>(sourceTopology.getNodeTemplates()),
                new HashSet<>(targetTopology.getNodeTemplates()),
                new HashSet<>());

        // find valid subset inside common subgraph, i.e.:
        // any component that is a platform node (every node without outgoing
        // hostedOn edges), or is a node in the subgraph where its (transitive) platform
        // nodes are also in the subgraph are valid
        Set<TNodeTemplate> deployableMaxCommonSubgraph = this.getDeployableSubgraph(maxCommonSubgraph, sourceCsarName, targetCsarName);

        // determine steps which have to be deleted from the original topology
        Set<TNodeTemplate> nodesToTerminate =
            new HashSet<>(sourceTopology.getNodeTemplates());
        nodesToTerminate.removeAll(deployableMaxCommonSubgraph);
        Collection<TRelationshipTemplate> relationsToTerminate = this.getOutgoingRelations(nodesToTerminate, targetCsarName);

        AbstractPlan termPlan = AbstractTerminationPlanBuilder.generateTOG("transformTerminate"
                + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(), sourceDefinitions, sourceServiceTemplate,
            nodesToTerminate, relationsToTerminate, sourceCsarName);

        // migrate node instances from old service instance to new service instance
        AbstractPlan migrateInstancePlan =
            this.generateInstanceMigrationPlan(deployableMaxCommonSubgraph,
                this.getConnectingEdges(sourceTopology.getRelationshipTemplates(),
                    deployableMaxCommonSubgraph, sourceCsarName),
                sourceDefinitions, targetDefinitions, sourceServiceTemplate,
                targetServiceTemplate, sourceCsarName);

        // determine steps which have to be start within the new topology
        Set<TNodeTemplate> nodesToStart = new HashSet<>(targetTopology.getNodeTemplates());
        nodesToStart.removeAll(this.getCorrespondingNodes(deployableMaxCommonSubgraph,
            targetTopology.getNodeTemplates()));
        Collection<TRelationshipTemplate> relationsToStart = this.getOutgoingRelations(nodesToStart, targetCsarName);

        AbstractPlan startPlan =
            AbstractBuildPlanBuilder.generatePOG("transformStart" + sourceDefinitions.getId() + "_to_"
                + targetDefinitions.getId(), targetDefinitions, targetServiceTemplate, nodesToStart, relationsToStart, sourceCsarName);

        AbstractTransformationPlan transPlan =
            this.mergePlans("transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(), PlanType.TRANSFORMATION, termPlan, migrateInstancePlan);

        transPlan = this.mergePlans(
            "transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(),
            PlanType.TRANSFORMATION, transPlan, startPlan);

        return transPlan;
    }

    private AbstractTransformationPlan generateInstanceMigrationPlan(Collection<TNodeTemplate> nodeTemplates,
                                                                     Collection<TRelationshipTemplate> relationshipTemplates,
                                                                     AbstractDefinitions sourceDefinitions,
                                                                     AbstractDefinitions targetDefinitions,
                                                                     AbstractServiceTemplate sourceServiceTemplate,
                                                                     AbstractServiceTemplate targetServiceTemplate, Csar sourceCsar) {
        // General flow is as within a build plan

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        this.generateIMOGActivitesAndLinks(activities, links, new HashMap<>(), nodeTemplates, new HashMap<>(),
            relationshipTemplates, sourceCsar);

        return new AbstractTransformationPlan(
            "migrateInstance" + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(),
            PlanType.TRANSFORMATION, sourceDefinitions, sourceServiceTemplate, targetDefinitions,
            targetServiceTemplate, activities, links);
    }

    private void generateIMOGActivitesAndLinks(final Collection<AbstractActivity> activities, final Set<Link> links,
                                               final Map<TNodeTemplate, AbstractActivity> nodeActivityMapping,
                                               final Collection<TNodeTemplate> nodeTemplates,
                                               final Map<TRelationshipTemplate, AbstractActivity> relationActivityMapping,
                                               final Collection<TRelationshipTemplate> relationshipTemplates, Csar sourceCsar) {
        for (final TNodeTemplate nodeTemplate : nodeTemplates) {
            final AbstractActivity activity = new NodeTemplateActivity(
                nodeTemplate.getId() + "_instance_migration_activity", ActivityType.MIGRATION, nodeTemplate);
            activities.add(activity);
            nodeActivityMapping.put(nodeTemplate, activity);
        }

        for (final TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity =
                new RelationshipTemplateActivity(relationshipTemplate.getId() + "_instance_migration_activity",
                    ActivityType.MIGRATION, relationshipTemplate);
            activities.add(activity);
            relationActivityMapping.put(relationshipTemplate, activity);
        }

        AbstractDefrostPlanBuilder.connectActivities(links, nodeActivityMapping, relationActivityMapping, relationshipTemplates, sourceCsar);
    }

    private Collection<TRelationshipTemplate> getConnectingEdges(Collection<TRelationshipTemplate> allEdges,
                                                                        Collection<TNodeTemplate> subgraphNodes, Csar csar) {
        Collection<TRelationshipTemplate> connectingEdges = new HashSet<>();

        for (TRelationshipTemplate rel : allEdges) {
            TNodeTemplate source = ModelUtils.getSource(rel, csar);
            TNodeTemplate target = ModelUtils.getTarget(rel, csar);
            if (subgraphNodes.contains(source) && subgraphNodes.contains(target)) {
                connectingEdges.add(rel);
            }
        }

        return connectingEdges;
    }

    private Collection<TNodeTemplate> getCorrespondingNodes(Collection<TNodeTemplate> subgraph,
                                                                   Collection<TNodeTemplate> graph) {
        Collection<TNodeTemplate> correspondingNodes = new HashSet<>();
        for (TNodeTemplate subgraphNode : subgraph) {
            TNodeTemplate correspondingNode = this.getCorrespondingNode(subgraphNode, graph);
            if (correspondingNode != null) {
                correspondingNodes.add(correspondingNode);
            }
        }

        return correspondingNodes;
    }

    protected TNodeTemplate getCorrespondingNode(TNodeTemplate subNode,
                                                        Collection<TNodeTemplate> graph) {
        for (TNodeTemplate graphNode : graph) {
            if (this.mappingEquals(subNode, graphNode)) {
                return graphNode;
            }
        }
        return null;
    }

    public TRelationshipTemplate getCorrespondingEdge(TRelationshipTemplate subEdge,
                                                             Collection<TRelationshipTemplate> graphEdges, Csar csar) {
        for (TRelationshipTemplate graphEdge : graphEdges) {
            if (this.mappingEquals(subEdge, graphEdge, csar)) {
                return graphEdge;
            }
        }
        return null;
    }

    private AbstractTransformationPlan mergePlans(String id, PlanType type, AbstractPlan plan1, AbstractPlan plan2) {

        Collection<AbstractActivity> activities = new HashSet<>();
        activities.addAll(plan1.getActivites());
        activities.addAll(plan2.getActivites());

        Collection<Link> links = new HashSet<>();
        links.addAll(plan1.getLinks());
        links.addAll(plan2.getLinks());

        Collection<AbstractActivity> sinks = plan1.getSinks();
        Collection<AbstractActivity> sources = plan2.getSources();

        // naively we connect each sink with each source
        for (AbstractActivity sink : sinks) {
            for (AbstractActivity source : sources) {
                links.add(new Link(sink, source));
            }
        }

        return new AbstractTransformationPlan(id, type, plan1.getDefinitions(), plan1.getServiceTemplate(),
            plan2.getDefinitions(), plan2.getServiceTemplate(), activities, links);
    }

    private Collection<TRelationshipTemplate> getOutgoingRelations(Set<TNodeTemplate> nodes, Csar csar) {
        Collection<TRelationshipTemplate> relations = new HashSet<>();
        for (TNodeTemplate node : nodes) {
            relations.addAll(ModelUtils.getOutgoingRelations(node, csar));
        }
        return relations;
    }

    private Collection<TNodeTemplate> getNeededNodes(TNodeTemplate nodeTemplate, Csar csar) {
        for (IPlanBuilderTypePlugin<?> typePlugin : this.pluginRegistry.getTypePlugins()) {
            if (typePlugin.canHandleCreate(csar, nodeTemplate)) {
                if (typePlugin instanceof IPlanBuilderTypePlugin.NodeDependencyInformationInterface) {
                    return ((IPlanBuilderTypePlugin.NodeDependencyInformationInterface) typePlugin).getCreateDependencies(nodeTemplate, csar);
                }
            }
        }
        return null;
    }

    public Set<TNodeTemplate> getDeployableSubgraph(Set<TNodeTemplate> graph, Csar sourceCsar, Csar targetCsar) {
        Set<TNodeTemplate> validDeploymentSubgraph = new HashSet<>(graph);
        Set<TNodeTemplate> toRemove = new HashSet<>();

        for (TNodeTemplate node : graph) {

            if (this.isRunning(node) && this.hasNoHostingNodes(node, sourceCsar)) {
                continue;
            }

            Collection<TNodeTemplate> neededNodes = this.getNeededNodes(node, sourceCsar);

            // no plugin found that can deploy given node on whole topology
            if (neededNodes == null) {
                toRemove.add(node);
                continue;
            }

            // if the needed nodes are not in the graph we cannot deploy it
            if (!this.contains(graph, neededNodes)) {
                toRemove.add(node);
            }
        }

        if (toRemove.isEmpty()) {
            return validDeploymentSubgraph;
        } else {
            validDeploymentSubgraph.removeAll(toRemove);
            return getDeployableSubgraph(validDeploymentSubgraph, sourceCsar, targetCsar);
        }
    }

    private boolean hasNoHostingNodes(TNodeTemplate nodeTemplate, Csar csar) {
        for (TRelationshipTemplate rel : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            if (rel.getType().equals(Types.hostedOnRelationType) | rel.getType().equals(Types.dependsOnRelationType)) {
                return false;
            }
        }

        return true;
    }

    private boolean contains(Collection<TNodeTemplate> subgraph1, Collection<TNodeTemplate> subgraph2) {

        for (TNodeTemplate nodeInGraph2 : subgraph2) {
            boolean matched = false;
            for (TNodeTemplate nodeInGraph1 : subgraph1) {
                if (this.mappingEquals(nodeInGraph1, nodeInGraph2)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                // element in subgraph2 is not in subgraph 1
                return false;
            }
        }

        return true;
    }

    // TODO FIXME this is a really naive implementation until we can integrate a
    //  proper(i.e. efficient) subgraph calculation based on https://stackoverflow.com/a/14644158
    private Set<TNodeTemplate> getMaxCommonSubgraph(Set<TNodeTemplate> vertices,
                                                           Set<TNodeTemplate> graph1,
                                                           Set<TNodeTemplate> graph2,
                                                           Set<TNodeTemplate> currentSubset) {

        LOG.debug("Finding MaxCommon Subgraph with vertices {}", this.printCandidate(vertices));
        if (vertices.isEmpty()) {
            if (this.isCommonSubgraph(graph1, graph2, currentSubset)) {
                LOG.debug("Returning the current subset of {}", this.printCandidate(currentSubset));
                return new HashSet<>(currentSubset);
            } else {
                return new HashSet<>();
            }
        }

        TNodeTemplate v = this.pop(vertices);

        LOG.debug("Removed vertex {}", v.getId());
        Set<TNodeTemplate> cand1 = this.getMaxCommonSubgraph(vertices, graph1, graph2, currentSubset);
        currentSubset.add(v);
        LOG.debug("Current subset {}", this.printCandidate(currentSubset));

        Set<TNodeTemplate> cand2 = new HashSet<>();

        if (this.isCommonSubgraph(graph1, graph2, currentSubset)) {
            cand2 = this.getMaxCommonSubgraph(vertices, graph1, graph2, currentSubset);
        } else {
            LOG.debug("Removing vertex {} from current subset {}", v.getId(), this.printCandidate(currentSubset));
            currentSubset.remove(v);
        }

        LOG.debug("Current candidates:");
        LOG.debug("Candidate1: {}", this.printCandidate(cand1));
        LOG.debug("Candidate2: {}", this.printCandidate(cand2));

        if (cand1.size() > cand2.size()) {
            LOG.debug("Returning cand1");
        } else {
            LOG.debug("Returning cand2");
        }

        return (cand1.size() > cand2.size()) ? cand1 : cand2;
    }

    private String printCandidate(Collection<TNodeTemplate> nodeTemplates) {
        StringBuilder print = new StringBuilder("{");

        TNodeTemplate[] nodes = nodeTemplates.toArray(new TNodeTemplate[0]);

        for (int i = 0; i < nodes.length; i++) {
            print.append(nodes[i].getId());
            if (i + 1 < nodes.length) {
                print.append(",");
            }
        }

        print.append("}");

        return print.toString();
    }

    private TNodeTemplate pop(Set<TNodeTemplate> nodes) {
        TNodeTemplate pop = null;

        Iterator<TNodeTemplate> iter = nodes.iterator();

        if (iter.hasNext()) {
            pop = iter.next();
        }

        nodes.remove(pop);

        return pop;
    }

    private boolean isCommonSubgraph(Set<TNodeTemplate> graph1, Set<TNodeTemplate> graph2,
                                     Set<TNodeTemplate> subgraph) {

        for (TNodeTemplate nodeTemplate : subgraph) {
            boolean matchedIn1 = false;
            boolean matchedIn2 = false;

            for (TNodeTemplate nodeIn1 : graph1) {
                if (this.mappingEquals(nodeTemplate, nodeIn1)) {
                    matchedIn1 = true;
                    break;
                }
            }

            for (TNodeTemplate nodeIn2 : graph2) {
                if (this.mappingEquals(nodeTemplate, nodeIn2)) {
                    matchedIn2 = true;
                    break;
                }
            }

            if (!matchedIn1 | !matchedIn2) {
                return false;
            }
        }

        return true;
    }

    private boolean mappingEquals(TRelationshipTemplate rel1, TRelationshipTemplate rel2, Csar csar) {
        if (!rel1.getType().equals(rel2.getType())) {
            return false;
        }

        // really weak and messy check incoming!
        return this.mappingEquals(ModelUtils.getSource(rel1, csar), ModelUtils.getSource(rel2, csar))
            && this.mappingEquals(ModelUtils.getTarget(rel1, csar), ModelUtils.getTarget(rel2, csar));
    }

    private boolean mappingEquals(TNodeTemplate node1, TNodeTemplate node2) {
        LOG.debug("Matching node {} with node {} ", node1.getId(), node2.getId());
        if (!node1.getType().equals(node2.getType())) {
            return false;
        }

        if (node1.getDeploymentArtifacts().size() != node2.getDeploymentArtifacts().size()) {
            return false;
        } else {
            for (TDeploymentArtifact da : node1.getDeploymentArtifacts()) {
                boolean matched = false;
                for (TDeploymentArtifact da2 : node2.getDeploymentArtifacts()) {
                    if (da.getArtifactType().equals(da.getArtifactType())) {
                        if (da.getArtifactRef().equals(da2.getArtifactRef())) {
                            // up to this point only the type and id of the artifact template match, a deeper mathcing
                            // would really look at the references and stuff, but we assume that artifact template id's
                            // are unique across multiple service templates
                            matched = true;
                        }
                    }
                }
                if (!matched) {
                    return false;
                }
            }
        }

        // Maybe add it later

        // This check is pretty heavy if i think about the State Property or changes in
        // values etc.
        // FIXME? Check for values as well?
        if (!ModelUtils.asMap(node1.getProperties()).keySet().containsAll(ModelUtils.asMap(node2.getProperties()).keySet())) {
            return false;
        }

        if (!ModelUtils.getElementName(node1.getProperties()).equals(ModelUtils.getElementName(node2.getProperties()))) {
            return false;
        }

        if (!ModelUtils.getNamespace(node1.getProperties()).equals(ModelUtils.getNamespace(node2.getProperties()))) {
            return false;
        }

        LOG.debug("Matched node {} with node {} ", node1.getId(), node2.getId());

        return node1.getId().equals(node2.getId());
    }
}
