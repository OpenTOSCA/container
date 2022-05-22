package org.opentosca.planbuilder.postphase.plugin.setproperties.bpmn;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMNSetPropertiesHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNSetPropertiesHandler.class);
    private BPMNProcessFragments bpmnProcessFragments;
    private BPMNSubprocessHandler bpmnSubprocessHandler;

    public BPMNSetPropertiesHandler() {
        try {
            this.bpmnProcessFragments = new BPMNProcessFragments();
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean handleCreate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        BPMNSubprocess subprocess = context.getSubprocessElement();
        final BPMNSubprocess createSetStateTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.SET_NODE_PROPERTY_TASK);
        //subprocess.addScopeToSubprocess(createSetStateTask);
        return true;
    }

    public boolean handleCreate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }
}
