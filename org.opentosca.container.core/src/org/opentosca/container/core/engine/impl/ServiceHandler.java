package org.opentosca.container.core.engine.impl;

import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceHandler {

    public static ICoreFileService coreFileService = null;
    public static IXMLSerializerService xmlSerializerService = null;
    public static IFileAccessService fileAccessService = null;

    private final Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);


    protected void bindICoreFileService(final ICoreFileService service) {
        if (service == null) {
            this.LOG.error("Service ICoreFileService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreFileService.");
            ServiceHandler.coreFileService = service;
        }
    }

    protected void unbindICoreFileService(final ICoreFileService service) {
        this.LOG.debug("Unbind of the ICoreFileService.");
        ServiceHandler.coreFileService = null;
    }

    protected void bindIXMLSerializerService(final IXMLSerializerService service) {
        if (service == null) {
            this.LOG.error("Service IXMLSerializerService is null.");
        } else {
            this.LOG.debug("Bind of the IXMLSerializerService.");
            ServiceHandler.xmlSerializerService = service;
        }
    }

    protected void unbindIXMLSerializerService(final IXMLSerializerService service) {
        this.LOG.debug("Unbind of the IXMLSerializerService.");
        ServiceHandler.xmlSerializerService = null;
    }

    protected void bindIFileAccessService(final IFileAccessService service) {
        if (service == null) {
            this.LOG.error("Service IFileAccessService is null.");
        } else {
            this.LOG.debug("Bind of the IFileAccessService.");
            ServiceHandler.fileAccessService = service;
        }
    }

    protected void unbindIFileAccessService(final IFileAccessService service) {
        this.LOG.debug("Unbind of the IFileAccessService.");
        ServiceHandler.fileAccessService = null;
    }
}
