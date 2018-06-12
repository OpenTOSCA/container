package org.opentosca.container.portability.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ArtifactReferences {

    public ArtifactReferences(final List<String> references) {
        this.allReferences = references;
    }


    @XmlElement(name = "ref")
    public List<String> allReferences;

}
