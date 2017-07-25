package org.opentosca.container.core.next.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.common.base.Objects;

@MappedSuperclass
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (this.getClass() != o.getClass())) {
      return false;
    }
    final BaseEntity entity = (BaseEntity) o;
    return Objects.equal(this.id, entity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id);
  }
}
