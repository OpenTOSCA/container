package org.opentosca.container.core.model.deployment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.persistence.CsarIdConverter;

/**
 * Abstract class for deployment information that belongs to a CSAR file.
 */
@MappedSuperclass
@Converter(name = AbstractDeploymentInfo.converterName, converterClass = CsarIdConverter.class)
public abstract class AbstractDeploymentInfo {

    static final String converterName = "CsarIdConverter";
    
    // TODO: Rename property to csarId
    @Convert(converterName)
    @Column(name = "csarID")
    private CsarId csarID;


    // 0-args ctor for JPA 
    protected AbstractDeploymentInfo() { }

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
