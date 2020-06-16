package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "SituationResources")
public class SituationListDTO extends ResourceSupport {

  @JsonProperty
  @XmlElement(name = "Situation")
  @XmlElementWrapper(name = "Situations")
  private final List<SituationDTO> situations = Lists.newArrayList();

  public List<SituationDTO> getSituations() {
    return this.situations;
  }

  public void add(final SituationDTO... situations) {
    this.situations.addAll(Arrays.asList(situations));
  }
}
