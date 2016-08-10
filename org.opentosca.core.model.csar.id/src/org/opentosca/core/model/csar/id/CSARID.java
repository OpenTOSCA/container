package org.opentosca.core.model.csar.id;

import java.io.Serializable;

/**
 * Identification of a CSAR file in OpenTOSCA.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CSARID implements Comparable<CSARID>, Serializable {
	
	private static final long serialVersionUID = 1889149925607823116L;
	
	/**
	 * File name of the CSAR file.
	 */
	protected String fileName;
	
	
	/**
	 * Needed by Eclipse Link.
	 */
	protected CSARID() {
	}
	
	/**
	 * Creates a {@link CSARID}.
	 * 
	 * @param fileName to set
	 */
	public CSARID(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 
	 * @return File name of the CSAR file.
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * String representation of the CSAR ID.
	 */
	@Override
	public String toString() {
		return this.getFileName();
	}
	
	/**
	 * Compares two CSAR IDs.
	 */
	@Override
	public int compareTo(CSARID csarID) {
		return this.toString().compareTo(csarID.toString());
	}
	
	@Override
	public boolean equals(Object id) {
		if (id instanceof CSARID) {
			return this.toString().equals(((CSARID) id).toString());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
