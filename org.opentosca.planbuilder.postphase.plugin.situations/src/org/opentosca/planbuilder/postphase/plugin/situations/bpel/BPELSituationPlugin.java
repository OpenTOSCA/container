package org.opentosca.planbuilder.postphase.plugin.situations.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.text.Document;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This plugin enables situation-aware execution of management operations. It needs the appropiate
 * policies annotated, wich are: multiple SituationPolicy policies which specify which situations
 * must be active to execute an operation and a single SituationAwareExecutionPolicy per
 * NodeTemplate which configures whether to 'Wait' or 'Abort' when trying to execute an operation
 * and when it is executed should operation 'Continue', 'Abort' or 'Compensate.
 * </p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELSituationPlugin implements IPlanBuilderPostPhasePlugin<BPELPlanContext> {

    private static final String PLAN_ID = "OpenTOSCA Situation-Aware Post Phase Plugin";
    private final Fragments fragments;
    private final BPELProcessFragments mainFragments;

    public BPELSituationPlugin() throws ParserConfigurationException {
        this.fragments = new Fragments();
        this.mainFragments = new BPELProcessFragments();
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return this.getSituationAwareExecutionPolicy(nodeTemplate) != null
            && !this.getSituationPolicies(nodeTemplate).isEmpty();
    }

    private Collection<AbstractPolicy> getSituationPolicies(Collection<AbstractNodeTemplate> nodeTemplates) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();
        for (AbstractNodeTemplate node : nodeTemplates) {
            policies.addAll(this.getSituationPolicies(node));
        }
        return policies;
    }

    private Collection<AbstractPolicy> getSituationPolicies(AbstractNodeTemplate nodeTemplate) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();
        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().getLocalPart().startsWith("SituationPolicy")) {
                policies.add(policy);
            }
        }
        return policies;
    }

    private AbstractPolicy getSituationAwareExecutionPolicy(AbstractNodeTemplate nodeTemplate) {
        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().getLocalPart().startsWith("SituationAwareExecutionPolicy")) {
                return policy;
            }
        }
        return null;
    }

    private Collection<AbstractNodeTemplate> fetchUsedNodeTemplates(BPELPlanContext context) {
        // in some cases plugins use operations of other node templates (e.g. docker containers and docker
        // engines or VM's and cloud providers)
        // therefore we have to find those node templates here
        Collection<AbstractNodeTemplate> nodes = new ArrayList<AbstractNodeTemplate>();


        Element provPhaseElement = context.getProvisioningPhaseElement();
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            NodeList nodeTemplateIDNodes =
                (NodeList) xpath.evaluate("//*[local-name()='invokeOperationAsync']/*[local-name()='NodeTemplateID']",
                                          provPhaseElement, XPathConstants.NODESET);

            for (int i = 0; i < nodeTemplateIDNodes.getLength(); i++) {
                String nodeTemplateId = nodeTemplateIDNodes.item(i).getTextContent();
                for (AbstractNodeTemplate node : context.getNodeTemplates()) {
                    if (node.getId().equals(nodeTemplateId)) {
                        nodes.add(node);
                    }
                }
            }
        }
        catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return nodes;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // get annotated policy for the situational scope
        AbstractPolicy situationAwareExecutionPolicy = this.getSituationAwareExecutionPolicy(nodeTemplate);
        String entryMode = situationAwareExecutionPolicy.getTemplate().getProperties().asMap().get("EntryMode");
        String situationViolation =
            situationAwareExecutionPolicy.getTemplate().getProperties().asMap().get("SituationViolation");


        // get annotated situation policies
        Collection<AbstractPolicy> situationPolicies = this.getSituationPolicies(this.fetchUsedNodeTemplates(context));
        Map<AbstractPolicy, Variable> situationPolicies2DataVariables = new HashMap<AbstractPolicy, Variable>();
        Map<AbstractPolicy, Variable> situationPolicies2IdVariables = new HashMap<AbstractPolicy, Variable>();
        Map<AbstractPolicy, String> situationPolicies2InputParamName = new HashMap<AbstractPolicy, String>();


        // create variable to check if we started the situational scope yet
        Variable situationalScopeStartedVariable =
            context.createGlobalStringVariable(nodeTemplate.getId() + "_situationalScope_started", "false");

        // create ID(/URL) and data variable for each situation

        for (AbstractPolicy policy : situationPolicies) {
            String varName = policy.getName() + "_URL_" + System.currentTimeMillis();
            Variable policyIdVar = context.createGlobalStringVariable(varName, "-1");

            varName = policy.getName() + "_DATA_" + System.currentTimeMillis();
            context.addGlobalVariable(varName, VariableType.TYPE, new QName("http://www.w3.org/2001/XMLSchema",
                "anyType", "xsd" + System.currentTimeMillis()));
            Variable policyDataVar = context.getVariable(varName);

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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
        }

        // add update situation data
        this.addGETSituationData(context, situationPolicies2IdVariables, situationPolicies2DataVariables,
                                 context.getPrePhaseElement());

        String xpathQuery = this.getSituationDataEvaluationQuery(situationPolicies2DataVariables);     

        // If entryMode is 'abort' we exit the process if one situation is not active at this point
        if (entryMode.equals("Abort")) {
            Node node = this.createIfXPathExprTrueThrowError(xpathQuery, nodeTemplate);
            node = context.importNode(node);
            context.getPrePhaseElement().appendChild(node);
            context.getProvisioningCompensationPhaseElement()
                   .appendChild(context.createElement(BPELPlan.bpelNamespace, "exit"));
        }

        // ..if 'wait' we wait 10s and re-evaluate via a while activity wrapping a sequence of wait and data updates
        if (entryMode.equals("Wait")) {
            try {
                Element waitForConditionActivities =
                    (Element) this.mainFragments.createWaitForCondition("not(" + xpathQuery + ")", "'PT10S'");
                waitForConditionActivities = (Element) context.importNode(waitForConditionActivities);
                
                
                Node seq = this.getFirstChildNode(waitForConditionActivities, "sequence");                
                
                this.addGETSituationData(context, situationPolicies2IdVariables, situationPolicies2DataVariables,
                                         (Element)seq);

                waitForConditionActivities = (Element) context.importNode(waitForConditionActivities);
                context.getPrePhaseElement().appendChild(waitForConditionActivities);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Node assignStartedVar = this.mainFragments.createAssignXpathQueryToStringVarFragmentAsNode(nodeTemplate.getId()
                + "_assignSituationScopeStarted", "boolean('true')", situationalScopeStartedVariable.getVariableName());
            assignStartedVar = context.importNode(assignStartedVar);
            context.getPrePhaseElement().appendChild(assignStartedVar);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* Add EventHandler Activity that observes the situations */
        this.addSituationObservationActivities(context, nodeTemplate, "'PT10S'", situationPolicies2DataVariables,
                                               situationPolicies2IdVariables, situationViolation,
                                               situationalScopeStartedVariable);
        return true;
    }
    
    private Node getFirstChildNode(Node node, String localName) {
        NodeList childList = node.getChildNodes();
        for(int i = 0; i< childList.getLength(); i++) {           
            String nodeName = childList.item(i).getNodeName();            
            if(childList.item(i).getNodeType() == Node.ELEMENT_NODE && nodeName.substring(nodeName.indexOf(":") + 1).equals(localName)) {
                return childList.item(i);
            }            
        }
        return null;
    }

    private Node createIfXPathExprTrueThrowError(String xpathQuery, AbstractNodeTemplate nodeTemplate) {
        Node node = this.mainFragments.createIfTrueThrowsError(xpathQuery, new QName("http://opentosca.org/situations",
            "SituationsNotActive_AbortError_" + nodeTemplate.getId()));
        return node;
    }

    private String getSituationDataEvaluationQuery(Map<AbstractPolicy, Variable> situationPolicies2DataVariables) {
        String xpathQuery = "";
        for (Variable situationDataVar : situationPolicies2DataVariables.values()) {
            xpathQuery += "count($" + situationDataVar.getVariableName()
                + "/*[local-name()='Active' and text()='true']) = 1 and ";
        }
        xpathQuery = xpathQuery.substring(0, xpathQuery.length() - " and ".length());
        return xpathQuery;
    }

    private void addGETSituationData(BPELPlanContext context,
                                     Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                     Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                     Element elementToAppendTo) {
        for (AbstractPolicy policy : situationPolicies2DataVariables.keySet()) {
            try {
                // fetch situation data
                Node fetchSituationState =
                    this.fragments.generateBPEL4RESTLightGETAsNode(situationPolicies2IdVariables.get(policy)
                                                                                                .getVariableName(),
                                                                   situationPolicies2DataVariables.get(policy)
                                                                                                  .getVariableName());
                fetchSituationState = context.importNode(fetchSituationState);
                elementToAppendTo.appendChild(fetchSituationState);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
        }

    }

    private void addSituationDataUpdate(BPELPlanContext context, Node nodeToAppendTo,
                                        Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                        Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                        String situationViolation, Variable situationalScopeStartedVariable) {
        // main sequence
        Element sequenceElement = context.createElement(BPELPlan.bpelNamespace, "sequence");

        this.addGETSituationData(context, situationPolicies2IdVariables, situationPolicies2DataVariables,
                                 sequenceElement);

        String evalDataExpr = this.getSituationDataEvaluationQuery(situationPolicies2DataVariables);


        if (situationViolation.equals("Abort")) {
            // we exit the process if the situation are not active
            Element ifElement = this.createXpathExprIfElement(context, evalDataExpr);
            ifElement.appendChild(context.createElement(BPELPlan.bpelNamespace, "exit"));
        }

        if (situationViolation.equals("Compensate")) {
            // throw error when situation not okay and use integrated compensation logic
            Node throwErrorIfEvalFalse =
                context.importNode(this.createIfXPathExprTrueThrowError(evalDataExpr, context.getNodeTemplate()));
            sequenceElement.appendChild(throwErrorIfEvalFalse);
        }

        // add the fetch/check/action sequence into an if that checks whether the scope started already
        Element ifElement =
            this.createXpathExprIfElement(context, "$" + situationalScopeStartedVariable.getVariableName());
        ifElement.appendChild(sequenceElement);
        nodeToAppendTo.appendChild(ifElement);
    }

    private Element createXpathExprIfElement(BPELPlanContext context, String xpr) {
        Element ifElement = context.createElement(BPELPlan.bpelNamespace, "if");
        Element conditionElement = context.createElement(BPELPlan.bpelNamespace, "condition");
        conditionElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);
        conditionElement.setTextContent(xpr);
        ifElement.appendChild(conditionElement);
        return ifElement;
    }

    private void addSituationObservationActivities(final BPELPlanContext context,
                                                   final AbstractNodeTemplate nodeTemplate, String durationExpression,
                                                   Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                                   Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                                   String situationViolation,
                                                   Variable situationalScopeStartedVariable) {
        Element onAlarmElement = this.createOnAlarmEventHandler(context, durationExpression);
        this.addSituationDataUpdate(context, onAlarmElement, situationPolicies2DataVariables,
                                    situationPolicies2IdVariables, situationViolation, situationalScopeStartedVariable);        
        context.getEventHandlersElement().appendChild(onAlarmElement);
    }

    private Element createOnAlarmEventHandler(BPELPlanContext context, String durationExpression) {
        Element onAlarmElement = context.createElement(BPELPlan.bpelNamespace, "onAlarm");
        Element repeatElement = context.createElement(BPELPlan.bpelNamespace, "repeatEvery");
        repeatElement.setAttribute("expressionLanguague", BPELPlan.xpath2Namespace);
        repeatElement.setTextContent(durationExpression);
        onAlarmElement.appendChild(repeatElement);
        return onAlarmElement;
    }


    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // we can handle relations
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }


    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }



    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                AbstractNodeTemplate sourceNodeTemplate, AbstractNodeTemplate targetNodeTemplate) {

        return false;
    }

    @Override
    public boolean canHandleUpdate(AbstractNodeTemplate sourceNodeTemplate, AbstractNodeTemplate targetNodeTemplate) {
        // this plugin can create instance data for only equal nodeTemplates as of now
        if (sourceNodeTemplate.getType().getId().equals(targetNodeTemplate.getType().getId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext sourceContext, BPELPlanContext targetContext,
                                AbstractRelationshipTemplate sourceRelationshipTemplate,
                                AbstractRelationshipTemplate targetRelationshipTemplate) {


        return false;
    }

    @Override
    public boolean canHandleUpdate(AbstractRelationshipTemplate sourceRelationshipTemplate,
                                   AbstractRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

}
