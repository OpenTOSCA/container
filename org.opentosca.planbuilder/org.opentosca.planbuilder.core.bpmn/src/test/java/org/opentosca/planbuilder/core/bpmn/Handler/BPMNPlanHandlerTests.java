package org.opentosca.planbuilder.core.bpmn.Handler;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPlanHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BPMNPlanHandlerTests {

    final static Logger logger = LoggerFactory.getLogger(BPMNPlanHandlerTests.class);
    AbstractPlan emptyAbstractPlan;
    BPMNPlan bpmnPlan;
    BPELPlan bpelPlan;
    AbstractPlan abstractPlan;
    BPMNPlanHandler bpmnPlanHandler;

    @Before
    public void init() throws ParserConfigurationException {
        emptyAbstractPlan = new AbstractPlan("tid", PlanType.BUILD, null, null, null, null) {};
        bpmnPlan = new BPMNPlan("tid", PlanType.BUILD, null, null, null, null);
        abstractPlan = new BPMNPlan("tid", PlanType.BUILD, null, null, null, null);
        bpmnPlanHandler = new BPMNPlanHandler();
    }

    @Test
    public void testAbstractPlanCastShouldBeBPMNPlan() {
        assertThat(abstractPlan instanceof BPMNPlan, is(true));
    }

    @Test
    public void testAbstractPlanCastShouldNotBeBPELPlan() {
        assertThat(abstractPlan instanceof BPELPlan, is(false));
    }

    @Test
    public void testCastAbstractPlanShouldBeBPMNPlan() {
        BPMNPlan castedPlan = (BPMNPlan) abstractPlan;
        assertThat(castedPlan instanceof BPMNPlan, is(true));
    }

    @Test
    public void testInitEmptyAbstractPlanShouldBeNoNull() {
        assertThat(emptyAbstractPlan, is(notNullValue()));
    }

    @Test
    public void testInitBPMNPlanShouldBeNoNull() {
        assertThat(bpmnPlan, is(notNullValue()));
    }

    @Test
    public void testBPMNPlanLanguageShouldBeBPMN() {
        assertThat(bpmnPlan.getLanguage(), is(PlanLanguage.BPMN));
    }

    @Test
    public void testInitializeXMLElementsShouldBeNoNull() throws ParserConfigurationException {
        logger.info("Running unit test for {}", "initializeXMLElements");
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        assertThat(bpmnPlan.getBpmnStartEvent(), is(notNullValue()));
        assertThat(bpmnPlan.getBpmnEndEvent(), is(notNullValue()));
        assertThat(bpmnPlan.getBpmnDiagramElement(), is(notNullValue()));
        assertThat(bpmnPlan.getBpmnProcessElement(), is(notNullValue()));
    }

}
