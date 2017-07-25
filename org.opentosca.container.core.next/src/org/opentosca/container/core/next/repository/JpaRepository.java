package org.opentosca.container.core.next.repository;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.opentosca.container.core.next.utils.EntityManagerProvider;

public class JpaRepository<T> implements Repository<T> {

  protected final Class<T> clazz;

  protected final EntityManager em;


  public JpaRepository(final Class<T> clazz) {
    this.clazz = clazz;
    this.em = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
  }

  @Override
  public void add(final T entity) {
    try {
      this.em.getTransaction().begin();
      this.em.persist(entity);
      this.em.getTransaction().commit();
    } finally {
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      this.em.close();
    }
  }

  @Override
  public void add(final Iterable<T> items) {
    try {
      this.em.getTransaction().begin();
      items.forEach(this.em::persist);
      this.em.getTransaction().commit();
    } finally {
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      this.em.close();
    }
  }

  @Override
  public void update(final T entity) {
    try {
      this.em.getTransaction().begin();
      this.em.merge(entity);
      this.em.getTransaction().commit();
    } finally {
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      this.em.close();
    }
  }

  @Override
  public void remove(final T entity) {
    try {
      this.em.getTransaction().begin();
      this.em.remove(entity);
      this.em.getTransaction().commit();
    } finally {
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      this.em.close();
    }
  }

  @Override
  public T findById(final Long id) {
    return this.em.find(this.clazz, id);
  }

  @Override
  public Collection<T> findAll() {
    return this.em.createQuery("from " + this.clazz.getName(), this.clazz).getResultList();
  }
}
