package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;

import org.hibernate.Hibernate;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

public class ServiceTemplateInstanceRepository extends JpaRepository<ServiceTemplateInstance> {

  public ServiceTemplateInstanceRepository() {
    super(ServiceTemplateInstance.class);
  }

  @Override
  public Optional<ServiceTemplateInstance> find(final Long id) {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      final ServiceTemplateInstance entity = em.find(this.clazz, id);
      em.refresh(entity);
      fetchDependentBags(entity);
      return Optional.ofNullable(entity);
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  private void fetchDependentBags(ServiceTemplateInstance entity) {
    Hibernate.initialize(entity.getDeploymentTests());
    Hibernate.initialize(entity.getNodeTemplateInstances());
    Hibernate.initialize(entity.getProperties());
    Hibernate.initialize(entity.getRelationshipTemplateInstances());
    Hibernate.initialize(entity.getPlanInstances());
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
