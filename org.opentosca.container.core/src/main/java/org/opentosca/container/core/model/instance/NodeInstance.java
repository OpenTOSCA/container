package org.opentosca.container.core.model.instance;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

import javax.persistence.*;
import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.common.jpa.QNameConverter;
import org.w3c.dom.Document;

/**
 * Model class representing a NodeInstance
 */
@Deprecated
@Entity
@NamedQueries( {@NamedQuery(name = NodeInstance.getNodeInstances, query = NodeInstance.getNodeInstancesQuery)})
public class NodeInstance {

  // Query to retrieve NodeInstances identified by some parameters
  public final static String getNodeInstances = "NodeInstance.getNodeInstancesQuery";
  protected final static String getNodeInstancesQuery =
    "select n from NodeInstance n where" + " n.id = COALESCE(:internalID, n.id) AND"
      + " n.nodeTemplateName = COALESCE(:nodeTemplateName, n.nodeTemplateName) AND"
      + " n.serviceInstance.id = COALESCE(:internalServiceInstanceID, n.serviceInstance.id) AND"
      + " n.nodeTemplateID = COALESCE(:nodeTemplateID, n.nodeTemplateID)";

  // the internal ID (Database) of the NodeInstance
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // the external ID (used in all contexts BUT in the Database)
  // it is separated because there is no need to save BOTH into the DB!
  @Transient
  private URI nodeInstanceID;

  @Convert(converter = QNameConverter.class)
  private QName nodeTemplateID;

  // the name of the corresponding NodeTemplate
  private String nodeTemplateName;

  @Temporal(TemporalType.TIMESTAMP)
  // the creation date of a nodeInstance
  private Date created;

  // foreign key relationship to serviceInstance
  @ManyToOne
  @JoinColumn(name = "serviceInstance")
  private ServiceInstance serviceInstance;

  @Column(name = "properties", columnDefinition = "VARCHAR(8192)")
  @Convert(converter = DocumentConverter.class)
  private Document properties;

  @Enumerated(EnumType.STRING)
  private State.Node state = State.Node.INITIAL;

  // nodeType of the nodeTemplate which this nodeInstance depends on
  @Convert(converter = QNameConverter.class)
  private QName nodeType;


  // This empty constructor is required by JPA
  @SuppressWarnings("unused")
  private NodeInstance() {
  }

  /**
   * Creates a new instance of a NodeTemplate. ID and creation date will be set automatically.
   *
   * @param nodeTemplateID   - the nodeTemplateID specified by the Namespace and the ID value of the
   *                         NodeTemplate
   * @param nodeTemplateName - the name of the nodeTemplate
   */
  public NodeInstance(final QName nodeTemplateID, final String nodeTemplateName, final QName nodeTypeOfNodeTemplate,
                      final ServiceInstance serviceInstance) {
    super();
    this.nodeTemplateID = nodeTemplateID;
    this.nodeTemplateName = nodeTemplateName;
    this.serviceInstance = serviceInstance;
    this.created = new Date();
    this.properties = null;
    this.nodeType = nodeTypeOfNodeTemplate;
  }

  public QName getNodeType() {
    return this.nodeType;
  }

  public String getNodeTemplateName() {
    return this.nodeTemplateName;
  }

  public int getId() {
    return this.id;
  }

  public URI getNodeInstanceID() {
    return this.nodeInstanceID;
  }

  public QName getNodeTemplateID() {
    return this.nodeTemplateID;
  }

  public Date getCreated() {
    return this.created;
  }

  public State.Node getState() {
    return this.state;
  }

  public void setState(final State.Node state) {
    this.state = state;
  }

  public void setProperties(final Document props) {
    this.properties = props;
  }

  public Document getProperties() {
    return this.properties;
  }

  public ServiceInstance getServiceInstance() {
    return this.serviceInstance;
  }

  /**
   * The ID persisted in the database is "only" an integer. To the outside, we need the ID to be an
   * URI. To avoid storing two IDs in the database we generate the URI ID out of the integer ID.
   * Therefore, when reading a NodeInstance object from the database we need to set the URI ID
   * accordingly.
   */
  @PostLoad
  @PostPersist
  public void setNodeInstanceID() {
    try {
      this.nodeInstanceID = new URI(Settings.CONTAINER_API_LEGACY + "/CSARs/" + this.serviceInstance.getCSAR_ID()
        + "/ServiceTemplates/"
        + URLEncoder.encode(URLEncoder.encode(this.serviceInstance.getServiceTemplateID().toString(), "UTF-8"),
        "UTF-8")
        + "/Instances/" + this.serviceInstance.getDBId() + "/NodeTemplates/"
        + this.nodeTemplateID.getLocalPart() + "/Instances/" + this.id);

    } catch (final URISyntaxException e) {
      e.printStackTrace();
    } catch (final UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void setId(final int id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "NodeInstance [id=" + this.id + ", nodeInstanceID=" + this.nodeInstanceID + ", nodeTemplateID="
      + this.nodeTemplateID + ", nodeTemplateName=" + this.nodeTemplateName + ", created=" + this.created
      + ", serviceInstance=" + this.serviceInstance + ", properties=" + this.properties + "]";
  }

  public void setCreated(final Date created) {
    this.created = created;
  }
}
