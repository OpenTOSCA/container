package org.opentosca.planbuilder.core.bpel.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.utils.ModelUtils;
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
public class BPELScopeHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPELScopeHandler.class);

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

    /**
     * Adds the given correlation to the given TemplateBuildPlan
     *
     * @param correlationSetName the name of the correlationSet
     * @param propertyName       the property the correlationSet works on
     * @param templateBuildPlan  the TemplateBuildPlan to add the correlationSet to
     * @return true if adding CorrelationSet was successful, else false
     */
    public boolean addCorrelationSet(final String correlationSetName, final String propertyName,
                                     final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Trying to add correlationSet {} with property {} to templateBuildPlan {}",
            correlationSetName, propertyName,
            templateBuildPlan.getBpelScopeElement().getAttribute("name"));
        if (this.hasCorrelationSet(correlationSetName, templateBuildPlan)) {
            BPELScopeHandler.LOG.warn("Failed adding correlationSet");
            return false;
        }
        final Element correlationSetsElement = templateBuildPlan.getBpelCorrelationSets();
        final Element correlationSetElement =
            templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "correlationSet");
        correlationSetElement.setAttribute("name", correlationSetName);
        correlationSetElement.setAttribute("properties", "tns:" + propertyName);
        correlationSetsElement.appendChild(correlationSetElement);
        BPELScopeHandler.LOG.debug("Adding correlationSet was succesful");
        return true;
    }

    /**
     * Adds a partnerLink to the given TemplateBuildPlan
     *
     * @param partnerLinkName       the name of the partnerLink
     * @param partnerLinkType       the partnerLinkType which must be already set in the BuildPlan of the
     *                              TemplateBuildPlan
     * @param myRole                the 1st role of this partnerLink
     * @param partnerRole           the 2nd role this partnerLink
     * @param initializePartnerRole whether to initialize the partnerRole
     * @param templateBuildPlan     the TemplateBuildPlan to add the partnerLink to
     * @return true if setting partnerLink was successful, else false
     */
    public boolean addPartnerLink(final String partnerLinkName, final QName partnerLinkType, final String myRole,
                                  final String partnerRole, final boolean initializePartnerRole,
                                  final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Trying to add partnerLink {} with partnerLinkType {}, myRole {}, partnerRole {} and initializePartnerRole {} on TemplateBuildPlan {}",
            partnerLinkName, partnerLinkType.toString(), myRole, partnerRole,
            String.valueOf(initializePartnerRole),
            templateBuildPlan.getBpelScopeElement().getAttribute("name"));
        if (this.hasPartnerlink(partnerLinkName, templateBuildPlan)) {
            BPELScopeHandler.LOG.warn("Failed to add partnerLink");
            return false;
        }
        final Element partnerLinksElement = templateBuildPlan.getBpelPartnerLinksElement();
        final Element partnerLinkElement =
            templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "partnerLink");

        partnerLinkElement.setAttribute("name", partnerLinkName);
        partnerLinkElement.setAttribute("partnerLinkType",
            partnerLinkType.getPrefix() + ":" + partnerLinkType.getLocalPart());
        if (myRole != null) {
            partnerLinkElement.setAttribute("myRole", myRole);
        }
        if (partnerRole != null && !partnerRole.equals("")) {
            partnerLinkElement.setAttribute("partnerRole", partnerRole);
        }

        partnerLinkElement.setAttribute("initializePartnerRole", initializePartnerRole ? "yes" : "no");

        partnerLinksElement.appendChild(partnerLinkElement);
        BPELScopeHandler.LOG.debug("Adding partnerLink was successful");
        return true;
    }

    /**
     * Adds a link as a Source to the given TemplateBuildPlan
     *
     * @param linkName          the name of the link to use
     * @param templateBuildPlan the TemplateBuildPlan to add the Link to
     * @return true if adding the Link was successful, else false
     */
    public boolean addSource(final String linkName, final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Trying to add link {} as source to TemplateBuildPlan {}", linkName,
            templateBuildPlan.getBpelScopeElement().getAttribute("name"));
        if (this.hasSource(linkName, templateBuildPlan)) {
            BPELScopeHandler.LOG.warn("Failed to add link as source");
            return false;
        }
        final Element sourcesElement = templateBuildPlan.getBpelSourcesElement();
        final Element sourceElement =
            templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "source");
        sourceElement.setAttribute("linkName", linkName);
        sourcesElement.appendChild(sourceElement);
        BPELScopeHandler.LOG.debug("Adding link as source was successful");
        return true;
    }

    /**
     * Adds link as a target to the given TemplateBuildPlan
     *
     * @param linkName          the name of the link
     * @param templateBuildPlan the TemplateBuildPlan to add the link to
     * @return true if adding link was successful, else false
     */
    public boolean addTarget(final String linkName, final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Trying to add link {} as target to TemplateBuildPlan {}", linkName,
            templateBuildPlan.getBpelScopeElement().getAttribute("name"));
        if (this.hasTarget(linkName, templateBuildPlan)) {
            BPELScopeHandler.LOG.warn("Failed adding link as target");
            return false;
        }
        final Element targetsElement = templateBuildPlan.getBpelTargetsElement();
        final Element targetElement =
            templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "target");
        targetElement.setAttribute("linkName", linkName);
        targetsElement.appendChild(targetElement);
        BPELScopeHandler.LOG.debug("Adding link as target was successful");
        return true;
    }

    /**
     * Adds a variable to the given TemplateBuildPlan
     *
     * @param name              the name of the variable
     * @param variableType      the type of the variable
     * @param declarationId     the QName of the XML complexType of the variable
     * @param templateBuildPlan the TemplateBuildPlan to add the variable to
     * @return true if adding variable was successful, else false
     */
    public boolean addVariable(final String name, final BPELPlan.VariableType variableType, final QName declarationId,
                               final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Trying to add variable {} with of type {} and XML Schematype {} to TemplateBuildPlan {}",
            name, variableType, declarationId.toString(),
            templateBuildPlan.getBpelScopeElement().getAttribute("name"));
        if (this.hasVariable(name, templateBuildPlan)) {
            BPELScopeHandler.LOG.warn("Failed adding variable");
            return false;
        }

        // fetch variables element and create variable element
        final Element variablesElement = templateBuildPlan.getBpelVariablesElement();
        final Element variableElement =
            templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "variable");

        // set the type and declaration id
        switch (variableType) {
            case MESSAGE:
                variableElement.setAttribute("messageType",
                    declarationId.getPrefix() + ":" + declarationId.getLocalPart());
                break;
            case TYPE:
                variableElement.setAttribute("type", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
                break;
            default:
                break;
        }

        // set name
        variableElement.setAttribute("name", name);

        // append to variables element
        variablesElement.appendChild(variableElement);
        BPELScopeHandler.LOG.debug("Adding variable was successful");
        return true;
    }

    /**
     * Connects two TemplateBuildPlans (which are basically bpel scopes) with the given link
     *
     * @param source   the TemplateBuildPlan which should be a source of the link
     * @param target   the TemplateBuildPlan which should be a target of the link
     * @param linkName the name of the link used to connect the two templates
     * @return true if connections between templates was sucessfully created, else false
     */
    public boolean connect(final BPELScope source, final BPELScope target, final String linkName) {
        BPELScopeHandler.LOG.debug("Trying to connect TemplateBuildPlan {} as source with TemplateBuildPlan {} as target",
            source.getBpelScopeElement().getAttribute("name"),
            target.getBpelScopeElement().getAttribute("name"));
        boolean check = true;
        // if everything was successfully added return true
        check &= this.addSource(linkName, source);
        check &= this.addTarget(linkName, target);
        return check;
    }

    public BPELScope createTemplateBuildPlan(final AbstractActivity activity, final BPELPlan buildPlan,
                                             String namePrefix) {
        final BPELScope newTemplateBuildPlan = new BPELScope(activity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
        this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
                + activity.getId()) + "_" + activity.getType(),
            newTemplateBuildPlan);
        return newTemplateBuildPlan;
    }

    public BPELScope createTemplateBuildPlan(final NodeTemplateActivity nodeTemplateActivity, final BPELPlan buildPlan,
                                             String namePrefix) {
        final BPELScope newTemplateBuildPlan = new BPELScope(nodeTemplateActivity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
        this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
                + nodeTemplateActivity.getNodeTemplate().getId()) + "_" + nodeTemplateActivity.getType(),
            newTemplateBuildPlan);
        newTemplateBuildPlan.setNodeTemplate(nodeTemplateActivity.getNodeTemplate());
        return newTemplateBuildPlan;
    }

    public BPELScope createTemplateBuildPlan(final RelationshipTemplateActivity relationshipTemplateActivity,
                                             final BPELPlan buildPlan, String namePrefix) {

        final BPELScope newTemplateBuildPlan = new BPELScope(relationshipTemplateActivity);
        this.initializeXMLElements(newTemplateBuildPlan, buildPlan);
        this.setName(this.getNCNameFromString(((namePrefix == null || namePrefix.isEmpty()) ? "" : namePrefix + "_")
            + relationshipTemplateActivity.getRelationshipTemplate().getId()) + "_"
            + relationshipTemplateActivity.getType(), newTemplateBuildPlan);
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
     * Returns the predecessors of the given TemplateBuildPlan
     *
     * @param templatePlan the TemplateBuildPlan to get predecessors from
     * @return a List of TemplateBuildPlans that are predecessors of the given TemplateBuildPlan
     */
    public List<BPELScope> getPredecessors(final BPELScope templatePlan) {
        final List<BPELScope> preds = new ArrayList<>();
        final List<String> linkNamesInTargets = this.getLinksInTarget(templatePlan);

        for (final String linkAsTarget : linkNamesInTargets) {
            for (final BPELScope template : templatePlan.getBuildPlan().getTemplateBuildPlans()) {
                final List<String> linkNamesInSources = this.getLinksInSources(template);
                if (linkNamesInSources.contains(linkAsTarget)) {
                    preds.add(template);
                }
            }
        }
        return preds;
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
     * Returns a List of Names of the variables defined inside the given templatePlan
     *
     * @param templatePlan a templatePlan
     * @return a List of Strings with the names of the variables defined inside the given templatePlan
     */
    public List<String> getVariableNames(final BPELScope templatePlan) {
        final List<String> varNames = new ArrayList<>();
        final NodeList variableNodesList = templatePlan.getBpelVariablesElement().getChildNodes();

        for (int index = 0; index < variableNodesList.getLength(); index++) {
            if (variableNodesList.item(index).getNodeType() == Node.ELEMENT_NODE) {
                final String varName = ((Element) variableNodesList.item(index)).getAttribute("name");
                if (varName != null) {
                    varNames.add(varName);
                }
            }
        }

        return varNames;
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
    public void initializeXMLElements(final BPELScope newTemplateBuildPlan, final BPELPlan buildPlan) {
        // set the build plan of the new template buildplan
        newTemplateBuildPlan.setBuildPlan(buildPlan);

        // initialize bpelScopeElement and append to flow
        newTemplateBuildPlan.setBpelScopeElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace, "scope"));
        // info: append to flow element of the buildplan
        newTemplateBuildPlan.getBuildPlan().getBpelMainFlowElement()
            .appendChild(newTemplateBuildPlan.getBpelScopeElement());

        // initialize bpelTargetsElement and append to scope
        newTemplateBuildPlan.setBpelTargetsElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "targets"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelTargetsElement());

        // initialize bpelSourcesElement and append to scope
        newTemplateBuildPlan.setBpelSourcesElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "sources"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelSourcesElement());

        // init bpelPartnerLinksElement append to scope
        newTemplateBuildPlan.setBpelPartnerLinks(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "partnerLinks"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelPartnerLinksElement());

        // init bpelVariablesElement and append to scope
        newTemplateBuildPlan.setBpelVariablesElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "variables"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelVariablesElement());

        // init bpelCorrelationSetsElement and append to scope
        newTemplateBuildPlan.setBpelCorrelationSets(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "correlationSets"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelCorrelationSets());

        // initialize bpelMainSequenceElement and append to scope
        newTemplateBuildPlan.setBpelMainSequenceElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "sequence"));
        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelMainSequenceElement());

        // initialize bpelSequencePrePhaseElement and append to mainsequence
        newTemplateBuildPlan.setBpelSequencePrePhaseElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "sequence"));
        newTemplateBuildPlan.getBpelMainSequenceElement()
            .appendChild(newTemplateBuildPlan.getBpelSequencePrePhaseElement());

        // initialize bpelSequenceProvisioningPhaseElement and append to
        // mainsequence
        newTemplateBuildPlan.setBpelSequenceProvisioningPhaseElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "sequence"));
        newTemplateBuildPlan.getBpelMainSequenceElement()
            .appendChild(newTemplateBuildPlan.getBpelSequenceProvisioningPhaseElement());

        // initialize bpelSequencePostPhaseElement and append to scope
        newTemplateBuildPlan.setBpelSequencePostPhaseElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "sequence"));
        newTemplateBuildPlan.getBpelMainSequenceElement()
            .appendChild(newTemplateBuildPlan.getBpelSequencePostPhaseElement());

        newTemplateBuildPlan.setBpelEventHandlersElement(newTemplateBuildPlan.getBpelDocument()
            .createElementNS(BPELPlan.bpelNamespace,
                "eventHandlers"));

        newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelEventHandlersElement());
    }

    /**
     * Checks whether the given TemplateBuildPlan is for a NodeTemplate
     *
     * @param template the TemplateBuildPlan to check
     * @return true if the given TemplateBuildPlan is for a NodeTemplate
     */
    public boolean isNodeTemplatePlan(final BPELScope template) {
        return template.getNodeTemplate() != null;
    }

    /**
     * Checks whether the given TemplateBuildPlan is for a RelationshipTemplate
     *
     * @param template the TemplateBuildPlan to check
     * @return true if the given TemplateBuildPlan is for a RelationshipTemplate
     */
    public boolean isRelationshipTemplatePlan(final BPELScope template) {
        return template.getRelationshipTemplate() != null;
    }

    /**
     * Removes all connections the given TemplateBuildPlan contains. All source/target relations are removed from the
     * given TemplateBuildPlan
     *
     * @param template the TemplateBuildPlan to remove its relations
     */
    public void removeAllConnetions(final BPELScope template) {
        this.removeSources(template);
        this.removeTargets(template);
    }

    /**
     * Removes all links which use the given TemplateBuildPlan as source
     *
     * @param template the TemplateBuildPlan for that all source relations have to be removed
     */
    public void removeSources(final BPELScope template) {
        final Element sources = template.getBpelSourcesElement();

        if (sources != null) {
            BPELScopeHandler.removeAllChildNodes(sources);
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
            BPELScopeHandler.removeAllChildNodes(targets);
        }
    }

    /**
     * Sets the name of the TemplateBuildPlan
     *
     * @param name              the name to set
     * @param templateBuildPlan the TemplateBuildPlan to set the name for
     */
    public void setName(final String name, final BPELScope templateBuildPlan) {
        BPELScopeHandler.LOG.debug("Setting name {} for TemplateBuildPlan", name);
        // set scope name
        templateBuildPlan.getBpelScopeElement().setAttribute("name", name + "_scope");
        // set main sequence name
        templateBuildPlan.getBpelMainSequenceElement().setAttribute("name", name + "_mainSequence");
        // set prephase name
        templateBuildPlan.getBpelSequencePrePhaseElement().setAttribute("name", name + "_prePhase");
        // set provisioning phase name
        templateBuildPlan.getBpelSequenceProvisioningPhaseElement().setAttribute("name", name + "_provisioningPhase");
        // set post phase name
        templateBuildPlan.getBpelSequencePostPhaseElement().setAttribute("name", name + "_postPhase");
    }
}
