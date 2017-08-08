package org.opentosca.container.core.model.instance;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.common.jpa.QNameConverter;
import org.w3c.dom.Document;

/**
 *
 * Model class representing a NodeInstance
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 */
@Deprecated
@Entity
@Converters({@Converter(name = "QNameConverter", converterClass = QNameConverter.class),
    @Converter(name = "DOMDocumentConverter", converterClass = DocumentConverter.class)})
@NamedQueries({@NamedQuery(name = RelationInstance.getRelationInstances,
    query = RelationInstance.getRelationInstancesQuery)})
public class RelationInstance {

  // Query to retrieve NodeInstances identified by some parameters
  public final static String getRelationInstances = "RelationInstance.getRelationInstancesQuery";
  protected final static String getRelationInstancesQuery = "select n from RelationInstance n where"
      + " n.id = COALESCE(:internalID, n.id) AND"
      + " n.relationshipTemplateName = COALESCE(:relationshipTemplateName, n.relationshipTemplateName) AND"
      + " n.serviceInstance.id = COALESCE(:internalServiceInstanceID, n.serviceInstance.id) AND"
      + " n.relationshipTemplateID = COALESCE(:relationshipTemplateID, n.relationshipTemplateID)";

  // the internal ID (Database) of the NodeInstance
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // the external ID (used in all contexts BUT in the Database)
  // it is separated because there is no need to save BOTH into the DB!
  @Transient
  private URI relationInstanceID;

  @Convert("QNameConverter")
  private QName relationshipTemplateID;

  // the name of the corresponding NodeTemplate
  private String relationshipTemplateName;

  @Temporal(TemporalType.TIMESTAMP)
  // the creation date of a nodeInstance
  private Date created;

  // foreign key relationship to serviceInstance
  @ManyToOne
  @JoinColumn(name = "serviceInstance")
  ServiceInstance serviceInstance;

  @OneToOne
  @PrimaryKeyJoinColumn(name = "id")
  NodeInstance sourceInstance;

  @OneToOne
  @PrimaryKeyJoinColumn(name = "id")
  NodeInstance targetInstance;

  @Column(name = "properties", columnDefinition = "VARCHAR(8192)")
  @Convert("DOMDocumentConverter")
  Document properties;

  @Enumerated(EnumType.STRING)
  private State.Relationship state = State.Relationship.INITIAL;

  // nodeType of the nodeTemplate which this nodeInstance depends on
  @Convert("QNameConverter")
  private QName relationshipType;


  // This empty constructor is required by JPA
  @SuppressWarnings("unused")
  private RelationInstance() {}

  /**
   * Creates a new instance of a NodeTemplate. ID and creation date will be set automatically.
   *
   * @param relationshipTemplateID - the relationshipTemplateID specified by the Namespace and the
   *        ID value of the RelationshipTemplate
   * @param relationshipTemplateName - the name of the nodeTemplate
   */
  public RelationInstance(final QName relationshipTemplateID, final String relationshipTemplateName,
      final QName relationshipTypeOfRelationshipTemplate, final ServiceInstance serviceInstance,
      final NodeInstance sourceInstanceID, final NodeInstance targetInstanceID) {
    super();
    this.relationshipTemplateID = relationshipTemplateID;
    this.relationshipTemplateName = relationshipTemplateName;
    this.serviceInstance = serviceInstance;
    this.sourceInstance = sourceInstanceID;
    this.targetInstance = targetInstanceID;
    this.created = new Date();
    this.properties = null;
    this.relationshipType = relationshipTypeOfRelationshipTemplate;
  }

  public QName getRelationshipType() {
    return this.relationshipType;
  }

  public String getRelationshipTemplateName() {
    return this.relationshipTemplateName;
  }

  public int getId() {
    return this.id;
  }

  public URI getRelationInstanceID() {
    return this.relationInstanceID;
  }

  public QName getRelationshipTemplateID() {
    return this.relationshipTemplateID;
  }

  public Date getCreated() {
    return this.created;
  }

  public State.Relationship getState() {
    return this.state;
  }

  public void setState(final State.Relationship state) {
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

  public NodeInstance getSourceInstance() {
    return this.sourceInstance;
  }

  public NodeInstance getTargetInstance() {
    return this.targetInstance;
  }

  /**
   * The ID persisted in the database is "only" an integer. To the outside, we need the ID to be an
   * URI. To avoid storing two IDs in the database we generate the URI ID out of the integer ID.
   * Therefore, when reading a NodeInstance object from the database we need to set the URI ID
   * accordingly.
   */
  @PostLoad
  @PostPersist
  private void setRelationInstanceID() {
    try {
      this.relationInstanceID =
          new URI(Settings.CONTAINER_API + "/CSARs/" + this.serviceInstance.getCSAR_ID()
              + "/ServiceTemplates/"
              + URLEncoder.encode(URLEncoder
                  .encode(this.serviceInstance.getServiceTemplateID().toString(), "UTF-8"), "UTF-8")
              + "/Instances/" + this.serviceInstance.getDBId() + "/RelationshipTemplates/"
              + this.relationshipTemplateName + "/Instances/" + this.id);

    } catch (final URISyntaxException e) {
      e.printStackTrace();
    } catch (final UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RelationInstance [id=" + this.id + ", relationInstanceID=" + this.relationInstanceID
        + ", relationshipTemplateID=" + this.relationshipTemplateID + ", relationshipTemplateName="
        + this.relationshipTemplateName + ", created=" + this.created + ", serviceInstance="
        + this.serviceInstance + ", properties=" + this.properties + "]";
  }

}
