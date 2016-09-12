package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.planinvocationengine.service.IPlanInvocationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInvocationEngineHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(ToscaServiceHandler.class);
	
	public static IPlanInvocationEngine planInvocationEngine;
	
	
	public void bindIPlanInvocationEngineService(IPlanInvocationEngine service) {
		PlanInvocationEngineHandler.LOG.debug("ContainerApi: Bind IPlanInvocationEngine");
		PlanInvocationEngineHandler.planInvocationEngine = service;
	}
	
	public void unbindIPlanInvocationEngineService(IPlanInvocationEngine service) {
		PlanInvocationEngineHandler.LOG.debug("ContainerApi: Unbind IPlanInvocationEngine");
		PlanInvocationEngineHandler.planInvocationEngine = null;
	}
	
}
