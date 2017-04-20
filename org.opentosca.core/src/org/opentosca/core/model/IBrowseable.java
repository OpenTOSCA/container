/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.core.model;

import java.util.Set;

/**
 * Provides methods for getting the directories and files in a directory
 * respectively root.
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
