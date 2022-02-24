package org.opentosca.planbuilder.core.bpel.context;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.common.collect.Sets;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.OperationChain;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.plugins.context.DeployTechDescriptorMapping;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.plan.bpel.GenericWsdlWrapper;
import org.opentosca.container.core.model.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>
 * This class is used for all Plugins. All acitions on TemplateBuildPlans and BuildPlans should be done with the
 * operations of this class. It is basically a Facade to Template and its TemplateBuildPlan
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELPlanContext extends PlanContext {

    private final static Logger LOG = LoggerFactory.getLogger(BPELPlanContext.class);

    private final BPELScope templateBuildPlan;
    private final BPELScopeBuilder scopeBuilder;
    private final BPELScopeHandler bpelTemplateHandler;
    private BPELPlanHandler buildPlanHandler;
    private BPELPlanHandler bpelProcessHandler;
    private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;
    final WSDLFactory factory;
    final WSDLReader reader;



    public BPELPlanContext(BPELScopeBuilder scopeBuilder, final BPELPlan plan, final BPELScope templateBuildPlan, final Property2VariableMapping map,
                       final TServiceTemplate serviceTemplate, String serviceInstanceURLVarName,
                       String serviceInstanceIDVarName, String serviceTemplateURLVarName, String planInstanceUrlVarName, Csar csar) {
        this(scopeBuilder, plan, templateBuildPlan, map, null, serviceTemplate, serviceInstanceURLVarName, serviceInstanceIDVarName, serviceTemplateURLVarName, planInstanceUrlVarName, csar);
    }

    public BPELPlanContext(BPELScopeBuilder scopeBuilder, final BPELPlan plan, final BPELScope templateBuildPlan, final Property2VariableMapping map,
                           final DeployTechDescriptorMapping descriptorMap,
                           final TServiceTemplate serviceTemplate, String serviceInstanceURLVarName,
                           String serviceInstanceIDVarName, String serviceTemplateURLVarName, String planInstanceUrlVarName, Csar csar) {
        super(plan, serviceTemplate, map, descriptorMap, serviceInstanceURLVarName, serviceInstanceIDVarName, serviceTemplateURLVarName, planInstanceUrlVarName, csar);


        this.scopeBuilder = scopeBuilder;
        this.templateBuildPlan = templateBuildPlan;
        this.bpelTemplateHandler = new BPELScopeHandler();
        try {
            this.buildPlanHandler = new BPELPlanHandler();
            this.bpelProcessHandler = new BPELPlanHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.bpelProcessHandler);
            this.factory = WSDLFactory.newInstance();
            this.reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
        } catch (final ParserConfigurationException | WSDLException e) {
            BPELPlanContext.LOG.warn("Coulnd't initialize internal handlers", e);
            throw new RuntimeException("Coulnd't initialize internal handlers", e);
        }
    }

    public static Variable getVariable(String varName) {
        return new Variable(varName);
    }

    public AbstractActivity getActivity() {
        return this.templateBuildPlan.getActivity();
    }

    public void addUsedOperation(TOperation operation, TOperation compensationOperation) {
        this.templateBuildPlan.addUsedOperation(operation, compensationOperation);
    }

    public boolean addUsedOperation(String interfaceName, String operationName, String compensationInterfaceName, String compensationOperationName, Csar csar) {
        TOperation op = ModelUtils.findNodeOperation(csar, interfaceName, operationName);
        TOperation compensationOp = ModelUtils.findNodeOperation(csar, compensationInterfaceName, compensationOperationName);
        if (op != null) {
            this.addUsedOperation(op, compensationOp);
            return true;
        } else {
            return false;
        }
    }

    // TODO Refactor methods up to the BPEL specific methods

    public Map<TOperation, TOperation> getUsedOperations() {
        return this.templateBuildPlan.getUsedOperations();
    }

    /**
     * Looks for a Property with the same localName as the given String. The search is on either the Infrastructure on
     * the Source or Target of the Template this TemplateContext belongs to.
     *
     * @param propertyName  a String
     * @param directionSink whether to look in direction of the sinks or sources (If Template is NodeTemplate) or to
     *                      search on the Source-/Target-Interface (if template is RelationshipTemplate)
     * @return a Variable Object with TemplateId and Name, if null the whole Infrastructure has no Property with the
     * specified localName
     */
    public PropertyVariable getPropertyVariable(final String propertyName, final boolean directionSink) {
        final Set<TNodeTemplate> infraNodes = Sets.newHashSet();

        if (isNodeTemplate()) {
            if (directionSink) {
                // get all NodeTemplates that are reachable from this
                // nodeTemplate
                ModelUtils.getNodesFromNodeToSink(getNodeTemplate(), infraNodes, this.csar);
            } else {
                ModelUtils.getNodesFromNodeToSource(getNodeTemplate(), infraNodes, this.csar);
            }
        } else {
            if (directionSink) {
                ModelUtils.getNodesFromNodeToSink(ModelUtils.getSource(getRelationshipTemplate(), this.csar), infraNodes, this.csar);
            } else {
                ModelUtils.getNodesFromRelationToSink(getRelationshipTemplate(), infraNodes, this.csar);
            }
        }

        for (final TNodeTemplate infraNode : infraNodes) {
            for (PropertyVariable var : this.propertyMap.getNodePropertyVariables(this.serviceTemplate, infraNode)) {
                if (var.getPropertyName().equals(propertyName)) {
                    return var;
                }
            }
        }
        return null;
    }

    public String getTemplateId() {
        if (getNodeTemplate() != null) {
            return getNodeTemplate().getId();
        } else {
            return getRelationshipTemplate().getId();
        }
    }

    public Element getEventHandlersElement() {
        return this.templateBuildPlan.getBpelEventHandlersElement();
    }

    /**
     * Returns whether this context is for a nodeTemplate
     *
     * @return true if this context is for a nodeTemplate, else false
     */
    public boolean isNodeTemplate() {
        return this.templateBuildPlan.getNodeTemplate() != null;
    }

    /**
     * Returns whether this context is for a relationshipTemplate
     *
     * @return true if this context is for a relationshipTemplate, else false
     */
    public boolean isRelationshipTemplate() {
        return this.templateBuildPlan.getRelationshipTemplate() != null;
    }

    public String findInstanceURLVar(final String templateId, final boolean isNode) {
        return this.nodeRelationInstanceHandler.findInstanceUrlVarName(this.templateBuildPlan.getBuildPlan(),
            this.serviceTemplate, templateId, isNode);
    }

    public String findInstanceIDVar(final String templateId, final boolean isNode) {
        return this.nodeRelationInstanceHandler.findInstanceIdVarName(this.templateBuildPlan.getBuildPlan(), this.serviceTemplate, templateId, isNode);
    }

    /**
     * Adds a Element which is a String parameter to the BuildPlan request message
     *
     * @param localName the localName of the Element to add
     * @return true if adding was successful, else false
     */
    public boolean addStringValueToPlanRequest(final String localName) {
        return this.buildPlanHandler.addStringElementToPlanRequest(localName, this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a Element which is a String parameter to the BuildPlan response message
     *
     * @param localName the localName of the Element to add
     * @return true if adding was successful, else false
     */
    public boolean addStringValueToPlanResponse(final String localName) {
        return this.buildPlanHandler.addStringElementToPlanResponse(localName, this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a variable to the TemplateBuildPlan of the template this context belongs to
     *
     * @param name          the name of the variable
     * @param variableType  sets if this variable is a Message variable or simple BPEL variable
     * @param declarationId the XSD Type of the variable
     */
    public boolean addVariable(final String name, final BPELPlan.VariableType variableType, QName declarationId) {
        declarationId = importNamespace(declarationId);
        return this.bpelTemplateHandler.addVariable(name, variableType, declarationId, this.templateBuildPlan);
    }

    /**
     * Appends the given node the the main sequence of the buildPlan this context belongs to
     *
     * @param node a XML DOM Node
     * @return true if adding the node to the main sequence was successfull
     */
    public boolean appendToInitSequence(final Node node) {
        final Node importedNode = importNode(node);

        final Element flowElement = this.templateBuildPlan.getBuildPlan().getBpelMainFlowElement();

        final Node mainSequenceNode = flowElement.getParentNode();

        mainSequenceNode.insertBefore(importedNode, flowElement);

        return true;
    }

    /**
     * creates a context with the current context as it's parent scope using the given node template and activity types
     * as input
     */
    public BPELPlanContext createContext(final TNodeTemplate nodeTemplate, ActivityType... activityType) {
        LOG.debug("Trying to create {} plan context for nodeTemplate {}", activityType, nodeTemplate);
        for (BPELScope scope : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
            if (scope.getNodeTemplate() != null && scope.getNodeTemplate().equals(nodeTemplate)
                && Arrays.asList(activityType).contains(scope.getActivity().getType())) {
                LOG.debug("Found scope of nodeTemplate");
                return new BPELPlanContext(this.scopeBuilder, (BPELPlan) this.plan, scope, this.propertyMap, this.serviceTemplate,
                    this.serviceInstanceURLVarName, this.serviceInstanceIDVarName, this.serviceTemplateURLVarName, this.planInstanceUrlVarName,
                    this.csar);
            }
        }
        return null;
    }

    /**
     * Generates a bpel string variable with the given name + "_" + randomPositiveInt.
     *
     * @param variableName String containing a name
     * @param initVal      the value for the variable, if null the value will be empty
     * @return a TemplatePropWrapper containing the generated Id for the variable
     */
    public Variable createGlobalStringVariable(final String variableName, final String initVal) {
        final String varName = variableName + "_" + getIdForNames();
        return this.buildPlanHandler.createGlobalStringVariable(varName, initVal, this.templateBuildPlan.getBuildPlan());
    }

    public boolean isOperationExecutable(final TNodeTemplate nodeTemplate, final String interfaceName,
                                         final String operationName,
                                         final Map<TParameter, Variable> param2variableMapping) {
        scopeBuilder.createOperationCall(this, nodeTemplate, interfaceName, operationName);
        return true;
    }

    /**
     * Executes the operation of the given NodeTemplate
     *
     * @param nodeTemplate          the NodeTemplate the operation belongs to
     * @param operationName         the name of the operation to execute
     * @param param2variableMapping If a Map of Parameter to Variable is given this will be used for the operation call
     * @return true if appending logic to execute the operation at runtime was successfull
     */
    public boolean executeOperation(final TNodeTemplate nodeTemplate, final String interfaceName,
                                    final String operationName,
                                    final Map<TParameter, Variable> param2variableMapping) {

        final OperationChain chain = scopeBuilder.createOperationCall(this, nodeTemplate, interfaceName, operationName);
        if (chain == null) {
            return false;
        }

        final List<String> opNames = new ArrayList<>();
        opNames.add(operationName);

        /*
         * create a new templatePlanContext that combines the requested nodeTemplate and the scope of this
         * context
         */
        // backup nodes
        final TRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final TNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = this.createContext(nodeTemplate);

        /*
         * chain.executeIAProvisioning(context); chain.executeDAProvisioning(context);
         */
        boolean executeProvOps = false;
        if (param2variableMapping == null) {
            executeProvOps = chain.executeOperationProvisioning(context, opNames);
        } else {
            executeProvOps = chain.executeOperationProvisioning(context, opNames, param2variableMapping);
        }

        // re-set the orginal configuration of the templateBuildPlan
        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return executeProvOps;
    }

    /**
     * Returns a set of nodes that will be provisioned in the plan of this context
     */
    public Collection<TNodeTemplate> getNodesInCreation() {
        Collection<AbstractActivity> activities = this.templateBuildPlan.getBuildPlan().getActivites();
        Collection<TNodeTemplate> result = new HashSet<>();
        for (AbstractActivity activity : activities) {
            if ((activity instanceof NodeTemplateActivity) &&
                (activity.getType().equals(ActivityType.PROVISIONING) || activity.getType().equals(ActivityType.MIGRATION))) {
                result.add(((NodeTemplateActivity) activity).getNodeTemplate());
            }
        }
        return result;
    }

    /**
     * Returns all InfrastructureNodes of the Template this context belongs to
     *
     * @return a List of TNodeTemplate which are InfrastructureNodeTemplate of the template this context handles
     */

    public List<TNodeTemplate> getInfrastructureNodes() {
        final List<TNodeTemplate> infrastructureNodes = new ArrayList<>();
        if (this.templateBuildPlan.getNodeTemplate() != null) {
            ModelUtils.getInfrastructureNodes(getNodeTemplate(), infrastructureNodes, this.getCsar());
        } else {
            final TRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
            if (ModelUtils.getRelationshipBaseType(template, this.getCsar()).equals(Types.connectsToRelationType)) {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, true, this.getCsar());
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false, this.getCsar());
            } else {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false, this.getCsar());
            }
        }
        return infrastructureNodes;
    }

    /**
     * Returns the names of the global variables defined in the buildPlan this context belongs to
     *
     * @return a List of Strings representing the global variable names
     */
    public List<String> getMainVariableNames() {
        return this.bpelProcessHandler.getMainVariableNames(this.templateBuildPlan.getBuildPlan());
    }

    public boolean executeOperation(final TRelationshipTemplate relationshipTemplate, final String interfaceName,
                                    final String operationName, Map<TParameter, Variable> inputPropertyMapping,
                                    Map<TParameter, Variable> outputPropertyMapping) {

        if (inputPropertyMapping == null) {
            inputPropertyMapping = new HashMap<>();
        }
        if (outputPropertyMapping == null) {
            outputPropertyMapping = new HashMap<>();
        }

        final OperationChain chain =
            scopeBuilder.createOperationCall(relationshipTemplate, interfaceName, operationName, this.csar);
        if (chain == null) {
            return false;
        }

        final List<String> opNames = new ArrayList<>();
        opNames.add(operationName);

        final TRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final TNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        final BPELPlanContext context = new BPELPlanContext(this.scopeBuilder, (BPELPlan) this.plan, this.templateBuildPlan,
            this.propertyMap, this.serviceTemplate, this.serviceInstanceURLVarName, this.serviceInstanceIDVarName,
            this.serviceTemplateURLVarName, this.planInstanceUrlVarName, this.csar);

        context.templateBuildPlan.setNodeTemplate(null);
        context.templateBuildPlan.setRelationshipTemplate(relationshipTemplate);

        chain.executeOperationProvisioning(context, opNames, inputPropertyMapping, outputPropertyMapping);

        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return true;
    }

    public boolean executeOperation(final TNodeTemplate nodeTemplate, final String interfaceName,
                                    final String operationName,
                                    final Map<TParameter, Variable> param2propertyMapping,
                                    final Map<TParameter, Variable> param2propertyOutputMapping,
                                    final BPELScopePhaseType phase, Element elementToAppendTo) {
        final OperationChain chain = scopeBuilder.createOperationCall(this, nodeTemplate, interfaceName, operationName);
        if (chain == null) {
            return false;
        }

        final List<String> opNames = new ArrayList<>();
        opNames.add(operationName);

        /*
         * create a new templatePlanContext that combines the requested nodeTemplate and the scope of this
         * context
         */
        // backup nodes
        final TRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final TNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = this.createContext(nodeTemplate);

        /*
         * chain.executeIAProvisioning(context); chain.executeDAProvisioning(context);
         */
        if (param2propertyMapping == null) {
            chain.executeOperationProvisioning(context, opNames);
        } else {
            if (param2propertyOutputMapping == null) {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping, elementToAppendTo);
            } else {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping, param2propertyOutputMapping,
                    elementToAppendTo);
            }
        }

        // re-set the orginal configuration of the templateBuildPlan
        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return true;
    }

    private BPELPlanContext createContext(TNodeTemplate nodeTemplate) {
        final BPELPlanContext context = new BPELPlanContext(this.scopeBuilder, (BPELPlan) this.plan, this.templateBuildPlan,
            this.propertyMap, this.serviceTemplate, this.serviceInstanceURLVarName, this.serviceInstanceIDVarName,
            this.serviceTemplateURLVarName, this.planInstanceUrlVarName, this.csar);

        context.templateBuildPlan.setNodeTemplate(nodeTemplate);
        context.templateBuildPlan.setRelationshipTemplate(null);
        return context;
    }

    public boolean executeOperation(final TNodeTemplate nodeTemplate, final String interfaceName,
                                    final String operationName,
                                    final Map<TParameter, Variable> param2propertyMapping,
                                    final Map<TParameter, Variable> param2propertyOutputMapping) {

        final OperationChain chain = scopeBuilder.createOperationCall(this, nodeTemplate, interfaceName, operationName);
        if (chain == null) {
            return false;
        }

        final List<String> opNames = new ArrayList<>();
        opNames.add(operationName);

        /*
         * create a new templatePlanContext that combines the requested nodeTemplate and the scope of this
         * context
         */
        // backup nodes
        final TRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final TNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = this.createContext(nodeTemplate);

        /*
         * chain.executeIAProvisioning(context); chain.executeDAProvisioning(context);
         */
        if (param2propertyMapping == null) {
            chain.executeOperationProvisioning(context, opNames);
        } else {
            if (param2propertyOutputMapping == null) {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping);
            } else {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping,
                    param2propertyOutputMapping);
            }
        }

        // re-set the orginal configuration of the templateBuildPlan
        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return true;
    }

    /**
     * Returns the NodeTemplate of this BPELPlanContext
     *
     * @return an TNodeTemplate if this BPELPlanContext handles a NodeTemplate, else null
     */
    public TNodeTemplate getNodeTemplate() {
        return this.templateBuildPlan.getNodeTemplate();
    }

    /**
     * Returns the name of variable which is the output message of the buildPlan
     *
     * @return a String containing the variable name of the outputmessage of the BuildPlan
     */
    public String getPlanResponseMessageName() {
        return "output";
    }

    /**
     * Returns the ProvPhase Element of the TemplateBuildPlan this context belongs to
     *
     * @return a Element which is the ProvPhase Element
     */
    public Element getProvisioningPhaseElement() {
        return this.templateBuildPlan.getBpelSequenceProvisioningPhaseElement();
    }

    /**
     * Returns the Provisioning Phase Sequence Element of the Compensation scope of this context
     *
     * @return an Element which is a bpel sequence
     */
    public Element getProvisioningCompensationPhaseElement() {
        return this.templateBuildPlan.getBpelCompensationHandlerScope().getBpelSequenceProvisioningPhaseElement();
    }

    /**
     * Returns a BPEL sequence element which is used as the main fault handler sequence of this scope
     *
     * @return a DOM Element which is a BPEL sequence activity
     */
    public Element getProvisioningFaultHandlerPhaseElement() {
        return this.templateBuildPlan.getBpelFaultHandlerScope().getBpelSequenceProvisioningPhaseElement();
    }

    /**
     * Returns the RelationshipTemplate this context handles
     *
     * @return an TRelationshipTemplate if this context handle a RelationshipTemplate, else null
     */
    public TRelationshipTemplate getRelationshipTemplate() {
        return this.templateBuildPlan.getRelationshipTemplate();
    }

    // All BPEL related methods

    /**
     * Adds a copy element to the main assign element of the buildPlan this context belongs to
     *
     * @param inputRequestLocalName the localName inside the input request message
     * @param internalVariable      an internalVariable of this buildPlan
     * @return true iff adding the copy was successful, else false
     */
    public boolean addAssignFromInput2VariableToMainAssign(final String inputRequestLocalName,
                                                           final Variable internalVariable) {
        return this.bpelProcessHandler.assignVariableValueFromInput(internalVariable.getVariableName(),
            inputRequestLocalName,
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a correlationSet with the specified property
     *
     * @param correlationSetName the name for the correlationSet
     * @param propertyName       the property to use inside the correlationSet
     * @return true if adding the correlation set was successful, else false
     */
    public boolean addCorrelationSet(final String correlationSetName, final String propertyName) {
        return this.bpelTemplateHandler.addCorrelationSet(correlationSetName, propertyName, this.templateBuildPlan);
    }

    public boolean addGlobalCorrelationSet(final String correlationSetName, final Collection<String> propertyName) {
        return this.buildPlanHandler.addCorrelationSet(correlationSetName, propertyName, this.templateBuildPlan.getBuildPlan());
    }

    public Collection<String> getGlobalCorrelationSetNames() {
        return this.buildPlanHandler.getCorrelationSetNames(this.templateBuildPlan.getBuildPlan());
    }

    public boolean addGlobalVariable(final String name, final BPELPlan.VariableType variableType, QName declarationId) {
        declarationId = importNamespace(declarationId);
        return this.bpelProcessHandler.addVariable(name, variableType, declarationId,
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a partnerLink to the TemplateBuildPlan of the Template this context handles
     *
     * @param partnerLinkName       the name of the partnerLink
     * @param partnerLinkType       the name of the partnerLinkType
     * @param myRole                the name of a role inside the partnerLinkType for the myRole
     * @param partnerRole           the name of a role inside ther partnerLinkType for the partnerRole
     * @param initializePartnerRole whether the partnerRole should be initialized
     * @return true if adding the partnerLink was successful, else false
     */
    public boolean addPartnerLinkToTemplateScope(final String partnerLinkName, final String partnerLinkType,
                                                 final String myRole, final String partnerRole,
                                                 final boolean initializePartnerRole) {
        boolean check;

        // here we set the qname with namespace of the plan "ba.example"
        final QName partnerType =
            new QName(this.templateBuildPlan.getBuildPlan().getProcessNamespace(), partnerLinkType, "tns");
        check = addPLtoDeploy(partnerLinkName, partnerLinkType);
        check &= this.bpelTemplateHandler.addPartnerLink(partnerLinkName, partnerType, myRole, partnerRole,
            initializePartnerRole, this.templateBuildPlan);
        return check;
    }

    /**
     * Adds a partnerLinkType to the BuildPlan which can be used for partnerLinks in TemplateBuildPlans
     *
     * @param partnerLinkTypeName the name of the partnerLinkTypes
     * @param role1Name           the name of the 1st role
     * @param portType1           the 1st portType
     * @param role2Name           the name of the 2nd role
     * @param portType2           the 2nd porType
     * @return true if adding the partnerLinkType was successful, else false
     */
    public boolean addPartnerLinkType(final String partnerLinkTypeName, final String role1Name, QName portType1,
                                      final String role2Name, QName portType2) {
        portType1 = importNamespace(portType1);
        portType2 = importNamespace(portType2);
        return this.bpelProcessHandler.addPartnerLinkType(partnerLinkTypeName, role1Name, portType1, role2Name,
            portType2, this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a partnerLink to the deployment deskriptor of the BuildPlan
     *
     * @param partnerLinkName the name of the partnerLink
     * @param partnerLinkType the name of the partnerLinkType
     * @return true if adding the partnerLink to the deployment deskriptor was successful, else false
     */
    private boolean addPLtoDeploy(final String partnerLinkName, final String partnerLinkType) {
        final BPELPlan buildPlan = this.templateBuildPlan.getBuildPlan();
        final GenericWsdlWrapper wsdl = buildPlan.getWsdl();

        // get porttypes inside partnerlinktype
        final QName portType1 = wsdl.getPortType1FromPartnerLinkType(partnerLinkType);
        final QName portType2 = wsdl.getPortType2FromPartnerLinkType(partnerLinkType);
        final List<Path> wsdlFiles = getWSDLFiles();
        for (final Path wsdlFile : wsdlFiles) {
            try {
                // TODO: in both if blocks we make huge assumptions with the
                //  get(0)'s, as a wsdl file can have multiple services with
                //  given portTypes

                // if we only have one portType in the partnerLink, we just add
                // a invoke
                if (portType1 != null & portType2 == null && containsPortType(portType1, wsdlFile)) {
                    final List<Service> services = getServicesInWSDLFile(wsdlFile, portType1);
                    final List<Port> ports = this.getPortsFromService(services.get(0), portType1);
                    this.buildPlanHandler.addInvokeToDeploy(partnerLinkName, services.get(0).getQName(),
                        ports.get(0).getName(), buildPlan);
                }

                // if two porttypes are used in this partnerlink, the first
                // portType is used as provided interface, while the second is
                // invoked
                if (portType1 != null & portType2 != null
                    && containsPortType(portType1, wsdlFile) & containsPortType(portType2, wsdlFile)) {
                    // portType1 resembles a service to provide
                    final List<Service> services = getServicesInWSDLFile(wsdlFile, portType1);
                    final List<Port> ports = this.getPortsFromService(services.get(0), portType1);
                    this.buildPlanHandler.addProvideToDeploy(partnerLinkName, services.get(0).getQName(),
                        ports.get(0).getName(), buildPlan);

                    // portType2 resembles a service to invoke
                    final List<Service> outboundServices = getServicesInWSDLFile(wsdlFile, portType2);
                    final List<Port> outboundPorts = this.getPortsFromService(outboundServices.get(0), portType2);
                    this.buildPlanHandler.addInvokeToDeploy(partnerLinkName, outboundServices.get(0).getQName(),
                        outboundPorts.get(0).getName(), buildPlan);
                }
            } catch (final WSDLException e) {
                BPELPlanContext.LOG.error("Error while reading WSDL data", e);
                return false;
            }
        }
        return true;
    }

    /**
     * Adds Property with its Type to the BuildPlan WSDL
     *
     * @param propertyName the name of the Property
     * @param propertyType the XSD Type of the Property
     * @return a QName to be used for References
     */
    public QName addProperty(final String propertyName, final QName propertyType) {
        final QName importedQName = importNamespace(propertyType);
        this.templateBuildPlan.getBuildPlan().getWsdl().addProperty(propertyName, importedQName);
        return importedQName;
    }

    public List<String> getCorrelationProperties() {
        return this.templateBuildPlan.getBuildPlan().getWsdl().getProperties();
    }

    /**
     * Adds a Property Alias for the given Property into the BuildPlan WSDL
     *
     * @param propertyName the name of the property
     * @param messageType  the type of the Message to make an Alias for
     * @param partName     the part name of the Message
     * @param query        the query to the Element inside the Message
     * @return true if adding property alias was successful, else false
     */
    public boolean addPropertyAlias(final String propertyName, final QName messageType, final String partName,
                                    final String query) {
        final QName importedQName = importNamespace(messageType);
        return this.templateBuildPlan.getBuildPlan().getWsdl().addPropertyAlias(propertyName, partName, importedQName,
            query);
    }

    /**
     * Checks whether the given portType is declared in the given WSDL File
     *
     * @param portType the portType to check with
     * @param wsdlFile the WSDL File to check in
     * @return true if the portType is declared in the given WSDL file
     * @throws WSDLException is thrown when either the given File is not a WSDL File or initializing the WSDL Factory
     *                       failed
     */
    public boolean containsPortType(final QName portType, final Path wsdlFile) throws WSDLException {
        final Definition wsdlInstance = this.reader.readWSDL(wsdlFile.toAbsolutePath().toString());
        final Map<?, ?> portTypes = wsdlInstance.getAllPortTypes();
        for (final Object key : portTypes.keySet()) {
            final PortType portTypeInWsdl = (PortType) portTypes.get(key);
            if (portTypeInWsdl.getQName().getNamespaceURI().equals(portType.getNamespaceURI())
                && portTypeInWsdl.getQName().getLocalPart().equals(portType.getLocalPart())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates an element with given namespace and localName for the BuildPlan Document
     *
     * @param namespace the namespace of the element
     * @param localName the localName of the element
     * @return a new Element created with the BuildPlan document
     */
    public Element createElement(final String namespace, final String localName) {
        return this.templateBuildPlan.getBpelDocument().createElementNS(namespace, localName);
    }

    /**
     * Returns the WSDL Ports of the given WSDL Service
     *
     * @param service the WSDL Service
     * @return a List of Port which belong to the service
     */
    private List<Port> getPortsFromService(final Service service) {
        final List<Port> portOfService = new ArrayList<>();
        final Map<?, ?> ports = service.getPorts();
        for (final Object key : ports.keySet()) {
            portOfService.add((Port) ports.get(key));
        }
        return portOfService;
    }

    /**
     * Returns the WSDL Ports of the given WSDL Service, that have binding with the given WSDL PortType
     *
     * @param service  the WSDL Service
     * @param portType the PortType which the Bindings of the Ports implement
     * @return a List of Port which belong to the service and have a Binding with the given PortType
     */
    private List<Port> getPortsFromService(final Service service, final QName portType) {
        final List<Port> ports = this.getPortsFromService(service);
        final List<Port> portsWithPortType = new ArrayList<>();
        for (final Port port : ports) {
            if (port.getBinding().getPortType().getQName().equals(portType)) {
                portsWithPortType.add(port);
            }
        }

        return portsWithPortType;
    }

    /**
     * Returns the PostPhase Element of the TemplateBuildPlan this context belongs to
     *
     * @return a Element which is the PostPhase Element
     */
    public Element getPostPhaseElement() {
        return this.templateBuildPlan.getBpelSequencePostPhaseElement();
    }

    /**
     * Returns the PrePhas Element of the TemplateBuildPlan this context belongs to
     *
     * @return a Element which is the PrePhase Element
     */
    public Element getPrePhaseElement() {
        return this.templateBuildPlan.getBpelSequencePrePhaseElement();
    }

    /**
     * Returns the Services inside the given WSDL file which implement the given portType
     *
     * @param portType the portType to search with
     * @param wsdlFile the WSDL file to look in
     * @return a List of Service which implement the given portType
     * @throws WSDLException is thrown when the WSDLFactory to read the WSDL can't be initialized
     */
    private List<Service> getServicesInWSDLFile(final Path wsdlFile, final QName portType) throws WSDLException {
        final List<Service> servicesInWsdl = new ArrayList<>();

        final Definition wsdlInstance = this.reader.readWSDL(wsdlFile.toAbsolutePath().toString());
        final Map<?, ?> services = wsdlInstance.getAllServices();
        for (final Object key : services.keySet()) {
            final Service service = (Service) services.get(key);
            final Map<?, ?> ports = service.getPorts();
            for (final Object portKey : ports.keySet()) {
                final Port port = (Port) ports.get(portKey);
                if (port.getBinding().getPortType().getQName().getNamespaceURI().equals(portType.getNamespaceURI())
                    && port.getBinding().getPortType().getQName().getLocalPart().equals(portType.getLocalPart())) {
                    servicesInWsdl.add(service);
                }
            }
        }

        return servicesInWsdl;
    }

    /**
     * Returns all files of the BuildPlan which have the ending ".wsdl"
     *
     * @return a List of File which have the ending ".wsdl"
     */
    private List<Path> getWSDLFiles() {
        final List<Path> wsdlFiles = new ArrayList<>();
        final BPELPlan buildPlan = this.templateBuildPlan.getBuildPlan();
        for (final Path file : buildPlan.getImportedFiles()) {
            if (file.getFileName().toString().endsWith(".wsdl")) {
                wsdlFiles.add(file);
            }
        }
        return wsdlFiles;
    }

    /**
     * Imports the given QName Namespace into the BuildPlan
     *
     * @param qname a QName to import
     * @return the QName with set prefix
     */
    public QName importNamespace(final QName qname) {
        return this.bpelProcessHandler.importNamespace(qname, this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Imports the given Node into the BuildPlan Document, to be able to append it to the Phases
     *
     * @param node the Node to import into the Document
     * @return the imported Node
     */
    public Node importNode(final Node node) {
        return this.templateBuildPlan.getBuildPlan().getBpelDocument().importNode(node, true);
    }

    /**
     * Imports the given QName into the BuildPlan
     *
     * @param qname the QName to import
     * @return the imported QName with set prefix
     */
    public QName importQName(final QName qname) {
        return importNamespace(qname);
    }

    /**
     * Registers a portType with the associated WSDL File in the BuildPlan
     *
     * @param portType            the portType to register
     * @param wsdlDefinitionsFile the WSDL file where the portType is declared
     * @return a QName for portType with set prefix etc. after registration within the BuildPlan
     */
    public QName registerPortType(QName portType, final Path wsdlDefinitionsFile) {
        portType = importNamespace(portType);
        boolean check;
        // import wsdl into plan wsdl
        check = this.templateBuildPlan.getBuildPlan().getWsdl()
            .addImportElement("http://schemas.xmlsoap.org/wsdl/", portType.getNamespaceURI(),
                portType.getPrefix(),

                wsdlDefinitionsFile.toAbsolutePath().toString());
        if (!check && this.templateBuildPlan.getBuildPlan().getWsdl()
            .isImported(portType, wsdlDefinitionsFile.toAbsolutePath().toString())) {
            // check if already imported
            check = true;
        }
        // import wsdl into bpel plan
        check &=
            this.buildPlanHandler.addImportToBpel(portType.getNamespaceURI(), wsdlDefinitionsFile.toAbsolutePath().toString(),
                "http://schemas.xmlsoap.org/wsdl/",
                this.templateBuildPlan.getBuildPlan());

        if (!check && this.buildPlanHandler.hasImport(portType.getNamespaceURI(), wsdlDefinitionsFile.toAbsolutePath().toString(),
            "http://schemas.xmlsoap.org/wsdl/",
            this.templateBuildPlan.getBuildPlan())) {
            check = true;
        }

        // add file to imported files of buildplan
        this.buildPlanHandler.addImportedFile(wsdlDefinitionsFile, this.templateBuildPlan.getBuildPlan());
        return check ? portType : null;
    }

    /**
     * Registers XML Schema Types in the given BPEL Plan
     *
     * @param type          QName of the XML Schema Type
     * @param xmlSchemaFile file where the type is declared in
     * @return true if registered type successful, else false
     */
    public boolean registerType(final QName type, final Path xmlSchemaFile) {
        boolean check;
        // add as imported file to plan
        check = this.buildPlanHandler.addImportedFile(xmlSchemaFile, this.templateBuildPlan.getBuildPlan());
        // import type inside bpel file
        check &= this.buildPlanHandler.addImportToBpel(type.getNamespaceURI(), xmlSchemaFile.toAbsolutePath().toString(),
            "http://www.w3.org/2001/XMLSchema",
            this.templateBuildPlan.getBuildPlan());
        return check;
    }

    public void addCorrelationSetToInputReceive(String correlationSetName, Boolean b) {

        BPELPlan plan = this.templateBuildPlan.getBuildPlan();
        Element mainReceiveElement = plan.getBpelMainSequenceReceiveElement();

        Element correlationsElement;
        if (mainReceiveElement.getElementsByTagName("correlations").getLength() != 0) {
            correlationsElement = (Element) mainReceiveElement.getElementsByTagName("correlations").item(0);
        } else {
            correlationsElement = plan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "correlations");
            mainReceiveElement.appendChild(correlationsElement);
        }

        Element correlationElement = plan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "correlation");
        correlationElement.setAttribute("set", correlationSetName);

        if (b != null) {
            if (b) {
                correlationElement.setAttribute("initiate", "yes");
            } else {
                correlationElement.setAttribute("initiate", "no");
            }
        } else {
            correlationElement.setAttribute("initiate", "join");
        }

        correlationsElement.appendChild(correlationElement);

        //
		/*
		 * <bpel:correlations>
                                        <bpel:correlation initiate="yes" set="InvokePortTypeCorrelationSet17"/>
                                    </bpel:correlations>
		 */

    }

    public QName getPlanRequestMessageType() {
        return this.templateBuildPlan.getBuildPlan().getWsdl().getRequestMessageTypeId();
    }
}
