package org.opentosca.container.core.model.endpoint.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * This class Represents a REST-Endpoint (an endpoint with a REST-Operation). For the fields of this
 * class refer to the REST operation element in the TOSCA-Specification.
 */
// Named Queries for JPA
@NamedQueries({@NamedQuery(name = RESTEndpoint.getEndpointForPath, query = RESTEndpoint.getEndpointForPathQuery),
               @NamedQuery(name = RESTEndpoint.getEndpointForPathAndMethod,
                           query = RESTEndpoint.getEndpointForPathAndMethodQuery),
               @NamedQuery(name = RESTEndpoint.getEndpointForUri, query = RESTEndpoint.getEndpointForUriQuery)})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = RESTEndpoint.tableName, uniqueConstraints = @UniqueConstraint(columnNames = {"path", "method", "csarId"}))
public class RESTEndpoint extends GenericEndpoint {

    protected static final String tableName = "RESTEndpoint";

    // Named queries:

    // Query to retrieve RESTEndpoints by Path.
    public static final String getEndpointForPath = "RESTEndpoint.getByPath";
    protected static final String getEndpointForPathQuery =
        "select t from " + RESTEndpoint.tableName + " t where t.path = :path and t.csarId = :csarId";

    // Query to retrieve a RESTEndpoint by Path and Method
    public static final String getEndpointForPathAndMethod = "RESTEndpoint.getByPathAndMethod";
    protected static final String getEndpointForPathAndMethodQuery = "select t from " + RESTEndpoint.tableName
        + " t where t.path = :path and t.method = :method and t.csarId = :csarId";

    // Query to check if an Endpoint with given URI exists.
    public static final String getEndpointForUri = "RESTEndpoint.getByUri";
    protected static final String getEndpointForUriQuery =
        "select t from " + RESTEndpoint.tableName + " t where t.uri = :uri and t.csarId = :csarId";


    public static enum restMethod {
        GET, PUT, POST, DELETE
    };


    // Converter to Convert QNames to String, and back from String to QName.
    // Used when persisting, so we can Query for QName-Objects.
    @Basic
    @Converter(name = "QNameConverter", converterClass = org.opentosca.container.core.common.jpa.QNameConverter.class)
    @Column(name = "method")
    private restMethod method;

    @Column(name = "path")
    private String path;

    @Convert("QNameConverter")
    private QName requestPayload;

    @Convert("QNameConverter")
    private QName responsePayload;

    @Column(name = "RequestHeaders")
    private final List<RequestHeader> headers = new ArrayList<>();

    @Column(name = "Parameters")
    private final List<Parameter> params = new ArrayList<>();


    public RESTEndpoint() {
        super();
    }

    public RESTEndpoint(final URI uri, final restMethod method, final String triggeringContainer,
                        final String managingContainer, final CSARID csarId, final Long serviceInstanceID, Map<String,String> metadata) {
        super(uri, triggeringContainer, managingContainer, csarId, serviceInstanceID, metadata);
        this.method = method;
        this.path = uri.getPath();
    }

    public RESTEndpoint(final String host, final String path, final restMethod method, final String managingContainer,
                        final String triggeringContainer, final CSARID csarId,
                        final Long serviceInstanceID, Map<String,String> metadata) throws URISyntaxException {
        // Check if the path starts with a "/", if not we prepend a "/".
        this(new URI(host + (path.charAt(0) == '/' ? path : '/' + path)), method, triggeringContainer,
             managingContainer, csarId, serviceInstanceID, metadata);
    }

    public RESTEndpoint(final URI uri, final restMethod method, final QName requestPayload, final QName responsePayload,
                        final String triggeringContainer, final String managingContainer, final CSARID csarId,
                        final Long serviceInstanceID, Map<String,String> metadata) {
        this(uri, method, triggeringContainer, managingContainer, csarId, serviceInstanceID, metadata);
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
    }

    public QName getRequestPayload() {
        return this.requestPayload;
    }

    public QName getResponsePayload() {
        return this.responsePayload;
    }

    public restMethod getRequestMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void addParameter(final Parameter p) {
        this.params.add(p);
    }

    public void addRequestHeader(final RequestHeader h) {
        this.headers.add(h);
    }

    public List<RequestHeader> getRequestHeaders() {
        return this.headers;
    }

    public List<Parameter> getParameters() {
        return this.params;
    }

}
