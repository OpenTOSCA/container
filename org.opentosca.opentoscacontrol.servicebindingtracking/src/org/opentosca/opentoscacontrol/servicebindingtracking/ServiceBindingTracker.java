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

	private final Logger LOG = LoggerFactory.getLogger(ServiceBindingTracker.class);


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
				boolAllServicesBound = boolAllServicesBound && (field.get(this) != null);

			} catch (IllegalArgumentException e) {
				this.LOG.error(e.getLocalizedMessage());
			} catch (IllegalAccessException e) {
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
		
		this.LOG.info("Start of the OpenTOSCA Container, now invoke the resolving and consolidation of TOSCA data inside of stored CSARs.");
		for (CSARID csarID : this.coreFileService.getCSARIDs()) {
			this.openToscaControlService.invokeTOSCAProcessing(csarID);

			for (QName serviceTemplateID : this.toscaEngineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID)) {
				this.openToscaControlService.invokeIADeployment(csarID, serviceTemplateID);
				this.openToscaControlService.invokePlanDeployment(csarID, serviceTemplateID);
			}
		}

		this.toscaEngineService.getToscaReferenceMapper().printStoredData();

		this.LOG.info("#################################################################################################");
		this.LOG.info("#################################################################################################");
		this.LOG.info("########################### The OpenTOSCA Container is ready for use! ###########################");
		this.LOG.info("#################################################################################################");
		this.LOG.info("#################################################################################################");
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIIAEngineService(IIAEngineService service) {
		if (service == null) {
			this.LOG.error("Service IIAEngineService is null.");
		} else {
			this.LOG.debug("Bind of the IIAEngineService.");
			this.iaEngineService = service;
			this.log_online(service.getClass().getSimpleName());
			this.checkAvailability();
		}
	}

	protected void unbindIIAEngineService(IIAEngineService service) {
		this.LOG.debug("Unbind of the IIAEngineService.");
		this.iaEngineService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreCapabilityService(ICoreCapabilityService service) {
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
	protected void unbindICoreCapabilityService(ICoreCapabilityService service) {
		this.LOG.debug("Unbind of the ICoreCapabilityService.");
		this.coreCapabilityService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreDeploymentTrackerService(ICoreDeploymentTrackerService service) {
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
	protected void unbindICoreDeploymentTrackerService(ICoreDeploymentTrackerService service) {
		this.LOG.debug("Unbind of the ICoreDeploymentTrackerService.");
		this.coreDeploymentTrackerService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreEndpointService(ICoreEndpointService service) {
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
	protected void unbindICoreEndpointService(ICoreEndpointService service) {
		this.LOG.debug("Unbind of the ICoreEndpointService.");
		this.coreEndpointService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreFileService(ICoreFileService service) {
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
	protected void unbindICoreFileService(ICoreFileService service) {
		this.LOG.debug("Unbind of the ICoreFileService.");
		this.coreFileService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreInternalCapabilityService(ICoreInternalCapabilityService service) {
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
	protected void unbindICoreInternalCapabilityService(ICoreInternalCapabilityService service) {
		this.LOG.debug("Unbind of the ICoreInternalCapabilityService.");
		this.coreInternalCapabilityService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreInternalDeploymentTrackerService(ICoreInternalDeploymentTrackerService service) {
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
	protected void unbindICoreInternalDeploymentTrackerService(ICoreInternalDeploymentTrackerService service) {
		this.LOG.debug("Unbind of the ICoreInternalDeploymentTrackerService.");
		this.coreInternalDeploymentTrackerService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreInternalEndpointService(ICoreInternalEndpointService service) {
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
	protected void unbindICoreInternalEndpointService(ICoreInternalEndpointService service) {
		this.LOG.debug("Unbind of the ICoreInternalEndpointService.");
		this.coreInternalEndpointService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreInternalFileService(ICoreInternalFileService service) {
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
	protected void unbindICoreInternalFileService(ICoreInternalFileService service) {
		this.LOG.debug("Unbind of the ICoreInternalFileService.");
		this.coreInternalFileService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreInternalModelRepositoryService(ICoreInternalModelRepositoryService service) {
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
	protected void unbindICoreInternalModelRepositoryService(ICoreInternalModelRepositoryService service) {
		this.LOG.debug("Unbind of the ICoreInternalModelRepositoryService.");
		this.coreInternalModelRepositoryService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindICoreModelRepositoryService(ICoreModelRepositoryService service) {
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
	protected void unbindICoreModelRepositoryService(ICoreModelRepositoryService service) {
		this.LOG.debug("Unbind of the ICoreModelRepositoryService.");
		this.coreModelRepositoryService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIFileAccessService(IFileAccessService service) {
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
	protected void unbindIFileAccessService(IFileAccessService service) {
		this.LOG.debug("Unbind of the IFileAccessService.");
		this.fileAccessService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIHTTPService(IHTTPService service) {
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
	protected void unbindIHTTPService(IHTTPService service) {
		this.LOG.debug("Unbind of the IHTTPService.");
		this.httpService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIOpenToscaControlService(IOpenToscaControlService service) {
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
	protected void unbindIOpenToscaControlService(IOpenToscaControlService service) {
		this.LOG.debug("Unbind of the IOpenToscaControlService.");
		this.openToscaControlService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIPlanEngineService(IPlanEngineService service) {
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
	protected void unbindIPlanEngineService(IPlanEngineService service) {
		this.LOG.debug("Unbind of the IPlanEngineService.");
		this.planEngineService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIToscaEngineService(IToscaEngineService service) {
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
	protected void unbindIToscaEngineService(IToscaEngineService service) {
		this.LOG.debug("Unbind of the IToscaEngineService.");
		this.toscaEngineService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIXMLSerializerService(IXMLSerializerService service) {
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
	protected void unbindIXMLSerializerService(IXMLSerializerService service) {
		this.LOG.debug("Unbind of the IXMLSerializerService.");
		this.xmlSerializerService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIPlanInvocationEngine(IPlanInvocationEngine service) {
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
	protected void unbindIPlanInvocationEngine(IPlanInvocationEngine service) {
		this.LOG.debug("Unbind of the planInvocationEngine.");
		this.planInvocationEngine = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	/**
	 * Bind method for a service.
	 *
	 * @param service The service to bind.
	 */
	protected void bindIManagementBusService(IManagementBusService service) {
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
	protected void unbindIManagementBusService(IManagementBusService service) {
		this.LOG.debug("Unbind of the IManagementBusService.");
		this.managementBusService = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	protected void bindEventAdmin(EventAdmin service) {
		if (service == null) {
			this.LOG.error("Service EventAdmin is null.");
		} else {
			this.LOG.debug("Bind of the EventAdmin.");
			this.eventAdmin = service;

			Map<String, Object> eventValues = new Hashtable<String, Object>();
			eventValues.put("callbackaddressrequest", "request");
			Event event = new Event("org_opentosca_plans/callbackaddressrequest", eventValues);

			this.LOG.debug("Send callback address request.");
			this.eventAdmin.postEvent(event);
			this.log_online(service.getClass().getSimpleName());

			this.checkAvailability();
		}
	}

	protected void unbindEventAdmin(EventAdmin service) {
		this.LOG.debug("Unbind of the EventAdmin.");
		this.eventAdmin = null;
		this.log_offline(service.getClass().getSimpleName());
	}

	private void log_online(String servicename) {
		// this.LOG.info("+++ Service is online: {}", servicename);
	}

	private void log_offline(String servicename) {
		this.LOG.warn("--- Service is offline: {}", servicename);
	}

}