package org.opentosca.container.core.engine.impl.resolver.data;

/**
 * This enum provides the correct names of TOSCA elements which are searched by the ReferenceMapper
 * with a name instead of an ID.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
public enum ElementNamesEnum {
  NODETYPE("tosca:NodeType"), NODETYPEIMPLEMENTATION("tosca:NodeTypeImplementation"), RELATIONSHIPTYPE("tosca:RelationshipType"), RELATIONSHIPTYPEIMPLEMENTATION("tosca:RelationshipTypeImplementation"), REQUIREMENTTYPE("tosca:RequirementType"), CAPABILITYTYPE("tosca:CapabilityType"), ARTIFACTTYPE("tosca:ArtifactType"), POLICYTYPE("tosca:PolicyType"), ALLELEMENTS(null);

  // the element name definded by TOSCA
  private String elementName;


  /**
   * This constructor sets the name of an element.
   *
   * @param elementName the name of the element.
   */
  ElementNamesEnum(final String elementName) {
    this.elementName = elementName;
  }

  /**
   * This method returns the name of the element defined by TOSCA.
   */
  @Override
  public String toString() {
    return this.elementName;
  }
}
