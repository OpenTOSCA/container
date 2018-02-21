package org.opentosca.container.core.model.deployment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Abstract class for the deployment information of a file inside a CSAR file.
 */
@MappedSuperclass
public abstract class AbstractFileDeploymentInfo extends AbstractDeploymentInfo {

    /**
     * Relative path where the file is located inside a CSAR file.
     */
    @Column(name = "RelPath")
    private String relPath;

    /**
     * Counts the number of deployment / undeployment attempts. It will be incremented by one on every
     * deployment / undeployment attempt. If a file is deployed and will be now undeployed it will be
     * reseted to 0.
     */
    @Column(name = "Attempt")
    private int attempt = 0;


    protected AbstractFileDeploymentInfo() {

    }

    public AbstractFileDeploymentInfo(final CSARID csarID, final String relPath) {
        super(csarID);
        this.relPath = relPath;
    }

    public String getRelPath() {
        return this.relPath;
    }

    public void setRelPath(final String relPath) {
        this.relPath = relPath;
    }

    public int getAttempt() {
        return this.attempt;
    }

    public void setAttempt(final int attempt) {
        this.attempt = attempt;
    }
}
