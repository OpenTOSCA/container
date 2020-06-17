package org.opentosca.planbuilder.type.plugin.hardware;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static Logger LOG = LoggerFactory.getLogger(HardwarePlugin.class);

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin Platform Provisioning";

    // INFO: An alternative solution for this plugin would be to implement the lifecycle interface by the node types (install,
    // start,..) which may be implemented with NOPs

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        // available platforms such as Clouds and Devices are not provisioned (as of yet), therefore do
        // nothing here
        return this.canHandleCreate(nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate) & (this.getCreateDependencies(nodeTemplate) != null);
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractNodeTemplate> deps = this.getDependecies(nodeTemplate);
        if (this.isSupportedType(nodeTemplate) & !deps.isEmpty()) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return deps;
        } else {
            return null;
        }
    }

    @Override
    public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractNodeTemplate> deps = this.getDependecies(nodeTemplate);
        if (this.isSupportedType(nodeTemplate) & !deps.isEmpty()) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return deps;
        } else {
            return null;
        }
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        // handle these running components by doing nothing
        return this.canHandleTerminate(nodeTemplate);
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate) & (this.getTerminateDependencies(nodeTemplate) != null);
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private boolean isSupportedType(AbstractNodeTemplate nodeTemplate) {
        QName type = nodeTemplate.getType().getId();
        return Utils.isSupportedHardwareNodeType(type) | Utils.isSupportedDeviceNodeType(type);        
    }

    private Collection<AbstractNodeTemplate> getDependecies(AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        // if it is supported hardware is should be connected to some kind of device
        for (AbstractRelationshipTemplate rel : nodeTemplate.getIngoingRelations()) {
            if (ModelUtils.isCommunicationRelationshipType(rel.getType()) && Utils.isSupportedDeviceNodeType(rel.getSource().getType().getId())) {
                deps.add(rel.getSource());
            }
        }
        return deps;
    }
}
