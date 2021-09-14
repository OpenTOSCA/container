package org.opentosca.planbuilder.core.bpel.fragments;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
@Component
public class BPELProcessFragments {

    private final static Logger LOG = LoggerFactory.getLogger(BPELProcessFragments.class);

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPELProcessFragments() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public Node transformStringToNode(String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createAssignVarWithLiteral(final String literal, final String varName,
                                             final String intent) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/assignVarWithLiteral.xml"));
        template = template.replaceAll("\\$literal", literal);
        template = template.replaceAll("\\$VarName", varName);
        template = template.replaceAll("\\$intent", intent);
        return template;
    }

    public Node createAssignTransformXmltoString(final BPELPlan plan, final String xmlVar, final String stringVar,
                                                 final Element elementToAppendBefore, String rootElementName) throws IOException,
        SAXException {
        final String xpathQuery1 = "ode:dom-to-string(\\$" + xmlVar + "/*[local-name()='" + rootElementName + "'])";
        final String xpathQuery2 = "\\$" + stringVar;

        return this.createAssignVarToVarWithXpathQueriesAsNode("transformXMLtoStringVar", xmlVar, null,
            stringVar, null, xpathQuery1, xpathQuery2,
            "Transforms one xml var to a string var as ODE sets a an xml element as wrapper around complex type when using the rest extension.",
            new QName(
                "http://www.apache.org/ode/type/extension",
                "ode", "ode"));
    }

    public Node createAssignVarWithLiteralAsNode(final String literal, final String varName,
                                                 final String intent) throws IOException, SAXException {
        final String templateString = createAssignVarWithLiteral(literal, varName, intent);
        return this.transformStringToNode(templateString);
    }

    public Node createAssignVarToVarWithXpathQueryAsNode(final String assignName, final String fromVarName,
                                                         final String toVarName,
                                                         final String xpathQuery) throws IOException, SAXException {
        final String templateString =
            createAssignVarToVarWithXPathQuery(assignName, fromVarName, toVarName, xpathQuery);
        return this.transformStringToNode(templateString);
    }

    public String createAssignAndPostSituationMonitor(Map<TNodeTemplate, Collection<TPolicy>> situationPolicies,
                                                      Map<TPolicy, String> policy2IdMap, String serviceTemplateInstanceUrlVarName, String anyVarName, String requestVarName) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPELMonitoringSituation.xml"));

        StringBuilder situationIdRequestBody = new StringBuilder();
        StringBuilder copyFromInputToRequestBody = new StringBuilder();

        /*
         * <bpel:copy>
                <bpel:from part="payload" variable="input"><bpel:query queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![CDATA[//*[local-name()='$inputElementLocalName']/text()]]></bpel:query></bpel:from>
                <bpel:to variable="$requestVar">
                    <bpel:query queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0"><![CDATA[//*[local-name()='SituationsMonitor']/*[local-name()='Situations']/*[local-name()='SituationId']/[$index]]]>
                    </bpel:query>
                </bpel:to>
            </bpel:copy>
         */

        for (TNodeTemplate node : situationPolicies.keySet()) {
            String nodeTemplateId = node.getId();
            List<TPolicy> policies = new ArrayList<>(situationPolicies.get(node));

            situationIdRequestBody.append("<entry><key>").append(nodeTemplateId).append("</key><value><SituationIdsList>");
            for (int i = 0; i < policies.size(); i++) {
                TPolicy policy = policies.get(i);
                String inputLocalName = policy2IdMap.get(policy);

                situationIdRequestBody.append("<situationId/>");
                copyFromInputToRequestBody.append("<bpel:copy><bpel:from part=\"payload\" variable=\"input\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()='").append(inputLocalName).append("']/text()]]></bpel:query></bpel:from><bpel:to variable=\"$anyVar\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0\"><![CDATA[//*[local-name()='SituationsMonitor']/*[local-name()='NodeIds2SituationIds']/*[local-name()='entry' and ./*[local-name()='key' and text()='").append(nodeTemplateId).append("']]/*[local-name()='value']/*[local-name()='SituationIdsList']/*[local-name()='situationId'][").append(i + 1).append("]]]></bpel:query></bpel:to></bpel:copy>");
            }
            situationIdRequestBody.append("</SituationIdsList></value></entry>");
        }

//        for(int i = 0; i < situationIdInputLocalNames.size() ; i++) {
//            String inputLocalName = situationIdInputLocalNames.get(i);
//            situationIdRequestBody += "<SituationId/>";
//
//
//            copyFromInputToRequestBody += "<bpel:copy><bpel:from part=\"payload\" variable=\"input\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()='"+inputLocalName+"']/text()]]></bpel:query></bpel:from><bpel:to variable=\"$anyVar\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0\"><![CDATA[//*[local-name()='SituationsMonitor']/*[local-name()='Situations']/*[local-name()='SituationId']["+String.valueOf(i+1)+"]]]></bpel:query></bpel:to></bpel:copy>";
//        }

        template = template.replace("$SituationIds", situationIdRequestBody.toString());
        template = template.replace("$situationIdFromInputCopies", copyFromInputToRequestBody.toString());
        template = template.replace("$anyVar", anyVarName);
        template = template.replace("$requestVar", requestVarName);
        template = template.replace("$urlVarName", serviceTemplateInstanceUrlVarName);

        return template;
    }

    public Node createAssignAndPostSituationMonitorAsNode(Map<TNodeTemplate, Collection<TPolicy>> situationPolicies,
                                                          Map<TPolicy, String> policy2IdMap, String serviceTemplateInstanceUrlVarName, String anyVarName, String requestVarName) throws SAXException, IOException {
        final String templateString = this.createAssignAndPostSituationMonitor(situationPolicies, policy2IdMap, serviceTemplateInstanceUrlVarName, anyVarName, requestVarName);
        return this.transformStringToNode(templateString);
    }

    public String createAssignVarToVarWithXPathQuery(final String assignName, final String fromVarName,
                                                     final String toVarName,
                                                     final String xpathQuery) throws IOException {
        // <!-- $xpath2query, $fromVarName, $toVarName -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/assignVarFromVarWithXpath2Query.xml"));
        template = template.replaceAll("\\$assignName", assignName);
        template = template.replaceAll("\\$fromVarName", fromVarName);
        template = template.replaceAll("\\$toVarName", toVarName);
        template = template.replace("$xpath2query", xpathQuery);
        return template;
    }

    public Node createHTTPPOST(final String urlVarName, final String responseVarName) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI.xml"));
        template = template.replaceAll("\\$InstanceDataURLVar", urlVarName);
        template = template.replaceAll("\\$ResponseVarName", responseVarName);
        return this.transformStringToNode(template);
    }

    public String createAssignVarToVarWithXpathQueries(final String assignName, final String fromVarName,
                                                       final String part1, final String toVarName, final String part2,
                                                       final String xpathQuery1, final String xpathQuery2,
                                                       final String intent, final QName extension) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/assignVarFromVarWithXpath2Queries.xml"));
        template = template.replaceAll("\\$assignName", assignName);
        template = template.replaceAll("\\$fromVarName", fromVarName);
        template = template.replaceAll("\\$toVarName", toVarName);
        template = template.replace("$xpath2query1", xpathQuery1);
        if (part1 != null) {
            template = template.replaceAll("\\$part1", "part=\"" + part1 + "\"");
        } else {
            template = template.replaceAll("\\$part1", "");
        }
        template = template.replace("$xpath2query2", xpathQuery2);

        if (part2 != null) {
            template = template.replaceAll("\\$part2", "part=\"" + part2 + "\"");
        } else {
            template = template.replaceAll("\\$part2", "");
        }
        template = template.replaceAll("\\$intent", intent);

        if (extension != null) {
            template =
                template.replaceAll("\\$extension",
                    "xmlns:" + extension.getPrefix() + "=\"" + extension.getNamespaceURI() + "\"");
        } else {
            template = template.replaceAll("\\$extension", "");
        }
        return template;
    }

    public Node createAssignVarToVarWithXpathQueriesAsNode(final String assignName, final String fromVarName,
                                                           final String part1, final String toVarName,
                                                           final String part2, final String xpathQuery1,
                                                           final String xpathQuery2, final String intent,
                                                           final QName extension) throws IOException, SAXException {
        final String templateString =
            createAssignVarToVarWithXpathQueries(assignName, fromVarName, part1, toVarName, part2, xpathQuery1,
                xpathQuery2, intent, extension);
        return this.transformStringToNode(templateString);
    }

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property response and sets the
     * given variables
     *
     * @param assignName                          the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap          a Map from DOM Elements (representing Node Properties) to BPEL
     *                                            variable names
     * @return a Node containing a BPEL assign activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node createAssignFromInstancePropertyToBPELVariableAsNode(final String assignName,
                                                                     final String nodeInstancePropertyResponseVarName,
                                                                     final Map<String, String> propElement2BpelVarNameMap, String namespace) throws IOException,
        SAXException {
        final String templateString =
            createAssignFromInstancePropertyToBPELVariableAsString(assignName, nodeInstancePropertyResponseVarName,
                propElement2BpelVarNameMap, namespace);
        return this.transformStringToNode(templateString);
    }

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property response and sets the
     * given variables
     *
     * @param assignName                          the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap          a Map from DOM Elements (representing Node Properties) to BPEL
     *                                            variable names
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String createAssignFromInstancePropertyToBPELVariableAsString(final String assignName,
                                                                         final String nodeInstancePropertyResponseVarName,
                                                                         final Map<String, String> propElement2BpelVarNameMap, String namespace) throws IOException {
        final String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelCopyFromPropertyVarToNodeInstanceProperty.xml"));

        StringBuilder assignString =
            new StringBuilder("<bpel:assign name=\"" + assignName + "\" xmlns:bpel=\"" + BPELPlan.bpelNamespace + "\" >");

        // <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
        // $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
        for (final String propElement : propElement2BpelVarNameMap.keySet()) {
            String copyString = template.replace("$PropertyVarName", propElement2BpelVarNameMap.get(propElement));
            copyString = copyString.replace("$NodeInstancePropertyRequestVarName", nodeInstancePropertyResponseVarName);
            copyString = copyString.replace("$NodeInstancePropertyLocalName", propElement);
            copyString = copyString.replace("$NodeInstancePropertyNamespace", namespace);
            assignString.append(copyString);
        }

        assignString.append("</bpel:assign>");

        BPELProcessFragments.LOG.debug("Generated following assign string:");
        BPELProcessFragments.LOG.debug(assignString.toString());

        return assignString.toString();
    }

    public String createAssignSelectFirstRelationInstanceFromResponse(final String referencesResponseVarName, final String resultVarName) throws IOException {
        String bpelAssignString =
            ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignSelectFromRelationInstancesRequestToStringVar.xml"));

        bpelAssignString =
            bpelAssignString.replaceAll("\\$assignName", "assignSelectFirstReference" + System.currentTimeMillis());
        bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", resultVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$NodeInstancesResponseVarName", referencesResponseVarName);
        return bpelAssignString;
    }

    public Node createAssignSelectFirstRelationInstanceFromResponseAsNode(final String referencesResponseVarName, final String resultVarName) throws SAXException, IOException {
        final String templateString = this.createAssignSelectFirstRelationInstanceFromResponse(referencesResponseVarName, resultVarName);
        return this.transformStringToNode(templateString);
    }

    public String createAssignSelectFirstNodeInstanceAndAssignToStringVar(final String referencesResponseVarName,
                                                                          final String stringVarName,
                                                                          final String idVarName) throws IOException {
        // BpelAssignSelectFromNodeInstancesRequestToStringVar.xml
        // <!-- $assignName, $stringVarName, $NodeInstancesResponseVarName -->
        String bpelAssignString =
            ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignSelectFromNodeInstancesRequestToStringVar.xml"));

        bpelAssignString =
            bpelAssignString.replaceAll("\\$assignName", "assignSelectFirstReference" + System.currentTimeMillis());
        bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$NodeInstancesResponseVarName", referencesResponseVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$nodeInstanceIDVar", idVarName);
        return bpelAssignString;
    }

    public Node createAssignSelectFirstNodeInstanceAndAssignToStringVarAsNode(final String referencesResponseVarName,
                                                                              final String stringVarName,
                                                                              final String idVarName) throws IOException,
        SAXException {
        final String templateString =
            createAssignSelectFirstNodeInstanceAndAssignToStringVar(referencesResponseVarName, stringVarName, idVarName);
        return this.transformStringToNode(templateString);
    }

    public Node createEmptySequence(String name) {
        final Document doc = this.docBuilder.newDocument();
        final Element scopeElement = doc.createElementNS(BPELPlan.bpelNamespace, "sequence");
        scopeElement.setAttribute("name" , name);
        return scopeElement;
    }

    public Node createIfTrueThrowsError(final String xpath, final QName faultName, String faultVariableName) {
        final Document doc = this.docBuilder.newDocument();

        final Element ifElement = doc.createElementNS(BPELPlan.bpelNamespace, "if");

        final Element conditionElement = doc.createElementNS(BPELPlan.bpelNamespace, "condition");

        conditionElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);

        final Text textSectionValue = doc.createTextNode(xpath);
        conditionElement.appendChild(textSectionValue);

        ifElement.appendChild(conditionElement);

        final Element throwElement = doc.createElementNS(BPELPlan.bpelNamespace, "throw");

        final String nsPrefix = "ns" + System.currentTimeMillis();

        throwElement.setAttribute("xmlns:" + nsPrefix, faultName.getNamespaceURI());

        throwElement.setAttribute("faultName", nsPrefix + ":" + faultName.getLocalPart());

        if (faultVariableName != null) {
            throwElement.setAttribute("faultVariable", faultVariableName);
        }

        ifElement.appendChild(throwElement);

        return ifElement;
    }

    public Node createWait(String durationExpression) throws IOException, SAXException {
        String templateString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPELWait.xml"));

        templateString = templateString.replace("$durationexpression", durationExpression);

        return this.transformStringToNode(templateString);
    }

    public Node createWaitForCondition(String xpathExpr, String durationExpression) throws IOException, SAXException {
        String templateString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPELWaitTillTrue.xml"));

        templateString = templateString.replace("$xpath1Expr", xpathExpr);
        templateString = templateString.replace("$durationexpression", durationExpression);

        return this.transformStringToNode(templateString);
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String variable.
     *
     * @param assignName    the name of the BPEL assign
     * @param xpath2Query   the xPath query
     * @param stringVarName the variable to load the queries results into
     * @return a DOM Node representing a BPEL assign element
     * @throws IOException  is thrown when loading internal bpel fragments fails
     * @throws SAXException is thrown when parsing internal format into DOM fails
     */
    public Node createAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                                                                final String stringVarName) throws IOException,
        SAXException {
        final String templateString =
            createAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String variable.
     *
     * @param assignName    the name of the BPEL assign
     * @param xpath2Query   the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String createAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                                                                    final String stringVarName) throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/assignStringVarWithXpath2Query.xml"));
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }

    public String createBPEL4RESTLightPlanInstanceLOGsPOST(final String urlVarName,
                                                           final String stringVarNameWithLogContent,
                                                           final String unassignedLogReqMessage) throws IOException {
        // BPEL4RESTLightPOST_PlanInstance_Logs.xml
        // <!-- $urlVarName, $requestVar, $correlationId -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightPOST_PlanInstance_Logs.xml"));
        template = template.replaceAll("\\$urlVarName", urlVarName);
        template = template.replaceAll("\\$requestVar", unassignedLogReqMessage);
        template = template.replaceAll("\\$contentStringVar", stringVarNameWithLogContent);
        return template;
    }

    public Node createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(final String urlVarName,
                                                               final String stringVarNameWithLogContent,
                                                               final String unassignedLogReqMessage) throws IOException,
        SAXException {
        final String templateString =
            createBPEL4RESTLightPlanInstanceLOGsPOST(urlVarName, stringVarNameWithLogContent, unassignedLogReqMessage);
        return this.transformStringToNode(templateString);
    }

    public String createBPEL4RESTLightPUTState(final String instanceURLVarName,
                                               final String requestVarName) throws IOException {
        // <!-- $urlVarName, $requestVar -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightPUTInstanceState.xml"));
        template = template.replace("$urlVarName", instanceURLVarName);
        template = template.replace("$requestVar", requestVarName);
        return template;
    }

    public Node createBPEL4RESTLightPutStateAsNode(final String instanceURLVarName,
                                                   final String requestVarName) throws IOException, SAXException {
        final String templateString = createBPEL4RESTLightPUTState(instanceURLVarName, requestVarName);
        return this.transformStringToNode(templateString);
    }

    public Node createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(final String serviceInstanceIdVarName,
                                                                                      final String relationshipTemplateId,
                                                                                      final String responseVarName,
                                                                                      final String nodeInstanceIdVarName) throws IOException,
        SAXException {
        final String templateString =
            createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(serviceInstanceIdVarName,
                relationshipTemplateId,
                responseVarName,
                nodeInstanceIdVarName);
        return this.transformStringToNode(templateString);
    }

    public String createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(final String serviceTemplateUrlVarName,
                                                                                          final String relationshipTemplateId,
                                                                                          final String responseVarName,
                                                                                          final String nodeInstanceIdVarName) throws IOException {
        // BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml
        // <!-- $serviceInstanceURLVar, $relationshipTemplateId, $ResponseVarName,
        // $nodeInstanceIdVarName -->
        String template =
            ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml"));

        // <!-- $ServiceTemplateURLVarKeyword, $relationshipTemplateId,
        // $nodeInstanceIdVarName,
        // $ResponseVarName-->

        template = template.replace("$ServiceTemplateURLVarKeyword", serviceTemplateUrlVarName);
        template = template.replace("$relationshipTemplateId", relationshipTemplateId);
        template = template.replace("$ResponseVarName", responseVarName);
        template = template.replace("$nodeInstanceIdVarName", nodeInstanceIdVarName);
        return template;
    }

    /**
     * Creates a Node containing a BPEL fragment which uses the BPELRESTExtension to fetch the InstanceData from an
     * OpenTOSCA Container instanceDataAPI
     *
     * @param serviceTemplateUrlVar the name of the variable holding an URL to a serviceTemplate
     * @param responseVarName       the name of the variable holding the response of the request (must be xsd:anyType)
     * @param templateId            the id of the template the instance belongs to
     * @return a Node containing a BPEL Fragment
     * @throws IOException  is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTExtensionGETForNodeInstanceDataAsNode(final String serviceTemplateUrlVar,
                                                                final String responseVarName, final String templateId,
                                                                final String query) throws SAXException, IOException {
        final String templateString =
            createRESTExtensionGETForNodeInstanceDataAsString(serviceTemplateUrlVar, responseVarName, templateId,
                query);
        return this.transformStringToNode(templateString);
    }

    /**
     * Creates a String containing a BPEL fragment which uses the BPELRESTExtension to fetch the InstanceData from an
     * OpenTOSCA Container instanceDataAPI
     *
     * @param serviceTemplateUrlVar the name of the variable holding an URL to a serviceTemplate
     * @param responseVarName       the name of the variable holding the response of the request (must be xsd:anyType)
     * @param templateId            the id of the template the instance belongs to
     * @return a String containing a BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTExtensionGETForNodeInstanceDataAsString(final String serviceTemplateUrlVar,
                                                                    final String responseVarName,
                                                                    final String templateId,
                                                                    final String query) throws IOException {
        // <!-- $InstanceDataURLVar, $ResponseVarName, $TemplateId,
        // $serviceInstanceUrlVarName, $templateType -->

        // <!-- $InstanceDataURLVar, $ResponseVarName, $nodeType -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml"));
        template = template.replaceAll("\\$InstanceDataURLVar", serviceTemplateUrlVar);
        template = template.replaceAll("\\$ResponseVarName", responseVarName);
        template = template.replaceAll("\\$templateId", templateId);

        template = template.replace("?query", Objects.requireNonNullElse(query, ""));

        return template;
    }

    public Node createRESTExtensionGETForRelationInstanceDataAsNode(final String serviceTemplateUrlVar,
                                                                    final String responseVarName,
                                                                    final String templateId,
                                                                    final String query) throws SAXException, IOException {
        final String templateString =
            createRESTExtensionGETForRelationInstanceDataAsString(serviceTemplateUrlVar, responseVarName, templateId,
                query);
        return this.transformStringToNode(templateString);
    }

    public String createRESTExtensionGETForRelationInstanceDataAsString(final String serviceTemplateUrlVar,
                                                                        final String responseVarName,
                                                                        final String templateId,
                                                                        final String query) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightGET_RelationInstance_InstanceDataAPI.xml"));
        template = template.replaceAll("\\$InstanceDataURLVar", serviceTemplateUrlVar);
        template = template.replaceAll("\\$ResponseVarName", responseVarName);
        template = template.replaceAll("\\$templateId", templateId);

        template = template.replace("?query", Objects.requireNonNullElse(query, ""));

        return template;
    }

    /**
     * Creates a RESTExtension GET to fetch properties of NodeInstance
     *
     * @param nodeInstanceIDUrl the name of the variable holding the address to the nodeInstance
     * @param responseVarName   the name of the variable to store the response into
     * @return a Node containing a BPEL RESTExtension Activity
     * @throws IOException  is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTExtensionGETForInstancePropertiesAsNode(final String nodeInstanceIDUrl,
                                                                  final String responseVarName) throws IOException,
        SAXException {
        final String templateString =
            createRESTExtensionGETForInstancePropertiesAsString(nodeInstanceIDUrl, responseVarName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Creates a RESTExtension GET to fetch properties of NodeInstance
     *
     * @param nodeInstanceIDUrl the name of the variable holding the address to the nodeInstance
     * @param responseVarName   the name of the variable to store the response into
     * @return a String containing a BPEL RESTExtension Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTExtensionGETForInstancePropertiesAsString(final String nodeInstanceIDUrl,
                                                                      final String responseVarName) throws IOException {
        // <!-- $urlVarName, $ResponseVarName -->
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightGET_NodeInstance_Properties.xml"));
        template = template.replace("$urlVarName", nodeInstanceIDUrl);
        template = template.replace("$ResponseVarName", responseVarName);

        return template;
    }

    /**
     * Returns an XPath Query which contructs a valid String, to GET a File from the openTOSCA API
     *
     * @param artifactPath a path inside an ArtifactTemplate
     * @return a String containing an XPath query
     */
    public String createXPathQueryForURLRemoteFilePath(final String artifactPath) {
        BPELProcessFragments.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
        return "string(concat(substring-before($input.payload//*[local-name()='instanceDataAPIUrl']/text(),'/servicetemplates'),'/content/"
            + artifactPath + "'))";
    }

    public String createXPathQueryForURLRemoteFilePathViaContainerAPI(final String artifactPath, final String csarId) {
        BPELProcessFragments.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
        return "string(concat($input.payload//*[local-name()='containerApiAddress']/text(),'/csars/" + csarId + "', '/content/"
            + artifactPath + "'))";
    }

    public String generateServiceInstanceRequestMessageAssign(final String inputMessageElementLocalName,
                                                              final String anyElementariableName) throws IOException {
        String bpelAssignString =
            ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignServiceInstanceCorrelationIdPOSTRequest.xml"));
        // <!-- $inputElementLocalName, $StringVariableName, $assignName -->
        bpelAssignString = bpelAssignString.replaceAll("\\$inputElementLocalName", inputMessageElementLocalName);
        bpelAssignString = bpelAssignString.replaceAll("\\$StringVariableName", anyElementariableName);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$assignName",
                "assignServiceInstanceCreateRequest" + System.currentTimeMillis());
        return bpelAssignString;
    }

    public Node generateServiceInstanceRequestMessageAssignAsNode(final String inputMessageElementLocalName,
                                                                  final String anyElementariableName) throws IOException,
        SAXException {
        final String templateString =
            generateServiceInstanceRequestMessageAssign(inputMessageElementLocalName, anyElementariableName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Generates an assign activity that fetches the value of the input message and writes it into a string variable
     *
     * @param inputMessageElementLocalName the localName of the element inside the input message
     * @param stringVariableName           the name of the variable to assign the value to
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateAssignFromInputMessageToStringVariable(final String inputMessageElementLocalName,
                                                                 final String stringVariableName) throws IOException {
        String bpelAssignString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignFromInputToStringVar.xml"));
        // <!-- $inputElementLocalName, $StringVariableName, $assignName -->
        bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
        bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
        bpelAssignString =
            bpelAssignString.replace("$assignName", "assignFromInputToString" + System.currentTimeMillis());
        return bpelAssignString;
    }

    /**
     * Generates an assign activity that fetches the value of the input message and writes it into a string variable
     *
     * @param inputMessageElementLocalName the localName of the element inside the input message
     * @param stringVariableName           the name of the variable to assign the value to
     * @return a Node containing a BPEL assign activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateAssignFromInputMessageToStringVariableAsNode(final String inputMessageElementLocalName,
                                                                     final String stringVariableName) throws IOException,
        SAXException {
        final String templateString =
            generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName, stringVariableName);
        return this.transformStringToNode(templateString);
    }

    public String generateBPEL4RESTLightGETonURL(final String urlVarName,
                                                 final String responseVarName, final String statusCodeVarName) throws IOException {
        String bpelAssignString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightGET_URL_ApplicationXML.xml"));
        // <!-- $ResponseVarName, $urlVar -->
        bpelAssignString = bpelAssignString.replace("$ResponseVarName", responseVarName);
        bpelAssignString = bpelAssignString.replace("$urlVar", urlVarName);

        if (statusCodeVarName == null) {
            bpelAssignString = bpelAssignString.replace("statusCode=\"$StatusCodeVarName\"", "");
        } else {
            bpelAssignString = bpelAssignString.replace("$StatusCodeVarName", statusCodeVarName);
        }

        return bpelAssignString;
    }

    public Node generateBPEL4RESTLightGETonURLAsNode(final String urlVarName,
                                                     final String reponseVarName, final String statusCodeVarName) throws IOException, SAXException {
        final String templateString = generateBPEL4RESTLightGETonURL(urlVarName, reponseVarName, statusCodeVarName);
        return this.transformStringToNode(templateString);
    }

    public Path getOpenTOSCAAPISchemaFile() throws IOException {
        final URL url = getClass().getClassLoader().getResource("schemas/opentoscaapischema.xsd");
        ResourceAccess resource = new ResourceAccess(Objects.requireNonNull(url, "url may not be null"));
        return resource.resolvedPath();
    }

    public QName getOpenToscaApiCorrelationElementQname() {
        return new QName("http://opentosca.org/api", "correlationID");
    }

    public QName getOpenToscaApiCreateRelationshipTemplateInstanceRequestElementQname() {
        return new QName("http://opentosca.org/api", "CreateRelationshipTemplateInstanceRequest");
    }

    public QName getOpenToscaApiLogMsgReqElementQName() {
        return new QName("http://opentosca.org/api", "log");
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a Service
     * Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the instanceDataAPI
     * @param responseVariableName           a name of an anyType variable to save the response into
     * @return a String containing a BPEL4RESTLight POST extension activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateBPEL4RESTLightServiceInstancePOST(final String instanceDataAPIUrlVariableName,
                                                            final String requestVariableName,
                                                            final String responseVariableName) throws IOException {
        // tags in xml snippet: $InstanceDataURLVar, $CSARName,
        // $serviceTemplateId, $ResponseVarName
        String bpel4RestString =
            ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI_WithBody.xml"));

        bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
        bpel4RestString = bpel4RestString.replace("$RequestVarName", requestVariableName);
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

        return bpel4RestString;
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a Service
     * Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the instanceDataAPI
     * @param requestVariableName            a name of an anyType variable to take the request content from
     * @param responseVariableName           a name of an anyType variable to save the response into
     * @return a Node containing a BPEL4RESTLight POST extension activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(final String instanceDataAPIUrlVariableName,
                                                                final String requestVariableName,
                                                                final String responseVariableName) throws IOException,
        SAXException {
        final String templateString =
            this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName, requestVariableName,
                responseVariableName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a Node containing a BPEL If Activity
     * @throws IOException  is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node generateBPELIfTrueThrowFaultAsNode(final String xpath1Expr, final QName faultQName) throws IOException,
        SAXException {
        final String templateString = generateBPELIfTrueThrowFaultAsString(xpath1Expr, faultQName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a String containing a BPEL If Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateBPELIfTrueThrowFaultAsString(final String xpath1Expr,
                                                       final QName faultQName) throws IOException {
        // <!-- $xpath1Expr, $faultPrefix, $faultNamespace, $faultLocalName-->
        String bpelIfString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BPELIfTrueThrowFault.xml"));

        bpelIfString = bpelIfString.replace("$xpath1Expr", xpath1Expr);

        bpelIfString = bpelIfString.replace("$faultPrefix", faultQName.getLocalPart());
        bpelIfString = bpelIfString.replace("$faultLocalName", faultQName.getLocalPart());

        return bpelIfString;
    }

    /**
     * Generates an Assign Acitivity that writes the content of a Strig variable into the first element specified by
     * prefix and localname
     *
     * @param variableName       the name of the string variable to take the value from
     * @param outputVarName      the name of the output message variable
     * @param outputVarPartName  the name of the part inside the message variable
     * @param outputVarLocalName the localName of the element inside the message part
     * @return a DOM Node containing a BPEL Assign Activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateCopyFromStringVarToOutputVariableAsNode(final String variableName, final String outputVarName,
                                                                final String outputVarPartName,
                                                                final String outputVarLocalName) throws IOException,
        SAXException {
        final String templateString =
            generateCopyFromStringVarToOutputVariableAsString(variableName, outputVarName, outputVarPartName,
                outputVarLocalName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Generates an Assign Activity that writes the content of a String variable into the first element specified by
     * prefix and localname
     *
     * @param variableName       the name of the string variable to take the value from
     * @param outputVarName      the name of the output message variable
     * @param outputVarPartName  the name of the part inside the message variable
     * @param outputVarLocalName the localname of the element inside the message part
     * @return a String containing a BPEL assign activitiy
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateCopyFromStringVarToOutputVariableAsString(final String variableName,
                                                                    final String outputVarName,
                                                                    final String outputVarPartName,
                                                                    final String outputVarLocalName) throws IOException {
        // BpelAssignOutputVarFromStringVariable.xml
        // <!-- ${assignName}, ${variableName}, ${outputVarName}, ${outputVarPartName}, ${outputVarPrefix}, ${outputVarLocalName} -->
        String bpelAssignString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelCopyOutputVarFromStringVariable.xml"));
        bpelAssignString = bpelAssignString.replace("${variableName}", variableName);
        bpelAssignString = bpelAssignString.replace("${outputVarName}", outputVarName);
        bpelAssignString = bpelAssignString.replace("${outputVarPartName}", outputVarPartName);
        bpelAssignString = bpelAssignString.replace("${outputVarLocalName}", outputVarLocalName);
        return bpelAssignString;
    }

    /**
     * Generates a BPEL assign that sets serviceInstanceURL, ID and serviceTemplate URL from the given
     * serviceTemplateInstance POST response message
     *
     * @param serviceInstanceResponseVarName the varariable name of the POST response message
     * @param serviceInstanceURLVarName      the varariable name to save the serviceTemplateInstance URL
     * @param serviceTemplateInstancesURLVar the variable name that holds the serviceTemplateInstances URL
     * @param serviceTemplateURLVarName      the variable name to save the serviceTemplate URL in
     * @return a DOM Node containing a BPEL assign activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateServiceInstanceDataVarsAssignForBuildPlansAsNode(final String serviceInstanceResponseVarName,
                                                                         final String serviceInstanceURLVarName,
                                                                         final String serviceTemplateInstancesURLVar,
                                                                         final String serviceInstanceIdVarName,
                                                                         final String serviceTemplateURLVarName,
                                                                         final String serviceInstanceCorrelationIdVarName,
                                                                         final String planName,
                                                                         final String buildPlanUrlVarName) throws IOException,
        SAXException {
        final String templateString =
            generateServiceInstanceDataVarsAssignForBuildPlansAsString(serviceInstanceResponseVarName,
                serviceInstanceURLVarName,
                serviceTemplateInstancesURLVar,
                serviceInstanceIdVarName,
                serviceTemplateURLVarName,
                serviceInstanceCorrelationIdVarName, planName,
                buildPlanUrlVarName);
        return this.transformStringToNode(templateString);
    }

    /**
     * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance POST response
     *
     * @param serviceInstanceResponseVarName the var name of the POST response
     * @param serviceInstanceURLVarName      the var name to save the URL/ID into
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */

    public String generateServiceInstanceDataVarsAssignForBuildPlansAsString(final String serviceInstanceResponseVarName,
                                                                             final String serviceInstanceURLVarName,
                                                                             final String serviceTemplateInstancesURLVar,
                                                                             final String serviceInstanceIdVarName,
                                                                             final String serviceTemplateURLVarName,
                                                                             final String serviceInstanceCorrelationIdVarName,
                                                                             final String planName,
                                                                             final String buildPlanUrlVarName) throws IOException {
        // <!-- $assignName $ServiceInstanceResponseVarName $ServiceInstanceURLVarName-->
        String bpelAssignString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignServiceInstancePOSTResponse.xml"));
        bpelAssignString =
            bpelAssignString.replaceAll("\\$assignName", "assignServiceInstance" + System.currentTimeMillis());
        bpelAssignString =
            bpelAssignString.replaceAll("\\$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$ServiceInstanceURLVarName", serviceInstanceURLVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$ServiceInstanceIDVarName", serviceInstanceIdVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$serviceTemplateURLVarName", serviceTemplateURLVarName);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$serviceTemplateInstancesURLVar", serviceTemplateInstancesURLVar);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$ServiceInstanceCorrelationID", serviceInstanceCorrelationIdVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$planName", planName);
        bpelAssignString = bpelAssignString.replaceAll("\\$planInstanceURL", buildPlanUrlVarName);

        return bpelAssignString;
    }

    public Node generateServiceInstanceDataVarsAssignForManagementPlansAsNode(final String serviceInstanceResponseVarName,
                                                                              final String serviceInstanceURLVarName,
                                                                              final String serviceTemplateInstancesURLVar,
                                                                              final String serviceInstanceIdVarName,
                                                                              final String serviceTemplateURLVarName,
                                                                              final String serviceInstanceCorrelationIdVarName,
                                                                              final String planName,
                                                                              final String buildPlanUrlVarName) throws IOException,
        SAXException {
        final String templateString =
            generateServiceInstanceDataVarsAssignForManagementPlansAsString(serviceInstanceResponseVarName,
                serviceInstanceURLVarName,
                serviceTemplateInstancesURLVar,
                serviceInstanceIdVarName,
                serviceTemplateURLVarName,
                serviceInstanceCorrelationIdVarName,
                planName, buildPlanUrlVarName);
        return this.transformStringToNode(templateString);
    }

    public String generateServiceInstanceDataVarsAssignForManagementPlansAsString(final String serviceInstanceResponseVarName,
                                                                                  final String serviceInstanceURLVarName,
                                                                                  final String serviceTemplateInstancesURLVar,
                                                                                  final String serviceInstanceIdVarName,
                                                                                  final String serviceTemplateURLVarName,
                                                                                  final String serviceInstanceCorrelationIdVarName,
                                                                                  final String planName,
                                                                                  final String buildPlanUrlVarName) throws IOException {
        // <!-- $assignName $ServiceInstanceResponseVarName $ServiceInstanceURLVarName-->
        String bpelAssignString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelAssignServiceInstancePOSTResponse2.xml"));
        bpelAssignString =
            bpelAssignString.replaceAll("\\$assignName", "assignServiceInstance" + System.currentTimeMillis());
        bpelAssignString =
            bpelAssignString.replaceAll("\\$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$ServiceInstanceURLVarName", serviceInstanceURLVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$ServiceInstanceIDVarName", serviceInstanceIdVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$serviceTemplateURLVarName", serviceTemplateURLVarName);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$serviceTemplateInstancesURLVar", serviceTemplateInstancesURLVar);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$ServiceInstanceCorrelationID", serviceInstanceCorrelationIdVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$planName", planName);
        bpelAssignString = bpelAssignString.replaceAll("\\$planInstanceURL", buildPlanUrlVarName);

        return bpelAssignString;
    }
}
