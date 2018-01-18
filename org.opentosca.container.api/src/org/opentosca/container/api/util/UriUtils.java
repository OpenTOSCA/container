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
		return Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build();
	}

	public static Link generateGroupSubResourceLink(final UriInfo uriInfo, final String subResourceName) {
		return Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(subResourceName).build()))
				.rel(subResourceName).build();
	}

	public static Link generateSubResourceSelfLink(final UriInfo uriInfo, final String subResourceId) {
		return Link
				.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("{resourceId}")
						.build(UriComponent.encode(subResourceId, UriComponent.Type.PATH_SEGMENT))))
				.rel("self").build();
	}
	
	public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResourceId) {
		return Link
				.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("{resourceId}")
						.build(UriComponent.encode(subResourceId, UriComponent.Type.PATH_SEGMENT))))
				.rel(subResourceId).build();
	}

	private UriUtils() {
		throw new UnsupportedOperationException();
	}
}
