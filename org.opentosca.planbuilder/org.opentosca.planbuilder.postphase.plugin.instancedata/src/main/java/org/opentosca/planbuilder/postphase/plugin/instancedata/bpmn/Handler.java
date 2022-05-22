package org.opentosca.planbuilder.postphase.plugin.instancedata.bpmn;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class contains all logic to append BPMN code regarding instances.
 */
public class Handler {

    private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private Fragments fragments;
    private BPMNProcessFragments bpmnProcessFragments;
    private BPMNInvokerPlugin invoker;
    private BPMNSubprocessHandler bpmnSubprocessHandler;

    public Handler() {
        try {
            this.fragments = new Fragments();
            this.bpmnProcessFragments = new BPMNProcessFragments();
            this.invoker = new BPMNInvokerPlugin();
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void appendStateUpdateToPostPhase(BPMNPlanContext context, String nodeRelationInstanceURLVarName,
                                              String stateVarName, String stateToSet) {
        //this.appendStateUpdateAsChild(context, nodeRelationInstanceURLVarName, stateVarName, stateToSet, context.getPostPhaseElement());
    }

    private void appendStateUpdateToPrePhase(BPMNPlanContext context, String nodeInstanceURLVarName,
                                             String stateVarName, String stateToSet) {
        //this.appendStateUpdateAsChild(context, nodeInstanceURLVarName, stateVarName, stateToSet, context.getPrePhaseElement());
    }

    private void appendStateUpdateAsChild(BPMNPlanContext context, String nodeRelationInstanceURLVarName,
                                          String stateVarName, String stateToSet, Element parentElement) {
        try {
            // update state variable to uninstalled
            final BPELProcessFragments frag = new BPELProcessFragments();
            Node assignNode =
                frag.createAssignXpathQueryToStringVarFragmentAsNode("assignSetNodeState" + System.currentTimeMillis(),
                    "string('" + stateToSet + "')", stateVarName);
            assignNode = context.importNode(assignNode);
            parentElement.appendChild(assignNode);

            // send state to api
            final String bpelString =
                this.fragments.generateBPEL4RESTLightPUTInstanceState(nodeRelationInstanceURLVarName, stateVarName);
            Node extActiv = ModelUtils.string2dom(bpelString);
            extActiv = context.importNode(extActiv);
            parentElement.appendChild(extActiv);
        } catch (final IOException e2) {
            e2.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends BPMN Code that creates first a instance then handels the node oper that updates InstanceData for the
     * given NodeTemplate. Needs initialization code on the global level in the plan. This will be checked and appended
     * if needed.
     *
     * @param context      the TemplateContext of the NodeTemplate
     * @param nodeTemplate the NodeTemplate to handle
     * @return true iff appending all BPMN code was successful
     */
    public boolean handleCreate(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        BPMNSubprocess subprocess = context.getSubprocessElement();
        BPMNPlan buildPlan = ((BPMNSubprocess) subprocess).getBuildPlan();
        String idPrefix = BPMNSubprocessType.SUBPROCESS.toString();
        final BPMNSubprocess createNodeInstanceTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.CREATE_NODE_INSTANCE_TASK);
        createNodeInstanceTask.setResultVariableName(idPrefix + nodeTemplate.getId());
        subprocess.addTaskToSubproces(createNodeInstanceTask);
        return createNodeInstanceTask != null;
    }

    public boolean handleCreate(final BPMNPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        LOG.info("Handling of relationshiptemplate with id {}", relationshipTemplate.getId());
        BPMNSubprocess subprocess = context.getSubprocessElement();
        // corresponding data object id
        String subprocessDataobjectId = subprocess.getId().replace("Subprocess", "");

        String dataObjectId = BPMNSubprocessType.DATA_OBJECT + subprocessDataobjectId;
        LOG.info(dataObjectId);
        BPMNPlan buildPlan = ((BPMNSubprocess) subprocess).getBuildPlan();
        final BPMNSubprocess createRelationshipInstanceTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.CREATE_RT_INSTANCE);
        for (BPMNDataObject dataObject : buildPlan.getDataObjectsList()) {
            if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_REL && dataObject.getId().equals(dataObjectId)) {
                String sourceInstanceURL = dataObject.getSourceInstanceURL();
                String targetInstanceURL = dataObject.getTargetInstanceURL();
                createRelationshipInstanceTask.setSourceInstanceURL(sourceInstanceURL);
                createRelationshipInstanceTask.setTargetInstanceURL(targetInstanceURL);
                createRelationshipInstanceTask.setResultVariableName(subprocess.getId());
            }
        }

/**
 try {
 Node childCreateRelationshipInstance = this.bpmnProcessFragments.createRelationshipTemplateInstance(createRelationshipInstanceTask);
 NodeList subprocesses = context.getTemplateBuildPlan().getBuildPlan().getBpmnDocument().getElementsByTagName("bpmn:subProcess");
 for (int i = 0; i < subprocesses.getLength(); i++) {
 Node element = subprocesses.item(i);
 for (int j = 0; j < element.getAttributes().getLength(); j++) {
 String id = element.getAttributes().item(j).getTextContent();
 if (id.equals(subprocess.getId())) {
 LOG.info("ICH GEH HIER REIN");
 Node parent2 = subprocesses.item(i);
 //parent2.appendChild(childCreateNodeInstance);
 bpmnProcessFragments.addNodeInsideSubprocess(childCreateRelationshipInstance, parent2, buildPlan);
 }
 }
 }
 } catch (IOException e) {
 e.printStackTrace();
 } catch (SAXException e) {
 e.printStackTrace();
 }
 */
        subprocess.addTaskToSubproces(createRelationshipInstanceTask);
        // This might not be true for every relationship template
        String state = "CREATED";
        final BPMNSubprocess setState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
        setState.setInstanceState(state);
        subprocess.addTaskToSubproces(setState);
        return createRelationshipInstanceTask != null;
    }
}
