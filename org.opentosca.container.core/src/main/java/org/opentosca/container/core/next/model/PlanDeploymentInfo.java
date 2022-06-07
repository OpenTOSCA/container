package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.NoArgsConstructor;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.AbstractFileDeploymentInfo;

/**
 * Deployment information of a Plan inside a CSAR file. It is used for tracking its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = PlanDeploymentInfo.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(columnNames = {"csarID", "RelPath"})
)
@NoArgsConstructor
public class PlanDeploymentInfo extends AbstractFileDeploymentInfo {

    protected final static String TABLE_NAME = "PlanDeploymentInfo";

    /**
     * Deployment state of this Plan.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DeploymentState")
    private PlanDeploymentState deploymentState;

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
