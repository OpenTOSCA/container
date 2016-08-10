package org.opentosca.core.model.artifact.file;

import java.io.InputStream;
import java.nio.file.Path;

import org.opentosca.exceptions.SystemException;
import org.opentosca.util.fileaccess.service.IFileAccessService;

/**
 * Abstract class of a file.<br />
 * <br />
 * Provides methods to fetch the file and get it's meta data.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public abstract class AbstractFile {
	
	private final String FILE_REFERENCE;
	
	
	/**
	 * Creates a {@link AbstractFile}.
	 * 
	 * @param fileReference that points to this file.
	 */
	public AbstractFile(String fileReference) {
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
