package org.opentosca.planbuilder.type.plugin.ubuntuvm.bpel;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

/**
 * <p>
 * This class implements the logic to provision an EC2VM Stack, consisting of the NodeTypes
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}EC2, {http://www.example.com/tosca/ServiceTemplates/EC2VM}VM,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}Ubuntu.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public interface UbuntuVmTypePluginHandler<T extends PlanContext> {

    /**
     * Adds fragments to provision a VM
     *
     * @param context      a TemplatePlanContext for a EC2, VM or Ubuntu Node
     * @param nodeTemplate the NodeTemplate on which the fragments are used
     * @return true iff adding the fragments was successful
     */
    boolean handle(final T context, final AbstractNodeTemplate nodeTemplate);

    boolean handleCreateWithCloudProviderInterface(final T context, final AbstractNodeTemplate nodeTemplate);

    /**
     * Provisions a Docker Ubuntu Container on a DockerEngine
     *
     * @param context      a TemplatePlanContext for a DockerEngine or Ubuntu Node
     * @param nodeTemplate the NodeTemplate on which the fragments are used
     * @return true iff provisioning the container was successful
     */
    boolean handleWithDockerEngineInterface(final T context, final AbstractNodeTemplate nodeTemplate);
}
