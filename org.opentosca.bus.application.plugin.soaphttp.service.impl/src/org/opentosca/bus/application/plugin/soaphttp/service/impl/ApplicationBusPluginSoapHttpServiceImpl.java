package org.opentosca.bus.application.plugin.soaphttp.service.impl;

import java.util.Arrays;
import java.util.List;

import org.opentosca.bus.application.plugin.service.IApplicationBusPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Bus-Plug-in for invoking a service with a SOAP message over HTTP.
 * <br>
 *
 * The Plug-in gets needed information (like endpoint of the service or
 * operation to invoke) from the Application Bus and creates a SOAP message out
 * of it. If needed the Plug-in parses the WSDL of the service. The Plug-in
 * supports synchronous request-response communication, asynchronous
 * communication with callbacks and one-way invocation.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
public class ApplicationBusPluginSoapHttpServiceImpl implements IApplicationBusPluginService {
	
	
	final private static Logger LOG = LoggerFactory.getLogger(ApplicationBusPluginSoapHttpServiceImpl.class);

	// Supported types defined in messages.properties.
	private static final List<String> invocationTypes = Arrays.asList("SOAP/HTTP");

	// Routing endpoint of the Application Bus bundle
	public static final String ENDPOINT = "direct-vm:" + Activator.getBundleID();


	@Override
	public List<String> getSupportedInvocationTypes() {
		ApplicationBusPluginSoapHttpServiceImpl.LOG.debug("Supported Types: {}.", ApplicationBusPluginSoapHttpServiceImpl.invocationTypes);

		return ApplicationBusPluginSoapHttpServiceImpl.invocationTypes;
	}

	@Override
	public String getRoutingEndpoint() {
		return ApplicationBusPluginSoapHttpServiceImpl.ENDPOINT;
	}
}
