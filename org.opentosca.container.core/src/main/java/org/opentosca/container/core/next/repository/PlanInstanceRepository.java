package org.opentosca.container.core.next.repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.PlanInstance;

public class PlanInstanceRepository extends JpaRepository<PlanInstance> {

  public PlanInstanceRepository() {
    super(PlanInstance.class);
  }

  public PlanInstance findByCorrelationId(final String correlationId) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      // Parameters
      final ParameterExpression<String> correlationIdParameter = cb.parameter(String.class);
      // Build the Criteria Query
      final CriteriaQuery<PlanInstance> cq = cb.createQuery(PlanInstance.class);
      final Root<PlanInstance> sti = cq.from(PlanInstance.class);
      cq.select(sti).where(cb.equal(sti.get("correlationId"), correlationIdParameter));
      // Create a TypedQuery
      final TypedQuery<PlanInstance> q = em.createQuery(cq);
      q.setParameter(correlationIdParameter, correlationId);
      // Execute
      PlanInstance result = q.getSingleResult();
      // fetch dependent entity bags
       Hibernate.initialize(result.getEvents());
       Hibernate.initialize(result.getInputs());
       Hibernate.initialize(result.getOutputs());
      return result;
    }
  }
}
