package org.opentosca.planbuilder.core.bpel.artifactbasednodehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseParamOperationPlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.w3c.dom.Element;

/**
 * <p>
 * This Class is a wrapper class for the other wrapper classes (IACandidateWrapper,DACandidateWrapper,..). The class
 * also represents if there are complete provisioning possible with the available template implementations.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class OperationChain {

    // this chain either holds a NodeTemplate or RelationshipTemplate
    TNodeTemplate nodeTemplate;
    TRelationshipTemplate relationshipTemplate;

    // lists for all other wrapper classes
    List<DANodeTypeImplCandidate> daCandidates = new ArrayList<>();
    List<IANodeTypeImplCandidate> iaCandidates = new ArrayList<>();
    List<OperationNodeTypeImplCandidate> provCandidates = new ArrayList<>();

    // select candidate set
    int selectedCandidateSet = 0;

    /**
     * <p>
     * Constructor for a NodeTemplate
     * </p>
     *
     * @param nodeTemplate a NodeTemplate which the ProvisioningChain should belong
     */
    OperationChain(final TNodeTemplate nodeTemplate) {
        this.nodeTemplate = nodeTemplate;
    }

    /**
     * <p>
     * Constructor for a RelationshipTemplate
     * </p>
     *
     * @param relationshipTemplate a RelationshipTemplate which the ProvisioningChain should belong
     */
    OperationChain(final TRelationshipTemplate relationshipTemplate) {
        this.relationshipTemplate = relationshipTemplate;
    }

    /**
     * <p>
     * Executes the first found DACandidate to provision DA's with the appropiate plugins set in the candidate
     * </p>
     *
     * @param context a TemplatePlanContext which is initialized for either a NodeTemplate or RelationshipTemplate this
     *                ProvisioningChain belongs to
     * @return returns false only when execution of a plugin inside the DACandidate failed, else true. There may be no
     * DACandidate available, because there is no need for DA's to provision. In this case true is also returned.
     */
    public boolean executeDAProvisioning(final BPELPlanContext context) {
        boolean check = true;
        if (!this.daCandidates.isEmpty()) {
            final DANodeTypeImplCandidate daCandidate = this.daCandidates.get(this.selectedCandidateSet);
            for (int index = 0; index < daCandidate.das.size(); index++) {
                final TDeploymentArtifact da = daCandidate.das.get(index);
                final TNodeTemplate infraNode = daCandidate.infraNodes.get(index);
                final IPlanBuilderPrePhaseDAPlugin plugin = daCandidate.plugins.get(index);
                check &= plugin.handle(context, da, infraNode);
            }
        }
        return check;
    }

    /**
     * <p>
     * Executes the first found IACandidate to provision IA's with the appropiate plugins set in the candidate
     * </p>
     *
     * @param context a BPELPlanContext which is initialized for either a NodeTemplate or RelationshipTemplate this
     *                ProvisioningChain belongs to
     * @return returns false only when execution of a plugin inside the IACandidate failed, else true. There may be no
     * IACandidate available, because there is no need for IA's to provision. In this case true is also returned.
     */
    public boolean executeIAProvisioning(final PlanContext context) {
        boolean check = true;
        if (!this.iaCandidates.isEmpty()) {
            final IANodeTypeImplCandidate iaCandidate = this.iaCandidates.get(this.selectedCandidateSet);
            for (int index = 0; index < iaCandidate.ias.size(); index++) {
                final TImplementationArtifact ia = iaCandidate.ias.get(index);
                final TNodeTemplate infraNode = iaCandidate.infraNodes.get(index);
                final IPlanBuilderPrePhaseIAPlugin plugin = iaCandidate.plugins.get(index);
                check &= plugin.handle(context, ia, infraNode);
            }
        }
        return check;
    }

    /**
     * Checks whether this operation chain is valid, in the sense code generation is possible and there is an IA which implements the operations in the chain
     * @return true if there is a plugin which can generate code for the operations and there are IA implementing them
     */
    public boolean isValid() {
        if (this.provCandidates.isEmpty()) {
            return false;
        }
        return this.provCandidates.size() == this.iaCandidates.size();
    }

    /**
     * <p>
     * Executes the first found ProvisioningCandidate to execute provisioning operations with the appropiate plugins set
     * in the candidate. The order of calling each operation provisioning is represented in the given list of strings
     * </p>
     *
     * @param context a BPELPlanContext which is initialized for either a NodeTemplate or RelationshipTemplate this
     *                ProvisioningChain belongs to
     * @return returns false only when execution of a plugin inside the ProvisioningCandidate failed, else true. There
     * may be no ProvisioningCandidate available, because there is no need for operation to call. In this case true is
     * also returned.
     */
    public boolean executeOperationProvisioning(final BPELPlanContext context, final List<String> operationNames) {
        boolean check = true;
        if (!this.provCandidates.isEmpty()) {
            final OperationNodeTypeImplCandidate provCandidate = this.provCandidates.get(this.selectedCandidateSet);
            final Map<String, Integer> order = new HashMap<>();
            // check for index of prov candidates
            for (final String opName : operationNames) {
                for (Integer index = 0; index < provCandidate.ops.size(); index++) {
                    final TOperation op = provCandidate.ops.get(index);
                    if (op instanceof InterfaceDummy) {
                        if (((InterfaceDummy) op).getOperationNames().contains(opName)) {
                            order.put(opName, index);
                        }
                    } else {
                        if (opName.equals(op.getName())) {
                            order.put(opName, index);
                        }
                    }
                }
            }

            for (final String opName : operationNames) {
                final Integer index = order.get(opName);
                if (index == null) {
                    continue;
                }
                TOperation op = provCandidate.ops.get(index);

                if (op instanceof InterfaceDummy) {
                    op = ((InterfaceDummy) op).getOperation(opName);
                }

                if (!operationNames.contains(op.getName())) {
                    // if the operation isn't mentioned in operationName
                    // list, don't execute the operation
                    continue;
                }
                final TImplementationArtifact ia = provCandidate.ias.get(index);
                final IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins.get(index);
                check &= plugin.handle(context, op, ia);
            }
        }
        return check;
    }

    public boolean executeOperationProvisioning(final BPELPlanContext context, final List<String> operationNames,
                                                final Map<TParameter, Variable> param2propertyMapping) {
        int checkCount = 0;
        if (!this.provCandidates.isEmpty()) {
            final OperationNodeTypeImplCandidate provCandidate = this.provCandidates.get(this.selectedCandidateSet);
            final Map<String, Integer> order = new HashMap<>();
            // check for index of prov candidates
            for (final String opName : operationNames) {
                for (Integer index = 0; index < provCandidate.ops.size(); index++) {
                    final TOperation op = provCandidate.ops.get(index);
                    if (opName.equals(op.getName())) {
                        order.put(opName, index);
                    }
                }
            }

            for (final String opName : operationNames) {
                final Integer index = order.get(opName);
                if (index == null) {
                    continue;
                }
                final TOperation op = provCandidate.ops.get(index);
                if (!operationNames.contains(op.getName())) {
                    // if the operation isn't mentioned in operationName
                    // list, don't execute the operation
                    continue;
                }
                final TImplementationArtifact ia = provCandidate.ias.get(index);
                final IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins.get(index);

                if (plugin instanceof IPlanBuilderProvPhaseParamOperationPlugin) {
                    final IPlanBuilderProvPhaseParamOperationPlugin paramPlugin =
                        (IPlanBuilderProvPhaseParamOperationPlugin) plugin;
                    if (!(op instanceof InterfaceDummy)) {
                        if (paramPlugin.handle(context, op, ia, param2propertyMapping)) {
                            checkCount++;
                        }
                    } else {
                        final TOperation dummyOp = this.createDummyOperation(opName, op);
                        if (paramPlugin.handle(context, dummyOp, ia, param2propertyMapping)) {
                            checkCount++;
                        }
                    }
                }
            }
        }
        return checkCount == operationNames.size();
    }

    public boolean executeOperationProvisioning(final BPELPlanContext context, final List<String> operationNames,
                                                final Map<TParameter, Variable> param2propertyMapping,
                                                final Map<TParameter, Variable> param2propertyOutputMapping) {

        int checkCount = 0;
        if (!this.provCandidates.isEmpty()) {
            final OperationNodeTypeImplCandidate provCandidate = this.provCandidates.get(this.selectedCandidateSet);
            final Map<String, Integer> order = new HashMap<>();
            // check for index of prov candidates
            for (final String opName : operationNames) {
                for (Integer index = 0; index < provCandidate.ops.size(); index++) {
                    final TOperation op = provCandidate.ops.get(index);
                    if (op instanceof InterfaceDummy) {
                        if (((InterfaceDummy) op).getOperation(opName) != null) {
                            order.put(opName, index);
                        }
                    } else {
                        if (opName.equals(op.getName())) {
                            order.put(opName, index);
                        }
                    }
                }
            }

            for (final String opName : operationNames) {
                final Integer index = order.get(opName);
                if (index == null) {
                    continue;
                }
                final TOperation op = provCandidate.ops.get(index);
                if (op instanceof InterfaceDummy) {
                    boolean matched = true;
                    for (final String opname : operationNames) {
                        if (((InterfaceDummy) op).getOperation(opname) == null) {
                            matched = false;
                            break;
                        }
                    }
                    if (!matched) {
                        continue;
                    }
                } else {
                    if (!operationNames.contains(op.getName())) {
                        // if the operation isn't mentioned in operationName
                        // list, don't execute the operation
                        continue;
                    }
                }
                final TImplementationArtifact ia = provCandidate.ias.get(index);
                final IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins.get(index);

                if (plugin instanceof IPlanBuilderProvPhaseParamOperationPlugin) {
                    final IPlanBuilderProvPhaseParamOperationPlugin paramPlugin =
                        (IPlanBuilderProvPhaseParamOperationPlugin) plugin;
                    if (!(op instanceof InterfaceDummy)) {
                        if (paramPlugin.handle(context, op, ia, param2propertyMapping, param2propertyOutputMapping)) {
                            checkCount++;
                        }
                    } else {
                        final TOperation dummyOp = this.createDummyOperation(opName, op);
                        if (paramPlugin.handle(context, dummyOp, ia, param2propertyMapping,
                            param2propertyOutputMapping)) {
                            checkCount++;
                        }
                    }
                }
            }
        }
        return checkCount == operationNames.size();
    }

    public boolean executeOperationProvisioning(final BPELPlanContext context, final List<String> operationNames,
                                                final Map<TParameter, Variable> param2propertyMapping,
                                                final Map<TParameter, Variable> param2propertyOutputMapping,
                                                final Element elementToAppendTo) {
        int checkCount = 0;
        if (!this.provCandidates.isEmpty()) {
            final OperationNodeTypeImplCandidate provCandidate = this.provCandidates.get(this.selectedCandidateSet);
            final Map<String, Integer> order = new HashMap<>();
            // check for index of prov candidates
            for (final String opName : operationNames) {
                for (Integer index = 0; index < provCandidate.ops.size(); index++) {
                    final TOperation op = provCandidate.ops.get(index);
                    if (op instanceof InterfaceDummy) {
                        if (((InterfaceDummy) op).getOperation(opName) != null) {
                            order.put(opName, index);
                        }
                    } else {
                        if (opName.equals(op.getName())) {
                            order.put(opName, index);
                        }
                    }
                }
            }

            for (final String opName : operationNames) {
                final Integer index = order.get(opName);
                if (index == null) {
                    continue;
                }
                final TOperation op = provCandidate.ops.get(index);
                if (op instanceof InterfaceDummy) {
                    boolean matched = true;
                    for (final String opname : operationNames) {
                        if (((InterfaceDummy) op).getOperation(opname) == null) {
                            matched = false;
                            break;
                        }
                    }
                    if (!matched) {
                        continue;
                    }
                } else {
                    if (!operationNames.contains(op.getName())) {
                        // if the operation isn't mentioned in operationName
                        // list, don't execute the operation
                        continue;
                    }
                }
                final TImplementationArtifact ia = provCandidate.ias.get(index);
                final IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins.get(index);

                if (plugin instanceof IPlanBuilderProvPhaseParamOperationPlugin) {
                    final IPlanBuilderProvPhaseParamOperationPlugin paramPlugin =
                        (IPlanBuilderProvPhaseParamOperationPlugin) plugin;
                    if (!(op instanceof InterfaceDummy)) {
                        if (paramPlugin.handle(context, op, ia, param2propertyMapping, elementToAppendTo)) {
                            checkCount++;
                        }
                    } else {
                        final TOperation dummyOp = this.createDummyOperation(opName, op);
                        if (paramPlugin.handle(context, dummyOp, ia, param2propertyMapping, param2propertyOutputMapping,
                            elementToAppendTo)) {
                            checkCount++;
                        }
                    }
                }
            }
        }
        return checkCount == operationNames.size();
    }

    public boolean executeOperationProvisioning(final BPELPlanContext context, final List<String> operationNames,
                                                final Map<TParameter, Variable> param2propertyMapping,
                                                final Element elementToAppendTo) {
        int checkCount = 0;
        if (!this.provCandidates.isEmpty()) {
            final OperationNodeTypeImplCandidate provCandidate = this.provCandidates.get(this.selectedCandidateSet);
            final Map<String, Integer> order = new HashMap<>();
            // check for index of prov candidates
            for (final String opName : operationNames) {
                for (Integer index = 0; index < provCandidate.ops.size(); index++) {
                    final TOperation op = provCandidate.ops.get(index);
                    if (opName.equals(op.getName())) {
                        order.put(opName, index);
                    }
                }
            }

            for (final String opName : operationNames) {
                final Integer index = order.get(opName);
                if (index == null) {
                    continue;
                }
                final TOperation op = provCandidate.ops.get(index);
                if (!operationNames.contains(op.getName())) {
                    // if the operation isn't mentioned in operationName
                    // list, don't execute the operation
                    continue;
                }
                final TImplementationArtifact ia = provCandidate.ias.get(index);
                final IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins.get(index);

                if (plugin instanceof IPlanBuilderProvPhaseParamOperationPlugin) {
                    final IPlanBuilderProvPhaseParamOperationPlugin paramPlugin =
                        (IPlanBuilderProvPhaseParamOperationPlugin) plugin;
                    if (!(op instanceof InterfaceDummy)) {
                        if (paramPlugin.handle(context, op, ia, param2propertyMapping, elementToAppendTo)) {
                            checkCount++;
                        }
                    } else {
                        final TOperation dummyOp = this.createDummyOperation(opName, op);
                        if (paramPlugin.handle(context, dummyOp, ia, param2propertyMapping, elementToAppendTo)) {
                            checkCount++;
                        }
                    }
                }
            }
        }
        return checkCount == operationNames.size();
    }

    private TOperation createDummyOperation(String opName, TOperation op) {
        return new TOperation() {

            private final String operationName = opName;
            private final InterfaceDummy iface = (InterfaceDummy) op;

            @Override
            public List<TParameter> getOutputParameters() {
                return this.iface.getOperation(this.operationName).getOutputParameters();
            }

            @Override
            public String getName() {
                return this.operationName;
            }

            @Override
            public List<TParameter> getInputParameters() {
                return this.iface.getOperation(this.operationName).getInputParameters();
            }
        };
    }
}
