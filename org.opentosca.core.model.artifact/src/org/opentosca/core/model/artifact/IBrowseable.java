package org.opentosca.core.model.artifact;

import java.util.Set;

import org.opentosca.core.model.artifact.directory.AbstractDirectory;
import org.opentosca.core.model.artifact.file.AbstractFile;

/**
 * Provides methods for getting the directories and files in a directory
 * respectively root.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public interface IBrowseable {
	
	/**
	 * @param relPathOfDirectory - directory path relative to this directory /
	 *            root.
	 * @return {@link AbstractDirectory} of directory {@code relPathOfDirectory}
	 *         . If it not exists {@code null}.
	 */
	public AbstractDirectory getDirectory(String relPathOfDirectory);
	
	/**
	 * @param relPathOfFile - path of file relative to this directory / root.
	 * @return {@link AbstractFile} of file {@code relPathOfFile}. If it not
	 *         exists {@code null}.
	 */
	public AbstractFile getFile(String relPathOfFile);
	
	/**
	 * @return {@code Set} containing {@link AbstractDirectory} of directories
	 *         in this directory (not recursively).
	 */
	public Set<AbstractDirectory> getDirectories();
	
	/**
	 * @return {@code Set} containing {@link AbstractFile} of files in this
	 *         directory (not recursively).
	 */
	public Set<AbstractFile> getFiles();
	
	/**
	 * @return {@code Set} containing {@link AbstractFile} of files in this
	 *         directory and it's sub directories (recursively).
	 */
	public Set<AbstractFile> getFilesRecursively();
	
}
