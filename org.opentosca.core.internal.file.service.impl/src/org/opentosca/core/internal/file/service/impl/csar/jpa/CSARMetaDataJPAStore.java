package org.opentosca.core.internal.file.service.impl.csar.jpa;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.exceptions.NotFoundException;
import org.opentosca.exceptions.UserException;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages CSAR meta data in the database by using Eclipse Link (JPA).<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class CSARMetaDataJPAStore {
	
	private final static Logger LOG = LoggerFactory.getLogger(CSARMetaDataJPAStore.class);
	
	/**
	 * JDBC-URL to the OpenTOSCA database. It will be created if it does not
	 * exist yet.
	 *
	 * @see org.opentosca.settings.Settings
	 */
	private final String DB_URL = "jdbc:derby:" + Settings.getSetting("databaseLocation") + ";create=true";
	
	/**
	 * JPA EntityManager and Factory. These variables are global, as we do not
	 * want to create a new EntityManager / Factory each time a method is
	 * called.
	 */
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	/**
	 * Initializes JPA.
	 */
	private void initJPA() {
		if (this.em == null) {
			Map<String, String> properties = new HashMap<>();
			properties.put(PersistenceUnitProperties.JDBC_URL, this.DB_URL);
			this.emf = Persistence.createEntityManagerFactory("CSARContent", properties);
			this.em = this.emf.createEntityManager();
		}
	}
	
	/**
	 * Destructor. This method is called when the garbage collector destroys the
	 * class. We will then manually close the EntityManager / Factory and pass
	 * control back.
	 */
	@Override
	protected void finalize() throws Throwable {
		this.em.close();
		this.emf.close();
		super.finalize();
	}
	
	/**
	 * Persists the meta data of CSAR {@code csarID}.
	 *
	 * @param csarID of the CSAR.
	 * @param directories - all directories of the CSAR relative to CSAR root.
	 * @param fileToStorageProviderIDMap - file to storage provider ID mapping
	 *            of all files of the CSAR. Each file path must be given
	 *            relative to the CSAR root.
	 * @param toscaMetaFile - represents the content of the TOSCA meta file of
	 *            the CSAR.
	 */
	public void storeCSARMetaData(CSARID csarID, Set<Path> directories, Map<Path, String> fileToStorageProviderIDMap, TOSCAMetaFile toscaMetaFile) {
		
		CSARMetaDataJPAStore.LOG.debug("Storing meta data of CSAR \"{}\"...", csarID);
		
		CSARContent csar = new CSARContent(csarID, directories, fileToStorageProviderIDMap, toscaMetaFile);
		
		this.initJPA();
		
		this.em.getTransaction().begin();
		this.em.persist(csar);
		this.em.getTransaction().commit();
		
		// clear the JPA 1st level cache
		this.em.clear();
		
		CSARMetaDataJPAStore.LOG.debug("Storing meta data of CSAR \"{}\" completed.", csarID);
		
	}
	
	/**
	 * @param csarID of CSAR
	 * @return {@code true} if meta data of CSAR {@code csarID} were found,
	 *         otherwise {@code false}.
	 */
	public boolean isCSARMetaDataStored(CSARID csarID) {
		
		CSARMetaDataJPAStore.LOG.debug("Checking if meta data of CSAR \"{}\" are stored...", csarID);
		this.initJPA();
		
		CSARContent csar = this.em.find(CSARContent.class, csarID);
		
		if (csar == null) {
			CSARMetaDataJPAStore.LOG.debug("Meta data of CSAR \"{}\" were not found.", csarID);
			return false;
		}
		
		CSARMetaDataJPAStore.LOG.debug("Meta data of CSAR \"{}\" were found.", csarID);
		return true;
		
	}
	
	/**
	 * Retrieves the meta data of CSAR {@code csarID}.
	 *
	 * @param csarID of CSAR.
	 * @return {@link CSARContent} that gives access to all files and
	 *         directories and the TOSCA meta file of the CSAR.
	 * @throws UserException if meta data of CSAR {@code csarID} were not found.
	 */
	public CSARContent getCSARMetaData(CSARID csarID) throws UserException {
		
		this.initJPA();
		
		CSARMetaDataJPAStore.LOG.debug("Retrieving meta data of CSAR \"{}\"...", csarID);
		
		CSARContent csar = this.em.find(CSARContent.class, csarID);
		
		if (csar == null) {
			CSARMetaDataJPAStore.LOG.debug("Meta data of CSAR \"{}\" were not found.", csarID);
			throw new NotFoundException();
		}
		
		CSARMetaDataJPAStore.LOG.debug("Meta data of CSAR \"{}\" were retrieved.", csarID);
		
		return csar;
	}
	
	/**
	 * @return CSAR IDs of all stored CSAR files.
	 */
	public Set<CSARID> getCSARIDsMetaData() {
		
		CSARMetaDataJPAStore.LOG.trace("Retrieving CSAR IDs of all stored CSARs...");
		this.initJPA();
		Query getCSARIDsQuery = this.em.createNamedQuery(CSARContent.getCSARIDs);
		
		@SuppressWarnings("unchecked")
		List<CSARID> csarIDs = getCSARIDsQuery.getResultList();
		CSARMetaDataJPAStore.LOG.trace("{} CSAR ID(s) was / were found.", csarIDs.size());
		return new HashSet<>(csarIDs);
		
	}
	
	/**
	 * Deletes the meta data of CSAR {@code csarID}.
	 *
	 * @param csarID of CSAR.
	 * @throws UserException if meta data of CSAR {@code csarID} were not found.
	 */
	public void deleteCSARMetaData(CSARID csarID) throws UserException {
		
		this.initJPA();
		
		CSARMetaDataJPAStore.LOG.debug("Deleting meta data of CSAR \"{}\"...", csarID);
		
		CSARContent csarContent = this.getCSARMetaData(csarID);
		
		this.em.getTransaction().begin();
		this.em.remove(csarContent);
		this.em.getTransaction().commit();
		
		CSARMetaDataJPAStore.LOG.debug("Deleting meta data of CSAR \"{}\" completed.", csarID);
		
	}
	
	/**
	 * Persists / updates the storage provider ID of file
	 * {@code fileRelToCSARRoot} in CSAR {@code csarID} to
	 * {@code storageProviderID}.
	 *
	 * @param csarID of CSAR.
	 * @param fileRelToCSARRoot - file relative to CSAR root.
	 * @param storageProviderID of storage provider to set for file
	 *            {@code fileRelToCSARRoot}.
	 * @throws UserException if meta data of file {@code fileRelToCSARRoot} in
	 *             CSAR {@code CSARID} were not found.
	 */
	public void storeFileStorageProviderIDOfCSAR(CSARID csarID, Path fileRelToCSARRoot, String storageProviderID) throws UserException {
		
		CSARMetaDataJPAStore.LOG.debug("Setting storage provider \"{}\" in meta data of file \"{}\" in CSAR \"{}\"...", storageProviderID, fileRelToCSARRoot, csarID);
		
		this.initJPA();
		
		Query storeStorageProviderIDByFileAndCSARIDQuery = this.em.createNamedQuery(CSARContent.storeStorageProviderIDByFileAndCSARID);
		
		storeStorageProviderIDByFileAndCSARIDQuery.setParameter(1, storageProviderID);
		storeStorageProviderIDByFileAndCSARIDQuery.setParameter(2, fileRelToCSARRoot.toString());
		storeStorageProviderIDByFileAndCSARIDQuery.setParameter(3, csarID.toString());
		
		this.em.getTransaction().begin();
		int updatedFiles = storeStorageProviderIDByFileAndCSARIDQuery.executeUpdate();
		this.em.getTransaction().commit();
		
		if (updatedFiles > 0) {
			
			// After the execution of the native query we must manually
			// synchronize the persistence context with the database context.
			// For this will clear the 1st level cache and invalidate the
			// CSARContent entity in the 2nd level cache.
			this.em.clear();
			this.emf.getCache().evict(CSARContent.class, csarID);
			
			CSARMetaDataJPAStore.LOG.debug("Setting storage provider \"{}\" in meta data of file \"{}\" in CSAR \"{}\" completed.", storageProviderID, fileRelToCSARRoot, csarID);
			
		} else {
			
			throw new UserException("Meta data of file \"" + fileRelToCSARRoot + "\" of CSAR \"" + csarID + "\" were not found.");
			
		}
		
	}
	
	/**
	 * @param csarID of CSAR.
	 * @return Each file of CSAR {@code csarID} relative to CSAR root mapped to
	 *         the ID of the storage provider the file is stored on.
	 * @throws UserException if file to storage provider ID mapping meta data of
	 *             CSAR {@code csarID} were not found.
	 */
	public Map<Path, String> getFileToStorageProviderIDMap(CSARID csarID) throws UserException {
		CSARMetaDataJPAStore.LOG.debug("Retrieving file to storage provider mapping meta data of CSAR \"{}\"...", csarID);
		this.initJPA();
		Query getFileToStorageProviderIDMapQuery = this.em.createNamedQuery(CSARContent.getFileToStorageProviderIDMapByCSARID);
		getFileToStorageProviderIDMapQuery.setParameter("csarID", csarID);
		
		@SuppressWarnings("unchecked")
		List<Object[]> fileToStorageProviderIDEntries = getFileToStorageProviderIDMapQuery.getResultList();
		
		if (fileToStorageProviderIDEntries.isEmpty()) {
			throw new UserException("Meta data of CSAR \"" + csarID + "\" were not found.");
		}
		
		Map<Path, String> fileToStorageProviderIDMap = new HashMap<>();
		
		for (Object[] fileToStorageProviderIDEntry : fileToStorageProviderIDEntries) {
			Path file = (Path) fileToStorageProviderIDEntry[0];
			String storageProviderID = (String) fileToStorageProviderIDEntry[1];
			fileToStorageProviderIDMap.put(file, storageProviderID);
		}
		
		CSARMetaDataJPAStore.LOG.debug("Retrieving file to storage provider mapping meta data of CSAR \"{}\" completed.", csarID);
		
		return fileToStorageProviderIDMap;
		
	}
	
	/**
	 * @param csarID of CSAR.
	 * @return Directories meta data of CSAR {@code csarID}.
	 * @throws UserException if directories meta data of CSAR {@code csarID}
	 *             were not found.
	 */
	public Set<Path> getDirectories(CSARID csarID) throws UserException {
		
		CSARMetaDataJPAStore.LOG.debug("Retrieving directories meta data of CSAR \"{}\"...", csarID);
		
		this.initJPA();
		
		Query getDirectoriesQuery = this.em.createNamedQuery(CSARContent.getDirectoriesByCSARID);
		getDirectoriesQuery.setParameter("csarID", csarID);
		
		// Query should return a list of Path objects, but it returns a list
		// of String objects instead (Eclipse Link bug). As a workaround we
		// manually convert all String objects to Path objects.
		
		@SuppressWarnings("unchecked")
		List<String> directoriesAsString = getDirectoriesQuery.getResultList();
		
		if (directoriesAsString.isEmpty()) {
			throw new UserException("Meta data of CSAR \"" + csarID + "\" were not found.");
		}
		
		Set<Path> directories = new HashSet<>();
		
		for (String directoryAsString : directoriesAsString) {
			directories.add(Paths.get(directoryAsString));
		}
		
		CSARMetaDataJPAStore.LOG.debug("Retrieving directories meta data of CSAR \"{}\" completed.", csarID);
		return directories;
	}
}
