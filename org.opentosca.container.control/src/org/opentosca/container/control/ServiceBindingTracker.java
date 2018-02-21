package org.opentosca.container.control;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.service.ICoreDeploymentTrackerService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.ICoreModelRepositoryService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.service.internal.ICoreInternalCapabilityService;
import org.opentosca.container.core.service.internal.ICoreInternalDeploymentTrackerService;
import org.opentosca.container.core.service.internal.ICoreInternalEndpointService;
import org.opentosca.container.core.service.internal.ICoreInternalFileService;
import org.opentosca.container.core.service.internal.ICoreInternalModelRepositoryService;
import org.opentosca.container.engine.ia.IIAEngineService;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a tracker for binding of the core services. If all defined services are
 * bound, there is a log output saying that the container is ready for use.
 */
public class ServiceBindingTracker {

    IIAEngineService iaEngineService;
    ICoreCapabilityService coreCapabilityService;
    ICoreDeploymentTrackerService coreDeploymentTrackerService;
    ICoreEndpointService coreEndpointService;
    ICoreFileService coreFileService;
    ICoreInternalCapabilityService coreInternalCapabilityService;
    ICoreInternalDeploymentTrackerService coreInternalDeploymentTrackerService;
    ICoreInternalEndpointService coreInternalEndpointService;
    ICoreInternalFileService coreInternalFileService;
    ICoreInternalModelRepositoryService coreInternalModelRepositoryService;
    ICoreModelRepositoryService coreModelRepositoryService;
    IFileAccessService fileAccessService;
    IHTTPService httpService;
    IOpenToscaControlService openToscaControlService;
    IPlanEngineService planEngineService;
    IToscaEngineService toscaEngineService;
    IXMLSerializerService xmlSerializerService;
    IPlanInvocationEngine planInvocationEngine;
    IManagementBusService managementBusService;
    EventAdmin eventAdmin;

    private final Logger LOG = LoggerFactory.getLogger(ServiceBindingTracker.class);


    /**
     * Checks if all services defined by this class are bound. If all are bound there is a log output
     * saying that the container is ready for use.
     */
    private void checkAvailability() {

        // is true as long as each service is bound
        boolean boolAllServicesBound = true;

        // Get all declared fields of this class. This contains all services.
        for (final Field field : this.getClass().getDeclaredFields()) {
            try {

                // Check if the fields are null or not. As soon as one field is
                // null the check field never gets true again.
                boolAllServicesBound = boolAllServicesBound && field.get(this) != null;

            } catch (final IllegalArgumentException e) {
                this.LOG.error(e.getLocalizedMessage());
            } catch (final IllegalAccessException e) {
                this.LOG.error(e.getLocalizedMessage());
            }
        }

        // put status log
        if (boolAllServicesBound) {
            this.logContainerIsAvailable();
        }
    }

    /**
     * Method for a log output saying that the container is ready for use.
     */
    private void logContainerIsAvailable() {

        this.LOG.info(
            "Start of the OpenTOSCA Container, now invoke the resolving and consolidation of TOSCA data inside of stored CSARs.");
        for (final CSARID csarID : this.coreFileService.getCSARIDs()) {
            this.openToscaControlService.invokeTOSCAProcessing(csarID);

            for (final QName serviceTemplateID : this.toscaEngineService.getToscaReferenceMapper()
                                                                        .getServiceTemplateIDsContainedInCSAR(csarID)) {
                this.openToscaControlService.invokeIADeployment(csarID, serviceTemplateID);
                this.openToscaControlService.invokePlanDeployment(csarID, serviceTemplateID);
            }
        }

        this.toscaEngineService.getToscaReferenceMapper().printStoredData();

        this.LOG.info(
            "#################################################################################################");
        this.LOG.info(
            "#################################################################################################");
        this.LOG.info(
            "########################### The OpenTOSCA Container is ready for use! ###########################");
        this.LOG.info(
            "#################################################################################################");
        this.LOG.info(
            "#################################################################################################");
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIIAEngineService(final IIAEngineService service) {
        if (service == null) {
            this.LOG.error("Service IIAEngineService is null.");
        } else {
            this.LOG.debug("Bind of the IIAEngineService.");
            this.iaEngineService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    protected void unbindIIAEngineService(final IIAEngineService service) {
        this.LOG.debug("Unbind of the IIAEngineService.");
        this.iaEngineService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreCapabilityService(final ICoreCapabilityService service) {
        if (service == null) {
            this.LOG.error("Service ICoreCapabilityService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreCapabilityService.");
            this.coreCapabilityService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreCapabilityService(final ICoreCapabilityService service) {
        this.LOG.debug("Unbind of the ICoreCapabilityService.");
        this.coreCapabilityService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreDeploymentTrackerService(final ICoreDeploymentTrackerService service) {
        if (service == null) {
            this.LOG.error("Service ICoreDeploymentTrackerService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreDeploymentTrackerService.");
            this.coreDeploymentTrackerService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreDeploymentTrackerService(final ICoreDeploymentTrackerService service) {
        this.LOG.debug("Unbind of the ICoreDeploymentTrackerService.");
        this.coreDeploymentTrackerService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreEndpointService(final ICoreEndpointService service) {
        if (service == null) {
            this.LOG.error("Service ICoreEndpointService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreEndpointService.");
            this.coreEndpointService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreEndpointService(final ICoreEndpointService service) {
        this.LOG.debug("Unbind of the ICoreEndpointService.");
        this.coreEndpointService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreFileService(final ICoreFileService service) {
        if (service == null) {
            this.LOG.error("Service ICoreFileService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreFileService.");
            this.coreFileService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreFileService(final ICoreFileService service) {
        this.LOG.debug("Unbind of the ICoreFileService.");
        this.coreFileService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreInternalCapabilityService(final ICoreInternalCapabilityService service) {
        if (service == null) {
            this.LOG.error("Service ICoreInternalCapabilityService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreInternalCapabilityService.");
            this.coreInternalCapabilityService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreInternalCapabilityService(final ICoreInternalCapabilityService service) {
        this.LOG.debug("Unbind of the ICoreInternalCapabilityService.");
        this.coreInternalCapabilityService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreInternalDeploymentTrackerService(final ICoreInternalDeploymentTrackerService service) {
        if (service == null) {
            this.LOG.error("Service ICoreInternalDeploymentTrackerService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreInternalDeploymentTrackerService.");
            this.coreInternalDeploymentTrackerService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreInternalDeploymentTrackerService(final ICoreInternalDeploymentTrackerService service) {
        this.LOG.debug("Unbind of the ICoreInternalDeploymentTrackerService.");
        this.coreInternalDeploymentTrackerService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreInternalEndpointService(final ICoreInternalEndpointService service) {
        if (service == null) {
            this.LOG.error("Service ICoreInternalEndpointService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreInternalEndpointService.");
            this.coreInternalEndpointService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreInternalEndpointService(final ICoreInternalEndpointService service) {
        this.LOG.debug("Unbind of the ICoreInternalEndpointService.");
        this.coreInternalEndpointService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreInternalFileService(final ICoreInternalFileService service) {
        if (service == null) {
            this.LOG.error("Service ICoreInternalFileService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreInternalFileService.");
            this.coreInternalFileService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreInternalFileService(final ICoreInternalFileService service) {
        this.LOG.debug("Unbind of the ICoreInternalFileService.");
        this.coreInternalFileService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreInternalModelRepositoryService(final ICoreInternalModelRepositoryService service) {
        if (service == null) {
            this.LOG.error("Service ICoreInternalModelRepositoryService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreInternalModelRepositoryService.");
            this.coreInternalModelRepositoryService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreInternalModelRepositoryService(final ICoreInternalModelRepositoryService service) {
        this.LOG.debug("Unbind of the ICoreInternalModelRepositoryService.");
        this.coreInternalModelRepositoryService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindICoreModelRepositoryService(final ICoreModelRepositoryService service) {
        if (service == null) {
            this.LOG.error("Service ICoreModelRepositoryService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreModelRepositoryService.");
            this.coreModelRepositoryService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindICoreModelRepositoryService(final ICoreModelRepositoryService service) {
        this.LOG.debug("Unbind of the ICoreModelRepositoryService.");
        this.coreModelRepositoryService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIFileAccessService(final IFileAccessService service) {
        if (service == null) {
            this.LOG.error("Service IFileAccessService is null.");
        } else {
            this.LOG.debug("Bind of the IFileAccessService.");
            this.fileAccessService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIFileAccessService(final IFileAccessService service) {
        this.LOG.debug("Unbind of the IFileAccessService.");
        this.fileAccessService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIHTTPService(final IHTTPService service) {
        if (service == null) {
            this.LOG.error("Service IHTTPService is null.");
        } else {
            this.LOG.debug("Bind of the IHTTPService.");
            this.httpService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIHTTPService(final IHTTPService service) {
        this.LOG.debug("Unbind of the IHTTPService.");
        this.httpService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIOpenToscaControlService(final IOpenToscaControlService service) {
        if (service == null) {
            this.LOG.error("Service IOpenToscaControlService is null.");
        } else {
            this.LOG.debug("Bind of the IOpenToscaControlService.");
            this.openToscaControlService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIOpenToscaControlService(final IOpenToscaControlService service) {
        this.LOG.debug("Unbind of the IOpenToscaControlService.");
        this.openToscaControlService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIPlanEngineService(final IPlanEngineService service) {
        if (service == null) {
            this.LOG.error("Service IPlanEngineService is null.");
        } else {
            this.LOG.debug("Bind of the IPlanEngineService.");
            this.planEngineService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIPlanEngineService(final IPlanEngineService service) {
        this.LOG.debug("Unbind of the IPlanEngineService.");
        this.planEngineService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIToscaEngineService(final IToscaEngineService service) {
        if (service == null) {
            this.LOG.error("Service IToscaEngineService is null.");
        } else {
            this.LOG.debug("Bind of the IToscaEngineService.");
            this.toscaEngineService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIToscaEngineService(final IToscaEngineService service) {
        this.LOG.debug("Unbind of the IToscaEngineService.");
        this.toscaEngineService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIXMLSerializerService(final IXMLSerializerService service) {
        if (service == null) {
            this.LOG.error("Service IXMLSerializerService is null.");
        } else {
            this.LOG.debug("Bind of the IXMLSerializerService.");
            this.xmlSerializerService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIXMLSerializerService(final IXMLSerializerService service) {
        this.LOG.debug("Unbind of the IXMLSerializerService.");
        this.xmlSerializerService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIPlanInvocationEngine(final IPlanInvocationEngine service) {
        if (service == null) {
            this.LOG.error("Service planInvocationEngine is null.");
        } else {
            this.LOG.debug("Bind of the planInvocationEngine.");
            this.planInvocationEngine = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIPlanInvocationEngine(final IPlanInvocationEngine service) {
        this.LOG.debug("Unbind of the planInvocationEngine.");
        this.planInvocationEngine = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     *
     * @param service The service to bind.
     */
    protected void bindIManagementBusService(final IManagementBusService service) {
        if (service == null) {
            this.LOG.error("Service IManagementBusService is null.");
        } else {
            this.LOG.debug("Bind of the IManagementBusService.");
            this.managementBusService = service;
            this.log_online(service.getClass().getSimpleName());
            this.checkAvailability();
        }
    }

    /**
     * Unbind method for a service.
     *
     * @param service The service to unbind.
     */
    protected void unbindIManagementBusService(final IManagementBusService service) {
        this.LOG.debug("Unbind of the IManagementBusService.");
        this.managementBusService = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    protected void bindEventAdmin(final EventAdmin service) {
        if (service == null) {
            this.LOG.error("Service EventAdmin is null.");
        } else {
            this.LOG.debug("Bind of the EventAdmin.");
            this.eventAdmin = service;

            final Map<String, Object> eventValues = new Hashtable<>();
            eventValues.put("callbackaddressrequest", "request");
            final Event event = new Event("org_opentosca_plans/callbackaddressrequest", eventValues);

            this.LOG.debug("Send callback address request.");
            this.eventAdmin.postEvent(event);
            this.log_online(service.getClass().getSimpleName());

            this.checkAvailability();
        }
    }

    protected void unbindEventAdmin(final EventAdmin service) {
        this.LOG.debug("Unbind of the EventAdmin.");
        this.eventAdmin = null;
        this.log_offline(service.getClass().getSimpleName());
    }

    private void log_online(final String servicename) {
        // this.LOG.info("+++ Service is online: {}", servicename);
    }

    private void log_offline(final String servicename) {
        this.LOG.warn("--- Service is offline: {}", servicename);
    }

}
