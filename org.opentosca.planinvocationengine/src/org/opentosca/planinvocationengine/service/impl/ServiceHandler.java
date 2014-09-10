package org.opentosca.planinvocationengine.service.impl;

import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.planinvocationengine.service.impl.correlation.CorrelationHandler;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static service handler which provides services of other OSGI components in a
 * static way for classes of this bundle.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class ServiceHandler {
	
	public static IToscaEngineService toscaEngineService = null;
	public static IToscaReferenceMapper toscaReferenceMapper = null;
	public static ICSARInstanceManagementService csarInstanceManagement = null;
	
	public static CorrelationHandler correlationHandler = new CorrelationHandler();
	
	public static EventAdmin eventAdmin;
	
	private Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);
	
	
	protected void bindEventAdmin(EventAdmin service) {
		if (service == null) {
			this.LOG.error("Service EventAdmin is null.");
		} else {
			this.LOG.debug("Bind of the EventAdmin.");
			ServiceHandler.eventAdmin = service;
		}
	}
	
	protected void unbindEventAdmin(EventAdmin service) {
		this.LOG.debug("Unbind of the EventAdmin.");
		ServiceHandler.eventAdmin = null;
	}
	
	protected void bindICSARInstanceManagementService(ICSARInstanceManagementService service) {
		if (service == null) {
			this.LOG.error("Service ICSARInstanceManagementService is null.");
		} else {
			this.LOG.debug("Bind of the ICSARInstanceManagementService.");
			ServiceHandler.csarInstanceManagement = service;
		}
	}
	
	protected void unbindICSARInstanceManagementService(ICSARInstanceManagementService service) {
		this.LOG.debug("Unbind of the ICSARInstanceManagementService.");
		ServiceHandler.csarInstanceManagement = null;
	}
	
	protected void bindtoscaEngineService(IToscaEngineService service) {
		if (service == null) {
			this.LOG.error("Service toscaEngineService is null.");
		} else {
			this.LOG.debug("Bind of the toscaEngineService.");
			ServiceHandler.toscaEngineService = service;
			
			if (null != ServiceHandler.toscaEngineService.getToscaReferenceMapper()) {
				ServiceHandler.toscaReferenceMapper = ServiceHandler.toscaEngineService.getToscaReferenceMapper();
			} else {
				this.LOG.error("The ToscaReferenceMapper is not ready yet.");
			}
			
		}
	}
	
	protected void unbindtoscaEngineService(IToscaEngineService service) {
		this.LOG.debug("Unbind of the toscaEngineService.");
		ServiceHandler.toscaEngineService = null;
	}
	
}
