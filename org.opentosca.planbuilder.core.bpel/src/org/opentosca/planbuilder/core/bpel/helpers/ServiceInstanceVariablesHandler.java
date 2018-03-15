package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * Appends init code to the given BuildPlan to instantiate a serviceInstance at the responsible
 * OpenTOSCA Container
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ServiceInstanceVariablesHandler {

    private static final String ServiceInstanceURLVarKeyword = "OpenTOSCAContainerAPIServiceInstanceURL";
    private static final String ServiceInstanceIDVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
    private static final String ServiceTemplateURLVarKeyword = "OpenTOSCAContainerAPIServiceTemplateURL";
    private static final String ServiceInstancesURLVarKeyword = "OpenTOSCAContainerAPIServiceInstancesURL";
    private static final String PlanInstanceURLVarKeyword = "OpenTOSCAContainerAPIPlanInstanceURL";
    private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";

    private final BPELProcessFragments fragments;

    private final BPELPlanHandler bpelProcessHandler;

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    public ServiceInstanceVariablesHandler() throws ParserConfigurationException {
        this.bpelProcessHandler = new BPELPlanHandler();
        this.fragments = new BPELProcessFragments();
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    /**
     * Sets the main variables such as ServiceInstanceID, ServiceTemplateURL for management plans
     *
     * @param plan a plan
     */
    public void addManagementPlanServiceInstanceVarHandlingFromInput(final BPELPlan plan) {
        this.appendAssignFromInputToVariable(plan, ServiceInstanceVariablesHandler.InstanceDataAPIUrlKeyword);
        this.appendAssignFromInputToVariable(plan, ServiceInstanceVariablesHandler.ServiceInstanceURLVarKeyword);

        this.bpelProcessHandler.addGlobalStringVariable(ServiceTemplateURLVarKeyword, plan);
        this.addAssignServiceTemplateURLVariable(plan);

        this.bpelProcessHandler.addGlobalStringVariable(PlanInstanceURLVarKeyword, plan);
        this.addAssignManagementPlanInstanceUrlVariable(plan);
    }

    protected static String findServiceInstanceUrlVariableName(final BPELPlanHandler bpelProcessHandler,
                                                               final BPELPlan plan) {
        for (final String varName : bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstanceURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    protected static String findInstanceDataAPIUrlVariableName(final BPELPlanHandler bpelProcessHandler,
                                                               final BPELPlan plan) {
        for (final String varName : bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(InstanceDataAPIUrlKeyword)) {
                return varName;
            }
        }
        return null;
    }

    protected static String findServiceInstancesUrlVariableName(final BPELPlanHandler bpelProcessHandler,
                                                                final BPELPlan plan) {
        for (final String varName : bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstancesURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    protected static String findServiceTemplateUrlVariableName(final BPELPlanHandler bpelProcessHandler,
                                                               final BPELPlan plan) {
        for (final String varName : bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceTemplateURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    protected static String findPlanInstanceUrlVariableName(final BPELPlanHandler bpelProcessHandler,
                                                            final BPELPlan plan) {
        for (final String varName : bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(PlanInstanceURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    private void addAssignServiceTemplateURLVariable(final BPELPlan plan) {

        final String instanceDataAPIUrlVarName =
            ServiceInstanceVariablesHandler.findInstanceDataAPIUrlVariableName(this.bpelProcessHandler, plan);

        // create variable
        final String serviceTemplateUrlVariableName =
            ServiceInstanceVariablesHandler.findServiceTemplateUrlVariableName(this.bpelProcessHandler, plan);

        final String xpath2Query = "string(replace($" + instanceDataAPIUrlVarName + ", '/instances', ''))";
        try {
            Node assignFragment =
                this.fragments.createAssignXpathQueryToStringVarFragmentAsNode("assignServiceTemplateUrl"
                    + System.currentTimeMillis(), xpath2Query, serviceTemplateUrlVariableName);
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            this.appendToInitSequence(assignFragment, plan);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addAssignManagementPlanInstanceUrlVariable(final BPELPlan plan) {
        final String planInstanceUrlVarName =
            ServiceInstanceVariablesHandler.findPlanInstanceUrlVariableName(this.bpelProcessHandler, plan);
        final String serviceTemplateInstanceUrlVarName =
            ServiceInstanceVariablesHandler.findServiceInstanceUrlVariableName(this.bpelProcessHandler, plan);

        final String xpath2Query = "string(concat($" + serviceTemplateInstanceUrlVarName + ", '/managementplans/', '"
            + plan.getId().substring(plan.getId().lastIndexOf("}") + 1)
            + "', '/instances/', $input.payload/*[local-name()='CorrelationID']))";
        try {
            Node assignFragment = this.fragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPlanInstanceUrl"
                + System.currentTimeMillis(), xpath2Query, planInstanceUrlVarName);
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            this.appendToInitSequence(assignFragment, plan);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Adds BPEL activities to set the PlanInstanceURL Variable for BuildPlans
     *
     * @param plan the BPEL Plan to add the assign to
     */
    private void addAssignBuildPlanInstanceURLVariable(final BPELPlan plan) {

        final String planInstanceUrlVarName =
            ServiceInstanceVariablesHandler.findPlanInstanceUrlVariableName(this.bpelProcessHandler, plan);
        final String serviceTemplateUrlVarName =
            ServiceInstanceVariablesHandler.findServiceTemplateUrlVariableName(this.bpelProcessHandler, plan);

        final String xpath2Query = "string(concat($" + serviceTemplateUrlVarName + ", '/buildplans/', '"
            + plan.getId().substring(plan.getId().lastIndexOf("}") + 1)
            + "', '/instances/', $input.payload/*[local-name()='CorrelationID']))";
        try {
            Node assignFragment = this.fragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPlanInstanceUrl"
                + System.currentTimeMillis(), xpath2Query, planInstanceUrlVarName);
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            this.appendToInitSequence(assignFragment, plan);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Appends logic to handle instanceDataAPI interaction. Adds instanceDataAPI element into input
     * message. At runtime saves the input value into a global variable and creates a serviceInstance
     * for the plan.
     *
     * @param plan a plan
     */
    public void initializeInstanceDataFromInput(final BPELPlan plan) {
        final String instanceDataAPIVarName =
            this.appendAssignFromInputToVariable(plan, ServiceInstanceVariablesHandler.InstanceDataAPIUrlKeyword);
        this.appendServiceInstanceInitCode(plan, instanceDataAPIVarName);
        this.addAssignOutputWithServiceInstanceId(plan);
    }

    private void addAssignOutputWithServiceInstanceId(final BPELPlan plan) {
        this.bpelProcessHandler.addStringElementToPlanResponse("instanceId", plan);

        String serviceInstanceVarName = null;
        for (final String varName : this.bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstanceVariablesHandler.ServiceInstanceURLVarKeyword)) {
                serviceInstanceVarName = varName;
                break;
            }
        }

        try {
            Node copyNode =
                this.fragments.generateCopyFromStringVarToOutputVariableAsNode(serviceInstanceVarName, "output",
                                                                               "payload", "instanceId");
            copyNode = plan.getBpelDocument().importNode(copyNode, true);
            plan.getBpelMainSequenceOutputAssignElement().appendChild(copyNode);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }

    }

    public static String getServiceTemplateURLVariableName(final Collection<String> varNames) {
        for (final String varName : varNames) {
            if (varName.contains(ServiceInstanceVariablesHandler.ServiceTemplateURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    public String getServiceInstanceURLVariableName(final Collection<String> names) {
        for (final String varName : names) {
            if (varName.contains(ServiceInstanceVariablesHandler.ServiceInstanceURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    public String getServiceInstanceVariableName(final BPELPlan plan) {
        return this.getServiceInstanceURLVariableName(this.bpelProcessHandler.getMainVariableNames(plan));
    }

    public boolean appendSetServiceInstanceState(final BPELPlan plan, final Element insertBeforeElement,
                                                 final String state) {

        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        // generate any type variable for REST call response
        final String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        final QName rescalResponseVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
                                                 rescalResponseVarDeclId, plan)) {
            return false;
        }

        final String restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
        final QName rescalRequestVarDeclId = new QName(xsdNamespace, "string", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.TYPE,
                                                 rescalRequestVarDeclId, plan)) {
            return false;
        }

        final String assignName = "assignServiceInstanceState" + System.currentTimeMillis();

        try {
            Node assignRequestWithStateNode =
                this.fragments.createAssignXpathQueryToStringVarFragmentAsNode(assignName, "string('" + state + "')",
                                                                               restCallRequestVarName);
            assignRequestWithStateNode = plan.getBpelDocument().importNode(assignRequestWithStateNode, true);
            insertBeforeElement.getParentNode().insertBefore(assignRequestWithStateNode, insertBeforeElement);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String serviceInstanceVarName = "";

        for (final String varName : this.bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstanceURLVarKeyword)) {
                serviceInstanceVarName = varName;
            }
        }

        if (serviceInstanceVarName.isEmpty()) {
            return false;
        }

        try {
            Node setInstanceStateRequestNode =
                this.fragments.createBPEL4RESTLightPutStateAsNode(serviceInstanceVarName, restCallRequestVarName);
            setInstanceStateRequestNode = plan.getBpelDocument().importNode(setInstanceStateRequestNode, true);
            insertBeforeElement.getParentNode().insertBefore(setInstanceStateRequestNode, insertBeforeElement);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public boolean appendServiceInstanceDelete(final BPELPlan plan) {

        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        // generate any type variable for REST call response
        final String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        final QName rescalResponseVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
                                                 rescalResponseVarDeclId, plan)) {
            return false;
        }

        try {
            Node RESTDeleteNode =
                this.fragments.createRESTDeleteOnURLBPELVarAsNode(ServiceInstanceVariablesHandler.ServiceInstanceURLVarKeyword,
                                                                  restCallResponseVarName);
            RESTDeleteNode = plan.getBpelDocument().importNode(RESTDeleteNode, true);
            plan.getBpelMainSequenceOutputAssignElement().getParentNode()
                .insertBefore(RESTDeleteNode, plan.getBpelMainSequenceOutputAssignElement());
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public boolean initPropertyVariablesFromInstanceData(final BPELPlan plan, final PropertyMap propMap) {

        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        // generate any type variable for REST call response
        final String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        final QName rescalResponseVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
                                                 rescalResponseVarDeclId, plan)) {
            return false;
        }

        // create temp nodeInstanceIDLink var
        final String tempNodeInstanceIDVarName = "tempNodeInstanceID" + System.currentTimeMillis();
        final QName tempNodeInstanceIDVarDeclId = new QName(xsdNamespace, "string", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(tempNodeInstanceIDVarName, BPELPlan.VariableType.TYPE,
                                                 tempNodeInstanceIDVarDeclId, plan)) {
            return false;
        }

        // create temp anyType element for properties
        final String tempNodeInstancePropertiesVarName = "tempNodeInstanceProperties" + System.currentTimeMillis();
        final QName tempNodeInstancePropertiesVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(tempNodeInstancePropertiesVarName, BPELPlan.VariableType.TYPE,
                                                 tempNodeInstancePropertiesVarDeclId, plan)) {
            return false;
        }

        final String serviceTemplateUrlVarName =
            ServiceInstanceVariablesHandler.findServiceTemplateUrlVariableName(this.bpelProcessHandler, plan);

        for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {

            if (templatePlan.getNodeTemplate() == null) {
                continue;
            }

            if (templatePlan.getNodeTemplate().getProperties() == null) {
                continue;
            }

            // find nodeInstance with query at instanceDataAPI
            try {
                Node nodeInstanceGETNode =
                    this.fragments.createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
                                                                                   restCallResponseVarName,
                                                                                   templatePlan.getNodeTemplate()
                                                                                               .getId(),
                                                                                   null);
                nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstanceGETNode,
                                                                           plan.getBpelMainFlowElement());
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }

            // fetch nodeInstanceID from nodeInstance query
            try {
                Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                    this.fragments.createAssignSelectFirstReferenceAndAssignToStringVarAsNode(restCallResponseVarName,
                                                                                              tempNodeInstanceIDVarName);
                assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                    templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse,
                                                              true);
                plan.getBpelMainFlowElement().getParentNode()
                    .insertBefore(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, plan.getBpelMainFlowElement());
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }

            // fetch properties into temp anyType var
            try {
                Node nodeInstancePropertiesGETNode =
                    this.fragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(tempNodeInstanceIDVarName,
                                                                                         restCallResponseVarName);
                nodeInstancePropertiesGETNode =
                    templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstancePropertiesGETNode,
                                                                           plan.getBpelMainFlowElement());

            }
            catch (final IOException e) {
                e.printStackTrace();
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }

            // assign bpel variables from the requested properties
            // create mapping from property dom nodes to bpelvariable
            final Map<Element, String> element2BpelVarNameMap = new HashMap<>();
            final NodeList propChildNodes =
                templatePlan.getNodeTemplate().getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < propChildNodes.getLength(); index++) {
                if (propChildNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
                    final Element childElement = (Element) propChildNodes.item(index);
                    // find bpelVariable
                    final String bpelVarName = propMap.getPropertyMappingMap(templatePlan.getNodeTemplate().getId())
                                                      .get(childElement.getLocalName());
                    if (bpelVarName != null) {
                        element2BpelVarNameMap.put(childElement, bpelVarName);
                    }
                }
            }

            try {
                Node assignPropertiesToVariables =
                    this.fragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                        + System.currentTimeMillis(), restCallResponseVarName, element2BpelVarNameMap);
                assignPropertiesToVariables =
                    templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(assignPropertiesToVariables,
                                                                           plan.getBpelMainFlowElement());
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private String appendServiceInstanceInitCode(final BPELPlan buildPlan, final String instanceDataAPIUrlVarName) {
        // here we'll add code to:
        // instantiate a full instance of the serviceTemplate at the container
        // instancedata api

        // get csar and serviceTemplate
        final String csarId = buildPlan.getCsarName();
        final QName serviceTemplateId = buildPlan.getServiceTemplate().getQName();

        // generate any type variable for REST call response
        final QName responseVariableQName =
            new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd" + System.currentTimeMillis());

        try {
            final File schemaFile = this.fragments.getOpenTOSCAAPISchemaFile();
            final QName correlationIdElementSchemaQname = this.fragments.getOpenToscaApiCorrelationElementQname();
            this.bpelProcessHandler.addImportedFile(schemaFile, buildPlan);
            this.bpelProcessHandler.addImportToBpel(correlationIdElementSchemaQname.getNamespaceURI(),
                                                    schemaFile.getAbsolutePath(), "http://www.w3.org/2001/XMLSchema",
                                                    buildPlan);

        }
        catch (final IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // and string for request
        QName requestVariableQName =
            new QName(this.fragments.getOpenToscaApiCorrelationElementQname().getNamespaceURI(),
                this.fragments.getOpenToscaApiCorrelationElementQname().getLocalPart(), "opentoscaAPI");

        requestVariableQName = this.bpelProcessHandler.importNamespace(requestVariableQName, buildPlan);

        this.bpelProcessHandler.addNamespaceToBPELDoc(responseVariableQName.getPrefix(),
                                                      responseVariableQName.getNamespaceURI(), buildPlan);

        this.bpelProcessHandler.addNamespaceToBPELDoc(requestVariableQName.getPrefix(),
                                                      requestVariableQName.getNamespaceURI(), buildPlan);

        final String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
                                                 responseVariableQName, buildPlan)) {
            return null;
        }

        final String restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.ELEMENT,
                                                 requestVariableQName, buildPlan)) {
            return null;
        }

        try {
            Node assignRestRequestNode =
                this.fragments.generateServiceInstanceRequestMessageAssignAsNode("CorrelationID",
                                                                                 restCallRequestVarName);
            assignRestRequestNode = buildPlan.getBpelDocument().importNode(assignRestRequestNode, true);
            this.appendToInitSequence(assignRestRequestNode, buildPlan);
        }
        catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            Node serviceInstancePOSTNode =
                this.fragments.generateBPEL4RESTLightServiceInstancePOSTAsNode(instanceDataAPIUrlVarName,
                                                                               restCallRequestVarName,
                                                                               restCallResponseVarName);
            serviceInstancePOSTNode = buildPlan.getBpelDocument().importNode(serviceInstancePOSTNode, true);
            this.appendToInitSequence(serviceInstancePOSTNode, buildPlan);
        }
        catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (final SAXException e) {
            e.printStackTrace();
            return null;
        }

        // assign the serviceInstance REST POST Response into global service
        // instance variable
        final QName serviceInstanceUrlDeclId =
            new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd" + System.currentTimeMillis());
        this.bpelProcessHandler.addNamespaceToBPELDoc(serviceInstanceUrlDeclId.getPrefix(),
                                                      serviceInstanceUrlDeclId.getNamespaceURI(), buildPlan);

        final String serviceInstanceUrlVarName =
            ServiceInstanceVariablesHandler.ServiceInstanceURLVarKeyword + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(serviceInstanceUrlVarName, BPELPlan.VariableType.TYPE,
                                                 serviceInstanceUrlDeclId, buildPlan)) {
            return null;
        }

        final String serviceInstanceIdVarName =
            ServiceInstanceVariablesHandler.ServiceInstanceIDVarKeyword + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(serviceInstanceIdVarName, BPELPlan.VariableType.TYPE,
                                                 serviceInstanceUrlDeclId, buildPlan)) {
            return null;
        }

        final String serviceTemplateUrlVarName =
            ServiceInstanceVariablesHandler.ServiceTemplateURLVarKeyword + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(serviceTemplateUrlVarName, BPELPlan.VariableType.TYPE,
                                                 serviceInstanceUrlDeclId, buildPlan)) {
            return null;
        }

        final String buildPlanUrlVarName =
            ServiceInstanceVariablesHandler.PlanInstanceURLVarKeyword + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(buildPlanUrlVarName, BPELPlan.VariableType.TYPE,
                                                 serviceInstanceUrlDeclId, buildPlan)) {
            return null;
        }

        final String planName = buildPlan.getId().substring(buildPlan.getId().lastIndexOf("}") + 1);
        try {
            this.bpelProcessHandler.addVariable("ServiceInstanceCorrelationID", BPELPlan.VariableType.TYPE,
                                                serviceInstanceUrlDeclId, buildPlan);
            Node assignCorr =
                this.fragments.generateAssignFromInputMessageToStringVariableAsNode("CorrelationID",
                                                                                    "ServiceInstanceCorrelationID");
            assignCorr = buildPlan.getBpelDocument().importNode(assignCorr, true);
            this.appendToInitSequence(assignCorr, buildPlan);
        }
        catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Node serviceInstanceURLAssignNode =
                this.fragments.generateServiceInstanceDataVarsAssignAsNode(restCallResponseVarName,
                                                                           serviceInstanceUrlVarName,
                                                                           instanceDataAPIUrlVarName,
                                                                           serviceInstanceIdVarName,
                                                                           serviceTemplateUrlVarName, planName,
                                                                           buildPlanUrlVarName);
            serviceInstanceURLAssignNode = buildPlan.getBpelDocument().importNode(serviceInstanceURLAssignNode, true);
            this.appendToInitSequence(serviceInstanceURLAssignNode, buildPlan);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        // add logic to update the variables at runtime before one of the
        // templates is going to be handled

        return serviceInstanceUrlVarName;
    }

    /**
     * Adds an element with the given varName to the input message of the given plan and adds logic
     * assign the input value to an internal variable with the given varName.
     *
     * @param plan a plan to add the logic to
     * @param varName a name to use inside the input message and as name for the global string variable
     *        where the value will be added to.
     * @return a String containing the generated Variable Name of the Variable holding the value from
     *         the input at runtime
     */
    private String appendAssignFromInputToVariable(final BPELPlan plan, final String varName) {
        // add instancedata api url element to plan input message
        this.bpelProcessHandler.addStringElementToPlanRequest(varName, plan);

        // generate single string variable for InstanceDataAPI HTTP calls, as
        // REST BPEL PLugin
        // can only handle simple xsd types (no queries from input message)

        final QName instanceDataAPIUrlDeclId =
            new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd" + System.currentTimeMillis());
        this.bpelProcessHandler.addNamespaceToBPELDoc(instanceDataAPIUrlDeclId.getPrefix(),
                                                      instanceDataAPIUrlDeclId.getNamespaceURI(), plan);

        if (!this.bpelProcessHandler.addVariable(varName, BPELPlan.VariableType.TYPE, instanceDataAPIUrlDeclId, plan)) {
            return null;
        }

        try {
            Node assignNode = this.fragments.generateAssignFromInputMessageToStringVariableAsNode(varName, varName);

            assignNode = plan.getBpelDocument().importNode(assignNode, true);
            this.appendToInitSequence(assignNode, plan);
        }
        catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (final SAXException e) {
            e.printStackTrace();
            return null;
        }
        return varName;
    }

    /**
     * Appends the given node the the main sequence of the buildPlan this context belongs to
     *
     * @param node a XML DOM Node
     * @return true if adding the node to the main sequence was successfull
     */
    private boolean appendToInitSequence(final Node node, final BPELPlan buildPlan) {

        final Element flowElement = buildPlan.getBpelMainFlowElement();

        final Node mainSequenceNode = flowElement.getParentNode();

        mainSequenceNode.insertBefore(node, flowElement);

        return true;
    }

    public void addCorrellationID(final BPELPlan buildPlan) {
        // set a correlation id which will can be set in the input and will be
        // send back with the response
        this.bpelProcessHandler.addStringElementToPlanRequest("CorrelationID", buildPlan);
        this.bpelProcessHandler.addStringElementToPlanResponse("CorrelationID", buildPlan);

        // add an assign
        try {
            Node assignNode = this.createAssignFromInputToOutputAsNode(buildPlan.getWsdl().getTargetNamespace());
            assignNode = buildPlan.getBpelDocument().importNode(assignNode, true);
            final Element flowElement = buildPlan.getBpelMainFlowElement();

            final Node mainSequenceNode = flowElement.getParentNode();

            mainSequenceNode.insertBefore(assignNode, flowElement);
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createAssignFromInputToOutput(final String targetNamespace) {
        final String bpelAssign =
            "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignCorrelationID\"><bpel:copy><bpel:from variable=\"input\" part=\"payload\"><bpel:query xmlns:tns=\""
                + targetNamespace
                + "\" queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[tns:CorrelationID]]></bpel:query></bpel:from><bpel:to variable=\"output\" part=\"payload\"><bpel:query xmlns:tns=\""
                + targetNamespace
                + "\" queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[tns:CorrelationID]]></bpel:query></bpel:to></bpel:copy></bpel:assign>";
        return bpelAssign;
    }

    public Node createAssignFromInputToOutputAsNode(final String targetNamespace) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(this.createAssignFromInputToOutput(targetNamespace)));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

}
