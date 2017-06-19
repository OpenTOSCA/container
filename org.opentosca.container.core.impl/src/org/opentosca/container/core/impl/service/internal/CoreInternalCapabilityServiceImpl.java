package org.opentosca.container.core.impl.service.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.service.internal.ICoreInternalCapabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class stores, gets and deletes capabilities of different provider and
 * provider types in/from a SQL database. The IAEngine needs this capabilities
 * to decide if a Implementation Artifact should be deployed or not.
 *
 * @see ProviderType
 *
 * @TODO This is a first, prototypical implementation of
 *       ICoreInternalCapabilityService. In future, this implementation maybe
 *       also should be realized with Eclipse-JPA, like it is done in e.g.
 *       CoreInternalEndpointServiceImpl.
 */
public class CoreInternalCapabilityServiceImpl implements ICoreInternalCapabilityService {
	
	private final static Logger LOG = LoggerFactory.getLogger(CoreInternalCapabilityServiceImpl.class);
	
	/**
	 * Location of the database that stores the capabilities. It will be created
	 * if it does not exist yet.
	 *
	 * @see org.opentosca.settings.Settings
	 */
	private final String dbLocation = Settings.getSetting("databaseLocation");
	private final String databaseURL = "jdbc:derby:" + this.dbLocation + ";create=true";
	
	private final String CapabilitiesTable = "CAPABILITIES";
	
	private Connection conn = null;
	
	/**
	 * SQL queries for CapabilitiesTable: Creating the CapabilitiesTable,
	 * storing capabilities, getting already stored capabilities and deleting
	 * capabilities.
	 */
	private final String createCapabilitiesTable = "CREATE TABLE " + this.CapabilitiesTable + " (Capability VARCHAR(1000) NOT NULL, ProviderName VARCHAR(1000) NOT NULL, ProviderType VARCHAR(1000) NOT NULL)";
	private final String storeCapabilities = "INSERT INTO " + this.CapabilitiesTable + " (Capability, ProviderName, ProviderType) VALUES (?, ?, ?)";
	private final String getCapabilitiesByType = "SELECT Capability, ProviderName FROM " + this.CapabilitiesTable + " WHERE ProviderType = ?";
	private final String getCapabilitiesByName = "SELECT Capability FROM " + this.CapabilitiesTable + " WHERE ProviderName = ? AND ProviderType = ?";
	private final String deleteCapabilities = "DELETE FROM " + this.CapabilitiesTable + " WHERE ProviderName = ?";
	
	
	/**
	 * Creates database connection and CapabilitiesTable.
	 */
	public CoreInternalCapabilityServiceImpl() {
		
		this.connectDatabase();
		this.createCapabilitiesTable();
	}
	
	/**
	 * Connects to the Derby database.
	 */
	private void connectDatabase() {
		
		try {
			new EmbeddedDriver();
			this.conn = DriverManager.getConnection(this.databaseURL);
		} catch (final SQLException exc) {
			CoreInternalCapabilityServiceImpl.LOG.error("Can't create connection to database.", exc);
		}
		
	}
	
	/**
	 * Creates CapabilitiesTable if it not already exists.
	 */
	private void createCapabilitiesTable() {
		
		Statement stmt = null;
		
		try {
			CoreInternalCapabilityServiceImpl.LOG.debug("Checking if table \"{}\" already exists...", this.CapabilitiesTable);
			
			final DatabaseMetaData metaData = this.conn.getMetaData();
			final ResultSet rs = metaData.getTables(null, null, this.CapabilitiesTable, null);
			
			if (!rs.next()) {
				CoreInternalCapabilityServiceImpl.LOG.debug("Table \"{}\" did not exist. Creating...", this.CapabilitiesTable);
				stmt = this.conn.createStatement();
				stmt.executeUpdate(this.createCapabilitiesTable);
				stmt.close();
				CoreInternalCapabilityServiceImpl.LOG.debug("Table \"{}\" successfully created.", this.CapabilitiesTable);
			}
			
		} catch (final SQLException exc) {
			CoreInternalCapabilityServiceImpl.LOG.error("Database error - can't create CapabilitiesTable.", exc);
		}
		
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void storeCapabilities(final List<String> capabilities, final String providerName, final ProviderType providerType) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			CoreInternalCapabilityServiceImpl.LOG.debug("Storing \"{}\" capabilities of \"{}\" ...", providerType, providerName);
			
			pstmt = this.conn.prepareStatement(this.storeCapabilities);
			
			for (final String capability : capabilities) {
				
				pstmt.setString(1, capability);
				pstmt.setString(2, providerName);
				pstmt.setString(3, providerType.name());
				pstmt.executeUpdate();
			}
			
			pstmt.close();
			
		} catch (final SQLException e) {
			CoreInternalCapabilityServiceImpl.LOG.error("Database error - can't store \"{}\" capabilities in database.", providerType, e);
		}
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Map<String, List<String>> getCapabilities(final ProviderType providerType) {
		
		PreparedStatement pstmt = null;
		ResultSet queryRes = null;
		
		CoreInternalCapabilityServiceImpl.LOG.debug("Getting \"{}\" capabilities...", providerType);
		
		try {
			
			pstmt = this.conn.prepareStatement(this.getCapabilitiesByType);
			pstmt.setString(1, providerType.name());
			queryRes = pstmt.executeQuery();
			
			final Map<String, List<String>> capsMap = new HashMap<>();
			
			// Fill map with Providers and their capabilities
			while (queryRes.next()) {
				
				if (capsMap.containsKey(queryRes.getString(2))) {
					capsMap.get(queryRes.getString(2)).add(queryRes.getString(1));
				} else {
					capsMap.put(queryRes.getString(2), new ArrayList<String>());
					capsMap.get(queryRes.getString(2)).add(queryRes.getString(1));
				}
			}
			
			queryRes.close();
			pstmt.close();
			
			CoreInternalCapabilityServiceImpl.LOG.debug("Getting \"{}\" capabilities successfully completed.", providerType);
			
			return capsMap;
			
		} catch (final SQLException e) {
			CoreInternalCapabilityServiceImpl.LOG.error("Database error - can't get \"{}\" capabilities from database.", providerType, e);
			return null;
		}
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getCapabilities(final String providerName, final ProviderType providerType) {
		
		PreparedStatement pstmt = null;
		ResultSet queryRes = null;
		
		CoreInternalCapabilityServiceImpl.LOG.debug("Getting \"{}\" capabilities of \"{}\"...", providerType, providerName);
		
		try {
			
			pstmt = this.conn.prepareStatement(this.getCapabilitiesByName);
			pstmt.setString(1, providerName);
			pstmt.setString(2, providerType.name());
			queryRes = pstmt.executeQuery();
			
			final List<String> capabilities = new ArrayList<>();
			
			// Fill List with capabilities
			while (queryRes.next()) {
				capabilities.add(queryRes.getString(1));
			}
			
			queryRes.close();
			pstmt.close();
			
			CoreInternalCapabilityServiceImpl.LOG.debug("Getting \"{}\" capabilities of \"{}\" successfully completed.", providerType, providerName);
			
			return capabilities;
			
		} catch (final SQLException e) {
			CoreInternalCapabilityServiceImpl.LOG.error("Database error - can't get capabilities of \"{}\" from database.", providerName, e);
			return null;
		}
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void deleteCapabilities(final String providerName) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			CoreInternalCapabilityServiceImpl.LOG.debug("Deleting capabilities of \"{}\" ...", providerName);
			
			pstmt = this.conn.prepareStatement(this.deleteCapabilities);
			pstmt.setString(1, providerName);
			pstmt.executeUpdate();
			pstmt.close();
			
		} catch (final SQLException e) {
			CoreInternalCapabilityServiceImpl.LOG.error("Database error - can't delete capabilities of \"{}\" from database.", providerName, e);
		}
	}
}
