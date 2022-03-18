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
import org.opentosca.container.core.model.deployment.AbstractFileDeploymentInfo;

/**
 * Deployment information of a Implementation Artifact inside a CSAR file. It is used for tracking its deploy progress.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = IADeploymentInfo.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(columnNames = {"csarID", "RelPath"}))
@NoArgsConstructor
public class IADeploymentInfo extends AbstractFileDeploymentInfo {

    protected static final String TABLE_NAME = "IADeploymentInfo";

    /**
     * Deployment state of this IA.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DeploymentState")
    private IADeploymentState deploymentState;

    public IADeploymentState getDeploymentState() {
        return this.deploymentState;
    }

    public void setDeploymentState(final IADeploymentState deploymentState) {
        this.deploymentState = deploymentState;
    }
}
