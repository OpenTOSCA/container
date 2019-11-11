package org.opentosca.bus.management.service.impl.util;

import java.net.URI;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
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
     * @param serviceInstanceID
     * @return the ServiceTemplateInstanceId if the retrieval is successful, <code>Long.MIN_VALUE</code>
     *         otherwise
     */
    public static long determineServiceTemplateInstanceId(final URI serviceInstanceID) {
        if (Objects.nonNull(serviceInstanceID)) {
            try {
                if (serviceInstanceID.toString().contains("/")) {
                    return Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));
                } else {
                    return Long.parseLong(serviceInstanceID.toString());
                }
            }
            catch (final NumberFormatException e) {
                LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID: {}", serviceInstanceID);
            }
        } else {
            LOG.error("Unable to parse ServiceTemplateInstance ID out of serviceInstanceID because it is null!");
        }
        return Long.MIN_VALUE;
    }

    /**
     * Checks if a certain property was specified in the Tosca.xml of the ArtifactTemplate and returns
     * it if so.
     *
     * @param csarID the ID of the CSAR which contains the ArtifactTemplate
     * @param artifactTemplateID the ID of the ArtifactTemplate
     * @param propertyName the name of the property
     * @return the property value if specified, null otherwise
     */
    public static String getProperty(final CSARID csarID, final QName artifactTemplateID, final String propertyName) {
        final Document properties =
            ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactTemplateID);

        // check if there are specified properties at all
        if (properties != null) {

            final NodeList list = properties.getFirstChild().getChildNodes();

            // iterate through properties and check name
            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);

                final String localName = propNode.getLocalName();

                if (localName != null && localName.equals(propertyName)) {
                    return propNode.getTextContent().trim();
                }
            }
        }
        return null;
    }

    /**
     * Checks if a PortType property was specified in the Tosca.xml of the ArtifactTemplate and returns
     * it if so.
     *
     * @param csarID the ID of the CSAR which contains the ArtifactTemplate
     * @param artifactTemplateID the ID of the ArtifactTemplate
     * @return the PortType property value as QName if specified, null otherwise
     */
    public static QName getPortTypeQName(final CSARID csarID, final QName artifactTemplateID) {
        QName portType = null;
        try {
            portType = QName.valueOf(getProperty(csarID, artifactTemplateID, "PortType"));
            LOG.debug("PortType property: {}", portType.toString());
            return portType;
        }
        catch (final IllegalArgumentException e) {
            LOG.warn("PortType property can not be parsed to QName.");
        }
        return null;
    }
}
