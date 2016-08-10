package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface ICSARInstanceManagementService
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceManagementHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(CSARInstanceManagementHandler.class);
	
	public static ICSARInstanceManagementService csarInstanceManagement;
	
	
	public void bindService(ICSARInstanceManagementService service) {
		CSARInstanceManagementHandler.LOG.debug("ContainerApi: Bind ICSARInstanceManagementService");
		CSARInstanceManagementHandler.csarInstanceManagement = service;
	}
	
	public void unbindService(ICSARInstanceManagementService service) {
		CSARInstanceManagementHandler.LOG.debug("ContainerApi: Unbind ICSARInstanceManagementService");
		CSARInstanceManagementHandler.csarInstanceManagement = null;
	}
}
