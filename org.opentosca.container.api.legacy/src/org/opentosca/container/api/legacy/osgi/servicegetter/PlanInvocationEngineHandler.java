package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInvocationEngineHandler {

	final private static Logger LOG = LoggerFactory.getLogger(ToscaServiceHandler.class);

	public static IPlanInvocationEngine planInvocationEngine;


	public void bindIPlanInvocationEngineService(final IPlanInvocationEngine service) {
		PlanInvocationEngineHandler.LOG.debug("ContainerApi: Bind IPlanInvocationEngine");
		PlanInvocationEngineHandler.planInvocationEngine = service;
	}

	public void unbindIPlanInvocationEngineService(final IPlanInvocationEngine service) {
		PlanInvocationEngineHandler.LOG.debug("ContainerApi: Unbind IPlanInvocationEngine");
		PlanInvocationEngineHandler.planInvocationEngine = null;
	}

}
