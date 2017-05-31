package org.opentosca.bus.application.service.impl.servicehandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.opentosca.bus.application.plugin.service.IApplicationBusPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Class to bind interface {@link IApplicationBusPluginService}.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ApplicationBusPluginServiceHandler {

	// HashMap that stores available plug-ins. The supported InvocationType is
	// used as key and the corresponding routing endpoint as value.
	private static ConcurrentHashMap<String, String> pluginServices = new ConcurrentHashMap<>();

	final private static Logger LOG = LoggerFactory.getLogger(ApplicationBusPluginServiceHandler.class);


	/**
	 * @param invocationType
	 * @return BundleID of the matching ApplicationBusPlugin
	 */
	public static String getApplicationBusPluginBundleID(final String invocationType) {

		return pluginServices.get(invocationType);
	}

	/**
	 * Bind IApplicationBusPluginService and store InvocationType & BundleID in
	 * local HashMap.
	 *
	 * @param plugin - A AppInvokerPluginService to register.
	 */
	public void bindPluginService(final IApplicationBusPluginService plugin) {

		final List<String> types = plugin.getSupportedInvocationTypes();

		for (final String type : types) {
			pluginServices.put(type, plugin.getRoutingEndpoint());
			ApplicationBusPluginServiceHandler.LOG.debug("Bound IApplicationBusPluginService: {} for Type: {}", plugin.toString(), type);
		}

	}

	/**
	 * Unbind IApplicationBusPluginService.
	 *
	 * @param plugin - A IApplicationBusPluginService to unregister.
	 */
	public void unbindPluginService(final IApplicationBusPluginService plugin) {

		final List<String> types = plugin.getSupportedInvocationTypes();

		for (final String type : types) {
			final Object deletedObject = pluginServices.remove(type);
			if (deletedObject != null) {
				ApplicationBusPluginServiceHandler.LOG.debug("Unbound IApplicationBusPluginService: {} for Type: {}", plugin.toString(), type);
			} else {
				ApplicationBusPluginServiceHandler.LOG.debug("IApplicationBusPluginService {} could not be unbound, because it is not bound!", plugin.toString());
			}
		}
	}
}
