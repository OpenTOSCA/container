package org.opentosca.opentoscacontrol.servicebindingtracking;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.core.capability.service.ICoreCapabilityService;
import org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService;
import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.internal.capability.service.ICoreInternalCapabilityService;
import org.opentosca.core.internal.deployment.tracker.service.ICoreInternalDeploymentTrackerService;
import org.opentosca.core.internal.endpoint.service.ICoreInternalEndpointService;
import org.opentosca.core.internal.file.service.ICoreInternalFileService;
import org.opentosca.core.internal.model.repository.service.ICoreInternalModelRepositoryService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.repository.service.ICoreModelRepositoryService;
import org.opentosca.iaengine.service.IIAEngineService;
import org.opentosca.opentoscacontrol.service.IOpenToscaControlService;
import org.opentosca.planengine.service.IPlanEngineService;
import org.opentosca.planinvocationengine.service.IPlanInvocationEngine;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.opentosca.util.http.service.IHTTPService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a tracker for binding of the core services. If all
 * defined services are bound, there is a log output saying that the container
 * is ready for use.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
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

    private final Logger LOG = LoggerFactory
	.getLogger(ServiceBindingTracker.class);

    /**
     * Checks if all services defined by this class are bound. If all are bound
     * there is a log output saying that the container is ready for use.
     */
    private void checkAvailability() {

	// is true as long as each service is bound
	boolean boolAllServicesBound = true;

	// Get all declared fields of this class. This contains all services.
	for (Field field : this.getClass().getDeclaredFields()) {
	    try {

		// Check if the fields are null or not. As soon as one field is
		// null the check field never gets true again.
		boolAllServicesBound = boolAllServicesBound
		    && (field.get(this) != null);

	    } catch (IllegalArgumentException e) {
		LOG.error(e.getLocalizedMessage());
	    } catch (IllegalAccessException e) {
		LOG.error(e.getLocalizedMessage());
	    }
	}

	// put status log
	if (boolAllServicesBound) {
	    logContainerIsAvailable();
	}
    }

    /**
     * Method for a log output saying that the container is ready for use.
     */
    private void logContainerIsAvailable() {

	LOG.info("Start of the OpenTOSCA Container, now invoke the resolving and consolidation of TOSCA data inside of stored CSARs.");
	for (CSARID csarID : coreFileService.getCSARIDs()) {
	    openToscaControlService.invokeTOSCAProcessing(csarID);

	    for (QName serviceTemplateID : toscaEngineService
		.getToscaReferenceMapper()
		.getServiceTemplateIDsContainedInCSAR(csarID)) {
		openToscaControlService.invokeIADeployment(csarID,
		    serviceTemplateID);
		openToscaControlService.invokePlanDeployment(csarID,
		    serviceTemplateID);
	    }
	}

	toscaEngineService.getToscaReferenceMapper().printStoredData();

	LOG.info("#################################################################################################");
	LOG.info("#################################################################################################");
	LOG.info("########################### The OpenTOSCA Container is ready for use! ###########################");
	LOG.info("#################################################################################################");
	LOG.info("#################################################################################################");
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIIAEngineService(IIAEngineService service) {
	if (service == null) {
	    LOG.error("Service IIAEngineService is null.");
	} else {
	    LOG.debug("Bind of the IIAEngineService.");
	    iaEngineService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    protected void unbindIIAEngineService(IIAEngineService service) {
	LOG.debug("Unbind of the IIAEngineService.");
	iaEngineService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreCapabilityService(ICoreCapabilityService service) {
	if (service == null) {
	    LOG.error("Service ICoreCapabilityService is null.");
	} else {
	    LOG.debug("Bind of the ICoreCapabilityService.");
	    coreCapabilityService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreCapabilityService(ICoreCapabilityService service) {
	LOG.debug("Unbind of the ICoreCapabilityService.");
	coreCapabilityService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreDeploymentTrackerService(
	ICoreDeploymentTrackerService service) {
	if (service == null) {
	    LOG.error("Service ICoreDeploymentTrackerService is null.");
	} else {
	    LOG.debug("Bind of the ICoreDeploymentTrackerService.");
	    coreDeploymentTrackerService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreDeploymentTrackerService(
	ICoreDeploymentTrackerService service) {
	LOG.debug("Unbind of the ICoreDeploymentTrackerService.");
	coreDeploymentTrackerService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreEndpointService(ICoreEndpointService service) {
	if (service == null) {
	    LOG.error("Service ICoreEndpointService is null.");
	} else {
	    LOG.debug("Bind of the ICoreEndpointService.");
	    coreEndpointService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreEndpointService(ICoreEndpointService service) {
	LOG.debug("Unbind of the ICoreEndpointService.");
	coreEndpointService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreFileService(ICoreFileService service) {
	if (service == null) {
	    LOG.error("Service ICoreFileService is null.");
	} else {
	    LOG.debug("Bind of the ICoreFileService.");
	    coreFileService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreFileService(ICoreFileService service) {
	LOG.debug("Unbind of the ICoreFileService.");
	coreFileService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreInternalCapabilityService(
	ICoreInternalCapabilityService service) {
	if (service == null) {
	    LOG.error("Service ICoreInternalCapabilityService is null.");
	} else {
	    LOG.debug("Bind of the ICoreInternalCapabilityService.");
	    coreInternalCapabilityService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreInternalCapabilityService(
	ICoreInternalCapabilityService service) {
	LOG.debug("Unbind of the ICoreInternalCapabilityService.");
	coreInternalCapabilityService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreInternalDeploymentTrackerService(
	ICoreInternalDeploymentTrackerService service) {
	if (service == null) {
	    LOG.error("Service ICoreInternalDeploymentTrackerService is null.");
	} else {
	    LOG.debug("Bind of the ICoreInternalDeploymentTrackerService.");
	    coreInternalDeploymentTrackerService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreInternalDeploymentTrackerService(
	ICoreInternalDeploymentTrackerService service) {
	LOG.debug("Unbind of the ICoreInternalDeploymentTrackerService.");
	coreInternalDeploymentTrackerService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreInternalEndpointService(
	ICoreInternalEndpointService service) {
	if (service == null) {
	    LOG.error("Service ICoreInternalEndpointService is null.");
	} else {
	    LOG.debug("Bind of the ICoreInternalEndpointService.");
	    coreInternalEndpointService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreInternalEndpointService(
	ICoreInternalEndpointService service) {
	LOG.debug("Unbind of the ICoreInternalEndpointService.");
	coreInternalEndpointService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreInternalFileService(ICoreInternalFileService service) {
	if (service == null) {
	    LOG.error("Service ICoreInternalFileService is null.");
	} else {
	    LOG.debug("Bind of the ICoreInternalFileService.");
	    coreInternalFileService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreInternalFileService(
	ICoreInternalFileService service) {
	LOG.debug("Unbind of the ICoreInternalFileService.");
	coreInternalFileService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreInternalModelRepositoryService(
	ICoreInternalModelRepositoryService service) {
	if (service == null) {
	    LOG.error("Service ICoreInternalModelRepositoryService is null.");
	} else {
	    LOG.debug("Bind of the ICoreInternalModelRepositoryService.");
	    coreInternalModelRepositoryService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreInternalModelRepositoryService(
	ICoreInternalModelRepositoryService service) {
	LOG.debug("Unbind of the ICoreInternalModelRepositoryService.");
	coreInternalModelRepositoryService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindICoreModelRepositoryService(
	ICoreModelRepositoryService service) {
	if (service == null) {
	    LOG.error("Service ICoreModelRepositoryService is null.");
	} else {
	    LOG.debug("Bind of the ICoreModelRepositoryService.");
	    coreModelRepositoryService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindICoreModelRepositoryService(
	ICoreModelRepositoryService service) {
	LOG.debug("Unbind of the ICoreModelRepositoryService.");
	coreModelRepositoryService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIFileAccessService(IFileAccessService service) {
	if (service == null) {
	    LOG.error("Service IFileAccessService is null.");
	} else {
	    LOG.debug("Bind of the IFileAccessService.");
	    fileAccessService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIFileAccessService(IFileAccessService service) {
	LOG.debug("Unbind of the IFileAccessService.");
	fileAccessService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIHTTPService(IHTTPService service) {
	if (service == null) {
	    LOG.error("Service IHTTPService is null.");
	} else {
	    LOG.debug("Bind of the IHTTPService.");
	    httpService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIHTTPService(IHTTPService service) {
	LOG.debug("Unbind of the IHTTPService.");
	httpService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIOpenToscaControlService(IOpenToscaControlService service) {
	if (service == null) {
	    LOG.error("Service IOpenToscaControlService is null.");
	} else {
	    LOG.debug("Bind of the IOpenToscaControlService.");
	    openToscaControlService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIOpenToscaControlService(
	IOpenToscaControlService service) {
	LOG.debug("Unbind of the IOpenToscaControlService.");
	openToscaControlService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIPlanEngineService(IPlanEngineService service) {
	if (service == null) {
	    LOG.error("Service IPlanEngineService is null.");
	} else {
	    LOG.debug("Bind of the IPlanEngineService.");
	    planEngineService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIPlanEngineService(IPlanEngineService service) {
	LOG.debug("Unbind of the IPlanEngineService.");
	planEngineService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIToscaEngineService(IToscaEngineService service) {
	if (service == null) {
	    LOG.error("Service IToscaEngineService is null.");
	} else {
	    LOG.debug("Bind of the IToscaEngineService.");
	    toscaEngineService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIToscaEngineService(IToscaEngineService service) {
	LOG.debug("Unbind of the IToscaEngineService.");
	toscaEngineService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIXMLSerializerService(IXMLSerializerService service) {
	if (service == null) {
	    LOG.error("Service IXMLSerializerService is null.");
	} else {
	    LOG.debug("Bind of the IXMLSerializerService.");
	    xmlSerializerService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIXMLSerializerService(IXMLSerializerService service) {
	LOG.debug("Unbind of the IXMLSerializerService.");
	xmlSerializerService = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIPlanInvocationEngine(IPlanInvocationEngine service) {
	if (service == null) {
	    LOG.error("Service planInvocationEngine is null.");
	} else {
	    LOG.debug("Bind of the planInvocationEngine.");
	    planInvocationEngine = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIPlanInvocationEngine(IPlanInvocationEngine service) {
	LOG.debug("Unbind of the planInvocationEngine.");
	planInvocationEngine = null;
	log_offline(service.getClass().getSimpleName());
    }

    /**
     * Bind method for a service.
     * 
     * @param service
     *            The service to bind.
     */
    protected void bindIManagementBusService(IManagementBusService service) {
	if (service == null) {
	    LOG.error("Service IManagementBusService is null.");
	} else {
	    LOG.debug("Bind of the IManagementBusService.");
	    managementBusService = service;
	    log_online(service.getClass().getSimpleName());
	    checkAvailability();
	}
    }

    /**
     * Unbind method for a service.
     * 
     * @param service
     *            The service to unbind.
     */
    protected void unbindIManagementBusService(IManagementBusService service) {
	LOG.debug("Unbind of the IManagementBusService.");
	managementBusService = null;
	log_offline(service.getClass().getSimpleName());
    }

    protected void bindEventAdmin(EventAdmin service) {
	if (service == null) {
	    LOG.error("Service EventAdmin is null.");
	} else {
	    LOG.debug("Bind of the EventAdmin.");
	    eventAdmin = service;

	    Map<String, Object> eventValues = new Hashtable<String, Object>();
	    eventValues.put("callbackaddressrequest", "request");
	    Event event = new Event(
		"org_opentosca_plans/callbackaddressrequest", eventValues);

	    LOG.debug("Send callback address request.");
	    eventAdmin.postEvent(event);
	    log_online(service.getClass().getSimpleName());

	    checkAvailability();
	}
    }

    protected void unbindEventAdmin(EventAdmin service) {
	LOG.debug("Unbind of the EventAdmin.");
	eventAdmin = null;
	log_offline(service.getClass().getSimpleName());
    }

    private void log_online(String servicename) {
	// this.LOG.info("+++ Service is online: {}", servicename);
    }

    private void log_offline(String servicename) {
	LOG.warn("--- Service is offline: {}", servicename);
    }

}