package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public class ScalingPlanDefinition {

    // topology
    public String name;
    AbstractTopologyTemplate topology;

    // region
    public List<AbstractNodeTemplate> nodeTemplates;
    public List<AbstractRelationshipTemplate> relationshipTemplates;

    // nodes with selection strategies
    public Collection<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes;

    public static class AnnotatedAbstractNodeTemplate extends AbstractNodeTemplate {

        private final Collection<String> annotations;
        private final AbstractNodeTemplate nodeTemplate;

        public AnnotatedAbstractNodeTemplate(final AbstractNodeTemplate nodeTemplate,
                                             final Collection<String> annotations) {
            this.annotations = annotations;
            this.nodeTemplate = nodeTemplate;
        }

        public Collection<String> getAnnotations() {
            return this.annotations;
        }

        @Override
        public List<AbstractRelationshipTemplate> getOutgoingRelations() {
            return this.nodeTemplate.getOutgoingRelations();
        }

        @Override
        public List<AbstractRelationshipTemplate> getIngoingRelations() {
            return this.nodeTemplate.getIngoingRelations();
        }

        @Override
        public List<AbstractCapability> getCapabilities() {
            return this.nodeTemplate.getCapabilities();
        }

        @Override
        public List<AbstractRequirement> getRequirements() {
            return this.nodeTemplate.getRequirements();
        }

        @Override
        public String getName() {
            return this.nodeTemplate.getId();
        }

        @Override
        public List<AbstractNodeTypeImplementation> getImplementations() {
            return this.nodeTemplate.getImplementations();
        }

        @Override
        public String getId() {
            return this.nodeTemplate.getId();
        }

        @Override
        public AbstractNodeType getType() {
            return this.nodeTemplate.getType();
        }

        @Override
        public AbstractProperties getProperties() {
            return this.nodeTemplate.getProperties();
        }

        @Override
        public List<AbstractDeploymentArtifact> getDeploymentArtifacts() {
            return this.nodeTemplate.getDeploymentArtifacts();
        }

        @Override
        public int getMinInstances() {
            return this.nodeTemplate.getMinInstances();
        }

        @Override
        public List<AbstractPolicy> getPolicies() {
            return this.nodeTemplate.getPolicies();
        }

    }

    // recursive selections
    public List<AbstractNodeTemplate> nodeTemplatesRecursiveSelection;
    public List<AbstractRelationshipTemplate> relationshipTemplatesRecursiveSelection;

    // border crossing relations
    public Set<AbstractRelationshipTemplate> borderCrossingRelations;

    public ScalingPlanDefinition(final String name, final AbstractTopologyTemplate topology,
                                 final List<AbstractNodeTemplate> nodeTemplates,
                                 final List<AbstractRelationshipTemplate> relationshipTemplate,
                                 final Collection<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes) {
        this.name = name;
        this.topology = topology;
        this.nodeTemplates = nodeTemplates;
        this.relationshipTemplates = relationshipTemplate;
        this.selectionStrategy2BorderNodes = selectionStrategy2BorderNodes;

        this.nodeTemplatesRecursiveSelection = new ArrayList<>();
        this.relationshipTemplatesRecursiveSelection = new ArrayList<>();

        init();

        this.borderCrossingRelations = calculateBorderCrossingRelations();
    }

    private void init() {

        isValid();

        // calculate recursive nodes
        for (final AbstractNodeTemplate nodeTemplate : this.selectionStrategy2BorderNodes) {
            final List<AbstractNodeTemplate> sinkNodes = new ArrayList<>();

            ModelUtils.getNodesFromNodeToSink(nodeTemplate, ModelUtils.TOSCABASETYPE_HOSTEDON, sinkNodes);
            ModelUtils.getNodesFromNodeToSink(nodeTemplate, ModelUtils.TOSCABASETYPE_DEPENDSON, sinkNodes);
            ModelUtils.getNodesFromNodeToSink(nodeTemplate, ModelUtils.TOSCABASETYPE_DEPLOYEDON, sinkNodes);

            final List<AbstractRelationshipTemplate> outgoing =
                ModelUtils.getOutgoingRelations(nodeTemplate, ModelUtils.TOSCABASETYPE_HOSTEDON,
                                                ModelUtils.TOSCABASETYPE_DEPENDSON,
                                                ModelUtils.TOSCABASETYPE_DEPLOYEDON);

            this.nodeTemplatesRecursiveSelection.addAll(sinkNodes);
            this.relationshipTemplatesRecursiveSelection.addAll(outgoing);
        }
    }

    private Set<AbstractRelationshipTemplate> calculateBorderCrossingRelations() {
        final Set<AbstractRelationshipTemplate> borderCrossingRelations = new HashSet<>();

        for (final AbstractRelationshipTemplate relationshipTemplate : this.relationshipTemplates) {
            final AbstractNodeTemplate nodeStratSelection = crossesBorder(relationshipTemplate, this.nodeTemplates);
            if (nodeStratSelection != null && this.selectionStrategy2BorderNodes.contains(nodeStratSelection)) {
                borderCrossingRelations.add(relationshipTemplate);
            }
        }

        for (final AbstractNodeTemplate nodeTemplate : this.nodeTemplates) {
            final List<AbstractRelationshipTemplate> relations =
                getBorderCrossingRelations(nodeTemplate, this.nodeTemplates);
            borderCrossingRelations.addAll(relations);
        }
        return borderCrossingRelations;
    }

    private boolean isValid() {
        // check if all nodes at the border are attached with a selection
        // strategy
        /* calculate all border crossing relations */
        final Set<AbstractRelationshipTemplate> borderCrossingRelations = calculateBorderCrossingRelations();

        for (final AbstractRelationshipTemplate relation : borderCrossingRelations) {
            final AbstractNodeTemplate nodeStratSelection = crossesBorder(relation, this.nodeTemplates);
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

    private List<AbstractRelationshipTemplate> getBorderCrossingRelations(final AbstractNodeTemplate nodeTemplate,
                                                                          final List<AbstractNodeTemplate> nodesToScale) {
        final List<AbstractRelationshipTemplate> borderCrossingRelations = new ArrayList<>();

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
            if (crossesBorder(relation, nodesToScale) != null) {
                borderCrossingRelations.add(relation);
            }
        }

        for (final AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
            if (crossesBorder(relation, nodesToScale) != null) {
                borderCrossingRelations.add(relation);
            }
        }

        return borderCrossingRelations;
    }

    private AbstractNodeTemplate crossesBorder(final AbstractRelationshipTemplate relationship,
                                               final List<AbstractNodeTemplate> nodesToScale) {

        final AbstractNodeTemplate source = relationship.getSource();
        final AbstractNodeTemplate target = relationship.getTarget();

        final QName baseType = ModelUtils.getRelationshipBaseType(relationship);

        if (baseType.equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
            // if either the source or target is not in the nodesToScale
            // list =>
            // relation crosses border
            if (!nodesToScale.contains(source)) {
                return source;
            } else if (!nodesToScale.contains(target)) {
                return target;
            }
        } else if (baseType.equals(ModelUtils.TOSCABASETYPE_DEPENDSON)
            | baseType.equals(ModelUtils.TOSCABASETYPE_HOSTEDON)
            | baseType.equals(ModelUtils.TOSCABASETYPE_DEPLOYEDON)) {
            // if target is not in the nodesToScale list => relation crosses
            // border
            if (!nodesToScale.contains(target)) {
                return target;
            }

        }

        return null;
    }
}
