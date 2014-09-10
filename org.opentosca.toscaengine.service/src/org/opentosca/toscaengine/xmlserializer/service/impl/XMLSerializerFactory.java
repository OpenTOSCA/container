package org.opentosca.toscaengine.xmlserializer.service.impl;

import java.io.File;

import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ObjectFactory class for creating instances of the Serializer.
 * 
 * This class is used by org.opentosca.toscaengine.service.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class XMLSerializerFactory implements IXMLSerializerFactory {
	
	final private static Logger LOG = LoggerFactory.getLogger(XMLSerializer.class);
	
	
	public XMLSerializerFactory() {
		XMLSerializerFactory.LOG.debug("Initialize the SerializerFactory.");
	}
	
	/**
	 * Creates a new instance of the XML Serializer of the TOSCA universe for a
	 * passed class which marks the context and a File object which represents
	 * the TOSCA Schema.
	 * 
	 * @param classContext A JAXB class which marks the context in which the
	 *            Serializer is instantiated.
	 * @param schemaFile A File object which provides the Schema file of TOSCA.
	 * @return An instance of the XML Serializer of the TOSCA universe.
	 */
	@Override
	public IXMLSerializer createSerializer(Class<?> classContext, File schemaFile) {
		XMLSerializerFactory.LOG.debug("Create a new ISerializer.");
		return new XMLSerializer(classContext, schemaFile);
	}
}
