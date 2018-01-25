package org.opentosca.container.core.next.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = Verification.TABLE_NAME)
public class Verification extends PersistenceObject {

  public static final String TABLE_NAME = "VERIFICATION";

  private static final long serialVersionUID = 4929775787088737689L;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "verification", cascade = {CascadeType.ALL})
  private List<VerificationResult> verificationResults = Lists.newArrayList();

  @ManyToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  @JsonIgnore
  private ServiceTemplateInstance serviceTemplateInstance;


  public Verification() {
    this.timestamp = new Date();
  }


  @Override
  @JsonIgnore
  public Long getId() {
    return super.getId();
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = timestamp;
  }

  public List<VerificationResult> getVerificationResults() {
    return verificationResults;
  }

  public void setVerificationResults(final List<VerificationResult> verificationResults) {
    this.verificationResults = verificationResults;
  }

  public void addVerificationResult(final VerificationResult verificationResult) {
    this.verificationResults.add(verificationResult);
    if (verificationResult.getVerification() != this) {
      verificationResult.setVerification(this);
    }
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getVerifications().contains(this)) {
      serviceTemplateInstance.getVerifications().add(this);
    }
  }

  public Map<String, Object> getStatistics() {
    final Map<String, Object> stats = Maps.newHashMap();
    stats.put("total", this.verificationResults.size());
    stats.put("success", this.countJobsByState(VerificationState.SUCCESS));
    stats.put("failed", this.countJobsByState(VerificationState.FAILED));
    stats.put("unknown", this.countJobsByState(VerificationState.UNKNOWN));
    return stats;
  }

  private long countJobsByState(final VerificationState state) {
    return this.verificationResults.stream().filter(r -> r.getState().equals(state)).count();
  }
}
