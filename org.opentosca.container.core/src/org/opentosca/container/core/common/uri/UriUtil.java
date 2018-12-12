package org.opentosca.container.core.common.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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

    private static Logger logger = LoggerFactory.getLogger(UriUtil.class);

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

    public static String encodePathSegment(final String pathSegment) {
        try {
            return URLEncoder.encode(pathSegment, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Link generateSelfLink(final UriInfo uriInfo) {
        return Link.fromUri(UriUtil.generateSelfURI(uriInfo)).rel("self").build();
    }

    public static URI generateSelfURI(final UriInfo uriInfo) {
        return UriUtil.encode(uriInfo.getAbsolutePath());
    }


    public static Link generateSubResourceLink(final UriInfo uriInfo, final String subResource,
                                               final boolean encodeSubResourcePathSegment, final String rel) {
        final URI finalUri = UriUtil.generateSubResourceURI(uriInfo, subResource, encodeSubResourcePathSegment);

        return Link.fromUri(finalUri).rel(rel).build();
    }

    public static URI generateSubResourceURI(final UriInfo uriInfo, final String subResource,
                                             final boolean encodeSubResourcePathSegment) {
        final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
        final URI absolutePathEncoded = UriUtil.encode(uriInfo.getAbsolutePath());
        uriBuilder.path(absolutePathEncoded.toString());
        uriBuilder.path("{resourceId}");
        URI finalUri;

        if (encodeSubResourcePathSegment) {
            finalUri = uriBuilder.build(UriComponent.encode(subResource, UriComponent.Type.PATH_SEGMENT));
        } else {
            finalUri = uriBuilder.build(subResource);
        }

        return finalUri;
    }

}
