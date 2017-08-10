package org.opentosca.container.core.next.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = ServiceTemplateInstanceProperty.TABLE_NAME)
public class ServiceTemplateInstanceProperty extends Property {

  private static final long serialVersionUID = -8847410322957873980L;

  public static final String TABLE_NAME =
      ServiceTemplateInstance.TABLE_NAME + "_" + Property.TABLE_NAME;

  @ManyToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  private ServiceTemplateInstance serviceTemplateInstance;


  public ServiceTemplateInstanceProperty() {
    super();
  }

  public ServiceTemplateInstanceProperty(final String name, final String value) {
    super(name, value, null);
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getProperties().contains(this)) {
      serviceTemplateInstance.getProperties().add(this);
    }
  }
}
