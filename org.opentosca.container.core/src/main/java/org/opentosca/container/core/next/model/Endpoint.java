package org.opentosca.container.core.next.model;

import java.net.URI;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.common.jpa.QNameConverter;
import org.opentosca.container.core.common.jpa.UriConverter;
import org.opentosca.container.core.model.csar.CsarId;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Endpoint.TABLE_NAME)
public class Endpoint extends PersistenceObject {

    public static final String TABLE_NAME = "ENDPOINT";

    /**
     * The URI which can be used to access the endpoint that is represented by this class.
     */
    @Convert(converter = UriConverter.class)
    @Column(name = "uri", nullable = false, length = 1028)
    private URI uri;

    /**
     * The host name of the OpenTOSCA Container where the CSAR identified by {@link Endpoint#csarId} is deployed. This
     * attribute is needed to clearly identify CSARs and service instances for collaboration, because otherwise
     * different OpenTOSCA Containers could use the same IDs for different instances. When no collaboration is used,
     * this attribute is always the host name of the local Container.
     */
    @Basic
    @Column(name = "triggeringContainer", nullable = false)
    private String triggeringContainer;

    /**
     * The host name of the OpenTOSCA Container which manages this endpoint. If the endpoint is deployed by the local
     * Container, the field equals the value of {@link org.opentosca.container.core.common.Settings#OPENTOSCA_CONTAINER_HOSTNAME}.
     * If the endpoint was created by collaboration between different OpenTOSCA Container instances, the field
     * identifies the Container that is responsible for access to and the undeployment of the endpoint. This might be
     * necessary if the endpoint is protected by a firewall and can not be accessed directly by this Container.
     */
    @Basic
    @Column(name = "managingContainer", nullable = false)
    private String managingContainer;

    /**
     * Identifies the CSAR to which this endpoint belongs. The ID has to be set for all endpoints, except the Management
     * Bus endpoint as this endpoint is CSAR independent. To avoid null values "***" is set for this endpoint.
     */
    @Convert(converter = CsarIdConverter.class)
    @Column(name = "csarId")
    private CsarId csarId;

    /**
     * Identifies a service instance of the CSAR defined by {@link Endpoint#csarId} where this endpoint belongs to. Some
     * endpoints belong to a certain service instance of a CSAR (IA endpoints) whereas others only belong to a CSAR
     * (Plan endpoints). Therefore, this field is set for IA endpoints and it is <tt>null</tt> for Plan endpoints.
     */
    @Basic
    @Column(name = "serviceTemplateInstanceID")
    private Long serviceTemplateInstanceID;

    @ElementCollection
    @Column(name = "metadata")
    private Map<String, String> metadata;

    @Basic
    @Convert(converter = QNameConverter.class)
    @Column(name = "portType")
    private QName portType;

    // NodeTypeImplementation/RelationshipTypeImplementation and IA name are there to identify specific IAs
    @Basic
    @Convert(converter = QNameConverter.class)
    @Column(name = "typeImplementation")
    private QName typeImplementation;

    @Basic
    @Column(name = "iaName")
    private String iaName;

    // only the plan id is used for plan endpoints, cause in tosca the id for a plan must be unique in the target namespace
    @Basic
    @Convert(converter = QNameConverter.class)
    @Column(name = "planId")
    private QName planId;
}
