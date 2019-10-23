package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.opentosca.container.core.next.jpa.AutoCloseableEntityManager;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;

public abstract class JpaRepository<T> implements Repository<T, Long> {

  protected final Class<T> clazz;

  public JpaRepository(final Class<T> clazz) {
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
      em.refresh(entity);
      return Optional.ofNullable(entity);
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Collection<T> findAll() {
    try (AutoCloseableEntityManager em = EntityManagerProvider.createEntityManager()) {
      return em.createQuery(String.format("SELECT e FROM %s e", this.clazz.getSimpleName()), this.clazz)
        .getResultList();
    }
  }
}
