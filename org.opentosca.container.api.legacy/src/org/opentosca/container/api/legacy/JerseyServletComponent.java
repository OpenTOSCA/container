package org.opentosca.container.api.legacy;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.glassfish.jersey.servlet.ServletContainer;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


	protected void bindHttpService(final ExtendedHttpService httpService) {
		JerseyServletComponent.LOG.debug("Binding HTTP Service");
		try {
			
			// final JerseyApplication app = new JerseyApplication();
			final ServletContainer container = new ServletContainer();

			// this is for supporting json, but unfortunately we need further
			// bundles in the target platform ...
			// <init-param>
			// <param-name>com.sun.jersey.api.json.POJOMappingFeature</param-name>
			// <param-value>true</param-value>
			// </init-param>
			final Dictionary<String, String> initParams = new Hashtable<>();
			initParams.put("javax.ws.rs.Application", JerseyApplication.class.getName());
			// initParams.put("com.sun.jersey.api.json.POJOMappingFeature",
			// "true");

			// TODO: Temporary workaround
			// This is a workaround related to issue JERSEY-2093; grizzly
			// (1.9.5)
			final ClassLoader classLoader = this.getClass().getClassLoader();
			final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(classLoader);
				httpService.registerServlet(ResourceConstants.ROOT, container, initParams, null);
				httpService.registerFilter("/", new CorsFilter(), null, null);
			} finally {
				Thread.currentThread().setContextClassLoader(contextClassLoader);
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void unbindHttpService(final ExtendedHttpService httpService) {
		JerseyServletComponent.LOG.debug("Unbinding HTTP Service");
		httpService.unregister(ResourceConstants.ROOT);
	}

	protected void activate(final ComponentContext componentContext) {
		// the Uri for the ContainerApi is also stored in the SettingsBundle
		String port = componentContext.getBundleContext().getProperty("org.osgi.service.http.port");
		if ((port == null) || (port.trim().length() == 0)) {
			port = "1337";
		}

		JerseyServletComponent.LOG.info("Container API started: http://localhost:{}{}", port, ResourceConstants.ROOT);
	}
}
