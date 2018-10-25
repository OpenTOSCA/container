package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "CsarResources")
public class CsarListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "Csar")
    @XmlElementWrapper(name = "Csars")
    private final List<CsarDTO> csars = new ArrayList<>();


    public List<CsarDTO> getCsars() {
        return this.csars;
    }

    public void add(final CsarDTO... csars) {
        this.csars.addAll(Arrays.asList(csars));
    }
}
