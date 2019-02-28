package org.opentosca.container.core.impl.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.opentosca.container.core.common.Settings;
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
    private final String CapabilitiesTable = "CAPABILITIES";
    private final EntityManager em;
    
    /**
     * SQL queries for CapabilitiesTable: Creating the CapabilitiesTable, storing capabilities, getting
     * already stored capabilities and deleting capabilities.
     */
    private final String createCapabilitiesTable = "CREATE TABLE " + this.CapabilitiesTable
        + " (Capability VARCHAR(1000) NOT NULL, ProviderName VARCHAR(1000) NOT NULL, ProviderType VARCHAR(1000) NOT NULL)";
    private final String storeCapabilities =
        "INSERT INTO " + this.CapabilitiesTable + " (Capability, ProviderName, ProviderType) VALUES (?, ?, ?)";
    private final String getCapabilitiesByType =
        "SELECT Capability, ProviderName FROM " + this.CapabilitiesTable + " WHERE ProviderType = ?";
    private final String getCapabilitiesByName =
        "SELECT Capability FROM " + this.CapabilitiesTable + " WHERE ProviderName = ? AND ProviderType = ?";
    private final String deleteCapabilities = "DELETE FROM " + this.CapabilitiesTable + " WHERE ProviderName = ?";


    /**
     * Get the capabilities of the container to store them.
     *
     * @see org.opentosca.settings.Settings
     */
    private final String containerCapabilities = Settings.getSetting("containerCapabilities");

    final private static Logger LOG = LoggerFactory.getLogger(CoreCapabilityServiceImpl.class);

    public CoreCapabilityServiceImpl() {
        this.em = EntityManagerProvider.createEntityManager();
        this.createCapabilitiesTable();
    }
    
    /**
     * Creates CapabilitiesTable if it not already exists.
     */
    private void createCapabilitiesTable() {
        LOG.debug("Checking if table \"{}\" already exists...", this.CapabilitiesTable);
        try {
            this.em.getTransaction().begin();
            final Connection conn = this.em.unwrap(Connection.class);

            final DatabaseMetaData metaData = conn.getMetaData();
            final ResultSet rs = metaData.getTables(null, null, this.CapabilitiesTable, null);

            if (!rs.next()) {
                LOG.debug("Table \"{}\" did not exist. Creating...", this.CapabilitiesTable);
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(this.createCapabilitiesTable);
                }
                LOG.debug("Table \"{}\" successfully created.", this.CapabilitiesTable);
            }
            this.em.getTransaction().commit();
        }
        catch (final SQLException exc) {
            LOG.error("Database error - can't create CapabilitiesTable.", exc);
            this.em.getTransaction().setRollbackOnly();
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void storeCapabilities(final List<String> capabilities, final String providerName,
                                  final ProviderType providerType) {
        try {
            LOG.debug("Storing \"{}\" capabilities of \"{}\" ...", providerType, providerName);
            em.getTransaction().begin();
            final Connection conn = em.unwrap(Connection.class);
        
            try (PreparedStatement pstmt = conn.prepareStatement(storeCapabilities)) {
                for (final String capability : capabilities) {
                    pstmt.setString(1, capability);
                    pstmt.setString(2, providerName);
                    pstmt.setString(3, providerType.name());
                    pstmt.executeUpdate();
                }
            }
            em.getTransaction().commit();
        }
        catch (final SQLException e) {
            LOG.error("Database error - can't store \"{}\" capabilities in database.", providerType, e);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getCapabilities(final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities...", providerType);
        try {
            em.getTransaction().begin();
            final Connection conn = em.unwrap(Connection.class);
            final Map<String, List<String>> capsMap = new HashMap<>();
            try (PreparedStatement pstmt = conn.prepareStatement(getCapabilitiesByType)) {
                pstmt.setString(1, providerType.name());
                try (ResultSet queryRes = pstmt.executeQuery()) {
                    // Fill map with Providers and their capabilities
                    while (queryRes.next()) {
                        capsMap.computeIfAbsent(queryRes.getString(2), a -> new ArrayList<String>())
                            .add(queryRes.getString(1));
                    }
                }
            }
            LOG.debug("Getting \"{}\" capabilities successfully completed.", providerType);
            em.getTransaction().commit();
            return capsMap;
        }
        catch (final SQLException e) {
            LOG.error("Database error - can't get \"{}\" capabilities from database.", providerType, e);
            return null;
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<String> getCapabilities(final String providerName, final ProviderType providerType) {
        LOG.debug("Getting \"{}\" capabilities of \"{}\"...", providerType, providerName);
        try {
            em.getTransaction().begin();
            final Connection conn = em.unwrap(Connection.class);
            final List<String> capabilities = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(getCapabilitiesByName)) {
                pstmt.setString(1, providerName);
                pstmt.setString(2, providerType.name());
                try (ResultSet queryRes = pstmt.executeQuery()) {
                    // Fill List with capabilities
                    while (queryRes.next()) {
                        capabilities.add(queryRes.getString(1));
                    }
                }
            }
            LOG.debug("Getting \"{}\" capabilities of \"{}\" successfully completed.", providerType, providerName);
            em.getTransaction().commit();
            return capabilities;
        }
        catch (final SQLException e) {
            LOG.error("Database error - can't get capabilities of \"{}\" from database.", providerName, e);
            return null;
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void deleteCapabilities(final String providerName) {
        try {
            LOG.debug("Deleting capabilities of \"{}\" ...", providerName);
            em.getTransaction().begin();
            final Connection conn = em.unwrap(Connection.class);
            try (PreparedStatement pstmt = conn.prepareStatement(deleteCapabilities)) {
                pstmt.setString(1, providerName);
                pstmt.executeUpdate();
            }
            em.getTransaction().commit();
        }
        catch (final SQLException e) {
            LOG.error("Database error - can't delete capabilities of \"{}\" from database.", providerName, e);
        }
    }

    /**
     * @return all capabilities of the container.
     */
    private List<String> getCapabilities() {
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : this.containerCapabilities.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

}
