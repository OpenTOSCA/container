package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents a bpmn plan and its properties.
 */
public class BPMNPlan extends AbstractPlan {
    public static final String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    // xml document
    private int outerFlowCounter = 0;
    private int innerFlowCounter = 0;
    private int errorOuterFlowCounter = 0;
    private int errorInnerFlowCounter = 0;
    private ArrayList<BPMNSubprocess> errorFlowElements = new ArrayList<>();
    // to be very specific every subprocess is associated with a data object but the properties are globally visible
    // that's why they are added here
    private List<BPMNDataObject> dataObjectsList = new ArrayList<>();
    private Document bpmnProcessDocument;
    private ArrayList<String> bpmnScript;
    private Element bpmnDefinitionElement;
    private Element bpmnProcessElement;
    private ArrayList<String> scriptNames;
    private ArrayList<String> inputParameters;
    private String csarName = null;
    private List<BPMNSubprocess> templateBuildPlans = new ArrayList<>();

    private HashMap<String, String> propertiesOutputParameter = new HashMap<>();
    private ArrayList<BPMNSubprocess> flowElements = new ArrayList<>();

    public BPMNPlan(final String id, final PlanType type, final TDefinitions definitions, final TServiceTemplate serviceTemplate, final Collection<AbstractActivity> activities, final Collection<AbstractPlan.Link> links) {
        super(id, type, definitions, serviceTemplate, activities, links);
    }

    public ArrayList<BPMNSubprocess> getFlowElements() {
        return flowElements;
    }

    public void setFlowElements(final ArrayList<BPMNSubprocess> flow) {
        this.flowElements = flow;
    }

    public ArrayList<BPMNSubprocess> getErrorFlowElements() {
        return errorFlowElements;
    }

    public void setErrorFlowElements(final ArrayList<BPMNSubprocess> flow) {
        this.errorFlowElements = flow;
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

    public ArrayList<String> getScriptNames() {
        return this.scriptNames;
    }

    public void setScriptNames(final ArrayList<String> scriptNames) {
        this.scriptNames = scriptNames;
    }

    public void setInputParameters(final ArrayList<String> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public ArrayList<String> getInputParameters() {
        return inputParameters;
    }

    public boolean addSubprocess(final BPMNSubprocess template) {
        return this.templateBuildPlans.add(template);
    }

    public List<BPMNSubprocess> getSubprocess() {
        return this.templateBuildPlans;
    }

    public BPMNSubprocess getTemplateBuildPlan(final TNodeTemplate nodeTemplate) {
        for (final BPMNSubprocess subprocess : this.getTemplateBuildPlans()) {
            if (subprocess.getNodeTemplate() != null && subprocess.getNodeTemplate().equals(nodeTemplate)) {
                return subprocess;
            }
        }
        return null;
    }

    public BPMNSubprocess getTemplateBuildPlan(final TRelationshipTemplate relationshipTemplate) {
        for (final BPMNSubprocess subprocess : this.getTemplateBuildPlans()) {
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

    public List<BPMNDataObject> getDataObjectsList() {
        return this.dataObjectsList;
    }

    public void setDataObjectsList(final List<BPMNDataObject> dataObjectsList) {
        this.dataObjectsList = dataObjectsList;
    }

    public HashMap<String, String> getPropertiesOutputParameters() {
        return propertiesOutputParameter;
    }

    public void setOutputParameters(final HashMap<String, String> propertiesOutputParameter) {
        this.propertiesOutputParameter = propertiesOutputParameter;
    }

    public int getOuterFlowTestCounterId() {
        return this.outerFlowCounter;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setOuterFlowTestCounterId(final int id) {
        this.outerFlowCounter = id;
    }

    public int getIdForOuterFlowTestAndIncrement() {
        final int idToReturn = this.getOuterFlowTestCounterId();
        this.setOuterFlowTestCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getInnerFlowTestCounterId() {
        return this.innerFlowCounter;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setInnerFlowTestCounterId(final int id) {
        this.innerFlowCounter = id;
    }

    public int getIdForInnerFlowTestAndIncrement() {
        final int idToReturn = this.getInnerFlowTestCounterId();
        this.setInnerFlowTestCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getErrorOuterFlowCounterId() {
        return this.errorOuterFlowCounter;
    }

    public void setErrorInnerFlowCounterId(final int id) {
        this.errorInnerFlowCounter = id;
    }

    public int getIdForErrorInnerFlowAndIncrement() {
        final int idToReturn = this.getErrorInnerFlowCounterId();
        this.setErrorInnerFlowCounterId(idToReturn + 1);
        return idToReturn;
    }

    public int getErrorInnerFlowCounterId() {
        return this.errorInnerFlowCounter;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setErrorOuterFlowCounterId(final int id) {
        this.errorOuterFlowCounter = id;
    }

    public int getIdForErrorOuterFlowAndIncrement() {
        final int idToReturn = this.getErrorOuterFlowCounterId();
        this.setErrorOuterFlowCounterId(idToReturn + 1);
        return idToReturn;
    }
}
