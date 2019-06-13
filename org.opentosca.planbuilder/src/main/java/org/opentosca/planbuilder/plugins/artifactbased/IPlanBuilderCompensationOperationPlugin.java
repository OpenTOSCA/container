package org.opentosca.planbuilder.plugins.artifactbased;

import java.util.Map;

import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;

public interface IPlanBuilderCompensationOperationPlugin<T extends PlanContext>
                                                        extends IPlanBuilderProvPhaseParamOperationPlugin<T> {
    
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);
}
