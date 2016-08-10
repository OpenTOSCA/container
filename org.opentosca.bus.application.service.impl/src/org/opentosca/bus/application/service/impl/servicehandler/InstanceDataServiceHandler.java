package org.opentosca.bus.application.service.impl.servicehandler;

import org.opentosca.instancedata.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class to bind interface {@link IInstanceDataService}.
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class InstanceDataServiceHandler {

	final private static Logger LOG = LoggerFactory.getLogger(InstanceDataServiceHandler.class);

	private static IInstanceDataService instanceDataService;

	public static IInstanceDataService getInstanceDataService() {
		return InstanceDataServiceHandler.instanceDataService;
	}

	/**
	 * Bind IInstanceDataService.
	 * 
	 * @param instanceDataService
	 *            - A IInstanceDataService to register.
	 */
	public void bindInstanceDataService(IInstanceDataService instanceDataService) {
		InstanceDataServiceHandler.LOG.debug("App-Invoker: Bind IInstanceDataService");
		InstanceDataServiceHandler.instanceDataService = instanceDataService;
	}

	/**
	 * Unbind IInstanceDataService.
	 * 
	 * @param instanceDataService
	 *            - A IInstanceDataService to unregister.
	 */
	public void unbindInstanceDataService(IInstanceDataService instanceDataService) {
		InstanceDataServiceHandler.LOG.debug("App-Invoker: Unbind IInstanceDataService");
		InstanceDataServiceHandler.instanceDataService = null;
	}

}
