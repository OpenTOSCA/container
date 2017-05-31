package org.opentosca.container.core.model;

import java.io.InputStream;
import java.nio.file.Path;

import org.opentosca.container.core.common.SystemException;

/**
 * Abstract class of a file. Provides methods to fetch the file and get it's
 * meta data.
 */
public abstract class AbstractFile {
	
	private final String FILE_REFERENCE;
	
	
	/**
	 * Creates a {@link AbstractFile}.
	 *
	 * @param fileReference that points to this file.
	 */
	public AbstractFile(final String fileReference) {
		this.FILE_REFERENCE = fileReference;
	}
	
	/**
	 * Retrieves this file to a Temp directory from
	 * {@link IFileAccessService#getTemp()}.
	 *
	 * @throws SystemException if an error occurred during retrieving.
	 *
	 * @return {@link Path} of this file.
	 */
	public abstract Path getFile() throws SystemException;
	
	/**
	 * @throws SystemException if an error occurred during getting.
	 *
	 * @return {@link InputStream} of this file.
	 */
	public abstract InputStream getFileAsInputStream() throws SystemException;
	
	/**
	 * @return File name of this file.
	 */
	public abstract String getName();
	
	/**
	 * @return Reference that points to this file.
	 */
	public String getPath() {
		return this.FILE_REFERENCE;
	}
	
	/**
	 * @return String representation of this file.
	 */
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object file);
	
}
