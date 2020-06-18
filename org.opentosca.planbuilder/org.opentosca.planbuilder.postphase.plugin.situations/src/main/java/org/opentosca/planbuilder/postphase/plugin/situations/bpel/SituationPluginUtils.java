package org.opentosca.planbuilder.postphase.plugin.situations.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SituationPluginUtils {

    public static Node getFirstChildNode(Node node, String localName) {
        NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            String nodeName = childList.item(i).getNodeName();
            if (childList.item(i).getNodeType() == Node.ELEMENT_NODE
                && nodeName.substring(nodeName.indexOf(":") + 1).equals(localName)) {
                return childList.item(i);
            }
        }
        return null;
    }

    public static Collection<AbstractNodeTemplate> fetchUsedNodeTemplates(BPELPlanContext context) {
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

    public static Collection<AbstractNodeTemplate> findUsedNodes(BPELPlanContext context) {
        Map<AbstractOperation, AbstractOperation> ops = context.getUsedOperations();
        Set<AbstractNodeTemplate> nodes = new HashSet<AbstractNodeTemplate>();

        // Temp fix
        //          for (AbstractOperation key : ops.keySet()) {
        //              nodes.addAll(SituationPluginUtils.findUsedNodes(context, key));
        //            nodes.addAll(SituationPluginUtils.findUsedNodes(context, ops.get(key)));
        //
        //        }
        
        nodes.add(context.getNodeTemplate());
        return nodes;
    }

    public static Collection<AbstractNodeTemplate> findUsedNodes(BPELPlanContext context, AbstractOperation op) {
        Set<AbstractNodeTemplate> nodes = new HashSet<AbstractNodeTemplate>();
        for (AbstractNodeTemplate node : context.getNodeTemplates()) {
            for (AbstractInterface iface : node.getType().getInterfaces()) {
                for (AbstractOperation o : iface.getOperations()) {
                    if (op != null && op.equals(o)) {
                        nodes.add(node);
                    }
                }
            }
        }

        return nodes;
    }

    public static AbstractPolicy getSituationAwareExecutionPolicy(AbstractNodeTemplate nodeTemplate) {
        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().getLocalPart().startsWith("SituationAwareExecutionPolicy")) {
                return policy;
            }
        }
        return null;
    }

    public static AbstractPolicy getSituationAwareExecutionPolicy(Collection<AbstractNodeTemplate> nodes) {
        // Note: right now we assume a nodeTemplate uses operations of only a single node (either itself or
        // other), multiple execution policies for a single scope is not supported yet
        for (AbstractNodeTemplate node : nodes) {
            AbstractPolicy pol = SituationPluginUtils.getSituationAwareExecutionPolicy(node);
            if (pol != null) {
                return pol;
            }
        }
        return null;
    }

    public static Collection<AbstractPolicy> getSituationPolicies(AbstractNodeTemplate nodeTemplate) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();
        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().getLocalPart().startsWith("SituationPolicy")) {
                policies.add(policy);
            }
        }
        return policies;
    }

    public static Collection<AbstractPolicy> getSituationPolicies(Collection<AbstractNodeTemplate> nodeTemplates) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();
        for (AbstractNodeTemplate node : nodeTemplates) {
            policies.addAll(SituationPluginUtils.getSituationPolicies(node));
        }
        return policies;
    }

    public static void addGETSituationData(BPELPlanContext context,
                                           Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                           Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                           Element elementToAppendTo, Fragments fragments) {
        for (AbstractPolicy policy : situationPolicies2DataVariables.keySet()) {
            try {
                // fetch situation data
                Node fetchSituationState =
                    fragments.generateBPEL4RESTLightGETAsNode(situationPolicies2IdVariables.get(policy)
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

    public static void addSituationDataUpdate(BPELPlanContext context, Node nodeToAppendTo,
                                              Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                              Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                              String situationViolation, Variable situationalScopeStartedVariable,
                                              Fragments fragments, BPELProcessFragments mainFragments) {
        // main sequence
        Element sequenceElement = context.createElement(BPELPlan.bpelNamespace, "sequence");

        SituationPluginUtils.addGETSituationData(context, situationPolicies2IdVariables,
                                                 situationPolicies2DataVariables, sequenceElement, fragments);

        String evalDataExpr = SituationPluginUtils.getSituationDataEvaluationQuery(situationPolicies2DataVariables);


        if (situationViolation.equals("Abort")) {
            // we exit the process if the situation are not active
            Element ifElement = SituationPluginUtils.createXpathExprIfElement(context, evalDataExpr);
            ifElement.appendChild(context.createElement(BPELPlan.bpelNamespace, "exit"));
        }

        if (situationViolation.equals("Compensate")) {
            // throw error when situation not okay and use integrated compensation logic
            Node throwErrorIfEvalFalse =
                context.importNode(SituationPluginUtils.createIfXPathExprTrueThrowError(evalDataExpr,
                                                                                        context.getNodeTemplate(),
                                                                                        mainFragments));
            sequenceElement.appendChild(throwErrorIfEvalFalse);
        }

        // add the fetch/check/action sequence into an if that checks whether the scope started already
        Element ifElement =
            SituationPluginUtils.createXpathExprIfElement(context,
                                                          "$" + situationalScopeStartedVariable.getVariableName());
        ifElement.appendChild(sequenceElement);
        nodeToAppendTo.appendChild(ifElement);
    }

    public static void addSituationObservationActivities(final BPELPlanContext context,
                                                         final AbstractNodeTemplate nodeTemplate,
                                                         String durationExpression,
                                                         Map<AbstractPolicy, Variable> situationPolicies2DataVariables,
                                                         Map<AbstractPolicy, Variable> situationPolicies2IdVariables,
                                                         String situationViolation,
                                                         Variable situationalScopeStartedVariable,
                                                         Fragments pluginFragments,
                                                         BPELProcessFragments processFagments) {
        Element onAlarmElement = SituationPluginUtils.createOnAlarmEventHandler(context, durationExpression);
        SituationPluginUtils.addSituationDataUpdate(context, onAlarmElement, situationPolicies2DataVariables,
                                                    situationPolicies2IdVariables, situationViolation,
                                                    situationalScopeStartedVariable, pluginFragments, processFagments);
        context.getEventHandlersElement().appendChild(onAlarmElement);
    }

    public static boolean isWCETCalculationPossible(BPELPlanContext context, AbstractNodeTemplate nodeTemplate,
                                                    Collection<AbstractNodeTemplate> usedNodes) {

        // at least all compensation operations must have a WCET defined to be able to work with timing
        Map<AbstractOperation, AbstractOperation> usedOperations = context.getUsedOperations();
        int usedCompensationOperationsCount = usedOperations.values().size();

        Collection<AbstractPolicy> operationExecutionTimePolicies =
            SituationPluginUtils.getOperationExecutionTimePolicies(usedNodes);
        
        int defiendWcets = 0;

        for (AbstractOperation op : usedOperations.values()) {
            if (op != null) {
                for (AbstractPolicy pol : operationExecutionTimePolicies) {

                    if (pol.getTemplate().getProperties().asMap().get("InterfaceName")
                           .equals(op.getInterface().getName())
                        && pol.getTemplate().getProperties().asMap().get("OperationName").equals(op.getName())) {

                        String wcetProp = pol.getTemplate().getProperties().asMap().get("WorstCaseExecutionTime");
                        if (wcetProp != null) {
                            defiendWcets++;
                        }
                    }
                }
            }
        }

        return defiendWcets == usedCompensationOperationsCount;
    }

    public static Variable appendCompensationWCETCalculation(BPELPlanContext context, AbstractNodeTemplate nodeTemplate,
                                                 Collection<AbstractNodeTemplate> usedNodes) {

        Collection<AbstractPolicy> operationExecutionTimePolicies =
            SituationPluginUtils.getOperationExecutionTimePolicies(usedNodes);

        Map<AbstractOperation, AbstractOperation> usedOperations = context.getUsedOperations();

        // we sum up the compensation operations' wcet, as if something happens we should be able to
        // compensate in time and we assume that scope only have sequences of operations

        int wcet = 0;

        for (AbstractOperation op : usedOperations.values()) {
            if (op != null) {
                for (AbstractPolicy pol : operationExecutionTimePolicies) {

                    if (pol.getTemplate().getProperties().asMap().get("InterfaceName")
                           .equals(op.getInterface().getName())
                        && pol.getTemplate().getProperties().asMap().get("OperationName").equals(op.getName())) {

                        String wcetProp = pol.getTemplate().getProperties().asMap().get("WorstCaseExecutionTime");
                        if (wcetProp != null) {
                            wcet += Integer.valueOf(wcetProp);
                        }
                    }
                }
            }
        }

        Variable wcetVariable =
            context.createGlobalStringVariable(nodeTemplate.getId() + "_WCET_" + System.currentTimeMillis(),
                                               String.valueOf(wcet));

        return wcetVariable;
    }

    public static Node createIfXPathExprTrueThrowError(String xpathQuery, AbstractNodeTemplate nodeTemplate,
                                                       BPELProcessFragments mainFragments) {
        Node node = mainFragments.createIfTrueThrowsError(xpathQuery, new QName("http://opentosca.org/situations",
            "SituationsNotActive_AbortError_" + nodeTemplate.getId()));
        return node;
    }

    public static Element createOnAlarmEventHandler(BPELPlanContext context, String durationExpression) {
        Element onAlarmElement = context.createElement(BPELPlan.bpelNamespace, "onAlarm");
        Element repeatElement = context.createElement(BPELPlan.bpelNamespace, "repeatEvery");
        repeatElement.setAttribute("expressionLanguague", BPELPlan.xpath2Namespace);
        repeatElement.setTextContent(durationExpression);
        onAlarmElement.appendChild(repeatElement);
        return onAlarmElement;
    }

    public static Element createXpathExprIfElement(BPELPlanContext context, String xpr) {
        Element ifElement = context.createElement(BPELPlan.bpelNamespace, "if");
        Element conditionElement = context.createElement(BPELPlan.bpelNamespace, "condition");
        conditionElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);
        conditionElement.setTextContent(xpr);
        ifElement.appendChild(conditionElement);
        return ifElement;
    }

    public static Collection<AbstractPolicy> getOperationExecutionTimePolicies(Collection<AbstractNodeTemplate> nodeTemplates) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();
        for (AbstractNodeTemplate node : nodeTemplates) {
            policies.addAll(getOperationExecutionTimePolicies(node));
        }
        return policies;
    }

    public static Collection<AbstractPolicy> getOperationExecutionTimePolicies(AbstractNodeTemplate nodeTemplate) {
        Set<AbstractPolicy> policies = new HashSet<AbstractPolicy>();

        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().getLocalPart().contains("ExecutionTimePolicy")) {
                policies.add(policy);
            }
        }

        return policies;
    }

    public static String getSituationDataEvaluationQuery(Map<AbstractPolicy, Variable> situationPolicies2DataVariables) {
        String xpathQuery = "";
        for (Variable situationDataVar : situationPolicies2DataVariables.values()) {
            xpathQuery += "count($" + situationDataVar.getVariableName()
                + "/*[local-name()='Active' and text()='true']) = 1 and ";
        }
        xpathQuery = xpathQuery.substring(0, xpathQuery.length() - " and ".length());
        return xpathQuery;
    }

    public static String getSituationMinActiveTimeEvaluationQuery(Map<AbstractPolicy, Variable> situationPolicies2DataVariables) {
        String xpathQuery = "min((";

        for (Variable situationDataVar : situationPolicies2DataVariables.values()) {
            xpathQuery += "number($" + situationDataVar.getVariableName() + "/*[local-name()='EventTime']), ";
        }
        xpathQuery = xpathQuery.substring(0, xpathQuery.length() - ", ".length());
        xpathQuery += "))";

        return xpathQuery;
    }

}
