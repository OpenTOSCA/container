package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.AbstractDeploymentInfo;

/**
 * Deployment information of a Csar file. It is used for tracking its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = DeploymentProcessInfo.TABLE_NAME)
@NoArgsConstructor
public class DeploymentProcessInfo extends AbstractDeploymentInfo {

    protected static final String TABLE_NAME = "DeploymentProcessInfo";

    @Enumerated(EnumType.STRING)
    @Column(name = "DeploymentProcessState")
    private DeploymentProcessState deploymentProcessState;

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
