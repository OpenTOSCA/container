package org.opentosca.planbuilder.postphase.plugin.instancedata.bpmn;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all logic to append BPMN code regarding instances.
 */
public class Handler {

    private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
    private BPMNSubprocessHandler bpmnSubprocessHandler;

    public Handler() {
        this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
    }

    /**
     * Appends BPMN Code that creates first an instance then handles the node operation that updates InstanceData for
     * the given NodeTemplate. Needs initialization code on the global level in the plan. This will be checked and
     * appended if needed.
     *
     * @param context      the TemplateContext of the NodeTemplate
     * @param nodeTemplate the NodeTemplate to handle
     * @return true iff appending all BPMN code was successful
     */
    public boolean handleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        BPMNSubprocess subprocess = context.getSubprocessElement();
        String idPrefix = BPMNSubprocessType.SUBPROCESS.toString();
        final BPMNSubprocess createNodeInstanceTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.CREATE_NODE_INSTANCE_TASK);
        createNodeInstanceTask.setResultVariableName(idPrefix + nodeTemplate.getId());
        subprocess.addTaskToSubprocess(createNodeInstanceTask);
        return true;
    }

    /**
     * Appends BPMN Code that creates a relationship instance. Currently, each instance is set to CREATED.
     *
     * @param context              the TemplateContext of the NodeTemplate
     * @param relationshipTemplate the RelationshipTemplate to handle
     */
    public boolean handleCreate(final BPMNPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        LOG.debug("Handling of relationship template with id {}", relationshipTemplate.getId());
        BPMNSubprocess subprocess = context.getSubprocessElement();
        // corresponding data object id
        String subprocessDataObjectId = subprocess.getId().replace("Subprocess", "DataObject");

        BPMNPlan buildPlan = subprocess.getBuildPlan();
        final BPMNSubprocess createRelationshipInstanceTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.CREATE_RT_INSTANCE);
        for (BPMNDataObject dataObject : buildPlan.getDataObjectsList()) {
            if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_REL && dataObject.getId().equals(subprocessDataObjectId)) {
                String sourceInstanceURL = dataObject.getSourceInstanceURL();
                String targetInstanceURL = dataObject.getTargetInstanceURL();
                createRelationshipInstanceTask.setSourceInstanceURL(sourceInstanceURL);
                createRelationshipInstanceTask.setTargetInstanceURL(targetInstanceURL);
                createRelationshipInstanceTask.setResultVariableName(subprocess.getId());
            }
        }

        subprocess.addTaskToSubprocess(createRelationshipInstanceTask);
        // This might not be true for every relationship template
        String state = "CREATED";
        final BPMNSubprocess setState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
        setState.setInstanceState(state);
        subprocess.addTaskToSubprocess(setState);
        return createRelationshipInstanceTask != null;
    }
}
