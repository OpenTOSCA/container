package org.opentosca.planbuilder.core.bpel.handlers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Appends init code to the given BuildPlan to instantiate a serviceInstance at the responsible OpenTOSCA Container
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class SimplePlanBuilderServiceInstanceHandler extends AbstractServiceInstanceHandler {

    public SimplePlanBuilderServiceInstanceHandler() throws ParserConfigurationException {
        super();
    }

    public static String getServiceTemplateURLVariableName(final Collection<String> varNames) {
        for (final String varName : varNames) {
            if (varName.contains(SimplePlanBuilderServiceInstanceHandler.ServiceTemplateURLVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    public String findServiceInstanceIdVarName(final BPELPlan plan) {
        return getLowestId(findServiceInstanceIdVarNames(this.bpelProcessHandler, plan), ServiceInstanceIDVarKeyword);
    }

    public String findServiceInstanceUrlVariableName(final BPELPlan plan) {
        return getLowestId(findServiceInstanceURLVarName(this.bpelProcessHandler, plan), ServiceInstanceURLVarKeyword);
    }

    public String findServiceTemplateUrlVariableName(final BPELPlan plan) {
        return getLowestId(findServiceTemplateURLVarName(this.bpelProcessHandler, plan), ServiceTemplateURLVarKeyword);
    }

    public String findPlanInstanceUrlVariableName(final BPELPlan plan) {
        return getLowestId(findPlanInstanceURLVarName(this.bpelProcessHandler, plan), PlanInstanceURLVarKeyword);
    }

    public String getServiceTemplateURLVariableName(final BPELPlan plan) {
        return getLowestId(findServiceTemplateURLVarName(this.bpelProcessHandler, plan), ServiceTemplateURLVarKeyword);
    }

    public void addServiceInstanceHandlingFromInput(final BPELPlan plan, String serviceInstancesUrlVarName,
                                                    String serviceInstanceURLVarName, String serviceTemplateUrlVarName,
                                                    String serviceInstanceIdVarName, String planInstanceURLVarName) {
        // add instancedata api url element to plan input message
        this.bpelProcessHandler.addStringElementToPlanRequest(InstanceDataAPIUrlKeyword, plan);
        appendAssignFromInputToVariable(plan, InstanceDataAPIUrlKeyword, serviceInstancesUrlVarName);

        this.bpelProcessHandler.addStringElementToPlanRequest(ServiceInstanceURLVarKeyword, plan);
        serviceInstanceURLVarName =
            appendAssignFromInputToVariable(plan, ServiceInstanceURLVarKeyword, serviceInstanceURLVarName);

        addAssignServiceTemplateURLVariable(plan, serviceInstancesUrlVarName, serviceTemplateUrlVarName);

        addAssignServiceInstanceIdVarFromServiceInstanceURLVar(plan, serviceInstanceURLVarName,
            serviceInstanceIdVarName);

        addAssignManagementPlanInstanceUrlVariable(plan, planInstanceURLVarName, serviceInstanceURLVarName);
    }

    /**
     * Sets the main variables such as ServiceInstanceID, ServiceTemplateURL for management plans and add code to
     * initialize the variables based on the input instanceDataAPI and serviceInstanceURL
     *
     * @param plan a plan
     */
    public void addServiceInstanceHandlingFromInput(final BPELPlan plan) {
        // add instancedata api url element to plan input message
        this.bpelProcessHandler.addStringElementToPlanRequest(InstanceDataAPIUrlKeyword, plan);
        String instanceDataAPIUrlVarName = this.addInstanceDataAPIURLVariable(plan);
        appendAssignFromInputToVariable(plan, InstanceDataAPIUrlKeyword, instanceDataAPIUrlVarName);

        this.bpelProcessHandler.addStringElementToPlanRequest(ServiceInstanceURLVarKeyword, plan);
        String serviceInstanceURLVarName = this.addServiceInstanceURLVariable(plan);
        serviceInstanceURLVarName =
            appendAssignFromInputToVariable(plan, ServiceInstanceURLVarKeyword, serviceInstanceURLVarName);

        String serviceTemplateUrlVarName = this.addServiceTemplateURLVariable(plan);
        addAssignServiceTemplateURLVariable(plan, instanceDataAPIUrlVarName, serviceTemplateUrlVarName);

        String serviceInstanceIdVarName = this.addServiceInstanceIDVariable(plan);
        addAssignServiceInstanceIdVarFromServiceInstanceURLVar(plan, serviceInstanceURLVarName,
            serviceInstanceIdVarName);

        String planInstanceURLVarName = this.addPlanInstanceURLVariable(plan);
        addAssignManagementPlanInstanceUrlVariable(plan, planInstanceURLVarName, serviceInstanceURLVarName);
    }

    /**
     * Adds code to initialize serviceInstance at the given instance data API and sets the given service instance
     * variables with the created serviceInstance
     *
     * @param plan                        the plan to add the code to
     * @param instanceDataAPIVariableName the variable to hold the instance data api url
     * @param serviceInstanceUrlVarName   the variable to hold the serviceInstanceUrl
     * @param serviceInstanceIdVarName    the variable to hold the serviceInstanceId
     * @param serviceTemplateUrlVarName   the variable to hold the serviceTemplateUrl
     * @param planInstanceUrlVarName      the variable to hold the planInstance
     */
    public void appendCreateServiceInstance(final BPELPlan plan, String instanceDataAPIVariableName,
                                            final String serviceInstanceUrlVarName,
                                            final String serviceInstanceIdVarName,
                                            final String serviceTemplateUrlVarName, final String planInstanceUrlVarName,
                                            boolean isManagementPlan) {

        appendServiceInstanceInitCode(plan, instanceDataAPIVariableName, serviceInstanceUrlVarName,
            serviceInstanceIdVarName, serviceTemplateUrlVarName, planInstanceUrlVarName,
            isManagementPlan);
    }

    public void appendAssignServiceInstanceIdToOutput(BPELPlan plan, String serviceInstanceIdVarName) {
        addAssignOutputWithServiceInstanceId(plan, serviceInstanceIdVarName);
    }

    /**
     * Appends logic to handle instanceDataAPI interaction. Adds instanceDataAPI element into input message. At runtime
     * saves the input value into a global variable and creates a serviceInstance for the plan.
     *
     * @param plan a plan
     */
    public void appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(final BPELPlan plan) {

        String instanceDataAPIVariableName = this.addInstanceDataAPIURLVariable(plan);

        this.bpelProcessHandler.addStringElementToPlanRequest(InstanceDataAPIUrlKeyword, plan);
        final String instanceDataAPIVarName =
            appendAssignFromInputToVariable(plan, InstanceDataAPIUrlKeyword, instanceDataAPIVariableName);

        final String serviceInstanceUrlVarName = this.addServiceInstanceURLVariable(plan);
        final String serviceInstanceIdVarName = this.addServiceInstanceIDVariable(plan);
        final String serviceTemplateUrlVarName = this.addServiceTemplateURLVariable(plan);
        final String planInstanceUrlVarName = this.addPlanInstanceURLVariable(plan);
        boolean isManagementPlan = plan.getType().equals(PlanType.MANAGEMENT);

        appendServiceInstanceInitCode(plan, instanceDataAPIVarName, serviceInstanceUrlVarName, serviceInstanceIdVarName,
            serviceTemplateUrlVarName, planInstanceUrlVarName, isManagementPlan);
        String serviceInstanceVarName =
            getLowestId(findServiceInstanceIdVarNames(bpelProcessHandler, plan), ServiceInstanceURLVarKeyword);
        addAssignOutputWithServiceInstanceId(plan, serviceInstanceVarName);
    }

    public boolean appendSetServiceInstanceStateAsChild(final BPELPlan plan, final Element insertAsChild,
                                                        final String state, String urlVarName) {
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        // generate any type variable for REST call response
        final String restCallResponseVarName = "bpel4restlightVarResponse_setServiceInstanceState_" + state + "_" + System.currentTimeMillis();
        final QName rescalResponseVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
            rescalResponseVarDeclId, plan)) {
            return false;
        }

        final String restCallRequestVarName = "bpel4restlightVarRequest_setServiceInstanceState_" + state + "_" + System.currentTimeMillis();
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
            insertAsChild.appendChild(assignRequestWithStateNode);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        // URL Varname
        if (urlVarName == null) {
            throw new ServiceInsanceHandlingException("ServiceInstanceURLVar is null in plan: " + plan.getId());
        }

        if (urlVarName.isEmpty()) {
            throw new ServiceInsanceHandlingException("ServiceInstanceURLVar is empty in plan: " + plan.getId());
        }

        try {
            Node setInstanceStateRequestNode =
                this.fragments.createBPEL4RESTLightPutStateAsNode(urlVarName, restCallRequestVarName);
            setInstanceStateRequestNode = plan.getBpelDocument().importNode(setInstanceStateRequestNode, true);
            insertAsChild.appendChild(setInstanceStateRequestNode);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        return true;
    }

    public boolean appendSetServiceInstanceState(final BPELPlan plan, final Element insertBeforeElement,
                                                 final String state, String serviceInstanceURLVarName) {

        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        // generate any type variable for REST call response

        String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        final QName rescalResponseVarDeclId = new QName(xsdNamespace, "anyType", xsdPrefix);

        boolean addedRestReponseVar = this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
            rescalResponseVarDeclId, plan);
        while (!addedRestReponseVar) {
            restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
            addedRestReponseVar = this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
                rescalResponseVarDeclId, plan);
        }

        String restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
        final QName rescalRequestVarDeclId = new QName(xsdNamespace, "string", xsdPrefix);

        boolean addedRestRequestVar = this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.TYPE,
            rescalRequestVarDeclId, plan);
        while (!addedRestRequestVar) {
            restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
            addedRestRequestVar = this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.TYPE,
                rescalRequestVarDeclId, plan);
        }

        try {
            Node waitNode = this.fragments.createWait("'PT1S'");
            waitNode = plan.getBpelDocument().importNode(waitNode, true);
            insertBeforeElement.getParentNode().insertBefore(waitNode, insertBeforeElement);
        } catch (SAXException | IOException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        final String assignName = "assignServiceInstanceState" + System.currentTimeMillis();

        try {
            Node assignRequestWithStateNode =
                this.fragments.createAssignXpathQueryToStringVarFragmentAsNode(assignName, "string('" + state + "')",
                    restCallRequestVarName);
            assignRequestWithStateNode = plan.getBpelDocument().importNode(assignRequestWithStateNode, true);
            insertBeforeElement.getParentNode().insertBefore(assignRequestWithStateNode, insertBeforeElement);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        if (serviceInstanceURLVarName == null) {
            throw new ServiceInsanceHandlingException("ServiceInstanceURLVar is null in plan: " + plan.getId());
        }

        if (serviceInstanceURLVarName.isEmpty()) {
            throw new ServiceInsanceHandlingException("ServiceInstanceURLVar is empty in plan: " + plan.getId());
        }

        try {
            Node setInstanceStateRequestNode =
                this.fragments.createBPEL4RESTLightPutStateAsNode(serviceInstanceURLVarName, restCallRequestVarName);
            setInstanceStateRequestNode = plan.getBpelDocument().importNode(setInstanceStateRequestNode, true);
            insertBeforeElement.getParentNode().insertBefore(setInstanceStateRequestNode, insertBeforeElement);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        return true;
    }

    public boolean appendInitPropertyVariablesFromServiceInstanceData(final BPELPlan plan,
                                                                      Property2VariableMapping propMap,
                                                                      String serviceTemplateUrlVarName,
                                                                      Collection<BPELScope> scopes,
                                                                      TServiceTemplate serviceTemplate, String query) {
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

        final String tempNodeInstanceUrlVarName = "tempNodeInstanceUrl" + System.currentTimeMillis();

        if (!this.bpelProcessHandler.addVariable(tempNodeInstanceUrlVarName, BPELPlan.VariableType.TYPE,
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

        if (serviceTemplateUrlVarName == null) {
            throw new ServiceInsanceHandlingException("ServiceTemplateURLVarName is null in plan: " + plan.getId());
        }

        for (final BPELScope templatePlan : scopes) {

            if (templatePlan.getNodeTemplate() == null) {
                continue;
            }

            if (templatePlan.getNodeTemplate().getProperties() == null) {
                continue;
            }

            // find nodeInstance with query at serviceTemplateUrl
            try {
                Node nodeInstanceGETNode =
                    this.fragments.createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
                        restCallResponseVarName,
                        templatePlan.getNodeTemplate()
                            .getId(),
                        query);
                nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstanceGETNode,
                    plan.getBpelMainFlowElement());
            } catch (final SAXException | IOException e) {
                throw new ServiceInsanceHandlingException(e);
            }

            // fetch nodeInstanceID from nodeInstance query
            try {
                Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                    this.fragments.createAssignSelectFirstNodeInstanceAndAssignToStringVarAsNode(restCallResponseVarName, tempNodeInstanceUrlVarName,
                        tempNodeInstanceIDVarName);
                assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                    templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse,
                        true);
                plan.getBpelMainFlowElement().getParentNode()
                    .insertBefore(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, plan.getBpelMainFlowElement());
            } catch (final SAXException | IOException e) {
                throw new ServiceInsanceHandlingException(e);
            }

            // fetch properties into temp anyType var
            try {
                Node nodeInstancePropertiesGETNode =
                    this.fragments.createRESTExtensionGETForInstancePropertiesAsNode(tempNodeInstanceUrlVarName,
                        restCallResponseVarName);
                nodeInstancePropertiesGETNode =
                    templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstancePropertiesGETNode,
                    plan.getBpelMainFlowElement());
            } catch (final IOException | SAXException e) {
                throw new ServiceInsanceHandlingException(e);
            }

            // assign bpel variables from the requested properties
            // create mapping from property dom nodes to bpelvariable

            final Map<String, String> propName2BpelVarNameMap = new HashMap<>();

            Map<String, String> propertiesMap = ModelUtils.asMap(templatePlan.getNodeTemplate().getProperties());

            for (PropertyVariable var : propMap.getNodePropertyVariables(serviceTemplate,
                templatePlan.getNodeTemplate())) {
                if (propertiesMap.containsKey(var.getPropertyName())) {
                    propName2BpelVarNameMap.put(var.getPropertyName(), var.getVariableName());
                }
            }

            try {
                Node assignPropertiesToVariables =
                    this.fragments.createAssignFromInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                        + System.currentTimeMillis(), restCallResponseVarName, propName2BpelVarNameMap, ModelUtils.getNamespace(templatePlan.getNodeTemplate().getProperties()));
                assignPropertiesToVariables =
                    templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(assignPropertiesToVariables,
                    plan.getBpelMainFlowElement());
            } catch (final IOException | SAXException e) {
                throw new ServiceInsanceHandlingException(e);
            }
        }
        return true;
    }

    public boolean appendInitPropertyVariablesFromServiceInstanceData(final BPELPlan plan,
                                                                      final Property2VariableMapping propMap,
                                                                      String serviceTemplateUrlVarName,
                                                                      TServiceTemplate serviceTemplate, String query) {
        return this.appendInitPropertyVariablesFromServiceInstanceData(plan, propMap, serviceTemplateUrlVarName,
            plan.getTemplateBuildPlans(), serviceTemplate, query);
    }

    private void addAssignServiceTemplateURLVariable(final BPELPlan plan, String serviceInstancesUrlVarName,
                                                     String serviceTemplateUrlVariableName) {

        // create variable
        if (serviceTemplateUrlVariableName == null) {
            throw new ServiceInsanceHandlingException("ServiceTempalteURLVarName is null " + plan.getId());
        }

        final String xpath2Query = "string(replace($" + serviceInstancesUrlVarName + ", '/instances', ''))";
        try {
            Node assignFragment =
                this.fragments.createAssignXpathQueryToStringVarFragmentAsNode("assignServiceTemplateUrl"
                    + System.currentTimeMillis(), xpath2Query, serviceTemplateUrlVariableName);
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            appendToInitSequence(assignFragment, plan);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }
    }

    private void addAssignManagementPlanInstanceUrlVariable(final BPELPlan plan, String planInstanceUrlVarName,
                                                            String serviceTemplateInstanceUrlVarName) {
        if (planInstanceUrlVarName == null) {
            throw new ServiceInsanceHandlingException("PlanInstanceURLVarName is null in plan " + plan.getId());
        }
        if (serviceTemplateInstanceUrlVarName == null) {
            throw new ServiceInsanceHandlingException("serviceTemplateInstanceUrlVarName is null in plan " + plan.getId());
        }

        final String xpath2Query = "string(concat($" + serviceTemplateInstanceUrlVarName + ", '/managementplans/', '"
            + plan.getId().substring(plan.getId().lastIndexOf("}") + 1)
            + "', '/instances/', $input.payload/*[local-name()='CorrelationID']))";
        try {
            Node assignFragment = this.fragments.createAssignXpathQueryToStringVarFragmentAsNode("assignPlanInstanceUrl"
                + System.currentTimeMillis(), xpath2Query, planInstanceUrlVarName);
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            appendToInitSequence(assignFragment, plan);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }
    }

    private void addAssignOutputWithServiceInstanceId(final BPELPlan plan, String serviceInstanceVarName) {
        this.bpelProcessHandler.addStringElementToPlanResponse("instanceId", plan);

        if (serviceInstanceVarName == null) {
            throw new ServiceInsanceHandlingException("serviceInstanceVarName variable is null for plan " + plan.getId());
        }

        try {
            Node copyNode =
                this.fragments.generateCopyFromStringVarToOutputVariableAsNode(serviceInstanceVarName, "output",
                    "payload", "instanceId");
            copyNode = plan.getBpelDocument().importNode(copyNode, true);
            plan.getBpelMainSequenceOutputAssignElement().appendChild(copyNode);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }
    }

    /**
     * Generates code to create service instance at the instance data api based on the given plan with the referenced
     * service instance variables
     *
     * @param plan                      the plan to add the code to
     * @param instanceDataAPIUrlVarName the variable storing the url to the instance data api
     * @param serviceInstanceUrlVarName the variable for storing the serviceInstanceUrl
     * @param serviceInstanceIdVarName  the variable for storing the serviceInstanceId
     * @param serviceTemplateUrlVarName the variable for storing the serviceTemplateUrl
     * @param planInstanceUrlVarName    the variable for storing the planinstanceUrl
     */
    private void appendServiceInstanceInitCode(final BPELPlan plan, final String instanceDataAPIUrlVarName,
                                               String serviceInstanceUrlVarName, String serviceInstanceIdVarName,
                                               String serviceTemplateUrlVarName, String planInstanceUrlVarName,
                                               boolean isManagementPlan) {
        // here we'll add code to:
        // instantiate a full instance of the serviceTemplate at the container
        // instancedata api

        // generate any type variable for REST call response
        final QName responseVariableQName =
            new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xsd" + System.currentTimeMillis());

        try {
            final Path schemaFile = this.fragments.getOpenTOSCAAPISchemaFile();
            final QName correlationIdElementSchemaQname = this.fragments.getOpenToscaApiCorrelationElementQname();
            this.bpelProcessHandler.addImportedFile(schemaFile, plan);
            this.bpelProcessHandler.addImportToBpel(correlationIdElementSchemaQname.getNamespaceURI(),
                schemaFile.toAbsolutePath().toString(), "http://www.w3.org/2001/XMLSchema",
                plan);
        } catch (final IOException e2) {
            throw new ServiceInsanceHandlingException(e2);
        }

        // and string for request
        QName requestVariableQName =
            new QName(this.fragments.getOpenToscaApiCorrelationElementQname().getNamespaceURI(),
                this.fragments.getOpenToscaApiCorrelationElementQname().getLocalPart(), "opentoscaAPI");

        requestVariableQName = this.bpelProcessHandler.importNamespace(requestVariableQName, plan);

        this.bpelProcessHandler.addNamespaceToBPELDoc(responseVariableQName.getPrefix(),
            responseVariableQName.getNamespaceURI(), plan);

        this.bpelProcessHandler.addNamespaceToBPELDoc(requestVariableQName.getPrefix(),
            requestVariableQName.getNamespaceURI(), plan);

        String restCallResponseVarName;
        do {
            restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        } while (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE,
            responseVariableQName, plan));

        String restCallRequestVarName;
        do {
            restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
        } while (!this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.ELEMENT,
            requestVariableQName, plan));

        try {
            Node assignRestRequestNode =
                this.fragments.generateServiceInstanceRequestMessageAssignAsNode("CorrelationID",
                    restCallRequestVarName);
            assignRestRequestNode = plan.getBpelDocument().importNode(assignRestRequestNode, true);
            appendToInitSequence(assignRestRequestNode, plan);
        } catch (final IOException | SAXException e1) {
            e1.printStackTrace();
            throw new ServiceInsanceHandlingException(e1);
        }

        try {
            Node serviceInstancePOSTNode =
                this.fragments.generateBPEL4RESTLightServiceInstancePOSTAsNode(instanceDataAPIUrlVarName,
                    restCallRequestVarName,
                    restCallResponseVarName);
            serviceInstancePOSTNode = plan.getBpelDocument().importNode(serviceInstancePOSTNode, true);
            appendToInitSequence(serviceInstancePOSTNode, plan);
        } catch (final IOException | SAXException e) {
            throw new ServiceInsanceHandlingException(e);
        }

        // assign the serviceInstance REST POST Response into global service
        // instance variable

        if (serviceInstanceUrlVarName == null) {
            throw new ServiceInsanceHandlingException("serviceInstanceUrlVarName is null in plan: " + plan.getId());
        }

        if (serviceInstanceIdVarName == null) {
            throw new ServiceInsanceHandlingException("serviceInstanceIdVarName is null in plan: " + plan.getId());
        }

        if (serviceTemplateUrlVarName == null) {
            throw new ServiceInsanceHandlingException("serviceTemplateUrlVarName is null in plan: " + plan.getId());
        }

        if (planInstanceUrlVarName == null) {
            throw new ServiceInsanceHandlingException("planInstanceUrlVarName is null in plan: " + plan.getId());
        }

        final String planName = plan.getId().substring(plan.getId().lastIndexOf("}") + 1);

        try {
            String serviceInstanceCorrelationIdVarName =
                this.bpelProcessHandler.addGlobalStringVariable("ServiceInstanceCorrelationID", plan);
            Node assignCorr =
                this.fragments.generateAssignFromInputMessageToStringVariableAsNode("CorrelationID",
                    serviceInstanceCorrelationIdVarName);
            assignCorr = plan.getBpelDocument().importNode(assignCorr, true);
            appendToInitSequence(assignCorr, plan);
            Node serviceInstanceURLAssignNode;
            if (isManagementPlan) {
                serviceInstanceURLAssignNode =
                    this.fragments.generateServiceInstanceDataVarsAssignForManagementPlansAsNode(restCallResponseVarName,
                        serviceInstanceUrlVarName,
                        instanceDataAPIUrlVarName,
                        serviceInstanceIdVarName,
                        serviceTemplateUrlVarName,
                        serviceInstanceCorrelationIdVarName,
                        planName,
                        planInstanceUrlVarName);
                serviceInstanceURLAssignNode = plan.getBpelDocument().importNode(serviceInstanceURLAssignNode, true);
            } else {

                serviceInstanceURLAssignNode =
                    this.fragments.generateServiceInstanceDataVarsAssignForBuildPlansAsNode(restCallResponseVarName,
                        serviceInstanceUrlVarName,
                        instanceDataAPIUrlVarName,
                        serviceInstanceIdVarName,
                        serviceTemplateUrlVarName,
                        serviceInstanceCorrelationIdVarName,
                        planName,
                        planInstanceUrlVarName);
                serviceInstanceURLAssignNode = plan.getBpelDocument().importNode(serviceInstanceURLAssignNode, true);
            }
            appendToInitSequence(serviceInstanceURLAssignNode, plan);
        } catch (final IOException | SAXException e) {
            e.printStackTrace();
            throw new ServiceInsanceHandlingException("Can't read xml template, couldn't generate bpel code");
        }
    }

    public void initServiceInstancesURLVariableFromAvailableServiceInstanceUrlVar(BPELPlan plan,
                                                                                  String availableServiceInstanceUrlVar,
                                                                                  QName serviceTemplateId,
                                                                                  String csarName,
                                                                                  String targetServiceInstancesUrlVar) {
        String xpathQuery1 = "concat(substring-before(string($" + availableServiceInstanceUrlVar
            + "),'csars'),'csars/','" + csarName + "','/servicetemplates/','" + serviceTemplateId.getLocalPart() + "','/instances')";
        try {
            Node assignServiceInstancesUrl =
                this.fragments.createAssignVarToVarWithXpathQueryAsNode("createTargetServiceInstancesUrl",
                    availableServiceInstanceUrlVar,
                    targetServiceInstancesUrlVar, xpathQuery1);
            assignServiceInstancesUrl = plan.getBpelDocument().importNode(assignServiceInstancesUrl, true);
            appendToInitSequence(assignServiceInstancesUrl, plan);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            throw new ServiceInsanceHandlingException("Can't read xml template, couldn't generate bpel code");
        }
    }

    public class ServiceInsanceHandlingException extends RuntimeException {
        public ServiceInsanceHandlingException(Exception e) {
            super(e);
        }

        public ServiceInsanceHandlingException(String s) {
            super(s);
        }
    }
}
