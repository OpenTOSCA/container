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
        return this.object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    public Object getEmbedded() {
        return this.embedded;
    }

    public void setEmbedded(final Object embedded) {
        this.embedded = embedded;
    }
}
