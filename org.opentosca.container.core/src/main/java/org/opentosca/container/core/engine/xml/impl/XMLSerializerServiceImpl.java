package org.opentosca.container.core.engine.xml.impl;

import java.net.URL;

import org.eclipse.winery.model.tosca.Definitions;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opentosca.container.core.engine.xml.IXMLSerializer;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Implementation of the interface org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * @see org.opentosca.container.core.engine.xml.IXMLSerializerService
 */
@Service
@NonNullByDefault
class XMLSerializerServiceImpl implements IXMLSerializerService {

    private final IXMLSerializer xmlSerializer;

    private final Logger LOG = LoggerFactory.getLogger(XMLSerializerServiceImpl.class);

    public XMLSerializerServiceImpl() {
        this.LOG.debug("Caching a new XMLSerializer.");
        final URL schemaFile = getClass().getClassLoader().getResource("TOSCA-v1.0.xsd");

        // this boolean is for preventing a unused warning and to get
        // the Serialization working with validation easily if needed
        final boolean trueForCreateValidation = false;
        if (trueForCreateValidation) {
            this.LOG.debug("Create TOSCA XML Serialization with schema validation.");
            this.xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, schemaFile);
        } else {
            this.LOG.debug("Create TOSCA XML Serialization without schema validation.");
            this.xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, null);
        }
        this.xmlSerializer.setValidation(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean
    public IXMLSerializer getXmlSerializer() {
        return this.xmlSerializer;
    }
}
