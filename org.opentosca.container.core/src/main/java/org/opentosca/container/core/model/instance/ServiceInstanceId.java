package org.opentosca.container.core.model.instance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Identifies a service template instance.
 */
@Deprecated
@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInstanceId {

  @XmlElement(name = "Id")
  private Integer id;

  @XmlElement(name = "Csar")
  private String csar;

  @XmlElement(name = "ServiceTemplate")
  private String serviceTemplate;


  public ServiceInstanceId() {
  }

  public ServiceInstanceId(final Integer id, final String csar, final String serviceTemplate) {
    this.id = id;
    this.csar = csar;
    this.serviceTemplate = serviceTemplate;
  }

  public String getCsar() {
    return this.csar;
  }

  public void setCsar(final String csar) {
    this.csar = csar;
  }

  public String getServiceTemplate() {
    return this.serviceTemplate;
  }

  public void setServiceTemplate(final String serviceTemplate) {
    this.serviceTemplate = serviceTemplate;
  }

  public Integer getId() {
    return this.id;
  }
}
