package org.opentosca.bus.management.service.impl.util;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.container.core.engine.ToscaEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util {

    private final static Logger LOG = LoggerFactory.getLogger(Util.class);

    /**
     * Determine the ServiceTemplateInstanceId long from the ServiceInstanceId QName.
     *
     * @return the ServiceTemplateInstanceId if the retrieval is successful, <code>Long.MIN_VALUE</code> otherwise
     */
    public static long determineServiceTemplateInstanceId(final URI serviceInstanceID) {
        if (Objects.nonNull(serviceInstanceID)) {
            try {
                if (serviceInstanceID.toString().contains("/")) {
                    return Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));
                } else {
                    return Long.parseLong(serviceInstanceID.toString());
                }
            } catch (final NumberFormatException e) {
                LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID: {}", serviceInstanceID);
            }
        } else {
            LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID because it is null!");
        }
        return Long.MIN_VALUE;
    }

    /**
     * Checks if a certain property was specified in the Tosca.xml of the ArtifactTemplate and returns it if so.
     *
     * @param artifactTemplate the ID of the ArtifactTemplate
     * @param propertyName     the name of the property
     * @return the property value if specified, null otherwise
     */
    public static String getProperty(final TArtifactTemplate artifactTemplate, final String propertyName) {
        final Document properties = ToscaEngine.getEntityTemplateProperties(artifactTemplate);
        // check if there are specified properties at all
        if (properties == null) {
            return null;
        }

        final NodeList list = properties.getFirstChild().getChildNodes();
        // iterate through properties and check name
        for (int i = 0; i < list.getLength(); i++) {
            final Node propNode = list.item(i);
            final String localName = propNode.getLocalName();
            if (localName != null && localName.equals(propertyName)) {
                return propNode.getTextContent().trim();
            }
        }
        return null;
    }

    /**
     * Checks if a PortType property was specified in the Tosca.xml of the ArtifactTemplate and returns it if so.
     *
     * @param artifactTemplate the ArtifactTemplate
     * @return the PortType property value as QName if specified, null otherwise
     */
    public static QName getPortTypeQName(final TArtifactTemplate artifactTemplate) {
        try {
            QName portType = QName.valueOf(getProperty(artifactTemplate, "PortType"));
            LOG.debug("PortType property: {}", portType.toString());
            return portType;
        } catch (final IllegalArgumentException e) {
            LOG.warn("PortType property can not be parsed to QName.");
        }
        return null;
    }

    /**
     * Get the endpoints of all choreography partners from the ServiceTemplate.
     *
     * @param serviceTemplate the ServiceTemplate for the choreography
     * @return a list of tags containing the partner name as key and the endpoints as value or
     * <code>null</code> if no tags are defined on the ServiceTemplate
     */
    public static List<TTag> getPartnerEndpoints(final TServiceTemplate serviceTemplate) {

        // get the tags containing the enpoints of the partners
        if (Objects.isNull(serviceTemplate.getTags())) {
            LOG.error("Unable to retrieve tags for ServiceTemplate with ID {}.", serviceTemplate.getId());
            return null;
        }
        final List<TTag> tags = serviceTemplate.getTags().getTag();

        // get the provider names defined in the NodeTemplates to check which tag names specify a partner
        // endpoint
        final List<String> partnerNames =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
                .filter(entity -> entity instanceof TNodeTemplate).map(entity -> entity.getOtherAttributes())
                .map(attributes -> attributes.get(Constants.LOCATION_ATTRIBUTE)).distinct()
                .collect(Collectors.toList());

        // remove tags that do not specify a partner endpoint and get endpoints
        tags.removeIf(tag -> !partnerNames.contains(tag.getName()));
        return tags;
    }
}
