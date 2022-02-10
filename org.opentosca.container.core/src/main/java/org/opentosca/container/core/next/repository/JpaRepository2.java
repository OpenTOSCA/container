package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JpaRepository2<T> implements Repository<T, Long> {

    protected static final Logger logger = LoggerFactory.getLogger(JpaRepository2.class);
    protected final Class<T> clazz;

    public JpaRepository2(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void add(final T entity) {
        final EntityManager em = EntityManagerProvider.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Override
    public void add(final Iterable<T> items) {
        final EntityManager em = EntityManagerProvider.createEntityManager();
        try {
            em.getTransaction().begin();
            items.forEach(em::persist);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Override
    public void update(final T entity) {
        final EntityManager em = EntityManagerProvider.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Override
    public void remove(final T entity) {
        final EntityManager em = EntityManagerProvider.createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Override
    public Optional<T> find(final Long id) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            final T entity = em.find(this.clazz, id);
            if (entity == null) {
                return Optional.empty();
            }
            em.refresh(entity);
            initializeInstance(entity);
            return Optional.of(entity);
        } catch (final Exception e) {
            logger.info("Failed to find instance of class {} with id {} in persistence context.", clazz.getSimpleName(), id, e);
            return Optional.empty();
        }
    }

    @Override
    public Collection<T> findAll() {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            Collection<T> result = em.createQuery(String.format("SELECT e FROM %s e", this.clazz.getSimpleName()), this.clazz)
                .getResultList();
            result.forEach(x -> this.initializeInstance(x));
            return result;
        }
    }

    protected abstract void initializeInstance(final T instance);
}
