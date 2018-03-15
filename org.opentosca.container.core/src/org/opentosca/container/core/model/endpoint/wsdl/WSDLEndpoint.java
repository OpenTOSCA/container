package org.opentosca.container.core.model.endpoint.wsdl;

import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.GenericEndpoint;

/**
 * This class Represents a WSDL-Endpoint (an endpoint which points to a SOAP-Operation of a WSDL).
 * For the fields of this class refer to the WSDL operation element in the TOSCA-Specification.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({@NamedQuery(name = WSDLEndpoint.getWSDLEndpointByPortType,
                           query = WSDLEndpoint.getWSDLEndpointByPortTypeQuery),
               // @NamedQuery(name =
               // WSDLEndpoint.getWSDLEndpointByPortTypeAndAddressType, query =
               // WSDLEndpoint.getWSDLEndpointByPortTypeAndAddressTypeQuery),
               @NamedQuery(name = WSDLEndpoint.getWSDLEndpointByUri, query = WSDLEndpoint.getWSDLEndpointByUriQuery)})
@Table(name = WSDLEndpoint.tableName,
       uniqueConstraints = @UniqueConstraint(columnNames = {"portType", "addressType", "csarId"}))
public class WSDLEndpoint extends GenericEndpoint {

    // Table Name
    protected final static String tableName = "WSDLEndpoint";

    // Named queries for JPA

    // Query to retrieve WSDLEndpoints identified by a given PortType
    public final static String getWSDLEndpointByPortType = "WSDLEndpoint.getWSDLEndpointByPortType";
    protected final static String getWSDLEndpointByPortTypeQuery =
        "select t from WSDLEndpoint t where t.PortType = :portType and t.csarId = :csarId";

    // Query to retrieve a WSDLEndpoint identified by a given PortType and
    // AddressType
    // public final static String getWSDLEndpointByPortTypeAndAddressType =
    // "WSDLEndpoint.getWSDLEndpointByPortTypeAndAddressType";
    // protected final static String
    // getWSDLEndpointByPortTypeAndAddressTypeQuery =
    // "select t from WSDLEndpoint t where t.PortType = :portType and
    // t.AddressType = :addressType and t.csarId = :csarId";

    public final static String getWSDLEndpointByUri = "WSDLEndpoint.getWSDLEndpointByUri";
    protected final static String getWSDLEndpointByUriQuery =
        "select t from WSDLEndpoint t where t.uri = :uri and t.csarId = :csarId";

    // Converter to Convert QNames to String, and back from String to QName.
    // Used when persisting, so we can Query for QName-Objects.
    @Basic
    @Converter(name = "QNameConverter", converterClass = org.opentosca.container.core.common.jpa.QNameConverter.class)
    @Convert("QNameConverter")
    @Column(name = "PortType")
    private QName PortType;

    // nodetypeimplementation and ianame are there to identify specific ias
    @Basic
    @Convert("QNameConverter")
    @Column(name = "NodeTypeImplementation")
    private QName NodeTypeImplementation;

    @Basic
    @Column(name = "IaName")
    private String IaName;

    // only the planid is used for plan endpoints, cause in tosca the id for a
    // plan must be unique in the targetnamespace
    @Basic
    @Convert("QNameConverter")
    @Column(name = "PlanId")
    private QName PlanId;


    // @Convert("QNameConverter")
    // @Column(name = "AddressType")
    // private QName AddressType;

    public WSDLEndpoint() {
        super();
    }

    // if planid is set nodeTypeimpl and iaName must be "null"
    public WSDLEndpoint(final URI uri, final QName portType, final CSARID csarId, final QName planid,
                        final QName nodeTypeImplementation, final String iaName) {
        super(uri, csarId);
        this.setPortType(portType);
        this.setIaName(iaName);
        this.setPlanId(planid);
        this.setNodeTypeImplementation(nodeTypeImplementation);
        // this.setAddressType(addressType);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public QName getPortType() {
        return this.PortType;
    }

    public void setPortType(final QName portType) {
        this.PortType = portType;
    }

    public QName getNodeTypeImplementation() {
        return this.NodeTypeImplementation;
    }

    public void setNodeTypeImplementation(final QName nodeTypeImplementation) {
        this.NodeTypeImplementation = nodeTypeImplementation;
    }

    public QName getPlanId() {
        return this.PlanId;
    }

    public void setPlanId(final QName planId) {
        this.PlanId = planId;
    }

    public String getIaName() {
        return this.IaName;
    }

    public void setIaName(final String iaName) {
        this.IaName = iaName;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof WSDLEndpoint)) {
            return false;
        }

        final WSDLEndpoint compareEndpoint = (WSDLEndpoint) o;
        if (compareEndpoint.getId() != this.getId()) {
            return false;
        }
        if (!compareEndpoint.getCSARId().equals(this.getCSARId())) {
            return false;
        }
        return true;
    }

}
