package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * Appends init code to the given BuildPlan to instantiate a serviceInstance at the responsible
 * OpenTOSCA Container
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ServiceInstanceInitializer {

    private static final String ServiceInstanceVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
    private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";

    private final BPELProcessFragments fragments;

    private final BPELPlanHandler planHandler;
    private final BPELPlanHandler bpelProcessHandler;

    public ServiceInstanceInitializer() throws ParserConfigurationException {
        this.planHandler = new BPELPlanHandler();
        this.bpelProcessHandler = new BPELPlanHandler();
        this.fragments = new BPELProcessFragments();
    }

    /**
     * Appends the logic to handle instanceDataAPI interaction. Adds instanceDataAPI and
     * serviceInstanceAPI elements into the input message of the given plan and assign internal global
     * variables with the input values
     *
     * @param plan a plan
     */
    public void initializeInstanceDataAPIandServiceInstanceIDFromInput(final BPELPlan plan) {
        this.appendAssignFromInputToVariable(plan, ServiceInstanceInitializer.InstanceDataAPIUrlKeyword);
        this.appendAssignFromInputToVariable(plan, ServiceInstanceInitializer.ServiceInstanceVarKeyword);
    }

    /**
     * Appends logic to handle instanceDataAPI interaction. Adds instanceDataAPI element into input
     * message. At runtime saves the input value into a global variable and creates a serviceInstance
     * for the plan.
     *
     * @param plan a plan
     */
    public void initializeInstanceDataFromInput(final BPELPlan plan) {
        final String instanceDataAPIVarName = this.appendAssignFromInputToVariable(plan,
            ServiceInstanceInitializer.InstanceDataAPIUrlKeyword);
        this.appendServiceInstanceInitCode(plan, instanceDataAPIVarName);
        this.addAssignOutputWithServiceInstanceId(plan);
    }

    private void addAssignOutputWithServiceInstanceId(final BPELPlan plan) {
        this.planHandler.addStringElementToPlanResponse("instanceId", plan);

        String serviceInstanceVarName = null;
        for (final String varName : this.bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstanceInitializer.ServiceInstanceVarKeyword)) {
                serviceInstanceVarName = varName;
                break;
            }
        }

        try {
            Node copyNode = this.fragments.generateCopyFromStringVarToOutputVariableAsNode(serviceInstanceVarName,
                "output", "payload", "instanceId");
            copyNode = plan.getBpelDocument().importNode(copyNode, true);
            plan.getBpelMainSequenceOutputAssignElement().appendChild(copyNode);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

    }

    public String getServiceInstanceVariableName(final Collection<String> names) {
        for (final String varName : names) {
            if (varName.contains(ServiceInstanceInitializer.ServiceInstanceVarKeyword)) {
                return varName;
            }
        }
        return null;
    }

    public String getServiceInstanceVariableName(final BPELPlan plan) {
        return this.getServiceInstanceVariableName(this.bpelProcessHandler.getMainVariableNames(plan));
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
            Node assignRequestWithStateNode = this.fragments.createAssignXpathQueryToStringVarFragmentAsNode(assignName,
                "string('" + state + "')", restCallRequestVarName);
            assignRequestWithStateNode = plan.getBpelDocument().importNode(assignRequestWithStateNode, true);
            insertBeforeElement.getParentNode().insertBefore(assignRequestWithStateNode, insertBeforeElement);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String serviceInstanceVarName = "";

        for (final String varName : this.bpelProcessHandler.getMainVariableNames(plan)) {
            if (varName.contains(ServiceInstanceVarKeyword)) {
                serviceInstanceVarName = varName;
            }
        }

        if (serviceInstanceVarName.isEmpty()) {
            return false;
        }

        try {
            Node setInstanceStateRequestNode = this.fragments.createBPEL4RESTLightPutStateAsNode(serviceInstanceVarName,
                restCallRequestVarName);
            setInstanceStateRequestNode = plan.getBpelDocument().importNode(setInstanceStateRequestNode, true);
            insertBeforeElement.getParentNode().insertBefore(setInstanceStateRequestNode, insertBeforeElement);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
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
            Node RESTDeleteNode = this.fragments.createRESTDeleteOnURLBPELVarAsNode(
                ServiceInstanceInitializer.ServiceInstanceVarKeyword, restCallResponseVarName);
            RESTDeleteNode = plan.getBpelDocument().importNode(RESTDeleteNode, true);
            plan.getBpelMainSequenceOutputAssignElement().getParentNode().insertBefore(RESTDeleteNode,
                plan.getBpelMainSequenceOutputAssignElement());
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
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

        for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {

            if (templatePlan.getNodeTemplate() == null) {
                continue;
            }

            if (templatePlan.getNodeTemplate().getProperties() == null) {
                continue;
            }

            // find nodeInstance with query at instanceDataAPI
            try {
                Node nodeInstanceGETNode = this.fragments.createRESTExtensionGETForNodeInstanceDataAsNode(
                    ServiceInstanceInitializer.ServiceInstanceVarKeyword, restCallResponseVarName,
                    templatePlan.getNodeTemplate().getId(), null);
                nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstanceGETNode,
                    plan.getBpelMainFlowElement());
            } catch (final SAXException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            // fetch nodeInstanceID from nodeInstance query
            try {
                Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse = this.fragments.createAssignSelectFirstReferenceAndAssignToStringVarAsNode(
                    restCallResponseVarName, tempNodeInstanceIDVarName);
                assignNodeInstanceIDFromInstanceDataAPIQueryResponse = templatePlan.getBpelDocument().importNode(
                    assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
                plan.getBpelMainFlowElement().getParentNode()
                    .insertBefore(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, plan.getBpelMainFlowElement());
            } catch (final SAXException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            // fetch properties into temp anyType var
            try {
                Node nodeInstancePropertiesGETNode = this.fragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(
                    tempNodeInstanceIDVarName, restCallResponseVarName);
                nodeInstancePropertiesGETNode = templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode,
                    true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(nodeInstancePropertiesGETNode,
                    plan.getBpelMainFlowElement());

            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final SAXException e) {
                e.printStackTrace();
            }

            // assign bpel variables from the requested properties
            // create mapping from property dom nodes to bpelvariable
            final Map<Element, String> element2BpelVarNameMap = new HashMap<>();
            final NodeList propChildNodes = templatePlan.getNodeTemplate().getProperties().getDOMElement()
                                                        .getChildNodes();
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
                Node assignPropertiesToVariables = this.fragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode(
                    "assignPropertiesFromResponseToBPELVariable" + System.currentTimeMillis(), restCallResponseVarName,
                    element2BpelVarNameMap);
                assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables,
                    true);
                plan.getBpelMainFlowElement().getParentNode().insertBefore(assignPropertiesToVariables,
                    plan.getBpelMainFlowElement());
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final SAXException e) {
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

        // Our Goal with the REST Extension:
        // POST
        // http://localhost:1337/containerapi/instancedata/serviceInstances?csarID=csarId&serviceTemplateID={ns}LocalName
        // then use the response the set the proper id in the
        // serviceInstanceVariable

        /*
         * <bpel:extensionActivity> <bpel4RestLight:POST uri=
         * "$bpelvar[ContainerURL]/instancedata/serviceInstances?csarID=$bpelvar[CSARName]&amp;serviceTemplateID={http://www.example.com/tosca/ServiceTemplates/Moodle}Moodle"
         * accept="application/xml" response="instanceAPIResponse"></bpel4RestLight:POST>
         * </bpel:extensionActivity>
         */

        // generate any type variable for REST call response
        final QName reqResVarDeclId = new QName("http://www.w3.org/2001/XMLSchema", "anyType",
            "xsd" + System.currentTimeMillis());
        this.bpelProcessHandler.addNamespaceToBPELDoc(reqResVarDeclId.getPrefix(), reqResVarDeclId.getNamespaceURI(),
            buildPlan);

        final String restCallResponseVarName = "bpel4restlightVarResponse" + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(restCallResponseVarName, BPELPlan.VariableType.TYPE, reqResVarDeclId,
            buildPlan)) {
            return null;
        }

        final String restCallRequestVarName = "bpel4restlightVarRequest" + System.currentTimeMillis();
        if (!this.bpelProcessHandler.addVariable(restCallRequestVarName, BPELPlan.VariableType.TYPE, reqResVarDeclId,
            buildPlan)) {
            return null;
        }

        try {
            Node assignRestRequestNode = this.fragments.generateAssignFromInputMessageToStringVariableAsNode(
                "CorrelationID", restCallRequestVarName);
            assignRestRequestNode = buildPlan.getBpelDocument().importNode(assignRestRequestNode, true);
            this.appendToInitSequence(assignRestRequestNode, buildPlan);
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (final SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            Node serviceInstancePOSTNode = this.fragments.generateBPEL4RESTLightServiceInstancePOSTAsNode(
                instanceDataAPIUrlVarName, csarId, serviceTemplateId, restCallRequestVarName, restCallResponseVarName);
            serviceInstancePOSTNode = buildPlan.getBpelDocument().importNode(serviceInstancePOSTNode, true);
            this.appendToInitSequence(serviceInstancePOSTNode, buildPlan);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        // assign the serviceInstance REST POST Response into global service
        // instance variable

        /*
         * Sample: <?xml version="1.0" encoding="UTF-8"?> <ns2:link ns1:href=
         * "http://localhost:1337/containerapi/instancedata/serviceInstances/1" ns1:title=
         * "http://localhost:1337/containerapi/instancedata/serviceInstances/1" ns1:type="simple"
         * xmlns:ns2="http://opentosca.org/api/pp" xmlns:ns1="http://www.w3.org/1999/xlink"/>
         */

        /*
         *//*
             * [local-name()='link' and namespace-uri()='http://opentosca.org/api/pp']/@[local-name()=' href'
             * and namespace-uri()='http://www.w3.org/1999/xlink']
             */

        // create serviceInstanceVariable

        // TemplatePropWrapper serviceInstanceVariable =
        // context.createGlobalStringVariable(Handler.ServiceInstanceVarKeyword,
        // "-1");

        final String serviceInstanceUrlVarName = ServiceInstanceInitializer.ServiceInstanceVarKeyword
            + System.currentTimeMillis();
        final QName serviceInstanceUrlDeclId = new QName("http://www.w3.org/2001/XMLSchema", "string",
            "xsd" + System.currentTimeMillis());
        this.bpelProcessHandler.addNamespaceToBPELDoc(serviceInstanceUrlDeclId.getPrefix(),
            serviceInstanceUrlDeclId.getNamespaceURI(), buildPlan);

        if (!this.bpelProcessHandler.addVariable(serviceInstanceUrlVarName, BPELPlan.VariableType.TYPE,
            serviceInstanceUrlDeclId, buildPlan)) {
            return null;
        }

        try {
            Node serviceInstanceURLAssignNode = this.fragments.generateServiceInstanceURLVarAssignAsNode(
                restCallResponseVarName, serviceInstanceUrlVarName);
            serviceInstanceURLAssignNode = buildPlan.getBpelDocument().importNode(serviceInstanceURLAssignNode, true);
            this.appendToInitSequence(serviceInstanceURLAssignNode, buildPlan);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (final SAXException e) {
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
        this.planHandler.addStringElementToPlanRequest(varName, plan);

        // generate single string variable for InstanceDataAPI HTTP calls, as
        // REST BPEL PLugin
        // can only handle simple xsd types (no queries from input message)

        final QName instanceDataAPIUrlDeclId = new QName("http://www.w3.org/2001/XMLSchema", "string",
            "xsd" + System.currentTimeMillis());
        this.bpelProcessHandler.addNamespaceToBPELDoc(instanceDataAPIUrlDeclId.getPrefix(),
            instanceDataAPIUrlDeclId.getNamespaceURI(), plan);

        if (!this.bpelProcessHandler.addVariable(varName, BPELPlan.VariableType.TYPE, instanceDataAPIUrlDeclId, plan)) {
            return null;
        }

        try {
            Node assignNode = this.fragments.generateAssignFromInputMessageToStringVariableAsNode(varName, varName);

            assignNode = plan.getBpelDocument().importNode(assignNode, true);
            this.appendToInitSequence(assignNode, plan);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        } catch (final SAXException e) {
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

}
