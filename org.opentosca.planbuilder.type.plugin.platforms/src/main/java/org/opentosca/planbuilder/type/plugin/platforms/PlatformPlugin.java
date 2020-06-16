package org.opentosca.planbuilder.type.plugin.platforms;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of NodeTypes that are platform components such
 * as Clouds (OpenStack, AWS,..) or Devices (Raspberry Pi,..).
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class PlatformPlugin implements IPlanBuilderTypePlugin<BPELPlanContext>,
    IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private final static Logger LOG = LoggerFactory.getLogger(PlatformPlugin.class);

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin Platform Provisioning";

    // INFO: An alternative solution for this plugin would be to implement the lifecycle interface by the node types (install,
    // start,..) which may be implemented with NOPs

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        if (!this.canHandleCreate(nodeTemplate)) {
            return false;
        }
        // available platforms such as Clouds and Devices are not provisioned (as of yet), therefore do
        // nothing here
        return true;
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate);
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
        if (this.isSupportedType(nodeTemplate)) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return new HashSet<AbstractNodeTemplate>();
        } else {
            return null;
        }
    }

    @Override
    public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate) {
        if (this.isSupportedType(nodeTemplate)) {
            // if we can support this type we return an empty set for dependencies, because they are already
            // running
            return new HashSet<AbstractNodeTemplate>();
        } else {
            return null;
        }
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        if (!this.isSupportedType(nodeTemplate)) {
            return false;
        }
        // handle these running components by doing nothing
        return true;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        return this.isSupportedType(nodeTemplate);
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
        return Utils.isSupportedCloudProviderNodeType(type) || Utils.isSupportedOSNodeType(type) || Utils.isSupportedDeviceNodeType(type);
    }
}
