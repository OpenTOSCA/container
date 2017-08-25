package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.bpel.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.opentosca.planbuilder.utils.Utils;

public class ScalingPlanDefinition {
	

	// topology
	public String name;
	AbstractTopologyTemplate topology;
	
	// region
	public List<AbstractNodeTemplate> nodeTemplates;
	public List<AbstractRelationshipTemplate> relationshipTemplates;
	
	// nodes with selection strategies
	public Collection<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes;
	
	public static class AnnotatedAbstractNodeTemplate extends AbstractNodeTemplate{
		
		private final Collection<String> annotations;
		private final AbstractNodeTemplate nodeTemplate;
		 
		public AnnotatedAbstractNodeTemplate(AbstractNodeTemplate nodeTemplate, Collection<String> annotations) {			
			this.annotations = annotations;
			this.nodeTemplate = nodeTemplate;
		}
		
		public Collection<String> getAnnotations(){
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
		
	}
	
	// recursive selections
	public List<AbstractNodeTemplate> nodeTemplatesRecursiveSelection;
	public List<AbstractRelationshipTemplate> relationshipTemplatesRecursiveSelection;
	
	// border crossing relations
	public Set<AbstractRelationshipTemplate> borderCrossingRelations;
	
	
	public ScalingPlanDefinition(String name, AbstractTopologyTemplate topology, List<AbstractNodeTemplate> nodeTemplates, List<AbstractRelationshipTemplate> relationshipTemplate, Collection<AnnotatedAbstractNodeTemplate> selectionStrategy2BorderNodes) {		
		this.name = name;
		this.topology = topology;
		this.nodeTemplates = nodeTemplates;
		this.relationshipTemplates = relationshipTemplate;
		this.selectionStrategy2BorderNodes = selectionStrategy2BorderNodes;
		
		this.nodeTemplatesRecursiveSelection = new ArrayList<AbstractNodeTemplate>();
		this.relationshipTemplatesRecursiveSelection = new ArrayList<AbstractRelationshipTemplate>();
		
		this.init();
		
		this.borderCrossingRelations = this.calculateBorderCrossingRelations();
	}
	
	private void init() {
		
		this.isValid();
		
		// calculate recursive nodes
		for (AbstractNodeTemplate nodeTemplate : selectionStrategy2BorderNodes) {
			List<AbstractNodeTemplate> sinkNodes = new ArrayList<AbstractNodeTemplate>();
			
			Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_HOSTEDON, sinkNodes);
			Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_DEPENDSON, sinkNodes);
			Utils.getNodesFromNodeToSink(nodeTemplate, Utils.TOSCABASETYPE_DEPLOYEDON, sinkNodes);
			
			List<AbstractRelationshipTemplate> outgoing = Utils.getOutgoingRelations(nodeTemplate, Utils.TOSCABASETYPE_HOSTEDON, Utils.TOSCABASETYPE_DEPENDSON, Utils.TOSCABASETYPE_DEPLOYEDON);
			
			this.nodeTemplatesRecursiveSelection.addAll(sinkNodes);
			this.relationshipTemplatesRecursiveSelection.addAll(outgoing);
		}
	}
	
	private Set<AbstractRelationshipTemplate> calculateBorderCrossingRelations() {
		Set<AbstractRelationshipTemplate> borderCrossingRelations = new HashSet<AbstractRelationshipTemplate>();
		
		for (AbstractRelationshipTemplate relationshipTemplate : this.relationshipTemplates) {
			AbstractNodeTemplate nodeStratSelection = this.crossesBorder(relationshipTemplate, nodeTemplates);
			if (nodeStratSelection != null && this.selectionStrategy2BorderNodes.contains(nodeStratSelection)) {
				borderCrossingRelations.add(relationshipTemplate);
			}
		}
		
		for (AbstractNodeTemplate nodeTemplate : this.nodeTemplates) {
			List<AbstractRelationshipTemplate> relations = this.getBorderCrossingRelations(nodeTemplate, nodeTemplates);
			borderCrossingRelations.addAll(relations);
		}
		return borderCrossingRelations;
	}
	
	private boolean isValid() {
		// check if all nodes at the border are attached with a selection
		// strategy
		/* calculate all border crossing relations */
		Set<AbstractRelationshipTemplate> borderCrossingRelations = this.calculateBorderCrossingRelations();
		
		for (AbstractRelationshipTemplate relation : borderCrossingRelations) {
			AbstractNodeTemplate nodeStratSelection = this.crossesBorder(relation, nodeTemplates);
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
	
	private List<AbstractRelationshipTemplate> getBorderCrossingRelations(AbstractNodeTemplate nodeTemplate, List<AbstractNodeTemplate> nodesToScale) {
		List<AbstractRelationshipTemplate> borderCrossingRelations = new ArrayList<AbstractRelationshipTemplate>();
		
		for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
			if (this.crossesBorder(relation, nodesToScale) != null) {
				borderCrossingRelations.add(relation);
			}
		}
		
		for (AbstractRelationshipTemplate relation : nodeTemplate.getIngoingRelations()) {
			if (this.crossesBorder(relation, nodesToScale) != null) {
				borderCrossingRelations.add(relation);
			}
		}
		
		return borderCrossingRelations;
	}
	
	private AbstractNodeTemplate crossesBorder(AbstractRelationshipTemplate relationship, List<AbstractNodeTemplate> nodesToScale) {
		
		AbstractNodeTemplate source = relationship.getSource();
		AbstractNodeTemplate target = relationship.getTarget();
		
		QName baseType = Utils.getRelationshipBaseType(relationship);
		
		if (baseType.equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
			// if either the source or target is not in the nodesToScale
			// list =>
			// relation crosses border
			if (!nodesToScale.contains(source)) {
				return source;
			} else if (!nodesToScale.contains(target)) {
				return target;
			}
		} else if (baseType.equals(Utils.TOSCABASETYPE_DEPENDSON) | baseType.equals(Utils.TOSCABASETYPE_HOSTEDON) | baseType.equals(Utils.TOSCABASETYPE_DEPLOYEDON)) {
			// if target is not in the nodesToScale list => relation crosses
			// border
			if (!nodesToScale.contains(target)) {
				return target;
			}
			
		}
		
		return null;
	}
}