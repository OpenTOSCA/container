package org.opentosca.container.core.next.repository;

import java.util.List;

import org.opentosca.container.core.next.model.Capability;
import org.opentosca.container.core.next.model.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapabilityRepository extends JpaRepository<Capability, Long> {

    List<Capability> findByProviderType(ProviderType providerType);

    List<Capability> findByProviderName(String providerName);

    List<Capability> findByProviderTypeAndProviderName(ProviderType providerType, String providerName);
}
