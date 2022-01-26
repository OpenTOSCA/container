package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.bpmn.Handler.BPMNScopeHandlerTests;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BPMNProcessFragmentsTests {

    final static Logger logger = LoggerFactory.getLogger(BPMNScopeHandlerTests.class);

    BPMNProcessFragments fragments;
    BPMNPlan bpmnPlan;
    BPMNScopeHandler bpmnScopeHandler;

    @Before
    public void init() throws ParserConfigurationException {
        fragments = new BPMNProcessFragments();
        bpmnPlan = new BPMNPlan("tid", PlanType.BUILD, null, null, null, null);
        bpmnScopeHandler = new BPMNScopeHandler();
    }

    @Test
    public void testStartEventIdSet() throws IOException, SAXException {
        String targetId = "Event_0";
        String template = fragments.createBPMNStartEvent(targetId);
        Node node = fragments.transformStringToNode(template);
        assertThat(((Element) node).getAttribute("id"), is(targetId));
    }

    @Test
    public void testTransformStringToNode() throws IOException, SAXException {
        Node node = fragments.transformStringToNode("<bpmn:incoming>IncomingFlowToReplace</bpmn:incoming>");
        assertThat(node.getNodeName(), is(notNullValue()));
        logger.debug(node.getNodeName());
        assertThat(node.getNodeName(), is("bpmn:incoming"));
    }

    @Test
    public void test() throws IOException, SAXException {
        Node node = fragments.transformStringToNode("<bpmn:incoming>IncomingFlowToReplace</bpmn:incoming>");
        Node parentNode = fragments.transformStringToNode("<bpmn:outgoing>OutgoingFlowToReplace</bpmn:outgoing>");
        Document doc = parentNode.getOwnerDocument();
        parentNode.appendChild(doc.adoptNode(node));
    }

    @Test
    public void testEndEventIdSetShouldOnlyHaveIncoming() throws IOException, SAXException {
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(bpmnPlan);
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(bpmnPlan);
        BPMNScope sf = bpmnScopeHandler.createSequenceFlow(startEvent, endEvent, bpmnPlan);
        for (BPMNScope link : endEvent.getIncomingLinks()) {
            assertThat(sf, is(link));
        }
        Element endNode = (Element) fragments.createBPMNEndEventAsNode(endEvent);
        assertThat(endNode.getElementsByTagName("bpmn:incoming").item(0).getTextContent(), is(sf.getId()));
        assertThat(endNode.getElementsByTagName("bpmn:outgoing").getLength(), is(0));
    }

    @Test
    public void testStartEventIdSetShouldOnlyHaveOutgoing() throws IOException, SAXException {
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(bpmnPlan);
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(bpmnPlan);
        BPMNScope sf = bpmnScopeHandler.createSequenceFlow(startEvent, endEvent, bpmnPlan);
        for (BPMNScope link : startEvent.getOutgoingLinks()) {
            assertThat(sf, is(link));
        }
        Element startNode = (Element) fragments.createBPMNStartEventAsNode(startEvent);

        assertThat(startNode.getElementsByTagName("bpmn:outgoing").item(0).getTextContent(), is(sf.getId()));
        assertThat(startNode.getElementsByTagName("bpmn:incoming").getLength(), is(0));
    }

    @Test
    public void testServiceTemplateInstanceIdSet() throws IOException, SAXException {
        BPMNScope st = bpmnScopeHandler.createServiceTemplateInstanceTask(bpmnPlan);
        Element stNode = (Element) fragments.createServiceTemplateInstanceAsNode(st);

        assertThat(((Element) stNode).getAttribute("id"), is(st.getId()));
        assertThat(stNode.getNodeName(), is("bpmn:scriptTask"));
    }

    @Test
    public void testCreateSetServiceTemplateState() throws IOException, SAXException {
        BPMNScope scope = bpmnScopeHandler.createSetServiceTemplateStateTask(bpmnPlan);
        Element sNode = (Element) fragments.createSetServiceTemplateStateAsNode(scope);
        assertThat((sNode).getAttribute("id"), is(scope.getId()));
    }

    // for debugging purpose, don't remove
    public void exportDocToConsole(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}