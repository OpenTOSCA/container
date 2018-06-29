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
import org.opentosca.container.core.next.model.NodeTemplateInstance;

public class NodeTemplateInstanceRepository extends JpaRepository<NodeTemplateInstance> {

    public NodeTemplateInstanceRepository() {
        super(NodeTemplateInstance.class);
    }

    public Collection<NodeTemplateInstance> findByTemplateId(final QName templateId) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            // Parameters
            final ParameterExpression<QName> templateIdParameter = cb.parameter(QName.class);
            // Build the Criteria Query
            final CriteriaQuery<NodeTemplateInstance> cq = cb.createQuery(NodeTemplateInstance.class);
            final Root<NodeTemplateInstance> sti = cq.from(NodeTemplateInstance.class);
            cq.select(sti).where(cb.equal(sti.get("templateId"), templateIdParameter));
            // Create a TypedQuery
            final TypedQuery<NodeTemplateInstance> q = em.createQuery(cq);
            q.setParameter(templateIdParameter, templateId);
            // Execute
            return q.getResultList();
        }
    }

    public Collection<NodeTemplateInstance> findByTemplateType(final QName templateType) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            // Parameters
            final ParameterExpression<QName> templateTypeParameter = cb.parameter(QName.class);
            // Build the Criteria Query
            final CriteriaQuery<NodeTemplateInstance> cq = cb.createQuery(NodeTemplateInstance.class);
            final Root<NodeTemplateInstance> sti = cq.from(NodeTemplateInstance.class);
            cq.select(sti).where(cb.equal(sti.get("templateType"), templateTypeParameter));
            // Create a TypedQuery
            final TypedQuery<NodeTemplateInstance> q = em.createQuery(cq);
            q.setParameter(templateTypeParameter, templateType);
            // Execute
            return q.getResultList();
        }
    }
}
