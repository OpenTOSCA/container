package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;

import com.google.common.collect.Lists;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.TOSCAManagementInfrastructureNodeTemplate;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This Class represents the low-level algorithm for the concept in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>. This
 * includes selecting a implementation (NodeTypeImplementation,Relationship..) where all artifacts (IA, DA) and the
 * operations of the template (Node and Relation) can be used for calling a chain/list/.. of TOSCA operations. All
 * complete possibilities of calls based on the selected Node Type Implementation are hold inside a OperationChain
 * Object. And can be selected to be generated by the determined operations and handling plugins.
 *
 * </p>
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Component
public class BPELScopeBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPELScopeBuilder.class);

    private final PluginRegistry pluginRegistry;

    @Inject
    public BPELScopeBuilder(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    /**
     * <p>
     * Filters IA and DA Candidates inside the given ProvisioningChain. Filtering means if there are IA and DACandidates
     * which don't operate on the same Template Implementation they are deleted.
     * </p>
     *
     * @param chain a ProvisioningChain to filter
     */
    private void filterIncompatibleIADACandidates(final OperationChain chain) {
        final Map<IANodeTypeImplCandidate, DANodeTypeImplCandidate> compatibleCandidates = new HashMap<>();
        for (final IANodeTypeImplCandidate iaCandidate : chain.iaCandidates) {
            for (final DANodeTypeImplCandidate daCandidate : chain.daCandidates) {
                if (iaCandidate.nodeImpl.getName().equals(daCandidate.impl.getName())) {
                    compatibleCandidates.put(iaCandidate, daCandidate);
                }
            }
        }
        chain.daCandidates = new ArrayList<>();
        chain.iaCandidates = new ArrayList<>();

        for (final IANodeTypeImplCandidate key : compatibleCandidates.keySet()) {
            chain.iaCandidates.add(key);
            chain.daCandidates.add(compatibleCandidates.get(key));
        }
    }

    /**
     * TODO: We assume that IAs are already provisinoned on IA engine
     *
     * @return OperationChain
     */
    public OperationChain createOperationCall(final TRelationshipTemplate relationshipTemplate,
                                              final String interfaceName, final String operationName, Csar csar) {

        final Collection<TRelationshipTypeImplementation> impls = ModelUtils.findRelationshipTypeImplementation(relationshipTemplate, csar);
        if (impls.isEmpty()) {
            LOG.warn("No implementations available for RelationshipTemplate {} , can't generate Provisioning logic",
                relationshipTemplate.getId());
            return null;
        }

        final OperationChain chain = new OperationChain(relationshipTemplate);
        chain.provCandidates = new ArrayList<>();

        final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins =
            pluginRegistry.getProvPlugins();

        for (final TRelationshipTypeImplementation impl : impls) {
            final OperationNodeTypeImplCandidate provCandidate = new OperationNodeTypeImplCandidate();
            for (final TImplementationArtifact ia : impl.getImplementationArtifacts()) {
                for (final IPlanBuilderProvPhaseOperationPlugin<?> plugin : provPlugins) {
                    if (plugin.canHandle(ia.getArtifactType())
                        && getOperationForIa(chain.relationshipTemplate, ia, operationName, csar) != null) {
                        provCandidate.add(getOperationForIa(chain.relationshipTemplate, ia,
                                operationName, csar),
                            ia, plugin);
                    }
                }
            }
            chain.provCandidates.add(provCandidate);
        }
        return chain;
    }

    /**
     * Creates a complete ProvisioningChain for the given NodeTemplate
     *
     * @param nodeTemplate an TNodeTemplate to create a ProvisioningChain for
     * @return a complete ProvisioningChain
     */
    public OperationChain createOperationCall(BPELPlanContext context, final TNodeTemplate nodeTemplate,
                                              final String interfaceName, final String operationName) {
        // get nodetype implementations
        final Collection<TNodeTypeImplementation> nodeTypeImpls = ModelUtils.findNodeTypeImplementation(nodeTemplate, context.getCsar());

        if (nodeTypeImpls.isEmpty()) {
            LOG.warn("No implementations available for NodeTemplate {} , can't generate Provisioning logic",
                nodeTemplate.getId());
            return null;
        }

        final OperationChain chain = new OperationChain(nodeTemplate);

        // calculate infrastructure nodes
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, context.getCsar());

        // we'll add here a dummy infra node, representing the management
        // infrastructure of the tosca engine (WAR IA's implementing tosca
        // operation,..)
        infraNodes.add(new TOSCAManagementInfrastructureNodeTemplate());

        // check for IA Plugins
        final List<IPlanBuilderPrePhaseIAPlugin<?>> iaPlugins = pluginRegistry.getIaPlugins();
        final List<IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>> iaPlugins2 = Lists.newArrayList();

        iaPlugins.forEach(x -> iaPlugins2.add((IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>) x));

        LOG.debug("Calculating best IA candidates for nodeTemplate {} ", nodeTemplate.getId());
        // calculate nodeImpl candidates where all IAs of each can be
        // provisioned
        calculateBestImplementationIACandidates(context, nodeTypeImpls, iaPlugins2, infraNodes, chain,
            interfaceName, operationName);
        for (final IANodeTypeImplCandidate iaCandidate : chain.iaCandidates) {
            final int length = iaCandidate.ias.size();
            for (int i = 0; i < length; i++) {
                final TImplementationArtifact ia = iaCandidate.ias.get(i);
                final TNodeTemplate infraNode = iaCandidate.infraNodes.get(i);
                final IPlanBuilderPlugin plugin = iaCandidate.plugins.get(i);
                LOG.debug("Found IA {} for deployment on the InfraNode {} with the Plugin {}",
                    ia.getName(), infraNode.getId(), plugin.getID());
            }
        }

        // check for prov plugins
        final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins =
            pluginRegistry.getProvPlugins();

        // search for prov plugins according to the chosen IA provisionings in
        // the chain
        calculateProvPlugins(chain, provPlugins, interfaceName, operationName, context.getCsar());

        // filter ia and da candidates where the operations can't be executed
        filterIADACandidates(chain);

        // order provisioning candidates
        reorderProvCandidates(chain);

        // TODO consistency plugins
        final List<String> array = new ArrayList<>();

        array.add(operationName);
        // select provisioning
        selectProvisioning(chain, array);

        return chain;
    }

    /**
     * Creates a complete ProvisioningChain for the given NodeTemplate
     *
     * @param nodeTemplate an TNodeTemplate to create a ProvisioningChain for
     * @return a complete ProvisioningChain
     */
    public OperationChain createOperationChain(BPELPlanContext context, final TNodeTemplate nodeTemplate, final List<String> operationNames) {
        // get nodetype implementations
        final Collection<TNodeTypeImplementation> nodeTypeImpls = ModelUtils.findNodeTypeImplementation(nodeTemplate, context.getCsar());

        if (nodeTypeImpls.isEmpty()) {
            LOG.warn("No implementations available for NodeTemplate {} , can't generate Provisioning logic",
                nodeTemplate.getId());
            return null;
        }

        final OperationChain chain = new OperationChain(nodeTemplate);

        // calculate infrastructure nodes
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, context.getCsar());

        // we'll add here a dummy infra node, representing the management
        // infrastructure of the tosca engine (WAR IA's implementing tosca
        // operation,..)
        infraNodes.add(new TOSCAManagementInfrastructureNodeTemplate());

        // check for IA Plugins
        final List<IPlanBuilderPrePhaseIAPlugin<?>> iaPlugins = pluginRegistry.getIaPlugins();
        final List<IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>> iaPlugins2 = Lists.newArrayList();

        iaPlugins.forEach(x -> iaPlugins2.add((IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>) x));

        LOG.debug("Calculating best IA candidates for nodeTemplate {} ", nodeTemplate.getId());
        // calculate nodeImpl candidates where all IAs of each can be
        // provisioned
        calculateBestImplementationIACandidates(context, nodeTypeImpls, iaPlugins2, infraNodes, chain);
        for (final IANodeTypeImplCandidate wrapper : chain.iaCandidates) {
            final int length = wrapper.ias.size();
            for (int i = 0; i < length; i++) {
                final TImplementationArtifact ia = wrapper.ias.get(i);
                final TNodeTemplate infraNode = wrapper.infraNodes.get(i);
                final IPlanBuilderPlugin plugin = wrapper.plugins.get(i);
                LOG.debug("Found IA {} for deployment on the InfraNode {} with the Plugin {}",
                    ia.getName(), infraNode.getId(), plugin.getID());
            }
        }

        // check for DA Plugins
        final List<IPlanBuilderPrePhaseDAPlugin<?>> daPlugins = pluginRegistry.getDaPlugins();
        final List<IPlanBuilderPrePhaseDAPlugin<BPELPlanContext>> daPlugins2 = Lists.newArrayList();

        daPlugins.forEach(x -> daPlugins2.add((IPlanBuilderPrePhaseDAPlugin<BPELPlanContext>) x));

        // calculate nodeImpl candidates where all DAs of each can be
        // provisioned
        calculateBestImplementationDACandidates(context, nodeTemplate, nodeTypeImpls, daPlugins2, infraNodes,
            chain);
        for (final DANodeTypeImplCandidate wrapper : chain.daCandidates) {
            final int length = wrapper.das.size();
            for (int i = 0; i < length; i++) {
                final TDeploymentArtifact da = wrapper.das.get(i);
                final TNodeTemplate infraNode = wrapper.infraNodes.get(i);
                final IPlanBuilderPlugin plugin = wrapper.plugins.get(i);
                LOG.debug("Found DA {} for deployment on the InfraNode {} with the Plugin {}",
                    da.getName(), infraNode.getId(), plugin.getID());
            }
        }

        // filter for nodeTypeImpl Candidates where both DAs and IAs can
        // be provisioned
        filterIncompatibleIADACandidates(chain);

        // check for prov plugins
        final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins =
            pluginRegistry.getProvPlugins();

        // search for prov plugins according to the chosen IA provisionings in
        // the chain
        calculateProvPlugins(chain, provPlugins, context.getCsar());

        // filter ia and da candidates where the operations can't be executed
        filterIADACandidates(chain);

        // order provisioning candidates
        reorderProvCandidates(chain);

        // TODO consistency plugins

        // select provisioning
        selectProvisioning(chain, operationNames);

        return chain;
    }

    /**
     * Reorders the IA/ProvCandidates inside the given ProvisioningChain, so that a correct order is enforced
     *
     * @param chain a ProvisioningChain
     */
    private void reorderProvCandidates(final OperationChain chain) {
        // ia candidates and da candidates in the chains are already ordered
        // accordingly
        final List<OperationNodeTypeImplCandidate> reorderedList = new ArrayList<>();
        for (final IANodeTypeImplCandidate iaCandidate : chain.iaCandidates) {
            for (final OperationNodeTypeImplCandidate provCandidate : chain.provCandidates) {
                for (final TImplementationArtifact iaCandidateIa : iaCandidate.ias) {
                    for (final TImplementationArtifact provCandidateIa : provCandidate.ias) {
                        if (iaCandidateIa.equals(provCandidateIa)) {
                            reorderedList.add(provCandidate);
                            break;
                        }
                    }
                }
            }
        }

        chain.provCandidates = reorderedList;
    }

    private void searchIaDaCandidatesWithNoOperation(OperationChain chain, List<IANodeTypeImplCandidate> iaCandidatesToRemove, Set<OperationNodeTypeImplCandidate> provCandidatesWithMatch) {
        for (final IANodeTypeImplCandidate iaCandidate : chain.iaCandidates) {
            final int iaCandidateSize = iaCandidate.ias.size();
            OperationNodeTypeImplCandidate match = null;
            for (final OperationNodeTypeImplCandidate provCandidate : chain.provCandidates) {
                int count = 0;
                for (final TImplementationArtifact iaCandidateIa : iaCandidate.ias) {
                    for (final TImplementationArtifact provCandidateIa : provCandidate.ias) {
                        if (iaCandidateIa.equals(provCandidateIa)) {
                            count++;
                        }
                    }
                }
                if (count == iaCandidateSize) {
                    match = provCandidate;
                }
            }
            if (match == null && !chain.provCandidates.isEmpty()) {
                iaCandidatesToRemove.add(iaCandidate);
            } else {
                if (match != null) {
                    provCandidatesWithMatch.add(match);
                }
            }
        }
    }

    /**
     * Filters DA/IA Candidates where no OperationCandidates could be found
     *
     * @param chain a ProvisioningChain
     */
    private void filterIADACandidates(final OperationChain chain) {
        if (chain.provCandidates.size() != chain.iaCandidates.size()) {
            // search for ia/da-Candidates where no operation candidate could be found
            final List<IANodeTypeImplCandidate> iaCandidatesToRemove = new ArrayList<>();
            final Set<OperationNodeTypeImplCandidate> provCandidatesWithMatch = new HashSet<>();
            searchIaDaCandidatesWithNoOperation(chain, iaCandidatesToRemove, provCandidatesWithMatch);

            if (!iaCandidatesToRemove.isEmpty()) {
                // we need to remove ia and da candidates accordingly, because
                // we didn't found matching operation candidates for them
                for (final IANodeTypeImplCandidate iaCandidateToRemove : iaCandidatesToRemove) {
                    final int index = chain.iaCandidates.indexOf(iaCandidateToRemove);
                    chain.iaCandidates.remove(index);
                    chain.daCandidates.remove(index);
                }
            }

            if (!provCandidatesWithMatch.isEmpty()) {
                // remove all prov candidates which weren't matched to some ia candidate
                chain.provCandidates = new ArrayList<>();
                chain.provCandidates.addAll(provCandidatesWithMatch);
            }
        }
    }

    private void selectProvisioning(final OperationChain chain, final List<String> operationNames) {
        // TODO just select the first ia candidate, da candidate and prov candidate for now
        //  Selection should determine a minimal provisioning.
        //  Minimal= min{|IACandidates| + |DACandidates| + |ProvPhaseOperations|}

        // select first candidate set where the provisioning candidate uses the given operations

        int selectedCandidateSet = -1;
        for (int i = 0; i < chain.provCandidates.size(); i++) {

            for (final TOperation op : chain.provCandidates.get(i).ops) {
                if (operationNames.contains(op.getName())) {
                    selectedCandidateSet = i;
                    break;
                }
            }
            if (selectedCandidateSet != -1) {
                break;
            }
        }

        if (selectedCandidateSet != -1) {
            chain.selectedCandidateSet = selectedCandidateSet;
        }
    }

    /**
     * Calculates which Provisioning can be used for Provisioining according to the given IA/DACandidates inside the
     * given ProvisioningChain
     *
     * @param chain       a ProvisioningChain with set DA/IACandidates
     * @param provPlugins a List of ProvPhaseOperationPlugins
     */
    private void calculateProvPlugins(final OperationChain chain,
                                      final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins,
                                      final String interfaceName, final String operationName, Csar csar) {
        final List<OperationNodeTypeImplCandidate> candidates = new ArrayList<>();
        for (final IANodeTypeImplCandidate iaCandidate : chain.iaCandidates) {
            final OperationNodeTypeImplCandidate provCandidate = new OperationNodeTypeImplCandidate();
            for (final TImplementationArtifact ia : iaCandidate.ias) {
                if (!ia.getInterfaceName().trim().equals(interfaceName.trim())) {
                    continue;
                }
                if (ia.getOperationName() != null && !ia.getOperationName().trim().equals(operationName.trim())) {
                    continue;
                }
                determineProvisioningPlugin(chain, provPlugins, provCandidate, ia, csar);
            }
            if (chain.nodeTemplate != null) {
                if (provCandidate.isValid(chain.nodeTemplate, interfaceName, operationName, csar)) {
                    candidates.add(provCandidate);
                }
            } else {
                if (provCandidate.isValid(chain.relationshipTemplate, csar)) {
                    candidates.add(provCandidate);
                }
            }
        }
        chain.provCandidates = candidates;
    }

    /**
     * Calculates which Provisioning can be used for Provisioining according to the given IA/DACandidates inside the
     * given ProvisioningChain
     *
     * @param chain       a ProvisioningChain with set DA/IACandidates
     * @param provPlugins a List of ProvPhaseOperationPlugins
     */
    private void calculateProvPlugins(final OperationChain chain,
                                      final List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins, Csar csar) {
        final List<OperationNodeTypeImplCandidate> candidates = new ArrayList<>();
        for (final IANodeTypeImplCandidate candidate : chain.iaCandidates) {
            final OperationNodeTypeImplCandidate provCandidate = new OperationNodeTypeImplCandidate();
            for (final TImplementationArtifact ia : candidate.ias) {
                determineProvisioningPlugin(chain, provPlugins, provCandidate, ia, csar);
            }
            if (chain.nodeTemplate != null) {
                if (provCandidate.isValid(chain.nodeTemplate, csar)) {
                    candidates.add(provCandidate);
                }
            } else {
                if (provCandidate.isValid(chain.relationshipTemplate, csar)) {
                    candidates.add(provCandidate);
                }
            }
        }
        chain.provCandidates = candidates;
    }

    private void determineProvisioningPlugin(OperationChain chain, List<IPlanBuilderProvPhaseOperationPlugin<?>> provPlugins, OperationNodeTypeImplCandidate provCandidate, TImplementationArtifact ia, Csar csar) {
        for (final IPlanBuilderProvPhaseOperationPlugin<?> plugin : provPlugins) {
            if (chain.nodeTemplate != null) {
                if (plugin.canHandle(ia.getArtifactType())
                    && getOperationForIa(chain.nodeTemplate, ia, csar) != null) {

                    provCandidate.add(getOperationForIa(chain.nodeTemplate, ia, csar), ia, plugin);
                }
            } else {
                if (plugin.canHandle(ia.getArtifactType())
                    && getOperationForIa(chain.relationshipTemplate, ia, csar) != null) {
                    provCandidate.add(getOperationForIa(chain.relationshipTemplate, ia, csar), ia,
                        plugin);
                }
            }
        }
    }

    /**
     * Returns the Operation which is implemented by the given IA
     *
     * @param nodeTemplate an TNodeTemplate
     * @param ia           an TImplementationArtifact
     * @return AbstractOperation of the NodeTemplate if the given IA implements it, else null
     */
    private TOperation getOperationForIa(final TNodeTemplate nodeTemplate,
                                         final TImplementationArtifact ia, Csar csar) {

        if (ia.getInterfaceName() != null & ia.getOperationName() == null) {
            return new InterfaceDummy(nodeTemplate, ia, csar);
        }
        List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
        if (interfaces != null) {
            for (final TInterface iface : interfaces) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(ia.getOperationName())) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the Operation which is implemented by the given IA
     *
     * @param relationshipTemplate an TRelationshipTemplate
     * @param ia                   an TImplementationArtifact
     * @return AbstractOperation of the RelationshipTemplate if the given IA implements it, else null
     */
    private TOperation getOperationForIa(final TRelationshipTemplate relationshipTemplate,
                                         final TImplementationArtifact ia, Csar csar) {
        return getOperationForIa(relationshipTemplate, ia, ia.getOperationName(), csar);
    }

    private TOperation getOperationForIa(final TRelationshipTemplate relationshipTemplate,
                                         final TImplementationArtifact ia,
                                         final String operationNameFallback, Csar csar) {
        String name = ia.getOperationName();
        if (name == null) {
            name = operationNameFallback;
        }

        TRelationshipType relType = ModelUtils.findRelationshipType(relationshipTemplate, csar);

        for (final TInterface iface : relType.getSourceInterfaces()) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(name)) {
                    return op;
                }
            }
        }
        for (final TInterface iface : relType.getTargetInterfaces()) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(name)) {
                    return op;
                }
            }
        }
        for (final TInterface iface : relType.getInterfaces()) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(name)) {
                    return op;
                }
            }
        }
        return null;
    }

    /**
     * Calculates correct mappings of the given NodeTypeImplementations, PrePhaseDAPlugins and InfrastructureNodes for
     * the given ProvisioningChain
     *
     * @param impls      a List of NodeTypeImplementations
     * @param plugins    a List of PrePhaseDAPlugins
     * @param infraNodes a List of InfrastructureNode of the NodeTemplate the NodeTypeImplementations belong to
     * @param chain      a ProvisioningChain where the candidates are added to
     */
    private void calculateBestImplementationDACandidates(BPELPlanContext context, final TNodeTemplate nodeTemplate,
                                                         final Collection<TNodeTypeImplementation> impls,
                                                         final Collection<IPlanBuilderPrePhaseDAPlugin<BPELPlanContext>> plugins,
                                                         final Collection<TNodeTemplate> infraNodes,
                                                         final OperationChain chain) {
        final List<DANodeTypeImplCandidate> candidates = new ArrayList<>();

        for (final TNodeTypeImplementation impl : impls) {
            LOG.debug("Checking DAs of NodeTypeImpl {} and NodeTemplate {}", impl.getName(),
                nodeTemplate.getId());
            final DANodeTypeImplCandidate candidate = new DANodeTypeImplCandidate(nodeTemplate, impl, context.getCsar());

            final List<TDeploymentArtifact> effectiveDAs = ModelUtils.calculateEffectiveDAs(nodeTemplate, impl, context.getCsar());

            for (final TDeploymentArtifact da : effectiveDAs) {
                LOG.debug("Checking whether DA {} can be deployed", da.getName());
                for (final TNodeTemplate infraNode : infraNodes) {
                    LOG.debug("Checking if DA {} can be deployed on InfraNode {}", da.getName(),
                        infraNode.getId());
                    for (final IPlanBuilderPrePhaseDAPlugin<BPELPlanContext> plugin : plugins) {
                        LOG.debug("Checking with Plugin {}", plugin.getID());
                        if (plugin.canHandle(context, da, ModelUtils.findNodeType(infraNode, context.getCsar()))) {
                            LOG.debug("Adding Plugin, can handle DA on InfraNode");
                            candidate.add(da, infraNode, plugin);
                        }
                    }
                }
            }
            if (candidate.isValid()) {
                LOG.debug("Generated Candidate was valid, adding to all Candidates");
                candidates.add(candidate);
            } else {
                LOG.debug("Generated Candidate was invalid, don't add to all Candidates");
            }
        }
        chain.daCandidates = candidates;
    }

    /**
     * Searches for NodeTypeImplementations where all IA's can be provisioned by some plugin in the system.
     *
     * Saves a list of Wrapper class Object which contain information of which ia is provisioned on which infrastructure
     * by which plugin in <code>chain.iaCandidates</code>.
     *
     * @param impls      all implementations of single nodetype
     * @param plugins    all plugins possibly capable of working with the ia's contained in a nodetypeImplementation
     * @param infraNodes all infrastructure nodes of the nodetemplate the nodetypeimplementations originate from
     */
    private void calculateBestImplementationIACandidates(BPELPlanContext context, final Collection<TNodeTypeImplementation> impls,
                                                         final Collection<IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>> plugins,
                                                         final Collection<TNodeTemplate> infraNodes,
                                                         final OperationChain chain, final String interfaceName,
                                                         final String operationName) {

        final List<IANodeTypeImplCandidate> candidates = new ArrayList<>();
        // cycle through all implementations
        for (final TNodeTypeImplementation impl : impls) {
            final IANodeTypeImplCandidate candidate = new IANodeTypeImplCandidate(impl);
            // match the ias of the implementation with the infrastructure nodes
            for (final TImplementationArtifact ia : impl.getImplementationArtifacts()) {
                if (!ia.getInterfaceName().trim().equals(interfaceName.trim())) {
                    continue;
                }
                if (ia.getOperationName() != null && !ia.getOperationName().trim().equals(operationName.trim())) {
                    continue;
                }

                checkIaCanBeDeployedOnNode(context, plugins, infraNodes, candidate, ia);
            }
            // check if all ias of the implementation can be provisioned
            if (candidate.isValid(interfaceName, operationName)) {
                candidates.add(candidate);
                LOG.debug("IA Candidate is valid, adding to candidate list");
            } else {
                LOG.debug("IA Candidate is invalid, discarding candidate");
            }
        }
        chain.iaCandidates = candidates;
    }

    private void checkIaCanBeDeployedOnNode(BPELPlanContext context, Collection<IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>> plugins, Collection<TNodeTemplate> infraNodes, IANodeTypeImplCandidate candidate, TImplementationArtifact ia) {
        LOG.debug("Checking whether IA {} can be deployed on a specific Infrastructure Node",
            ia.getName());
        for (final TNodeTemplate infraNode : infraNodes) {
            // check if any plugin can handle installing the ia on the
            // infraNode
            for (final IPlanBuilderPrePhaseIAPlugin<BPELPlanContext> plugin : plugins) {
                if (plugin.canHandle(context, ia, ModelUtils.findNodeType(infraNode, context.getCsar()))) {
                    candidate.add(ia, infraNode, plugin);
                }
            }
        }
    }

    /**
     * Searches for NodeTypeImplementations where all IA's can be provisioned by some plugin in the system.
     *
     * Saves a list of Wrapper class Object which contain information of which ia is provisioned on which infrastructure
     * by which plugin in <code>chain.iaCandidates</code>.
     *
     * @param impls      all implementations of single nodetype
     * @param plugins    all plugins possibly capable of working with the ia's contained in a nodetypeImplementation
     * @param infraNodes all infrastructure nodes of the nodetemplate the nodetypeimplementations originate from
     */
    private void calculateBestImplementationIACandidates(BPELPlanContext context, final Collection<TNodeTypeImplementation> impls,
                                                         final Collection<IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>> plugins,
                                                         final Collection<TNodeTemplate> infraNodes,
                                                         final OperationChain chain) {

        final List<IANodeTypeImplCandidate> candidates = new ArrayList<>();
        // cycle through all implementations
        for (final TNodeTypeImplementation impl : impls) {
            final IANodeTypeImplCandidate candidate = new IANodeTypeImplCandidate(impl);
            // match the ias of the implementation with the infrastructure nodes
            for (final TImplementationArtifact ia : impl.getImplementationArtifacts()) {
                checkIaCanBeDeployedOnNode(context, plugins, infraNodes, candidate, ia);
            }
            // check if all ias of the implementation can be provisioned
            if (candidate.isValid()) {
                candidates.add(candidate);
                LOG.debug("IA Candidate is valid, adding to candidate list");
            } else {
                LOG.debug("IA Candidate is invalid, discarding candidate");
            }
        }
        chain.iaCandidates = candidates;
    }
}
