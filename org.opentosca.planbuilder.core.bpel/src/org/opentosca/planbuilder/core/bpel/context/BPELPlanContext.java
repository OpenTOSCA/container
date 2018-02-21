package org.opentosca.planbuilder.core.bpel.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.opentosca.planbuilder.core.bpel.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.OperationChain;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.plan.bpel.GenericWsdlWrapper;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is used for all Plugins. All acitions on TemplateBuildPlans and BuildPlans should be
 * done with the operations of this class. It is basically a Facade to Template and its
 * TemplateBuildPlan
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELPlanContext implements PlanContext {

    private final static Logger LOG = LoggerFactory.getLogger(BPELPlanContext.class);

    private static final String BPEL_PLAN_CONTEXT = "BPEL Plan Context";
    private final BPELScopeActivity templateBuildPlan;
    private final AbstractServiceTemplate serviceTemplate;

    private BPELPlanHandler buildPlanHandler;

    private BPELPlanHandler bpelProcessHandler;

    private final BPELScopeHandler bpelTemplateHandler;

    private final Map<String, String> namespaceMap;
    private final PropertyMap propertyMap;

    private final String planNamespace = "ba.example";

    public List<String> getPropertyNames(final AbstractNodeTemplate nodeTemplate) {
        final List<String> propertyNames = new ArrayList<>();
        final NodeList propertyNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
        for (int index = 0; index < propertyNodes.getLength(); index++) {
            final Node propertyNode = propertyNodes.item(index);
            if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                propertyNames.add(propertyNode.getLocalName());
            }
        }
        return propertyNames;
    }

    public static String getVariableContent(final Variable variable, final BPELPlanContext context) {
        // check whether the property is empty --> external parameter
        for (final AbstractNodeTemplate node : context.getNodeTemplates()) {
            if (node.getId().equals(variable.getTemplateId())) {
                if (node.getProperties() == null) {
                    continue;
                }
                final NodeList children = node.getProperties().getDOMElement().getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    final Node child = children.item(i);
                    if (child.getNodeType() != 1) {
                        continue;
                    }
                    final String variableName = variable.getName();
                    if (variable.getName().endsWith("_" + child.getLocalName())) {
                        // check if content is empty
                        return children.item(i).getTextContent();
                    }
                }
            }
        }

        for (final AbstractRelationshipTemplate relation : context.getRelationshipTemplates()) {
            if (relation.getId().equals(variable.getTemplateId())) {
                final NodeList children = relation.getProperties().getDOMElement().getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    if (variable.getName().endsWith(children.item(i).getLocalName())) {
                        // check if content is empty
                        return children.item(i).getTextContent();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the property of the given variable is empty in the TopologyTemplate
     *
     * @param variable a property variable (var must belong to a topology template property) to check
     * @param context the context the variable belongs to
     * @return true iff the content of the given variable is empty in the topology template property
     */
    public static boolean isVariableValueEmpty(final Variable variable, final BPELPlanContext context) {
        final String content = BPELPlanContext.getVariableContent(variable, context);
        return content == null || content.isEmpty();
    }

    /**
     * Constructor
     *
     * @param templateBuildPlan the TemplateBuildPlan of a Template
     * @param serviceTemplateName the name of the ServiceTemplate where the Template of the context
     *        originates
     * @param map a PropertyMap containing mappings for all Template properties of the TopologyTemplate
     *        the ServiceTemplate has
     */
    public BPELPlanContext(final BPELScopeActivity templateBuildPlan, final PropertyMap map,
                           final AbstractServiceTemplate serviceTemplateId) {
        this.templateBuildPlan = templateBuildPlan;
        this.serviceTemplate = serviceTemplateId;

        try {
            this.buildPlanHandler = new BPELPlanHandler();
            this.bpelProcessHandler = new BPELPlanHandler();
        } catch (final ParserConfigurationException e) {
            BPELPlanContext.LOG.warn("Coulnd't initialize internal handlers", e);
        }
        this.bpelTemplateHandler = new BPELScopeHandler();
        this.namespaceMap = new HashMap<>();
        this.propertyMap = map;
    }

    /**
     * Adds a copy element to the main assign element of the buildPlan this context belongs to
     *
     * @param inputRequestLocalName the localName inside the input request message
     * @param internalVariable an internalVariable of this buildPlan
     * @return true iff adding the copy was successful, else false
     */
    public boolean addAssignFromInput2VariableToMainAssign(final String inputRequestLocalName,
                    final Variable internalVariable) {
        return this.bpelProcessHandler.assignVariableValueFromInput(internalVariable.getName(), inputRequestLocalName,
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a correlationSet with the specified property
     *
     * @param correlationSetName the name for the correlationSet
     * @param propertyName the property to use inside the correlationSet
     * @return true if adding the correlation set was successful, else false
     */
    public boolean addCorrelationSet(final String correlationSetName, final String propertyName) {
        return this.bpelTemplateHandler.addCorrelationSet(correlationSetName, propertyName, this.templateBuildPlan);
    }

    public boolean addGlobalVariable(final String name, final BPELPlan.VariableType variableType, QName declarationId) {
        declarationId = this.importNamespace(declarationId);
        return this.bpelProcessHandler.addVariable(name, variableType, declarationId,
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds the namespace inside the given QName to the buildPlan
     *
     * @param qname a QName with set prefix and namespace
     * @return true if adding the namespace was successful, else false
     */
    private boolean addNamespaceToBPELDoc(final QName qname) {
        return this.bpelProcessHandler.addNamespaceToBPELDoc(qname.getPrefix(), qname.getNamespaceURI(),
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Adds a partnerLink to the TemplateBuildPlan of the Template this context handles
     *
     * @param partnerLinkName the name of the partnerLink
     * @param partnerLinkType the name of the partnerLinkType
     * @param myRole the name of a role inside the partnerLinkType for the myRole
     * @param partnerRole the name of a role inside ther partnerLinkType for the partnerRole
     * @param initializePartnerRole whether the partnerRole should be initialized
     * @return true if adding the partnerLink was successful, else false
     */
    public boolean addPartnerLinkToTemplateScope(final String partnerLinkName, final String partnerLinkType,
                    final String myRole, final String partnerRole, final boolean initializePartnerRole) {
        boolean check = true;
        // here we set the qname with namespace of the plan "ba.example"
        final QName partnerType = new QName(this.planNamespace, partnerLinkType, "tns");
        check &= this.addPLtoDeploy(partnerLinkName, partnerLinkType);
        check &= this.bpelTemplateHandler.addPartnerLink(partnerLinkName, partnerType, myRole, partnerRole,
            initializePartnerRole, this.templateBuildPlan);
        return check;
    }

    /**
     * Adds a partnerlinkType to the BuildPlan, which can be used for partnerLinks in the
     * TemplateBuildPlan
     *
     * @param partnerLinkTypeName the name of the partnerLinkType
     * @param roleName the name of the 1st role
     * @param portType the portType of the partnerLinkType
     * @return true if adding the partnerLinkType was successful, else false
     */
    public boolean addPartnerLinkType(final String partnerLinkTypeName, final String roleName, QName portType) {
        portType = this.importNamespace(portType);
        return this.bpelProcessHandler.addPartnerLinkType(partnerLinkTypeName, roleName, portType,
            this.templateBuildPlan.getBuildPlan());
    }

    public List<Variable> getPropertyVariables(final AbstractNodeTemplate nodeTemplate) {
        final List<Variable> vars = new ArrayList<>();

        final Map<String, String> propMap = this.propertyMap.getPropertyMappingMap(nodeTemplate.getId());

        for (final String localName : propMap.keySet()) {
            final Variable var = this.getPropertyVariable(nodeTemplate, localName);
            if (var != null) {
                vars.add(var);
            }
        }
        return vars;
    }

    /**
     * Adds a partnerLinkType to the BuildPlan which can be used for partnerLinks in TemplateBuildPlans
     *
     * @param partnerLinkTypeName the name of the partnerLinkTypes
     * @param role1Name the name of the 1st role
     * @param portType1 the 1st portType
     * @param role2Name the name of the 2nd role
     * @param portType2 the 2nd porType
     * @return true if adding the partnerLinkType was successful, else false
     */
    public boolean addPartnerLinkType(final String partnerLinkTypeName, final String role1Name, QName portType1,
                    final String role2Name, QName portType2) {
        portType1 = this.importNamespace(portType1);
        portType2 = this.importNamespace(portType2);
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
        // QName portTypeToAdd;
        // if (portType1.getNamespaceURI().equals(this.planNamespace) &&
        // portType1.getLocalPart().equals(this.portName)) {
        // portTypeToAdd = portType2;
        // } else {
        // portTypeToAdd = portType1;
        // }

        // check for port in used wsdl
        // List<File> wsdlFiles = this.getWSDLFiles();
        // for (File wsdlFile : wsdlFiles) {
        // try {
        // if (this.containsPortType(portTypeToAdd, wsdlFile)) {
        // // List<Port> ports =
        // // this.getPortsInWSDLFileForPortType(portTypeToAdd,
        // // wsdlFile);
        // List<Service> services =
        // this.getServiceInWSDLFileForPortType(portTypeToAdd, wsdlFile);
        // List<Port> ports = this.getPortsFromService(services.get(0));
        // this.buildPlanHandler.addInvokeToDeploy(partnerLinkName,
        // services.get(0).getQName(), ports.get(0).getName(), buildPlan);
        // }
        // } catch (WSDLException e) {
        // e.printStackTrace();
        // return false;
        // }
        //
        // }
        final List<File> wsdlFiles = this.getWSDLFiles();
        for (final File wsdlFile : wsdlFiles) {
            try {
                // TODO: in both if blocks we make huge assumptions with the
                // get(0)'s, as a wsdl file can have multiple services with
                // given portTypes

                // if we only have one portType in the partnerLink, we just add
                // a invoke
                if (portType1 != null & portType2 == null && this.containsPortType(portType1, wsdlFile)) {
                    // List<Port> ports =
                    // this.getPortsInWSDLFileForPortType(portTypeToAdd,
                    // wsdlFile);
                    final List<Service> services = this.getServicesInWSDLFile(wsdlFile, portType1);
                    final List<Port> ports = this.getPortsFromService(services.get(0), portType1);
                    this.buildPlanHandler.addInvokeToDeploy(partnerLinkName, services.get(0).getQName(),
                        ports.get(0).getName(), buildPlan);
                }

                // if two porttypes are used in this partnerlink, the first
                // portType is used as provided interface, while the second is
                // invoked
                if (portType1 != null & portType2 != null
                    && this.containsPortType(portType1, wsdlFile) & this.containsPortType(portType2, wsdlFile)) {
                    // portType1 resembles a service to provide
                    final List<Service> services = this.getServicesInWSDLFile(wsdlFile, portType1);
                    final List<Port> ports = this.getPortsFromService(services.get(0), portType1);
                    this.buildPlanHandler.addProvideToDeploy(partnerLinkName, services.get(0).getQName(),
                        ports.get(0).getName(), buildPlan);

                    // portType2 resembles a service to invoke
                    final List<Service> outboundServices = this.getServicesInWSDLFile(wsdlFile, portType2);
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
        final QName importedQName = this.importNamespace(propertyType);
        this.templateBuildPlan.getBuildPlan().getWsdl().addProperty(propertyName, importedQName);
        return importedQName;
    }

    /**
     * Adds a Property Alias for the given Property into the BuildPlan WSDL
     *
     * @param propertyName the name of the property
     * @param messageType the type of the Message to make an Alias for
     * @param partName the part name of the Message
     * @param query the query to the Element inside the Message
     * @return true if adding property alias was successful, else false
     */
    public boolean addPropertyAlias(final String propertyName, final QName messageType, final String partName,
                    final String query) {
        final QName importedQName = this.importNamespace(messageType);
        return this.templateBuildPlan.getBuildPlan().getWsdl().addPropertyAlias(propertyName, partName, importedQName,
            query);
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
     * @param name the name of the variable
     * @param variableType sets if this variable is a Message variable or simple BPEL variable
     * @param declarationId the XSD Type of the variable
     * @return
     */
    public boolean addVariable(final String name, final BPELPlan.VariableType variableType, QName declarationId) {
        declarationId = this.importNamespace(declarationId);
        return this.bpelTemplateHandler.addVariable(name, variableType, declarationId, this.templateBuildPlan);
    }

    /**
     * Appends the given node the the main sequence of the buildPlan this context belongs to
     *
     * @param node a XML DOM Node
     * @return true if adding the node to the main sequence was successfull
     */
    public boolean appendToInitSequence(final Node node) {
        final Node importedNode = this.importNode(node);

        final Element flowElement = this.templateBuildPlan.getBuildPlan().getBpelMainFlowElement();

        final Node mainSequenceNode = flowElement.getParentNode();

        mainSequenceNode.insertBefore(importedNode, flowElement);

        return true;
    }

    /**
     * Checks whether the given portType is declared in the given WSDL File
     *
     * @param portType the portType to check with
     * @param wsdlFile the WSDL File to check in
     * @return true if the portType is declared in the given WSDL file
     * @throws WSDLException is thrown when either the given File is not a WSDL File or initializing the
     *         WSDL Factory failed
     */
    public boolean containsPortType(final QName portType, final File wsdlFile) throws WSDLException {
        final WSDLFactory factory = WSDLFactory.newInstance();
        final WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        final Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
        final Map portTypes = wsdlInstance.getAllPortTypes();
        for (final Object key : portTypes.keySet()) {
            final PortType portTypeInWsdl = (PortType) portTypes.get(key);
            if (portTypeInWsdl.getQName().getNamespaceURI().equals(portType.getNamespaceURI())
                && portTypeInWsdl.getQName().getLocalPart().equals(portType.getLocalPart())) {
                return true;
            }
        }
        return false;
    }

    public BPELPlanContext createContext(final AbstractNodeTemplate nodeTemplate) {
        for (final BPELScopeActivity plan : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
            if (plan.getNodeTemplate() != null && plan.getNodeTemplate().equals(nodeTemplate)) {
                return new BPELPlanContext(plan, this.propertyMap, this.serviceTemplate);
            }
        }
        return null;
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
     * Generates a bpel string variable with the given name + "_" + randomPositiveInt.
     *
     * @param variableName String containing a name
     * @param initVal the value for the variable, if null the value will be empty
     * @return a TemplatePropWrapper containing the generated Id for the variable
     */
    public Variable createGlobalStringVariable(final String variableName, final String initVal) {
        final String varName = variableName + "_" + this.getIdForNames();
        boolean check = this.buildPlanHandler.addPropertyVariable(varName, this.templateBuildPlan.getBuildPlan());
        check &= this.buildPlanHandler.initializePropertyVariable(varName, initVal == null ? "" : initVal,
            this.templateBuildPlan.getBuildPlan());
        if (check) {
            return new Variable(this.getTemplateId(), "prop_" + varName);
        } else {
            return null;
        }
    }

    /**
     * Executes the operation of the given NodeTemplate
     *
     * @param nodeTemplate the NodeTemplate the operation belongs to
     * @param operationName the name of the operation to execute
     * @param param2propertyMapping If a Map of Parameter to Variable is given this will be used for the
     *        operation call
     * @return true if appending logic to execute the operation at runtime was successfull
     */
    public boolean executeOperation(final AbstractNodeTemplate nodeTemplate, final String interfaceName,
                    final String operationName, final Map<AbstractParameter, Variable> param2propertyMapping) {

        final OperationChain chain = BPELScopeBuilder.createOperationCall(nodeTemplate, interfaceName, operationName);
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
        final AbstractRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final AbstractNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = new BPELPlanContext(this.templateBuildPlan, this.propertyMap,
            this.serviceTemplate);

        context.templateBuildPlan.setNodeTemplate(nodeTemplate);
        context.templateBuildPlan.setRelationshipTemplate(null);

        /*
         * chain.executeIAProvisioning(context); chain.executeDAProvisioning(context);
         */
        if (param2propertyMapping == null) {
            chain.executeOperationProvisioning(context, opNames);
        } else {
            chain.executeOperationProvisioning(context, opNames, param2propertyMapping);
        }

        // re-set the orginal configuration of the templateBuildPlan
        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return true;
    }

    public Variable generateVariableWithRandomValue() {
        final String varName = "randomVar" + this.getIdForNames();
        boolean check = this.buildPlanHandler.addPropertyVariable(varName, this.templateBuildPlan.getBuildPlan());
        check &= this.buildPlanHandler.initializePropertyVariable(varName, String.valueOf(System.currentTimeMillis()),
            this.templateBuildPlan.getBuildPlan());
        if (check) {
            return new Variable(this.getTemplateId(), "prop_" + varName);
        } else {
            return null;

        }

    }

    /**
     * Returns all NodeTemplates of the BuildPlan
     *
     * @return a List of AbstractNodeTemplates
     */
    private List<AbstractNodeTemplate> getAllNodeTemplates() {
        final List<AbstractNodeTemplate> list = new ArrayList<>();

        for (final BPELScopeActivity template : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
            if (template.getNodeTemplate() != null) {
                list.add(template.getNodeTemplate());
            }
        }
        return list;
    }

    /**
     * Returns all RelationshipTemplate of the BuildPlan
     *
     * @return a List of AbstractRelationshipTemplates
     */
    private List<AbstractRelationshipTemplate> getAllRelationshipTemplates() {
        final List<AbstractRelationshipTemplate> list = new ArrayList<>();

        for (final BPELScopeActivity template : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
            if (template.getNodeTemplate() == null) {
                list.add(template.getRelationshipTemplate());
            }
        }
        return list;
    }

    /**
     * Returns the TOSCA BaseType of the given RelationshipTemplate
     *
     * @param template an AbstractRelationshipTemplate
     * @return a QName representing the BaseType of the given Template
     */
    public QName getBaseType(final AbstractRelationshipTemplate template) {
        return ModelUtils.getRelationshipBaseType(template);
    }

    /**
     * Returns the file name of the CSAR in which this Template resides
     *
     * @return a String with the file name of the CSAR
     */
    public String getCSARFileName() {
        return this.templateBuildPlan.getBuildPlan().getCsarName();
    }

    /**
     * Returns an absolute File for the given AbstractArtifactReference
     *
     * @param ref an AbstractArtifactReference
     * @return a File with an absolute path to the file
     */
    public File getFileFromArtifactReference(final AbstractArtifactReference ref) {
        return this.templateBuildPlan.getBuildPlan().getDefinitions().getAbsolutePathOfArtifactReference(ref);
    }

    @Override
    public String getId() {
        return BPEL_PLAN_CONTEXT;
    }

    /**
     * Returns an Integer which can be used as variable names etc. So that there are no collisions with
     * other declarations
     *
     * @return an Integer
     */
    public int getIdForNames() {
        final int idToReturn = this.templateBuildPlan.getBuildPlan().getInternalCounterId();
        this.templateBuildPlan.getBuildPlan().setInternalCounterId(idToReturn + 1);
        return idToReturn;
    }

    /**
     * Returns alls InfrastructureEdges of the Template this context belongs to
     *
     * @return a List of AbstractRelationshipTemplate which are InfrastructureEdges of the template this
     *         context handles
     */
    public List<AbstractRelationshipTemplate> getInfrastructureEdges() {
        final List<AbstractRelationshipTemplate> infraEdges = new ArrayList<>();
        if (this.templateBuildPlan.getNodeTemplate() != null) {
            ModelUtils.getInfrastructureEdges(this.getNodeTemplate(), infraEdges);
        } else {
            final AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
            if (ModelUtils.getRelationshipBaseType(template).equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
                ModelUtils.getInfrastructureEdges(template, infraEdges, true);
                ModelUtils.getInfrastructureEdges(template, infraEdges, false);
            } else {
                ModelUtils.getInfrastructureEdges(template, infraEdges, false);
            }
        }
        return infraEdges;
    }

    /**
     * Returns all InfrastructureNodes of the Template this context belongs to
     *
     * @return a List of AbstractNodeTemplate which are InfrastructureNodeTemplate of the template this
     *         context handles
     */
    public List<AbstractNodeTemplate> getInfrastructureNodes() {
        final List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<>();
        if (this.templateBuildPlan.getNodeTemplate() != null) {
            ModelUtils.getInfrastructureNodes(this.getNodeTemplate(), infrastructureNodes);
        } else {
            final AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
            if (ModelUtils.getRelationshipBaseType(template).equals(ModelUtils.TOSCABASETYPE_CONNECTSTO)) {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, true);
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false);
            } else {
                ModelUtils.getInfrastructureNodes(template, infrastructureNodes, false);
            }

        }
        return infrastructureNodes;
    }

    /**
     * Returns all InfrastructureNodes of the Template this context belongs to
     *
     * @param forSource whether to look for InfrastructureNodes along the Source relations or Target
     *        relations
     * @return a List of AbstractNodeTemplate which are InfrastructureNodeTemplate of the template this
     *         context handles
     */
    public List<AbstractNodeTemplate> getInfrastructureNodes(final boolean forSource) {
        final List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<>();
        if (this.templateBuildPlan.getNodeTemplate() != null) {
            ModelUtils.getInfrastructureNodes(this.getNodeTemplate(), infrastructureNodes);
        } else {
            final AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
            ModelUtils.getInfrastructureNodes(template, infrastructureNodes, forSource);
        }
        return infrastructureNodes;
    }

    /**
     * Returns the localNames defined inside the input message of the buildPlan this context belongs to
     *
     * @return a List of Strings representing the XML localNames of the elements inside the input
     *         message of the buildPlan this context belongs to
     */
    public List<String> getInputMessageElementNames() {
        return this.templateBuildPlan.getBuildPlan().getWsdl().getInputMessageLocalNames();
    }

    /**
     * Returns a Map with ToscaParameter Names as Key and a Wrapper for
     * (TemplateId,PropertyVariableName) as Value.
     *
     * The Parameters to Property Mapping is calculated on all Infrastructure Element (Nodes, Edges) of
     * the Template this context belongs to.
     *
     * @param toscaParameters Set of Parameter Names for which the list of internal properties or
     *        external properties should be calculated
     * @return Map<String, TemplatePropWrapper> if a value is null, it indicates that the parameter is
     *         external. The Key is a TOSCA Operation Parameters and value is a Mapping from TemplateId
     *         to Property Variable name
     */
    // this method is to mighty -> non-deterministic
    @Deprecated
    public Map<String, Variable> getInternalExternalParameters(final Set<String> toscaParameters) {
        // initialize hashmap to save found matches
        final Map<String, Variable> matchMap = new HashMap<>();
        for (final String param : toscaParameters) {
            matchMap.put(param, null);
        }

        // check for properties inside context template
        String id;
        NodeList TemplateChilde = null;
        if (this.getNodeTemplate() != null && this.getNodeTemplate().getProperties() != null
            && this.getNodeTemplate().getProperties().getDOMElement() != null) {
            id = this.getNodeTemplate().getId();
            TemplateChilde = this.getNodeTemplate().getProperties().getDOMElement().getChildNodes();
        } else if (this.getRelationshipTemplate() != null && this.getRelationshipTemplate().getProperties() != null
            && this.getRelationshipTemplate().getProperties().getDOMElement() != null) {
            id = this.getRelationshipTemplate().getId();
            TemplateChilde = this.getRelationshipTemplate().getProperties().getDOMElement().getChildNodes();
        }
        if (TemplateChilde != null) {
            for (int index = 0; index < TemplateChilde.getLength(); index++) {
                final Node nodeTemplateProp = TemplateChilde.item(index);
                if (matchMap.containsKey(nodeTemplateProp.getLocalName())
                    && matchMap.get(nodeTemplateProp.getLocalName()) == null) {
                    matchMap.put(nodeTemplateProp.getLocalName(), new Variable(this.getNodeTemplate().getId(),
                        this.getVarNameOfTemplateProperty(nodeTemplateProp.getLocalName())));
                }
            }
        }

        // check for matches first on the infrastructure
        for (final AbstractNodeTemplate infraNode : this.getInfrastructureNodes()) {
            if (infraNode.getProperties() == null || infraNode.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraNodeProp = infraNodePropChilde.item(index);
                if (matchMap.containsKey(infraNodeProp.getLocalName())
                    && matchMap.get(infraNodeProp.getLocalName()) == null) {
                    matchMap.put(infraNodeProp.getLocalName(), new Variable(infraNode.getId(),
                        this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName())));
                }
            }
        }

        for (final AbstractRelationshipTemplate infraEdge : this.getInfrastructureEdges()) {
            if (infraEdge.getProperties() == null || infraEdge.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraEdgeProp = infraNodePropChilde.item(index);
                if (matchMap.containsKey(infraEdgeProp.getLocalName())
                    && matchMap.get(infraEdgeProp.getLocalName()) == null) {
                    matchMap.put(infraEdgeProp.getLocalName(), new Variable(infraEdge.getId(),
                        this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName())));
                }
            }
        }

        // then on everything else
        for (final AbstractNodeTemplate infraNode : this.getAllNodeTemplates()) {
            if (infraNode.getProperties() == null || infraNode.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraNodeProp = infraNodePropChilde.item(index);
                if (matchMap.containsKey(infraNodeProp.getLocalName())
                    && matchMap.get(infraNodeProp.getLocalName()) == null) {
                    matchMap.put(infraNodeProp.getLocalName(), new Variable(infraNode.getId(),
                        this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName())));
                }
            }
        }

        for (final AbstractRelationshipTemplate infraEdge : this.getAllRelationshipTemplates()) {
            if (infraEdge.getProperties() == null || infraEdge.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraEdgeProp = infraNodePropChilde.item(index);
                if (matchMap.containsKey(infraEdgeProp.getLocalName())
                    && matchMap.get(infraEdgeProp.getLocalName()) == null) {
                    matchMap.put(infraEdgeProp.getLocalName(), new Variable(infraEdge.getId(),
                        this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName())));
                }
            }
        }

        return matchMap;
    }

    /**
     * Returns the names of the global variables defined in the buildPlan this context belongs to
     *
     * @return a List of Strings representing the global variable names
     */
    public List<String> getMainVariableNames() {
        return this.bpelProcessHandler.getMainVariableNames(this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Returns a NCName String of the given String
     *
     * @param string a String to convert
     * @return the String which is a NCName
     */
    public String getNCNameFromString(final String string) {
        // TODO check if this enough
        return string.replace(" ", "_");
    }

    public boolean executeOperation(final AbstractNodeTemplate nodeTemplate, final String interfaceName,
                    final String operationName, final Map<AbstractParameter, Variable> param2propertyMapping,
                    final Map<AbstractParameter, Variable> param2propertyOutputMapping,
                    final boolean appendToPrePhase) {
        final OperationChain chain = BPELScopeBuilder.createOperationCall(nodeTemplate, interfaceName, operationName);
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
        final AbstractRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final AbstractNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = new BPELPlanContext(this.templateBuildPlan, this.propertyMap,
            this.serviceTemplate);

        context.templateBuildPlan.setNodeTemplate(nodeTemplate);
        context.templateBuildPlan.setRelationshipTemplate(null);

        /*
         * chain.executeIAProvisioning(context); chain.executeDAProvisioning(context);
         */
        if (param2propertyMapping == null) {
            chain.executeOperationProvisioning(context, opNames);
        } else {
            if (param2propertyOutputMapping == null) {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping, appendToPrePhase);
            } else {
                chain.executeOperationProvisioning(context, opNames, param2propertyMapping, param2propertyOutputMapping,
                    appendToPrePhase);
            }
        }

        // re-set the orginal configuration of the templateBuildPlan
        this.templateBuildPlan.setNodeTemplate(nodeBackup);
        this.templateBuildPlan.setRelationshipTemplate(relationBackup);

        return true;

    }


    public boolean executeOperation(final AbstractNodeTemplate nodeTemplate, final String interfaceName,
                    final String operationName, final Map<AbstractParameter, Variable> param2propertyMapping,
                    final Map<AbstractParameter, Variable> param2propertyOutputMapping) {

        final OperationChain chain = BPELScopeBuilder.createOperationCall(nodeTemplate, interfaceName, operationName);
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
        final AbstractRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
        final AbstractNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

        // create context from this context and set the given nodeTemplate as
        // the node for the scope
        final BPELPlanContext context = new BPELPlanContext(this.templateBuildPlan, this.propertyMap,
            this.serviceTemplate);

        context.templateBuildPlan.setNodeTemplate(nodeTemplate);
        context.templateBuildPlan.setRelationshipTemplate(null);

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
     * @return an AbstractNodeTemplate if this BPELPlanContext handles a NodeTemplate, else null
     */
    public AbstractNodeTemplate getNodeTemplate() {
        return this.templateBuildPlan.getNodeTemplate();
    }

    /**
     * <p>
     * Returns all NodeTemplates that are part of the ServiceTemplate this context belongs to.
     * </p>
     *
     * @return a List of AbstractNodeTemplate
     */
    public List<AbstractNodeTemplate> getNodeTemplates() {
        // find the serviceTemplate
        return this.templateBuildPlan.getBuildPlan().getServiceTemplate().getTopologyTemplate().getNodeTemplates();
    }

    /**
     * Returns the name of variable which is the input message of the buildPlan
     *
     * @return a String containing the variable name of the inputmessage of the BuildPlan
     */
    public String getPlanRequestMessageName() {
        return "input";
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
     * Returns the plan type of this context
     *
     * @return a TOSCAPlan.PlanType
     */
    public BPELPlan.PlanType getPlanType() {
        return this.templateBuildPlan.getBuildPlan().getType();
    }

    /**
     * Returns the WSDL Ports of the given WSDL Service
     *
     * @param service the WSDL Service
     * @return a List of Port which belong to the service
     */
    private List<Port> getPortsFromService(final Service service) {
        final List<Port> portOfService = new ArrayList<>();
        final Map ports = service.getPorts();
        for (final Object key : ports.keySet()) {
            portOfService.add((Port) ports.get(key));
        }
        return portOfService;
    }

    /**
     * Returns the WSDL Ports of the given WSDL Service, that have binding with the given WSDL PortType
     *
     * @param service the WSDL Service
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
     * Returns a List of Port which implement the given portType inside the given WSDL File
     *
     * @param portType the portType to use
     * @param wsdlFile the WSDL File to look in
     * @return a List of Port which implement the given PortType
     * @throws WSDLException is thrown when the given File is not a WSDL File or initializing the WSDL
     *         Factory failed
     */
    public List<Port> getPortsInWSDLFileForPortType(final QName portType, final File wsdlFile) throws WSDLException {
        final List<Port> wsdlPorts = new ArrayList<>();
        // taken from http://www.java.happycodings.com/Other/code24.html
        final WSDLFactory factory = WSDLFactory.newInstance();
        final WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        final Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
        final Map services = wsdlInstance.getAllServices();
        for (final Object key : services.keySet()) {
            final Service service = (Service) services.get(key);
            final Map ports = service.getPorts();
            for (final Object portKey : ports.keySet()) {
                final Port port = (Port) ports.get(portKey);
                if (port.getBinding().getPortType().getQName().getNamespaceURI().equals(portType.getNamespaceURI())
                    && port.getBinding().getPortType().getQName().getLocalPart().equals(portType.getLocalPart())) {
                    wsdlPorts.add(port);
                }
            }
        }
        return wsdlPorts;
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
     * Returns a prefix for the given namespace if it is declared in the buildPlan
     *
     * @param namespace the namespace to get the prefix for
     * @return a String containing the prefix, else null
     */
    public String getPrefixForNamespace(final String namespace) {
        if (this.namespaceMap.containsValue(namespace)) {
            for (final String key : this.namespaceMap.keySet()) {
                if (this.namespaceMap.get(key).equals(namespace)) {
                    return key;
                }
            }
        }
        return null;
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
     * Returns a Variable object that represents a property inside the given nodeTemplate with the given
     * name
     *
     * @param nodeTemplate a nodeTemplate to look for the property in
     * @param localName the name of the searched property
     * @return a Variable object representing the property
     */
    public Variable getPropertyVariable(final AbstractNodeTemplate nodeTemplate, final String localName) {
        if (nodeTemplate.getProperties() == null || nodeTemplate.getProperties().getDOMElement() == null) {
            return null;
        }

        final NodeList propertyNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
        for (int index = 0; index < propertyNodes.getLength(); index++) {
            final Node propertyNode = propertyNodes.item(index);
            if (propertyNode.getNodeType() == Node.ELEMENT_NODE && propertyNode.getLocalName().equals(localName)) {
                return new Variable(nodeTemplate.getId(),
                    this.getVariableNameOfProperty(nodeTemplate.getId(), propertyNode.getLocalName()));
            }
        }

        return null;
    }

    public Variable getPropertyVariable(final AbstractRelationshipTemplate relationshipTemplate,
                    final String propertyName) {
        if (relationshipTemplate.getProperties() == null
            || relationshipTemplate.getProperties().getDOMElement() == null) {
            return null;
        }

        final NodeList propertyNodes = relationshipTemplate.getProperties().getDOMElement().getChildNodes();
        for (int index = 0; index < propertyNodes.getLength(); index++) {
            final Node propertyNode = propertyNodes.item(index);
            if (propertyNode.getNodeType() == Node.ELEMENT_NODE && propertyNode.getLocalName().equals(propertyName)) {
                return new Variable(relationshipTemplate.getId(),
                    this.getVariableNameOfProperty(relationshipTemplate.getId(), propertyNode.getLocalName()));

            }
        }
        return null;
    }

    /**
     * Looks for a Property with the same localName as the given toscaParameter. The search is on the
     * whole TopologyTemplate this TemplateContext belongs to.
     *
     * @param localName a String
     * @return a Variable Object with TemplateId and Name, if null the whole Topology has no Property
     *         with the specified localName
     */
    public Variable getPropertyVariable(final String localName) {
        // then on everything else
        for (final AbstractNodeTemplate infraNode : this.getAllNodeTemplates()) {
            if (infraNode.getProperties() == null || infraNode.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraNodeProp = infraNodePropChilde.item(index);
                if (localName.equals(infraNodeProp.getLocalName())) {
                    return new Variable(infraNode.getId(),
                        this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName()));
                }
            }
        }

        for (final AbstractRelationshipTemplate infraEdge : this.getAllRelationshipTemplates()) {
            if (infraEdge.getProperties() == null || infraEdge.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraEdgeProp = infraNodePropChilde.item(index);
                if (localName.equals(infraEdgeProp.getLocalName())) {
                    return new Variable(infraEdge.getId(),
                        this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName()));
                }
            }
        }

        return null;
    }

    /**
     *
     * Looks for a Property with the same localName as the given String. The search is on either the
     * Infrastructure on the Source or Target of the Template this TemplateContext belongs to.
     *
     * @param localName a String
     * @param forSource whether to look in direction of the sinks or sources (If Template is
     *        NodeTemplate) or to search on the Source-/Target-Interface (if template is
     *        RelationshipTemplate)
     * @return a Variable Object with TemplateId and Name, if null the whole Infrastructure has no
     *         Property with the specified localName
     */
    public Variable getPropertyVariable(final String localName, final boolean forSource) {
        final List<AbstractNodeTemplate> infraNodes = new ArrayList<>();

        if (this.isNodeTemplate()) {
            if (forSource) {
                // get all NodeTemplates that are reachable from this
                // nodeTemplate
                ModelUtils.getNodesFromNodeToSink(this.getNodeTemplate(), infraNodes);
            } else {
                ModelUtils.getNodesFromNodeToSource(this.getNodeTemplate(), infraNodes);
            }
        } else {
            if (forSource) {
                ModelUtils.getNodesFromNodeToSink(this.getRelationshipTemplate().getSource(), infraNodes);
            } else {
                ModelUtils.getNodesFromRelationToSink(this.getRelationshipTemplate(), infraNodes);
            }
        }

        for (final AbstractNodeTemplate infraNode : infraNodes) {
            if (infraNode.getProperties() == null || infraNode.getProperties().getDOMElement() == null) {
                continue;
            }
            final NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
            for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
                final Node infraNodeProp = infraNodePropChilde.item(index);
                if (localName.equals(infraNodeProp.getLocalName())) {
                    return new Variable(infraNode.getId(),
                        this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName()));
                }
            }
        }
        return null;
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
     * Returns the RelationshipTemplate this context handles
     *
     * @return an AbstractRelationshipTemplate if this context handle a RelationshipTemplate, else null
     */
    public AbstractRelationshipTemplate getRelationshipTemplate() {
        return this.templateBuildPlan.getRelationshipTemplate();
    }

    /**
     * <p>
     * Returns all RelationshipTemplates that are part of the ServiceTemplate this context belongs to.
     * </p>
     *
     * @return a List of AbstractRelationshipTemplate
     */
    public List<AbstractRelationshipTemplate> getRelationshipTemplates() {
        for (final AbstractServiceTemplate serviceTemplate : this.templateBuildPlan.getBuildPlan().getDefinitions()
                                                                                   .getServiceTemplates()) {
            if (serviceTemplate.getQName().equals(this.serviceTemplate)) {
                return serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
            }
        }
        return null;
    }

    /**
     * Returns the Services inside the given WSDL file which implement the given portType
     *
     * @param portType the portType to search with
     * @param wsdlFile the WSDL file to look in
     * @return a List of Service which implement the given portType
     * @throws WSDLException is thrown when the WSDLFactory to read the WSDL can't be initialized
     */
    private List<Service> getServicesInWSDLFile(final File wsdlFile, final QName portType) throws WSDLException {
        final List<Service> servicesInWsdl = new ArrayList<>();

        final WSDLFactory factory = WSDLFactory.newInstance();
        final WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        final Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
        final Map services = wsdlInstance.getAllServices();
        for (final Object key : services.keySet()) {
            final Service service = (Service) services.get(key);
            final Map ports = service.getPorts();
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

    public QName getServiceTemplateId() {
        return this.serviceTemplate.getQName();
    }

    /**
     * Returns the name of the TemplateBuildPlan this BPELPlanContext belongs to
     *
     * @return a String containing a Name for the TemplateBuildPlan consisting of the Id of the
     *         NodeTemplate processed in that plan
     */
    public String getTemplateBuildPlanName() {
        return this.templateBuildPlan.getBpelScopeElement().getAttribute("name");
    }

    public String getTemplateId() {
        if (this.getNodeTemplate() != null) {
            return this.getNodeTemplate().getId();
        } else {
            return this.getRelationshipTemplate().getId();
        }

    }

    /**
     * Returns the variable name of the first occurence of a property with the given Property name of
     * InfrastructureNodes
     *
     * @param propertyName
     * @return a String containing the variable name, else null
     */
    public String getVariableNameOfInfraNodeProperty(final String propertyName) {
        for (final AbstractNodeTemplate infraNode : this.getInfrastructureNodes()) {
            if (this.propertyMap.getPropertyMappingMap(infraNode.getId()) != null
                && this.propertyMap.getPropertyMappingMap(infraNode.getId()).containsKey(propertyName)) {
                return this.propertyMap.getPropertyMappingMap(infraNode.getId()).get(propertyName);
            }
        }
        return null;
    }

    /**
     * Returns the variable name of the given template and property localName
     *
     * @param templateId the Id of the Template to look in
     * @param propertyName the LocalName of a Template Property
     * @return a String containing the variable name, else null
     */
    public String getVariableNameOfProperty(final String templateId, final String propertyName) {
        if (this.propertyMap.getPropertyMappingMap(templateId) != null) {
            return this.propertyMap.getPropertyMappingMap(templateId).get(propertyName);
        } else {
            return null;
        }
    }

    /**
     * Returns the name of variable which the given property name belongs to
     *
     * @param propertyName a LocalName of a Property of the Template this context belongs to
     * @return a String containing the variable name of the property, else null
     */
    public String getVarNameOfTemplateProperty(final String propertyName) {
        Map<String, String> propertyMapping = null;
        if (this.templateBuildPlan.getNodeTemplate() != null) {
            propertyMapping = this.propertyMap.getPropertyMappingMap(this.templateBuildPlan.getNodeTemplate().getId());
        } else {
            propertyMapping = this.propertyMap.getPropertyMappingMap(
                this.templateBuildPlan.getRelationshipTemplate().getId());
        }
        return propertyMapping.get(propertyName);
    }

    /**
     * Returns all files of the BuildPlan which have the ending ".wsdl"
     *
     * @return a List of File which have the ending ".wsdl"
     */
    private List<File> getWSDLFiles() {
        final List<File> wsdlFiles = new ArrayList<>();
        final BPELPlan buildPlan = this.templateBuildPlan.getBuildPlan();
        for (final File file : buildPlan.getImportedFiles()) {
            if (file.getName().endsWith(".wsdl")) {
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
    private QName importNamespace(final QName qname) {
        String prefix = qname.getPrefix();
        final String namespace = qname.getNamespaceURI();
        boolean prefixInUse = false;
        boolean namespaceInUse = false;

        // check if prefix is in use
        if (prefix != null && !prefix.isEmpty()) {
            prefixInUse = this.namespaceMap.containsKey(prefix);
        }

        // check if namespace is in use
        if (namespace != null && !namespace.isEmpty()) {
            namespaceInUse = this.namespaceMap.containsValue(namespace);
        }

        // TODO refactor this whole thing
        if (prefixInUse & namespaceInUse) {
            // both is already registered, this means we set the prefix of the
            // given qname to the prefix used in the system
            for (final String key : this.namespaceMap.keySet()) {
                if (this.namespaceMap.get(key).equals(namespace)) {
                    prefix = key;
                }
            }
        } else if (!prefixInUse & namespaceInUse) {
            // the prefix isn't in use, but the namespace is, re-set the prefix
            for (final String key : this.namespaceMap.keySet()) {
                if (this.namespaceMap.get(key).equals(namespace)) {
                    prefix = key;
                }
            }
        } else if (!prefixInUse & !namespaceInUse) {
            // just add the namespace and prefix to the system
            if (prefix == null || prefix.isEmpty()) {
                // generate new prefix
                prefix = "ns" + this.namespaceMap.keySet().size();
            }
            this.namespaceMap.put(prefix, namespace);
            this.addNamespaceToBPELDoc(new QName(namespace, qname.getLocalPart(), prefix));

        } else {
            if (prefix == null || prefix.isEmpty()) {
                // generate new prefix
                prefix = "ns" + this.namespaceMap.keySet().size();
            }
            this.namespaceMap.put(prefix, namespace);
            this.addNamespaceToBPELDoc(new QName(namespace, qname.getLocalPart(), prefix));
        }
        return new QName(namespace, qname.getLocalPart(), prefix);
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
        return this.importNamespace(qname);
    }

    /**
     * Returns whether this context is for a nodeTemplate
     *
     * @return true if this context is for a nodeTemplate, else false
     */
    public boolean isNodeTemplate() {
        return this.templateBuildPlan.getNodeTemplate() != null ? true : false;
    }

    /**
     * Returns whether this context is for a relationshipTemplate
     *
     * @return true if this context is for a relationshipTemplate, else false
     */
    public boolean isRelationshipTemplate() {
        return this.templateBuildPlan.getRelationshipTemplate() != null ? true : false;
    }

    /**
     * Registers the given namespace as extension inside the BuildPlan
     *
     * @param namespace the namespace of the extension
     * @param mustUnderstand the mustUnderstand attribute
     * @return true if adding was successful, else false
     */
    public boolean registerExtension(final String namespace, final boolean mustUnderstand) {
        return this.buildPlanHandler.registerExtension(namespace, mustUnderstand,
            this.templateBuildPlan.getBuildPlan());
    }

    /**
     * Registers a portType which is declared inside the given AbstractArtifactReference
     *
     * @param portType the portType to register
     * @param ref ArtifactReference where the portType is declared
     * @return a QName for the registered PortType with a set prefix
     */
    public QName registerPortType(final QName portType, final AbstractArtifactReference ref) {
        return this.registerPortType(portType,
            this.templateBuildPlan.getBuildPlan().getDefinitions().getAbsolutePathOfArtifactReference(ref));
    }

    /**
     * Registers a portType with the associated WSDL File in the BuildPlan
     *
     * @param portType the portType to register
     * @param wsdlDefinitionsFile the WSDL file where the portType is declared
     * @return a QName for portType with set prefix etc. after registration within the BuildPlan
     */
    public QName registerPortType(QName portType, final File wsdlDefinitionsFile) {
        portType = this.importNamespace(portType);
        boolean check = true;
        // import wsdl into plan wsdl
        check &= this.templateBuildPlan.getBuildPlan().getWsdl().addImportElement("http://schemas.xmlsoap.org/wsdl/",
            portType.getNamespaceURI(), portType.getPrefix(),

            wsdlDefinitionsFile.getAbsolutePath());
        if (!check && this.templateBuildPlan.getBuildPlan().getWsdl().isImported(portType,
            wsdlDefinitionsFile.getAbsolutePath())) {
            // check if already imported
            check = true;
        }
        // import wsdl into bpel plan
        check &= this.buildPlanHandler.addImportToBpel(portType.getNamespaceURI(),
            wsdlDefinitionsFile.getAbsolutePath(), "http://schemas.xmlsoap.org/wsdl/",
            this.templateBuildPlan.getBuildPlan());

        if (!check && this.buildPlanHandler.hasImport(portType.getNamespaceURI(), wsdlDefinitionsFile.getAbsolutePath(),
            "http://schemas.xmlsoap.org/wsdl/", this.templateBuildPlan.getBuildPlan())) {
            check = true;
        }

        // add file to imported files of buildplan
        this.buildPlanHandler.addImportedFile(wsdlDefinitionsFile, this.templateBuildPlan.getBuildPlan());
        return check ? portType : null;
    }

    /**
     * Registers XML Schema Types in the given BPEL Plan
     *
     * @param type QName of the XML Schema Type
     * @param xmlSchemaFile file where the type is declared in
     * @return true if registered type successful, else false
     */
    public boolean registerType(final QName type, final File xmlSchemaFile) {
        boolean check = true;
        // add as imported file to plan
        check &= this.buildPlanHandler.addImportedFile(xmlSchemaFile, this.templateBuildPlan.getBuildPlan());
        // import type inside bpel file
        check &= this.buildPlanHandler.addImportToBpel(type.getNamespaceURI(), xmlSchemaFile.getAbsolutePath(),
            "http://www.w3.org/2001/XMLSchema", this.templateBuildPlan.getBuildPlan());
        return true;
    }
}
