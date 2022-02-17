package org.opentosca.planbuilder.core.bpmn.Handler;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNScopeHandler;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BPMNScopeHandlerTests {

    final static Logger logger = LoggerFactory.getLogger(BPMNScopeHandlerTests.class);
    BPMNPlan bpmnPlan;
    BPMNScopeHandler bpmnScopeHandler;

    @Before
    public void init() throws ParserConfigurationException {
        bpmnPlan = new BPMNPlan("tid", PlanType.BUILD, null, null, null, null);
        bpmnScopeHandler = new BPMNScopeHandler();
    }

    @Test
    public void testCreateStartEventNonNullAndType() {
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(bpmnPlan);
        assertThat(startEvent, is(notNullValue()));
        assertThat(startEvent.getBpmnScopeType(), is(BPMNScopeType.START_EVENT));
    }

    @Test
    public void testCreateEndEventNonNullAndType() {
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(bpmnPlan);
        assertThat(endEvent, is(notNullValue()));
        assertThat(endEvent.getBpmnScopeType(), is(BPMNScopeType.END_EVENT));
    }

    @Test
    public void testCreateStartEventPlanIdSet() {
        int createdId = bpmnPlan.getInternalCounterId();
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(bpmnPlan);
        assertThat(startEvent.getBuildPlan(), is(bpmnPlan));
        int index = startEvent.getId().indexOf("_");
        assertThat(Integer.parseInt(startEvent.getId().substring(index + 1)), is(createdId));
    }

    @Test
    public void testCreateSequenceFlowLinked() {
        BPMNScope startEvent = bpmnScopeHandler.createStartEvent(bpmnPlan);
        BPMNScope endEvent = bpmnScopeHandler.createEndEvent(bpmnPlan);
        BPMNScope sf = bpmnScopeHandler.createSequenceFlow(startEvent, endEvent, bpmnPlan);
        assertThat(sf.containsIncomingScope(startEvent), is(true));
        assertThat(sf.containsOutgoingScope(endEvent), is(true));
        assertThat(startEvent.containsOutgoingScope(sf), is(true));
        assertThat(startEvent.getNumIncomingLinks(), is(0));
        assertThat(endEvent.containsIncomingScope(sf), is(true));
        assertThat(endEvent.getNumOutgoingLinks(), is(0));
        assertThat(sf.getNumIncomingLinks(), is(1));
        assertThat(sf.getNumOutgoingLinks(), is(1));
    }

    @Test
    public void testCreateServiceTemplateIdSet() {
        int createdId = bpmnPlan.getInternalCounterId();
        BPMNScope st = bpmnScopeHandler.createServiceTemplateInstanceTask(bpmnPlan);
        assertThat(st.getBpmnScopeType(), is(BPMNScopeType.CREATE_ST_INSTANCE));
        assertThat(Integer.parseInt(st.getId().substring(st.getId().length() - 1)), is(createdId));
    }

    @Test
    public void testCreateRelationshipTemplateIdSet() {
        int createdId = bpmnPlan.getInternalCounterId();
        AbstractActivity activity = new RelationshipTemplateActivity("" + createdId, ActivityType.PROVISIONING, null);
        BPMNScope rt = bpmnScopeHandler.createTemplateBuildPlan(activity, bpmnPlan);
        assertThat(rt.getBpmnScopeType(), is(BPMNScopeType.CREATE_RT_INSTANCE));
        assertThat(Integer.parseInt(rt.getId().substring(rt.getId().length() - 1)), is(createdId));

    }

    @Test
    public void testCreateSubprocessFromNodeTemplate() {
        int createdId = bpmnPlan.getInternalCounterId();
        AbstractActivity activity = new NodeTemplateActivity("" + createdId, ActivityType.PROVISIONING, null);
        BPMNScope subprocess = bpmnScopeHandler.createTemplateBuildPlan(activity, bpmnPlan);
        assertThat(subprocess.getBpmnScopeType(), is(BPMNScopeType.SUBPROCESS));
    }

    @Test
    public void testCreateStartEventFromSubprocess() {
        int createdId = bpmnPlan.getInternalCounterId();
        AbstractActivity activity = new NodeTemplateActivity("" + createdId, ActivityType.PROVISIONING, null);
        BPMNScope subprocess = bpmnScopeHandler.createTemplateBuildPlan(activity, bpmnPlan);
        BPMNScope startEvent = bpmnScopeHandler.createStartEventSubprocess(bpmnPlan, subprocess);
        assertThat(startEvent.getParentProcess(), is(subprocess));
        assertThat(subprocess.getSubStartEvent(), is(startEvent));
    }

    @Test
    public void testCreateEndEventFromSubprocess() {
        int createdId = bpmnPlan.getInternalCounterId();
        AbstractActivity activity = new NodeTemplateActivity("" + createdId, ActivityType.PROVISIONING, null);
        BPMNScope subprocess = bpmnScopeHandler.createTemplateBuildPlan(activity, bpmnPlan);
        BPMNScope endEvent = bpmnScopeHandler.createStartEventSubprocess(bpmnPlan, subprocess);
        assertThat(endEvent.getParentProcess(), is(subprocess));
        assertThat(subprocess.getSubStartEvent(), is(endEvent));
    }
}
