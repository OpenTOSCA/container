package org.opentosca.core.engine.impl;

import org.opentosca.core.tosca.model.TArtifactTemplate;
import org.opentosca.core.tosca.model.TArtifactType;
import org.opentosca.core.tosca.model.TCapability;
import org.opentosca.core.tosca.model.TCapabilityDefinition;
import org.opentosca.core.tosca.model.TCapabilityType;
import org.opentosca.core.tosca.model.TDefinitions;
import org.opentosca.core.tosca.model.TDeploymentArtifact;
import org.opentosca.core.tosca.model.TImplementationArtifact;
import org.opentosca.core.tosca.model.TInterface;
import org.opentosca.core.tosca.model.TNodeTemplate;
import org.opentosca.core.tosca.model.TNodeType;
import org.opentosca.core.tosca.model.TNodeTypeImplementation;
import org.opentosca.core.tosca.model.TOperation;
import org.opentosca.core.tosca.model.TPlan;
import org.opentosca.core.tosca.model.TPolicy;
import org.opentosca.core.tosca.model.TPolicyTemplate;
import org.opentosca.core.tosca.model.TPolicyType;
import org.opentosca.core.tosca.model.TRelationshipTemplate;
import org.opentosca.core.tosca.model.TRelationshipType;
import org.opentosca.core.tosca.model.TRelationshipTypeImplementation;
import org.opentosca.core.tosca.model.TRequirement;
import org.opentosca.core.tosca.model.TRequirementDefinition;
import org.opentosca.core.tosca.model.TRequirementType;
import org.opentosca.core.tosca.model.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This enum provides a list of available JAXB classes of TOSCA which are
 * supported by the ToscaReferenceMapper. It is used by the ToscaReferenceMapper
 * for getting a class object for a passed name of a Node. Due this the
 * ToscaReferenceMapper can provide the method getJAXBReference which serializes
 * a DOM Node into the returned class object.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
public enum AvailableToscaElements {

	CAPABILITY, ARTIFACTTEMPLATE, ARTIFACTTYPE, CAPABILITYDEFINITION, CAPABILITYTYPE, DEFINITIONS, DEPLOYMENTARTIFACT, IMPLEMENTATIONARTIFACT, INTERFACE, NODETEMPLATE, NODETYPE, NODETYPEIMPLEMENTATION, OPERATION, POLICY, POLICYTEMPLATE, POLICYTYPE, RELATIONSHIPTEMPLATE, RELATIONSHIPTYPE, REQUIREMENT, REQUIREMENTDEFINITION, REQUIREMENTTYPE, SERVICETEMPLATE, SOURCEELEMENT, TARGETELEMENT, RELATIONSHIPTYPEIMPLEMENTATION, PLAN;

	Logger LOG = LoggerFactory.getLogger(AvailableToscaElements.class);


	/**
	 * Returns the ENUM constant for a given element name.
	 *
	 * @param name Name of the element defined by the TOSCA speficiation of
	 *            OASIS.
	 * @return The ENUM constant or null, if nothing matches.
	 */
	public static AvailableToscaElements getElementName(final String name) {

		final Logger LOG = LoggerFactory.getLogger(ToscaReferenceMapper.class);

		if (name.equals("ArtifactTemplate")) {
			return ARTIFACTTEMPLATE;
		} else if (name.equals("ArtifactType")) {
			return ARTIFACTTYPE;
		} else if (name.equals("Capability")) {
			return CAPABILITY;
		} else if (name.equals("CapabilityDefinition")) {
			return CAPABILITYDEFINITION;
		} else if (name.equals("CapabilityType")) {
			return CAPABILITYTYPE;
		} else if (name.equals("Definitions")) {
			return DEFINITIONS;
		} else if (name.equals("DeploymentArtifact")) {
			return DEPLOYMENTARTIFACT;
		} else if (name.equals("ImplementationArtifact")) {
			return IMPLEMENTATIONARTIFACT;
		} else if (name.equals("Interface")) {
			return INTERFACE;
		} else if (name.equals("NodeTemplate")) {
			return NODETEMPLATE;
		} else if (name.equals("NodeType")) {
			return NODETYPE;
		} else if (name.equals("NodeTypeImplementation")) {
			return NODETYPEIMPLEMENTATION;
		} else if (name.equals("Operation")) {
			return OPERATION;
		} else if (name.equals("Policy")) {
			return POLICY;
		} else if (name.equals("PolicyTemplate")) {
			return POLICYTEMPLATE;
		} else if (name.equals("PolicyType")) {
			return POLICYTYPE;
		} else if (name.equals("RelationshipTemplate")) {
			return RELATIONSHIPTEMPLATE;
		} else if (name.equals("RelationshipType")) {
			return RELATIONSHIPTYPE;
		} else if (name.equals("RelationshipTypeImplementation")) {
			return RELATIONSHIPTYPEIMPLEMENTATION;
		} else if (name.equals("Requirement")) {
			return REQUIREMENT;
		} else if (name.equals("RequirementDefinition")) {
			return REQUIREMENTDEFINITION;
		} else if (name.equals("RequirementType")) {
			return REQUIREMENTTYPE;
		} else if (name.equals("ServiceTemplate")) {
			return SERVICETEMPLATE;
		} else if (name.equals("SourceElement")) {
			return SOURCEELEMENT;
		} else if (name.equals("TargetElement")) {
			return TARGETELEMENT;
		} else if (name.equals("Plan")) {
			return PLAN;
		}

		// nothing found
		LOG.error("The constant for \"" + name + "\" was not found.");
		return null;
	}

	/**
	 * Returns the JAXB class for a ENUM constant.
	 *
	 * @return JAXB element class or null if it is none of the constants.
	 */
	public Class<?> getElementClass() {
		final Logger LOG = LoggerFactory.getLogger(ToscaReferenceMapper.class);

		switch (this) {
		case ARTIFACTTEMPLATE:
			return TArtifactTemplate.class;
		case ARTIFACTTYPE:
			return TArtifactType.class;
		case CAPABILITY:
			return TCapability.class;
		case CAPABILITYDEFINITION:
			return TCapabilityDefinition.class;
		case CAPABILITYTYPE:
			return TCapabilityType.class;
		case DEFINITIONS:
			return TDefinitions.class;
		case DEPLOYMENTARTIFACT:
			return TDeploymentArtifact.class;
		case IMPLEMENTATIONARTIFACT:
			return TImplementationArtifact.class;
		case INTERFACE:
			return TInterface.class;
		case NODETEMPLATE:
			return TNodeTemplate.class;
		case NODETYPE:
			return TNodeType.class;
		case NODETYPEIMPLEMENTATION:
			return TNodeTypeImplementation.class;
		case OPERATION:
			return TOperation.class;
		case POLICY:
			return TPolicy.class;
		case POLICYTEMPLATE:
			return TPolicyTemplate.class;
		case POLICYTYPE:
			return TPolicyType.class;
		case RELATIONSHIPTEMPLATE:
			return TRelationshipTemplate.class;
		case RELATIONSHIPTYPE:
			return TRelationshipType.class;
		case RELATIONSHIPTYPEIMPLEMENTATION:
			return TRelationshipTypeImplementation.class;
		case REQUIREMENT:
			return TRequirement.class;
		case REQUIREMENTDEFINITION:
			return TRequirementDefinition.class;
		case REQUIREMENTTYPE:
			return TRequirementType.class;
		case SERVICETEMPLATE:
			return TServiceTemplate.class;
		case SOURCEELEMENT:
			return TRelationshipTemplate.SourceElement.class;
		case TARGETELEMENT:
			return TRelationshipTemplate.TargetElement.class;
		case PLAN:
			return TPlan.class;
		
		default:
			LOG.error("The class for \"" + this.toString() + "\" was not found.");
			return null;
		}
	}
}
