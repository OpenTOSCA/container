package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@XmlRootElement(name = "SituationTriggerResources")
public class SituationTriggerListDTO extends ResourceSupport {

  @JsonProperty
  @XmlElement(name = "SituationTrigger")
  @XmlElementWrapper(name = "SituationTriggers")
  private final List<SituationTriggerDTO> situationTriggers = Lists.newArrayList();

  public List<SituationTriggerDTO> getSituationTriggers() {
    return this.situationTriggers;
  }

  public void add(final SituationTriggerDTO... situations) {
    this.situationTriggers.addAll(Arrays.asList(situations));
  }
}
