package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.w3c.dom.Element;

public abstract class PatternBasedHandler {

    protected static final BPELInvokerPlugin invoker = new BPELInvokerPlugin();

    protected boolean invokeOperation(final BPELPlanContext context, final ConcreteOperationMatching matching,
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

    protected boolean invokeArtifactReferenceUpload(BPELPlanContext context, TArtifactReference ref, TNodeTemplate infraNode) {
        PropertyVariable ip = this.getIpProperty(context, infraNode);
        PropertyVariable user = this.getUserProperty(context, infraNode);
        PropertyVariable key = this.getKeyProperty(context, infraNode);

        if (!(Objects.nonNull(ip) && Objects.nonNull(user) && Objects.nonNull(key))) {
            throw new RuntimeException("Couldn't fetch required variables to enable DA upload with the Remote Manager pattern");
        }

        return invoker.handleArtifactReferenceUpload(ref, context, ip, user, key, infraNode, context.getPrePhaseElement());
    }

    protected PropertyVariable getIpProperty(BPELPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineIPPropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected PropertyVariable getUserProperty(BPELPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineLoginUserNamePropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected PropertyVariable getKeyProperty(BPELPlanContext context, TNodeTemplate node) {
        for (String propName : Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            PropertyVariable propVar = context.getPropertyVariable(propName);
            if (propVar != null) {
                return propVar;
            }
        }
        return null;
    }

    protected boolean invokeWithMatching(final BPELPlanContext context, final TNodeTemplate nodeTemplate,
                                         final TInterface iface, final TOperation op,
                                         final Set<TNodeTemplate> nodesForMatching, Element elementToAppendTo) {
        OperationMatching propertyToParameterMatching = createPropertyToParameterMatching(nodesForMatching, iface, op);
        DeployTechDescriptorOperationMatching deployDescriptorPropertyToParameterMatching =
            createDeployDescriptorPropertyToParameterMatching(context, propertyToParameterMatching, nodesForMatching);
        final ConcreteOperationMatching matching =
            createConcreteOperationMatching(context, deployDescriptorPropertyToParameterMatching);
        return invokeOperation(context, matching, nodeTemplate, elementToAppendTo);
    }

    protected ConcreteOperationMatching createConcreteOperationMatching(final PlanContext context,
                                                                        final DeployTechDescriptorOperationMatching abstractMatching) {

        final ConcreteOperationMatching matching =
            new ConcreteOperationMatching(abstractMatching.interfaceName, abstractMatching.operationName);

        matching.matchedNodes = abstractMatching.matchedNodes;

        for (final TParameter param : abstractMatching.abstractInputMatching.keySet()) {
            boolean added = false;

            for (final TNodeTemplate nodeForMatch : matching.matchedNodes) {
                for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
                    if (abstractMatching.abstractInputMatching.get(param).equals(nodePropName)) {
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

        for (final TParameter param : abstractMatching.abstractOutputMatching.keySet()) {
            boolean added = false;
            for (final TNodeTemplate nodeForMatch : matching.matchedNodes) {
                for (final String nodePropName : ModelUtils.getPropertyNames(nodeForMatch)) {
                    if (abstractMatching.abstractOutputMatching.get(param).equals(nodePropName)) {
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

        final OperationMatching matching =
            createPropertyToParameterMatching(nodesForMatching, ifaceToMatch, operationToMatch);

        int inputParamSize = 0;

        if (operationToMatch.getInputParameters() != null) {
            inputParamSize = operationToMatch.getInputParameters().size();
        }

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
                boolean isIp = false;
                if (Utils.isSupportedVirtualMachineIPProperty(param.getName())) {
                    isIp = true;
                }

                for (final TNodeTemplate nodeForMatching : nodesForMatching) {
                    for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                        // in case we have an ip property to match we do it differently
                        if (isIp && Utils.isSupportedVirtualMachineIPProperty(propName)) {
                            matching.inputMatching.put(param, propName);
                            matched = true;
                            matchedNodes.add(nodeForMatching);
                            break;
                        }
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

    /**
     * Try to find a parameter matching in the deployment technology descriptors. For every previously unmatched
     * operation parameters a matching property in the deployment technology descriptors is searched.
     *
     * @param context          The context, providing the deployment technology descriptor variable mapping
     * @param abstractMatching The previous node property matching
     * @param nodesForMatching Set of nodes that can be searched for deployment technology descriptor property matches
     * @return The computed parameter matching
     */
    private DeployTechDescriptorOperationMatching createDeployDescriptorPropertyToParameterMatching(final BPELPlanContext context,
                                                                                                    final OperationMatching abstractMatching,
                                                                                                    Set<TNodeTemplate> nodesForMatching) {
        final DeployTechDescriptorOperationMatching matching =
            new DeployTechDescriptorOperationMatching(abstractMatching);
        TOperation operation = matching.operationName;

        if (operation.getInputParameters() != null) {
            Set<TParameter> unmappedInputParams =
                operation
                    .getInputParameters()
                    .stream()
                    .filter(tParameter -> !matching.abstractInputMatching.containsKey(tParameter))
                    .collect(Collectors.toSet());

            for (TParameter unmappedInputParam : unmappedInputParams) {
                for (TNodeTemplate nodeToMatch : nodesForMatching) {
                    PropertyVariable propVar = context.getPropertyVariable(nodeToMatch, unmappedInputParam.getName());
                    if (propVar != null) {
                        matching.concreteInputMatching.put(unmappedInputParam, propVar);
                        matching.matchedNodes.add(nodeToMatch);
                    }
                }
            }
        }

        if (operation.getOutputParameters() != null) {
            Set<TParameter> unmappedOutputParams =
                operation
                    .getOutputParameters()
                    .stream()
                    .filter(tParameter -> !matching.abstractOutputMatching.containsKey(tParameter))
                    .collect(Collectors.toSet());

            for (TParameter unmappedOutputParam : unmappedOutputParams) {
                for (TNodeTemplate nodeToMatch : nodesForMatching) {
                    PropertyVariable propVar = context.getPropertyVariable(nodeToMatch, unmappedOutputParam.getName());
                    if (propVar != null) {
                        matching.concreteOutputMatching.put(unmappedOutputParam, propVar);
                        matching.matchedNodes.add(nodeToMatch);
                    }
                }
            }
        }

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

    class DeployTechDescriptorOperationMatching {

        TInterface interfaceName;
        TOperation operationName;

        Map<TParameter, String> abstractInputMatching;
        Map<TParameter, String> abstractOutputMatching;

        Map<TParameter, Variable> concreteInputMatching;
        Map<TParameter, Variable> concreteOutputMatching;

        Set<TNodeTemplate> matchedNodes;

        public DeployTechDescriptorOperationMatching(final OperationMatching abstractMatching) {
            this.interfaceName = abstractMatching.interfaceName;
            this.operationName = abstractMatching.operationName;
            this.abstractInputMatching = abstractMatching.inputMatching;
            this.abstractOutputMatching = abstractMatching.outputMatching;
            this.concreteInputMatching = new HashMap<>();
            this.concreteOutputMatching = new HashMap<>();
            this.matchedNodes = abstractMatching.matchedNodes;
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
