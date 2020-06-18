package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;

import org.hibernate.Hibernate;
import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

public class NodeTemplateInstanceRepository extends JpaRepository<NodeTemplateInstance> {

    public NodeTemplateInstanceRepository() {
        super(NodeTemplateInstance.class);
    }

    public List<NodeTemplateInstance> find(final ServiceTemplateInstance sti, String nodeTemplateId) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();

            final ParameterExpression<ServiceTemplateInstance> owner = cb.parameter(ServiceTemplateInstance.class, "sti");
            final ParameterExpression<String> templateId = cb.parameter(String.class, "ntId");

            final CriteriaQuery<NodeTemplateInstance> query = cb.createQuery(NodeTemplateInstance.class);
            final Root<NodeTemplateInstance> nti = query.from(NodeTemplateInstance.class);

            query.select(nti).where(
                cb.equal(nti.get("serviceTemplateInstance"), owner)
                , cb.equal(nti.get("templateId"), templateId));

            final TypedQuery<NodeTemplateInstance> q = em.createQuery(query);
            q.setParameter(owner, sti);
            q.setParameter(templateId, nodeTemplateId);

            final List<NodeTemplateInstance> result = q.getResultList();
            result.forEach(this::initializeInstance);
            return result;
        }
    }

    public Collection<NodeTemplateInstance> findByTemplateId(final String templateId) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();

            final ParameterExpression<String> templateIdParameter = cb.parameter(String.class);

            final CriteriaQuery<NodeTemplateInstance> cq = cb.createQuery(NodeTemplateInstance.class);
            final Root<NodeTemplateInstance> sti = cq.from(NodeTemplateInstance.class);
            cq.select(sti).where(cb.equal(sti.get("templateId"), templateIdParameter));

            final TypedQuery<NodeTemplateInstance> q = em.createQuery(cq);
            q.setParameter(templateIdParameter, templateId);

            return q.getResultList();
        }
    }

    public Collection<NodeTemplateInstance> findByTemplateType(final QName templateType) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final CriteriaBuilder cb = em.getCriteriaBuilder();

            final ParameterExpression<QName> templateTypeParameter = cb.parameter(QName.class);

            final CriteriaQuery<NodeTemplateInstance> cq = cb.createQuery(NodeTemplateInstance.class);
            final Root<NodeTemplateInstance> sti = cq.from(NodeTemplateInstance.class);
            cq.select(sti).where(cb.equal(sti.get("templateType"), templateTypeParameter));

            final TypedQuery<NodeTemplateInstance> q = em.createQuery(cq);
            q.setParameter(templateTypeParameter, templateType);

            return q.getResultList();
        }
    }

    @Override
    protected void initializeInstance(NodeTemplateInstance instance) {
        Hibernate.initialize(instance.getDeploymentTestResults());
        Hibernate.initialize(instance.getProperties());
        Hibernate.initialize(instance.getOutgoingRelations());
        Hibernate.initialize(instance.getIncomingRelations());
    }
}
