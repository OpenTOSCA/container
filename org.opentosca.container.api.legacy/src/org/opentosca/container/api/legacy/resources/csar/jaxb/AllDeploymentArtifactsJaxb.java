package org.opentosca.container.api.legacy.resources.csar.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is currently not in Use.<br>
 * <br>
 *
 * Class to provide JAXB-Annotation for a AllDeploymentArtifacts <br>
 * Intended to show all DeploymentArtifacts and their absolutePaths within a csarFile. <br>
 * <br>
 *
 * To be used by Jersey to automatically generate XML-Responses<br>
 * <br>
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
@XmlRootElement(name = "AllDeploymentArtifacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllDeploymentArtifactsJaxb {

    @XmlElement(name = "DeploymentArtifact")
    private List<DeploymentArtifactAbsJaxb> allDeploymentArtifacts;
    @XmlAttribute(name = "ServiceTemplateId")
    private String serviceTemplateID;
    @XmlAttribute(name = "csarID")
    private String csarID;


    public AllDeploymentArtifactsJaxb() {

    }

    public List<DeploymentArtifactAbsJaxb> getAllDeploymentArtifacts() {
        return this.allDeploymentArtifacts;
    }

    public void setAllDeploymentArtifacts(final List<DeploymentArtifactAbsJaxb> allDeploymentArtifacts) {
        this.allDeploymentArtifacts = allDeploymentArtifacts;
    }

    public String getServiceTemplateID() {
        return this.serviceTemplateID;
    }

    public void setServiceTemplateID(final String serviceTemplateID) {
        this.serviceTemplateID = serviceTemplateID;
    }

    public String getCSARID() {
        return this.csarID;
    }

    public void setCSARID(final String csarID) {
        this.csarID = csarID;
    }
}
