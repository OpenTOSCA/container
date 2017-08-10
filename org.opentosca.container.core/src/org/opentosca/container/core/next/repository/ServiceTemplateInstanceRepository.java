package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;

import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

public class ServiceTemplateInstanceRepository extends JpaRepository<ServiceTemplateInstance> {

  public ServiceTemplateInstanceRepository() {
    super(ServiceTemplateInstance.class);
  }

  public Collection<ServiceTemplateInstance> findByTemplateId(QName templateId) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      // Parameters
      ParameterExpression<QName> templateIdParameter = cb.parameter(QName.class);
      // Build the Criteria Query
      CriteriaQuery<ServiceTemplateInstance> cq = cb.createQuery(ServiceTemplateInstance.class);
      Root<ServiceTemplateInstance> sti = cq.from(ServiceTemplateInstance.class);
      cq.select(sti).where(cb.equal(sti.get("templateId"), templateIdParameter));
      // Create a TypedQuery
      TypedQuery<ServiceTemplateInstance> q = em.createQuery(cq);
      q.setParameter(templateIdParameter, templateId);
      // Execute
      return q.getResultList();
    }
  }
}
