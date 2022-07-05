package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is part of the Facade to handle actions on BuildPlans. This particular class handle XML related operations
 * on TemplateBuildPlans.
 */
public class BPMNSubprocessHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNSubprocessHandler.class);

    private final BPMNProcessFragments processFragments;

    public BPMNSubprocessHandler() throws ParserConfigurationException {
        this.processFragments = new BPMNProcessFragments();
    }

    /**
     * Generates an empty subprocess for the given activity and adds a data object to the buildPlan which can be used
     * later to access properties faster.
     */
    public BPMNSubprocess generateEmptySubprocess(final AbstractActivity activity, final BPMNPlan buildPlan) {
        LOG.debug("Create empty subprocess for abstract activity: {} of type: {}", activity.getId(), activity.getType());
        String idPrefix;
        final BPMNSubprocess bpmnSubprocess;
        final String resultVariablePrefix = "ResultVariable";
        if (activity instanceof NodeTemplateActivity) {
            NodeTemplateActivity ntActivity = (NodeTemplateActivity) activity;
            idPrefix = BPMNSubprocessType.SUBPROCESS.toString();
            bpmnSubprocess = new BPMNSubprocess(ntActivity, BPMNSubprocessType.SUBPROCESS, idPrefix + "_" + activity.getId());
            bpmnSubprocess.setBuildPlan(buildPlan);
            bpmnSubprocess.setNodeTemplate(((NodeTemplateActivity) activity).getNodeTemplate());

            // with each subprocess a node data object is associated to enable fast access to node instance url & maybe properties
            BPMNDataObject dataObject = new BPMNDataObject(BPMNSubprocessType.DATA_OBJECT_NODE, "DataObject_" +activity.getId());
            String resultVariable = resultVariablePrefix + activity.getId();
            dataObject.setNodeInstanceURL(resultVariable);
            dataObject.setNodeTemplate(((NodeTemplateActivity) activity).getNodeTemplate().getId());

            ArrayList<String> properties = computePropertiesOfNodeTemplate(((NodeTemplateActivity) activity).getNodeTemplate());
            dataObject.setProperties(properties);
            buildPlan.getDataObjectsList().add(dataObject);
            bpmnSubprocess.setDataObject(dataObject);
        } else if (activity instanceof RelationshipTemplateActivity) {
            RelationshipTemplateActivity relActivity = (RelationshipTemplateActivity) activity;
            idPrefix = BPMNSubprocessType.SUBPROCESS.toString();
            bpmnSubprocess = new BPMNSubprocess(relActivity, BPMNSubprocessType.SUBPROCESS, idPrefix + "_" + activity.getId());
            bpmnSubprocess.setBuildPlan(buildPlan);
            bpmnSubprocess.setRelationshipTemplate(((RelationshipTemplateActivity) activity).getRelationshipTemplate());

            String source = relActivity.getRelationshipTemplate().getSourceElement().getRef().getId();
            String target = relActivity.getRelationshipTemplate().getTargetElement().getRef().getId();

            // with each subprocess a relationship data object is associated to enable fast access to relationship instance url & maybe properties
            BPMNDataObject dataObject = new BPMNDataObject(BPMNSubprocessType.DATA_OBJECT_REL, "DataObject_" + activity.getId());
            dataObject.setRelationshipTemplate(((RelationshipTemplateActivity) activity).getRelationshipTemplate().getId());
            String resultVariable = resultVariablePrefix + activity.getId();
            dataObject.setSourceInstanceURL(resultVariablePrefix + source + "_provisioning_activity");
            dataObject.setTargetInstanceURL(resultVariablePrefix + target + "_provisioning_activity");
            dataObject.setRelationshipInstanceURL(resultVariable);
            buildPlan.getDataObjectsList().add(dataObject);
            bpmnSubprocess.setDataObject(dataObject);
            return bpmnSubprocess;
        } else {
            LOG.debug("No subprocess is generated");
            bpmnSubprocess = null;
        }

        return bpmnSubprocess;
    }

    /**
     * Creates a set state task inside a subprocess. But this method is only called if we didn't apply any
     * pattern based plugin. Per default, we set then the node template to CREATED.
     */
    public void createSetStateTaskInsideSubprocess(final BPMNPlan buildPlan, final BPMNSubprocess bpmnSubprocess) throws IOException, SAXException {
        String idPrefix = BPMNSubprocessType.TASK.toString();
        final BPMNSubprocess setState = new BPMNSubprocess(BPMNSubprocessType.SET_ST_STATE, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());
        setState.setParentProcess(bpmnSubprocess);
        setState.setBuildPlan(buildPlan);
        if (bpmnSubprocess.getNodeTemplate() != null) {
            setState.setNodeTemplate(bpmnSubprocess.getNodeTemplate());
        } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
            setState.setRelationshipTemplate(bpmnSubprocess.getRelationshipTemplate());
        }
        bpmnSubprocess.setSubProSetStateTask(setState);
        setState.setParentProcess(bpmnSubprocess);
        bpmnSubprocess.setInstanceState("CREATED");
        bpmnSubprocess.addTaskToSubprocess(setState);
        this.processFragments.createSetServiceTemplateStateAsNode(bpmnSubprocess);
    }

    /**
     * Sets the name of the TemplateBuildPlan
     *
     * @param name              the name to set
     * @param templateBuildPlan the TemplateBuildPlan to set the name for
     */
    public void setName(final String name, final BPMNSubprocess templateBuildPlan) {
        BPMNSubprocessHandler.LOG.debug("Setting name {} for TemplateBuildPlan", name);
        // set subprocess name
        templateBuildPlan.getBpmnSubprocessElement().setAttribute("name", name + "_subprocess");
    }

    public BPMNSubprocess createBPMNSubprocessWithinSubprocess(BPMNSubprocess parentSubprocess, BPMNSubprocessType type) {
        LOG.debug("Create BPMN Subprocess with SubprocessType {} within subprocess {}", type.name(), parentSubprocess.getId());
        BPMNPlan buildPlan = parentSubprocess.getBuildPlan();
        AbstractActivity activity = parentSubprocess.getActivity();
        String idPrefix = type.name();
        if (activity instanceof NodeTemplateActivity) {
            NodeTemplateActivity nodeTemplateActivity = (NodeTemplateActivity) activity;
            BPMNSubprocess createdScope = new BPMNSubprocess(nodeTemplateActivity,
                type, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());
            if (type == BPMNSubprocessType.CREATE_NODE_INSTANCE_TASK) {
                parentSubprocess.setSubProCreateNodeInstanceTask(createdScope);
                createdScope.setNodeTemplate(nodeTemplateActivity.getNodeTemplate());
                createdScope.setParentProcess(parentSubprocess);
                createdScope.setBuildPlan(buildPlan);
                return createdScope;
            } else if (type == BPMNSubprocessType.CALL_NODE_OPERATION_TASK) {
                parentSubprocess.setSubProCallOperationTask(createdScope);
                createdScope.setNodeTemplate(parentSubprocess.getNodeTemplate());
                createdScope.setHostingNodeTemplate(parentSubprocess.getHostingNodeTemplate());
                createdScope.setParentProcess(parentSubprocess);
                createdScope.setBuildPlan(buildPlan);
                return createdScope;
            } else if (type == BPMNSubprocessType.SET_NODE_PROPERTY_TASK || type == BPMNSubprocessType.ACTIVATE_DATA_OBJECT_TASK) {
                parentSubprocess.setSubProSetNodePropertyTask(createdScope);
                createdScope.setNodeTemplate(nodeTemplateActivity.getNodeTemplate());
            } else if (type == BPMNSubprocessType.SET_ST_STATE) {
                parentSubprocess.setSubProSetStateTask(createdScope);
                createdScope.setNodeTemplate(nodeTemplateActivity.getNodeTemplate());
                createdScope.setParentProcess(parentSubprocess);
                createdScope.setBuildPlan(buildPlan);
                return createdScope;
            }
            parentSubprocess.addTaskToSubprocess(createdScope);
            createdScope.setParentProcess(parentSubprocess);
            createdScope.setBuildPlan(buildPlan);
            return createdScope;
        } else if (activity instanceof RelationshipTemplateActivity) {
            RelationshipTemplateActivity relationshipTemplateActivity = (RelationshipTemplateActivity) activity;
            BPMNSubprocess createdScope = new BPMNSubprocess(relationshipTemplateActivity,
                type, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());
            createdScope.setRelationshipTemplate(relationshipTemplateActivity.getRelationshipTemplate());
            createdScope.setParentProcess(parentSubprocess);
            createdScope.setBuildPlan(buildPlan);
            if (type == BPMNSubprocessType.CREATE_RT_INSTANCE) {
                parentSubprocess.setSubProCreateNodeInstanceTask(createdScope);
            } else if (type == BPMNSubprocessType.SET_ST_STATE) {
                parentSubprocess.setSubProSetStateTask(createdScope);
            }
            return createdScope;
        }
        return null;
    }

    /**
     * Computes the input parameters based on the topology, e.g. the properties value which starts with get_input.
     */
    public ArrayList<String> computeInputParametersBasedTopology(TTopologyTemplate topologyTemplate) {
        ArrayList<String> inputParameters = new ArrayList<>();
        inputParameters.add("instanceDataAPIUrl");
        inputParameters.add("OpenTOSCAContainerAPIServiceInstanceURL");
        inputParameters.add("CorrelationID");
        for (TNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            Document document = ToscaEngine.getEntityTemplateProperties(nodeTemplate);
            NodeList nodeList = Objects.requireNonNull(document).getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // node.getTextContent() gives the value of the property in the topology template
                if (node.getTextContent().startsWith("get_input")) {
                    String parameterValue = node.getTextContent().split("get_input: ")[1];
                    inputParameters.add(parameterValue);
                }
            }
        }

        return inputParameters;
    }

    /**
     * Computes the properties of the given nodeTemplate.
     */
    public ArrayList<String> computePropertiesOfNodeTemplate(TNodeTemplate nodeTemplate) {
        ArrayList<String> properties = new ArrayList<>();

        Document document = ToscaEngine.getEntityTemplateProperties(nodeTemplate);
        NodeList nodeList = Objects.requireNonNull(document).getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String propertyName = node.getNodeName();
            String propertyValue = node.getTextContent();
            // node.getTextContent() gives the value of the property in the topology template
            if (propertyValue.startsWith("get_input")) {
                propertyValue = node.getTextContent().split("get_input: ")[1];
                // to mark is at get_input value
                properties.add(propertyName + "#G" + propertyValue);
            } else if (!propertyValue.isBlank()) {
                properties.add(propertyName + "#" + propertyValue);
            } else {
                properties.add(propertyName + "#LEER");
            }
        }

        return properties;
    }
}
