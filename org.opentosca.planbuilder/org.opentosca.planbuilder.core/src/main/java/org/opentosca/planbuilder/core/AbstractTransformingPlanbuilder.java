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

import org.opentosca.container.core.convention.Types;
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
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param sourceCsarName          the name of the source csar
     * @param sourceDefinitions       the id of the source definitions inside the referenced source csar
     * @param sourceServiceTemplateId the id of the source service templates inside the referenced definitions
     * @param targetCsarName          the name of the target csar
     * @param targetDefinitions       the id of the target definitions inside the referenced target csar
     * @param targetServiceTemplateId the id of the target service templates inside the referenced definitions
     * @return a single AbstractPlan with a concrete implementation of a transformation function from the source to the
     * target topology
     */
    abstract public AbstractPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                           QName sourceServiceTemplateId, String targetCsarName,
                                           AbstractDefinitions targetDefinitions, QName targetServiceTemplateId);

    /**
     * Generates a Set of Plans that is generated based on the given source and target definitions. This generation is
     * done for each Topology Template defined in both definitions therefore for each combination of source and target
     * topology template a plan is generated
     *
     * @param sourceCsarName    the name of the source csar
     * @param sourceDefinitions the id of the source definitions inside the referenced source csar
     * @param targetCsarName    the name of the target csar
     * @param targetDefinitions the id of the target definitions inside the referenced target csar
     * @return a List of AbstractPlans
     */
    abstract public List<AbstractPlan> buildPlans(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                                  String targetCsarName, AbstractDefinitions targetDefinitions);

    public AbstractTransformationPlan generateTFOG(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                                   AbstractServiceTemplate sourceServiceTemplate,
                                                   Collection<AbstractNodeTemplate> sourceNodeTemplates,
                                                   Collection<AbstractRelationshipTemplate> sourceRelationshipTemplates,
                                                   String targetCsarName, AbstractDefinitions targetDefinitions,
                                                   AbstractServiceTemplate targetServiceTemplate,
                                                   Collection<AbstractNodeTemplate> targetNodeTemplates,
                                                   Collection<AbstractRelationshipTemplate> targetRelationshipTemplates) {

        Set<AbstractNodeTemplate> maxCommonSubgraph =
            this.getMaxCommonSubgraph(new HashSet<AbstractNodeTemplate>(sourceNodeTemplates),
                new HashSet<AbstractNodeTemplate>(sourceNodeTemplates),
                new HashSet<AbstractNodeTemplate>(targetNodeTemplates),
                new HashSet<AbstractNodeTemplate>());

        // find valid subset inside common subgraph, i.e.:
        // any component that is a platform node (every node without outgoing
        // hostedOn edges), or is a node in the subgraph where its (transitive) platform
        // nodes are also in the subgraph are valid
        Set<AbstractNodeTemplate> deployableMaxCommonSubgraph = this.getDeployableSubgraph(new HashSet<AbstractNodeTemplate>(this.getCorrespondingNodes(maxCommonSubgraph, targetNodeTemplates)));

        // determine steps which have to be deleted from the original topology
        Set<AbstractNodeTemplate> nodesToTerminate = new HashSet<AbstractNodeTemplate>(sourceNodeTemplates);
        nodesToTerminate.removeAll(deployableMaxCommonSubgraph);
        Collection<AbstractRelationshipTemplate> relationsToTerminate = this.getOutgoingRelations(nodesToTerminate);

        AbstractPlan termPlan = AbstractTerminationPlanBuilder.generateTOG("transformTerminate"
                + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(), sourceDefinitions, sourceServiceTemplate,
            nodesToTerminate, relationsToTerminate);

        // migrate node instances from old service instance to new service instance
        AbstractPlan migrateInstancePlan =
            this.generateInstanceMigrationPlan(deployableMaxCommonSubgraph,
                this.getConnectingEdges(sourceRelationshipTemplates,
                    deployableMaxCommonSubgraph),
                sourceDefinitions, targetDefinitions, sourceServiceTemplate,
                targetServiceTemplate);

        // determine steps which have to be start within the new topology
        Set<AbstractNodeTemplate> nodesToStart = new HashSet<AbstractNodeTemplate>(targetNodeTemplates);
        nodesToStart.removeAll(this.getCorrespondingNodes(deployableMaxCommonSubgraph, targetNodeTemplates));

        Collection<AbstractRelationshipTemplate> relationsToStart = this.getDeployableSubgraph(targetNodeTemplates, this.getOutgoingRelations(nodesToStart));

        AbstractPlan startPlan =
            AbstractBuildPlanBuilder.generatePOG("transformStart" + sourceDefinitions.getId() + "_to_"
                + targetDefinitions.getId(), targetDefinitions, targetServiceTemplate, nodesToStart, relationsToStart);

        AbstractTransformationPlan transPlan =
            this.mergePlans("transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(), PlanType.TRANSFORMATION, termPlan, migrateInstancePlan);

        transPlan = this.mergePlans(
            "transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(),
            PlanType.TRANSFORMATION, transPlan, startPlan);

        return transPlan;
    }

    public Collection<AbstractRelationshipTemplate> getDeployableSubgraph(Collection<AbstractNodeTemplate> nodes, Collection<AbstractRelationshipTemplate> relations) {
        Collection<AbstractRelationshipTemplate> result = new HashSet<AbstractRelationshipTemplate>();
        for (AbstractRelationshipTemplate rel : relations) {
            if (nodes.contains(rel.getSource()) && nodes.contains(rel.getTarget())) {
                result.add(rel);
            }
        }
        return result;
    }

    public AbstractTransformationPlan generateTFOG(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                                   AbstractServiceTemplate sourceServiceTemplate, String targetCsarName,
                                                   AbstractDefinitions targetDefinitions,
                                                   AbstractServiceTemplate targetServiceTemplate) {
        return this.generateTFOG(sourceCsarName, sourceDefinitions, sourceServiceTemplate,
            sourceServiceTemplate.getTopologyTemplate().getNodeTemplates(),
            sourceServiceTemplate.getTopologyTemplate().getRelationshipTemplates(), targetCsarName,
            targetDefinitions, targetServiceTemplate,
            targetServiceTemplate.getTopologyTemplate().getNodeTemplates(),
            targetServiceTemplate.getTopologyTemplate().getRelationshipTemplates());
    }

    /**
     * Generates an abstract order of activities to transform from the source service template to the target service
     * template
     *
     * @param sourceCsarName          the name of the source csar
     * @param sourceDefinitions       the id of the source definitions inside the referenced source csar
     * @param sourceServiceTemplateId the id of the source service templates inside the referenced definitions
     * @param targetCsarName          the name of the target csar
     * @param targetDefinitions       the id of the target definitions inside the referenced target csar
     * @param targetServiceTemplateId the id of the target service templates inside the referenced definitions
     * @return a single AbstractPlan containing abstract activities for a transformation function from the source to the
     * target topology
     */
    public AbstractTransformationPlan _generateTFOG(String sourceCsarName, AbstractDefinitions sourceDefinitions,
                                                    AbstractServiceTemplate sourceServiceTemplate,
                                                    String targetCsarName, AbstractDefinitions targetDefinitions,
                                                    AbstractServiceTemplate targetServiceTemplate) {
        AbstractTopologyTemplate sourceTopology = sourceServiceTemplate.getTopologyTemplate();
        AbstractTopologyTemplate targetTopology = targetServiceTemplate.getTopologyTemplate();

        Set<AbstractNodeTemplate> maxCommonSubgraph =
            this.getMaxCommonSubgraph(new HashSet<AbstractNodeTemplate>(sourceTopology.getNodeTemplates()),
                new HashSet<AbstractNodeTemplate>(sourceTopology.getNodeTemplates()),
                new HashSet<AbstractNodeTemplate>(targetTopology.getNodeTemplates()),
                new HashSet<AbstractNodeTemplate>());

        // find valid subset inside common subgraph, i.e.:
        // any component that is a platform node (every node without outgoing
        // hostedOn edges), or is a node in the subgraph where its (transitive) platform
        // nodes are also in the subgraph are valid
        Set<AbstractNodeTemplate> deployableMaxCommonSubgraph = this.getDeployableSubgraph(maxCommonSubgraph);

        // determine steps which have to be deleted from the original topology
        Set<AbstractNodeTemplate> nodesToTerminate =
            new HashSet<AbstractNodeTemplate>(sourceTopology.getNodeTemplates());
        nodesToTerminate.removeAll(deployableMaxCommonSubgraph);
        Collection<AbstractRelationshipTemplate> relationsToTerminate = this.getOutgoingRelations(nodesToTerminate);

        AbstractPlan termPlan = AbstractTerminationPlanBuilder.generateTOG("transformTerminate"
                + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(), sourceDefinitions, sourceServiceTemplate,
            nodesToTerminate, relationsToTerminate);

        // migrate node instances from old service instance to new service instance
        AbstractPlan migrateInstancePlan =
            this.generateInstanceMigrationPlan(deployableMaxCommonSubgraph,
                this.getConnectingEdges(sourceTopology.getRelationshipTemplates(),
                    deployableMaxCommonSubgraph),
                sourceDefinitions, targetDefinitions, sourceServiceTemplate,
                targetServiceTemplate);

        // determine steps which have to be start within the new topology
        Set<AbstractNodeTemplate> nodesToStart = new HashSet<AbstractNodeTemplate>(targetTopology.getNodeTemplates());
        nodesToStart.removeAll(this.getCorrespondingNodes(deployableMaxCommonSubgraph,
            targetTopology.getNodeTemplates()));
        Collection<AbstractRelationshipTemplate> relationsToStart = this.getOutgoingRelations(nodesToStart);

        AbstractPlan startPlan =
            AbstractBuildPlanBuilder.generatePOG("transformStart" + sourceDefinitions.getId() + "_to_"
                + targetDefinitions.getId(), targetDefinitions, targetServiceTemplate, nodesToStart, relationsToStart);

        AbstractTransformationPlan transPlan =
            this.mergePlans("transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(), PlanType.TRANSFORMATION, termPlan, migrateInstancePlan);

        transPlan = this.mergePlans(
            "transformationPlan_" + termPlan.getServiceTemplate().getId() + "_to_"
                + startPlan.getServiceTemplate().getId(),
            PlanType.TRANSFORMATION, transPlan, startPlan);

        return transPlan;
    }

    private AbstractTransformationPlan generateInstanceMigrationPlan(Collection<AbstractNodeTemplate> nodeTemplates,
                                                                     Collection<AbstractRelationshipTemplate> relationshipTemplates,
                                                                     AbstractDefinitions sourceDefinitions,
                                                                     AbstractDefinitions targetDefinitions,
                                                                     AbstractServiceTemplate sourceServiceTemplate,
                                                                     AbstractServiceTemplate targetServiceTemplate) {

        // General flow is as within a build plan

        final Collection<AbstractActivity> activities = new ArrayList<>();
        final Set<Link> links = new HashSet<>();
        final Map<AbstractNodeTemplate, AbstractActivity> nodeMapping = new HashMap<>();
        final Map<AbstractRelationshipTemplate, AbstractActivity> relationMapping = new HashMap<>();

        this.generateIMOGActivitesAndLinks(activities, links, nodeMapping, nodeTemplates, relationMapping,
            relationshipTemplates);

        return new AbstractTransformationPlan(
            "migrateInstance" + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(),
            PlanType.TRANSFORMATION, sourceDefinitions, sourceServiceTemplate, targetDefinitions,
            targetServiceTemplate, activities, links);
    }

    private void generateIMOGActivitesAndLinks(final Collection<AbstractActivity> activities, final Set<Link> links,
                                               final Map<AbstractNodeTemplate, AbstractActivity> nodeActivityMapping,
                                               final Collection<AbstractNodeTemplate> nodeTemplates,
                                               final Map<AbstractRelationshipTemplate, AbstractActivity> relationActivityMapping,
                                               final Collection<AbstractRelationshipTemplate> relationshipTemplates) {
        for (final AbstractNodeTemplate nodeTemplate : nodeTemplates) {
            final AbstractActivity activity = new NodeTemplateActivity(
                nodeTemplate.getId() + "_instance_migration_activity", ActivityType.MIGRATION, nodeTemplate);
            activities.add(activity);
            nodeActivityMapping.put(nodeTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity =
                new RelationshipTemplateActivity(relationshipTemplate.getId() + "_instance_migration_activity",
                    ActivityType.MIGRATION, relationshipTemplate);
            activities.add(activity);
            relationActivityMapping.put(relationshipTemplate, activity);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            final AbstractActivity activity = relationActivityMapping.get(relationshipTemplate);
            final QName baseType = ModelUtils.getRelationshipBaseType(relationshipTemplate);
            if (baseType.equals(Types.connectsToRelationType)) {
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getSource()), activity));
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
            } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
                | baseType.equals(Types.deployedOnRelationType)) {
                links.add(new Link(nodeActivityMapping.get(relationshipTemplate.getTarget()), activity));
                links.add(new Link(activity, nodeActivityMapping.get(relationshipTemplate.getSource())));
            }
        }
    }

    private Collection<AbstractRelationshipTemplate> getConnectingEdges(Collection<AbstractRelationshipTemplate> allEdges,
                                                                        Collection<AbstractNodeTemplate> subgraphNodes) {
        Collection<AbstractRelationshipTemplate> connectingEdges = new HashSet<AbstractRelationshipTemplate>();

        for (AbstractRelationshipTemplate rel : allEdges) {
            if (subgraphNodes.contains(rel.getSource()) && subgraphNodes.contains(rel.getTarget())) {
                connectingEdges.add(rel);
            }
        }

        return connectingEdges;
    }

    private Collection<AbstractNodeTemplate> getCorrespondingNodes(Collection<AbstractNodeTemplate> subgraph,
                                                                   Collection<AbstractNodeTemplate> graph) {
        Collection<AbstractNodeTemplate> correspondingNodes = new HashSet<AbstractNodeTemplate>();
        for (AbstractNodeTemplate subgraphNode : subgraph) {
            AbstractNodeTemplate correspondingNode = null;
            if ((correspondingNode = this.getCorrespondingNode(subgraphNode, graph)) != null) {
                correspondingNodes.add(correspondingNode);
            }
        }

        return correspondingNodes;
    }

    protected AbstractNodeTemplate getCorrespondingNode(AbstractNodeTemplate subNode,
                                                        Collection<AbstractNodeTemplate> graph) {
        for (AbstractNodeTemplate graphNode : graph) {
            if (this.mappingEquals(subNode, graphNode)) {
                return graphNode;
            }
        }
        return null;
    }

    public AbstractRelationshipTemplate getCorrespondingEdge(AbstractRelationshipTemplate subEdge,
                                                             Collection<AbstractRelationshipTemplate> graphEdges) {
        for (AbstractRelationshipTemplate graphEdge : graphEdges) {
            if (this.mappingEquals(subEdge, graphEdge)) {
                return graphEdge;
            }
        }
        return null;
    }

    private AbstractTransformationPlan mergePlans(String id, PlanType type, AbstractPlan plan1, AbstractPlan plan2) {

        Collection<AbstractActivity> activities = new HashSet<AbstractActivity>();
        activities.addAll(plan1.getActivites());
        activities.addAll(plan2.getActivites());

        Collection<Link> links = new HashSet<Link>();
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

    private Collection<AbstractRelationshipTemplate> getOutgoingRelations(Set<AbstractNodeTemplate> nodes) {
        Collection<AbstractRelationshipTemplate> relations = new HashSet<AbstractRelationshipTemplate>();
        for (AbstractNodeTemplate node : nodes) {
            relations.addAll(node.getOutgoingRelations());
        }
        return relations;
    }

    private Collection<AbstractNodeTemplate> getNeededNodes(AbstractNodeTemplate nodeTemplate) {
        for (IPlanBuilderTypePlugin typePlugin : this.pluginRegistry.getTypePlugins()) {
            if (typePlugin.canHandleCreate(nodeTemplate)) {
                if (typePlugin instanceof IPlanBuilderTypePlugin.NodeDependencyInformationInterface) {
                    return ((IPlanBuilderTypePlugin.NodeDependencyInformationInterface) typePlugin).getCreateDependencies(nodeTemplate);
                }
            }
        }
        return null;
    }

    public Set<AbstractNodeTemplate> getDeployableSubgraph(Set<AbstractNodeTemplate> graph) {
        Set<AbstractNodeTemplate> validDeploymentSubgraph = new HashSet<AbstractNodeTemplate>(graph);
        Set<AbstractNodeTemplate> toRemove = new HashSet<AbstractNodeTemplate>();

        for (AbstractNodeTemplate node : graph) {

            if (this.isRunning(node) && this.hasNoHostingNodes(node)) {
                continue;
            }

            Collection<AbstractNodeTemplate> neededNodes = this.getNeededNodes(node);

            // no plugin found that can deploy given node on whole topology
            if (neededNodes == null) {
                toRemove.add(node);
                continue;
            }

            // if the needed nodes are not in the graph we cannot deploy it
            if (!this.contains(graph, neededNodes)) {
                toRemove.add(node);
                continue;
            }
        }

        if (toRemove.isEmpty()) {
            return validDeploymentSubgraph;
        } else {
            validDeploymentSubgraph.removeAll(toRemove);
            return getDeployableSubgraph(validDeploymentSubgraph);
        }
    }

    private boolean hasNoHostingNodes(AbstractNodeTemplate nodeTemplate) {
        for (AbstractRelationshipTemplate rel : nodeTemplate.getOutgoingRelations()) {
            if (rel.getType().equals(Types.hostedOnRelationType) | rel.getType().equals(Types.dependsOnRelationType)) {
                return false;
            }
        }

        return true;
    }

    private boolean contains(Collection<AbstractNodeTemplate> subgraph1, Collection<AbstractNodeTemplate> subgraph2) {

        for (AbstractNodeTemplate nodeInGraph2 : subgraph2) {
            boolean matched = false;
            for (AbstractNodeTemplate nodeInGraph1 : subgraph1) {
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
    private Set<AbstractNodeTemplate> getMaxCommonSubgraph(Set<AbstractNodeTemplate> vertices,
                                                           Set<AbstractNodeTemplate> graph1,
                                                           Set<AbstractNodeTemplate> graph2,
                                                           Set<AbstractNodeTemplate> currentSubset) {

        LOG.debug("Finding MaxCommon Subgraph with vertices {}", this.printCandidate(vertices));
        if (vertices.isEmpty()) {
            if (this.isCommonSubgraph(graph1, graph2, currentSubset)) {
                LOG.debug("Returning the current subset of {}", this.printCandidate(currentSubset));
                return new HashSet<AbstractNodeTemplate>(currentSubset);
            } else {
                return new HashSet<AbstractNodeTemplate>();
            }
        }

        AbstractNodeTemplate v = this.pop(vertices);

        LOG.debug("Removed vertex {}", v.getId());
        Set<AbstractNodeTemplate> cand1 = this.getMaxCommonSubgraph(vertices, graph1, graph2, currentSubset);
        currentSubset.add(v);
        LOG.debug("Current subset {}", this.printCandidate(currentSubset));

        Set<AbstractNodeTemplate> cand2 = new HashSet<AbstractNodeTemplate>();

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

    private String printCandidate(Collection<AbstractNodeTemplate> nodeTemplates) {
        String print = "{";

        AbstractNodeTemplate[] nodes = nodeTemplates.toArray(new AbstractNodeTemplate[nodeTemplates.size()]);

        for (int i = 0; i < nodes.length; i++) {
            print += nodes[i].getId();
            if (i + 1 < nodes.length) {
                print += ",";
            }
        }

        print += "}";

        return print;
    }

    private AbstractNodeTemplate pop(Set<AbstractNodeTemplate> nodes) {
        AbstractNodeTemplate pop = null;

        Iterator<AbstractNodeTemplate> iter = nodes.iterator();

        if (iter.hasNext()) {
            pop = iter.next();
        }

        nodes.remove(pop);

        return pop;
    }

    private boolean isCommonSubgraph(Set<AbstractNodeTemplate> graph1, Set<AbstractNodeTemplate> graph2,
                                     Set<AbstractNodeTemplate> subgraph) {

        for (AbstractNodeTemplate nodeTemplate : subgraph) {
            boolean matchedIn1 = false;
            boolean matchedIn2 = false;

            for (AbstractNodeTemplate nodeIn1 : graph1) {
                if (this.mappingEquals(nodeTemplate, nodeIn1)) {
                    matchedIn1 = true;
                    break;
                }
            }

            for (AbstractNodeTemplate nodeIn2 : graph2) {
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

    private boolean mappingEquals(AbstractRelationshipTemplate rel1, AbstractRelationshipTemplate rel2) {
        if (!rel1.getType().equals(rel2.getType())) {
            return false;
        }

        // really weak and messy check incoming!
        return this.mappingEquals(rel1.getSource(), rel2.getSource())
            && this.mappingEquals(rel1.getTarget(), rel2.getTarget());
    }

    private boolean mappingEquals(AbstractNodeTemplate node1, AbstractNodeTemplate node2) {
        LOG.debug("Matching node {} with node {} ", node1.getId(), node2.getId());
        if (!node1.getType().getId().equals(node2.getType().getId())) {
            return false;
        }

        if (node1.getDeploymentArtifacts().size() != node2.getDeploymentArtifacts().size()) {
            return false;
        } else {
            for (AbstractDeploymentArtifact da : node1.getDeploymentArtifacts()) {
                boolean matched = false;
                for (AbstractDeploymentArtifact da2 : node2.getDeploymentArtifacts()) {
                    if (da.getArtifactType().equals(da.getArtifactType())) {
                        if (da.getArtifactRef().getId().equals(da2.getArtifactRef().getId())) {
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
        if(!node1.getProperties().asMap().keySet().containsAll(node2.getProperties().asMap().keySet())) {
        	return false;
        }
        
        if(!node1.getProperties().getElementName().equals(node2.getProperties().getElementName())) {
        	return false;
        }
        
        if(node1.getProperties().getNamespace().equals(node2.getProperties().getNamespace())) {
        	return false;
        }
        
        LOG.debug("Matched node {} with node {} ", node1.getId(), node2.getId());

        return node1.getId().equals(node2.getId());
    }
}
