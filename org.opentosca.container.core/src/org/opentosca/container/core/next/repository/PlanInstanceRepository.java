package org.opentosca.container.core.next.repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.PlanInstance;

public class PlanInstanceRepository extends JpaRepository<PlanInstance> {

  public PlanInstanceRepository() {
    super(PlanInstance.class);
  }

  public PlanInstance findByCorrelationId(String correlationId) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      // Parameters
      ParameterExpression<String> correlationIdParameter = cb.parameter(String.class);
      // Build the Criteria Query
      CriteriaQuery<PlanInstance> cq = cb.createQuery(PlanInstance.class);
      Root<PlanInstance> sti = cq.from(PlanInstance.class);
      cq.select(sti).where(cb.equal(sti.get("correlationId"), correlationIdParameter));
      // Create a TypedQuery
      TypedQuery<PlanInstance> q = em.createQuery(cq);
      q.setParameter(correlationIdParameter, correlationId);
      // Execute
      return q.getSingleResult();
    }
  }
}
