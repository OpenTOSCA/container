package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is the model for BPMN Subprocesses which contains all the necessary information to replace later the
 * required information for the fragments.
 */
public class BPMNSubprocess {

    private final AbstractActivity activity;
    private BPMNPlan buildPlan;
    private Element bpmnSubprocessElement;

    private Collection<BPMNSubprocess> incomingScope = new HashSet<>();
    private BPMNDataObject dataObject;
    private BPMNSubprocess subProServiceInstanceTask;
    private ArrayList<BPMNSubprocess> outgoingFlowElements = new ArrayList<>();
    private ArrayList<BPMNSubprocess> incomingFlowElements = new ArrayList<>();
    private ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();
    private ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();
    private String serviceInstanceURL;

    private TNodeTemplate nodeTemplate = null;
    private TNodeTemplate hostingNodeTemplate = null;
    private TRelationshipTemplate relationshipTemplate = null;

    private String id;
    private BPMNSubprocessType bpmnSubprocessType = null;
    private String resultVariableName;

    //callNodeOperation Variables
    private String interfaceVariable;
    private String operation;
    private String outputParameterNames;
    private String outputParameterValues;
    private String inputParameterNames;
    private String inputParameterValues;

    // create relationship instance variables
    private String sourceInstanceURL;
    private String targetInstanceURL;

    // all BPMNScopes contains in the subprocess (including Task and Sequence flow, bpmn elements are fulfilled by plugin
    private final ArrayList<BPMNSubprocess> subprocessBPMNScopes = new ArrayList<>();

    // parent process for current BPMNScope contained within subprocess
    private BPMNSubprocess parentProcess;

    private BPMNSubprocess subProCreateNodeInstanceTask;
    private BPMNSubprocess subProSetStateTask;
    private BPMNSubprocess subProSetNodePropertyTask;

    private String instanceState;

    private ArrayList<Integer> errorEventIds = new ArrayList<>();
    private String deploymentArtifactString;
    private double x;
    private double y;

    public BPMNSubprocess(AbstractActivity activity, BPMNSubprocessType subprocessType, String id) {
        this.activity = activity;
        this.bpmnSubprocessType = subprocessType;
        this.id = id;
    }

    public BPMNSubprocess(BPMNSubprocessType subprocessType, String id) {
        this.activity = null;
        this.bpmnSubprocessType = subprocessType;
        this.id = id;
    }

    public ArrayList<BPMNSubprocess> getFlowElements() {
        return flowElements;
    }

    public void setFlowElements(ArrayList<BPMNSubprocess> flow) {
        this.flowElements = flow;
    }

    public ArrayList<BPMNSubprocess> getErrorFlowElements() {
        return errorFlowElements;
    }

    public void setErrorFlowElements(ArrayList<BPMNSubprocess> flow) {
        this.errorFlowElements = flow;
    }

    public ArrayList<BPMNSubprocess> getIncomingTestFlow() {
        return outgoingFlowElements;
    }

    public ArrayList<BPMNSubprocess> getOutgoingFlow() {
        return outgoingFlowElements;
    }

    public void setOutgoingFlow(BPMNSubprocess outgoingSubprocess) {
        this.outgoingFlowElements.add(outgoingSubprocess);
    }

    public ArrayList<BPMNSubprocess> getOuterFlow() {
        return this.incomingFlowElements;
    }

    public void setIncomingFlowElements(BPMNSubprocess incomingTestScopeflow) {
        this.incomingFlowElements.add(incomingTestScopeflow);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeploymentArtifactString() {
        return deploymentArtifactString;
    }

    public void setDeploymentArtifactString(String deploymentArtifactString) {
        this.deploymentArtifactString = deploymentArtifactString;
    }

    @Override
    public String toString() {
        return "BPMNSubprocess Plan: " + buildPlan.getId() + " Activity: " + this.activity + ((this.getNodeTemplate() != null) ? " Node: " + this.nodeTemplate.getId() : " Relation: " + this.relationshipTemplate.getId());
    }

    public BPMNSubprocessType getSubprocessType() {
        return this.bpmnSubprocessType;
    }

    public AbstractActivity getActivity() {
        return this.activity;
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
     * Returns a DOM Element which is BPMN subprocess element
     *
     * @return a DOM Element
     */
    public Element getBpmnSubprocessElement() {
        return this.bpmnSubprocessElement;
    }

    public String getResultVariableName() {
        return this.resultVariableName;
    }

    public void setResultVariableName(String resultVariableName) {
        this.resultVariableName = resultVariableName;
    }

    /**
     * Sets the BPMN subprocess element of this templateBuildPlan
     *
     * @param bpmnSubprocessElement a DOM Element
     */
    public void setBpmnSubprocessElement(final Element bpmnSubprocessElement) {
        this.bpmnSubprocessElement = bpmnSubprocessElement;
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
     * The usage is for the call node operation task because there is a difference between the node template and the
     * node template from which we call the operation
     *
     * @return an TNodeTemplate, else null
     */
    public TNodeTemplate getHostingNodeTemplate() {
        return this.hostingNodeTemplate;
    }

    /**
     * Set the HostingNodeTemplate
     *
     * @param nodeTemplate an TNodeTemplate
     */
    public void setHostingNodeTemplate(final TNodeTemplate nodeTemplate) {
        this.hostingNodeTemplate = nodeTemplate;
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

    public BPMNSubprocessType getBpmnSubprocessType() {
        return this.bpmnSubprocessType;
    }

    public void setIncomingSubprocess(Collection<BPMNSubprocess> incoming) {
        this.incomingScope = incoming;
    }

    public void addTaskToSubprocess(BPMNSubprocess scope) {
        this.subprocessBPMNScopes.add(scope);
    }

    public Collection<BPMNSubprocess> getIncomingLinks() {
        return this.incomingScope;
    }

    public String getId() {
        return id;
    }

    public BPMNSubprocess getParentProcess() {
        return parentProcess;
    }

    public void setParentProcess(BPMNSubprocess parentProcess) {
        this.parentProcess = parentProcess;
    }

    public ArrayList<BPMNSubprocess> getSubprocessBPMNSubprocess() {
        return subprocessBPMNScopes;
    }

    public BPMNSubprocess getSubProServiceInstanceTask() {
        return subProServiceInstanceTask;
    }

    public void setSubProServiceInstanceTask(BPMNSubprocess subProServiceInstanceTask) {
        this.subProServiceInstanceTask = subProServiceInstanceTask;
    }

    public BPMNSubprocess getSubProSetNodePropertyTask() {
        return subProSetNodePropertyTask;
    }

    public void setSubProSetNodePropertyTask(BPMNSubprocess subProSetNodePropertyTask) {
        this.subProSetNodePropertyTask = subProSetNodePropertyTask;
    }

    public BPMNSubprocess getSubProCreateNodeInstanceTask() {
        return subProCreateNodeInstanceTask;
    }

    public void setSubProCreateNodeInstanceTask(BPMNSubprocess subProCreateNodeInstanceTask) {
        this.subProCreateNodeInstanceTask = subProCreateNodeInstanceTask;
    }

    public BPMNSubprocess getSubProSetStateTask() {
        return subProSetStateTask;
    }

    public void setSubProSetStateTask(BPMNSubprocess subProSetStateTask) {
        this.subProSetStateTask = subProSetStateTask;
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

    public void setServiceInstanceURL(String serviceInstanceURL) {
        this.serviceInstanceURL = serviceInstanceURL;
    }

    public String getServiceInstanceURL() {
        return this.serviceInstanceURL;
    }

    public String getInterfaceVariable() {
        return interfaceVariable;
    }

    public void setInterfaceVariable(String interfaceVariable) {
        this.interfaceVariable = interfaceVariable;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getInputParameterNames() {
        return inputParameterNames;
    }

    public void setInputParameterNames(String inputParameterName) {
        this.inputParameterNames = inputParameterName;
    }

    public String getInputParameterValues() {
        return inputParameterValues;
    }

    public void setInputParameterValues(String inputparamvalues) {
        this.inputParameterValues = inputparamvalues;
    }

    public String getOutputParameterNames() {
        return outputParameterNames;
    }

    public void setOutputParameterNames(String outputParameterNames) {
        this.outputParameterNames = outputParameterNames;
    }

    public String getOutputParameterValues() {
        return outputParameterValues;
    }

    public void setOutputParameterValues(String outputParameterValues) {
        this.outputParameterValues = outputParameterValues;
    }

    public String getSourceInstanceURL() {
        return this.sourceInstanceURL;
    }

    public void setSourceInstanceURL(String sourceInstanceURL) {
        this.sourceInstanceURL = sourceInstanceURL;
    }

    public String getTargetInstanceURL() {
        return targetInstanceURL;
    }

    public void setTargetInstanceURL(String targetInstanceURL) {
        this.targetInstanceURL = targetInstanceURL;
    }

    public ArrayList<Integer> getErrorEventIds() {
        return errorEventIds;
    }

    public void setErrorEventIds(ArrayList<Integer> errorEventIds) {
        this.errorEventIds = errorEventIds;
    }

    public BPMNDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(BPMNDataObject dataObject) {
        this.dataObject = dataObject;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
