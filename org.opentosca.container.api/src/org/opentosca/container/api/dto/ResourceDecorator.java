package org.opentosca.container.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDecorator extends ResourceSupport {

  @JsonUnwrapped
  @JsonProperty("items")
  private Object object;

  @JsonProperty("_embedded")
  private Object embedded;

  public Object getObject() {
    return object;
  }

  public void setObject(Object object) {
    this.object = object;
  }

  public Object getEmbedded() {
    return embedded;
  }

  public void setEmbedded(Object embedded) {
    this.embedded = embedded;
  }
}
