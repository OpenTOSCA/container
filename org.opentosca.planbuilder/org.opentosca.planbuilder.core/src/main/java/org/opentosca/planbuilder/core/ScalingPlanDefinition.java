package org.opentosca.planbuilder.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.google.common.collect.Sets;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;

public class ScalingPlanDefinition {

    private final Csar csar;
    public String name;
    public List<TNodeTemplate> nodeTemplates;
    public List<TNodeTemplate> nodeTemplatesRecursiveSelection;
    public List<TRelationshipTemplate> relationshipTemplates;
    public List<TRelationshipTemplate> relationshipTemplatesRecursiveSelection;
    public Collection<AnnotatedTNodeTemplate> selectionStrategy2BorderNodes;
    public TTopologyTemplate topology;

    public ScalingPlanDefinition(final String name, final TTopologyTemplate topology,
                                 final List<TNodeTemplate> nodeTemplates,
                                 final List<TRelationshipTemplate> relationshipTemplate,
                                 final Collection<AnnotatedTNodeTemplate> selectionStrategy2BorderNodes, Csar csar) {
        this.name = name;
        this.topology = topology;
        this.nodeTemplates = nodeTemplates;
        this.relationshipTemplates = relationshipTemplate;
        this.selectionStrategy2BorderNodes = selectionStrategy2BorderNodes;

        this.nodeTemplatesRecursiveSelection = new ArrayList<>();
        this.relationshipTemplatesRecursiveSelection = new ArrayList<>();
        this.csar = csar;

        init();
    }

    private void init() {

        isValid(this.csar);

        // calculate recursive nodes
        for (final TNodeTemplate nodeTemplate : this.selectionStrategy2BorderNodes) {
            final Set<TNodeTemplate> sinkNodes = Sets.newHashSet();

            ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.hostedOnRelationType, sinkNodes, csar);
            ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.dependsOnRelationType, sinkNodes, csar);
            ModelUtils.getNodesFromNodeToSink(nodeTemplate, Types.deployedOnRelationType, sinkNodes, csar);

            sinkNodes.remove(nodeTemplate);

            Collection<QName> types = Sets.newHashSet();
            types.add(Types.hostedOnRelationType);
            types.add(Types.dependsOnRelationType);
            types.add(Types.deployedOnRelationType);
            final List<TRelationshipTemplate> outgoing =
                ModelUtils.getOutgoingRelations(nodeTemplate, types, csar);

            this.nodeTemplatesRecursiveSelection.addAll(sinkNodes);
            this.relationshipTemplatesRecursiveSelection.addAll(outgoing);
        }
    }

    private Set<TRelationshipTemplate> calculateBorderCrossingRelations(Csar csar) {
        final Set<TRelationshipTemplate> borderCrossingRelations = new HashSet<>();

        for (final TRelationshipTemplate relationshipTemplate : this.relationshipTemplates) {
            final TNodeTemplate nodeStratSelection = crossesBorder(relationshipTemplate, this.nodeTemplates, csar);
            if (nodeStratSelection != null && this.selectionStrategy2BorderNodes.contains(nodeStratSelection)) {
                borderCrossingRelations.add(relationshipTemplate);
            }
        }

        for (final TNodeTemplate nodeTemplate : this.nodeTemplates) {
            final List<TRelationshipTemplate> relations =
                getBorderCrossingRelations(nodeTemplate, this.nodeTemplates, csar);
            borderCrossingRelations.addAll(relations);
        }
        return borderCrossingRelations;
    }

    private boolean isValid(Csar csar) {
        // check if all nodes at the border are attached with a selection
        // strategy
        /* calculate all border crossing relations */
        final Set<TRelationshipTemplate> borderCrossingRelations = calculateBorderCrossingRelations(csar);

        for (final TRelationshipTemplate relation : borderCrossingRelations) {
            final TNodeTemplate nodeStratSelection = crossesBorder(relation, this.nodeTemplates, csar);
            if (nodeStratSelection == null) {
                // these edges MUST be connected to a strategically selected
                // node
                return false;
            }

            if (!this.selectionStrategy2BorderNodes.contains(nodeStratSelection)) {
                return false;
            }
        }

        return true;
    }

    private List<TRelationshipTemplate> getBorderCrossingRelations(final TNodeTemplate nodeTemplate,
                                                                   final List<TNodeTemplate> nodesToScale, Csar csar) {
        final List<TRelationshipTemplate> borderCrossingRelations = new ArrayList<>();

        for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            if (crossesBorder(relation, nodesToScale, csar) != null) {
                borderCrossingRelations.add(relation);
            }
        }

        for (final TRelationshipTemplate relation : ModelUtils.getIngoingRelations(nodeTemplate, csar)) {
            if (crossesBorder(relation, nodesToScale, csar) != null) {
                borderCrossingRelations.add(relation);
            }
        }

        return borderCrossingRelations;
    }

    private TNodeTemplate crossesBorder(final TRelationshipTemplate relationship,
                                        final List<TNodeTemplate> nodesToScale, Csar csar) {

        final TNodeTemplate source = ModelUtils.getSource(relationship, csar);
        final TNodeTemplate target = ModelUtils.getTarget(relationship, csar);

        final QName baseType = ModelUtils.getRelationshipBaseType(relationship, csar);

        if (baseType.equals(Types.connectsToRelationType)) {
            // if either the source or target is not in the nodesToScale
            // list =>
            // relation crosses border
            if (!nodesToScale.contains(source)) {
                return source;
            } else if (!nodesToScale.contains(target)) {
                return target;
            }
        } else if (baseType.equals(Types.dependsOnRelationType) | baseType.equals(Types.hostedOnRelationType)
            | baseType.equals(Types.deployedOnRelationType)) {
            // if target is not in the nodesToScale list => relation crosses
            // border
            if (!nodesToScale.contains(target)) {
                return target;
            }
        }

        return null;
    }

    public static class AnnotatedTNodeTemplate extends TNodeTemplate {

        private final Collection<String> annotations;
        private final TNodeTemplate nodeTemplate;

        public AnnotatedTNodeTemplate(final TNodeTemplate nodeTemplate,
                                      final Collection<String> annotations) {
            this.annotations = annotations;
            this.nodeTemplate = nodeTemplate;
        }

        public Collection<String> getAnnotations() {
            return this.annotations;
        }

        @Override
        public List<TCapability> getCapabilities() {
            return this.nodeTemplate.getCapabilities();
        }

        @Override
        public List<TRequirement> getRequirements() {
            return this.nodeTemplate.getRequirements();
        }

        @Override
        public String getName() {
            return this.nodeTemplate.getId();
        }

        @Override
        public String getId() {
            return this.nodeTemplate.getId();
        }

        @Override
        public QName getType() {
            return this.nodeTemplate.getType();
        }

        @Override
        public TEntityTemplate.Properties getProperties() {
            return this.nodeTemplate.getProperties();
        }

        @Override
        public List<TDeploymentArtifact> getDeploymentArtifacts() {
            return this.nodeTemplate.getDeploymentArtifacts();
        }

        @Override
        public int getMinInstances() {
            return this.nodeTemplate.getMinInstances();
        }

        @Override
        public List<TPolicy> getPolicies() {
            return this.nodeTemplate.getPolicies();
        }

        @Override
        public Map<QName, String> getOtherAttributes() {
            return this.nodeTemplate.getOtherAttributes();
        }
    }
}
