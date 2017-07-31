package org.opentosca.container.core.next.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

@MappedSuperclass
public class PersistenceObject implements Serializable {

  private static final long serialVersionUID = 7082895776724756832L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Version
  protected Long version;

  @Column(name = "CREATED_AT", insertable = true, updatable = false)
  protected Date createdAt;

  @Column(name = "UPDATED_AT", insertable = false, updatable = true)
  protected Date updatedAt;


  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Date getCreatedAt() {
    return this.createdAt;
  }

  private void setCreatedAt(final Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return this.updatedAt;
  }

  private void setUpdatedAt(final Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PrePersist
  void onCreate() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    this.setUpdatedAt(new Date());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (this.getClass() != o.getClass())) {
      return false;
    }
    final PersistenceObject entity = (PersistenceObject) o;
    return Objects.equals(this.id, entity.id) && Objects.equals(this.version, entity.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.version);
  }
}
