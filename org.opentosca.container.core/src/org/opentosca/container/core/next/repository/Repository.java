package org.opentosca.container.core.next.repository;

import java.util.Collection;

public interface Repository<T, K> {

  void add(final T entity);

  void add(final Iterable<T> items);

  void update(final T entity);

  void remove(final T entity);

  T find(final K id);

  Collection<T> findAll();
}
