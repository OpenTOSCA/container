package org.opentosca.container.api.util;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.glassfish.jersey.uri.UriComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UriUtils {

	private static Logger logger = LoggerFactory.getLogger(UriUtils.class);

	public static URI encode(final URI uri) {
		final List<PathSegment> pathSegments = UriComponent.decodePath(uri, false);
		final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
		// Build base URL
		uriBuilder.scheme(uri.getScheme()).host(uri.getHost()).port(uri.getPort());
		// Iterate over path segments and encode it if necessary
		for (final PathSegment ps : pathSegments) {
			uriBuilder.path(UriComponent.encode(ps.toString(), UriComponent.Type.PATH_SEGMENT));
		}
		logger.debug("URL before encoding: {}", uri);
		logger.debug("URL after encoding:  {}", uriBuilder);
		return URI.create(uriBuilder.toString());
	}

	public static Link generateSelfLink(final UriInfo uriInfo) {
		return Link.fromUri(UriUtils.generateSelfURI(uriInfo)).rel("self").build();
	}
	
	public static URI generateSelfURI(final UriInfo uriInfo) {
		return UriUtils.encode(uriInfo.getAbsolutePath());
	}


	public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResource,
			final boolean encodeSubResourcePathSegment, final String rel) {
		final URI finalUri = UriUtils.generateSubResourceURI(uriInfo, subResource, encodeSubResourcePathSegment);
			
		return Link.fromUri(finalUri).rel(rel).build();
	}
	
	public static URI generateSubResourceURI(final UriInfo uriInfo, final String subResource,
			final boolean encodeSubResourcePathSegment) {
		final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
		final URI absolutePathEncoded = UriUtils.encode(uriInfo.getAbsolutePath());
		uriBuilder.path(absolutePathEncoded.toString());
		uriBuilder.path("{resourceId}");
		URI finalUri;
		
		if(encodeSubResourcePathSegment) {
			finalUri = uriBuilder.build(UriComponent.encode(subResource, UriComponent.Type.PATH_SEGMENT));
		} else {
			finalUri = uriBuilder.build(subResource);
		}
		
		return finalUri;
	}
	

	private UriUtils() {
		throw new UnsupportedOperationException();
	}
}
