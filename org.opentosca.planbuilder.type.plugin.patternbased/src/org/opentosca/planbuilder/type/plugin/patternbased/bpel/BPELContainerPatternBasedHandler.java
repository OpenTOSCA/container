package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;

public class BPELContainerPatternBasedHandler {

    private static final String containerPatternInterfaceName = "http://opentosca.org/interfaces/pattern/container";
    private static final String containerPatternCreateOperationName = "create";
    private static final BPELInvokerPlugin invoker = new BPELInvokerPlugin();

    class OperationMatching {
        Map<AbstractParameter, String> inputMatching = new HashMap<>();
        Map<AbstractParameter, String> outputMatching = new HashMap<>();
    }

    class ConcreteOperationMatching {
        Map<AbstractParameter, Variable> inputMatching = new HashMap<>();
        Map<AbstractParameter, Variable> outputMatching = new HashMap<>();
    }

    public boolean handle(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

        final AbstractNodeTemplate hostingContainer = getHostingNode(nodeTemplate);
        final AbstractOperation createOperation = getContainerPatternCreateMethod(hostingContainer);
        final ConcreteOperationMatching matching =
            createConcreteOperationMatching(context,
                                            createPropertyToParameterMatching(nodeTemplate, hostingContainer,
                                                                              createOperation),
                                            nodeTemplate, hostingContainer);
        return invokeOperation(context, matching, hostingContainer);
    }



    public boolean isProvisionableByContainerPattern(final AbstractNodeTemplate nodeTemplate) {
        // find hosting node
        AbstractNodeTemplate hostingNode = null;
        if ((hostingNode = getHostingNode(nodeTemplate)) == null) {
            return false;
        }

        if (!hasContainerPatternCreateMethod(hostingNode)) {
            return false;
        }

        if (!hasCompleteMatching(nodeTemplate, hostingNode, getContainerPatternCreateMethod(hostingNode))) {
            return false;
        }

        return true;
    }

    private boolean invokeOperation(final BPELPlanContext context, final ConcreteOperationMatching matching,
                                    final AbstractNodeTemplate hostingContainer) {

        return invoker.handle(context, hostingContainer.getId(), true, containerPatternCreateOperationName,
                              containerPatternInterfaceName, "planCallbackAddress_invoker",
                              transformForInvoker(matching.inputMatching), transformForInvoker(matching.outputMatching),
                              BPELScopePhaseType.PROVISIONING);
    }

    private Map<String, Variable> transformForInvoker(final Map<AbstractParameter, Variable> map) {
        final Map<String, Variable> newMap = new HashMap<>();
        map.forEach((x, y) -> newMap.put(x.getName(), y));
        return newMap;
    }

    private ConcreteOperationMatching createConcreteOperationMatching(final BPELPlanContext context,
                                                                      final OperationMatching abstractMatching,
                                                                      final AbstractNodeTemplate nodeTemplate,
                                                                      final AbstractNodeTemplate hostingContainer) {
        final ConcreteOperationMatching matching = new ConcreteOperationMatching();

        for (final AbstractParameter param : abstractMatching.inputMatching.keySet()) {
            boolean added = false;
            for (final String nodePropName : ModelUtils.getPropertyNames(nodeTemplate)) {
                if (abstractMatching.inputMatching.get(param).equals(nodePropName)) {
                    matching.inputMatching.put(param, context.getPropertyVariable(nodeTemplate, nodePropName));
                    added = true;
                    break;
                }
            }
            if (added) {
                continue;
            }
            for (final String nodePropName : ModelUtils.getPropertyNames(hostingContainer)) {
                if (abstractMatching.inputMatching.get(param).equals(nodePropName)) {
                    matching.inputMatching.put(param, context.getPropertyVariable(hostingContainer, nodePropName));
                }
            }
        }

        for (final AbstractParameter param : abstractMatching.outputMatching.keySet()) {
            boolean added = false;
            for (final String nodePropName : ModelUtils.getPropertyNames(nodeTemplate)) {
                if (abstractMatching.outputMatching.get(param).equals(nodePropName)) {
                    matching.outputMatching.put(param, context.getPropertyVariable(nodeTemplate, nodePropName));
                    added = true;
                    break;
                }
            }
            if (added) {
                continue;
            }
            for (final String nodePropName : ModelUtils.getPropertyNames(hostingContainer)) {
                if (abstractMatching.outputMatching.get(param).equals(nodePropName)) {
                    matching.outputMatching.put(param, context.getPropertyVariable(hostingContainer, nodePropName));
                }
            }
        }

        return matching;
    }


    private boolean hasCompleteMatching(final AbstractNodeTemplate nodeTemplate,
                                        final AbstractNodeTemplate hostingContainer,
                                        final AbstractOperation operationToMatch) {

        final OperationMatching matching =
            createPropertyToParameterMatching(nodeTemplate, hostingContainer, operationToMatch);

        if (matching.inputMatching.size() == operationToMatch.getInputParameters().size()
            && matching.outputMatching.size() == operationToMatch.getOutputParameters().size()) {
            return true;
        }

        return false;
    }

    private OperationMatching createPropertyToParameterMatching(final AbstractNodeTemplate nodeTemplate,
                                                                final AbstractNodeTemplate hostingContainer,
                                                                final AbstractOperation operationToMatch) {
        final OperationMatching matching = new OperationMatching();

        for (final AbstractParameter param : operationToMatch.getInputParameters()) {
            boolean matched = false;
            for (final String propName : ModelUtils.getPropertyNames(nodeTemplate)) {
                if (param.getName().equals(propName)) {
                    matching.inputMatching.put(param, propName);
                    matched = true;
                    break;
                }
            }
            if (matched) {
                continue;
            }
            for (final String propName : ModelUtils.getPropertyNames(hostingContainer)) {
                if (param.getName().equals(propName)) {
                    matching.inputMatching.put(param, propName);
                    break;
                }
            }
        }

        for (final AbstractParameter param : operationToMatch.getOutputParameters()) {
            boolean matched = false;
            for (final String propName : ModelUtils.getPropertyNames(nodeTemplate)) {
                if (param.getName().equals(propName)) {
                    matching.outputMatching.put(param, propName);
                    matched = true;
                    break;
                }
            }
            if (matched) {
                continue;
            }
            for (final String propName : ModelUtils.getPropertyNames(hostingContainer)) {
                if (param.getName().equals(propName)) {
                    matching.outputMatching.put(param, propName);
                    break;
                }
            }
        }

        return matching;
    }

    private boolean hasContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        if (getContainerPatternCreateMethod(nodeTemplate) != null) {
            return true;
        } else {
            return false;
        }
    }

    private AbstractOperation getContainerPatternCreateMethod(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (iface.getName().equals(containerPatternInterfaceName)) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.getName().equals(containerPatternCreateOperationName)) {
                        return op;
                    }
                }
            }
        }

        return null;
    }

    private AbstractNodeTemplate getHostingNode(final AbstractNodeTemplate nodeTemplate) {
        for (final AbstractRelationshipTemplate rel : nodeTemplate.getOutgoingRelations()) {
            for (final QName typeInHierarchy : ModelUtils.getRelationshipTypeHierarchy(rel.getRelationshipType())) {
                if (ModelUtils.isInfrastructureRelationshipType(typeInHierarchy)) {
                    return rel.getTarget();
                }
            }
        }
        return null;
    }

}
