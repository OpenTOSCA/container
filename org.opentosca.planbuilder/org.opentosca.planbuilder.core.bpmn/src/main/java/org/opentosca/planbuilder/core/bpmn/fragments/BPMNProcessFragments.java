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
import org.opentosca.planbuilder.model.plan.bpmn.BPMNComponentType;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
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
    private final String ServiceInstanceURLVarKeyword = "ServiceInstanceURL";

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPMNProcessFragments() throws ParserConfigurationException {
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public Node transformStringToNode(final String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String createScript(final String scriptName) throws IOException {
        return ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("scripts/" + scriptName + ".groovy"));
    }

    /**
     * creates relationshipTemplateInstance task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createRelationshipTemplateInstance Node
     */
    public String createRelationshipTemplateInstance(final BPMNSubprocess bpmnSubprocess) throws IOException {
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
    public String createNodeTemplateInstance(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String nodeTemplateId = bpmnSubprocess.getNodeTemplate().getId();
        String createNodeInstance = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeTemplateInstanceScriptTask.xml"));
        createNodeInstance = createNodeInstance.replaceAll("NodeTemplateInstance_IdToReplace", bpmnSubprocess.getId());
        createNodeInstance = createNodeInstance.replaceAll("NodeTemplateToSet", bpmnSubprocess.getNodeTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");
        createNodeInstance = createNodeInstance.replaceAll("ResultVariableToSet", parentId.replace("-", "_"));
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
    public String createNodeOperation(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String callNodeOperation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeOperationScriptTask.xml"));
        callNodeOperation = callNodeOperation.replaceAll("CallNodeOperation_IdToReplace", bpmnSubprocess.getId());
        callNodeOperation = callNodeOperation.replace("NamespaceToSet", bpmnSubprocess.getBuildPlan().getServiceTemplate().getTargetNamespace());
        callNodeOperation = callNodeOperation.replaceAll("CsarToSet", bpmnSubprocess.getBuildPlan().getCsarName());
        callNodeOperation = callNodeOperation.replaceAll("ServiceTemplateNameToSet", bpmnSubprocess.getBuildPlan().getServiceTemplate().getId().trim());
        callNodeOperation = callNodeOperation.replace("NodeTemplateToSet", bpmnSubprocess.getHostingNodeTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId();
        String prefix = BPMNComponentType.DATA_OBJECT_REFERENCE + "_" + BPMNComponentType.DATA_OBJECT;
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
        LOG.info("INPUTNUMAEA3:");
        if (bpmnSubprocess.getOutputParameterNames() != null) {
            callNodeOperation = callNodeOperation.replace("OutputParamNamesToSet", bpmnSubprocess.getOutputParameterNames());
        }
        if (bpmnSubprocess.getOutputParameterValues() != null) {
            callNodeOperation = callNodeOperation.replace("OutputParamValuesToSet", bpmnSubprocess.getOutputParameterValues());
        }

        String[] original = callNodeOperation.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];

        StringBuilder inputParameterBuilder = new StringBuilder();
        if ((bpmnSubprocess.getInputParameterNames() != null) && !(bpmnSubprocess.getInputParameterNames().isBlank()) && (bpmnSubprocess.getInputParameterValues() != null)) {
            int counter = 0;
            for (final String inputParameterName : bpmnSubprocess.getInputParameterNames().split(",")) {
                String inputParameterValue = bpmnSubprocess.getInputParameterValues().split(",")[counter];
                if (inputParameterName.equals("ContainerPorts")) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append("String!" + inputParameterValue).append("</camunda:inputParameter>");
                } else if (inputParameterName.equals("Script")) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append("String!" + inputParameterValue).append("</camunda:inputParameter>");
                } else if (inputParameterName.equals("ImageLocation") && (bpmnSubprocess.getParentProcess().getDeploymentArtifactString() != null)) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append(bpmnSubprocess.getParentProcess().getDeploymentArtifactString()).append("</camunda:inputParameter>");
                } else if (inputParameterValue.startsWith("VALUE")) {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append(inputParameterValue).append("</camunda:inputParameter>");
                } else {
                    inputParameterBuilder.append("<camunda:inputParameter name=\"Input_").append(inputParameterName).append("\">").append("String!" + inputParameterValue).append("</camunda:inputParameter>");
                }
                counter++;
            }
        }

        //rebuild the template String

        return original[0] + inputParameterBuilder + original[1];
    }

    public String getServiceInstanceURLFromDataObject(final BPMNSubprocess bpmnSubprocess, String template) {
        for (final BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_ST) {
                template = template.replaceAll("ServiceInstanceURLToSet", bpmnDataObject.getServiceInstanceURL());
            } else if (bpmnDataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_INOUT) {
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
    public Node createBPMNUserTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String userTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNUserTask.xml"));
        userTask = userTask.replaceAll("Task_IdToSet", bpmnSubprocess.getId());
        return this.createImportNodeFromString(bpmnSubprocess, userTask);
    }

    /**
     * This method is necessary to prevent the error: import node from different document.
     */
    public void addNodeToBPMN(Node nodeToImport, final BPMNPlan bpmnPlan) {
        nodeToImport = bpmnPlan.getBpmnDocument().importNode(nodeToImport, true);
        bpmnPlan.getBpmnProcessElement().appendChild(nodeToImport);
    }
    // --------------------------
    // Data Objects Begin

    /**
     * Creates a relationship data object which consists of the source & target url.
     */
    public String createRelationDataObjectReference(final BPMNDataObject dataObject) throws IOException {
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
    public String createNodeDataObjectReference(final BPMNDataObject dataObject) throws IOException {
        String nodeDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeDataObjectReference.xml"));
        nodeDataObject = nodeDataObject.replaceAll("NodeInstanceURLToSet", "\\${" + dataObject.getNodeInstanceURL() + "}");
        nodeDataObject = nodeDataObject.replaceAll("NodeTemplateToSet", dataObject.getNodeTemplate());
        nodeDataObject = nodeDataObject.replaceAll("IdToSet", dataObject.getId());
        StringBuilder properties = new StringBuilder();
        // property is structured as propertyName#propertyValue
        if (!dataObject.getProperties().isEmpty()) {
            for (final String property : dataObject.getProperties()) {
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
    public String createServiceInstanceDataObjectReference(final BPMNDataObject dataObject, final BPMNPlan bpmnPlan) throws IOException {
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
    public String createInputOutputDataObjectReference(final BPMNDataObject dataObject, final BPMNPlan bpmnPlan) throws IOException {
        String inputOutputDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNInputOutputDataObjectReference.xml"));
        inputOutputDataObject = inputOutputDataObject.replaceAll("ServiceInstanceTaskName", dataObject.getId());
        inputOutputDataObject = inputOutputDataObject.replaceAll("IdToSet", dataObject.getId());
        StringBuilder inputParameters = new StringBuilder();
        StringBuilder inputParameterNames = new StringBuilder();
        for (final String inputParameterName : bpmnPlan.getInputParameters()) {
            inputParameterNames.append(inputParameterName).append(",");
            inputParameters.append("<camunda:inputParameter name='").append(inputParameterName).append("'>").append("\\${").append(inputParameterName).append("}").append("</camunda:inputParameter>");
        }
        StringBuilder outputParameters = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        for (final String outputParameterName : bpmnPlan.getPropertiesOutputParameters().keySet()) {
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
    public void createDataObjectAsNode(final BPMNPlan bpmnPlan, final Document d, final BPMNDataObject dataObject) throws IOException, SAXException {
        BPMNComponentType dataObjectType = dataObject.getDataObjectType();
        String dataObjectReference = "";
        if (dataObjectType == BPMNComponentType.DATA_OBJECT_ST) {
            dataObjectReference = createServiceInstanceDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNComponentType.DATA_OBJECT_NODE) {
            dataObjectReference = createNodeDataObjectReference(dataObject);
        } else if (dataObjectType == BPMNComponentType.DATA_OBJECT_INOUT) {
            dataObjectReference = createInputOutputDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNComponentType.DATA_OBJECT_REL) {
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
    private String createDiagramDataObjectReference(final BPMNDataObject dataObject, final BPMNPlan bpmnPlan) throws IOException {
        String dataObjectReference = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/diagram/BPMNDiagramDataObjectReference.xml"));
        for (final BPMNSubprocess outerSubprocess : bpmnPlan.getSubprocess()) {
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
    public Node createBPMNSubprocessAndComponentsAsNode(final BPMNSubprocess bpmnSubprocess) {
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
    private Node createActivateDataObjectTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String activateDataObjectString = createActivateDataObjectString(bpmnSubprocess);
        return createImportNodeFromString(bpmnSubprocess, activateDataObjectString);
    }

    /**
     * create the template String for a ActivateDataObjectTask node
     *
     * @return template String
     */
    private String createActivateDataObjectString(final BPMNSubprocess bpmnSubprocess) throws IOException {
        BPMNPlan bpmnPlan = bpmnSubprocess.getBuildPlan();
        String activateDataObjectTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNActivateDataObjectTask.xml"));
        activateDataObjectTask = activateDataObjectTask.replace("ActivateDataObject_IdToReplace", bpmnSubprocess.getId());
        activateDataObjectTask = activateDataObjectTask.replace("NameToSet", "Activate data object " + bpmnSubprocess.getDataObject().getId());
        activateDataObjectTask = activateDataObjectTask.replace("DataObjectIdToSet", BPMNComponentType.DATA_OBJECT_REFERENCE + "_" + bpmnSubprocess.getDataObject().getId());
        StringBuilder properties = new StringBuilder();
        StringBuilder propertiesNames = new StringBuilder();

        StringBuilder inputParameterNames = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        if (bpmnSubprocess.getDataObject().getDataObjectType() == BPMNComponentType.DATA_OBJECT_INOUT) {
            for (final String inputParameterName : bpmnSubprocess.getBuildPlan().getInputParameters()) {
                inputParameterNames.append(inputParameterName).append(",");
            }
            StringBuilder outputParameters = new StringBuilder();

            for (final String outputParameterName : bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().keySet()) {
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
            for (final String property : bpmnSubprocess.getDataObject().getProperties()) {
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
    private Node createSetNodePropertiesTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String setNodePropertiesTask = createSetNodePropertiesState(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, setNodePropertiesTask);
    }

    /**
     * create the template String for a SetNodeProperties node
     *
     * @return template String
     */
    private String createSetNodePropertiesState(final BPMNSubprocess bpmnSubprocess) throws IOException {
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
        setNodeProperties = setNodeProperties.replace("NodeInstanceURLToSet", "${" + nodeInstanceURL.replace("-", "_") + "}");
        setNodeProperties = setNodeProperties.replaceAll("NodeTemplateToSet", nodeTemplateId);
        String prefix = BPMNComponentType.DATA_OBJECT_REFERENCE + "_" + BPMNComponentType.DATA_OBJECT;
        String dataObjectReferenceId = parentId.replace("Subprocess", prefix);
        setNodeProperties = setNodeProperties.replaceAll("DataObjectToSet", dataObjectReferenceId);

        List<String> properties = null;
        StringBuilder propertiesToSet = new StringBuilder();
        // find corresponding data object
        if (bpmnSubprocess.getBuildPlan().getDataObjectsList() != null) {
            for (final BPMNDataObject dataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
                // (dataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_NODE) &&
                if (dataObjectReferenceId.contains(dataObject.getId()) && dataObject.getProperties() != null) {
                    properties = dataObject.getProperties();
                }
            }
        }

        StringBuilder inputParamBuilder = new StringBuilder();

        //set input properties
        if (!Objects.requireNonNull(properties).isEmpty()) {
            for (final String property : properties) {
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
    public Node createBPMNSubprocessAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("id {}, type {}", bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessType());
        final String subprocess = createBPMNSubprocess(bpmnSubprocess);

        Node node = this.createImportNodeFromString(bpmnSubprocess, subprocess);

        ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
        ArrayList<Node> flowNodes = new ArrayList<>();
        // add Start Event inside subprocess
        BPMNSubprocess innerStartEvent = new BPMNSubprocess(BPMNComponentType.INNER_START_EVENT, "StartEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerStartEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerStartEvent.setParentProcess(bpmnSubprocess);
        BPMNSubprocess previousIncoming = innerStartEvent;
        // compute the sequence flows before components are added to the xml
        for (final BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            BPMNSubprocess innerSequenceFlow2 = new BPMNSubprocess(BPMNComponentType.SEQUENCE_FLOW, "InnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
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
        BPMNSubprocess innerEndEvent = new BPMNSubprocess(BPMNComponentType.END_EVENT, "EndEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerEndEvent.setParentProcess(bpmnSubprocess);

        BPMNSubprocess innerEndEventSequenceFlow = new BPMNSubprocess(BPMNComponentType.SEQUENCE_FLOW, "InnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
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
        BPMNSubprocess innerErrorEndEvent = new BPMNSubprocess(BPMNComponentType.ERROR_END_EVENT, "ErrorEndEvent_" + bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement());
        innerErrorEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerErrorEndEvent.setParentProcess(bpmnSubprocess);
        for (final BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            subSubprocess.setParentProcess(bpmnSubprocess);
            //Node child = this.createBPMNSubprocessAndComponentsAsNode(subSubprocess);
            if (subSubprocess.getSubprocessType() != BPMNComponentType.SEQUENCE_FLOW && subSubprocess.getSubprocessType() != BPMNComponentType.DATA_OBJECT
                && subSubprocess.getSubprocessType() != BPMNComponentType.DATA_OBJECT_NODE && subSubprocess.getSubprocessType() != BPMNComponentType.DATA_OBJECT_REL
                && subSubprocess.getSubprocessType() != BPMNComponentType.DATA_OBJECT_ST && subSubprocess.getSubprocessType() != BPMNComponentType.INNER_START_EVENT
                && subSubprocess.getSubprocessType() != BPMNComponentType.START_EVENT && subSubprocess.getSubprocessType() != BPMNComponentType.END_EVENT) {
                for (final Integer errorId : bpmnSubprocess.getErrorEventIds()) {
                    bpmnSubprocess.getBuildPlan().getIdForErrorInnerFlowAndIncrement();
                    BPMNSubprocess innerErrorSequenceFlow = new BPMNSubprocess(BPMNComponentType.SEQUENCE_FLOW, "ErrorInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
                    BPMNSubprocess innerBoundaryEvent = new BPMNSubprocess(BPMNComponentType.EVENT, "BoundaryEvent_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
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

            Node child = this.createBPMNSubprocessAndComponentsAsNode(subSubprocess);
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(child);
        }

        Node endEventNode = this.createBPMNSubprocessAndComponentsAsNode(innerEndEvent);
        bpmnSubprocess.getBpmnSubprocessElement().appendChild(endEventNode);
        for (final Node flowNode : flowNodes) {
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(flowNode);
        }
        for (final BPMNSubprocess flowNode : errorFlowElements) {
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
    private Node createImportNodeFromString(final BPMNSubprocess bpmnSubprocess, final String s) throws
        IOException, SAXException {
        Node transformedNode = this.transformStringToNode(s);
        ArrayList<String> incomingFlowIds;
        ArrayList<String> outgoingFlowIds;
        LOG.info("DER TYPE {} {}", bpmnSubprocess.getSubprocessType(), bpmnSubprocess.getId());
        Document doc = bpmnSubprocess.getBpmnDocument();
        if (bpmnSubprocess.getSubprocessType() == BPMNComponentType.ERROR_END_EVENT) {
            outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:endEvent>";
            int end = s.indexOf(closingTag);
            StringBuilder result = new StringBuilder();
            for (final String flowId : outgoingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            return getResultCreateNode(bpmnSubprocess, s, doc, begin, closingTag, end, result);
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNComponentType.END_EVENT) {
            outgoingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            if (bpmnSubprocess.getId().contains("Error")) {
                outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            }
            String[] original = s.split("</bpmn:endEvent>");
            StringBuilder result = new StringBuilder();
            for (final String flowId : outgoingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:endEvent>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNComponentType.EVENT) {
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:boundaryEvent>";
            int end = s.indexOf(closingTag);
            StringBuilder result = new StringBuilder();
            for (final String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            return getResultCreateNode(bpmnSubprocess, s, doc, begin, closingTag, end, result);
        }
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.SEQUENCE_FLOW
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.SUBPROCESS && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.EVENT && bpmnSubprocess.getSubprocessType() != BPMNComponentType.SUBPROCESS_ERROR_BOUNDARY
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.USER_TASK && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.START_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNComponentType.INNER_START_EVENT) {

            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:scriptTask>");
            StringBuilder result = new StringBuilder();
            for (final String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (final String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:scriptTask>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNComponentType.SUBPROCESS) {
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:subProcess>");
            StringBuilder result = new StringBuilder();
            for (final String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (final String flowId : outgoingFlowIds) {
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
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNComponentType.USER_TASK) {
            incomingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:userTask>");
            StringBuilder result = new StringBuilder();
            for (final String flowId : incomingFlowIds) {
                result.append("<bpmn:incoming>").append(flowId).append("</bpmn:incoming>");
            }
            for (final String flowId : outgoingFlowIds) {
                result.append("<bpmn:outgoing>").append(flowId).append("</bpmn:outgoing>");
            }
            result = new StringBuilder(original[0] + result + "</bpmn:userTask>");
            Node transformedChangedNode = this.transformStringToNode(result.toString());
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNComponentType.INNER_START_EVENT || bpmnSubprocess.getBpmnSubprocessType() == BPMNComponentType.START_EVENT) {
            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:startEvent>");
            StringBuilder result = new StringBuilder();
            for (final String flowId : outgoingFlowIds) {
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

    private Node getResultCreateNode(final BPMNSubprocess bpmnSubprocess, final String s, final Document doc, final int begin, final String closingTag, final int end, StringBuilder result) throws SAXException, IOException {
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
    private Node createImportNodeFromString(final BPMNPlan bpmnPlan, final Document d, final String s, final boolean diagramNode) throws
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
    private String createBPMNSubprocess(final BPMNSubprocess bpmnSubprocess) throws IOException {
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
    public Node createBPMNStartEventAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String startEvent = createBPMNStartEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, startEvent);
    }

    /**
     * create a start bpmn error definition Node from template String
     *
     * @param id id of the error event definition
     * @return created node
     */
    public Node createBPMNErrorEventDefinitionAsNode(final int id) throws IOException, SAXException {
        final String bpmnErrorEventDefinition = createBPMNErrorEventDefinition(id);
        return this.transformStringToNode(bpmnErrorEventDefinition);
    }

    /**
     * create the template String for an error event definition Node
     *
     * @param id id of the error event definition
     * @return template String
     */
    public String createBPMNErrorEventDefinition(final int id) throws IOException {
        final String idPrefix = BPMNComponentType.EVENT.toString();
        String bpmnErrorEventDefinition = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNError.xml"));
        bpmnErrorEventDefinition = bpmnErrorEventDefinition.replaceAll("IdToSet", idPrefix + id);
        bpmnErrorEventDefinition = bpmnErrorEventDefinition.replaceAll("NameToSet", "Error Event");
        return bpmnErrorEventDefinition;
    }

    private Node createBPMNSequenceFlowAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("BPMN Subprocess Id {} {}", bpmnSubprocess.getId(), bpmnSubprocess.getIncomingTestFlow().size());
        String sequenceFlow = createBPMNSequenceFlow(bpmnSubprocess.getId(),
            bpmnSubprocess.getOuterFlow().iterator().next().getId(),
            bpmnSubprocess.getIncomingTestFlow().iterator().next().getId()
        );
        return this.createImportNodeFromString(bpmnSubprocess, sequenceFlow);
    }

    public String createBPMNSequenceFlow(final String FlowID, final String incomingFlowName, final String outgoingFlowName) throws
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
    public Node createSubprocessErrorBoundaryEventAsNode(final BPMNSubprocess bpmnSubprocess, final int errorId) throws
        IOException, SAXException {
        final String bpmnSubprocessErrorBoundaryEvent = createBPMNSubprocessErrorBoundaryEvent(bpmnSubprocess, errorId);
        return this.createImportNodeFromString(bpmnSubprocess, bpmnSubprocessErrorBoundaryEvent);
    }

    /**
     * create an error boundary event String
     */
    public Node createTaskErrorBoundaryEventAsNode(final BPMNSubprocess innerEvent, final BPMNSubprocess bpmnSubprocess,
                                                   final int errorId) throws IOException, SAXException {
        final String bpmnTaskErrorBoundaryEvent = createBPMNTaskErrorBoundaryEvent(bpmnSubprocess, errorId);
        innerEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        return this.createImportNodeFromString(innerEvent, bpmnTaskErrorBoundaryEvent);
    }

    /**
     * create a subprocess error boundary event node from template String
     *
     * @return template String
     */
    public String createBPMNSubprocessErrorBoundaryEvent(final BPMNSubprocess bpmnSubprocess, final int id) throws IOException {
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
    public String createBPMNTaskErrorBoundaryEvent(final BPMNSubprocess bpmnSubprocess, final int id) throws IOException {
        String bpmnTaskErrorBoundaryEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNTaskErrorBoundaryEvent.xml"));
        String idPrefix = BPMNComponentType.SUBPROCESS_ERROR_BOUNDARY.toString();
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
    public String createBPMNStartEvent(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String startEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        startEvent = startEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        return startEvent;
    }

    private ArrayList<String> computeOutgoingFlowElements(final BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getFlowElements();
            }
            for (final BPMNSubprocess flowElement : flowElements) {
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

    private ArrayList<String> computeErrorOutgoingFlowElements(final BPMNSubprocess bpmnSubprocess) {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getErrorFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getErrorFlowElements();
            }
            for (final BPMNSubprocess flowElement : flowElements) {
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

    private ArrayList<String> computeIncomingFlowElements(final BPMNSubprocess bpmnSubprocess) {
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

    private void computeIncomingFlowIds(final BPMNSubprocess bpmnSubprocess, final ArrayList<String> test, final ArrayList<BPMNSubprocess> flowElements) {
        for (final BPMNSubprocess flowElement : flowElements) {
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

    private ArrayList<String> computeIncomingErrorFlowElements(final BPMNSubprocess bpmnSubprocess) {
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
    public Node createBPMNEndEventAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String endEvent = createBPMNEndEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, endEvent);
    }

    /**
     * create a bpmn end event template String
     *
     * @return template String
     */
    public String createBPMNEndEvent(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String bpmnEndEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        bpmnEndEvent = bpmnEndEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        return bpmnEndEvent;
    }

    /**
     * create a bpmn error end event node from template String
     *
     * @return created node
     */
    public Node createBPMNErrorEndEventAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String bpmnErrorEndEvent = createBPMNErrorEndEvent(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, bpmnErrorEndEvent);
    }

    /**
     * create a bpmn error end event template String
     *
     * @return template String
     */
    public String createBPMNErrorEndEvent(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String bpmnErrorEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNErrorEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForErrorInnerFlowAndIncrement();
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        // This is currently a high assumption that we only have one specific error
        int errorId = bpmnSubprocess.getErrorEventIds().get(0);
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("errorRefToSet", "Error_Event" + errorId);
        StringBuilder incomingBoundaryLinks = new StringBuilder();
        for (final BPMNSubprocess subprocess : bpmnSubprocess.getIncomingLinks()) {
            incomingBoundaryLinks.append("<bpmn:incoming>").append(subprocess.getId()).append("</bpmn:incoming>");
        }

        bpmnErrorEvent = bpmnErrorEvent.replaceAll("<bpmn:incoming>Flow_Input</bpmn:incoming>", incomingBoundaryLinks.toString());

        final String idPrefix = BPMNComponentType.EVENT.toString();
        bpmnErrorEvent = bpmnErrorEvent.replaceAll("ErrorEventDefinitionIdToSet", "ErrorDefinition_" + idPrefix + id);
        return bpmnErrorEvent;
    }

    /**
     * create a callNodeOperation node from template String
     *
     * @return created Node
     */
    private Node createCallNodeOperationTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String nodeOperation = createNodeOperation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, nodeOperation);
    }

    /**
     * create a NodeTemplateInstanceTask node from template String
     *
     * @return template String
     */
    private Node createNodeTemplateInstanceTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String nodeTemplateInstance = createNodeTemplateInstance(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, nodeTemplateInstance);
    }

    /**
     * create a BpmnServiceInstance node from template String
     *
     * @return created node
     */
    private Node createBPMNCreateServiceInstanceAsNode(final BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String serviceInstance = createServiceInstance(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, serviceInstance);
    }

    /**
     * create a Set Service Template node from template String
     *
     * @return created Node
     */
    public Node createSetServiceTemplateStateAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String setServiceTemplateState = createSetServiceTemplateState(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, setServiceTemplateState);
    }

    /**
     * create a SetServiceTemplate template String
     *
     * @return template String
     */
    private String createSetServiceTemplateState(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String setState = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetStateTask.xml"));
        setState = setState.replace("Activity_IdToSet", bpmnSubprocess.getId());
        LOG.info("DERSTATEIST");
        setState = setState.replaceAll("StateToSet", "CREATED");
        if (bpmnSubprocess.getInstanceState() != null) {
            setState = setState.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        }
        LOG.info(bpmnSubprocess.getInstanceState());
        for (final BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnSubprocess.getNodeTemplate() != null) {
                if (bpmnDataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_NODE && bpmnDataObject.getNodeTemplate().equals(bpmnSubprocess.getNodeTemplate().getId())) {
                    setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getNodeInstanceURL() + "}");
                }
            } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
                if (bpmnDataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_REL && bpmnDataObject.getRelationshipTemplate().equals(bpmnSubprocess.getRelationshipTemplate().getId())) {
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
    private Node createRelationshipTemplateInstanceAsNode(final BPMNSubprocess bpmnSubprocess) throws
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
    public String createServiceInstance(final BPMNSubprocess bpmnSubprocess) throws IOException {
        String createServiceInstance = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        createServiceInstance = createServiceInstance.replaceAll("ResultVariableToSet", bpmnSubprocess.getResultVariableName());
        createServiceInstance = createServiceInstance.replaceAll("Subprocess_IdToSet", bpmnSubprocess.getId());
        createServiceInstance = createServiceInstance.replaceAll("StateToSet", "CREATING");
        createServiceInstance = createServiceInstance.replaceAll("DataObjectToSet", bpmnSubprocess.getParentProcess().getDataObject().getId());
        return createServiceInstance;
    }

    public Node createOutputParamsTaskAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String outputParamsTask = createOutputParamsTask(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, outputParamsTask);
    }

    private String createOutputParamsTask(final BPMNSubprocess bpmnSubprocess) throws IOException {
        LOG.info("Create output parameter task of id {}", bpmnSubprocess.getId());
        String outputParameterTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateOutputParameterTask.xml"));
        outputParameterTask = outputParameterTask.replace("Activity_IdToSet", bpmnSubprocess.getId());
        final BPMNPlan bpmnPlan = bpmnSubprocess.getBuildPlan();
        // find data object
        if (bpmnSubprocess.getBuildPlan().getDataObjectsList() != null) {
            for (final BPMNDataObject dataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
                if (dataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_INOUT) {
                    outputParameterTask = outputParameterTask.replaceAll("DataObjectToSet", BPMNComponentType.DATA_OBJECT_REFERENCE + "_" + dataObject.getId());
                }
            }
        }
        StringBuilder outputParameters = new StringBuilder();
        StringBuilder outputParameterNames = new StringBuilder();
        final String concat = "concat(";
        for (final String outputParameterName : bpmnPlan.getPropertiesOutputParameters().keySet()) {
            outputParameterNames.append(outputParameterName).append(",");
            if (outputParameterName.equals(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName))) {
                outputParameters.append("<camunda:inputParameter name='Output.").append(outputParameterName).append("'>").append("\\${").append(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName)).append("}").append("</camunda:inputParameter>");
            } else {
                String outputParameterValue = "";
                // this is the case where we have in the service template some property mapping and each property is associated to a node template.
                // To find the data object which holds the correct properties we split at the first 'point'
                for (final BPMNDataObject dataObject : bpmnPlan.getDataObjectsList()) {
                    if (dataObject.getDataObjectType() == BPMNComponentType.DATA_OBJECT_NODE) {
                        outputParameterValue = bpmnPlan.getPropertiesOutputParameters().get(outputParameterName);
                        // schema: NodeTemplate.Properties.PropertyName
                        String[] outputParameterValueParts = outputParameterValue.split(",");
                        for (final String outputParameterValuePart : outputParameterValueParts) {
                            if (outputParameterValuePart.contains(".")) {
                                String nodeTemplate = outputParameterValuePart.split("\\.")[0].trim();
                                if (dataObject.getNodeTemplate().equals(nodeTemplate)) {
                                    String outputParameterPartValue = outputParameterValuePart.replaceAll(nodeTemplate, BPMNComponentType.DATA_OBJECT_REFERENCE + "_" + dataObject.getId());
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

    public void addDataAssociations(final BPMNPlan buildPlan, final Document d, final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
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
                                    if (bpmnSubprocess.getDataObject().getDataObjectType() != BPMNComponentType.DATA_OBJECT_INOUT &&
                                        bpmnSubprocess.getDataObject().getDataObjectType() != BPMNComponentType.DATA_OBJECT_ST) {
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

    public void addTaskDataAssociations(final BPMNPlan buildPlan, final Document d, final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
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

    private Node createBPMNPropertyAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("Create BPMN Property with id {}", "Property_" + bpmnSubprocess.getId());
        String bpmnProperty = createBPMNProperty(bpmnSubprocess.getDataObject().getId());
        return this.transformStringToNode(bpmnProperty);
    }

    private String createBPMNProperty(final String bpmnSubprocessId) throws IOException {
        String property = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNProperty.xml"));
        property = property.replaceAll("IdToSet", bpmnSubprocessId);
        property = property.replaceAll("targetRef", bpmnSubprocessId);
        return property;
    }

    private Node createBPMNDataOutputAssociationAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String bpmnDataOutputAssociation = createBPMNDataOutputAssociation(bpmnSubprocess.getDataObject().getId());
        return this.transformStringToNode(bpmnDataOutputAssociation);
    }

    public String createBPMNDataOutputAssociation(final String bpmnSubprocessId) throws
        IOException {
        String bpmnOutputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataOutputAssociation.xml"));
        bpmnOutputAssociation = bpmnOutputAssociation.replaceAll("IdToSet", bpmnSubprocessId);
        return bpmnOutputAssociation;
    }

    private Node createBPMNDiagramDataOutputAssociationAsNode(final BPMNSubprocess bpmnSubprocess, final Document d) throws IOException, SAXException {
        LOG.info("BPMN Subprocess ID {} {}", bpmnSubprocess.getId(), bpmnSubprocess.getIncomingTestFlow().size());
        String bpmnDiagramDataOutputAssociation = createBPMNDiagramDataOutputAssociation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess.getBuildPlan(), d, bpmnDiagramDataOutputAssociation, true);
    }

    public String createBPMNDiagramDataOutputAssociation(final BPMNSubprocess bpmnSubprocess) throws
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

    private Node createBPMNDataInputAssociationAsNode(final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("Create BPMN Diagram Data Input Association for subprocess {} with data object {}", bpmnSubprocess.getId(), bpmnSubprocess.getDataObject().getId());
        String dataInputAssociation = createBPMNDataInputAssociation(bpmnSubprocess);
        return this.transformStringToNode(dataInputAssociation);
    }

    public String createBPMNDataInputAssociation(final BPMNSubprocess bpmnSubprocess) throws
        IOException {
        String dataInputAssociation = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataInputAssociation.xml"));
        dataInputAssociation = dataInputAssociation.replaceAll("DataObjectIdToSet", bpmnSubprocess.getDataObject().getId());
        return dataInputAssociation;
    }

    private Node createBPMNDiagramDataInputAssociationAsNode(final BPMNSubprocess bpmnSubprocess, final Document d) throws IOException, SAXException {
        LOG.info("Create BPMN Diagram Data Input Association for subprocess {} with data object {}", bpmnSubprocess.getId(), bpmnSubprocess.getDataObject().getId());
        String diagramDataInputAssociation = createBPMNDiagramDataInputAssociation(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess.getBuildPlan(), d, diagramDataInputAssociation, true);
    }

    public String createBPMNDiagramDataInputAssociation(final BPMNSubprocess bpmnSubprocess) throws
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
