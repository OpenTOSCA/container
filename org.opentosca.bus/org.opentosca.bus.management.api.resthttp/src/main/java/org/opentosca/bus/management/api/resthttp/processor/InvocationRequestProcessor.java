package org.opentosca.bus.management.api.resthttp.processor;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.model.csar.CsarId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * InvocationRequestProcessor of the Management Bus REST-API.<br>
 * <br>
 * <p>
 * This processor handles "invokeOperation" requests.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
@Component
public class InvocationRequestProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(InvocationRequestProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {
        LOG.debug("Processing Invocation request...");

        final String bodyString = exchange.getIn().getBody(String.class);
        final LinkedHashMap<String, LinkedHashMap<String, String>> requestMap = this.requestToMap(bodyString);
        final LinkedHashMap<String, String> infosMap = requestMap.get("invocation-information");

        if (infosMap == null) {
            LOG.warn("Needed information not specified.");
            throw new CamelExchangeException("Needed information not specified.", exchange);
        }
        checkRequiredKeys(infosMap, "csarID", "serviceTemplateID", "interface", "operation");
        String nodeTemplateID = null;
        String relationshipTemplateID = null;
        if (infosMap.containsKey("nodeTemplateID")) {
            nodeTemplateID = infosMap.get("nodeTemplateID");
            LOG.debug("nodeTemplateID: {}", nodeTemplateID);
            exchange.getIn().setHeader(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplateID);
        }
        if (infosMap.containsKey("relationshipTemplateID")) {
            relationshipTemplateID = infosMap.get("relationshipTemplateID");
            LOG.debug("relationshipTemplateID: {}", relationshipTemplateID);
            exchange.getIn().setHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), relationshipTemplateID);
        }

        if (nodeTemplateID == null && relationshipTemplateID == null) {
            LOG.debug("Can't process request: Eighter nodeTemplateID or relationshipTemplateID is required!");
            throw new CamelExchangeException(
                "Can't process request: Eighter nodeTemplateID or relationshipTemplateID is required!", exchange);
        }

        final String csarID = infosMap.get("csarID");
        LOG.debug("csarID: {}", csarID);
        exchange.getIn().setHeader(MBHeader.CSARID.toString(), new CsarId(csarID));

        final QName serviceTemplateID = QName.valueOf(infosMap.get("serviceTemplateID"));
        LOG.debug("serviceTemplateID: {}", serviceTemplateID);
        exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

        final String interfaceName = infosMap.get("interface");
        LOG.debug("interface: {}", interfaceName);
        exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);

        if (infosMap.containsKey("serviceInstanceID")) {
            final String serviceInstanceID = infosMap.get("serviceInstanceID");
            LOG.debug("serviceInstanceID: {}", serviceInstanceID);
            if (serviceInstanceID != null) {
                final URI serviceInstanceURI = new URI(serviceInstanceID);
                exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
            }
        }

        if (infosMap.containsKey("nodeInstanceID")) {
            final String nodeInstanceID = infosMap.get("nodeInstanceID");
            LOG.debug("nodeInstanceID: {}", nodeInstanceID);
            exchange.getIn().setHeader(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);
        }

        final String operationName = infosMap.get("operation");
        LOG.debug("operationName: {}", operationName);
        exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), operationName);

        final HashMap<String, String> paramsMap = requestMap.get("params");
        if (paramsMap != null) {
            exchange.getIn().setBody(paramsMap);
            LOG.debug("Params: {}", paramsMap);
        } else {
            LOG.debug("No parameter specified.");
        }

        exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(), "org.opentosca.bus.management.api.resthttp");
    }

    private void checkRequiredKeys(Map<String, ?> parameters, String... keys) throws NotFoundException {
        Set<String> missing = Arrays.stream(keys)
            .filter(((Predicate<String>) parameters::containsKey).negate())
            .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            final String pretty = missing.stream().collect(Collectors.joining(", "));
            LOG.warn("Can not process request due to missing information. Missing key(s): {}", pretty);
            throw new NotFoundException(String.format("\"Can not process request due to missing information. Missing key(s): %s", pretty));
        }
    }

    /**
     * Parses and maps a json String to a {@literal LinkedHashMap<String, LinkedHashMap<String, String>>}.
     *
     * @return LinkedHashMap
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, LinkedHashMap<String, String>> requestToMap(final String body) throws ParseException {

        final ContainerFactory orderedKeyFactory = new ContainerFactory() {

            @Override
            public Map<String, LinkedHashMap<String, String>> createObjectContainer() {
                return new LinkedHashMap<>();
            }

            @Override
            public List<?> creatArrayContainer() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        final JSONParser parser = new JSONParser();

        final Object obj = parser.parse(body, orderedKeyFactory);

        return (LinkedHashMap<String, LinkedHashMap<String, String>>) obj;
    }
}
