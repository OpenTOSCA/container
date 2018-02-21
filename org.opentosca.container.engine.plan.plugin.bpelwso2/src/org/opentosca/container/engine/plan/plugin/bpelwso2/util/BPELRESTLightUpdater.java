package org.opentosca.container.engine.plan.plugin.bpelwso2.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class provides functionality for updating BPEL4RESTLight Elements inside a WSBPEL 2.0 file.
 *
 * BPEL4RESTLight elements are extension activities defined by the ODE Extension BPEL4RESTLight
 * (see: OpenTOSCA/trunk/plans/odeextensions). These must have URI attribute which represent
 * endpoints for http requests but maybe have to be updated.
 *
 * This class maps BPEL4RESTLight XML elements into BPELRESTLightElement objects and operates on
 * them for the updates.
 *
 * Rules for update:
 * <li>1. The uri is absolute -> no update. This is because we need to have the ability to use URIs
 * like amazon.com
 * <li>2. The uri is relative -> update. Relative URIs are updated if an endpoint exists in the
 * openTOSCA Core for the referenced HTTP service.
 * <li>3. The uri is a $bpelvar[varname] construct -> no update.
 * <li>4. The uri is a mix of 1 and 3 or 2 and 3. The class tries to cut out bpelVar part and checks
 * if URI is a URI in first place and if it is relative or absolute, then the above rules are tried
 * to apply
 *
 * @See org.opentosca.planengine.plugin.bpelwso2.util.BPEL4RESTLightElement
 * @See org.opentosca.planengine.plugin.bpelwso2.util.BPEL4RESTLightElementType
 */
public class BPELRESTLightUpdater {

    private static ICoreEndpointService endpointService;
    private static ICoreEndpointService oldEndpointService;
    private final DocumentBuilder builder;
    private final DocumentBuilderFactory domFactory;
    private final XPathFactory factory;
    private final TransformerFactory transformerFactory;
    private final Transformer transformer;
    private CSARID csarId;

    private final static Logger LOG = LoggerFactory.getLogger(BPELRESTLightUpdater.class);


    public BPELRESTLightUpdater() throws ParserConfigurationException, TransformerConfigurationException {
        // initialize parsers
        this.domFactory = DocumentBuilderFactory.newInstance();
        this.domFactory.setNamespaceAware(true);
        this.builder = this.domFactory.newDocumentBuilder();
        this.factory = XPathFactory.newInstance();
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformer = this.transformerFactory.newTransformer();
    }

    /**
     * <p>
     * Changes endpoints (URIs) inside the given BPEL file. If the bpel file contains somewhere the tag
     * {OPENTOSCA} the tag will be replaced by the actual host of the container
     * </p>
     * <p>
     * If the BPEL file contains elements of BPEL4RESTLight (GET, PUT, POST, DELETE) it will check if
     * the given URIs are services contained in the endpoint service and change them accordingly
     * </p>
     *
     *
     * @param processFiles a list of files containing the complete content of a Apache ODE WS-BPEL 2.0
     *        zip file
     * @param csarId a identifier of the CSAR this BPEL file belongs to
     * @return true only if some change was made
     * @throws IOException is thrown when access of BPEL file failed
     * @throws SAXException is thrown when parsing of BPEL file failed
     */
    public boolean changeEndpoints(final List<File> processFiles, final CSARID csarId)
        throws IOException, SAXException {
        this.csarId = csarId;
        final File bpelFile = this.getBPELFile(processFiles);

        if (bpelFile == null) {
            BPELRESTLightUpdater.LOG.debug("No bpel file found");
            return false;
        }

        BPELRESTLightUpdater.LOG.debug("Parsing bpel file {} ", bpelFile.getAbsoluteFile());
        final Document document = this.builder.parse(bpelFile);

        // get the elements
        final List<BPELRESTLightElement> elements = this.getAllBPELRESTLightElements(document);

        final Set<URI> localURIs = this.getRESTURI(elements);
        final Set<BPELRESTLightElement> notChanged = new HashSet<>();

        if (BPELRESTLightUpdater.endpointService != null) {
            for (final URI localUri : localURIs) {
                for (final RESTEndpoint endpoint : BPELRESTLightUpdater.endpointService.getRestEndpoints(localUri,
                    this.csarId)) {
                    notChanged.addAll(this.changeAddress(endpoint, elements));
                }
            }
        } else {
            BPELRESTLightUpdater.LOG.warn("No EndpointService available");
        }

        if (notChanged.isEmpty()) {
            final DOMSource source = new DOMSource(document);
            final StreamResult result = new StreamResult(bpelFile);
            boolean wroteFile = false;
            try {
                this.transformer.transform(source, result);
                wroteFile = true;
            } catch (final TransformerException e) {
                wroteFile = false;
            }

            return wroteFile;
        } else {
            // log couldn't change all uris inside bpel --> isn't valid anymore
            BPELRESTLightUpdater.LOG.warn("Chouldn't change all URIs in bpel file");
            for (final BPELRESTLightElement element : notChanged) {
                BPELRESTLightUpdater.LOG.warn("Could'nt change address in element {}", element.toString());
            }

            // just return true
            return true;
        }
    }

    /**
     * Retrieves all BPEL4RESTLight elements in the given DOM document.
     *
     * @param document the DOM document to look in
     * @return a List of BPELRESTLightElements
     */
    public List<BPELRESTLightElement> getAllBPELRESTLightElements(final Document document) {
        BPELRESTLightUpdater.LOG.debug("Retrieving all BPEL4RESTLight elements");
        BPELRESTLightUpdater.LOG.debug("Retrieving PUT elements");
        final List<BPELRESTLightElement> elements = this.getBPELRESTLightElements(BPELRESTLightElementType.PUT,
            document);
        BPELRESTLightUpdater.LOG.debug("Retrieving POST elements");
        elements.addAll(this.getBPELRESTLightElements(BPELRESTLightElementType.POST, document));
        BPELRESTLightUpdater.LOG.debug("Retrieving GET elements");
        elements.addAll(this.getBPELRESTLightElements(BPELRESTLightElementType.GET, document));
        BPELRESTLightUpdater.LOG.debug("Retrieving DELETE elements");
        elements.addAll(this.getBPELRESTLightElements(BPELRESTLightElementType.DELETE, document));
        return elements;
    }

    /**
     * Retrieves all BPEL4RESTLight elements of the given type (GET,PUT,POST,DELETE)
     *
     * @param type BPELRESTLightElementType to parse for
     * @param document the DOM document to look in
     * @return a List containing all elements of the given type
     */
    private List<BPELRESTLightElement> getBPELRESTLightElements(final BPELRESTLightElementType type,
                    final Document document) {
        String xpathExp = "";
        // using straight forward xpath expressions
        // TODO do with namespace check
        switch (type) {
            case PUT:
                xpathExp = "//*[local-name()='PUT']";
                break;
            case POST:
                xpathExp = "//*[local-name()='POST']";
                break;
            case DELETE:
                xpathExp = "//*[local-name()='DELETE']";
                break;
            case GET:
                xpathExp = "//*[local-name()='GET']";
                break;
        }
        final List<BPELRESTLightElement> elements = new LinkedList<>();
        NodeList result = null;

        final XPath xpath = this.factory.newXPath();
        XPathExpression expr;
        try {
            BPELRESTLightUpdater.LOG.debug("Querying document with {} ", xpathExp);
            expr = xpath.compile(xpathExp);
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            BPELRESTLightUpdater.LOG.error("Internal Error: XPath expression wasn't valid", e);
        }
        BPELRESTLightUpdater.LOG.debug("Found Elements: {} ", String.valueOf(result.getLength()));
        for (int i = 0; i < result.getLength(); i++) {
            // in this block the BPELRESTLightElement class assures that we
            // change only elements which should be changed.
            // the rules for this are written in the header of this class
            BPELRESTLightElement element = null;
            try {
                final Node node = result.item(i);
                element = new BPELRESTLightElement(node);
            } catch (final org.opentosca.container.engine.plan.plugin.bpelwso2.util.BPELRESTLightElement.NoBPELRESTLightElementException e) {
                BPELRESTLightUpdater.LOG.warn(e.getMessage());
            } finally {
                if (element != null) {
                    // adding element which can and should be changed to the
                    // list
                    elements.add(element);
                }
            }
        }
        return elements;
    }

    /**
     * Returns the URIs of the given BPELRESTLight elements
     *
     * @param elements a list of BPELRESTLight elements
     * @return a set of URIs
     */
    private Set<URI> getRESTURI(final List<BPELRESTLightElement> elements) {
        // set is used to achieve that endpoint fetching is more efficient
        final Set<URI> uris = new HashSet<>();
        for (final BPELRESTLightElement element : elements) {
            if (element.getURI() != null) {
                uris.add(element.getURI());
            }
        }
        return uris;
    }

    /**
     * Changes the URI in the given BPELRESTLight elements
     *
     * @param endpoint an endpoint with the uri
     * @param elements a list of BPELRESTLight elements
     * @return a list of NOT changed elements, if list is empty every element was changed
     */
    private Set<BPELRESTLightElement> changeAddress(final RESTEndpoint endpoint,
                    final List<BPELRESTLightElement> elements) {
        final List<BPELRESTLightElement> toRemove = new LinkedList<>();
        final Set<BPELRESTLightElement> notChanged = new HashSet<>();
        for (final BPELRESTLightElement element : elements) {
            // the following check is pretty unstable, if the path isn't exactly
            // the
            // same. this could really happen if the service was deployed in
            // some "nested" environment.
            // Example:
            // {serviceroot}/{somepath} are URIs of the REST service and after
            // deployment the endpoint is
            // host/someotherpath/{serviceroot}/{somepath}
            // the path method would return
            // someotherpath/{serviceroot}/{somepath}
            // which isn't equal to {serviceroot}/{somepath}
            final String endpointPath = this.normalizePath(endpoint.getURI().getPath());
            final String elementPath = this.normalizePath(element.getURI().getPath());
            if (endpointPath.equals(elementPath)) {
                try {
                    BPELRESTLightUpdater.LOG.debug("Setting address in bpel4RestLight element ");
                    if (element.setURI(endpoint.getURI())) {
                        toRemove.add(element);
                    }
                } catch (final URISyntaxException e) {
                    BPELRESTLightUpdater.LOG.debug("Setting address failed (URISyntaxException): URI {}",
                        endpoint.getURI().toString());
                }
            }
        }
        // remove changed elements
        elements.removeAll(toRemove);
        // return as set for uniqueness
        notChanged.addAll(elements);
        return notChanged;
    }

    /**
     * Adds Slashes ('/') at beginning and end of the given string
     *
     * @param path The String to modify
     * @return returns a String where String.charAt(0) == '/' and String.charAt(String.length() - 1) ==
     *         '/'
     */
    private String normalizePath(final String path) {
        String temp = path;
        if (path.charAt(0) != '/') {
            temp = "/" + path;
        }
        if (path.charAt(path.length() - 1) != '/') {
            temp = temp + "/";
        }
        return temp;
    }

    /**
     * Looks for the first BPEL file it finds in the given list
     *
     * @param files a list of files
     * @return file which ends with .bpel, else null
     */
    private File getBPELFile(final List<File> files) {
        for (final File file : files) {
            final int pos = file.getName().lastIndexOf('.');
            if (pos > 0 && pos < file.getName().length() - 1) {
                if (file.getName().substring(pos + 1).equals("bpel")) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Bind method for EndpointService
     *
     * @param endpointService the EndpointService to bind
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService != null) {
            BPELRESTLightUpdater.LOG.debug("Registering EndpointService {}", endpointService.toString());
            if (BPELRESTLightUpdater.endpointService == null) {
                BPELRESTLightUpdater.endpointService = endpointService;
            } else {
                BPELRESTLightUpdater.oldEndpointService = endpointService;
                BPELRESTLightUpdater.endpointService = endpointService;
            }
            BPELRESTLightUpdater.LOG.debug("Registered EndpointService {}", endpointService.toString());
        }
    }

    /**
     * Unbind method for EndpointService
     *
     * @param endpointService the EndpointService to unbind
     */
    public void unbindEndpointService(final ICoreEndpointService endpointService) {
        BPELRESTLightUpdater.LOG.debug("Unregistering EndpointService {}", endpointService.toString());
        if (BPELRESTLightUpdater.oldEndpointService == null) {
            BPELRESTLightUpdater.endpointService = null;
        } else {
            BPELRESTLightUpdater.oldEndpointService = null;
        }
        BPELRESTLightUpdater.LOG.debug("Unregistered EndpointService {}", endpointService.toString());
    }
}
