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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BPMNPlanHandlerTests {

    final static Logger logger = LoggerFactory.getLogger(BPMNPlanHandlerTests.class);
    final static String[][] NS_PAIRS = {
        {"xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"},
        {"xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"},
        {"xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC"},
        {"xmlns:camunda", "http://camunda.org/schema/1.0/bpmn"},
        {"xmlns:di", "http://www.omg.org/spec/DD/20100524/DI"},
        {"xmlns:qa", "http://some-company/schema/bpmn/qa"},
    };
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
    public void testInitializeXMLElementsShouldContainDocument() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        assertThat(bpmnPlan.getBpmnDocument(), is(notNullValue()));
    }

    @Test
    public void testInitializeXMLElementsShouldContainDefinitionElement() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        Element element = bpmnPlan.getBpmnDefinitionElement();
        assertThat(element, is(notNullValue()));
        assertThat(element.hasAttribute("id"), is(true));

    }

    @Test
    public void testInitializeXMLElementsShouldContainProcessElement() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        Element element = bpmnPlan.getBpmnProcessElement();
        assertThat(element, is(notNullValue()));
        assertThat(element.hasAttribute("id"), is(true));
    }

    @Test
    public void testInitializeXMLElementsShouldContainDiagramElement() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        Element element = bpmnPlan.getBpmnDiagramElement();
        assertThat(element, is(notNullValue()));
        assertThat(element.hasAttribute("id"), is(true));
    }

    @Test
    public void testInitializeXMLElementsShouldContainPlaneElement() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        Element element = bpmnPlan.getBpmnPlaneElement();
        assertThat(element, is(notNullValue()));
        assertThat(element.hasAttribute("id"), is(true));
    }

    @Test
    public void testInitializeXMLElementsNSDefined() {
        bpmnPlanHandler.initializeXMLElements(bpmnPlan);
        Element defElement = bpmnPlan.getBpmnDefinitionElement();
        for (String[] p : NS_PAIRS) {
            assertThat(defElement.hasAttribute(p[0]), is(true));
            assertThat(defElement.getAttribute(p[0]), is(p[1]));
        }
    }

    @Test
    public void testGenerateBPMNDiagramShouldHaveStartEndEvent() {

    }

    @Test
    public void testBPMNPlanInterfaceOperationSet() {
        String inter = "TOSCAInterface";
        String ops = "TOSCAOperation";
        bpmnPlan.setTOSCAInterfaceName(inter);
        assertThat(bpmnPlan.getTOSCAInterfaceName(), is(inter));
        bpmnPlan.setTOSCAOperationName(ops);
        assertThat(bpmnPlan.getTOSCAOperationName(), is(ops));
    }

}
