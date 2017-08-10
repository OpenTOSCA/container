package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T, K> {

  void add(final T entity);

  void add(final Iterable<T> items);

  void update(final T entity);

  void remove(final T entity);

  Optional<T> find(final K id);

  Collection<T> findAll();
}
