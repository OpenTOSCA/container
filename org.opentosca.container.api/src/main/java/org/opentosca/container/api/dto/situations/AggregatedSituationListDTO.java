package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "AggregatedSituationResources")
public class AggregatedSituationListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "AggregatedSituation")
    @XmlElementWrapper(name = "AggregatedSituations")
    private final List<AggregatedSituationDTO> aggregatedSituations = Lists.newArrayList();


    public List<AggregatedSituationDTO> getAggregatedSituations() {
        return this.aggregatedSituations;
    }

    public void add(final AggregatedSituationDTO... aggregatedSituations) {
        this.aggregatedSituations.addAll(Arrays.asList(aggregatedSituations));
    }
}

