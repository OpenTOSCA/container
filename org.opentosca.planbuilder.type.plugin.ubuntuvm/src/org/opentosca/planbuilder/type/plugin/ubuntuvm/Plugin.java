package org.opentosca.planbuilder.type.plugin.ubuntuvm;

import java.util.LinkedList;

import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElement;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.impl.BPELUbuntuVmHandler;
import org.opentosca.planbuilder.type.plugin.ubuntuvm.handler.impl.BPMN4ToscaUbuntuVmHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class represents a generic plugin containing bpel logic to start a
 * virtual machine instance with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class Plugin extends AbstractPlugin {

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);

	@Override
	public boolean handle(final TemplatePlanContext templateContext) {
		LOG.debug("Handling BPEL");
		final BPELUbuntuVmHandler handler = new BPELUbuntuVmHandler(templateContext);
		final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		return handler.handle(nodeTemplate);
	}

	@Override
	public boolean handle(LinkedList<BPMN4ToscaElement> elements, AbstractNodeTemplate nodeTemplate) {
		LOG.debug("Handling BPMN4Tosca");
		final BPMN4ToscaUbuntuVmHandler handler = new BPMN4ToscaUbuntuVmHandler(elements);
		return handler.handle(nodeTemplate);
	}

}