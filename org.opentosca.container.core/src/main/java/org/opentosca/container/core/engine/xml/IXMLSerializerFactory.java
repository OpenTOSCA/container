package org.opentosca.container.core.engine.xml;

import java.net.URL;

public interface IXMLSerializerFactory {

  /**
   * Creates a new instance of the XML Serializer of the TOSCA universe for a passed class which marks
   * the context and a File object which represents the TOSCA Schema.
   *
   * @param classContext A JAXB class which marks the context in which the Serializer is instantiated.
   * @param schemaFile   A File object which provides the Schema file of TOSCA.
   * @return An instance of the XML Serializer of the TOSCA universe.
   */
  public abstract IXMLSerializer createSerializer(Class<?> classContext, URL schemaFile);

}
