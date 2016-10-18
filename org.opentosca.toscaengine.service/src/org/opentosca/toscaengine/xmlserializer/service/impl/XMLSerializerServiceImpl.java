package org.opentosca.toscaengine.xmlserializer.service.impl;

import java.io.File;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.IToscaModelFactory;
import org.opentosca.model.tosca.ObjectFactory;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the interface
 * org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService
 * 
 * @see org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class XMLSerializerServiceImpl implements IXMLSerializerService {
	
	private IXMLSerializer xmlSerializer;
	private IToscaModelFactory toscaModelFactory;
	
	private IFileAccessService fileAccessService = null;
	
	private Logger LOG = LoggerFactory.getLogger(XMLSerializerServiceImpl.class);
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IXMLSerializer getXmlSerializer() {
		if (xmlSerializer == null) {
			LOG.error("There is no XMLSerializer initiated yet.");
		}
		return xmlSerializer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IToscaModelFactory getToscaModelFactory() {
		if (toscaModelFactory == null) {
			toscaModelFactory = new ObjectFactory();
		}
		return toscaModelFactory;
	}
	
	public void bindIFileAccessService(IFileAccessService service) {
		if (service == null) {
			LOG.error("Service IFileAccessService is null.");
		} else {
			LOG.debug("Bind of the IFileAccessService.");
			fileAccessService = service;
			
			if (xmlSerializer == null) {
				
				LOG.debug("Create a new XMLSerializer.");
				File schemaFile = fileAccessService.getOpenToscaSchemaFile();
				
				// this boolean is for preventing a unused warning and to get
				// the Serialization working with validation easily if needed
				boolean trueForCreateValidation = false;
				if (trueForCreateValidation) {
					LOG.debug("Create TOSCA XML Serialization with schema validation.");
					xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, schemaFile);
				} else {
					LOG.debug("Create TOSCA XML Serialization without schema validation.");
					xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, null);
				}
				xmlSerializer.setValidation(true);
			}
		}
	}
	
	public void unbindIFileAccessService(IFileAccessService service) {
		LOG.debug("Unbind of the IFileAccessService.");
		fileAccessService = null;
	}
	
}
