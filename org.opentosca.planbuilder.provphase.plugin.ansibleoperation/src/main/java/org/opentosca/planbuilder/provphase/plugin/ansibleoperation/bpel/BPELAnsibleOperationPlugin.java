package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel;

import java.util.Map;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel.handler.BPELAnsibleOperationPluginHandler;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.core.AnsibleOperationPlugin;

/**
 * <p>
 * This class implements a ProvPhase Plugin, in particular to enable provisioning with ansible
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public class BPELAnsibleOperationPlugin extends AnsibleOperationPlugin<BPELPlanContext> {

    private final BPELAnsibleOperationPluginHandler handler = new BPELAnsibleOperationPluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia) {
        return this.handler.handle(context, operation, ia);
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping) {
        return this.handler.handle(context, operation, ia, param2propertyMapping);
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final BPELScopePhaseType phase) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final Map<AbstractParameter, Variable> param2PropertyOutputMapping) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          final BPELScopePhaseType phase) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
