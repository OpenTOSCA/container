package org.opentosca.container.core.model.endpoint;

import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.opentosca.container.core.common.jpa.UriConverter;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.csar.id.CSARIDConverter;

/**
 * This abstract class is used as a super-class for WSDL and REST Endpoints.
 */
@MappedSuperclass
@Converters({@Converter(converterClass = CSARIDConverter.class, name = "CSARIDConverter"),
             @Converter(converterClass = UriConverter.class, name = "URIConverter")})
public abstract class GenericEndpoint {

    @Id
    @GeneratedValue
    protected Long id;

    /**
     * The URI which can be used to access the endpoint that is represented by this class.
     */
    @Basic
    @Convert("URIConverter")
    @Column(name = "uri", nullable = false)
    private URI uri;

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
    @Convert("CSARIDConverter")
    @Column(name = "csarID", nullable = false)
    private CSARID csarId;

    /**
     * Identifies a service instance of the CSAR defined by {@link GenericEndpoint#csarId} where
     * this endpoint belongs to. Some endpoints belong to a certain service instance of a CSAR (IA
     * endpoints) whereas others only belong to a CSAR (Plan endpoints). Therefore, this field is
     * set for IA endpoints and it is <tt>null</tt> for Plan endpoints.
     */
    @Basic
    @Convert("URIConverter")
    @Column(name = "serviceInstanceID")
    private URI serviceInstanceID;

    /**
     * Constructor
     *
     * @param uri
     * @param csarId
     * @param serviceInstanceID
     */
    public GenericEndpoint(final URI uri, final String managingContainer, final CSARID csarId,
                           final URI serviceInstanceID) {
        setURI(uri);
        setManagingContainer(managingContainer);
        setCSARId(csarId);
        setServiceInstanceID(serviceInstanceID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public GenericEndpoint() {
        super();
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(final URI uri) {
        this.uri = uri;
    }

    public CSARID getCSARId() {
        return this.csarId;
    }

    public void setCSARId(final CSARID csarId) {
        this.csarId = csarId;
    }

    public URI getServiceInstanceID() {
        return this.serviceInstanceID;
    }

    public void setServiceInstanceID(final URI serviceInstanceID) {
        this.serviceInstanceID = serviceInstanceID;
    }

    public String getManagingContainer() {
        return this.managingContainer;
    }

    public void setManagingContainer(final String managingContainer) {
        this.managingContainer = managingContainer;
    }
}
