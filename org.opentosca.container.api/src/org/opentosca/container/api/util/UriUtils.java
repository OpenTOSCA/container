package org.opentosca.container.api.util;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
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
		// Interate over path segments and encode it if necessary
		for (final PathSegment ps : pathSegments) {
			uriBuilder.path(UriComponent.encode(ps.toString(), UriComponent.Type.PATH_SEGMENT));
		}
		logger.debug("URL before encoding: {}", uri);
		logger.debug("URL after encoding:  {}", uriBuilder);
		return URI.create(uriBuilder.toString());
	}
	
	private UriUtils() {
		throw new UnsupportedOperationException();
	}
}
