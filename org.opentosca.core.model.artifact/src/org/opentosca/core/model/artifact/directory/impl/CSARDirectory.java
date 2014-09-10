package org.opentosca.core.model.artifact.directory.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.model.artifact.directory.AbstractDirectory;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.artifact.file.impl.CSARFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a directory in a CSAR.<br />
 * Provides methods for browsing and getting the meta data of the directory by
 * using the local stored meta data of the CSAR.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CSARDirectory extends AbstractDirectory {
	
	private final static Logger LOG = LoggerFactory.getLogger(CSARDirectory.class);
	
	/**
	 * CSAR ID of CSAR that contains this directory.
	 */
	private final CSARID CSAR_ID;
	
	/**
	 * Files and directories in this directory.
	 */
	private final Set<Path> DIRECTORIES;
	private final Map<Path, String> FILE_TO_STORAGE_PROVIDER_ID_MAP;
	
	
	/**
	 * Creates a {@link CSARDirectory} without any include / exclude patterns
	 * which have to be considered. Besides, the directory not represents a file
	 * artifact (reference of a file artifact points to a file).
	 * 
	 * @param path - relative path to CSAR root of this directory. An empty
	 *            string means the root.
	 * @param csarID of CSAR that contains this directory.
	 * @param directories - directories in this directory (recursively). Each
	 *            directory must be given relative to the CSAR root.
	 * @param fileToStorageProviderIDMap - file to storage provider ID mapping
	 *            of all files in this directory (recursively). Each file path
	 *            must be given relative to the CSAR root.
	 */
	public CSARDirectory(String directoryPath, CSARID csarID, Set<Path> directories, Map<Path, String> fileToStorageProviderIDMap) {
		// no patterns, so we pass empty sets (immutable to avoid unnecessary
		// object creations)
		this(directoryPath, Collections.<String> emptySet(), Collections.<String> emptySet(), csarID, directories, fileToStorageProviderIDMap, false);
	}
	
	/**
	 * Creates a {@link CSARDirectory}.
	 * 
	 * @param path - relative path to CSAR root of this directory. An empty
	 *            string means the root.
	 * @param includePatterns to include only certain files in this directory.
	 * @param excludePatterns to exclude certain files from this directory.
	 * @param csarID of CSAR that contains this directory.
	 * @param directories - directories in this directory (recursively). Each
	 *            directory must be given relative to the CSAR root.
	 * @param fileArtifact - {@code true} if this directory represents a file
	 *            artifact (directory contains only the file at the artifact
	 *            reference), otherwise {@code false}.
	 * @param fileToStorageProviderIDMap - file to storage provider ID mapping
	 *            of files in this directory (recursively). Each file must be
	 *            given relative to the CSAR root.
	 */
	public CSARDirectory(String directoryPath, Set<String> includePatterns, Set<String> excludePatterns, CSARID csarID, Set<Path> directories, Map<Path, String> fileToStorageProviderIDMap, boolean fileArtifact) {
		super(directoryPath, includePatterns, excludePatterns, fileArtifact);
		this.FILE_TO_STORAGE_PROVIDER_ID_MAP = fileToStorageProviderIDMap;
		this.CSAR_ID = csarID;
		this.DIRECTORIES = directories;
		
	}
	
	@Override
	protected AbstractFile getFileNotConsiderPatterns(String relPathOfFile) {
		
		Path relPathOfFileToCSARRoot = Paths.get(this.getPath()).resolve(relPathOfFile);
		
		if (!this.isFileArtifact()) {
			
			for (Map.Entry<Path, String> fileToStorageProviderIDEntry : this.FILE_TO_STORAGE_PROVIDER_ID_MAP.entrySet()) {
				
				Path file = fileToStorageProviderIDEntry.getKey();
				String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
				
				// found file to get
				if (file.equals(relPathOfFileToCSARRoot)) {
					return new CSARFile(file.toString(), this.CSAR_ID, fileStorageProviderID);
				}
				
			}
			
		} else {
			
			// If it's a file artifact we know directly that the one file in the
			// Map is the file of the artifact.
			for (Map.Entry<Path, String> fileToStorageProviderIDEntry : this.FILE_TO_STORAGE_PROVIDER_ID_MAP.entrySet()) {
				
				Path file = fileToStorageProviderIDEntry.getKey();
				String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
				
				return new CSARFile(file.toString(), this.CSAR_ID, fileStorageProviderID);
				
			}
			
		}
		
		return null;
		
	}
	
	@Override
	protected Set<AbstractFile> getFilesNotConsiderPatterns() {
		
		Set<AbstractFile> csarFiles = new HashSet<AbstractFile>();
		
		if (!this.isFileArtifact()) {
			
			Path directoryReferenceAsPath = Paths.get(this.getPath());
			
			for (Map.Entry<Path, String> fileToStorageProviderIDEntry : this.FILE_TO_STORAGE_PROVIDER_ID_MAP.entrySet()) {
				
				Path file = fileToStorageProviderIDEntry.getKey();
				Path fileParent = file.getParent();
				String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
				
				// the second condition only applies if the file is in
				// the CSAR root, because a file in root has no parent
				if (directoryReferenceAsPath.equals(fileParent) || (fileParent == null)) {
					csarFiles.add(new CSARFile(file.toString(), this.CSAR_ID, fileStorageProviderID));
				}
				
			}
			
		} else {
			csarFiles.add(this.getFileNotConsiderPatterns(""));
		}
		
		return csarFiles;
		
	}
	
	@Override
	public AbstractDirectory getDirectory(String relPathOfDirectory) {
		
		// If it's a file artifact we have no directories and can directly
		// return null
		if (!this.isFileArtifact()) {
			
			Path relPathOfDirectoryToCSARRoot = Paths.get(this.getPath()).resolve(relPathOfDirectory);
			
			AbstractDirectory directory = this.getDirectory(relPathOfDirectoryToCSARRoot);
			
			if (directory != null) {
				CSARDirectory.LOG.debug("Directory \"{}\" relative to \"{}\" was found.", relPathOfDirectory, this.getPath());
			} else {
				CSARDirectory.LOG.warn("Directory \"{}\" relative to \"{}\" was not found.", relPathOfDirectory, this.getPath());
			}
			
			return directory;
			
		}
		
		return null;
		
	}
	
	/**
	 * @param relPathOfDirectoryToCSARRoot - directory path relative to CSAR
	 *            root.
	 * @return {@link AbstractDirectory} of directory
	 *         {@code relPathOfDirectoryToCSARRoot}. If it not exists
	 *         {@code null}.
	 */
	private AbstractDirectory getDirectory(Path relPathOfDirectoryToCSARRoot) {
		
		// directories in directory to get
		Set<Path> directoriesInDirectory = new HashSet<Path>();
		
		for (Path directory : this.DIRECTORIES) {
			if (directory.startsWith(relPathOfDirectoryToCSARRoot)) {
				directoriesInDirectory.add(directory);
			}
		}
		
		// directory to get exists
		if (!directoriesInDirectory.isEmpty()) {
			
			// files in directory to get
			Map<Path, String> fileToStorageProviderIDMapOfDirectory = new HashMap<Path, String>();
			
			for (Map.Entry<Path, String> fileToStorageProviderID : this.FILE_TO_STORAGE_PROVIDER_ID_MAP.entrySet()) {
				
				Path file = fileToStorageProviderID.getKey();
				String fileStorageProviderID = fileToStorageProviderID.getValue();
				
				if (file.startsWith(relPathOfDirectoryToCSARRoot)) {
					fileToStorageProviderIDMapOfDirectory.put(file, fileStorageProviderID);
				}
				
			}
			
			// remove the directory to get (it's not IN the directory)
			directoriesInDirectory.remove(relPathOfDirectoryToCSARRoot);
			
			return new CSARDirectory(relPathOfDirectoryToCSARRoot.toString(), this.getIncludePatterns(), this.getExcludePatterns(), this.CSAR_ID, directoriesInDirectory, fileToStorageProviderIDMapOfDirectory, false);
			
		}
		
		return null;
	}
	
	@Override
	public Set<AbstractDirectory> getDirectories() {
		
		Set<AbstractDirectory> csarDirectories = new HashSet<AbstractDirectory>();
		
		// If it's a file artifact we have no directories and can directly
		// return an empty set.
		if (!this.isFileArtifact()) {
			
			Path directoryReferenceAsPath = Paths.get(this.getPath());
			
			for (Path directory : this.DIRECTORIES) {
				
				Path directoryParent = directory.getParent();
				
				// the second condition only applies if this directory is in
				// the CSAR root, because a directory in root has no parent
				if (directoryReferenceAsPath.equals(directoryParent) || (directoryParent == null)) {
					csarDirectories.add(this.getDirectory(directory));
				}
				
			}
			
		}
		
		return csarDirectories;
		
	}
	
	@Override
	public String getName() {
		Path directoryPath = Paths.get(this.getPath());
		return directoryPath.getFileName().toString();
	}
	
	/**
	 * @return {@inheritDoc} It's the relative path to CSAR root.
	 */
	@Override
	public String getPath() {
		return super.getPath();
	}
	
}
