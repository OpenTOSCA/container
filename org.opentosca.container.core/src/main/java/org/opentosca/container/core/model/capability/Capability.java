package org.opentosca.container.core.model.capability;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.opentosca.container.core.model.capability.provider.ProviderType;

@Entity
@Table(name = Capability.TABLE_NAME)
@NamedQueries( {
    @NamedQuery(name = Capability.byProviderType, query = Capability.byProviderTypeQuery),
    @NamedQuery(name = Capability.byProvider, query = Capability.byProviderQuery)
})
public class Capability implements Serializable {
    public static final String TABLE_NAME = "CAPABILITIES";

    public static final String byProviderType = "Capability.ByProviderType";
    public static final String byProvider = "Capability.ByProvider";

    static final String byProviderTypeQuery = "SELECT c FROM Capability c WHERE c.providerType = :providerType";
    static final String byProviderQuery = "SELECT c FROM Capability c WHERE c.providerType = :providerType AND c.providerName = :providerName";

    private static final long serialVersionUid = 684635434L;

    @Column(name = "Capability")
    @Id
    private String capability;
    @Column(name = "ProviderName")
    @Id
    private String providerName;
    @Enumerated(EnumType.STRING)
    @Column(name = "ProviderType")
    @Id
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

    @Override
    public int hashCode() {
        return capability.hashCode() ^ providerName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Capability)) {
            return false;
        }
        Capability other = (Capability) o;
        return capability.equals(other.capability)
            && providerName.equals(other.providerName)
            && providerType.equals(other.providerType);
    }
}
