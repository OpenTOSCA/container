package org.opentosca.container.core.next.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.opentosca.container.core.next.utils.Consts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = VerificationResult.TABLE_NAME)
public class VerificationResult extends PersistenceObject {

  private static final long serialVersionUID = 7456157949253267729L;

  public static final String TABLE_NAME = "VERIFICATION_RESULT";

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date start;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date end;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VerificationState state = VerificationState.UNKNOWN;

  private String output;

  @ManyToOne
  @JoinColumn(name = "VERIFICATION_ID")
  @JsonIgnore
  private Verification verification;

  @ManyToOne
  @JoinColumn(name = "NODE_TEMPLATE_INSTANCE_ID")
  @JsonIgnoreProperties({"state", "service_template_instance", "incoming_relations",
      "outgoing_relations"})
  private NodeTemplateInstance nodeTemplateInstance;

  @ManyToOne
  @JoinColumn(name = "SERVICEE_TEMPLATE_INSTANCE_ID")
  @JsonIgnoreProperties({"state", "plan_instances", "node_template_instances",})
  private ServiceTemplateInstance serviceTemplateInstance;


  public VerificationResult() {}


  @Override
  @JsonIgnore
  public Long getId() {
    return super.getId();
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(final Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(final Date end) {
    this.end = end;
  }

  public VerificationState getState() {
    return state;
  }

  public void setState(final VerificationState state) {
    this.state = state;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(final String output) {
    this.output = output;
  }

  public Verification getVerification() {
    return verification;
  }

  public void setVerification(final Verification verification) {
    this.verification = verification;
    if (!verification.getVerificationResults().contains(this)) {
      verification.getVerificationResults().add(this);
    }
  }

  public NodeTemplateInstance getNodeTemplateInstance() {
    return this.nodeTemplateInstance;
  }

  public void setNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
    this.nodeTemplateInstance = nodeTemplateInstance;
    if (!nodeTemplateInstance.getVerificationResults().contains(this)) {
      nodeTemplateInstance.getVerificationResults().add(this);
    }
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getVerificationResults().contains(this)) {
      serviceTemplateInstance.getVerificationResults().add(this);
    }
  }

  public void start() {
    this.start = new Date();
  }

  public void append(final String output) {
    if (this.output == null) {
      this.output = output;
    } else {
      this.output = new StringBuilder(this.output).append(Consts.NL).append(output).toString();
    }
  }

  public void failed() {
    this.end = new Date();
    if (this.start == null) {
      this.start = this.end;
    }
    this.state = VerificationState.FAILED;
  }

  public void success() {
    this.end = new Date();
    if (this.start == null) {
      this.start = this.end;
    }
    this.state = VerificationState.SUCCESS;
  }
}
