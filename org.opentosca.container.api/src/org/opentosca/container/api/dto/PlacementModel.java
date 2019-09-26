package org.opentosca.container.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacementModel {

    @JsonProperty("needToBePlaced")
    private List<PlacementNodeTemplate> needToBePlaced = Lists.newArrayList();

    PlacementModel() {}

    PlacementModel(final List<PlacementNodeTemplate> need_to_be_placed) {
        this.needToBePlaced = need_to_be_placed;
    }

    public void setNeedToBePlaced(final List<PlacementNodeTemplate> needToBePlaced) {
        this.needToBePlaced = needToBePlaced;
    }

    public List<PlacementNodeTemplate> getNeedToBePlaced() {
        return this.needToBePlaced;
    }

}
