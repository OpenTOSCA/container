package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNComponentType;
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
    private final String xmlns = "http://www.w3.org/2000/xmlns/";
    private final String[][] namespaces = {
        {"xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"},
        {"xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"},
        {"xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC"},
        {"xmlns:camunda", "http://camunda.org/schema/1.0/bpmn"},
        {"xmlns:di", "http://www.omg.org/spec/DD/20100524/DI"},
        // this might be not necessary since the namespace is for the modeler
        {"xmlns:qa", "http://some-company/schema/bpmn/qa"},
    };
    private final String[] BPMN_SCRIPT_NAMES = {"CreateServiceInstance",
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
        newBuildPlan.setBpmnScripts(scripts);
        ArrayList<String> scriptNames = new ArrayList<>();
        String script;
        try {
            for (final String name : BPMN_SCRIPT_NAMES) {
                script = processFragments.createScript(name);
                scripts.add(script);
                scriptNames.add(name);
            }
            newBuildPlan.setScriptNames(scriptNames);
            newBuildPlan.setBpmnScripts(scripts);
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
        for (final String[] namespace : namespaces) {
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
     * For each activity it is checked whether it complies with the hosted on relationship. If this is not the case, the activity is moved forward.
     * Creates for each activity an empty subprocess and add a data object of a specific type. For example for
     * NodeTemplateActivity the data object type is DATA_OBJECT_NODE. For RelationshipActivity the data object type is
     * DATA_OBJECT_REL. The fault subprocess is added in the BPMNFinalizer.
     */
    public void initializeBPMNSkeleton(final BPMNPlan plan) {
        ArrayList<String> visitedNodesID = new ArrayList<>();
        BPMNSubprocess subprocess;
        HashMap<String, Integer> relationshipVisitedMap = new HashMap<>();
        ArrayList<AbstractActivity> sortOfActivities = new ArrayList<>(plan.getActivites());
        for (final AbstractPlan.Link links : plan.getLinks()) {
            for (final AbstractActivity activity : plan.getActivites()) {
                AbstractActivity source = links.getSrcActiv();
                AbstractActivity target = links.getTrgActiv();
                AbstractPlan.Link l = new AbstractPlan.Link(activity, source);
                if (source instanceof RelationshipTemplateActivity && plan.getLinks().contains(l) && !activity.getId().equals(target.getId())) {
                    // if the inequality holds then the target of the hosted on relationship is before the source of the hosted on that's why we need to move the activities forward
                    if (((RelationshipTemplateActivity) source).getRelationshipTemplate().getTypeAsQName().equals(Types.hostedOnRelationType) && sortOfActivities.indexOf(activity) > sortOfActivities.indexOf(target)) {
                        sortOfActivities.remove(activity);
                        sortOfActivities.add(sortOfActivities.indexOf(target), activity);
                    }
                }
            }
        }

        for (final AbstractActivity activity : sortOfActivities) {
            LOG.debug("Generate empty subprocess for {}", activity.getId());
            if (activity instanceof NodeTemplateActivity) {
                subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(activity, plan);
                LOG.debug("Generate empty node template subprocess for {}", activity.getId());
                plan.addSubprocess(subprocess);
                visitedNodesID.add(activity.getId());
                for (final AbstractPlan.Link links : plan.getLinks()) {
                    AbstractActivity source = links.getSrcActiv();
                    AbstractActivity target = links.getTrgActiv();
                    if (target instanceof RelationshipTemplateActivity && source.getId().equals(activity.getId())) {
                        // special case since connectsTo relationship is only target never source
                        if (((RelationshipTemplateActivity) target).getRelationshipTemplate().getTypeAsQName().equals(Types.connectsToRelationType)) {
                            handleRelationshipTemplate(plan, visitedNodesID, relationshipVisitedMap, activity, source, target);
                        }
                    }
                    handleRelationshipTemplate(plan, visitedNodesID, relationshipVisitedMap, activity, source, target);
                    handleRelationshipTemplate(plan, visitedNodesID, relationshipVisitedMap, activity, target, source);
                }
            }
        }
    }

    /**
     * Adds the subprocess to the plan if both node templates are already in the plan
     */
    public void handleRelationshipTemplate(final BPMNPlan plan, final ArrayList<String> visitedNodeIds,
                                           final HashMap<String, Integer> relationshipVisitedMap, final AbstractActivity activity,
                                           final AbstractActivity source, final AbstractActivity target) {
        BPMNSubprocess subprocess;
        if (source instanceof RelationshipTemplateActivity && visitedNodeIds.contains(target.getId()) && target.getId().startsWith(activity.getId())) {
            if (relationshipVisitedMap.containsKey(source.getId())) {
                if (relationshipVisitedMap.get(source.getId()) == 0) {
                    LOG.info("Generate empty relationship template subprocess for {}", source.getId());
                    relationshipVisitedMap.put(source.getId(), relationshipVisitedMap.get(source.getId()) + 1);
                    subprocess = this.bpmnSubprocessHandler.generateEmptySubprocess(source, plan);
                    plan.addSubprocess(subprocess);
                }
            } else {
                relationshipVisitedMap.put(source.getId(), 0);
            }
        }
    }

    /**
     * In the BPMNBuildProcessBuilder we already created a subprocess which is now filled with activate data object
     * tasks to make use of the data objects. for each template we add a tasks in the corresponding subprocess. This enables to read and write the variables
     * during runtime. This is the replacement for the variables in bpel.
     */
    public void addActivateDataObjectTaskToSubprocess(final BPMNSubprocess dataObjectSubprocess,
                                                      final BPMNPlan bpmnPlan) {
        for (final BPMNDataObject bpmnDataObject : bpmnPlan.getDataObjectsList()) {
            BPMNSubprocess activateDataObjectTask = new BPMNSubprocess(BPMNComponentType.ACTIVATE_DATA_OBJECT_TASK, bpmnDataObject.getId() + "_DataObjectActivateTask");
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
