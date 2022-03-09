package org.opentosca.container.core.common.uri;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.glassfish.jersey.uri.UriComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UriUtil {

    private static final Logger logger = LoggerFactory.getLogger(UriUtil.class);

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
        URI result = uriBuilder.build();
        logger.debug("URL after encoding:  {}", result.toString());
        return result;
    }

    public static String encodePathSegment(final String pathSegment) {
        return URLEncoder.encode(pathSegment, StandardCharsets.UTF_8);
    }

    public static Link generateSelfLink(final UriInfo uriInfo) {
        return Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build();
    }

    public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResource,
                                               final boolean encodeSubResourcePathSegment, final String rel) {
        final URI finalUri = UriUtil.generateSubResourceURI(uriInfo, subResource, encodeSubResourcePathSegment);

        return Link.fromUri(finalUri).rel(rel).build();
    }

    public static URI generateSubResourceURI(final UriInfo uriInfo, final String subResource,
                                             final boolean encodeSubResourcePathSegment) {
        logger.debug("Generating sub resource URI for sub resource: {} with encoding flag: {}", subResource, encodeSubResourcePathSegment);
        final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
        uriBuilder.path(uriInfo.getAbsolutePath().toString());
        uriBuilder.path("{resourceId}");
        URI finalUri;

        if (encodeSubResourcePathSegment) {
            finalUri = uriBuilder.build(subResource);
        } else {
            finalUri = uriBuilder.buildFromEncoded(subResource);
        }
        logger.debug("Final URI: {}", finalUri);

        return finalUri;
    }
}
