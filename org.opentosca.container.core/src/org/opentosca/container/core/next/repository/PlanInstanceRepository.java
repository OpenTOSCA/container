package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.PlanInstance;

public class PlanInstanceRepository extends JpaRepository<PlanInstance> {

  public PlanInstanceRepository() {
    super(PlanInstance.class);
  }
}
