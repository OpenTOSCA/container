package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Endpoint.TABLE_NAME)
public class Capability extends PersistenceObject {

    public static final String TABLE_NAME = "CAPABILITIES";

    private static final long serialVersionUid = 684635434L;

    @Column(name = "capability")
    private String capability;

    @Column(name = "providerName")
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "providerType")
    private ProviderType providerType;

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
