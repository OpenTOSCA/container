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
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.common.jpa.UriConverter;
import org.opentosca.container.core.model.csar.CsarId;

/**
 * This abstract class is used as a super-class for WSDL and REST Endpoints.
 */
@MappedSuperclass
@Converters({@Converter(converterClass = CsarIdConverter.class, name = CsarIdConverter.name),
             @Converter(converterClass = UriConverter.class, name = UriConverter.name)})
public abstract class AbstractEndpoint {

    @Basic
    @Convert(UriConverter.name)
    @Column(name = "uri")
    private URI uri;

    @Convert(CsarIdConverter.name)
    @Column(name = "csarID")
    private CsarId csarId;

    @Id
    @GeneratedValue
    protected Long id;


    /**
     * Constructor
     *
     * @param uri
     * @param thorID
     */
    public AbstractEndpoint(final URI uri, final CsarId csarId) {
        this.setCsarId(csarId);
        this.setURI(uri);
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

    public AbstractEndpoint() {
        super();
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
}
