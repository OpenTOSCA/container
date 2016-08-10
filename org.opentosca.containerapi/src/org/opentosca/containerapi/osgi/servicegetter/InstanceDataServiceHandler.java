package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.instancedata.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IInstanceDataService
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class InstanceDataServiceHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(InstanceDataServiceHandler.class);
	
	private static IInstanceDataService instanceDataService;
	
	public static IInstanceDataService getInstanceDataService() {
		return InstanceDataServiceHandler.instanceDataService;
	}
	
	public void bindInstanceDataService(IInstanceDataService instanceDataService) {
		InstanceDataServiceHandler.LOG.debug("ContainerApi: Bind IInstanceDataService");
		InstanceDataServiceHandler.instanceDataService = instanceDataService;
	}
	
	public void unbindInstanceDataService(IInstanceDataService credentialsService) {
		InstanceDataServiceHandler.LOG.debug("ContainerApi: Unbind IInstanceDataService");
		InstanceDataServiceHandler.instanceDataService = null;
	}
	
}
