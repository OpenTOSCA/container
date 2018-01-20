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

//	public static Link generateGroupSubResourceLink(final UriInfo uriInfo, final String subResourceName) {
//		return Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(subResourceName).build()))
//				.rel(subResourceName).build();
//	}
//
//	public static Link generateSubResourceSelfLink(final UriInfo uriInfo, final String subResourceId) {
////		return Link.fromUri(uriInfo.getAbsolutePathBuilder().path("{resourceId}")
////				.build(UriComponent.encode(subResourceId, UriComponent.Type.PATH_SEGMENT))).rel("self").build();
//		
//		return UriUtils.generateSubResourceLink(uriInfo, subResourceId, true, "self");
//
//	}
//
//	public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResourceId) {
////		return Link
////				.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("{resourceId}")
////						.build(UriComponent.encode(subResourceId, UriComponent.Type.PATH_SEGMENT))))
////				.rel(subResourceId).build();
//		
//		return UriUtils.generateSubResourceLink(uriInfo, subResourceId, true, subResourceId);
//	}

	public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResource,
			final boolean encodeSubResourcePathSegment, final String rel) {
		final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
		final URI absolutePathEncoded = UriUtils.encode(uriInfo.getAbsolutePath());
		uriBuilder.path(absolutePathEncoded.toString());
		//logger.debug("uriBuilder after adding the encoded absolutePath {}", uriBuilder.toString());
		uriBuilder.path("{resourceId}");
		URI finalUri;
		
		if(encodeSubResourcePathSegment) {
			finalUri = uriBuilder.build(UriComponent.encode(subResource, UriComponent.Type.PATH_SEGMENT));
		} else {
			finalUri = uriBuilder.build(subResource);
		}
		
		//logger.debug("uriBuilder result uri {}", finalUri);
		
		return Link.fromUri(finalUri).rel(rel).build();
	}

	private UriUtils() {
		throw new UnsupportedOperationException();
	}
}
