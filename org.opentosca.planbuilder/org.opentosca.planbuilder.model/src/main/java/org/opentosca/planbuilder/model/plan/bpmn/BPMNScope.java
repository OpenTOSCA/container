package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class is the model for TemplateBuildPlans as declared in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans für OpenTOSCA</a> and
 * enforces those concepts by defining placeholder elements
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNScope {

    private final AbstractActivity act;
    private final Map<TOperation, TOperation> usedOperations;
    // the buildplan this templatebuildplan belongs to
    private BPMNPlan buildPlan;

    // bpmn elements this templatebuildplan controls
    private Element bpmnScopeElement;

    // various elements of BPMN subprocess
    private Element bpmnSourcesElement;
    private Element bpmnTargetsElement;
    private Element bpmnVariablesElement;
    private Element bpelPartnerLinks;
    private Element bpelCorrelationSets;
    private Element bpelMainSequenceElement;
    private Element bpelSequencePrePhaseElement;
    private Element bpelSequenceProvisioningPhaseElement;
    private Element bpelSequencePostPhaseElement;
    private Element bpelEventHandlersElement;

    private BPMNScope bpelCompensationScope;
    private BPMNScope bpelFaultScope;
    private TNodeTemplate nodeTemplate = null;
    private TRelationshipTemplate relationshipTemplate = null;

    public BPMNScope(AbstractActivity activity) {
        this.act = activity;
        this.usedOperations = new HashMap<TOperation, TOperation>();
    }

    @Override
    public String toString() {
        return "BPMNScope Plan: " + buildPlan.getId() + " Activity: " + this.act + ((this.getNodeTemplate() != null) ? " Node: " + this.nodeTemplate.getId() : " Relation: " + this.relationshipTemplate.getId());
    }

    public AbstractActivity getActivity() {
        return this.act;
    }

    /**
     * Returns the DOM Document this TemplateBuildPlan is declared
     *
     * @return a DOM Document
     */
    public Document getBpmnDocument() {
        return this.buildPlan.getBpmnDocument();
    }

    /**
     * Returns the BuildPlan this TemplateBuildPlan belongs to
     *
     * @return a BuildPlan
     */
    public BPMNPlan getBuildPlan() {
        return this.buildPlan;
    }

    /**
     * Sets the BuildPlan this TemplateBuildPlan belongs
     *
     * @param buildPlan a BuildPlan
     */
    public void setBuildPlan(final BPMNPlan buildPlan) {
        this.buildPlan = buildPlan;
    }

    /**
     * Returns a DOM Element which is BPEL scope element
     *
     * @return a DOM Element
     */
    public Element getBpmnScopeElement() {
        return this.bpmnScopeElement;
    }

    /**
     * Sets the BPEL scope element of this templateBuildPlan
     *
     * @param bpmnScopeElement a DOM Element
     */
    public void setBpmnScopeElement(final Element bpmnScopeElement) {
        this.bpmnScopeElement = bpmnScopeElement;
    }

    /**
     * Gets the BPEL Sources element of this TemplateBuildPlan
     *
     * @return a DOM ELement
     */
    public Element getBpmnSourcesElement() {
        return this.bpmnSourcesElement;
    }

    /**
     * Sets the BPEL Sources element of this TemplateBuildPlan
     *
     * @param bpmnSourcesElement a DOM Element
     */
    public void setBpmnSourcesElement(final Element bpmnSourcesElement) {
        this.bpmnSourcesElement = bpmnSourcesElement;
    }

    /**
     * Gets the BPMN Targets ELement of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpmnTargetsElement() {
        return this.bpmnTargetsElement;
    }

    /**
     * Sets the BPMN Targets Element of this TemplateBuildPlan
     *
     * @param bpmnTargetsElement a DOM Element
     */
    public void setBpmnTargetsElement(final Element bpmnTargetsElement) {
        this.bpmnTargetsElement = bpmnTargetsElement;
    }

    /**
     * Gets the BPEL Variables element of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelVariablesElement() {
        return this.bpmnVariablesElement;
    }

    /**
     * Sets the BPMN Variables element of this TemplateBuildPlan
     *
     * @param bpmnVariablesElement a DOM Element
     */
    public void setBpmnVariablesElement(final Element bpmnVariablesElement) {
        this.bpmnVariablesElement = bpmnVariablesElement;
    }

    /**
     * Gets the BPEL PartnerLinks element of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelPartnerLinksElement() {
        return this.bpelPartnerLinks;
    }

    /**
     * Sets the BPEL PartnerLinks element of this TemplateBuildPlan
     *
     * @param bpelPartnerLinks a DOM Element
     */
    public void setBpelPartnerLinks(final Element bpelPartnerLinks) {
        this.bpelPartnerLinks = bpelPartnerLinks;
    }

    /**
     * Gets the main BPEL Sequence element of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelMainSequenceElement() {
        return this.bpelMainSequenceElement;
    }

    /**
     * Sets the main BPEL Sequence element of this TemplateBuildPlan
     *
     * @param bpelMainSequenceElement a DOM Element
     */
    public void setBpelMainSequenceElement(final Element bpelMainSequenceElement) {
        this.bpelMainSequenceElement = bpelMainSequenceElement;
    }

    /**
     * Gets the BPEL Sequence element which is the PrePhase of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelSequencePrePhaseElement() {
        return this.bpelSequencePrePhaseElement;
    }

    /**
     * Set the BPEL Sequence element which is the PrePhase of this TemplateBuildPlan
     *
     * @param bpelSequencePrePhaseElement a DOM Element
     */
    public void setBpelSequencePrePhaseElement(final Element bpelSequencePrePhaseElement) {
        this.bpelSequencePrePhaseElement = bpelSequencePrePhaseElement;
    }

    /**
     * Gets the BPEL Sequence element which is the ProvisioningPhase of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelSequenceProvisioningPhaseElement() {
        return this.bpelSequenceProvisioningPhaseElement;
    }

    /**
     * Set the BPEL Sequence element which is the ProvisioningPhase of this TemplateBuildPlan
     *
     * @param bpelSequenceProvisioningPhaseElement a DOM Element
     */
    public void setBpelSequenceProvisioningPhaseElement(final Element bpelSequenceProvisioningPhaseElement) {
        this.bpelSequenceProvisioningPhaseElement = bpelSequenceProvisioningPhaseElement;
    }

    /**
     * Gets the BPEL Sequence element which is the PostPhase of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelSequencePostPhaseElement() {
        return this.bpelSequencePostPhaseElement;
    }

    /**
     * Sets the BPEL Sequence element which is the PostPhase of this TemplateBuildPlan
     *
     * @param bpelSequencePostPhaseElement a DOM Element
     */
    public void setBpelSequencePostPhaseElement(final Element bpelSequencePostPhaseElement) {
        this.bpelSequencePostPhaseElement = bpelSequencePostPhaseElement;
    }

    /**
     * Returns the scope containing the compensation activities of this scope
     *
     * @return a DOM Element
     */
    public BPMNScope getBpelCompensationHandlerScope() {
        return bpelCompensationScope;
    }

    /**
     * Sets the scope as the compensation handler of this scope
     *
     * @param bpelCompensationScope a BPEL DOM Element with a compensation handler
     */
    public void setBpelCompensationHandlerScope(BPMNScope bpelCompensationScope) {
        this.bpelCompensationScope = bpelCompensationScope;
        Element compensationHandlerElement = this.buildPlan.getBpmnDocument().createElementNS(BPELPlan.bpelNamespace, "compensationHandler");
        compensationHandlerElement.appendChild(this.bpelCompensationScope.getBpmnScopeElement());
        this.bpmnScopeElement.insertBefore(compensationHandlerElement, this.bpelMainSequenceElement);
    }

    /**
     * Returns the scope containing the faul handling activities of this scope
     */
    public BPMNScope getBpelFaultHandlerScope() {
        return this.bpelFaultScope;
    }

    /**
     * Sets the scope as the fault handler of this scope
     *
     * @param bpelFaultScope a BPMN DOM Element with fault handler
     */
    public void setBpelFaultHandlerScope(BPMNScope bpelFaultScope) {
        this.bpelFaultScope = bpelFaultScope;
        Element rethrowElement = this.buildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace, "rethrow");
        bpelFaultScope.getBpelSequencePostPhaseElement().appendChild(rethrowElement);
        Element faultHandlersElement = this.buildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace, "faultHandlers");
        Element catchAllElement = this.buildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace, "catchAll");
        catchAllElement.appendChild(this.bpelFaultScope.getBpmnScopeElement());
        faultHandlersElement.appendChild(catchAllElement);
        this.bpmnScopeElement.insertBefore(faultHandlersElement, this.bpelMainSequenceElement);
    }

    /**
     * Gets the NodeTemplate this TemplateBuildPlan belongs to
     *
     * @return an TNodeTemplate, else null if this is a TemplateBuildPlan for a RelationshipTemplate
     */
    public TNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }

    /**
     * Set the NodeTemplate of this TemplateBuildPlan
     *
     * @param nodeTemplate an TNodeTemplate
     */
    public void setNodeTemplate(final TNodeTemplate nodeTemplate) {
        this.nodeTemplate = nodeTemplate;
    }

    /**
     * Get the RelationshipTemplate this TemplateBuildPlan belongs to
     *
     * @return an TRelationshipTemplate, else null if this is a TemplateBuildPlan for a RelationshipTemplate
     */
    public TRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }

    /**
     * Sets the RelationshipTemplate of this TemplateBuildPlan
     *
     * @param relationshipTemplate an TRelationshipTemplate
     */
    public void setRelationshipTemplate(final TRelationshipTemplate relationshipTemplate) {
        this.relationshipTemplate = relationshipTemplate;
    }

    /**
     * Gets the BPEL CorrelationSets element of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelCorrelationSets() {
        return this.bpelCorrelationSets;
    }

    /**
     * Sets the BPEL CorrelationSets element of this TemplateBuildPlan
     *
     * @param bpelCorrelationSets a DOM Element
     */
    public void setBpelCorrelationSets(final Element bpelCorrelationSets) {
        this.bpelCorrelationSets = bpelCorrelationSets;
    }

    public Element getBpelEventHandlersElement() {
        return bpelEventHandlersElement;
    }

    public void setBpelEventHandlersElement(Element bpelEventHandlersElement) {
        this.bpelEventHandlersElement = bpelEventHandlersElement;
    }

    public Map<TOperation, TOperation> getUsedOperations() {
        return usedOperations;
    }

    public void addUsedOperation(TOperation usedOperation, TOperation compensationOperation) {
        this.usedOperations.put(usedOperation, compensationOperation);
    }

    public enum BPELScopePhaseType {
        PRE, PROVISIONING, POST
    }
}
