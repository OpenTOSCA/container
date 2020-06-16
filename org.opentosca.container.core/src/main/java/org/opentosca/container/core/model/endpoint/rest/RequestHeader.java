package org.opentosca.container.core.model.endpoint.rest;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RequestHeader {

    @Column(unique = true)
    private String header;
    private boolean required;

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }
}
