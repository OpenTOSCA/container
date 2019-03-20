package org.opentosca.container.core.model.deployment;

import javax.persistence.*;

import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.model.csar.CsarId;

/**
 * Abstract class for deployment information that belongs to a CSAR file.
 */
@MappedSuperclass
public abstract class AbstractDeploymentInfo {

  // because we cannot convert CsarId when it's marked as Id, we use a surrogate Id here
  @Id
  @GeneratedValue
  private long id;

  // must not be annotated as @Id because converters do not apply to Ids
  @Column(name = "csarID", unique = true, nullable = false)
  @Convert(converter = CsarIdConverter.class)
  private CsarId csarID;


  // 0-args ctor for JPA
  protected AbstractDeploymentInfo() {
  }

  /**
   * @param csarID that uniquely identifies a CSAR file
   */
  public AbstractDeploymentInfo(final CsarId csarID) {
    this.csarID = csarID;
  }

  public CsarId getCsarID() {
    return this.csarID;
  }

  public void setCsarID(final CsarId csarID) {
    this.csarID = csarID;
  }
}
