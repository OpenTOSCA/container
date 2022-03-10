package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.Optional;

import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JpaRepository<T> implements Repository<T, Long> {

    protected static final Logger logger = LoggerFactory.getLogger(JpaRepository.class);
    protected final Class<T> clazz;

    public JpaRepository(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void add(final T entity) {
        logger.debug("Adding following entity with class " + entity.getClass().getCanonicalName() + " entity: " + entity);
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (final Exception e) {
            logger.error("Failed to add instance of class {} with id {} in persistence context.", clazz.getSimpleName(), entity, e);
            e.printStackTrace();
        }
    }

    @Override
    public void add(final Iterable<T> items) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            em.getTransaction().begin();
            items.forEach(em::persist);
            em.getTransaction().commit();
        } catch (final Exception e) {
            logger.error("Failed to add instances of class {} with id {} in persistence context.", clazz.getSimpleName(), items.toString(), e);
            e.printStackTrace();
        }
    }

    @Override
    public void update(final T entity) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (final Exception e) {
            logger.error("Failed to update instance of class {} with id {} in persistence context.", clazz.getSimpleName(), entity.toString(), e);
        }
    }

    @Override
    public void remove(final T entity) {
        try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } catch (final Exception e) {
            logger.error("Failed to update instance of class {} with id {} in persistence context.", clazz.getSimpleName(), entity.toString(), e);
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
            logger.error("Failed to find instance of class {} with id {} in persistence context.", clazz.getSimpleName(), id, e);
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
