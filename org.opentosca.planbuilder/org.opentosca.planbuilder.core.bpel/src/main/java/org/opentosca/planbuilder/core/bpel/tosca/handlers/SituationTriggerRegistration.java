package org.opentosca.planbuilder.core.bpel.tosca.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SituationTriggerRegistration {

    private static final String xpath_query_situationtriggers =
        "//*[local-name()='SituationTrigger' and namespace-uri()='http://opentosca.org/situations']";

    public class Situation {
        private final String situationTemplateId;
        private final String thingId;
        private final boolean fromInput;

        public Situation(final String situationTemplateId, final String thingId, final boolean fromInput) {
            this.situationTemplateId = situationTemplateId;
            this.thingId = thingId;
            this.fromInput = fromInput;
        }
    }

    public class Triplet<A, B, C> {
        private final A first;
        private final B second;
        private final C third;

        public Triplet(final A first, final B second, final C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() {
            return this.first;
        }

        public B getSecond() {
            return this.second;
        }

        public C getThird() {
            return this.third;
        }
    }

    public class SituationTrigger {

        public static final String xpath_query_situations =
            "//*[local-name()='Situation' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situations_situationTemplateId =
            "//*[local-name()='Situation' and namespace-uri()='http://opentosca.org/situations']/*[local-name()='SituationTemplateId']";

        public static final String xpath_query_situations_thingId =
            "//*[local-name()='Situation' and namespace-uri()='http://opentosca.org/situations']/*[local-name()='ThingId']";

        public static final String xpath_query_situationtrigger_onActivation =
            "//*[local-name()='onActivation' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_isSingleInstance =
            "//*[local-name()='isSingleInstance' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_serviceInstanceId =
            "//*[local-name()='ServiceInstanceId' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_nodeInstanceId =
            "//*[local-name()='NodeInstanceId' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_interfaceName =
            "//*[local-name()='InterfaceName' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_operationName =
            "//*[local-name()='OperationName' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_inputParameters =
            "//*[local-name()='InputParameter' and namespace-uri()='http://opentosca.org/situations']";

        public static final String xpath_query_situationtrigger_outputParameters =
            "//*[local-name()='OutputParameter' and namespace-uri()='http://opentosca.org/situations']";

        private List<Situation> situations;
        private boolean onActivation;
        private boolean isSingleInstance;
        private String serviceInstanceId;
        private String nodeInstanceId;
        private String interfaceName;
        private String operationName;
        private List<Triplet<String, String, String>> inputParameters;

        public List<Situation> getSituations() {
            return this.situations;
        }

        public void setSituations(final List<Situation> situations) {
            this.situations = situations;
        }

        public boolean isOnActivation() {
            return this.onActivation;
        }

        public void setOnActivation(final boolean onActivation) {
            this.onActivation = onActivation;
        }

        public boolean isSingelInstance() {
            return this.isSingleInstance;
        }

        public void setSingelInstance(final boolean isSingelInstance) {
            this.isSingleInstance = isSingelInstance;
        }

        public String getServiceInstanceId() {
            return this.serviceInstanceId;
        }

        public void setServiceInstanceId(final String serviceInstanceId) {
            this.serviceInstanceId = serviceInstanceId;
        }

        public String getNodeInstanceId() {
            return this.nodeInstanceId;
        }

        public void setNodeInstanceId(final String nodeInstanceId) {
            this.nodeInstanceId = nodeInstanceId;
        }

        public String getInterfaceName() {
            return this.interfaceName;
        }

        public void setInterfaceName(final String interfaceName) {
            this.interfaceName = interfaceName;
        }

        public String getOperationName() {
            return this.operationName;
        }

        public void setOperationName(final String operationName) {
            this.operationName = operationName;
        }

        public List<Triplet<String, String, String>> getInputParameters() {
            return this.inputParameters;
        }

        public void setInputParameters(final List<Triplet<String, String, String>> inputParameters) {
            this.inputParameters = inputParameters;
        }
    }

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    private final BPELProcessFragments fragments;
    private final BPELPlanHandler handler;
    private final SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;

    public SituationTriggerRegistration() throws ParserConfigurationException {
        this.fragments = new BPELProcessFragments();
        this.handler = new BPELPlanHandler();
        this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
    }

    public boolean handle(final AbstractServiceTemplate serviceTemplate, final BPELPlan plan) {

        try {
            // parse triggers
            final List<SituationTrigger> triggers = parseSituationTriggers(serviceTemplate);

            for (final SituationTrigger trigger : triggers) {
                // create xml literal, add variable, assign variable
                final String xmlLiteral = createXMLLiteral(trigger);

                final String varName = "SituationTriggerRegistration_" + System.currentTimeMillis();

                appendAssignLiteralVariable(plan, xmlLiteral, varName, plan.getBpelMainFlowElement());

                final List<String> situationIdVarNames = new ArrayList<>();

                for (int i = 0; i < trigger.situations.size(); i++) {
                    // TODO set situationId
                    final Situation situation = trigger.situations.get(i);
                    if (situation.fromInput) {
                        final String inputName = "SituationId_" + i + "_" + situation.situationTemplateId;
                        this.handler.addStringElementToPlanRequest(inputName, plan);
                        appendAssignSituationidFromInput(plan, inputName, varName, plan.getBpelMainFlowElement(), i);
                        final String situationIdVarName =
                            this.handler.addGlobalStringVariable("SituationId_" + i + "_var", plan);
                        appendAssignSituationIdFromInputToSituationIdVar(plan, inputName, i, situationIdVarName,
                            plan.getBpelMainFlowElement());
                        situationIdVarNames.add(situationIdVarName);
                    } else {
                        // TODO Add Selection of SituationId
                    }
                }

                // TODO set serviceInstanceId
                if (trigger.serviceInstanceId.equals("Build")) {
                    // fetch serviceInstance from buildPlan
                    final String serviceInstanceIdVar = this.serviceInstanceHandler.findServiceInstanceIdVarName(plan);
                    appendAssignServiceInstanceIdFromServiceInstanceIdVar(plan, serviceInstanceIdVar, varName,
                        plan.getBpelMainFlowElement());
                }

                // optional TODO set nodeInstance selection

                // create REST call

                final String situationsAPI = "SituationsAPI_Triggers_URL";
                this.handler.addStringElementToPlanRequest(situationsAPI, plan);
                final String situationsAPIVarName = "SituationsAPI_Triggers_URL" + System.currentTimeMillis();

                final String situationsAPIVar = this.handler.addGlobalStringVariable(situationsAPIVarName, plan);

                appendAssignSituationsAPIURLVar(plan, "input", "payload", situationsAPI, situationsAPIVar,
                    plan.getBpelMainFlowElement());

                final String stringReqVar =
                    this.handler.addGlobalStringVariable("situationRegistrationStringVar" + System.currentTimeMillis(),
                        plan);

                appendAssignTransformXmltoString(plan, varName, stringReqVar, plan.getBpelMainFlowElement(), "SituationTrigger");

                appendAssignRESTPOST(plan, situationsAPIVar, stringReqVar,
                    this.handler.addGlobalStringVariable("SituationRegistrationResponse", plan),
                    plan.getBpelMainFlowElement());
            }
        } catch (final XPathExpressionException e) {
            e.printStackTrace();
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void appendAssignTransformXmltoString(final BPELPlan plan, final String xmlVar, final String stringVar,
                                                 final Element elementToAppendBefore, String rootElementName) throws IOException,
        SAXException {
        final String xpathQuery1 = "ode:dom-to-string(\\$" + xmlVar + "/*[local-name()='" + rootElementName + "'])";
        final String xpathQuery2 = "\\$" + stringVar;

        Node assign =
            this.fragments.createAssignVarToVarWithXpathQueriesAsNode("transformXMLtoStringVar", xmlVar, null,
                stringVar, null, xpathQuery1, xpathQuery2,
                "Transforms one xml var to a string var as ODE sets a an xml element as wrapper around complex type when using the rest extension.",
                new QName(
                    "http://www.apache.org/ode/type/extension",
                    "ode", "ode"));
        assign = this.handler.importNode(plan, assign);
        elementToAppendBefore.getParentNode().insertBefore(assign, elementToAppendBefore);
    }

    private void appendAssignRESTPOST(final BPELPlan plan, final String urlVar, final String requestVar,
                                      final String responseVar,
                                      final Element elementToAppendBefore) throws IOException, SAXException {
        Node restCall = this.fragments.generateBPEL4RESTLightServiceInstancePOSTAsNode(urlVar, requestVar, responseVar);
        restCall = this.handler.importNode(plan, restCall);
        elementToAppendBefore.getParentNode().insertBefore(restCall, elementToAppendBefore);
    }

    private void appendAssignSituationsAPIURLVar(final BPELPlan plan, final String varName, final String partName,
                                                 final String situationsAPIINputFieldName,
                                                 final String situationsAPIVarName,
                                                 final Element elementToAppendBefore) throws IOException, SAXException {

        final String xpathQuery1 = "//*[local-name()='" + situationsAPIINputFieldName + "']/text()";
        final String xpathQuery2 = "\\$" + situationsAPIVarName;
        Node assign =
            this.fragments.createAssignVarToVarWithXpathQueriesAsNode("AssignSituationsAPIUrl", varName, partName,
                situationsAPIVarName, null, xpathQuery1,
                xpathQuery2,
                "Assigns the SituationsAPIURL from Input to the designated Variable",
                null);
        assign = this.handler.importNode(plan, assign);
        elementToAppendBefore.getParentNode().insertBefore(assign, elementToAppendBefore);
    }

    private void appendAssignServiceInstanceIdFromServiceInstanceIdVar(final BPELPlan plan,
                                                                       final String serviceInstanceIdVarName,
                                                                       final String situationTriggerReqVarName,
                                                                       final Element elementToAppendBefore) throws IOException,
        SAXException {
        final String xpathQuery1 = "text()";
        final String xpathQuery2 = "//*[local-name()='ServiceInstanceId']";
        Node assign =
            this.fragments.createAssignVarToVarWithXpathQueriesAsNode("assignSituationTriggerReqWithServiceInstanceID",
                serviceInstanceIdVarName, null,
                situationTriggerReqVarName, null, xpathQuery1,
                xpathQuery2,
                "Assign the ServiceInstanceID of SituationTrigger Request from ServiceInstanceID inside this BuildPlan",
                null);

        assign = this.handler.importNode(plan, assign);
        elementToAppendBefore.getParentNode().insertBefore(assign, elementToAppendBefore);
    }

    private void appendAssignSituationIdFromInputToSituationIdVar(final BPELPlan plan, final String inputFieldName,
                                                                  final int situationIndex, final String varName,
                                                                  final Element elementToAppendBefore) throws IOException,
        SAXException {
        final String xpathQuery1 = "//*[local-name()='" + inputFieldName + "']/text()";
        final String xpathQuery2 = "text()";

        Node assignNode =
            this.fragments.createAssignVarToVarWithXpathQueriesAsNode("AssignSituationIdFromInputToVar", "input",
                "payload", varName, null, xpathQuery1,
                xpathQuery2,
                "Assigning the SituationId of a SituationTrigger based on the input variable to situationId Variable",
                null);

        assignNode = this.handler.importNode(plan, assignNode);

        elementToAppendBefore.getParentNode().insertBefore(assignNode, elementToAppendBefore);
    }

    private void appendAssignSituationidFromInput(final BPELPlan plan, final String inputFieldName,
                                                  final String situationTriggerRequestVar,
                                                  final Element elementToAppendBefore,
                                                  final int situationIndex) throws IOException, SAXException {

        final String xpathQuery1 = "//*[local-name()='" + inputFieldName + "']/text()";
        final String xpathQuery2 = "//*[local-name()='SituationId'][" + (situationIndex + 1) + "]";

        Node assignNode =
            this.fragments.createAssignVarToVarWithXpathQueriesAsNode("AssignSituationIdFromInput", "input", "payload",
                situationTriggerRequestVar, null, xpathQuery1,
                xpathQuery2,
                "Assigning the SituationId of a SituationTrigger based on the input variable",
                null);

        assignNode = this.handler.importNode(plan, assignNode);

        elementToAppendBefore.getParentNode().insertBefore(assignNode, elementToAppendBefore);
    }

    private void appendAssignLiteralVariable(final BPELPlan plan, final String xmlLiteral, final String varName,
                                             final Element elementToAppendBefore) throws IOException, SAXException {

        final QName anyDecl = new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd");
        this.handler.importNamespace(anyDecl, plan);
        this.handler.addVariable(varName, VariableType.TYPE, anyDecl, plan);

        Node node =
            this.fragments.createAssignVarWithLiteralAsNode(xmlLiteral, varName,
                "Appending the initial xml body of a situationtrigger which will be used for registering such a trigger");

        node = this.handler.importNode(plan, node);
        elementToAppendBefore.getParentNode().insertBefore(node, elementToAppendBefore);
    }

    private String createXMLLiteral(final SituationTrigger trigger) {
        /*
         * <?xml version="1.0" encoding="UTF-8"?> <SituationTrigger> <SituationId>36</SituationId>
         * <onActivation>true</onActivation> <isSingleInstance>true</isSingleInstance>
         * <ServiceInstanceId>19</ServiceInstanceId> <InterfaceName>scaleout_dockercontainer</InterfaceName>
         * <OperationName>scale-out</OperationName> <InputParameters> <InputParameter>
         * <name>ApplicationPort</name> <Value>9991</Value> <Type>String</Type> </InputParameter>
         * <InputParameter> <name>ContainerSSHPort</name> <Value></Value> <Type>String</Type>
         * </InputParameter> </InputParameters> </SituationTrigger>
         */

        final StringBuilder strB = new StringBuilder();
        strB.append("<SituationTrigger>");
        strB.append("<SituationTrigger xmlns=\"\"><Situations>");
        for (final Situation situation : trigger.situations) {
            strB.append("<SituationId></SituationId>");
        }
        strB.append("</Situations>");
        strB.append("<onActivation>" + trigger.onActivation + "</onActivation>");
        strB.append("<isSingleInstance>" + trigger.isSingleInstance + "</isSingleInstance>");
        strB.append("<ServiceInstanceId></ServiceInstanceId>");
        if (trigger.nodeInstanceId != null) {
            strB.append("<NodeInstanceId></NodeInstanceId>");
        }
        strB.append("<InterfaceName>" + trigger.interfaceName + "</InterfaceName>");
        strB.append("<OperationName>" + trigger.operationName + "</OperationName>");

        strB.append("<InputParameters>");
        for (final Triplet<String, String, String> inputParam : trigger.inputParameters) {

            strB.append("<InputParameter>");

            strB.append("<name>" + inputParam.first + "</name>");
            strB.append("<Value>" + inputParam.second + "</Value>");
            strB.append("<Type>" + inputParam.third + "</Type>");

            strB.append("</InputParameter>");
        }
        strB.append("</InputParameters>");
        strB.append("</SituationTrigger>");
        strB.append("</SituationTrigger>");

        return strB.toString();
    }

    public boolean canHandle(final AbstractServiceTemplate serviceTemplate, final BPELPlan plan) {

        List<SituationTrigger> triggers = new ArrayList<>();
        try {
            triggers = parseSituationTriggers(serviceTemplate);
        } catch (final XPathExpressionException e) {
            e.printStackTrace();
        }

        return triggers.size() != 0;
    }

    private NodeList queryNodeSet(final Node rootElement, final String xpathQuery) throws XPathExpressionException {
        final XPathExpression expr = this.xpath.compile(xpathQuery);
        final Object result = expr.evaluate(rootElement, XPathConstants.NODESET);
        final NodeList list = (NodeList) result;
        return list;
    }

    private List<SituationTrigger> parseSituationTriggers(final AbstractServiceTemplate serviceTemplate) throws XPathExpressionException {
        final List<SituationTrigger> situationTriggers = new ArrayList<>();
        final Map<String,String> properties = getPropertiesSafely(serviceTemplate);

        if (properties == null) {
            return situationTriggers;
        }



        return new ArrayList<>();

        // Thx to the fact that winery is unable to work with namespaces and complex types in properties this feature is now dead, for a while. thx winery
        // TODO: FIXME
        /*final NodeList list = queryNodeSet(properties, xpath_query_situationtriggers);

        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                final SituationTrigger trigger = parseSituationTrigger((Element) list.item(i));
                situationTriggers.add(trigger);
            }
        }

        return situationTriggers;*/
    }

    private Map<String, String> getPropertiesSafely(final AbstractServiceTemplate serviceTemplate) {
        if (serviceTemplate.getBoundaryDefinitions() != null) {
            if (serviceTemplate.getBoundaryDefinitions().getProperties() != null) {
                if (serviceTemplate.getBoundaryDefinitions().getProperties().getProperties() != null) {
                    if (serviceTemplate.getBoundaryDefinitions().getProperties().getProperties() != null && !serviceTemplate.getBoundaryDefinitions().getProperties().getProperties().asMap().isEmpty()) {
                        return serviceTemplate.getBoundaryDefinitions().getProperties().getProperties().asMap();
                    }
                }
            }
        }
        return null;
    }

    private SituationTrigger parseSituationTrigger(final Element situationTriggerElement) throws XPathExpressionException {

        /*
         * <sits:SituationTrigger> <sits:Situation>
         * <sits:SituationTemplateId>AtHome</sits:SituationTemplateId> <sits:ThingId>get_input:
         * ThingId</sits:ThingId> </sits:Situation> <sits:onActivation>true</sits:onActivation>
         * <sits:isSingleInstance>true</sits:isSingleInstance>
         * <sits:ServiceInstanceId>Build</sits:ServiceInstanceId>
         * <sits:InterfaceName>scaleout_dockercontainer</sits:InterfaceName>
         * <sits:OperationName>scale-out</sits:OperationName> <sits:InputParameters> <sits:InputParameter>
         * <sits:name>ApplicationPort</sits:name> <sits:Value>9991</sits:Value>
         * <sits:Type>String</sits:Type> </sits:InputParameter> <sits:InputParameter>
         * <sits:name>ContainerSSHPort</sits:name> <sits:Value></sits:Value> <sits:Type>String</sits:Type>
         * </sits:InputParameter> </sits:InputParameters> </sits:SituationTrigger>
         */

        final SituationTrigger trigger = new SituationTrigger();

        final List<Situation> situations =
            parseSituations(queryNodeSet(situationTriggerElement, SituationTrigger.xpath_query_situations));

        final String onActivation =
            getNodeContent(queryNodeSet(situationTriggerElement,
                SituationTrigger.xpath_query_situationtrigger_onActivation).item(0));
        final String isSingleInstance =
            getNodeContent(queryNodeSet(situationTriggerElement,
                SituationTrigger.xpath_query_situationtrigger_isSingleInstance).item(0));
        final String serviceInstanceId =
            getNodeContent(queryNodeSet(situationTriggerElement,
                SituationTrigger.xpath_query_situationtrigger_serviceInstanceId).item(0));

        NodeList nodeInstanceIdList = null;
        if ((nodeInstanceIdList = queryNodeSet(situationTriggerElement,
            SituationTrigger.xpath_query_situationtrigger_nodeInstanceId)).getLength() != 0) {
            final String nodeInstanceId = getNodeContent(nodeInstanceIdList.item(0));
            trigger.setNodeInstanceId(nodeInstanceId);
        }

        final String interfaceName =
            getNodeContent(queryNodeSet(situationTriggerElement,
                SituationTrigger.xpath_query_situationtrigger_interfaceName).item(0));
        final String operationName =
            getNodeContent(queryNodeSet(situationTriggerElement,
                SituationTrigger.xpath_query_situationtrigger_operationName).item(0));
        final List<Triplet<String, String, String>> inputParameters =
            parseParameters(situationTriggerElement, SituationTrigger.xpath_query_situationtrigger_inputParameters);

        trigger.setSituations(situations);
        trigger.setOnActivation(Boolean.valueOf(onActivation));
        trigger.setSingelInstance(Boolean.valueOf(isSingleInstance));
        trigger.setServiceInstanceId(serviceInstanceId);
        trigger.setInterfaceName(interfaceName);
        trigger.setOperationName(operationName);
        trigger.setInputParameters(inputParameters);

        return trigger;
    }

    private List<Triplet<String, String, String>> parseParameters(final Element situationTriggerElement,
                                                                  final String xpathQuery) throws XPathExpressionException {
        final List<Triplet<String, String, String>> parameters = new ArrayList<>();

        final NodeList parameterNodes = queryNodeSet(situationTriggerElement, xpathQuery);

        for (int i = 0; i < parameterNodes.getLength(); i++) {
            final String name =
                getNodeContent(queryNodeSet(parameterNodes.item(i), "//*[local-name()='name']").item(0));
            final String val =
                getNodeContent(queryNodeSet(parameterNodes.item(i), "//*[local-name()='Value']").item(0));
            final String type =
                getNodeContent(queryNodeSet(parameterNodes.item(i), "//*[local-name()='Type']").item(0));
            parameters.add(new Triplet<>(name, val, type));
        }
        return parameters;
    }

    private List<Situation> parseSituations(final NodeList situationElements) throws XPathExpressionException {
        final List<Situation> situations = new ArrayList<>();
        for (int i = 0; i < situationElements.getLength(); i++) {
            if (situationElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
                final Element situationElement = (Element) situationElements.item(i);
                final String situationTemplateId =
                    getNodeContent(queryNodeSet(situationElement,
                        SituationTrigger.xpath_query_situations_situationTemplateId).item(0));
                final String thingId =
                    getNodeContent(queryNodeSet(situationElement,
                        SituationTrigger.xpath_query_situations_thingId).item(0));
                situations.add(new Situation(situationTemplateId, thingId,
                    Boolean.valueOf(situationElement.getAttribute("fromInput"))));
            }
        }

        return situations;
    }

    private String getNodeContent(final Node node) {
        return node.getTextContent();
    }
}
