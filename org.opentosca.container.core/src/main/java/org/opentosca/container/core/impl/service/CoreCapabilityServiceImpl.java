package org.opentosca.container.core.impl.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.next.model.Capability;
import org.opentosca.container.core.next.repository.CapabilityRepository;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class stores, gets and deletes capabilities of different provider and provider types in/from a SQL database. The
 * IAEngine needs this capabilities to decide if a Implementation Artifact should be deployed or not.
 *
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see ProviderType
 */
@Service
public class CoreCapabilityServiceImpl implements ICoreCapabilityService {
    private static final Logger LOG = LoggerFactory.getLogger(CoreCapabilityServiceImpl.class);

    private final CapabilityRepository capabilityRepository;

    public CoreCapabilityServiceImpl(CapabilityRepository capabilityRepository) {
        this.capabilityRepository = capabilityRepository;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    @Transactional
    public void storeCapabilities(final List<String> capabilities, final String providerName,
                                  final ProviderType providerType) {
        LOG.debug("Storing \"{}\" capabilities of \"{}\" ...", providerType, providerName);
        for (String capability : capabilities) {
            capabilityRepository.save(new Capability(capability, providerName, providerType));
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getCapabilities(final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities...", providerType);

        Map<String, List<String>> capabilitiesMap = capabilityRepository.findByProviderType(providerType).stream()
            .collect(Collectors.groupingBy(Capability::getProviderName, Collectors.mapping(Capability::getCapability, Collectors.toList())));

        LOG.debug("Getting \"{}\" capabilities successfully completed.", providerType);
        return capabilitiesMap;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilities(final String providerName, final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities of \"{}\"...", providerType, providerName);

        final List<String> capabilities = capabilityRepository.findByProviderTypeAndProviderName(providerType, providerName).stream()
            .map(Capability::getCapability).collect(Collectors.toList());

        LOG.debug("Getting \"{}\" capabilities of \"{}\" successfully completed.", providerType, providerName);
        return capabilities;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    @Transactional
    public void deleteCapabilities(final String providerName) {
        LOG.debug("Deleting capabilities of \"{}\" ...", providerName);
        List<Capability> capabilitiesToDelete = capabilityRepository.findByProviderName(providerName);
        capabilityRepository.deleteAll(capabilitiesToDelete);
    }
}
