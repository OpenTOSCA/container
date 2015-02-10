package org.opentosca.planbuilder.csarhandler;

import org.opentosca.core.file.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is used to bind different services for the CSARHandler
 * </p>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class ServiceRegistry {

	final private static Logger LOG = LoggerFactory
			.getLogger(ServiceRegistry.class);

	private static ICoreFileService openTOSCACoreFileService = null;

	protected void bindOpenTOSCACoreFileService(ICoreFileService fileService) {
		LOG.debug("Binding CoreFileService");
		this.openTOSCACoreFileService = fileService;
	}

	protected void unbindOpenTOSCACoreFileService(ICoreFileService fileService) {
		LOG.debug("Unbinding CoreFileService");
		this.openTOSCACoreFileService = null;
	}

	protected static ICoreFileService getCoreFileService() {
		return ServiceRegistry.openTOSCACoreFileService;
	}
}
