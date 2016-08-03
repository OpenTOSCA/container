package org.opentosca.model.csarinstancemanagement;

import org.opentosca.core.model.csar.id.CSARID;

/**
 * This is an identification class for CSARInstances. The CSARInstance is
 * identified by the CSAR it is an instance of and a internal ID.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceID {
	
	private CSARID csarID;
	private int instanceID = 0;
	
	
	@SuppressWarnings("unused")
	private CSARInstanceID() {
	}
	
	public CSARInstanceID(CSARID csarID, int internalID) {
		super();
		this.csarID = csarID;
		this.instanceID = internalID;
	}
	
	public int getInstanceID() {
		return this.instanceID;
	}
	
	public CSARID getOwner() {
		return this.csarID;
	}
	
	@Override
	public String toString() {
		return "InstanceID for CSAR \"" + this.csarID + "\" and internal ID " + this.instanceID + ".";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.csarID == null) ? 0 : this.csarID.hashCode());
		result = (prime * result) + this.instanceID;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CSARInstanceID)) {
			return false;
		}
		CSARInstanceID other = (CSARInstanceID) obj;
		if (this.csarID == null) {
			if (other.csarID != null) {
				return false;
			}
		} else if (!this.csarID.equals(other.csarID)) {
			return false;
		}
		if (this.instanceID != other.instanceID) {
			return false;
		}
		return true;
	}
}
