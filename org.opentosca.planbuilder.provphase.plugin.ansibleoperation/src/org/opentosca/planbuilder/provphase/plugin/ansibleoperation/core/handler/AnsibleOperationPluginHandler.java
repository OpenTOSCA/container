package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.core.handler;

import java.util.Map;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes Ansible Playbooks on
 * remote machine. The class assumes that the playbook that must be called are already uploaded to
 * the appropriate path. For example by the ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public interface AnsibleOperationPluginHandler<T extends PlanContext> {

    /**
     * Adds logic to the Plan to call a Ansible Playbook on a remote machine
     *
     * @param context the TemplatePlanContext where the logical provisioning operation is called
     * @param operation the operation to call
     * @param ia the ia that implements the operation
     * @return true iff adding BPEL Fragment was successful
     */
    public boolean handle(final T templateContext, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia);

    public boolean handle(final T context, final AbstractOperation operation, final AbstractImplementationArtifact ia,
                    final Map<AbstractParameter, Variable> param2propertyMapping);

}
