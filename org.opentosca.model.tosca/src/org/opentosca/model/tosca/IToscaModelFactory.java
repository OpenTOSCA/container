//
// TOSCA version: TOSCA-v1.0-cs02.xsd
//

package org.opentosca.model.tosca;

public interface IToscaModelFactory {
	
	/**
	 * Create an instance of {@link TDefinitions }
	 * 
	 */
	public TDefinitions createTDefinitions();
	
	/**
	 * Create an instance of {@link TRelationshipTemplate }
	 * 
	 */
	public TRelationshipTemplate createTRelationshipTemplate();
	
	/**
	 * Create an instance of
	 * {@link TRelationshipTemplate.RelationshipConstraints }
	 * 
	 */
	public TRelationshipTemplate.RelationshipConstraints createTRelationshipTemplateRelationshipConstraints();
	
	/**
	 * Create an instance of {@link TRequirementDefinition }
	 * 
	 */
	public TRequirementDefinition createTRequirementDefinition();
	
	/**
	 * Create an instance of {@link TNodeTemplate }
	 * 
	 */
	public TNodeTemplate createTNodeTemplate();
	
	/**
	 * Create an instance of {@link TTopologyElementInstanceStates }
	 * 
	 */
	public TTopologyElementInstanceStates createTTopologyElementInstanceStates();
	
	/**
	 * Create an instance of {@link TAppliesTo }
	 * 
	 */
	public TAppliesTo createTAppliesTo();
	
	/**
	 * Create an instance of {@link TImplementationArtifacts }
	 * 
	 */
	public TImplementationArtifacts createTImplementationArtifacts();
	
	/**
	 * Create an instance of {@link TArtifactReference }
	 * 
	 */
	public TArtifactReference createTArtifactReference();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions }
	 * 
	 */
	public TBoundaryDefinitions createTBoundaryDefinitions();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.Properties }
	 * 
	 */
	public TBoundaryDefinitions.Properties createTBoundaryDefinitionsProperties();
	
	/**
	 * Create an instance of {@link TExportedOperation }
	 * 
	 */
	public TExportedOperation createTExportedOperation();
	
	/**
	 * Create an instance of {@link TPlan }
	 * 
	 */
	public TPlan createTPlan();
	
	/**
	 * Create an instance of {@link TCapabilityDefinition }
	 * 
	 */
	public TCapabilityDefinition createTCapabilityDefinition();
	
	/**
	 * Create an instance of {@link TOperation }
	 * 
	 */
	public TOperation createTOperation();
	
	/**
	 * Create an instance of {@link TArtifactTemplate }
	 * 
	 */
	public TArtifactTemplate createTArtifactTemplate();
	
	/**
	 * Create an instance of {@link TRelationshipTypeImplementation }
	 * 
	 */
	public TRelationshipTypeImplementation createTRelationshipTypeImplementation();
	
	/**
	 * Create an instance of {@link TRelationshipType }
	 * 
	 */
	public TRelationshipType createTRelationshipType();
	
	/**
	 * Create an instance of {@link TNodeTypeImplementation }
	 * 
	 */
	public TNodeTypeImplementation createTNodeTypeImplementation();
	
	/**
	 * Create an instance of {@link TNodeType }
	 * 
	 */
	public TNodeType createTNodeType();
	
	/**
	 * Create an instance of {@link TDocumentation }
	 * 
	 */
	public TDocumentation createTDocumentation();
	
	/**
	 * Create an instance of {@link Definitions }
	 * 
	 */
	public Definitions createDefinitions();
	
	/**
	 * Create an instance of {@link TExtensibleElements }
	 * 
	 */
	public TExtensibleElements createTExtensibleElements();
	
	/**
	 * Create an instance of {@link TDefinitions.Extensions }
	 * 
	 */
	public TDefinitions.Extensions createTDefinitionsExtensions();
	
	/**
	 * Create an instance of {@link TImport }
	 * 
	 */
	public TImport createTImport();
	
	/**
	 * Create an instance of {@link TDefinitions.Types }
	 * 
	 */
	public TDefinitions.Types createTDefinitionsTypes();
	
	/**
	 * Create an instance of {@link TServiceTemplate }
	 * 
	 */
	public TServiceTemplate createTServiceTemplate();
	
	/**
	 * Create an instance of {@link TRequirementType }
	 * 
	 */
	public TRequirementType createTRequirementType();
	
	/**
	 * Create an instance of {@link TCapabilityType }
	 * 
	 */
	public TCapabilityType createTCapabilityType();
	
	/**
	 * Create an instance of {@link TArtifactType }
	 * 
	 */
	public TArtifactType createTArtifactType();
	
	/**
	 * Create an instance of {@link TPolicyType }
	 * 
	 */
	public TPolicyType createTPolicyType();
	
	/**
	 * Create an instance of {@link TPolicyTemplate }
	 * 
	 */
	public TPolicyTemplate createTPolicyTemplate();
	
	/**
	 * Create an instance of {@link TRequirementRef }
	 * 
	 */
	public TRequirementRef createTRequirementRef();
	
	/**
	 * Create an instance of {@link TImplementationArtifact }
	 * 
	 */
	public TImplementationArtifact createTImplementationArtifact();
	
	/**
	 * Create an instance of {@link TParameter }
	 * 
	 */
	public TParameter createTParameter();
	
	/**
	 * Create an instance of {@link TExtension }
	 * 
	 */
	public TExtension createTExtension();
	
	/**
	 * Create an instance of {@link TDeploymentArtifact }
	 * 
	 */
	public TDeploymentArtifact createTDeploymentArtifact();
	
	/**
	 * Create an instance of {@link TRequiredContainerFeatures }
	 * 
	 */
	public TRequiredContainerFeatures createTRequiredContainerFeatures();
	
	/**
	 * Create an instance of {@link TExportedInterface }
	 * 
	 */
	public TExportedInterface createTExportedInterface();
	
	/**
	 * Create an instance of {@link TConstraint }
	 * 
	 */
	public TConstraint createTConstraint();
	
	/**
	 * Create an instance of {@link TPlans }
	 * 
	 */
	public TPlans createTPlans();
	
	/**
	 * Create an instance of {@link TPropertyMapping }
	 * 
	 */
	public TPropertyMapping createTPropertyMapping();
	
	/**
	 * Create an instance of {@link TCondition }
	 * 
	 */
	public TCondition createTCondition();
	
	/**
	 * Create an instance of {@link TExtensions }
	 * 
	 */
	public TExtensions createTExtensions();
	
	/**
	 * Create an instance of {@link TTopologyTemplate }
	 * 
	 */
	public TTopologyTemplate createTTopologyTemplate();
	
	/**
	 * Create an instance of {@link TTags }
	 * 
	 */
	public TTags createTTags();
	
	/**
	 * Create an instance of {@link TPolicy }
	 * 
	 */
	public TPolicy createTPolicy();
	
	/**
	 * Create an instance of {@link TRequiredContainerFeature }
	 * 
	 */
	public TRequiredContainerFeature createTRequiredContainerFeature();
	
	/**
	 * Create an instance of {@link TDeploymentArtifacts }
	 * 
	 */
	public TDeploymentArtifacts createTDeploymentArtifacts();
	
	/**
	 * Create an instance of {@link TCapability }
	 * 
	 */
	public TCapability createTCapability();
	
	/**
	 * Create an instance of {@link TPropertyConstraint }
	 * 
	 */
	public TPropertyConstraint createTPropertyConstraint();
	
	/**
	 * Create an instance of {@link TTag }
	 * 
	 */
	public TTag createTTag();
	
	/**
	 * Create an instance of {@link TInterface }
	 * 
	 */
	public TInterface createTInterface();
	
	/**
	 * Create an instance of {@link TRequirement }
	 * 
	 */
	public TRequirement createTRequirement();
	
	/**
	 * Create an instance of {@link TCapabilityRef }
	 * 
	 */
	public TCapabilityRef createTCapabilityRef();
	
	/**
	 * Create an instance of {@link model.TEntityTemplate.Properties }
	 * 
	 */
	public TEntityTemplate.Properties createTEntityTemplateProperties();
	
	/**
	 * Create an instance of {@link model.TEntityTemplate.PropertyConstraints }
	 * 
	 */
	public TEntityTemplate.PropertyConstraints createTEntityTemplatePropertyConstraints();
	
	/**
	 * Create an instance of {@link TRelationshipTemplate.SourceElement }
	 * 
	 */
	public TRelationshipTemplate.SourceElement createTRelationshipTemplateSourceElement();
	
	/**
	 * Create an instance of {@link TRelationshipTemplate.TargetElement }
	 * 
	 */
	public TRelationshipTemplate.TargetElement createTRelationshipTemplateTargetElement();
	
	/**
	 * Create an instance of
	 * {@link TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint }
	 * 
	 */
	public TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint createTRelationshipTemplateRelationshipConstraintsRelationshipConstraint();
	
	/**
	 * Create an instance of {@link TRequirementDefinition.Constraints }
	 * 
	 */
	public TRequirementDefinition.Constraints createTRequirementDefinitionConstraints();
	
	/**
	 * Create an instance of {@link TNodeTemplate.Requirements }
	 * 
	 */
	public TNodeTemplate.Requirements createTNodeTemplateRequirements();
	
	/**
	 * Create an instance of {@link TNodeTemplate.Capabilities }
	 * 
	 */
	public TNodeTemplate.Capabilities createTNodeTemplateCapabilities();
	
	/**
	 * Create an instance of {@link TNodeTemplate.Policies }
	 * 
	 */
	public TNodeTemplate.Policies createTNodeTemplatePolicies();
	
	/**
	 * Create an instance of
	 * {@link TTopologyElementInstanceStates.InstanceState }
	 * 
	 */
	public TTopologyElementInstanceStates.InstanceState createTTopologyElementInstanceStatesInstanceState();
	
	/**
	 * Create an instance of {@link TAppliesTo.NodeTypeReference }
	 * 
	 */
	public TAppliesTo.NodeTypeReference createTAppliesToNodeTypeReference();
	
	/**
	 * Create an instance of
	 * {@link TImplementationArtifacts.ImplementationArtifact }
	 * 
	 */
	public TImplementationArtifacts.ImplementationArtifact createTImplementationArtifactsImplementationArtifact();
	
	/**
	 * Create an instance of {@link TArtifactReference.Include }
	 * 
	 */
	public TArtifactReference.Include createTArtifactReferenceInclude();
	
	/**
	 * Create an instance of {@link TArtifactReference.Exclude }
	 * 
	 */
	public TArtifactReference.Exclude createTArtifactReferenceExclude();
	
	/**
	 * Create an instance of {@link model.TEntityType.DerivedFrom }
	 * 
	 */
	public TEntityType.DerivedFrom createTEntityTypeDerivedFrom();
	
	/**
	 * Create an instance of {@link model.TEntityType.PropertiesDefinition }
	 * 
	 */
	public TEntityType.PropertiesDefinition createTEntityTypePropertiesDefinition();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.PropertyConstraints }
	 * 
	 */
	public TBoundaryDefinitions.PropertyConstraints createTBoundaryDefinitionsPropertyConstraints();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.Requirements }
	 * 
	 */
	public TBoundaryDefinitions.Requirements createTBoundaryDefinitionsRequirements();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.Capabilities }
	 * 
	 */
	public TBoundaryDefinitions.Capabilities createTBoundaryDefinitionsCapabilities();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.Policies }
	 * 
	 */
	public TBoundaryDefinitions.Policies createTBoundaryDefinitionsPolicies();
	
	/**
	 * Create an instance of {@link TBoundaryDefinitions.Interfaces }
	 * 
	 */
	public TBoundaryDefinitions.Interfaces createTBoundaryDefinitionsInterfaces();
	
	/**
	 * Create an instance of
	 * {@link TBoundaryDefinitions.Properties.PropertyMappings }
	 * 
	 */
	public TBoundaryDefinitions.Properties.PropertyMappings createTBoundaryDefinitionsPropertiesPropertyMappings();
	
	/**
	 * Create an instance of {@link TExportedOperation.NodeOperation }
	 * 
	 */
	public TExportedOperation.NodeOperation createTExportedOperationNodeOperation();
	
	/**
	 * Create an instance of {@link TExportedOperation.RelationshipOperation }
	 * 
	 */
	public TExportedOperation.RelationshipOperation createTExportedOperationRelationshipOperation();
	
	/**
	 * Create an instance of {@link TExportedOperation.Plan }
	 * 
	 */
	public TExportedOperation.Plan createTExportedOperationPlan();
	
	/**
	 * Create an instance of {@link TPlan.InputParameters }
	 * 
	 */
	public TPlan.InputParameters createTPlanInputParameters();
	
	/**
	 * Create an instance of {@link TPlan.OutputParameters }
	 * 
	 */
	public TPlan.OutputParameters createTPlanOutputParameters();
	
	/**
	 * Create an instance of {@link TPlan.PlanModel }
	 * 
	 */
	public TPlan.PlanModel createTPlanPlanModel();
	
	/**
	 * Create an instance of {@link TPlan.PlanModelReference }
	 * 
	 */
	public TPlan.PlanModelReference createTPlanPlanModelReference();
	
	/**
	 * Create an instance of {@link TCapabilityDefinition.Constraints }
	 * 
	 */
	public TCapabilityDefinition.Constraints createTCapabilityDefinitionConstraints();
	
	/**
	 * Create an instance of {@link TOperation.InputParameters }
	 * 
	 */
	public TOperation.InputParameters createTOperationInputParameters();
	
	/**
	 * Create an instance of {@link TOperation.OutputParameters }
	 * 
	 */
	public TOperation.OutputParameters createTOperationOutputParameters();
	
	/**
	 * Create an instance of {@link TArtifactTemplate.ArtifactReferences }
	 * 
	 */
	public TArtifactTemplate.ArtifactReferences createTArtifactTemplateArtifactReferences();
	
	/**
	 * Create an instance of {@link TRelationshipTypeImplementation.DerivedFrom }
	 * 
	 */
	public TRelationshipTypeImplementation.DerivedFrom createTRelationshipTypeImplementationDerivedFrom();
	
	/**
	 * Create an instance of {@link TRelationshipType.SourceInterfaces }
	 * 
	 */
	public TRelationshipType.SourceInterfaces createTRelationshipTypeSourceInterfaces();
	
	/**
	 * Create an instance of {@link TRelationshipType.TargetInterfaces }
	 * 
	 */
	public TRelationshipType.TargetInterfaces createTRelationshipTypeTargetInterfaces();
	
	/**
	 * Create an instance of {@link TRelationshipType.ValidSource }
	 * 
	 */
	public TRelationshipType.ValidSource createTRelationshipTypeValidSource();
	
	/**
	 * Create an instance of {@link TRelationshipType.ValidTarget }
	 * 
	 */
	public TRelationshipType.ValidTarget createTRelationshipTypeValidTarget();
	
	/**
	 * Create an instance of {@link TNodeTypeImplementation.DerivedFrom }
	 * 
	 */
	public TNodeTypeImplementation.DerivedFrom createTNodeTypeImplementationDerivedFrom();
	
	/**
	 * Create an instance of {@link TNodeType.RequirementDefinitions }
	 * 
	 */
	public TNodeType.RequirementDefinitions createTNodeTypeRequirementDefinitions();
	
	/**
	 * Create an instance of {@link TNodeType.CapabilityDefinitions }
	 * 
	 */
	public TNodeType.CapabilityDefinitions createTNodeTypeCapabilityDefinitions();
	
	/**
	 * Create an instance of {@link TNodeType.Interfaces }
	 * 
	 */
	public TNodeType.Interfaces createTNodeTypeInterfaces();
	
}