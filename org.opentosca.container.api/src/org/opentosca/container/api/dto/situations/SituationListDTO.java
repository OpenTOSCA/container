package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "Situations")
public class SituationListDTO {


    @JsonProperty
    @XmlElement(name = "Situation")
    @XmlElementWrapper(name = "Situations")
    private final List<SituationDTO> situations = Lists.newArrayList();


    public void add(final SituationDTO... situations) {
        this.situations.addAll(Arrays.asList(situations));
    }
}
