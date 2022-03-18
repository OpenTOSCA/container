package org.opentosca.container.api.service;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.next.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public abstract class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final DocumentConverter converter = new DocumentConverter();

    /**
     * Converts an xml document to an xml-based property sui/table for service or node template instances
     */
    public static <T extends Property> T convertDocumentToProperty(final Document propertyDoc,
                                                            final Class<T> type) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        if (propertyDoc == null) {
            final String msg =
                String.format("The set of parameters of an instance of type %s cannot be null", type.getName());
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        final String propertyAsString = converter.convertToDatabaseColumn(propertyDoc);
        final T property = type.newInstance();
        property.setName("xml");
        property.setType("xml");
        property.setValue(propertyAsString);

        return property;
    }
}
