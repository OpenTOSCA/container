package org.opentosca.planbuilder.core.bpmn.Handler;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNDiagramElementHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramType;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BPMNDiagramElementHandlerTests {

    private final static int WIDTH = 100;
    private final static int HEIGHT = 80;
    private final static int LENGTH = 100;
    BPMNPlan bpmnPlan;
    BPMNDiagramElementHandler diagramElementHandler;
    BPMNScopeHandler scopeHandler;
    BPMNScope startEvent;
    BPMNScope endEvent;
    BPMNScope sf;
    @Before
    public void init() throws ParserConfigurationException {
        bpmnPlan = new BPMNPlan("test-bpmn-plan-0", PlanType.BUILD, null, null, null, null);
        diagramElementHandler = new BPMNDiagramElementHandler();
        scopeHandler = new BPMNScopeHandler();
        startEvent =  scopeHandler.createStartEvent(bpmnPlan);
        endEvent = scopeHandler.createEndEvent(bpmnPlan);
        sf = scopeHandler.createSequenceFlow(startEvent, endEvent, bpmnPlan);
    }

    @Test
    public void testCreateEdgeFromSequenceFlow() {
        int x = 1;
        int y = 10;
        BPMNDiagramElement diagramElement = diagramElementHandler.createDiagramElementFromScope(x, y, sf, bpmnPlan);
        assertThat(diagramElement.getType(), is(BPMNDiagramType.EDGE));
        assertThat(diagramElement.getXpos(), is(x));
        assertThat(diagramElement.getYpos(), is(y));
        assertThat(diagramElement.getWaypointOutX(), is(x + diagramElement.getLength()));
        assertThat(diagramElement.getWaypointOutY(), is(y));
    }

    @Test
    public void testCreateShape() {
        int x = 0;
        int y = 0;
        BPMNDiagramElement diagramElement = diagramElementHandler.createDiagramElementFromScope(x, y, startEvent, bpmnPlan);
        assertThat(diagramElement.getType(), is(BPMNDiagramType.SHAPE));
        assertThat(diagramElement.getXpos(), is(x));
        assertThat(diagramElement.getYpos(), is(y - diagramElement.getHeight() / 2));
        assertThat(diagramElement.getRefScope(), is(startEvent));
    }

    @Test
    public void testGetNextPos() {
        int x = 1;
        int y = 13;
        BPMNDiagramElement startDi = diagramElementHandler.createDiagramElementFromScope(x, y, startEvent, bpmnPlan);
        int nx = diagramElementHandler.getNextXpos(startDi); // 1 + 100
        int ny = diagramElementHandler.getNextYpos(startDi); // 13
        BPMNDiagramElement sfDi = diagramElementHandler.createDiagramElementFromScope(nx, ny, sf, bpmnPlan);
        assertThat(sfDi.getXpos(), is(startDi.getXpos() + WIDTH));
        int nx1 = diagramElementHandler.getNextXpos(sfDi);
        int ny1 = diagramElementHandler.getNextYpos(sfDi);
        BPMNDiagramElement endDi = diagramElementHandler.createDiagramElementFromScope(nx1, ny1, endEvent, bpmnPlan);
        assertThat(sfDi.getWaypointOutX(), is(endDi.getXpos()));
    }
}
