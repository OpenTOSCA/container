package org.opentosca.planbuilder.model.plan.bpmn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;

/**
 * This class represents a bpmn plan and its properties.
 */
public class BPMNPlan extends AbstractPlan {
    public static final String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private String toscaInterfaceName = null;
    private String toscaOperationName = null;
    // xml document

    private int outerFlowTestCounter = 0;
    private int innerFlowTestCounter = 0;
    private int outsidecounterforflows = 0;
    private int erroroutsidecounterforflows = 0;
    // bpmn -> process = main sequence, subprozess = process element
    // process -> 'main' sequence / flow
    private Document bpmnProcessDocument;
    private ArrayList<String> bpmnScript;
    private Element bpmnDefinitionElement;
    private Element bpmnProcessElement;
    private Node bpmnStartEvent;
    private Element bpmnEndEvent;
    private ArrayList<String> scriptNames;

    private Map<AbstractActivity, BPMNSubprocess> abstract2bpmnMap;
    private ArrayList<String> inputParameters;
    private String csarName = null;
    private List<BPMNSubprocess> templateBuildPlans = new ArrayList<>();
    private Map<TNodeTemplate, String> nodeTemplate2InstanceUrlVariableName = new HashMap<>();
    private Map<TRelationshipTemplate, String> relationTemplate2InstanceUrlVariableName = new HashMap<>();

    private HashMap<String, String> propertiesOutputParameter = new HashMap<>();
    private ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();

    public ArrayList<BPMNSubprocess> getFlowElements() {
        return flowElements;
    }

    public void setFlowElements(ArrayList<BPMNSubprocess> flow) {
        this.flowElements = flow;
    }

    private ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();

    public ArrayList<BPMNSubprocess> getErrorFlowElements() {
        return errorFlowElements;
    }

    public void setErrorFlowElements(ArrayList<BPMNSubprocess> flow) {
        this.errorFlowElements = flow;
    }

    // to be very specific every subprocess is associated with a data object but the properties are globally visible
    // that's why they are added here
    private List<BPMNDataObject> dataObjectsList = new ArrayList<>();
    private int errorinnercounterforflows = 0;

    public BPMNPlan(String id, PlanType type, TDefinitions definitions, TServiceTemplate serviceTemplate, Collection<AbstractActivity> activities, Collection<AbstractPlan.Link> links) {
        super(id, type, definitions, serviceTemplate, activities, links);
        ;
    }

    public void setBpmnDocument(final Document bpmnProcessDocument) {
        this.bpmnProcessDocument = bpmnProcessDocument;
    }

    public Document getBpmnDocument() {
        return this.bpmnProcessDocument;
    }

    public ArrayList<String> getBpmnScripts() {
        return this.bpmnScript;
    }

    public void setBpmnScript(final ArrayList<String> bpmnScript) {
        this.bpmnScript = bpmnScript;
    }

    public void setBpmnProcessElement(final Element bpmnProcessElement) {
        this.bpmnProcessElement = bpmnProcessElement;
    }

    public Element getBpmnProcessElement() {
        return this.bpmnProcessElement;
    }

    public void setBpmnDefinitionElement(final Element bpmnDefinitionElement) {
        this.bpmnDefinitionElement = bpmnDefinitionElement;
    }

    public Element getBpmnDefinitionElement() {
        return this.bpmnDefinitionElement;
    }

    public void setBpmnStartEvent(final Node bpmnStartEvent) {
        this.bpmnStartEvent = bpmnStartEvent;
    }

    public Node getBpmnStartEvent() {
        return this.bpmnStartEvent;
    }

    public void setBpmnEndEvent(final Element bpmnEndEvent) {
        this.bpmnEndEvent = bpmnEndEvent;
    }

    public Element getBpmnEndEvent() {
        return this.bpmnEndEvent;
    }

    public ArrayList<String> getScriptNames() {
        return this.scriptNames;
    }

    public void setScriptNames(ArrayList<String> scriptNames) {
        this.scriptNames = scriptNames;
    }

    public String getTOSCAInterfaceName() {
        if (this.toscaInterfaceName != null) {
            return this.toscaInterfaceName;
        } else {
            return this.bpmnProcessElement.getAttribute("name");
        }
    }

    public void setTOSCAInterfaceName(String interfaceName) {
        this.toscaInterfaceName = interfaceName;
    }

    public String getTOSCAOperationName() {
        if (this.toscaOperationName != null) {
            return this.toscaOperationName;
        } else {
            return null;
        }
    }

    public void setTOSCAOperationname(String operationName) {
        this.toscaOperationName = operationName;
    }

    public void setInputParameters(ArrayList<String> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public ArrayList<String> getInputParameters() {
        return inputParameters;
    }

    public void setAbstract2BPMNMapping(final Map<AbstractActivity, BPMNSubprocess> abstract2bpmnMap) {
        this.abstract2bpmnMap = abstract2bpmnMap;
    }

    public Map<AbstractActivity, BPMNSubprocess> getAbstract2BPMN() {
        return this.abstract2bpmnMap;
    }

    public boolean addSubprocess(final BPMNSubprocess template) {
        return this.templateBuildPlans.add(template);
    }

    public List<BPMNSubprocess> getSubprocess() {
        return this.templateBuildPlans;
    }

    public BPMNSubprocess getTemplateBuildPlan(TNodeTemplate nodeTemplate) {
        for (BPMNSubprocess subprocess : this.getTemplateBuildPlans()) {
            if (subprocess.getNodeTemplate() != null && subprocess.getNodeTemplate().equals(nodeTemplate)) {
                return subprocess;
            }
        }
        return null;
    }

    public BPMNSubprocess getTemplateBuildPlan(TRelationshipTemplate relationshipTemplate) {
        for (BPMNSubprocess subprocess : this.getTemplateBuildPlans()) {
            if (subprocess.getRelationshipTemplate() != null
                && subprocess.getRelationshipTemplate().equals(relationshipTemplate)) {
                return subprocess;
            }
        }
        return null;
    }

    public void setCsarName(final String csarName) {
        this.csarName = csarName;
    }

    public String getCsarName() {
        return csarName;
    }

    public List<BPMNSubprocess> getTemplateBuildPlans() {
        return templateBuildPlans;
    }

    /**
     * Returns all files this bBuildPlan has imported
     *
     * @return a List of File
     */

    public String getNodeTemplateInstanceUrlVariableName(TNodeTemplate nodeTemplate) {
        return nodeTemplate2InstanceUrlVariableName.get(nodeTemplate);
    }

    public String getRelationshipTemplateInstanceUrlVariableName(TRelationshipTemplate relationshipTemplate) {
        return relationTemplate2InstanceUrlVariableName.get(relationshipTemplate);
    }

    public List<BPMNDataObject> getDataObjectsList() {
        return this.dataObjectsList;
    }

    public void setDataObjectsList(List<BPMNDataObject> dataObjectsList) {
        this.dataObjectsList = dataObjectsList;
    }

    public HashMap<String, String> getPropertiesOutputParameters() {
        return propertiesOutputParameter;
    }

    public void setOutputParameters(HashMap<String, String> propertiesOutputParameter) {
        this.propertiesOutputParameter = propertiesOutputParameter;
    }

    public int getOuterFlowCounterId() {
        return this.outsidecounterforflows;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setOuterFlowCounterId(final int id) {
        this.outsidecounterforflows = id;
    }

    public int getIdForOuterFlowAndIncrement() {
        final int idToReturn = this.getOuterFlowCounterId();
        this.setOuterFlowCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getOuterFlowTestCounterId() {
        return this.outerFlowTestCounter;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setOuterFlowTestCounterId(final int id) {
        this.outerFlowTestCounter = id;
    }

    public int getIdForOuterFlowTestAndIncrement() {
        final int idToReturn = this.getOuterFlowTestCounterId();
        this.setOuterFlowTestCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getInnerFlowTestCounterId() {
        return this.innerFlowTestCounter;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setInnerFlowTestCounterId(final int id) {
        this.innerFlowTestCounter = id;
    }

    public int getIdForInnerFlowTestAndIncrement() {
        final int idToReturn = this.getInnerFlowTestCounterId();
        this.setInnerFlowTestCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getErrorOuterFlowCounterId() {
        return this.erroroutsidecounterforflows;
    }

    public void setErrorInnerFlowCounterId(final int id) {
        this.errorinnercounterforflows = id;
    }

    public int getIdForErrorInnerFlowAndIncrement() {
        final int idToReturn = this.getErrorInnerFlowCounterId();
        this.setErrorInnerFlowCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getErrorInnerFlowCounterId() {
        return this.errorinnercounterforflows;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setErrorOuterFlowCounterId(final int id) {
        this.erroroutsidecounterforflows = id;
    }

    public int getIdForErrorOuterFlowAndIncrement() {
        final int idToReturn = this.getErrorOuterFlowCounterId();
        this.setErrorOuterFlowCounterId(idToReturn + 1);
        return idToReturn;
    }
}
