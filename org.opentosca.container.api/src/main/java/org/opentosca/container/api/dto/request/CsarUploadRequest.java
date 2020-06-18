package org.opentosca.container.api.dto.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "CsarUploadRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsarUploadRequest {

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Url")
    private String url;

    @XmlElement(name = "Enrich")
    private String enrich;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getEnrich() {
        return this.enrich;
    }

    public void setEnrich(final String enrich) {
        this.enrich = enrich;
    }
}
