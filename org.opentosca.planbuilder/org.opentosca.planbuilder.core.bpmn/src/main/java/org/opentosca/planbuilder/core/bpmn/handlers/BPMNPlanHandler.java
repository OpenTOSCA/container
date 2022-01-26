package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.javatuples.Pair;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;

public class BPMNPlanHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPlanHandler.class);
    private final DocumentBuilderFactory documentBuilderFactory;
    private final DocumentBuilder documentBuilder;
    private final BPMNScopeHandler bpmnScopeHandler;
    private final BPMNProcessFragments fragmentclass;
    private final BPMNDiagramElementHandler diagramHandler;
    // TODO: consider non-hard coded id
    final static String[][] NS_PAIRS = {
        {"xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"},
        {"xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"},
        {"xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC"},
        {"xmlns:camunda", "http://camunda.org/schema/1.0/bpmn"},
        {"xmlns:di", "http://www.omg.org/spec/DD/20100524/DI"},
        {"xmlns:qa", "http://some-company/schema/bpmn/qa"},
    };

    public BPMNPlanHandler() throws ParserConfigurationException {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
        this.bpmnScopeHandler = new BPMNScopeHandler();
        // test
        this.fragmentclass = new BPMNProcessFragments();
        this.diagramHandler = new BPMNDiagramElementHandler();
    }

    public BPMNPlan createEmptyBPMNPlan(final String processNamespace, final String processName,
                                        final AbstractPlan abstractPlan, final String inputOperationName) {
        BPMNPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}",
            abstractPlan.getServiceTemplate().getId());

        final BPMNPlan buildPlan =
            new BPMNPlan(abstractPlan.getId(), abstractPlan.getType(), abstractPlan.getDefinitions(),
                abstractPlan.getServiceTemplate(), abstractPlan.getActivites(), abstractPlan.getLinks());
        initializeXMLElements(buildPlan);
        initializeScriptDocuments(buildPlan);

        return buildPlan;
    }

    public void initializeScriptDocuments(final BPMNPlan newBuildPlan) {
        ArrayList<String> scripts = new ArrayList<>();
        newBuildPlan.setBpmnScript(scripts);
        String script = "";
        try {
            String[] scriptNames = {"CreateServiceInstance", "CreateNodeInstance", "CreateRelationshipInstance", "DataObject", "SetProperties", "SetState"};
            for (String name : scriptNames) {
                script = fragmentclass.createScript(name);
                scripts.add(script);
            }
            newBuildPlan.setBpmnScript(scripts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the basic XML elements for BPMN plan
     * bpmn:definitions
     * - bpmn:process
     * - bpmndi:BPMNDiagram
     * @param newBuildPlan
     */
    public void initializeXMLElements(final BPMNPlan newBuildPlan) {
        newBuildPlan.setBpmnDocument(this.documentBuilder.newDocument());
        // root: definition element
        Element definition = newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace, "bpmn:definitions");

        // declare xml schema namespace
        for (String[] pair : NS_PAIRS) {
            definition.setAttributeNS("http://www.w3.org/2000/xmlns/", pair[0], pair[1]);
        }

        // TODO: consider non-hardcode process_id
        definition.setAttribute("id", "Definitions_0");
        definition.setAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");

        newBuildPlan.setBpmnDefinitionElement(definition);
        newBuildPlan.getBpmnDocument().appendChild(newBuildPlan.getBpmnDefinitionElement());

        // initialize and append extensions element to process
        // process element
        Element process = newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace, "bpmn:process");

        process.setAttribute("id", "Process_0");
        process.setAttribute("isExecutable", "true");


        newBuildPlan.setBpmnProcessElement(process);
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnProcessElement());

        // diagram element
        Element diagram = newBuildPlan.getBpmnDocument().createElement("bpmndi:BPMNDiagram");

        diagram.setAttribute("id", "Diagram_0");

        newBuildPlan.setBpmnDiagramElement(diagram);
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnDiagramElement());

        Element planeElement = newBuildPlan.getBpmnDocument().createElement("bpmndi:BPMNPlane");

        planeElement.setAttribute("id", "Plane_0");
        // every elements in bpmn:BPMNDiagram needs to have a matching element in bpmn:process
        planeElement.setAttribute("bpmnElement", "Process_0");

        newBuildPlan.setBpmnPlaneElement(planeElement);
        newBuildPlan.getBpmnDiagramElement().appendChild(newBuildPlan.getBpmnPlaneElement());
    }

    /**
     * Instantiate all BPMN Process elements with class BPMNScope for later plugin refinement
     * @param plan
     * @param csar
     */
    public void initializeBPMNSkeleton(final BPMNPlan plan, final Csar csar) {
        plan.setCsarName(csar.id().csarName());

        final Map<AbstractActivity, BPMNScope> abstract2bpmnMap = new HashMap<>();

        // TODO: instantiate and add "Create Service Template Instance" BPMN script task
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(plan);
        BPMNScope createServiceTemplateInstanceTask = bpmnScopeHandler.createServiceTemplateInstanceTask(plan);
        bpmnScopeHandler.createSequenceFlow(startEvent, createServiceTemplateInstanceTask, plan);

        // a graph of AbstractActivity used for BFS
        final Map<AbstractActivity, Set<AbstractActivity>> aaGraph = new HashMap<>();
        // [curA : prevB]
        Deque<Pair<AbstractActivity, BPMNScope>> q = new LinkedList<>();
        Set<AbstractActivity> visited = new HashSet<>();

        // build graph for iteration
        for (final AbstractPlan.Link link : plan.getLinks()) {
            AbstractActivity src = link.getSrcActiv();
            AbstractActivity trg = link.getTrgActiv();
            aaGraph.putIfAbsent(src, new HashSet<>());
            aaGraph.get(src).add(trg);
        }

        for (final AbstractActivity activity : plan.getSources()) {
            q.offer(new Pair<>(activity, createServiceTemplateInstanceTask));
        }

        AbstractActivity curA = null;
        BPMNScope prevB = null;

        while (!q.isEmpty()) {
            int size = q.size();
            while (size > 0) {
                Pair<AbstractActivity, BPMNScope> p = q.poll();
                size -= 1;

                curA = p.getValue0();
                prevB = p.getValue1();

                if (aaGraph.containsKey(curA)) {
                    List<AbstractActivity> order = new ArrayList<>();
                    order.add(curA);
                    Set<AbstractActivity> nxtSet = aaGraph.get(curA);
                    for (AbstractActivity nxtA : nxtSet) {
                        // need to go one more step to reach another NodeTemplate
                        if (nxtA instanceof RelationshipTemplateActivity) {
                            AbstractActivity nxtnxtA = aaGraph.get(nxtA).iterator().next();
                            order.add(nxtnxtA);
                        }
                        // add RelationshipTemplate last
                        order.add(nxtA);
                    }


                    // iterating to instantiate BPMNScope
                    for (int i = 0; i < order.size(); i += 1) {
                        curA = order.get(i);

                        // make sure no duplicate is created
                        BPMNScope curB = abstract2bpmnMap.getOrDefault(curA, bpmnScopeHandler.createTemplateBuildPlan(curA, plan));
                        abstract2bpmnMap.put(curA, curB);
                        bpmnScopeHandler.createSequenceFlow(prevB, curB, plan);
                        prevB = curB;

                        // enqueue at last stage
                        if (i == order.size() - 1) {
                            // size == 3 indicate curA is relationTemplate
                            // need to enqueue prev nodeTemplate
                            if (order.size() == 3) {
                                curA = order.get(i - 1);
                            }

                            // TODO: recheck with parallel case
                            if (visited.contains(curA)) {
                                continue;
                            }

                            visited.add(curA);
                            Pair<AbstractActivity, BPMNScope> nxtPair = new Pair<>(curA, curB);
                            q.offer(nxtPair);
                        }
                    }
                }
            }
        }

        // TODO: instantiate and set "Set Service Template State" BPMN script task
        BPMNScope setSTStateTask = bpmnScopeHandler.createSetServiceTemplateStateTask(plan);
        bpmnScopeHandler.createSequenceFlow(prevB, setSTStateTask, plan);
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(plan);
        bpmnScopeHandler.createSequenceFlow(setSTStateTask, endEvent, plan);
    }

    /**
     * The method generate BPMNDiagram objects after all BPMNScope objects are fulfilled by plugin.
     * BPMNDiagram object must have 1-to-1 relation to BPMNScope object. The BPMNDiagram object will
     * later be transformed into XML element by BPMNFinalizer.
     * The method employs BFS to traverse with startEvent and creating connecting BPMNDiagram objects
     * @param bpmnPlan
     */
    public void generateBPMNDiagram(BPMNPlan bpmnPlan) {
        LOG.debug("Generating BPMN Diagram with plan {}", bpmnPlan);
        BPMNScope start = bpmnPlan.getBpmnStartEventElement();
        // {current scope, current start point [x, y]}
        Deque<Pair<BPMNScope, int[]>> q = new LinkedList<>();
        Set<BPMNScope> visited = new HashSet<>();

        q.offer(new Pair(start, new int[]{0, 0}));
        visited.add(start);
        while (!q.isEmpty()) {
            int size = q.size();
            // TODO: consider with parallel case, where y should be split
            while (size > 0) {
                size -= 1;
                Pair<BPMNScope, int[]> cur = q.poll();
                BPMNScope curScope = cur.getValue0();
                int curX = cur.getValue1()[0];
                int curY = cur.getValue1()[1];
                BPMNDiagramElement diagram = diagramHandler.createDiagramElementFromScope(curX, curY, curScope, bpmnPlan);
                for (BPMNScope nxtScope : curScope.getOutgoingLinks()) {
                    if (visited.contains(nxtScope)) {
                        continue;
                    }
                    visited.add(nxtScope);
                    int nx = diagramHandler.getNextXpos(diagram);
                    int ny = diagramHandler.getNextYpos(diagram);
                    q.offer(new Pair<>(nxtScope, new int[]{nx, ny}));
                }
            }
        }
        LOG.debug("Total of {} diagrams are generated from {} BPMN elements", bpmnPlan.getDiagramElements().size(), bpmnPlan.getTemplateBuildPlans().size());
    }
}
