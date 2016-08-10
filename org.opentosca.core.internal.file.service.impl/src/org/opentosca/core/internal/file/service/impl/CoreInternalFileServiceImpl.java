package org.opentosca.core.internal.file.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.internal.file.service.ICoreInternalFileService;
import org.opentosca.core.internal.file.service.impl.csar.CSARUnpacker;
import org.opentosca.core.internal.file.service.impl.csar.CSARValidator;
import org.opentosca.core.internal.file.service.impl.csar.jpa.CSARMetaDataJPAStore;
import org.opentosca.core.internal.file.service.impl.visitors.DirectoryDeleteVisitor;
import org.opentosca.core.internal.file.service.impl.visitors.DirectoryVisitor;
import org.opentosca.core.internal.file.storage.providers.service.ICoreInternalFileStorageProviderService;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.core.model.csar.toscametafile.TOSCAMetaFileParser;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.settings.Settings;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a store and management functionalities for CSAR files.<br />
 * <br />
 * Files and directories in a CSAR can be stored with any of the available
 * storage providers (plug-ins). For using a certain storage provider it must be
 * defined as the active storage provider with
 * {@link #setActiveStorageProvider(String)}. Only available storage providers
 * can be set as active. By default (e.g. after the start of the container), no
 * active storage provider is set. In this case or if the active storage
 * provider is not ready (e.g. has no credentials) the default storage provider
 * will be used instead, if it's available and ready. Otherwise the operation
 * fails. The default one is hard-coded and can be get with
 * {@link #getDefaultStorageProvider()}. <br />
 * A stored CSAR file or only a file / directory of it can be moved to another
 * storage provider. The target storage provider for the move is analogous the
 * active storage provider respectively the default one. <br />
 * If an active storage provider goes unavailable no active storage provider is
 * set (setting will be cleared).<br />
 * Meta data (file and directory paths, TOSCA meta file content and CSAR ID) of
 * a CSAR will be stored locally in the database. This makes it possible to
 * browse in a CSAR and get the TOSCA meta file data without network access. <br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @see ICoreInternalFileStorageProviderService
 * @see CSARContent
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CoreInternalFileServiceImpl implements ICoreInternalFileService {
	
	private final static Logger LOG = LoggerFactory.getLogger(CoreInternalFileServiceImpl.class);
	
	private final StorageProviderManager STORAGE_PROVIDER_MANAGER = new StorageProviderManager();
	private final CSARMetaDataJPAStore JPA_STORE = new CSARMetaDataJPAStore();
	
	private static IFileAccessService fileAccessService = null;
	
	/**
	 * Relative path to CSAR root of the TOSCA meta file.
	 * 
	 * @see org.opentosca.settings.Settings
	 */
	private final String TOSCA_META_FILE_REL_PATH = Settings.getSetting("toscaMetaFileRelPath");
	
	
	// /**
	// * File extension of a CSAR.
	// *
	// * @see org.opentosca.settings.Settings
	// */
	// private final String CSAR_EXTENSION =
	// Settings.getSetting("csarExtension");
	
	@Override
	public CSARID storeCSAR(Path csarFile) throws UserException, SystemException {
		
		CoreInternalFileServiceImpl.LOG.debug("Given file to store: {}", csarFile);
		
		CSARUnpacker csarUnpacker = null;
		Path csarUnpackDir = null;
		DirectoryVisitor csarVisitor = null;
		
		try {
			
			if (!Files.isRegularFile(csarFile)) {
				throw new UserException("\"" + csarFile.toString() + "\" to store is not an absolute path to an existent file.");
			}
			
			// if (!PathUtils.hasFileExtension(csarFile, this.CSAR_EXTENSION)) {
			// throw new UserException("File \"" + csarFile.toString() +
			// "\" to store has not correct file extension \"" +
			// this.CSAR_EXTENSION + "\".");
			// }
			
			CSARID csarID = new CSARID(csarFile.getFileName().toString());
			
			if (this.JPA_STORE.isCSARMetaDataStored(csarID)) {
				throw new UserException("CSAR \"" + csarID.toString() + "\" is already stored. Overwriting a CSAR is not allowed.");
			}
			
			String storageProviderID = this.STORAGE_PROVIDER_MANAGER.chooseStorageProvider();
			
			csarUnpacker = new CSARUnpacker(csarFile);
			csarUnpacker.unpackAndVisitUnpackDir();
			
			csarUnpackDir = csarUnpacker.getUnpackDirectory();
			csarVisitor = csarUnpacker.getFilesAndDirectories();
			
			CSARValidator csarValidator = new CSARValidator(csarID, csarUnpackDir, csarVisitor);
			
			if (!csarValidator.isValid()) {
				throw new UserException(csarValidator.getErrorMessage());
			}
			
			Path toscaMetaFileAbsPath = csarUnpackDir.resolve(this.TOSCA_META_FILE_REL_PATH);
			
			TOSCAMetaFile toscaMetaFile = new TOSCAMetaFileParser().parse(toscaMetaFileAbsPath);
			
			if (toscaMetaFile == null) {
				throw new UserException("TOSCA meta file is invalid.");
			}
			
			Set<Path> filesInCSARUnpackDir = csarVisitor.getVisitedFiles();
			Set<Path> directoriesInCSARUnpackDir = csarVisitor.getVisitedDirectories();
			
			Map<Path, String> fileToStorageProviderIDMap = new HashMap<Path, String>();
			Set<Path> directories = new HashSet<Path>();
			
			for (Path fileInCSARUnpackDir : filesInCSARUnpackDir) {
				Path fileRelToCSARRoot = csarUnpackDir.relativize(fileInCSARUnpackDir);
				this.STORAGE_PROVIDER_MANAGER.storeFileOfCSAR(fileInCSARUnpackDir, csarID, fileRelToCSARRoot, storageProviderID);
				fileToStorageProviderIDMap.put(fileRelToCSARRoot, storageProviderID);
			}
			
			for (Path directoryInCSARUnpackDir : directoriesInCSARUnpackDir) {
				Path directoryRelToCSARRoot = csarUnpackDir.relativize(directoryInCSARUnpackDir);
				directories.add(directoryRelToCSARRoot);
			}
			
			this.JPA_STORE.storeCSARMetaData(csarID, directories, fileToStorageProviderIDMap, toscaMetaFile);
			
			CoreInternalFileServiceImpl.LOG.debug("Storing CSAR \"{}\" located at \"{}\" successfully completed.", csarID, csarFile);
			
			return csarID;
		} finally {
			// At the end or if an exception occurred we should delete the
			// unpack directory, if necessary.
			if (csarUnpackDir != null) {
				csarUnpacker.deleteUnpackDir();
			}
		}
	}
	
	@Override
	public CSARContent getCSAR(CSARID csarID) throws UserException {
		
		CSARContent csarContent = this.JPA_STORE.getCSARMetaData(csarID);
		return csarContent;
		
	}
	
	@Override
	public Set<CSARID> getCSARIDs() {
		
		Set<CSARID> csarIDs = this.JPA_STORE.getCSARIDsMetaData();
		return csarIDs;
		
	}
	
	@Override
	public Path exportCSAR(CSARID csarID) throws UserException, SystemException {
		
		CoreInternalFileServiceImpl.LOG.debug("Exporting CSAR \"{}\"...", csarID);
		
		Map<Path, String> fileToStorageProviderIDMapOfCSAR;
		
		fileToStorageProviderIDMapOfCSAR = this.JPA_STORE.getFileToStorageProviderIDMap(csarID);
		
		// create new Set to remove duplicates
		Set<String> storageProviderIDsOfCSAR = new HashSet<String>(fileToStorageProviderIDMapOfCSAR.values());
		
		Set<String> idsOfNotReadyStorageProviders = new HashSet<String>();
		
		idsOfNotReadyStorageProviders = this.STORAGE_PROVIDER_MANAGER.areStorageProvidersReady(storageProviderIDsOfCSAR);
		
		if (!idsOfNotReadyStorageProviders.isEmpty()) {
			throw new SystemException("Can't export CSAR \"" + csarID + "\", because the following storage provider(s) is / are not available and ready: " + idsOfNotReadyStorageProviders);
		}
		
		Set<Path> directoriesOfCSAR = this.JPA_STORE.getDirectories(csarID);
		
		Path tempDirectory = CoreInternalFileServiceImpl.fileAccessService.getTemp().toPath();
		Path csarDownloadDirectory = tempDirectory.resolve("content");
		
		try {
			
			Files.createDirectory(csarDownloadDirectory);
			
			for (Path directoryOfCSAR : directoriesOfCSAR) {
				Path directoryOfCSARAbsPath = csarDownloadDirectory.resolve(directoryOfCSAR);
				Files.createDirectories(directoryOfCSARAbsPath);
			}
			
			for (Map.Entry<Path, String> fileToStorageProviderIDOfCSAREntry : fileToStorageProviderIDMapOfCSAR.entrySet()) {
				
				Path fileRelToCSARRoot = fileToStorageProviderIDOfCSAREntry.getKey();
				String fileStorageProviderID = fileToStorageProviderIDOfCSAREntry.getValue();
				
				Path fileOfCSARDownloadAbsPath = csarDownloadDirectory.resolve(fileRelToCSARRoot);
				
				this.STORAGE_PROVIDER_MANAGER.getFileOfCSAR(csarID, fileRelToCSARRoot, fileStorageProviderID, fileOfCSARDownloadAbsPath);
				
			}
			
			Path csarFile = tempDirectory.resolve(csarID.getFileName());
			
			CoreInternalFileServiceImpl.fileAccessService.zip(csarDownloadDirectory.toFile(), csarFile.toFile());
			
			CoreInternalFileServiceImpl.LOG.debug("CSAR \"{}\" was successfully exported to \"{}\".", csarID, csarFile);
			
			return csarFile;
			
		} catch (IOException exc) {
			throw new SystemException("An IO Exception occured.", exc);
		} finally {
			DirectoryDeleteVisitor csarDeleteVisitor = new DirectoryDeleteVisitor();
			try {
				CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR download directory \"{}\"...", csarDownloadDirectory);
				Files.walkFileTree(csarDownloadDirectory, csarDeleteVisitor);
				CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR download directory \"{}\" completed.", csarDownloadDirectory);
			} catch (IOException exc) {
				throw new SystemException("An IO Exception occured. Deleting CSAR download directory \"" + csarDownloadDirectory + "\" failed.", exc);
			}
		}
		
	}
	
	@Override
	public void moveCSAR(CSARID csarID) throws UserException, SystemException {
		
		CoreInternalFileServiceImpl.LOG.debug("CSAR to move: \"{}\"", csarID);
		
		String targetStorageProviderID = this.STORAGE_PROVIDER_MANAGER.chooseStorageProvider();
		
		CoreInternalFileServiceImpl.LOG.debug("CSAR \"{}\" will be moved to storage provider \"{}\".", csarID, targetStorageProviderID);
		
		Map<Path, String> fileToStorageProviderIDMap = this.JPA_STORE.getFileToStorageProviderIDMap(csarID);
		
		Map<Path, String> fileToMoveToStorageProviderIDMap = this.findFilesToMove(null, fileToStorageProviderIDMap, targetStorageProviderID);
		
		if (fileToMoveToStorageProviderIDMap.isEmpty()) {
			CoreInternalFileServiceImpl.LOG.debug("CSAR \"{}\" is already completely stored on storage provider \"{}\".", csarID, targetStorageProviderID);
		} else {
			
			for (Map.Entry<Path, String> fileToMoveToStorageProviderIDEntry : fileToMoveToStorageProviderIDMap.entrySet()) {
				
				Path fileRelToCSARRoot = fileToMoveToStorageProviderIDEntry.getKey();
				String fileStorageProviderID = fileToMoveToStorageProviderIDEntry.getValue();
				
				this.moveFileToStorageProvider(csarID, fileRelToCSARRoot, fileStorageProviderID, targetStorageProviderID);
				
			}
			
		}
		
		CoreInternalFileServiceImpl.LOG.debug("Moving CSAR \"{}\" to storage provider \"{}\" completed.", csarID, targetStorageProviderID);
		
	}
	
	/**
	 * Moves the file {@code fileRelToCSARRoot} of CSAR {@code csarID} from its
	 * current storage provider {@code fileStorageProviderID} to the storage
	 * provider {@code targetStorageProviderID}.<br />
	 * <br />
	 * A file will be moved in the following way:<br />
	 * 1. Getting input stream and size of file from the source storage
	 * provider.<br />
	 * 2. Storing file on target storage provider
	 * {@code targetStorageProviderID} using the input stream and size.<br />
	 * 3. Updating meta data of file.<br />
	 * 4. Deleting file on source storage provider.
	 * 
	 * @param csarID of CSAR
	 * @param fileRelToCSARRoot - file relative to CSAR root.
	 * @param fileStorageProviderID of storage provider
	 * @param targetStorageProviderID of storage provider.
	 * @throws UserException if CSAR {@code csarID} or it's file
	 *             {@code fileRelToCSARRoot} was not found.
	 * 
	 * @throws SystemException if source or target storage provider is not
	 *             available and ready, the file to move was not found on source
	 *             storage provider or an error occurred during getting from
	 *             source storage provider, storing on target storage provider
	 *             or deleting from source storage provider.
	 * @return {@code true} if moving file was successful, otherwise
	 *         {@code false}.
	 */
	private void moveFileToStorageProvider(CSARID csarID, Path fileRelToCSARRoot, String fileStorageProviderID, String targetStorageProviderID) throws UserException, SystemException {
		
		CoreInternalFileServiceImpl.LOG.debug("Moving file \"{}\" of CSAR \"{}\" from source storage provider \"{}\" to target storage provider \"{}\"...", fileRelToCSARRoot, csarID, fileStorageProviderID, targetStorageProviderID);
		
		InputStream fileInputStream = this.STORAGE_PROVIDER_MANAGER.getFileOfCSARAsInputStream(csarID, fileRelToCSARRoot, fileStorageProviderID);
		
		long fileSize = this.STORAGE_PROVIDER_MANAGER.getFileOfCSARSize(csarID, fileRelToCSARRoot, fileStorageProviderID);
		
		this.STORAGE_PROVIDER_MANAGER.storeFileOfCSAR(csarID, fileInputStream, fileSize, fileRelToCSARRoot, targetStorageProviderID);
		
		try {
			fileInputStream.close();
		} catch (IOException exc) {
			throw new SystemException("An IOException occured.", exc);
		}
		
		this.JPA_STORE.storeFileStorageProviderIDOfCSAR(csarID, fileRelToCSARRoot, targetStorageProviderID);
		
		this.STORAGE_PROVIDER_MANAGER.deleteFileOfCSAR(csarID, fileRelToCSARRoot, fileStorageProviderID);
		
		CoreInternalFileServiceImpl.LOG.debug("Moving file \"{}\" of CSAR \"{}\" from storage provider \"{}\" to target storage provider \"{}\" completed.", fileRelToCSARRoot, csarID, fileStorageProviderID, targetStorageProviderID);
		
	}
	
	@Override
	public void moveFileOrDirectoryOfCSAR(CSARID csarID, Path relPathToCSARRoot) throws SystemException, UserException {
		
		CoreInternalFileServiceImpl.LOG.debug("CSAR: \"{}\", file / directory of CSAR to move: \"{}\"", csarID, relPathToCSARRoot);
		
		String targetStorageProviderID = this.STORAGE_PROVIDER_MANAGER.chooseStorageProvider();
		
		CoreInternalFileServiceImpl.LOG.debug("File / directory \"{}\" of CSAR \"{}\" will be moved to storage provider \"{}\".", relPathToCSARRoot, csarID, targetStorageProviderID);
		
		Map<Path, String> fileToStorageProviderIDMap = this.JPA_STORE.getFileToStorageProviderIDMap(csarID);
		
		Set<Path> directories = this.JPA_STORE.getDirectories(csarID);
		
		if (fileToStorageProviderIDMap.containsKey(relPathToCSARRoot)) {
			
			CoreInternalFileServiceImpl.LOG.debug("\"{}\" to move is a file of CSAR \"{}\".", relPathToCSARRoot, csarID);
			String fileStorageProviderID = fileToStorageProviderIDMap.get(relPathToCSARRoot);
			
			if (fileStorageProviderID.equals(targetStorageProviderID)) {
				
				CoreInternalFileServiceImpl.LOG.debug("File \"{}\" is already stored on target storage provider \"{}\".", relPathToCSARRoot, targetStorageProviderID);
				
			} else {
				
				this.moveFileToStorageProvider(csarID, relPathToCSARRoot, fileStorageProviderID, targetStorageProviderID);
				
			}
			
		} else if (directories.contains(relPathToCSARRoot)) {
			
			CoreInternalFileServiceImpl.LOG.debug("\"{}\" to move is a directory of CSAR \"{}\".", relPathToCSARRoot, csarID);
			Map<Path, String> fileToMoveToStorageProviderIDMap = this.findFilesToMove(relPathToCSARRoot, fileToStorageProviderIDMap, targetStorageProviderID);
			
			if (fileToMoveToStorageProviderIDMap.isEmpty()) {
				
				CoreInternalFileServiceImpl.LOG.debug("Files in directory \"{}\" of CSAR \"{}\" are already stored on target storage provider \"{}\" or directory \"{}\" contains no files.", relPathToCSARRoot, csarID, targetStorageProviderID, relPathToCSARRoot);
				
			} else {
				
				for (Map.Entry<Path, String> fileToMoveToStorageProviderIDEntry : fileToMoveToStorageProviderIDMap.entrySet()) {
					Path fileRelToCSARRoot = fileToMoveToStorageProviderIDEntry.getKey();
					String fileStorageProviderID = fileToMoveToStorageProviderIDEntry.getValue();
					this.moveFileToStorageProvider(csarID, fileRelToCSARRoot, fileStorageProviderID, targetStorageProviderID);
				}
				
			}
			
			CoreInternalFileServiceImpl.LOG.debug("Moving directory \"{}\" of CSAR \"{}\" to target storage provider \"{}\" completed.", relPathToCSARRoot, csarID, targetStorageProviderID);
			
		} else {
			throw new UserException("File or directory \"" + relPathToCSARRoot + "\" to move was not found in CSAR \"" + csarID + "\".");
		}
		
	}
	
	/**
	 * Finds all files that must be moved to the storage provider
	 * {@code targetStorageProviderID}. If a file is already on
	 * {@code targetStorageProviderID}, it must be not moved.
	 * 
	 * @param searchDirRelToCSARRoot - directory relative to CSAR root where to
	 *            search for files that must be moved. {@code null} means it
	 *            should be searched in the complete CSAR.
	 * @param fileToStorageProviderIDMap - file to storage provider ID mapping
	 *            of all files of the CSAR. Each file path must be given
	 *            relative to the CSAR root.
	 * @param targetStorageProviderID of storage provider.
	 * @return Mapping of relative path to CSAR root of file to its storage
	 *         provider ID of all files that must be moved.
	 */
	private Map<Path, String> findFilesToMove(Path searchDirRelToCSARRoot, Map<Path, String> fileToStorageProviderIDMap, String targetStorageProviderID) {
		
		CoreInternalFileServiceImpl.LOG.debug("Searching for files that must be moved to target storage provider \"{}\"...", targetStorageProviderID);
		
		Map<Path, String> fileToMoveToStorageProviderIDMap = new HashMap<Path, String>();
		
		for (Map.Entry<Path, String> fileToStorageProviderIDEntry : fileToStorageProviderIDMap.entrySet()) {
			Path file = fileToStorageProviderIDEntry.getKey();
			if ((searchDirRelToCSARRoot == null) || file.startsWith(searchDirRelToCSARRoot)) {
				String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
				boolean isAlreadyOnTargetStorageProvider = fileStorageProviderID.equals(targetStorageProviderID);
				if (!isAlreadyOnTargetStorageProvider) {
					// found file that must be moved to target storage provider
					fileToMoveToStorageProviderIDMap.put(file, fileStorageProviderID);
				}
			}
		}
		
		CoreInternalFileServiceImpl.LOG.debug("Found {} file(s) that must be moved to target storage provider \"{}\".", fileToMoveToStorageProviderIDMap.size(), targetStorageProviderID);
		
		return fileToMoveToStorageProviderIDMap;
		
	}
	
	@Override
	public void deleteCSAR(CSARID csarID) throws SystemException, UserException {
		
		CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR \"{}\"...", csarID);
		
		Map<Path, String> fileToStorageProviderIDMap = this.JPA_STORE.getFileToStorageProviderIDMap(csarID);
		
		// create new Set to remove duplicates
		Set<String> storageProviderIDsOfCSAR = new HashSet<String>(fileToStorageProviderIDMap.values());
		
		Set<String> idsOfNotReadyStorageProviders = new HashSet<String>();
		
		idsOfNotReadyStorageProviders = this.STORAGE_PROVIDER_MANAGER.areStorageProvidersReady(storageProviderIDsOfCSAR);
		
		if (!idsOfNotReadyStorageProviders.isEmpty()) {
			throw new SystemException("Can't delete CSAR \"" + csarID + "\", because the following storage provider(s) is / are not available and ready: " + idsOfNotReadyStorageProviders);
		}
		
		CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR \"{}\" on storage provider(s) {}...", csarID, storageProviderIDsOfCSAR);
		
		for (Map.Entry<Path, String> fileToStorageProviderIDEntry : fileToStorageProviderIDMap.entrySet()) {
			
			Path fileRelToCSARRoot = fileToStorageProviderIDEntry.getKey();
			String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
			
			this.STORAGE_PROVIDER_MANAGER.deleteFileOfCSAR(csarID, fileRelToCSARRoot, fileStorageProviderID);
			
		}
		
		CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR \"{}\" on storage provider(s) completed.", csarID);
		
		this.JPA_STORE.deleteCSARMetaData(csarID);
		
		CoreInternalFileServiceImpl.LOG.debug("Deleting CSAR \"{}\" completed.", csarID);
		
	}
	
	@Override
	public void deleteCSARs() throws SystemException {
		
		CoreInternalFileServiceImpl.LOG.debug("Deleting all CSARs...");
		
		Set<CSARID> csarIDs = this.JPA_STORE.getCSARIDsMetaData();
		
		if (!csarIDs.isEmpty()) {
			
			CoreInternalFileServiceImpl.LOG.debug("{} CSAR(s) is / are currently stored and will be deleted now.", csarIDs.size());
			
			for (CSARID csarID : csarIDs) {
				try {
					this.deleteCSAR(csarID);
				} catch (UserException exc) {
					throw new SystemException("An System Exception occured.", exc);
				}
			}
			
			CoreInternalFileServiceImpl.LOG.debug("Deleting all CSARs completed.");
			
		} else {
			CoreInternalFileServiceImpl.LOG.debug("No CSARs are currently stored.");
		}
		
	}
	
	@Override
	public Set<String> getReadyStorageProviders() {
		return this.STORAGE_PROVIDER_MANAGER.getReadyStorageProviders();
	}
	
	@Override
	public boolean isReadyStorageProvider(String storageProviderID) {
		return this.STORAGE_PROVIDER_MANAGER.isStorageProviderReady(storageProviderID);
	}
	
	@Override
	public Set<String> getStorageProviders() {
		return this.STORAGE_PROVIDER_MANAGER.getStorageProviders();
	}
	
	@Override
	public String getActiveStorageProvider() {
		return this.STORAGE_PROVIDER_MANAGER.getActiveStorageProvider();
	}
	
	@Override
	public void setActiveStorageProvider(String storageProviderID) throws UserException {
		this.STORAGE_PROVIDER_MANAGER.setActiveStorageProvider(storageProviderID);
	}
	
	@Override
	public String getDefaultStorageProvider() {
		return this.STORAGE_PROVIDER_MANAGER.getDefaultStorageProvider();
	}
	
	@Override
	public String getStorageProviderName(String storageProviderID) {
		return this.STORAGE_PROVIDER_MANAGER.getStorageProviderName(storageProviderID);
	}
	
	/**
	 * Binds the File Access Service.
	 * 
	 * @param fileAccessService to bind
	 */
	protected void bindFileAccessService(IFileAccessService fileAccessService) {
		if (fileAccessService == null) {
			CoreInternalFileServiceImpl.LOG.warn("Can't bind File Access Service.");
		} else {
			CoreInternalFileServiceImpl.fileAccessService = fileAccessService;
			CoreInternalFileServiceImpl.LOG.debug("File Access Service bound.");
		}
	}
	
	/**
	 * Unbinds the File Access Service.
	 * 
	 * @param fileAccessService to unbind
	 */
	protected void unbindFileAccessService(IFileAccessService fileAccessService) {
		CoreInternalFileServiceImpl.fileAccessService = null;
		CoreInternalFileServiceImpl.LOG.debug("File Access Service unbound.");
	}
	
}
