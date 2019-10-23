package org.opentosca.container.core.engine.xml.impl;

import java.io.File;
import java.net.URL;

import org.opentosca.container.core.engine.xml.IXMLSerializer;
import org.opentosca.container.core.engine.xml.IXMLSerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * A ObjectFactory class for creating instances of the Serializer.
 * <p>
 * This class is used by org.opentosca.toscaengine.service.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class XMLSerializerFactory implements IXMLSerializerFactory {

  final private static Logger LOG = LoggerFactory.getLogger(XMLSerializer.class);


  public XMLSerializerFactory() {
    XMLSerializerFactory.LOG.debug("Initialize the SerializerFactory.");
  }

  /**
   * Creates a new instance of the XML Serializer of the TOSCA universe for a passed class which marks
   * the context and a File object which represents the TOSCA Schema.
   *
   * @param classContext A JAXB class which marks the context in which the Serializer is instantiated.
   * @param schemaFile   A File object which provides the Schema file of TOSCA.
   * @return An instance of the XML Serializer of the TOSCA universe.
   */
  @Override
  public IXMLSerializer createSerializer(final Class<?> classContext, final URL schemaFile) {
    LOG.debug("Create a new ISerializer.");
    return new XMLSerializer(classContext, schemaFile);
  }
}
