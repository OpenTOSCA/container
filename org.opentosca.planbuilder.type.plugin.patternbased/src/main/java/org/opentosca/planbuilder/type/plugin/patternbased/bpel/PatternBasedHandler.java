package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;

public abstract class PatternBasedHandler {

    class OperationMatching {

        AbstractInterface interfaceName;
        AbstractOperation operationName;

        Map<AbstractParameter, String> inputMatching;
        Map<AbstractParameter, String> outputMatching;

        Set<AbstractNodeTemplate> matchedNodes;

        public OperationMatching(final AbstractInterface iface, final AbstractOperation op) {
            this.interfaceName = iface;
            this.operationName = op;
            this.inputMatching = new HashMap<>();
            this.outputMatching = new HashMap<>();
            this.matchedNodes = new HashSet<>();
        }
    }

    class ConcreteOperationMatching {

        AbstractInterface interfaceName;
        AbstractOperation operationName;
        Map<AbstractParameter, Variable> inputMatching = new HashMap<>();
        Map<AbstractParameter, Variable> outputMatching = new HashMap<>();
        Set<AbstractNodeTemplate> matchedNodes;

        public ConcreteOperationMatching(final AbstractInterface iface, final AbstractOperation op) {
            this.interfaceName = iface;
            this.operationName = op;
            this.inputMatching = new HashMap<>();
            this.outputMatching = new HashMap<>();
            this.matchedNodes = new HashSet<>();
        }
    }

    protected static final BPELInvokerPlugin invoker = new BPELInvokerPlugin();

    protected boolean invokeOperation(final BPELPlanContext context, final ConcreteOperationMatching matching,
                                      final AbstractNodeTemplate hostingContainer) {

        return invoker.handle(context, hostingContainer.getId(), true, matching.operationName.getName(),
                              matching.interfaceName.getName(), transformForInvoker(matching.inputMatching),
                              transformForInvoker(matching.outputMatching), BPELScopePhaseType.PROVISIONING);
    }

    private Map<String, Variable> transformForInvoker(final Map<AbstractParameter, Variable> map) {
        final Map<String, Variable> newMap = new HashMap<>();
        map.forEach((x, y) -> newMap.put(x.getName(), y));
        return newMap;
    }

    protected boolean invokeWithMatching(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                                         final AbstractInterface iface, final AbstractOperation op,
                                         final Set<AbstractNodeTemplate> nodesForMatching) {
        final ConcreteOperationMatching matching =
            createConcreteOperationMatching(context, createPropertyToParameterMatching(nodesForMatching, iface, op));
        return invokeOperation(context, matching, nodeTemplate);
    }

    protected ConcreteOperationMatching createConcreteOperationMatching(final PlanContext context,
                                                                        final OperationMatching abstractMatching) {

        final ConcreteOperationMatching matching =
            new ConcreteOperationMatching(abstractMatching.interfaceName, abstractMatching.operationName);

        matching.matchedNodes = abstractMatching.matchedNodes;

        for (final AbstractParameter param : abstractMatching.inputMatching.keySet()) {
            boolean added = false;

            for (final AbstractNodeTemplate nodeForMatch : matching.matchedNodes) {
                for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
                    if (abstractMatching.inputMatching.get(param).equals(nodePropName)) {
                        matching.inputMatching.put(param, context.getPropertyVariable(nodeForMatch, nodePropName));
                        added = true;
                        break;
                    }
                }
                if (added) {
                    break;
                }
            }
        }

        for (final AbstractParameter param : abstractMatching.outputMatching.keySet()) {
            boolean added = false;
            for (final AbstractNodeTemplate nodeForMatch : matching.matchedNodes) {
                for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
                    if (abstractMatching.outputMatching.get(param).equals(nodePropName)) {
                        matching.outputMatching.put(param, context.getPropertyVariable(nodeForMatch, nodePropName));
                        added = true;
                        break;
                    }
                }
                if (added) {
                    break;
                }
            }
        }

        return matching;
    }

    protected boolean hasCompleteMatching(final Collection<AbstractNodeTemplate> nodesForMatching,
                                          final AbstractInterface ifaceToMatch,
                                          final AbstractOperation operationToMatch) {

        final OperationMatching matching =
            createPropertyToParameterMatching(nodesForMatching, ifaceToMatch, operationToMatch);

        if (matching.inputMatching.size() == operationToMatch.getInputParameters().size()) {
            return true;
        }

        return false;
    }

    protected OperationMatching createPropertyToParameterMatching(final Collection<AbstractNodeTemplate> nodesForMatching,
                                                                  final AbstractInterface ifaceToMatch,
                                                                  final AbstractOperation operationToMatch) {
        final OperationMatching matching = new OperationMatching(ifaceToMatch, operationToMatch);
        final Set<AbstractNodeTemplate> matchedNodes = new HashSet<>();

        for (final AbstractParameter param : operationToMatch.getInputParameters()) {
            boolean matched = false;

            for (final AbstractNodeTemplate nodeForMatching : nodesForMatching) {
                for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                    if (param.getName().equals(propName)) {
                        matching.inputMatching.put(param, propName);
                        matched = true;
                        matchedNodes.add(nodeForMatching);
                        break;
                    }
                }
                if (matched) {
                    break;
                }
            }
        }

        for (final AbstractParameter param : operationToMatch.getOutputParameters()) {
            boolean matched = false;

            for (final AbstractNodeTemplate nodeForMatching : nodesForMatching) {
                for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                    if (param.getName().equals(propName)) {
                        matching.outputMatching.put(param, propName);
                        matched = true;
                        matchedNodes.add(nodeForMatching);
                        break;
                    }
                }
                if (matched) {
                    break;
                }
            }
        }

        matching.matchedNodes = matchedNodes;
        return matching;
    }
}
