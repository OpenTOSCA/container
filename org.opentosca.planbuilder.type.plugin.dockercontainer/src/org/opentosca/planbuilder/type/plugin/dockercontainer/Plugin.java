package org.opentosca.planbuilder.type.plugin.dockercontainer;

import java.util.LinkedList;

import org.opentosca.planbuilder.model.plan.bpmn4tosca.BPMN4ToscaElement;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.handler.BPELDockerContainerHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.handler.BPMN4ToscaDockerContainerHandler;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP
 * Server with the OpenTOSCA Container Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public final class Plugin extends AbstractPlugin {

	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		BPELDockerContainerHandler handler = new BPELDockerContainerHandler();
		return checkNodeTemplate(templateContext.getNodeTemplate()) && handler.handle(templateContext);
	}

	@Override
	public boolean handle(LinkedList<BPMN4ToscaElement> elements, AbstractNodeTemplate nodeTemplate) {
		BPMN4ToscaDockerContainerHandler handler = new BPMN4ToscaDockerContainerHandler();
		return checkNodeTemplate(nodeTemplate) && handler.handle(elements, nodeTemplate);
	}
}
