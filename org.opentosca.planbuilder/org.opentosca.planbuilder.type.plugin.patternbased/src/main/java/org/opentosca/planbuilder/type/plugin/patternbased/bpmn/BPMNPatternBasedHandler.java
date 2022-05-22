package org.opentosca.planbuilder.type.plugin.patternbased.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin;
import org.w3c.dom.Element;

public abstract class BPMNPatternBasedHandler {

    protected static final BPMNInvokerPlugin invoker = new BPMNInvokerPlugin();

    protected boolean invokeOperation(final BPMNPlanContext context, final ConcreteOperationMatching matching,
                                      final TNodeTemplate hostingContainer, Element elementToAppendTo) {

        return invoker.handle(context, hostingContainer.getId(), true, matching.operationName.getName(),
            matching.interfaceName.getName(), transformForInvoker(matching.inputMatching),
            transformForInvoker(matching.outputMatching), elementToAppendTo);
    }

    private Map<String, Variable> transformForInvoker(final Map<TParameter, Variable> map) {
        final Map<String, Variable> newMap = new HashMap<>();
        map.forEach((x, y) -> newMap.put(x.getName(), y));
        return newMap;
    }


    protected boolean invokeArtifactReferenceUpload(BPMNPlanContext context, TArtifactReference ref, TNodeTemplate infraNode) {
        PropertyVariable ip = this.getIpProperty(context, infraNode);
        PropertyVariable user = this.getUserProperty(context, infraNode);
        PropertyVariable key = this.getKeyProperty(context, infraNode);

        if (!(Objects.nonNull(ip) && Objects.nonNull(user) && Objects.nonNull(key))) {
            throw new RuntimeException("Couldn't fetch required variables to enable DA upload with the Remote Manager pattern");
        }

        //return invoker.handleArtifactReferenceUpload(ref, context, ip, user, key, infraNode, context.getPrePhaseElement());
        // @todo hier fehlt was
        return false;
    }


    protected PropertyVariable getIpProperty(BPMNPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineIPPropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected PropertyVariable getUserProperty(BPMNPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected PropertyVariable getKeyProperty(BPMNPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected boolean invokeWithMatching(final BPMNPlanContext context, final TNodeTemplate nodeTemplate,
                                         final TInterface iface, final TOperation op,
                                         final Set<TNodeTemplate> nodesForMatching, Element elementToAppendTo) {
        final ConcreteOperationMatching matching =
            createConcreteOperationMatching(context, createPropertyToParameterMatching(nodesForMatching, iface, op));
        return invokeOperation(context, matching, nodeTemplate, elementToAppendTo);
    }

    protected ConcreteOperationMatching createConcreteOperationMatching(final PlanContext context,
                                                                        final OperationMatching abstractMatching) {

        final ConcreteOperationMatching matching =
            new ConcreteOperationMatching(abstractMatching.interfaceName, abstractMatching.operationName);

        matching.matchedNodes = abstractMatching.matchedNodes;

        for (final TParameter param : abstractMatching.inputMatching.keySet()) {
            boolean added = false;

            for (final TNodeTemplate nodeForMatch : matching.matchedNodes) {
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

        for (final TParameter param : abstractMatching.outputMatching.keySet()) {
            boolean added = false;
            for (final TNodeTemplate nodeForMatch : matching.matchedNodes) {
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

    protected boolean hasCompleteMatching(final Collection<TNodeTemplate> nodesForMatching,
                                          final TInterface ifaceToMatch,
                                          final TOperation operationToMatch) {

        System.out.println("hascompletematching start");
        final OperationMatching matching =
            createPropertyToParameterMatching(nodesForMatching, ifaceToMatch, operationToMatch);

        int inputParamSize = 0;

        if (operationToMatch.getInputParameters() != null) {
            inputParamSize = operationToMatch.getInputParameters().size();
        }

        System.out.println("hascompletematching return: "+ (matching.inputMatching.size() == inputParamSize));
        return matching.inputMatching.size() == inputParamSize;
    }

    protected OperationMatching createPropertyToParameterMatching(final Collection<TNodeTemplate> nodesForMatching,
                                                                  final TInterface ifaceToMatch,
                                                                  final TOperation operationToMatch) {
        final OperationMatching matching = new OperationMatching(ifaceToMatch, operationToMatch);
        final Set<TNodeTemplate> matchedNodes = new HashSet<>();

        if (operationToMatch.getInputParameters() != null) {
            for (final TParameter param : operationToMatch.getInputParameters()) {
                boolean matched = false;

                for (final TNodeTemplate nodeForMatching : nodesForMatching) {
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
        }

        if (operationToMatch.getOutputParameters() != null) {
            for (final TParameter param : operationToMatch.getOutputParameters()) {
                boolean matched = false;

                for (final TNodeTemplate nodeForMatching : nodesForMatching) {
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
        }

        matching.matchedNodes = matchedNodes;
        return matching;
    }

    class OperationMatching {

        TInterface interfaceName;
        TOperation operationName;

        Map<TParameter, String> inputMatching;
        Map<TParameter, String> outputMatching;

        Set<TNodeTemplate> matchedNodes;

        public OperationMatching(final TInterface iface, final TOperation op) {
            this.interfaceName = iface;
            this.operationName = op;
            this.inputMatching = new HashMap<>();
            this.outputMatching = new HashMap<>();
            this.matchedNodes = new HashSet<>();
        }
    }

    class ConcreteOperationMatching {

        TInterface interfaceName;
        TOperation operationName;
        Map<TParameter, Variable> inputMatching = new HashMap<>();
        Map<TParameter, Variable> outputMatching = new HashMap<>();
        Set<TNodeTemplate> matchedNodes;

        public ConcreteOperationMatching(final TInterface iface, final TOperation op) {
            this.interfaceName = iface;
            this.operationName = op;
            this.inputMatching = new HashMap<>();
            this.outputMatching = new HashMap<>();
            this.matchedNodes = new HashSet<>();
        }
    }
}
