package org.opentosca.planbuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractTransformationPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Class for generating an AbstractPlan implementing a Transformation
 * Function from a Source Model to a Target Model and their respective instances
 * 
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class AbstractTransformingPlanbuilder extends AbstractPlanBuilder {

	protected final PluginRegistry pluginRegistry = new PluginRegistry();

	private final static Logger LOG = LoggerFactory.getLogger(AbstractTransformingPlanbuilder.class);

	@Override
	public PlanType createdPlanType() {
		return PlanType.MANAGE;
	}

	/**
	 * <p>
	 * Creates a BuildPlan in WS-BPEL 2.0 by using the the referenced source and
	 * target service templates as the transforming function between two models.
	 * </p>
	 * 
	 * @param sourceCsarName          the name of the source csar
	 * @param sourceDefinitions       the id of the source definitions inside the
	 *                                referenced source csar
	 * @param sourceServiceTemplateId the id of the source service templates inside
	 *                                the referenced definitions
	 * @param targetCsarName          the name of the target csar
	 * @param targetDefinitions       the id of the target definitions inside the
	 *                                referenced target csar
	 * @param targetServiceTemplateId the id of the target service templates inside
	 *                                the referenced definitions
	 * @return a single AbstractPlan with a concrete implementation of a
	 *         transformation function from the source to the target topology
	 */
	abstract public AbstractPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			QName sourceServiceTemplateId, String targetCsarName, AbstractDefinitions targetDefinitions,
			QName targetServiceTemplateId);

	/**
	 * Generates a Set of Plans that is generated based on the given source and
	 * target definitions. This generation is done for each Topology Template
	 * defined in both definitions therefore for each combination of source and
	 * target topology template a plan is generated
	 * 
	 * @param sourceCsarName    the name of the source csar
	 * @param sourceDefinitions the id of the source definitions inside the
	 *                          referenced source csar
	 * @param targetCsarName    the name of the target csar
	 * @param targetDefinitions the id of the target definitions inside the
	 *                          referenced target csar
	 * @return a List of AbstractPlans
	 */
	abstract public List<AbstractPlan> buildPlans(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			String targetCsarName, AbstractDefinitions targetDefinitions);

	/**
	 * Generates an abstract order of activities to transform from the source
	 * service template to the target service template
	 * 
	 * @param sourceCsarName          the name of the source csar
	 * @param sourceDefinitions       the id of the source definitions inside the
	 *                                referenced source csar
	 * @param sourceServiceTemplateId the id of the source service templates inside
	 *                                the referenced definitions
	 * @param targetCsarName          the name of the target csar
	 * @param targetDefinitions       the id of the target definitions inside the
	 *                                referenced target csar
	 * @param targetServiceTemplateId the id of the target service templates inside
	 *                                the referenced definitions
	 * @return a single AbstractPlan containing abstract activities for a
	 *         transformation function from the source to the target topology
	 */
	public AbstractPlan generateTFOG(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			AbstractServiceTemplate sourceServiceTemplate, String targetCsarName, AbstractDefinitions targetDefinitions,
			AbstractServiceTemplate targetServiceTemplate) {
		AbstractTopologyTemplate sourceTopology = sourceServiceTemplate.getTopologyTemplate();
		AbstractTopologyTemplate targetTopology = targetServiceTemplate.getTopologyTemplate();

		Set<AbstractNodeTemplate> maxCommonSubgraph = this.getMaxCommonSubgraph(
				new HashSet<AbstractNodeTemplate>(sourceTopology.getNodeTemplates()),
				new HashSet<AbstractNodeTemplate>(sourceTopology.getNodeTemplates()),
				new HashSet<AbstractNodeTemplate>(targetTopology.getNodeTemplates()),
				new HashSet<AbstractNodeTemplate>());

		// find valid subset inside common subgraph, i.e.:
		// any component that is a platform node (every node without outgoing
		// hostedOn edges), or is a node in the subgraph where its (transitive) platform
		// nodes are also in the subgraph are valid
		Set<AbstractNodeTemplate> deployableMaxCommonSubgraph = this.getDeployableSubgraph(maxCommonSubgraph);

		// determine steps which have to be deleted from the original topology
		Set<AbstractNodeTemplate> nodesToTerminate = new HashSet<AbstractNodeTemplate>(
				sourceTopology.getNodeTemplates());
		nodesToTerminate.removeAll(deployableMaxCommonSubgraph);
		Set<AbstractRelationshipTemplate> relationsToTerminate = this.getOutgoingRelations(nodesToTerminate);

		AbstractPlan termPlan = AbstractTerminationPlanBuilder.generateTOG(
				"transformTerminate" + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(),
				sourceDefinitions, sourceServiceTemplate, nodesToTerminate, relationsToTerminate);

		// determine steps which have to be start within the new topology
		Set<AbstractNodeTemplate> nodesToStart = new HashSet<AbstractNodeTemplate>(targetTopology.getNodeTemplates());
		nodesToStart.removeAll(deployableMaxCommonSubgraph);
		Set<AbstractRelationshipTemplate> relationsToStart = this.getOutgoingRelations(nodesToStart);

		AbstractPlan startPlan = AbstractBuildPlanBuilder.generatePOG(
				"transformStart" + sourceDefinitions.getId() + "_to_" + targetDefinitions.getId(), targetDefinitions,
				targetServiceTemplate, nodesToStart, relationsToStart);

		AbstractTransformationPlan transPlan = this.mergeTermAndStartPlans("transformationPlan_"
				+ termPlan.getServiceTemplate().getId() + "_to_" + startPlan.getServiceTemplate().getId(),
				PlanType.TRANSFORMATION, termPlan, startPlan);

		
		return transPlan;
	}

	private AbstractTransformationPlan mergeTermAndStartPlans(String id, PlanType type, AbstractPlan termPlan,
			AbstractPlan startPlan) {

		Collection<AbstractActivity> activities = new HashSet<AbstractActivity>();
		activities.addAll(termPlan.getActivites());
		activities.addAll(startPlan.getActivites());

		Collection<Link> links = new HashSet<Link>();
		links.addAll(termPlan.getLinks());
		links.addAll(startPlan.getLinks());
		
		Collection<AbstractActivity> sinks = termPlan.getSinks();
		Collection<AbstractActivity> sources = startPlan.getSources();
		
		// naively we connect each sink with each source
		for(AbstractActivity sink : sinks) {
			for(AbstractActivity source : sources) {
				links.add(new Link(sink, source));
			}
		}

		return new AbstractTransformationPlan(id, type, termPlan.getDefinitions(), termPlan.getServiceTemplate(),
				startPlan.getDefinitions(), startPlan.getServiceTemplate(), activities, links);

	}
	
	

	private Set<AbstractRelationshipTemplate> getOutgoingRelations(Set<AbstractNodeTemplate> nodes) {
		Set<AbstractRelationshipTemplate> relations = new HashSet<AbstractRelationshipTemplate>();
		for (AbstractNodeTemplate node : nodes) {
			relations.addAll(node.getOutgoingRelations());
		}
		return relations;
	}

	private Collection<AbstractNodeTemplate> getNeededNodes(AbstractNodeTemplate nodeTemplate) {
		for (IPlanBuilderTypePlugin typePlugin : this.pluginRegistry.getTypePlugins()) {
			if (typePlugin.canHandleCreate(nodeTemplate)) {
				if (typePlugin instanceof IPlanBuilderTypePlugin.NodeDependencyInformationInterface) {
					return ((IPlanBuilderTypePlugin.NodeDependencyInformationInterface) typePlugin)
							.getCreateDependencies(nodeTemplate);
				}
			}
		}
		return null;
	}

	private Set<AbstractNodeTemplate> getDeployableSubgraph(Set<AbstractNodeTemplate> graph) {
		Set<AbstractNodeTemplate> validDeploymentSubgraph = new HashSet<AbstractNodeTemplate>(graph);
		Set<AbstractNodeTemplate> toRemove = new HashSet<AbstractNodeTemplate>();

		for (AbstractNodeTemplate node : graph) {
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
	// proper(i.e. efficient) subgraph calculation
	// based on https://stackoverflow.com/a/14644158
	private Set<AbstractNodeTemplate> getMaxCommonSubgraph(Set<AbstractNodeTemplate> vertices,
			Set<AbstractNodeTemplate> graph1, Set<AbstractNodeTemplate> graph2,
			Set<AbstractNodeTemplate> currentSubset) {

		if (vertices.isEmpty()) {
			if (this.isCommonSubgraph(graph1, graph2, currentSubset)) {
				return new HashSet<AbstractNodeTemplate>(currentSubset);
			} else {
				return new HashSet<AbstractNodeTemplate>();
			}
		}

		AbstractNodeTemplate v = this.pop(vertices);
		currentSubset.add(v);
		Set<AbstractNodeTemplate> cand1 = this.getMaxCommonSubgraph(vertices, graph1, graph2, currentSubset);
		Set<AbstractNodeTemplate> cand2 = new HashSet<AbstractNodeTemplate>();

		if (this.isCommonSubgraph(graph1, graph2, currentSubset)) {
			cand2 = this.getMaxCommonSubgraph(vertices, graph1, graph2, currentSubset);
		}

		currentSubset.remove(v);

		return (cand1.size() > cand2.size()) ? cand1 : cand2;
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
				if (this.mappingEquals(nodeIn1, nodeTemplate)) {
					matchedIn1 = true;
					break;
				}
			}

			for (AbstractNodeTemplate nodeIn2 : graph2) {
				if (this.mappingEquals(nodeIn2, nodeTemplate)) {
					matchedIn2 = true;
					break;
				}
			}

			if (!matchedIn1) {
				return false;
			}

			if (!matchedIn2) {
				return false;
			}
		}

		return true;
	}

	private boolean mappingEquals(AbstractNodeTemplate node1, AbstractNodeTemplate node2) {
		if (!node1.getType().getId().equals(node2.getType().getId())) {
			return false;
		}

		// Maybe add it later
		// if
		// (!(node1.getDeploymentArtifacts().containsAll(node2.getDeploymentArtifacts())
		// &&
		// node2.getDeploymentArtifacts().containsAll(node1.getDeploymentArtifacts())))
		// {
		// return false;
		// }

		// This check is pretty heavy if i think about the State Property or changes in
		// values etc.
		// if (!(node1.getProperties().equals(node2.getProperties()))) {
		// return false;
		// }

		// if (!(node1.getPolicies().containsAll(node2.getPolicies())
		// && node2.getPolicies().containsAll(node1.getPolicies()))) {
		// return false;
		// }

		return true;
	}

}
