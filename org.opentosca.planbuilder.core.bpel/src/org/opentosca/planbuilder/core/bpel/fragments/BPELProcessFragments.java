package org.opentosca.planbuilder.core.bpel.fragments;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
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

    /**
     * Create a BPEL assign that copies the NodeInstanceURL from a NodeInstances Query (See
     * {@link #createRESTExtensionGETForNodeInstanceDataAsNode(String, String, String, String, boolean)}
     *
     * @param assignName the name of the assign
     * @param stringVarName the name of the xsd:string variable to write the NodeInstanceId into
     * @param nodeInstanceResponseVarName the instanceDataAPI response to fetch the NodeInstanceId from
     * @return a Node containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsNode(final String assignName,
                    final String stringVarName, final String nodeInstanceResponseVarName)
        throws SAXException, IOException {
        final String templateString = this.createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(
            assignName, stringVarName, nodeInstanceResponseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Create a BPEL assign that copies the NodeInstanceURL from a NodeInstances Query (See
     * {@link #createRESTExtensionGETForNodeInstanceDataAsNode(String, String, String, String, boolean)}
     *
     * @param assignName the name of the assign
     * @param stringVarName the name of the xsd:string variable to write the NodeInstanceId into
     * @param nodeInstanceResponseVarName the instanceDataAPI response to fetch the NodeInstanceId from
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String createAssign2FetchNodeInstanceIDFromInstanceDataAPIResponseAsString(final String assignName,
                    final String stringVarName, final String nodeInstanceResponseVarName)
        throws IOException {
        // <!-- $assignName, $stringVarName, $NodeInstanceResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BpelAssignFromNodeInstanceRequestToStringVar.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelfragmentfile);
        template = template.replace("$assignName", assignName);
        template = template.replace("$stringVarName", stringVarName);
        template = template.replace("$NodeInstanceResponseVarName", nodeInstanceResponseVarName);

        return template;
    }

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property
     * response and sets the given variables
     *
     * @param assignName the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap a Map from DOM Elements (representing Node Properties) to BPEL
     *        variable names
     * @return a Node containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node createAssignFromNodeInstancePropertyToBPELVariableAsNode(final String assignName,
                    final String nodeInstancePropertyResponseVarName,
                    final Map<Element, String> propElement2BpelVarNameMap)
        throws IOException, SAXException {
        final String templateString = this.createAssignFromNodeInstancePropertyToBPELVariableAsString(assignName,
            nodeInstancePropertyResponseVarName, propElement2BpelVarNameMap);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property
     * response and sets the given variables
     *
     * @param assignName the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap a Map from DOM Elements (representing Node Properties) to BPEL
     *        variable names
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String createAssignFromNodeInstancePropertyToBPELVariableAsString(final String assignName,
                    final String nodeInstancePropertyResponseVarName,
                    final Map<Element, String> propElement2BpelVarNameMap)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        final String template = FileUtils.readFileToString(bpelfragmentfile);

        String assignString = "<bpel:assign name=\"" + assignName + "\" xmlns:bpel=\"" + BPELPlan.bpelNamespace
            + "\" >";

        // <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
        // $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
        for (final Element propElement : propElement2BpelVarNameMap.keySet()) {
            String copyString = template.replace("$PropertyVarName", propElement2BpelVarNameMap.get(propElement));
            copyString = copyString.replace("$NodeInstancePropertyRequestVarName", nodeInstancePropertyResponseVarName);
            copyString = copyString.replace("$NodeInstancePropertyLocalName", propElement.getLocalName());
            copyString = copyString.replace("$NodeInstancePropertyNamespace", propElement.getNamespaceURI());
            assignString += copyString;
        }

        assignString += "</bpel:assign>";

        BPELProcessFragments.LOG.debug("Generated following assign string:");
        BPELProcessFragments.LOG.debug(assignString);

        return assignString;
    }

    public String createAssignSelectFirstReferenceAndAssignToStringVar(final String referencesResponseVarName,
                    final String stringVarName)
        throws IOException {
        // BpelAssignSelectFromNodeInstancesRequestToStringVar.xml
        // <!-- $assignName, $stringVarName, $NodeInstancesResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BpelAssignSelectFromNodeInstancesRequestToStringVar.xml");
        final File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);

        bpelAssignString = bpelAssignString.replaceAll("\\$assignName",
            "assignSelectFirstReference" + System.currentTimeMillis());
        bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$NodeInstancesResponseVarName", referencesResponseVarName);
        return bpelAssignString;
    }

    public Node createAssignSelectFirstReferenceAndAssignToStringVarAsNode(final String referencesResponseVarName,
                    final String stringVarName)
        throws IOException, SAXException {
        final String templateString = this.createAssignSelectFirstReferenceAndAssignToStringVar(
            referencesResponseVarName, stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node createIfTrueThrowsError(final String xpath, final QName faultName) {
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

        ifElement.appendChild(throwElement);

        return ifElement;
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param xpath2Query the xPath query
     * @param stringVarName the variable to load the queries results into
     * @return a DOM Node representing a BPEL assign element
     * @throws IOException is thrown when loading internal bpel fragments fails
     * @throws SAXException is thrown when parsing internal format into DOM fails
     */
    public Node createAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                    final String stringVarName)
        throws IOException, SAXException {
        final String templateString = this.createAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query,
            stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param xpath2Query the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String createAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                    final String stringVarName)
        throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("assignStringVarWithXpath2Query.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }

    public Node createBPEL4RESTLightNodeInstancesGETAsNode(final String nodeTemplateId,
                    final String serviceInstanceIdVarName, final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.createBPEL4RESTLightNodeInstancesGETAsString(nodeTemplateId,
            serviceInstanceIdVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createBPEL4RESTLightNodeInstancesGETAsString(final String nodeTemplateId,
                    final String serviceInstanceIdVarName, final String responseVarName)
        throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightGET_NodeInstances_InstanceDataAPI.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
        template = template.replace("$ResponseVarName", responseVarName);
        template = template.replace("$nodeTemplateId", nodeTemplateId);
        return template;
    }

    public String createBPEL4RESTLightPlanInstanceLOGsPOST(final String urlVarName, final String requestVarName,
                    final String correlationIdVarName)
        throws IOException {
        // BPEL4RESTLightPOST_PlanInstance_Logs.xml
        // <!-- $urlVarName, $requestVar, $correlationId -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightPOST_PlanInstance_Logs.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$urlVarName", urlVarName);
        template = template.replace("$requestVar", requestVarName);
        template = template.replace("$correlationId", correlationIdVarName);
        return template;
    }

    public Node createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(final String urlVarName, final String requestVarName,
                    final String correlationIdVarName)
        throws IOException, SAXException {
        final String templateString = this.createBPEL4RESTLightPlanInstanceLOGsPOST(urlVarName, requestVarName,
            correlationIdVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createBPEL4RESTLightPUTState(final String instanceURLVarName, final String requestVarName)
        throws IOException {
        // <!-- $urlVarName, $requestVar -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("BPEL4RESTLightPUTInstanceState.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$urlVarName", instanceURLVarName);
        template = template.replace("$requestVar", requestVarName);
        return template;
    }

    public Node createBPEL4RESTLightPutStateAsNode(final String instanceURLVarName, final String requestVarName)
        throws IOException, SAXException {
        final String templateString = this.createBPEL4RESTLightPUTState(instanceURLVarName, requestVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node createBPEL4RESTLightRelationInstancesGETAsNode(final String relationshipTemplateId,
                    final String serviceInstanceIdVarName, final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.createBPEL4RESTLightRelationInstancesGETAsString(relationshipTemplateId,
            serviceInstanceIdVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createBPEL4RESTLightRelationInstancesGETAsString(final String relationshipTemplateId,
                    final String serviceInstanceIdVarName, final String responseVarName)
        throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightGET_RelationInstances_InstanceDataAPI.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
        template = template.replace("$ResponseVarName", responseVarName);
        template = template.replace("relationshipTemplateId", relationshipTemplateId);
        return template;
    }

    public Node createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsNode(
                    final String serviceInstanceIdVarName, final String relationshipTemplateId,
                    final String responseVarName, final String nodeInstanceIdVarName)
        throws IOException, SAXException {
        final String templateString = this.createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(
            serviceInstanceIdVarName, relationshipTemplateId, responseVarName, nodeInstanceIdVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createBPEL4RESTLightRelationInstancesTargetNodeInstanceQueryGETAsString(
                    final String serviceInstanceIdVarName, final String relationshipTemplateId,
                    final String responseVarName, final String nodeInstanceIdVarName)
        throws IOException {
        // BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml
        // <!-- $serviceInstanceURLVar, $relationshipTemplateId, $ResponseVarName,
        // $nodeInstanceIdVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource(
            "BPEL4RESTLightGET_RelationInstances_QueryOnTargetInstance_InstanceDataAPI.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$serviceInstanceURLVar", serviceInstanceIdVarName);
        template = template.replace("$relationshipTemplateId", relationshipTemplateId);
        template = template.replace("$ResponseVarName", responseVarName);
        template = template.replace("$nodeInstanceIdVarName", nodeInstanceIdVarName);
        return template;
    }

    /**
     * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to request on.
     *
     * @param bpelVarName the variable containing an URL
     * @param responseVarName the variable to hold the response
     * @return a String containing a BPEL4RESTLight Activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTDeleteOnURLBPELVarAsNode(final String bpelVarName, final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.createRESTDeleteOnURLBPELVarAsString(bpelVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to request on.
     *
     * @param bpelVarName the variable containing an URL
     * @param responseVarName the variable to hold the response
     * @return a String containing a BPEL4RESTLight Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTDeleteOnURLBPELVarAsString(final String bpelVarName, final String responseVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("BPEL4RESTLightDELETE.xml");
        // <!-- $urlVarName, $ResponseVarName -->

        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());

        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$urlVarName", bpelVarName);
        template = template.replace("$ResponseVarName", responseVarName);

        return template;
    }

    /**
     * Creates a Node containing a BPEL fragment which uses the BPELRESTExtension to fetch the
     * InstanceData from an OpenTOSCA Container instanceDataAPI
     *
     * @param serviceInstanceUrlVar the name of the variable holding an URL to a serviceInstance
     * @param responseVarName the name of the variable holding the response of the request (must be
     *        xsd:anyType)
     * @param templateId the id of the template the instance belongs to
     * @param serviceInstanceUrlVarName the name of the variable holding the id/link of the
     *        serviceInstance
     * @param isNodeTemplate whether the given tmeplateId belongs to a NodeTemplate or
     *        RelationshipTemplate
     * @return a Node containing a BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTExtensionGETForNodeInstanceDataAsNode(final String serviceInstanceUrlVar,
                    final String responseVarName, final String templateId, final String query)
        throws SAXException, IOException {
        final String templateString = this.createRESTExtensionGETForNodeInstanceDataAsString(serviceInstanceUrlVar,
            responseVarName, templateId, query);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Creates a String containing a BPEL fragment which uses the BPELRESTExtension to fetch the
     * InstanceData from an OpenTOSCA Container instanceDataAPI
     *
     * @param serviceInstanceUrlVar the name of the variable holding an URL to a serviceInstance
     * @param responseVarName the name of the variable holding the response of the request (must be
     *        xsd:anyType)
     * @param templateId the id of the template the instance belongs to
     * @param serviceInstanceUrlVarName the name of the variable holding the id/link of the
     *        serviceInstance
     * @param isNodeTemplate whether the given tmeplateId belongs to a NodeTemplate or
     *        RelationshipTemplate
     * @return a String containing a BPEL Fragment
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTExtensionGETForNodeInstanceDataAsString(final String serviceInstanceUrlVar,
                    final String responseVarName, final String templateId, final String query)
        throws IOException {
        // <!-- $InstanceDataURLVar, $ResponseVarName, $TemplateId,
        // $serviceInstanceUrlVarName, $templateType -->

        // <!-- $InstanceDataURLVar, $ResponseVarName, $nodeType -->

        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelfragmentfile);
        template = template.replaceAll("\\$InstanceDataURLVar", serviceInstanceUrlVar);
        template = template.replaceAll("\\$ResponseVarName", responseVarName);
        template = template.replaceAll("\\$templateId", templateId);

        if (query != null) {
            template = template.replace("?query", query);
        } else {
            template = template.replace("?query", "");
        }

        return template;
    }

    /**
     * Creates a RESTExtension GET to fetch properties of NodeInstance
     *
     * @param nodeInstanceIDUrl the name of the variable holding the address to the nodeInstance
     * @param responseVarName the name of the variable to store the response into
     * @return a Node containing a BPEL RESTExtension Activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTExtensionGETForNodeInstancePropertiesAsNode(final String nodeInstanceIDUrl,
                    final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.createRESTExtensionGETForNodeInstancePropertiesAsString(nodeInstanceIDUrl,
            responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Creates a RESTExtension GET to fetch properties of NodeInstance
     *
     * @param nodeInstanceIDUrl the name of the variable holding the address to the nodeInstance
     * @param responseVarName the name of the variable to store the response into
     * @return a String containing a BPEL RESTExtension Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTExtensionGETForNodeInstancePropertiesAsString(final String nodeInstanceIDUrl,
                    final String responseVarName)
        throws IOException {
        // <!-- $urlVarName, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightGET_NodeInstance_Properties.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelfragmentfile);
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
        final String filePath = "string(concat($input.payload//*[local-name()='csarEntrypoint']/text(),'/Content/"
            + artifactPath + "'))";
        return filePath;
    }

    /**
     * Generates an assign activity that fetches the value of the input message and writes it into a
     * string variable
     *
     * @param inputMessageElementLocalName the localName of the element inside the input message
     * @param stringVariableName the name of the variable to assign the value to
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateAssignFromInputMessageToStringVariable(final String inputMessageElementLocalName,
                    final String stringVariableName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("BpelAssignFromInputToStringVar.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        // <!-- $inputElementLocalName, $StringVariableName, $assignName -->
        bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
        bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
        bpelAssignString = bpelAssignString.replace("$assignName",
            "assignFromInputToString" + System.currentTimeMillis());
        return bpelAssignString;
    }

    /**
     * Generates an assign activity that fetches the value of the input message and writes it into a
     * string variable
     *
     * @param inputMessageElementLocalName the localName of the element inside the input message
     * @param stringVariableName the name of the variable to assign the value to
     * @return a Node containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateAssignFromInputMessageToStringVariableAsNode(final String inputMessageElementLocalName,
                    final String stringVariableName)
        throws IOException, SAXException {
        final String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName,
            stringVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateBPEL4RESTLightGETonURL(final String urlVarName, final String responseVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightGET_URL_ApplicationXML.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        // <!-- $ResponseVarName, $urlVar -->
        bpelAssignString = bpelAssignString.replace("$ResponseVarName", responseVarName);
        bpelAssignString = bpelAssignString.replace("$urlVar", urlVarName);
        return bpelAssignString;
    }

    public Node generateBPEL4RESTLightGETonURLAsNode(final String urlVarName, final String reponseVarName)
        throws IOException, SAXException {
        final String templateString = this.generateBPEL4RESTLightGETonURL(urlVarName, reponseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a
     * Service Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the
     *        instanceDataAPI
     * @param csarId the name of the csar the serviceTemplate belongs to
     * @param serviceTemplateId the id of the serviceTemplate
     * @param responseVariableName a name of an anyType variable to save the response into
     * @return a String containing a BPEL4RESTLight POST extension activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateBPEL4RESTLightServiceInstancePOST(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String responseVariableName)
        throws IOException {
        // tags in xml snippet: $InstanceDataURLVar, $CSARName,
        // $serviceTemplateId, $ResponseVarName
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

        bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
        bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
        bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

        return bpel4RestString;
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a
     * Service Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the
     *        instanceDataAPI
     * @param csarId the name of the csar the serviceTemplate belongs to
     * @param serviceTemplateId the id of the serviceTemplate
     * @param responseVariableName a name of an anyType variable to save the response into
     * @return a String containing a BPEL4RESTLight POST extension activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateBPEL4RESTLightServiceInstancePOST(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String requestVariableName,
                    final String responseVariableName)
        throws IOException {
        // tags in xml snippet: $InstanceDataURLVar, $CSARName,
        // $serviceTemplateId, $ResponseVarName
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI_WithBody.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

        bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
        bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
        bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
        bpel4RestString = bpel4RestString.replace("$RequestVarName", requestVariableName);
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

        return bpel4RestString;
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a
     * Service Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the
     *        instanceDataAPI
     * @param csarId the name of the csar the serviceTemplate belongs to
     * @param serviceTemplateId the id of the serviceTemplate
     * @param responseVariableName a name of an anyType variable to save the response into
     * @return a Node containing a BPEL4RESTLight POST extension activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String responseVariableName)
        throws IOException, SAXException {
        final String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName,
            csarId, serviceTemplateId, responseVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL POST at the given InstanceDataAPI with the given ServiceTemplate id to create a
     * Service Instance
     *
     * @param instanceDataAPIUrlVariableName the name of the variable holding the address to the
     *        instanceDataAPI
     * @param csarId the name of the csar the serviceTemplate belongs to
     * @param serviceTemplateId the id of the serviceTemplate
     * @param requestVariableName a name of an anyType variable to take the request content from
     * @param responseVariableName a name of an anyType variable to save the response into
     * @return a Node containing a BPEL4RESTLight POST extension activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String requestVariableName,
                    final String responseVariableName)
        throws IOException, SAXException {
        final String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName,
            csarId, serviceTemplateId, requestVariableName, responseVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at
     * runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a Node containing a BPEL If Activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node generateBPELIfTrueThrowFaultAsNode(final String xpath1Expr, final QName faultQName)
        throws IOException, SAXException {
        final String templateString = this.generateBPELIfTrueThrowFaultAsString(xpath1Expr, faultQName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL If activity that throws the given fault when the given expr evaluates to true at
     * runtime
     *
     * @param xpath1Expr a XPath 1.0 expression as String
     * @param faultQName a QName denoting the fault to be thrown when the if evaluates to true
     * @return a String containing a BPEL If Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String generateBPELIfTrueThrowFaultAsString(final String xpath1Expr, final QName faultQName)
        throws IOException {
        // <!-- $xpath1Expr, $faultPrefix, $faultNamespace, $faultLocalName-->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("BPELIfTrueThrowFault.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelIfString = FileUtils.readFileToString(bpel4RestFile);

        bpelIfString = bpelIfString.replace("$xpath1Expr", xpath1Expr);

        bpelIfString = bpelIfString.replace("$faultPrefix", faultQName.getLocalPart());
        bpelIfString = bpelIfString.replace("$faultLocalName", faultQName.getLocalPart());

        return bpelIfString;
    }

    /**
     * Generates an Assign Acitivity that writes the content of a Strig variable into the first element
     * specified by prefix and localname
     *
     * @param assignName the name of the assign
     * @param variableName the name of the string variable to take the value from
     * @param outputVarName the name of the output message variable
     * @param outputVarPartName the name of the part inside the message variable
     * @param outputVarPrefix the prefix of the element inside the message part
     * @param outputVarLocalName the localName of the element inside the message part
     * @return a DOM Node containing a BPEL Assign Activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateCopyFromStringVarToOutputVariableAsNode(final String variableName, final String outputVarName,
                    final String outputVarPartName, final String outputVarLocalName)
        throws IOException, SAXException {
        final String templateString = this.generateCopyFromStringVarToOutputVariableAsString(variableName,
            outputVarName, outputVarPartName, outputVarLocalName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates an Assign Activity that writes the content of a String variable into the first element
     * specified by prefix and localname
     *
     * @param assignName the name of the assign
     * @param variableName the name of the string variable to take the value from
     * @param outputVarName the name of the output message variable
     * @param outputVarPartName the name of the part inside the message variable
     * @param outputVarPrefix the prefix of the element inside the message part
     * @param outputVarLocalName the localname of the element inside the message part
     * @return a String containing a BPEL assign activitiy
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateCopyFromStringVarToOutputVariableAsString(final String variableName,
                    final String outputVarName, final String outputVarPartName, final String outputVarLocalName)
        throws IOException {
        // BpelAssignOutputVarFromStringVariable.xml
        // <!-- ${assignName}, ${variableName}, ${outputVarName},
        // ${outputVarPartName}, ${outputVarPrefix}, ${outputVarLocalName} -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getResource("BpelCopyOutputVarFromStringVariable.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        bpelAssignString = bpelAssignString.replace("${variableName}", variableName);
        bpelAssignString = bpelAssignString.replace("${outputVarName}", outputVarName);
        bpelAssignString = bpelAssignString.replace("${outputVarPartName}", outputVarPartName);
        bpelAssignString = bpelAssignString.replace("${outputVarLocalName}", outputVarLocalName);
        return bpelAssignString;
    }

    /**
     * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance POST response
     *
     * @param serviceInstanceResponseVarName the var name of the POST response
     * @param serviceInstanceURLVarName the var name to save the URL/ID into
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node generateServiceInstanceURLVarAssignAsNode(final String serviceInstanceResponseVarName,
                    final String serviceInstanceURLVarName)
        throws IOException, SAXException {
        final String templateString = this.generateServiceInstanceURLVarAssignAsString(serviceInstanceResponseVarName,
            serviceInstanceURLVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a BPEL assign that retrieves the URL/ID of a serviceInstance POST response
     *
     * @param serviceInstanceResponseVarName the var name of the POST response
     * @param serviceInstanceURLVarName the var name to save the URL/ID into
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String generateServiceInstanceURLVarAssignAsString(final String serviceInstanceResponseVarName,
                    final String serviceInstanceURLVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass())
                                     .getResource("BpelAssignServiceInstancePOSTResponse.xml");
        final File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);
        // <!-- $assignName $ServiceInstanceResponseVarName
        // $ServiceInstanceURLVarName-->

        bpelAssignString = bpelAssignString.replace("$assignName",
            "assignServiceInstance" + System.currentTimeMillis());
        bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
        bpelAssignString = bpelAssignString.replace("$ServiceInstanceURLVarName", serviceInstanceURLVarName);
        return bpelAssignString;
    }

}
