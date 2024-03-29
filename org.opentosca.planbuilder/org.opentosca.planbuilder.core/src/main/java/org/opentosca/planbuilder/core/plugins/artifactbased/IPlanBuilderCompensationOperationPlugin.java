package org.opentosca.planbuilder.core.plugins.artifactbased;

import java.util.Map;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;

public interface IPlanBuilderCompensationOperationPlugin<T extends PlanContext>
    extends IPlanBuilderProvPhaseParamOperationPlugin<T> {

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     *
     * @param context                           the plan context for the plugin
     * @param operation                         the operation for that this plugin should generate invocation logic
     * @param ia                                the implementation artifact of the given operation
     * @param param2propertyMapping             a mapping from operation parameters to variables
     * @param compensationOperation             the operation which compensates the given operation
     * @param compensationIa                    the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to variables
     * @return true iff generating invocation logic was successful
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   TOperation compensationOperation, TImplementationArtifact compensationIa,
                   Map<TParameter, Variable> compensationParam2VariableMapping);

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     *
     * @param context                           the plan context for the plugin
     * @param operation                         the operation for that this plugin should generate invocation logic
     * @param ia                                the implementation artifact of the given operation
     * @param param2propertyMapping             a mapping from operation parameters to variables
     * @param compensationOperation             the operation which compensates the given operation
     * @param compensationIa                    the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to variables
     * @param phase                             determines to which phase of the scope the operation logic should be
     *                                          added to
     * @return true iff generating invocation logic was successful
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   TOperation compensationOperation, TImplementationArtifact compensationIa,
                   Map<TParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     *
     * @param context                           the plan context for the plugin
     * @param operation                         the operation for that this plugin should generate invocation logic
     * @param ia                                the implementation artifact of the given operation
     * @param param2propertyMapping             a mapping from operation parameters to variables
     * @param param2PropertyOutputMapping       a mapping from operation output parameters to variables
     * @param compensationOperation             the operation which compensates the given operation
     * @param compensationIa                    the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to variables
     * @return true iff generating invocation logic was successful
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   Map<TParameter, Variable> param2PropertyOutputMapping,
                   TOperation compensationOperation, TImplementationArtifact compensationIa,
                   Map<TParameter, Variable> compensationParam2VariableMapping);

    /**
     * Create BPEL code to invoke given method and additionally add compensation logic
     *
     * @param context                           the plan context for the plugin
     * @param operation                         the operation for that this plugin should generate invocation logic
     * @param ia                                the implementation artifact of the given operation
     * @param param2propertyMapping             a mapping from operation parameters to variables
     * @param param2PropertyOutputMapping       a mapping from operation output parameters to variables
     * @param compensationOperation             the operation which compensates the given operation
     * @param compensationIa                    the implementation artifact of the compensation operation
     * @param compensationParam2VariableMapping a mapping from compensation operation parameters to variables
     * @param phase                             determines to which phase of the scope the operation logic should be
     *                                          added to
     * @return true iff generating invocation logic was successful
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   Map<TParameter, Variable> param2PropertyOutputMapping,
                   TOperation compensationOperation, TImplementationArtifact compensationIa,
                   Map<TParameter, Variable> compensationParam2VariableMapping, BPELScopePhaseType phase);
}
