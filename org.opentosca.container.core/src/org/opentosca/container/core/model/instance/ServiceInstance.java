package org.opentosca.container.core.model.instance;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.common.jpa.QNameConverter;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.w3c.dom.Document;

/**
 * Model class representing a ServiceInstance
 */
@Entity
@NamedQueries({@NamedQuery(name = ServiceInstance.getServiceInstances,
    query = ServiceInstance.getServiceInstancesQuery)})
@Deprecated
public class ServiceInstance {

  // Query to retrieve ServiceInstances identified by a some parameters
  public final static String getServiceInstances = "ServiceInstance.getServiceInstancesQuery";
  protected final static String getServiceInstancesQuery =
      "select s from ServiceInstance s where" + " s.id = COALESCE(:id, s.id) AND"
          + " s.serviceTemplateName = COALESCE(:serviceTemplateName, s.serviceTemplateName) AND"
          + " s.serviceTemplateID = COALESCE(:serviceTemplateID, s.serviceTemplateID)";

  // the internal ID (Database) of the ServiceInstance
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // the external ID (used in all contexts BUT in the Database)
  // it is separated because there is no need to save BOTH into the DB!
  @Transient
  private URI serviceInstanceID;

  @Convert("QNameConverter")
  @Converter(name = "QNameConverter", converterClass = QNameConverter.class)
  private QName serviceTemplateID;

  // the name of the corresponding ServiceTemplate
  private String serviceTemplateName;

  @Temporal(TemporalType.TIMESTAMP)
  // the creation date of a ServiceInstance
  private Date created;

  @Transient
  private CSARID csarID;

  @Column(name = "csarID")
  private String csarID_DB;

  @Column(name = "properties", columnDefinition = "VARCHAR(4096)")
  @Convert("DocumentConverter")
  @Converter(name = "DocumentConverter", converterClass = DocumentConverter.class)
  Document properties;

  @Enumerated(EnumType.STRING)
  private State.ServiceTemplate state = State.ServiceTemplate.INITIAL;


  // This empty constructor is required by JPA
  @SuppressWarnings("unused")
  private ServiceInstance() {}


  @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  // cascade on delete tells the JPA Framework to let the DB handle the
  // deletion (if serviceInstance is deleted => delete also all nodeInstances
  // who reference it!)
  @CascadeOnDelete
  private List<NodeInstance> nodeInstances;

  @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  // cascade on delete tells the JPA Framework to let the DB handle the
  // deletion (if serviceInstance is deleted => delete also all nodeInstances
  // who reference it!)
  @CascadeOnDelete
  private List<RelationInstance> relationInstances;


  /**
   * Creates a new instance of a ServiceTemplate. ID and creation date will be set automatically.
   *
   * @param serviceTemplateID - the serviceTemplateID specified by the Namespace and the ID value of
   *        the ServiceTemplate
   * @param serviceTemplateName - the name of the ServiceTemplate
   */
  public ServiceInstance(final CSARID csarID, final QName serviceTemplateID,
      final String serviceTemplateName) {
    super();
    this.csarID = csarID;
    // needed to persist the object
    this.csarID_DB = csarID.getFileName();
    this.setServiceTemplateID(serviceTemplateID);
    this.serviceTemplateName = serviceTemplateName;

    this.created = new Date();
    this.properties = null;
  }

  public String getServiceTemplateName() {
    return this.serviceTemplateName;
  }

  public int getDBId() {
    return this.id;
  }

  public URI getServiceInstanceID() {
    return this.serviceInstanceID;
  }

  public QName getToscaID() {
    return this.getServiceTemplateID();
  }

  public Date getCreated() {
    return this.created;
  }

  public void setServiceTemplateName(final String serviceTemplateName) {
    this.serviceTemplateName = serviceTemplateName;
  }

  public CSARID getCSAR_ID() {
    return this.csarID;
  }

  /**
   * The ID persisted in the database is "only" an integer. To the outside, we need the ID to be an
   * URI. To avoid storing two IDs in the database we generate the URI ID out of the integer ID.
   * Therefore, when reading a ServiceInstance object from the database we need to set the URI ID
   * accordingly.
   */
  @PostLoad
  @PostPersist
  public void setIDs() {
    try {
      // old: serviceInstanceID = new URI(Settings.CONTAINER_API +
      // IdConverter.serviceInstancePath + id);
      // http://localhost:1337/containerapi/CSARs/BPMNLAMPStack.csar/ServiceTemplates/%257Bhttp%253A%252F%252Fopentosca.org%252FBPMN%257DBPMNLAMPStack/Instances/1/
      this.serviceInstanceID = new URI(Settings.CONTAINER_API_LEGACY + "/CSARs/" + this.csarID
          + "/ServiceTemplates/" + URLEncoder
              .encode(URLEncoder.encode(this.serviceTemplateID.toString(), "UTF-8"), "UTF-8")
          + "/Instances/" + this.id);
      this.csarID = new CSARID(this.csarID_DB);
    } catch (final URISyntaxException e) {
      e.printStackTrace();
    } catch (final UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void setProperties(final Document props) {
    this.properties = props;
  }

  public Document getProperties() {
    return this.properties;
  }

  public State.ServiceTemplate getState() {
    return this.state;
  }

  public void setState(final State.ServiceTemplate state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return "id:" + this.id + " created:" + this.created + " sID:" + this.serviceInstanceID
        + " templateID: " + this.getToscaID().toString() + " template name: "
        + this.serviceTemplateName;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return this.id;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ServiceInstance)) {
      return false;
    }
    final ServiceInstance other = (ServiceInstance) obj;
    if (this.created == null) {
      if (other.created != null) {
        return false;
      }
    } else if (!this.created.equals(other.created)) {
      return false;
    }
    if (this.csarID == null) {
      if (other.csarID != null) {
        return false;
      }
    } else if (!this.csarID.equals(other.csarID)) {
      return false;
    }
    if (this.csarID_DB == null) {
      if (other.csarID_DB != null) {
        return false;
      }
    } else if (!this.csarID_DB.equals(other.csarID_DB)) {
      return false;
    }
    if (this.id != other.id) {
      return false;
    }
    if (this.serviceInstanceID == null) {
      if (other.serviceInstanceID != null) {
        return false;
      }
    } else if (!this.serviceInstanceID.equals(other.serviceInstanceID)) {
      return false;
    }
    if (this.getServiceTemplateID() == null) {
      if (other.getServiceTemplateID() != null) {
        return false;
      }
    } else if (!this.getServiceTemplateID().equals(other.getServiceTemplateID())) {
      return false;
    }
    if (this.serviceTemplateName == null) {
      if (other.serviceTemplateName != null) {
        return false;
      }
    } else if (!this.serviceTemplateName.equals(other.serviceTemplateName)) {
      return false;
    }
    return true;
  }

  public QName getServiceTemplateID() {
    return this.serviceTemplateID;
  }

  public void setServiceTemplateID(final QName serviceTemplateID) {
    this.serviceTemplateID = serviceTemplateID;
  }

  public List<NodeInstance> getNodeInstances() {
    return this.nodeInstances;
  }

  public List<RelationInstance> getRelationInstances() {
    return this.relationInstances;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public CSARID getCsarID() {
    return csarID;
  }

  public void setCsarID(CSARID csarID) {
    this.csarID = csarID;
  }

  public String getCsarID_DB() {
    return csarID_DB;
  }

  public void setCsarID_DB(String csarID_DB) {
    this.csarID_DB = csarID_DB;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

}
