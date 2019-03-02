package org.opentosca.container.core.model.deployment.process;

import javax.persistence.*;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.AbstractDeploymentInfo;

/**
 * Deployment information of a Csar file. It is used for tracking its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({@NamedQuery(name = DeploymentProcessInfo.getDeploymentProcessInfoByCSARID,
                           query = DeploymentProcessInfo.getDeploymentProcessInfoByCSARIDQuery)})
@Table(name = DeploymentProcessInfo.tableName)
public class DeploymentProcessInfo extends AbstractDeploymentInfo {

    protected static final String tableName = "DeploymentProcessInfo";

    public static final String getDeploymentProcessInfoByCSARID = "DeploymentProcessInfo.ByCSARID";
    protected static final String getDeploymentProcessInfoByCSARIDQuery =
        "select t from " + DeploymentProcessInfo.tableName + " t where t.csarID = :csarID";

    @Enumerated(EnumType.STRING)
    @Column(name = "DeploymentProcessState")
    private DeploymentProcessState deploymentProcessState;

    // 0-args ctor for JPA
    protected DeploymentProcessInfo() { }

    public DeploymentProcessInfo(final CsarId csarID, final DeploymentProcessState deploymentProcessState) {
        super(csarID);
        this.deploymentProcessState = deploymentProcessState;
    }

    public DeploymentProcessState getDeploymentProcessState() {
        return this.deploymentProcessState;
    }

    public void setDeploymentProcessState(final DeploymentProcessState deploymentProcessState) {
        this.deploymentProcessState = deploymentProcessState;
    }
}
