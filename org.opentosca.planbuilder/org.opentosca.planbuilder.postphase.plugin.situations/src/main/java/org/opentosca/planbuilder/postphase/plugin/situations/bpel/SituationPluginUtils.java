package org.opentosca.planbuilder.postphase.plugin.situations.bpel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPolicy;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
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

    public static Collection<TNodeTemplate> fetchUsedNodeTemplates(BPELPlanContext context) {
        // in some cases plugins use operations of other node templates (e.g. docker containers and docker
        // engines or VM's and cloud providers)
        // therefore we have to find those node templates here
        Collection<TNodeTemplate> nodes = new ArrayList<>();

        Element provPhaseElement = context.getProvisioningPhaseElement();
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            NodeList nodeTemplateIDNodes =
                (NodeList) xpath.evaluate("//*[local-name()='invokeOperationAsync']/*[local-name()='NodeTemplateID']",
                    provPhaseElement, XPathConstants.NODESET);

            for (int i = 0; i < nodeTemplateIDNodes.getLength(); i++) {
                String nodeTemplateId = nodeTemplateIDNodes.item(i).getTextContent();
                for (TNodeTemplate node : context.getNodeTemplates()) {
                    if (node.getId().equals(nodeTemplateId)) {
                        nodes.add(node);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return nodes;
    }

    public static Collection<TNodeTemplate> findUsedNodes(BPELPlanContext context) {
        Set<TNodeTemplate> nodes = new HashSet<>();
        nodes.add(context.getNodeTemplate());
        return nodes;
    }

    public static Collection<TNodeTemplate> findUsedNodes(BPELPlanContext context, TOperation op) {
        Set<TNodeTemplate> nodes = new HashSet<>();
        for (TNodeTemplate node : context.getNodeTemplates()) {
            List<TInterface> interfaces = ModelUtils.findNodeType(node, context.getCsar()).getInterfaces();
            if (interfaces != null) {
                for (TInterface iface : interfaces) {
                    for (TOperation o : iface.getOperations()) {
                        if (op != null && op.equals(o)) {
                            nodes.add(node);
                        }
                    }
                }
            }
        }

        return nodes;
    }

    public static TPolicy getSituationAwareExecutionPolicy(TNodeTemplate nodeTemplate) {
        if (nodeTemplate.getPolicies() == null) {
            return null;
        }
        for (TPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getPolicyType().getLocalPart().startsWith("SituationAwareExecutionPolicy")) {
                return policy;
            }
        }
        return null;
    }

    public static TPolicy getSituationAwareExecutionPolicy(Collection<TNodeTemplate> nodes) {
        // Note: right now we assume a nodeTemplate uses operations of only a single node (either itself or
        // other), multiple execution policies for a single scope is not supported yet
        for (TNodeTemplate node : nodes) {
            TPolicy pol = SituationPluginUtils.getSituationAwareExecutionPolicy(node);
            if (pol != null) {
                return pol;
            }
        }
        return null;
    }

    public static Collection<TPolicy> getSituationPolicies(TNodeTemplate nodeTemplate) {
        Set<TPolicy> policies = new HashSet<>();

        if (nodeTemplate.getPolicies() == null) {
            return policies;
        }

        for (TPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getPolicyType().getLocalPart().startsWith("SituationPolicy")) {
                policies.add(policy);
            }
        }
        return policies;
    }

    public static Collection<TPolicy> getSituationPolicies(Collection<TNodeTemplate> nodeTemplates) {
        Set<TPolicy> policies = new HashSet<>();
        for (TNodeTemplate node : nodeTemplates) {
            policies.addAll(SituationPluginUtils.getSituationPolicies(node));
        }
        return policies;
    }

    public static void addGETSituationData(BPELPlanContext context,
                                           Map<TPolicy, Variable> situationPolicies2IdVariables,
                                           Map<TPolicy, Variable> situationPolicies2DataVariables,
                                           Element elementToAppendTo, Fragments fragments) {
        for (TPolicy policy : situationPolicies2DataVariables.keySet()) {
            try {
                // fetch situation data
                Node fetchSituationState =
                    fragments.generateBPEL4RESTLightGETAsNode(situationPolicies2IdVariables.get(policy)
                            .getVariableName(),
                        situationPolicies2DataVariables.get(policy)
                            .getVariableName());
                fetchSituationState = context.importNode(fetchSituationState);
                elementToAppendTo.appendChild(fetchSituationState);
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addSituationDataUpdate(BPELPlanContext context, Node nodeToAppendTo,
                                              Map<TPolicy, Variable> situationPolicies2DataVariables,
                                              Map<TPolicy, Variable> situationPolicies2IdVariables,
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
            Variable faultMessage = context.createGlobalStringVariable("faultMessage" + context.getIdForNames(), "Compensating scope");
            Node throwErrorIfEvalFalse =
                context.importNode(SituationPluginUtils.createIfXPathExprTrueThrowError(evalDataExpr,
                    context.getNodeTemplate(),
                    mainFragments, faultMessage.getVariableName()));
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
                                                         final TNodeTemplate nodeTemplate,
                                                         String durationExpression,
                                                         Map<TPolicy, Variable> situationPolicies2DataVariables,
                                                         Map<TPolicy, Variable> situationPolicies2IdVariables,
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

    public static boolean isWCETCalculationPossible(BPELPlanContext context, TNodeTemplate nodeTemplate,
                                                    Collection<TNodeTemplate> usedNodes) {

        // at least all compensation operations must have a WCET defined to be able to work with timing
        Map<TOperation, TOperation> usedOperations = context.getUsedOperations();
        int usedCompensationOperationsCount = usedOperations.values().size();

        Collection<TPolicy> operationExecutionTimePolicies =
            SituationPluginUtils.getOperationExecutionTimePolicies(usedNodes);

        int defiendWcets = 0;

        for (TOperation op : usedOperations.values()) {
            if (op != null) {
                for (TPolicy pol : operationExecutionTimePolicies) {
                    // FIXME Removed this from the lower if, could be a bomb...
                    //ModelUtils.asMap(pol.getTemplate().getProperties()).get("InterfaceName")
                    //                        .equals(op.getInterface().getName())
                    //                        &&
                    //
                    if (ModelUtils.asMap(pol.getProperties()).get("OperationName").equals(op.getName())) {

                        String wcetProp = ModelUtils.asMap(pol.getProperties()).get("WorstCaseExecutionTime");
                        if (wcetProp != null) {
                            defiendWcets++;
                        }
                    }
                }
            }
        }

        return defiendWcets == usedCompensationOperationsCount;
    }

    public static Variable appendCompensationWCETCalculation(BPELPlanContext context, TNodeTemplate nodeTemplate,
                                                             Collection<TNodeTemplate> usedNodes) {

        Collection<TPolicy> operationExecutionTimePolicies =
            SituationPluginUtils.getOperationExecutionTimePolicies(usedNodes);

        Map<TOperation, TOperation> usedOperations = context.getUsedOperations();

        // we sum up the compensation operations' wcet, as if something happens we should be able to
        // compensate in time and we assume that scope only have sequences of operations

        int wcet = 0;

        for (TOperation op : usedOperations.values()) {
            if (op != null) {
                for (TPolicy pol : operationExecutionTimePolicies) {
                    // FIXME Removed this from the lower if, could be a bomb, however, there is no way of finding out the interface the operation belongs with basic winery backend methods
                    //ModelUtils.asMap(pol.getTemplate().getProperties()).get("InterfaceName")
                    //                        .equals(op.getInterface().getName())
                    //                        &&
                    if (ModelUtils.asMap(pol.getProperties()).get("OperationName").equals(op.getName())) {

                        String wcetProp = ModelUtils.asMap(pol.getProperties()).get("WorstCaseExecutionTime");
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

    public static Node createIfXPathExprTrueThrowError(String xpathQuery, TNodeTemplate nodeTemplate,
                                                       BPELProcessFragments mainFragments, String faultMessageVariableName) {

        return mainFragments.createIfTrueThrowsError(xpathQuery, new QName("http://opentosca.org/situations",
            "SituationsNotActive_AbortError_" + nodeTemplate.getId()), faultMessageVariableName);
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

    public static Collection<TPolicy> getOperationExecutionTimePolicies(Collection<TNodeTemplate> nodeTemplates) {
        Set<TPolicy> policies = new HashSet<>();
        for (TNodeTemplate node : nodeTemplates) {
            policies.addAll(getOperationExecutionTimePolicies(node));
        }
        return policies;
    }

    public static Collection<TPolicy> getOperationExecutionTimePolicies(TNodeTemplate nodeTemplate) {
        Set<TPolicy> policies = new HashSet<>();

        for (TPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getPolicyType().getLocalPart().contains("ExecutionTimePolicy")) {
                policies.add(policy);
            }
        }

        return policies;
    }

    public static String getSituationDataEvaluationQuery(Map<TPolicy, Variable> situationPolicies2DataVariables) {
        StringBuilder xpathQuery = new StringBuilder();
        for (Variable situationDataVar : situationPolicies2DataVariables.values()) {
            xpathQuery.append("count($").append(situationDataVar.getVariableName())
                .append("/*[local-name()='Active' and text()='true']) = 1 and ");
        }
        xpathQuery = new StringBuilder(xpathQuery.substring(0, xpathQuery.length() - " and ".length()));
        return xpathQuery.toString();
    }

    public static String getSituationMinActiveTimeEvaluationQuery(Map<TPolicy, Variable> situationPolicies2DataVariables) {
        StringBuilder xpathQuery = new StringBuilder("min((");

        for (Variable situationDataVar : situationPolicies2DataVariables.values()) {
            xpathQuery.append("number($").append(situationDataVar.getVariableName())
                .append("/*[local-name()='EventTime']), ");
        }
        xpathQuery = new StringBuilder(xpathQuery.substring(0, xpathQuery.length() - ", ".length()));
        xpathQuery.append("))");

        return xpathQuery.toString();
    }
}
