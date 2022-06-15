package org.opentosca.planbuilder.type.plugin.dockercontainer.bpmn;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerBPMNTypePlugin;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP Server with the OpenTOSCA Container
 * Invoker Service
 * </p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPMNDockerContainerTypePlugin extends DockerContainerBPMNTypePlugin<BPMNPlanContext> {

    private final BPMNDockerContainerTypePluginHandler handler = new BPMNDockerContainerTypePluginHandler();

    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        boolean check = false;
        if (this.canHandleCreate(templateContext.getCsar(), nodeTemplate)) {
            check = this.handler.handleCreate(templateContext);
        }
        return check;
    }

    @Override
    public boolean handleCreate(BPMNPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public Collection<TNodeTemplate> getCreateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        Collection<TNodeTemplate> deps = new HashSet<>();
        deps.add(getDockerEngineNode(nodeTemplate, csar));
        return deps;
    }

    @Override
    public Collection<TNodeTemplate> getTerminateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        return null;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TNodeTemplate nodeTemplate) {
        boolean check = false;
        if (this.canHandleTerminate(templateContext.getCsar(), nodeTemplate)) {
            check = this.handler.handleTerminate(templateContext);
        }
        return check;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public int getPriority() {
        // specific first than generic handling
        return 0;
    }
}

