package org.opentosca.container.legacy.core.engine.resolver.resolver.data;

/**
 * This enum provides the correct names of TOSCA elements which are searched by the ReferenceMapper
 * with a name instead of an ID.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
public enum ElementNamesEnum {
  NODE_TYPE("tosca:NodeType"),
  NODE_TYPE_IMPLEMENTATION("tosca:NodeTypeImplementation"),
  RELATIONSHIP_TYPE("tosca:RelationshipType"),
  RELATIONSHIP_TYPE_IMPLEMENTATION("tosca:RelationshipTypeImplementation"),
  REQUIREMENT_TYPE("tosca:RequirementType"),
  CAPABILITY_TYPE("tosca:CapabilityType"),
  ARTIFACT_TYPE("tosca:ArtifactType"),
  POLICY_TYPE("tosca:PolicyType"),
  ALL_ELEMENTS(null);

  // the element name defined by TOSCA
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
