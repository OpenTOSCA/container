package org.opentosca.planbuilder.postphase.plugin.situations.bpel;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.container.core.model.ModelUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This plugin enables situation-aware execution of management operations. It needs the appropiate policies annotated,
 * wich are: multiple SituationPolicy policies which specify which situations must be active to execute an operation and
 * a single SituationAwareExecutionPolicy per NodeTemplate which configures whether to 'Wait' or 'Abort' when trying to
 * execute an operation and when it is executed should operation 'Continue', 'Abort' or 'Compensate.
 * </p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 */
@Component
public class BPELSituationPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext> {

    private static final String PLAN_ID = "OpenTOSCA Situation-Aware Post Phase Plugin";
    private final Fragments fragments;
    private final BPELProcessFragments mainFragments;

    public BPELSituationPlugin() throws ParserConfigurationException {
        this.fragments = new Fragments();
        this.mainFragments = new BPELProcessFragments();
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        Collection<TNodeTemplate> nodes = SituationPluginUtils.findUsedNodes(context);
        return SituationPluginUtils.getSituationAwareExecutionPolicy(nodes) != null & !SituationPluginUtils.getSituationPolicies(nodes).isEmpty();
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        Collection<TNodeTemplate> usedNodes = SituationPluginUtils.findUsedNodes(context);
        // get annotated policy for the situational scope
        TPolicy situationAwareExecutionPolicy = SituationPluginUtils.getSituationAwareExecutionPolicy(usedNodes);
        String entryMode = ModelUtils.asMap(situationAwareExecutionPolicy.getProperties()).get("EntryMode");
        String situationViolation =
            ModelUtils.asMap(situationAwareExecutionPolicy.getProperties()).get("SituationViolation");

        // get annotated situation policies
        Collection<TPolicy> situationPolicies = SituationPluginUtils.getSituationPolicies(usedNodes);
        Map<TPolicy, Variable> situationPolicies2DataVariables = new HashMap<TPolicy, Variable>();
        Map<TPolicy, Variable> situationPolicies2IdVariables = new HashMap<TPolicy, Variable>();
        Map<TPolicy, String> situationPolicies2InputParamName = new HashMap<TPolicy, String>();

        // create variable to check if we started the situational scope yet
        Variable situationalScopeStartedVariable =
            context.createGlobalStringVariable(nodeTemplate.getId() + "_situationalScope_started", "false");

        // create ID(/URL) and data variable for each situation

        for (TPolicy policy : situationPolicies) {
            String varName = policy.getName() + "_URL_" + System.currentTimeMillis();
            Variable policyIdVar = context.createGlobalStringVariable(varName, "-1");

            varName = policy.getName() + "_DATA_" + System.currentTimeMillis();
            context.addGlobalVariable(varName, VariableType.TYPE, new QName("http://www.w3.org/2001/XMLSchema",
                "anyType", "xsd" + System.currentTimeMillis()));
            Variable policyDataVar = BPELPlanContext.getVariable(varName);

            situationPolicies2IdVariables.put(policy, policyIdVar);
            situationPolicies2DataVariables.put(policy, policyDataVar);

            // add input param for situation
            String inputName = policy.getName() + "_URL";
            context.addStringValueToPlanRequest(inputName);
            situationPolicies2InputParamName.put(policy, inputName);

            // assign ID(/URL) from input
            try {
                Node assignIdFromInputToVar =
                    this.fragments.generateAssignFromInputMessageToStringVariableAsNode(inputName,
                        policyIdVar.getVariableName());
                assignIdFromInputToVar = context.importNode(assignIdFromInputToVar);
                context.appendToInitSequence(assignIdFromInputToVar);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        // add update situation data
        SituationPluginUtils.addGETSituationData(context, situationPolicies2IdVariables, situationPolicies2DataVariables,
            context.getPrePhaseElement(), this.fragments);

        String situationsActiveXpathQuery = SituationPluginUtils.getSituationDataEvaluationQuery(situationPolicies2DataVariables);

        String combinedQuery = situationsActiveXpathQuery;

        if (SituationPluginUtils.isWCETCalculationPossible(context, nodeTemplate, usedNodes)) {
            String situationsMinActiveTimeXpathQuery =
                SituationPluginUtils.getSituationMinActiveTimeEvaluationQuery(situationPolicies2DataVariables);

            Variable compensationWcetTimeVariable = SituationPluginUtils.appendCompensationWCETCalculation(context, nodeTemplate, usedNodes);

            String wcetQuery = "number($" + compensationWcetTimeVariable.getVariableName() + ") <= number(" + situationsMinActiveTimeXpathQuery + ")";

            combinedQuery += " and " + wcetQuery;
        }

        // If entryMode is 'abort' we exit the process if one situation is not active at this point
        if (entryMode.equals("Abort")) {
            Variable faultMessageVar = context.createGlobalStringVariable("faultMessage" + context.getIdForNames(), "Aborting, SituationScope was reached were the situations were not met");
            Node node = SituationPluginUtils.createIfXPathExprTrueThrowError(combinedQuery, nodeTemplate, this.mainFragments, faultMessageVar.getVariableName());
            node = context.importNode(node);
            context.getPrePhaseElement().appendChild(node);
            context.getProvisioningCompensationPhaseElement()
                .appendChild(context.createElement(BPELPlan.bpelNamespace, "exit"));
        }

        // ..if 'wait' we wait 5s and re-evaluate via a while activity wrapping a sequence of wait and data
        // updates
        if (entryMode.equals("Wait")) {
            try {
                Element waitForConditionActivities =
                    (Element) this.mainFragments.createWaitForCondition("not(" + combinedQuery + ")",
                        "'PT5S'");
                waitForConditionActivities = (Element) context.importNode(waitForConditionActivities);

                Node seq = SituationPluginUtils.getFirstChildNode(waitForConditionActivities, "sequence");

                SituationPluginUtils.addGETSituationData(context, situationPolicies2IdVariables, situationPolicies2DataVariables,
                    (Element) seq, this.fragments);

                waitForConditionActivities = (Element) context.importNode(waitForConditionActivities);
                context.getPrePhaseElement().appendChild(waitForConditionActivities);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Node assignStartedVar = this.mainFragments.createAssignXpathQueryToStringVarFragmentAsNode(nodeTemplate
                .getId()
                + "_assignSituationScopeStarted", "boolean('true')", situationalScopeStartedVariable.getVariableName());
            assignStartedVar = context.importNode(assignStartedVar);
            context.getPrePhaseElement().appendChild(assignStartedVar);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* Add EventHandler Activity that observes the situations */
        SituationPluginUtils.addSituationObservationActivities(context, nodeTemplate, "'PT5S'", situationPolicies2DataVariables,
            situationPolicies2IdVariables, situationViolation,
            situationalScopeStartedVariable, this.fragments, this.mainFragments);
        return true;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        // if we can handle creation, we can also handle termination as we only add situation observation
        // code
        return this.canHandleCreate(context, nodeTemplate);
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        // we can handle relations
        return false;
    }

    @Override
    public boolean canHandleTerminate(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {

        return false;
    }

    @Override
    public boolean canHandleUpdate(TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        // this plugin can create instance data for only equal nodeTemplates as of now
        return sourceNodeTemplate.getType().equals(targetNodeTemplate.getType());
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                TRelationshipTemplate sourceRelationshipTemplate,
                                TRelationshipTemplate targetRelationshipTemplate) {

        return false;
    }

    @Override
    public boolean canHandleUpdate(TRelationshipTemplate sourceRelationshipTemplate,
                                   TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPELPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
