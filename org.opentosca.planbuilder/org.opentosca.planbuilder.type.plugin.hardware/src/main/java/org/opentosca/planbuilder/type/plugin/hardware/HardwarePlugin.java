package org.opentosca.planbuilder.type.plugin.hardware;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of NodeTypes that are hardware components such
 * as Sensors and Actuators.
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class HardwarePlugin implements IPlanBuilderTypePlugin<BPELPlanContext>,
    IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin Platform Provisioning";

    // INFO: An alternative solution for this plugin would be to implement the lifecycle interface by the node types (install,
    // start,..) which may be implemented with NOPs

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // available platforms such as Clouds and Devices are not provisioned (as of yet), therefore do
        // nothing here
        return this.canHandleCreate(templateContext.getCsar(), nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate) & (this.getCreateDependencies(nodeTemplate, csar) != null);
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public Collection<TNodeTemplate> getCreateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        Collection<TNodeTemplate> deps = this.getDependecies(nodeTemplate, csar);
        if (this.isSupportedType(nodeTemplate) & !deps.isEmpty()) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return deps;
        } else {
            return null;
        }
    }

    @Override
    public Collection<TNodeTemplate> getTerminateDependencies(TNodeTemplate nodeTemplate, Csar csar) {
        Collection<TNodeTemplate> deps = this.getDependecies(nodeTemplate, csar);
        if (this.isSupportedType(nodeTemplate) & !deps.isEmpty()) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return deps;
        } else {
            return null;
        }
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TNodeTemplate nodeTemplate) {
        // handle these running components by doing nothing
        return this.canHandleTerminate(templateContext.getCsar(), nodeTemplate);
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate) & (this.getTerminateDependencies(nodeTemplate, csar) != null);
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private boolean isSupportedType(TNodeTemplate nodeTemplate) {
        QName type = nodeTemplate.getType();
        return Utils.isSupportedHardwareNodeType(type) | Utils.isSupportedDeviceNodeType(type);
    }

    private Collection<TNodeTemplate> getDependecies(TNodeTemplate nodeTemplate, Csar csar) {
        Collection<TNodeTemplate> deps = new HashSet<TNodeTemplate>();
        // if it is supported hardware is should be connected to some kind of device
        for (TRelationshipTemplate rel : ModelUtils.getIngoingRelations(nodeTemplate, csar)) {
            TNodeTemplate source = ModelUtils.getSource(rel, csar);
            if (ModelUtils.isCommunicationRelationshipType(rel.getType()) && Utils.isSupportedDeviceNodeType(ModelUtils.findNodeType(source, csar).getQName())) {
                deps.add(source);
            }
        }
        return deps;
    }
}
