package org.opentosca.containerapi;


import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class JerseyServletComponent {
	
	private static final Logger LOG = LoggerFactory.getLogger(JerseyServletComponent.class);
	
	
	protected void bindHttpService(ExtendedHttpService httpService) {
		JerseyServletComponent.LOG.debug("Binding HTTP Service");
		try {
			httpService.registerServlet(ResourceConstants.ROOT, new ServletContainer(new JerseyApplication()), null, null);
			httpService.registerFilter("/", new CorsFilter(), null, null);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void unbindHttpService(ExtendedHttpService httpService) {
		JerseyServletComponent.LOG.debug("Unbinding HTTP Service");
		httpService.unregister(ResourceConstants.ROOT);
	}
	
	protected void activate(ComponentContext componentContext) {
		// the Uri for the ContainerApi is also stored in the SettingsBundle
		String port = componentContext.getBundleContext().getProperty("org.osgi.service.http.port");
		if ((port == null) || (port.trim().length() == 0)) {
			port = "1337";
		}
		
		JerseyServletComponent.LOG.info("Container API started: http://localhost:{}{}", port, ResourceConstants.ROOT);
	}
}
