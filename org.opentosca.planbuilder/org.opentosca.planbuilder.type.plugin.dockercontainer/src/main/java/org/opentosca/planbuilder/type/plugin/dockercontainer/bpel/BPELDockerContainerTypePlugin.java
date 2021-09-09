package org.opentosca.planbuilder.type.plugin.dockercontainer.bpel;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
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
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {

        boolean check = false;
        if (this.canHandleCreate(templateContext.getCsar(), nodeTemplate)) {
            check = this.handler.handleCreate(templateContext);
        }

        if (check) {
            templateContext.addUsedOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER);
        }

        return check;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        deps.add(getDockerEngineNode(nodeTemplate));
        return deps;
    }

    @Override
    public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar) {
        return null;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        boolean check = false;
        if (this.canHandleTerminate(templateContext.getCsar(), nodeTemplate)) {
            check = this.handler.handleTerminate(templateContext);
        }

        if (check) {
            templateContext.addUsedOperation(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER);
        }

        return check;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public int getPriority() {
        // specific first than generic handling
        return 0;
    }
}
