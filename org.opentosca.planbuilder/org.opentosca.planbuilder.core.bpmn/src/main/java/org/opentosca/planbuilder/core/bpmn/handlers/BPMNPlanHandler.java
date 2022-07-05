package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;

/**
 * Handles all actions which are required for the BPMN Plan. For example initialization of xml and script documents.
 */
public class BPMNPlanHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPlanHandler.class);
    private final DocumentBuilder documentBuilder;
    private final BPMNSubprocessHandler bpmnSubprocessHandler;
    private final BPMNProcessFragments processFragments;
    final static String xmlns = "http://www.w3.org/2000/xmlns/";
    final static String[][] namespaces = {
        {"xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"},
        {"xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"},
        {"xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC"},
        {"xmlns:camunda", "http://camunda.org/schema/1.0/bpmn"},
        {"xmlns:di", "http://www.omg.org/spec/DD/20100524/DI"},
        // this might be not necessary since the namespace is for the modeler
        {"xmlns:qa", "http://some-company/schema/bpmn/qa"},
    };

    public static final String[] BPMN_SCRIPT_NAMES = {"CreateServiceInstance",
        "CreateNodeInstance",
        "CreateRelationshipInstance",
        "CallNodeOperation",
        "DataObject",
        "SetProperties",
        "SetState", "SetOutputParameters"};

    public BPMNPlanHandler() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        this.processFragments = new BPMNProcessFragments();
    }

    /**
     * Creates an "empty" xml document. The main reason why we called it empty because there are no elements in the
     * process.
     */
    public BPMNPlan createEmptyBPMNPlan(final AbstractPlan abstractPlan) {
        BPMNPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}",
            abstractPlan.getServiceTemplate().getId());

        final BPMNPlan buildPlan =
            new BPMNPlan(abstractPlan.getId(), abstractPlan.getType(), abstractPlan.getDefinitions(),
                abstractPlan.getServiceTemplate(), abstractPlan.getActivites(), abstractPlan.getLinks());
        initializeXMLElements(buildPlan);
        initializeScriptDocuments(buildPlan);

        return buildPlan;
    }

    /**
     * Initialize the script Elements to export them later.
     */
    public void initializeScriptDocuments(final BPMNPlan newBuildPlan) {
        ArrayList<String> scripts = new ArrayList<>();
        newBuildPlan.setBpmnScript(scripts);
        ArrayList<String> scriptNames = new ArrayList<>();
        String script;
        try {
            for (String name : BPMN_SCRIPT_NAMES) {
                script = processFragments.createScript(name);
                scripts.add(script);
                scriptNames.add(name);
            }
            newBuildPlan.setScriptNames(scriptNames);
            newBuildPlan.setBpmnScript(scripts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the necessary namespaces, definition and process element. The diagram components are added in the
     * BPMNFinalizer.
     */
    public void initializeXMLElements(final BPMNPlan bpmnPlan) {
        bpmnPlan.getIdForNamesAndIncrement();
        bpmnPlan.setBpmnDocument(this.documentBuilder.newDocument());
        bpmnPlan.setBpmnDefinitionElement(bpmnPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:definitions"));
        bpmnPlan.getBpmnDocument().appendChild(bpmnPlan.getBpmnDefinitionElement());
        // declare xml schema namespace
        for (String[] namespace : namespaces) {
            bpmnPlan.getBpmnDefinitionElement().setAttributeNS(xmlns, namespace[0], namespace[1]);
        }

        bpmnPlan.getBpmnDefinitionElement().setAttribute("id", "Definitions_" + bpmnPlan.getIdForNamesAndIncrement());
        // set targetNamespace for deployment
        bpmnPlan.getBpmnDefinitionElement().setAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");

        // initialize and append extensions element to process
        bpmnPlan.setBpmnProcessElement((bpmnPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:process")));

        bpmnPlan.getBpmnProcessElement().setAttribute("id", "Process_" + bpmnPlan.getIdForNamesAndIncrement());
        bpmnPlan.getBpmnProcessElement().setAttribute("isExecutable", "true");
        bpmnPlan.getBpmnDefinitionElement().appendChild(bpmnPlan.getBpmnProcessElement());
    }

    /**
     * Creates for each activity an empty subprocess and add a data object of a specific type. For example for
     * NodeTemplateActivity the data object type is DATA_OBJECT_NODE. For RelationshipActivity the data object type is
     * DATA_OBJECT_REL. The fault subprocess is added in the BPMNFinalizer.
     */
    public void initializeBPMNSkeleton(final BPMNPlan plan) {
        ArrayList<String> visitedNodeIds = new ArrayList<>();
        for (final AbstractPlan.Link links : plan.getLinks()) {
            AbstractActivity source = links.getSrcActiv();
            AbstractActivity target = links.getTrgActiv();
            BPMNSubprocess subprocess;
            if (source instanceof NodeTemplateActivity) {
                ((RelationshipTemplateActivity) target).setVisitedCounter();
                if (!visitedNodeIds.contains(source.getId())) {
                    subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(source, plan);
                    visitedNodeIds.add(source.getId());
                    LOG.debug("Generate empty subprocess for {}", source);
                    plan.addSubprocess(subprocess);
                }
                // each relationship template must be visited twice after that
                // we can add it otherwise one of the instance url is unknown
                if (((RelationshipTemplateActivity) target).getVisitedCounter() == 2) {
                    subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(target, plan);
                    LOG.debug("Generate empty subprocess for {}", target);
                    plan.addSubprocess(subprocess);
                }
            }
            if (source instanceof RelationshipTemplateActivity) {
                ((RelationshipTemplateActivity) source).setVisitedCounter();
                if (!visitedNodeIds.contains(target.getId())) {
                    subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(target, plan);
                    visitedNodeIds.add(target.getId());
                    LOG.debug("Generate empty subprocess for target node {}", target);
                    plan.addSubprocess(subprocess);
                }

                // each relationship template must be visited twice after that
                // we can add it otherwise one of the instance url is unknown
                if (((RelationshipTemplateActivity) source).getVisitedCounter() == 2) {
                    subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(source, plan);
                    LOG.debug("Generate empty subprocess for {}", source);
                    plan.addSubprocess(subprocess);
                }
            }
        }
    }

    /**
     * In the BPMNBuildProcessBuilder we already created a subprocess which is now filled with activate data object
     * tasks to make use of the data objects.
     */
    public void addActivateDataObjectTaskToSubprocess(BPMNSubprocess dataObjectSubprocess, BPMNPlan bpmnPlan) {
        for (BPMNDataObject bpmnDataObject : bpmnPlan.getDataObjectsList()) {
            BPMNSubprocess activateDataObjectTask = new BPMNSubprocess(BPMNSubprocessType.ACTIVATE_DATA_OBJECT_TASK, bpmnDataObject.getId() + "_DataObjectActivateTask");
            activateDataObjectTask.setBuildPlan(bpmnPlan);
            TNodeTemplate dataObjectNodeTemplate = null;
            for (int i = 0; i < Objects.requireNonNull(bpmnPlan.getServiceTemplate().getTopologyTemplate()).getNodeTemplates().size(); i++) {
                TNodeTemplate nodeTemplate = bpmnPlan.getServiceTemplate().getTopologyTemplate().getNodeTemplates().get(i);
                if (nodeTemplate.getId().equals(bpmnDataObject.getNodeTemplate())) {
                    dataObjectNodeTemplate = nodeTemplate;
                }
            }
            // dataObjectNodeTemplate can be null if we have serviceInstance, inout or relationship Data Objects
            if (dataObjectNodeTemplate != null) {
                ArrayList<String> properties = this.bpmnSubprocessHandler.computePropertiesOfNodeTemplate(dataObjectNodeTemplate);
                bpmnDataObject.setProperties(properties);
            }

            activateDataObjectTask.setDataObject(bpmnDataObject);
            dataObjectSubprocess.addTaskToSubprocess(activateDataObjectTask);
        }
    }
}
