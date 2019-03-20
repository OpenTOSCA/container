package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

public class ServiceTemplateInstanceRepository extends JpaRepository<ServiceTemplateInstance> {

  public ServiceTemplateInstanceRepository() {
    super(ServiceTemplateInstance.class);
  }

  public Collection<ServiceTemplateInstance> findByTemplateId(final QName templateId) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      // Parameters
      final ParameterExpression<QName> templateIdParameter = cb.parameter(QName.class);
      // Build the Criteria Query
      final CriteriaQuery<ServiceTemplateInstance> cq = cb.createQuery(ServiceTemplateInstance.class);
      final Root<ServiceTemplateInstance> sti = cq.from(ServiceTemplateInstance.class);
      cq.select(sti).where(cb.equal(sti.get("templateId"), templateIdParameter));
      // Create a TypedQuery
      final TypedQuery<ServiceTemplateInstance> q = em.createQuery(cq);
      q.setParameter(templateIdParameter, templateId);
      // Execute
      return q.getResultList();
    }
  }

  public Collection<ServiceTemplateInstance> findByCsarId(final CSARID csarId) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      final CriteriaBuilder cb = em.getCriteriaBuilder();
      // Parameters
      final ParameterExpression<CSARID> csarIdParameter = cb.parameter(CSARID.class);
      // Build the Criteria Query
      final CriteriaQuery<ServiceTemplateInstance> cq = cb.createQuery(ServiceTemplateInstance.class);
      final Root<ServiceTemplateInstance> sti = cq.from(ServiceTemplateInstance.class);
      cq.select(sti).where(cb.equal(sti.get("csarId"), csarIdParameter));
      // Create a TypedQuery
      final TypedQuery<ServiceTemplateInstance> q = em.createQuery(cq);
      q.setParameter(csarIdParameter, csarId);
      // Execute
      return q.getResultList();
    }
  }
}
