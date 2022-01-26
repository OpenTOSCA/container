package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramType;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BPMNDiagramFragmentTests {
    BPMNDiagramFragments diagramFragments;
    @Before
    public void init() throws ParserConfigurationException {
        diagramFragments = new BPMNDiagramFragments();
    }

    @Test
    public void testShapeIDSet() throws IOException, SAXException {
        int x = 1;
        int y = 13;
        int w = 100;
        int h = 80;
        String id = "015_di";
        BPMNScope scope = new BPMNScope(BPMNScopeType.EVENT, "015");
        BPMNDiagramElement diagram = new BPMNDiagramElement(BPMNDiagramType.SHAPE, x, y, id);
        diagram.setWidth(w);
        diagram.setHeight(h);
        diagram.setRefScope(scope);
        Node node = diagramFragments.createBPMNDiagramElementAsNode(diagram);
        assertThat(((Element) node).getAttribute("id"), is(id));
        assertThat(((Element) node).getAttribute("bpmnElement"), is(diagram.getRefScope().getId()));
        Element child = (Element) ((Element) node).getElementsByTagName("dc:Bounds").item(0);


        assertThat(child.getAttribute("x"), is(String.valueOf(diagram.getXpos())));
        assertThat(child.getAttribute("y"), is(String.valueOf(diagram.getYpos())));
        assertThat(child.getAttribute("width"), is(String.valueOf(diagram.getWidth())));
        assertThat(child.getAttribute("height"), is(String.valueOf(diagram.getHeight())));
    }

    // TODO: implement the EdgeTest
    @Test
    public void testEdgeIDSet() {

    }
}
