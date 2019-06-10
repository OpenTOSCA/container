package org.opentosca.container.core.model.instance;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * This is an identification class for CSARInstances. The CSARInstance is identified by the CSAR it
 * is an instance of and a internal ID.
 */
@Deprecated
public class ServiceTemplateInstanceID {

  private CsarId csarID;
  private QName serviceTemplateId;
  private int serviceTemplateInstanceID = 0;


  @SuppressWarnings("unused")
  private ServiceTemplateInstanceID() {
  }

  public ServiceTemplateInstanceID(final CsarId csarID, final QName serviceTemplateId,
                                   final int serviceTemplateInstanceID) {
    super();
    this.setServiceTemplateId(serviceTemplateId);
    this.csarID = csarID;
    this.serviceTemplateInstanceID = serviceTemplateInstanceID;
  }

  public int getInstanceID() {
    return this.serviceTemplateInstanceID;
  }

  public CsarId getCsarId() {
    return this.csarID;
  }

  @Override
  public String toString() {
    return "InstanceID for CSAR \"" + this.csarID.csarName() + "\" and internal ID " + this.serviceTemplateInstanceID + ".";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.csarID == null ? 0 : this.csarID.hashCode());
    result = prime * result + this.serviceTemplateInstanceID;
    return result;
  }

  public QName getServiceTemplateId() {
    return this.serviceTemplateId;
  }

  public void setServiceTemplateId(final QName serviceTemplateId) {
    this.serviceTemplateId = serviceTemplateId;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ServiceTemplateInstanceID)) {
      return false;
    }
    final ServiceTemplateInstanceID other = (ServiceTemplateInstanceID) obj;
    if (this.csarID == null) {
      if (other.csarID != null) {
        return false;
      }
    } else if (!this.csarID.equals(other.csarID)) {
      return false;
    }
    if (this.serviceTemplateInstanceID != other.serviceTemplateInstanceID) {
      return false;
    }
    return true;
  }
}
