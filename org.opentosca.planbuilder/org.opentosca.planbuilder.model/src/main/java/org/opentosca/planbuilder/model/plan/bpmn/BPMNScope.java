package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
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
    // aka bpmn subprozess
    private Element bpmnScopeElement;

    private Set<BPMNScope> incomingScope = new HashSet<>();
    private Set<BPMNScope> outgoingScope = new HashSet<>();

    private String[] flowElements = new String[2];

    // various elements of BPMN subprocess
    private Element bpmnSourcesElement;
    private Element bpmnTargetsElement;
    private String serviceInstanceURL;

    private TNodeTemplate nodeTemplate = null;
    private TRelationshipTemplate relationshipTemplate = null;

    public BPMNScope(AbstractActivity activity) {
        this.act = activity;
        this.usedOperations = new HashMap<>();
    }

    public BPMNScope(String name) {
        this.act = null;
        this.usedOperations = new HashMap<>();
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

    public void setFlowElements(String[] flowElements) {
        this.flowElements = flowElements;
    }

    public String[] getFlowElements() {
        return this.flowElements;
    }

    public void setServiceInstanceURL(String serviceInstanceURL) {
        this.serviceInstanceURL = serviceInstanceURL;
    }

    public String getServiceInstanceURL() {
        return this.serviceInstanceURL;
    }
}
