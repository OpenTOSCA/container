package org.opentosca.container.api.dto.openapi;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OpenAPIMap extends HashMap<String, String> {

    @JsonIgnore
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
