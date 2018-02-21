package org.opentosca.container.api.legacy.resources.csar.jaxb;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is currently not in Use.<br>
 * <br>
 * Provides static methods to create instances of Classes provided by this package. <br>
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class JaxbFactoryCSAR {

    public static AllDeploymentArtifactsJaxb createAllDeploymentArtifactsDTO(
                    final List<DeploymentArtifactAbsJaxb> allDeploymentArtifacts, final String serviceTemplateID,
                    final String csarID) {
        final AllDeploymentArtifactsJaxb jaxbObject = new AllDeploymentArtifactsJaxb();
        jaxbObject.setAllDeploymentArtifacts(allDeploymentArtifacts);
        jaxbObject.setServiceTemplateID(serviceTemplateID);
        jaxbObject.setCSARID(csarID);
        return jaxbObject;
    }

    public static DeploymentArtifactAbsJaxb createDeploymentArtifactAbsDTO(final String absolutePath, final String name,
                    final String type) {
        final DeploymentArtifactAbsJaxb dto = new DeploymentArtifactAbsJaxb();
        dto.setAbsolutePath(absolutePath);
        dto.setName(name);
        dto.setType(type);
        return dto;
    }

    public static List<DeploymentArtifactAbsJaxb> createDADTOList() {
        return new ArrayList<>();
    }
}
