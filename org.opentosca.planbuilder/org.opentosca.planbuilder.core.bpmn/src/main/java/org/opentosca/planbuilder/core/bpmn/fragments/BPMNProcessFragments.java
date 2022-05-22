package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the DOM Parsers fails
     */
    public BPMNProcessFragments() throws ParserConfigurationException {
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

    public String createScript(String scriptName) throws IOException {
        String script = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("scripts/" + scriptName + ".groovy"));
        return script;
    }

    /**
     * create sequence flow node inside a bpmnSubprocess
     *
     * @param sourceBpmnSubprocess source for the sequence flow
     * @param targetBpmnSubprocess target for the sequence flow
     * @return sequenceFlow Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNSequenceFlow2(BPMNSubprocess sourceBpmnSubprocess, BPMNSubprocess targetBpmnSubprocess) throws IOException, SAXException {
        String sequenceFlow = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        sequenceFlow = sequenceFlow.replaceAll("Flow_IdToReplace", sourceBpmnSubprocess.getOutflow());
        sequenceFlow = sequenceFlow.replaceAll("SourceToReplace", sourceBpmnSubprocess.getId());
        sequenceFlow = sequenceFlow.replaceAll("TargetToReplace", targetBpmnSubprocess.getId());
        LOG.info("PPPPPPPPPPPPPPPPPPPPPPP");
        LOG.info(sourceBpmnSubprocess.getId());
        LOG.info(targetBpmnSubprocess.getId());
        Node sequenceFlowNode = this.createImportNodeFromString(sourceBpmnSubprocess, sequenceFlow);
        return sequenceFlowNode;
    }

    /**
     * create sequence flow node for error events of the outside flow
     *
     * @param sourceBpmnSubprocess source for the sequence flow
     * @param targetBpmnSubprocess target for the sequence flow
     * @return sequenceFlow Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNOuterErrorSequenceFlow(BPMNSubprocess sourceBpmnSubprocess, BPMNSubprocess targetBpmnSubprocess) throws IOException, SAXException {
        String errorOuterFlow = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        errorOuterFlow = errorOuterFlow.replaceAll("Flow_IdToReplace", sourceBpmnSubprocess.getErrorOutflow());
        LOG.info(sourceBpmnSubprocess.getId());
        if (!sourceBpmnSubprocess.getId().contains("userTask")) {
            //String bpmnSubprocessId = bpmnSubprocess.getId().replace("errorEvent", "");
            errorOuterFlow = errorOuterFlow.replaceAll("SourceToReplace", "Event" + sourceBpmnSubprocess.getId());
        } else {
            errorOuterFlow = errorOuterFlow.replaceAll("SourceToReplace", sourceBpmnSubprocess.getId());
        }
        errorOuterFlow = errorOuterFlow.replaceAll("TargetToReplace", targetBpmnSubprocess.getId());
        LOG.info("PPPPPPPPPPPPPPP2");
        LOG.info(sourceBpmnSubprocess.getId());
        Node sequenceFlowNode = this.createImportNodeFromString(sourceBpmnSubprocess, errorOuterFlow);
        return sequenceFlowNode;
    }

    /**
     * create sequence flow node for error events of the inside flow
     *
     * @param SourceBpmnSubprocess source for the sequence flow
     * @param targetBpmnSubprocess target for the sequence flow
     * @return sequenceFlow Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNInnerErrorSequenceFlow(BPMNSubprocess SourceBpmnSubprocess, BPMNSubprocess targetBpmnSubprocess) throws IOException, SAXException {
        String errorInnerFlow = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        errorInnerFlow = errorInnerFlow.replaceAll("Flow_IdToReplace", SourceBpmnSubprocess.getErrorInnerflow());
        errorInnerFlow = errorInnerFlow.replaceAll("SourceToReplace", SourceBpmnSubprocess.getId());
        errorInnerFlow = errorInnerFlow.replaceAll("TargetToReplace", targetBpmnSubprocess.getId());
        LOG.info("PPPPPPPPPPPPPPP2");
        LOG.info(targetBpmnSubprocess.getId());
        Node sequenceFlowNode = this.createImportNodeFromString(SourceBpmnSubprocess, errorInnerFlow);
        return sequenceFlowNode;
    }

    /**
     * creates relationshipTemplateInstance task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createRelationshipTemplateInstance Node
     * @throws IOException
     * @throws SAXException
     */
    public String createRelationshipTemplateInstance(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        String idPrefix = BPMNSubprocessType.SUBPROCESS.toString();
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

        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_ST) {
                createRelationshipInstance = createRelationshipInstance.replaceAll("ServiceInstanceURLToSet", bpmnDataObject.getServiceInstanceURL());
            }
        }
        //Node relationshipInstanceNode = this.transformStringToNode(template);
        //bpmnSubprocess.setBpmnSubprocessElement((Element) relationshipInstanceNode);
        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);
        return createRelationshipInstance;
    }

    /**
     * creates NodeTemplateInstance task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createNodeTemplateInstance Node
     * @throws IOException
     * @throws SAXException
     */
    public String createNodeTemplateInstance(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        String prefix = BPMNSubprocessType.SUBPROCESS.toString();
        String nodeTemplateId = bpmnSubprocess.getNodeTemplate().getId();
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("NodeTemplateInstance_IdToReplace", bpmnSubprocess.getId());
        template = template.replaceAll("NodeTemplateToSet", bpmnSubprocess.getNodeTemplate().getId());
        String parentId = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");
        template = template.replaceAll("ResultVariableToSet", parentId);
        template = template.replaceAll("NodeTemplateToSet", nodeTemplateId);
        template = template.replaceAll("Flow_Input", "Flow_" + sourceId);
        template = template.replaceAll("Flow_Output", "Flow_" + id);
        template = template.replaceAll("StateToSet", "INITIAL");
        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_ST) {
                template = template.replaceAll("ServiceInstanceURLToSet", bpmnDataObject.getServiceInstanceURL());
            }
        }

        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);

        return template;
    }

    /**
     * creates NodeOperation task node inside a bpmnSubprocess
     *
     * @param bpmnSubprocess the subprocess
     * @return createNodeOperation Node
     * @throws IOException
     * @throws SAXException
     */
    public String createNodeOperation(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateNodeOperationScriptTask.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        template = template.replaceAll("CallNodeOperation_IdToReplace", bpmnSubprocess.getId());
        //template = template.replaceAll("ServiceInstanceURLToSet", bpmnSubprocess.getServiceInstanceURL());
        // bpmnSubprocess.getBuildPlan().getServiceTemplate().getTargetNamespace();
        template = template.replaceAll("CsarToSet", bpmnSubprocess.getBuildPlan().getCsarName());
        //template = template.replaceAll("NodeTemplateToSet", bpmnSubprocess.getNodeTemplate().getId());
        template = template.replaceAll("NodeTemplateToSet", bpmnSubprocess.getParentProcess().getNodeTemplate().getId());
        //template = template.replaceAll("InterfaceToSet", Interface);
        //template = template.replaceAll("OperationToSet", Operation);
        //template = template.replaceAll("InputParamNamesToSet", InputParamNames);
        //template = template.replaceAll("InputParamValuesToSet", InputParamValues);
        //template = template.replaceAll("OutputParamNamesToSet", OutputParamNames);
        String parentId = bpmnSubprocess.getParentProcess().getId();
        String prefix = BPMNSubprocessType.DATA_OBJECT_REFERENCE.toString() + "_" + BPMNSubprocessType.DATA_OBJECT.toString();
        String dataObjectReferenceId = parentId.replace("Subprocess", prefix);
        template = template.replaceAll("DataObjectToSet", dataObjectReferenceId);
        template = template.replaceAll("Flow_Input", "Flow_" + sourceId);
        template = template.replaceAll("Flow_Output", "Flow_" + id);
        //Node callNodeOperationNode = this.transformStringToNode(template);

        if (bpmnSubprocess.getServiceInstanceURL() != null) {
            template = template.replaceAll("ServiceInstanceURLToSet", bpmnSubprocess.getServiceInstanceURL());
        }
        LOG.info("interface variable: " + bpmnSubprocess.getInterfaceVariable());
        if (bpmnSubprocess.getInterfaceVariable() != null) {
            template = template.replaceAll("InterfaceToSet", bpmnSubprocess.getInterfaceVariable());
        }
        if (bpmnSubprocess.getOperation() != null) {
            template = template.replaceAll("OperationToSet", bpmnSubprocess.getOperation());
        }
        if (bpmnSubprocess.getInputparamnames() != null) {
            template = template.replaceAll("InputParamNamesToSet", bpmnSubprocess.getInputparamnames());
        }
        if (bpmnSubprocess.getInputparamvalues() != null) {
            template = template.replaceAll("InputParamValuesToSet", bpmnSubprocess.getInputparamvalues());
        }
        if (bpmnSubprocess.getOutputparamnames() != null) {
            template = template.replaceAll("OutputParamNamesToSet", bpmnSubprocess.getOutputparamnames());
        }
        if (bpmnSubprocess.getOutputparamvalues() != null) {
            template = template.replaceAll("OutputParamValuesToSet", bpmnSubprocess.getOutputparamvalues());
        }

        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);

        String[] original = template.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];

        String inputparambuilder = "";
        if ((bpmnSubprocess.getInputparamnames() != null) && (bpmnSubprocess.getInputparamvalues() != null)) {
            int counter = 0;
            for (String namestring : bpmnSubprocess.getInputparamnames().split(",")) {
                String namestringvalue = bpmnSubprocess.getInputparamvalues().split(",")[counter];
                if (namestring.equals("DockerEngineURL")) {
                    inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + namestring + "\">" + "String!${DockerEngineURL}" + "</camunda:inputParameter>";
                } else if (namestring.equals("ContainerPorts")) {
                    inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + namestring + "\">" + "String!80->9990;" + "</camunda:inputParameter>";
                } else if (namestring.equals("ImageLocation") && (bpmnSubprocess.getParentProcess().getDAstring() != null)) {
                    inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + namestring + "\">" + bpmnSubprocess.getParentProcess().getDAstring() + "</camunda:inputParameter>";
                } else {
                    inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + namestring + "\">" + namestringvalue + "</camunda:inputParameter>";
                }
                counter++;
            }
        }

        //rebuild the template String
        String resultstring = original[0] + inputparambuilder + original[1];

        return resultstring;
    }

    /**
     * create EndEvent Node for the outside process
     *
     * @param bpmnSubprocess the subprocess
     * @param name           name of the end event node
     * @return outer end event node
     * @throws IOException
     * @throws SAXException
     */
    public Node createOuterBPMNEndEvent(BPMNSubprocess bpmnSubprocess, String name) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        int sourceId = id - 1;
        template = template.replaceAll("Event_IdToReplace", name);
        //template = template.replaceAll("Flow_Input", "OuterFlow_" + sourceId);
        Node endEventNode = this.transformStringToNode(template);
        //bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
        return this.createImportNodeFromString(bpmnSubprocess, template);
    }

    /**
     * create ErrorEndEvent Node for the outside process
     *
     * @param bpmnSubprocess the subprocess
     * @param name           name of the error end event node
     * @return outer end event error node
     * @throws IOException
     * @throws SAXException
     */
    public Node createOuterBPMNErrorEndEvent(BPMNSubprocess bpmnSubprocess, String name) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        int sourceId = id - 1;
        template = template.replaceAll("Event_IdToReplace", name);
        String incomingEdges = "";

        LOG.info(incomingEdges);
        template = template.replaceAll("<bpmn:incoming>Flow_Input</bpmn:incoming>", incomingEdges);
        LOG.info("OUTERENDEVENT");
        LOG.info(template);
        Node endEventNode = this.transformStringToNode(template);
        bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
        return endEventNode;
    }

    /**
     * create bpmn user task node
     *
     * @param bpmnSubprocess the subprocess
     * @return bpmn userTask node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNUserTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String userTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNUserTask.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        int sourceId = id - 1;
        userTask = userTask.replaceAll("Task_IdToSet", bpmnSubprocess.getId());
        String incomingEdges = "";
        //for (BPMNSubprocess incomingSubprocess : bpmnSubprocess.getIncomingLinks()) {
        //  String incomingSubprocessId = incomingSubprocess.getId().replace("errorEventSubprocess", "Subprocess");
        //incomingSubprocessId = incomingSubprocess.getId().replace("errorEvent", "");
        //incomingEdges = incomingEdges + "<bpmn:incoming>ErrorOuterFlow_" + incomingSubprocessId + "</bpmn:incoming>";
        // }
        LOG.info(incomingEdges);
        //userTask = userTask.replaceAll("<bpmn:incoming>FlowIdToSet</bpmn:incoming>", incomingEdges);

        LOG.info(userTask);
        //userTask = userTask.replaceAll("OuterFlowIdToSet", "OuterFlow_" + sourceId);
        Node userTaskAsNode = this.createImportNodeFromString(bpmnSubprocess, userTask);
        //bpmnSubprocess.setErrorOutflow("OuterFlow_" + sourceId);
        return userTaskAsNode;
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
     * Creates a relationship data object which consists of the source & targeturl.
     *
     * @param dataObject
     * @param bpmnPlan
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public String createRelationDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException, SAXException {
        String relationshipDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNRelationshipDataObjectReference.xml"));
        relationshipDataObject = relationshipDataObject.replaceAll("RelationshipInstanceURLToSet", "\\${" + dataObject.getRelationshipInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("RelationshipTemplateToSet", dataObject.getRelationshipTemplate());
        relationshipDataObject = relationshipDataObject.replaceAll("SourceURLToSet", "\\${" + dataObject.getSourceInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("TargetURLToSet", "\\${" + dataObject.getTargetInstanceURL() + "}");
        relationshipDataObject = relationshipDataObject.replaceAll("IdToSet", dataObject.getId());
        return relationshipDataObject;
    }

    /**
     * Creates a node instance data object which consists of the nodetemplate and its properties
     *
     * @param dataObject
     * @param bpmnPlan
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public String createNodeDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException, SAXException {
        String nodeDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNNodeDataObjectReference.xml"));
        nodeDataObject = nodeDataObject.replaceAll("NodeInstanceURLToSet", "\\${" + dataObject.getNodeInstanceURL() + "}");
        nodeDataObject = nodeDataObject.replaceAll("NodeTemplateToSet", dataObject.getNodeTemplate());
        nodeDataObject = nodeDataObject.replaceAll("IdToSet", dataObject.getId());
        String properties = "";
        // property is structured as propertyName#propertyValue
        if (!dataObject.getProperties().isEmpty()) {
            for (String property : dataObject.getProperties()) {
                String propertyName = property.split("#")[0];
                String propertyValue = property.split("#")[1];
                // either we have something like DockerEngine#GDockerEngineURL or
                // Port#GApplicationPort these values have to be in the dollar brackets otherwise we get an error
                if (propertyValue.equals(propertyName) || propertyValue.startsWith("G")) {
                    propertyValue = propertyValue.replace("G", "");
                    properties = properties + "<camunda:inputParameter name='Properties." + propertyName + "'>" + "\\${" + propertyValue + "}" + "</camunda:inputParameter>";
                } else {
                    // for the cases like ContainerPort#80
                    properties = properties + "<camunda:inputParameter name='Properties." + propertyName + "'>" + propertyValue + "</camunda:inputParameter>";
                }
            }
        }
        nodeDataObject = nodeDataObject.replaceAll("<camunda:inputParameter name='Properties'>PropertiesToSet</camunda:inputParameter>", properties);

        return nodeDataObject;
    }

    /**
     * Creates a service instance data object which consists of the servicetemplate and its properties
     *
     * @param dataObject
     * @param bpmnPlan
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public String createServiceInstanceDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException, SAXException {
        String serviceTemplateNamespace = bpmnPlan.getServiceTemplate().getTargetNamespace();
        String csarName = bpmnPlan.getCsarName();
        String serviceInstanceDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNServiceInstanceDataObjectReference.xml"));

        LOG.info("SERVICEINSTANCEDAAOBJECT");
        LOG.info(dataObject.getServiceInstanceURL());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceTaskName", dataObject.getId());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceURLToSet", dataObject.getServiceInstanceURL());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceInstanceIdToSet", dataObject.getServiceInstanceURL());
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("ServiceTemplateURLToSet", serviceTemplateNamespace + csarName);
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("CsarIdToSet", csarName);
        serviceInstanceDataObject = serviceInstanceDataObject.replaceAll("IdToSet", dataObject.getId());
        return serviceInstanceDataObject;
    }

    /**
     * Creates a inputoutput data object which handles in and outputs properties
     *
     * @param dataObject
     * @param bpmnPlan
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public String createInputOutputDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException, SAXException {
        String inputOutputDataObject = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNInputOutputDataObjectReference.xml"));
        inputOutputDataObject = inputOutputDataObject.replaceAll("ServiceInstanceTaskName", dataObject.getId());
        inputOutputDataObject = inputOutputDataObject.replaceAll("IdToSet", dataObject.getId());
        String inputParameters = "";
        String inputParameterNames = "";
        for (String inputParameterName : bpmnPlan.getInputParameters()) {
            inputParameterNames += inputParameterName + ",";
            inputParameters = inputParameters + "<camunda:inputParameter name='" + inputParameterName + "'>" + "\\${" + inputParameterName + "}" + "</camunda:inputParameter>";
        }
        String outputParameters = "";
        String outputParameterNames = "";
        for (String outputParameterName : bpmnPlan.getPropertiesOutputParameters().keySet()) {
            outputParameterNames += outputParameterName + ",";
            if (outputParameterName.equals(bpmnPlan.getPropertiesOutputParameters().get(outputParameterName))) {
                outputParameters = outputParameters + "<camunda:inputParameter name='Output." + outputParameterName + "'>" + "\\${" + bpmnPlan.getPropertiesOutputParameters().get(outputParameterName) + "}" + "</camunda:inputParameter>";
            } else {
                outputParameters = outputParameters + "<camunda:inputParameter name='Output." + outputParameterName + "'>" + bpmnPlan.getPropertiesOutputParameters().get(outputParameterName) + "</camunda:inputParameter>";
            }
        }
        inputParameterNames = inputParameterNames.substring(0, inputParameterNames.lastIndexOf(","));
        outputParameterNames = outputParameterNames.substring(0, outputParameterNames.lastIndexOf(","));
        inputOutputDataObject = inputOutputDataObject.replaceAll("<camunda:inputParameter name='InputParameter'>InputParameterToSet</camunda:inputParameter>", inputParameters);
        inputOutputDataObject = inputOutputDataObject.replaceAll(" <camunda:inputParameter name='OutputParameter'>OutputParameterToSet</camunda:inputParameter>", outputParameters);
        inputOutputDataObject = inputOutputDataObject.replaceAll("InputParameterNamesToSet", inputParameterNames);
        inputOutputDataObject = inputOutputDataObject.replaceAll("OutputParameterToSet", outputParameterNames);
        return inputOutputDataObject;
    }

    /**
     * Each dataObject is composed of two components:
     * 1)DataObjectReference which holds the actual content of the data object
     * 2) the data Object itself
     *
     * @param bpmnPlan
     * @param d        The finalized Document (with diagramelements) without dataObjects
     * @return finished data object node
     * @throws IOException
     * @throws SAXException Erweitern auf die verschiedenen Typen
     */
    public Node createDataObjectAsNode(BPMNPlan bpmnPlan, Document d, BPMNDataObject dataObject) throws IOException, SAXException {
        int id = bpmnPlan.getInternalCounterId();
        BPMNSubprocessType dataObjectType = dataObject.getDataObjectType();
        String dataObjectReference = "";
        if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_ST) {
            dataObjectReference = createServiceInstanceDataObjectReference(dataObject, bpmnPlan);
            LOG.info(dataObjectReference);
            //Node serviceInstanceDataObject = this.transformStringToNode(dataObject);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_NODE) {
            dataObjectReference = createNodeDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_INOUT) {
            dataObjectReference = createInputOutputDataObjectReference(dataObject, bpmnPlan);
        } else if (dataObjectType == BPMNSubprocessType.DATA_OBJECT_REL) {
            dataObjectReference = createRelationDataObjectReference(dataObject, bpmnPlan);
        }
        this.createImportNodeFromString(bpmnPlan, d, dataObjectReference, false);
        String diagramServiceInstanceDataObjectReference = createDiagramDataObjectReference(dataObject, bpmnPlan);
        this.createImportNodeFromString(bpmnPlan, d, diagramServiceInstanceDataObjectReference, true);
        String dataObject2 = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDataObject.xml"));
        dataObject2 = dataObject2.replaceAll("IdToSet", dataObject.getId());

        Node dataObjectAsNode = this.createImportNodeFromString(bpmnPlan, d, dataObject2, false);
        return dataObjectAsNode;
    }

    /**
     * create data object reference for diagram
     *
     * @param dataObject
     * @param bpmnPlan
     * @return
     * @throws IOException
     */
    private String createDiagramDataObjectReference(BPMNDataObject dataObject, BPMNPlan bpmnPlan) throws IOException {
        String dataObjectReference = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/diagram/BPMNDiagramDataObjectReference.xml"));
        for (BPMNSubprocess outerSubprocess : bpmnPlan.getSubprocess()) {
            String dataObjectOuterSubprocessId = outerSubprocess.getId().replace("Subprocess", "DataObject");
            if (outerSubprocess.getId().contains(dataObject.getId()) || dataObjectOuterSubprocessId.contains(dataObject.getId())) {
                dataObjectReference = dataObjectReference.replaceAll("<dc:Bounds x='xToSet' y='yToSet' width='36' height='50' />", "<dc:Bounds x=\"" + outerSubprocess.getX() + "\" y=\"" + outerSubprocess.getY() + "\" width=\"36\" height=\"50\" />");
            }
        }
        dataObjectReference = dataObjectReference.replaceAll("IdToSet", dataObject.getId());
        return dataObjectReference;
    }

    /**
     * creates a Node of the specified type (bpmnSubprocessType)
     *
     * @param bpmnSubprocess the subprocess
     * @return reated node
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
                    node = this.createBPMNStartEventAsNode(bpmnSubprocess);
                    break;
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
                    node = this.createCreateNodeTemplateInstanceTaskAsNode(bpmnSubprocess);
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
                //case ERROR_INNER_FLOW:
                //  node = this.createBPMNInnerErrorSequenceFlow(bpmnSubprocess, bpmnSubprocess);
                // break;
                default:
                    LOG.debug("Doesn't find matching BPMNSubprocess Type for {}", bpmnSubprocess.getId());
                    break;
            }
        } catch (Exception e) {
            LOG.debug("Fail to create BPMN Element due to {}", e);
        }

        return node;
    }

    /**
     * create ActivateDataObjectTask node and import node to document
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    private Node createActivateDataObjectTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("ACTIVATEDATAOBJECT");
        String activateDataObjectString = createActivateDataObjectString(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP5");
        LOG.info(bpmnSubprocess.getId());
        Node node = createImportNodeFromString(bpmnSubprocess, activateDataObjectString);
        return node;
    }

    /**
     * create the template String for a ActivateDataObjectTask node
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    private String createActivateDataObjectString(BPMNSubprocess bpmnSubprocess) throws IOException {
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        BPMNPlan bpmnPlan = bpmnSubprocess.getBuildPlan();
        int sourceId = id - 1;
        String activateDataObjectTask = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNActivateDataObjectTask.xml"));
        //TNodeTemplate nodeTemplate = bpmnSubprocess.getNodeTemplate();
        activateDataObjectTask = activateDataObjectTask.replace("ActivateDataObject_IdToReplace", bpmnSubprocess.getId());
        activateDataObjectTask = activateDataObjectTask.replace("NameToSet", "Activate data object " + bpmnSubprocess.getDataObject().getId());
        activateDataObjectTask = activateDataObjectTask.replace("DataObjectIdToSet", bpmnSubprocess.getDataObject().getId());
        //activateDataObjectTask = activateDataObjectTask.replace("FlowIncomingToSet", "Flow_" + sourceId);
        //activateDataObjectTask = activateDataObjectTask.replace("FlowOutgoingToSet", "Flow_" + id);
        //activateDataObjectTask = activateDataObjectTask.replace("NameToSet", "Activate " + nodeTemplate.getId() + " DataObject");
        String properties = "";
        String propertiesNames = "";

        String inputParameterNames = "";
        String outputParameterNames = "";
        if (bpmnSubprocess.getDataObject().getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT) {
            for (String inputParameterName : bpmnSubprocess.getBuildPlan().getInputParameters()) {
                inputParameterNames += inputParameterName + ",";
            }
            String outputParameters = "";

            for (String outputParameterName : bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().keySet()) {
                outputParameterNames += outputParameterName + ",";
                if (outputParameterName.equals(bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().get(outputParameterName))) {
                    outputParameters = outputParameters + "<camunda:inputParameter name='Output." + outputParameterName + "'>" + "\\${" + bpmnPlan.getPropertiesOutputParameters().get(outputParameterName) + "}" + "</camunda:inputParameter>";
                } else {
                    outputParameters = outputParameters + "<camunda:inputParameter name='Output." + outputParameterName + "'>" + bpmnPlan.getPropertiesOutputParameters().get(outputParameterName) + "</camunda:inputParameter>";
                }
            }
            inputParameterNames = inputParameterNames.substring(0, inputParameterNames.lastIndexOf(","));
            outputParameterNames = outputParameterNames.substring(0, outputParameterNames.lastIndexOf(","));
            activateDataObjectTask = activateDataObjectTask.replaceAll("InputParameterNamesToSet", inputParameterNames);
            activateDataObjectTask = activateDataObjectTask.replaceAll("OutputParameterNamesToSet", outputParameterNames);
            activateDataObjectTask = activateDataObjectTask.replaceAll(" <camunda:inputParameter name='OutputParameter'>OutputParameterToSet</camunda:inputParameter>", outputParameters);
        }
        if (!bpmnSubprocess.getDataObject().getProperties().isEmpty()) {
            LOG.info("SIZE {}", bpmnSubprocess.getDataObject().getProperties().size());
            for (String property : bpmnSubprocess.getDataObject().getProperties()) {
                LOG.info("PRO2");
                LOG.info(property);

                propertiesNames += property + ",";
                String propertyName = property;
                String propertyValue = property;
                if (property.contains("#")) {
                    propertyName = property.split("#")[0];
                    propertyValue = property.split("#")[1];
                }
                if (propertyValue.equals(propertyName) && (!propertyValue.startsWith("G"))) {
                    properties = properties + "<camunda:inputParameter name='Properties." + propertyName + "'>" + propertyValue + "</camunda:inputParameter>";
                }
                // G marks that the property is given by input thus brackets are needed
                else if (propertyValue.startsWith("G")) {
                    propertyValue = propertyValue.substring(1);
                    properties = properties + "<camunda:inputParameter name='Properties." + propertyName + "'>" + "\\${" + propertyValue + "}" + "</camunda:inputParameter>";
                } else {
                    properties = properties + "<camunda:inputParameter name='Properties." + propertyName + "'>" + propertyValue + "</camunda:inputParameter>";
                }
            }
            LOG.info("PROPERTIESNAMES8");
            LOG.info(propertiesNames);
            propertiesNames = propertiesNames.substring(0, propertiesNames.lastIndexOf(","));
            LOG.info(propertiesNames);
            activateDataObjectTask = activateDataObjectTask.replaceAll("<camunda:inputParameter name='Properties'>PropertiesToSet</camunda:inputParameter>", properties);
            activateDataObjectTask = activateDataObjectTask.replaceAll("PropertiesNamesToSet", propertiesNames);
        }

        LOG.info("PROPERTIESNAMES10");
        LOG.info(outputParameterNames);
        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);
        bpmnSubprocess.setErrorOutflow("OuterFlow_" + sourceId);

        return activateDataObjectTask;
    }

    /**
     * create setNodePropertiesTask node and import node to document
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    private Node createSetNodePropertiesTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String templateString = createSetNodePropertiesState(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP5");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, templateString);
        return node;
    }

    /**
     * create the template String for a SetNodeProperties node
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    private String createSetNodePropertiesState(BPMNSubprocess bpmnSubprocess) throws IOException {

        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetPropertiesTask.xml"));
        template = template.replace("Activity_IdToSet", bpmnSubprocess.getId());
        template = template.replace("name_toSet", bpmnSubprocess.getId());
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        String nodeTemplateId = bpmnSubprocess.getNodeTemplate().getId();
        String dataObjectidentifier = bpmnSubprocess.getParentProcess().getId().split("_")[1];

        if (bpmnSubprocess.getInstanceState() != null) {
            template = template.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        }

        String parentId = bpmnSubprocess.getParentProcess().getId().replace("Subprocess_", "ResultVariable");
        template = template.replace("NodeInstanceURLToSet", "${" + parentId + "}");
        template = template.replaceAll("NodeTemplateToSet", nodeTemplateId);

        List<String> properties = null;
        String propertiesToSet = "";

        // find correspondant data object
        if (bpmnSubprocess.getBuildPlan().getDataObjectsList() != null) {
            if (!bpmnSubprocess.getBuildPlan().getDataObjectsList().isEmpty()) {
                for (BPMNDataObject dataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
                    // (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE) &&
                    if (dataObject.getId().contains(dataObjectidentifier)) {
                        if (dataObject.getProperties() != null) {
                            LOG.info("OK");
                            properties = dataObject.getProperties();
                        }
                    }
                }
            } else {
                LOG.info("NO DATA OBJECTS");
            }
        }
        String inputparambuilder = "";

        //set input properties
        if (!properties.isEmpty()) {
            for (String property : properties) {
                if (propertiesToSet.equals("")) {
                    propertiesToSet = propertiesToSet + property;
                } else {
                    propertiesToSet = propertiesToSet + "," + property;
                }
                String inputparamname = property.split("#")[0];
                String inputparamvalue = property.split("#")[1];
                if (inputparamname.charAt(0) == 'G') {
                    inputparamname = "${" + inputparamname.substring(1) + "}";
                }
                if (inputparamvalue.charAt(0) == 'G') {
                    inputparamvalue = "${" + inputparamvalue.substring(1) + "}";
                }
                inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + inputparamname + "\">" + inputparamvalue + "</camunda:inputParameter>";
            }
        }

        template = template.replaceAll("PropertiesToSet", propertiesToSet);

        template = template.replace("Flow_Input", "Flow_" + sourceId);
        template = template.replace("Flow_Output", "Flow_" + id);

        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);

        //rebuild template String
        String[] original = template.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];
        String resultstring = original[0] + inputparambuilder + original[1];

        return resultstring;
    }

    /**
     * create a Node of the bpmnSubprocess containing all child processes
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNSubprocessAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("id {}, type {}", bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessType());
        final String templateString = createBPMNSubprocess(bpmnSubprocess);
        LOG.info("templatestring");
        LOG.info(templateString);
        LOG.info("PPPPPPPPPPPPPPP8");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, templateString);
        //this.imcomings(bpmnSubprocess);
        //this.addOutgoings(bpmnSubprocess);
        LOG.info("Subprocess");
        LOG.info(bpmnSubprocess.getId());
        ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
        ArrayList<Node> flowNodes = new ArrayList<>();
        // add Start Event inside subprocess
        BPMNSubprocess innerStartEvent = new BPMNSubprocess(BPMNSubprocessType.INNER_START_EVENT, "StartEvent_" + bpmnSubprocess.getBuildPlan().getOuterFlowCounterId());
        innerStartEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerStartEvent.setParentProcess(bpmnSubprocess);
        BPMNSubprocess previousIncoming = innerStartEvent;
        // compute the sequence flows before components are added to the xml
        for (BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            BPMNSubprocess innerSequenceFlow2 = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
            innerSequenceFlow2.setBuildPlan(bpmnSubprocess.getBuildPlan());
            innerSequenceFlow2.setIncomingTestScope(previousIncoming);
            innerSequenceFlow2.setOuterflow(subSubprocess);

            Node innersequenceFlowNode = this.createBPMNSubprocessAndComponentsAsNode(innerSequenceFlow2);
            flowNodes.add(innersequenceFlowNode);
            flowElements.add(innerSequenceFlow2);
            bpmnSubprocess.setFlowElements(flowElements);
            //innerSequenceFlow2.getIncomingTestScope().clear();
            //innerSequenceFlow.cleanOuterflow();
            //innerSequenceFlow2.getOuterflow().clear();
            previousIncoming = subSubprocess;
            //innerSequenceFlow2.setId("TestInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
            //innerSequenceFlow = innerSequenceFlow2;
        }
        // add End Event inside subprocess
        BPMNSubprocess innerEndEvent = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "EndEvent_" + bpmnSubprocess.getBuildPlan().getOuterFlowCounterId());
        innerEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerEndEvent.setParentProcess(bpmnSubprocess);

        BPMNSubprocess innerEndEventSequenceFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
        innerEndEventSequenceFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerEndEventSequenceFlow.setIncomingTestScope(previousIncoming);
        innerEndEventSequenceFlow.setOuterflow(innerEndEvent);
        flowElements.add(innerEndEventSequenceFlow);
        bpmnSubprocess.setFlowElements(flowElements);
        Node innerEndEventflowNode = this.createBPMNSubprocessAndComponentsAsNode(innerEndEventSequenceFlow);
        flowNodes.add(innerEndEventflowNode);
        //BPMNSubprocess innerSequenceFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestInnerFlow_" + bpmnSubprocess.getBuildPlan().getIdForInnerFlowTestAndIncrement());
        //innerSequenceFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
        //innerSequenceFlow.setOuterflow(innerStartEvent);
        //innerSequenceFlow.setOuterflow(innerStartEvent);
        //flowElements.add(innerSequenceFlow);
        for (int i = 0; i < flowElements.size(); i++) {
            LOG.info("FLFLO {}", flowElements.get(i).getId());
        }
        bpmnSubprocess.setFlowElements(flowElements);
        //innerSequenceFlow.getOuterflow().clear();
        Node startEventNode = this.createBPMNSubprocessAndComponentsAsNode(innerStartEvent);
        bpmnSubprocess.getBpmnSubprocessElement().appendChild(startEventNode);

        // importing all elements within Subprocess recursively
        ArrayList<BPMNSubprocess> boundaryEvents = new ArrayList<>();
        ArrayList<BPMNSubprocess> incomingBoundaryEventLinks = new ArrayList<>();
        ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();
        //innerSequenceFlow.getOuterflow().clear();
        LOG.info("SCHLEIFESUBSUBPROCESS");
        //innerSequenceFlow.setIncomingTestScope(bpmnSubprocess.getSubprocessBPMNSubprocess().get(0));
        //Node sequenceFlowNode = this.createBPMNSubprocessAndComponentsAsNode(innerSequenceFlow);
        //bpmnSubprocess.getBpmnSubprocessElement().appendChild(sequenceFlowNode);
        innerEndEvent.setParentProcess(bpmnSubprocess);
        // add errorend event inside subprocess
        BPMNSubprocess innerErrorEndEvent = new BPMNSubprocess(BPMNSubprocessType.ERROR_END_EVENT, "ErrorEndEvent_" + bpmnSubprocess.getBuildPlan().getOuterFlowCounterId());
        innerErrorEndEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        innerErrorEndEvent.setParentProcess(bpmnSubprocess);
        for (BPMNSubprocess subSubprocess : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            subSubprocess.setParentProcess(bpmnSubprocess);

            if (subSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2) {
                LOG.info("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
                LOG.info(subSubprocess.getId());
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
                        innerErrorSequenceFlow.setIncomingTestScope(innerBoundaryEvent);
                        innerErrorSequenceFlow.setOuterflow(innerErrorEndEvent);
                        errorFlowElements.add(innerErrorSequenceFlow);
                        bpmnSubprocess.setFlowElements(flowElements);
                        bpmnSubprocess.setErrorFlowElements(errorFlowElements);
                        innerBoundaryEvent.setParentProcess(bpmnSubprocess);
                        Node errorChild = this.createTaskErrorBoundaryEventAsNode(innerBoundaryEvent, subSubprocess, errorId);
                        bpmnSubprocess.getBpmnSubprocessElement().appendChild(errorChild);

                        //innerBoundaryEvent.setErrorInnerflow("ErrorInnerFlow_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
                        boundaryEvents.add(innerBoundaryEvent);
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
            LOG.info("FLWBOUNDARY {}", flowNode.getId());
            Node errorSequenceNode = this.createBPMNSequenceFlowAsNode(flowNode);
            bpmnSubprocess.getBpmnSubprocessElement().appendChild(errorSequenceNode);
        }

        LOG.info("SUBPROCESS TYPE {} {} {}", bpmnSubprocess.getBpmnSubprocessType(), bpmnSubprocess.getId(), bpmnSubprocess.getBpmnSubprocessElement().getChildNodes().getLength());

        // add sequence flows
        for (BPMNSubprocess subSubprocess2 : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
            for (BPMNSubprocess subSubprocess3 : bpmnSubprocess.getSubprocessBPMNSubprocess()) {
                if (!subSubprocess2.equals(subSubprocess3)) {
                    if ((subSubprocess2.getOutflow() != null) && (subSubprocess3.getInflow() != null)) {
                        if (subSubprocess2.getOutflow().equals(subSubprocess3.getInflow())) {
                            //BPMNSubprocess sequenceFlow2 = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "EGAL");
                            //sequenceFlow2.setOuterflow(subSubprocess3);
                            //sequenceFlow2.setIncomingTestScope(sequenceFlow2);
                            //this.createBPMNSubprocessAndComponentsAsNode(sequenceFlow2);
                            //Node child = this.createBPMNSequenceFlow2(subSubprocess2, subSubprocess3);
                            //bpmnSubprocess.getBpmnSubprocessElement().appendChild(child);
                        }
                    }
                }
            }
            //add sequence flow for start event inside subprocess
            if (subSubprocess2.getInflow().equals(innerStartEvent.getOutflow()) && (subSubprocess2.getInflow() != null)) {
                //    Node startflow = this.createBPMNSequenceFlow2(innerStartEvent, subSubprocess2);
                //  bpmnSubprocess.getBpmnSubprocessElement().appendChild(startflow);
            }
            //add sequence flow for end event inside subprocess
            if (subSubprocess2.getOutflow().equals(innerEndEvent.getInflow()) && (subSubprocess2.getOutflow() != null)) {
                //Node endflow = this.createBPMNSequenceFlow2(subSubprocess2, innerEndEvent);
                //bpmnSubprocess.getBpmnSubprocessElement().appendChild(endflow);
            }
        }

        //for (BPMNSubprocess errorBoundaryEvent : boundaryEvents) {
        //  LOG.info("Id von ERB");
        //LOG.info(errorBoundaryEvent.getId());
        //Node endflow = this.createBPMNInnerErrorSequenceFlow(errorBoundaryEvent, innerErrorEndEvent);
        //BPMNSubprocess outgoingLinks = new BPMNSubprocess(BPMNSubprocessType.EVENT, errorBoundaryEvent.getErrorInnerflow());
        //incomingBoundaryEventLinks.add(outgoingLinks);
        //bpmnSubprocess.getBpmnSubprocessElement().appendChild(endflow);
        //}

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
     * @param s              template String to trnsform into a Node
     * @return the created and imported Node
     * @throws IOException
     * @throws SAXException
     */
    private Node createImportNodeFromString(BPMNSubprocess bpmnSubprocess, String s) throws
        IOException, SAXException {
        Node transformedNode = this.transformStringToNode(s);
        ArrayList<String> incomingFlowIds = new ArrayList<>();
        ArrayList<String> outgoingFlowIds = new ArrayList<>();
        LOG.info("DER TYPE {} {}", bpmnSubprocess.getSubprocessType(), bpmnSubprocess.getId());
        LOG.info(s);
        Document doc = bpmnSubprocess.getBpmnDocument();
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.ERROR_END_EVENT) {
            outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            LOG.info("FLOWCOMPUTED");
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:endEvent>";
            int end = s.indexOf(closingTag);
            String result = "";
            for (String flowId : outgoingFlowIds) {
                LOG.info(flowId);
                result += "<bpmn:incoming>" + flowId + "</bpmn:incoming>";
                //Node importedNode2 = doc.importNode(node, true);
                //importedNode.insertBefore(importedNode.getFirstChild(), importedNode.getFirstChild());
                // importedNode.appendChild(importedNode2);
                //importedNode.insertBefore(importedNode2, importedNode.getFirstChild());
            }
            result = s.substring(0, begin) + result + s.substring(begin, end + closingTag.length());
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.END_EVENT) {
            outgoingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            if (bpmnSubprocess.getId().contains("Error")) {
                outgoingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            }
            LOG.info("FLOWCOMPUTED");
            String[] original = s.split("</bpmn:endEvent>");
            String result = "";
            for (String flowId : outgoingFlowIds) {
                LOG.info(flowId);
                result += "<bpmn:incoming>" + flowId + "</bpmn:incoming>";
                //Node importedNode2 = doc.importNode(node, true);
                //importedNode.insertBefore(importedNode.getFirstChild(), importedNode.getFirstChild());
                // importedNode.appendChild(importedNode2);
                //importedNode.insertBefore(importedNode2, importedNode.getFirstChild());
            }
            result = original[0] + result + "</bpmn:endEvent>";
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getSubprocessType() == BPMNSubprocessType.EVENT) {
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            LOG.info("FLOWCOMPUTED");
            int begin = s.indexOf("<bpmn:errorEventDefinition");
            String closingTag = "</bpmn:boundaryEvent>";
            int end = s.indexOf(closingTag);
            String result = "";
            for (String flowId : outgoingFlowIds) {
                LOG.info(flowId);
                result += "<bpmn:outgoing>" + flowId + "</bpmn:outgoing>";
                //Node importedNode2 = doc.importNode(node, true);
                //importedNode.insertBefore(importedNode.getFirstChild(), importedNode.getFirstChild());
                // importedNode.appendChild(importedNode2);
                //importedNode.insertBefore(importedNode2, importedNode.getFirstChild());
            }
            result = s.substring(0, begin) + result + s.substring(begin, end + closingTag.length());
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2 && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SUBPROCESS && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.EVENT && bpmnSubprocess.getSubprocessType() != BPMNSubprocessType.SUBPROCESS_ERROR_BOUNDARY
            && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.USER_TASK && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.SEQUENCE_FLOW2 && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.START_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.ERROR_END_EVENT && bpmnSubprocess.getBpmnSubprocessType() != BPMNSubprocessType.INNER_START_EVENT) {

            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:scriptTask>");
            String result = "";
            for (String flowId : incomingFlowIds) {
                result += "<bpmn:incoming>" + flowId + "</bpmn:incoming>";
            }
            for (String flowId : outgoingFlowIds) {
                result += "<bpmn:outgoing>" + flowId + "</bpmn:outgoing>";
            }
            result = original[0] + result + "</bpmn:scriptTask>";
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.SUBPROCESS) {
            incomingFlowIds = computeOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:subProcess>");
            String result = "";
            for (String flowId : incomingFlowIds) {
                result += "<bpmn:incoming>" + flowId + "</bpmn:incoming>";
            }
            for (String flowId : outgoingFlowIds) {
                result += "<bpmn:outgoing>" + flowId + "</bpmn:outgoing>";
            }
            result = original[0] + result + "</bpmn:subProcess>";
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.USER_TASK) {
            incomingFlowIds = computeErrorOutgoingFlowElements(bpmnSubprocess);
            outgoingFlowIds = computeIncomingErrorFlowElements(bpmnSubprocess);
            String[] original = s.split("</bpmn:userTask>");
            String result = "";
            for (String flowId : incomingFlowIds) {
                result += "<bpmn:incoming>" + flowId + "</bpmn:incoming>";
            }
            for (String flowId : outgoingFlowIds) {
                result += "<bpmn:outgoing>" + flowId + "</bpmn:outgoing>";
            }
            result = original[0] + result + "</bpmn:userTask>";
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }
        if (bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.INNER_START_EVENT || bpmnSubprocess.getBpmnSubprocessType() == BPMNSubprocessType.START_EVENT) {
            // make sure all elements belongs to same document
            ///Node importedNode = doc.importNode(transformedNode, true);
            outgoingFlowIds = computeIncomingFlowElements(bpmnSubprocess);
            LOG.info("FLOWCOMPUTED");
            String[] original = s.split("</bpmn:startEvent>");
            String result = "";
            for (String flowId : outgoingFlowIds) {
                LOG.info(flowId);
                result += "<bpmn:outgoing>" + flowId + "</bpmn:outgoing>";
                //Node importedNode2 = doc.importNode(node, true);
                //importedNode.insertBefore(importedNode.getFirstChild(), importedNode.getFirstChild());
                // importedNode.appendChild(importedNode2);
                //importedNode.insertBefore(importedNode2, importedNode.getFirstChild());
            }
            result = original[0] + result + "</bpmn:startEvent>";
            LOG.info("RRRRRRRRRRRRRRRRRRRRRESULT");
            LOG.info(result);
            Node transformedChangedNode = this.transformStringToNode(result);
            Node importedOutgoingNode2 = doc.importNode(transformedChangedNode, true);
            LOG.info("fertig");
            bpmnSubprocess.setBpmnSubprocessElement((Element) importedOutgoingNode2);
            return importedOutgoingNode2;
        }

        LOG.info("STRINGAUSSEN");
        LOG.info(s);
        // make sure all elements belongs to same document
        Node importedNode = doc.importNode(transformedNode, true);

        bpmnSubprocess.setBpmnSubprocessElement((Element) importedNode);

        return importedNode;
    }

    /**
     * Adds the node to the corresponding part of the document. Diagram elements are not inside
     * the process element so they have to be moved after it.
     *
     * @param bpmnPlan
     * @param d
     * @param s
     * @param diagramNode
     * @return
     * @throws IOException
     * @throws SAXException
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
     * @throws IOException
     */
    private String createBPMNSubprocess(BPMNSubprocess bpmnSubprocess) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSubprocess.xml"));
        template = template.replace("Subprocess_IdToSet", bpmnSubprocess.getId());
        LOG.info("Das NodeTemplate");
        LOG.info(bpmnSubprocess.getSubprocessType().toString());
        if (bpmnSubprocess.getRelationshipTemplate() != null) {
            template = template.replace("NameToSet",
                bpmnSubprocess.getRelationshipTemplate().getName() + " Subprocess");
            int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
            int sourceId = id - 1;
            template = template.replace("Flow_Input", "OuterFlow_" + sourceId);
            template = template.replace("Flow_Output", "OuterFlow_" + id);
            bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
            bpmnSubprocess.setOutflow("OuterFlow_" + id);
            return template;
        } else if (bpmnSubprocess.getNodeTemplate() != null) {
            template = template.replace("NameToSet",
                bpmnSubprocess.getNodeTemplate().getName() + " Subprocess");
            int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
            int sourceId = id - 1;
            template = template.replace("Flow_Input", "OuterFlow_" + sourceId);
            template = template.replace("Flow_Output", "OuterFlow_" + id);
            bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
            bpmnSubprocess.setOutflow("OuterFlow_" + id);
            return template;
        }
        if (bpmnSubprocess.getServiceInstanceURL() != null) {
            template = template.replace("NameToSet",
                "Service Instance Creation Subprocess");
            int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
            int sourceId = id - 1;
            template = template.replace("Flow_Input", "OuterFlow_" + sourceId);
            template = template.replace("Flow_Output", "OuterFlow_" + id);
            bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
            bpmnSubprocess.setOutflow("OuterFlow_" + id);
            return template;
        }
        // hier noch eine if abfrage wegen data input output
        template = template.replace("NameToSet",
            "Subprocess to activate data objects");
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        int sourceId = id - 1;
        template = template.replace("Flow_Input", "OuterFlow_" + sourceId);
        template = template.replace("Flow_Output", "OuterFlow_" + id);
        bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
        bpmnSubprocess.setOutflow("OuterFlow_" + id);

        return template;
    }

    /**
     * create a start event Node from template String
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNStartEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String template = createBPMNStartEvent(bpmnSubprocess);
        LOG.info("WIE OFT RUFST DU DAS AUF");
        LOG.info("PPPPPPPPPPPPPPP9");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        // Out Event only has outgoing Flows
        //addOutgoings(bpmnSubprocess);
        return node;
    }

    /**
     * create a start bpmn error definition Node from template String
     *
     * @param id id of the error event definition
     * @return created node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNErrorEventDefinitionAsNode(int id) throws IOException, SAXException {
        final String template = createBPMNErrorEventDefinition(id);
        Node node = this.transformStringToNode(template);
        // Out Event only has outgoing Flows
        //addOutgoings(bpmnSubprocess);
        return node;
    }

    /**
     * create the template String for a error event definition Node
     *
     * @param id id of the error event definition
     * @return template String
     * @throws IOException
     * @throws SAXException
     */
    public String createBPMNErrorEventDefinition(int id) throws IOException, SAXException {
        final String idPrefix = BPMNSubprocessType.EVENT.toString();
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNError.xml"));
        template = template.replaceAll("IdToSet", idPrefix + id);
        template = template.replaceAll("NameToSet", "Error Event");
        return template;
    }

    private Node createBPMNSequenceFlowAsNode(BPMNSubprocess bpmnScope) throws IOException, SAXException {
        LOG.info("CREATE SEQF with scope id {}", bpmnScope.getId());
        LOG.info("BPMNSCOPEID {} {}", bpmnScope.getId(), bpmnScope.getIncomingTestFlow().size());
        String template = createBPMNSequenceFlow(bpmnScope.getId(),
            bpmnScope.getOuterFlow().iterator().next().getId(),
            bpmnScope.getIncomingTestFlow().iterator().next().getId()
        );
        LOG.info("PPPPPPPPPPPPPPP10");
        LOG.info(bpmnScope.getId());
        return this.createImportNodeFromString(bpmnScope, template);
    }

    public String createBPMNSequenceFlow(String FlowID, String incomingFlowName, String outgoingFlowName) throws
        IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSequenceFlow.xml"));
        // each sequence flow is guaranteed to only two ends
        template = template.replaceAll("Flow_IdToReplace", FlowID);
        template = template.replaceAll("SourceToReplace", incomingFlowName);
        template = template.replaceAll("TargetToReplace", outgoingFlowName);
        return template;
    }

    /**
     * create a error boundary event node from template String
     *
     * @param bpmnSubprocess
     * @param errorId
     * @return created node
     * @throws IOException
     * @throws SAXException
     */
    public Node createSubprocessErrorBoundaryEventAsNode(BPMNSubprocess bpmnSubprocess, int errorId) throws
        IOException, SAXException {
        final String template = createBPMNSubprocessErrorBoundaryEvent(bpmnSubprocess, errorId);
        LOG.info("PPPPPPPPPPPPPPP11");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        // Out Event only has outgoing Flows
        //addOutgoings(bpmnSubprocess);
        //bpmnSubprocess.setErrorOutflow("ErrorOuterFlow_" + bpmnSubprocess.getId());
        return node;
    }

    /**
     * create a error boundary event template String
     *
     * @param bpmnSubprocess
     * @param errorId
     * @return template String
     * @throws IOException
     * @throws SAXException
     */
    public Node createTaskErrorBoundaryEventAsNode(BPMNSubprocess innerEvent, BPMNSubprocess bpmnSubprocess,
                                                   int errorId) throws IOException, SAXException {
        final String template = createBPMNTaskErrorBoundaryEvent(bpmnSubprocess, errorId);
        innerEvent.setBuildPlan(bpmnSubprocess.getBuildPlan());
        LOG.info("PPPPPPPPPPPPPPP12");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(innerEvent, template);
        // Out Event only has outgoing Flows
        //addOutgoings(bpmnSubprocess);
        //bpmnSubprocess.setErrorInnerflow("ErrorInnerFlow_" + bpmnSubprocess.getId());
        return node;
    }

    /**
     * create a subprocess error boundary event node from template String
     *
     * @param bpmnSubprocess
     * @param id
     * @return template String
     * @throws IOException
     */
    public String createBPMNSubprocessErrorBoundaryEvent(BPMNSubprocess bpmnSubprocess, int id) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSubprocessErrorBoundaryEvent.xml"));
        String idPrefix = BPMNSubprocessType.SUBPROCESS_ERROR_BOUNDARY.toString();
        String attachedElementId = bpmnSubprocess.getId().replace("BoundaryEvent_ErrorEvent", "");
        template = template.replaceAll("Event_IdToSet", bpmnSubprocess.getId());
        template = template.replaceAll("Activity_ActIdToSet", attachedElementId);
        template = template.replaceAll("IdToSet", bpmnSubprocess.getId());
        template = template.replaceAll("Error_Id", "Error_Event" + id);
        LOG.info("error boundary");
        LOG.info(template);
        return template;
    }

    /**
     * create a error boundary event template String
     *
     * @param bpmnSubprocess
     * @param id
     * @return template String
     * @throws IOException
     */
    public String createBPMNTaskErrorBoundaryEvent(BPMNSubprocess bpmnSubprocess, int id) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNTaskErrorBoundaryEvent.xml"));
        String idPrefix = BPMNSubprocessType.SUBPROCESS_ERROR_BOUNDARY.toString();
        template = template.replaceAll("Event_IdToSet", "BoundaryEvent_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
        template = template.replaceAll("Activity_ActIdToSet", bpmnSubprocess.getId());
        template = template.replaceAll("IdToSet", bpmnSubprocess.getId());
        template = template.replaceAll("Error_Id", "Error_" + idPrefix + id);
        //template = template.replaceAll("Flow_Output", "ErrorInnerFlow_" + bpmnSubprocess.getBuildPlan().getErrorInnerFlowCounterId());
        LOG.info("HHHHHHHHHHHHHHHHHHHHHHH");
        LOG.info(template);
        return template;
    }

    /**
     * create a bpmn start event template String
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    public String createBPMNStartEvent(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        String startEvent = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        startEvent = startEvent.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        // startEvent = startEvent.replaceAll("Flow_Output", "Flow_" + id);
        bpmnSubprocess.setOutflow("Flow_" + id);
        return startEvent;
    }

    private ArrayList<String> computeOutgoingFlowElements(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        LOG.info(bpmnSubprocess.getId());
        LOG.info("aaaaaaaaaaaaaaaaaaaaaaa");
        LOG.info("NODE HAT FUNKTIONIERT");
        Document doc = bpmnSubprocess.getBpmnDocument();
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getFlowElements();
            }
            for (int i = 0; i < flowElements.size(); i++) {
                if (!flowElements.get(i).getIncomingTestFlow().isEmpty() && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
                    LOG.info("GROESSE");
                    LOG.info(flowElements.get(i).getIncomingTestFlow().size() + "");
                    for (int j = 0; j < flowElements.get(i).getIncomingTestFlow().size(); j++) {
                        String outgoingflowId = flowElements.get(i).getIncomingTestFlow().get(j).getId();
                        LOG.info("OUTERFLOWID BPMNSUBPROCESS {} OUTGOINGFLOWID {}", bpmnSubprocess.getId(), outgoingflowId);
                        LOG.info(outgoingflowId.equals(bpmnSubprocess.getId()) + "");
                        if (outgoingflowId.equals(bpmnSubprocess.getId())) {
                            Node outgoing = createOutgoingFlowAsNode(flowElements.get(i).getId());
                            LOG.info("DURCHGELAUFEN");
                            Node importedNode = doc.importNode(outgoing, true);
                            LOG.info("das geht");
                            test.add(flowElements.get(i).getId());
                            //bpmnSubprocessNode.appendChild(importedNode);
                        }
                    }
                }
            }
        }
        return test;
    }

    private ArrayList<String> computeErrorOutgoingFlowElements(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        LOG.info(bpmnSubprocess.getId());
        LOG.info("aaaaaaaaaaaaaaaaaaaaaaa");
        LOG.info("NODE HAT FUNKTIONIERT");
        Document doc = bpmnSubprocess.getBpmnDocument();
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getErrorFlowElements();
        if (!bpmnSubprocess.getId().contains("firstStartEvent") && !bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getErrorFlowElements();
            }
            for (int i = 0; i < flowElements.size(); i++) {
                if (!flowElements.get(i).getIncomingTestFlow().isEmpty()) {
                    LOG.info("GROESSE");
                    LOG.info(flowElements.get(i).getIncomingTestFlow().size() + "");
                    for (int j = 0; j < flowElements.get(i).getIncomingTestFlow().size(); j++) {
                        String outgoingflowId = flowElements.get(i).getIncomingTestFlow().get(j).getId();
                        LOG.info("OUTERFLOWID BPMNSUBPROCESS {} OUTGOINGFLOWID {}", bpmnSubprocess.getId(), outgoingflowId);
                        LOG.info(outgoingflowId.equals(bpmnSubprocess.getId()) + "");
                        if (outgoingflowId.equals(bpmnSubprocess.getId())) {
                            Node outgoing = createOutgoingFlowAsNode(flowElements.get(i).getId());
                            LOG.info("DURCHGELAUFEN");
                            Node importedNode = doc.importNode(outgoing, true);
                            LOG.info("das geht");
                            test.add(flowElements.get(i).getId());
                            //bpmnSubprocessNode.appendChild(importedNode);
                        }
                    }
                }
            }
        }
        return test;
    }

    private ArrayList<String> computeIncomingFlowElements(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        LOG.info(bpmnSubprocess.getId());
        LOG.info("aaaaaaaaaaaaaaaaaaaaaaa");
        LOG.info("NODE HAT FUNKTIONIERT");
        Document doc = bpmnSubprocess.getBpmnDocument();
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getFlowElements();
        if (!bpmnSubprocess.getId().contains("BoundaryEvent")) {
            if (bpmnSubprocess.getParentProcess() != null) {
                flowElements = bpmnSubprocess.getParentProcess().getFlowElements();
            }
            for (int i = 0; i < flowElements.size(); i++) {
                LOG.info("DAS SIND ALLE FLOWIDS {}", flowElements.get(i).getId());
            }

            for (int i = 0; i < flowElements.size(); i++) {
                LOG.info("FRD {} {}", bpmnSubprocess.getId(), flowElements.get(i).getId());
                if (!flowElements.get(i).getOuterflow().isEmpty()) {

                    for (int j = 0; j < flowElements.get(i).getOuterFlow().size(); j++) {
                        String incomingflowId = flowElements.get(i).getOuterFlow().get(j).getId();
                        LOG.info("FRD2 {} {}", bpmnSubprocess.getId(), incomingflowId);
                        if (incomingflowId.equals(bpmnSubprocess.getId())) {
                            //      Node ingoing = createIncomingFlowAsNode(flowElements.get(i).getId());
                            test.add(flowElements.get(i).getId());
                            //bpmnSubprocessNode.appendChild(doc.importNode(ingoing, true));
                        }
                    }
                }
            }
        }
        return test;
    }

    private ArrayList<String> computeIncomingErrorFlowElements(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        LOG.info(bpmnSubprocess.getId());
        LOG.info("aaaaaaaaaaaaaaaaaaaaaaa");
        LOG.info("NODE HAT FUNKTIONIERT");
        Document doc = bpmnSubprocess.getBpmnDocument();
        ArrayList<String> test = new ArrayList<>();
        ArrayList<BPMNSubprocess> flowElements = bpmnSubprocess.getBuildPlan().getErrorFlowElements();
        if (bpmnSubprocess.getParentProcess() != null) {
            flowElements = bpmnSubprocess.getParentProcess().getErrorFlowElements();
        }

        for (int i = 0; i < flowElements.size(); i++) {
            LOG.info("FRDERROR {} {}", bpmnSubprocess.getId(), flowElements.get(i).getId());
            if (!flowElements.get(i).getOuterflow().isEmpty()) {

                for (int j = 0; j < flowElements.get(i).getOuterFlow().size(); j++) {
                    String incomingflowId = flowElements.get(i).getOuterFlow().get(j).getId();
                    LOG.info("FRDERROR2 {} {}", bpmnSubprocess.getId(), incomingflowId);
                    if (incomingflowId.equals(bpmnSubprocess.getId())) {
                        //      Node ingoing = createIncomingFlowAsNode(flowElements.get(i).getId());
                        test.add(flowElements.get(i).getId());
                        //bpmnSubprocessNode.appendChild(doc.importNode(ingoing, true));
                    }
                }
            }
        }
        return test;
    }

    private Node createOutgoingFlowAsNode(String id) throws IOException, SAXException {
        LOG.info("OUTERFLOWASNODE");
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNOutgoing.xml"));
        template = template.replace("OutgoingFlowToReplace", id);
        return this.transformStringToNode(template);
    }

    private Node createIncomingFlowAsNode(String id) throws IOException, SAXException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNIncoming.xml"));
        template = template.replace("IncomingFlowToReplace", id);
        return this.transformStringToNode(template);
    }

    /**
     * create a bpmn end event node from template String
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNEndEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String endEvent = createBPMNEndEvent(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP13");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, endEvent);
        return node;
    }

    /**
     * create a bpmn end event template String
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    public String createBPMNEndEvent(BPMNSubprocess bpmnSubprocess) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        template = template.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        template = template.replaceAll("Flow_Input", "Flow_" + sourceId);
        //bpmnSubprocess.setInflow("Flow_" + sourceId);
        //bpmnSubprocess.setErrorInnerflow("ErrorInnerFlow_" + sourceId);
        return template;
    }

    /**
     * create a bpmn error end event node from template String
     *
     * @param bpmnSubprocess
     * @return created node
     * @throws IOException
     * @throws SAXException
     */
    public Node createBPMNErrorEndEventAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String template = createBPMNErrorEndEvent(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP14");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        // End Event only has incoming Flows
        //addIncomings(bpmnSubprocess);
        return node;
    }

    /**
     * create a bpmn error end event template String
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    public String createBPMNErrorEndEvent(BPMNSubprocess bpmnSubprocess) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNErrorEndEvent.xml"));
        int id = bpmnSubprocess.getBuildPlan().getIdForErrorInnerFlowAndIncrement();
        template = template.replaceAll("Event_IdToReplace", bpmnSubprocess.getId());
        // This is currently a high assumption that we only have one specific error
        int errorId = bpmnSubprocess.getErrorEventIds().get(0);
        template = template.replaceAll("errorRefToSet", "Error_Event" + errorId);
        String incomingBoundaryLinks = "";
        for (BPMNSubprocess subprocess : bpmnSubprocess.getIncomingLinks()) {
            incomingBoundaryLinks = incomingBoundaryLinks + "<bpmn:incoming>" + subprocess.getId() + "</bpmn:incoming>";
        }

        template = template.replaceAll("<bpmn:incoming>Flow_Input</bpmn:incoming>", incomingBoundaryLinks);
        bpmnSubprocess.setInflow("ErrorInnerFlow_" + id);

        final String idPrefix = BPMNSubprocessType.EVENT.toString();
        template = template.replaceAll("ErrorEvenDefinitionIdToSet", "Error_" + idPrefix + id);
        return template;
    }

    /**
     * create a callNodeOperation node from template String
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    private Node createCallNodeOperationTaskAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String template = createNodeOperation(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP15");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        return node;
    }

    /**
     * create a NodeTemplateInstanceTask node from template String
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     * @throws SAXException
     */
    private Node createCreateNodeTemplateInstanceTaskAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String template = createNodeTemplateInstance(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP16");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        return node;
    }

    /**
     * create a BpmnServiceInstance node from template String
     *
     * @param bpmnSubprocess
     * @return created node
     * @throws IOException
     * @throws SAXException
     */
    private Node createBPMNCreateServiceInstanceAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        LOG.info(" createBPMNCreateServiceInstanceAsNode");
        String template = createServiceInstance(bpmnSubprocess);
        LOG.info(template);
        LOG.info("PPPPPPPPPPPPPPP17");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        return node;
    }

    /**
     * create a SetServiceTempalte node from template String
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createSetServiceTemplateStateAsNode(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        final String templateString = createSetServiceTemplateState(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP18");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, templateString);
        return node;
    }

    /**
     * create a SetServiceTemplate template String
     *
     * @param bpmnSubprocess
     * @return template String
     * @throws IOException
     */
    private String createSetServiceTemplateState(BPMNSubprocess bpmnSubprocess) throws IOException {
        String setState = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetStateTask.xml"));
        setState = setState.replace("Activity_IdToSet", bpmnSubprocess.getId());
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        LOG.info("createSetServiceTemplateState");
        setState = setState.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        setState = setState.replace("Flow_Input", "Flow_" + sourceId);
        setState = setState.replace("Flow_Output", "Flow_" + id);

        LOG.info("createSetStateTask12345");
        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() != BPMNSubprocessType.DATA_OBJECT_INOUT) {
                LOG.info(bpmnDataObject.getId());
                LOG.info(bpmnDataObject.getNodeTemplate());
                LOG.info(bpmnDataObject.getRelationshipTemplate());
                if (bpmnSubprocess.getNodeTemplate() != null) {
                    if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE && bpmnDataObject.getNodeTemplate().equals(bpmnSubprocess.getNodeTemplate().getId())) {
                        setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getNodeInstanceURL() + "}");
                    }
                } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
                    if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_REL && bpmnDataObject.getRelationshipTemplate().equals(bpmnSubprocess.getRelationshipTemplate().getId())) {
                        setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getRelationshipInstanceURL() + "}");
                    }
                } else {
                    setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getServiceInstanceURL() + "}");
                }
            }
        }
        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);
        return setState;
    }

    /**
     * create set State Node for service instance in the outer flow
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    public Node createOuterSetServiceTemplateStateAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        final String template = createOuterSetServiceTemplateState(bpmnSubprocess);
        return this.createImportNodeFromString(bpmnSubprocess, template);
    }

    /**
     * create set State Node template String for service instance in the outer flow
     *
     * @param bpmnSubprocess the subprocess
     * @return created Node template String
     * @throws IOException
     */
    private String createOuterSetServiceTemplateState(BPMNSubprocess bpmnSubprocess) throws IOException {
        String setState = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNSetStateTask.xml"));
        setState = setState.replace("Activity_IdToSet", bpmnSubprocess.getId());
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        int sourceId = id - 1;
        setState = setState.replaceAll("StateToSet", bpmnSubprocess.getInstanceState());
        //setState = setState.replace("Flow_Input", "OuterFlow_" + sourceId);
        //setState = setState.replace("Flow_Output", "OuterFlow_" + id);
        setState = setState.replace("Set State", "Set ServiceTemplateInstance State");

        for (BPMNDataObject bpmnDataObject : bpmnSubprocess.getBuildPlan().getDataObjectsList()) {
            if (bpmnDataObject.getDataObjectType() != BPMNSubprocessType.DATA_OBJECT_INOUT) {
                if (bpmnSubprocess.getNodeTemplate() != null) {
                    if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE && bpmnDataObject.getNodeTemplate().equals(bpmnSubprocess.getNodeTemplate().getId())) {
                        setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getNodeInstanceURL() + "}");
                    }
                } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
                    if (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_REL && bpmnDataObject.getRelationshipTemplate().equals(bpmnSubprocess.getRelationshipTemplate().getId())) {
                        setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getRelationshipInstanceURL() + "}");
                    }
                } else {
                    setState = setState.replaceAll("InstanceURLToSet", "\\${" + bpmnDataObject.getServiceInstanceURL() + "}");
                }
            }
        }
        bpmnSubprocess.setInflow("OuterFlow_" + sourceId);
        bpmnSubprocess.setOutflow("OuterFlow_" + id);
        return setState;
    }

    /**
     * create a RelationshipTempalteInstance node from template String
     *
     * @param bpmnSubprocess
     * @return created Node
     * @throws IOException
     * @throws SAXException
     */
    private Node createRelationshipTemplateInstanceAsNode(BPMNSubprocess bpmnSubprocess) throws
        IOException, SAXException {
        String template = createRelationshipTemplateInstance(bpmnSubprocess);
        LOG.info("PPPPPPPPPPPPPPP19");
        LOG.info(bpmnSubprocess.getId());
        Node node = this.createImportNodeFromString(bpmnSubprocess, template);
        //addIncomings(bpmnSubprocess);
        //addOutgoings(bpmnSubprocess);
        return node;
    }

    /**
     * create a ServiceInstance template String
     *
     * @param bpmnSubprocess the subprocess
     * @return tempalte String
     * @throws IOException
     * @throws SAXException
     */
    public String createServiceInstance(BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        LOG.info("Passiert hier was");
        int id = bpmnSubprocess.getBuildPlan().getIdForNamesAndIncrement();
        int sourceId = id - 1;
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNCreateServiceTemplateInstanceScriptTask.xml"));
        template = template.replaceAll("ResultVariableToSet", bpmnSubprocess.getResultVariableName());
        template = template.replaceAll("Subprocess_IdToSet", bpmnSubprocess.getId());
        template = template.replaceAll("Flow_Input", "Flow_" + sourceId);
        template = template.replaceAll("Flow_Output", "Flow_" + id);
        template = template.replaceAll("StateToSet", "CREATING");
        // we create for each output parameter a corresponding variable later in activate data object
        String outputParameterNames = "";
        HashMap<String, String> propertyNames = bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters();
        for (String propertyOutput : bpmnSubprocess.getBuildPlan().getPropertiesOutputParameters().keySet()) {
            outputParameterNames = propertyOutput + "," + outputParameterNames;
        }
        template = template.replaceAll("OutputParameterNamesToSet", outputParameterNames);
        String[] original = template.split("</camunda:inputOutput>");
        original[1] = "</camunda:inputOutput>" + original[1];

        String inputparambuilder = "";
        for (String namestring : outputParameterNames.split(",")) {
            //String namestringvalue = bpmnSubprocess.getInputparamvalues().split(",")[counter];
            inputparambuilder = inputparambuilder + "<camunda:inputParameter name=\"Input_" + namestring + "\">" + propertyNames.get(namestring) + "</camunda:inputParameter>";
        }

        String resultstring = original[0] + inputparambuilder + original[1];
        //Node nodetemplateInstanceNode = this.transformStringToNode(template);
        //cope.setBpmnSubprocessElement((Element) serv);
        bpmnSubprocess.setInflow("Flow_" + sourceId);
        bpmnSubprocess.setOutflow("Flow_" + id);

        LOG.info("create instance template string");
        LOG.info(template);
        template = resultstring;
        return template;
    }

    /**
     * Creates a start Event and connects it to the first bpmnSubprocess.
     *
     * @param bpmnSubprocess
     * @param name
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public Node createOuterBPMNStartEvent(BPMNSubprocess bpmnSubprocess, String name) throws
        IOException, SAXException {
        int id = bpmnSubprocess.getBuildPlan().getIdForOuterFlowAndIncrement();
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNStartEvent.xml"));
        template = template.replaceAll("Event_IdToReplace", name);
        //template = template.replaceAll("Flow_Output", "OuterFlow_" + id);
        Node startEvent = this.createImportNodeFromString(bpmnSubprocess, template);
        //bpmnSubprocess.setOutflow("OuterFlow_" + id);
        return startEvent;
    }
}
