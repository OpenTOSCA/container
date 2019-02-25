package org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler;

import org.opentosca.planbuilder.plugins.context.PlanContext;

/**
 * <p>
 * This class contains all the logic to add BPEL Code which installs a PhpModule on an Apache HTTP
 * Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public interface DockerContainerTypePluginHandler<T extends PlanContext> {
    public boolean handle(final T context);
}
