package org.opentosca.planbuilder.core.bpel.tosca.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class NodeRelationInstanceVariablesHandler {

    private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
    // private static final String InstanceURLVarKeyword = "InstanceURL";
    private static final String nodeInstanceURLVarKeyword = "nodeInstanceURL";
    private static final String relationInstanceURLVarKeyword = "relationshipInstanceURL";
    // private static final String InstanceIDVarKeyword = "InstanceID";
    private static final String nodeInstanceIDVarKeyword = "nodeInstanceID";
    private static final String relationInstanceIDVarKeyword = "relationInstanceID";

    private final BPELPlanHandler bpelProcessHandler;

    private final BPELScopeHandler bpelTemplateScopeHandler;

    private final BPELProcessFragments bpelFragments;
    private final SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;

    public NodeRelationInstanceVariablesHandler(final BPELPlanHandler bpelProcessHandler) throws ParserConfigurationException {
        this.bpelTemplateScopeHandler = new BPELScopeHandler();
        this.bpelFragments = new BPELProcessFragments();
        this.bpelProcessHandler = bpelProcessHandler;
        this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
    }

    public boolean addIfNullAbortCheck(final BPELPlan plan, final Property2VariableMapping propMap,
                                       AbstractServiceTemplate serviceTemplate) {
        boolean check = true;
        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null) {
                check &= this.addIfNullAbortCheck(templatePlan, propMap, serviceTemplate);
            }
        }
        return check;
    }

    public boolean addIfNullAbortCheck(final BPELScope templatePlan, final Property2VariableMapping propMap,
                                       AbstractServiceTemplate serviceTemplate) {

        for (PropertyVariable var : propMap.getNodePropertyVariables(serviceTemplate, templatePlan.getNodeTemplate())) {
            final String bpelVarName = var.getVariableName();
            // as the variables are there and only possibly empty we just check
            // the string inside
            final String xpathQuery = "string-length(normalize-space($" + bpelVarName + ")) = 0";
            final QName propertyEmptyFault = new QName("http://opentosca.org/plans/faults", "PropertyValueEmptyFault");
            try {
                Node bpelIf = this.bpelFragments.generateBPELIfTrueThrowFaultAsNode(xpathQuery, propertyEmptyFault);
                bpelIf = templatePlan.getBpelDocument().importNode(bpelIf, true);
                templatePlan.getBpelSequencePrePhaseElement().appendChild(bpelIf);
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            } catch (final SAXException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Fetches the correct nodeInstanceID link for the given TemplatePlan and sets the value inside a NodeInstanceID
     * bpel variable
     *
     * @param templatePlan              a templatePlan with set variable with name NodeInstanceID
     * @param serviceTemplateUrlVarName the name of the variable holding the url to the serviceTemplate
     */
    public boolean addNodeInstanceFindLogic(final BPELScope templatePlan, final String serviceTemplateUrlVarName,
                                            final String query, AbstractServiceTemplate serviceTemplate) {

        if (templatePlan.getNodeTemplate() == null) {
            throw new RuntimeException("Can't create instance find logic only for nodes");
        }
        // add XML Schema Namespace for the logic
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
            new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
        // find nodeInstance with query at instanceDataAPI
        try {
            Node nodeInstanceGETNode =
                this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
                    instanceDataAPIResponseVarName,
                    templatePlan.getNodeTemplate()
                        .getId(),
                    query);
            nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstanceGETNode);
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final String instanceUrlVarName = this.findInstanceVarName(templatePlan, serviceTemplate, true);
        final String instanceIdVarName = this.findInstanceVarName(templatePlan, serviceTemplate, false);

        // fetch nodeInstanceID from nodeInstance query
        try {
            Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                this.bpelFragments.createAssignSelectFirstNodeInstanceAndAssignToStringVarAsNode(instanceDataAPIResponseVarName,
                    instanceUrlVarName, instanceIdVarName);
            assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
            templatePlan.getBpelSequencePrePhaseElement()
                .appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean addRelationInstanceFindLogic(final BPELScope templatePlan, final String serviceTemplateUrlVarName,
                                                final String query, AbstractServiceTemplate serviceTemplate) {
        if (templatePlan.getRelationshipTemplate() == null) {
            throw new RuntimeException("Can't create instance find logic only for relations");
        }
        // add XML Schema Namespace for the logic
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
            new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
        // find relationInstance with query at instanceDataAPI
        try {
            Node relationInstanceGETNode =
                this.bpelFragments.createRESTExtensionGETForRelationInstanceDataAsNode(serviceTemplateUrlVarName,
                    instanceDataAPIResponseVarName,
                    templatePlan.getRelationshipTemplate()
                        .getId(),
                    query);
            relationInstanceGETNode = templatePlan.getBpelDocument().importNode(relationInstanceGETNode, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(relationInstanceGETNode);
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final String instanceIDVarName = this.findInstanceVarName(templatePlan, serviceTemplate);

        // fetch nodeInstanceID from nodeInstance query
        try {
            Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                this.bpelFragments.createAssignSelectFirstRelationInstanceFromResponseAsNode(instanceDataAPIResponseVarName, instanceIDVarName);
            assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
                templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
            templatePlan.getBpelSequencePrePhaseElement()
                .appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean addInstanceIDVarToTemplatePlans(final BPELPlan plan, AbstractServiceTemplate serviceTemplate) {
        boolean check = true;

        for (AbstractNodeTemplate node : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            check &= addInstanceIDVarToPlan(node, plan, serviceTemplate);
        }

        for (AbstractRelationshipTemplate relation : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
            check &= addInstanceIDVarToPlan(relation, plan, serviceTemplate);
        }

        return check;
    }

    private boolean addInstanceIDVarToPlan(AbstractNodeTemplate nodeTemplate, BPELPlan plan, AbstractServiceTemplate serviceTemplate) {
        String templateId = nodeTemplate.getId();
        String instanceIdVarName = nodeInstanceIDVarKeyword;
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        instanceIdVarName += "_" + ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(templateId) + "_" + System.currentTimeMillis();

        return this.bpelProcessHandler.addVariable(instanceIdVarName, VariableType.TYPE,
            new QName(xsdNamespace, "string", xsdPrefix),
            plan);
    }

    private boolean addInstanceIDVarToPlan(AbstractRelationshipTemplate relationshipTemplate, BPELPlan plan, AbstractServiceTemplate serviceTemplate) {
        String templateId = relationshipTemplate.getId();
        String instanceIdVarName = relationInstanceIDVarKeyword;
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);

        instanceIdVarName += "_" + ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(templateId) + "_" + System.currentTimeMillis();

        return this.bpelProcessHandler.addVariable(instanceIdVarName, VariableType.TYPE,
            new QName(xsdNamespace, "string", xsdPrefix),
            plan);
    }

    private boolean addInstanceURLVarToTemplatePlan(BPELPlan plan, final AbstractNodeTemplate nodeTemplate,
                                                    AbstractServiceTemplate serviceTemplate) {

        String templateId = nodeTemplate.getId();
        String instanceUrlVarName = nodeInstanceURLVarKeyword;
        boolean addNamespace = false;
        String xsdPrefix = null;
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

        while (!addNamespace) {

            xsdPrefix = "xsd" + System.currentTimeMillis();
            addNamespace =
                this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);
        }

        instanceUrlVarName += "_" + ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(templateId) + "_" + System.currentTimeMillis();

        return this.bpelProcessHandler.addVariable(instanceUrlVarName, VariableType.TYPE,
            new QName(xsdNamespace, "string", xsdPrefix),
            plan);
    }

    private boolean addInstanceURLVarToTemplatePlan(BPELPlan plan, final AbstractRelationshipTemplate relationshipTemplate,
                                                    AbstractServiceTemplate serviceTemplate) {

        String templateId = relationshipTemplate.getId();
        String instanceUrlVarName = relationInstanceURLVarKeyword;
        boolean addNamespace = false;
        String xsdPrefix = null;
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

        while (!addNamespace) {

            xsdPrefix = "xsd" + System.currentTimeMillis();
            addNamespace =
                this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);
        }

        instanceUrlVarName += "_" + ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(templateId) + "_" + System.currentTimeMillis();

        return this.bpelProcessHandler.addVariable(instanceUrlVarName, VariableType.TYPE,
            new QName(xsdNamespace, "string", xsdPrefix),
            plan);
    }

    /**
     * Adds a NodeInstanceID Variable to each TemplatePlan inside the given Plan
     *
     * @param plan a plan with TemplatePlans
     */
    public boolean addInstanceURLVarToTemplatePlans(final BPELPlan plan, AbstractServiceTemplate serviceTemplate) {
        boolean check = true;

        for (AbstractRelationshipTemplate relation : serviceTemplate.getTopologyTemplate().getRelationshipTemplates()) {
            check &= addInstanceURLVarToTemplatePlan(plan, relation, serviceTemplate);
        }

        for (AbstractNodeTemplate node : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            check &= addInstanceURLVarToTemplatePlan(plan, node, serviceTemplate);
        }
        return check;
    }

    public boolean addNodeInstanceFindLogic(final BPELPlan plan, final String queryForNodeInstances,
                                            AbstractServiceTemplate serviceTemplate) {
        boolean check = true;

        final String serviceTemplateUrlVarName = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            if (templatePlan.getNodeTemplate() != null) {
                check &= addNodeInstanceFindLogic(templatePlan, serviceTemplateUrlVarName, queryForNodeInstances,
                    serviceTemplate);
            }
        }

        return check;
    }

    public boolean addRelationInstanceFindLogic(final BPELPlan plan, final String queryForRelationInstances, AbstractServiceTemplate serviceTemplate) {
        boolean check = true;

        final String serviceTemplateUrlVarName = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            if (templatePlan.getRelationshipTemplate() != null) {
                check &= addRelationInstanceFindLogic(templatePlan, serviceTemplateUrlVarName, queryForRelationInstances,
                    serviceTemplate);
            }
        }

        return check;
    }

    /**
     * Adds logic to fetch property data from the instanceDataAPI with the nodeInstanceID variable. The property data is
     * then assigned to appropriate BPEL variables of the given plan.
     *
     * @param plan    a plan containing templatePlans with set nodeInstanceID variables
     * @param propMap a Mapping from NodeTemplate Properties to BPEL Variables
     * @return true if adding logic described above was successful
     */
    public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELPlan plan,
                                                                  final Property2VariableMapping propMap,
                                                                  AbstractServiceTemplate serviceTemplate) {
        boolean check = true;
        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null
                && !templatePlan.getNodeTemplate().getProperties().asMap().isEmpty()) {
                check &= this.addPropertyVariableUpdateBasedOnNodeInstanceID(templatePlan, propMap, serviceTemplate);
            }
        }
        return check;
    }

    public boolean addPropertyVariableUpdateBasedOnRelationInstanceID(final BPELPlan plan, final Property2VariableMapping propMap, AbstractServiceTemplate serviceTemplate) {
        boolean check = true;
        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            if (templatePlan.getRelationshipTemplate() != null && templatePlan.getRelationshipTemplate().getProperties() != null
                && !templatePlan.getRelationshipTemplate().getProperties().asMap().isEmpty()) {
                check &= this.addPropertyVariableUpdateBasedOnRelationInstanceID(templatePlan, propMap, serviceTemplate);
            }
        }
        return check;
    }

    public boolean addPropertyVariableUpdateBasedOnRelationInstanceID(final BPELScope templatePlan, final Property2VariableMapping propMap, AbstractServiceTemplate serviceTemplate) {
        // check if everything is available
        if (templatePlan.getRelationshipTemplate() == null) {
            return false;
        }

        if (templatePlan.getRelationshipTemplate().getProperties() == null) {
            return false;
        }

        if (this.findInstanceVarName(templatePlan, serviceTemplate) == null) {
            return false;
        }

        final String instanceIdVarName = this.findInstanceVarName(templatePlan, serviceTemplate);

        final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
        // add XMLSchema Namespace for the logic
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
            new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);

        // fetch properties from nodeInstance
        try {
            Node nodeInstancePropertiesGETNode =
                this.bpelFragments.createRESTExtensionGETForInstancePropertiesAsNode(instanceIdVarName,
                    instanceDataAPIResponseVarName);
            nodeInstancePropertiesGETNode =
                templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        // assign bpel variables from the requested properties
        // create mapping from property dom nodes to bpelvariable
        final Map<String, String> string2BpelVarNameMap = new HashMap<>();

        Map<String, String> propertiesMap = relationshipTemplate.getProperties().asMap();

        for (PropertyVariable var : propMap.getRelationPropertyVariables(serviceTemplate, relationshipTemplate)) {
            if (propertiesMap.containsKey(var.getPropertyName())) {
                string2BpelVarNameMap.put(var.getPropertyName(), var.getVariableName());
            }
        }

        try {
            Node assignPropertiesToVariables =
                this.bpelFragments.createAssignFromInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                    + System.currentTimeMillis(), instanceDataAPIResponseVarName, string2BpelVarNameMap, relationshipTemplate.getProperties().getNamespace());
            assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Adds logic to fetch property data from the instanceDataAPI with the nodeInstanceID variable. The property data is
     * then assigned to appropriate BPEL Variables of the given templatePlan.
     *
     * @param templatePlan a TemplatePlan of a NodeTemplate that has properties
     * @param propMap      a Mapping from NodeTemplate Properties to BPEL Variables
     * @return true if adding logic described above was successful
     */
    public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELScope templatePlan,
                                                                  final Property2VariableMapping propMap,
                                                                  AbstractServiceTemplate serviceTemplate) {
        // check if everything is available
        if (templatePlan.getNodeTemplate() == null) {
            return false;
        }

        if (templatePlan.getNodeTemplate().getProperties() == null) {
            return false;
        }

        if (this.findInstanceVarName(templatePlan, serviceTemplate) == null) {
            return false;
        }

        final String instanceIdVarName = this.findInstanceVarName(templatePlan, serviceTemplate);

        final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
        // add XMLSchema Namespace for the logic
        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
            new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);

        // fetch properties from nodeInstance
        try {
            Node nodeInstancePropertiesGETNode =
                this.bpelFragments.createRESTExtensionGETForInstancePropertiesAsNode(instanceIdVarName,
                    instanceDataAPIResponseVarName);
            nodeInstancePropertiesGETNode =
                templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        // assign bpel variables from the requested properties
        // create mapping from property dom nodes to bpelvariable
        final Map<String, String> string2BpelVarNameMap = new HashMap<>();

        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        for (PropertyVariable var : propMap.getNodePropertyVariables(serviceTemplate, nodeTemplate)) {
            if (propertiesMap.containsKey(var.getPropertyName())) {
                string2BpelVarNameMap.put(var.getPropertyName(), var.getVariableName());
            }
        }

        try {
            Node assignPropertiesToVariables =
                this.bpelFragments.createAssignFromInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                    + System.currentTimeMillis(), instanceDataAPIResponseVarName, string2BpelVarNameMap, nodeTemplate.getProperties().getNamespace());
            assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
            templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELPlanContext context,
                                                                  final AbstractNodeTemplate nodeTemplate,
                                                                  AbstractServiceTemplate serviceTemplate) {

        final String instanceIdVarName =
            this.findInstanceVarName(serviceTemplate, context.getMainVariableNames(), nodeTemplate.getId(), true, nodeInstanceURLVarKeyword, relationInstanceURLVarKeyword);

        if (instanceIdVarName == null) {
            return false;
        }

        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

        // create Response Variable for interaction
        final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();

        context.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
            new QName(xsdNamespace, "anyType", xsdPrefix));

        // fetch properties from nodeInstance
        try {
            Node nodeInstancePropertiesGETNode =
                this.bpelFragments.createRESTExtensionGETForInstancePropertiesAsNode(instanceIdVarName,
                    instanceDataAPIResponseVarName);

            nodeInstancePropertiesGETNode = context.importNode(nodeInstancePropertiesGETNode);
            context.getPrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        // assign bpel variables from the requested properties
        // create mapping from property dom nodes to bpelvariable
        final Map<String, String> string2BpelVarNameMap = new HashMap<>();

        Map<String, String> propertiesMap = nodeTemplate.getProperties().asMap();

        for (String propertyName : propertiesMap.keySet()) {
            final String bpelVarName = context.getVariableNameOfProperty(nodeTemplate, propertyName);
            if (bpelVarName != null) {
                string2BpelVarNameMap.put(propertyName, bpelVarName);
            }
        }

        try {
            Node assignPropertiesToVariables =
                this.bpelFragments.createAssignFromInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
                    + System.currentTimeMillis(), instanceDataAPIResponseVarName, string2BpelVarNameMap, nodeTemplate.getProperties().getNamespace());
            assignPropertiesToVariables = context.importNode(assignPropertiesToVariables);
            context.getPrePhaseElement().appendChild(assignPropertiesToVariables);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String appendCountInstancesLogic(final BPELPlanContext context, final AbstractRelationshipTemplate relationshipTemplate,
                                            final String query) {

        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

        // create Response Variable for interaction
        final String responseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        final String counterVarName = "counterVariable" + System.currentTimeMillis();

        context.addGlobalVariable(responseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix));

        final Variable counterVariable = context.createGlobalStringVariable(counterVarName, "0");

        // context.addVariable(counterVarName, VariableType.TYPE, new
        // QName(xsdNamespace, "unsignedInt", xsdPrefix));

        final Node templateMainSequeceNode = context.getPrePhaseElement().getParentNode();
        final Node templateMainScopeNode = templateMainSequeceNode.getParentNode();

        // we'll move the correlation sets down one scope later

        try {

            Node getNodeInstancesREST =
                this.bpelFragments.createRESTExtensionGETForRelationInstanceDataAsNode(SimplePlanBuilderServiceInstanceHandler.getServiceTemplateURLVariableName(context.getMainVariableNames()),
                    responseVarName,
                    relationshipTemplate.getId(), query);
            getNodeInstancesREST = context.importNode(getNodeInstancesREST);
            templateMainSequeceNode.appendChild(getNodeInstancesREST);

            Node assignCounter =
                this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignInstanceCount_"
                        + relationshipTemplate.getId() + "_" + context.getIdForNames(), responseVarName,
                    counterVariable.getVariableName(),
                    "count(//*[local-name()='RelationshipTemplateInstance'])");
            assignCounter = context.importNode(assignCounter);
            templateMainSequeceNode.appendChild(assignCounter);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // count(//*[local-name()='Reference' and @*[local-name()!='Self']])

        final Element forEachElement = createForEachActivity(context, counterVariable.getVariableName());

        final Element forEachScopeElement = (Element) forEachElement.getElementsByTagName("scope").item(0);

        if (((Element) templateMainScopeNode).getElementsByTagName("correlationSets").getLength() != 0) {
            final Element correlationSets =
                (Element) ((Element) templateMainScopeNode).getElementsByTagName("correlationSets").item(0);

            final Node cloneCorreElement = correlationSets.cloneNode(true);

            forEachScopeElement.appendChild(cloneCorreElement);
            templateMainScopeNode.removeChild(correlationSets);
        }
        final Element sequenceElement = context.createElement(BPELPlan.bpelNamespace, "sequence");

        sequenceElement.appendChild(context.importNode(context.getPrePhaseElement().cloneNode(true)));
        sequenceElement.appendChild(context.importNode(context.getProvisioningPhaseElement().cloneNode(true)));
        sequenceElement.appendChild(context.importNode(context.getPostPhaseElement().cloneNode(true)));

        forEachScopeElement.appendChild(sequenceElement);

        templateMainSequeceNode.removeChild(context.getPrePhaseElement());
        templateMainSequeceNode.removeChild(context.getPostPhaseElement());
        templateMainSequeceNode.removeChild(context.getProvisioningPhaseElement());

        templateMainSequeceNode.appendChild(forEachElement);

        return null;
    }

    public String appendCountInstancesLogic(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                                            final String query) {

        final String xsdPrefix = "xsd" + System.currentTimeMillis();
        final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

        // create Response Variable for interaction
        final String responseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
        final String counterVarName = "counterVariable" + System.currentTimeMillis();

        context.addGlobalVariable(responseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix));

        final Variable counterVariable = context.createGlobalStringVariable(counterVarName, "0");

        // context.addVariable(counterVarName, VariableType.TYPE, new
        // QName(xsdNamespace, "unsignedInt", xsdPrefix));

        final Node templateMainSequeceNode = context.getPrePhaseElement().getParentNode();
        final Node templateMainScopeNode = templateMainSequeceNode.getParentNode();

        // we'll move the correlation sets down one scope later

        try {

            Node getNodeInstancesREST =
                this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(SimplePlanBuilderServiceInstanceHandler.getServiceTemplateURLVariableName(context.getMainVariableNames()),
                    responseVarName,
                    nodeTemplate.getId(), query);
            getNodeInstancesREST = context.importNode(getNodeInstancesREST);
            templateMainSequeceNode.appendChild(getNodeInstancesREST);

            Node assignCounter =
                this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignInstanceCount_"
                        + nodeTemplate.getId() + "_" + context.getIdForNames(), responseVarName,
                    counterVariable.getVariableName(),
                    "count(//*[local-name()='NodeTemplateInstance'])");
            assignCounter = context.importNode(assignCounter);
            templateMainSequeceNode.appendChild(assignCounter);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // count(//*[local-name()='Reference' and @*[local-name()!='Self']])

        final Element forEachElement = createForEachActivity(context, counterVariable.getVariableName());

        final Element forEachScopeElement = (Element) forEachElement.getElementsByTagName("scope").item(0);

        if (((Element) templateMainScopeNode).getElementsByTagName("correlationSets").getLength() != 0) {
            final Element correlationSets =
                (Element) ((Element) templateMainScopeNode).getElementsByTagName("correlationSets").item(0);

            final Node cloneCorreElement = correlationSets.cloneNode(true);

            forEachScopeElement.appendChild(cloneCorreElement);
            templateMainScopeNode.removeChild(correlationSets);
        }

        if (((Element) templateMainScopeNode).getElementsByTagName("compensationHandler").getLength() != 0) {
            final Element compensationHandler = (Element) ((Element) templateMainScopeNode).getElementsByTagName("compensationHandler").item(0);

            final Node cloneCompensationHandler = compensationHandler.cloneNode(true);

            forEachScopeElement.appendChild(cloneCompensationHandler);
            templateMainScopeNode.removeChild(compensationHandler);
        }

        final Element sequenceElement = context.createElement(BPELPlan.bpelNamespace, "sequence");

        sequenceElement.appendChild(context.importNode(context.getPrePhaseElement().cloneNode(true)));
        sequenceElement.appendChild(context.importNode(context.getProvisioningPhaseElement().cloneNode(true)));
        sequenceElement.appendChild(context.importNode(context.getPostPhaseElement().cloneNode(true)));

        forEachScopeElement.appendChild(sequenceElement);

        templateMainSequeceNode.removeChild(context.getPrePhaseElement());
        templateMainSequeceNode.removeChild(context.getPostPhaseElement());
        templateMainSequeceNode.removeChild(context.getProvisioningPhaseElement());

        templateMainSequeceNode.appendChild(forEachElement);

        return null;
    }

    public String appendCountInstancesLogic(final PlanContext context,
                                            final AbstractRelationshipTemplate relationshipTemplate) {
        // TODO
        return null;
    }

    public String appendCountInstancesLogic(final BPELPlanContext context, final String query) {
        if (context.getNodeTemplate() == null) {
            return this.appendCountInstancesLogic(context, context.getRelationshipTemplate());
        } else {
            return this.appendCountInstancesLogic(context, context.getNodeTemplate(), query);
        }
    }

    public Element createForEachActivity(final BPELPlanContext context, final String instanceCountVariableName) {
        final Element forEachElement = context.createElement(BPELPlan.bpelNamespace, "forEach");

        // tz
        forEachElement.setAttribute("counterName", "selectInstanceCounter" + System.currentTimeMillis());
        forEachElement.setAttribute("parallel", "no");

        /*
         * <startCounterValue expressionLanguage="anyURI"?> unsigned-integer-expression </startCounterValue>
         * <finalCounterValue expressionLanguage="anyURI"?> unsigned-integer-expression </finalCounterValue>
         * <completionCondition>? <branches expressionLanguage="anyURI"? successfulBranchesOnly="yes|no"?>?
         * unsigned-integer-expression </branches> </completionCondition> <scope ...>...</scope>
         */

        final Element startCounterValueElement = context.createElement(BPELPlan.bpelNamespace, "startCounterValue");

        startCounterValueElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);

        final Text textSectionStartValue = startCounterValueElement.getOwnerDocument().createTextNode("\"1\"");
        startCounterValueElement.appendChild(textSectionStartValue);

        final Element finalCounterValueElement = context.createElement(BPELPlan.bpelNamespace, "finalCounterValue");

        finalCounterValueElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);

        final Text textSectionFinalValue =
            startCounterValueElement.getOwnerDocument().createTextNode("$" + instanceCountVariableName);
        finalCounterValueElement.appendChild(textSectionFinalValue);

        final Element scopeElement = context.createElement(BPELPlan.bpelNamespace, "scope");

        forEachElement.appendChild(startCounterValueElement);
        forEachElement.appendChild(finalCounterValueElement);
        forEachElement.appendChild(scopeElement);

        return forEachElement;
    }

    public String findInstanceUrlVarName(final BPELPlan plan, AbstractServiceTemplate serviceTemplate,
                                         final String templateId, final boolean isNode) {
        return this.findInstanceVarName(serviceTemplate, this.bpelProcessHandler.getMainVariableNames(plan),
            templateId, isNode, nodeInstanceURLVarKeyword, relationInstanceURLVarKeyword);
    }

    public String findInstanceIdVarName(final BPELPlan plan, AbstractServiceTemplate serviceTemplate,
                                        final String templateId, final boolean isNode) {
        return this.findInstanceVarName(serviceTemplate, this.bpelProcessHandler.getMainVariableNames(plan),
            templateId, isNode, nodeInstanceIDVarKeyword, relationInstanceIDVarKeyword);
    }

    public String findInstanceVarName(final BPELScope templatePlan, AbstractServiceTemplate serviceTemplate) {
        return findInstanceVarName(templatePlan, serviceTemplate, true);
    }

    public String findInstanceVarName(final BPELScope templatePlan, AbstractServiceTemplate serviceTemplate, boolean findUrl) {
        String templateId = "";

        boolean isNode = true;
        if (templatePlan.getNodeTemplate() != null) {
            templateId = templatePlan.getNodeTemplate().getId();
        } else {
            templateId = templatePlan.getRelationshipTemplate().getId();
            isNode = false;
        }
        if (findUrl) {
            return this.findInstanceUrlVarName(templatePlan.getBuildPlan(), serviceTemplate, templateId, isNode);
        } else {
            return this.findInstanceIdVarName(templatePlan.getBuildPlan(), serviceTemplate, templateId, isNode);
        }
    }

    public String findInstanceVarName(AbstractServiceTemplate serviceTemplate, final List<String> varNames,
                                      final String templateId, final boolean isNode, String nodeInstanceURLVarKeyword, String relationInstanceURLVarKeyword) {
        final String instanceURLVarName = (isNode ? nodeInstanceURLVarKeyword : relationInstanceURLVarKeyword) + "_"
            + ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(templateId) + "_";
        for (final String varName : varNames) {
            if (varName.contains(instanceURLVarName)) {
                return varName;
            }
        }
        return null;
    }
}
