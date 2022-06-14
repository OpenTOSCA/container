package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private BPMNProcessFragments processFragments;

    public BPMNFinalizer() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            this.processFragments = new BPMNProcessFragments();
        } catch (ParserConfigurationException e) {
            LOG.error("Initializing factories and handlers failed", e);
        }
    }

    /**
     * The method is responsible to add the xml code of the BPMN subprocess to the bpmn document. First each sequence
     * flow has to be computed (not added!) before the component is added to the xml document otherwise some flow
     * elements are missing. After adding the xml elements, the DiagramAutoGenerator is called (which makes use of the
     * Camunda API). Since data objects are not supported by now, we added them "manually" to the document. According to
     * the bpmn standard, we added the corresponding data flow.
     */
    public void finalize(final BPMNPlan buildPlan) throws IOException, SAXException {
        LOG.info("Finalizing BPMN build Plan {}", buildPlan.getId());
        final Document doc = buildPlan.getBpmnDocument();
        List<BPMNSubprocess> bpmnSubprocessList = buildPlan.getTemplateBuildPlans();
        ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
        ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();

        final Element definitionsElement = buildPlan.getBpmnDefinitionElement();
        int idError0 = buildPlan.getIdForNamesAndIncrement();

        // error events are outside the bpmn:process element
        // currently only one error event since the only error that a script task can throw is the InvalidStatusCode but can be extended very easily
        Node errorEventDefinition = processFragments.createBPMNErrorEventDefinitionAsNode(idError0);
        definitionsElement.appendChild(doc.importNode(errorEventDefinition, true));

        BPMNSubprocess startEvent = new BPMNSubprocess(BPMNSubprocessType.START_EVENT, "StartEvent_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        startEvent.setBuildPlan(buildPlan);

        BPMNSubprocess previousIncoming = startEvent;
        BPMNSubprocess userTask = new BPMNSubprocess(BPMNSubprocessType.USER_TASK, "userTask");
        userTask.setBuildPlan(buildPlan);
        for (BPMNSubprocess bpmnSubprocess : bpmnSubprocessList) {
            // compute sequence flows of subprocess first otherwise flowElements are not correct
            setBuildPlanAndSequenceFlows(buildPlan, flowElements, previousIncoming, bpmnSubprocess, false);
            bpmnSubprocess.getErrorEventIds().add(idError0);
            previousIncoming = bpmnSubprocess;
            for (Integer idError : bpmnSubprocess.getErrorEventIds()) {
                BPMNSubprocess errorSubprocess = new BPMNSubprocess(BPMNSubprocessType.EVENT, "BoundaryEvent_ErrorEvent" + bpmnSubprocess.getId());
                setBuildPlanAndSequenceFlows(buildPlan, errorFlowElements, errorSubprocess, userTask, true);
                computeErrorNodeAndAddToXML(errorSubprocess, idError);
            }
        }

        buildPlan.setFlowElements(flowElements);
        computeNodeAndAddToXML(startEvent);

        BPMNSubprocess lastErrorFlow = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "ErrorEndEvent_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        lastErrorFlow.setBuildPlan(buildPlan);
        BPMNSubprocess outputParamTask = new BPMNSubprocess(BPMNSubprocessType.COMPUTE_OUTPUT_PARAMS_TASK, "Activity_outputParamTask");
        outputParamTask.setDataObject(buildPlan.getDataObjectsList().stream().filter(bpmnDataObject -> (bpmnDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT)).findFirst().get());
        setBuildPlanAndSequenceFlows(buildPlan, flowElements, previousIncoming, outputParamTask, false);
        previousIncoming = outputParamTask;
        for (BPMNSubprocess bpmnSubprocess : bpmnSubprocessList) {
            computeNodeAndAddToXML(bpmnSubprocess);
        }

        BPMNSubprocess endEvent = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "EndEvent_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        setBuildPlanAndSequenceFlows(buildPlan, flowElements, previousIncoming, endEvent, false);

        BPMNSubprocess errorBoundaryOutputTask = new BPMNSubprocess(BPMNSubprocessType.EVENT, "BoundaryEvent_ErrorEvent" + outputParamTask.getId());
        errorBoundaryOutputTask.setBuildPlan(buildPlan);
        setBuildPlanAndSequenceFlows(buildPlan, errorFlowElements, errorBoundaryOutputTask, userTask, true);
        computeNodeAndAddToXML(outputParamTask);

        computeErrorNodeAndAddToXML(errorBoundaryOutputTask, idError0);
        computeNodeAndAddToXML(endEvent);

        BPMNSubprocess errorEndEvent = new BPMNSubprocess(BPMNSubprocessType.END_EVENT, "ErrorEndEvent_" + buildPlan.getIdForErrorOuterFlowAndIncrement());
        setBuildPlanAndSequenceFlows(buildPlan, errorFlowElements, userTask, errorEndEvent, true);
        computeNodeAndAddToXML(userTask);
        computeNodeAndAddToXML(errorEndEvent);
        buildPlan.addSubprocess(outputParamTask);

        // add sequence flows in the end
        for (BPMNSubprocess flow : flowElements) {
            computeNodeAndAddToXML(flow);
        }

        // add error sequence flows in the end
        for (BPMNSubprocess errorFlow : errorFlowElements) {
            computeNodeAndAddToXML(errorFlow);
        }
        String diagram = BPMNDiagramGenerator.generateDiagram(buildPlan);
        Document d;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser;
        try {
            parser = factory.newDocumentBuilder();
            d = parser.parse(new InputSource(new StringReader(Objects.requireNonNull(diagram))));
            for (BPMNDataObject dataObject : buildPlan.getDataObjectsList()) {
                processFragments.createDataObjectAsNode(buildPlan, d, dataObject);
            }

            // to be operational conform each data object has corresponding data flows but this will pollute the model
            // uncomment this if you want all the data flows in your diagram
            // for (BPMNSubprocess bpmnSubprocess : buildPlan.getSubprocess()) {
            //  processFragments.addDataAssociations(buildPlan, d, bpmnSubprocess);
            // }

            // special case for outputParameterTask
            //processFragments.addTaskDataAssociations(buildPlan, d, outputParamTask);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LOG.error("Diagram is null!");
        }

        LOG.info("BPMN build Plan is finalized");
    }

    private void computeNodeAndAddToXML(BPMNSubprocess bpmnSubprocess) {
        Node outputParamTaskNode = processFragments.createBPMNSubprocessAndComponentsAsNode(bpmnSubprocess);
        processFragments.addNodeToBPMN(outputParamTaskNode, bpmnSubprocess.getBuildPlan());
    }

    private void computeErrorNodeAndAddToXML(BPMNSubprocess errorSubprocess, int errorId) throws IOException, SAXException {
        Node outputParamTaskNode = processFragments.createSubprocessErrorBoundaryEventAsNode(errorSubprocess, errorId);
        processFragments.addNodeToBPMN(outputParamTaskNode, errorSubprocess.getBuildPlan());
    }

    public void setBuildPlanAndSequenceFlows(BPMNPlan buildPlan, ArrayList<BPMNSubprocess> flowElements, BPMNSubprocess previousIncoming, BPMNSubprocess outputParamTask, boolean errorFlow) {
        outputParamTask.setBuildPlan(buildPlan);
        previousIncoming.setBuildPlan(buildPlan);
        BPMNSubprocess subprocessToOutputParamTask = new BPMNSubprocess(BPMNSubprocessType.SEQUENCE_FLOW, "ErrorOuterFlow_" + buildPlan.getIdForOuterFlowTestAndIncrement());
        subprocessToOutputParamTask.setBuildPlan(buildPlan);
        subprocessToOutputParamTask.setIncomingTestScope(previousIncoming);
        subprocessToOutputParamTask.setOuterflow(outputParamTask);
        if (!errorFlow) {
            subprocessToOutputParamTask.setId(subprocessToOutputParamTask.getId().replace("Error", ""));
            flowElements.add(subprocessToOutputParamTask);
            buildPlan.setFlowElements(flowElements);
        } else {
            flowElements.add(subprocessToOutputParamTask);
            buildPlan.setErrorFlowElements(flowElements);
        }
    }
}
