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


    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     * 
     * @param context the plan context for the plugin
     * @param operation the operation for that this plugin should generate invocation logic
     * @param ia the implementation artifact of the given operation
     * @param param2propertyMapping a mapping from operation parameters to variables
     * @param compensationOperation the operation which compensates the given operation
     * @param compensationIa the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to
     *        variables
     * @return true iff generating invocation logic was successful
     */
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping);


    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     * 
     * @param context the plan context for the plugin
     * @param operation the operation for that this plugin should generate invocation logic
     * @param ia the implementation artifact of the given operation
     * @param param2propertyMapping a mapping from operation parameters to variables
     * @param compensationOperation the operation which compensates the given operation
     * @param compensationIa the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to
     *        variables
     * @param phase determines to which phase of the scope the operation logic should be added to
     * @return true iff generating invocation logic was successful
     */
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     * 
     * @param context the plan context for the plugin
     * @param operation the operation for that this plugin should generate invocation logic
     * @param ia the implementation artifact of the given operation
     * @param param2propertyMapping a mapping from operation parameters to variables
     * @param param2PropertyOutputMapping a mapping from operation output parameters to variables
     * @param compensationOperation the operation which compensates the given operation
     * @param compensationIa the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to
     *        variables
     * @return true iff generating invocation logic was successful
     */
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping);

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     * 
     * @param context the plan context for the plugin
     * @param operation the operation for that this plugin should generate invocation logic
     * @param ia the implementation artifact of the given operation
     * @param param2propertyMapping a mapping from operation parameters to variables
     * @param param2PropertyOutputMapping a mapping from operation output parameters to variables
     * @param compensationOperation the operation which compensates the given operation
     * @param compensationIa the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to
     *        variables
     * @param phase determines to which phase of the scope the operation logic should be added to
     * @return true iff generating invocation logic was successful
     */
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          AbstractOperation compensationOperation, AbstractImplementationArtifact compensationIa,
                          Map<AbstractParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);
}
