package org.opentosca.container.api.legacy.resources.utilities;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.uri.UriComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utilities. Provides static methods for the ContainerApi<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class Utilities {

    private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);


    public static URI encode(final URI uri) {
        final List<PathSegment> pathSegments = UriComponent.decodePath(uri, false);
        final UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder();
        // Build base URL
        uriBuilder.scheme(uri.getScheme()).host(uri.getHost()).port(uri.getPort());
        // Interate over path segments and encode it if necessary
        for (final PathSegment ps : pathSegments) {
            uriBuilder.path(UriComponent.encode(ps.toString(), UriComponent.Type.PATH_SEGMENT));
        }
        LOG.debug("URL before encoding: {}", uri);
        LOG.debug("URL after encoding:  {}", uriBuilder);
        return URI.create(uriBuilder.toString());
    }

    /**
     * Encodes URI path according to RFC 2396. This means e.g space will be encoded to "%20" and not
     * "+".
     *
     * @param path to encode
     * @return Encoded URI path.
     */
    public static String encodeURIPath(final String path) {
        try {

            return new URI(null, null, path, null).toString();

        }
        catch (final URISyntaxException e) {
            Utilities.LOG.warn("Can't encode URI path \"{}\".", path, e);
        }

        return null;
    }

    public static String buildURI(final UriInfo uriInfo, final String path) {

        final UriBuilder builder = uriInfo.getBaseUriBuilder();
        final List<PathSegment> pathSegments = uriInfo.getPathSegments(false);

        // Interate over path segments and encode it if necessary
        for (final PathSegment ps : pathSegments) {
            final String pathEncoded = URLencode(ps.toString());
            LOG.debug("Encoding path segment <{}> to <{}>", ps, pathEncoded);
            builder.path(pathEncoded);
        }

        final URI url = builder.path(URLencode(path)).build();

        LOG.debug("Final URL: {}", url);
        return url.toString();
    }

    /**
     * Builds an URI
     *
     * @param base baseURI as String
     * @param path path to extend the URI
     * @return
     */
    public static String buildURI(final String base, final String path) {

        LOG.debug("Create URL; base=<{}>, path=<{}>", base, path);

        final UriBuilder builder = UriBuilder.fromUri(base);

        // separately encode URI path first, because builder.path(...) later
        // prevents double-encoding (we want double-encoding if given path is
        // already encoded)
        final String pathEncoded = Utilities.encodeURIPath(path);

        builder.path(pathEncoded);
        final URI uri = builder.build();
        Utilities.LOG.debug("URI built: {}", uri);

        try {
            LOG.debug("URL: {}", uri.toURL());
        }
        catch (final MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LOG.debug("Encoded URL: {}", URLencode(uri.toString()));

        return uri.toString();

    }

    public static String URLencode(final String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }

    public static String UrlDoubleEncode(final String s) {
        return URLencode(URLencode(s));
    }

    public static String URLdecode(final String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }

    public static boolean areNotNull(final Object... objs) {
        for (final Object obj : objs) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks all given string if they are <code>null</code> or empty and returns yes if at
     * <b>least one</b> String is null or empty
     *
     * This method is perfectly fitted for checking all required parameters of a request at once (jersey
     * doesn't support @required for parameters)
     *
     * @param strings
     * @return false - if all given Strings are initialized and not <code>""</code> true - if at least
     *         one given string is <code>NULL</code> or empty
     */
    public static boolean areEmpty(final String... strings) {
        for (final String string : strings) {
            if (string == null || string.isEmpty()) {
                return true;
            }
        }
        return false;

    }

    public static Document fileToDom(final File file) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(file);
        return doc;
    }

}
