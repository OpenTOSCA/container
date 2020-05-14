package org.opentosca.planbuilder.model.plan.bpel;

import java.util.Map;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class is the model for TemplateBuildPlans as declared in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans für
 * OpenTOSCA</a> and enforces those concepts by defining placeholder elements
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELScope{

    private AbstractActivity act;
    
    
    public BPELScope(AbstractActivity activity) {
        this.act = activity;
    }

    
    // the buildplan this templatebuildplan belongs to
    private BPELPlan buildPlan;

    
    // bpel elements this templatebuildplan controls
    private Element bpelScopeElement;

    // various elements of BPEL scope
    private Element bpelSourcesElement;
    private Element bpelTargetsElement;
    private Element bpelVariablesElement;
    private Element bpelPartnerLinks;
    private Element bpelCorrelationSets;
    private Element bpelMainSequenceElement;
    private Element bpelSequencePrePhaseElement;
    private Element bpelSequenceProvisioningPhaseElement;
    private Element bpelSequencePostPhaseElement;    


    private BPELScope bpelCompensationScope;
    private BPELScope bpelFaultScope;
    
    private Map<AbstractOperation, AbstractOperation> usedOperations;


    private AbstractNodeTemplate nodeTemplate = null;
    private AbstractRelationshipTemplate relationshipTemplate = null;
    
    @Override
    public String toString() {
        return "BPELScope Plan: " + buildPlan.getId() + " Activity: " + this.act + ((this.getNodeTemplate() != null) ? " Node: " +this.nodeTemplate.getId() : " Relation: " + this.relationshipTemplate.getId());
    }

    public static enum BPELScopePhaseType {
        PRE, PROVISIONING, POST
    }

    public AbstractActivity getActivity() {
        return this.act;
    }
    
    /**
     * Returns the DOM Document this TemplateBuildPlan is declared
     *
     * @return a DOM Document
     */
    public Document getBpelDocument() {
        return this.buildPlan.getBpelDocument();
    }

    /**
     * Returns the BuildPlan this TemplateBuildPlan belongs to
     *
     * @return a BuildPlan
     */
    public BPELPlan getBuildPlan() {
        return this.buildPlan;
    }

    /**
     * Sets the BuildPlan this TemplateBuildPlan belongs
     *
     * @param buildPlan a BuildPlan
     */
    public void setBuildPlan(final BPELPlan buildPlan) {
        this.buildPlan = buildPlan;
    }

    /**
     * Returns a DOM Element which is BPEL scope element
     *
     * @return a DOM Element
     */
    public Element getBpelScopeElement() {
        return this.bpelScopeElement;
    }

    /**
     * Sets the BPEL scope element of this templateBuildPlan
     *
     * @param bpelScopeElement a DOM Element
     */
    public void setBpelScopeElement(final Element bpelScopeElement) {
        this.bpelScopeElement = bpelScopeElement;
    }

    /**
     * Gets the BPEL Sources element of this TemplateBuildPlan
     *
     * @return a DOM ELement
     */
    public Element getBpelSourcesElement() {
        return this.bpelSourcesElement;
    }

    /**
     * Sets the BPEL Sources element of this TemplateBuildPlan
     *
     * @param bpelSourcesElement a DOM Element
     */
    public void setBpelSourcesElement(final Element bpelSourcesElement) {
        this.bpelSourcesElement = bpelSourcesElement;
    }

    /**
     * Gets the BPEL Targets ELement of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelTargetsElement() {
        return this.bpelTargetsElement;
    }

    /**
     * Sets the BPEL Targets Element of this TemplateBuildPlan
     *
     * @param bpelTargetsElement a DOM Element
     */
    public void setBpelTargetsElement(final Element bpelTargetsElement) {
        this.bpelTargetsElement = bpelTargetsElement;
    }

    /**
     * Gets the BPEL Variables element of this TemplateBuildPlan
     *
     * @return a DOM Element
     */
    public Element getBpelVariablesElement() {
        return this.bpelVariablesElement;
    }

    /**
     * Sets the BPEL Variables element of this TemplateBuildPlan
     *
     * @param bpelVariablesElement a DOM Element
     */
    public void setBpelVariablesElement(final Element bpelVariablesElement) {
        this.bpelVariablesElement = bpelVariablesElement;
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
     * @return a DOM Element
     */
    public BPELScope getBpelCompensationHandlerScope() {
        return bpelCompensationScope;
    }

    /**
     * Sets the scope as the compensation handler of this scope 
     * @param bpelCompensationScope a BPEL DOM Element with a compensation handler
     */
    public void setBpelCompensationHandlerScope(BPELScope bpelCompensationScope) {
        this.bpelCompensationScope = bpelCompensationScope;        
        Element compensationHandlerElement = this.buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "compensationHandler");                
        compensationHandlerElement.appendChild(this.bpelCompensationScope.getBpelScopeElement());        
        this.bpelScopeElement.insertBefore(compensationHandlerElement, this.bpelMainSequenceElement);        
    }

    /**
     * Returns the scope containing the faul handling activities of this scope
     * @return
     */
    public BPELScope getBpelFaultHandlerScope() {
        return this.bpelFaultScope;
    }
    
    /**
     * Sets the scope as the fault handler of this scope 
     * @param bpelFaultScope a BPEL DOM Element with fault handler
     */
    public void setBpelFaultHandlerScope(BPELScope bpelFaultScope) {
        this.bpelFaultScope = bpelFaultScope;
        Element rethrowElement = this.buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "rethrow");
        bpelFaultScope.getBpelSequencePostPhaseElement().appendChild(rethrowElement);
        Element faultHandlersElement = this.buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "faultHandlers");
        Element catchAllElement = this.buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "catchAll");
        catchAllElement.appendChild(this.bpelFaultScope.getBpelScopeElement());
        faultHandlersElement.appendChild(catchAllElement);                
        this.bpelScopeElement.insertBefore(faultHandlersElement, this.bpelMainSequenceElement);        
    }
    

    /**
     * Gets the NodeTemplate this TemplateBuildPlan belongs to
     *
     * @return an AbstractNodeTemplate, else null if this is a TemplateBuildPlan for a
     *         RelationshipTemplate
     */
    public AbstractNodeTemplate getNodeTemplate() {
        return this.nodeTemplate;
    }

    /**
     * Set the NodeTemplate of this TemplateBuildPlan
     *
     * @param nodeTemplate an AbstractNodeTemplate
     */
    public void setNodeTemplate(final AbstractNodeTemplate nodeTemplate) {
        this.nodeTemplate = nodeTemplate;
    }

    /**
     * Get the RelationshipTemplate this TemplateBuildPlan belongs to
     *
     * @return an AbstractRelationshipTemplate, else null if this is a TemplateBuildPlan for a
     *         RelationshipTemplate
     */
    public AbstractRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }

    /**
     * Sets the RelationshipTemplate of this TemplateBuildPlan
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate
     */
    public void setRelationshipTemplate(final AbstractRelationshipTemplate relationshipTemplate) {
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

}
