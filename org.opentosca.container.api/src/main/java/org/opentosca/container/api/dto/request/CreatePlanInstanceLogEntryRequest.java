package org.opentosca.container.api.dto.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "log")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePlanInstanceLogEntryRequest {

  @XmlValue
  private String logEntry;

  public String getLogEntry() {
    return this.logEntry;
  }

  public void setLogEntry(final String logEntry) {
    this.logEntry = logEntry;
  }
}
