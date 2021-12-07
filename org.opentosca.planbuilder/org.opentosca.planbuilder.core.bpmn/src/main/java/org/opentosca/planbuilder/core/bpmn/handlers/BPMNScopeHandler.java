package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is part of the Facade to handle actions on BuildPlans. This particular class handle XML related operations
 * on TemplateBuildPlans
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNScopeHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNScopeHandler.class);

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

}
