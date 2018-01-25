package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler.BPELDockerContainerTypePluginHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;

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
public class BPELDockerContainerTypePlugin extends DockerContainerTypePlugin<BPELPlanContext> {

	private BPELDockerContainerTypePluginHandler handler = new BPELDockerContainerTypePluginHandler();

	@Override
	public boolean handle(BPELPlanContext templateContext) {
		if (templateContext.getNodeTemplate() == null) {
			// error
			return false;
		} else {
			if (this.canHandle(templateContext.getNodeTemplate())) {
				return this.handler.handle(templateContext);
			}
		}
		return false;
	}

}
