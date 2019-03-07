package org.opentosca.container.core.impl.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.opentosca.container.core.model.capability.Capability;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class stores, gets and deletes capabilities of different provider and provider types in/from
 * a SQL database. The IAEngine needs this capabilities to decide if a Implementation Artifact
 * should be deployed or not.
 *
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @see ProviderType
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
@Service
public class CoreCapabilityServiceImpl implements ICoreCapabilityService {
    private static final Logger LOG = LoggerFactory.getLogger(CoreCapabilityServiceImpl.class);

    private final EntityManager em;

    public CoreCapabilityServiceImpl() {
        this.em = EntityManagerProvider.createEntityManager();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void storeCapabilities(final List<String> capabilities, final String providerName,
                                  final ProviderType providerType) {
        LOG.debug("Storing \"{}\" capabilities of \"{}\" ...", providerType, providerName);
        em.getTransaction().begin();

        for (String capability : capabilities) {
            Capability entity = new Capability(capability, providerName, providerType);
            em.persist(entity);
        }
        em.getTransaction().commit();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getCapabilities(final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities...", providerType);
        em.getTransaction().begin();

        TypedQuery<Capability> query = em.createNamedQuery(Capability.byProviderType, Capability.class);
        query.setParameter("providerType", providerType);

        Map<String, List<String>> capabilitiesMap = query.getResultList().stream()
            .collect(Collectors.groupingBy(Capability::getProviderName, Collectors.mapping(Capability::getCapability, Collectors.toList())));

        LOG.debug("Getting \"{}\" capabilities successfully completed.", providerType);
        em.getTransaction().commit();
        return capabilitiesMap;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilities(final String providerName, final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities of \"{}\"...", providerType, providerName);
        em.getTransaction().begin();

        TypedQuery<Capability> query = em.createNamedQuery(Capability.byProvider, Capability.class);
        query.setParameter("providerType", providerType);
        query.setParameter("providerName", providerName);

        final List<String> capabilities = query.getResultList().stream()
            .map(Capability::getCapability).collect(Collectors.toList());

        LOG.debug("Getting \"{}\" capabilities of \"{}\" successfully completed.", providerType, providerName);
        em.getTransaction().commit();
        return capabilities;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void deleteCapabilities(final String providerName) {
        LOG.debug("Deleting capabilities of \"{}\" ...", providerName);
        em.getTransaction().begin();
        TypedQuery<Capability> query = em.createNamedQuery(Capability.byProvider, Capability.class);
        query.setParameter("providerName", providerName);
        for (ProviderType providerType : ProviderType.values()) {
            query.setParameter("providerType", providerType);
            query.getResultList().forEach(em::remove);
        }
        em.getTransaction().commit();
    }
}
