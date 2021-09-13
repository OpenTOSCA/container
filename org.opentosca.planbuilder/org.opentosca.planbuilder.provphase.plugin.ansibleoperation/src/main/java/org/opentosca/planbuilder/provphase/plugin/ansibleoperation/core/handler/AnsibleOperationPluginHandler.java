package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.core.handler;

import java.util.Map;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;

/**
 * <p>
 * This class is contains the logic to add BPEL Fragments, which executes Ansible Playbooks on remote machine. The class
 * assumes that the playbook that must be called are already uploaded to the appropriate path. For example by the
 * ScriptIAOnLinux Plugin
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 */
public interface AnsibleOperationPluginHandler<T extends PlanContext> {

    /**
     * Adds logic to the Plan to call a Ansible Playbook on a remote machine
     *
     * @param templateContext   the TemplatePlanContext where the logical provisioning operation is called
     * @param operation the operation to call
     * @param ia        the ia that implements the operation
     * @return true iff adding BPEL Fragment was successful
     */
    boolean handle(final T templateContext, final TOperation operation,
                   final TImplementationArtifact ia);

    boolean handle(final T context, final TOperation operation, final TImplementationArtifact ia,
                   final Map<TParameter, Variable> param2propertyMapping);
}
