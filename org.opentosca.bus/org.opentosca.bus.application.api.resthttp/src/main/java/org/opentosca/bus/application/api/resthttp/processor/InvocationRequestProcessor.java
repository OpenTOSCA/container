package org.opentosca.bus.application.api.resthttp.processor;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.application.api.resthttp.route.Route;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * InvocationRequestProcessor of the Application Bus-REST/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles "invokeOperation" requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class InvocationRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);

    @Override
    public void process(final Exchange exchange) throws ParseException, ApplicationBusExternalException, SAXException {

        String nodeTemplateID = null;
        Integer nodeInstanceID = null;
        Integer serviceInstanceID = null;
        String interfaceName = null;
        String operationName = null;
        LinkedHashMap<String, Object> params = null;

        InvocationRequestProcessor.LOG.debug("Processing Invocation request...");

        final Message message = exchange.getIn();

        serviceInstanceID = message.getHeader(Route.SI, Integer.class);
        InvocationRequestProcessor.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
        exchange.getIn().setHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(), serviceInstanceID);

        nodeInstanceID = message.getHeader(Route.NI, Integer.class);
        InvocationRequestProcessor.LOG.debug("NodeInstanceID: {}", nodeInstanceID);
        exchange.getIn().setHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), nodeInstanceID);

        nodeTemplateID = message.getHeader(Route.NT, String.class);
        InvocationRequestProcessor.LOG.debug("NodeTemplateID: {}", nodeTemplateID);
        exchange.getIn().setHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), nodeTemplateID);

        interfaceName = message.getHeader(Route.IN, String.class);
        InvocationRequestProcessor.LOG.debug("Interface: {}", interfaceName);
        exchange.getIn().setHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), interfaceName);

        operationName = message.getHeader(Route.ON, String.class);
        InvocationRequestProcessor.LOG.debug("Operation: {}", operationName);
        exchange.getIn().setHeader(ApplicationBusConstants.OPERATION_NAME.toString(), operationName);

        final Form httpHeaders = (Form) exchange.getIn().getHeader("org.restlet.http.headers");
        final String contentType = httpHeaders.getValues("Content-Type").toString();

        InvocationRequestProcessor.LOG.debug("Content-Type: {}", contentType);

        final String bodyString = message.getBody(String.class);

        if (bodyString != null) {

            if (contentType != null && contentType.equals(MediaType.APPLICATION_JSON.getName())) {

                params = jsonStringToMap(bodyString);
            } else if (contentType != null && contentType.equals(MediaType.APPLICATION_XML.getName())) {

                params = xmlStringToMap(bodyString);
            } else {
                InvocationRequestProcessor.LOG.warn("The request entity media type is not supported. Supported types are {} and {}",
                    MediaType.APPLICATION_JSON.getName(),
                    MediaType.APPLICATION_XML.getName());
                throw new ApplicationBusExternalException(
                    "The request entity media type is not supported. Supported types are "
                        + MediaType.APPLICATION_JSON.getName() + " and " + MediaType.APPLICATION_XML.getName(),
                    Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode());
            }
        }

        exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
            ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

        exchange.getIn().setBody(params);
    }

    /**
     * Parses and maps a json String to a {@literal LinkedHashMap<String, Object>}.
     *
     * @return LinkedHashMap
     */
    private LinkedHashMap<String, Object> jsonStringToMap(final String jsonString) throws ParseException {

        final ContainerFactory orderedKeyFactory = new ContainerFactory() {
            @Override
            public LinkedHashMap<String, Object> createObjectContainer() {
                return new LinkedHashMap<>();
            }

            @Override
            public List<?> creatArrayContainer() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        final JSONParser parser = new JSONParser();

        final Object obj = parser.parse(jsonString, orderedKeyFactory);

        return (LinkedHashMap<String, Object>) obj;
    }

    private LinkedHashMap<String, Object> xmlStringToMap(final String xmlString) throws SAXException {

        final LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        final Document xml = convertStringToDocument(xmlString);
        final Node parent = xml.getFirstChild();
        final NodeList childs = parent.getChildNodes();
        Node child;
        for (int i = 0; i < childs.getLength(); i++) {
            child = childs.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                params.put(child.getNodeName(), child.getTextContent());
            }
        }

        return params;
    }

    private static Document convertStringToDocument(final String xmlString) throws SAXException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
