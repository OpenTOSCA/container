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

    @Basic
    @Convert("URIConverter")
    @Column(name = "uri")
    private URI uri;

    // @Convert("QNameConverter")
    // @Column(name = "thorID")

    // The ThorID ID is used to distinguish endpoints between
    // different Thors
    // private QName thorID;
    @Convert("CSARIDConverter")
    @Column(name = "csarID")
    private CSARID csarId;

    @Id
    @GeneratedValue
    protected Long id;


    /**
     * Constructor
     *
     * @param uri
     * @param thorID
     */
    public GenericEndpoint(final URI uri, final CSARID csarId) {
        this.setCSARId(csarId);
        this.setURI(uri);
    }

    public void setCSARId(final CSARID csarId) {
        this.csarId = csarId;
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

    // public QName getThorID() {
    // return this.thorID;
    // }

    public CSARID getCSARId() {
        return this.csarId;
    }

    // public void setThorID(QName thorID) {
    // this.thorID = thorID;
    // }
}
