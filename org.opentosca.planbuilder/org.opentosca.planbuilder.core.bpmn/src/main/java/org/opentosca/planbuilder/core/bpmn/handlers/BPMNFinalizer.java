package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Adds the xml content to the bpmnDocument, creates diagram elements and error subprocess.
 */
public class BPMNFinalizer {
    private final static Logger LOG = LoggerFactory.getLogger(BPMNFinalizer.class);
    private DocumentBuilderFactory docFactory;
    private BPMNProcessFragments processFragments;

    public BPMNFinalizer() {
        try {
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.processFragments = new BPMNProcessFragments();
        } catch (ParserConfigurationException e) {
            LOG.error("Initializing factories and handlers failed", e);
        }
    }

    /**
     * The method is responsible to add the xml code of the BPMN subprocess to the bpmnDocument.
     * After adding the xml elements, the DiagramAutoGenerator is called (which makes use of the Camunda API).
     * Since data objects are not supported by now, we added them manually to the document.
     *
     * @param buildPlan
     * @throws IOException
     * @throws SAXException
     */
    public void finalize(final BPMNPlan buildPlan) throws IOException, SAXException {
        LOG.info("Finalizing BPMN build Plan {}", buildPlan.getId());
        final Document doc = buildPlan.getBpmnDocument();
        List<BPMNSubprocess> bpmnSubprocessList = buildPlan.getTemplateBuildPlans();
        ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
        ArrayList<BPMNSubprocess> errorflowElements = new ArrayList<>();

        // Create and import XML process element to document
        final Element processElement = buildPlan.getBpmnProcessElement();
        final Element definitionsElement = buildPlan.getBpmnDefinitionElement();
        int idError0 = buildPlan.getIdForNamesAndIncrement();

        // error events are outside the bpmn:process element
        // this is very simple for now but can be extended very easily
        Node errorEventDefinition = processFragments.createBPMNErrorEventDefinitionAsNode(idError0);
        definitionsElement.appendChild(doc.importNode(errorEventDefinition, true));

        //create first Start Event
        BPMNSubprocess startEvent = new BPMNSubprocess(BPMNSubprocessType.START_EVENT, "firstStartEvent");
        startEvent.setBuildPlan(buildPlan);

        BPMNSubprocess previousIncoming = startEvent;
        ArrayList<BPMNSubprocess> boundaryEvents = new ArrayList<>();
        BPMNSubprocess userTask = new BPMNSubprocess(BPMNSubprocessType.USER_TASK, "userTask");
        userTask.setBuildPlan(buildPlan);
        for (BPMNSubprocess bpmnSubprocess : bpmnSubprocessList) {
            BPMNSubprocess outerFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestOuterFlow_" + buildPlan.getIdForOuterFlowTestAndIncrement());
            outerFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
            outerFlow.setIncomingTestScope(previousIncoming);
            outerFlow.setOuterflow(bpmnSubprocess);
            previousIncoming = bpmnSubprocess;
            flowElements.add(outerFlow);
            bpmnSubprocess.getErrorEventIds().add(idError0);
            for (Integer idError : bpmnSubprocess.getErrorEventIds()) {
                BPMNSubprocess errorOuterFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "ErrorOuterFlow_" + buildPlan.getIdForErrorOuterFlowAndIncrement());
                BPMNSubprocess errorSubprocess = new BPMNSubprocess(BPMNSubprocessType.EVENT, "BoundaryEvent_ErrorEvent" + bpmnSubprocess.getId());
                errorSubprocess.setBuildPlan(buildPlan);
                errorOuterFlow.setBuildPlan(bpmnSubprocess.getBuildPlan());
                errorOuterFlow.setIncomingTestScope(errorSubprocess);
                errorOuterFlow.setOuterflow(userTask);
                errorflowElements.add(errorOuterFlow);

                buildPlan.setErrorFlowElements(errorflowElements);
                Node errorNode = processFragments.createSubprocessErrorBoundaryEventAsNode(errorSubprocess, idError);
                processElement.appendChild(errorNode);
                //previousErrorSubprocess = bpmnSubprocess;
                boundaryEvents.add(errorSubprocess);
            }
        }

        for (int i = 0; i < flowElements.size(); i++) {
            LOG.info("FLID: {}", flowElements.get(i).getId());
        }
        buildPlan.setFlowElements(flowElements);
        Node firstStartEvent = processFragments.createOuterBPMNStartEvent(startEvent, startEvent.getId());
        processFragments.addNodeToBPMN(firstStartEvent, buildPlan);

        // create first and last error events
        BPMNSubprocess previousErrorSubprocess = new BPMNSubprocess(BPMNSubprocessType.EVENT, "firstErrorEvent");
        previousErrorSubprocess.setBuildPlan(buildPlan);

        BPMNSubprocess lastErrorFlow = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "lastErrorEndEvent");
        lastErrorFlow.setBuildPlan(buildPlan);

        for (BPMNSubprocess bpmnSubprocess : bpmnSubprocessList) {
            // create subprocesses
            Node node = processFragments.createBPMNSubprocessAsNode(bpmnSubprocess);
            processElement.appendChild(node);
            //create connections with sequence flows
            //Node flownode = processFragments.createBPMNSequenceFlow2(startEvent, bpmnSubprocess);
            //buildPlan.getBpmnProcessElement().appendChild(flownode);

            //startEvent = bpmnSubprocess;

            //connect boundary events to user task
            //Node lastErrorflownode = processFragments.createBPMNOuterErrorSequenceFlow(previousErrorSubprocess, userTask);
            //buildPlan.getBpmnProcessElement().appendChild(lastErrorflownode);
            //userTask.setIncomingSubprocess(boundaryEvents);
        }

        // create setService instance state
        BPMNSubprocess setInstanceState = new BPMNSubprocess(BPMNSubprocessType.SET_ST_STATE, "Activity_ServiceInstanceState");
        setInstanceState.setBuildPlan(buildPlan);
        BPMNSubprocess subprocessToStateFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestOuterFlow_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        subprocessToStateFlow.setBuildPlan(buildPlan);
        subprocessToStateFlow.setIncomingTestScope(previousIncoming);
        subprocessToStateFlow.setOuterflow(setInstanceState);
        flowElements.add(subprocessToStateFlow);
        buildPlan.setFlowElements(flowElements);
        //create last end event
        BPMNSubprocess lastFlow = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "lastEndEvent");
        lastFlow.setBuildPlan(buildPlan);

        BPMNSubprocess lastOuterFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "TestOuterFlow_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        lastOuterFlow.setBuildPlan(buildPlan);
        lastOuterFlow.setIncomingTestScope(setInstanceState);
        lastOuterFlow.setOuterflow(lastFlow);
        flowElements.add(lastOuterFlow);
        buildPlan.setFlowElements(flowElements);
        BPMNSubprocess errorOuterFlow = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "ErrorOuterFlow_" + buildPlan.getIdForErrorOuterFlowAndIncrement());
        setInstanceState.setInstanceState("CREATED");

        //create set Service instance state boundary error event
        BPMNSubprocess errorSubprocess2 = new BPMNSubprocess(BPMNSubprocessType.EVENT, "BoundaryEvent_ErrorEvent" + setInstanceState.getId());
        errorSubprocess2.setBuildPlan(buildPlan);
        errorOuterFlow.setBuildPlan(buildPlan);
        errorOuterFlow.setIncomingTestScope(errorSubprocess2);
        errorOuterFlow.setOuterflow(userTask);
        errorflowElements.add(errorOuterFlow);
        buildPlan.setErrorFlowElements(errorflowElements);
        //create user task
        Node setInstancestatNode = processFragments.createBPMNSubprocessAndComponentsAsNode(setInstanceState);
        processFragments.addNodeToBPMN(setInstancestatNode, buildPlan);

        //create setService instance state outer flow
        //Node setServiceInstanceStateflow = processFragments.createBPMNSequenceFlow2(startEvent, setInstanceState);
        //buildPlan.getBpmnProcessElement().appendChild(setServiceInstanceStateflow);
        //startEvent = setInstanceState;

        previousErrorSubprocess = setInstanceState;

        Node errorNode2 = processFragments.createSubprocessErrorBoundaryEventAsNode(errorSubprocess2, idError0);
        processElement.appendChild(errorNode2);
        //boundaryEvents.add(errorSubprocess2);
        //Node lastErrorflownode2 = processFragments.createBPMNOuterErrorSequenceFlow(previousErrorSubprocess, userTask);
        //buildPlan.getBpmnProcessElement().appendChild(lastErrorflownode2);

        //Node lastErrorEndEvent = processFragments.createBPMNSubprocessAndComponentsAsNode(lastErrorFlow);
        LOG.info("LASTENDEVENT1234");
        Node lastEndEvent = processFragments.createBPMNSubprocessAndComponentsAsNode(lastFlow);
        processFragments.addNodeToBPMN(lastEndEvent, buildPlan);

        //Node lastflownode = processFragments.createBPMNSequenceFlow2(startEvent, lastFlow);
        //buildPlan.getBpmnProcessElement().appendChild(lastflownode);

        // create user task Node and flow
        userTask.setIncomingSubprocess(boundaryEvents);

        LOG.info("ADDUSER123");
        Node userTaskAsNode = processFragments.createBPMNUserTaskAsNode(userTask);

        //processFragments.addNodeToBPMN(userTaskAsNode, buildPlan);
        //Node lastErrorflownode = processFragments.createBPMNOuterErrorSequenceFlow(userTask, lastErrorFlow);
        //buildPlan.getBpmnProcessElement().appendChild(lastErrorflownode);
        BPMNSubprocess errorOuterFlowToEndEvent = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "ErrorOuterFlow_" + buildPlan.getIdForErrorOuterFlowAndIncrement());
        setInstanceState.setInstanceState("CREATED");

        //create set Service instance state boundary error event
        BPMNSubprocess errorEndEvent = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "ErrorEndEvent" + buildPlan.getIdForErrorOuterFlowAndIncrement());
        errorEndEvent.setBuildPlan(buildPlan);
        errorOuterFlowToEndEvent.setBuildPlan(buildPlan);
        errorOuterFlowToEndEvent.setIncomingTestScope(userTask);
        errorOuterFlowToEndEvent.setOuterflow(errorEndEvent);
        errorflowElements.add(errorOuterFlowToEndEvent);
        buildPlan.setErrorFlowElements(errorflowElements);
        Node userTaskNode = processFragments.createBPMNUserTaskAsNode(userTask);
        processElement.appendChild(userTaskNode);
        //create last error end event
        Node lastErrorEndEvent = processFragments.createBPMNSubprocessAndComponentsAsNode(errorEndEvent);
        processFragments.addNodeToBPMN(lastErrorEndEvent, buildPlan);

        for (BPMNSubprocess flow : flowElements) {
            Node sequenceNode = processFragments.createBPMNSubprocessAndComponentsAsNode(flow);
            processElement.appendChild(sequenceNode);
        }

        for (BPMNSubprocess errorFlow : errorflowElements) {
            Node sequenceNode = processFragments.createBPMNSubprocessAndComponentsAsNode(errorFlow);
            processElement.appendChild(sequenceNode);
        }
        writeXML(buildPlan.getBpmnDocument());
        String diagram = BPMNDiagramGenerator.generateDiagram(buildPlan);
        Document d;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = null;
        try {
            parser = factory.newDocumentBuilder();
            d = parser.parse(new InputSource(new StringReader(diagram)));
            for (BPMNDataObject dataObject : buildPlan.getDataObjectsList()) {
                processFragments.createDataObjectAsNode(buildPlan, d, dataObject);
            }
        } catch (
            ParserConfigurationException e) {
            e.printStackTrace();
        }

        LOG.info("BPMN build Plan is finalized");
    }

    public void writeXML(Document s) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(s);
            StreamResult result = new StreamResult(new File("C://Users//livia//Downloads//zwischen.xml"));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
