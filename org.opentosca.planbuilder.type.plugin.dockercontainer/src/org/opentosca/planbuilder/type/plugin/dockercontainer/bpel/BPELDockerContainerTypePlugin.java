package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
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

	private final BPELDockerContainerTypePluginHandler handler = new BPELDockerContainerTypePluginHandler();

	@Override
	public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {

		if (this.canHandleCreate(nodeTemplate)) {
			return this.handler.handleCreate(templateContext);
		}
		return false;
	}

	@Override
	public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
		return false;
	}

	@Override
	public Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate) {
		Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
		deps.add(this.getDockerEngineNode(nodeTemplate));
		return deps;
	}

	@Override
	public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate) {
		return null;
	}

	@Override
	public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
		if (this.canHandleTerminate(nodeTemplate)) {
			return this.handler.handleTerminate(templateContext);
		}
		return false;
	}
	
	@Override
	public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
		// never handles relationshipTemplates
		return false;
	}

	
	@Override
	public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
		// never handles relationshipTemplates
		return false;
	}

}
