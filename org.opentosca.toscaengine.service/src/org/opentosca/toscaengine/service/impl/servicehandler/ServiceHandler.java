package org.opentosca.toscaengine.service.impl.servicehandler;

import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceHandler {
	
	public static ICoreFileService coreFileService = null;
	public static IXMLSerializerService xmlSerializerService = null;
	public static IFileAccessService fileAccessService = null;
	
	private Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);
	
	
	protected void bindICoreFileService(ICoreFileService service) {
		if (service == null) {
			this.LOG.error("Service ICoreFileService is null.");
		} else {
			this.LOG.debug("Bind of the ICoreFileService.");
			ServiceHandler.coreFileService = service;
		}
	}
	
	protected void unbindICoreFileService(ICoreFileService service) {
		this.LOG.debug("Unbind of the ICoreFileService.");
		ServiceHandler.coreFileService = null;
	}
	
	protected void bindIXMLSerializerService(IXMLSerializerService service) {
		if (service == null) {
			this.LOG.error("Service IXMLSerializerService is null.");
		} else {
			this.LOG.debug("Bind of the IXMLSerializerService.");
			ServiceHandler.xmlSerializerService = service;
		}
	}
	
	protected void unbindIXMLSerializerService(IXMLSerializerService service) {
		this.LOG.debug("Unbind of the IXMLSerializerService.");
		ServiceHandler.xmlSerializerService = null;
	}
	
	protected void bindIFileAccessService(IFileAccessService service) {
		if (service == null) {
			this.LOG.error("Service IFileAccessService is null.");
		} else {
			this.LOG.debug("Bind of the IFileAccessService.");
			ServiceHandler.fileAccessService = service;
		}
	}
	
	protected void unbindIFileAccessService(IFileAccessService service) {
		this.LOG.debug("Unbind of the IFileAccessService.");
		ServiceHandler.fileAccessService = null;
	}
}
