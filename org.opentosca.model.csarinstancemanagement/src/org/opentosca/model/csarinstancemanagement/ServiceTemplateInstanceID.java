package org.opentosca.model.csarinstancemanagement;

import javax.xml.namespace.QName;

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
public class ServiceTemplateInstanceID {
	
	
	private CSARID csarID;
	private QName serviceTemplateId;
	private int serviceTemplateInstanceID = 0;
	
	
	@SuppressWarnings("unused")
	private ServiceTemplateInstanceID() {
	}
	
	public ServiceTemplateInstanceID(CSARID csarID, QName serviceTemplateId, int serviceTemplateInstanceID) {
		super();
		setServiceTemplateId(serviceTemplateId);
		this.csarID = csarID;
		this.serviceTemplateInstanceID = serviceTemplateInstanceID;
	}
	
	public int getInstanceID() {
		return serviceTemplateInstanceID;
	}
	
	public CSARID getCsarId() {
		return csarID;
	}
	
	@Override
	public String toString() {
		return "InstanceID for CSAR \"" + csarID + "\" and internal ID " + serviceTemplateInstanceID + ".";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((csarID == null) ? 0 : csarID.hashCode());
		result = (prime * result) + serviceTemplateInstanceID;
		return result;
	}
	
	public QName getServiceTemplateId() {
		return serviceTemplateId;
	}
	
	public void setServiceTemplateId(QName serviceTemplateId) {
		this.serviceTemplateId = serviceTemplateId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ServiceTemplateInstanceID)) {
			return false;
		}
		ServiceTemplateInstanceID other = (ServiceTemplateInstanceID) obj;
		if (csarID == null) {
			if (other.csarID != null) {
				return false;
			}
		} else if (!csarID.equals(other.csarID)) {
			return false;
		}
		if (serviceTemplateInstanceID != other.serviceTemplateInstanceID) {
			return false;
		}
		return true;
	}
}
