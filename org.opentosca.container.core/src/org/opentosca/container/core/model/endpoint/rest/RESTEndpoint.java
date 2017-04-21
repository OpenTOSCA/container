package org.opentosca.container.core.model.endpoint.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
 * This class Represents a REST-Endpoint (an endpoint with a REST-Operation).
 * For the fields of this class refer to the REST operation element in the
 * TOSCA-Specification.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */

// Named Queries for JPA
// @formatter:off
@NamedQueries({
	@NamedQuery(name = RESTEndpoint.getEndpointForPath, query = RESTEndpoint.getEndpointForPathQuery),
	@NamedQuery(name = RESTEndpoint.getEndpointForPathAndMethod, query = RESTEndpoint.getEndpointForPathAndMethodQuery),
	@NamedQuery(name = RESTEndpoint.getEndpointForUri, query = RESTEndpoint.getEndpointForUriQuery)
})
// @formatter:on
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = RESTEndpoint.tableName, uniqueConstraints = @UniqueConstraint(columnNames = {"path", "method", "csarId"}))
public class RESTEndpoint extends GenericEndpoint {
	
	protected static final String tableName = "RESTEndpoint";
	
	// Named queries:
	
	// Query to retrieve RESTEndpoints by Path.
	public static final String getEndpointForPath = "RESTEndpoint.getByPath";
	protected static final String getEndpointForPathQuery = "select t from " + RESTEndpoint.tableName + " t where t.path = :path and t.csarId = :csarId";
	
	// Query to retrieve a RESTEndpoint by Path and Method
	public static final String getEndpointForPathAndMethod = "RESTEndpoint.getByPathAndMethod";
	protected static final String getEndpointForPathAndMethodQuery = "select t from " + RESTEndpoint.tableName + " t where t.path = :path and t.method = :method and t.csarId = :csarId";
	
	// Query to check if an Endpoint with given URI exists.
	public static final String getEndpointForUri = "RESTEndpoint.getByUri";
	protected static final String getEndpointForUriQuery = "select t from " + RESTEndpoint.tableName + " t where t.uri = :uri and t.csarId = :csarId";
	
	
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
	private List<RequestHeader> headers = new ArrayList<RequestHeader>();
	
	@Column(name = "Parameters")
	private List<Parameter> params = new ArrayList<Parameter>();
	
	
	public RESTEndpoint() {
		super();
	}
	
	public RESTEndpoint(URI uri, restMethod method, CSARID csarId) {
		super(uri, csarId);
		this.method = method;
		this.path = uri.getPath();
	}
	
	public RESTEndpoint(String host, String path, restMethod method, CSARID csarId) throws URISyntaxException {
		// Check if the path starts with a "/", if not we prepend a "/".
		this(new URI(host + ((path.charAt(0) == '/') ? path : '/' + path)), method, csarId);
	}
	
	public RESTEndpoint(URI uri, restMethod method, QName requestPayload, QName responsePayload, CSARID csarId) {
		this(uri, method, csarId);
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
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void addParameter(Parameter p) {
		this.params.add(p);
	}
	
	public void addRequestHeader(RequestHeader h) {
		this.headers.add(h);
	}
	
	public List<RequestHeader> getRequestHeaders() {
		return this.headers;
	}
	
	public List<Parameter> getParameters() {
		return this.params;
	}
	
}
