package org.opentosca.planbuilder.type.plugin.connectsto.bpel.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.type.plugin.connectsto.core.handler.ConnectsToPluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class BPELConnectsToPluginHandler implements ConnectsToPluginHandler<BPELPlanContext> {

    private final static Logger LOG = LoggerFactory.getLogger(BPELConnectsToPluginHandler.class);

    @Override
    public boolean handle(final BPELPlanContext templateContext) {
        final TRelationshipTemplate relationTemplate = templateContext.getRelationshipTemplate();
        final TNodeTemplate sourceNodeTemplate = ModelUtils.getSource(relationTemplate, templateContext.getCsar());
        final TNodeTemplate targetNodeTemplate = ModelUtils.getTarget(relationTemplate, templateContext.getCsar());

        // if the target has connectTo we execute it
        if (hasOperation(targetNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_CONNECTTO, templateContext.getCsar())) {
            // if we can stop and start the node, and it is not defined as non-interruptive, stop it
            if (!ModelUtils.hasInterface(targetNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_NON_INTERRUPTIVE, templateContext.getCsar())
                && startAndStopAvailable(targetNodeTemplate, templateContext.getCsar())) {
                final String ifaceName =
                    getInterface(targetNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, templateContext.getCsar());
                templateContext.executeOperation(targetNodeTemplate, ifaceName,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, null);
            }

            // connectTo
            executeConnectsTo(templateContext, targetNodeTemplate, sourceNodeTemplate, targetNodeTemplate);

            // start the node again
            if (!ModelUtils.hasInterface(targetNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_NON_INTERRUPTIVE, templateContext.getCsar())
                && startAndStopAvailable(targetNodeTemplate, templateContext.getCsar())) {
                templateContext.executeOperation(targetNodeTemplate,
                    getInterface(targetNodeTemplate,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, templateContext.getCsar()),
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, null);
            }
        }

        // if the source has connectTo we execute it
        if (hasOperation(sourceNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_CONNECTTO, templateContext.getCsar())) {

            // if we can stop and start the node and it is not defined as non interruptive, stop it
            if (!ModelUtils.hasInterface(sourceNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_NON_INTERRUPTIVE, templateContext.getCsar())
                && startAndStopAvailable(sourceNodeTemplate, templateContext.getCsar())) {
                templateContext.executeOperation(sourceNodeTemplate,
                    getInterface(sourceNodeTemplate,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, templateContext.getCsar()),
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, null);
            }

            // connectTo
            executeConnectsTo(templateContext, sourceNodeTemplate, sourceNodeTemplate, targetNodeTemplate);

            // start the node again
            if (!ModelUtils.hasInterface(sourceNodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_NON_INTERRUPTIVE, templateContext.getCsar())
                && startAndStopAvailable(sourceNodeTemplate, templateContext.getCsar())) {
                templateContext.executeOperation(sourceNodeTemplate,
                    getInterface(sourceNodeTemplate,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, templateContext.getCsar()),
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, null);
            }
        }

        return true;
    }

    /**
     * Executes the connectTo operation on the given connectToNode NodeTemplate, the parameters for the operation will
     * be searched starting from the opposite NodeTemplate.
     * <p>
     * Additionally it is possible to search properties which start with "SOURCE_" or "TARGET_" on the source/target
     * NodeTemplate.
     *
     * @param templateContext     the context of this operation call
     * @param connectToNode       a Node Template with a connectTo operation
     * @param sourceParameterNode the source node template of the connectsTo relationship
     * @param targetParameterNode the target node template of the connectsTo relationship
     */
    private boolean executeConnectsTo(final BPELPlanContext templateContext, final TNodeTemplate connectToNode,
                                      final TNodeTemplate sourceParameterNode,
                                      final TNodeTemplate targetParameterNode) {
        // fetch the connectsTo Operation of the source node and it's parameters
        TInterface connectsToIface = null;
        TOperation connectsToOp = null;
        Map<TParameter, Variable> param2propertyMapping = null;
        for (final TInterface iface : ModelUtils.findNodeType(connectToNode, templateContext.getCsar()).getInterfaces()) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CONNECT_CONNECTTO)) {
                    // find properties that match the params on the target nodes' stack or prefixed
                    // properties at the source stack
                    BPELConnectsToPluginHandler.LOG.debug("Found connectTo operation. Searching for matching parameters in the properties.");
                    param2propertyMapping = findInputParameters(templateContext, op, connectToNode, sourceParameterNode,
                        targetParameterNode);

                    // check if all input params (or at least all required input params) can be matched with properties
                    if (param2propertyMapping.size() != op.getInputParameters().size()
                        && !allRequiredParamsAreMatched(op.getInputParameters(), param2propertyMapping)) {
                        BPELConnectsToPluginHandler.LOG.warn("Didn't find necessary matchings from parameter to property. Can't initialize connectsTo relationship.");
                    } else {
                        // executable operation found
                        connectsToIface = iface;
                        connectsToOp = op;
                        break;
                    }
                }
            }
            if (connectsToOp != null) {
                break;
            }
        }

        // no connectTo operation found with matching parameters
        if (connectsToOp == null) {
            BPELConnectsToPluginHandler.LOG.warn("No executable connectTo operation found.");
            return false;
        }

        // execute the connectTo operation with the found parameters
        BPELConnectsToPluginHandler.LOG.debug("Adding connectTo operation execution to build plan.");
        final Boolean result = templateContext.executeOperation(connectToNode, connectsToIface.getName(),
            connectsToOp.getName(), param2propertyMapping);
        BPELConnectsToPluginHandler.LOG.debug("Result from adding operation: " + result);

        return true;
    }

    /**
     * Searches for a property for the given param and adds it to the mapping. The search is based on the following
     * convention: 1. check if there is a property with the same name as the parameter on the node having the connectTo
     * operation 2. if not 1. then look for the property on the opposite stack of the connectTo operation first (if op
     * on source then look on target stack and so on) 3. if not 2. then look for the property on its own stack (if op on
     * soruce then look on source stack)
     *
     * @param templateContext       the plan context
     * @param connectToNode         the node on which we want to call connectTo
     * @param sourceParameterNode   the source node of the connectTo relation
     * @param targetParameterNode   the target node of the connecTo relation
     * @param param2propertyMapping a parameter to variable mapping to add the result to
     * @param param                 the parameter to match
     * @return true if adding a matching was possible
     */
    private boolean findInputParameter(final BPELPlanContext templateContext,
                                       final TNodeTemplate connectToNode,
                                       final TNodeTemplate sourceParameterNode,
                                       final TNodeTemplate targetParameterNode,
                                       Map<TParameter, Variable> param2propertyMapping, TParameter param) {
        // search matching property  in the RelationshipTemplate properties
        final Variable var =
            templateContext.getPropertyVariable(templateContext.getRelationshipTemplate(), param.getName());

        if (var != null) {
            param2propertyMapping.put(param, var);
            return true;
        } else {
            // if the connectTo operation is on the source node, we look in the target stack
            // if on the target node, we look in the source stack
            boolean definedOnSource = sourceParameterNode.equals(connectToNode);

            // we didn't find anything yet, lets try the whole topology and for ambigious properties (IPs etc.)
            String paramName = param.getName();
            if (Utils.isSupportedVirtualMachineIPProperty(paramName)) {
                for (final String ipParam : Utils.getSupportedVirtualMachineIPPropertyNames()) {
                    if (this.searchBasedOnDefinedOnSource(definedOnSource, templateContext, ipParam, param, param2propertyMapping, sourceParameterNode, targetParameterNode)) {
                        return true;
                    }
                }
            } else {
                // param is not an ip property search for whatever we can
                if (this.searchBasedOnDefinedOnSource(definedOnSource, templateContext, paramName, param, param2propertyMapping, sourceParameterNode, targetParameterNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tries to match the given parameter to a property according to the parameter prefix. The matching is done via the
     * following way: 1. Check the parameter for a prefix SOURCE_ or TARGET_ 2. Check the source or target stack for a
     * property matching the name after prefix
     *
     * @param templateContext       the plan context
     * @param sourceParameterNode   the source node template of the connectTo relation
     * @param targetParameterNode   the target node tempalte of the connectTo relation
     * @param param2propertyMapping the mapping from parameter to variable to add the matching to
     * @param param                 the parameter of the connectTo operation
     * @return true if a matching was found
     */
    private boolean findInputPrefixedParameter(final BPELPlanContext templateContext,
                                               final TNodeTemplate sourceParameterNode,
                                               final TNodeTemplate targetParameterNode,
                                               Map<TParameter, Variable> param2propertyMapping, TParameter param) {
        String unprefixedParam = null;
        boolean isSource = false;

        if (param.getName().startsWith("SOURCE_")) {
            unprefixedParam = param.getName().substring(7);
            isSource = true;
        } else if (param.getName().startsWith("TARGET_")) {
            unprefixedParam = param.getName().substring(7);
        }

        if (isSource) {
            // search in source stack
            this.searchAndAddIfFound(templateContext, sourceParameterNode, unprefixedParam, param, param2propertyMapping);
        } else {
            // search in target stack
            this.searchAndAddIfFound(templateContext, targetParameterNode, unprefixedParam, param, param2propertyMapping);
        }

        if (!param2propertyMapping.containsKey(param) && Utils.isSupportedVirtualMachineIPProperty(unprefixedParam)) {
            // we didn't find anything yet, lets try the whole topology and for ambigious properties (IPs etc.)

            // the params seems to be an IP property and prefixed therefore search in the stack according to the prefix
            for (final String ipParam : Utils.getSupportedVirtualMachineIPPropertyNames()) {
                if (isSource) {
                    if (this.searchAndAddIfFound(templateContext, sourceParameterNode, ipParam, param, param2propertyMapping)) {
                        break;
                    }
                } else {
                    if (this.searchAndAddIfFound(templateContext, targetParameterNode, ipParam, param, param2propertyMapping)) {
                        break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Search the input parameters for a given connectTo operation.
     *
     * @param templateContext     the context of the operation
     * @param connectsToOp        the connectTo operation object
     * @param connectToNode       the node which tries to establish the connection
     * @param sourceParameterNode the source node of the relationship
     * @param targetParameterNode the target node of the relationship
     * @return the Map which contains all found input parameters
     */
    private Map<TParameter, Variable> findInputParameters(final BPELPlanContext templateContext,
                                                          final TOperation connectsToOp,
                                                          final TNodeTemplate connectToNode,
                                                          final TNodeTemplate sourceParameterNode,
                                                          final TNodeTemplate targetParameterNode) {
        final Map<TParameter, Variable> param2propertyMapping = new HashMap<>();
        // search the input parameters in the properties
        for (final TParameter param : connectsToOp.getInputParameters()) {
            if (this.isPrefixedParam(param)) {
                this.findInputPrefixedParameter(templateContext, sourceParameterNode, targetParameterNode, param2propertyMapping, param);
            } else {
                this.findInputParameter(templateContext, connectToNode, sourceParameterNode, targetParameterNode, param2propertyMapping, param);
            }
        }
        return param2propertyMapping;
    }

    private boolean searchBasedOnDefinedOnSource(final boolean definedOnSource, final BPELPlanContext templateContext,
                                                 final String paramName,
                                                 final TParameter param,
                                                 final Map<TParameter, Variable> param2propertyMapping, final TNodeTemplate sourceParameterNode, final TNodeTemplate targetParameterNode) {
        if (definedOnSource) {
            // if it is defined on source, search the target stack, if found return true.
            // if not found check on source stack, if found return true
            if (this.searchAndAddIfFound(templateContext, paramName, param, param2propertyMapping, targetParameterNode, sourceParameterNode)) {
                return true;
            }
        } else {
            // if it is not defined on source, search the source stack, if found return true.
            // if not found check on target stack, if found return true
            if (this.searchAndAddIfFound(templateContext, paramName, param, param2propertyMapping, sourceParameterNode, targetParameterNode)) {
                return true;
            }
        }
        return false;
    }

    private boolean searchAndAddIfFound(final BPELPlanContext templateContext,
                                        final String paramName,
                                        final TParameter param,
                                        final Map<TParameter, Variable> param2propertyMapping, final TNodeTemplate... parametersRootNodes) {
        for (TNodeTemplate paramRootNode : parametersRootNodes) {
            if (this.searchAndAddIfFound(templateContext, paramRootNode, paramName, param, param2propertyMapping)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrefixedParam(TParameter param) {
        return param.getName().startsWith("SOURCE_") || param.getName().startsWith("TARGET_");
    }

    private boolean searchAndAddIfFound(final BPELPlanContext templateContext,
                                        final TNodeTemplate parametersRootNode,
                                        final String paramName,
                                        final TParameter param,
                                        final Map<TParameter, Variable> param2propertyMapping) {
        final Variable property =
            searchPropertyInStack(templateContext, parametersRootNode, paramName);
        if (property != null) {
            param2propertyMapping.put(param, property);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Search for a property with a certain name on the stack of a node template.
     *
     * @param templateContext the context of the operation
     * @param currentNode     the node which is part of the stack
     * @param propName        the name of the property
     * @return the property if found, null otherwise
     */
    private Variable searchPropertyInStack(final PlanContext templateContext, TNodeTemplate currentNode,
                                           final String propName) {
        while (currentNode != null) {
            final Variable property = templateContext.getPropertyVariable(currentNode, propName);
            if (property != null) {
                return property;
            } else {
                currentNode = fetchNodeConnectedWithHostedOn(currentNode, templateContext.getCsar());
            }
        }
        return null;
    }

    /**
     * Returns the first node found connected trough a hostedOn relation
     *
     * @param nodeTemplate the node which is a possible source of an hostedOn relation
     * @return an TNodeTemplate which is a target of an hostedOn relation. Null if the given nodeTemplate isn't
     * connected to as a source to a hostedOn relation
     */
    private TNodeTemplate fetchNodeConnectedWithHostedOn(final TNodeTemplate nodeTemplate, Csar csar) {
        for (final TRelationshipTemplate relation : ModelUtils.getOutgoingRelations(nodeTemplate, csar)) {
            if (ModelUtils.getRelationshipTypeHierarchy(ModelUtils.findRelationshipType(relation, csar), csar)
                .contains(Types.hostedOnRelationType)) {
                return ModelUtils.getTarget(relation, csar);
            }
        }
        return null;
    }

    private String getInterface(final TNodeTemplate nodeTemplate, final String operationName, Csar csar) {
        List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
        if (interfaces != null) {
            for (final TInterface iface : interfaces) {
                for (final TOperation op : iface.getOperations()) {
                    if (op.getName().equals(operationName)) {
                        return iface.getName();
                    }
                }
            }
        }
        return null;
    }

    private boolean startAndStopAvailable(final TNodeTemplate nodeTemplate, Csar csar) {
        return hasOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_STOP, csar)
            & hasOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_LIFECYCLE_START, csar);
    }

    private boolean hasOperation(final TNodeTemplate nodeTemplate, final String operationName, Csar csar) {
        TNodeType nodeType = ModelUtils.findNodeType(nodeTemplate, csar);
        if (Objects.isNull(nodeType.getInterfaces())) {
            return false;
        } else {
            return nodeType.getInterfaces().stream().flatMap(inter -> inter.getOperations().stream())
                .filter(op -> op.getName().equals(operationName)).findFirst().isPresent();
        }
    }

    /**
     * Checks if all required input params have a matching property
     *
     * @param inputParameters       of the connectsTo operation
     * @param param2propertyMapping mapping between inputParameters and matched properties
     * @return true, if all required input params have a matching property. Otherwise, false.
     */
    private boolean allRequiredParamsAreMatched(final List<TParameter> inputParameters,
                                                final Map<TParameter, Variable> param2propertyMapping) {
        for (final TParameter inputParam : inputParameters) {
            if (inputParam.getRequired() && !param2propertyMapping.containsKey(inputParam)) {
                return false;
            }
        }
        return true;
    }
}
