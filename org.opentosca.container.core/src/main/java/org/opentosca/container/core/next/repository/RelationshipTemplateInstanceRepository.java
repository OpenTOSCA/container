package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;

public class RelationshipTemplateInstanceRepository extends JpaRepository<RelationshipTemplateInstance> {

    public RelationshipTemplateInstanceRepository() {
        super(RelationshipTemplateInstance.class);
    }

    public Collection<RelationshipTemplateInstance> findByTemplateId(final String templateId) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            // Parameters
            final ParameterExpression<String> templateIdParameter = cb.parameter(String.class);
            // Build the Criteria Query
            final CriteriaQuery<RelationshipTemplateInstance> cq = cb.createQuery(RelationshipTemplateInstance.class);
            final Root<RelationshipTemplateInstance> sti = cq.from(RelationshipTemplateInstance.class);
            cq.select(sti).where(cb.equal(sti.get("templateId"), templateIdParameter));
            // Create a TypedQuery
            final TypedQuery<RelationshipTemplateInstance> q = em.createQuery(cq);
            q.setParameter(templateIdParameter, templateId);
            // Execute
            return q.getResultList();
        }
    }

    @Override
    protected void initializeInstance(RelationshipTemplateInstance instance) {
        Hibernate.initialize(instance.getProperties());
    }
}
