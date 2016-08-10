package org.opentosca.toscaengine.xmlserializer.service.impl;

import java.io.File;

import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.IToscaModelFactory;
import org.opentosca.model.tosca.ObjectFactory;
import org.opentosca.toscaengine.service.impl.ToscaEngineServiceImpl;
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
	
	private Logger LOG = LoggerFactory.getLogger(ToscaEngineServiceImpl.class);
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IXMLSerializer getXmlSerializer() {
		if (this.xmlSerializer == null) {
			this.LOG.error("There is no XMLSerializer initiated yet.");
		}
		return this.xmlSerializer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IToscaModelFactory getToscaModelFactory() {
		if (this.toscaModelFactory == null) {
			this.toscaModelFactory = new ObjectFactory();
		}
		return this.toscaModelFactory;
	}
	
	public void bindIFileAccessService(IFileAccessService service) {
		if (service == null) {
			this.LOG.error("Service IFileAccessService is null.");
		} else {
			this.LOG.debug("Bind of the IFileAccessService.");
			this.fileAccessService = service;
			
			if (this.xmlSerializer == null) {
				
				this.LOG.debug("Create a new XMLSerializer.");
				File schemaFile = this.fileAccessService.getOpenToscaSchemaFile();
				
				// this boolean is for preventing a unused warning and to get
				// the Serialization working with validation easily if needed
				boolean trueForCreateValidation = false;
				if (trueForCreateValidation) {
					this.LOG.debug("Create TOSCA XML Serialization with schema validation.");
					this.xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, schemaFile);
				} else {
					this.LOG.debug("Create TOSCA XML Serialization without schema validation.");
					this.xmlSerializer = new XMLSerializerFactory().createSerializer(Definitions.class, null);
				}
				this.xmlSerializer.setValidation(true);
			}
		}
	}
	
	public void unbindIFileAccessService(IFileAccessService service) {
		this.LOG.debug("Unbind of the IFileAccessService.");
		this.fileAccessService = null;
	}
	
}
