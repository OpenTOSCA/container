package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.portability.service.IPortabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IPortabilityService.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class PortabilityServiceHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(PortabilityServiceHandler.class);
	
	private static IPortabilityService portabilityService;
	
	public static IPortabilityService getPortabilityService() {
		return PortabilityServiceHandler.portabilityService;
	}
	
	public void bindPortabilityService(IPortabilityService portabilityService) {
		PortabilityServiceHandler.LOG.debug("ContainerApi: Bind IPortabilityService");
		PortabilityServiceHandler.portabilityService = portabilityService;
	}
	
	public void unbindPortabilityService(IPortabilityService portabilityService) {
		PortabilityServiceHandler.LOG.debug("ContainerApi: Unbind IPortabilityService");
		PortabilityServiceHandler.portabilityService = null;
	}
	
}
