package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;

import org.hibernate.Hibernate;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.PlanInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInstanceRepository extends JpaRepository<PlanInstance> {

    protected static final Logger logger = LoggerFactory.getLogger(PlanInstanceRepository.class);

    public PlanInstanceRepository() {
        super(PlanInstance.class);
    }

    public PlanInstance findByCorrelationId(final String correlationId) {
        return this.findPlanByColumnValue("correlationId", correlationId);
    }

    public PlanInstance findByChoreographyCorrelationId(final String choreographyCorrelationId, QName planId) {
        return this.findAllPlansByColumnValue("choreographyCorrelationId", choreographyCorrelationId).stream().filter(plan -> plan.getTemplateId().equals(planId)).findFirst().orElse(null);
    }

    public PlanInstance findByChoreographyCorrelationId(final String choreographyCorrelationId) {
        return this.findAllPlansByColumnValue("choreographyCorrelationId", choreographyCorrelationId).stream().findFirst().orElse(null);
    }

    public Collection<PlanInstance> findAllPlansByColumnValue(final String columnName, final String columnValue) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            // Parameters
            final ParameterExpression<String> correlationIdParameter = cb.parameter(String.class);
            // Build the Criteria Query
            final CriteriaQuery<PlanInstance> cq = cb.createQuery(PlanInstance.class);
            final Root<PlanInstance> sti = cq.from(PlanInstance.class);
            cq.select(sti).where(cb.equal(sti.get(columnName), correlationIdParameter));
            // Create a TypedQuery
            final TypedQuery<PlanInstance> q = em.createQuery(cq);
            q.setParameter(correlationIdParameter, columnValue);
            // Execute
            Collection<PlanInstance> result = q.getResultList();
            initializeInstance(result);
            return result;
        }
    }

    public PlanInstance findPlanByColumnValue(final String columnName, final String columnValue) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            // Parameters
            final ParameterExpression<String> correlationIdParameter = cb.parameter(String.class);
            // Build the Criteria Query
            final CriteriaQuery<PlanInstance> cq = cb.createQuery(PlanInstance.class);
            final Root<PlanInstance> sti = cq.from(PlanInstance.class);
            cq.select(sti).where(cb.equal(sti.get(columnName), correlationIdParameter));
            // Create a TypedQuery
            final TypedQuery<PlanInstance> q = em.createQuery(cq);
            q.setParameter(correlationIdParameter, columnValue);
            // Execute
            PlanInstance result = q.getSingleResult();
            initializeInstance(result);
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    protected void initializeInstance(PlanInstance instance) {
        Hibernate.initialize(instance.getServiceTemplateInstance());
        Hibernate.initialize(instance.getEvents());
        Hibernate.initialize(instance.getInputs());
        Hibernate.initialize(instance.getOutputs());
    }

    private PlanInstance initInstance(PlanInstance instance) {
        initializeInstance(instance);
        return instance;
    }

    protected void initializeInstance(Collection<PlanInstance> instance) {
        instance.forEach(i -> initializeInstance(i));
    }
}
