package org.opentosca.container.core.next.repository;

import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.DeploymentTest;

public class DeploymentTestRepository extends JpaRepository<DeploymentTest> {

  public DeploymentTestRepository() {
    super(DeploymentTest.class);
  }

  @Override
  protected void initializeInstance(DeploymentTest instance) {
    Hibernate.initialize(instance.getDeploymentTestResults());
  }
}
