package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.opentosca.container.core.next.utils.EntityManagerProvider;

public class JpaRepository<T> implements Repository<T, Long> {

  protected final Class<T> clazz;

  protected final EntityManagerFactory emf;


  public JpaRepository(final Class<T> clazz) {
    this.clazz = clazz;
    this.emf = EntityManagerProvider.getEntityManagerFactory();
  }

  @Override
  public void add(final T entity) {
    final EntityManager em = this.emf.createEntityManager();
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
    final EntityManager em = this.emf.createEntityManager();
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
  public T update(final T entity) {
    final EntityManager em = this.emf.createEntityManager();
    T updatedEntity = null;
    try {
      em.getTransaction().begin();
      updatedEntity = em.merge(entity);
      em.getTransaction().commit();
    } finally {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      em.close();
    }
    return updatedEntity;
  }

  @Override
  public void remove(final T entity) {
    final EntityManager em = this.emf.createEntityManager();
    try {
      em.getTransaction().begin();
      em.remove(entity);
      em.getTransaction().commit();
    } finally {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      em.close();
    }
  }

  @Override
  public T findById(final Long id) {
    final EntityManager em = this.emf.createEntityManager();
    return em.find(this.clazz, id);
  }

  @Override
  public Collection<T> findAll() {
    final EntityManager em = this.emf.createEntityManager();
    return em.createQuery(String.format("from %s e", this.clazz.getSimpleName()), this.clazz)
        .getResultList();
  }
}
