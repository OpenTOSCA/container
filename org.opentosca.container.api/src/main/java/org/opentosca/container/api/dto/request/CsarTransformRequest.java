package org.opentosca.container.api.dto.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "CsarTransformRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsarTransformRequest {

    @XmlElement(name = "SourceCsarName")
    private String sourceCsarName;

    @XmlElement(name = "TargetCsarName")
    private String targetCsarName;

    public String getSourceCsarName() {
        return this.sourceCsarName;
    }

    public void setSourceCsarName(final String sourceCsarName) {
        this.sourceCsarName = sourceCsarName;
    }

    public String getTargetCsarName() {
        return this.targetCsarName;
    }

    public void setTargetCsarName(final String targetCsarName) {
        this.targetCsarName = targetCsarName;
    }
}
