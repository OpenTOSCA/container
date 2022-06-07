package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.type.plugin.dockercontainer.bpel.handler.BPELDockerContainerTypePluginHandler;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;

/**
 * <p>
 * This class represents a generic plugin to install a PhpModule on Apache HTTP Server with the OpenTOSCA Container
 * Invoker Service
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELDockerContainerTypePlugin extends DockerContainerTypePlugin<BPELPlanContext> {

    private final BPELDockerContainerTypePluginHandler handler = new BPELDockerContainerTypePluginHandler();

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {

        boolean check = false;
        if (this.canHandleCreate(templateContext.getCsar(), nodeTemplate, PlanLanguage.BPEL)) {
            check = this.handler.handleCreate(templateContext);
        }

        if (check) {
            templateContext.addUsedOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER, templateContext.getCsar());
        }

        return check;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public Collection<TNodeTemplate> getCreateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        Collection<TNodeTemplate> deps = new HashSet<TNodeTemplate>();
        deps.add(getDockerEngineNode(nodeTemplate, csar));
        return deps;
    }

    @Override
    public Collection<TNodeTemplate> getTerminateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        return null;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        boolean check = false;
        if (this.canHandleTerminate(templateContext.getCsar(), nodeTemplate)) {
            check = this.handler.handleTerminate(templateContext);
        }

        if (check) {
            templateContext.addUsedOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER, templateContext.getCsar());
        }

        return check;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
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
