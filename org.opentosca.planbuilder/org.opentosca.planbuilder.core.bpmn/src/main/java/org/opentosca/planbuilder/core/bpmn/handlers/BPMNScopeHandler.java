package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is part of the Facade to handle actions on BPMN process components.
 * This particular class handle XML related operations
 * on TemplateBuildPlans
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNScopeHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNScopeHandler.class);
    private final BPMNProcessFragments fragments;

    public BPMNScopeHandler() throws ParserConfigurationException {
        this.fragments = new BPMNProcessFragments();
    }

    /**
     * Removes all ChildNodes of he given DOM Node
     *
     * @param node the DOM Node to remove its child elements
     */
    private static void removeAllChildNodes(final Node node) {
        final NodeList children = node.getChildNodes();
        while (children.getLength() > 0) {
            final Node child = children.item(0);
            child.getParentNode().removeChild(child);
        }
    }

    public BPMNScope createStartEvent(final BPMNPlan buildPlan) {
        String idPrefix = BPMNScopeType.EVENT.toString();
        final BPMNScope startEvent = new BPMNScope(BPMNScopeType.START_EVENT, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());

        // collecting BPMNScope for refinement in next stage
        startEvent.setBuildPlan(buildPlan);
        buildPlan.addTemplateBuildPlan(startEvent);
        buildPlan.setBpmnStartEventElement(startEvent);
        return startEvent;
    }

    public BPMNScope createEndEvent(final BPMNPlan buildPlan) {
        String idPrefix = BPMNScopeType.EVENT.name();
        final BPMNScope endEvent = new BPMNScope(BPMNScopeType.END_EVENT, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());

        // collecting BPMNScope for refinement in next stage
        endEvent.setBuildPlan(buildPlan);
        buildPlan.addTemplateBuildPlan(endEvent);
        return endEvent;
    }

    public BPMNScope createTemplateBuildPlan(final AbstractActivity activity, final  BPMNPlan buildPlan) {
        LOG.debug("Create template build plan with Abstract activity: {} type: {}", activity.getId(), activity.getType());
        // reuse activity id with prefix
        String idPrefix = "";
        final BPMNScope templateBuildPlan;
        if (activity instanceof NodeTemplateActivity) {
            idPrefix = BPMNScopeType.SUBPROCESS.toString();
            templateBuildPlan = new BPMNScope(activity, BPMNScopeType.SUBPROCESS, idPrefix + "_" + activity.getId());
        } else if (activity instanceof RelationshipTemplateActivity) {
            idPrefix = BPMNScopeType.CREATE_RT_INSTANCE.toString();
            templateBuildPlan = new BPMNScope(activity, BPMNScopeType.CREATE_RT_INSTANCE, idPrefix + "_" + activity.getId());
        } else {
            templateBuildPlan = null;
        }

        // collecting BPMNScope for refinement in next stage
        templateBuildPlan.setBuildPlan(buildPlan);
        buildPlan.addTemplateBuildPlan(templateBuildPlan);
        return templateBuildPlan;
    }


    public BPMNScope createSequenceFlow(BPMNScope src, BPMNScope trg, final BPMNPlan buildPlan) {
        String idPrefix = BPMNScopeType.SEQUENCE_FLOW.name();
        // create new id
        final BPMNScope flow = new BPMNScope(BPMNScopeType.SEQUENCE_FLOW, idPrefix + "_" + buildPlan.getIdForNamesAndIncrement());

        // src ->  flow -> trg
        src.addOutgoingScope(flow);
        trg.addIncomingScope(flow);
        flow.addIncomingScope(src);
        flow.addOutgoingScope(trg);

        // collecting all BPMNScope for refinement in next stage
        flow.setBuildPlan(buildPlan);
        buildPlan.addTemplateBuildPlan(flow);
        return flow;
    }

    public BPMNScope createTemplateBuildPlan(final AbstractActivity activity, final BPMNPlan buildPlan,
                                             String namePrefix) {
        final BPMNScope newTemplateBuildPlan = new BPMNScope(activity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
        //this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
          //      + activity.getId()) + "_" + activity.getType(),
           // newTemplateBuildPlan);
        return newTemplateBuildPlan;
    }

    public BPMNScope createTemplateBuildPlan(final NodeTemplateActivity nodeTemplateActivity, final BPMNPlan buildPlan,
                                             String namePrefix) {
        final BPMNScope newTemplateBuildPlan = new BPMNScope(nodeTemplateActivity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
       // this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
         //       + nodeTemplateActivity.getNodeTemplate().getId()) + "_" + nodeTemplateActivity.getType(),
           // newTemplateBuildPlan);
        //newTemplateBuildPlan.setNodeTemplate(nodeTemplateActivity.getNodeTemplate());
        return newTemplateBuildPlan;
    }

    public BPMNScope createTemplateBuildPlan(final RelationshipTemplateActivity relationshipTemplateActivity,
                                             final BPMNPlan buildPlan, String namePrefix) {

        final BPMNScope newTemplateBuildPlan = new BPMNScope(relationshipTemplateActivity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
        // this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
         //   + relationshipTemplateActivity.getRelationshipTemplate().getId()) + "_"
           // + relationshipTemplateActivity.getType(), newTemplateBuildPlan);
        newTemplateBuildPlan.setRelationshipTemplate(relationshipTemplateActivity.getRelationshipTemplate());
        return newTemplateBuildPlan;
    }

    /**
     * Connects two TemplateBuildPlans (which are basically bpel scopes) with the given link
     *
     * @param source   the TemplateBuildPlan which should be a source of the link
     * @param target   the TemplateBuildPlan which should be a target of the link
     * @param linkName the name of the link used to connect the two templates
     * @return true if connections between templates was sucessfully created, else false
     */
    /*
    public boolean connect(final BPMNScope source, final BPMNScope target, final String linkName) {
        BPMNScopeHandler.LOG.debug("Trying to connect TemplateBuildPlan {} as source with TemplateBuildPlan {} as target",
            source.getBpelScopeElement().getAttribute("name"),
            target.getBpelScopeElement().getAttribute("name"));
        boolean check = true;
        // if everything was successfully added return true
        check &= this.addSource(linkName, source);
        check &= this.addTarget(linkName, target);
        return check;
    }
    */
    /**
     * Returns the names of the links declared in the sources element of the given TemplateBuildPlan
     *
     * @param template the TemplateBuildPlan to fetch the names from
     * @return a List of String representing Links inside the sources element of the given TemplateBuildPlan
     */
    public List<String> getLinksInSources(final BPELScope template) {
        final List<String> sourcesLinkNames = new ArrayList<>();
        if (template.getBpelSourcesElement() == null) {
            return sourcesLinkNames;
        }
        if (template.getBpelSourcesElement().hasChildNodes()) {
            final NodeList children = template.getBpelSourcesElement().getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final NamedNodeMap attrs = children.item(i).getAttributes();
                if (attrs.getNamedItem("linkName") != null) {
                    sourcesLinkNames.add(attrs.getNamedItem("linkName").getNodeValue());
                }
            }
        }
        return sourcesLinkNames;
    }

    /**
     * Returns the names of the links declared in the targets element of the given TemplateBuildPlan
     *
     * @param template the TemplateBuildPlan to fetch the names from
     * @return a List of Strings representing Links inside the targets element of the given TemplateBuildPlan
     */
    public List<String> getLinksInTarget(final BPELScope template) {
        final List<String> targetsLinkNames = new ArrayList<>();
        if (template.getBpelTargetsElement() == null) {
            return targetsLinkNames;
        }
        if (template.getBpelTargetsElement().hasChildNodes()) {
            for (int i = 0; i < template.getBpelTargetsElement().getChildNodes().getLength(); i++) {
                if (template.getBpelTargetsElement().getChildNodes().item(i).hasAttributes()) {
                    final NamedNodeMap attrs = template.getBpelTargetsElement().getChildNodes().item(i).getAttributes();
                    if (attrs.getNamedItem("linkName") != null) {
                        targetsLinkNames.add(attrs.getNamedItem("linkName").getNodeValue());
                    }
                }
            }
        }
        return targetsLinkNames;
    }

    /**
     * Returns a valid NCName string
     *
     * @param string a String to convert to valid NCName
     * @return a String which can be used as NCName
     */
    public String getNCNameFromString(final String string) {
        // TODO check if this enough ;)
        return string.replace(" ", "_");
    }

    /**
     * Returns all Successors of the given TemplateBuildPlan
     *
     * @param templatePlan the TemplateBuildPlan whose Successors should be returned
     * @return a List of TemplateBuildPlans that are Successors of the given TemplateBuildPlan
     */
    public List<BPELScope> getSuccessors(final BPELScope templatePlan) {
        final List<BPELScope> successors = new ArrayList<>();

        final List<String> linkNamesInSources = this.getLinksInSources(templatePlan);

        for (final String linkAsSource : linkNamesInSources) {
            for (final BPELScope template : templatePlan.getBuildPlan().getTemplateBuildPlans()) {
                final List<String> linkNamesInTargets = this.getLinksInTarget(template);
                if (linkNamesInTargets.contains(linkAsSource)) {
                    successors.add(template);
                }
            }
        }

        return successors;
    }

    /**
     * Checks whether the given TemplateBuildPlan has the given correlationSet
     *
     * @param correlationSetName the name of the correlationSet to check for
     * @param templateBuildPlan  the TemplateBuildPlan to check on
     * @return true if the correlationSet is declared in the TemplateBuildPlan
     */
    private boolean hasCorrelationSet(final String correlationSetName, final BPELScope templateBuildPlan) {
        return ModelUtils.hasChildElementWithAttribute(templateBuildPlan.getBpelCorrelationSets(), "name",
            correlationSetName);
    }

    /**
     * Checks whether the given partnerLinkName is already used in the given TemplateBuildPlan
     *
     * @param name              the name of the partnerLink to check with
     * @param templateBuildPlan the TemplateBuildPlan to check on
     * @return true if the given TemplateBuildPlan already has a partnerLink with the given name, else false
     */
    private boolean hasPartnerlink(final String name, final BPELScope templateBuildPlan) {
        return ModelUtils.hasChildElementWithAttribute(templateBuildPlan.getBpelPartnerLinksElement(), "name", name);
    }

    /**
     * Checks whether the given link is a source in the given TemplateBuildPlan
     *
     * @param name              the name of the link to check with
     * @param templateBuildPlan the TemplateBuildPlan to check on
     * @return true if the given link is declared as source in the given TemplateBuildPlan
     */
    public boolean hasSource(final String name, final BPELScope templateBuildPlan) {
        return ModelUtils.hasChildElementWithAttribute(templateBuildPlan.getBpelSourcesElement(), "linkName", name);
    }

    /**
     * Checks whether the given link is a target in the given TemplateBuildPlan
     *
     * @param name              the name of the link to check with
     * @param templateBuildPlan the TemplateBuildPlan to check on
     * @return true if the given link is declared as target in the given TemplateBuildPlan
     */
    public boolean hasTarget(final String name, final BPELScope templateBuildPlan) {
        return ModelUtils.hasChildElementWithAttribute(templateBuildPlan.getBpelTargetsElement(), "linkName", name);
    }

    /**
     * Checks whether the given variableName is already used in the given TemplateBuildPlan
     *
     * @param name              the name of the variable to check
     * @param templateBuildPlan the TemplateBuildPlan to check on
     * @return true if the variableName is already used, else false
     */
    private boolean hasVariable(final String name, final BPELScope templateBuildPlan) {
        return ModelUtils.hasChildElementWithAttribute(templateBuildPlan.getBpelVariablesElement(), "name", name);
    }

    /**
     * Initializes XML Elements of the given TemplateBuildPlan and connects it to the given BuildPlan
     *
     * @param newTemplateBuildPlan the TemplateBuildPlan to initialize
     * @param buildPlan            the BuildPlan to connect to TemplateBuildPlan to
     */
    public void initializeXMLElements(final BPMNScope newTemplateBuildPlan, final BPMNPlan buildPlan) {

    }

    /**
     * Removes all links which use the given TemplateBuildPlan as source
     *
     * @param template the TemplateBuildPlan for that all source relations have to be removed
     */
    public void removeSources(final BPELScope template) {
        final Element sources = template.getBpelSourcesElement();

        if (sources != null) {
            BPMNScopeHandler.removeAllChildNodes(sources);
        }
    }

    /**
     * Removes all links which use the given TemplateBuildPlan as source
     *
     * @param template the TemplateBuildPlan for that all target relations have to be removed
     */
    public void removeTargets(final BPELScope template) {
        final Element targets = template.getBpelTargetsElement();
        if (targets != null) {
            BPMNScopeHandler.removeAllChildNodes(targets);
        }
    }

    public BPMNScope createServiceTemplateInstanceTask(BPMNPlan plan) {
        String idPrefix = BPMNScopeType.CREATE_ST_INSTANCE.name();
        BPMNScope task = new BPMNScope(BPMNScopeType.CREATE_ST_INSTANCE, idPrefix + "_" + plan.getIdForNamesAndIncrement());

        plan.addTemplateBuildPlan(task);
        task.setBuildPlan(plan);
        return task;
    }

    public BPMNScope createSetServiceTemplateStateTask(BPMNPlan plan) {
        String idPrefix = BPMNScopeType.SET_ST_STATE.name();
        BPMNScope task = new BPMNScope(BPMNScopeType.SET_ST_STATE, idPrefix + "_" + plan.getIdForNamesAndIncrement());

        plan.addTemplateBuildPlan(task);
        task.setBuildPlan(plan);
        return task;
    }
}
