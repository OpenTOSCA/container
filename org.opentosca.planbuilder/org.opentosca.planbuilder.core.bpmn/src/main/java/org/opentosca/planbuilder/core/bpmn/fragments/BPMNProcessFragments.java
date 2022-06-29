package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class takes the corresponding snippets and replace it with the correct content.
 */
@Component
public class BPMNProcessFragments {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNProcessFragments.class);

    private final DocumentBuilder docBuilder;
    protected static final String ServiceInstanceURLVarKeyword = "ServiceInstanceURL";

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPMNProcessFragments() throws ParserConfigurationException {
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public Node transformStringToNode(String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createScript(String scriptName) throws IOException {
        return ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("scripts/" + scriptName + ".groovy"));
    }

    /**
     * creates relationshipTemplateInstance task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createRelationshipTemplateInstance Node
     */
    public String createRelationshipTemplateInstance(BPMNSubprocess bpmnSubprocess) throws IOException {
        String relationshipTemplateId = bpmnSubprocess.getRelationshipTemplate().getId();
        String createRelationshipInstance = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateRelationshipTemplateInstanceScriptTask.xml"));
        createRelationshipInstance = createRelationshipInstance.replaceAll("RelationshipTemplate_IdToReplace", bpmnSubprocess.getId());
        createRelationshipInstance = createRelationshipInstance.replaceAll("StateToSet", "INITIAL");
        createRelationshipInstance = createRelationshipInstance.replaceAll("RelationshipTemplateToSet", bpmnSubprocess.getRelationshipTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");
        createRelationshipInstance = createRelationshipInstance.replaceAll("ResultVariableToSet", parentId);
        createRelationshipInstance = createRelationshipInstance.replaceAll("RelationshipTemplateToSet", relationshipTemplateId);
        createRelationshipInstance = createRelationshipInstance.replaceAll("SourceURLToSet", bpmnSubprocess.getSourceInstanceURL());
        createRelationshipInstance = createRelationshipInstance.replaceAll("TargetURLToSet", bpmnSubprocess.getTargetInstanceURL());
        createRelationshipInstance = getServiceInstanceURLFromDataObject(bpmnSubprocess, createRelationshipInstance);
        return createRelationshipInstance;
    }

    /**
     * creates NodeTemplateInstance task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createNodeTemplateInstance Node
     */
    public String createNodeTemplateInstance(BPMNSubprocess bpmnSubprocess) throws IOException {
        String nodeTemplateId = bpmnSubprocess.getNodeTemplate().getId();
        String createNodeInstance = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeTemplateInstanceScriptTask.xml"));
        createNodeInstance = createNodeInstance.replaceAll("NodeTemplateInstance_IdToReplace", bpmnSubprocess.getId());
        createNodeInstance = createNodeInstance.replaceAll("NodeTemplateToSet", bpmnSubprocess.getNodeTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");
        createNodeInstance = createNodeInstance.replaceAll("ResultVariableToSet", parentId);
        createNodeInstance = createNodeInstance.replaceAll("NodeTemplateToSet", nodeTemplateId);
        createNodeInstance = createNodeInstance.replaceAll("StateToSet", "INITIAL");
        createNodeInstance = getServiceInstanceURLFromDataObject(bpmnSubprocess, createNodeInstance);
        return createNodeInstance;
    }

    /**
     * creates NodeOperation task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createNodeOperation Node
     */
    public String createNodeOperation(BPMNSubprocess bpmnSubprocess) throws IOException {
        BPMNDataObject dataObject = bpmnSubprocess.getParentProcess().getDataObject();
        String callNodeOperation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeOperationScriptTask.xml"));
        callNodeOperation = callNodeOperation.replaceAll("CallNodeOperation_IdToReplace", bpmnSubprocess.getId());
        callNodeOperation = callNodeOperation.replaceAll("CsarToSet", bpmnSubprocess.getBuildPlan().getCsarName());
        callNodeOperation = callNodeOperation.replaceAll("NodeTemplateToSet", bpmnSubprocess.getHostingNodeTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId();
        String prefix = BPMNSubprocessType.DATA_OBJECT_REFERENCE + "_" + BPMNSubprocessType.DATA_OBJECT;
        String dataObjectReferenceId = parentId.replace("Subprocess", prefix);
        callNodeOperation = callNodeOperation.replaceAll("DataObjectToSet", dataObjectReferenceId);
        callNodeOperation = getServiceInstanceURLFromDataObject(bpmnSubprocess, callNodeOperation);
        LOG.info("interface variable: " + bpmnSubprocess.getInterfaceVariable());
        if (bpmnSubprocess.getInterfaceVariable() != null) {
            callNodeOperation = callNodeOperation.replaceAll("InterfaceToSet", bpmnSubprocess.getInterfaceVariable());
        }
        LOG.info("Operation variable: " + bpmnSubprocess.getOperation());
        if (bpmnSubprocess.getOperation() != null) {
            callNodeOperation = callNodeOperation.replaceAll("OperationToSet", bpmnSubprocess.getOperation());
        }
        LOG.info("INPUTNUMAEA:" + bpmnSubprocess.getInputParameterNames());
        if (bpmnSubprocess.getInputParameterNames() != null) {
            callNodeOperation = callNodeOperation.replace("InputParamNamesToSet", bpmnSubprocess.getInputParameterNames());
        }
        LOG.info("INPUTNUMAEA2:" + bpmnSubprocess.getInputParameterValues());
        if (bpmnSubprocess.getInputParameterValues() != null) {
            callNodeOperation = callNodeOperation.replace("InputParamValuesToSet", bpmnSubprocess.getInputParameterValues());
        }
        LOG.info("INPUTNUMAEA3:" + bpmnSubprocess.getOutputParameterNames());
        if (bpmnSubprocess.getOutputParameterNames() != null) {
            callNodeOperation = callNodeOperation.replaceAll("OutputParamNamesToSet", bpmnSubprocess.getOutputParameterNames());
        }
        if (bpmnSubprocess.getOutputParameterValues() != null) {
            callNodeOperation = callNodeOperation.replaceAll("OutputParamValuesToSet", bpmnSubprocess.getOutputParameterValues());
        }

        String[] original = callNodeOperation.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];

        StringBuilder inputParameterBuilder = new StringBuilder();
        if ((bpmnSubprocess.getInputParameterNames() != null) && (bpmnSubprocess.getInputParameterValues() != null)) {
            int counter = 0;
            for (String inputParameterName : bpmnSubprocess.getInputParameterNames().split(",")) {
                String inputParameterValue = bpmnSubprocess.getInputParameterValues().split(",")[counter];
                if (inputParameterName.equals("DockerEngineURL")) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append("String!${DockerEngineURL}").append("</camunda:inputParameter>");
                } else if (inputParameterName.equals("ContainerPorts")) {
                    LOG.info("CONTainerPorts");
                    LOG.info(inputParameterValue);
                    String containerPortValue = "";
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append("String!80->9990;").append("</camunda:inputParameter>");
                } else if (inputParameterName.equals("ImageLocation") && (bpmnSubprocess.getParentProcess().getDeploymentArtifactString() != null)) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append(bpmnSubprocess.getParentProcess().getDeploymentArtifactString()).append("</camunda:inputParameter>");
                } else {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append(inputParameterValue).append("</camunda:inputParameter>");
                }
                counter++;
            }
        }

        //rebuild the template String

        return original[0] + inputParameterBuilder + original[1];
    }

    public String getServiceInstanceURLFromDataObject(BPMNSubprocess bpmnSubprocess, String template) {
        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_ST) {
                template = template.replaceAll("ServiceInstanceURLToSet", bpmnDataObject.getServiceInstanceURL());
            } else if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT) {
                template = template.replaceAll("ServiceInstanceURLToSet", bpmnDataObject.getProperties().stream().filter(property -> property.startsWith(ServiceInstanceURLVarKeyword)).toString());
            }
        }
        return template;
    }

    /**
     * create bpmn user task node
     *
     * @param bpmnSubprocess the subprocess
     * @return bpmn userTask node
     */
    public Node createBPMNUserTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String userTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNUserTask.xml"));
        userTask = userTask.replaceAll("Task_IdToSet", bpmnSubprocess.getId());
        return this.createImportNodeFromString(bpmnSubprocess, userTask);
    }

    /**
     * This method is necessary to prevent the error: import node from different document.
     */
    public void addNodeToBPMN(Node nodeToImport, BPMNPlan bpmnPlan) {
        nodeToImport = bpmnPlan.getBpmnDocument().importNode(nodeToImport, true);
        bpmnPlan.getBpmnProcessElement().appendChild(nodeToImport);
    }
    // --------------------------
    // Data Objects Begin

    /**
     * Creates a relationship data object which consists of the source & target url.
     */
    public String createRelationDataObjectReference(BPMNDataObject dataObject) throws IOException {
        String relationshipDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNRelationshipDataObjectReference.xml"));
        relationshipDataObject = relationshipDataObject.replaceAll("ResultVariableToSet", "\\${" + dataObject.getRelationshipInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("RelationshipTemplateToSet", dataObject.getRelationshipTemplate());
        relationshipDataObject = relationshipDataObject.replaceAll("SourceURLToSet", "\\${" + dataObject.getSourceInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("TargetURLToSet", "\\${" + dataObject.getTargetInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("IdToSet", dataObject.getId());
        return relationshipDataObject;
    }

    /**
     * Creates a node instance data object which consists of the node template and its properties
     */
    public String createNodeDataObjectReference(BPMNDataObject dataObject) throws IOException {
        String nodeDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeDataObjectReference.xml"));
        nodeDataObject = nodeDataObject.replaceAll("NodeInstanceURLToSet", "\\${" + dataObject.getNodeInstanceURL() + "}");
        nodeDataObject = nodeDataObject.replaceAll("NodeTemplateToSet", dataObject.getNodeTemplate());
        nodeDataObject = nodeDataObject.replaceAll("IdToSet", dataObject.getId());
        StringBuilder properties = new StringBuilder();
        // property is structured as propertyName#propertyValue
        if (!dataObject.getProperties().isEmpty()) {
            for (String property : dataObject.getProperties()) {
                String propertyName = property.split("#")[0];
                String propertyValue = property.split("#")[1];
                // either we have something like DockerEngine#GDockerEngineURL or
                // Port#GApplicationPort these values have to be in the dollar brackets otherwise we get an error
                if (propertyValue.equals(propertyName) || propertyValue.startsWith("G")) {
                    propertyValue = propertyValue.replace("G", "");
                    properties.append("<camunda:inputParameter name='Properties.").append(propertyName).append("'>").append("\\${").append(propertyValue).append("}").append("</camunda:inputParameter>");
                } else {
                    // for the cases like ContainerPort#80
                    properties.append("<camunda:inputParameter name='Properties.").append(propertyName).append("'>").append(propertyValue).append("</camunda:inputParameter>");
                }
            }
        }
        nodeDataObject = nodeDataObject.replace("<camunda:inputParameter name='Properties'>PropertiesToSet</camunda:inputParameter>", properties.toString());

        return nodeDataObject;
    }

    /**
     * Creates a service instance data object which consists of the service template and its properties
     */
    public String createServiceInstanceDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException {
        String serviceTemplateNamespace = bpmnPlan.getServiceTemplate().getTargetNamespace();
        String csarName = bpmnPlan.getCsarName();
        String serviceInstanceDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNServiceInstanceDataObjectReference.xml"));
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceTaskName", dataObject.getId());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceURLToSet", dataObject.getServiceInstanceURL());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceIdToSet", dataObject.getServiceInstanceURL());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceTemplateURLToSet", serviceTemplateNamespace + csarName);
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("CsarIdToSet", csarName);
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("IdToSet", dataObject.getId());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("NameToSet", "Service_Instance_Creation");
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ResultVariableToSet", dataObject.getServiceInstanceURL());
        return serviceInstanceDataObject;
    }

    /**
     * Creates an input output data object which handles in and outputs properties
     */
    public String createInputOutputDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException {
        String inputOutputDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNInputOutputDataObjectReference.xml"));
        inputOutputDataObject = inputOutputDataObject.replaceAll("ServiceInstanceTaskName", dataObject.getId());
        inputOutputDataObject = inputOutputDataObject.replaceAll("IdToSet", dataObject.getId());
        StringBuilder inputParameters = new StringBuilder();
        StringBuilder inputParameterNames = new StringBuilder();
        for (String inputParameterName : bpmnPlan.getInputParameters()) {
            inputParameterNames.append(inputParameterName).append(",");
            inputParameters.append("<camunda:inputParameter name='").append(inputParameterName).append("'>").append("\\${").append(inputParameterName).append("}").append("</camunda:inputParameter>");
        }
        StringBuilder outputParameters = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        for (String outputParameterName : bpmnPlan.getPropertiesOutputParameters().keySet()) {
            outputParameterNames.append(outputParameterName).append(",");
            if (outputParameterName.equals(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName))) {
                outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append("\\${").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("}").append("</camunda:inputParameter>");
            } else {
                outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("</camunda:inputParameter>");
            }
        }
        inputParameterNames = new StringBuilder(inputParameterNames.substring(0, inputParameterNames.lastIndexOf(",")));
        outputParameterNames = new StringBuilder(outputParameterNames.substring(0, outputParameterNames.lastIndexOf(",")));
        inputOutputDataObject = inputOutputDataObject.replaceAll("<camunda:inputParameter name='InputParameter'>InputParameterToSet</camunda:inputParameter>", inputParameters.toString());
        inputOutputDataObject = inputOutputDataObject.replaceAll(" <camunda:inputParameter name='OutputParameter'>OutputParameterToSet</camunda:inputParameter>", outputParameters.toString());
        inputOutputDataObject = inputOutputDataObject.replaceAll("InputParameterNamesToSet", inputParameterNames.toString());
        inputOutputDataObject = inputOutputDataObject.replaceAll("OutputParameterToSet", outputParameterNames.toString());
        return inputOutputDataObject;
    }

    /**
     * Each dataObject is composed of two components: 1)DataObjectReference which holds the actual content of the data
     * object 2) the data Object itself
     *
     * @param d The finalized Document (with diagram elements) without dataObjects
     */
    public void createDataObjectAsNode(BPMNPlan bpmnPlan, Document d, BPMNDataObject dataObject) throws IOException, SAXException {
        BPMNSubprocessType dataObjectType = dataObject.getDataObjectType();
        String dataObjectReference = "";
        if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_ST) {
            dataObjectReference = createServiceInstanceDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_NODE) {
            dataObjectReference = createNodeDataObjectReference(dataObject);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_INOUT) {
            dataObjectReference = createInputOutputDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_REL) {
            dataObjectReference = createRelationDataObjectReference(dataObject);
        }
        this.createImportNodeFromString(bpmnPlan, d, dataObjectReference, false);
        String diagramServiceInstanceDataObjectReference = createDiagramDataObjectReference(dataObject, bpmnPlan);
        this.createImportNodeFromString(bpmnPlan, d, diagramServiceInstanceDataObjectReference, true);

        String dataObject2 = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataObject.xml"));
        dataObject2 = dataObject2.replaceAll("IdToSet", dataObject.getId());
        this.createImportNodeFromString(bpmnPlan, d, dataObject2, false);
    }

    /**
     * create data object reference for diagram
     */
    private String createDiagramDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException {
        String dataObjectReference = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/diagram/BPMNDiagramDataObjectReference.xml"));
        for (BPMNSubprocess outerSubprocess : bpmnPlan.getSubprocess()) {
            String dataObjectOuterSubprocessId = outerSubprocess.getId().replace("Subprocess", "DataObject");
            if (outerSubprocess.getId().contains(dataObject.getId()) || dataObjectOuterSubprocessId.contains(dataObject.getId())) {
                // subprocess width: 100, data object reference width: 36 -> 32 is the center
                double x = outerSubprocess.getX() + 32;
                // to get the data objects above the subprocess
                double y = outerSubprocess.getY() - 150;
                double yLabel = y - 60;
                dataObject.setX(x);
                dataObject.setY(y);
                dataObjectReference = dataObjectReference.replaceAll("<dc:Bounds x='xToSet' y='yToSet' width='36' height='50' />", "<dc:Bounds x=\"" + x + "\" y=\"" + y + "\" width=\"36\" height=\"50\" />");
                dataObjectReference = dataObjectReference.replace("xLabelToSet", "" + x);
                dataObjectReference = dataObjectReference.replace("yLabelToSet", "" + yLabel);
            }
        }
        dataObjectReference = dataObjectReference.replaceAll("IdToSet", dataObject.getId());
        return dataObjectReference;
    }

    /**
     * creates a Node of the specified type (bpmnSubprocessType)
     *
     * @param bpmnSubprocess the subprocess
     * @return created node
     */
    public Node createBPMNSubprocessAndComponentsAsNode(BPMNSubprocess bpmnSubprocess) {
        LOG.info("Creating BPMNSubprocess as Node: {} with type: {}", bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessType().name());
        Node node = null;
        try {
            switch (bpmnSubprocess.getBpmnSubprocessType()) {
                case ERROR_END_EVENT:
                    node = this.createBPMNErrorEndEventAsNode(bpmnSubprocess);
                    break;
                case SEQUENCE_FLOW:
                    node = this.createBPMNSequenceFlowAsNode(bpmnSubprocess);
                    break;
                case CREATE_ST_INSTANCE:
                    node = this.createBPMNCreateServiceInstanceAsNode(bpmnSubprocess);
                    break;
                case SUBPROCESS:
                    node = this.createBPMNSubprocessAsNode(bpmnSubprocess);
                    break;
                case START_EVENT:
                case INNER_START_EVENT:
                    node = this.createBPMNStartEventAsNode(bpmnSubprocess);
                    break;
                case END_EVENT:
                    node = this.createBPMNEndEventAsNode(bpmnSubprocess);
                    break;
                case SET_ST_STATE:
                    node = this.createSetServiceTemplateStateAsNode(bpmnSubprocess);
                    break;
                case CREATE_RT_INSTANCE:
                    node = this.createRelationshipTemplateInstanceAsNode(bpmnSubprocess);
                    break;
                case CREATE_NODE_INSTANCE_TASK:
                    node = this.createNodeTemplateInstanceTaskAsNode(bpmnSubprocess);
                    break;
                case CALL_NODE_OPERATION_TASK:
                    node = this.createCallNodeOperationTaskAsNode(bpmnSubprocess);
                    break;
                case SET_NODE_PROPERTY_TASK:
                    node = this.createSetNodePropertiesTaskAsNode(bpmnSubprocess);
                    break;
                case ACTIVATE_DATA_OBJECT_TASK:
                    node = this.createActivateDataObjectTaskAsNode(bpmnSubprocess);
                    break;
                case COMPUTE_OUTPUT_PARAMS_TASK:
                    node = this.createOutputParamsTaskAsNode(bpmnSubprocess);
                    break;
                case USER_TASK:
                    node = this.createBPMNUserTaskAsNode(bpmnSubprocess);
                    break;
                default:
                    LOG.debug("Doesn't find matching BPMNSubprocess Type for {}", bpmnSubprocess.getId());
                    break;
            }
        } catch (Exception e) {
            LOG.debug("Fail to create BPMN Element due to {}", e.toString());
        }

        return node;
    }

    /**
     * create ActivateDataObjectTask node and import node to document
     *
     * @return created Node
     */
    private Node createActivateDataObjectTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String activateDataObjectString = createActivateDataObjectString(bpmnSubprocess);
        return createImportNodeFromString(bpmnSubprocess, activateDataObjectString);
    }

    /**
     * create the template String for a ActivateDataObjectTask node
     *
     * @return template String
     */
    private String createActivateDataObjectString(BPMNSubprocess bpmnSubprocess) throws IOException {
        BPMNPlan bpmnPlan = bpmnSubprocess.getBuildPlan();
        String activateDataObjectTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNActivateDataObjectTask.xml"));
        activateDataObjectTask = activateDataObjectTask.replace("ActivateDataObject_IdToReplace", bpmnSubprocess.getId());
        activateDataObjectTask = activateDataObjectTask.replace("NameToSet", "Activate data object " + bpmnSubprocess.getDataObject().getId());
        activateDataObjectTask = activateDataObjectTask.replace("DataObjectIdToSet", BPMNSubprocessType.DATA_OBJECT_REFERENCE + "_" + bpmnSubprocess.getDataObject().getId());
        StringBuilder properties = new StringBuilder();
        StringBuilder propertiesNames = new StringBuilder();

        StringBuilder inputParameterNames = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        if (bpmnSubprocess.getDataObject().getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT) {
            for (String inputParameterName : bpmnSubprocess.getBuildPlan().getInputParameters()) {
                inputParameterNames.append(inputParameterName).append(",");
            }
            StringBuilder outputParameters = new StringBuilder();

            for (String outputParameterName : bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().keySet()) {
                outputParameterNames.append(outputParameterName).append(",");
                if (outputParameterName.equals(bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().get(outputParameterName))) {
                    outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append("\\${").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("}").append("</camunda:inputParameter>");
                } else {
                    outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("</camunda:inputParameter>");
                }
            }
            inputParameterNames = new StringBuilder(inputParameterNames.substring(0, inputParameterNames.lastIndexOf(",")));
            outputParameterNames = new StringBuilder(outputParameterNames.substring(0, outputParameterNames.lastIndexOf(",")));
            activateDataObjectTask = activateDataObjectTask.replaceAll("InputParameterNamesToSet", inputParameterNames.toString());
            activateDataObjectTask = activateDataObjectTask.replaceAll("OutputParameterNamesToSet", outputParameterNames.toString());
            activateDataObjectTask = activateDataObjectTask.replaceAll(" <camunda:inputParameter name='OutputParameter'>OutputParameterToSet</camunda:inputParameter>", outputParameters.toString());
        }
        if (!bpmnSubprocess.getDataObject().getProperties().isEmpty()) {
            for (String property : bpmnSubprocess.getDataObject().getProperties()) {
                String propertyName = property;
                String propertyValue = property;
                if (property.contains("#")) {
                    propertyName = property.split("#")[0];
                    propertyValue = property.split("#")[1];
                    propertiesNames.append(propertyName).append(",");
                } else {
                    propertiesNames.append(property).append(",");
                }
                if (propertyValue.equals(propertyName) && (!propertyValue.startsWith("G"))) {
                    properties.append("<camunda:inputParameter name='Properties.").append(propertyName).append("'>").append(propertyValue).append("</camunda:inputParameter>");
                }
                // G marks that the property is given by input thus brackets are needed
                else if (propertyValue.startsWith("G")) {
                    propertyValue = propertyValue.substring(1);
                    properties.append("<camunda:inputParameter name='Properties.").append(propertyName).append("'>").append("${").append(propertyValue).append("}").append("</camunda:inputParameter>");
                } else {
                    properties.append("<camunda:inputParameter name='Properties.").append(propertyName).append("'>").append(propertyValue).append("</camunda:inputParameter>");
                }
            }
            propertiesNames = new StringBuilder(propertiesNames.substring(0, propertiesNames.lastIndexOf(",")));
            activateDataObjectTask = activateDataObjectTask.replace("<camunda:inputParameter name='Properties'>PropertiesToSet</camunda:inputParameter>", properties.toString());
            activateDataObjectTask = activateDataObjectTask.replaceAll("PropertiesNamesToSet", propertiesNames.toString());
        }
        return activateDataObjectTask;
    }

    /**
     * create setNodePropertiesTask node and import node to document
     *
     * @return created Node
     */
    private Node createSetNodePropertiesTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String setNodePropertiesTask = createSetNodePropertiesState(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, setNodePropertiesTask);
    }

    /**
     * create the template String for a SetNodeProperties node
     *
     * @return template String
     */
    private String createSetNodePropertiesState(BPMNSubprocess bpmnSubprocess) throws IOException {
        final String PROPERTIES = ".Properties.";
        String setNodeProperties = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetPropertiesTask.xml"));
        setNodeProperties = setNodeProperties.replace("Activity_IdToSet", bpmnSubprocess.getId());
        setNodeProperties = setNodeProperties.replace("name_toSet", bpmnSubprocess.getId());
        String nodeTemplateId = bpmnSubprocess.getNodeTemplate().getId();
        String nodeInstanceURL = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");

        if (bpmnSubprocess.getInstanceState() != null) {
            setNodeProperties = setNodeProperties.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        }

        String parentId = bpmnSubprocess.getParentProcess().getId();
        setNodeProperties = setNodeProperties.replace("NodeInstanceURLToSet", "${" + nodeInstanceURL + "}");
        setNodeProperties = setNodeProperties.replaceAll("NodeTemplateToSet", nodeTemplateId);
        String prefix = BPMNSubprocessType.DATA_OBJECT_REFERENCE + "_" + BPMNSubprocessType.DATA_OBJECT;
        String dataObjectReferenceId = parentId.replace("Subprocess", prefix);
        setNodeProperties = setNodeProperties.replaceAll("DataObjectToSet", dataObjectReferenceId);

        List<String> properties = null;
        StringBuilder propertiesToSet = new StringBuilder();
        // find corresponding data object
        if (bpmnSubprocess.getBuildPlan().getDataObjectsList() != null) {
            for (BPMNDataObject dataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
                // (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE) &&
                if (dataObjectReferenceId.contains(dataObject.getId())) {
                    if (dataObject.getProperties() != null) {
                        properties = dataObject.getProperties();
                    }
                }
            }
        }

        StringBuilder inputParamBuilder = new StringBuilder();

        //set input properties
        if (!Objects.requireNonNull(properties).isEmpty()) {
            for (String property : properties) {
                String inputParameterName = property.split("#")[0];
                propertiesToSet.append(",").append(inputParameterName);
                inputParamBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append(dataObjectReferenceId).append(PROPERTIES).append(inputParameterName).append("</camunda:inputParameter>");
            }
        }
        // cut the first semicolon out
        propertiesToSet = new StringBuilder(propertiesToSet.substring(propertiesToSet.indexOf(",") + 1, propertiesToSet.length()));
        setNodeProperties = setNodeProperties.replaceAll("PropertiesToSet", propertiesToSet.toString());

        //rebuild template String
        String[] original = setNodeProperties.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];
        return original[0] + inputParamBuilder + original[1];
    }

    /**
     * create a Node of the bpmnSubprocess containing all child processes
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node
     */
    public Node createBPMNSubprocessAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("id {}, type {}", bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessType());
        final String subprocess = createBPMNSubprocess(bpmnSubprocess);

        Node node = this.createImportNodeFromString(bpmnSubprocess, subprocess);

        ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
        ArrayList<Node> flowNodes = new ArrayList<>();
        // add Start Event inside subprocess
        BPMNSubprocess innerStartEvent = new BPMNSubprocess(BPMNSubprocessType.INNER_START_EVENT, "StartEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerStartEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerStartEvent.setParentProcess(bpmnSubprocess);
        BPMNSubprocess previousIncoming = innerStartEvent;
        // compute the sequence flows before components are added to the xml
        for (BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            BPMNSubprocess innerSequenceFlow2 = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "InnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
            innerSequenceFlow2.setBuildPlan(bpmnSubprocess.getBuildPlan());
            innerSequenceFlow2.setIncomingFlowElements(previousIncoming);
            innerSequenceFlow2.setOutgoingFlow(subSubprocess);

            Node innerSequenceFlowNode = this.createBPMNSubprocessAndComponentsAsNode(innerSequenceFlow2);
            flowNodes.add(innerSequenceFlowNode);
            flowElements.add(innerSequenceFlow2);
            bpmnSubprocess.setFlowElements(flowElements);
            previousIncoming = subSubprocess;
        }
        // add End Event inside subprocess
        BPMNSubprocess innerEndEvent = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "EndEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerEndEvent.setParentProcess(bpmnSubprocess);

        BPMNSubprocess innerEndEventSequenceFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "InnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
        innerEndEventSequenceFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerEndEventSequenceFlow.setIncomingFlowElements(previousIncoming);
        innerEndEventSequenceFlow.setOutgoingFlow(innerEndEvent);
        flowElements.add(innerEndEventSequenceFlow);
        bpmnSubprocess.setFlowElements(flowElements);
        Node innerEndEventFlowNode = this.createBPMNSubprocessAndComponentsAsNode(innerEndEventSequenceFlow);
        flowNodes.add(innerEndEventFlowNode);
        bpmnSubprocess.setFlowElements(flowElements);
        Node startEventNode = this.createBPMNSubprocessAndComponentsAsNode(innerStartEvent);
        bpmnSubprocess.getBpmnSubprocessElement().appendChild(startEventNode);

        // importing all elements within Subprocess recursively
        ArrayList<BPMNSubprocess> incomingBoundaryEventLinks = new ArrayList<>();
        ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();
        innerEndEvent.setParentProcess(bpmnSubprocess);
        // add error end event inside subprocess
        BPMNSubprocess innerErrorEndEvent = new BPMNSubprocess(BPMNSubprocessType.ERROR_END_EVENT, "ErrorEndEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerErrorEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerErrorEndEvent.setParentProcess(bpmnSubprocess);
        for (BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            subSubprocess.setParentProcess(bpmnSubprocess);

            if (subSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2) {
                //Node child = this.createBPMNSubprocessAndComponentsAsNode(subSubprocess);
                if (subSubprocess.getSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW && subSubprocess.getSubprocessType() != BPMNSubprocessType.DATA_OBJECT
                    && subSubprocess.getSubprocessType() != BPMNSubprocessType.DATA_OBJECT_NODE && subSubprocess.getSubprocessType() != BPMNSubprocessType.DATA_OBJECT_REL
                    && subSubprocess.getSubprocessType() != BPMNSubprocessType.DATA_OBJECT_ST && subSubprocess.getSubprocessType() != BPMNSubprocessType.INNER_START_EVENT
                    && subSubprocess.getSubprocessType() != BPMNSubprocessType.START_EVENT && subSubprocess.getSubprocessType() != BPMNSubprocessType.END_EVENT) {
                    for (Integer errorId : bpmnSubprocess.getErrorEventIds()) {
                        bpmnSubprocess.getBuildPlan().getIdForErrorInnerFlowAndIncrement();
                        BPMNSubprocess innerErrorSequenceFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "ErrorInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
                        BPMNSubprocess innerBoundaryEvent = new BPMNSubprocess(BPMNSubprocessType.EVENT, "BoundaryEvent_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
                        innerBoundaryEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
                        innerErrorSequenceFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
                        innerErrorSequenceFlow.setIncomingFlowElements(innerBoundaryEvent);
                        innerErrorSequenceFlow.setOutgoingFlow(innerErrorEndEvent);
                        errorFlowElements.add(innerErrorSequenceFlow);
                        bpmnSubprocess.setFlowElements(flowElements);
                        bpmnSubprocess.setErrorFlowElements(errorFlowElements);
                        innerBoundaryEvent.setParentProcess(bpmnSubprocess);
                        Node errorChild = this.createTaskErrorBoundaryEventAsNode(innerBoundaryEvent, subSubprocess, errorId);
                        bpmnSubprocess.getBpmnSubprocessElement().appendChild(errorChild);
                    }
                }
            }
            Node child = this.createBPMNSubprocessAndComponentsAsNode(subSubprocess);
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(child);
        }

        Node endEventNode = this.createBPMNSubprocessAndComponentsAsNode(innerEndEvent);
        bpmnSubprocess.getBpmnSubprocessElement().appendChild(endEventNode);
        for (Node flowNode : flowNodes) {
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(flowNode);
        }
        for (BPMNSubprocess flowNode : errorFlowElements) {
            Node errorSequenceNode = this.createBPMNSequenceFlowAsNode(flowNode);
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(errorSequenceNode);
        }

        LOG.info("SUBPROCESS TYPE {} {} {}", bpmnSubprocess.getBpmnSubprocessType(), bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessElement().getChildNodes().getLength());
        innerErrorEndEvent.setIncomingSubprocess(incomingBoundaryEventLinks);
        innerErrorEndEvent.setErrorEventIds(bpmnSubprocess.getErrorEventIds());
        Node errorEndEventNode = this.createBPMNSubprocessAndComponentsAsNode(innerErrorEndEvent);
        bpmnSubprocess.getBpmnSubprocessElement().appendChild(errorEndEventNode);

        return node;
    }

    /**
     * create a Node from a template String and import the Node into the document
     *
     * @param bpmnSubprocess the subprocess
     * @param s              template String to transform into a Node
     * @return the created and imported Node
     */
    private Node createImportNodeFromString(BPMNSubprocess bpmnSubprocess, String s) throws
        IOException, SAXException {
        Node transformedNode = this.transformStringToNode(s);
        ArrayList<String> incomingFlowIds;
        ArrayList<String> outgoingFlowIds;
        LOG.info("DER TYPE {} {}", bpmnSubprocess.getSubprocessType(), bpmnSubprocess.getId());
        Document doc = bpmnSubprocess.getBpmnDocument();
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.ERROR_END_EVENT) {
            outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:endEvent>";
            int end = s.indexOf(closingTag);
            StringBuilder result = new StringBuilder();
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            return getResultCreateNode(bpmnSubprocess, s, doc, begin, closingTag, end, result);
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.END_EVENT) {
            outgoingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            if (bpmnSubprocess.getId().contains("Error")) {
                outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            }
            String[] original = s.split("</bpmn:endEvent>");
            StringBuilder result = new StringBuilder();
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:endEvent>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.EVENT) {
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:boundaryEvent>";
            int end = s.indexOf(closingTag);
            StringBuilder result = new StringBuilder();
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            return getResultCreateNode(bpmnSubprocess, s, doc, begin, closingTag, end, result);
        }
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2 && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SUBPROCESS && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.EVENT && bpmnSubprocess.getSubprocessType() != BPMNSubprocessType.SUBPROCESS_ERROR_BOUNDARY
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.USER_TASK && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2 && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.START_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.INNER_START_EVENT) {

            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:scriptTask>");
            StringBuilder result = new StringBuilder();
            for (String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:scriptTask>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.SUBPROCESS) {
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:subProcess>");
            StringBuilder result = new StringBuilder();
            for (String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result.insert(0, original[0]);
            // there can only be maximal one dataOutputAssociation per subprocess
            /**
             if (bpmnSubprocess.getDataObject() != null) {
             String dataObjectID = bpmnSubprocess.getDataObject().getId();
             result += " <bpmn:dataOutputAssociation id=\"DataOutputAssociation_" + dataObjectID + "\">\n" +
             "        <bpmn:targetRef>DataObjectReference_" + dataObjectID + "</bpmn:targetRef>\n" +
             "      </bpmn:dataOutputAssociation>";
             LOG.info(result);
             }
             */
            result.append("</bpmn:subProcess>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.USER_TASK) {
            incomingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:userTask>");
            StringBuilder result = new StringBuilder();
            for (String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:userTask>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.INNER_START_EVENT || bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.START_EVENT) {
            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:startEvent>");
            StringBuilder result = new StringBuilder();
            for (String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:startEvent>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }

        // make sure all elements belongs to same document
        Node importedNode = doc.importNode(transformedNode, true);

        bpmnSubprocess.setBpmnSubprocessElement((Element) importedNode);

        return importedNode;
    }

    private Node getResultCreateNode(BPMNSubprocess bpmnSubprocess, String s, Document doc, int begin, String closingTag, int end, StringBuilder result) throws SAXException, IOException {
        result = new StringBuilder(s.substring(0, begin) + result + s.substring(begin, end + closingTag.length()));
        Node transformedChangedNode = this.transformStringToNode(result.toString());
        Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
        bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
        return importedOutgoingNode2;
    }

    /**
     * Adds the node to the corresponding part of the document. Diagram elements are not inside the process element so,
     * they have to be moved after it.
     */
    private Node createImportNodeFromString(BPMNPlan bpmnPlan, Document d, String s, boolean diagramNode) throws
        IOException, SAXException {
        Node transformedNode = this.transformStringToNode(s);
        // make sure all elements belongs to same document
        Node importedNode = d.importNode(transformedNode, true);
        NodeList childrenOfFinishedDocument = d.getFirstChild().getChildNodes();
        for (int i = 0; i < d.getFirstChild().getChildNodes().getLength(); i++) {
            if (diagramNode) {
                if (childrenOfFinishedDocument.item(i).getNodeName().equals("bpmndi:BPMNDiagram")) {
                    for (int j = 0; j < childrenOfFinishedDocument.item(i).getChildNodes().getLength(); j++) {
                        if (childrenOfFinishedDocument.item(i).getChildNodes().item(j).getNodeName().equals("bpmndi:BPMNPlane")) {
                            childrenOfFinishedDocument.item(i).getChildNodes().item(j).appendChild(importedNode);
                        }
                    }
                }
            } else {
                if (d.getFirstChild().getChildNodes().item(i).getNodeName().equals("bpmn:process")) {
                    d.getFirstChild().getChildNodes().item(i).appendChild(importedNode);
                }
            }
        }
        bpmnPlan.setBpmnDocument(d);
        return importedNode;
    }

    /**
     * create a bpmnSubprocess Node
     *
     * @param bpmnSubprocess thr subprocess
     * @return created Node
     */
    private String createBPMNSubprocess(BPMNSubprocess bpmnSubprocess) throws IOException {
        String subprocess = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSubprocess.xml"));
        subprocess = subprocess.replace("Subprocess_IdToSet", bpmnSubprocess.getId());
        if (bpmnSubprocess.getRelationshipTemplate() != null) {
            subprocess = subprocess.replace("NameToSet",
                bpmnSubprocess.getRelationshipTemplate().getName() + " Subprocess");
            return subprocess;
        } else if (bpmnSubprocess.getNodeTemplate() != null) {
            subprocess = subprocess.replace("NameToSet",
                bpmnSubprocess.getNodeTemplate().getName() + " Subprocess");
            return subprocess;
        }
        if (bpmnSubprocess.getServiceInstanceURL() != null) {
            subprocess = subprocess.replace("NameToSet",
                "Service Instance Creation Subprocess");
            return subprocess;
        }
        subprocess = subprocess.replace("NameToSet",
            "Subprocess to activate data objects");
        return subprocess;
    }

    /**
     * create a start event Node from template String
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node
     */
    public Node createBPMNStartEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String startEvent = createBPMNStartEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, startEvent);
    }

    /**
     * create a start bpmn error definition Node from template String
     *
     * @param id id of the error event definition
     * @return created node
     */
    public Node createBPMNErrorEventDefinitionAsNode(int id) throws IOException, SAXException {
        final String bpmnErrorEventDefinition = createBPMNErrorEventDefinition(id);
        return this.transformStringToNode(bpmnErrorEventDefinition);
    }

    /**
     * create the template String for an error event definition Node
     *
     * @param id id of the error event definition
     * @return template String
     */
    public String createBPMNErrorEventDefinition(int id) throws IOException {
        final String idPrefix = BPMNSubprocessType.EVENT.toString();
        String bpmnErrorEventDefinition = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNError.xml"));
        bpmnErrorEventDefinition = bpmnErrorEventDefinition.replaceAll("IdToSet", idPrefix + id);
        bpmnErrorEventDefinition = bpmnErrorEventDefinition.replaceAll("NameToSet", "Error Event");
        return bpmnErrorEventDefinition;
    }

    private Node createBPMNSequenceFlowAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("BPMN Subprocess Id {} {}", bpmnSubprocess.getId(), bpmnSubprocess.getIncomingTestFlow().size());
        String sequenceFlow = createBPMNSequenceFlow(bpmnSubprocess.getId(),
            bpmnSubprocess.getOuterFlow().iterator().next().getId(),
            bpmnSubprocess.getIncomingTestFlow().iterator().next().getId()
        );
        return this.createImportNodeFromString(bpmnSubprocess, sequenceFlow);
    }

    public String createBPMNSequenceFlow(String FlowID, String incomingFlowName, String outgoingFlowName) throws
        IOException {
        String sequenceFlow = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        // each sequence flow is guaranteed to only two ends
        sequenceFlow = sequenceFlow.replaceAll("Flow_IdToReplace", FlowID);
        sequenceFlow = sequenceFlow.replaceAll("SourceToReplace", incomingFlowName);
        sequenceFlow = sequenceFlow.replaceAll("TargetToReplace", outgoingFlowName);
        return sequenceFlow;
    }

    /**
     * create an error boundary event node from template String
     *
     * @return created node
     */
    public Node createSubprocessErrorBoundaryEventAsNode(BPMNSubprocess bpmnSubprocess, int errorId) throws
        IOException, SAXException {
        final String bpmnSubprocessErrorBoundaryEvent = createBPMNSubprocessErrorBoundaryEvent(bpmnSubprocess, errorId);
        return this.createImportNodeFromString(bpmnSubprocess, bpmnSubprocessErrorBoundaryEvent);
    }

    /**
     * create an error boundary event String
     *
     */
    public Node createTaskErrorBoundaryEventAsNode(BPMNSubprocess innerEvent, BPMNSubprocess bpmnSubprocess,
                                                   int errorId) throws IOException, SAXException {
        final String bpmnTaskErrorBoundaryEvent = createBPMNTaskErrorBoundaryEvent(bpmnSubprocess, errorId);
        innerEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        return this.createImportNodeFromString(innerEvent, bpmnTaskErrorBoundaryEvent);
    }

    /**
     * create a subprocess error boundary event node from template String
     *
     * @return template String
     */
    public String createBPMNSubprocessErrorBoundaryEvent(BPMNSubprocess bpmnSubprocess, int id) throws IOException {
        String bpmnSubprocessErrorBoundaryEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSubprocessErrorBoundaryEvent.xml"));
        String attachedElementId = bpmnSubprocess.getId().replace("BoundaryEvent_ErrorEvent", "");
        bpmnSubprocessErrorBoundaryEvent = bpmnSubprocessErrorBoundaryEvent.replaceAll("Event_IdToSet", bpmnSubprocess.getId());
        bpmnSubprocessErrorBoundaryEvent = bpmnSubprocessErrorBoundaryEvent.replaceAll("Activity_ActIdToSet", attachedElementId);
        bpmnSubprocessErrorBoundaryEvent = bpmnSubprocessErrorBoundaryEvent.replaceAll("IdToSet", bpmnSubprocess.getId());
        bpmnSubprocessErrorBoundaryEvent = bpmnSubprocessErrorBoundaryEvent.replaceAll("Error_Id", "Error_Event" + id);
        return bpmnSubprocessErrorBoundaryEvent;
    }

    /**
     * create an error boundary event template String
     *
     * @return template String
     */
    public String createBPMNTaskErrorBoundaryEvent(BPMNSubprocess bpmnSubprocess, int id) throws IOException {
        String bpmnTaskErrorBoundaryEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNTaskErrorBoundaryEvent.xml"));
        String idPrefix = BPMNSubprocessType.SUBPROCESS_ERROR_BOUNDARY.toString();
        bpmnTaskErrorBoundaryEvent = bpmnTaskErrorBoundaryEvent.replaceAll("Event_IdToSet", "BoundaryEvent_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
        bpmnTaskErrorBoundaryEvent = bpmnTaskErrorBoundaryEvent.replaceAll("Activity_ActIdToSet", bpmnSubprocess.getId());
        bpmnTaskErrorBoundaryEvent = bpmnTaskErrorBoundaryEvent.replaceAll("IdToSet", bpmnSubprocess.getId());
        bpmnTaskErrorBoundaryEvent = bpmnTaskErrorBoundaryEvent.replaceAll("Error_Id", "Error_" + idPrefix + id);
        return bpmnTaskErrorBoundaryEvent;
    }

    /**
     * create a bpmn start event template String
     *
     * @return template String
     */
    public String createBPMNStartEvent(BPMNSubprocess bpmnSubprocess) throws IOException {
        String startEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        startEvent = startEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        return startEvent;
    }

    private ArrayList<String> computeOutgoingFlowElements(BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getFlowElements();
            }
            for (BPMNSubprocess flowElement : flowElements) {
                if (!flowElement.getIncomingTestFlow().isEmpty() && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
                    for (int j = 0; j < flowElement.getIncomingTestFlow().size(); j++) {
                        String outgoingFlowId = flowElement.getIncomingTestFlow().get(j).getId();
                        if (outgoingFlowId.equals(bpmnSubprocess.getId())) {
                            test.add(flowElement.getId());
                        }
                    }
                }
            }
        }
        return test;
    }

    private ArrayList<String> computeErrorOutgoingFlowElements(BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getErrorFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getErrorFlowElements();
            }
            for (BPMNSubprocess flowElement : flowElements) {
                if (!flowElement.getIncomingTestFlow().isEmpty()) {
                    for (int j = 0; j < flowElement.getIncomingTestFlow().size(); j++) {
                        String outgoingFlowId = flowElement.getIncomingTestFlow().get(j).getId();
                        if (outgoingFlowId.equals(bpmnSubprocess.getId())) {
                            test.add(flowElement.getId());
                        }
                    }
                }
            }
        }
        return test;
    }

    private ArrayList<String> computeIncomingFlowElements(BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getFlowElements();
        if (!bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getFlowElements();
            }

            computeIncomingFlowIds(bpmnSubprocess, test, flowElements);
        }
        return test;
    }

    private void computeIncomingFlowIds(BPMNSubprocess bpmnSubprocess, ArrayList<String> test, ArrayList<BPMNSubprocess> flowElements) {
        for (BPMNSubprocess flowElement : flowElements) {
            if (!flowElement.getOutgoingFlow().isEmpty()) {

                for (int j = 0; j < flowElement.getOuterFlow().size(); j++) {
                    String incomingFlowId = flowElement.getOuterFlow().get(j).getId();
                    if (incomingFlowId.equals(bpmnSubprocess.getId())) {
                        test.add(flowElement.getId());
                    }
                }
            }
        }
    }

    private ArrayList<String> computeIncomingErrorFlowElements(BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getErrorFlowElements();
        if (bpmnSubprocess.getParentProcess() != null) {
            flowElements = bpmnSubprocess.getParentProcess().getErrorFlowElements();
        }

        computeIncomingFlowIds(bpmnSubprocess, test, flowElements);
        return test;
    }

    /**
     * create a bpmn end event node from template String
     *
     * @return created Node
     */
    public Node createBPMNEndEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String endEvent = createBPMNEndEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, endEvent);
    }

    /**
     * create a bpmn end event template String
     *
     * @return template String
     */
    public String createBPMNEndEvent(BPMNSubprocess bpmnSubprocess) throws IOException {
        String bpmnEndEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        bpmnEndEvent = bpmnEndEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        return bpmnEndEvent;
    }

    /**
     * create a bpmn error end event node from template String
     *
     * @return created node
     */
    public Node createBPMNErrorEndEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String bpmnErrorEndEvent = createBPMNErrorEndEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, bpmnErrorEndEvent);
    }

    /**
     * create a bpmn error end event template String
     *
     * @return template String
     */
    public String createBPMNErrorEndEvent(BPMNSubprocess bpmnSubprocess) throws IOException {
        String bpmnErrorEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNErrorEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForErrorInnerFlowAndIncrement();
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        // This is currently a high assumption that we only have one specific error
        int errorId = bpmnSubprocess.getErrorEventIds().get(0);
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("errorRefToSet", "Error_Event" + errorId);
        StringBuilder incomingBoundaryLinks = new StringBuilder();
        for (BPMNSubprocess subprocess : bpmnSubprocess.getIncomingLinks()) {
            incomingBoundaryLinks.append("<bpmn:incoming>").append(subprocess.getId()).append("</bpmn:incoming>");
        }

        bpmnErrorEvent = bpmnErrorEvent.replaceAll("<bpmn:incoming>Flow_Input</bpmn:incoming>", incomingBoundaryLinks.toString());

        final String idPrefix = BPMNSubprocessType.EVENT.toString();
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("ErrorEventDefinitionIdToSet", "ErrorDefinition_" + idPrefix + id);
        return bpmnErrorEvent;
    }

    /**
     * create a callNodeOperation node from template String
     *
     * @return created Node
     */
    private Node createCallNodeOperationTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String nodeOperation = createNodeOperation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, nodeOperation);
    }

    /**
     * create a NodeTemplateInstanceTask node from template String
     *
     * @return template String
     */
    private Node createNodeTemplateInstanceTaskAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String nodeTemplateInstance = createNodeTemplateInstance(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, nodeTemplateInstance);
    }

    /**
     * create a BpmnServiceInstance node from template String
     *
     * @return created node
     */
    private Node createBPMNCreateServiceInstanceAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String serviceInstance = createServiceInstance(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, serviceInstance);
    }

    /**
     * create a Set Service Template node from template String
     *
     * @return created Node
     */
    public Node createSetServiceTemplateStateAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String setServiceTemplateState = createSetServiceTemplateState(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, setServiceTemplateState);
    }

    /**
     * create a SetServiceTemplate template String
     *
     * @return template String
     */
    private String createSetServiceTemplateState(BPMNSubprocess bpmnSubprocess) throws IOException {
        String setState = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetStateTask.xml"));
        setState = setState.replace("Activity_IdToSet", bpmnSubprocess.getId());
        setState = setState.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnSubprocess.getNodeTemplate() != null) {
                if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE && bpmnDataObject.getNodeTemplate().equals(bpmnSubprocess.getNodeTemplate().getId())) {
                    setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getNodeInstanceURL() + "}");
                }
            } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
                if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_REL && bpmnDataObject.getRelationshipTemplate().equals(bpmnSubprocess.getRelationshipTemplate().getId())) {
                    setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getRelationshipInstanceURL() + "}");
                }
            } else if (bpmnDataObject.getServiceInstanceURL() != null) {
                setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getServiceInstanceURL() + "}");
            } else {
                setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getProperties().stream().filter(s -> s.startsWith(ServiceInstanceURLVarKeyword)) + "}");
            }
        }
        return setState;
    }

    /**
     * create a Relationship TemplateInstance node from template String
     *
     * @return created Node
     */
    private Node createRelationshipTemplateInstanceAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String relationshipTemplateInstance = createRelationshipTemplateInstance(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, relationshipTemplateInstance);
    }

    /**
     * create a ServiceInstance template String
     *
     * @param bpmnSubprocess the subprocess
     * @return createServiceInstance String
     */
    public String createServiceInstance(BPMNSubprocess bpmnSubprocess) throws IOException {
        String createServiceInstance = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        createServiceInstance = createServiceInstance.replaceAll("ResultVariableToSet", bpmnSubprocess.getResultVariableName());
        createServiceInstance = createServiceInstance.replaceAll("Subprocess_IdToSet", bpmnSubprocess.getId());
        createServiceInstance = createServiceInstance.replaceAll("StateToSet", "CREATING");
        createServiceInstance = createServiceInstance.replaceAll("DataObjectToSet", bpmnSubprocess.getParentProcess().getDataObject().getId());
        return createServiceInstance;
    }

    public Node createOutputParamsTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String outputParamsTask = createOutputParamsTask(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, outputParamsTask);
    }

    private String createOutputParamsTask(BPMNSubprocess bpmnSubprocess) throws IOException {
        LOG.info("Create output parameter task of id {}", bpmnSubprocess.getId());
        String outputParameterTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateOutputParameterTask.xml"));
        outputParameterTask = outputParameterTask.replace("Activity_IdToSet", bpmnSubprocess.getId());
        final BPMNPlan bpmnPlan = bpmnSubprocess.getBuildPlan();
        // find data object
        if (bpmnSubprocess.getBuildPlan().getDataObjectsList() != null) {
            for (BPMNDataObject dataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
                if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT) {
                    outputParameterTask = outputParameterTask.replaceAll("DataObjectToSet", BPMNSubprocessType.DATA_OBJECT_REFERENCE + "_" + dataObject.getId());
                }
            }
        }
        StringBuilder outputParameters = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        final String concat = "concat(";
        for (String outputParameterName : bpmnPlan.getPropertiesOutputParameters().keySet()) {
            outputParameterNames.append(outputParameterName).append(",");
            if (outputParameterName.equals(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName))) {
                outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append("\\${").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("}").append("</camunda:inputParameter>");
            } else {
                String outputParameterValue = "";
                // this is the case where we have in the service template some property mapping and each property is associated to a node template.
                // To find the data object which holds the correct properties we split at the first 'point'
                for (BPMNDataObject dataObject : bpmnPlan.getDataObjectsList()) {
                    if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE) {
                        outputParameterValue = bpmnPlan.getPropertiesOutputParameters().get(outputParameterName);
                        // schema: NodeTemplate.Properties.PropertyName
                        String[] outputParameterValueParts = outputParameterValue.split(",");
                        for (String outputParameterValuePart : outputParameterValueParts) {
                            if (outputParameterValuePart.contains(".")) {
                                String nodeTemplate = outputParameterValuePart.split("\\.")[0].trim();
                                if (dataObject.getNodeTemplate().equals(nodeTemplate)) {
                                    String outputParameterPartValue = outputParameterValuePart.replaceAll(nodeTemplate, BPMNSubprocessType.DATA_OBJECT_REFERENCE + "_" + dataObject.getId());
                                    if (outputParameterValue.contains(concat)) {
                                        outputParameterValue = outputParameterValue.replaceAll(",", "\\+");
                                        outputParameterValue = outputParameterValue.replace(outputParameterValuePart, outputParameterPartValue);
                                    }
                                }
                            }
                        }
                    }
                }
                outputParameterValue = outputParameterValue.substring(outputParameterValue.indexOf(concat) + concat.length(), outputParameterValue.lastIndexOf(")"));
                outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append(outputParameterValue).append("</camunda:inputParameter>");
            }
        }
        outputParameterNames = new StringBuilder(outputParameterNames.substring(0, outputParameterNames.lastIndexOf(",")));
        outputParameterTask = outputParameterTask.replaceAll("<camunda:inputParameter name='OutputParameter'>OutputParameterToSet</camunda:inputParameter>", outputParameters.toString());
        outputParameterTask = outputParameterTask.replaceAll("OutputParameterNamesToSet", outputParameterNames.toString());
        return outputParameterTask;
    }

    public void addDataAssociations(BPMNPlan buildPlan, Document d, BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        if (bpmnSubprocess.getDataObject() != null) {
            NodeList subprocesses = d.getElementsByTagName("bpmn:subProcess");
            for (int i = 0; i < subprocesses.getLength(); i++) {
                // if(subprocesses.item(i))
                String subprocessId = subprocesses.item(i).getAttributes().getNamedItem("id").getNodeValue();
                if (subprocessId.equals(bpmnSubprocess.getId())) {
                    Node propertyNode = createBPMNPropertyAsNode(bpmnSubprocess);
                    Node dataInputAssociationAsNode = createBPMNDataInputAssociationAsNode(bpmnSubprocess);
                    NodeList childNodes = subprocesses.item(i).getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        // outgoing and incoming child nodes have no attributes
                        if (childNodes.item(j).getAttributes() != null) {
                            if (childNodes.item(j).getAttributes().getLength() > 0) {
                                String childId = childNodes.item(j).getAttributes().getNamedItem("id").getNodeValue();
                                if (childId.contains("StartEvent")) {
                                    Node startEventNode = childNodes.item(j);
                                    Node dataOutputAssociationAsNode = createBPMNDataOutputAssociationAsNode(bpmnSubprocess);
                                    if (bpmnSubprocess.getDataObject().getDataObjectType() != BPMNSubprocessType.DATA_OBJECT_INOUT &&
                                        bpmnSubprocess.getDataObject().getDataObjectType() != BPMNSubprocessType.DATA_OBJECT_ST) {
                                        Node importedPropertyNode = d.importNode(propertyNode, true);
                                        subprocesses.item(i).insertBefore(importedPropertyNode, startEventNode);
                                        Node importedDataInputNode = d.importNode(dataInputAssociationAsNode, true);
                                        subprocesses.item(i).insertBefore(importedDataInputNode, startEventNode);
                                        createBPMNDiagramDataInputAssociationAsNode(bpmnSubprocess, d);
                                    }
                                    Node importedDataOutputNode = d.importNode(dataOutputAssociationAsNode, true);
                                    subprocesses.item(i).insertBefore(importedDataOutputNode, startEventNode);
                                    createBPMNDiagramDataOutputAssociationAsNode(bpmnSubprocess, d);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addTaskDataAssociations(BPMNPlan buildPlan, Document d, BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        if (bpmnSubprocess.getDataObject() != null) {
            NodeList subprocesses = d.getElementsByTagName("bpmn:scriptTask");
            LOG.info("Number of tasks {}", subprocesses.getLength());
            for (int i = 0; i < subprocesses.getLength(); i++) {
                // if(subprocesses.item(i))
                String taskId = subprocesses.item(i).getAttributes().getNamedItem("id").getNodeValue();
                if (taskId.equals(bpmnSubprocess.getId())) {
                    Node propertyNode = createBPMNPropertyAsNode(bpmnSubprocess);
                    Node dataInputAssociationAsNode = createBPMNDataInputAssociationAsNode(bpmnSubprocess);
                    Node importedPropertyNode = d.importNode(propertyNode, true);
                    subprocesses.item(i).appendChild(importedPropertyNode);
                    Node importedDataInputNode = d.importNode(dataInputAssociationAsNode, true);
                    subprocesses.item(i).appendChild(importedDataInputNode);
                    createBPMNDiagramDataInputAssociationAsNode(bpmnSubprocess, d);
                    break;
                }
            }
        }
    }

    private Node createBPMNPropertyAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("Create BPMN Property with id {}", "Property_" + bpmnSubprocess.getId());
        String bpmnProperty = createBPMNProperty(bpmnSubprocess.getDataObject().getId());
        return this.transformStringToNode(bpmnProperty);
    }

    private String createBPMNProperty(String bpmnSubprocessId) throws IOException {
        String property = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNProperty.xml"));
        property = property.replaceAll("IdToSet", bpmnSubprocessId);
        property = property.replaceAll("targetRef", bpmnSubprocessId);
        return property;
    }

    private Node createBPMNDataOutputAssociationAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String bpmnDataOutputAssociation = createBPMNDataOutputAssociation(bpmnSubprocess.getDataObject().getId());
        return this.transformStringToNode(bpmnDataOutputAssociation);
    }

    public String createBPMNDataOutputAssociation(String bpmnSubprocessId) throws
        IOException {
        String bpmnOutputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataOutputAssociation.xml"));
        bpmnOutputAssociation = bpmnOutputAssociation.replaceAll("IdToSet", bpmnSubprocessId);
        return bpmnOutputAssociation;
    }

    private Node createBPMNDiagramDataOutputAssociationAsNode(BPMNSubprocess bpmnSubprocess, Document d) throws IOException, SAXException {
        LOG.info("BPMN Subprocess ID {} {}", bpmnSubprocess.getId(), bpmnSubprocess.getIncomingTestFlow().size());
        String bpmnDiagramDataOutputAssociation = createBPMNDiagramDataOutputAssociation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess.getBuildPlan(), d, bpmnDiagramDataOutputAssociation, true);
    }

    public String createBPMNDiagramDataOutputAssociation(BPMNSubprocess bpmnSubprocess) throws
        IOException {
        String bpmnDiagramDataOutputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/diagram/BPMNDiagramDataOutputAssociation.xml"));
        // each sequence flow is guaranteed to only two ends
        double dataOutputAssociationXSource = bpmnSubprocess.getX() + 50;
        double dataOutputAssociationYTarget = bpmnSubprocess.getDataObject().getY() + 50;
        bpmnDiagramDataOutputAssociation = bpmnDiagramDataOutputAssociation.replaceAll("IdToSet", bpmnSubprocess.getDataObject().getId());
        bpmnDiagramDataOutputAssociation = bpmnDiagramDataOutputAssociation.replaceAll("xToSet", "" + dataOutputAssociationXSource);
        bpmnDiagramDataOutputAssociation = bpmnDiagramDataOutputAssociation.replaceAll("yToSetMinusDataObjectHeight", "" + dataOutputAssociationYTarget);
        bpmnDiagramDataOutputAssociation = bpmnDiagramDataOutputAssociation.replaceAll("yToSet", "" + bpmnSubprocess.getY());

        return bpmnDiagramDataOutputAssociation;
    }

    private Node createBPMNDataInputAssociationAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("Create BPMN Diagram Data Input Association for subprocess {} with data object {}", bpmnSubprocess.getId(), bpmnSubprocess.getDataObject().getId());
        String dataInputAssociation = createBPMNDataInputAssociation(bpmnSubprocess);
        return this.transformStringToNode(dataInputAssociation);
    }

    public String createBPMNDataInputAssociation(BPMNSubprocess bpmnSubprocess) throws
        IOException {
        String dataInputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataInputAssociation.xml"));
        dataInputAssociation = dataInputAssociation.replaceAll("DataObjectIdToSet", bpmnSubprocess.getDataObject().getId());
        return dataInputAssociation;
    }

    private Node createBPMNDiagramDataInputAssociationAsNode(BPMNSubprocess bpmnSubprocess, Document d) throws IOException, SAXException {
        LOG.info("Create BPMN Diagram Data Input Association for subprocess {} with data object {}", bpmnSubprocess.getId(), bpmnSubprocess.getDataObject().getId());
        String diagramDataInputAssociation = createBPMNDiagramDataInputAssociation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess.getBuildPlan(), d, diagramDataInputAssociation, true);
    }

    public String createBPMNDiagramDataInputAssociation(BPMNSubprocess bpmnSubprocess) throws
        IOException {
        String diagramDataInputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/diagram/BPMNDiagramDataInputAssociation.xml"));
        double dataOutputAssociationYTarget = bpmnSubprocess.getDataObject().getY() + 50;
        double dataOutputAssociationXSource = bpmnSubprocess.getDataObject().getX() + 25;
        double dataOutputAssociationXTarget = bpmnSubprocess.getX() + 50;
        diagramDataInputAssociation = diagramDataInputAssociation.replaceAll("IdToSet", bpmnSubprocess.getDataObject().getId());
        diagramDataInputAssociation = diagramDataInputAssociation.replaceAll("xToSetTarget", "" + dataOutputAssociationXTarget);
        diagramDataInputAssociation = diagramDataInputAssociation.replaceAll("xToSetSource", "" + dataOutputAssociationXSource);
        diagramDataInputAssociation = diagramDataInputAssociation.replaceAll("yToSetMinusDataObjectHeight", "" + bpmnSubprocess.getY());
        diagramDataInputAssociation = diagramDataInputAssociation.replaceAll("yToSet", "" + dataOutputAssociationYTarget);

        return diagramDataInputAssociation;
    }
}
