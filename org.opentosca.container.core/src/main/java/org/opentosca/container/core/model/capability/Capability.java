package org.opentosca.container.core.model.capability;

import org.opentosca.container.core.model.capability.provider.ProviderType;

import javax.persistence.*;

@Entity
@Table(name = "CAPABILITIES")
@NamedQueries( {
  @NamedQuery(name = Capability.byProviderType, query = Capability.byProviderTypeQuery),
  @NamedQuery(name = Capability.byProvider, query = Capability.byProviderQuery)
})
public class Capability {
  public static final String byProviderType = "Capability.ByProviderType";
  public static final String byProvider = "Capability.ByProvider";

  static final String byProviderTypeQuery = "SELECT Capability c WHERE c.ProviderType = :providerType";
  static final String byProviderQuery = "SELECT Capability c WHERE c.ProviderType = :providerType AND c.ProviderName = :providerName";

  @Column(name = "Capability")
  private String capability;
  @Column(name = "ProviderName")
  private String providerName;
  @Enumerated(EnumType.STRING)
  @Column(name = "ProviderType")
  private ProviderType providerType;

  // required for JPA
  public Capability() {
  }

  public Capability(String capability, String providerName, ProviderType providerType) {
    this.capability = capability;
    this.providerName = providerName;
    this.providerType = providerType;
  }

  public String getCapability() {
    return capability;
  }

  public void setCapability(String capability) {
    this.capability = capability;
  }

  public String getProviderName() {
    return providerName;
  }

  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }

  public ProviderType getProviderType() {
    return providerType;
  }

  public void setProviderType(ProviderType providerType) {
    this.providerType = providerType;
  }
}
