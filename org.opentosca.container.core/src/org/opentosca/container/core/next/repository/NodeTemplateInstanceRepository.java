package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.NodeTemplateInstance;

public class NodeTemplateInstanceRepository extends JpaRepository<NodeTemplateInstance> {

  public NodeTemplateInstanceRepository() {
    super(NodeTemplateInstance.class);
  }
}
