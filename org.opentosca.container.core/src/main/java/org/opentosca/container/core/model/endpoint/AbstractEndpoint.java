package org.opentosca.container.core.model.endpoint;

import java.net.URI;

import javax.persistence.*;

import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.common.jpa.UriConverter;
import org.opentosca.container.core.model.csar.CsarId;

/**
 * This abstract class is used as a super-class for WSDL and REST Endpoints.
 */
@MappedSuperclass
public abstract class AbstractEndpoint {

  @Id
  @GeneratedValue
  protected Long id;

  /**
   * The URI which can be used to access the endpoint that is represented by this class.
   */
  @Basic
  @Convert(converter = UriConverter.class)
  @Column(name = "uri", nullable = false)
  private URI uri;

  /**
   * The host name of the OpenTOSCA Container where the CSAR identified by
   * {@link GenericEndpoint#csarId} is deployed. This attribute is needed to clearly identify
   * CSARs and service instances for collaboration, because otherwise different OpenTOSCA
   * Containers could use the same IDs for different instances. When no collaboration is used,
   * this attribute is always the host name of the local Container.
   */
  @Basic
  @Column(name = "triggeringContainer", nullable = false)
  private String triggeringContainer;

  /**
   * The host name of the OpenTOSCA Container which manages this endpoint. If the endpoint is
   * deployed by the local Container, the field equals the value of
   * {@link org.opentosca.container.core.common.Settings#OPENTOSCA_CONTAINER_HOSTNAME}. If the
   * endpoint was created by collaboration between different OpenTOSCA Container instances, the
   * field identifies the Container that is responsible for access to and the undeployment of the
   * endpoint. This might be necessary if the endpoint is protected by a firewall and can not be
   * accessed directly by this Container.
   */
  @Basic
  @Column(name = "managingContainer", nullable = false)
  private String managingContainer;

  /**
   * Identifies the CSAR to which this endpoint belongs. The ID has to be set for all endpoints,
   * except the Management Bus endpoint as this endpoint is CSAR independent. To avoid null values
   * "***" is set for this endpoint.
   */
  @Convert(converter = CsarIdConverter.class)
  @Column(name = "csarID")
  private CsarId csarId;

  /**
   * Identifies a service instance of the CSAR defined by {@link GenericEndpoint#csarId} where
   * this endpoint belongs to. Some endpoints belong to a certain service instance of a CSAR (IA
   * endpoints) whereas others only belong to a CSAR (Plan endpoints). Therefore, this field is
   * set for IA endpoints and it is <tt>null</tt> for Plan endpoints.
   */
  @Basic
  @Column(name = "serviceTemplateInstanceID")
  private Long serviceTemplateInstanceID;

  public AbstractEndpoint() {
    super();
  }

  /**
   * Constructor
   *
   * @param uri
   * @param managingContainer
   * @param managingContainer
   * @param csarId
   * @param serviceTemplateInstanceID
   */
  public AbstractEndpoint(final URI uri, final String triggeringContainer, final String managingContainer,
                          final CsarId csarId, final Long serviceTemplateInstanceID) {
    setURI(uri);
    setTriggeringContainer(triggeringContainer);
    setManagingContainer(managingContainer);
    setCsarId(csarId);
    setServiceTemplateInstanceID(serviceTemplateInstanceID);
  }

  public void setCsarId(final CsarId csarId) {
    this.csarId = csarId;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public URI getURI() {
    return this.uri;
  }

  public void setURI(final URI uri) {
    this.uri = uri;
  }

  public CsarId getCsarId() {
    return this.csarId;
  }

  public Long getServiceTemplateInstanceID() {
    return this.serviceTemplateInstanceID;
  }

  public void setServiceTemplateInstanceID(final Long serviceTemplateInstanceID) {
    this.serviceTemplateInstanceID = serviceTemplateInstanceID;
  }

  public String getManagingContainer() {
    return this.managingContainer;
  }

  public void setManagingContainer(final String managingContainer) {
    this.managingContainer = managingContainer;
  }

  public String getTriggeringContainer() {
    return this.triggeringContainer;
  }

  public void setTriggeringContainer(final String triggeringContainer) {
    this.triggeringContainer = triggeringContainer;
  }
}
