package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class SituationTriggerListDTO {

    @JsonProperty
    @XmlElement(name = "SituationTrigger")
    @XmlElementWrapper(name = "SituationTriggers")
    private final List<SituationTriggerDTO> situationTriggers = Lists.newArrayList();


    public void add(final SituationTriggerDTO... situations) {
        this.situationTriggers.addAll(Arrays.asList(situations));
    }
}
