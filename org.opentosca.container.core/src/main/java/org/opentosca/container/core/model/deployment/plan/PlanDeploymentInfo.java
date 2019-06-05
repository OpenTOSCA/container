package org.opentosca.container.core.model.deployment.plan;

import javax.persistence.*;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.AbstractFileDeploymentInfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Deployment information of a Plan inside a CSAR file. It is used for tracking its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries( {
  @NamedQuery(name = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDAndRelPath,
    query = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDAndRelPathQuery),
  @NamedQuery(name = PlanDeploymentInfo.getPlanDeploymentInfoByCSARID,
    query = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDQuery)
})
@Table(name = PlanDeploymentInfo.tableName,
  uniqueConstraints = @UniqueConstraint(columnNames = {"csarID", "RelPath"})
)
public class PlanDeploymentInfo extends AbstractFileDeploymentInfo {

  public static final String getPlanDeploymentInfoByCSARID = "PlanDeploymentInfo.ByCSARID";
  public static final String getPlanDeploymentInfoByCSARIDAndRelPath = "PlanDeploymentInfo.ByCSARIDAndRelPath";

  protected final static String tableName = "PlanDeploymentInfo";

  /*
   * JPQL Queries
   */
  protected static final String getPlanDeploymentInfoByCSARIDAndRelPathQuery =
    "select t from " + PlanDeploymentInfo.tableName + " t where t.relPath = :planRelPath and t.csarID = :csarID";
  protected static final String getPlanDeploymentInfoByCSARIDQuery =
    "select t from " + PlanDeploymentInfo.tableName + " t where t.csarID = :csarID";

  /**
   * Deployment state of this Plan.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "DeploymentState")
  private PlanDeploymentState deploymentState;


  protected PlanDeploymentInfo() {

  }

  public PlanDeploymentInfo(final CsarId csarID, final String relPath, final PlanDeploymentState deploymentState) {
    super(csarID, relPath);
    this.deploymentState = deploymentState;
  }

  public PlanDeploymentState getDeploymentState() {
    return this.deploymentState;
  }

  public void setDeploymentState(final PlanDeploymentState deploymentState) {
    this.deploymentState = deploymentState;
  }

}
