package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.*;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class is the placeholder for BPMN elements before
 * it is generated to XML snippet
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNScope {

    private final String id;
    private final AbstractActivity act;
    private final Map<TOperation, TOperation> usedOperations = new HashMap<TOperation, TOperation>();

    // for groovy script InputName:InputValue
    private final Map<String, String> inputParameterMap = new HashMap<>();

    // for groovy script OutputParameterName:OutputParameterValue
    private final Map<String, String> outputParameterMap = new HashMap<>();

    // from groovy script, unique for other script to identification
    private String resultVariableName;

    private final BPMNScopeType bpmnScopeType;

    // bpmn entity could have multiple incoming and outgoing
    private Set<BPMNScope> incomingScope = new HashSet<>();
    private Set<BPMNScope> outgoingScope = new HashSet<>();

    // the buildplan this templatebuildplan belongs to
    private BPMNPlan buildPlan;

    // all BPMNScopes contains in the subprocess (including Task and Sequence flow, bpmn elements are fulfilled by plugin
    private Set<BPMNScope> subprocessBPMNScopes = new HashSet<>();

    // parent process for current BPMNScope contained within subprocess
    private BPMNScope parentProcess;

    // events and elements contains in a Node subprocess
    private BPMNScope subStartEvent;
    private BPMNScope subProCreateNodeInstanceTask;
    private BPMNScope subProCallOperationTask;
    private BPMNScope subProSetNodePropertyTask;
    private BPMNScope subEndEvent;

    // bpmn elements this templatebuildplan controls
    private Element bpmnScopeElement;

    // various elements of BPMN subprocess
    private Element bpmnSourcesElement;
    private Element bpmnTargetsElement;
    private Element bpmnVariablesElement;

    private TNodeTemplate nodeTemplate = null;
    private TRelationshipTemplate relationshipTemplate = null;
    private String instanceState;

    /**
     *
     * @param activity
     * @param bpmnScopeType
     * @param id
     */
    public BPMNScope(AbstractActivity activity, BPMNScopeType bpmnScopeType, String id) {
        this.act = activity;
        this.bpmnScopeType = bpmnScopeType;
        this.id = id;
    }

    public BPMNScope(NodeTemplateActivity activity, BPMNScopeType bpmnScopeType, String id) {
        this.act = activity;
        this.nodeTemplate = activity.getNodeTemplate();
        this.bpmnScopeType = bpmnScopeType;
        this.id = id;
    }

    public BPMNScope(RelationshipTemplateActivity activity, BPMNScopeType bpmnScopeType, String id) {
        this.act = activity;
        this.relationshipTemplate = activity.getRelationshipTemplate();
        this.bpmnScopeType = bpmnScopeType;
        this.id = id;
    }

    // Use Case: for Link
    public BPMNScope(BPMNScopeType bpmnScopeType, String id) {
        this.act = null;
        this.bpmnScopeType = bpmnScopeType;
        this.id = id;
    }

    public BPMNScope(AbstractActivity activity) {
        this.act = activity;
        this.bpmnScopeType = null;
        this.id = null;
    }

    @Override
    public String toString() {
        return "BPMNScope ID: " + id + " Plan: " + buildPlan.getId() + " Activity: " + this.act +
            ((this.getNodeTemplate() != null) ?
                " Node: " + this.nodeTemplate.getId() :
                (relationshipTemplate != null) ? this.relationshipTemplate.getId() : "is null");
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

    public Map<TOperation, TOperation> getUsedOperations() {
        return usedOperations;
    }

    public void addUsedOperation(TOperation usedOperation, TOperation compensationOperation) {
        this.usedOperations.put(usedOperation, compensationOperation);
    }

    public BPMNScopeType getBpmnScopeType() {
        return bpmnScopeType;
    }

    public boolean containsIncomingScope(BPMNScope incoming) {
        return this.incomingScope.contains(incoming);
    }

    public boolean containsOutgoingScope(BPMNScope outgoing) {
        return this.outgoingScope.contains(outgoing);
    }

    public void addIncomingScope(BPMNScope incoming) {
        this.incomingScope.add(incoming);
    }

    public void addOutgoingScope(BPMNScope outgoing) {
        this.outgoingScope.add(outgoing);
    }

    public void addScopeToSubprocess(BPMNScope scope) {
        this.subprocessBPMNScopes.add(scope);
    }

    public int getNumIncomingLinks() {
        return this.incomingScope.size();
    }

    public int getNumOutgoingLinks() {
        return this.outgoingScope.size();
    }

    public Collection<BPMNScope> getIncomingLinks() {
        return this.incomingScope;
    }

    public Collection<BPMNScope> getOutgoingLinks() {
        return this.outgoingScope;
    }

    public String getId() {
        return id;
    }

    public BPMNScope getParentProcess() {
        return parentProcess;
    }

    public void setParentProcess(BPMNScope parentProcess) {
        this.parentProcess = parentProcess;
    }

    public Set<BPMNScope> getSubprocessBPMNScopes() {
        return subprocessBPMNScopes;
    }

    public BPMNScope getSubStartEvent() {
        return subStartEvent;
    }

    public void setSubStartEvent(BPMNScope subStartEvent) {
        this.subStartEvent = subStartEvent;
    }

    public BPMNScope getSubProCreateNodeInstanceTask() {
        return subProCreateNodeInstanceTask;
    }

    public void setSubProCreateNodeInstanceTask(BPMNScope subProCreateNodeInstanceTask) {
        this.subProCreateNodeInstanceTask = subProCreateNodeInstanceTask;
    }

    public BPMNScope getSubProCallOperationTask() {
        return subProCallOperationTask;
    }

    public void setSubProCallOperationTask(BPMNScope subProCallOperationTask) {
        this.subProCallOperationTask = subProCallOperationTask;
    }

    public BPMNScope getSubProSetNodePropertyTask() {
        return subProSetNodePropertyTask;
    }

    public void setSubProSetNodePropertyTask(BPMNScope subProSetNodePropertyTask) {
        this.subProSetNodePropertyTask = subProSetNodePropertyTask;
    }

    public BPMNScope getSubEndEvent() {
        return subEndEvent;
    }

    public void setSubEndEvent(BPMNScope subEndEvent) {
        this.subEndEvent = subEndEvent;
    }

    public String getNodeState() {
        return instanceState;
    }

    public void setInstanceState(String state) {
        instanceState = state;
    }

    public String getInstanceState() {
        return instanceState;
    }

    public String getInstanceUrlVariableName() {
        return buildPlan.getNodeTemplateInstanceUrlVariableName(this.nodeTemplate);
    }

    public String getInputParameter(String paramName) {
        return inputParameterMap.getOrDefault(paramName, "");
    }

    public Map<String, String> getInputParameterMap() {
        return inputParameterMap;
    }

    public boolean addInputparameter(String name, String value) {
        if (inputParameterMap.containsKey(name)) {
            return false;
        }
        inputParameterMap.put(name, value);
        return true;
    }

    public Map<String, String> getOutputParameterMap() {
        return outputParameterMap;
    }

    public boolean addOutputParameter(String name, String value) {
        if (outputParameterMap.containsKey(name)) {
            return false;
        }
        outputParameterMap.put(name, value);
        return false;
    }
}
