package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.RelationshipTemplateInstance;

public class RelationshipTemplateInstanceRepository
    extends JpaRepository<RelationshipTemplateInstance> {

  public RelationshipTemplateInstanceRepository() {
    super(RelationshipTemplateInstance.class);
  }
}
