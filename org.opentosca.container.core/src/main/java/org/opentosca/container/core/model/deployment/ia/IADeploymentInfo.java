package org.opentosca.container.core.model.deployment.ia;

import javax.persistence.*;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.AbstractFileDeploymentInfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Deployment information of a Implementation Artifact inside a CSAR file. It is used for tracking
 * its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries( {
  @NamedQuery(name = IADeploymentInfo.getIADeploymentInfoByCSARIDAndRelPath,
    query = IADeploymentInfo.getIADeploymentInfoByCSARIDAndRelPathQuery),
  @NamedQuery(name = IADeploymentInfo.getIADeploymentInfoByCSARID,
    query = IADeploymentInfo.getIADeploymentInfoByCSARIDQuery)
})
@Table(name = IADeploymentInfo.tableName,
uniqueConstraints = @UniqueConstraint(columnNames = {"csarID", "RelPath"}))
public class IADeploymentInfo extends AbstractFileDeploymentInfo {

  public static final String getIADeploymentInfoByCSARIDAndRelPath = "IADeploymentInfo.ByCSARIDAndRelPath";
  public static final String getIADeploymentInfoByCSARID = "IADeploymentInfo.ByCSARID";

  protected static final String tableName = "IADeploymentInfo";

  /*
   * JPQL Queries
   */
  protected static final String getIADeploymentInfoByCSARIDAndRelPathQuery =
    "select t from " + IADeploymentInfo.tableName + " t where t.relPath = :iaRelPath and t.csarID = :csarID";
  protected static final String getIADeploymentInfoByCSARIDQuery =
    "select t from " + IADeploymentInfo.tableName + " t where t.csarID = :csarID";

  /**
   * Deployment state of this IA.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "DeploymentState")
  private IADeploymentState deploymentState;


  protected IADeploymentInfo() {

  }

  public IADeploymentInfo(final CsarId csarID, final String relPath, final IADeploymentState deploymentState) {
    super(csarID, relPath);
    this.deploymentState = deploymentState;
  }

  public IADeploymentState getDeploymentState() {
    return this.deploymentState;
  }

  public void setDeploymentState(final IADeploymentState deploymentState) {
    this.deploymentState = deploymentState;
  }

}
