package org.opentosca.container.api.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "PlanInstance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanInstanceDTO extends ResourceSupport {

  @JsonIgnore
  private PlanInstance pi;


  public PlanInstanceDTO() {

  }

  public PlanInstanceDTO(PlanInstance pi) {
    this.pi = pi;
  }

  @JsonProperty
  @XmlAttribute(name = "id")
  public String getId() {
    return this.pi.getCorrelationId();
  }

  public void setId(final String id) {
    this.pi.setCorrelationId(id);
  }

  @JsonProperty
  @XmlAttribute(name = "state")
  public PlanInstanceState getState() {
    return this.pi.getState();
  }

  public void setState(final PlanInstanceState state) {
    this.pi.setState(state);
  }

  @JsonProperty
  @XmlElement(name = "OutputParameter")
  @XmlElementWrapper(name = "OutputParameters")
  public Collection<PlanInstanceOutput> getOutput() {
    return this.pi.getOutputs();
  }

  public void setOutput(final Set<PlanInstanceOutput> outputs) {
    this.pi.setOutputs(outputs);
  }

  @JsonProperty
  @XmlElement(name = "LogEntry")
  @XmlElementWrapper(name = "Logs")
  public List<PlanInstanceEvent> getLogs() {
    return this.pi.getEvents();
  }

  public void setLogs(final List<PlanInstanceEvent> logs) {
    this.pi.setEvents(logs);
  }

  @JsonIgnore
  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.pi.getServiceTemplateInstance();
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.pi.setServiceTemplateInstance(serviceTemplateInstance);
  }
}
