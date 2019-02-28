package org.opentosca.container.core.impl.plan;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Static service handler which provides services of other OSGI components in a static way for
 * classes of this bundle.
 */
public class ServiceProxy {

    @Inject
    public static IToscaEngineService toscaEngineService = null;
    @Inject
    public static IToscaReferenceMapper toscaReferenceMapper = null;
    @Inject
    public static IXMLSerializerService xmlSerializerService = null;
    @Inject
    public static ICSARInstanceManagementService csarInstanceManagement = null;

    public static CorrelationHandler correlationHandler = new CorrelationHandler();

    public static EventAdmin eventAdmin;

    private final Logger LOG = LoggerFactory.getLogger(ServiceProxy.class);


    protected void bindEventAdmin(final EventAdmin service) {
        if (service == null) {
            this.LOG.error("Service EventAdmin is null.");
        } else {
            this.LOG.debug("Bind of the EventAdmin.");
            ServiceProxy.eventAdmin = service;
        }
    }

    protected void unbindEventAdmin(final EventAdmin service) {
        this.LOG.debug("Unbind of the EventAdmin.");
        ServiceProxy.eventAdmin = null;
    }

    protected void bindICSARInstanceManagementService(final ICSARInstanceManagementService service) {
        if (service == null) {
            this.LOG.error("Service ICSARInstanceManagementService is null.");
        } else {
            this.LOG.debug("Bind of the ICSARInstanceManagementService.");
            ServiceProxy.csarInstanceManagement = service;
        }
    }

    protected void unbindICSARInstanceManagementService(final ICSARInstanceManagementService service) {
        this.LOG.debug("Unbind of the ICSARInstanceManagementService.");
        ServiceProxy.csarInstanceManagement = null;
    }

    protected void bindtoscaEngineService(final IToscaEngineService service) {
        if (service == null) {
            this.LOG.error("Service toscaEngineService is null.");
        } else {
            this.LOG.debug("Bind of the toscaEngineService.");
            ServiceProxy.toscaEngineService = service;

            if (null != ServiceProxy.toscaEngineService.getToscaReferenceMapper()) {
                ServiceProxy.toscaReferenceMapper = ServiceProxy.toscaEngineService.getToscaReferenceMapper();
            } else {
                this.LOG.error("The ToscaReferenceMapper is not ready yet.");
            }

        }
    }

    protected void unbindtoscaEngineService(final IToscaEngineService service) {
        this.LOG.debug("Unbind of the toscaEngineService.");
        ServiceProxy.toscaEngineService = null;
    }

    protected void bindIXMLSerializerService(final IXMLSerializerService service) {
        if (service == null) {
            this.LOG.error("Service IXMLSerializerService is null.");
        } else {
            this.LOG.debug("Bind of the IXMLSerializerService.");
            ServiceProxy.xmlSerializerService = service;

        }
    }

    protected void unbindIXMLSerializerService(final IXMLSerializerService service) {
        this.LOG.debug("Unbind of the IXMLSerializerService.");
        ServiceProxy.xmlSerializerService = null;
    }

}
